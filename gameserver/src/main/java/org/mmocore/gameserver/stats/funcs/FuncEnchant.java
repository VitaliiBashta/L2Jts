package org.mmocore.gameserver.stats.funcs;

import org.jts.dataparser.data.holder.ArmorEnchantBonusDataHolder;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.Stats;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.templates.item.ItemType;
import org.mmocore.gameserver.templates.item.WeaponTemplate.WeaponType;

public class FuncEnchant extends Func {
    public FuncEnchant(final Stats stat, final int order, final Object owner, final double value) {
        super(stat, order, owner);
    }

    @Override
    public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
        final ItemInstance item = (ItemInstance) owner;

        final int enchant = item.getEnchantLevel();
        final int overenchant = Math.max(0, enchant - 3);

        switch (stat) {
            case SHIELD_DEFENCE:
            case MAGIC_DEFENCE:
            case POWER_DEFENCE: {
                return initialValue + enchant + overenchant * 2;
            }

            case MAX_HP: {
                return initialValue + ArmorEnchantBonusDataHolder.getInstance().getHpBonus(item.getTemplate().getCrystalType().externalOrdinal,
                        item.getEnchantLevel(),
                        item.getTemplate().getBodyPart() == ItemTemplate.SLOT_FULL_ARMOR);
            }

            case MAGIC_ATTACK: {
                switch (item.getTemplate().getCrystalType().cry) {
                    case ItemTemplate.CRYSTAL_S:
                        return initialValue + 4 * (enchant + overenchant);
                    case ItemTemplate.CRYSTAL_A:
                        return initialValue + 3 * (enchant + overenchant);
                    case ItemTemplate.CRYSTAL_B:
                        return initialValue + 3 * (enchant + overenchant);
                    case ItemTemplate.CRYSTAL_C:
                        return initialValue + 3 * (enchant + overenchant);
                    case ItemTemplate.CRYSTAL_D:
                    case ItemTemplate.CRYSTAL_NONE:
                        return initialValue + 2 * (enchant + overenchant);
                }
            }

            case POWER_ATTACK: {
                final ItemType itemType = item.getItemType();
                final boolean isBow = itemType == WeaponType.BOW || itemType == WeaponType.CROSSBOW;
                final boolean isSword = (itemType == WeaponType.DUALFIST || itemType == WeaponType.DUAL || itemType == WeaponType.BIGSWORD ||
                        itemType == WeaponType.SWORD || itemType == WeaponType.RAPIER || itemType == WeaponType.ANCIENTSWORD) &&
                        item.getTemplate().getBodyPart() == ItemTemplate.SLOT_LR_HAND;
                switch (item.getTemplate().getCrystalType().cry) {
                    case ItemTemplate.CRYSTAL_S:
                        if (isBow) {
                            return initialValue + 10 * (enchant + overenchant);
                        } else if (isSword) {
                            return initialValue + 6 * (enchant + overenchant);
                        } else {
                            return initialValue + 5 * (enchant + overenchant);
                        }
                    case ItemTemplate.CRYSTAL_A:
                        if (isBow) {
                            return initialValue + 8 * (enchant + overenchant);
                        } else if (isSword) {
                            return initialValue + 5 * (enchant + overenchant);
                        } else {
                            return initialValue + 4 * (enchant + overenchant);
                        }
                    case ItemTemplate.CRYSTAL_B:
                    case ItemTemplate.CRYSTAL_C:
                        if (isBow) {
                            return initialValue + 6 * (enchant + overenchant);
                        } else if (isSword) {
                            return initialValue + 4 * (enchant + overenchant);
                        } else {
                            return initialValue + 3 * (enchant + overenchant);
                        }
                    case ItemTemplate.CRYSTAL_D:
                    case ItemTemplate.CRYSTAL_NONE:
                        if (isBow) {
                            return initialValue + 4 * (enchant + overenchant);
                        } else {
                            return initialValue + 2 * (enchant + overenchant);
                        }
                }
            }
        }

        return initialValue;
    }
}