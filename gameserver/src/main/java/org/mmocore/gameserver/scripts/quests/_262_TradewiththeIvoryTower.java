package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _262_TradewiththeIvoryTower extends Quest {
    //NPC
    public final int VOLODOS = 30137;

    //MOB
    public final int GREEN_FUNGUS = 20007;
    public final int BLOOD_FUNGUS = 20400;

    public final int FUNGUS_SAC = 707;


    public _262_TradewiththeIvoryTower() {
        super(false);

        addStartNpc(VOLODOS);
        addKillId(new int[]{
                BLOOD_FUNGUS,
                GREEN_FUNGUS
        });
        addQuestItem(new int[]{FUNGUS_SAC});
        addLevelCheck(8, 16);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equals("vollodos_q0262_03.htm")) {
            st.setCond(1);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        if (cond == 0) {
            switch (isAvailableFor(st.getPlayer())) {
                case LEVEL:
                    htmltext = "vollodos_q0262_01.htm";
                    st.exitQuest(true);
                    break;
                default:
                    htmltext = "vollodos_q0262_02.htm";
                    return htmltext;
            }
        } else if (cond == 1 && st.ownItemCount(FUNGUS_SAC) < 10) {
            htmltext = "vollodos_q0262_04.htm";
        } else if (cond == 2 && st.ownItemCount(FUNGUS_SAC) >= 10) {
            st.giveItems(ADENA_ID, 3000);
            st.takeItems(FUNGUS_SAC, -1);
            st.setCond(0);
            st.soundEffect(SOUND_FINISH);
            htmltext = "vollodos_q0262_05.htm";
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int random = Rnd.get(10);
        if (st.getCond() == 1 && st.ownItemCount(FUNGUS_SAC) < 10) {
            if (npcId == GREEN_FUNGUS && random < 3 || npcId == BLOOD_FUNGUS && random < 4) {
                st.giveItems(FUNGUS_SAC, 1);
                if (st.ownItemCount(FUNGUS_SAC) == 10) {
                    st.setCond(2);
                    st.soundEffect(SOUND_MIDDLE);
                } else {
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        }
        return null;
    }
}