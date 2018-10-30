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
public class _137_TempleChampionPart1 extends Quest {
    // npc
    private static final int sylvain = 30070;

    // questitem
    private static final int q_angel_report = 10340;
    private static final int q_bedge_hand_of_order = 10334;
    private static final int q_bedge_foot_of_order = 10339;

    // mobs
    private final static int granitic_golem = 20083;
    private final static int hanged_man_ripper = 20144;
    private final static int amber_basilisk = 20199;
    private final static int strain = 20200;
    private final static int ghoul = 20201;
    private final static int dead_seeker = 20202;

    public _137_TempleChampionPart1() {
        super(false);
        addStartNpc(sylvain);
        addKillId(granitic_golem, hanged_man_ripper, amber_basilisk, strain, ghoul, dead_seeker);
        addQuestItem(q_angel_report);
        addLevelCheck(35);
        addQuestCompletedCheck(134);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("fallen_angel_1");
        int npcId = npc.getNpcId();

        if (npcId == sylvain)
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("fallen_angel_1", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "sylvain_q0137_04.htm";
            } else if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 1)
                    htmltext = "sylvain_q0137_05.htm";
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState >= 1)
                    htmltext = "sylvain_q0137_07.htm";
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 1) {
                    st.setMemoState("fallen_angel_1", String.valueOf(2), true);
                    htmltext = "sylvain_q0137_08.htm";
                }
            } else if (event.equalsIgnoreCase("reply_5")) {
                if (GetMemoState == 2) {
                    st.setMemoState("fallen_angel_1", String.valueOf(3), true);
                    htmltext = "sylvain_q0137_10.htm";
                }
            } else if (event.equalsIgnoreCase("reply_6")) {
                if (GetMemoState == 3)
                    htmltext = "sylvain_q0137_12.htm";
            } else if (event.equalsIgnoreCase("reply_7")) {
                if (GetMemoState == 3) {
                    st.setCond(2);
                    st.setMemoState("fallen_angel_1", String.valueOf(5), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "sylvain_q0137_13.htm";
                }
            } else if (event.equalsIgnoreCase("reply_8")) {
                if (GetMemoState == 6)
                    htmltext = "sylvain_q0137_16.htm";
            } else if (event.equalsIgnoreCase("reply_9")) {
                if (GetMemoState == 6)
                    htmltext = "sylvain_q0137_18.htm";
            } else if (event.equalsIgnoreCase("reply_10")) {
                if (GetMemoState == 6) {
                    st.setMemoState("fallen_angel_1", String.valueOf(7), true);
                    htmltext = "sylvain_q0137_19.htm";
                }
            } else if (event.equalsIgnoreCase("reply_11")) {
                if (GetMemoState == 7)
                    htmltext = "sylvain_q0137_21.htm";
            } else if (event.equalsIgnoreCase("reply_12")) {
                if (GetMemoState == 7)
                    htmltext = "sylvain_q0137_22.htm";
            } else if (event.equalsIgnoreCase("reply_13")) {
                if (GetMemoState == 7)
                    htmltext = "sylvain_q0137_23.htm";
            } else if (event.equalsIgnoreCase("reply_14"))
                if (GetMemoState == 7) {
                    st.takeItems(q_bedge_hand_of_order, -1);
                    st.takeItems(q_bedge_foot_of_order, -1);
                    st.giveItems(ADENA_ID, 69146);
                    if (st.getPlayer().getLevel() < 41)
                        st.addExpAndSp(219975, 13047);
                    st.removeMemo("fallen_angel_1");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "sylvain_q0137_24.htm";
                }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("fallen_angel_1");
        int npcId = npc.getNpcId();
        int id = st.getState();
        QuestState qs_135 = st.getPlayer().getQuestState(135);

        switch (id) {
            case CREATED:
                if (npcId == sylvain) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "sylvain_q0137_02.htm";
                            st.exitQuest(true);
                            break;
                        case QUEST:
                            htmltext = "sylvain_q0137_03.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            if (qs_135 != null && qs_135.isCompleted())
                                htmltext = "sylvain_q0137_01.htm";
                            else {
                                htmltext = "sylvain_q0137_03.htm";
                                st.exitQuest(true);
                            }
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == sylvain)
                    if (GetMemoState == 1)
                        htmltext = "sylvain_q0137_06.htm";
                    else if (GetMemoState == 2)
                        htmltext = "sylvain_q0137_09.htm";
                    else if (GetMemoState == 3)
                        htmltext = "sylvain_q0137_11.htm";
                    else if (GetMemoState == 5) {
                        if (st.ownItemCount(q_angel_report) >= 30) {
                            st.takeItems(q_angel_report, -1);
                            st.setMemoState("fallen_angel_1", String.valueOf(6), true);
                            htmltext = "sylvain_q0137_15.htm";
                        } else
                            htmltext = "sylvain_q0137_14.htm";
                    } else if (GetMemoState == 6)
                        htmltext = "sylvain_q0137_17.htm";
                    else if (GetMemoState == 7)
                        htmltext = "sylvain_q0137_20.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("fallen_angel_1");
        int npcId = npc.getNpcId();

        if (GetMemoState == 5 && st.ownItemCount(q_angel_report) < 30)
            if (npcId == granitic_golem || npcId == hanged_man_ripper) {
                int i0 = Rnd.get(100);
                if (i0 < 100)
                    if (st.ownItemCount(q_angel_report) >= 29) {
                        st.setCond(3);
                        st.giveItems(q_angel_report, 1);
                        st.soundEffect(SOUND_MIDDLE);
                    } else {
                        st.giveItems(q_angel_report, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            } else if (npcId == amber_basilisk) {
                int i0 = Rnd.get(100);
                if (i0 < 89)
                    if (st.ownItemCount(q_angel_report) >= 29) {
                        st.setCond(3);
                        st.giveItems(q_angel_report, 1);
                        st.soundEffect(SOUND_MIDDLE);
                    } else {
                        st.giveItems(q_angel_report, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            } else if (npcId == strain) {
                int i0 = Rnd.get(100);
                if (i0 < 78)
                    if (st.ownItemCount(q_angel_report) >= 29) {
                        st.setCond(3);
                        st.giveItems(q_angel_report, 1);
                        st.soundEffect(SOUND_MIDDLE);
                    } else {
                        st.giveItems(q_angel_report, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            } else if (npcId == ghoul || npcId == dead_seeker) {
                int i0 = Rnd.get(100);
                if (i0 < 92)
                    if (st.ownItemCount(q_angel_report) >= 29) {
                        st.setCond(3);
                        st.giveItems(q_angel_report, 1);
                        st.soundEffect(SOUND_MIDDLE);
                    } else {
                        st.giveItems(q_angel_report, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            }
        return null;
    }
}