package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.commons.lang.ArrayUtils;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.network.lineage.components.ChatType;
import org.mmocore.gameserver.object.Player;

public class Say2C extends L2GameClientPacket {
    private String _text;
    private ChatType _type;
    private String _target;

    @Override
    protected void readImpl() {
        _text = readS(ServerConfig.CHAT_MESSAGE_MAX_LEN);
        _type = ArrayUtils.valid(ChatType.VALUES, readD());
        _target = _type == ChatType.TELL ? readS(ServerConfig.CNAME_MAXLEN) : null;
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        activeChar.getListeners().onPlayerChat(_text, _type, _target);
    }
}