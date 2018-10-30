package org.mmocore.gameserver.scripts.events.custom;

import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.EventsConfig;
import org.mmocore.gameserver.listener.actor.OnDeathListener;
import org.mmocore.gameserver.model.base.RestartType;
import org.mmocore.gameserver.model.base.TeamType;
import org.mmocore.gameserver.model.entity.events.objects.DuelSnapshotObject;
import org.mmocore.gameserver.model.zone.ZoneType;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.Servitor;
import org.mmocore.gameserver.scripts.events.custom.impl.Ctf.CtfBaseObject;
import org.mmocore.gameserver.scripts.events.custom.impl.Ctf.CtfFlagObject;
import org.mmocore.gameserver.skills.skillclasses.Resurrect;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.Location;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * @author VISTALL
 * @date 15:08/03.04.2012
 */
public class CaptureTeamFlagEvent extends CustomInstantTeamEvent {
    public static final String FLAGS = "flags";
    public static final String BASES = "bases";
    public static final String UPDATE_ARROW = "update_arrow";
    private final OnDeathListener _deathListener = new OnDeathListenerImpl();
    private final Map<Integer, ScheduledFuture<?>> _deadList = new ConcurrentHashMap<>();
    public CaptureTeamFlagEvent(MultiValueSet<String> set) {
        super(set);

        Resurrect.GLOBAL.add(this);

        addObject(BASES, new CtfBaseObject(35426, new Location(-187608, 205272, -9542), TeamType.BLUE));
        addObject(BASES, new CtfBaseObject(35423, new Location(-173720, 218088, -9536), TeamType.RED));
        addObject(FLAGS, new CtfFlagObject(new Location(-187752, 206072, -9454), TeamType.BLUE));
        addObject(FLAGS, new CtfFlagObject(new Location(-174264, 218424, -9577), TeamType.RED));
    }

    private static void preparePlayer(List<Player> team) {
        if (team == null || team.size() == 0) {
            return;
        }
        for (Player player : team) {
            if (player == null)
                return;
            Servitor servitor = player.getServitor();
            if (!EventsConfig.CFTAllowSummonTeleport) {
                if (servitor != null)
                    servitor.unSummon(true, true);
            }
            if (EventsConfig.CFTRemoveEffects) {
                player.getEffectList().stopAllEffects();
                if (servitor != null)
                    servitor.getEffectList().stopAllEffects();
            }
            if (EventsConfig.CFTAllowCustomBuffs) {
                if (player.isMageClass()) {
                    buffList(EventsConfig.CFTMageBuffs, player);
                    if (servitor != null)
                        buffList(EventsConfig.CFTMageBuffs, servitor);
                } else {
                    buffList(EventsConfig.CFTFighterBuffs, player);
                    if (servitor != null)
                        buffList(EventsConfig.CFTFighterBuffs, servitor);
                }
            }
            if (EventsConfig.CFTForbidTransformations)
                player.stopTransformation();
        }
    }

    private static void buffCreature(Creature creature, int id, int lvl) {
        SkillTable.getInstance().getSkillEntry(id, lvl).getEffects(creature, creature, false, false);
    }

    private void updateArrowInPlayers() {
        final List<CtfFlagObject> flagObjects = getObjects(FLAGS);

        for (int i = 0; i < TeamType.VALUES.length; i++) {
            TeamType teamType = TeamType.VALUES[i];

            CtfFlagObject selfFlag = flagObjects.get(teamType.ordinal2());
            CtfFlagObject enemyFlag = flagObjects.get(teamType.revert().ordinal2());

            List<DuelSnapshotObject> objects = getObjects(teamType);

            for (DuelSnapshotObject object : objects) {
                Player player = object.getPlayer();
                if (player == null)
                    continue;

                Location location;
                // � �� ����� ���� � �����, �������� � ����
                if (enemyFlag.getOwner() == player) {
                    List<CtfBaseObject> bases = getObjects(BASES);

                    location = bases.get(i).getLoc();
                }
                // ���� ���� �������, �������� � ������
                else if (selfFlag.getOwner() != null)
                    location = selfFlag.getOwner().getLoc();
                    // ����� �������� � ����� �����
                else
                    location = enemyFlag.getLocation();

                player.addRadar(location.getX(), location.getY(), location.getZ());
            }
        }
    }

    public void setWinner(TeamType teamType) {
        if (_winner != TeamType.NONE)
            return;

        _winner = teamType;

        stopEvent();
    }

