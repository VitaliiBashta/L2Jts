package org.mmocore.gameserver.model.entity;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.data.xml.holder.InstantZoneHolder;
import org.mmocore.gameserver.manager.DimensionalRiftManager;
import org.mmocore.gameserver.manager.DimensionalRiftManager.DimensionalRiftRoom;
import org.mmocore.gameserver.model.SimpleSpawner;
import org.mmocore.gameserver.model.Spawner;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.templates.InstantZone;
import org.mmocore.gameserver.utils.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class DimensionalRift extends Reflection {
    protected static final long seconds_5 = 5000L;
    protected static final int MILLISECONDS_IN_MINUTE = 60000;

    protected final int roomType;
    protected List<Integer> completedRooms = new ArrayList<>();
    protected int jumps_current = 0;
    protected int choosenRoom = -1;
    protected boolean hasJumped = false;
    protected boolean isBossRoom = false;
    private Future<?> teleporterTask;
    private Future<?> spawnTask;
    private volatile Future<?> killRiftTask;

    public DimensionalRift(final Party party, final int type, final int room) {
        super();
        onCreate();
        startCollapseTimer(7200000); // 120 минут таймер, для защиты от утечек памяти
        setName("Dimensional Rift");
        if (this instanceof DelusionChamber) {
            final InstantZone iz = InstantZoneHolder.getInstance().getInstantZone(type + 120); // Для равенства типа комнаты и ИД инстанса
            setInstancedZone(iz);
            setName(iz.getName());
        }
        roomType = type;
        setParty(party);
        if (!(this instanceof DelusionChamber)) {
            party.setDimensionalRift(this);
        }
        party.setReflection(this);
        choosenRoom = room;
        checkBossRoom(choosenRoom);

        final Location coords = getRoomCoord(choosenRoom);

        setReturnLoc(party.getGroupLeader().getLoc());
        setTeleportLoc(coords);
        for (final Player p : party.getPartyMembers()) {
            p.getPlayerVariables().set(PlayerVariables.BACK_COORDINATES, getReturnLoc().toXYZString(), -1);
            DimensionalRiftManager.teleToLocation(p, Location.findPointToStay(coords, 50, 100, getGeoIndex()), this);
            p.setReflection(this);
        }

        createSpawnTimer(choosenRoom);
        createTeleporterTimer();
    }

    public int getType() {
        return roomType;
    }

    public int getCurrentRoom() {
        return choosenRoom;
    }

    protected void createTeleporterTimer() {
        if (teleporterTask != null) {
            teleporterTask.cancel(false);
            teleporterTask = null;
        }

        teleporterTask = ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
            @Override
            public void runImpl() {
                if (jumps_current < getMaxJumps() && getPlayersInside(true) > 0) {
                    jumps_current++;
                    teleportToNextRoom();
                    createTeleporterTimer();
                } else {
                    createNewKillRiftTimer();
                }
            }
        }, calcTimeToNextJump()); //Teleporter task, 8-10 minutes
    }

    public void createSpawnTimer(final int room) {
        if (spawnTask != null) {
            spawnTask.cancel(false);
            spawnTask = null;
        }

        final DimensionalRiftRoom riftRoom = DimensionalRiftManager.getInstance().getRoom(roomType, room);

        spawnTask = ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
            @Override
            public void runImpl() {
                for (final SimpleSpawner s : riftRoom.getSpawns()) {
                    final SimpleSpawner sp = s.clone();
                    sp.setReflection(DimensionalRift.this);
                    addSpawn(sp);
                    if (!isBossRoom) {
                        sp.startRespawn();
                    }
                    for (int i = 0; i < sp.getAmount(); i++) {
                        sp.doSpawn(true);
                    }
                }
                DimensionalRift.this.addSpawnWithoutRespawn(getManagerId(), riftRoom.getTeleportCoords(), 0);
            }
        }, AllSettingsConfig.RIFT_SPAWN_DELAY);
    }

    public void createNewKillRiftTimer() {
        if (killRiftTask != null) {
            killRiftTask.cancel(false);
            killRiftTask = null;
        }

        killRiftTask = ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
            @Override
            public void runImpl() {
                if (isCollapseStarted()) {
                    return;
                }
                getParty().getPartyMembers().stream().filter(p -> p != null && p.getReflection() == DimensionalRift.this).forEach(p -> {
                    DimensionalRiftManager.getInstance().teleportToWaitingRoom(p);
                });
                DimensionalRift.this.collapse();
            }
        }, 100L);
    }

    public void partyMemberInvited() {
        createNewKillRiftTimer();
    }

    public void partyMemberExited(final Player player) {
        if (getParty().getMemberCount() < AllSettingsConfig.RIFT_MIN_PARTY_SIZE || getParty().getMemberCount() == 1 || getPlayersInside(true) == 0) {
            createNewKillRiftTimer();
        }
    }

    public void manualTeleport(final Player player, final NpcInstance npc) {
        if (!player.isInParty() || !player.getParty().isInReflection() || !(player.getParty().getReflection() instanceof DimensionalRift)) {
            return;
        }

        if (!player.getParty().isLeader(player)) {
            DimensionalRiftManager.getInstance().showHtmlFile(player, "rift/NotPartyLeader.htm", npc);
            return;
        }

        if (!isBossRoom) {
            if (hasJumped) {
                DimensionalRiftManager.getInstance().showHtmlFile(player, "rift/AlreadyTeleported.htm", npc);
                return;
            }
            hasJumped = true;
        } else {
            manualExitRift(player, npc);
            return;
        }

        teleportToNextRoom();
    }

    public void manualExitRift(final Player player, final NpcInstance npc) {
        if (!player.isInParty() || !player.getParty().isInDimensionalRift()) {
            return;
        }

        if (!player.getParty().isLeader(player)) {
            DimensionalRiftManager.getInstance().showHtmlFile(player, "rift/NotPartyLeader.htm", npc);
            return;
        }

        createNewKillRiftTimer();
    }

    protected void teleportToNextRoom() {
        completedRooms.add(choosenRoom);

        for (final Spawner s : getSpawns()) {
            s.deleteAll();
        }

        final int size = DimensionalRiftManager.getInstance().getRooms(roomType).size();
		/*
		if(jumps_current < getMaxJumps())
			size--; // комната босса может быть только последней
		 */

        if (getType() >= 11 && jumps_current == getMaxJumps()) {
            choosenRoom = 9; // В DC последние 2 печати всегда кончаются рейдом
        } else { // выбираем комнату, где еще не были
            final List<Integer> notCompletedRooms = new ArrayList<>();
            for (int i = 1; i <= size; i++) {
                if (!completedRooms.contains(i)) {
                    notCompletedRooms.add(i);
                }
            }
            choosenRoom = notCompletedRooms.get(Rnd.get(notCompletedRooms.size()));
        }

        checkBossRoom(choosenRoom);
        setTeleportLoc(getRoomCoord(choosenRoom));

        getParty().getPartyMembers().stream().filter(p -> p.getReflection() == this).forEach(p -> {
            DimensionalRiftManager.teleToLocation(p, Location.findPointToStay(getRoomCoord(choosenRoom), 50, 100, DimensionalRift.this.getGeoIndex()), this);
        });

        createSpawnTimer(choosenRoom);
    }

    @Override
    public void collapse() {
        if (isCollapseStarted()) {
            return;
        }

        Future<?> task = teleporterTask;
        if (task != null) {
            teleporterTask = null;
            task.cancel(false);
        }

        task = spawnTask;
        if (task != null) {
            spawnTask = null;
            task.cancel(false);
        }

        task = killRiftTask;
        if (task != null) {
            killRiftTask = null;
            task.cancel(false);
        }

        completedRooms = null;

        final Party party = getParty();
        if (party != null) {
            party.setDimensionalRift(null);
        }

        super.collapse();
    }

    protected long calcTimeToNextJump() {
        if (isBossRoom) {
            return 60 * MILLISECONDS_IN_MINUTE;
        }
        return AllSettingsConfig.RIFT_AUTO_JUMPS_TIME * MILLISECONDS_IN_MINUTE + Rnd.get(AllSettingsConfig.RIFT_AUTO_JUMPS_TIME_RAND);
    }

    public void memberDead(final Player player) {
        if (getPlayersInside(true) == 0) {
            createNewKillRiftTimer();
        }
    }

    public void usedTeleport(final Player player) {
        if (getPlayersInside(false) < AllSettingsConfig.RIFT_MIN_PARTY_SIZE) {
            createNewKillRiftTimer();
        }
    }

    public void checkBossRoom(final int room) {
        isBossRoom = DimensionalRiftManager.getInstance().getRoom(roomType, room).isBossRoom();
    }

    public Location getRoomCoord(final int room) {
        return DimensionalRiftManager.getInstance().getRoom(roomType, room).getTeleportCoords();
    }

    /**
     * По умолчанию 4
     */
    public int getMaxJumps() {
        return Math.max(Math.min(AllSettingsConfig.RIFT_MAX_JUMPS, 8), 1);
    }

    @Override
    public boolean canChampions() {
        return true;
    }

    @Override
    public String getName() {
        return "Dimensional Rift";
    }

    protected int getManagerId() {
        return 31865;
    }

    protected int getPlayersInside(final boolean alive) {
        if (_playerCount == 0) {
            return 0;
        }

        int sum = 0;

        for (final Player p : getPlayers()) {
            if (!alive || !p.isDead()) {
                sum++;
            }
        }

        return sum;
    }

    @Override
    public void removeObject(final GameObject o) {
        if (o.isPlayer()) {
            if (_playerCount <= 1) {
                createNewKillRiftTimer();
            }
        }
        super.removeObject(o);
    }
}