package org.mmocore.gameserver.listener.actor.player;

import org.mmocore.gameserver.listener.PlayerListener;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;

/**
 * @author KilRoy
 */
@FunctionalInterface
public interface OnItemPickupListener extends PlayerListener {
    void onItemPickup(Player activeChar, ItemInstance item);
}
