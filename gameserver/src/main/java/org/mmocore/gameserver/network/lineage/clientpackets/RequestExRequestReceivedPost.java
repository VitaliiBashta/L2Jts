package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.commons.database.dao.JdbcEntityState;
import org.mmocore.gameserver.database.dao.impl.MailDAO;
import org.mmocore.gameserver.model.mail.Mail;
import org.mmocore.gameserver.network.lineage.serverpackets.ExChangePostState;
import org.mmocore.gameserver.network.lineage.serverpackets.ExReplyReceivedPost;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowReceivedPostList;
import org.mmocore.gameserver.object.Player;

/**
 * Запрос информации об полученном письме. Появляется при нажатии на письмо из списка {@link ExShowReceivedPostList}.
 *
 * @see org.mmocore.gameserver.network.lineage.clientpackets.RequestExRequestSentPost
 */
public class RequestExRequestReceivedPost extends L2GameClientPacket {
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

        final Mail mail = MailDAO.getInstance().getReceivedMailByMailId(activeChar.getObjectId(), postId);
        if (mail != null) {
            if (mail.isUnread()) {
                mail.setUnread(false);
                mail.setJdbcState(JdbcEntityState.UPDATED);
                mail.update();
                activeChar.sendPacket(new ExChangePostState(true, Mail.READED, mail));
            }

            activeChar.sendPacket(new ExReplyReceivedPost(mail));
            return;
        }

        activeChar.sendPacket(new ExShowReceivedPostList(activeChar));
    }
}