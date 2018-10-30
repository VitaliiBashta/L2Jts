package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.network.lineage.serverpackets.ExNoticePostArrived;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowReceivedPostList;
import org.mmocore.gameserver.object.Player;

/**
 * Отсылается при нажатии на кнопку "почта", "received mail" или уведомление от {@link ExNoticePostArrived}, запрос входящих писем.
 * В ответ шлется {@link ExShowReceivedPostList}
 */
public class RequestExRequestReceivedPostList extends L2GameClientPacket {
    @Override
    protected void readImpl() {
        //just a trigger
    }

    @Override
    protected void runImpl() {
        final Player cha = getClient().getActiveChar();
        if (cha != null) {
            cha.sendPacket(new ExShowReceivedPostList(cha));
        }
    }
}