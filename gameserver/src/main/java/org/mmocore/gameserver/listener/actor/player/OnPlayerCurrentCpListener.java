package org.mmocore.gameserver.listener.actor.player;

import org.mmocore.gameserver.listener.PlayerListener;
import org.mmocore.gameserver.object.Player;

/**
 * Create by Mangol on 30.12.2015.
 */
@FunctionalInterface
public interface OnPlayerCurrentCpListener extends PlayerListener {
    void onPlayerCurrentCp(final Player player, final double currentCp, final double currentCpPercents);
}
