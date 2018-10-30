package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.database.dao.impl.MailDAO;
import org.mmocore.gameserver.model.mail.Mail;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

import java.util.Collections;
import java.util.List;


/**
 * Появляется при нажатии на кнопку "sent mail", исходящие письма
 * Ответ на {@link org.mmocore.gameserver.network.lineage.clientpackets.RequestExRequestSentPostList}
 * При нажатии на письмо в списке шлется {@link org.mmocore.gameserver.network.lineage.clientpackets.RequestExRequestSentPost}, а в ответ {@link org.mmocore.gameserver.network.lineage.serverpackets.ExReplySentPost}.
 * При нажатии на "delete" шлется {@link org.mmocore.gameserver.network.lineage.clientpackets.RequestExDeleteSentPost}.
 *
 * @see ExShowReceivedPostList аналогичный список принятой почты
 */
public class ExShowSentPostList extends GameServerPacket {
    private final List<Mail> mails;

    public ExShowSentPostList(final Player cha) {
        mails = MailDAO.getInstance().getSentMailByOwnerId(cha.getObjectId());
        Collections.sort(mails);
    }

    // d dx[dSSddddd]
    @Override
    protected void writeData() {
        writeD((int) (System.currentTimeMillis() / 1000L));
        writeD(mails.size()); // количество писем
        for (final Mail mail : mails) {
            writeD(mail.getMessageId()); // уникальный id письма
            writeS(mail.getTopic()); // топик
            writeS(mail.getReceiverName()); // получатель
            writeD(mail.isPayOnDelivery() ? 1 : 0); // если тут 1 то письмо требует оплаты
            writeD(mail.getExpireTime()); // время действительности письма
            writeD(mail.isUnread() ? 1 : 0); // ?
            writeD(mail.getType() == Mail.SenderType.NORMAL ? 0 : 1); // returnable
            writeD(mail.getAttachments().isEmpty() ? 0 : 1); // 1 - письмо с приложением, 0 - просто письмо
        }
    }
}