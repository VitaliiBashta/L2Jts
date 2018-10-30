package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.object.Player;

public class _188_SealRemoval extends Quest {
    private static final int Dorothy = 30970;
    private static final int Lorain = 30673;
    private static final int Nikola = 30621;

    private static final int BrokenMetal = 10369;


    public _188_SealRemoval() {
        super(false);

        addTalkId(Dorothy, Nikola, Lorain);
        addFirstTalkId(Lorain);
        addQuestItem(BrokenMetal);
        addLevelCheck(41);
        addQuestCompletedCheck(186);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equalsIgnoreCase("researcher_lorain_q0188_03.htm")) {
            st.soundEffect(SOUND_ACCEPT);
            st.setCond(1);
            st.giveItems(BrokenMetal, 1);
        } else if (event.equalsIgnoreCase("maestro_nikola_q0188_03.htm")) {
            st.setCond(2);
            st.soundEffect(SOUND_MIDDLE);
        } else if (event.equalsIgnoreCase("dorothy_the_locksmith_q0188_03.htm")) {
            st.giveItems(ADENA_ID, 98583);
            st.addExpAndSp(285935, 18711);
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
        QuestState qs_187 = st.getPlayer().getQuestState(187);
        if (st.getState() == STARTED) {
            if (npcId == Lorain) {
                if (cond == 0) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                        case QUEST:
                            htmltext = "researcher_lorain_q0188_01.htm";
                            break;
                        default:
                            if (qs_187 != null && qs_187.isCompleted())
                                htmltext = "researcher_lorain_q0188_02.htm";
                            else
                                htmltext = "researcher_lorain_q0188_01.htm";
                            break;
                    }
                } else if (cond == 1) {
                    htmltext = "researcher_lorain_q0188_04.htm";
                }
            } else if (npcId == Nikola) {
                if (cond == 1) {
                    htmltext = "maestro_nikola_q0188_01.htm";
                } else if (cond == 2) {
                    htmltext = "maestro_nikola_q0188_05.htm";
                }
            } else if (npcId == Dorothy) {
                if (cond == 2) {
                    htmltext = "dorothy_the_locksmith_q0188_01.htm";
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