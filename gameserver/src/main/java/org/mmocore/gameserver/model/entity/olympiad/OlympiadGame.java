package org.mmocore.gameserver.model.entity.olympiad;

import org.apache.commons.lang3.StringUtils;
import org.mmocore.commons.lang.ArrayUtils;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.configuration.config.OlympiadConfig;
import org.mmocore.gameserver.configuration.config.clientCustoms.LostDreamCustom;
import org.mmocore.gameserver.data.xml.holder.InstantZoneHolder;
import org.mmocore.gameserver.manager.OlympiadHistoryManager;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.instances.DoorInstance;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.network.lineage.components.IBroadcastPacket;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ExOlympiadUserInfo;
import org.mmocore.gameserver.network.lineage.serverpackets.ExReceiveOlympiad;
import org.mmocore.gameserver.network.lineage.serverpackets.L2GameServerPacket;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.templates.InstantZone;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.utils.ChatUtils;
import org.mmocore.gameserver.utils.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;

public class OlympiadGame {
    public static final int MAX_POINTS_LOOSE = 10;
    private static final Logger _log = LoggerFactory.getLogger(OlympiadGame.class);
    private static final List<RewardTemplate> customWinnerRewardItems = new ArrayList<>();
    private static final List<RewardTemplate> customLoserRewardItems = new ArrayList<>();

    // init custom rewards
    static {
        try {
            parseAdditionalRewards(customWinnerRewardItems, LostDreamCustom.additionalOlyWinnerRewardItems);
            parseAdditionalRewards(customLoserRewardItems, LostDreamCustom.additionalOlyLoserRewardItems);
        } catch (Exception e) {
            _log.warn("Can't load additional olympiad reward items!");
        }
    }

    private final int _id;
    private final Reflection _reflection;
    private final CompType _type;

    private final OlympiadTeam _team1;
    private final OlympiadTeam _team2;

    private final int pointLoose;

    private final List<Player> _spectators = new CopyOnWriteArrayList<>();
    public boolean validated = false;
    OlympiadGameTask _task;
    ScheduledFuture<?> _shedule;
    private int _winner = 0;
    private int _state = 0;
    private Instant _startTime;
    private Calendar calendar;
    private long nowTime;
    private long startPointTransferTime;
    private long stopPointTransferTime;

    public OlympiadGame(final int id, final CompType type, final List<Integer> opponents) {
        _type = type;
        _id = id;
        _reflection = new Reflection();
        final InstantZone instantZone = InstantZoneHolder.getInstance().getInstantZone(Rnd.get(147, 150));
        _reflection.init(instantZone);

        _team1 = new OlympiadTeam(this, 1);
        _team2 = new OlympiadTeam(this, 2);

        int[] score = {
                0,
                0
        };

        int charId;
        for (int i = 0; i < opponents.size() / 2; i++) {
            charId = opponents.get(i);
            _team1.addMember(charId);
            score[0] += Olympiad.getNoblePoints(charId);
        }

        for (int i = opponents.size() / 2; i < opponents.size(); i++) {
            charId = opponents.get(i);
            _team2.addMember(charId);
            score[1] += Olympiad.getNoblePoints(charId);
        }

        pointLoose = (int) Math.min(Math.ceil((double) Math.min(score[0], score[1]) / getType().getLooseMult()), MAX_POINTS_LOOSE);

        Log.add("Olympiad System: Game - " + id + ": " + _team1.getName() + " Vs " + _team2.getName(), "olympiad");
    }

    private static void parseAdditionalRewards(List<RewardTemplate> rewardList, String str) {
        if (str != null && !str.equals("")) {
            String[] items = str.split(";");
            for (String item : items) {
                item = item.trim();
                String[] attrs = item.trim().split(",");
                rewardList.add(new RewardTemplate(Integer.parseInt(attrs[0]), Integer.parseInt(attrs[1]), Double.parseDouble(attrs[2])));
            }
        }
    }

