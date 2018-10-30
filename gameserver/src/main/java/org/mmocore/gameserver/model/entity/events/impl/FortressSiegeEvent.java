package org.mmocore.gameserver.model.entity.events.impl;

import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.commons.database.dao.JdbcEntityState;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.database.dao.impl.SiegeClanDAO;
import org.mmocore.gameserver.listener.actor.OnKillListener;
import org.mmocore.gameserver.listener.actor.npc.OnSpawnListener;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.entity.events.objects.DoorObject;
import org.mmocore.gameserver.model.entity.events.objects.SiegeClanObject;
import org.mmocore.gameserver.model.entity.events.objects.SpawnExObject;
import org.mmocore.gameserver.model.entity.events.objects.StaticObjectObject;
import org.mmocore.gameserver.model.entity.residence.Fortress;
import org.mmocore.gameserver.model.entity.residence.Residence;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.PlaySound;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.ChatUtils;
import org.mmocore.gameserver.utils.Log;
import org.mmocore.gameserver.utils.TimeUtils;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author VISTALL
 * @date 15:13/14.02.2011
 * Barracks:
 * 0 - Archer Captain
 * 1 - Guard Captain
 * 2 - Support Unit Captain
 * 3 - Control Room
 * 4 - General
 */
public class FortressSiegeEvent extends SiegeEvent<Fortress, SiegeClanObject> {
    public static final String FLAG_POLE = "flag_pole";
    public static final String COMBAT_FLAGS = "combat_flags";
    public static final String SIEGE_COMMANDERS = "siege_commanders";
    public static final String PEACE_COMMANDERS = "peace_commanders";
    public static final String UPGRADEABLE_DOORS = "upgradeable_doors";
    public static final String COMMANDER_DOORS = "commander_doors";
    public static final String ENTER_DOORS = "enter_doors";
    public static final String MACHINE_DOORS = "machine_doors";
    public static final String OUT_POWER_UNITS = "out_power_units";
    public static final String IN_POWER_UNITS = "in_power_units";
    public static final String GUARDS_LIVE_WITH_C_CENTER = "guards_live_with_c_center";
    public static final String ENVOY = "envoy";
    public static final String MERCENARY_POINTS = "mercenary_points";
    public static final String MERCENARY = "mercenary";
    public static final String MERCHANT = "merchant";
    public static final long SIEGE_WAIT_PERIOD_IN_HOURS = 4;
    public static final OnSpawnListener RESTORE_BARRACKS_LISTENER = new RestoreBarracksListener();
    private Future<?> _envoyTask;
    private Future<?> _merchantSpawnTask;
    private boolean[] _barrackStatus;

    public FortressSiegeEvent(final MultiValueSet<String> set) {
        super(set);
        killListener = new KillListener();
    }

    @Override
    public void processStep(final Clan newOwnerClan) {
        if (newOwnerClan.getCastle() > 0) {
            getResidence().changeOwner(null);
        } else {
            getResidence().changeOwner(newOwnerClan);

            stopEvent(true);
        }
    }

    @Override
    public void initEvent() {
        super.initEvent();

        final SpawnExObject exObject = getFirstObject(SIEGE_COMMANDERS);
        _barrackStatus = new boolean[exObject.getSpawns().size()];

        final int lvl = getResidence().getFacilityLevel(Fortress.DOOR_UPGRADE);
        final List<DoorObject> doorObjects = getObjects(UPGRADEABLE_DOORS);
        for (final DoorObject d : doorObjects) {
            d.setUpgradeValue(this, (int) d.getDoor().getMaxHp() * lvl);
            d.getDoor().addListener(_doorDeathListener);
        }

        flagPoleUpdate(false);
        if (getResidence().getOwnerId() > 0) {
            spawnEnvoy();
        }

        spawnMerchant();
    }

    @Override
    public void startEvent() {
        // принудительный старт осады
        if (_merchantSpawnTask != null) {
            _merchantSpawnTask.cancel(false);
            _merchantSpawnTask = null;
        }

        _oldOwner = getResidence().getOwner();

        if (_oldOwner != null) {
            addObject(DEFENDERS, new SiegeClanObject(DEFENDERS, _oldOwner, 0));
        }

        SiegeClanDAO.getInstance().delete(getResidence());

        flagPoleUpdate(true);
        updateParticles(true, ATTACKERS, DEFENDERS);

        broadcastTo(new SystemMessage(SystemMsg.THE_FORTRESS_BATTLE_S1_HAS_BEGUN).addResidenceName(getResidence()), ATTACKERS, DEFENDERS);

        super.startEvent();
    }

