package org.mmocore.gameserver.scripts.services.autoenchant;

import org.mmocore.commons.database.dao.JdbcEntityState;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.configuration.config.ServicesConfig;
import org.mmocore.gameserver.configuration.config.clientCustoms.LostDreamCustom;
import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.model.items.etcitems.EnchantScrolls.EnchantScrollInfo;
import org.mmocore.gameserver.model.items.etcitems.EnchantScrolls.EnchantScrollManager;
import org.mmocore.gameserver.model.items.etcitems.EnchantScrolls.EnchantScrollType;
import org.mmocore.gameserver.network.lineage.clientpackets.ItemModification.abstracts.AbstractEnchantPacket;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.InventoryUpdate;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.player.inventory.PcInventory;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hack
 * Date: 07.06.2016 2:13
 */
public class EnchantByScrollTask implements Runnable {
    private Player player;

    public EnchantByScrollTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        if (!AbstractEnchantPacket.isValidPlayer(player)) {
            player.sendMessage("Ошибка: улучшение в вашем состоянии невозможно!");
            return;
        }
        boolean isNeedEquip = false;
        boolean isNeedUpdate = false;

        // stat
        int isCrystallized = 0;
        int maxEnchant = 0;
        int commonScrolls = 0;
        int scrolls = 0;
        int success = 0;
        int count = 0;

