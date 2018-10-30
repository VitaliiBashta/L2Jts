package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

/**
 * Based on official High Five
 *
 * @author Magister
 * @version 1.0
 * @date 13/01/2015
 */
public class _901_HowLavasaurusesAreMade extends Quest {
    // npc
    private static final int warsmith_rooney = 32049;
    // questitem
    private static final int g_stone_of_lavasaurus1 = 21909;
    private static final int g_head_part_of_lavasaurus1 = 21910;
    private static final int g_body_part_of_lavasaurus1 = 21911;
    private static final int g_horn_part_of_lavasaurus1 = 21912;
    // mobs
    private static final int lavasaurus_lv1 = 18799;
    private static final int lavasaurus_lv2 = 18800;
    private static final int lavasaurus_lv3 = 18801;
    private static final int lavasaurus_lv4 = 18802;
    // etcitem
    private static final int g_item_totem_of_body1 = 21899;
    private static final int g_item_totem_of_mind1 = 21900;
    private static final int g_item_totem_of_bravery1 = 21901;
    private static final int g_item_totem_of_fortitude1 = 21902;

    public _901_HowLavasaurusesAreMade() {
        super(true);
        addStartNpc(warsmith_rooney);
        addKillId(lavasaurus_lv1, lavasaurus_lv2, lavasaurus_lv3, lavasaurus_lv4);
        addQuestItem(g_stone_of_lavasaurus1, g_head_part_of_lavasaurus1, g_body_part_of_lavasaurus1, g_horn_part_of_lavasaurus1);
        addLevelCheck(76);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == warsmith_rooney) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("dragon_totem", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "warsmith_rooney_q0901_05.htm";
            } else if (event.equalsIgnoreCase("reply_1"))
                htmltext = "warsmith_rooney_q0901_04.htm";
            else if (event.equalsIgnoreCase("reply_2")) {
                st.giveItems(g_item_totem_of_body1, 1);
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(this);
                htmltext = "warsmith_rooney_q0901_13.htm";
            } else if (event.equalsIgnoreCase("reply_3")) {
                st.giveItems(g_item_totem_of_mind1, 1);
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(this);
                htmltext = "warsmith_rooney_q0901_14.htm";
            } else if (event.equalsIgnoreCase("reply_4")) {
                st.giveItems(g_item_totem_of_fortitude1, 1);
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(this);
                htmltext = "warsmith_rooney_q0901_15.htm";
            } else if (event.equalsIgnoreCase("reply_5")) {
                st.giveItems(g_item_totem_of_bravery1, 1);
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(this);
                htmltext = "warsmith_rooney_q0901_16.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("dragon_totem");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == warsmith_rooney) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "warsmith_rooney_q0901_03.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            if (st.isNowAvailable())
                                htmltext = "warsmith_rooney_q0901_01.htm";
                            else
                                htmltext = "warsmith_rooney_q0901_02.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == warsmith_rooney) {
                    if (GetMemoState == 1 && (st.ownItemCount(g_stone_of_lavasaurus1) < 10 || st.ownItemCount(g_head_part_of_lavasaurus1) < 10 || st.ownItemCount(g_body_part_of_lavasaurus1) < 10 || st.ownItemCount(g_horn_part_of_lavasaurus1) < 10))
                        htmltext = "warsmith_rooney_q0901_06.htm";
                    else if (GetMemoState == 1 && st.ownItemCount(g_stone_of_lavasaurus1) >= 10 && st.ownItemCount(g_head_part_of_lavasaurus1) >= 10 && st.ownItemCount(g_body_part_of_lavasaurus1) >= 10 && st.ownItemCount(g_horn_part_of_lavasaurus1) >= 10) {
                        st.takeItems(g_stone_of_lavasaurus1, -1);
                        st.takeItems(g_head_part_of_lavasaurus1, -1);
                        st.takeItems(g_body_part_of_lavasaurus1, -1);
                        st.takeItems(g_horn_part_of_lavasaurus1, -1);
                        st.setMemoState("dragon_totem", String.valueOf(2), true);
                        htmltext = "warsmith_rooney_q0901_07.htm";
                    } else if (GetMemoState == 2)
                        htmltext = "warsmith_rooney_q0901_08.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("dragon_totem");
        int npcId = npc.getNpcId();
        if (npcId == lavasaurus_lv1) {
            if (GetMemoState == 1 && st.ownItemCount(g_stone_of_lavasaurus1) < 10) {
                if (st.ownItemCount(g_stone_of_lavasaurus1) >= 9 && st.ownItemCount(g_head_part_of_lavasaurus1) >= 10 && st.ownItemCount(g_body_part_of_lavasaurus1) >= 10 && st.ownItemCount(g_horn_part_of_lavasaurus1) >= 10) {
                    st.setCond(2);
                    st.giveItems(g_stone_of_lavasaurus1, 1);
                    st.soundEffect(SOUND_MIDDLE);
                } else if (st.ownItemCount(g_stone_of_lavasaurus1) < 10) {
                    st.giveItems(g_stone_of_lavasaurus1, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == lavasaurus_lv2) {
            if (GetMemoState == 1 && st.ownItemCount(g_head_part_of_lavasaurus1) < 10) {
                if (st.ownItemCount(g_stone_of_lavasaurus1) >= 10 && st.ownItemCount(g_head_part_of_lavasaurus1) >= 9 && st.ownItemCount(g_body_part_of_lavasaurus1) >= 10 && st.ownItemCount(g_horn_part_of_lavasaurus1) >= 10) {
                    st.setCond(2);
                    st.giveItems(g_head_part_of_lavasaurus1, 1);
                    st.soundEffect(SOUND_MIDDLE);
                } else if (st.ownItemCount(g_head_part_of_lavasaurus1) < 10) {
                    st.giveItems(g_head_part_of_lavasaurus1, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == lavasaurus_lv3) {
            if (GetMemoState == 1 && st.ownItemCount(g_body_part_of_lavasaurus1) < 10) {
                if (st.ownItemCount(g_stone_of_lavasaurus1) >= 10 && st.ownItemCount(g_head_part_of_lavasaurus1) >= 10 && st.ownItemCount(g_body_part_of_lavasaurus1) >= 9 && st.ownItemCount(g_horn_part_of_lavasaurus1) >= 10) {
                    st.setCond(2);
                    st.giveItems(g_body_part_of_lavasaurus1, 1);
                    st.soundEffect(SOUND_MIDDLE);
                } else if (st.ownItemCount(g_body_part_of_lavasaurus1) < 10) {
                    st.giveItems(g_body_part_of_lavasaurus1, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == lavasaurus_lv4) {
            if (GetMemoState == 1 && st.ownItemCount(g_horn_part_of_lavasaurus1) < 10) {
                if (st.ownItemCount(g_stone_of_lavasaurus1) >= 10 && st.ownItemCount(g_head_part_of_lavasaurus1) >= 10 && st.ownItemCount(g_body_part_of_lavasaurus1) >= 10 && st.ownItemCount(g_horn_part_of_lavasaurus1) >= 9) {
                    st.setCond(2);
                    st.giveItems(g_horn_part_of_lavasaurus1, 1);
                    st.soundEffect(SOUND_MIDDLE);
                } else if (st.ownItemCount(g_horn_part_of_lavasaurus1) < 10) {
                    st.giveItems(g_horn_part_of_lavasaurus1, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        }
        return null;
    }
}