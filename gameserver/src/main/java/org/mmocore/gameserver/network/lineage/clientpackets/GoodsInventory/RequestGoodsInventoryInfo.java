package org.mmocore.gameserver.network.lineage.clientpackets.GoodsInventory;

import org.mmocore.gameserver.network.lineage.clientpackets.L2GameClientPacket;
import org.mmocore.gameserver.network.lineage.serverpackets.GoodsInventory.ExGoodsInventoryChangedNotify;
import org.mmocore.gameserver.object.Player;

/**
 * @author VISTALL
 * @date 23:33/23.03.2011
 */
public class RequestGoodsInventoryInfo extends L2GameClientPacket {
    private int timeUpdate;

    @Override
    protected void readImpl() throws Exception {
        timeUpdate = readC();
    }

    @Override
    protected void runImpl() throws Exception {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (!activeChar.getPremiumAccountComponent().getPremiumItemList().isEmpty()) {
            activeChar.sendPacket(ExGoodsInventoryChangedNotify.STATIC);
        }
    }
}
