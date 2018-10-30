package org.mmocore.gameserver.listener.actor.player;

import org.mmocore.gameserver.listener.PlayerListener;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;

/**
 * Create by Mangol on 31.12.2015.
 */
@FunctionalInterface
public interface OnPlayerAddItemListener extends PlayerListener {
    void onPlayerAddItem(final Player player, final ItemInstance itemInstance);
}
