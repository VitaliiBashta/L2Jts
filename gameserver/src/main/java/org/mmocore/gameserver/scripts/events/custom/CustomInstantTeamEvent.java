package org.mmocore.gameserver.scripts.events.custom;

import com.google.common.collect.Iterators;
import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.commons.utils.QuartzUtils;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.data.StringHolder;
import org.mmocore.gameserver.data.xml.holder.InstantZoneHolder;
import org.mmocore.gameserver.listener.PlayerListener;
import org.mmocore.gameserver.listener.actor.player.OnPlayerExitListener;
import org.mmocore.gameserver.listener.actor.player.OnTeleportListener;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.base.TeamType;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.entity.events.EventType;
import org.mmocore.gameserver.model.entity.events.impl.SingleMatchEvent;
import org.mmocore.gameserver.model.entity.events.objects.DuelSnapshotObject;
import org.mmocore.gameserver.model.entity.olympiad.Olympiad;
import org.mmocore.gameserver.model.team.CommandChannel;
import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.model.zone.ZoneType;
import org.mmocore.gameserver.network.lineage.components.*;
import org.mmocore.gameserver.network.lineage.serverpackets.ConfirmDlg;
import org.mmocore.gameserver.network.lineage.serverpackets.PlaySound;
import org.mmocore.gameserver.network.lineage.serverpackets.Say2;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.community.Buff;
import org.mmocore.gameserver.object.components.player.community.BuffTask;
import org.mmocore.gameserver.utils.AnnouncementUtils;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.world.GameObjectsStorage;
import org.quartz.CronExpression;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author VISTALL
 * @date 15:10/03.04.2012
 */
public abstract class CustomInstantTeamEvent extends SingleMatchEvent implements Iterable<DuelSnapshotObject> {
    public static final String REGISTRATION = "registration";
    public static final String HWID = "hwid";
    // rewards
    protected final int _minLevel;
    protected final int _maxLevel;
    protected final boolean _levelMul;
    protected final int[] _rewardItems;
    protected final long[] _rewardCounts;
    protected final int[] _killRewardItems;
    protected final long[] _killRewardCounts;
    protected final int _fameReward;
    protected final boolean _isOnlyUniquePlayers;
    private final CronExpression _pattern;
    protected State _state = State.NONE;
    protected TeamType _winner = TeamType.NONE;
    protected Reflection _reflection = new EventReflection(-getId());
    private Set<String> hwids = new HashSet<>();
    // times
    private Instant _startTime;
    private boolean _registrationOver = true;
    private PlayerListener _playerListeners = new PlayerListeners();

    protected CustomInstantTeamEvent(MultiValueSet<String> set) {
        super(set);
        _pattern = QuartzUtils.createCronExpression(set.getString("pattern"));
        _minLevel = set.getInteger("min_level");
        _maxLevel = set.getInteger("max_level");
        _rewardItems = set.getIntegerArray("reward_items");
        _rewardCounts = set.getLongArray("reward_counts");
        _killRewardItems = set.getIntegerArray("kill_reward_items");
        _killRewardCounts = set.getLongArray("kill_reward_counts");
        _levelMul = set.getBool("reward_level_mul", false);
        _fameReward = set.getInteger("fame_reward");
        _isOnlyUniquePlayers = set.getBool("is_unique_player", false);
    }

    protected static void setParties(List<Player> team) {
        if (team == null || team.size() == 0)
            return;
        double membersCount = 9;
        int partyCount = (int) Math.ceil(team.size() / membersCount);
        List<Player> partyLeaders = new ArrayList<>(); // использованные плы удаляются из этого листа
        List<Party> parties = new ArrayList<>();

        for (Player player : team) { // рвем пати и набираем лидеров, отвечающих критериям
            if (player.getParty() != null)
                player.getParty().removePartyMember(player, false);
            if (isCanBeLeader(player))
                partyLeaders.add(player);
        }

        while (partyLeaders.size() < partyCount) { // добираем лидеров рандомно
            Player possibleLeader = team.get(Rnd.get(team.size()));
            if (possibleLeader != null && !possibleLeader.isInParty() && !partyLeaders.contains(possibleLeader))
                partyLeaders.add(possibleLeader);
        }

        Party party = null;
        for (Player player : team) { // создаем и набираем пати
            if (player == null || player.isInParty() || partyLeaders.contains(player))
                continue;
            if (party == null) {
                Player leader = partyLeaders.get(Rnd.get(partyLeaders.size()));
                party = new Party(leader, 0);
                leader.setParty(party);
                partyLeaders.remove(leader);
                parties.add(party);
            }
            if (!player.isInParty())
                player.joinParty(party);
            if (party.getPartyMembers().size() >= membersCount)
                party = null;
        }
        if (party != null && party.getPartyMembers().size() < 2) // если последняя пати не набралась, то рвем ее
            for (Player player : party.getPartyMembers())
                player.getParty().removePartyMember(player, false);

        if (parties.size() < 1)
            return;
        Player channelLeader = parties.get(Rnd.get(parties.size())).getGroupLeader(); // случайно выбираем лидера цц
        parties.remove(channelLeader.getParty()); // пати лидера цц добавляется в конструкторе
        CommandChannel channel = new CommandChannel(channelLeader);
        for (Party p : parties) // набираем пати в цц
            channel.addParty(p);
    }

