package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.object.Player;

public class _189_ContractCompletion extends Quest {
    private static final int Kusto = 30512;
    private static final int Lorain = 30673;
    private static final int Luka = 30621;
    private static final int Shegfield = 30068;

    private static final int Metal = 10370;


    public _189_ContractCompletion() {
        super(false);

        addTalkId(Kusto, Luka, Lorain, Shegfield);
        addFirstTalkId(Luka);
        addQuestItem(Metal);

        addLevelCheck(42);
        addQuestCompletedCheck(186);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equalsIgnoreCase("blueprint_seller_luka_q0189_03.htm")) {
            st.soundEffect(SOUND_ACCEPT);
            st.setCond(1);
            st.giveItems(Metal, 1);
        } else if (event.equalsIgnoreCase("researcher_lorain_q0189_02.htm")) {
            st.soundEffect(SOUND_MIDDLE);
            st.setCond(2);
            st.takeItems(Metal, -1);
        } else if (event.equalsIgnoreCase("shegfield_q0189_03.htm")) {
            st.setCond(3);
            st.soundEffect(SOUND_MIDDLE);
        } else if (event.equalsIgnoreCase("head_blacksmith_kusto_q0189_02.htm")) {
            st.giveItems(ADENA_ID, 121527);
            st.addExpAndSp(309467, 20614);
            st.exitQuest(false);
            st.soundEffect(SOUND_FINISH);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (st.getState() == STARTED) {
            if (npcId == Luka) {
                if (cond == 0) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                        case QUEST:
                            htmltext = "blueprint_seller_luka_q0189_02.htm";
                            break;
                        default:
                            htmltext = "blueprint_seller_luka_q0189_01.htm";
                            break;
                    }
                } else if (cond == 1) {
                    htmltext = "blueprint_seller_luka_q0189_04.htm";
                }
            } else if (npcId == Lorain) {
                if (cond == 1) {
                    htmltext = "researcher_lorain_q0189_01.htm";
                } else if (cond == 2) {
                    htmltext = "researcher_lorain_q0189_03.htm";
                } else if (cond == 3) {
                    htmltext = "researcher_lorain_q0189_04.htm";
                    st.setCond(4);
                    st.soundEffect(SOUND_MIDDLE);
                } else if (cond == 4) {
                    htmltext = "researcher_lorain_q0189_05.htm";
                }
            } else if (npcId == Shegfield) {
                if (cond == 2) {
                    htmltext = "shegfield_q0189_01.htm";
                } else if (cond == 3) {
                    htmltext = "shegfield_q0189_04.htm";
                }
            } else if (npcId == Kusto) {
                if (cond == 4) {
                    htmltext = "head_blacksmith_kusto_q0189_01.htm";
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onFirstTalk(NpcInstance npc, Player player) {
        switch (isAvailableFor(player)) {
            case LEVEL:
            case QUEST:
                break;
            default:
                if (player.getQuestState(getId()) == null)
                    newQuestState(player, STARTED);
                break;
        }
        return "";
    }
}