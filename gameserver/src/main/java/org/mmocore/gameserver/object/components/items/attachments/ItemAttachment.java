package org.mmocore.gameserver.object.components.items.attachments;

import org.mmocore.gameserver.object.components.items.ItemInstance;

/**
 * @author VISTALL
 * @date 18:55/27.03.2011
 */
@FunctionalInterface
public interface ItemAttachment {
    void setItem(ItemInstance item);
}
