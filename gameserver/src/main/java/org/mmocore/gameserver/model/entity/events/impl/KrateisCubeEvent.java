package org.mmocore.gameserver.model.entity.events.impl;

import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.commons.lang.ArrayUtils;
import org.mmocore.commons.utils.QuartzUtils;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.data.xml.holder.EventHolder;
import org.mmocore.gameserver.listener.actor.OnDeathListener;
import org.mmocore.gameserver.listener.actor.player.OnPlayerExitListener;
import org.mmocore.gameserver.listener.actor.player.OnTeleportListener;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.base.RestartType;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.entity.events.EventType;
import org.mmocore.gameserver.model.entity.events.objects.KrateisCubePlayerObject;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.IBroadcastPacket;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ExPVPMatchCCMyRecord;
import org.mmocore.gameserver.network.lineage.serverpackets.ExPVPMatchCCRecord;
import org.mmocore.gameserver.network.lineage.serverpackets.ExPVPMatchCCRetire;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Location;
import org.quartz.CronExpression;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author VISTALL
 * @date 13:10/10.12.2010
 */
public class KrateisCubeEvent extends Event {
    public static final String PARTICLE_PLAYERS = "particle_players";
    public static final String REGISTERED_PLAYERS = "registered_players";
    public static final String WAIT_LOCS = "wait_locs";
    public static final String TELEPORT_LOCS = "teleport_locs";
    public static final String PREPARE = "prepare";
    private static final CronExpression DATE_PATTERN = QuartzUtils.createCronExpression("0 0,30 * * * ?");
    private static final Location RETURN_LOC = new Location(-70381, -70937, -1428);
    private static final int[] SKILL_IDS = {1086, 1204, 1059, 1085, 1078, 1068, 1240, 1077, 1242, 1062, 5739};
    private static final int[] SKILL_LEVEL = {2, 2, 3, 3, 6, 3, 3, 3, 3, 2, 1};
    private final int _minLevel;
    private final int _maxLevel;
    private final Listeners _listeners = new Listeners();
    private Instant startTime = Instant.now();

    private KrateisCubeRunnerEvent _runnerEvent;
    public KrateisCubeEvent(final MultiValueSet<String> set) {
        super(set);
        _minLevel = set.getInteger("min_level");
        _maxLevel = set.getInteger("max_level");
    }

    @Override
    public void initEvent() {
        _runnerEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 2);

