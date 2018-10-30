package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.serverpackets.ExQuestNpcLogList;
import org.mmocore.gameserver.object.Player;

/**
 * @author VISTALL
 * @date 14:47/26.02.2011
 */
public class RequestAddExpandQuestAlarm extends L2GameClientPacket {
    private int _questId;

    @Override
    protected void readImpl() {
        _questId = readD();
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }

        final QuestState state = player.getQuestState(_questId);
        if (state == null) {
            return;
        }

        player.sendPacket(new ExQuestNpcLogList(state));
    }
}
