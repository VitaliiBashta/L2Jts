package org.mmocore.gameserver.network.lineage.clientpackets.PremiumShop;

import org.mmocore.gameserver.network.lineage.clientpackets.L2GameClientPacket;
import org.mmocore.gameserver.network.lineage.serverpackets.PremiumShop.ExBR_ProductList;
import org.mmocore.gameserver.object.Player;

/**
 * @author KilRoy
 */
public class RequestExBR_ProductList extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;

        activeChar.sendPacket(new ExBR_ProductList());
    }
}