    private static void addCustomRewards(TeamMember winnerMember, boolean isWinner) {
        Player player = winnerMember.getPlayer();
        if (player == null)
            return;

        List<RewardTemplate> templates = isWinner ? customWinnerRewardItems : customLoserRewardItems;
        int fame = isWinner ? LostDreamCustom.additionalOlyWinnerRewardFame : LostDreamCustom.additionalOlyLoserRewardFame;
        int crp = isWinner ? LostDreamCustom.additionalOlyWinnerRewardCrp : LostDreamCustom.additionalOlyLoserRewardCrp;

        for (RewardTemplate template : templates)
            if (Rnd.chance(template.getChance()))
                player.getInventory().addItem(template.getId(), template.getCount());
        Clan clan = player.getClan();
        if (clan != null && crp > 0)
            clan.incReputation(crp, false, "Olympiad win");
        if (fame > 0)
            player.setFame(player.getFame() + fame, "Olympiad win");
    }

    public void addBuffers() {
        if (!_type.hasBuffer()) {
            return;
        }

        _reflection.spawnByGroup("olympiad_" + _reflection.getInstancedZoneId() + "_buffers");
    }

    public void deleteBuffers() {
        _reflection.despawnByGroup("olympiad_" + _reflection.getInstancedZoneId() + "_buffers");
    }

    public void managerShout() {
        for (final NpcInstance npc : Olympiad.getNpcs()) {
            final NpcString npcString;
            switch (_type) {
                case TEAM:
                    npcString = NpcString.OLYMPIAD_CLASSFREE_TEAM_MATCH_IS_GOING_TO_BEGIN_IN_ARENA_S1_IN_A_MOMENT;
                    break;
                case CLASSED:
                    npcString = NpcString.OLYMPIAD_CLASS_INDIVIDUAL_MATCH_IS_GOING_TO_BEGIN_IN_ARENA_S1_IN_A_MOMENT;
                    break;
                case NON_CLASSED:
                    npcString = NpcString.OLYMPIAD_CLASSFREE_INDIVIDUAL_MATCH_IS_GOING_TO_BEGIN_IN_ARENA_S1_IN_A_MOMENT;
                    break;
                default:
                    continue;
            }
            ChatUtils.shout(npc, npcString, String.valueOf(_id + 1));
        }
    }

    public void portPlayersToArena() {
        _team1.portPlayersToArena();
        _team2.portPlayersToArena();
    }

    public void preparePlayers() {
        _team1.preparePlayers();
        _team2.preparePlayers();
    }

    public void portPlayersBack() {
        _team1.portPlayersBack();
        _team2.portPlayersBack();
    }

    public void heal() {
        _team1.heal();
        _team2.heal();
    }

    public void collapse() {
        portPlayersBack();
        clearSpectators();
        _reflection.collapse();
    }

    public void validateWinner(final boolean aborted) {
        final int state = _state;
        _state = 0;

        if (validated) {
            Log.add("Olympiad Result: " + _team1.getName() + " vs " + _team2.getName() + " ... double validate check!!!", "olympiad");
            return;
        }
        validated = true;

        // Если игра закончилась до телепортации на стадион, то забираем очки у вышедших из игры, не засчитывая никому победу
        if (state < 1 && aborted) {
            _team1.takePointsForCrash();
            _team2.takePointsForCrash();
            broadcastPacket(SystemMsg.YOUR_OPPONENT_MADE_HASTE_WITH_THEIR_TAIL_BETWEEN_THEIR_LEGS_THE_MATCH_HAS_BEEN_CANCELLED, true, false);
            return;
        }

        final boolean teamOneCheck = _team1.checkPlayers();
        final boolean teamTwoCheck = _team2.checkPlayers();

        if (_winner <= 0) {
            if (!teamOneCheck && !teamTwoCheck) {
                _winner = 0;
            } else if (!teamTwoCheck) {
                _winner = 1; // Выиграла первая команда
            } else if (!teamOneCheck) {
                _winner = 2; // Выиграла вторая команда
            } else if (_team2.getDamage() < _team1.getDamage()) // Вторая команда нанесла вреда меньше, чем первая
            {
                _winner = 1; // Выиграла первая команда
            } else if (_team2.getDamage() > _team1.getDamage()) // Вторая команда нанесла вреда больше, чем первая
            {
                _winner = 2; // Выиграла вторая команда
            }
        }

        if (_winner == 1) // Выиграла первая команда
        {
            winGame(_team1, _team2);
        } else if (_winner == 2) // Выиграла вторая команда
        {
            winGame(_team2, _team1);
        } else {
            tie();
        }

        _team1.saveNobleData();
        _team2.saveNobleData();

        broadcastRelation();

        _team1.revive();
        _team2.revive();
    }

