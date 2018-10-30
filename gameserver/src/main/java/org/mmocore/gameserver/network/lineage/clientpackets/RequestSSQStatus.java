package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.model.entity.SevenSigns;
import org.mmocore.gameserver.network.lineage.serverpackets.SSQStatus;
import org.mmocore.gameserver.object.Player;

/**
 * Seven Signs Record Update Request
 * packet type id 0xc8
 * format: cc
 */
public class RequestSSQStatus extends L2GameClientPacket {
    private int _page;

    @Override
    protected void readImpl() {
        _page = readC();
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        if ((SevenSigns.getInstance().isSealValidationPeriod() || SevenSigns.getInstance().isCompResultsPeriod()) && _page == 4) {
            return;
        }

        activeChar.sendPacket(new SSQStatus(activeChar, _page));
    }
}