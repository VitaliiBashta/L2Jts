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
public class _140_ShadowFoxPart2 extends Quest {
    // NPCs
    private final static int warehouse_keeper_kluck = 30895;
    private final static int magister_xenovia = 30912;

    // Items
    private final static int q_dark_crystal = 10347;
    private final static int q_dark_compaound = 10348;
    private final static int q_secdoc_godness_sword = 10349;

    // Monsters
    private final static int crokian = 20789;
    private final static int dailaon = 20790;
    private final static int crokian_warrior = 20791;
    private final static int farhite = 20792;

    public _140_ShadowFoxPart2() {
        super(false);
        addStartNpc(warehouse_keeper_kluck);
        addTalkId(warehouse_keeper_kluck, magister_xenovia);
        addQuestItem(q_dark_crystal, q_dark_compaound, q_secdoc_godness_sword);
        addKillId(crokian, dailaon, crokian_warrior, farhite);
        addLevelCheck(37);
        addQuestCompletedCheck(139);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("the_suspicious_one_2");

        if (event.equalsIgnoreCase("quest_accept")) {
            st.setCond(1);
            st.setMemoState("the_suspicious_one_2", String.valueOf(1), true);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            htmltext = "warehouse_keeper_kluck_q0140_04.htm";
        } else if (event.equalsIgnoreCase("reply_4") && GetMemoState == 1) {
            st.setCond(2);
            st.setMemoState("the_suspicious_one_2", String.valueOf(2), true);
            st.soundEffect(SOUND_MIDDLE);
            htmltext = "warehouse_keeper_kluck_q0140_09.htm";
        } else if (event.equalsIgnoreCase("reply_6") && GetMemoState == 6) {
            st.giveItems(ADENA_ID, 18775);
            if (st.getPlayer().getLevel() < 43)
                st.addExpAndSp(30000, 2000);
            st.soundEffect(SOUND_FINISH);
            st.removeMemo("the_suspicious_one_2");
            st.exitQuest(false);
            htmltext = "warehouse_keeper_kluck_q0140_14.htm";
        } else if (event.equalsIgnoreCase("reply_4") && GetMemoState == 2) {
            st.setMemoState("the_suspicious_one_2", String.valueOf(3), true);
            htmltext = "magister_xenovia_q0140_06.htm";
        } else if (event.equalsIgnoreCase("reply_6") && GetMemoState == 3) {
            st.setCond(3);
            st.setMemoState("the_suspicious_one_2", String.valueOf(4), true);
            st.soundEffect(SOUND_MIDDLE);
            htmltext = "magister_xenovia_q0140_09.htm";
        } else if (event.equalsIgnoreCase("reply_7") && GetMemoState == 4) {
            int i0 = Rnd.get(10);
            if (i0 <= 8) {
                if (st.ownItemCount(q_dark_compaound) < 2) {
                    st.giveItems(q_dark_compaound, 1);
                    st.takeItems(q_dark_crystal, 5);
                    htmltext = "magister_xenovia_q0140_12.htm";
                } else {
                    st.giveItems(q_secdoc_godness_sword, 1);
                    st.takeItems(q_dark_crystal, -1);
                    st.takeItems(q_dark_compaound, -1);
                    st.setCond(4);
                    st.setMemoState("the_suspicious_one_2", String.valueOf(5), true);
                    htmltext = "magister_xenovia_q0140_13.htm";
                    st.soundEffect(SOUND_MIDDLE);
                }
            } else {
                st.takeItems(q_dark_crystal, 5);
                htmltext = "magister_xenovia_q0140_14.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int GetMemoState = st.getInt("the_suspicious_one_2");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == warehouse_keeper_kluck) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "warehouse_keeper_kluck_q0140_03.htm";
                            st.exitQuest(true);
                            break;
                        case QUEST:
                            htmltext = "warehouse_keeper_kluck_q0140_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "warehouse_keeper_kluck_q0140_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == warehouse_keeper_kluck) {
                    if (GetMemoState == 1)
                        htmltext = "warehouse_keeper_kluck_q0140_05.htm";
                    else if (GetMemoState >= 2 && GetMemoState <= 4)
                        htmltext = "warehouse_keeper_kluck_q0140_10.htm";
                    else if (GetMemoState == 5) {
                        st.setMemoState("the_suspicious_one_2", String.valueOf(6), true);
                        st.takeItems(q_secdoc_godness_sword, -1);
                        htmltext = "warehouse_keeper_kluck_q0140_11.htm";
                    }
                } else if (npcId == magister_xenovia)
                    if (GetMemoState < 2)
                        htmltext = "magister_xenovia_q0140_01.htm";
                    else if (GetMemoState == 2)
                        htmltext = "magister_xenovia_q0140_02.htm";
                    else if (GetMemoState == 3)
                        htmltext = "magister_xenovia_q0140_07.htm";
                    else if (GetMemoState == 4 && st.ownItemCount(q_dark_compaound) < 3) {
                        if (st.ownItemCount(q_dark_crystal) < 5)
                            htmltext = "magister_xenovia_q0140_10.htm";
                        else if (st.ownItemCount(q_dark_crystal) >= 5)
                            htmltext = "magister_xenovia_q0140_11.htm";
                    } else if (GetMemoState >= 5)
                        htmltext = "magister_xenovia_q0140_15.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("the_suspicious_one_2");
        int npcId = npc.getNpcId();

        if (GetMemoState == 4)
            if (npcId == crokian) {
                int i0 = Rnd.get(100);
                if (i0 < 45) {
                    st.giveItems(q_dark_crystal, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == dailaon) {
                int i0 = Rnd.get(100);
                if (i0 < 58) {
                    st.giveItems(q_dark_crystal, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == crokian_warrior) {
                int i0 = Rnd.get(100);
                if (i0 < 100) {
                    st.giveItems(q_dark_crystal, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == farhite) {
                int i0 = Rnd.get(100);
                if (i0 < 92) {
                    st.giveItems(q_dark_crystal, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        return null;
    }
}