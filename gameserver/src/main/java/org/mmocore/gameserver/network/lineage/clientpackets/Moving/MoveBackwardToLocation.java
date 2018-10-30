package org.mmocore.gameserver.network.lineage.clientpackets.Moving;

import org.mmocore.gameserver.configuration.config.ExtConfig;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.network.lineage.clientpackets.L2GameClientPacket;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ActionFail;
import org.mmocore.gameserver.object.ObservePoint;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.bot_punish.BotPunish.Punish;
import org.mmocore.gameserver.utils.Location;

// cdddddd(d)
public class MoveBackwardToLocation extends L2GameClientPacket {
    private static final int DIFFERENCE_BETWEEN_LOCATIONS = 20; // TODO: хардкод достойный конфига :)
    private final Location _targetLoc = new Location();
    private final Location _originLoc = new Location();
    private int _moveMovement;

    /**
     * packet type id 0x0f
     */
    @Override
    protected void readImpl() {
        _targetLoc.x = readD();
        _targetLoc.y = readD();
        _targetLoc.z = readD();
        _originLoc.x = readD();
        _originLoc.y = readD();
        _originLoc.z = readD();
        if (_buf.hasRemaining()) {
            _moveMovement = readD(); // 0 клавиатура, 1 мышь.
        }
        if (_moveMovement == 1) {
            // Как щелкните мышью , вы получаете около на 27 ниже позиции , чем когда двигаетесь с помощью клавиатуры
            _targetLoc.z += 27;
        }
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }
        if (ExtConfig.EX_ENABLE_AUTO_HUNTING_REPORT && activeChar.getBotPunishComponent().isBeingPunished()) {
            if (activeChar.getBotPunishComponent().getPlayerPunish().canWalk() && activeChar.getBotPunishComponent().getPlayerPunish().getBotPunishType() == Punish.MOVEBAN)
                activeChar.getBotPunishComponent().endPunishment();
            else {
                activeChar.sendPacket(SystemMsg.YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_YOUR_ACTIONS_HAVE_BEEN_RESTRICTED);
                return;
            }
        }
        activeChar.setActive();
        if (System.currentTimeMillis() - activeChar.getLastMovePacket() < ServerConfig.MOVE_PACKET_DELAY) {
            activeChar.sendActionFailed();
            return;
        }
        activeChar.setLastMovePacket();
        if (_targetLoc.distance3D(activeChar.getLoc()) > 11000) {
            activeChar.sendActionFailed();
            return;
        }
        if (activeChar.getLastMovePacketDestinationDiff() != Double.MIN_VALUE) {
            final double distanceDiff = Math.abs(_targetLoc.distance3D(activeChar.getLoc()) - activeChar.getLastMovePacketDestinationDiff());
            if (distanceDiff < DIFFERENCE_BETWEEN_LOCATIONS) {
                activeChar.sendActionFailed();
                return;
            }
        }
        activeChar.setLastMovePacketDestinationDiff(_targetLoc.distance3D(activeChar.getLoc()));
        if (activeChar.isTeleporting()) {
            activeChar.sendActionFailed();
            return;
        }
        if (activeChar.isFrozen()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_MOVE_WHILE_FROZEN, ActionFail.STATIC);
            return;
        }
        if (activeChar.isInObserverMode()) {
            final ObservePoint observer = activeChar.getObservePoint();
            if (observer != null) {
                observer.moveToLocation(_targetLoc, 0, false);
            }
            return;
        }
        if (activeChar.isOutOfControl()) {
            activeChar.sendActionFailed();
            return;
        }
        if (!activeChar.canMoveAfterInteraction()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_MOVE_WHILE_SPEAKING_TO_AN_NPC, ActionFail.STATIC);
            return;
        }
        if (activeChar.getTeleMode() > 0) {
            if (activeChar.getTeleMode() == 1) {
                activeChar.setTeleMode(0);
            }
            activeChar.sendActionFailed();
            activeChar.teleToLocation(_targetLoc);
            return;
        }
        if (activeChar.isInFlyingTransform()) {
            _targetLoc.z = Math.min(5950, Math.max(50, _targetLoc.z)); // В летающей трансформе нельзя летать ниже, чем 0, и выше, чем 6000
        }
        activeChar.moveToLocation(_targetLoc, 0, _moveMovement != 0);

        //ThreadPoolManager.getInstance().executePathfind(new StartMoveTask(activeChar, _targetLoc, _moveMovement != 0));
    }

    public static class StartMoveTask implements Runnable {
        private final Player player;
        private final Location loc;
        private final boolean pathfind;

        public StartMoveTask(Player player, Location loc, boolean pathfind) {
            this.player = player;
            this.loc = loc;
            this.pathfind = pathfind;
        }

        @Override
        public void run() {
            player.moveToLocation(loc, 0, pathfind);
        }
    }
}