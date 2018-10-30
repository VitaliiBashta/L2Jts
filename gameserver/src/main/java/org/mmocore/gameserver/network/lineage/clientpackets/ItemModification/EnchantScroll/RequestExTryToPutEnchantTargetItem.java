package org.mmocore.gameserver.network.lineage.clientpackets.ItemModification.EnchantScroll;

import org.mmocore.gameserver.model.items.etcitems.EnchantScrolls.EnchantScrollInfo;
import org.mmocore.gameserver.model.items.etcitems.EnchantScrolls.EnchantScrollManager;
import org.mmocore.gameserver.network.lineage.clientpackets.ItemModification.abstracts.AbstractEnchantPacket;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ItemModification.EnchantScroll.ExPutEnchantTargetItemResult;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.player.inventory.PcInventory;
import org.mmocore.gameserver.utils.Log;

public class RequestExTryToPutEnchantTargetItem extends AbstractEnchantPacket {
    private int objectId;

    @Override
    protected void readImpl() {
        objectId = readD();
    }

    @Override
    protected void runImpl() {
        Player player = getClient().getActiveChar();
        if (player == null)
            return;

        if (!isValidPlayer(player)) {
            player.sendPacket(ExPutEnchantTargetItemResult.FAIL);
            player.setEnchantScroll(null);
            return;
        }

        PcInventory inventory = player.getInventory();
        ItemInstance itemToEnchant = inventory.getItemByObjectId(objectId);
        ItemInstance scroll = player.getEnchantScroll();

        if (itemToEnchant == null || scroll == null) {
            Log.audit("[EnchantTargetItem]", "Player(ID:" + player.getObjectId() + ") name: " + player.getName() + " used packetHack or another programm, and send wrong packet!");
            player.sendPacket(SystemMsg.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
            player.sendPacket(ExPutEnchantTargetItemResult.FAIL);
            player.setEnchantScroll(null);
            return;
        }

        Log.add(player.getName() + "|Trying to put enchant|" + itemToEnchant.getItemId() + "|+" + itemToEnchant.getEnchantLevel() + '|' + itemToEnchant.getObjectId(), "enchants");

        EnchantScrollInfo esi = EnchantScrollManager.getScrollInfo(scroll.getItemId());

        if (esi == null) {
            player.sendPacket(SystemMsg.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
            player.sendActionFailed();
            return;
        }

        if (!checkItem(itemToEnchant, esi)) {
            player.sendPacket(ExPutEnchantTargetItemResult.FAIL);
            player.sendPacket(SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS);
            player.setEnchantScroll(null);
            return;
        }

        if ((scroll = inventory.getItemByObjectId(scroll.getObjectId())) == null) {
            player.sendPacket(SystemMsg.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
            player.sendPacket(ExPutEnchantTargetItemResult.FAIL);
            player.setEnchantScroll(null);
            return;
        }

        if (itemToEnchant.getEnchantLevel() >= esi.getMax() || itemToEnchant.getEnchantLevel() < esi.getMin()) {
            player.sendPacket(ExPutEnchantTargetItemResult.FAIL);
            player.sendPacket(SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS);
            player.setEnchantScroll(null);
            return;
        }

        // Запрет на заточку чужих вещей, баг может вылезти на серверных лагах
        if (itemToEnchant.getOwnerId() != player.getObjectId()) {
            Log.audit("[EnchantTargetItem]", "Player(ID:" + player.getObjectId() + ") name: " + player.getName() + " used packetHack or another programm, and send wrong packet!");
            player.sendPacket(ExPutEnchantTargetItemResult.FAIL);
            player.setEnchantScroll(null);
            return;
        }

        player.sendPacket(ExPutEnchantTargetItemResult.SUCCESS);
    }
}