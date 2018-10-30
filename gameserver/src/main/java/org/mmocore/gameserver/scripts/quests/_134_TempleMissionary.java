package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
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
public class _134_TempleMissionary extends Quest {
    // npc
    private final static int glyvka = 30067;
    private final static int scroll_seller_rouke = 31418;

    // Mobs
    private final static int marsh_stakato = 20157;
    private final static int weird_bee = 20229;
    private final static int marsh_stakato_worker = 20230;
    private final static int inpicio = 20231;
    private final static int marsh_stakato_soldier = 20232;
    private final static int marsh_spider = 20233;
    private final static int marsh_stakato_drone = 20234;
    private final static int pagan_of_cruma = 27339;

    // Quest Items
    private final static int q_giant_tool_part = 10335;
    private final static int q_giant_tool = 10336;
    private final static int q_giant_tech_doc = 10337;
    private final static int q_rouke_report = 10338;
    private final static int q_bedge_foot_of_order = 10339;

    public _134_TempleMissionary() {
        super(false);
        addStartNpc(glyvka);
        addTalkId(scroll_seller_rouke);
        addKillId(marsh_stakato, weird_bee, marsh_stakato_worker, inpicio, marsh_stakato_soldier, marsh_spider, marsh_stakato_drone, pagan_of_cruma);
        addQuestItem(q_giant_tool_part, q_giant_tool, q_giant_tech_doc, q_rouke_report);
        addLevelCheck(35);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("foot_of_order");
        int npcId = npc.getNpcId();

        if (npcId == glyvka) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("foot_of_order", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "glyvka_q0134_03.htm";
            } else if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 1)
                    htmltext = "glyvka_q0134_05.htm";
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 1) {
                    st.setCond(2);
                    st.setMemoState("foot_of_order", String.valueOf(2), true);
                    htmltext = "glyvka_q0134_06.htm";
                    st.soundEffect(SOUND_MIDDLE);
                }
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 6)
                    htmltext = "glyvka_q0134_09.htm";
            } else if (event.equalsIgnoreCase("reply_4"))
                if (GetMemoState == 6) {
                    st.giveItems(q_bedge_foot_of_order, 1);
                    st.giveItems(ADENA_ID, 15100);
                    if (st.getPlayer().getLevel() < 41)
                        st.addExpAndSp(30000, 2000);
                    htmltext = "glyvka_q0134_11.htm";
                    st.removeMemo("foot_of_order");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                }
        } else if (npcId == scroll_seller_rouke)
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 2) {
                    st.setCond(3);
                    st.setMemoState("foot_of_order", String.valueOf(3), true);
                    htmltext = "scroll_seller_rouke_q0134_03.htm";
                    st.soundEffect(SOUND_MIDDLE);
                }
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 4)
                    htmltext = "scroll_seller_rouke_q0134_07.htm";
            } else if (event.equalsIgnoreCase("reply_3"))
                if (GetMemoState == 4) {
                    st.setCond(5);
                    st.setMemoState("foot_of_order", String.valueOf(5), true);
                    htmltext = "scroll_seller_rouke_q0134_09.htm";
                    st.giveItems(q_rouke_report, 1);
                    st.soundEffect(SOUND_MIDDLE);
                }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("foot_of_order");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == glyvka) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "glyvka_q0134_02.htm";
                            break;
                        default:
                            htmltext = "glyvka_q0134_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == glyvka) {
                    if (GetMemoState == 1)
                        htmltext = "glyvka_q0134_04.htm";
                    else if (GetMemoState >= 2 && GetMemoState < 5)
                        htmltext = "glyvka_q0134_07.htm";
                    else if (GetMemoState == 5) {
                        st.takeItems(q_rouke_report, -1);
                        st.setMemoState("foot_of_order", String.valueOf(6), true);
                        htmltext = "glyvka_q0134_08.htm";
                    } else if (GetMemoState == 6)
                        htmltext = "glyvka_q0134_10.htm";
                } else if (npcId == scroll_seller_rouke)
                    if (GetMemoState < 2)
                        htmltext = "scroll_seller_rouke_q0134_01.htm";
                    else if (GetMemoState == 2)
                        htmltext = "scroll_seller_rouke_q0134_02.htm";
                    else if (GetMemoState == 3 && st.ownItemCount(q_giant_tool_part) < 10 && st.ownItemCount(q_giant_tech_doc) < 3)
                        htmltext = "scroll_seller_rouke_q0134_04.htm";
                    else if (GetMemoState == 3 && st.ownItemCount(q_giant_tool_part) >= 10 && st.ownItemCount(q_giant_tech_doc) < 3) {
                        long i0 = st.ownItemCount(q_giant_tool_part) / 10;
                        if (i0 <= 0)
                            htmltext = "scroll_seller_rouke_q0134_04.htm";
                        else {
                            st.giveItems(q_giant_tool, i0);
                            st.takeItems(q_giant_tool_part, i0 * 10);
                        }
                        htmltext = "scroll_seller_rouke_q0134_05.htm";
                    } else if (GetMemoState == 3 && st.ownItemCount(q_giant_tech_doc) >= 3) {
                        st.takeItems(q_giant_tool_part, -1);
                        st.takeItems(q_giant_tool, -1);
                        st.takeItems(q_giant_tech_doc, -1);
                        st.setMemoState("foot_of_order", String.valueOf(4), true);
                        htmltext = "scroll_seller_rouke_q0134_06.htm";
                    } else if (GetMemoState == 4)
                        htmltext = "scroll_seller_rouke_q0134_08.htm";
                    else if (GetMemoState >= 5)
                        htmltext = "scroll_seller_rouke_q0134_10.htm";
                break;
        }

        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("foot_of_order");
        int npcId = npc.getNpcId();

        if (GetMemoState == 3 && st.ownItemCount(q_giant_tech_doc) < 3)
            if (npcId == marsh_stakato) {
                int i0 = Rnd.get(100);
                int i1 = Rnd.get(100);
                if (st.ownItemCount(q_giant_tool) >= 1) {
                    if (i1 < 100) {
                        st.addSpawn(pagan_of_cruma, st.getPlayer().getX() + 20, st.getPlayer().getY() + 20, st.getPlayer().getZ(), 0, 100, 900000);
                        st.takeItems(q_giant_tool, 1);
                    } else
                        st.takeItems(q_giant_tool, 1);
                } else if (i0 < 78) {
                    st.giveItems(q_giant_tool_part, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == weird_bee) {
                int i0 = Rnd.get(100);
                int i1 = Rnd.get(100);
                if (st.ownItemCount(q_giant_tool) >= 1) {
                    if (i1 < 100) {
                        st.addSpawn(pagan_of_cruma, st.getPlayer().getX() + 20, st.getPlayer().getY() + 20, st.getPlayer().getZ(), 0, 100, 900000);
                        st.takeItems(q_giant_tool, 1);
                    } else
                        st.takeItems(q_giant_tool, 1);
                } else if (i0 < 75) {
                    st.giveItems(q_giant_tool_part, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == marsh_stakato_worker) {
                int i0 = Rnd.get(100);
                int i1 = Rnd.get(100);
                if (st.ownItemCount(q_giant_tool) >= 1) {
                    if (i1 < 100) {
                        st.addSpawn(pagan_of_cruma, st.getPlayer().getX() + 20, st.getPlayer().getY() + 20, st.getPlayer().getZ(), 0, 100, 900000);
                        st.takeItems(q_giant_tool, 1);
                    } else
                        st.takeItems(q_giant_tool, 1);
                } else if (i0 < 86) {
                    st.giveItems(q_giant_tool_part, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == inpicio) {
                int i0 = Rnd.get(100);
                int i1 = Rnd.get(100);
                if (st.ownItemCount(q_giant_tool) >= 1) {
                    if (i1 < 100) {
                        st.addSpawn(pagan_of_cruma, st.getPlayer().getX() + 20, st.getPlayer().getY() + 20, st.getPlayer().getZ(), 0, 100, 900000);
                        st.takeItems(q_giant_tool, 1);
                    } else
                        st.takeItems(q_giant_tool, 1);
                } else if (i0 < 83) {
                    st.giveItems(q_giant_tool_part, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == marsh_stakato_soldier) {
                int i0 = Rnd.get(100);
                int i1 = Rnd.get(100);
                if (st.ownItemCount(q_giant_tool) >= 1) {
                    if (i1 < 100) {
                        st.addSpawn(pagan_of_cruma, st.getPlayer().getX() + 20, st.getPlayer().getY() + 20, st.getPlayer().getZ(), 0, 100, 900000);
                        st.takeItems(q_giant_tool, 1);
                    } else
                        st.takeItems(q_giant_tool, 1);
                } else if (i0 < 81) {
                    st.giveItems(q_giant_tool_part, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == marsh_spider) {
                int i0 = Rnd.get(100);
                int i1 = Rnd.get(100);
                if (st.ownItemCount(q_giant_tool) >= 1) {
                    if (i1 < 100) {
                        st.addSpawn(pagan_of_cruma, st.getPlayer().getX() + 20, st.getPlayer().getY() + 20, st.getPlayer().getZ(), 0, 100, 900000);
                        st.takeItems(q_giant_tool, 1);
                    } else
                        st.takeItems(q_giant_tool, 1);
                } else if (i0 < 95) {
                    st.giveItems(q_giant_tool_part, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == marsh_stakato_drone) {
                int i0 = Rnd.get(100);
                int i1 = Rnd.get(100);
                if (st.ownItemCount(q_giant_tool) >= 1) {
                    if (i1 < 100) {
                        st.addSpawn(pagan_of_cruma, st.getPlayer().getX() + 20, st.getPlayer().getY() + 20, st.getPlayer().getZ(), 0, 100, 900000);
                        st.takeItems(q_giant_tool, 1);
                    } else
                        st.takeItems(q_giant_tool, 1);
                } else if (i0 < 96) {
                    st.giveItems(q_giant_tool_part, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == pagan_of_cruma) {
                st.giveItems(q_giant_tech_doc, 1);
                if (st.ownItemCount(q_giant_tech_doc) >= 3) {
                    st.setCond(4);
                    st.soundEffect(SOUND_MIDDLE);
                } else
                    st.soundEffect(SOUND_ITEMGET);
            }
        return null;
    }
}