    private static boolean isCanBeLeader(Player player) {
        // в качестве лидеров берем бишей и кардиналов, чтобы в каждой пати был как минимум один хилл
        return player.getPlayerClassComponent().getActiveClassId() == 16 || player.getPlayerClassComponent().getActiveClassId() == 97;
    }

    protected static void buffList(Map<Integer, Integer> list, Playable target) {
        new BuffTask(list.entrySet().stream().map(entry ->
                new Buff(entry.getKey(), entry.getValue())).collect(Collectors.toList()), target);
    }

    protected static void buffPlayerPet(Map<Integer, Integer> list, Player player) {
        buffList(list, player);
        if (player.getServitor() != null)
            buffList(list, player.getServitor());
    }

    protected abstract String getEventName(Player player);

    //region Abstracts
    protected abstract int getInstantId();

    protected abstract Location getTeleportLoc(TeamType team);

    protected abstract void checkForWinner();

    protected abstract boolean canWalkInWaitTime();
    //endregion

    protected abstract void onTeleportOrExit(List<DuelSnapshotObject> objects, DuelSnapshotObject duelSnapshotObject, boolean exit);

    //region Start&Stop and player actions
    @Override
    public void teleportPlayers(String name, ZoneType zoneType) {
        _registrationOver = true;

        _state = State.TELEPORT_PLAYERS;

        for (TeamType team : TeamType.VALUES) {
            List<DuelSnapshotObject> list = getObjects(team);
            for (DuelSnapshotObject object : list) {
                Player player = object.getPlayer();
                if (!checkPlayer(player, false)) {
                    player.sendPacket(new HtmlMessage(0).setFile("org/mmocore/gameserver/scripts/events/custom_event_cancel.htm"));
                    list.remove(object);
                }
            }
        }

        if (getObjects(TeamType.RED).isEmpty() || getObjects(TeamType.BLUE).isEmpty()) {
            reCalcNextTime(false);

            announceToPlayersWithValidLevel(getClass().getSimpleName() + ".Cancelled");

            return;
        }

        setRegistrationOver(true); // �������� �������

        for (TeamType team : TeamType.VALUES) {
            List<DuelSnapshotObject> objects = getObjects(team);

            for (DuelSnapshotObject object : objects) {
                Player player = object.getPlayer();

                if (!canWalkInWaitTime())
                    player.startFrozen();

                object.store();

                player._stablePoint = object.getReturnLoc();
                player.teleToLocation(getTeleportLoc(team), _reflection);
            }
        }
    }

    protected List<Player> getPlayers(TeamType teamType) {
        List<DuelSnapshotObject> listObj = getObjects(teamType);
        List<Player> listP = new ArrayList<>();
        for (DuelSnapshotObject obj : listObj)
            listP.add(obj.getPlayer());
        return listP;
    }

    @Override
    public void startEvent() {
        _winner = TeamType.NONE;
        for (DuelSnapshotObject object : this) {
            Player player = object.getPlayer();
            if (!checkPlayer(player, true)) {
                removeObject(object.getTeam(), object);

                player.removeEvent(this);

                if (player.isTeleporting()) {
                    //player.setXYZ(object.getLoc().x, object.getLoc().y, object.getLoc().z);  // �������� ����
                    player.setReflection(ReflectionManager.DEFAULT);
                    _log.debug("TvT: player teleporting error:", player);
                } else
                    object.teleportBack();
            }
        }

        if (getObjects(TeamType.RED).isEmpty() || getObjects(TeamType.BLUE).isEmpty()) {
            _state = State.NONE;

            reCalcNextTime(false);

            announceToPlayersWithValidLevel(getClass().getSimpleName() + ".Cancelled");

            return;
        }

        _state = State.STARTED;

        updatePlayers(true, false);

        sendPackets(PlaySound.B04_S01, SystemMsg.LET_THE_DUEL_BEGIN);

        super.startEvent();
    }

