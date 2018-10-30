package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _116_BeyondtheHillsofWinter extends Quest {
    //NPC
    public final int FILAUR = 30535;
    public final int OBI = 32052;
    //Quest Item
    public final int Supplying_Goods_for_Railroad_Worker = 8098;
    //Item
    public final int Bandage = 1833;
    public final int Energy_Stone = 5589;
    public final int Thief_Key = 1661;
    public final int SSD = 1463;


    public _116_BeyondtheHillsofWinter() {
        super(false);

        addStartNpc(FILAUR);
        addTalkId(OBI);
        addLevelCheck(30);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equalsIgnoreCase("elder_filaur_q0116_0104.htm")) {
            st.setCond(1);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("elder_filaur_q0116_0201.htm")) {
            if (st.ownItemCount(Bandage) >= 20 && st.ownItemCount(Energy_Stone) >= 5 && st.ownItemCount(Thief_Key) >= 10) {
                st.takeItems(Bandage, 20);
                st.takeItems(Energy_Stone, 5);
                st.takeItems(Thief_Key, 10);
                st.giveItems(Supplying_Goods_for_Railroad_Worker, 1);
                st.setCond(2);
                st.setState(STARTED);
            } else {
                htmltext = "elder_filaur_q0116_0104.htm";
            }
        } else if (event.equalsIgnoreCase("materials")) {
            htmltext = "railman_obi_q0116_0302.htm";
            st.takeItems(Supplying_Goods_for_Railroad_Worker, 1);
            st.giveItems(SSD, 1740);
            st.addExpAndSp(82792, 4981);
            st.exitQuest(false);
        } else if (event.equalsIgnoreCase("adena")) {
            htmltext = "railman_obi_q0116_0302.htm";
            st.takeItems(Supplying_Goods_for_Railroad_Worker, 1);
            st.giveItems(ADENA_ID, 17387);
            st.addExpAndSp(82792, 4981);
            st.exitQuest(false);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int id = st.getState();
        int cond = 0;
        if (id != CREATED) {
            cond = st.getCond();
        }
        if (npcId == FILAUR) {
            if (cond == 0) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = "elder_filaur_q0116_0103.htm";
                        st.exitQuest(true);
                        break;
                    default:
                        htmltext = "elder_filaur_q0116_0101.htm";
                        break;
                }
            } else if (cond == 1) {
                htmltext = "elder_filaur_q0116_0105.htm";
            } else if (cond == 2) {
                htmltext = "elder_filaur_q0116_0201.htm";
            }
        } else if (npcId == OBI) {
            if (cond == 2 && st.ownItemCount(Supplying_Goods_for_Railroad_Worker) > 0) {
                htmltext = "railman_obi_q0116_0201.htm";
            }
        }
        return htmltext;
    }
}