package org.mmocore.gameserver.object.components.items.attachments;

import org.mmocore.gameserver.object.Player;

/**
 * @author VISTALL
 * @date 0:50/04.06.2011
 */
public interface PickableAttachment extends ItemAttachment {
    boolean canPickUp(Player player);

    void pickUp(Player player);
}
