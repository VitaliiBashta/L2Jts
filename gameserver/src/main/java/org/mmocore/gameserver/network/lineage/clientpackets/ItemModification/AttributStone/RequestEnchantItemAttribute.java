package org.mmocore.gameserver.network.lineage.clientpackets.ItemModification.AttributStone;

import org.mmocore.commons.database.dao.JdbcEntityState;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.model.base.Element;
import org.mmocore.gameserver.model.items.etcitems.AttributeStone.AttributeStoneInfo;
import org.mmocore.gameserver.model.items.etcitems.AttributeStone.AttributeStoneManager;
import org.mmocore.gameserver.network.lineage.clientpackets.ItemModification.abstracts.AbstractEnchantPacket;
import org.mmocore.gameserver.network.lineage.clientpackets.L2GameClientPacket;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ActionFail;
import org.mmocore.gameserver.network.lineage.serverpackets.InventoryUpdate;
import org.mmocore.gameserver.network.lineage.serverpackets.ItemModification.AttributeItem.ExAttributeEnchantResult;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.player.inventory.PcInventory;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.utils.Log;

/**
 * @author SYS
 * Format: d
 */
public class RequestEnchantItemAttribute extends L2GameClientPacket {
    private int objectId;

    @Override
    protected void readImpl() {
        objectId = readD();
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (objectId == -1) {
            activeChar.setEnchantScroll(null);
            activeChar.sendPacket(SystemMsg.ATTRIBUTE_ITEM_USAGE_HAS_BEEN_CANCELLED);
            return;
        }

        if (activeChar.isActionsDisabled()) {
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isInStoreMode()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_ADD_ELEMENTAL_POWER_WHILE_OPERATING_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP, ActionFail.STATIC);
            return;
        }

        if (activeChar.isInTrade()) {
            activeChar.sendActionFailed();
            return;
        }

        final PcInventory inventory = activeChar.getInventory();
        final ItemInstance itemToEnchant = inventory.getItemByObjectId(objectId);
        ItemInstance stone = activeChar.getEnchantScroll();
        activeChar.setEnchantScroll(null);

        if (itemToEnchant == null || stone == null) {
            Log.audit("[EnchantItemAttribute]", "Player(ID:" + activeChar.getObjectId() + ") name: " + activeChar.getName() + " used packetHack or another programm, and send wrong packet!");
            activeChar.sendActionFailed();
            return;
        }

        final ItemTemplate item = itemToEnchant.getTemplate();

        if (item.isHeroWeapon() || item.isCommonItem() || !item.isAttributable() || item.getCrystalType().cry < ItemTemplate.CRYSTAL_S) {
            activeChar.sendPacket(SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS, ActionFail.STATIC);
            return;
        }

        if (itemToEnchant.getLocation() != ItemInstance.ItemLocation.INVENTORY && itemToEnchant.getLocation() != ItemInstance.ItemLocation.PAPERDOLL) {
            activeChar.sendPacket(SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS, ActionFail.STATIC);
            return;
        }

        if (itemToEnchant.isStackable() || (stone = inventory.getItemByObjectId(stone.getObjectId())) == null) {
            Log.audit("[EnchantItemAttribute]", "Player(ID:" + activeChar.getObjectId() + ") name: " + activeChar.getName() + " used packetHack or another programm, and send wrong packet!");
            activeChar.sendActionFailed();
            return;
        }

        AttributeStoneInfo asi = AttributeStoneManager.getStoneInfo(stone.getItemId());

        if (asi == null) {
            activeChar.sendPacket(SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS, ActionFail.STATIC);
            return;
        }

        Element element = itemToEnchant.isArmor() ? Element.getReverseElement(asi.getElement()) : asi.getElement();

        if (itemToEnchant.isArmor()) {
            if (itemToEnchant.getAttributeElementValue(Element.getReverseElement(element), false) != 0) {
                activeChar.sendPacket(SystemMsg.ANOTHER_ELEMENTAL_POWER_HAS_ALREADY_BEEN_ADDED, ActionFail.STATIC);
                return;
            }
        } else if (itemToEnchant.isWeapon()) {
            if (itemToEnchant.getAttributeElement() != Element.NONE && itemToEnchant.getAttributeElement() != element) {
                activeChar.sendPacket(SystemMsg.ANOTHER_ELEMENTAL_POWER_HAS_ALREADY_BEEN_ADDED, ActionFail.STATIC);
                return;
            }
        } else {
            activeChar.sendPacket(SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS, ActionFail.STATIC);
            return;
        }

