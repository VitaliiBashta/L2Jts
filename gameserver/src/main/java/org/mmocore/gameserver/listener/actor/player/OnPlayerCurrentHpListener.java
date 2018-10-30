package org.mmocore.gameserver.listener.actor.player;

import org.mmocore.gameserver.listener.PlayerListener;
import org.mmocore.gameserver.object.Player;

/**
 * Create by Mangol on 30.12.2015.
 */
@FunctionalInterface
public interface OnPlayerCurrentHpListener extends PlayerListener {
    void onPlayerCurrentHp(final Player player, final double currentHp, final double currentHpPercents);
}
