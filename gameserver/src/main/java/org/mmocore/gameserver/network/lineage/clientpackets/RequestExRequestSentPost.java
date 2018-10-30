package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.database.dao.impl.MailDAO;
import org.mmocore.gameserver.model.mail.Mail;
import org.mmocore.gameserver.network.lineage.serverpackets.ExReplySentPost;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowSentPostList;
import org.mmocore.gameserver.object.Player;

/**
 * Запрос информации об отправленном письме. Появляется при нажатии на письмо из списка {@link ExShowSentPostList}.
 * В ответ шлется {@link ExReplySentPost}.
 *
 * @see RequestExRequestReceivedPost
 */
public class RequestExRequestSentPost extends L2GameClientPacket {
    private int postId;

    /**
     * format: d
     */
    @Override
    protected void readImpl() {
        postId = readD(); // id письма
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        final Mail mail = MailDAO.getInstance().getSentMailByMailId(activeChar.getObjectId(), postId);
        if (mail != null) {
            activeChar.sendPacket(new ExReplySentPost(mail));
            return;
        }

        activeChar.sendPacket(new ExShowSentPostList(activeChar));
    }
}