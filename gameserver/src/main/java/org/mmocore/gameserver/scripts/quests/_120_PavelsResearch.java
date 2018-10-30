package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.serverpackets.MagicSkillUse;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _120_PavelsResearch extends Quest {
    // npc
    private static final int collecter_yumi = 32041;
    private static final int weather_controller1 = 32042;
    private static final int weather_controller2 = 32043;
    private static final int weather_controller3 = 32044;
    private static final int drchaos_box = 32045;
    private static final int pavel_atlanta = 32046;
    private static final int chaos_secretary_wendy = 32047;

    // items
    private static final int sealed_phoenixs_earing = 6324;

    // questitem
    private static final int q_drchaos_diary_off = 8058;
    private static final int q_drchaos_diary_on = 8059;
    private static final int q_drchaos_diary_key = 8060;
    private static final int q_flower_of_pavel = 8290;
    private static final int q_whisper_atlanta = 8291;
    private static final int q_broch_of_wendy = 8292;

    public _120_PavelsResearch() {
        super(false);
        addStartNpc(pavel_atlanta);
        addTalkId(pavel_atlanta, collecter_yumi, chaos_secretary_wendy, drchaos_box, weather_controller1, weather_controller2, weather_controller3);
        addQuestItem(q_drchaos_diary_off, q_drchaos_diary_on, q_drchaos_diary_key, q_flower_of_pavel, q_whisper_atlanta, q_broch_of_wendy);
        addLevelCheck(70);
        addQuestCompletedCheck(114);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        Player player = st.getPlayer();
        int GetMemoState = st.getInt("last_research_of_pavel");
        int GetMemoStateEx = st.getInt("last_research_of_pavel_ex");
        int npcId = npc.getNpcId();

        if (npcId == pavel_atlanta) {
            if (event.equalsIgnoreCase("quest_accept")) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = "pavel_atlanta_q0120_08.htm";
                        break;
                    default:
                        st.setCond(1);
                        st.setMemoState("last_research_of_pavel", String.valueOf(1), true);
                        st.setState(STARTED);
                        st.soundEffect(SOUND_ACCEPT);
                        htmltext = "pavel_atlanta_q0120_09.htm";
                        break;
                }
            } else if (event.equalsIgnoreCase("reply_1"))
                htmltext = "pavel_atlanta_q0120_05.htm";
            else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 1) {
                    st.setCond(2);
                    st.setMemoState("last_research_of_pavel", String.valueOf(2), true);
                    st.setMemoState("last_research_of_pavel_ex", String.valueOf(0), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "pavel_atlanta_q0120_12.htm";
                }
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 3) {
                    st.setCond(6);
                    st.setMemoState("last_research_of_pavel", String.valueOf(4), true);
                    st.giveItems(q_flower_of_pavel, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "pavel_atlanta_q0120_16.htm";
                }
            } else if (event.equalsIgnoreCase("reply_5")) {
                if (GetMemoState == 7) {
                    st.setCond(10);
                    st.setMemoState("last_research_of_pavel", String.valueOf(8), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "pavel_atlanta_q0120_25.htm";
                }
            } else if (event.equalsIgnoreCase("reply_6")) {
                if (GetMemoState == 11) {
                    st.setCond(13);
                    st.setMemoState("last_research_of_pavel", String.valueOf(12), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "pavel_atlanta_q0120_32.htm";
                }
            } else if (event.equalsIgnoreCase("reply_7")) {
                if (GetMemoState == 19) {
                    st.setCond(20);
                    st.setMemoState("last_research_of_pavel", String.valueOf(20), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "pavel_atlanta_q0120_38.htm";
                }
            } else if (event.equalsIgnoreCase("reply_8"))
                if (GetMemoState == 22) {
                    st.setCond(23);
                    st.setMemoState("last_research_of_pavel", String.valueOf(23), true);
                    st.giveItems(q_whisper_atlanta, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "pavel_atlanta_q0120_41.htm";
                }
        } else if (npcId == collecter_yumi) {
            if (event.equalsIgnoreCase("reply_1"))
                htmltext = "collecter_yumi_q0120_02.htm";
            else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 2 && GetMemoStateEx == 0) {
                    st.setCond(3);
                    st.setMemoState("last_research_of_pavel_ex", String.valueOf(1), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "collecter_yumi_q0120_03.htm";
                }
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 2 && GetMemoStateEx == 0) {
                    st.setCond(4);
                    st.setMemoState("last_research_of_pavel_ex", String.valueOf(2), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "collecter_yumi_q0120_05.htm";
                }
            } else if (event.equalsIgnoreCase("reply_5")) {
                if (GetMemoState == 5) {
                    st.setCond(8);
                    st.setMemoState("last_research_of_pavel", String.valueOf(6), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "collecter_yumi_q0120_13.htm";
                }
            } else if (event.equalsIgnoreCase("reply_6")) {
                if (GetMemoState == 14) {
                    st.setCond(16);
                    st.setMemoState("last_research_of_pavel", String.valueOf(15), true);
                    st.giveItems(q_drchaos_diary_key, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "collecter_yumi_q0120_17.htm";
                }
            } else if (event.equalsIgnoreCase("reply_7")) {
                if (GetMemoState == 15 && st.ownItemCount(q_drchaos_diary_on) >= 1) {
                    st.setCond(17);
                    st.setMemoState("last_research_of_pavel", String.valueOf(16), true);
                    st.takeItems(q_drchaos_diary_key, -1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "collecter_yumi_q0120_23.htm";
                }
            } else if (event.equalsIgnoreCase("reply_8"))
                htmltext = "collecter_yumi_q0120_30.htm";
            else if (event.equalsIgnoreCase("reply_9"))
                htmltext = "collecter_yumi_q0120_31.htm";
            else if (event.equalsIgnoreCase("reply_10"))
                if (GetMemoState == 26) {
                    st.giveItems(sealed_phoenixs_earing, 1);
                    st.giveItems(ADENA_ID, 783720);
                    st.addExpAndSp(3447315, 272615);
                    st.takeItems(q_broch_of_wendy, -1);
                    st.removeMemo("last_research_of_pavel");
                    st.removeMemo("last_research_of_pavel_ex");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "collecter_yumi_q0120_33.htm";
                }
        } else if (npcId == chaos_secretary_wendy) {
            if (event.equalsIgnoreCase("reply_1"))
                htmltext = "chaos_secretary_wendy_q0120_02.htm";
            else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 2) {
                    st.setCond(5);
                    st.setMemoState("last_research_of_pavel", String.valueOf(3), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "chaos_secretary_wendy_q0120_06.htm";
                }
            } else if (event.equalsIgnoreCase("reply_4"))
                htmltext = "chaos_secretary_wendy_q0120_09.htm";
            else if (event.equalsIgnoreCase("reply_5")) {
                if (GetMemoState == 4) {
                    st.setCond(7);
                    st.setMemoState("last_research_of_pavel", String.valueOf(5), true);
                    st.soundEffect(SOUND_MIDDLE);
                    st.takeItems(q_flower_of_pavel, -1);
                    htmltext = "chaos_secretary_wendy_q0120_10.htm";
                }
            } else if (event.equalsIgnoreCase("reply_6")) {
                if (GetMemoState == 6) {
                    st.setCond(9);
                    st.setMemoState("last_research_of_pavel", String.valueOf(7), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "chaos_secretary_wendy_q0120_14.htm";
                }
            } else if (event.equalsIgnoreCase("reply_7")) {
                if (GetMemoState == 12) {
                    st.setCond(14);
                    st.setMemoState("last_research_of_pavel", String.valueOf(13), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "chaos_secretary_wendy_q0120_18.htm";
                }
            } else if (event.equalsIgnoreCase("reply_8")) {
                if (GetMemoState == 23) {
                    st.setCond(24);
                    st.setMemoState("last_research_of_pavel", String.valueOf(24), true);
                    st.takeItems(q_whisper_atlanta, -1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "chaos_secretary_wendy_q0120_26.htm";
                }
            } else if (event.equalsIgnoreCase("reply_9")) {
                if (GetMemoState == 24) {
                    st.setMemoState("last_research_of_pavel", String.valueOf(25), true);
                    htmltext = "chaos_secretary_wendy_q0120_29.htm";
                }
            } else if (event.equalsIgnoreCase("reply_10"))
                if (GetMemoState == 25) {
                    st.setCond(25);
                    st.setMemoState("last_research_of_pavel", String.valueOf(26), true);
                    st.giveItems(q_broch_of_wendy, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "chaos_secretary_wendy_q0120_32.htm";
                }
        } else if (npcId == drchaos_box) {
            if (event.equalsIgnoreCase("reply_1"))
                if (GetMemoState == 13) {
                    st.setCond(15);
                    st.setMemoState("last_research_of_pavel", String.valueOf(14), true);
                    st.giveItems(q_drchaos_diary_off, 1);
                    if (player != null)
                        npc.broadcastPacket(new MagicSkillUse(npc, player, 5073, 5, 1500, 0));
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "drchaos_box_q0120_02.htm";
                }
        } else if (npcId == weather_controller1) {
            if (event.equalsIgnoreCase("reply_90")) {
                if (GetMemoState == 8) {
                    st.setMemoState("last_research_of_pavel_ex", String.valueOf(0), true);
                    htmltext = "weather_controller1_q0120_02.htm";
                }
            } else if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 8) {
                    st.setMemoState("last_research_of_pavel_ex", String.valueOf(1), true);
                    htmltext = "weather_controller1_q0120_03.htm";
                }
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 8) {
                    int i0 = GetMemoStateEx;
                    i0 = i0 % 10;
                    st.setMemoState("last_research_of_pavel_ex", String.valueOf(10 + i0), true);
                    htmltext = "weather_controller1_q0120_04.htm";
                }
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 8 && GetMemoStateEx != 11)
                    htmltext = "weather_controller1_q0120_05.htm";
                if (GetMemoState == 8 && GetMemoStateEx == 11) {
                    st.setCond(11);
                    st.setMemoState("last_research_of_pavel", String.valueOf(9), true);
                    st.setMemoState("last_research_of_pavel_ex", String.valueOf(0), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "weather_controller1_q0120_06.htm";
                }
            } else if (event.equalsIgnoreCase("reply_4"))
                htmltext = "weather_controller1_q0120_07.htm";
            else if (event.equalsIgnoreCase("reply_5")) {
                if (GetMemoState == 9) {
                    st.setMemoState("last_research_of_pavel", String.valueOf(10), true);
                    st.soundEffect(SOUND_DT_PERCUSSION_01);
                    htmltext = "weather_controller1_q0120_08.htm";
                }
            } else if (event.equalsIgnoreCase("reply_6")) {
                if (GetMemoState == 10 && GetMemoStateEx != 10101)
                    htmltext = "weather_controller1_q0120_09.htm";
                if (GetMemoState == 10 && GetMemoStateEx == 10101)
                    htmltext = "weather_controller1_q0120_13.htm";
            } else if (event.equalsIgnoreCase("reply_7")) {
                if (GetMemoState == 10) {
                    int i0 = GetMemoStateEx;
                    i0 = i0 / 10;
                    st.setMemoState("last_research_of_pavel_ex", String.valueOf(i0 * 10 + 1), true);
                    htmltext = "weather_controller1_q0120_10.htm";
                }
            } else if (event.equalsIgnoreCase("reply_8")) {
                if (GetMemoState == 10) {
                    int i0 = GetMemoStateEx;
                    int i1 = i0 / 1000;
                    int i2 = i0 % 100;
                    st.setMemoState("last_research_of_pavel_ex", String.valueOf(i1 * 1000 + 100 + i2), true);
                    htmltext = "weather_controller1_q0120_11.htm";
                }
            } else if (event.equalsIgnoreCase("reply_9")) {
                if (GetMemoState == 10) {
                    int i0 = GetMemoStateEx;
                    int i1 = i0 % 10000;
                    st.setMemoState("last_research_of_pavel_ex", String.valueOf(10000 + i1), true);
                    htmltext = "weather_controller1_q0120_12.htm";
                }
            } else if (event.equalsIgnoreCase("reply_10"))
                if (GetMemoState == 10 && GetMemoStateEx == 10101) {
                    st.setCond(12);
                    st.setMemoState("last_research_of_pavel", String.valueOf(11), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "weather_controller1_q0120_13a.htm";
                }
        } else if (npcId == weather_controller2) {
            if (event.equalsIgnoreCase("reply_90")) {
                if (GetMemoState == 16) {
                    st.setMemoState("last_research_of_pavel_ex", String.valueOf(0), true);
                    htmltext = "weather_controller2_q0120_02.htm";
                }
            } else if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 16) {
                    st.setMemoState("last_research_of_pavel_ex", String.valueOf(1), true);
                    htmltext = "weather_controller2_q0120_03.htm";
                }
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 16) {
                    int i0 = GetMemoStateEx;
                    i0 = i0 % 10;
                    st.setMemoState("last_research_of_pavel_ex", String.valueOf(10 + i0), true);
                    htmltext = "weather_controller2_q0120_04.htm";
                }
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 16 && GetMemoStateEx != 11)
                    htmltext = "weather_controller2_q0120_05.htm";
                if (GetMemoState == 16 && GetMemoStateEx == 11) {
                    st.setCond(18);
                    st.setMemoState("last_research_of_pavel", String.valueOf(17), true);
                    st.setMemoState("last_research_of_pavel_ex", String.valueOf(0), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "weather_controller2_q0120_06.htm";
                }
            } else if (event.equalsIgnoreCase("reply_4"))
                htmltext = "weather_controller2_q0120_07.htm";
            else if (event.equalsIgnoreCase("reply_5")) {
                if (GetMemoState == 17) {
                    st.setMemoState("last_research_of_pavel", String.valueOf(18), true);
                    htmltext = "weather_controller2_q0120_09.htm";
                }
            } else if (event.equalsIgnoreCase("reply_6")) {
                if (GetMemoState == 18 && GetMemoStateEx != 1111)
                    htmltext = "weather_controller2_q0120_11.htm";
                if (GetMemoState == 18 && GetMemoStateEx == 1111)
                    htmltext = "weather_controller2_q0120_11b.htm";
            } else if (event.equalsIgnoreCase("reply_7")) {
                if (GetMemoState == 18) {
                    int i0 = GetMemoStateEx;
                    i0 = i0 / 10;
                    st.setMemoState("last_research_of_pavel_ex", String.valueOf(i0 * 10 + 1), true);
                    htmltext = "weather_controller2_q0120_12.htm";
                }
            } else if (event.equalsIgnoreCase("reply_10"))
                htmltext = "weather_controller2_q0120_13.htm";
            else if (event.equalsIgnoreCase("reply_8")) {
                if (GetMemoState == 18 && GetMemoStateEx < 1000)
                    htmltext = "weather_controller2_q0120_14.htm";
                if (GetMemoState == 18 && GetMemoStateEx >= 1000)
                    htmltext = "weather_controller2_q0120_17.htm";
            } else if (event.equalsIgnoreCase("reply_71"))
                htmltext = "weather_controller2_q0120_15.htm";
            else if (event.equalsIgnoreCase("reply_11")) {
                if (GetMemoState == 18) {
                    int i0 = GetMemoStateEx;
                    int i1 = i0 / 10000;
                    int i2 = i0 % 1000;
                    st.setMemoState("last_research_of_pavel_ex", String.valueOf(i1 * 10000 + 1000 + i2), true);
                    st.soundEffect(SOUND_ED_DRONE_02);
                    htmltext = "weather_controller2_q0120_16.htm";
                }
            } else if (event.equalsIgnoreCase("reply_9"))
                htmltext = "weather_controller2_q0120_19.htm";
            else if (event.equalsIgnoreCase("reply_12")) {
                if (GetMemoState == 18) {
                    int i0 = GetMemoStateEx;
                    int i1 = i0 / 100;
                    int i2 = i0 % 10;
                    st.setMemoState("last_research_of_pavel_ex", String.valueOf(i1 * 100 + 10 + i2), true);
                    htmltext = "weather_controller2_q0120_20.htm";
                }
            } else if (event.equalsIgnoreCase("reply_14"))
                htmltext = "weather_controller2_q0120_23.htm";
            else if (event.equalsIgnoreCase("reply_72")) {
                if (GetMemoState == 18) {
                    int i0 = GetMemoStateEx;
                    int i1 = i0 / 1000;
                    int i2 = i0 % 100;
                    st.setMemoState("last_research_of_pavel_ex", String.valueOf(i1 * 1000 + 100 + i2), true);
                    htmltext = "weather_controller2_q0120_25.htm";
                }
            } else if (event.equalsIgnoreCase("reply_13"))
                if (GetMemoState == 18 && GetMemoStateEx == 1111) {
                    st.setCond(19);
                    st.setMemoState("last_research_of_pavel", String.valueOf(19), true);
                    st.setMemoState("last_research_of_pavel_ex", String.valueOf(0), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "weather_controller2_q0120_21.htm";
                }
        } else if (npcId == weather_controller3)
            if (event.equalsIgnoreCase("reply_90")) {
                if (GetMemoState == 20) {
                    st.setMemoState("last_research_of_pavel_ex", String.valueOf(0), true);
                    htmltext = "weather_controller3_q0120_02.htm";
                }
            } else if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 20) {
                    st.setMemoState("last_research_of_pavel_ex", String.valueOf(1), true);
                    htmltext = "weather_controller3_q0120_03.htm";
                }
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 20) {
                    int i0 = GetMemoStateEx;
                    i0 = i0 % 10;
                    st.setMemoState("last_research_of_pavel_ex", String.valueOf(10 + i0), true);
                    htmltext = "weather_controller3_q0120_04.htm";
                }
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 20 && GetMemoStateEx != 11)
                    htmltext = "weather_controller3_q0120_05.htm";
                if (GetMemoState == 20 && GetMemoStateEx == 11) {
                    st.setCond(21);
                    st.setMemoState("last_research_of_pavel", String.valueOf(21), true);
                    st.setMemoState("last_research_of_pavel_ex", String.valueOf(0), true);
                    st.soundEffect(SOUND_AC_PERCUSSION_02);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "weather_controller3_q0120_06.htm";
                }
            } else if (event.equalsIgnoreCase("reply_4"))
                htmltext = "weather_controller3_q0120_07.htm";
            else if (event.equalsIgnoreCase("reply_100")) {
                if (GetMemoState == 21 && GetMemoStateEx % 100 != 11)
                    htmltext = "weather_controller3_q0120_09.htm";
                if (GetMemoState == 21 && GetMemoStateEx % 100 == 11)
                    htmltext = "weather_controller3_q0120_09a.htm";
            } else if (event.equalsIgnoreCase("reply_5")) {
                if (GetMemoState == 21) {
                    int i0 = GetMemoStateEx;
                    int i1 = i0 / 100;
                    int i2 = i0 % 10;
                    st.setMemoState("last_research_of_pavel_ex", String.valueOf(i1 * 100 + 10 + i2), true);
                    htmltext = "weather_controller3_q0120_10.htm";
                }
            } else if (event.equalsIgnoreCase("reply_6")) {
                if (GetMemoState == 21 && GetMemoStateEx / 100 != 1) {
                    int i0 = GetMemoStateEx;
                    int i1 = i0 / 10;
                    st.setMemoState("last_research_of_pavel_ex", String.valueOf(i1 * 10 + 1), true);
                    htmltext = "weather_controller3_q0120_11.htm";
                }
                if (GetMemoState == 21 && GetMemoStateEx / 100 == 1)
                    htmltext = "weather_controller3_q0120_11a.htm";
            } else if (event.equalsIgnoreCase("reply_7")) {
                if (GetMemoState == 21 && GetMemoStateEx / 100 != 1)
                    htmltext = "weather_controller3_q0120_12.htm";
                if (GetMemoState == 21 && GetMemoStateEx / 100 == 1)
                    htmltext = "weather_controller3_q0120_12a.htm";
            } else if (event.equalsIgnoreCase("reply_8")) {
                if (GetMemoState == 21 && GetMemoStateEx / 100 != 1) {
                    int i0 = GetMemoStateEx;
                    i0 = i0 % 100;
                    st.setMemoState("last_research_of_pavel_ex", String.valueOf(100 + i0), true);
                    htmltext = "weather_controller3_q0120_13.htm";
                }
            } else if (event.equalsIgnoreCase("reply_9"))
                htmltext = "weather_controller3_q0120_14.htm";
            else if (event.equalsIgnoreCase("reply_10"))
                htmltext = "weather_controller3_q0120_15.htm";
            else if (event.equalsIgnoreCase("reply_11"))
                htmltext = "weather_controller3_q0120_16.htm";
            else if (event.equalsIgnoreCase("reply_12"))
                if (GetMemoState == 21 && GetMemoStateEx / 100 == 1) {
                    st.setCond(22);
                    st.setMemoState("last_research_of_pavel", String.valueOf(22), true);
                    st.soundEffect(SOUND_ED_DRONE_02);
                    if (player != null)
                        npc.broadcastPacket(new MagicSkillUse(npc, player, 5073, 5, 1500, 0));
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "weather_controller3_q0120_17.htm";
                }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("last_research_of_pavel");
        int GetMemoStateEx = st.getInt("last_research_of_pavel_ex");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == pavel_atlanta) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "pavel_atlanta_q0120_03.htm";
                            break;
                        case QUEST:
                            htmltext = "pavel_atlanta_q0120_08.htm";
                            break;
                        default:
                            htmltext = "pavel_atlanta_q0120_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == pavel_atlanta) {
                    if (GetMemoState == 1)
                        htmltext = "pavel_atlanta_q0120_10.htm";
                    else if (GetMemoState == 2)
                        htmltext = "pavel_atlanta_q0120_13.htm";
                    else if (GetMemoState == 3)
                        htmltext = "pavel_atlanta_q0120_14.htm";
                    else if (GetMemoState == 4)
                        htmltext = "pavel_atlanta_q0120_16a.htm";
                    else if (GetMemoState == 7)
                        htmltext = "pavel_atlanta_q0120_17.htm";
                    else if (GetMemoState == 8)
                        htmltext = "pavel_atlanta_q0120_28.htm";
                    else if (GetMemoState == 11)
                        htmltext = "pavel_atlanta_q0120_29.htm";
                    else if (GetMemoState == 12)
                        htmltext = "pavel_atlanta_q0120_33.htm";
                    else if (GetMemoState == 19)
                        htmltext = "pavel_atlanta_q0120_34.htm";
                    else if (GetMemoState == 20)
                        htmltext = "pavel_atlanta_q0120_39.htm";
                    else if (GetMemoState == 22)
                        htmltext = "pavel_atlanta_q0120_40.htm";
                    else if (GetMemoState == 23)
                        htmltext = "pavel_atlanta_q0120_42.htm";
                } else if (npcId == collecter_yumi) {
                    if (GetMemoState == 2 && GetMemoStateEx == 0)
                        htmltext = "collecter_yumi_q0120_01.htm";
                    else if (GetMemoState == 2 && GetMemoStateEx == 1)
                        htmltext = "collecter_yumi_q0120_04.htm";
                    else if (GetMemoState == 2 && GetMemoStateEx == 2)
                        htmltext = "collecter_yumi_q0120_06.htm";
                    else if (GetMemoState == 5 && GetMemoStateEx > 0)
                        htmltext = "collecter_yumi_q0120_07.htm";
                    else if (GetMemoState == 5 && GetMemoStateEx == 0)
                        htmltext = "collecter_yumi_q0120_08.htm";
                    else if (GetMemoState == 6)
                        htmltext = "collecter_yumi_q0120_14.htm";
                    else if (GetMemoState == 14)
                        htmltext = "collecter_yumi_q0120_15.htm";
                    else if (GetMemoState == 15 && st.ownItemCount(q_drchaos_diary_on) == 0)
                        htmltext = "collecter_yumi_q0120_18.htm";
                    else if (GetMemoState == 15 && st.ownItemCount(q_drchaos_diary_on) >= 1)
                        htmltext = "collecter_yumi_q0120_19.htm";
                    else if (GetMemoState == 16)
                        htmltext = "collecter_yumi_q0120_26.htm";
                    else if (GetMemoState == 26)
                        htmltext = "collecter_yumi_q0120_27.htm";
                } else if (npcId == chaos_secretary_wendy) {
                    if (GetMemoState == 2)
                        htmltext = "chaos_secretary_wendy_q0120_01.htm";
                    else if (GetMemoState == 3)
                        htmltext = "chaos_secretary_wendy_q0120_07.htm";
                    else if (GetMemoState == 4)
                        htmltext = "chaos_secretary_wendy_q0120_08.htm";
                    else if (GetMemoState == 5)
                        htmltext = "chaos_secretary_wendy_q0120_11.htm";
                    else if (GetMemoState == 6)
                        htmltext = "chaos_secretary_wendy_q0120_11z.htm";
                    else if (GetMemoState == 7)
                        htmltext = "chaos_secretary_wendy_q0120_15.htm";
                    else if (GetMemoState == 12)
                        htmltext = "chaos_secretary_wendy_q0120_16.htm";
                    else if (GetMemoState == 13)
                        htmltext = "chaos_secretary_wendy_q0120_19.htm";
                    else if (GetMemoState == 14)
                        htmltext = "chaos_secretary_wendy_q0120_20.htm";
                    else if (GetMemoState == 23)
                        htmltext = "chaos_secretary_wendy_q0120_21.htm";
                    else if (GetMemoState == 24)
                        htmltext = "chaos_secretary_wendy_q0120_26.htm";
                    else if (GetMemoState == 25)
                        htmltext = "chaos_secretary_wendy_q0120_29.htm";
                    else if (GetMemoState == 26)
                        htmltext = "chaos_secretary_wendy_q0120_33.htm";
                } else if (npcId == drchaos_box) {
                    if (GetMemoState == 13)
                        htmltext = "drchaos_box_q0120_01.htm";
                    else if (GetMemoState == 14)
                        htmltext = "drchaos_box_q0120_03.htm";
                } else if (npcId == weather_controller1) {
                    if (GetMemoState == 8) {
                        st.soundEffect(SOUND_CD_CRYSTAL_LOOP);
                        htmltext = "weather_controller1_q0120_01.htm";
                    } else if (GetMemoState == 9)
                        htmltext = "weather_controller1_q0120_06.htm";
                    else if (GetMemoState == 10 && GetMemoStateEx != 10101)
                        htmltext = "weather_controller1_q0120_09.htm";
                    else if (GetMemoState == 10 && GetMemoStateEx == 10101)
                        htmltext = "weather_controller1_q0120_13.htm";
                    else if (GetMemoState == 11)
                        htmltext = "weather_controller1_q0120_13a.htm";
                } else if (npcId == weather_controller2) {
                    if (GetMemoState == 16)
                        htmltext = "weather_controller2_q0120_01.htm";
                    else if (GetMemoState == 17)
                        htmltext = "weather_controller2_q0120_06.htm";
                    else if (GetMemoState == 18)
                        htmltext = "weather_controller2_q0120_09.htm";
                    else if (GetMemoState == 19)
                        htmltext = "weather_controller2_q0120_22.htm";
                } else if (npcId == weather_controller3)
                    if (GetMemoState == 20)
                        htmltext = "weather_controller3_q0120_01.htm";
                    else if (GetMemoState == 21)
                        htmltext = "weather_controller3_q0120_08.htm";
                    else if (GetMemoState == 22)
                        htmltext = "weather_controller3_q0120_19.htm";
                break;
        }
        return htmltext;
    }
}