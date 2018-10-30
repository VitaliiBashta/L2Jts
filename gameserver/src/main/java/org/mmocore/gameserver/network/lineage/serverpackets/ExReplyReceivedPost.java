package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.model.mail.Mail;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.components.items.ItemInstance;

/**
 * Просмотр полученного письма. Шлется в ответ на {@link org.mmocore.gameserver.network.lineage.clientpackets.RequestExRequestReceivedPost}.
 * При попытке забрать приложенные вещи клиент шлет {@link org.mmocore.gameserver.network.lineage.clientpackets.RequestExReceivePost}.
 * При возврате письма клиент шлет {@link org.mmocore.gameserver.network.lineage.clientpackets.RequestExRejectPost}.
 *
 * @see org.mmocore.gameserver.network.lineage.serverpackets.ExReplySentPost
 */
public class ExReplyReceivedPost extends GameServerPacket {
    private final Mail mail;

    public ExReplyReceivedPost(final Mail mail) {
        this.mail = mail;
    }

    // dddSSS dx[hddQdddhhhhhhhhhh] Qdd
    @Override
    protected void writeData() {
        writeD(mail.getMessageId()); // id письма
        writeD(mail.isPayOnDelivery() ? 1 : 0); // 1 - письмо с запросом оплаты, 0 - просто письмо
        writeD(mail.getType() == Mail.SenderType.NORMAL ? 0 : 1); // returnable

        writeS(mail.getSenderName()); // от кого
        writeS(mail.getTopic()); // топик
        writeS(mail.getBody()); // тело

        writeD(mail.getAttachments().size()); // количество приложенных вещей
        for (final ItemInstance item : mail.getAttachments()) {
            writeItemInfo(item);
            writeD(item.getObjectId());
        }

        writeQ(mail.getPrice()); // для писем с оплатой - цена
        writeD(!mail.getAttachments().isEmpty() ? 1 : 0); // 1 - письмо можно вернуть
        writeD(mail.getType().ordinal()); // 1 - на письмо нельзя отвечать, его нельзя вернуть, в отправителе значится news informer (или "****" если установлен флаг в начале пакета)
    }
}