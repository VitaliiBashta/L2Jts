package org.mmocore.gameserver.utils;

import org.jts.dataparser.data.holder.variationdata.support.*;
import org.mmocore.commons.database.dao.JdbcEntityState;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.manager.VariationManager;
import org.mmocore.gameserver.network.lineage.serverpackets.InventoryUpdate;
import org.mmocore.gameserver.network.lineage.serverpackets.ShortCutRegister;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.player.interfaces.ShortCut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class VariationUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(VariationUtils.class);

    public static long getRemovePrice(final ItemInstance item) {
        if (item == null) {
            return -1L;
        }
        final VariationFee group = VariationManager.getInstance().getFee(item.getTemplate().getVariationGroupId(), item.getVariationStoneId());
        if (group == null) {
            if (item.getTemplate().isPvp())
                return 300000; // fixed price for pvp weapon. TODO: config
            else
                return -1L;
        }
        return group.getCancelFee();
    }

    public static VariationFee getVariationFee(final ItemInstance item, final ItemInstance stone) {
        if (item == null) {
            return null;
        }
        if (stone == null) {
            return null;
        }
        final VariationFee group = VariationManager.getInstance().getFee(item.getTemplate().getVariationGroupId(), stone.getItemId());
        if (group == null) {
            return null;
        }
        return group;
    }

    public static boolean tryAugmentItem(final Player player, final ItemInstance targetItem, final ItemInstance refinerItem, final ItemInstance feeItem, final long feeItemCount) {
        final int stoneId = refinerItem.getItemId();
        final VariationStone stone = VariationManager.getInstance().getStone(targetItem.getTemplate().getWeapontType(), stoneId);
        if (stone == null) {
            return false;
        }
        final int variation1Id = getRandomOptionId(stone.getVariation(1));
        final int variation2Id = getRandomOptionId(stone.getVariation(2));
        if (variation1Id == 0 || variation2Id == 0) {
            LOGGER.warn("[VariationUtils] : random variation chance 0 variation1Id = " + variation1Id + " variation2Id = " + variation2Id + " stone=" + stone);
            return false;
        }
        if (!player.getInventory().destroyItem(refinerItem, 1L)) {
            return false;
        }
        if (!player.getInventory().destroyItem(feeItem, feeItemCount)) {
            return false;
        }
        boolean equipped = false;
        if (equipped = targetItem.isEquipped()) {
            player.getInventory().unEquipItem(targetItem);
        }
        targetItem.setVariationStoneId(stoneId);
        targetItem.setVariation1Id(variation1Id);
        targetItem.setVariation2Id(variation2Id);
        targetItem.setJdbcState(JdbcEntityState.UPDATED);
        targetItem.update();
        if (equipped) {
            player.getInventory().equipItem(targetItem);
        }
        player.sendPacket(new InventoryUpdate().addModifiedItem(targetItem));
        player.getShortCutComponent().getAllShortCuts().stream().filter(sc -> sc.getId() == targetItem.getObjectId() && sc.getType() == ShortCut.TYPE_ITEM).forEach(sc -> {
            player.sendPacket(new ShortCutRegister(player, sc));
        });
        player.sendChanges();
        return true;
    }

    //[Hack]: публичный доступ нужен для сервисов
    public static int getRandomOptionId(final VariationInfo variation) {
        if (variation == null) {
            return 0;
        }
        double probalityAmount = 0;
        final VariationCategory[] categories = variation.getCategories();
        for (final VariationCategory category : categories) {
            probalityAmount += category.getProbability();
        }
        if (Rnd.chance(Math.ceil(probalityAmount))) {
            double probalityMod = (100 - probalityAmount) / categories.length;
            final List<VariationCategory> successCategories = new ArrayList<>();
            int tryCount = 0;
            while (successCategories.isEmpty()) {
                tryCount++;
                for (final VariationCategory category : categories) {
                    if (tryCount % 10 == 0) {
                        probalityMod += 1;
                    }
                    if (Rnd.chance(category.getProbability() + probalityMod)) {
                        successCategories.add(category);
                    }
                }
            }
            final VariationCategory[] categoriesArray = successCategories.toArray(new VariationCategory[successCategories.size()]);
            return getRandomOptionId(categoriesArray[Rnd.get(categoriesArray.length)]);
        }
        return 0;
    }

    private static int getRandomOptionId(final VariationCategory category) {
        if (category == null) {
            return 0;
        }
        double chanceAmount = 0;
        final VariationOption[] options = category.getOptions();
        for (final VariationOption option : options) {
            chanceAmount += option.getChance();
        }
        if (Rnd.chance(Math.ceil(chanceAmount))) {
            double chanceMod = (100 - chanceAmount) / options.length;
            final List<VariationOption> successOptions = new ArrayList<>();
            int tryCount = 0;
            while (successOptions.isEmpty()) {
                tryCount++;
                for (final VariationOption option : options) {
                    if (tryCount % 10 == 0) {
                        chanceMod += 1;
                    }
                    if (Rnd.chance(option.getChance() + chanceMod)) {
                        successOptions.add(option);
                    }
                }
            }
            final VariationOption[] optionsArray = successOptions.toArray(new VariationOption[successOptions.size()]);
            return optionsArray[Rnd.get(optionsArray.length)].getId();
        }
        return 0;
    }
}
