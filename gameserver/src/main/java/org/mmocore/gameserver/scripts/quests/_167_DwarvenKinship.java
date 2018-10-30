package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _167_DwarvenKinship extends Quest {
    //NPC
    private static final int Carlon = 30350;
    private static final int Haprock = 30255;
    private static final int Norman = 30210;
    //Quest Items
    private static final int CarlonsLetter = 1076;
    private static final int NormansLetter = 1106;


    public _167_DwarvenKinship() {
        super(false);

        addStartNpc(Carlon);

        addTalkId(Haprock);
        addTalkId(Norman);

        addQuestItem(CarlonsLetter, NormansLetter);
        addLevelCheck(15);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equalsIgnoreCase("30350-04.htm")) {
            st.giveItems(CarlonsLetter, 1);
            st.soundEffect(SOUND_ACCEPT);
            st.setCond(1);
            st.setState(STARTED);
        } else if (event.equalsIgnoreCase("30255-03.htm")) {
            st.takeItems(CarlonsLetter, -1);
            st.giveItems(ADENA_ID, 2000);
            st.giveItems(NormansLetter, 1);
            st.setCond(2);
            st.setState(STARTED);
        } else if (event.equalsIgnoreCase("30255-04.htm")) {
            st.takeItems(CarlonsLetter, -1);
            st.giveItems(ADENA_ID, 2000);
            st.soundEffect(SOUND_GIVEUP);
            st.exitQuest(false);
        } else if (event.equalsIgnoreCase("30210-02.htm")) {
            st.takeItems(NormansLetter, -1);
            st.giveItems(ADENA_ID, 20000);
            st.soundEffect(SOUND_FINISH);
            st.exitQuest(false);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npcId == Carlon) {
            if (cond == 0) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = "30350-02.htm";
                        st.exitQuest(true);
                        break;
                    default:
                        htmltext = "30350-03.htm";
                        break;
                }
            } else if (cond > 0) {
                htmltext = "30350-05.htm";
            }
        } else if (npcId == Haprock) {
            if (cond == 1) {
                htmltext = "30255-01.htm";
            } else if (cond > 1) {
                htmltext = "30255-05.htm";
            }
        } else if (npcId == Norman && cond == 2) {
            htmltext = "30210-01.htm";
        }
        return htmltext;
    }
}