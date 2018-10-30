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
public class _139_ShadowFoxPart1 extends Quest {
    // NPC
    private final static int warehouse_keeper_mia = 30896;

    // Items
    private final static int q_part_of_docbox_key = 10345;
    private final static int q_docbox = 10346;

    // Monsters
    private final static int tasaba_lizardman = 20784;
    private final static int tasaba_lizardman_shaman = 20785;
    private final static int tasaba_lizardman_a = 21639;
    private final static int tasaba_lizardman_sham_a = 21640;

    public _139_ShadowFoxPart1() {
        super(false);
        addStartNpc(warehouse_keeper_mia);
        addTalkId(warehouse_keeper_mia);
        addQuestItem(q_part_of_docbox_key, q_docbox);
        addKillId(tasaba_lizardman, tasaba_lizardman_shaman, tasaba_lizardman_a, tasaba_lizardman_sham_a);
        addLevelCheck(37);
        addQuestCompletedCheck(138);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("the_suspicious_one_1");

        if (event.equalsIgnoreCase("quest_accept")) {
            st.setCond(1);
            st.setMemoState("the_suspicious_one_1", String.valueOf(1), true);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            htmltext = "warehouse_keeper_mia_q0139_05.htm";
        } else if (event.equalsIgnoreCase("reply_7") && GetMemoState == 1) {
            st.setMemoState("the_suspicious_one_1", String.valueOf(2), true);
            htmltext = "warehouse_keeper_mia_q0139_12.htm";
        } else if (event.equalsIgnoreCase("reply_9") && GetMemoState == 2) {
            st.setCond(2);
            st.setMemoState("the_suspicious_one_1", String.valueOf(3), true);
            st.soundEffect(SOUND_MIDDLE);
            htmltext = "warehouse_keeper_mia_q0139_15.htm";
        } else if (event.equalsIgnoreCase("reply_10") && GetMemoState == 3) {
            int i0 = Rnd.get(20);
            if (i0 == 0) {
                st.takeItems(q_part_of_docbox_key, 10);
                st.takeItems(q_docbox, 1);
                htmltext = "warehouse_keeper_mia_q0139_18.htm";
            } else if (i0 == 1) {
                st.takeItems(q_part_of_docbox_key, 10);
                st.takeItems(q_docbox, 1);
                htmltext = "warehouse_keeper_mia_q0139_19.htm";
            } else if (i0 >= 2) {
                st.setMemoState("the_suspicious_one_1", String.valueOf(4), true);
                st.takeItems(q_part_of_docbox_key, -1);
                st.takeItems(q_docbox, -1);
                htmltext = "warehouse_keeper_mia_q0139_20.htm";
            }
        } else if (event.equalsIgnoreCase("reply_12") && GetMemoState == 4) {
            st.giveItems(ADENA_ID, 14050);
            if (st.getPlayer().getLevel() < 43)
                st.addExpAndSp(30000, 2000);
            st.soundEffect(SOUND_FINISH);
            st.exitQuest(false);
            st.removeMemo("the_suspicious_one_1");
            htmltext = "warehouse_keeper_mia_q0139_23.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int GetMemoState = st.getInt("the_suspicious_one_1");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == warehouse_keeper_mia && GetMemoState == 0) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "warehouse_keeper_mia_q0139_04.htm";
                            st.exitQuest(true);
                            break;
                        case QUEST:
                            htmltext = "warehouse_keeper_mia_q0139_03.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "warehouse_keeper_mia_q0139_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == warehouse_keeper_mia)
                    if (GetMemoState == 1)
                        htmltext = "warehouse_keeper_mia_q0139_06.htm";
                    else if (GetMemoState == 2)
                        htmltext = "warehouse_keeper_mia_q0139_13.htm";
                    else if (GetMemoState == 3)
                        if (st.ownItemCount(q_part_of_docbox_key) < 10 || st.ownItemCount(q_docbox) < 1)
                            htmltext = "warehouse_keeper_mia_q0139_16.htm";
                        else
                            htmltext = "warehouse_keeper_mia_q0139_17.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("the_suspicious_one_1");
        int npcId = npc.getNpcId();

        if (GetMemoState == 3) {
            if (npcId == tasaba_lizardman || npcId == tasaba_lizardman_sham_a) {
                int i0 = Rnd.get(100);
                int i1 = Rnd.get(11);
                if (i0 < 68) {
                    if (i1 < 10)
                        st.giveItems(q_part_of_docbox_key, 1);
                    else
                        st.giveItems(q_docbox, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == tasaba_lizardman_shaman) {
                int i0 = Rnd.get(100);
                int i1 = Rnd.get(11);
                if (i0 < 65) {
                    if (i1 < 10)
                        st.giveItems(q_part_of_docbox_key, 1);
                    else
                        st.giveItems(q_docbox, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == tasaba_lizardman_a) {
                int i0 = Rnd.get(100);
                int i1 = Rnd.get(11);
                if (i0 < 100) {
                    if (i1 < 10)
                        st.giveItems(q_part_of_docbox_key, 1);
                    else
                        st.giveItems(q_docbox, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        }
        return null;
    }
}