    public void winGame(final OlympiadTeam winnerTeam, final OlympiadTeam looseTeam) {
        if (OlympiadConfig.OLYMPIAD_POINT_TRANSFER) {
            calendar = Calendar.getInstance();
            nowTime = calendar.getTimeInMillis();
            calendar.set(Calendar.HOUR_OF_DAY, OlympiadConfig.OLYMPIAD_POINT_TRANSFER_TIMES[0]);
            startPointTransferTime = calendar.getTimeInMillis();
            calendar.set(Calendar.HOUR_OF_DAY, OlympiadConfig.OLYMPIAD_POINT_TRANSFER_TIMES[1]);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            stopPointTransferTime = calendar.getTimeInMillis();
        }
        final ExReceiveOlympiad.MatchResult packet = new ExReceiveOlympiad.MatchResult(false, winnerTeam.getName());

        int pointDiff = 0;

        final TeamMember[] looserMembers = looseTeam.getMembers().toArray(new TeamMember[looseTeam.getMembers().size()]);
        final TeamMember[] winnerMembers = winnerTeam.getMembers().toArray(new TeamMember[winnerTeam.getMembers().size()]);

        for (int i = 0; i < Party.MAX_SIZE; i++) {
            final TeamMember looserMember = ArrayUtils.valid(looserMembers, i);
            final TeamMember winnerMember = ArrayUtils.valid(winnerMembers, i);
            if (looserMember != null && winnerMember != null) {
                winnerMember.incGameCount();
                looserMember.incGameCount();

                addCustomRewards(winnerMember, true);
                addCustomRewards(looserMember, false);
                if (OlympiadConfig.OLYMPIAD_POINT_TRANSFER) {
                    if (nowTime >= startPointTransferTime && nowTime < stopPointTransferTime) {
                        final int gamePoints = transferPoints(looserMember.getStat(), winnerMember.getStat());
                        packet.addPlayer(winnerTeam == _team1 ? 0 : 1, winnerMember, gamePoints);
                        packet.addPlayer(looseTeam == _team1 ? 0 : 1, looserMember, -gamePoints);
                        pointDiff += gamePoints;
                    }
                } else {
                    final int gamePoints = transferPoints(looserMember.getStat(), winnerMember.getStat());
                    packet.addPlayer(winnerTeam == _team1 ? 0 : 1, winnerMember, gamePoints);
                    packet.addPlayer(looseTeam == _team1 ? 0 : 1, looserMember, -gamePoints);
                    pointDiff += gamePoints;
                }
            }
        }

        if (OlympiadConfig.OLYMPIAD_POINT_TRANSFER) {
            if (nowTime >= startPointTransferTime && nowTime < stopPointTransferTime) {
                if (_type != CompType.TEAM) {
                    final int team = _team1 == winnerTeam ? 1 : 2;

                    final TeamMember member1 = ArrayUtils.valid(_team1 == winnerTeam ? winnerMembers : looserMembers, 0);
                    final TeamMember member2 = ArrayUtils.valid(_team2 == winnerTeam ? winnerMembers : looserMembers, 0);
                    if (member1 != null && member2 != null) {
                        final int diff = (int) Duration.between(Instant.now(), _startTime).getSeconds();
                        final OlympiadHistory h = new OlympiadHistory(member1.getObjectId(), member1.getObjectId(), member1.getClassId(), member2.getClassId(),
                                member1.getName(), member2.getName(), _startTime, diff, team, _type.ordinal());

                        OlympiadHistoryManager.getInstance().saveHistory(h);
                    }
                }
            }
        } else {
            if (_type != CompType.TEAM) {
                final int team = _team1 == winnerTeam ? 1 : 2;

                final TeamMember member1 = ArrayUtils.valid(_team1 == winnerTeam ? winnerMembers : looserMembers, 0);
                final TeamMember member2 = ArrayUtils.valid(_team2 == winnerTeam ? winnerMembers : looserMembers, 0);
                if (member1 != null && member2 != null) {
                    final int diff = (int) Duration.between(Instant.now(), _startTime).getSeconds();
                    final OlympiadHistory h = new OlympiadHistory(member1.getObjectId(), member1.getObjectId(), member1.getClassId(), member2.getClassId(),
                            member1.getName(), member2.getName(), _startTime, diff, team, _type.ordinal());

                    OlympiadHistoryManager.getInstance().saveHistory(h);
                }
            }
        }

        _team1.removeBuffs(false);
        _team2.removeBuffs(false);

        broadcastPacket(new SystemMessage(SystemMsg.CONGRATULATIONS_C1_YOU_WIN_THE_MATCH).addString(winnerTeam.getName()), true, true);
        if (OlympiadConfig.OLYMPIAD_POINT_TRANSFER) {
            if (nowTime >= startPointTransferTime && nowTime < stopPointTransferTime) {
                winnerTeam.broadcast(new SystemMessage(SystemMsg.C1_HAS_EARNED_S2_POINTS_IN_THE_GRAND_OLYMPIAD_GAMES).addString(winnerTeam.getName()).addNumber(pointDiff));
                looseTeam.broadcast(new SystemMessage(SystemMsg.C1_HAS_LOST_S2_POINTS_IN_THE_GRAND_OLYMPIAD_GAMES).addString(looseTeam.getName()).addNumber(pointDiff));
            }
        } else {
            winnerTeam.broadcast(new SystemMessage(SystemMsg.C1_HAS_EARNED_S2_POINTS_IN_THE_GRAND_OLYMPIAD_GAMES).addString(winnerTeam.getName()).addNumber(pointDiff));
            looseTeam.broadcast(new SystemMessage(SystemMsg.C1_HAS_LOST_S2_POINTS_IN_THE_GRAND_OLYMPIAD_GAMES).addString(looseTeam.getName()).addNumber(pointDiff));
        }


        if (OlympiadConfig.OLYMPIAD_POINT_TRANSFER) {
            if (nowTime >= startPointTransferTime && nowTime < stopPointTransferTime) {
                for (final Player player : winnerTeam.getPlayers()) {
                    final ItemInstance item = player.getInventory().addItem(OlympiadConfig.ALT_OLY_BATTLE_REWARD_ITEM, getType().getReward());
                    player.sendPacket(SystemMessage.obtainItems(item.getItemId(), getType().getReward(), 0));
                    player.sendChanges();
                }
            }
        } else {
            for (final Player player : winnerTeam.getPlayers()) {
                final ItemInstance item = player.getInventory().addItem(OlympiadConfig.ALT_OLY_BATTLE_REWARD_ITEM, getType().getReward());
                player.sendPacket(SystemMessage.obtainItems(item.getItemId(), getType().getReward(), 0));
                player.sendChanges();
            }
        }


        final List<Player> teamsPlayers = new ArrayList<>();
        teamsPlayers.addAll(winnerTeam.getPlayers());
        teamsPlayers.addAll(looseTeam.getPlayers());
        for (final Player player : teamsPlayers) {
            if (player != null) {
                for (final QuestState qs : player.getAllQuestsStates()) {
                    if (qs.isStarted()) {
                        qs.getQuest().onOlympiadEnd(this, qs);
                    }
                }
            }
        }

        broadcastPacket(packet, true, false);

        Log.add("Olympiad Result: " + winnerTeam.getName() + " vs " + looseTeam.getName() + " ... (" + (int) winnerTeam.getDamage() + " vs " + (int) looseTeam.getDamage() + ") " + winnerTeam.getName() + " win " + pointDiff + " points", "olympiad");
    }

