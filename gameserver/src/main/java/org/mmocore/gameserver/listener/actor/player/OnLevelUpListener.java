package org.mmocore.gameserver.listener.actor.player;

import org.mmocore.gameserver.listener.PlayerListener;
import org.mmocore.gameserver.object.Player;

/**
 * @author KilRoy
 */
@FunctionalInterface
public interface OnLevelUpListener extends PlayerListener {
    void onLevelUp(Player player, int level);
}