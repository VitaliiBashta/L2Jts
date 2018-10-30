package org.mmocore.gameserver.network.lineage.clientpackets.PremiumShop;

import org.mmocore.gameserver.network.lineage.clientpackets.L2GameClientPacket;
import org.mmocore.gameserver.network.lineage.serverpackets.PremiumShop.ExBR_ProductInfo;
import org.mmocore.gameserver.object.Player;

public class RequestExBR_ProductInfo extends L2GameClientPacket {
    private int _productId;

    @Override
    protected void readImpl() {
        _productId = readD();
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();

        if (activeChar == null)
            return;

        activeChar.sendPacket(new ExBR_ProductInfo(_productId));
    }
}