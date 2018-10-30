package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.serverpackets.ExReplyPostItemList;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowReceivedPostList;
import org.mmocore.gameserver.object.Player;

/**
 * Нажатие на кнопку "send mail" в списке из {@link ExShowReceivedPostList}, запрос создания нового письма
 * В ответ шлется {@link ExReplyPostItemList}
 */
public class RequestExPostItemList extends L2GameClientPacket {
    @Override
    protected void readImpl() {
        //just a trigger
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (activeChar.isActionsDisabled()) {
            activeChar.sendActionFailed();
        }

        if (!ServerConfig.ALLOW_MAIL) {
            activeChar.sendMessage(new CustomMessage("mail.Disabled"));
            activeChar.sendActionFailed();
            return;
        }

        activeChar.sendPacket(new ExReplyPostItemList(activeChar));
    }
}