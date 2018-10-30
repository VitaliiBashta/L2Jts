package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.manager.HellboundManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

/**
 * Based on official Freya
 *
 * @author KilRoy
 * @date 07/12/2014
 */
public class _130_PathToHellbound extends Quest {
    // npc
    private static int sage_kasian = 30612;
    private static int galate = 32292;

    // questitem
    private static int q_blue_crystal_of_kasian = 12823;

    public _130_PathToHellbound() {
        super(false);

        addStartNpc(sage_kasian);
        addTalkId(galate);
        addQuestItem(q_blue_crystal_of_kasian);
        addLevelCheck(78);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int GetMemoState = st.getInt("q_path_to_hellbound");
        String htmltext = event;

        if (event.equals("reply_1")) {
            htmltext = "sage_kasian_q0130_04.htm";
        } else if (event.equalsIgnoreCase("quest_accept")) {
            st.setCond(1);
            st.setMemoState("q_path_to_hellbound", String.valueOf(1), true);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            htmltext = "sage_kasian_q0130_05.htm";
        } else if (event.equals("reply_2")) {
            if (GetMemoState == 2) {
                st.setCond(3);
                st.giveItems(q_blue_crystal_of_kasian, 1);
                st.setMemoState("q_path_to_hellbound", String.valueOf(3), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "sage_kasian_q0130_08.htm";
            }
        } else if (event.equals("reply_3")) {
            if (GetMemoState == 1) {
                htmltext = "galate_q0130_02.htm";
            }
        } else if (event.equals("reply_4")) {
            if (GetMemoState == 1) {
                st.setCond(2);
                st.setMemoState("q_path_to_hellbound", String.valueOf(2), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "galate_q0130_03.htm";
            }
        } else if (event.equals("reply_5")) {
            if (GetMemoState == 3) {
                htmltext = "galate_q0130_06.htm";
            }
        } else if (event.equals("reply_6")) {
            if (GetMemoState == 3) {
                st.takeItems(q_blue_crystal_of_kasian, 1);
                st.removeMemo("q_path_to_hellbound");
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(false);
                htmltext = "galate_q0130_07.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("q_path_to_hellbound");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == sage_kasian) {
                    if (HellboundManager.getHellboundLevel() >= 1) {
                        switch (isAvailableFor(st.getPlayer())) {
                            case LEVEL:
                                htmltext = "sage_kasian_q0130_02.htm";
                                break;
                            default:
                                htmltext = "sage_kasian_q0130_01.htm";
                                break;
                        }
                    } else {
                        htmltext = "sage_kasian_q0130_03.htm";
                    }
                }
                break;
            case STARTED:
                if (npcId == sage_kasian) {
                    if (GetMemoState == 1) {
                        htmltext = "sage_kasian_q0130_06.htm";
                    } else if (GetMemoState == 2) {
                        htmltext = "sage_kasian_q0130_07.htm";
                    } else if (GetMemoState == 3) {
                        htmltext = "sage_kasian_q0130_09.htm";
                    }
                } else if (npcId == galate) {
                    if (GetMemoState == 1) {
                        htmltext = "galate_q0130_01.htm";
                    } else if (GetMemoState == 2) {
                        htmltext = "galate_q0130_04.htm";
                    } else if (GetMemoState == 3) {
                        htmltext = "galate_q0130_05.htm";
                    }
                }
                break;
        }

        return htmltext;
    }
}