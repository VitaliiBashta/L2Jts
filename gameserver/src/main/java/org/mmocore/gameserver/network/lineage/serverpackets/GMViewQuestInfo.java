package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

public class GMViewQuestInfo extends GameServerPacket {
    private final Player player;

    public GMViewQuestInfo(final Player player) {
        this.player = player;
    }

    @Override
    protected final void writeData() {
        writeS(player.getName());

        final Quest[] quests = player.getAllActiveQuests();

        if (quests.length == 0) {
            writeH(0);
            writeH(0);
            return;
        }

        writeH(quests.length);
        for (final Quest q : quests) {
            writeD(q.getId());
            final QuestState qs = player.getQuestState(q);
            writeD(qs == null ? 0 : qs.getInt(QuestState.VAR_COND));
        }

        writeH(0); //количество элементов типа: ddQd , как-то связано с предметами
    }
}