    @Override
    public void stopEvent(final boolean step) {
        spawnAction(COMBAT_FLAGS, false);
        updateParticles(false, ATTACKERS, DEFENDERS);

        broadcastTo(new SystemMessage(SystemMsg.THE_FORTRESS_BATTLE_OF_S1_HAS_FINISHED).addResidenceName(getResidence()), ATTACKERS, DEFENDERS);

        final Clan ownerClan = getResidence().getOwner();
        if (ownerClan != null) {
            if (_oldOwner != ownerClan) {
                ownerClan.broadcastToOnlineMembers(PlaySound.SIEGE_VICTORY);

                ownerClan.incReputation(1700, false, toString());
                broadcastTo(new SystemMessage(SystemMsg.S1_IS_VICTORIOUS_IN_THE_FORTRESS_BATTLE_OF_S2).addString(ownerClan.getName())
                        .addResidenceName(getResidence()), ATTACKERS, DEFENDERS);

                getResidence().setOwnDate(ZonedDateTime.now());

                getResidence().startCycleTask();
                spawnEnvoy();
            }
        } else {
            getResidence().setOwnDate(Residence.MIN_SIEGE_DATE);
        }

        getResidence().setLastSiegeDate(ZonedDateTime.now());

        final List<SiegeClanObject> attackers = removeObjects(ATTACKERS);
        for (final SiegeClanObject siegeClan : attackers) {
            siegeClan.deleteFlag();
        }

        removeObjects(DEFENDERS);

        flagPoleUpdate(false);

        spawnMerchant();

        super.stopEvent(step);
    }

    @Override
    public synchronized void reCalcNextTime(final boolean onStart) {
        final int attackersSize = getObjects(ATTACKERS).size();

        ZonedDateTime startSiegeDate = getResidence().getSiegeDate();
        final ZonedDateTime currentTime = ZonedDateTime.now();

        if (startSiegeDate.isAfter(currentTime)) {
            if (attackersSize > 0) {
                if (onStart) {
                    registerActions();
                }
                return;
            }
        }

        clearActions();

        if (attackersSize > 0) {
            getResidence().setSiegeDate(currentTime.plusHours(1));

            registerActions();
        } else {
            getResidence().setSiegeDate(Residence.MIN_SIEGE_DATE);
        }

        getResidence().setJdbcState(JdbcEntityState.UPDATED);
        getResidence().update();
    }

    @Override
    public void announce(final int val) {
        final SystemMessage msg;
        final int min = val / 60;

        if (min > 0) {
            msg = new SystemMessage(SystemMsg.S1_MINUTES_UNTIL_THE_FORTRESS_BATTLE_STARTS).addNumber(min);
        } else {
            msg = new SystemMessage(SystemMsg.S1_SECONDS_UNTIL_THE_FORTRESS_BATTLE_STARTS).addNumber(val);
        }

        broadcastTo(msg, ATTACKERS, DEFENDERS);
    }

    public void spawnEnvoy() {
        final ZonedDateTime endTime = getResidence().getOwnDate().plusHours(1);
        final long diff = Duration.between(ZonedDateTime.now(), endTime).toMillis();

        if (diff > 0 && getResidence().getContractState() == Fortress.NOT_DECIDED) {
            //FIXME [VISTALL] debug
            final SpawnExObject exObject = getFirstObject(ENVOY);
            if (exObject.isSpawned()) {
                info("Last siege: " + TimeUtils.DATE_TIME_FORMATTER.format(getResidence().getLastSiegeDate()) + ", own date: "
                        + TimeUtils.DATE_TIME_FORMATTER.format(getResidence().getOwnDate()) + ", siege date: "
                        + TimeUtils.DATE_TIME_FORMATTER.format(getResidence().getSiegeDate()));
            }

            spawnAction(ENVOY, true);
            _envoyTask = ThreadPoolManager.getInstance().schedule(new EnvoyDespawn(), diff);
        } else if (getResidence().getContractState() == Fortress.NOT_DECIDED) {
            getResidence().setFortState(Fortress.INDEPENDENT, 0);
            getResidence().setJdbcState(JdbcEntityState.UPDATED);
            getResidence().update();
        }
    }

    public void spawnMerchant() {
        if (_merchantSpawnTask != null) {
            _merchantSpawnTask.cancel(false);
            _merchantSpawnTask = null;
        }

        final SpawnExObject object = getFirstObject(MERCHANT);
        if (object.isSpawned()) {
            Log.debug(this + ": merchant already spawned.", new Exception());
            return;
        }

        final ZonedDateTime needDate = getResidence().getLastSiegeDate().plusHours(SIEGE_WAIT_PERIOD_IN_HOURS);
        final long diff = Duration.between(ZonedDateTime.now(), needDate).toMillis();
        if (diff > 0) {
            _merchantSpawnTask = ThreadPoolManager.getInstance().schedule(new MerchantSpawnTask(), diff);
        } else {
            addState(REGISTRATION_STATE);
            spawnAction(MERCHANT, true);
        }
    }

