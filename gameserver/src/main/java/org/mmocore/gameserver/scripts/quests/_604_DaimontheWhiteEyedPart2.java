package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _604_DaimontheWhiteEyedPart2 extends Quest {
    // npc
    private final static int eye_of_argos = 31683;
    private final static int daimons_altar = 31541;

    // mobs
    private final static int daemon_of_hundred_eyes = 25290;

    // questitem
    private final static int q_unfinished_s_crystal = 7192;
    private final static int q_summon_crystal = 7193;
    private final static int q_essense_of_daimon = 7194;

    // etcitem
    private final static int dye_i2m2_c = 4595;
    private final static int dye_i2w2_c = 4596;
    private final static int dye_m2i2_c = 4597;
    private final static int dye_m2w2_c = 4598;
    private final static int dye_w2i2_c = 4599;
    private final static int dye_w2m2_c = 4600;

    public _604_DaimontheWhiteEyedPart2() {
        super(true);
        addStartNpc(eye_of_argos);
        addTalkId(daimons_altar);
        addKillId(daemon_of_hundred_eyes);
        addLevelCheck(73, 78);
        addQuestCompletedCheck(603);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int spawned_daemon_of_hundred_eyes = st.getInt("spawned_daemon_of_hundred_eyes");
        int GetHTMLCookie = st.getInt("daemon_of_hundred_eyes_second_cookie");
        int npcId = npc.getNpcId();

        if (npcId == eye_of_argos) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("daemon_of_hundred_eyes_second", String.valueOf(1 * 10 + 1), true);
                st.setMemoState("spawned_daemon_of_hundred_eyes", String.valueOf(0), true);
                st.takeItems(q_unfinished_s_crystal, 1);
                st.giveItems(q_summon_crystal, 1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "eye_of_argos_q0604_0104.htm";
            } else if (event.equalsIgnoreCase("reply_3") && GetHTMLCookie == 3 - 1)
                if (st.ownItemCount(q_essense_of_daimon) >= 1) {
                    int i1 = Rnd.get(1000);
                    st.takeItems(q_essense_of_daimon, 1);
                    if (i1 < 167)
                        st.giveItems(dye_i2m2_c, 5);
                    else if (i1 < 167 + 167)
                        st.giveItems(dye_i2w2_c, 5);
                    else if (i1 < 167 + 167 + 167)
                        st.giveItems(dye_m2i2_c, 5);
                    else if (i1 < 167 + 167 + 167 + 167)
                        st.giveItems(dye_m2w2_c, 5);
                    else if (i1 < 167 + 167 + 167 + 167 + 167)
                        st.giveItems(dye_w2i2_c, 5);
                    else if (i1 < 167 + 167 + 167 + 167 + 167 + 165)
                        st.giveItems(dye_w2m2_c, 5);
                    st.removeMemo("daemon_of_hundred_eyes_second");
                    st.removeMemo("daemon_of_hundred_eyes_second_cookie");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(true);
                    htmltext = "eye_of_argos_q0604_0301.htm";
                } else
                    htmltext = "eye_of_argos_q0604_0302.htm";
        } else if (npcId == daimons_altar)
            if (event.equalsIgnoreCase("60401")) {
                Functions.npcSay(npc, NpcString.CAN_LIGHT_EXIST_WITHOUT_DARKNESS);
                st.setMemoState("spawned_daemon_of_hundred_eyes", String.valueOf(0), true);
                if (npc != null)
                    npc.deleteMe();
                return null;
            } else if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 2 - 1)
                if (st.ownItemCount(q_summon_crystal) >= 1) {
                    if (spawned_daemon_of_hundred_eyes == 0) {
                        st.setCond(2);
                        st.setMemoState("daemon_of_hundred_eyes_second", String.valueOf(2 * 10 + 1), true);
                        st.takeItems(q_summon_crystal, 1);
                        htmltext = "daimons_altar_q0604_0201.htm";
                        st.setMemoState("spawned_daemon_of_hundred_eyes", String.valueOf(1), true);
                        NpcInstance daemon_eyes = st.addSpawn(daemon_of_hundred_eyes, 186320, -43904, -3175);
                        Functions.npcSay(daemon_eyes, NpcString.WHO_IS_CALLING_ME);
                        st.startQuestTimer("60401", 1000 * 1200, daemon_eyes);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "daimons_altar_q0604_0201.htm";
                    } else
                        htmltext = "daimons_altar_q0604_0202.htm";
                } else
                    htmltext = "daimons_altar_q0604_0203.htm";
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("daemon_of_hundred_eyes_second");
        int spawned_daemon_of_hundred_eyes = st.getInt("spawned_daemon_of_hundred_eyes");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == eye_of_argos) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                        case QUEST:
                            htmltext = "eye_of_argos_q0604_0103.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            if (st.ownItemCount(q_unfinished_s_crystal) >= 1)
                                htmltext = "eye_of_argos_q0604_0101.htm";
                            else {
                                htmltext = "eye_of_argos_q0604_0102.htm";
                                st.exitQuest(true);
                            }
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == eye_of_argos) {
                    if (GetMemoState == 1 * 10 + 1)
                        htmltext = "eye_of_argos_q0604_0105.htm";
                    else if (GetMemoState >= 2 * 10 + 2)
                        if (st.ownItemCount(q_essense_of_daimon) >= 1) {
                            st.setMemoState("daemon_of_hundred_eyes_second_cookie", String.valueOf(2), true);
                            htmltext = "eye_of_argos_q0604_0201.htm";
                        } else
                            htmltext = "eye_of_argos_q0604_0202.htm";
                } else if (npcId == daimons_altar)
                    if (st.ownItemCount(q_summon_crystal) >= 1 && GetMemoState == 1 * 10 + 1) {
                        st.setMemoState("daemon_of_hundred_eyes_second_cookie", String.valueOf(1), true);
                        htmltext = "daimons_altar_q0604_0101.htm";
                    } else if (GetMemoState == 2 * 10 + 1) {
                        if (spawned_daemon_of_hundred_eyes == 0) {
                            st.setMemoState("spawned_daemon_of_hundred_eyes", String.valueOf(1), true);
                            NpcInstance daemon_eyes = st.addSpawn(daemon_of_hundred_eyes, 186320, -43904, -3175);
                            Functions.npcSay(daemon_eyes, NpcString.WHO_IS_CALLING_ME);
                            st.startQuestTimer("60401", 1000 * 1200, daemon_eyes);
                            htmltext = "daimons_altar_q0604_0201.htm";
                        } else
                            htmltext = "daimons_altar_q0604_0202.htm";
                    } else if (GetMemoState >= 2 * 10 + 2)
                        htmltext = "daimons_altar_q0604_0204.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("daemon_of_hundred_eyes_second");
        int npcId = npc.getNpcId();

        if (GetMemoState >= 2 * 10 - 9 && GetMemoState <= 2 * 10 + 1)
            if (npcId == daemon_of_hundred_eyes) {
                int i4 = Rnd.get(1000);
                if (i4 < 1000)
                    if (st.ownItemCount(q_essense_of_daimon) + 1 >= 1) {
                        st.setCond(3);
                        st.setMemoState("daemon_of_hundred_eyes_second", String.valueOf(2 * 10 + 2), true);
                        st.setMemoState("spawned_daemon_of_hundred_eyes", String.valueOf(0), true);
                        st.giveItems(q_essense_of_daimon, 1 - st.ownItemCount(q_essense_of_daimon));
                        st.soundEffect(SOUND_MIDDLE);
                    } else {
                        st.giveItems(q_essense_of_daimon, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            }
        return null;
    }
}