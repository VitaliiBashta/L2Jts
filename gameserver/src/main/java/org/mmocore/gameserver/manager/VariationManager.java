package org.mmocore.gameserver.manager;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.jts.dataparser.data.holder.VariationDataHolder;
import org.jts.dataparser.data.holder.variationdata.VariationData;
import org.jts.dataparser.data.holder.variationdata.VariationData.WeaponType;
import org.jts.dataparser.data.holder.variationdata.VariationFeeData;
import org.jts.dataparser.data.holder.variationdata.VariationItemData;
import org.jts.dataparser.data.holder.variationdata.support.*;
import org.jts.dataparser.data.pch.LinkerFactory;
import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.data.xml.holder.OptionDataHolder;
import org.mmocore.gameserver.model.items.etcitems.LifeStone.LifeStoneManager;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Mangol
 */
public class VariationManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(VariationManager.class);
    private TIntObjectMap<TIntObjectMap<VariationFee>> _fee = new TIntObjectHashMap<>();
    private TIntObjectMap<TIntObjectMap<VariationStone>> _stones = new TIntObjectHashMap<>(WeaponType.values().length);
    private Map<String, Integer> items = new HashMap<>();

    public static VariationManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    public void load() {
        for (final VariationItemData item : VariationDataHolder.getInstance().getVariationItemGroup()) {
            final List<Integer> list = new ArrayList<>();
            final String item_group = item.item_group;
            final int id_group = item.id;
            for (final String item_name : item.item_list) {
                final int item_id = LinkerFactory.getInstance().findClearValue(item_name);
                list.add(item_id);
            }
            for (final int id : list) {
                final Optional<ItemTemplate> itemTemplate = Optional.ofNullable(ItemTemplateHolder.getInstance().getTemplate(id));
                if (!itemTemplate.isPresent()) {
                    LOGGER.warn(" itemTemplate isNull id: " + id);
                }
                itemTemplate.get().setVariationGroupId(id_group);
            }
            items.put(item_group, id_group);
        }
        final OptionDataHolder optionDataHolder = OptionDataHolder.getInstance();
        final LinkerFactory linkerFactory = LinkerFactory.getInstance();
        for (final VariationData variation : VariationDataHolder.getInstance().getVariationData()) {
            final WeaponType type = WeaponType.valueOf(variation.weapon_type.name());
            final int mineral_id = LinkerFactory.getInstance().findClearValue(variation.mineral);
            final VariationStone stone = new VariationStone(mineral_id);
            final VariationInfo variat1 = new VariationInfo(1);
            for (final VariationData.VariationGroup group : variation.variation1) {
                final double group_chance = group.group_chance;
                final VariationCategory category = new VariationCategory(group_chance);
                for (final VariationData.VariationInfo info : group.option) {
                    final int option_id = LinkerFactory.getInstance().optionPchfindClearValue(info.option_name);
                    final double option_chance = info.chance;
                    if (optionDataHolder.getTemplate(option_id) == null) {
                        LOGGER.warn("Option data not found, option id - " + option_id);
                        continue;
                    }
                    category.addOption(new VariationOption(option_id, option_chance));
                }
                variat1.addCategory(category);
            }
            stone.addVariation(variat1);
            final VariationInfo variat2 = new VariationInfo(2);
            for (final VariationData.VariationGroup group : variation.variation2) {
                final double group_chance = group.group_chance;
                final VariationCategory category = new VariationCategory(group_chance);
                for (final VariationData.VariationInfo info : group.option) {
                    final int option_id = linkerFactory.optionPchfindClearValue(info.option_name);
                    final double option_chance = info.chance;
                    if (optionDataHolder.getTemplate(option_id) == null) {
                        LOGGER.warn("Option data not found, option id - " + option_id);
                        continue;
                    }
                    category.addOption(new VariationOption(option_id, option_chance));
                }
                variat2.addCategory(category);
            }
            stone.addVariation(variat2);
            addStone(type, stone);
        }
        for (final VariationFeeData fee_data : VariationDataHolder.getInstance().getVariationFreeData()) {
            final int item_group = items.get(fee_data.item_group_name);
            final int mineral_id = linkerFactory.findClearValue(fee_data.mineral);
            if (LifeStoneManager.getStoneInfo(mineral_id) == null) {
                LOGGER.warn("Life Stone not found, Life Stone ID - " + mineral_id);
                continue;
            }
            final int fee_item_id = linkerFactory.findClearValue(fee_data.fee_item_name);
            final int fee_item_count = fee_data.fee_item_count.feeItemCount;
            final int fee_cancel = fee_data.cancel_fee.cancelFee;
            addFee(item_group, new VariationFee(mineral_id, fee_item_id, fee_item_count, fee_cancel));
        }
        LOGGER.info("group : " + _fee.size() + " type : " + _stones.size());
    }

    public void addStone(final WeaponType weaponType, final VariationStone stone) {
        TIntObjectMap<VariationStone> stones = _stones.get(weaponType.ordinal());
        if (stones == null) {
            stones = new TIntObjectHashMap<>();
            _stones.put(weaponType.ordinal(), stones);
        }
        stones.put(stone.getId(), stone);
    }

    public VariationStone getStone(final WeaponType weaponType, final int id) {
        final TIntObjectMap<VariationStone> stones = _stones.get(weaponType.ordinal());
        if (stones == null) {
            return null;
        }
        return stones.get(id);
    }

    public void addFee(final int group_id, final VariationFee mineral) {
        TIntObjectMap<VariationFee> minerales = _fee.get(group_id);
        if (minerales == null) {
            minerales = new TIntObjectHashMap<>();
            _fee.put(group_id, minerales);
        }
        minerales.put(mineral.getStoneId(), mineral);
    }

    public VariationFee getFee(final int group_id, final int mineral) {
        final TIntObjectMap<VariationFee> minerales = _fee.get(group_id);
        if (minerales == null) {
            return null;
        }
        return minerales.get(mineral);
    }

    private static class LazyHolder {
        private static final VariationManager INSTANCE = new VariationManager();
    }
}
