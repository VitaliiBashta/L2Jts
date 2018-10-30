package org.mmocore.gameserver.scripts.services.autoenchant;

import org.mmocore.commons.database.dao.JdbcEntityState;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.configuration.config.ServicesConfig;
import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.model.base.Element;
import org.mmocore.gameserver.model.items.etcitems.AttributeStone.AttributeStoneInfo;
import org.mmocore.gameserver.model.items.etcitems.AttributeStone.AttributeStoneManager;
import org.mmocore.gameserver.network.lineage.clientpackets.ItemModification.abstracts.AbstractEnchantPacket;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ActionFail;
import org.mmocore.gameserver.network.lineage.serverpackets.InventoryUpdate;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.player.inventory.PcInventory;
import org.mmocore.gameserver.templates.item.ItemTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hack
 * Date: 07.06.2016 3:33
 */
public class EnchantByAttributeTask implements Runnable {
    private Player player;
    private int stoneId = -1;

    public EnchantByAttributeTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        if (!AbstractEnchantPacket.isValidPlayer(player)) {
            player.sendMessage("Ошибка: улучшение в вашем состоянии невозможно!");
            return;
        }

        boolean isNeedUpdate = false;
        boolean isNeedEquip = false;
        int stones = 0;
        int crystals = 0;
        int success = 0;
        PcInventory inventory = player.getInventory();
        ItemInstance itemToEnchant = player.getEnchantParams().targetItem;
        ItemInstance stone = player.getEnchantParams().upgradeItem;
        stoneId = stone.getItemId();
        player.setEnchantScroll(null);
        try {
            for (int i = 0; i < player.getEnchantParams().upgradeItemLimit && checkAttributeLvl(player); i++) {
                if (!AbstractEnchantPacket.isValidPlayer(player)) {
                    player.sendMessage("Ошибка: улучшение в вашем состоянии невозможно!");
                    return;
                }
                if (itemToEnchant == null) {
                    player.sendActionFailed();
                    player.sendMessage("Ошибка: выберите предмет!");
                    return;
                }
                if (stone == null) {
                    player.sendActionFailed();
                    player.sendMessage("Ошибка: выберите камень!");
                    return;
                }

                ItemTemplate item = itemToEnchant.getTemplate();
                if (item.isCommonItem() || !item.isAttributable() || item.getCrystalType().cry < ItemTemplate.CRYSTAL_S
                        || item.getBodyPart() == ItemTemplate.SLOT_L_HAND) {
                    player.sendPacket(SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS, ActionFail.STATIC);
                    return;
                }

                if (itemToEnchant.getLocation() != ItemInstance.ItemLocation.INVENTORY && itemToEnchant.getLocation() != ItemInstance.ItemLocation.PAPERDOLL) {
                    player.sendPacket(SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS, ActionFail.STATIC);
                    return;
                }

                if (itemToEnchant.isStackable() || (stone = inventory.getItemByObjectId(stone.getObjectId())) == null) {
                    player.sendMessage("Ошибка: закончились улучшатели!");
                    return;
                }
                AttributeStoneInfo asi = AttributeStoneManager.getStoneInfo(stone.getItemId());
                if (asi == null) {
                    player.sendPacket(SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS, ActionFail.STATIC);
                    return;
                }
                Element element = itemToEnchant.isArmor() ? Element.getReverseElement(asi.getElement()) : asi.getElement();
                if (itemToEnchant.isArmor()) {
                    if (itemToEnchant.getAttributeElementValue(Element.getReverseElement(element), false) != 0) {
                        player.sendPacket(SystemMsg.ANOTHER_ELEMENTAL_POWER_HAS_ALREADY_BEEN_ADDED);
                        return;
                    }
                } else if (itemToEnchant.isWeapon()) {
                    if (itemToEnchant.getAttributeElement() != Element.NONE && itemToEnchant.getAttributeElement() != element) {
                        player.sendPacket(SystemMsg.ANOTHER_ELEMENTAL_POWER_HAS_ALREADY_BEEN_ADDED);
                        return;
                    }
                } else {
                    player.sendPacket(SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS);
                    return;
                }

                if (item.isUnderwear() || item.isCloak() || item.isBracelet() || item.isBelt()) {
                    player.sendPacket(SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS, ActionFail.STATIC);
                    return;
                }
                int maxValue = itemToEnchant.isWeapon() ? asi.getMaxWeapon() : asi.getMaxArmor();
                int minValue = itemToEnchant.isWeapon() ? asi.getMinWeapon() : asi.getMinArmor();
                int currentValue = itemToEnchant.getAttributeElementValue(element, false);
                if (currentValue >= maxValue || currentValue < minValue) {
                    player.sendPacket(SystemMsg.ATTRIBUTE_ITEM_USAGE_HAS_BEEN_CANCELLED, ActionFail.STATIC);
                    return;
                }

                // Запрет на заточку чужих вещей, баг может вылезти на серверных лагах
                if (itemToEnchant.getOwnerId() != player.getObjectId()) {
                    player.sendPacket(SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS, ActionFail.STATIC);
                    return;
                }

                if (!inventory.destroyItem(stone, 1L)) {
                    player.sendMessage("Ошибка: закончились улучшатели");
                    return;
                }
                if (Rnd.chance((asi.getChance() / 2) + ServicesConfig.ENCHANT_ATTRIBUTE_CHANCE_CORRECT)) {
                    success++;
                    int value = itemToEnchant.isWeapon() ? asi.getIncWeapon() : asi.getIncArmor();
                    // Для оружия 1й камень дает +20 атрибута
                    if (itemToEnchant.getAttributeElementValue(element, false) == 0 && itemToEnchant.isWeapon())
                        value = 20;

                    if (itemToEnchant.isEquipped()) {
                        player.getInventory().isRefresh = true;
                        player.getInventory().unEquipItem(itemToEnchant);
                        isNeedEquip = true;
                    }
                    itemToEnchant.setAttributeElement(element, itemToEnchant.getAttributeElementValue(element, false) + value);
                    itemToEnchant.setJdbcState(JdbcEntityState.UPDATED);
                    itemToEnchant.update();
                }

                if (EnchantUtils.getInstance().isAttributeStone(stone))
                    stones++;
                else if (EnchantUtils.getInstance().isAttributeCrystal(stone))
                    crystals++;
                isNeedUpdate = true;

            }
        } finally {
            if (stone == null || stone.getCount() <= 0)
                player.getEnchantParams().upgradeItem = null;
            if (isNeedUpdate) {
                if (ServicesConfig.ENCHANT_CONSUME_ITEM != 0) {
                    player.getInventory().destroyItemByItemId(ServicesConfig.ENCHANT_CONSUME_ITEM, ServicesConfig.ENCHANT_CONSUME_ITEM_COUNT);
                    ItemTemplate template = ItemTemplateHolder.getInstance().getTemplate(ServicesConfig.ENCHANT_CONSUME_ITEM);
                    player.sendMessage("Потрачено " + ServicesConfig.ENCHANT_CONSUME_ITEM_COUNT + " "
                            + (template != null ? template.getName() : "[Ошибка: не найден item id! Пожалуйста сообщение администратору об ошибке]"));
                }
                player.sendPacket(new InventoryUpdate().addModifiedItem(itemToEnchant));
                player.sendPacket(new InventoryUpdate());
                player.setEnchantScroll(null);
                player.updateStats();
                Map<String, Integer> result = new HashMap<>();
                result.put("enchant", getAttValue());
                result.put("stones", stones);
                result.put("crystals", crystals);
                int sum = stones + crystals;
                if (sum == 0) sum++; // unreachable in normal situation
                result.put("chance", (int) (((double) success / ((double) sum / 100.)) * 100));
                result.put("success", itemToEnchant == null ? 0 : getAttValue() >= player.getEnchantParams().maxEnchantAtt ? 1 : 0);
                EnchantParser.getInstance().showResultPage(player, EnchantType.Attribute, result);
                if (isNeedEquip)
                    player.getInventory().equipItem(itemToEnchant);
            }
        }
    }

    private boolean checkAttributeLvl(Player player) {
        if (player == null)
            return false;
        int attValue = getAttValue();
        int max = player.getEnchantParams().maxEnchantAtt;
        return attValue < max;
    }

    private int getAttValue() {
        ItemInstance targetItem = player.getEnchantParams().targetItem;
        ItemInstance enchantItem = player.getEnchantParams().upgradeItem;
        int usedStoneId;
        if (targetItem == null)
            return 0;
        if (enchantItem == null)
            usedStoneId = stoneId;
        else
            usedStoneId = enchantItem.getItemId();
        if (targetItem.isWeapon())
            return targetItem.getAttackElementValue();
        else
            return targetItem.getAttributeElementValue(Element.getReverseElement(EnchantUtils.getInstance().getStoneElement(usedStoneId)), false);
    }

}
