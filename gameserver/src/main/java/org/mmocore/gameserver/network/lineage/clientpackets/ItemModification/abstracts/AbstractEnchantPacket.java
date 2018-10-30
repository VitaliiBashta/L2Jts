package org.mmocore.gameserver.network.lineage.clientpackets.ItemModification.abstracts;

import org.mmocore.gameserver.configuration.config.ServicesConfig;
import org.mmocore.gameserver.model.items.etcitems.EnchantScrolls.EnchantScrollInfo;
import org.mmocore.gameserver.model.items.etcitems.EnchantScrolls.EnchantScrollTarget;
import org.mmocore.gameserver.network.lineage.clientpackets.L2GameClientPacket;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.utils.Log;

/**
 * @author ALF, KilRoy
 * @date 29.11.2012
 */
public abstract class AbstractEnchantPacket extends L2GameClientPacket {
    public static boolean isValidPlayer(final Player player) {
        if (player.isActionsDisabled())
            return false;

        if (player.isInTrade()) {
            player.sendPacket(SystemMsg.YOU_CANNOT_ENCHANT_WHILE_OPERATING_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
            return false;
        }

        if (player.isInStoreMode()) {
            player.sendPacket(SystemMsg.YOU_CANNOT_ENCHANT_WHILE_OPERATING_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
            return false;
        }

        return true;
    }

    public static boolean checkItem(final ItemInstance item, final EnchantScrollInfo esi) {
        if (item.isStackable())
            return false;

        if (esi.getTargetItems() != null && !esi.getTargetItems().isEmpty()) {
            return esi.getTargetItems().contains(item.getItemId());
        }

        if (item.getTemplate().getItemGrade().externalOrdinal != esi.getGrade().externalOrdinal)
            return false;

        if (item.isArmor() && esi.getTarget() != EnchantScrollTarget.ARMOR)
            return false;

        if (item.isWeapon() && esi.getTarget() != EnchantScrollTarget.WEAPON)
            return false;

        if (item.isAccessory() && esi.getTarget() != EnchantScrollTarget.ARMOR)
            return false;

        if (!item.canBeEnchanted())
            return false;

        if (item.getLocation() != ItemInstance.ItemLocation.INVENTORY && item.getLocation() != ItemInstance.ItemLocation.PAPERDOLL)
            return false;

        return true;
    }

    public static int getChance(final ItemInstance item, final EnchantScrollInfo esi) {
        int chance = esi.getChance(item.getEnchantLevel());
        if (item.getEnchantLevel() < esi.getSafe()) {
            return 100;
        } else if (item.getTemplate().isMagicWeapon()) {
            chance -= chance * 0.05D;
        } else if (item.getTemplate().isFullArmor()) {
            if (item.getEnchantLevel() < (esi.getSafe() + 1)) {
                return 100;
            } else {
                chance = esi.getChance(item.getEnchantLevel() - 1);
            }
        }
        return chance;
    }

    public static boolean checkReuseTime(Player player) {
        try {
            if (player.getEnchantParams().lastEnchant + ServicesConfig.minReuseEnchantMillis > System.currentTimeMillis()) {
                if (player.getEnchantParams().lastAbuse + ServicesConfig.minReuseEnchantMillis > System.currentTimeMillis()) {
                    player.kick();
                    Log.audit("[Enchant]", "Player(ID:" + player.getObjectId() + ") name: " + player.getName() + " has been kicked for use auto-enchant soft.");
                } else {
                    player.getEnchantParams().lastAbuse = System.currentTimeMillis();
                }
                return false;
            }
            return true;
        } finally {
            player.getEnchantParams().lastEnchant = System.currentTimeMillis();
        }
    }
}