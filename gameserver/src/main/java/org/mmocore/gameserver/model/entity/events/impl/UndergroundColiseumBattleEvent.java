package org.mmocore.gameserver.model.entity.events.impl;

import com.google.common.collect.Iterators;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import org.apache.commons.lang3.StringUtils;
import org.mmocore.commons.collections.CollectionUtils;
import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.data.xml.holder.EventHolder;
import org.mmocore.gameserver.listener.actor.OnKillListener;
import org.mmocore.gameserver.listener.actor.OnReviveListener;
import org.mmocore.gameserver.listener.actor.player.OnPlayerExitListener;
import org.mmocore.gameserver.listener.actor.player.OnPlayerPartyLeaveListener;
import org.mmocore.gameserver.listener.actor.player.OnTeleportListener;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.base.RestartType;
import org.mmocore.gameserver.model.base.TeamType;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.entity.events.EventType;
import org.mmocore.gameserver.model.entity.events.objects.SpawnExObject;
import org.mmocore.gameserver.model.entity.events.objects.UCMemberObject;
import org.mmocore.gameserver.model.entity.events.objects.UCTeamObject;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.network.lineage.components.IBroadcastPacket;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ExPVPMatchRecord;
import org.mmocore.gameserver.network.lineage.serverpackets.ExPVPMatchUserDie;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.Servitor;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.Location;

import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

public class UndergroundColiseumBattleEvent extends Event implements Iterable<UCMemberObject> {
    private Map<TeamType, IntObjectMap<Future<?>>> _deadList = new ConcurrentHashMap<TeamType, IntObjectMap<Future<?>>>();
    private TIntList _reviveList = new TIntArrayList();
    private UndergroundColiseumEvent _runnerEvent;
    private boolean _isInProgress;
    private TeamType _winner = TeamType.NONE;
    private Listeners _listeners = new Listeners();
    public UndergroundColiseumBattleEvent(MultiValueSet<String> set) {
        super(set);
    }
    protected UndergroundColiseumBattleEvent(UndergroundColiseumEvent event, Player... leaders) {
        super(0, StringUtils.EMPTY);

        // берем определенный евент с XML, переносим действия
        UndergroundColiseumBattleEvent motherBattleEvent = EventHolder.getInstance().getEvent(EventType.PVP_EVENT, 5);
        motherBattleEvent.cloneTo(this);

        // переносим с запускатора, спавн
        addObjects(UndergroundColiseumEvent.TOWERS, event.getObjects(UndergroundColiseumEvent.TOWERS));
        addObjects(UndergroundColiseumEvent.DOORS, event.getObjects(UndergroundColiseumEvent.DOORS));
        addObjects(UndergroundColiseumEvent.ZONES, event.getObjects(UndergroundColiseumEvent.ZONES));
        addObjects(UndergroundColiseumEvent.BOXES, event.getObjects(UndergroundColiseumEvent.BOXES));

        _runnerEvent = event;
        _runnerEvent.stopTimer();

        for (int i = 0; i < leaders.length; i++) {
            _deadList.put(TeamType.VALUES[i], new IntObjectHashMap<Future<?>>());
            addObject(TeamType.VALUES[i], new UCTeamObject(leaders[i], _listeners));
        }
    }

    @Override
    public void initEvent() {
        //
    }

    @Override
    public void startEvent() {
        SpawnExObject spawnEx = _runnerEvent.getFirstObject(UndergroundColiseumEvent.MANAGER);
        NpcInstance manager = spawnEx.getFirstSpawned();
        for (UCMemberObject member : this) {
            if (member == null)
                continue;
            if (member.getPlayer().getDistance(manager) > 400) {
                broadcastToMembers(new ExShowScreenMessage(NpcString.THE_MATCH_IS_AUTOMATICALLY_CANCELED_BECAUSE_YOU_ARE_TOO_FAR_FROM_THE_ADMISSION_MANAGER, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));
                cancel();
                return;
            }
        }

        broadcastRecord(ExPVPMatchRecord.START, TeamType.NONE);

        for (int i = 0; i < TeamType.VALUES.length; i++) {
            TeamType teamType = TeamType.VALUES[i];
            UCTeamObject teamObject = getFirstObject(teamType);
            Party party = teamObject.getParty();

            UCMemberObject[] objects = teamObject.getMembers();

            for (UCMemberObject memberObject : objects) {
                if (memberObject == null)
                    continue;

                Player player = memberObject.getPlayer();

                player.setTeam(teamType);
                player.addEvent(this);

                SkillTable.getInstance().getSkillEntry(5661, 1).getEffects(player, player, false, false);

                List<Location> locList = _runnerEvent.getObjects(teamType == TeamType.BLUE ? UndergroundColiseumEvent.BLUE_TELEPORT_LOCS : UndergroundColiseumEvent.RED_TELEPORT_LOCS);

                int index = party.indexOf(player);
                if (index < 0)
                    throw new UnsupportedOperationException();
                else {
                    player._stablePoint = player.getLoc();

                    player.teleToLocation(locList.get(index));
                    if (player.isDead())
                        player.doRevive();

                    Servitor servitor = player.getServitor();
                    if (servitor != null)
                        servitor.teleToLocation(locList.get(index));
                }
            }
        }

        broadcastRecord(ExPVPMatchRecord.UPDATE, TeamType.NONE);

        _isInProgress = true;

        super.startEvent();

        SpawnExObject spawnExObject = getFirstObject(UndergroundColiseumEvent.TOWERS);
        List<NpcInstance> npcs = spawnExObject.getAllSpawned();
        for (int i = 0; i < npcs.size(); i++)
            npcs.get(i).setTeam(TeamType.VALUES[i]);
    }

