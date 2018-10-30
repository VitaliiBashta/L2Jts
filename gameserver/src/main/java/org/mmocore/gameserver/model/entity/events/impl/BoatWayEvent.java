package org.mmocore.gameserver.model.entity.events.impl;

import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.data.BoatHolder;
import org.mmocore.gameserver.data.BoatHolder.Docks;
import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.entity.events.EventType;
import org.mmocore.gameserver.model.entity.events.objects.BoatPoint;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.ChatType;
import org.mmocore.gameserver.network.lineage.components.SysString;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.L2GameServerPacket;
import org.mmocore.gameserver.network.lineage.serverpackets.Say2;
import org.mmocore.gameserver.object.Boat;
import org.mmocore.gameserver.object.ClanAirShip;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.world.GameObjectsStorage;
import org.mmocore.gameserver.world.World;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * @author VISTALL
 * @date 17:48/26.12.2010
 */
public class BoatWayEvent extends Event {
    public static final String BOAT_POINTS = "boat_points";

    private final int _ticketId;
    private final Location _returnLoc;
    private final Boat _boat;
    private SystemMsg abortMessage = SystemMsg.NULL;

    private int currentDock = 0;
    private int wayToDockId = 0;

    public BoatWayEvent(final ClanAirShip boat) {
        super(boat.getObjectId(), "ClanAirShip");
        _ticketId = 0;
        _boat = boat;
        _returnLoc = null;
    }

    public BoatWayEvent(final MultiValueSet<String> set) {
        super(set);
        _ticketId = set.getInteger("ticketId", 0);
        _returnLoc = Location.parseLoc(set.getString("return_point"));
        final String className = set.getString("class", null);
        final String abortMessage = set.getString("abort_message", SystemMsg.NULL.toString());
        final String dock = set.getString("dock", null);
        final String way_to_dock = set.getString("way_to_dock", null);
        if (className != null) {
            _boat = BoatHolder.getInstance().initBoat(getName(), className);
            final Location loc = Location.parseLoc(set.getString("spawn_point"));
            _boat.setLoc(loc, true);
            _boat.setHeading(loc.h);
        } else {
            _boat = BoatHolder.getInstance().getBoat(getName());
        }
        _boat.setSayNpc(set.getString("say_npc"));
        setAbortMessage(SystemMsg.valueOf(abortMessage));
        if (dock != null) {
            setCurrentDock(Docks.valueOf(dock).ordinal());
            if (way_to_dock != null)
                setWayToDock(Docks.valueOf(way_to_dock).ordinal());
        }
        _boat.setWay(className != null ? 1 : 0, this);
    }

    public static void sendPacketToTerr(final GameObject object, final Say2 cs) {
        for (final Player player : GameObjectsStorage.getPlayers()) {
            if (object.isInRangeZ(player, 20000)) {
                player.sendPacket(cs);
            }
        }
    }

    @Override
    public void initEvent() {
    }

    @Override
    public void startEvent() {
        L2GameServerPacket startPacket = _boat.startPacket();
        for (Player player : _boat.getPlayers()) {
            if (_ticketId > 0) {
                if (player.consumeItem(_ticketId, 1)) {
                    if (startPacket != null) {
                        player.sendPacket(startPacket);
                    }
                } else {
                    player.sendPacket(SystemMsg.YOU_DO_NOT_POSSESS_THE_CORRECT_TICKET_TO_BOARD_THE_BOAT);
                    _boat.oustPlayer(player, _returnLoc, true);
                }
            } else {
                if (startPacket != null) {
                    player.sendPacket(startPacket);
                }
            }
        }
        moveNext();
    }

    public void moveNext() {
        final List<BoatPoint> points = getObjects(BOAT_POINTS);

        if (((_boat.getRunState() - points.size()) == 2) && (getWayToDock() > 0 && BoatHolder.getInstance().isDockBusy(getWayToDock()))) {
            startWaitThread();
            return;
        }

        if (getWayToDock() > 0 && BoatHolder.getInstance().isDockBusy(getWayToDock())) {
            startWaitThread();
            if (getAbortMessage() != SystemMsg.NULL) {
                Say2 packet = new Say2(0, ChatType.SYSTEM_MESSAGE, SysString.PASSENGER_BOAT_INFO, getAbortMessage());
                for (int npcs : _boat.getSayNpc()) {
                    NpcInstance npc = GameObjectsStorage.getByNpcId(npcs);
                    sendPacketToTerr(npc, packet);
                }
            }
            return;
        }

        if (_boat.getRunState() >= points.size()) {
            if (getWayToDock() > 0 && !BoatHolder.getInstance().isDockBusy(getWayToDock()))
                BoatHolder.getInstance().setDockBusy(getWayToDock(), true);
            _boat.trajetEnded(true);
            return;
        }

        final BoatPoint bp = points.get(_boat.getRunState());

        if (bp.getSpeed1() >= 0) {
            _boat.setMoveSpeed(bp.getSpeed1());
        }
        if (bp.getSpeed2() >= 0) {
            _boat.setRotationSpeed(bp.getSpeed2());
        }

        if (bp.getMessage() != null && bp.getMessage() != SystemMsg.NULL && _boat.getSayNpc() != null) {
            Say2 packet = new Say2(0, ChatType.SYSTEM_MESSAGE, SysString.PASSENGER_BOAT_INFO, bp.getMessage());
            for (int npcs : _boat.getSayNpc()) {
                NpcInstance npc = GameObjectsStorage.getByNpcId(npcs);
                sendPacketToTerr(npc, packet);
            }
        }

        if (_boat.getRunState() == 0) {
            _boat.broadcastCharInfo();
            if (getCurrentDock() > 0 && BoatHolder.getInstance().isDockBusy(getCurrentDock()))
                BoatHolder.getInstance().setDockBusy(getCurrentDock(), false);
        }

        _boat.setRunState(_boat.getRunState() + 1);

        if (bp.isTeleport()) {
            _boat.teleportShip(bp.getX(), bp.getY(), bp.getZ());
        } else {
            _boat.moveToLocation(bp.getX(), bp.getY(), bp.getZ(), 0, false);
        }
    }

    @Override
    public void reCalcNextTime(final boolean onInit) {
        registerActions();
    }

    @Override
    public EventType getType() {
        return EventType.BOAT_EVENT;
    }

    @Override
    protected Instant startTime() {
        return Instant.now();
    }

    @Override
    public List<Player> broadcastPlayers(final int range) {
        final List<Player> players;
        if (range > 0) {
            players = new ArrayList<>();
            for (Player player : World.getAroundObservers(_boat)) {
                if (player.isInRangeZ(_boat, range)) {
                    players.add(player);
                }
            }
        } else {
            players = World.getAroundObservers(_boat);
        }
        return players;
    }

    private void startWaitThread() {
        ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                moveNext();
            }

        }, 60000L);
    }

    public SystemMsg getAbortMessage() {
        return abortMessage;
    }

    public void setAbortMessage(final SystemMsg abortMessage) {
        this.abortMessage = abortMessage;
    }

    public int getCurrentDock() {
        return currentDock;
    }

    public void setCurrentDock(final int dockId) {
        currentDock = dockId;
    }

    public int getWayToDock() {
        return wayToDockId;
    }

    public void setWayToDock(int wayToDockId) {
        this.wayToDockId = wayToDockId;
    }

    @Override
    public void printInfo() {
    }

    public Location getReturnLoc() {
        return _returnLoc;
    }
}