    @Override
    public void stopEvent() {
        if (_state != State.STARTED)
            return;

        int maxPoints = 0;
        TeamType topTeam = null;
        for (TeamType team : TeamType.VALUES)
            if (team.getPoints() > maxPoints) {
                maxPoints = team.getPoints();
                topTeam = team;
            }
        _winner = maxPoints > 0 ? topTeam : TeamType.NONE;

        clearActions();

        _state = State.NONE;

        updatePlayers(false, false);

        switch (_winner) {
            case NONE:
                sendPacket(SystemMsg.THE_DUEL_HAS_ENDED_IN_A_TIE);
                break;
            case RED:
            case BLUE:
                List<DuelSnapshotObject> winners = getObjects(_winner);

//				sendPacket(new SystemMessage(_winner == TeamType.RED ? SystemMsg.THE_RED_TEAM_IS_VICTORIOUS : SystemMsg.THE_BLUE_TEAM_IS_VICTORIOUS));
                boolean isRedVictory = _winner == TeamType.RED;
                AnnouncementUtils.announceMultilang("Победила команда " + (isRedVictory ? "Красных" : "Синих") + "!",
                        (isRedVictory ? "Red" : "Blue") + " team won!");

                for (DuelSnapshotObject d : winners)
                    for (int i = 0; i < _rewardItems.length; i++)
                        if (d.getPlayer() != null) {
                            ItemFunctions.addItem(d.getPlayer(), _rewardItems[i], _levelMul ? _rewardCounts[i] * d.getPlayer().getLevel() : _rewardCounts[i]);
                            if (_fameReward > 0)
                                d.getPlayer().setFame(d.getPlayer().getFame() + _fameReward, getClass().getSimpleName());
                        }
                break;
        }

        updatePlayers(false, true);
        removeObjects(TeamType.RED);
        removeObjects(TeamType.BLUE);

        removeObjects(HWID);
        hwids.clear();

        reCalcNextTime(false);

        super.stopEvent();
    }

    protected void updatePlayers(boolean start, boolean teleport) {
        for (DuelSnapshotObject $snapshot : this) {
            if ($snapshot.getPlayer() == null)
                continue;

            if (teleport)
                $snapshot.teleportBack();
            else {
                Player $player = $snapshot.getPlayer();
                if ($player.isInvul())
                    $player.setIsInvul(false);
                if ($player.isDamageBlocked())
                    $player.stopDamageBlocked();

                if (start) {
                    $player.stopFrozen();
                    $player.setTeam($snapshot.getTeam());

                    $player.setCurrentMp($player.getMaxMp());
                    $player.setCurrentCp($player.getMaxCp());
                    $player.setCurrentHp($player.getMaxHp(), true);
                } else {
                    $player.startFrozen();
                    $player.removeEvent(this);

                    GameObject target = $player.getTarget();
                    if (target != null)
                        $player.getAI().notifyEvent(CtrlEvent.EVT_FORGET_OBJECT, target);

                    $snapshot.restore();
                    $player.setTeam(TeamType.NONE);
                    if ($player.getParty() != null)
                        $player.leaveParty();
                }

                actionUpdate(start, $player);
            }
        }
    }

    protected void actionUpdate(boolean start, Player player) {
    }

    //region Broadcast
    @Override
    public void sendPacket(IBroadcastPacket packet) {
        sendPackets(packet);
    }
    //endregion

    @Override
    public void sendPackets(IBroadcastPacket... packet) {
        for (DuelSnapshotObject d : this)
            if (d.getPlayer() != null)
                d.getPlayer().sendPacket(packet);
    }

    public void sendPacket(IBroadcastPacket packet, TeamType... ar) {
        for (TeamType a : ar) {
            final List<DuelSnapshotObject> objs = getObjects(a);
            objs.stream().filter(d -> d.getPlayer() != null).forEach(d -> d.getPlayer().sendPacket(packet));
        }
    }