    public void despawnEnvoy() {
        _envoyTask.cancel(false);
        _envoyTask = null;

        spawnAction(ENVOY, false);
        if (getResidence().getContractState() == Fortress.NOT_DECIDED) {
            getResidence().setFortState(Fortress.INDEPENDENT, 0);
            getResidence().setJdbcState(JdbcEntityState.UPDATED);
            getResidence().update();
        }
    }

    public void flagPoleUpdate(final boolean dis) {
        final StaticObjectObject object = getFirstObject(FLAG_POLE);
        if (object != null) {
            object.setMeshIndex(dis ? 0 : (getResidence().getOwner() != null ? 1 : 0));
        }
    }

    public synchronized void barrackAction(final int id, final boolean val) {
        _barrackStatus[id] = val;
    }

    public synchronized void checkBarracks() {
        boolean allDead = true;
        for (final boolean b : getBarrackStatus()) {
            if (!b) {
                allDead = false;
            }
        }
        if (allDead) {
            if (_oldOwner != null) {
                final SpawnExObject spawn = getFirstObject(MERCENARY);
                final NpcInstance npc = spawn.getFirstSpawned();
                if (npc == null || npc.isDead()) {
                    return;
                }

                ChatUtils.shout(npc, NpcString.THE_COMMAND_GATE_HAS_OPENED_CAPTURE_THE_FLAG_QUICKLY_AND_RAISE_IT_HIGH_TO_PROCLAIM_OUR_VICTORY);

                spawnFlags();
            } else {
                spawnFlags();
            }
        }
    }

    public void spawnFlags() {
        doorAction(COMMANDER_DOORS, true);
        spawnAction(SIEGE_COMMANDERS, false);
        spawnAction(COMBAT_FLAGS, true);

        if (_oldOwner != null) {
            spawnAction(MERCENARY, false);
        }

        spawnAction(GUARDS_LIVE_WITH_C_CENTER, false);

        broadcastTo(SystemMsg.ALL_BARRACKS_ARE_OCCUPIED, ATTACKERS, DEFENDERS);
    }

    @Override
    public boolean ifVar(final String name) {
        if (name.equals(OWNER)) {
            return getResidence().getOwner() != null;
        }
        if (name.equals(OLD_OWNER)) {
            return _oldOwner != null;
        }
        if ("reinforce_1".equalsIgnoreCase(name)) {
            return getResidence().getFacilityLevel(Fortress.REINFORCE) == 1;
        }
        if ("reinforce_2".equalsIgnoreCase(name)) {
            return getResidence().getFacilityLevel(Fortress.REINFORCE) == 2;
        }
        if ("dwarvens".equalsIgnoreCase(name)) {
            return getResidence().getFacilityLevel(Fortress.DWARVENS) == 1;
        }
        return false;
    }

    public boolean[] getBarrackStatus() {
        return _barrackStatus;
    }