        super.initEvent();
    }

    public void prepare() {
        final NpcInstance npc = _runnerEvent.getNpc();
        final List<KrateisCubePlayerObject> registeredPlayers = removeObjects(REGISTERED_PLAYERS);
        final List<Location> waitLocs = getObjects(WAIT_LOCS);
        for (final KrateisCubePlayerObject k : registeredPlayers) {
            if (npc.getDistance(k.getPlayer()) > 800) {
                continue;
            }

            addObject(PARTICLE_PLAYERS, k);

            final Player player = k.getPlayer();

            player.teleToLocation(Rnd.get(waitLocs), ReflectionManager.DEFAULT);
        }
    }

    @Override
    public void startEvent() {
        super.startEvent();

        final List<KrateisCubePlayerObject> players = getObjects(PARTICLE_PLAYERS);
        final List<Location> teleportLocs = getObjects(TELEPORT_LOCS);

        for (int i = 0; i < players.size(); i++) {
            final KrateisCubePlayerObject k = players.get(i);
            final Player player = k.getPlayer();

            player.getEffectList().stopAllEffects();

            giveEffects(player);

            player.teleToLocation(teleportLocs.get(i));
            player.addEvent(this);

            player.sendPacket(new ExPVPMatchCCMyRecord(k), SystemMsg.THE_MATCH_HAS_STARTED);
        }
    }

    @Override
    public void stopEvent() {
        super.stopEvent();
        reCalcNextTime(false);

        double dif = 0.05;
        int pos = 0;

        final List<KrateisCubePlayerObject> players = removeObjects(PARTICLE_PLAYERS);
        for (final KrateisCubePlayerObject krateisPlayer : players) {
            final Player player = krateisPlayer.getPlayer();
            pos++;
            if (krateisPlayer.getPoints() >= 10) {
                final int count = (int) (krateisPlayer.getPoints() * dif * (1.0 + players.size() / pos * 0.04));
                dif -= 0.0016;
                if (count > 0) {
                    ItemFunctions.addItem(player, 13067, count);

                    final int exp = count * 2880;
                    final int sp = count * 288;
                    player.addExpAndSp(exp, sp);
                }
            }

            player.removeEvent(this);

            player.sendPacket(ExPVPMatchCCRetire.STATIC, SystemMsg.END_MATCH);
            player.teleToLocation(RETURN_LOC);
        }
    }

    private void giveEffects(final Player player) {
        player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
        player.setCurrentCp(player.getMaxCp());

        for (int j = 0; j < SKILL_IDS.length; j++) {
            final SkillEntry skill = SkillTable.getInstance().getSkillEntry(SKILL_IDS[j], SKILL_LEVEL[j]);
            if (skill != null) {
                skill.getEffects(player, player, false, false);
            }
        }
    }

    @Override
    public void reCalcNextTime(final boolean onInit) {
        clearActions();

        startTime = Instant.ofEpochMilli(DATE_PATTERN.getNextValidTimeAfter(new Date()).getTime());

        registerActions();
    }

    @Override
    protected Instant startTime() {
        return startTime;
    }

    @Override
    public boolean canResurrect(final Creature active, final Creature target, final boolean force, final boolean quiet) {
        final KrateisCubeEvent cubeEvent = target.getEvent(KrateisCubeEvent.class);
        if (cubeEvent != null) {
            if (!quiet) {
                active.sendPacket(SystemMsg.INVALID_TARGET);
            }
            return false;
        } else {
            return true;
        }
    }

    public KrateisCubePlayerObject getRegisteredPlayer(final Player player) {
        final List<KrateisCubePlayerObject> registeredPlayers = getObjects(REGISTERED_PLAYERS);
        for (final KrateisCubePlayerObject p : registeredPlayers) {
            if (p.getPlayer() == player) {
                return p;
            }
        }
        return null;
    }

    public KrateisCubePlayerObject getParticlePlayer(final Player player) {
        final List<KrateisCubePlayerObject> registeredPlayers = getObjects(PARTICLE_PLAYERS);
        for (final KrateisCubePlayerObject p : registeredPlayers) {
            if (p.getPlayer() == player) {
                return p;
            }
        }
        return null;
    }

    public void showRank(final Player player) {
        final KrateisCubePlayerObject particlePlayer = getParticlePlayer(player);
        if (particlePlayer == null || particlePlayer.isShowRank()) {
            return;
        }

        particlePlayer.setShowRank(true);

        player.sendPacket(new ExPVPMatchCCRecord(this));
    }

    public void closeRank(final Player player) {
        final KrateisCubePlayerObject particlePlayer = getParticlePlayer(player);
        if (particlePlayer == null || !particlePlayer.isShowRank()) {
            return;
        }

        particlePlayer.setShowRank(false);
    }

    public void updatePoints(final KrateisCubePlayerObject k) {
        k.getPlayer().sendPacket(new ExPVPMatchCCMyRecord(k));

        final ExPVPMatchCCRecord p = new ExPVPMatchCCRecord(this);

        final List<KrateisCubePlayerObject> players = getObjects(PARTICLE_PLAYERS);
        for (final KrateisCubePlayerObject $player : players) {
            if ($player.isShowRank()) {
                $player.getPlayer().sendPacket(p);
            }
        }
    }

    public KrateisCubePlayerObject[] getSortedPlayers() {
        final List<KrateisCubePlayerObject> players = getObjects(PARTICLE_PLAYERS);
        final KrateisCubePlayerObject[] array = players.toArray(new KrateisCubePlayerObject[players.size()]);
        ArrayUtils.eqSort(array);
        return array;
    }

    public void exitCube(final Player player, final boolean teleport) {
        final KrateisCubePlayerObject krateisCubePlayer = getParticlePlayer(player);
        krateisCubePlayer.stopRessurectTask();

        getObjects(PARTICLE_PLAYERS).remove(krateisCubePlayer);

        player.sendPacket(ExPVPMatchCCRetire.STATIC);
        player.removeEvent(this);

        if (teleport) {
            player.teleToLocation(RETURN_LOC);
        }
    }

    @Override
    public void announce(final int a) {
        IBroadcastPacket p = null;
        if (a > 0) {
            p = new SystemMessage(SystemMsg.S1_SECONDS_TO_GAME_END).addNumber(a);
        } else {
            p = new SystemMessage(SystemMsg.THE_MATCH_WILL_START_IN_S1_SECONDS).addNumber(-a);
        }

        final List<KrateisCubePlayerObject> players = getObjects(PARTICLE_PLAYERS);
        for (final KrateisCubePlayerObject $player : players) {
            $player.getPlayer().sendPacket(p);
        }
    }

    @Override
    public void findEvent(final Player player) {
        if (getParticlePlayer(player) != null) {
            player.addEvent(this);
        }
    }

    @Override
    public void onAddEvent(final GameObject o) {
        if (o.isPlayer()) {
            o.getPlayer().addListener(_listeners);
        }
    }

    @Override
    public void onRemoveEvent(final GameObject o) {
        if (o.isPlayer()) {
            o.getPlayer().removeListener(_listeners);
        }
    }

    @Override
    public void action(final String name, final boolean start) {
        if (name.equalsIgnoreCase(PREPARE)) {
            prepare();
        } else {
            super.action(name, start);
        }
    }

    @Override
    public void checkRestartLocs(final Player player, final Map<RestartType, Boolean> r) {
        r.clear();
    }

    public int getMinLevel() {
        return _minLevel;
    }

    public int getMaxLevel() {
        return _maxLevel;
    }

    @Override
    public boolean isInProgress() {
        return _runnerEvent.isInProgress();
    }

    public boolean isRegistrationOver() {
        return _runnerEvent.isRegistrationOver();
    }

    @Override
    public EventType getType() {
        return EventType.PVP_EVENT;
    }

    private class Listeners implements OnDeathListener, OnPlayerExitListener, OnTeleportListener {
        @Override
        public void onDeath(final Creature actor, final Creature killer) {
            if (!actor.isPlayer()) {
                return;
            }
            final KrateisCubeEvent cubeEvent2 = actor.getEvent(KrateisCubeEvent.class);
            if (cubeEvent2 != KrateisCubeEvent.this) {
                return;
            }

            if (killer.isPlayer()) {
                final KrateisCubePlayerObject winnerPlayer = getParticlePlayer((Player) killer);

                winnerPlayer.setPoints(winnerPlayer.getPoints() + 5);
                updatePoints(winnerPlayer);
            }

            final KrateisCubePlayerObject looserPlayer = getParticlePlayer((Player) actor);

            looserPlayer.startRessurectTask();
        }

        @Override
        public void onPlayerExit(final Player player) {
            exitCube(player, false);
        }

        @Override
        public void onTeleport(final Player player, final int x, final int y, final int z, final Reflection reflection) {
            List<Location> waitLocs = getObjects(WAIT_LOCS);
            for (final Location loc : waitLocs) {
                if (loc.x == x && loc.y == y) {
                    return;
                }
            }

            waitLocs = getObjects(TELEPORT_LOCS);

            for (final Location loc : waitLocs) {
                if (loc.x == x && loc.y == y) {
                    return;
                }
            }

            exitCube(player, false);
        }
    }
}
