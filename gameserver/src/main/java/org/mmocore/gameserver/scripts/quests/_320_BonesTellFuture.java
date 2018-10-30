package org.mmocore.gameserver.scripts.quests;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _320_BonesTellFuture extends Quest {
    //item
    public final int BONE_FRAGMENT = 809;

    public _320_BonesTellFuture() {
        super(false);

        addStartNpc(30359);
        addTalkId(30359);

        addKillId(20517);
        addKillId(20518);

        addQuestItem(BONE_FRAGMENT);
        addLevelCheck(10, 18);
        addRaceCheck(PlayerRace.darkelf);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equalsIgnoreCase("tetrarch_kaitar_q0320_04.htm")) {
            st.setCond(1);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int state = st.getState();
        if (state == CREATED) {
            switch (isAvailableFor(st.getPlayer())) {
                case RACE:
                    htmltext = "tetrarch_kaitar_q0320_00.htm";
                    st.exitQuest(true);
                    break;
                case LEVEL:
                    htmltext = "tetrarch_kaitar_q0320_02.htm";
                    st.exitQuest(true);
                    break;
                default:
                    htmltext = "tetrarch_kaitar_q0320_03.htm";
                    break;
            }
        } else if (st.ownItemCount(BONE_FRAGMENT) < 10) {
            htmltext = "tetrarch_kaitar_q0320_05.htm";
        } else {
            htmltext = "tetrarch_kaitar_q0320_06.htm";
            st.giveItems(ADENA_ID, 8470, true);
            st.takeItems(BONE_FRAGMENT, -1);
            st.exitQuest(true);
            st.removeMemo("cond");
            st.soundEffect(SOUND_FINISH);
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        st.rollAndGive(BONE_FRAGMENT, 1, 1, 10, 10);
        if (st.ownItemCount(BONE_FRAGMENT) >= 10) {
            st.setCond(2);
        }
        st.setState(STARTED);
        return null;
    }
}