    // FIXME: DS: количество очков для ничьи
    public void tie() {
        if (OlympiadConfig.OLYMPIAD_POINT_TRANSFER) {
            calendar = Calendar.getInstance();
            nowTime = calendar.getTimeInMillis();
            calendar.set(Calendar.HOUR_OF_DAY, OlympiadConfig.OLYMPIAD_POINT_TRANSFER_TIMES[0]);
            startPointTransferTime = calendar.getTimeInMillis();
            calendar.set(Calendar.HOUR_OF_DAY, OlympiadConfig.OLYMPIAD_POINT_TRANSFER_TIMES[1]);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            stopPointTransferTime = calendar.getTimeInMillis();
        }
        final TeamMember[] teamMembers1 = _team1.getMembers().toArray(new TeamMember[_team1.getMembers().size()]);
        final TeamMember[] teamMembers2 = _team2.getMembers().toArray(new TeamMember[_team2.getMembers().size()]);

        final ExReceiveOlympiad.MatchResult packet = new ExReceiveOlympiad.MatchResult(true, StringUtils.EMPTY);
        for (int i = 0; i < teamMembers1.length; i++) {
            try {
                final TeamMember member1 = ArrayUtils.valid(teamMembers1, i);
                final TeamMember member2 = ArrayUtils.valid(teamMembers2, i);

                if (member1 != null) {
                    member1.incGameCount();
                    final StatsSet stat1 = member1.getStat();
                    packet.addPlayer(0, member1, -2);
                    if (OlympiadConfig.OLYMPIAD_POINT_TRANSFER) {
                        if (nowTime >= startPointTransferTime && nowTime < stopPointTransferTime) {
                            stat1.set(Olympiad.POINTS, stat1.getInteger(Olympiad.POINTS) - 2);
                        }
                    } else {
                        stat1.set(Olympiad.POINTS, stat1.getInteger(Olympiad.POINTS) - 2);
                    }
                }

                if (member2 != null) {
                    member2.incGameCount();
                    final StatsSet stat2 = member2.getStat();
                    packet.addPlayer(1, member2, -2);
                    if (OlympiadConfig.OLYMPIAD_POINT_TRANSFER) {
                        if (nowTime >= startPointTransferTime && nowTime < stopPointTransferTime) {
                            stat2.set(Olympiad.POINTS, stat2.getInteger(Olympiad.POINTS) - 2);
                        }
                    } else {
                        stat2.set(Olympiad.POINTS, stat2.getInteger(Olympiad.POINTS) - 2);
                    }
                }
            } catch (Exception e) {
                _log.error("OlympiadGame.tie(): " + e, e);
            }
        }

        if (OlympiadConfig.OLYMPIAD_POINT_TRANSFER) {
            if (nowTime >= startPointTransferTime && nowTime < stopPointTransferTime) {
                if (_type != CompType.TEAM) {
                    final TeamMember member1 = ArrayUtils.valid(teamMembers1, 0);
                    final TeamMember member2 = ArrayUtils.valid(teamMembers2, 0);
                    if (member1 != null && member2 != null) {
                        final int diff = (int) Duration.between(Instant.now(), _startTime).getSeconds();
                        final OlympiadHistory h = new OlympiadHistory(member1.getObjectId(), member1.getObjectId(), member1.getClassId(), member2.getClassId(),
                                member1.getName(), member2.getName(), _startTime, diff, 0, _type.ordinal());

                        OlympiadHistoryManager.getInstance().saveHistory(h);
                    }
                }
            }
        } else {
            if (_type != CompType.TEAM) {
                final TeamMember member1 = ArrayUtils.valid(teamMembers1, 0);
                final TeamMember member2 = ArrayUtils.valid(teamMembers2, 0);
                if (member1 != null && member2 != null) {
                    final int diff = (int) Duration.between(Instant.now(), _startTime).getSeconds();
                    final OlympiadHistory h = new OlympiadHistory(member1.getObjectId(), member1.getObjectId(), member1.getClassId(), member2.getClassId(),
                            member1.getName(), member2.getName(), _startTime, diff, 0, _type.ordinal());

                    OlympiadHistoryManager.getInstance().saveHistory(h);
                }
            }
        }


        broadcastPacket(SystemMsg.THERE_IS_NO_VICTOR_THE_MATCH_ENDS_IN_A_TIE, true, true);
        if (OlympiadConfig.OLYMPIAD_POINT_TRANSFER) {
            if (nowTime >= startPointTransferTime && nowTime < stopPointTransferTime) {
                _team1.broadcast(new SystemMessage(SystemMsg.C1_HAS_LOST_S2_POINTS_IN_THE_GRAND_OLYMPIAD_GAMES).addString(_team1.getName()).addNumber(2));
                _team2.broadcast(new SystemMessage(SystemMsg.C1_HAS_LOST_S2_POINTS_IN_THE_GRAND_OLYMPIAD_GAMES).addString(_team2.getName()).addNumber(2));
            }
        } else {
            _team1.broadcast(new SystemMessage(SystemMsg.C1_HAS_LOST_S2_POINTS_IN_THE_GRAND_OLYMPIAD_GAMES).addString(_team1.getName()).addNumber(2));
            _team2.broadcast(new SystemMessage(SystemMsg.C1_HAS_LOST_S2_POINTS_IN_THE_GRAND_OLYMPIAD_GAMES).addString(_team2.getName()).addNumber(2));
        }
        broadcastPacket(packet, true, false);

        _team1.removeBuffs(false);
        _team2.removeBuffs(false);

        Log.add("Olympiad Result: " + _team1.getName() + " vs " + _team2.getName() + " ... tie", "olympiad");
    }

