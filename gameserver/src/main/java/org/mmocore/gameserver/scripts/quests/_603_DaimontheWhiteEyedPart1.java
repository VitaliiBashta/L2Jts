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
public class _603_DaimontheWhiteEyedPart1 extends Quest {
    // npc
    private static final int eye_of_argos = 31683;
    private static final int ancient_lithography1 = 31548;
    private static final int ancient_lithography2 = 31549;
    private static final int ancient_lithography3 = 31550;
    private static final int ancient_lithography4 = 31551;
    private static final int ancient_lithography5 = 31552;

    // mobs
    private static final int buffalo_slave = 21299;
    private static final int bandersnatch_slave = 21297;
    private static final int grendel_slave = 21304;

    // questitem
    private static final int q_dark_evil_spirit = 7190;
    private static final int q_broken_crystal = 7191;
    private static final int q_unfinished_s_crystal = 7192;

    public _603_DaimontheWhiteEyedPart1() {
        super(true);
        addStartNpc(eye_of_argos);
        addTalkId(ancient_lithography1, ancient_lithography2, ancient_lithography3, ancient_lithography4, ancient_lithography5);
        addKillId(buffalo_slave, bandersnatch_slave, grendel_slave);
        addQuestItem(q_dark_evil_spirit);
        addLevelCheck(73, 78);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();

        if (npcId == eye_of_argos) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("daemon_of_hundred_eyes_first", String.valueOf(1 * 10 + 1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "eye_of_argos_q0603_0104.htm";
            } else if (event.equalsIgnoreCase("reply_1")) {
                if (st.ownItemCount(q_broken_crystal) >= 5) {
                    st.setCond(7);
                    st.setMemoState("daemon_of_hundred_eyes_first", String.valueOf(7 * 10 + 1), true);
                    st.takeItems(q_broken_crystal, 5);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "eye_of_argos_q0603_0701.htm";
                } else
                    htmltext = "eye_of_argos_q0603_0702.htm";
            } else if (event.equalsIgnoreCase("reply_3"))
                if (st.ownItemCount(q_dark_evil_spirit) >= 200) {
                    st.takeItems(q_dark_evil_spirit, -1);
                    st.giveItems(q_unfinished_s_crystal, 1);
                    st.removeMemo("daemon_of_hundred_eyes_first");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(true);
                    htmltext = "eye_of_argos_q0603_0801.htm";
                } else
                    htmltext = "eye_of_argos_q0603_0802.htm";
        } else if (npcId == ancient_lithography1) {
            if (event.equalsIgnoreCase("reply_1")) {
                st.setCond(2);
                st.setMemoState("daemon_of_hundred_eyes_first", String.valueOf(2 * 10 + 1), true);
                st.giveItems(q_broken_crystal, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "ancient_lithography1_q0603_0201.htm";
            }
        } else if (npcId == ancient_lithography2) {
            if (event.equalsIgnoreCase("reply_1")) {
                st.setCond(3);
                st.setMemoState("daemon_of_hundred_eyes_first", String.valueOf(3 * 10 + 1), true);
                st.giveItems(q_broken_crystal, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "ancient_lithography2_q0603_0301.htm";
            }
        } else if (npcId == ancient_lithography3) {
            if (event.equalsIgnoreCase("reply_1")) {
                st.setCond(4);
                st.setMemoState("daemon_of_hundred_eyes_first", String.valueOf(4 * 10 + 1), true);
                st.giveItems(q_broken_crystal, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "ancient_lithography3_q0603_0401.htm";
            }
        } else if (npcId == ancient_lithography4) {
            if (event.equalsIgnoreCase("reply_1")) {
                st.setCond(5);
                st.setMemoState("daemon_of_hundred_eyes_first", String.valueOf(5 * 10 + 1), true);
                st.giveItems(q_broken_crystal, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "ancient_lithography4_q0603_0501.htm";
            }
        } else if (npcId == ancient_lithography5)
            if (event.equalsIgnoreCase("reply_1")) {
                st.setCond(6);
                st.setMemoState("daemon_of_hundred_eyes_first", String.valueOf(6 * 10 + 1), true);
                st.giveItems(q_broken_crystal, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "ancient_lithography5_q0603_0601.htm";
            }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("daemon_of_hundred_eyes_first");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == eye_of_argos) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "eye_of_argos_q0603_0103.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "eye_of_argos_q0603_0101.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == eye_of_argos) {
                    if (GetMemoState == 1 * 10 + 1)
                        htmltext = "eye_of_argos_q0603_0105.htm";
                    else if (st.ownItemCount(q_broken_crystal) >= 1 && GetMemoState == 6 * 10 + 1)
                        htmltext = "eye_of_argos_q0603_0601.htm";
                    else if (GetMemoState <= 7 * 10 + 2 && GetMemoState >= 7 * 10 + 1)
                        if (GetMemoState == 7 * 10 + 2 && st.ownItemCount(q_dark_evil_spirit) >= 200)
                            htmltext = "eye_of_argos_q0603_0703.htm";
                        else
                            htmltext = "eye_of_argos_q0603_0704.htm";
                } else if (npcId == ancient_lithography1) {
                    if (GetMemoState == 1 * 10 + 1)
                        htmltext = "ancient_lithography1_q0603_0101.htm";
                    else if (GetMemoState == 2 * 10 + 1)
                        htmltext = "ancient_lithography1_q0603_0203.htm";
                } else if (npcId == ancient_lithography2) {
                    if (st.ownItemCount(q_broken_crystal) >= 1 && GetMemoState == 2 * 10 + 1)
                        htmltext = "ancient_lithography2_q0603_0201.htm";
                    else if (GetMemoState == 3 * 10 + 1)
                        htmltext = "ancient_lithography2_q0603_0303.htm";
                } else if (npcId == ancient_lithography3) {
                    if (st.ownItemCount(q_broken_crystal) >= 1 && GetMemoState == 3 * 10 + 1)
                        htmltext = "ancient_lithography3_q0603_0301.htm";
                    else if (GetMemoState == 4 * 10 + 1)
                        htmltext = "ancient_lithography3_q0603_0403.htm";
                } else if (npcId == ancient_lithography4) {
                    if (st.ownItemCount(q_broken_crystal) >= 1 && GetMemoState == 4 * 10 + 1)
                        htmltext = "ancient_lithography4_q0603_0401.htm";
                    else if (GetMemoState == 5 * 10 + 1)
                        htmltext = "ancient_lithography4_q0603_0503.htm";
                } else if (npcId == ancient_lithography5)
                    if (st.ownItemCount(q_broken_crystal) >= 1 && GetMemoState == 5 * 10 + 1)
                        htmltext = "ancient_lithography5_q0603_0501.htm";
                    else if (GetMemoState == 6 * 10 + 1)
                        htmltext = "ancient_lithography5_q0603_0603.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("daemon_of_hundred_eyes_first");
        int npcId = npc.getNpcId();

        if (GetMemoState == 7 * 10 + 1)
            if (npcId == buffalo_slave) {
                int i4 = Rnd.get(1000);
                if (i4 < 519)
                    if (st.ownItemCount(q_dark_evil_spirit) + 1 >= 200) {
                        if (st.ownItemCount(q_dark_evil_spirit) < 200) {
                            st.setCond(8);
                            st.setMemoState("daemon_of_hundred_eyes_first", String.valueOf(7 * 10 + 2), true);
                            st.giveItems(q_dark_evil_spirit, 200 - st.ownItemCount(q_dark_evil_spirit));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    } else {
                        st.giveItems(q_dark_evil_spirit, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            } else if (npcId == bandersnatch_slave) {
                int i4 = Rnd.get(1000);
                if (i4 < 500)
                    if (st.ownItemCount(q_dark_evil_spirit) + 1 >= 200) {
                        if (st.ownItemCount(q_dark_evil_spirit) < 200) {
                            st.setCond(8);
                            st.setMemoState("daemon_of_hundred_eyes_first", String.valueOf(7 * 10 + 2), true);
                            st.giveItems(q_dark_evil_spirit, 200 - st.ownItemCount(q_dark_evil_spirit));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    } else {
                        st.giveItems(q_dark_evil_spirit, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            } else if (npcId == grendel_slave) {
                int i4 = Rnd.get(1000);
                if (i4 < 673)
                    if (st.ownItemCount(q_dark_evil_spirit) + 1 >= 200) {
                        if (st.ownItemCount(q_dark_evil_spirit) < 200) {
                            st.setCond(8);
                            st.setMemoState("daemon_of_hundred_eyes_first", String.valueOf(7 * 10 + 2), true);
                            st.giveItems(q_dark_evil_spirit, 200 - st.ownItemCount(q_dark_evil_spirit));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    } else {
                        st.giveItems(q_dark_evil_spirit, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            }
        return null;
    }
}