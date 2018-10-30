package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.model.mail.Mail;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.components.items.ItemInstance;

/**
 * Просмотр собственного отправленного письма. Шлется в ответ на {@link org.mmocore.gameserver.network.lineage.clientpackets.RequestExRequestSentPost}.
 * При нажатии на кнопку Cancel клиент шлет {@link org.mmocore.gameserver.network.lineage.clientpackets.RequestExCancelSentPost}.
 *
 * @see ExReplyReceivedPost
 */
public class ExReplySentPost extends GameServerPacket {
    private final Mail mail;

    public ExReplySentPost(final Mail mail) {
        this.mail = mail;
    }

    // ddSSS dx[hddQdddhhhhhhhhhh] Qd
    @Override
    protected void writeData() {
        writeD(mail.getMessageId()); // id письма
        writeD(mail.isPayOnDelivery() ? 1 : 0); // 1 - письмо с запросом оплаты, 0 - просто письмо

        writeS(mail.getReceiverName()); // кому
        writeS(mail.getTopic()); // топик
        writeS(mail.getBody()); // тело

        writeD(mail.getAttachments().size()); // количество приложенных вещей
        for (final ItemInstance item : mail.getAttachments()) {
            writeItemInfo(item);
            writeD(item.getObjectId());
        }

        writeQ(mail.getPrice()); // для писем с оплатой - цена
        writeD(0); // ?
    }
}