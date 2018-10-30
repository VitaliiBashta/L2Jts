package org.mmocore.gameserver.listener.actor.player;

import org.mmocore.gameserver.listener.PlayerListener;
import org.mmocore.gameserver.object.Player;

/**
 * @author cyberhuman
 * @date 22:32/26.04.2012
 */
@FunctionalInterface
public interface OnMailActivationListener extends PlayerListener {

    void onMailActivation(Player activeChar);
}
