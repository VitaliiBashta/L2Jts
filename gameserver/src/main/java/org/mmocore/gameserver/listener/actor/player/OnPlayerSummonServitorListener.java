package org.mmocore.gameserver.listener.actor.player;

import org.mmocore.gameserver.listener.PlayerListener;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.Servitor;

/**
 * @author VISTALL
 * @date 15:37/05.08.2011
 */
@FunctionalInterface
public interface OnPlayerSummonServitorListener extends PlayerListener {
    void onSummonServitor(Player player, Servitor summon);
}