    private int transferPoints(final StatsSet from, final StatsSet to) {
        int fromPoints = from.getInteger(Olympiad.POINTS);
        int fromLoose = from.getInteger(Olympiad.COMP_LOOSE);
        int fromPlayed = from.getInteger(Olympiad.COMP_DONE);

        int toPoints = to.getInteger(Olympiad.POINTS);
        int toWin = to.getInteger(Olympiad.COMP_WIN);
        int toPlayed = to.getInteger(Olympiad.COMP_DONE);

        int pointDiff = Math.max(1, (int) Math.ceil((double) Math.min(fromPoints, toPoints) / getType().getLooseMult()));
        pointDiff = pointDiff > OlympiadGame.MAX_POINTS_LOOSE ? OlympiadGame.MAX_POINTS_LOOSE : pointDiff;

        from.set(Olympiad.POINTS, fromPoints - pointDiff);
        from.set(Olympiad.COMP_LOOSE, fromLoose + 1);
        from.set(Olympiad.COMP_DONE, fromPlayed + 1);

        to.set(Olympiad.POINTS, toPoints + pointDiff);
        to.set(Olympiad.COMP_WIN, toWin + 1);
        to.set(Olympiad.COMP_DONE, toPlayed + 1);

        return pointDiff;
    }

    public void openDoors() {
        for (final DoorInstance door : _reflection.getDoors()) {
            door.openMe();
        }
    }

