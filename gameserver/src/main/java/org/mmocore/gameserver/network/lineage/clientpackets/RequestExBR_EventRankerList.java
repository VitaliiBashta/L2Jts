package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.object.Player;

/**
 * @author KilRoy
 */
public class RequestExBR_EventRankerList extends L2GameClientPacket {
    private int eventId;
    private int eventState;
    private int unk3;

    @Override
    protected void readImpl() {
        eventId = readD();
        eventState = readD(); // 1 - new, 0 - old
        unk3 = readD();
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }
        activeChar.getListeners().onReceivedPacket(eventId, eventState, unk3);
    }
}