        ItemInstance item = player.getEnchantParams().targetItem;
        ItemInstance scroll;// = player.getEnchantParams().upgradeItem;
        PcInventory inventory = player.getInventory();
        if (item != null && item.isEquipped()) {
            inventory.unEquipItem(item);
            isNeedEquip = true;
        }
        try {
            inventory.writeLock();
            for (int i = 0; i < player.getEnchantParams().upgradeItemLimit && player.getEnchantParams().targetItem.getEnchantLevel() < player.getEnchantParams().maxEnchant; i++) {
                if (!AbstractEnchantPacket.isValidPlayer(player)) {
                    player.sendMessage("Ошибка: улучшение в вашем состоянии невозможно!");
                    return;
                }
                if (item == null) {
                    player.sendMessage("Ошибка: выберите предмет!");
                    return;
                }
                if (item.getEnchantLevel() < ServicesConfig.SAFE_ENCHANT_COMMON && player.getEnchantParams().isUseCommonScrollWhenSafe) {
                    scroll = EnchantUtils.getInstance().getUnsafeEnchantScroll(player, item);
                    if (scroll == null) {
                        player.sendMessage("Ошибка: не соблюдены условия, либо обычные свитки закончились!");
                        return;
                    }
                    commonScrolls++;
                } else {
                    scroll = player.getEnchantParams().upgradeItem;
                    if (scroll == null) {
                        player.sendMessage("Ошибка: закончились свитки!");
                        return;
                    }
                    scrolls++;
                }
                EnchantScrollInfo esi = EnchantScrollManager.getScrollInfo(scroll.getItemId());
                if (esi == null) {
                    player.sendMessage("Ошибка: свитки не соответствуют условиям!");
                    return;
                }
                if (item.getEnchantLevel() >= esi.getMax() || item.getEnchantLevel() < esi.getMin()) {
                    player.sendPacket(SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS);
                    return;
                }
                if (!AbstractEnchantPacket.checkItem(item, esi)) {
                    player.sendPacket(SystemMsg.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
                    return;
                }
                if (!inventory.destroyItem(scroll, 1L)) {
                    player.sendMessage("Ошибка: не хватает свитков!");
                    return;
                }
                if (item.isEquipped()) {
                    inventory.isRefresh = true;
                    inventory.unEquipItem(item);
                }
                int chance;
                if (esi.getType() == EnchantScrollType.DIVINE || esi.getType() == EnchantScrollType.CRYSTALL)
                    chance = 100;
                else
                    chance = AbstractEnchantPacket.getChance(item, esi);
                if (esi.getType() == EnchantScrollType.ANCIENT || esi.getType() == EnchantScrollType.ITEM_MALL)
                    chance += chance * 0.10D;
                chance = Math.min(chance, 100);
                if (Rnd.chance(chance + ServicesConfig.ENCHANT_SCROLL_CHANCE_CORRECT)) {
                    if (chance != 100)
                        success++;
                    item.setEnchantLevel(item.getEnchantLevel() + 1);
                    item.setJdbcState(JdbcEntityState.UPDATED);
                    item.update();
                } else {
                    switch (esi.getType()) {
                        case NORMAL:
                            isCrystallized = 1;
                            if (item.isEquipped()) {
                                player.sendDisarmMessage(item);
                            }
                            if (!inventory.destroyItem(item, 1L)) {
                                player.sendActionFailed();
                                return;
                            }
                            int crystalId = item.getCrystalType().cry;
                            if ((crystalId > 0) && (item.getTemplate().getCrystalCount() > 0)) {
                                int crystalAmount = (int) (item.getTemplate().getCrystalCount() * 0.87D);
                                if (item.getEnchantLevel() > 3)
                                    crystalAmount = (int) (crystalAmount + item.getTemplate().getCrystalCount() * 0.25D * (item.getEnchantLevel() - 3));
                                if (crystalAmount < 1) {
                                    crystalAmount = 1;
                                }
                                ItemFunctions.addItem(player, crystalId, crystalAmount, true);
                            }
                            player.getEnchantParams().targetItem = null;
                            item = null;
                            break;
                        case OLF_BLESSED:
                        case BLESSED:
                        case ITEM_MALL:
                        case CRYSTALL:
                            if (esi.getType() == EnchantScrollType.OLF_BLESSED)
                                item.setEnchantLevel(ServicesConfig.olfBlessedFailEnchantLvl);
                            else
                                item.setEnchantLevel(ServicesConfig.blessedFailEnchantLvl);
                            item.setJdbcState(JdbcEntityState.UPDATED);
                            item.update();
                            break;
                        case DESTRUCTION:
                            if (LostDreamCustom.customDestructionEnchant) {
                                if (item.getEnchantLevel() > ServicesConfig.SAFE_ENCHANT_COMMON)
                                    item.setEnchantLevel(item.getEnchantLevel() - 1);
                            }
                        case ANCIENT:
                            item.setJdbcState(JdbcEntityState.UPDATED);
                            item.update();
                            break;
                        default:
                            break;
                    }
                    return;
                }
                int ench = item.getEnchantLevel();
                if (ench > maxEnchant)
                    maxEnchant = ench;
                isNeedUpdate = true;
                if (ench >= ServicesConfig.SAFE_ENCHANT_COMMON)
                    count++;
            }
        } finally {
            if (isNeedEquip && item != null)
                inventory.equipItem(item);
            inventory.writeUnlock();
            if (isNeedUpdate) {
                if (ServicesConfig.ENCHANT_CONSUME_ITEM != 0) {
                    player.getInventory().destroyItemByItemId(ServicesConfig.ENCHANT_CONSUME_ITEM, ServicesConfig.ENCHANT_CONSUME_ITEM_COUNT);
                    ItemTemplate template = ItemTemplateHolder.getInstance().getTemplate(ServicesConfig.ENCHANT_CONSUME_ITEM);
                    player.sendMessage("Потрачено " + ServicesConfig.ENCHANT_CONSUME_ITEM_COUNT + " "
                            + (template != null ? template.getName() : "[Ошибка: не найден item id! Пожалуйста сообщение администратору об ошибке]"));
                }
                player.sendPacket(new InventoryUpdate());
                Map<String, Integer> result = new HashMap<>();
                result.put("crystallized", isCrystallized);
                result.put("enchant", item == null ? 0 : item.getEnchantLevel());
                result.put("maxenchant", maxEnchant);
                result.put("scrolls", scrolls);
                result.put("commonscrolls", commonScrolls);
                if (count == 0) count++; // unreachable in normal situation
                result.put("chance", (int) (((double) success / ((double) count / 100.)) * 100));
                result.put("success", item == null ? 0 : item.getEnchantLevel() == player.getEnchantParams().maxEnchant ? 1 : 0);
                EnchantParser.getInstance().showResultPage(player, EnchantType.Scroll, result);
            }
            player.setEnchantScroll(null);
            player.updateStats();
        }

    }
}
