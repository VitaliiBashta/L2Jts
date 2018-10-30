package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.0
 * @date 29/06/2015
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _367_ElectrifyingRecharge extends Quest {
    // npc
    private static final int researcher_lorain = 30673;
    // mobs
    private static final int catherok = 21035;
    // questitem
    private static final int q_giants_lamp1 = 5875;
    private static final int q_giants_lamp2 = 5876;
    private static final int q_giants_lamp3 = 5877;
    private static final int q_giants_lamp4 = 5878;
    private static final int q_giants_lamp5 = 5879;
    private static final int q_giants_broken_lamp = 5880;
    // etcitem
    private static final int dye_s1c1_c = 4553;
    private static final int dye_s1d1_c = 4554;
    private static final int dye_c1s1_c = 4555;
    private static final int dye_c1c1_c = 4556;
    private static final int dye_d1s1_c = 4557;
    private static final int dye_d1c1_c = 4558;
    private static final int dye_i1m1_c = 4559;
    private static final int dye_i1w1_c = 4560;
    private static final int dye_m1i1_c = 4561;
    private static final int dye_m1w1_c = 4562;
    private static final int dye_w1i1_c = 4563;
    private static final int dye_w1m1_c = 4564;
    private static final int dye_s1c3_d = 4445;

    public _367_ElectrifyingRecharge() {
        super(true);
        addStartNpc(researcher_lorain);
        addAttackId(catherok);
        addQuestItem(q_giants_lamp1, q_giants_lamp2, q_giants_lamp3, q_giants_lamp4, q_giants_lamp5, q_giants_broken_lamp);
        addLevelCheck(37, 47);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == researcher_lorain) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("Buzz_Buzz_Charging_adventure", String.valueOf(1), true);
                st.giveItems(q_giants_lamp1, 1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "researcher_lorain_q0367_03.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=367&reply=1"))
                htmltext = "researcher_lorain_q0367_07.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=367&reply=2")) {
                st.takeItems(q_giants_lamp1, -1);
                st.takeItems(q_giants_lamp2, -1);
                st.takeItems(q_giants_lamp3, -1);
                st.takeItems(q_giants_lamp4, -1);
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                htmltext = "researcher_lorain_q0367_08.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=367&reply=3"))
                htmltext = "researcher_lorain_q0367_09.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == researcher_lorain) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "researcher_lorain_q0367_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "researcher_lorain_q0367_01.htm";
                            return htmltext;
                    }
                }
                break;
            case STARTED:
                if (npcId == researcher_lorain) {
                    if (st.ownItemCount(q_giants_lamp5) == 0 && st.ownItemCount(q_giants_broken_lamp) == 0)
                        htmltext = "researcher_lorain_q0367_04.htm";
                    else if (st.ownItemCount(q_giants_broken_lamp) > 0) {
                        st.giveItems(q_giants_lamp1, 1);
                        st.takeItems(q_giants_broken_lamp, -1);
                        htmltext = "researcher_lorain_q0367_05.htm";
                    } else if (st.ownItemCount(q_giants_lamp5) > 0) {
                        int i0 = Rnd.get(14);
                        if (i0 == 0)
                            st.giveItems(dye_s1c1_c, 1);
                        else if (i0 == 1)
                            st.giveItems(dye_s1d1_c, 1);
                        else if (i0 == 2)
                            st.giveItems(dye_c1s1_c, 1);
                        else if (i0 == 3)
                            st.giveItems(dye_c1c1_c, 1);
                        else if (i0 == 4)
                            st.giveItems(dye_d1s1_c, 1);
                        else if (i0 == 5)
                            st.giveItems(dye_d1c1_c, 1);
                        else if (i0 == 6)
                            st.giveItems(dye_i1m1_c, 1);
                        else if (i0 == 7)
                            st.giveItems(dye_i1w1_c, 1);
                        else if (i0 == 8)
                            st.giveItems(dye_m1i1_c, 1);
                        else if (i0 == 9)
                            st.giveItems(dye_m1w1_c, 1);
                        else if (i0 == 10)
                            st.giveItems(dye_w1i1_c, 1);
                        else if (i0 == 11)
                            st.giveItems(dye_w1m1_c, 1);
                        else
                            st.giveItems(dye_s1c3_d, 1);
                        st.takeItems(q_giants_lamp5, -1);
                        st.giveItems(q_giants_lamp1, 1);
                        htmltext = "researcher_lorain_q0367_06.htm";
                    }
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onAttack(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int GetMemoState = st.getInt("Buzz_Buzz_Charging_adventure");
        if (GetMemoState == 1) {
            if (npcId == catherok) {
                if (st.ownItemCount(q_giants_lamp5) == 0) {
                    int i0 = Rnd.get(37);
                    if (i0 == 0) {
                        if (st.ownItemCount(q_giants_lamp1) > 0) {
                            st.giveItems(q_giants_lamp2, 1);
                            st.takeItems(q_giants_lamp1, -1);
                            st.soundEffect(SOUND_MIDDLE);
                        } else if (st.ownItemCount(q_giants_lamp2) > 0) {
                            st.giveItems(q_giants_lamp3, 1);
                            st.takeItems(q_giants_lamp2, -1);
                            st.soundEffect(SOUND_MIDDLE);
                        } else if (st.ownItemCount(q_giants_lamp3) > 0) {
                            st.giveItems(q_giants_lamp4, 1);
                            st.takeItems(q_giants_lamp3, -1);
                            st.soundEffect(SOUND_MIDDLE);
                        } else if (st.ownItemCount(q_giants_lamp4) > 0) {
                            st.giveItems(q_giants_lamp5, 1);
                            st.takeItems(q_giants_lamp4, -1);
                            st.setCond(2);
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        npc.doCast(SkillTable.getInstance().getSkillEntry(4072, 4), st.getPlayer(), true);
                    } else if (i0 == 1 && st.ownItemCount(q_giants_broken_lamp) == 0) {
                        st.giveItems(q_giants_broken_lamp, 1);
                        st.takeItems(q_giants_lamp1, -1);
                        st.takeItems(q_giants_lamp2, -1);
                        st.takeItems(q_giants_lamp3, -1);
                        st.takeItems(q_giants_lamp4, -1);
                        st.soundEffect(SOUND_MIDDLE);
                    }
                }
            }
        }
        return null;
    }
}