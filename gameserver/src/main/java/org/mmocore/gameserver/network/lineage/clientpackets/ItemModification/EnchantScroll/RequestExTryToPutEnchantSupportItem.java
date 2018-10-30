package org.mmocore.gameserver.network.lineage.clientpackets.ItemModification.EnchantScroll;

import org.mmocore.gameserver.network.lineage.clientpackets.L2GameClientPacket;
import org.mmocore.gameserver.network.lineage.serverpackets.ItemModification.EnchantScroll.ExPutEnchantSupportItemResult;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.player.inventory.PcInventory;
import org.mmocore.gameserver.utils.ItemFunctions;

public class RequestExTryToPutEnchantSupportItem extends L2GameClientPacket {
    private int itemId;
    private int catalystId;

    @Override
    protected void readImpl() {
        catalystId = readD();
        itemId = readD();
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        final PcInventory inventory = activeChar.getInventory();
        final ItemInstance itemToEnchant = inventory.getItemByObjectId(itemId);
        final ItemInstance catalyst = inventory.getItemByObjectId(catalystId);

        if (ItemFunctions.checkCatalyst(itemToEnchant, catalyst))
            activeChar.sendPacket(new ExPutEnchantSupportItemResult(1));
        else
            activeChar.sendPacket(new ExPutEnchantSupportItemResult(0));
    }
}