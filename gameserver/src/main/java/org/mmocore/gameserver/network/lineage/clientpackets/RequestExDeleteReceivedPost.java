package org.mmocore.gameserver.network.lineage.clientpackets;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.gameserver.database.dao.impl.MailDAO;
import org.mmocore.gameserver.model.mail.Mail;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowReceivedPostList;
import org.mmocore.gameserver.object.Player;

import java.util.List;

/**
 * Запрос на удаление полученных сообщений. Удалить можно только письмо без вложения. Отсылается при нажатии на "delete" в списке полученных писем.
 *
 * @see ExShowReceivedPostList
 * @see org.mmocore.gameserver.network.lineage.clientpackets.RequestExDeleteSentPost
 */
public class RequestExDeleteReceivedPost extends L2GameClientPacket {
    private int _count;
    private int[] _list;

    /**
     * format: dx[d]
     */
    @Override
    protected void readImpl() {
        _count = readD();
        if (_count * 4 > _buf.remaining() || _count > Short.MAX_VALUE || _count < 1) {
            _count = 0;
            return;
        }
        _list = new int[_count]; // количество элементов для удаления
        for (int i = 0; i < _count; i++) {
            _list[i] = readD(); // уникальный номер письма
        }
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null || _count == 0) {
            return;
        }

        final List<Mail> mails = MailDAO.getInstance().getReceivedMailByOwnerId(activeChar.getObjectId());
        if (!mails.isEmpty()) {
            mails.stream().filter(mail -> ArrayUtils.contains(_list, mail.getMessageId())).filter(mail -> mail.getAttachments().isEmpty()).forEach(mail -> MailDAO.getInstance().deleteReceivedMailByMailId(activeChar.getObjectId(), mail.getMessageId()));
        }

        activeChar.sendPacket(new ExShowReceivedPostList(activeChar));
    }
}