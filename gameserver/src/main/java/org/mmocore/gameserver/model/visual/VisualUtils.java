package org.mmocore.gameserver.model.visual;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.gameserver.configuration.config.ServicesConfig;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.templates.item.ItemTemplate;

/**
 * Created by Hack
 * Date: 08.06.2017 0:39
 */
public class VisualUtils {
    public static boolean isCostume(ItemInstance item) {
        return isCostume(item.getVisualItemId());
    }

    public static boolean isCostume(int itemId) {
        return ArrayUtils.contains(ServicesConfig.VISUAL_COSTUMES, itemId);
    }

    public static boolean isRejectedConsumable(int itemId) {
        return ArrayUtils.contains(ServicesConfig.VISUAL_DISALLOWED_ITEMS, itemId);
    }

    public static boolean isRejectedConsumable(ItemInstance item) {
        return isRejectedConsumable(item.getItemId());
    }

    public static boolean isAllowedItem(ItemInstance item) {
        return item.getBodyPart() != ItemTemplate.SLOT_BELT && item.getBodyPart() != ItemTemplate.SLOT_UNDERWEAR;
    }

}