    //region Registration
    private boolean checkPlayer(Player player, boolean second) {
        if (player.isInOfflineMode())
            return false;

        if (player.getLevel() > _maxLevel || player.getLevel() < _minLevel)
            return false;

        if (player.isMounted() || player.isDead() || player.isInObserverMode())
            return false;

        final SingleMatchEvent evt = player.getEvent(SingleMatchEvent.class);
        if (evt != null && evt != this)
            return false;

        if (player.getTeam() != TeamType.NONE)
            return false;

        if (player.getOlympiadGame() != null || Olympiad.isRegistered(player))
            return false;

        if (player.isInParty() && player.getParty().isInDimensionalRift())
            return false;

        if (player.isTeleporting())
            return false;

        if (!second) {
            if (player.getReflection() != ReflectionManager.DEFAULT)
                return false;

            if (player.isInZone(ZoneType.epic))
                return false;
        }

        return true;
    }
    //endregion

    public boolean registerPlayer(Player player) {
        if (_registrationOver)
            return false;
        for (DuelSnapshotObject d : this)
            if (d.getPlayer() == player)
                return false;

        if (!checkPlayer(player, false))
            return false;

        if (_isOnlyUniquePlayers && player.getHwid() != null) {
            if (hwids.contains(player.getHwid())) {
                player.sendMessage("Just one player from this PC can register.");
                return false;
            } else
                hwids.add(player.getHwid());
        }

        List<DuelSnapshotObject> blue = getObjects(TeamType.BLUE);
        List<DuelSnapshotObject> red = getObjects(TeamType.RED);
        if (blue.size() >= 100 && red.size() >= 100)
            return false;
        TeamType team;
        if (blue.size() == red.size())
            team = Rnd.get(TeamType.VALUES);
        else if (blue.size() > red.size())
            team = TeamType.RED;
        else
            team = TeamType.BLUE;

        addObject(team, new DuelSnapshotObject(player, team, false));

        player.addEvent(this);
        player.addListener(_playerListeners);
        player.sendMessage("You are successfully registered as a participant of the event!");
        return true;
    }

    protected void announceToPlayersWithValidLevel(String str) {
        GameObjectsStorage.getPlayers().stream().filter(player -> player.getPlayerTemplateComponent() != null && (player.isGM() || player.getLevel() >= _minLevel && player.getLevel() <= _maxLevel)).forEach(player -> player.sendPacket(new Say2(0, ChatType.ANNOUNCEMENT, "", String.format(StringHolder.getInstance().getString(str, player), _minLevel, _maxLevel), null)));
    }

    @Override
    public boolean isInProgress() {
        return _state == State.STARTED;
    }

    @Override
    public void action(String name, boolean start) {
        if (name.equalsIgnoreCase(REGISTRATION))
            setRegistrationOver(!start);
        else
            super.action(name, start);
    }

    public boolean isRegistrationOver() {
        return _registrationOver;
    }

    public void setRegistrationOver(boolean registrationOver) {
        _registrationOver = registrationOver;
        if (_registrationOver) {
            announceToPlayersWithValidLevel(getClass().getSimpleName() + ".RegistrationIsClose");
        } else {
            announceToPlayersWithValidLevel(getClass().getSimpleName() + ".RegistrationIsOpen");
            showRegisterWindow();
        }
//		announceToPlayersWithValidLevel(getClass().getSimpleName() + (_registrationOver ? ".RegistrationIsClose" : ".RegistrationIsOpen"));
    }

    public void showRegisterWindow() {
        for (Player player : GameObjectsStorage.getPlayers()) {
            if (player != null && player.getLevel() >= _minLevel && player.getLevel() <= _maxLevel && isCanRegister(player)) {
                ConfirmDlg ask = new ConfirmDlg(SystemMsg.S1, 30000);
                ask.addString(new CustomMessage("scripts.events.ask").toString(player) + " " + getEventName(player) + "?");
                player.ask(ask, new RegisterListener(player, this));
            }
        }
    }
    //endregion

    public boolean isCanRegister(Player player) {
        return player.getReflectionId() == 0 && !player.isInOfflineMode() && !isInEvent(player)
                && !Olympiad.isRegistered(player) && !player.isInOlympiadMode() && !player.isOnSiegeField();
    }