    public int getId() {
        return _id;
    }

    public Reflection getReflection() {
        return _reflection;
    }

    public boolean isRegistered(final int objId) {
        return _team1.contains(objId) || _team2.contains(objId);
    }

    public List<Player> getSpectators() {
        return _spectators;
    }

    public void addSpectator(final Player spec) {
        _spectators.add(spec);
    }

    public void removeSpectator(final Player spec) {
        _spectators.remove(spec);
    }

    public void clearSpectators() {
        for (final Player pc : _spectators) {
            if (pc != null && pc.isInObserverMode()) {
                pc.leaveOlympiadObserverMode(false);
            }
        }
        _spectators.clear();
    }

    public void broadcastInfo(final Player sender, final Player receiver, final boolean onlyToSpectators) {
        // TODO заюзать пакеты:
        // ExEventMatchCreate
        // ExEventMatchFirecracker
        // ExEventMatchManage
        // ExEventMatchMessage
        // ExEventMatchObserver
        // ExEventMatchScore
        // ExEventMatchTeamInfo
        // ExEventMatchTeamUnlocked
        // ExEventMatchUserInfo

        if (sender != null) {
            if (receiver != null) {
                receiver.sendPacket(new ExOlympiadUserInfo(sender, sender.getOlympiadSide()));
            } else {
                broadcastPacket(new ExOlympiadUserInfo(sender, sender.getOlympiadSide()), !onlyToSpectators, true);
            }
        } else {
            // Рассылаем информацию о первой команде
            for (final Player player : _team1.getPlayers()) {
                if (receiver != null) {
                    receiver.sendPacket(new ExOlympiadUserInfo(player, player.getOlympiadSide()));
                } else {
                    broadcastPacket(new ExOlympiadUserInfo(player, player.getOlympiadSide()), !onlyToSpectators, true);
                    player.broadcastRelationChanged();
                }
            }

            // Рассылаем информацию о второй команде
            for (final Player player : _team2.getPlayers()) {
                if (receiver != null) {
                    receiver.sendPacket(new ExOlympiadUserInfo(player, player.getOlympiadSide()));
                } else {
                    broadcastPacket(new ExOlympiadUserInfo(player, player.getOlympiadSide()), !onlyToSpectators, true);
                    player.broadcastRelationChanged();
                }
            }
        }
    }

