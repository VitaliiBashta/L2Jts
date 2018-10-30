package org.mmocore.gameserver.scripts.quests;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _164_BloodFiend extends Quest {
    //NPC
    private static final int Creamees = 30149;
    //Quest Items
    private static final int KirunakSkull = 1044;
    //MOB
    private static final int Kirunak = 27021;


    public _164_BloodFiend() {
        super(false);

        addStartNpc(Creamees);
        addTalkId(Creamees);
        addKillId(Kirunak);
        addQuestItem(KirunakSkull);
        addLevelCheck(21, 26);
        addRaceCheck(PlayerRace.darkelf);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equalsIgnoreCase("30149-04.htm")) {
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
        if (npcId == Creamees) {
            if (cond == 0) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = "30149-02.htm";
                        st.exitQuest(true);
                        break;
                    case RACE:
                        htmltext = "30149-03.htm";
                        break;
                    default:
                        htmltext = "30149-00.htm";
                        st.exitQuest(true);
                        break;
                }
            } else if (cond == 1) {
                htmltext = "30149-05.htm";
            } else if (cond == 2) {
                st.takeItems(KirunakSkull, -1);
                st.giveItems(ADENA_ID, 42130, true);
                st.addExpAndSp(35637, 1854);
                htmltext = "30149-06.htm";
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
        if (cond == 1 && npcId == Kirunak) {
            if (st.ownItemCount(KirunakSkull) == 0) {
                st.giveItems(KirunakSkull, 1);
            }
            st.soundEffect(SOUND_MIDDLE);
            st.setCond(2);
            st.setState(STARTED);
        }
        return null;
    }
}