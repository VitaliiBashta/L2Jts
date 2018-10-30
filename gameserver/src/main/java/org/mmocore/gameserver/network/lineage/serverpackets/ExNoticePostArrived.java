package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

/**
 * Уведомление о получении почты. При нажатии на него клиент отправляет {@link org.mmocore.gameserver.network.lineage.clientpackets.RequestExRequestReceivedPostList}.
 */
public class ExNoticePostArrived extends GameServerPacket {
    public static final GameServerPacket STATIC_TRUE = new ExNoticePostArrived(1);
    public static final GameServerPacket STATIC_FALSE = new ExNoticePostArrived(0);

    private final int anim;

    public ExNoticePostArrived(final int useAnim) {
        anim = useAnim;
    }

    @Override
    protected void writeData() {
        writeD(anim); // 0 - просто показать уведомление, 1 - с красивой анимацией
    }
}