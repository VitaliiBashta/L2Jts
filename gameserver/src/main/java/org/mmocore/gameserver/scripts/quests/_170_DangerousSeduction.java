package org.mmocore.gameserver.scripts.quests;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _170_DangerousSeduction extends Quest {
    //NPC
    private static final int Vellior = 30305;
    //Quest Items
    private static final int NightmareCrystal = 1046;
    //MOB
    private static final int Merkenis = 27022;


    public _170_DangerousSeduction() {
        super(false);
        addStartNpc(Vellior);
        addTalkId(Vellior);
        addKillId(Merkenis);
        addQuestItem(NightmareCrystal);
        addLevelCheck(21, 26);
        addRaceCheck(PlayerRace.darkelf);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equalsIgnoreCase("30305-04.htm")) {
            st.setCond(1);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npcId == Vellior) {
            if (cond == 0) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = "30305-02.htm";
                        st.exitQuest(true);
                        break;
                    case RACE:
                        htmltext = "30305-00.htm";
                        st.exitQuest(true);
                        break;
                    default:
                        htmltext = "30305-03.htm";
                        break;
                }
            } else if (cond == 1) {
                htmltext = "30305-05.htm";
            } else if (cond == 2) {
                st.takeItems(NightmareCrystal, -1);
                st.giveItems(ADENA_ID, 102680, true);
                st.addExpAndSp(38607, 4018);
                htmltext = "30305-06.htm";
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(false);
            }
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (cond == 1 && npcId == Merkenis) {
            if (st.ownItemCount(NightmareCrystal) == 0) {
                st.giveItems(NightmareCrystal, 1);
            }
            st.soundEffect(SOUND_MIDDLE);
            st.setCond(2);
            st.setState(STARTED);
        }
        return null;
    }
}