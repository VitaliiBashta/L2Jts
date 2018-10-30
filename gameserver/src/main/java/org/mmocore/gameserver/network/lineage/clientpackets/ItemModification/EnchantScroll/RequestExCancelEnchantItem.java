package org.mmocore.gameserver.network.lineage.clientpackets.ItemModification.EnchantScroll;

import org.mmocore.gameserver.network.lineage.clientpackets.L2GameClientPacket;
import org.mmocore.gameserver.network.lineage.serverpackets.ItemModification.EnchantScroll.EnchantResult;
import org.mmocore.gameserver.object.Player;

public class RequestExCancelEnchantItem extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar != null) {
            activeChar.setEnchantScroll(null);
            activeChar.sendPacket(EnchantResult.CANCEL);
        }
    }
}