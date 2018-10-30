package org.mmocore.gameserver.object;

import org.mmocore.gameserver.ai.BoatAI;
import org.mmocore.gameserver.ai.CharacterAI;
import org.mmocore.gameserver.model.entity.events.impl.BoatWayEvent;
import org.mmocore.gameserver.network.lineage.components.IBroadcastPacket;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ActionFail;
import org.mmocore.gameserver.network.lineage.serverpackets.L2GameServerPacket;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.templates.CharTemplate;
import org.mmocore.gameserver.templates.item.WeaponTemplate;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.PositionUtils;
import org.mmocore.gameserver.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author VISTALL
 * @date 17:45/26.12.2010
 */
public abstract class Boat extends Creature {
    protected final Set<Player> players = new CopyOnWriteArraySet<>();
    private final BoatWayEvent[] ways = new BoatWayEvent[2];
    protected int fromHome;
    protected int runState;
    private int moveSpeed; //speed 1
    private int rotationSpeed; //speed 2
    private int[] sayNpc = new int[2];

    public Boat(int objectId, CharTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onSpawn() {
        fromHome = 1;

        getCurrentWay().reCalcNextTime(false);
    }

    @Override
    public void setXYZ(int x, int y, int z, boolean MoveTask) {
        super.setXYZ(x, y, z, MoveTask);

        updatePeopleInTheBoat(x, y, z);
    }

    public void onEvtArrived() {
        getCurrentWay().moveNext();
    }

    protected void updatePeopleInTheBoat(int x, int y, int z) {
        for (Player player : players) {
            if (player != null) {
                player.setXYZ(x, y, z, true);
            }
        }
    }

    public void addPlayer(Player player, Location boatLoc) {
        if (!players.add(player))
            return;

        player.setBoat(this);
        player.setLoc(getLoc(), false);
        player.setInBoatPosition(boatLoc);
        player.broadcastPacket(getOnPacket(player, boatLoc));
    }

    public void moveInBoat(Player player, Location ori, Location loc) {
        if (player.getServitor() != null) {
            player.sendPacket(SystemMsg.YOU_SHOULD_RELEASE_YOUR_PET_OR_SERVITOR_SO_THAT_IT_DOES_NOT_FALL_OFF_OF_THE_BOAT_AND_DROWN, ActionFail.STATIC);
            return;
        }

        if (player.isTransformed()) {
            player.sendPacket(SystemMsg.YOU_CANNOT_BOARD_A_SHIP_WHILE_YOU_ARE_POLYMORPHED, ActionFail.STATIC);
            return;
        }

        if (player.isMovementDisabled() || player.isSitting()) {
            player.sendActionFailed();
            return;
        }

        if (!player.isInBoat()) {
            player.setBoat(this);
        }

        loc.h = PositionUtils.getHeadingTo(ori, loc);
        player.setInBoatPosition(loc);
        player.broadcastPacket(inMovePacket(player, ori, loc));
    }

    public void trajetEnded(boolean oust) {
        runState = 0;
        fromHome = fromHome == 1 ? 0 : 1;

        L2GameServerPacket checkLocation = checkLocationPacket();
        if (checkLocation != null) {
            broadcastPacket(infoPacket(), checkLocation);
        }

        if (oust) {
            oustPlayers();
            getCurrentWay().reCalcNextTime(false);
        }
    }

    public void teleportShip(int x, int y, int z) {
        if (isMoving) {
            stopMove(false);
        }

        for (Player player : players) {
            player.teleToLocation(x, y, z);
        }

        setHeading(calcHeading(x, y));

        setXYZ(x, y, z, true);

        getCurrentWay().moveNext();
    }

    public void oustPlayer(Player player, Location loc, boolean teleport) {
        if (!players.contains(player))
            return;

        player._stablePoint = null;

        player.setBoat(null);
        player.setInBoatPosition(null);
        player.broadcastPacket(getOffPacket(player, loc));

        if (teleport)
            player.teleToLocation(loc);

        players.remove(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public void broadcastPacketToPassengers(IBroadcastPacket packet) {
        for (Player player : players)
            player.sendPacket(packet);
    }

    //=========================================================================================================
    public abstract L2GameServerPacket infoPacket();

    @Override
    public abstract L2GameServerPacket movePacket();

    public abstract L2GameServerPacket inMovePacket(Player player, Location src, Location desc);

    @Override
    public abstract L2GameServerPacket stopMovePacket();

    public abstract L2GameServerPacket inStopMovePacket(Player player);

    public abstract L2GameServerPacket startPacket();

    public abstract L2GameServerPacket validateLocationPacket(Player player);

    public abstract L2GameServerPacket checkLocationPacket();

    public abstract L2GameServerPacket getOnPacket(Player player, Location location);

    public abstract L2GameServerPacket getOffPacket(Player player, Location location);

    public abstract void oustPlayers();

    @Override
    public CharacterAI getAI() {
        if (_ai == null) {
            _ai = new BoatAI(this);
        }

        return _ai;
    }

    @Override
    public void broadcastCharInfo() {
        broadcastPacket(infoPacket());
    }

    @Override
    public void broadcastPacket(IBroadcastPacket... packets) {
        for (Player player : World.getAroundObservers(this)) {
            player.sendPacket(packets);
        }
    }

    @Override
    public void validateLocation(int broadcast) {
    }

    @Override
    public void sendChanges() {
    }

    @Override
    public int getMoveSpeed() {
        return moveSpeed;
    }

    public void setMoveSpeed(int moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    @Override
    public int getRunSpeed() {
        return moveSpeed;
    }

    @Override
    public ItemInstance getActiveWeaponInstance() {
        return null;
    }

    @Override
    public WeaponTemplate getActiveWeaponItem() {
        return null;
    }

    @Override
    public ItemInstance getSecondaryWeaponInstance() {
        return null;
    }

    @Override
    public WeaponTemplate getSecondaryWeaponItem() {
        return null;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public boolean isAutoAttackable(Creature attacker) {
        return false;
    }

    public int[] getSayNpc() {
        return sayNpc;
    }

    public void setSayNpc(String val) {
        final String[] npcs = val.split(";");
        if (npcs.length < 1)
            throw new IllegalArgumentException("Can't parse npc_say from string: " + val);
        int[] result = new int[npcs.length];
        for (int i = 0; i < result.length; i++)
            result[i] = Integer.parseInt(npcs[i].trim());
        sayNpc = result;
    }

    //=========================================================================================================
    public int getRunState() {
        return runState;
    }

    public void setRunState(int runState) {
        this.runState = runState;
    }

    public int getRotationSpeed() {
        return rotationSpeed;
    }

    public void setRotationSpeed(int rotationSpeed) {
        this.rotationSpeed = rotationSpeed;
    }

    public BoatWayEvent getCurrentWay() {
        return ways[fromHome];
    }

    public void setWay(int id, BoatWayEvent v) {
        ways[id] = v;
    }

    public Set<Player> getPlayers() {
        return players;
    }

    public boolean isDocked() {
        return runState == 0;
    }

    public Location getReturnLoc() {
        return getCurrentWay().getReturnLoc();
    }

    @Override
    public boolean isBoat() {
        return true;
    }

    @Override
    public List<L2GameServerPacket> addPacketList(Player forPlayer, Creature dropper) {
        if (!isMoving) {
            return Collections.singletonList(infoPacket());
        } else {
            List<L2GameServerPacket> list = new ArrayList<>(2);
            list.add(infoPacket());
            list.add(movePacket());
            return list;
        }
    }
}