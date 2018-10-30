package org.mmocore.gameserver.network.lineage.clientpackets.Moving;

import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.network.lineage.clientpackets.L2GameClientPacket;
import org.mmocore.gameserver.network.lineage.serverpackets.Moving.StopRotation;
import org.mmocore.gameserver.object.ObservePoint;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Location;

public class CannotMoveAnymore extends L2GameClientPacket {
    private final Location location = new Location();

    /**
     * packet type id 0x47
     * <p>
     * sample
     * <p>
     * 36
     * a8 4f 02 00 // x
     * 17 85 01 00 // y
     * a7 00 00 00 // z
     * 98 90 00 00 // heading?
     * <p>
     * format:		cdddd
     */
    @Override
    protected void readImpl() {
        location.x = readD();
        location.y = readD();
        location.z = readD();
        location.h = readD();
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }
        if (activeChar.isAfraid()) {
            //TODO: Возможно есть месага?
            return;
        }
        if (activeChar.isInObserverMode()) {
            final ObservePoint observer = activeChar.getObservePoint();
            if (observer != null) {
                observer.stopMove();
            }
            return;
        }
        activeChar.setLoc(location);
        activeChar.stopMove(true, true);
        StopRotation sr = new StopRotation(activeChar.getObjectId(), location.h, 0);
        activeChar.broadcastPacket(sr);
        if (!activeChar.isOutOfControl()) {
            activeChar.getAI().notifyEvent(CtrlEvent.EVT_ARRIVED_BLOCKED, location, null);
        }
    }
}