package org.mmocore.gameserver.listener.actor.player;

import org.mmocore.gameserver.listener.PlayerListener;
import org.mmocore.gameserver.network.lineage.components.ChatType;
import org.mmocore.gameserver.object.Player;

/**
 * @author KilRoy
 */
@FunctionalInterface
public interface OnPlayerChatListener extends PlayerListener {
    void onPlayerChat(final Player activeChar, final String text, final ChatType chatType, final String target);
}