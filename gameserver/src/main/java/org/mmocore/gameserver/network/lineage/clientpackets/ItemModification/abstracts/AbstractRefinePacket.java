package org.mmocore.gameserver.network.lineage.clientpackets.ItemModification.abstracts;

import org.mmocore.gameserver.model.items.etcitems.LifeStone.LifeStoneInfo;
import org.mmocore.gameserver.model.items.etcitems.LifeStone.LifeStoneManager;
import org.mmocore.gameserver.network.lineage.clientpackets.L2GameClientPacket;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.templates.item.ItemTemplate.Grade;

/**
 * @author ALF
 * @date 28.06.2012
 */
public abstract class AbstractRefinePacket extends L2GameClientPacket {
    protected static boolean isValid(Player player, ItemInstance item, ItemInstance refinerItem, ItemInstance gemStones) {
        if (!isValid(player, item, refinerItem))
            return false;

        if (gemStones.getOwnerId() != player.getObjectId())
            return false;

        if (gemStones.getLocation() != ItemInstance.ItemLocation.INVENTORY)
            return false;
        return true;
    }

    protected static boolean isValid(Player player, ItemInstance item, ItemInstance refinerItem) {
        if (!isValid(player, item))
            return false;

        if (refinerItem.getLocation() != ItemInstance.ItemLocation.INVENTORY)
            return false;

        LifeStoneInfo ls = LifeStoneManager.getStoneInfo(refinerItem.getItemId());

        if (ls.getLevel() > 85 && player.getLevel() == 85) {
            return item.canBeAugmented(player, ls.getGrade());
        } else if (player.getLevel() < ls.getLevel())
            return false;
        if (!item.canBeAugmented(player, ls.getGrade()))
            return false;

        return true;
    }

    //[Hack]: публичный доступ нужен для сервисов
    public static boolean isValid(Player player, ItemInstance item) {
        if (!isValid(player))
            return false;

        // Source item can be equipped or in inventory
        switch (item.getLocation()) {
            case INVENTORY:
            case PAPERDOLL:
                break;
            default:
                return false;
        }

        return true;
    }

    protected static boolean isValid(Player player) {
        if (player.isInStoreMode()) {
            player.sendPacket(SystemMsg.YOU_CANNOT_AUGMENT_ITEMS_WHILE_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP_IS_IN_OPERATION);
            return false;
        }
        if (player.isInTrade()) {
            player.sendActionFailed();
            player.sendPacket(SystemMsg.YOU_CANNOT_AUGMENT_ITEMS_WHILE_ENGAGED_IN_TRADE_ACTIVITIES);
            return false;
        }
        if (player.isDead()) {
            player.sendPacket(SystemMsg.YOU_CANNOT_AUGMENT_ITEMS_WHILE_DEAD);
            return false;
        }
        if (player.isParalyzed()) {
            player.sendPacket(SystemMsg.YOU_CANNOT_AUGMENT_ITEMS_WHILE_PARALYZED);
            return false;
        }
        if (player.isFishing()) {
            player.sendPacket(SystemMsg.YOU_CANNOT_AUGMENT_ITEMS_WHILE_FISHING);
            return false;
        }
        if (player.isSitting()) {
            player.sendPacket(SystemMsg.YOU_CANNOT_AUGMENT_ITEMS_WHILE_SITTING_DOWN);
            return false;
        }
        if (player.isActionsDisabled()) {
            player.sendActionFailed();
            player.sendPacket(SystemMsg.AUGMENTATION_FAILED_DUE_TO_INAPPROPRIATE_CONDITIONS);
            return false;
        }
        if (player.getLevel() < 46) {
            player.sendPacket(SystemMsg.AUGMENTATION_FAILED_DUE_TO_INAPPROPRIATE_CONDITIONS);
            return false;
        }

        return true;
    }

    public static boolean isValid(ItemInstance items, Player player) {
        if (!items.canBeEnchanted()) {
            player.sendPacket(SystemMsg.THIS_IS_NOT_A_SUITABLE_ITEM);
            return false;
        }

        if (items.isAugmented()) {
            player.sendPacket(SystemMsg.ONCE_AN_ITEM_IS_AUGMENTED_IT_CANNOT_BE_AUGMENTED_AGAIN);
            return false;
        }

        if (items.isCommonItem()) {
            player.sendPacket(SystemMsg.THIS_IS_NOT_A_SUITABLE_ITEM);
            return false;
        }

        if (items.isTerritoryAccessory()) {
            player.sendPacket(SystemMsg.THIS_IS_NOT_A_SUITABLE_ITEM);
            return false;
        }

        if (items.getTemplate().getItemGrade().ordinal() < Grade.C.ordinal()) {
            player.sendPacket(SystemMsg.THIS_IS_NOT_A_SUITABLE_ITEM);
            return false;
        }

        if (!items.getTemplate().isAugmentable()) {
            player.sendPacket(SystemMsg.THIS_IS_NOT_A_SUITABLE_ITEM);
            return false;
        }

        if (items.isAccessory())
            return true;

        if (items.isArmor()) {
            player.sendPacket(SystemMsg.THIS_IS_NOT_A_SUITABLE_ITEM);
            return false;
        }

        if (items.isWeapon())
            return true;

        return true;
    }
}
