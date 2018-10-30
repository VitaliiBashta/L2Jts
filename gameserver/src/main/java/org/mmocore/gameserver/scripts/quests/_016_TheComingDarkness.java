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
public class _016_TheComingDarkness extends Quest {
    // NPC
    public final int dark_presbyter = 31517;
    public final int vicious_altar1 = 31512;
    public final int vicious_altar2 = 31513;
    public final int vicious_altar3 = 31514;
    public final int vicious_altar4 = 31515;
    public final int vicious_altar5 = 31516;
    // QUEST ITEMS
    public final int q_crystal_of_sealing = 7167;

    public _016_TheComingDarkness() {
        super(false);
        addStartNpc(dark_presbyter);
        addTalkId(vicious_altar1, vicious_altar2, vicious_altar3, vicious_altar4, vicious_altar5);
        addQuestItem(q_crystal_of_sealing);
        addLevelCheck(62);
    }

    @Override
    public String onEvent(final String event, final QuestState st, final NpcInstance npc) {
        String htmltext = event;
        final int GetMemoState = st.getInt("draw_to_darkness");
        if (event.equalsIgnoreCase("quest_accept")) {
            st.setCond(1);
            st.setMemoState("draw_to_darkness", String.valueOf(1), true);
            st.giveItems(q_crystal_of_sealing, 5);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            htmltext = "dark_presbyter_q0016_04.htm";
        } else if (event.equalsIgnoreCase("reply_1") && GetMemoState == 1) {
            if (st.ownItemCount(q_crystal_of_sealing) >= 1) {
                st.setCond(2);
                st.setMemoState("draw_to_darkness", String.valueOf(2), true);
                st.takeItems(q_crystal_of_sealing, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "vicious_altar1_q0016_02.htm";
                //TODO: myself::AddUseSkillDesire( myself.sm, @s_quest_unsealed_altar1, @ST_ATTACK, @AMT_STAND, 1000000 );
            } else {
                htmltext = "vicious_altar1_q0016_03.htm";
            }
        } else if (event.equalsIgnoreCase("reply_1a") && GetMemoState == 2) {
            if (st.ownItemCount(q_crystal_of_sealing) >= 1) {
                st.setCond(3);
                st.setMemoState("draw_to_darkness", String.valueOf(3), true);
                st.takeItems(q_crystal_of_sealing, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "vicious_altar2_q0016_02.htm";
                //TODO: myself::AddUseSkillDesire( myself.sm, @s_quest_unsealed_altar1, @ST_ATTACK, @AMT_STAND, 1000000 );
            } else {
                htmltext = "vicious_altar2_q0016_03.htm";
            }
        } else if (event.equalsIgnoreCase("reply_1b") && GetMemoState == 3) {
            if (st.ownItemCount(q_crystal_of_sealing) >= 1) {
                st.setCond(4);
                st.setMemoState("draw_to_darkness", String.valueOf(4), true);
                st.takeItems(q_crystal_of_sealing, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "vicious_altar3_q0016_02.htm";
                //TODO: myself::AddUseSkillDesire( myself.sm, @s_quest_unsealed_altar1, @ST_ATTACK, @AMT_STAND, 1000000 );
            } else {
                htmltext = "vicious_altar3_q0016_03.htm";
            }
        } else if (event.equalsIgnoreCase("reply_1c") && GetMemoState == 4) {
            if (st.ownItemCount(q_crystal_of_sealing) >= 1) {
                st.setCond(5);
                st.setMemoState("draw_to_darkness", String.valueOf(5), true);
                st.takeItems(q_crystal_of_sealing, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "vicious_altar4_q0016_02.htm";
                //TODO: myself::AddUseSkillDesire( myself.sm, @s_quest_unsealed_altar1, @ST_ATTACK, @AMT_STAND, 1000000 );
            } else {
                htmltext = "vicious_altar4_q0016_03.htm";
            }
        } else if (event.equalsIgnoreCase("reply_1d") && GetMemoState == 5) {
            if (st.ownItemCount(q_crystal_of_sealing) >= 1) {
                st.setCond(6);
                st.setMemoState("draw_to_darkness", String.valueOf(6), true);
                st.takeItems(q_crystal_of_sealing, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "vicious_altar5_q0016_02.htm";
                //TODO: myself::AddUseSkillDesire( myself.sm, @s_quest_unsealed_altar1, @ST_ATTACK, @AMT_STAND, 1000000 );
            } else {
                htmltext = "vicious_altar5_q0016_03.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(final NpcInstance npc, final QuestState st) {
        String htmltext = "noquest";
        final int npcId = npc.getNpcId();
        final int GetMemoState = st.getInt("draw_to_darkness");
        final int id = st.getState();
        switch (id) {
            case CREATED: {
                if (npcId == dark_presbyter) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL: {
                            htmltext = "dark_presbyter_q0016_03.htm";
                            st.exitQuest(true);
                            break;
                        }
                        default: {
                            htmltext = "dark_presbyter_q0016_01.htm";
                            break;
                        }
                    }
                }
                break;
            }
            case STARTED: {
                if (npcId == dark_presbyter) {
                    if (GetMemoState >= 1 && GetMemoState <= 5 && st.ownItemCount(q_crystal_of_sealing) >= 6) {
                        htmltext = "dark_presbyter_q0016_05.htm";
                    } else if (GetMemoState >= 1 && GetMemoState <= 5 && st.ownItemCount(q_crystal_of_sealing) < 6) {
                        st.takeItems(q_crystal_of_sealing, -1);
                        st.soundEffect(SOUND_FINISH);
                        st.removeMemo("draw_to_darkness");
                        htmltext = "dark_presbyter_q0016_06.htm";
                        st.exitQuest(false);
                    } else if (GetMemoState == 6) {
                        st.addExpAndSp(865187, 69172);
                        st.takeItems(q_crystal_of_sealing, -1);
                        st.soundEffect(SOUND_FINISH);
                        st.removeMemo("draw_to_darkness");
                        htmltext = "dark_presbyter_q0016_07.htm";
                        st.exitQuest(false);
                    }
                } else if (npcId == vicious_altar1) {
                    if (GetMemoState == 1 && st.ownItemCount(q_crystal_of_sealing) >= 1) {
                        htmltext = "vicious_altar1_q0016_01.htm";
                    } else if (GetMemoState == 1 && st.ownItemCount(q_crystal_of_sealing) < 1) {
                        htmltext = "vicious_altar1_q0016_04.htm";
                    } else if (GetMemoState == 2) {
                        htmltext = "vicious_altar1_q0016_05.htm";
                    }
                } else if (npcId == vicious_altar2) {
                    if (GetMemoState == 2 && st.ownItemCount(q_crystal_of_sealing) >= 1) {
                        htmltext = "vicious_altar2_q0016_01.htm";
                    } else if (GetMemoState == 2 && st.ownItemCount(q_crystal_of_sealing) < 1) {
                        htmltext = "vicious_altar2_q0016_04.htm";
                    } else if (GetMemoState == 3) {
                        htmltext = "vicious_altar2_q0016_05.htm";
                    }
                } else if (npcId == vicious_altar3) {
                    if (GetMemoState == 3 && st.ownItemCount(q_crystal_of_sealing) >= 1) {
                        htmltext = "vicious_altar3_q0016_01.htm";
                    } else if (GetMemoState == 3 && st.ownItemCount(q_crystal_of_sealing) < 1) {
                        htmltext = "vicious_altar3_q0016_04.htm";
                    } else if (GetMemoState == 4) {
                        htmltext = "vicious_altar3_q0016_05.htm";
                    }
                } else if (npcId == vicious_altar4) {
                    if (GetMemoState == 4 && st.ownItemCount(q_crystal_of_sealing) >= 1) {
                        htmltext = "vicious_altar4_q0016_01.htm";
                    } else if (GetMemoState == 4 && st.ownItemCount(q_crystal_of_sealing) < 1) {
                        htmltext = "vicious_altar4_q0016_04.htm";
                    } else if (GetMemoState == 5) {
                        htmltext = "vicious_altar4_q0016_05.htm";
                    }
                } else if (npcId == vicious_altar5) {
                    if (GetMemoState == 5 && st.ownItemCount(q_crystal_of_sealing) >= 1) {
                        htmltext = "vicious_altar5_q0016_01.htm";
                    } else if (GetMemoState == 5 && st.ownItemCount(q_crystal_of_sealing) < 1) {
                        htmltext = "vicious_altar5_q0016_04.htm";
                    } else if (GetMemoState == 6) {
                        htmltext = "vicious_altar5_q0016_05.htm";
                    }
                }
                break;
            }
        }
        return htmltext;
    }
}