    public void broadcastRelation() {
        for (final Player player : _team1.getPlayers()) {
            player.broadcastRelationChanged();
        }

        for (final Player player : _team2.getPlayers()) {
            player.broadcastRelationChanged();
        }
    }

    public void broadcastPacket(final L2GameServerPacket packet, final boolean toTeams, final boolean toSpectators) {
        if (toTeams) {
            _team1.broadcast(packet);
            _team2.broadcast(packet);
        }

        if (toSpectators && !_spectators.isEmpty()) {
            for (final Player spec : _spectators) {
                if (spec != null) {
                    spec.sendPacket(packet);
                }
            }
        }
    }

    public void broadcastPacket(final IBroadcastPacket packet, final boolean toTeams, final boolean toSpectators) {
        if (toTeams) {
            _team1.broadcast(packet);
            _team2.broadcast(packet);
        }

        if (toSpectators && !_spectators.isEmpty()) {
            for (final Player spec : _spectators) {
                if (spec != null) {
                    spec.sendPacket(packet);
                }
            }
        }
    }

    public List<Player> getAllPlayers() {
        final List<Player> result = new ArrayList<>();
        for (final Player player : _team1.getPlayers()) {
            result.add(player);
        }
        for (final Player player : _team2.getPlayers()) {
            result.add(player);
        }
        if (!_spectators.isEmpty()) {
            for (final Player spec : _spectators) {
                if (spec != null) {
                    result.add(spec);
                }
            }
        }
        return result;
    }

    public List<Player> getAllParticipants() {
        List<Player> res = new ArrayList<>();
        res.addAll(_team1.getPlayers());
        res.addAll(_team2.getPlayers());
        return res;
    }

    public void setWinner(final int val) {
        _winner = val;
    }

    public OlympiadTeam getWinnerTeam() {
        if (_winner == 1) // Выиграла первая команда
        {
            return _team1;
        } else if (_winner == 2) // Выиграла вторая команда
        {
            return _team2;
        }
        return null;
    }

    public int getState() {
        return _state;
    }

    public void setState(final int val) {
        _state = val;
        if (_state == 1) {
            _startTime = Instant.now();
        }
    }

    public List<Player> getTeamMembers(final Player player) {
        return player.getOlympiadSide() == 1 ? _team1.getPlayers() : _team2.getPlayers();
    }

    public void addDamage(final Player player, final double damage) {
        if (player.getOlympiadSide() == 1) {
            _team1.addDamage(player, damage);
        } else {
            _team2.addDamage(player, damage);
        }
    }

    public boolean doDie(final Player player) {
        return player.getOlympiadSide() == 1 ? _team1.doDie(player) : _team2.doDie(player);
    }

    public boolean checkPlayersOnline() {
        return _team1.checkPlayers() && _team2.checkPlayers();
    }

    public boolean logoutPlayer(final Player player) {
        return player != null && (player.getOlympiadSide() == 1 ? _team1.logout(player) : _team2.logout(player));
    }

    public synchronized void sheduleTask(final OlympiadGameTask task) {
        if (_shedule != null) {
            _shedule.cancel(false);
        }
        _task = task;
        _shedule = task.shedule();
    }

    public OlympiadGameTask getTask() {
        return _task;
    }

    public BattleStatus getStatus() {
        if (_task != null) {
            return _task.getStatus();
        }
        return BattleStatus.Begining;
    }

    public void endGame(final int time, final boolean aborted) {
        try {
            validateWinner(aborted);
        } catch (Exception e) {
            _log.error("", e);
        }

        sheduleTask(new OlympiadGameTask(this, time > 1 ? BattleStatus.PortBack : BattleStatus.Ending, time, 100));
    }

    public CompType getType() {
        return _type;
    }

    public String getTeamName1() {
        return _team1.getName();
    }

    public String getTeamName2() {
        return _team2.getName();
    }

    public int getPointLoose() {
        return pointLoose;
    }

    private static class RewardTemplate {
        private int id;
        private int count;
        private double chance;

        public RewardTemplate(int id, int count, double chance) {
            this.id = id;
            this.count = count;
            this.chance = chance;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public double getChance() {
            return chance;
        }

        public void setChance(double chance) {
            this.chance = chance;
        }
    }
}