    @Override
    public void teleportPlayers(String name, ZoneType zoneType) {
        super.teleportPlayers(name, zoneType);
        for (TeamType team : TeamType.VALUES) {
            preparePlayer(getPlayers(team));
            setParties(getPlayers(team));
        }
    }

    //region Implementation & Override
    @Override
    public void stopEvent() {
        for (Map.Entry<Integer, ScheduledFuture<?>> entry : _deadList.entrySet())
            entry.getValue().cancel(true);

        _deadList.clear();

        super.stopEvent();
    }

    @Override
    protected void actionUpdate(boolean start, Player player) {
        if (!start)
            player.removeRadar();
    }

    @Override
    public void action(String name, boolean start) {
        if (start && !EventsConfig.isCtfActive)
            return;
        if (name.equals(UPDATE_ARROW))
            updateArrowInPlayers();
        else
            super.action(name, start);
    }

    @Override
    public int getInstantId() {
        return 600;
    }

    @Override
    protected Location getTeleportLoc(TeamType team) {
        List<CtfBaseObject> objects = getObjects(BASES);

        return Location.findAroundPosition(objects.get(team.ordinal2()).getLoc(), 100, 200, _reflection.getGeoIndex());
    }

    @Override
    public synchronized void checkForWinner() {
        if (_state == State.NONE)
            return;

        TeamType winnerTeam = null;
        for (TeamType team : TeamType.VALUES) {
            List<DuelSnapshotObject> objects = getObjects(team);

            if (objects.isEmpty()) {
                winnerTeam = team.revert();
                break;
            }
        }

        if (winnerTeam != null) {
            _winner = winnerTeam;

            stopEvent();
        }
    }

    @Override
    protected boolean canWalkInWaitTime() {
        return false;
    }

    @Override
    protected void onTeleportOrExit(List<DuelSnapshotObject> objects, DuelSnapshotObject duelSnapshotObject, boolean exit) {
        objects.remove(duelSnapshotObject);
    }

    @Override
    public void checkRestartLocs(Player player, Map<RestartType, Boolean> r) {
        r.clear();
    }

    @Override
    public boolean canResurrect(Creature active, Creature target, boolean force, boolean quiet) {
        CaptureTeamFlagEvent cubeEvent = target.getEvent(CaptureTeamFlagEvent.class);
        if (cubeEvent == this) {
            if (!quiet)
                active.sendPacket(SystemMsg.INVALID_TARGET);
            return false;
        } else
            return true;
    }

    @Override
    public void onAddEvent(GameObject o) {
        super.onAddEvent(o);
        if (o.isPlayer())
            o.getPlayer().addListener(_deathListener);
    }

    @Override
    public void onRemoveEvent(GameObject o) {
        super.onRemoveEvent(o);
        if (o.isPlayer())
            o.getPlayer().removeListener(_deathListener);
    }

    @Override
    protected String getEventName(Player player) {
        return new CustomMessage("scripts.events.ctfName").toString(player);
    }

    private class RessurectTask extends RunnableImpl {
        private final Player _player;
        private int _seconds = 11;

        public RessurectTask(Player player) {
            _player = player;
        }

        @Override
        public void runImpl() {
            _seconds -= 1;
            if (_seconds == 0) {
                _deadList.remove(_player.getObjectId());

                if (_player.getTeam() == TeamType.NONE) // �� ��� �� �� ������.
                    return;

                _player.teleToLocation(getTeleportLoc(_player.getTeam()));
                _player.doRevive();
                if (_player.isMageClass())
                    buffPlayerPet(EventsConfig.CFTMageBuffs, _player);
                else
                    buffPlayerPet(EventsConfig.CFTFighterBuffs, _player);
            } else {
                _player.sendPacket(new SystemMessage(SystemMsg.RESURRECTION_WILL_TAKE_PLACE_IN_THE_WAITING_ROOM_AFTER_S1_SECONDS).addNumber(_seconds));
                ScheduledFuture<?> f = ThreadPoolManager.getInstance().schedule(this, 1000L);

                _deadList.put(_player.getObjectId(), f);
            }
        }
    }
    //endregion

    private class OnDeathListenerImpl implements OnDeathListener {
        @Override
        public void onDeath(Creature actor, Creature killer) {
            if (!isInProgress())
                _deadList.put(actor.getObjectId(), ThreadPoolManager.getInstance().schedule(new RessurectTask(actor.getPlayer()), 1000L));
        }
    }
}
