package org.mmocore.gameserver.handler.chat;

import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.gameserver.network.lineage.components.ChatType;

/**
 * @author VISTALL
 * @date 18:17/12.03.2011
 */
public class ChatHandler extends AbstractHolder {
    private static final ChatHandler _instance = new ChatHandler();

    private final IChatHandler[] _handlers = new IChatHandler[ChatType.VALUES.length];

    private ChatHandler() {

    }

    public static ChatHandler getInstance() {
        return _instance;
    }

    public void register(IChatHandler chatHandler) {
        _handlers[chatHandler.getType().ordinal()] = chatHandler;
    }

    public IChatHandler getHandler(ChatType type) {
        return _handlers[type.ordinal()];
    }

    @Override
    public int size() {
        return _handlers.length;
    }

    @Override
    public void clear() {

    }
}
