package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _313_CollectSpores extends Quest {
    //NPC
    public final int Herbiel = 30150;
    //Mobs
    public final int SporeFungus = 20509;
    //Quest Items
    public final int SporeSac = 1118;


    public _313_CollectSpores() {
        super(false);

        addStartNpc(Herbiel);
        addTalkId(Herbiel);
        addKillId(SporeFungus);
        addQuestItem(SporeSac);
        addLevelCheck(8, 13);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equalsIgnoreCase("green_q0313_05.htm")) {
            st.setCond(1);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        if (cond == 0) {
            switch (isAvailableFor(st.getPlayer())) {
                case LEVEL:
                    htmltext = "green_q0313_02.htm";
                    st.exitQuest(true);
                    break;
                default:
                    htmltext = "green_q0313_03.htm";
                    break;
            }
        } else if (cond == 1) {
            htmltext = "green_q0313_06.htm";
        } else if (cond == 2) {
            if (st.ownItemCount(SporeSac) < 10) {
                st.setCond(1);
                htmltext = "green_q0313_06.htm";
            } else {
                st.takeItems(SporeSac, -1);
                st.giveItems(ADENA_ID, 3500, true);
                st.soundEffect(SOUND_FINISH);
                htmltext = "green_q0313_07.htm";
                st.exitQuest(true);
            }
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (cond == 1 && npcId == SporeFungus && Rnd.chance(70)) {
            st.giveItems(SporeSac, 1);
            if (st.ownItemCount(SporeSac) < 10) {
                st.soundEffect(SOUND_ITEMGET);
            } else {
                st.soundEffect(SOUND_MIDDLE);
                st.setCond(2);
                st.setState(STARTED);
            }
        }
        return null;
    }
}