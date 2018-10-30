package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.database.dao.impl.MailDAO;
import org.mmocore.gameserver.model.mail.Mail;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

import java.util.Collections;
import java.util.List;


/**
 * Появляется при нажатии на кнопку "почта" или "received mail", входящие письма
 * <br> Ответ на {@link org.mmocore.gameserver.network.lineage.clientpackets.RequestExRequestReceivedPostList}.
 * <br> При нажатии на письмо в списке шлется {@link org.mmocore.gameserver.network.lineage.clientpackets.RequestExRequestReceivedPost} а в ответ {@link org.mmocore.gameserver.network.lineage.serverpackets.ExReplyReceivedPost}.
 * <br> При попытке удалить письмо шлется {@link org.mmocore.gameserver.network.lineage.clientpackets.RequestExDeleteReceivedPost}.
 * <br> При нажатии кнопки send mail шлется {@link org.mmocore.gameserver.network.lineage.clientpackets.RequestExPostItemList}.
 *
 * @see org.mmocore.gameserver.network.lineage.serverpackets.ExShowSentPostList аналогичный список отправленной почты
 */
public class ExShowReceivedPostList extends GameServerPacket {
    private final List<Mail> mails;
    private final int size;

    public ExShowReceivedPostList(final Player cha) {
        mails = MailDAO.getInstance().getReceivedMailByOwnerId(cha.getObjectId());
        size = (int) mails.stream().filter(mail -> !mail.isNewMail()).count();
        Collections.sort(mails);
    }

    // d dx[dSSddddddd]
    @Override
    protected void writeData() {
        writeD((int) (System.currentTimeMillis() / 1000L));
        writeD(size); // количество писем
        for (final Mail mail : mails) {
            if (mail.isNewMail())
                continue;
            writeD(mail.getMessageId()); // уникальный id письма
            writeS(mail.getTopic()); // топик
            writeS(mail.getSenderName()); // отправитель
            writeD(mail.isPayOnDelivery() ? 1 : 0); // если тут 1 то письмо требует оплаты
            writeD(mail.getExpireTime()); // время действительности письма
            writeD(mail.isUnread() ? 1 : 0); // письмо не прочитано - его нельзя удалить и оно выделяется ярким цветом
            writeD(mail.getType() == Mail.SenderType.NORMAL ? 0 : 1); // returnable
            writeD(mail.getAttachments().isEmpty() ? 0 : 1); // 1 - письмо с приложением, 0 - просто письмо
            //TODO [VISTALL] returned
            writeD(0x00); // если тут 1 и следующий параметр 1 то отправителем будет "****", если тут 2 то следующий параметр игнорируется
            writeD(mail.getType().ordinal()); // 1 - отправителем значится "**News Informer**"
            writeD(0x00);
        }
    }
}