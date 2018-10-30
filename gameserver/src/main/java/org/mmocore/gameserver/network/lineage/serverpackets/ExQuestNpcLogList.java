package org.mmocore.gameserver.network.lineage.serverpackets;


import org.mmocore.gameserver.model.quest.QuestNpcLogInfo;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author VISTALL
 * @date 14:50/26.02.2011
 */
public class ExQuestNpcLogList extends GameServerPacket {
    private final int questId;
    private List<int[]> logList = Collections.emptyList();

    public ExQuestNpcLogList(final QuestState state) {
        questId = state.getQuest().getId();
        final int cond = state.getCond();
        final List<QuestNpcLogInfo> vars = state.getQuest().getNpcLogList(cond);
        if (vars == null) {
            return;
        }

        logList = new ArrayList<>(vars.size());
        for (final QuestNpcLogInfo entry : vars) {
            final int[] i = new int[2];
            i[0] = entry.getNpcIds()[0] + 1000000;
            i[1] = state.getInt(entry.getVarName());
            logList.add(i);
        }
    }

    @Override
    protected void writeData() {
        writeD(questId);
        writeC(logList.size());
        for (final int[] values : logList) {
            writeD(values[0]);
            writeC(0);      // npc index?
            writeD(values[1]);
        }
    }
}
