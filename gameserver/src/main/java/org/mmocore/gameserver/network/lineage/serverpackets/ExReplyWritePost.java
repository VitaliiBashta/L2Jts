package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

/**
 * Запрос на отправку нового письма. Шлется в ответ на {@link org.mmocore.gameserver.network.lineage.clientpackets.RequestExSendPost}.
 */
public class ExReplyWritePost extends GameServerPacket {
    public static final GameServerPacket STATIC_TRUE = new ExReplyWritePost(1);
    public static final GameServerPacket STATIC_FALSE = new ExReplyWritePost(0);

    private final int reply;

    /**
     * @param i если 1 окно создания письма закрывается
     */
    public ExReplyWritePost(final int i) {
        reply = i;
    }

    @Override
    protected void writeData() {
        writeD(reply); // 1 - закрыть окно письма, иное - не закрывать
    }
}