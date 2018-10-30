package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.manager.QuestManager;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.serverpackets.QuestList;
import org.mmocore.gameserver.object.Player;

public class RequestQuestAbort extends L2GameClientPacket {
    private int _questID;

    @Override
    protected void readImpl() {
        _questID = readD();
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        final Quest quest = QuestManager.getQuest(_questID);
        if (activeChar == null || quest == null) {
            return;
        }

        if (!quest.canAbortByPacket()) {
            // обновляем клиент, ибо он сам удаляет
            activeChar.sendPacket(new QuestList(activeChar));
            return;
        }

        final QuestState qs = activeChar.getQuestState(_questID);
        if (qs != null && !qs.isCompleted()) {
            qs.abortQuest();
        }
    }
}