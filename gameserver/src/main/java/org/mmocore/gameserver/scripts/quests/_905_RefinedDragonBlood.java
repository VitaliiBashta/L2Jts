package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official High Five
 *
 * @author Magister
 * @version 1.0
 * @date 26/09/2015
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _905_RefinedDragonBlood extends Quest {
    // npc
    private static final int separated_soul_01 = 32864;
    private static final int separated_soul_02 = 32865;
    private static final int separated_soul_03 = 32866;
    private static final int separated_soul_04 = 32867;
    private static final int separated_soul_05 = 32868;
    private static final int separated_soul_06 = 32869;
    private static final int separated_soul_07 = 32870;
    private static final int separated_soul_08 = 32891;
    // mobs
    private static final int dragon_knight_3 = 22844;
    private static final int dragon_knight_5 = 22845;
    private static final int dragon_knight_9 = 22846;
    private static final int dragon_warrior = 22847;
    private static final int drake_leader = 22848;
    private static final int drake_warrior = 22849;
    private static final int drake_scout = 22850;
    private static final int drake_mage = 22851;
    private static final int dragon_guard = 22852;
    private static final int dragon_mage = 22853;
    // questitem
    private static final int g_red_blood_of_dragon1 = 21913;
    private static final int g_blue_blood_of_dragon1 = 21914;
    // etcitem
    private static final int g_refined_red_blood_of_dragon1 = 21903;
    private static final int g_refined_blue_blood_of_dragon1 = 21904;

    public _905_RefinedDragonBlood() {
        super(PARTY_ALL);
        addStartNpc(separated_soul_01, separated_soul_02, separated_soul_03, separated_soul_04, separated_soul_05, separated_soul_06, separated_soul_07, separated_soul_08);
        addKillId(dragon_knight_3, dragon_knight_5, dragon_knight_9, dragon_warrior, drake_leader, drake_warrior, drake_scout, drake_mage, dragon_guard, dragon_mage);
        addQuestItem(g_red_blood_of_dragon1, g_blue_blood_of_dragon1);
        addLevelCheck(83);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        int GetMemoState = st.getInt("refined_dragon_blood");
        if (npcId == separated_soul_01 || npcId == separated_soul_02 || npcId == separated_soul_03 || npcId == separated_soul_04 || npcId == separated_soul_05 || npcId == separated_soul_06 || npcId == separated_soul_07 || npcId == separated_soul_08) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("refined_dragon_blood", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "separated_soul_01_q0905_05.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=905&reply=1"))
                htmltext = "separated_soul_01_q0905_04.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=905&reply=2")) {
                if (GetMemoState == 2) {
                    st.giveItems(g_refined_red_blood_of_dragon1, 1);
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(this);
                    htmltext = "separated_soul_01_q0905_12.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=905&reply=3")) {
                if (GetMemoState == 2) {
                    st.giveItems(g_refined_blue_blood_of_dragon1, 1);
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(this);
                    htmltext = "separated_soul_01_q0905_12.htm";
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("refined_dragon_blood");
        int npcId = npc.getNpcId();
        int id = st.getState();
        boolean GetDailyQuestFlag = st.isNowAvailable();
        switch (id) {
            case CREATED:
                if (npcId == separated_soul_01 || npcId == separated_soul_02 || npcId == separated_soul_03 || npcId == separated_soul_04 || npcId == separated_soul_05 || npcId == separated_soul_06 || npcId == separated_soul_07 || npcId == separated_soul_08) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "separated_soul_01_q0905_03.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            if (GetDailyQuestFlag)
                                htmltext = "separated_soul_01_q0905_01.htm";
                            else
                                htmltext = "separated_soul_01_q0905_02.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == separated_soul_01 || npcId == separated_soul_02 || npcId == separated_soul_03 || npcId == separated_soul_04 || npcId == separated_soul_05 || npcId == separated_soul_06 || npcId == separated_soul_07 || npcId == separated_soul_08) {
                    if (GetMemoState == 1 && (st.ownItemCount(g_red_blood_of_dragon1) < 10 || st.ownItemCount(g_blue_blood_of_dragon1) < 10))
                        htmltext = "separated_soul_01_q0905_06.htm";
                    else if (GetMemoState == 1 && st.ownItemCount(g_red_blood_of_dragon1) >= 10 && st.ownItemCount(g_blue_blood_of_dragon1) >= 10) {
                        st.takeItems(g_red_blood_of_dragon1, -1);
                        st.takeItems(g_blue_blood_of_dragon1, -1);
                        st.soundEffect(SOUND_FINISH);
                        st.setMemoState("refined_dragon_blood", String.valueOf(2), true);
                        htmltext = "separated_soul_01_q0905_07.htm";
                    } else if (GetMemoState == 2)
                        htmltext = "separated_soul_01_q0905_08.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("refined_dragon_blood");
        int npcId = npc.getNpcId();
        if (npcId == dragon_knight_3 || npcId == dragon_knight_5 || npcId == dragon_knight_9 || npcId == dragon_warrior || npcId == dragon_guard || npcId == dragon_mage) {
            if (GetMemoState == 1) {
                int i0 = Rnd.get(100);
                if (i0 < 30) {
                    if (st.ownItemCount(g_red_blood_of_dragon1) >= 10 && st.ownItemCount(g_blue_blood_of_dragon1) >= 9) {
                        st.setCond(2);
                        st.giveItems(g_blue_blood_of_dragon1, 1);
                        st.soundEffect(SOUND_MIDDLE);
                    } else if (st.ownItemCount(g_blue_blood_of_dragon1) < 10) {
                        st.giveItems(g_blue_blood_of_dragon1, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        } else if (npcId == drake_leader || npcId == drake_warrior || npcId == drake_scout || npcId == drake_mage) {
            if (GetMemoState == 1) {
                int i0 = Rnd.get(100);
                if (i0 < 30) {
                    if (st.ownItemCount(g_red_blood_of_dragon1) >= 9 && st.ownItemCount(g_blue_blood_of_dragon1) >= 10) {
                        st.setCond(2);
                        st.giveItems(g_red_blood_of_dragon1, 1);
                        st.soundEffect(SOUND_MIDDLE);
                    } else if (st.ownItemCount(g_red_blood_of_dragon1) < 10) {
                        st.giveItems(g_red_blood_of_dragon1, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        }
        return null;
    }
}