        if (item.isUnderwear() || item.isCloak() || item.isBracelet() || item.isBelt()) {
            activeChar.sendPacket(SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS, ActionFail.STATIC);
            return;
        }

        int maxValue = itemToEnchant.isWeapon() ? asi.getMaxWeapon() : asi.getMaxArmor();
        int minValue = itemToEnchant.isWeapon() ? asi.getMinWeapon() : asi.getMinArmor();

        int currentValue = itemToEnchant.getAttributeElementValue(element, false);

        if (currentValue >= maxValue || currentValue < minValue) {
            activeChar.sendPacket(SystemMsg.ATTRIBUTE_ITEM_USAGE_HAS_BEEN_CANCELLED, ActionFail.STATIC);
            return;
        }

        // Запрет на заточку чужих вещей, баг может вылезти на серверных лагах
        if (itemToEnchant.getOwnerId() != activeChar.getObjectId()) {
            activeChar.sendPacket(SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS, ActionFail.STATIC);
            return;
        }

        if (!AbstractEnchantPacket.checkReuseTime(activeChar)) {
            activeChar.sendMessage(activeChar.isLangRus() ? "Слишком быстро!" : "Too fast!");
            activeChar.sendActionFailed();
            return;
        }

        if (!inventory.destroyItem(stone, 1L)) {
            Log.audit("[EnchantItemAttribute]", "Player(ID:" + activeChar.getObjectId() + ") name: " + activeChar.getName() + " used packetHack or another programm, and send wrong packet!");
            activeChar.sendActionFailed();
            return;
        }

        double premiumBonus = activeChar.getPremiumAccountComponent().getPremiumBonus().getAttributeChance();
        if (premiumBonus <= 0)
            premiumBonus = 1;

        if (Rnd.chance((asi.getChance() / 2) * premiumBonus)) {
            if (itemToEnchant.getEnchantLevel() == 0) {
                final SystemMessage sm = new SystemMessage(SystemMsg.S2_ELEMENTAL_POWER_HAS_BEEN_ADDED_SUCCESSFULLY_TO_S1);
                sm.addItemName(itemToEnchant.getItemId());
                sm.addItemName(stone.getItemId());
                activeChar.sendPacket(sm);
            } else {
                final SystemMessage sm = new SystemMessage(SystemMsg.S3_ELEMENTAL_POWER_HAS_BEEN_ADDED_SUCCESSFULLY_TO_S1_S2);
                sm.addNumber(itemToEnchant.getEnchantLevel());
                sm.addItemName(itemToEnchant.getItemId());
                sm.addItemName(stone.getItemId());
                activeChar.sendPacket(sm);
            }

            int value = itemToEnchant.isWeapon() ? asi.getIncWeapon() : asi.getIncArmor();

            // Для оружия 1й камень дает +20 атрибута
            if (itemToEnchant.getAttributeElementValue(element, false) == 0 && itemToEnchant.isWeapon())
                value = 20;

            boolean equipped = false;
            if (equipped = itemToEnchant.isEquipped()) {
                activeChar.getInventory().isRefresh = true;
                activeChar.getInventory().unEquipItem(itemToEnchant);
            }

            if (AllSettingsConfig.oneClickItemAttribute)
                itemToEnchant.setAttributeElement(element, maxValue);
            else
                itemToEnchant.setAttributeElement(element, itemToEnchant.getAttributeElementValue(element, false) + value);
            itemToEnchant.setJdbcState(JdbcEntityState.UPDATED);
            itemToEnchant.update();

            if (equipped) {
                activeChar.getInventory().equipItem(itemToEnchant);
                activeChar.getInventory().isRefresh = false;
            }

            activeChar.sendPacket(new InventoryUpdate().addModifiedItem(itemToEnchant));
            activeChar.sendPacket(new ExAttributeEnchantResult(value));
        } else
            activeChar.sendPacket(SystemMsg.YOU_HAVE_FAILED_TO_ADD_ELEMENTAL_POWER);

        activeChar.setEnchantScroll(null);
        activeChar.updateStats();
    }
}