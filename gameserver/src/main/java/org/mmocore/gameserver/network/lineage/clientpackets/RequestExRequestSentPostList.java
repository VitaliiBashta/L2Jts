package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.network.lineage.serverpackets.ExShowSentPostList;
import org.mmocore.gameserver.object.Player;

/**
 * Нажатие на кнопку "sent mail",запрос списка исходящих писем.
 * В ответ шлется {@link ExShowSentPostList}
 */
public class RequestExRequestSentPostList extends L2GameClientPacket {
    @Override
    protected void readImpl() {
        //just a trigger
    }

    @Override
    protected void runImpl() {
        final Player cha = getClient().getActiveChar();
        if (cha != null) {
            cha.sendPacket(new ExShowSentPostList(cha));
        }
    }
}