    public boolean isInEvent(Player player) {
        return player.getEvent(TeamVsTeamEvent.class) != null || player.getEvent(CaptureTeamFlagEvent.class) != null;
    }

    //region Implementation & Override
    @Override
    public void initEvent() {
        super.initEvent();
    }

    @Override
    public void reCalcNextTime(boolean onInit) {
        _reflection.clearVisitors();

        clearActions();

        _startTime = Instant.ofEpochMilli(_pattern.getNextValidTimeAfter(new Date()).getTime() + 60000L);
        registerActions();
    }

    @Override
    public SystemMsg checkForAttack(Creature target, Creature attacker, Skill skill, boolean force) {
        if (isInProgress() && !canAttack(target, attacker, null, force, false))
            return SystemMsg.INVALID_TARGET;

        return null;
    }

    @Override
    public boolean canAttack(Creature target, Creature attacker, Skill skill, boolean force, boolean nextAttackCheck) {
        return isInProgress() && target.getTeam() != attacker.getTeam() && target.getTeam() != TeamType.NONE && attacker.getTeam() != TeamType.NONE;
        //		if(_state != State.STARTED || target.getTeam() == TeamType.NONE || attacker.getTeam() == TeamType.NONE || target.getTeam() == attacker.getTeam())
        //			return false;
        //		return true;
    }

    @Override
    public Iterator<DuelSnapshotObject> iterator() {
        List<DuelSnapshotObject> blue = getObjects(TeamType.BLUE);
        List<DuelSnapshotObject> red = getObjects(TeamType.RED);
        return Iterators.concat(blue.iterator(), red.iterator());
    }

    @Override
    protected Instant startTime() {
        return _startTime;
    }

    @Override
    public EventType getType() {
        return EventType.PVP_EVENT;
    }

    @Override
    public void announce(int i) {
        switch (i) {
            case -240:
            case -180:
            case -120:
            case -60:
                AnnouncementUtils.announceToAll("The " + getClass().getSimpleName() + " will begin in " + (i / 60 * -1) + " minutes!");
                break;
            case -10:
            case -5:
            case -4:
            case -3:
            case -2:
            case -1:
                sendPacket(new SystemMessage(SystemMsg.THE_DUEL_WILL_BEGIN_IN_S1_SECONDS).addNumber(-i));
                break;
        }
    }

    @Override
    public void onRemoveEvent(GameObject o) {
        if (o.isPlayer())
            o.getPlayer().removeListener(_playerListeners);
    }

    @Override
    public Reflection getReflection() {
        return _reflection;
    }

    @Override
    public boolean unregisterPlayer(final Player player) {
        if (isRegistrationOver()) {
            return false;
        }
        if (player.getTeam() == TeamType.NONE) {
            return false;
        }
        final List<DuelSnapshotObject> list = getObjects(player.getTeam());
        for (final DuelSnapshotObject object : list) {
            if (player == object.getPlayer()) {
                list.remove(object);
                return true;
            }
        }
        return false;
    }
    //endregion

    public enum State {
        NONE,
        TELEPORT_PLAYERS,
        STARTED
    }

    private class PlayerListeners implements OnPlayerExitListener, OnTeleportListener {
        @Override
        public void onPlayerExit(Player player) {
            player.setReflection(ReflectionManager.DEFAULT);

            exitPlayer(player, true);
        }

        @Override
        public void onTeleport(Player player, int x, int y, int z, Reflection reflection) {
            if (_state != State.NONE && reflection != _reflection)
                exitPlayer(player, false);
        }

        private void exitPlayer(Player player, boolean exit) {
            for (TeamType team : TeamType.VALUES) {
                List<DuelSnapshotObject> objects = getObjects(team);

                for (DuelSnapshotObject d : objects)
                    if (d.getPlayer() == player) {
                        if (isInProgress())
                            onTeleportOrExit(objects, d, exit);
                        else
                            objects.remove(d);
                        break;
                    }
            }

            player.removeListener(_playerListeners);
            player.removeEvent(CustomInstantTeamEvent.this);

//			checkForWinner();
        }
    }

    private class EventReflection extends Reflection {
        EventReflection(int val) {
            super(val);

            init(InstantZoneHolder.getInstance().getInstantZone(getInstantId()));
        }

        @Override
        public void startCollapseTimer(long timeInMillis) {
        }
    }
}