    @Override
    public void stopEvent() {
        if (!_isInProgress)
            return;

        clearActions();

        super.stopEvent();

        _isInProgress = false;

        // если небыло форс остановки - ищем виннера
        if (_winner == TeamType.NONE) {
            UCTeamObject blueTeam = getFirstObject(TeamType.BLUE);
            UCTeamObject redTeam = getFirstObject(TeamType.RED);
            int blueKills = blueTeam.getKills();
            int redKills = redTeam.getKills();

            if (blueKills > redKills)
                _winner = TeamType.BLUE;
            else if (redKills > blueKills)
                _winner = TeamType.RED;
            else if (redKills == blueKills) {
                if (blueTeam.getDeaths() < redTeam.getDeaths())
                    _winner = TeamType.BLUE;
                else if (redTeam.getDeaths() < blueTeam.getDeaths())
                    _winner = TeamType.RED;
                else {
                    if (blueTeam.getRegisterTime() < redTeam.getRegisterTime())
                        _winner = TeamType.BLUE;
                    else if (redTeam.getRegisterTime() < blueTeam.getRegisterTime())
                        _winner = TeamType.RED;
                }
            }
        }

        if (_winner == TeamType.NONE)
            throw new RuntimeException();

        broadcastRecord(ExPVPMatchRecord.FINISH, _winner);

        for (TeamType teamType : TeamType.VALUES) {
            UCTeamObject teamObject = getFirstObject(teamType);
            UCMemberObject[] teams = teamObject.getMembers();

            cancelResurrects(teamType);

            for (UCMemberObject memberObject : teams) {
                if (memberObject == null)
                    continue;

                Player player = memberObject.getPlayer();
                player.setTeam(TeamType.NONE);
                player.removeEvent(this);

                if (player.isDead())
                    player.doRevive();

                player.getEffectList().stopEffect(5661);
                player.setCurrentCp(player.getMaxCp());
                player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());

                if (teamType == _winner) {
                    //TODO [VISTALL] rewards
                }

                player.teleToLocation(player._stablePoint);
                Servitor servitor = player.getServitor();
                if (servitor != null)
                    servitor.teleToLocation(player._stablePoint);
            }

            if (teamType == _winner) {
                _runnerEvent.addToHistory(teamObject.getLeader().getName());
                _runnerEvent.register(teamObject.getLeader());
            }
        }

