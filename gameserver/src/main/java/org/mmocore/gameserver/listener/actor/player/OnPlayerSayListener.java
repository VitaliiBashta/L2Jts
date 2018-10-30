package org.mmocore.gameserver.listener.actor.player;

import org.mmocore.gameserver.listener.PlayerListener;
import org.mmocore.gameserver.network.lineage.components.ChatType;
import org.mmocore.gameserver.object.Player;

/**
 * @author VISTALL
 * @date 20:45/15.09.2011
 */
@FunctionalInterface
public interface OnPlayerSayListener extends PlayerListener {
    void onSay(Player activeChar, ChatType type, String target, String text);
}