    @Override
    public boolean canAttack(final Creature target, final Creature attacker, final Skill skill, final boolean force, final boolean nextAttackCheck) {
        final Player playerTarget = target.getPlayer();
        final Player playerAttacker = attacker.getPlayer();
        final FortressSiegeEvent siegeEvent1 = playerTarget.getEvent(FortressSiegeEvent.class);
        final FortressSiegeEvent siegeEvent2 = playerAttacker.getEvent(FortressSiegeEvent.class);
        if (siegeEvent1 == null || siegeEvent2 == null || (siegeEvent1 != siegeEvent2)) {
            return false;
        }
        final SiegeClanObject siegeClan1 = siegeEvent1.getSiegeClan(ATTACKERS, playerTarget.getClan());
        final SiegeClanObject siegeClan2 = siegeEvent2.getSiegeClan(ATTACKERS, playerAttacker.getClan());
        //атакующие защитников (и наоборот) могут бить везде
        if ((siegeClan1 != null && siegeClan2 == null) || (siegeClan1 == null && siegeClan2 != null)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean canResurrect(final Creature active, final Creature target, final boolean force, final boolean quiet) {
        final boolean playerInZone = checkIfInZone(active);
        final boolean targetInZone = checkIfInZone(target);
        // если оба вне зоны - рес разрешен
        // если таргет вне осадный зоны - рес разрешен
        if (!playerInZone && !targetInZone || !targetInZone) {
            return true;
        }

        final Player resurectPlayer = active.getPlayer();
        final Player targetPlayer = target.getPlayer();

        // если оба незареганы - невозможно ресать
        // если таргет незареган - невозможно ресать
        final FortressSiegeEvent siegeEvent1 = resurectPlayer.getEvent(FortressSiegeEvent.class);
        final FortressSiegeEvent siegeEvent2 = targetPlayer.getEvent(FortressSiegeEvent.class);
        if (siegeEvent1 == null && siegeEvent2 == null || siegeEvent2 != this) {
            if (!quiet) {
                if (force) {
                    targetPlayer.sendPacket(SystemMsg.IT_IS_NOT_POSSIBLE_TO_RESURRECT_IN_BATTLEFIELDS_WHERE_A_SIEGE_WAR_IS_TAKING_PLACE);
                }
                active.sendPacket(force ? SystemMsg.IT_IS_NOT_POSSIBLE_TO_RESURRECT_IN_BATTLEFIELDS_WHERE_A_SIEGE_WAR_IS_TAKING_PLACE : SystemMsg.INVALID_TARGET);
            }
            return false;
        }

        final SiegeClanObject targetSiegeClan = siegeEvent2.getSiegeClan(ATTACKERS, targetPlayer.getClan());
        if (targetSiegeClan == null) {
            return false;
        }

        // если нету флага - рес запрещен
        if (targetSiegeClan.getFlag() == null) {
            if (!quiet) {
                if (force) {
                    targetPlayer.sendPacket(SystemMsg.IF_A_BASE_CAMP_DOES_NOT_EXIST_RESURRECTION_IS_NOT_POSSIBLE);
                }
                active.sendPacket(force ? SystemMsg.IF_A_BASE_CAMP_DOES_NOT_EXIST_RESURRECTION_IS_NOT_POSSIBLE : SystemMsg.INVALID_TARGET);
            }
            return false;
        }

        if (force) {
            return true;
        } else {
            if (!quiet) {
                active.sendPacket(SystemMsg.INVALID_TARGET);
            }
            return false;
        }
    }

    @Override
    public void removeState(final int val) {
        super.removeState(val);

        if (val == REGISTRATION_STATE) {
            if (getResidence().getOwner() != null) {
                getResidence().getOwner().broadcastToOnlineMembers(SystemMsg.ENEMY_BLOOD_PLEDGES_HAVE_INTRUDED_INTO_THE_FORTRESS);
            }
        }
    }

    private static class RestoreBarracksListener implements OnSpawnListener {
        @Override
        public void onSpawn(final NpcInstance actor) {
            final FortressSiegeEvent siegeEvent = actor.getEvent(FortressSiegeEvent.class);
            final SpawnExObject siegeCommanders = siegeEvent.getFirstObject(FortressSiegeEvent.SIEGE_COMMANDERS);
            if (siegeCommanders.isSpawned()) {
                siegeEvent.broadcastTo(SystemMsg.THE_BARRACKS_FUNCTION_HAS_BEEN_RESTORED, FortressSiegeEvent.ATTACKERS, FortressSiegeEvent.DEFENDERS);
            }
        }
    }

    public class KillListener implements OnKillListener {
        @Override
        public void onKill(final Creature actor, final Creature victim) {
            final Player winner = actor.getPlayer();
            if (winner == null || !victim.isPlayer() || winner == victim || !checkIfInZone(victim) || !((Player) victim)
                    .isUserRelationActive() || victim.getEvent(FortressSiegeEvent.class) != FortressSiegeEvent.this) {
                return;
            }

            final List<Player> players;
            if (winner.getParty() == null) {
                players = Collections.singletonList(winner);
            } else {
                players = winner.getParty().getPartyMembers();
            }

            for (final Player $player : players) {
                if ($player.getLevel() < 40 || !$player.isInRange(winner, AllSettingsConfig.ALT_PARTY_DISTRIBUTION_RANGE)) {
                    continue;
                }

                $player.setFame($player.getFame() + Rnd.get(1, 10), FortressSiegeEvent.this.toString());
            }

            //((Player)victim).startEnableUserRelationTask(BLOCK_FAME_TIME, FortressSiegeEvent.this);
            //blockedFameOnKill.put(victim.getObjectId(), System.currentTimeMillis() + BLOCK_FAME_TIME);
        }

        @Override
        public boolean ignorePetOrSummon() {
            return true;
        }
    }

    private class EnvoyDespawn extends RunnableImpl {
        @Override
        public void runImpl() throws Exception {
            despawnEnvoy();
        }
    }

    private class MerchantSpawnTask extends RunnableImpl {
        @Override
        public void runImpl() throws Exception {
            addState(REGISTRATION_STATE);
            spawnAction(MERCHANT, true);
            _merchantSpawnTask = null;
        }
    }
}