        cancel();
    }

    public void broadcastRecord(int type, TeamType teamType) {
        ExPVPMatchRecord packet = new ExPVPMatchRecord(type, teamType, this);
        ExPVPMatchUserDie packet2 = type == ExPVPMatchRecord.UPDATE ? new ExPVPMatchUserDie(this) : null;
        for (UCMemberObject memberObject : this) {
            if (memberObject == null)
                continue;

            Player player = memberObject.getPlayer();

            player.sendPacket(packet);
            if (packet2 != null)
                player.sendPacket(packet2);
        }
    }

    @Override
    public void announce(int val) {
        switch (val) {
            case -180:
            case -120:
            case -60:
                broadcastToMembers(new ExShowScreenMessage(NpcString.MATCH_BEGINS_IN_S1_MINUTES, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, String.valueOf(-(val / 60))));
                break;
            case 590:
            case 591:
            case 592:
            case 593:
            case 594:
            case 595:
            case 596:
            case 597:
            case 598:
            case 599:
                broadcastToMembers(new ExShowScreenMessage(NpcString.S1_SECONDS_REMAINING, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false, true, String.valueOf(600 - val)));
                break;
        }
    }

    public void broadcastToMembers(IBroadcastPacket... p) {
        for (UCMemberObject obj : this)
            if (obj != null)
                obj.getPlayer().sendPacket(p);
    }

    @Override
    public void reCalcNextTime(boolean onInit) {
        registerActions();
    }

    @Override
    protected Instant startTime() {
        return Instant.now().plusMillis(180000L);
    }

    @Override
    public Iterator<UCMemberObject> iterator() {
        UCTeamObject blue = getFirstObject(TeamType.BLUE);
        UCTeamObject red = getFirstObject(TeamType.RED);
        return Iterators.concat(blue.iterator(), red.iterator());
    }

    @Override
    public EventType getType() {
        return EventType.PVP_EVENT;
    }

    @Override
    public void checkRestartLocs(Player player, Map<RestartType, Boolean> r) {
        r.clear();
    }

    @Override
    public boolean isInProgress() {
        return _isInProgress;
    }

    public boolean isRegistered(final Player player) {
        for (UCMemberObject obj : this)
            if (obj != null)
                if (obj.getPlayer().getObjectId() == player.getObjectId())
                    return true;
        return false;
    }

    @Override
    public void onRemoveEvent(GameObject o) {
        if (o.isPlayer())
            o.getPlayer().removeListener(_listeners);
    }

    @Override
    public SystemMsg checkForAttack(Creature target, Creature attacker, Skill skill, boolean force) {
        if (target.getTeam() == TeamType.NONE || attacker.getTeam() == TeamType.NONE || target.getTeam() == attacker.getTeam())
            return SystemMsg.INVALID_TARGET;

        return null;
    }

    @Override
    public boolean canResurrect(Creature active, Creature target, boolean force, boolean quiet) {
        return true;
    }

    private void checkForWinner() {
        for (TeamType teamType : TeamType.VALUES) {
            UCTeamObject teamObject = getFirstObject(teamType);

            boolean allDead = true;
            int count = 0;
            UCMemberObject[] members = teamObject.getMembers();
            for (UCMemberObject memberObject : members) {
                if (memberObject == null)
                    continue;

                if (!memberObject.getPlayer().isDead())
                    allDead = false;

                count++;
            }

            if (allDead || count < UndergroundColiseumEvent.PARTY_SIZE) {
                if (_winner != TeamType.NONE)
                    return;

                _winner = teamType.revert();

                if (isInProgress()) {
                    clearActions();
                    ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
                        @Override
                        public void runImpl() throws Exception {
                            stopEvent();
                        }
                    }, 1000L);
                } else {
                    broadcastToMembers(new ExShowScreenMessage(NpcString.MATCH_CANCELLED, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));

                    cancel();
                }
                break;
            }
        }
    }

    public void cancel() {
        List<Player> leaders = _runnerEvent.getObjects(UndergroundColiseumEvent.REGISTERED_LEADERS);
        leaders.remove(0);
        leaders.remove(0);

        clearActions();

        _runnerEvent.startTimer();

        removeObjects(TeamType.RED);
        removeObjects(TeamType.BLUE);
    }

    public void cancelResurrects(TeamType team) {
        IntObjectMap<Future<?>> tasks = _deadList.get(team);
        for (Future<?> task : tasks.values()) // FIXME[K]
            task.cancel(false);

        tasks.clear();
    }

    private class RessurectTask extends RunnableImpl {
        private Player _player;
        private int _seconds = 11;

        public RessurectTask(Player player) {
            _player = player;
        }

        @Override
        public void runImpl() {
            if (_player.getTeam() == TeamType.NONE || !isInProgress())
                return;

            IntObjectMap<Future<?>> tasks = _deadList.get(_player.getTeam());

            _seconds -= 1;
            if (_seconds == 0) {
                tasks.remove(_player.getObjectId());

                SpawnExObject spawnExObject = getFirstObject(UndergroundColiseumEvent.TOWERS);
                List<NpcInstance> npcs = spawnExObject.getAllSpawned();

                NpcInstance ressurectTower = CollectionUtils.safeGet(npcs, _player.getTeam().ordinalWithoutNone());
                if (ressurectTower == null || ressurectTower.isDead())
                    return;

                _reviveList.add(_player.getObjectId());

                List<Location> locList = _runnerEvent.getObjects(_player.getTeam() == TeamType.BLUE ? UndergroundColiseumEvent.BLUE_TELEPORT_LOCS : UndergroundColiseumEvent.RED_TELEPORT_LOCS);

                _player.teleToLocation(Rnd.get(locList));
                _player.doRevive();
                _player.setCurrentHpMp(_player.getMaxHp(), _player.getMaxMp());
                _player.setCurrentCp(_player.getMaxCp());
            } else {
                _player.sendPacket(new SystemMessage(SystemMsg.RESURRECTION_WILL_TAKE_PLACE_IN_THE_WAITING_ROOM_AFTER_S1_SECONDS).addNumber(_seconds));
                Future<?> f = ThreadPoolManager.getInstance().schedule(this, 1000L);

                tasks.put(_player.getObjectId(), f);
            }
        }
    }

    private class Listeners implements OnKillListener, OnReviveListener, OnPlayerPartyLeaveListener, OnTeleportListener, OnPlayerExitListener {
        @Override
        public void onRevive(Creature actor) {
            if (actor.getTeam() == TeamType.NONE || !isInProgress())
                return;

            _reviveList.remove(actor.getObjectId());

            IntObjectMap<Future<?>> tasks = _deadList.get(actor.getTeam());
            Future<?> future = tasks.remove(actor.getObjectId());
            if (future != null)
                future.cancel(false);
        }

        @Override
        public void onKill(Creature actor, Creature victim) {
            if (victim.getTeam() == TeamType.NONE || !isInProgress() || !victim.isPlayer())
                return;

            TeamType victimTeam = victim.getTeam();

            IntObjectMap<Future<?>> tasks = _deadList.get(victim.getTeam());

            UndergroundColiseumBattleEvent event1 = victim.getEvent(UndergroundColiseumBattleEvent.class);
            UndergroundColiseumBattleEvent event2 = actor.getEvent(UndergroundColiseumBattleEvent.class);
            if (event1 != UndergroundColiseumBattleEvent.this || event2 != UndergroundColiseumBattleEvent.this)
                return;

            UCTeamObject killerTeam = getFirstObject(actor.getTeam());
            UCTeamObject deathTeam = getFirstObject(victim.getTeam());

            UCMemberObject killerMember = getMember(actor.getPlayer());
            UCMemberObject deathMember = getMember(victim.getPlayer());

            killerMember.incKills();
            deathMember.incDeaths();

            killerTeam.incKills();
            deathTeam.incDeaths();

            broadcastToMembers(new ExPVPMatchUserDie(UndergroundColiseumBattleEvent.this));

            SpawnExObject spawnExObject = getFirstObject(UndergroundColiseumEvent.TOWERS);
            List<NpcInstance> npcs = spawnExObject.getAllSpawned();

            checkForWinner();

            NpcInstance ressurectTower = CollectionUtils.safeGet(npcs, victimTeam.ordinalWithoutNone());
            if (ressurectTower == null || ressurectTower.isDead())
                return;

            tasks.put(victim.getObjectId(), ThreadPoolManager.getInstance().schedule(new RessurectTask(victim.getPlayer()), 1000L));
        }

        @Override
        public boolean ignorePetOrSummon() {
            return true;
        }

        @Override
        public void onPartyLeave(Player player) {
            exitPlayerAndCancelIfNeed(player, true);
        }

        @Override
        public void onPlayerExit(Player player) {
            exitPlayerAndCancelIfNeed(player, false);
        }

        @Override
        public void onTeleport(Player player, int x, int y, int z, Reflection reflection) {
            if (!isInProgress())
                return;

            if (_reviveList.remove(player.getObjectId()))
                return;

            exitPlayerAndCancelIfNeed(player, false);
        }

        private void exitPlayerAndCancelIfNeed(Player player, boolean tp) {
            player.removeListener(_listeners);

            for (TeamType teamType : TeamType.VALUES) {
                UCTeamObject teamObject = getFirstObject(teamType);
                if (teamObject == null)
                    return;

                for (int i = 0; i < teamObject.getMembers().length; i++) {
                    UCMemberObject memberObject = teamObject.getMembers()[i];
                    if (memberObject != null && memberObject.getPlayer() == player) {
                        player.sendPacket(new ExPVPMatchRecord(ExPVPMatchRecord.FINISH, TeamType.NONE, UndergroundColiseumBattleEvent.this));

                        teamObject.getMembers()[i] = null;

                        player.setTeam(TeamType.NONE);

                        if (tp && isInProgress())
                            player.teleToLocation(player._stablePoint);

                        break;
                    }
                }

                checkForWinner();
            }
        }

        private UCMemberObject getMember(Player player) {
            UCTeamObject teamObject = getFirstObject(player.getTeam());

            for (UCMemberObject ucMemberObject : teamObject.getMembers()) {
                if (ucMemberObject != null && ucMemberObject.getPlayer() == player)
                    return ucMemberObject;
            }

            throw new RuntimeException();
        }

    }
}