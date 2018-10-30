package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _017_LightAndDarkness extends Quest {
    // npc
    private static final int dark_presbyter = 31517;
    private static final int blessed_altar1 = 31508;
    private static final int blessed_altar2 = 31509;
    private static final int blessed_altar3 = 31510;
    private static final int blessed_altar4 = 31511;
    // questitem
    private static final int q_blood_of_saint = 7168;

    public _017_LightAndDarkness() {
        super(false);
        addStartNpc(dark_presbyter);
        addTalkId(blessed_altar1, blessed_altar2, blessed_altar3, blessed_altar4);
        addQuestItem(q_blood_of_saint);
        addQuestCompletedCheck(15);
        addLevelCheck(61);
    }

    @Override
    public String onEvent(final String event, final QuestState st, final NpcInstance npc) {
        String htmltext = event;
        final int GetMemoState = st.getInt("darkenning_light");
        final int npcId = npc.getNpcId();
        if (npcId == dark_presbyter) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("darkenning_light", String.valueOf(1), true);
                st.giveItems(q_blood_of_saint, 4);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "dark_presbyter_q0017_04.htm";
            } else if (event.equalsIgnoreCase("reply_1")) {
                htmltext = "dark_presbyter_q0017_02.htm";
            }
        } else if (npcId == blessed_altar1) {
            if (event.equalsIgnoreCase("reply_1") && GetMemoState == 1) {
                if (st.ownItemCount(q_blood_of_saint) >= 1) {
                    st.setCond(2);
                    st.setMemoState("darkenning_light", String.valueOf(2), true);
                    st.takeItems(q_blood_of_saint, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    // TODO: myself::AddUseSkillDesire( myself.sm,
                    // @s_quest_cursed_altar1, @ST_ATTACK, @AMT_STAND, 1000000
                    // );
                    htmltext = "blessed_altar1_q0017_02.htm";
                } else {
                    htmltext = "blessed_altar1_q0017_03.htm";
                }
            }
        } else if (npcId == blessed_altar2) {
            if (event.equalsIgnoreCase("reply_1") && GetMemoState == 2) {
                if (st.ownItemCount(q_blood_of_saint) >= 1) {
                    st.setCond(3);
                    st.setMemoState("darkenning_light", String.valueOf(3), true);
                    st.takeItems(q_blood_of_saint, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    // TODO: myself::AddUseSkillDesire( myself.sm,
                    // @s_quest_cursed_altar1, @ST_ATTACK, @AMT_STAND, 1000000
                    // );
                    htmltext = "blessed_altar2_q0017_02.htm";
                } else {
                    htmltext = "blessed_altar2_q0017_03.htm";
                }
            }
        } else if (npcId == blessed_altar3) {
            if (event.equalsIgnoreCase("reply_1") && GetMemoState == 3) {
                if (st.ownItemCount(q_blood_of_saint) >= 1) {
                    st.setCond(4);
                    st.setMemoState("darkenning_light", String.valueOf(4), true);
                    st.takeItems(q_blood_of_saint, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    // TODO: myself::AddUseSkillDesire( myself.sm,
                    // @s_quest_cursed_altar1, @ST_ATTACK, @AMT_STAND, 1000000
                    // );
                    htmltext = "blessed_altar3_q0017_02.htm";
                } else {
                    htmltext = "blessed_altar3_q0017_03.htm";
                }
            }
        } else if (npcId == blessed_altar4) {
            if (event.equalsIgnoreCase("reply_1") && GetMemoState == 4) {
                if (st.ownItemCount(q_blood_of_saint) >= 1) {
                    st.setCond(5);
                    st.setMemoState("darkenning_light", String.valueOf(5), true);
                    st.takeItems(q_blood_of_saint, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    // TODO: myself::AddUseSkillDesire( myself.sm,
                    // @s_quest_cursed_altar1, @ST_ATTACK, @AMT_STAND, 1000000
                    // );
                    htmltext = "blessed_altar4_q0017_02.htm";
                } else {
                    htmltext = "blessed_altar4_q0017_03.htm";
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("darkenning_light");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED: {
                if (npcId == dark_presbyter) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case QUEST:
                        case LEVEL: {
                            htmltext = "dark_presbyter_q0017_03.htm";
                            st.exitQuest(true);
                            break;
                        }
                        default: {
                            htmltext = "dark_presbyter_q0017_01.htm";
                            break;
                        }
                    }
                }
                break;
            }
            case STARTED: {
                if (npcId == dark_presbyter) {
                    if (GetMemoState >= 1 && GetMemoState <= 4 && GetMemoState + st.ownItemCount(q_blood_of_saint) >= 5) {
                        htmltext = "dark_presbyter_q0017_05.htm";
                    } else if (GetMemoState >= 1 && GetMemoState <= 4 && GetMemoState + st.ownItemCount(q_blood_of_saint) < 5) {
                        st.takeItems(q_blood_of_saint, -1);
                        st.removeMemo("darkenning_light");
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(true);
                        htmltext = "dark_presbyter_q0017_06.htm";
                    } else if (GetMemoState == 5) {
                        st.addExpAndSp(697040, 54887);
                        st.takeItems(q_blood_of_saint, -1);
                        st.removeMemo("darkenning_light");
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                        htmltext = "dark_presbyter_q0017_07.htm";
                    }
                } else if (npcId == blessed_altar1) {
                    if (GetMemoState == 1 && st.ownItemCount(q_blood_of_saint) >= 1) {
                        htmltext = "blessed_altar1_q0017_01.htm";
                    } else if (GetMemoState == 1 && st.ownItemCount(q_blood_of_saint) < 1) {
                        htmltext = "blessed_altar1_q0017_04.htm";
                    } else if (GetMemoState == 2) {
                        htmltext = "blessed_altar1_q0017_05.htm";
                    }
                } else if (npcId == blessed_altar2) {
                    if (GetMemoState == 2 && st.ownItemCount(q_blood_of_saint) >= 1) {
                        htmltext = "blessed_altar2_q0017_01.htm";
                    } else if (GetMemoState == 2 && st.ownItemCount(q_blood_of_saint) < 1) {
                        htmltext = "blessed_altar2_q0017_04.htm";
                    } else if (GetMemoState == 3) {
                        htmltext = "blessed_altar2_q0017_05.htm";
                    }
                } else if (npcId == blessed_altar3) {
                    if (GetMemoState == 3 && st.ownItemCount(q_blood_of_saint) >= 1) {
                        htmltext = "blessed_altar3_q0017_01.htm";
                    } else if (GetMemoState == 3 && st.ownItemCount(q_blood_of_saint) < 1) {
                        htmltext = "blessed_altar3_q0017_04.htm";
                    } else if (GetMemoState == 4) {
                        htmltext = "blessed_altar3_q0017_05.htm";
                    }
                } else if (npcId == blessed_altar4) {
                    if (GetMemoState == 4 && st.ownItemCount(q_blood_of_saint) >= 1) {
                        htmltext = "blessed_altar4_q0017_01.htm";
                    } else if (GetMemoState == 4 && st.ownItemCount(q_blood_of_saint) < 1) {
                        htmltext = "blessed_altar4_q0017_04.htm";
                    } else if (GetMemoState == 5) {
                        htmltext = "blessed_altar4_q0017_05.htm";
                    }
                }
                break;
            }
        }
        return htmltext;
    }
}