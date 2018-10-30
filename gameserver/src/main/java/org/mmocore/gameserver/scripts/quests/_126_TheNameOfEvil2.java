package org.mmocore.gameserver.scripts.quests;

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
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _126_TheNameOfEvil2 extends Quest {
    // npc
    private final static int mushika = 32114;
    private final static int asama = 32115;
    private final static int ulu_kaimu_stone = 32119;
    private final static int balu_kaimu_stone = 32120;
    private final static int jiuta_kaimu_stone = 32121;
    private final static int grave_of_brave_man = 32122;
    private final static int statue_of_shilen = 32109;

    // questitem
    private final static int q_ash_flour = 8783;
    private final static int q_muzzle_pattem = 8781;
    private final static int q_piece_of_gazk = 8782;

    // etcitem
    private final static int scrl_of_ench_wp_a = 729;

    public _126_TheNameOfEvil2() {
        super(false);
        addStartNpc(asama);
        addTalkId(mushika, ulu_kaimu_stone, balu_kaimu_stone, jiuta_kaimu_stone, grave_of_brave_man, statue_of_shilen);
        addQuestItem(q_ash_flour, q_muzzle_pattem);
        addLevelCheck(77);
        addQuestCompletedCheck(125);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("name_of_cruel_god_two");
        int GetMemoStateEx = st.getInt("name_of_cruel_god_two_ex");

        if (event.equalsIgnoreCase("quest_accept")) {
            st.setCond(1);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            htmltext = "asama_q0126_07.htm";
        } else if (event.equalsIgnoreCase("reply_4")) {
            st.setCond(2);
            st.setMemoState("name_of_cruel_god_two", String.valueOf(1), true);
            st.soundEffect(SOUND_MIDDLE);
            htmltext = "asama_q0126_10.htm";
        } else if (event.equalsIgnoreCase("reply_62") && GetMemoState == 406) {
            st.setCond(21);
            st.setMemoState("name_of_cruel_god_two", String.valueOf(407), true);
            st.soundEffect(SOUND_MIDDLE);
            htmltext = "asama_q0126_16.htm";
        } else if (event.equalsIgnoreCase("reply_71") && GetMemoState == 407) {
            st.setMemoState("name_of_cruel_god_two", String.valueOf(408), true);
            htmltext = "asama_q0126_26.htm";
        } else if (event.equalsIgnoreCase("reply_72") && GetMemoState == 408) {
            st.setCond(22);
            st.setMemoState("name_of_cruel_god_two", String.valueOf(409), true);
            st.soundEffect(SOUND_MIDDLE);
            htmltext = "asama_q0126_28.htm";
        } else if (event.equalsIgnoreCase("reply_1") && (GetMemoState == 409 || GetMemoState == 410)) {
            htmltext = "mushika_q0126_03.htm";
            if (GetMemoState == 409) {
                st.setCond(23);
                st.setMemoState("name_of_cruel_god_two", String.valueOf(410), true);
                st.soundEffect(SOUND_MIDDLE);
            }
        } else if (event.equalsIgnoreCase("reply_2") && GetMemoState == 410)
            htmltext = "mushika_q0126_08.htm";
        else if (event.equalsIgnoreCase("reply_3") && GetMemoState == 410) {
            st.giveItems(ADENA_ID, 460483);
            st.addExpAndSp(1015973, 102802);
            st.giveItems(scrl_of_ench_wp_a, 1);
            htmltext = "mushika_q0126_09.htm";
            st.removeMemo("name_of_cruel_god_two");
            st.removeMemo("name_of_cruel_god_two_ex");
            st.soundEffect(SOUND_FINISH);
            st.exitQuest(false);
        } else if (event.equalsIgnoreCase("reply_5") && GetMemoState == 2) {
            st.setCond(3);
            st.setMemoState("name_of_cruel_god_two", String.valueOf(3), true);
            st.soundEffect(SOUND_MIDDLE);
            htmltext = "ulu_kaimu_stone_q0126_03.htm";
        } else if (event.equalsIgnoreCase("reply_6") && GetMemoState == 3)
            htmltext = "ulu_kaimu_stone_q0126_05.htm";
        else if (event.equalsIgnoreCase("reply_7"))
            htmltext = "ulu_kaimu_stone_q0126_06.htm";
        else if (event.equalsIgnoreCase("reply_8") && GetMemoState == 3) {
            st.setCond(4);
            st.setMemoState("name_of_cruel_god_two", String.valueOf(4), true);
            st.soundEffect(SOUND_MIDDLE);
            htmltext = "ulu_kaimu_stone_q0126_07.htm";
        } else if (event.equalsIgnoreCase("reply_9")) {
            htmltext = "ulu_kaimu_stone_q0126_09.htm";
            st.soundEffect(SOUND_ELCROKI_SONG_1ST);
        } else if (event.equalsIgnoreCase("reply_10"))
            htmltext = "ulu_kaimu_stone_q0126_10.htm";
        else if (event.equalsIgnoreCase("reply_11") && GetMemoState == 4) {
            st.setCond(5);
            st.setMemoState("name_of_cruel_god_two", String.valueOf(5), true);
            st.soundEffect(SOUND_MIDDLE);
            htmltext = "ulu_kaimu_stone_q0126_11.htm";
        } else if (event.equalsIgnoreCase("reply_12")) {
            htmltext = "ulu_kaimu_stone_q0126_13.htm";
            st.soundEffect(SOUND_ELCROKI_SONG_1ST);
        } else if (event.equalsIgnoreCase("reply_12a") && GetMemoState == 5)
            htmltext = "balu_kaimu_stone_q0126_03.htm";
        else if (event.equalsIgnoreCase("reply_13") && GetMemoState == 5) {
            st.setCond(6);
            st.setMemoState("name_of_cruel_god_two", String.valueOf(7), true);
            st.soundEffect(SOUND_MIDDLE);
            htmltext = "balu_kaimu_stone_q0126_04.htm";
        } else if (event.equalsIgnoreCase("reply_400") && GetMemoState == 7)
            htmltext = "balu_kaimu_stone_q0126_06.htm";
        else if (event.equalsIgnoreCase("reply_14") && GetMemoState == 7) {
            st.setCond(7);
            st.setMemoState("name_of_cruel_god_two", String.valueOf(8), true);
            st.soundEffect(SOUND_MIDDLE);
            htmltext = "balu_kaimu_stone_q0126_07.htm";
        } else if (event.equalsIgnoreCase("reply_15")) {
            htmltext = "balu_kaimu_stone_q0126_09.htm";
            st.soundEffect(SOUND_ELCROKI_SONG_2ND);
        } else if (event.equalsIgnoreCase("reply_16"))
            htmltext = "balu_kaimu_stone_q0126_10.htm";
        else if (event.equalsIgnoreCase("reply_17") && GetMemoState == 8) {
            st.setCond(8);
            st.setMemoState("name_of_cruel_god_two", String.valueOf(9), true);
            st.soundEffect(SOUND_MIDDLE);
            htmltext = "balu_kaimu_stone_q0126_11.htm";
        } else if (event.equalsIgnoreCase("reply_18")) {
            htmltext = "balu_kaimu_stone_q0126_13.htm";
            st.soundEffect(SOUND_ELCROKI_SONG_2ND);
        } else if (event.equalsIgnoreCase("reply_18a") && GetMemoState == 9) {
            st.setCond(9);
            st.setMemoState("name_of_cruel_god_two", String.valueOf(11), true);
            st.soundEffect(SOUND_MIDDLE);
            htmltext = "jiuta_kaimu_stone_q0126_03.htm";
        } else if (event.equalsIgnoreCase("reply_19") && GetMemoState == 11)
            htmltext = "jiuta_kaimu_stone_q0126_05.htm";
        else if (event.equalsIgnoreCase("reply_20"))
            htmltext = "jiuta_kaimu_stone_q0126_06.htm";
        else if (event.equalsIgnoreCase("reply_21"))
            htmltext = "jiuta_kaimu_stone_q0126_07.htm";
        else if (event.equalsIgnoreCase("reply_22"))
            htmltext = "jiuta_kaimu_stone_q0126_08.htm";
        else if (event.equalsIgnoreCase("reply_23") && GetMemoState == 11) {
            st.setCond(10);
            st.setMemoState("name_of_cruel_god_two", String.valueOf(12), true);
            st.soundEffect(SOUND_MIDDLE);
            htmltext = "jiuta_kaimu_stone_q0126_09.htm";
        } else if (event.equalsIgnoreCase("reply_24")) {
            htmltext = "jiuta_kaimu_stone_q0126_11.htm";
            st.soundEffect(SOUND_ELCROKI_SONG_3RD);
        } else if (event.equalsIgnoreCase("reply_25"))
            htmltext = "jiuta_kaimu_stone_q0126_12.htm";
        else if (event.equalsIgnoreCase("reply_26"))
            htmltext = "jiuta_kaimu_stone_q0126_13.htm";
        else if (event.equalsIgnoreCase("reply_27"))
            htmltext = "jiuta_kaimu_stone_q0126_14.htm";
        else if (event.equalsIgnoreCase("reply_28") && GetMemoState == 12) {
            st.giveItems(q_piece_of_gazk, 1);
            st.setCond(11);
            st.setMemoState("name_of_cruel_god_two", String.valueOf(13), true);
            st.soundEffect(SOUND_MIDDLE);
            htmltext = "jiuta_kaimu_stone_q0126_15.htm";
        } else if (event.equalsIgnoreCase("reply_29")) {
            htmltext = "jiuta_kaimu_stone_q0126_17.htm";
            st.soundEffect(SOUND_ELCROKI_SONG_3RD);
        } else if (event.equalsIgnoreCase("reply_30") && GetMemoState == 13) {
            htmltext = "grave_of_brave_man_q0126_03.htm";
            npc.doCast(SkillTable.getInstance().getSkillEntry(5089, 1), st.getPlayer(), true);
        } else if (event.equalsIgnoreCase("reply_31"))
            htmltext = "grave_of_brave_man_q0126_04.htm";
        else if (event.equalsIgnoreCase("reply_32"))
            htmltext = "grave_of_brave_man_q0126_05.htm";
        else if (event.equalsIgnoreCase("reply_33") && GetMemoState == 13) {
            st.takeItems(q_piece_of_gazk, -1);
            st.setMemoState("name_of_cruel_god_two", String.valueOf(14), true);
            st.setMemoState("name_of_cruel_god_two_ex", String.valueOf(0), true);
            htmltext = "grave_of_brave_man_q0126_06.htm";
        } else if (event.equalsIgnoreCase("reply_300") && GetMemoState == 14)
            htmltext = "grave_of_brave_man_q0126_08.htm";
        else if (event.equalsIgnoreCase("reply_34"))
            htmltext = "grave_of_brave_man_q0126_09.htm";
        else if (event.equalsIgnoreCase("reply_35"))
            htmltext = "grave_of_brave_man_q0126_10.htm";
        else if (event.equalsIgnoreCase("reply_36"))
            htmltext = "grave_of_brave_man_q0126_11.htm";
        else if (event.equalsIgnoreCase("reply_37"))
            htmltext = "grave_of_brave_man_q0126_12.htm";
        else if (event.equalsIgnoreCase("reply_38"))
            htmltext = "grave_of_brave_man_q0126_13.htm";
        else if (event.equalsIgnoreCase("reply_39") && GetMemoState == 14)
            htmltext = "grave_of_brave_man_q0126_14.htm";
        else if (event.equalsIgnoreCase("reply_40") && GetMemoState == 14) {
            st.setCond(13);
            st.setMemoState("name_of_cruel_god_two", String.valueOf(15), true);
            htmltext = "grave_of_brave_man_q0126_15.htm";
            st.soundEffect(SOUND_MIDDLE);
        } else if (event.equalsIgnoreCase("reply_41") && GetMemoState == 15)
            htmltext = "grave_of_brave_man_q0126_17.htm";
        else if (event.equalsIgnoreCase("reply_42") && GetMemoState == 15) {
            st.setCond(14);
            st.setMemoState("name_of_cruel_god_two", String.valueOf(16), true);
            htmltext = "grave_of_brave_man_q0126_18.htm";
            st.soundEffect(SOUND_MIDDLE);
        } else if (event.equalsIgnoreCase("reply_43") && GetMemoState == 16) {
            st.setMemoState("name_of_cruel_god_two", String.valueOf(100), true);
            st.setMemoState("name_of_cruel_god_two_ex", String.valueOf(7473), true);
            htmltext = "grave_of_brave_man_q0126_21.htm";
        } else if (event.equalsIgnoreCase("reply_80") && GetMemoState >= 100 && GetMemoState < 200) {
            st.setMemoState("name_of_cruel_god_two", String.valueOf(100), true);
            st.setMemoState("name_of_cruel_god_two_ex", String.valueOf(7473), true);
            htmltext = "grave_of_brave_man_q0126_22.htm";
        } else if (event.equalsIgnoreCase("reply_44") && GetMemoState == 100)
            htmltext = "grave_of_brave_man_q0126_24.htm";
        else if (event.equalsIgnoreCase("reply_100") && GetMemoState == 100) {
            st.setMemoState("name_of_cruel_god_two", String.valueOf(101), true);
            htmltext = "grave_of_brave_man_q0126_26.htm";
        } else if (event.equalsIgnoreCase("reply_101") && GetMemoState == 100) {
            st.setMemoState("name_of_cruel_god_two", String.valueOf(101), true);
            st.setMemoState("name_of_cruel_god_two_ex", String.valueOf(97756), true);
            htmltext = "grave_of_brave_man_q0126_27.htm";
        } else if (event.equalsIgnoreCase("reply_45") && GetMemoState == 101)
            htmltext = "grave_of_brave_man_q0126_28.htm";
        else if (event.equalsIgnoreCase("reply_102") && GetMemoState == 101) {
            st.setMemoState("name_of_cruel_god_two", String.valueOf(102), true);
            htmltext = "grave_of_brave_man_q0126_30.htm";
        } else if (event.equalsIgnoreCase("reply_103") && GetMemoState == 101) {
            st.setMemoState("name_of_cruel_god_two", String.valueOf(102), true);
            st.setMemoState("name_of_cruel_god_two_ex", String.valueOf(97756), true);
            htmltext = "grave_of_brave_man_q0126_31.htm";
        } else if (event.equalsIgnoreCase("reply_46") && GetMemoState == 102)
            htmltext = "grave_of_brave_man_q0126_32.htm";
        else if (event.equalsIgnoreCase("reply_104") && GetMemoState == 102) {
            st.setMemoState("name_of_cruel_god_two", String.valueOf(103), true);
            htmltext = "grave_of_brave_man_q0126_34.htm";
        } else if (event.equalsIgnoreCase("reply_105") && GetMemoState == 102) {
            st.setMemoState("name_of_cruel_god_two", String.valueOf(103), true);
            st.setMemoState("name_of_cruel_god_two_ex", String.valueOf(97756), true);
            htmltext = "grave_of_brave_man_q0126_35.htm";
        } else if (event.equalsIgnoreCase("reply_47") && GetMemoState == 103)
            htmltext = "grave_of_brave_man_q0126_36.htm";
        else if (event.equalsIgnoreCase("reply_106") && GetMemoState == 103) {
            st.setMemoState("name_of_cruel_god_two", String.valueOf(104), true);
            htmltext = "grave_of_brave_man_q0126_38.htm";
        } else if (event.equalsIgnoreCase("reply_107") && GetMemoState == 103) {
            st.setMemoState("name_of_cruel_god_two", String.valueOf(104), true);
            st.setMemoState("name_of_cruel_god_two_ex", String.valueOf(97756), true);
            htmltext = "grave_of_brave_man_q0126_39.htm";
        } else if (event.equalsIgnoreCase("reply_48") && GetMemoState == 104)
            htmltext = "grave_of_brave_man_q0126_40.htm";
        else if (event.equalsIgnoreCase("reply_108") && GetMemoState == 104) {
            if (GetMemoStateEx == 7473) {
                st.setCond(15);
                st.setMemoState("name_of_cruel_god_two", String.valueOf(200), true);
                st.setMemoState("name_of_cruel_god_two_ex", String.valueOf(8302), true);
                htmltext = "grave_of_brave_man_q0126_42.htm";
                st.soundEffect(SOUND_MIDDLE);
            } else
                htmltext = "grave_of_brave_man_q0126_43.htm";
        } else if (event.equalsIgnoreCase("reply_109") && GetMemoState == 104)
            htmltext = "grave_of_brave_man_q0126_44.htm";
        else if (event.equalsIgnoreCase("reply_49") && GetMemoState == 200)
            htmltext = "grave_of_brave_man_q0126_45.htm";
        else if (event.equalsIgnoreCase("reply_110") && GetMemoState == 200) {
            st.setMemoState("name_of_cruel_god_two", String.valueOf(201), true);
            htmltext = "grave_of_brave_man_q0126_47.htm";
        } else if (event.equalsIgnoreCase("reply_111") && GetMemoState == 200) {
            st.setMemoState("name_of_cruel_god_two", String.valueOf(201), true);
            st.setMemoState("name_of_cruel_god_two_ex", String.valueOf(97756), true);
            htmltext = "grave_of_brave_man_q0126_48.htm";
        } else if (event.equalsIgnoreCase("reply_50") && GetMemoState == 201)
            htmltext = "grave_of_brave_man_q0126_49.htm";
        else if (event.equalsIgnoreCase("reply_112") && GetMemoState == 201) {
            st.setMemoState("name_of_cruel_god_two", String.valueOf(202), true);
            htmltext = "grave_of_brave_man_q0126_51.htm";
        } else if (event.equalsIgnoreCase("reply_113") && GetMemoState == 201) {
            st.setMemoState("name_of_cruel_god_two", String.valueOf(202), true);
            st.setMemoState("name_of_cruel_god_two_ex", String.valueOf(97756), true);
            htmltext = "grave_of_brave_man_q0126_52.htm";
        } else if (event.equalsIgnoreCase("reply_51") && GetMemoState == 202)
            htmltext = "grave_of_brave_man_q0126_53.htm";
        else if (event.equalsIgnoreCase("reply_114") && GetMemoState == 202) {
            st.setMemoState("name_of_cruel_god_two", String.valueOf(203), true);
            htmltext = "grave_of_brave_man_q0126_55.htm";
        } else if (event.equalsIgnoreCase("reply_115") && GetMemoState == 202) {
            st.setMemoState("name_of_cruel_god_two", String.valueOf(203), true);
            st.setMemoState("name_of_cruel_god_two_ex", String.valueOf(97756), true);
            htmltext = "grave_of_brave_man_q0126_56.htm";
        } else if (event.equalsIgnoreCase("reply_52") && GetMemoState == 203)
            htmltext = "grave_of_brave_man_q0126_57.htm";
        else if (event.equalsIgnoreCase("reply_116") && GetMemoState == 203) {
            st.setMemoState("name_of_cruel_god_two", String.valueOf(204), true);
            htmltext = "grave_of_brave_man_q0126_59.htm";
        } else if (event.equalsIgnoreCase("reply_117") && GetMemoState == 203) {
            st.setMemoState("name_of_cruel_god_two", String.valueOf(204), true);
            st.setMemoState("name_of_cruel_god_two_ex", String.valueOf(97756), true);
            htmltext = "grave_of_brave_man_q0126_60.htm";
        } else if (event.equalsIgnoreCase("reply_53") && GetMemoState == 204)
            htmltext = "grave_of_brave_man_q0126_61.htm";
        else if (event.equalsIgnoreCase("reply_118") && GetMemoState == 204) {
            if (GetMemoStateEx == 8302) {
                st.setCond(16);
                st.setMemoState("name_of_cruel_god_two", String.valueOf(300), true);
                st.setMemoState("name_of_cruel_god_two_ex", String.valueOf(1134), true);
                htmltext = "grave_of_brave_man_q0126_63.htm";
                st.soundEffect(SOUND_MIDDLE);
            } else {
                st.setMemoState("name_of_cruel_god_two", String.valueOf(200), true);
                st.setMemoState("name_of_cruel_god_two_ex", String.valueOf(8302), true);
                htmltext = "grave_of_brave_man_q0126_64.htm";
            }
        } else if (event.equalsIgnoreCase("reply_119") && GetMemoState == 204) {
            st.setMemoState("name_of_cruel_god_two", String.valueOf(200), true);
            st.setMemoState("name_of_cruel_god_two_ex", String.valueOf(8302), true);
            htmltext = "grave_of_brave_man_q0126_65.htm";
        } else if (event.equalsIgnoreCase("reply_54") && GetMemoState == 300)
            htmltext = "grave_of_brave_man_q0126_66.htm";
        else if (event.equalsIgnoreCase("reply_120") && GetMemoState == 300) {
            st.setMemoState("name_of_cruel_god_two", String.valueOf(301), true);
            htmltext = "grave_of_brave_man_q0126_68.htm";
        } else if (event.equalsIgnoreCase("reply_121") && GetMemoState == 300) {
            st.setMemoState("name_of_cruel_god_two", String.valueOf(301), true);
            st.setMemoState("name_of_cruel_god_two_ex", String.valueOf(97756), true);
            htmltext = "grave_of_brave_man_q0126_69.htm";
        } else if (event.equalsIgnoreCase("reply_55") && GetMemoState == 301)
            htmltext = "grave_of_brave_man_q0126_70.htm";
        else if (event.equalsIgnoreCase("reply_122") && GetMemoState == 301) {
            st.setMemoState("name_of_cruel_god_two", String.valueOf(302), true);
            htmltext = "grave_of_brave_man_q0126_72.htm";
        } else if (event.equalsIgnoreCase("reply_123") && GetMemoState == 301) {
            st.setMemoState("name_of_cruel_god_two", String.valueOf(302), true);
            st.setMemoState("name_of_cruel_god_two_ex", String.valueOf(97756), true);
            htmltext = "grave_of_brave_man_q0126_73.htm";
        } else if (event.equalsIgnoreCase("reply_56") && GetMemoState == 302)
            htmltext = "grave_of_brave_man_q0126_74.htm";
        else if (event.equalsIgnoreCase("reply_124") && GetMemoState == 302) {
            st.setMemoState("name_of_cruel_god_two", String.valueOf(303), true);
            htmltext = "grave_of_brave_man_q0126_76.htm";
        } else if (event.equalsIgnoreCase("reply_125") && GetMemoState == 302) {
            st.setMemoState("name_of_cruel_god_two", String.valueOf(303), true);
            st.setMemoState("name_of_cruel_god_two_ex", String.valueOf(97756), true);
            htmltext = "grave_of_brave_man_q0126_77.htm";
        } else if (event.equalsIgnoreCase("reply_57") && GetMemoState == 303)
            htmltext = "grave_of_brave_man_q0126_78.htm";
        else if (event.equalsIgnoreCase("reply_126") && GetMemoState == 303) {
            st.setMemoState("name_of_cruel_god_two", String.valueOf(304), true);
            htmltext = "grave_of_brave_man_q0126_80.htm";
        } else if (event.equalsIgnoreCase("reply_127") && GetMemoState == 303) {
            st.setMemoState("name_of_cruel_god_two", String.valueOf(304), true);
            st.setMemoState("name_of_cruel_god_two_ex", String.valueOf(97756), true);
            htmltext = "grave_of_brave_man_q0126_81.htm";
        } else if (event.equalsIgnoreCase("reply_58") && GetMemoState == 304)
            htmltext = "grave_of_brave_man_q0126_82.htm";
        else if (event.equalsIgnoreCase("reply_128") && GetMemoState == 304) {
            if (GetMemoStateEx == 1134) {
                st.setCond(17);
                st.setMemoState("name_of_cruel_god_two", String.valueOf(400), true);
                st.setMemoState("name_of_cruel_god_two_ex", String.valueOf(0), true);
                htmltext = "grave_of_brave_man_q0126_84.htm";
                st.soundEffect(SOUND_MIDDLE);
            } else {
                st.setMemoState("name_of_cruel_god_two", String.valueOf(300), true);
                st.setMemoState("name_of_cruel_god_two_ex", String.valueOf(1134), true);
                htmltext = "grave_of_brave_man_q0126_85.htm";
            }
        } else if (event.equalsIgnoreCase("reply_129") && GetMemoState == 304) {
            st.setMemoState("name_of_cruel_god_two", String.valueOf(300), true);
            st.setMemoState("name_of_cruel_god_two_ex", String.valueOf(1134), true);
            htmltext = "grave_of_brave_man_q0126_86.htm";
        } else if (event.equalsIgnoreCase("reply_130") && GetMemoState == 400 && st.ownItemCount(q_ash_flour) == 0) {
            st.giveItems(q_ash_flour, 1);
            st.soundEffect(SOUND_ELCROKI_SONG_FULL);
            htmltext = "grave_of_brave_man_q0126_87.htm";
            npc.doCast(SkillTable.getInstance().getSkillEntry(5089, 1), st.getPlayer(), true);
        } else if (event.equalsIgnoreCase("reply_131") && GetMemoState == 400 && st.ownItemCount(q_ash_flour) >= 1) {
            st.setMemoState("name_of_cruel_god_two", String.valueOf(401), true);
            htmltext = "grave_of_brave_man_q0126_88.htm";
        } else if (event.equalsIgnoreCase("reply_132") && GetMemoState == 401) {
            st.setCond(18);
            st.setMemoState("name_of_cruel_god_two", String.valueOf(402), true);
            htmltext = "grave_of_brave_man_q0126_90.htm";
            st.soundEffect(SOUND_MIDDLE);
        } else if (event.equalsIgnoreCase("reply_1a") && GetMemoState == 402 && st.ownItemCount(q_ash_flour) >= 1) {
            st.setCond(19);
            st.setMemoState("name_of_cruel_god_two", String.valueOf(404), true);
            htmltext = "statue_of_shilen_q0126_05.htm";
            st.soundEffect(SOUND_MIDDLE);
        } else if (event.equalsIgnoreCase("reply_2a") && GetMemoState == 404)
            htmltext = "statue_of_shilen_q0126_07.htm";
        else if (event.equalsIgnoreCase("reply_3a"))
            htmltext = "statue_of_shilen_q0126_08.htm";
        else if (event.equalsIgnoreCase("reply_4a"))
            htmltext = "statue_of_shilen_q0126_10.htm";
        else if (event.equalsIgnoreCase("reply_5a"))
            htmltext = "statue_of_shilen_q0126_11.htm";
        else if (event.equalsIgnoreCase("reply_6a"))
            htmltext = "statue_of_shilen_q0126_12.htm";
        else if (event.equalsIgnoreCase("reply_7a") && GetMemoState == 404) {
            st.setMemoState("name_of_cruel_god_two", String.valueOf(405), true);
            htmltext = "statue_of_shilen_q0126_13.htm";
        } else if (event.equalsIgnoreCase("reply_8a") && GetMemoState == 405)
            htmltext = "statue_of_shilen_q0126_15.htm";
        else if (event.equalsIgnoreCase("reply_9a"))
            htmltext = "statue_of_shilen_q0126_16.htm";
        else if (event.equalsIgnoreCase("reply_10a"))
            htmltext = "statue_of_shilen_q0126_17.htm";
        else if (event.equalsIgnoreCase("reply_11a"))
            htmltext = "statue_of_shilen_q0126_18.htm";
        else if (event.equalsIgnoreCase("reply_12b") && GetMemoState == 405) {
            st.setCond(20);
            st.setMemoState("name_of_cruel_god_two", String.valueOf(406), true);
            st.takeItems(q_ash_flour, -1);
            htmltext = "statue_of_shilen_q0126_19.htm";
            st.soundEffect(SOUND_MIDDLE);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("name_of_cruel_god_two");
        int GetMemoStateEx = st.getInt("name_of_cruel_god_two_ex");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == asama) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "asama_q0126_02.htm";
                            st.exitQuest(true);
                            break;
                        case QUEST:
                            htmltext = "asama_q0126_04.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "asama_q0126_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == asama) {
                    if (GetMemoState < 1)
                        htmltext = "asama_q0126_08.htm";
                    else if (GetMemoState == 1)
                        htmltext = "asama_q0126_11.htm";
                    else if (GetMemoState >= 2 && GetMemoState < 406)
                        htmltext = "asama_q0126_12.htm";
                    else if (GetMemoState == 406)
                        htmltext = "asama_q0126_13.htm";
                    else if (GetMemoState > 409)
                        htmltext = "asama_q0126_14.htm";
                    else if (GetMemoState == 407)
                        htmltext = "asama_q0126_17.htm";
                    else if (GetMemoState == 408)
                        htmltext = "asama_q0126_27.htm";
                    else if (GetMemoState == 409)
                        htmltext = "asama_q0126_29.htm";
                } else if (npcId == mushika) {
                    if (GetMemoState == 409)
                        htmltext = "mushika_q0126_01.htm";
                    else if (GetMemoState < 409)
                        htmltext = "mushika_q0126_02.htm";
                    else if (GetMemoState == 410)
                        htmltext = "mushika_q0126_03a.htm";
                } else if (npcId == ulu_kaimu_stone) {
                    if (GetMemoState == 1) {
                        npc.doCast(SkillTable.getInstance().getSkillEntry(5089, 1), st.getPlayer(), true);
                        st.setMemoState("name_of_cruel_god_two", String.valueOf(2), true);
                        htmltext = "ulu_kaimu_stone_q0126_01.htm";
                    } else if (GetMemoState < 1)
                        htmltext = "ulu_kaimu_stone_q0126_01a.htm";
                    else if (GetMemoState == 2)
                        htmltext = "ulu_kaimu_stone_q0126_02.htm";
                    else if (GetMemoState == 3)
                        htmltext = "ulu_kaimu_stone_q0126_04.htm";
                    else if (GetMemoState == 4)
                        htmltext = "ulu_kaimu_stone_q0126_08.htm";
                    else if (GetMemoState >= 5)
                        htmltext = "ulu_kaimu_stone_q0126_12.htm";
                } else if (npcId == balu_kaimu_stone) {
                    if (GetMemoState == 5) {
                        npc.doCast(SkillTable.getInstance().getSkillEntry(5089, 1), st.getPlayer(), true);
                        htmltext = "balu_kaimu_stone_q0126_01.htm";
                    } else if (GetMemoState < 5)
                        htmltext = "balu_kaimu_stone_q0126_02.htm";
                    else if (GetMemoState == 7)
                        htmltext = "balu_kaimu_stone_q0126_05.htm";
                    else if (GetMemoState == 8)
                        htmltext = "balu_kaimu_stone_q0126_08.htm";
                    else if (GetMemoState >= 9)
                        htmltext = "balu_kaimu_stone_q0126_12.htm";
                } else if (npcId == jiuta_kaimu_stone) {
                    if (GetMemoState == 9) {
                        npc.doCast(SkillTable.getInstance().getSkillEntry(5089, 1), st.getPlayer(), true);
                        htmltext = "jiuta_kaimu_stone_q0126_01.htm";
                    } else if (GetMemoState < 9)
                        htmltext = "jiuta_kaimu_stone_q0126_02.htm";
                    else if (GetMemoState == 11)
                        htmltext = "jiuta_kaimu_stone_q0126_04.htm";
                    else if (GetMemoState == 12)
                        htmltext = "jiuta_kaimu_stone_q0126_10.htm";
                    else if (GetMemoState >= 13)
                        htmltext = "jiuta_kaimu_stone_q0126_16.htm";
                } else if (npcId == grave_of_brave_man) {
                    if (GetMemoState == 13) {
                        htmltext = "grave_of_brave_man_q0126_01.htm";
                        if (GetMemoStateEx != 1818) {
                            st.setCond(12);
                            st.setMemoState("name_of_cruel_god_two_ex", String.valueOf(1818), true);
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    } else if (GetMemoState < 13)
                        htmltext = "grave_of_brave_man_q0126_02.htm";
                    else if (GetMemoState == 14)
                        htmltext = "grave_of_brave_man_q0126_07.htm";
                    else if (GetMemoState == 15)
                        htmltext = "grave_of_brave_man_q0126_16.htm";
                    else if (GetMemoState == 16)
                        htmltext = "grave_of_brave_man_q0126_19.htm";
                    else if (GetMemoState == 100)
                        htmltext = "grave_of_brave_man_q0126_25.htm";
                    else if (GetMemoState == 101)
                        htmltext = "grave_of_brave_man_q0126_29.htm";
                    else if (GetMemoState == 102)
                        htmltext = "grave_of_brave_man_q0126_33.htm";
                    else if (GetMemoState == 103)
                        htmltext = "grave_of_brave_man_q0126_37.htm";
                    else if (GetMemoState == 104)
                        htmltext = "grave_of_brave_man_q0126_41.htm";
                    else if (GetMemoState == 200)
                        htmltext = "grave_of_brave_man_q0126_46.htm";
                    else if (GetMemoState == 200)
                        htmltext = "grave_of_brave_man_q0126_46.htm";
                    else if (GetMemoState == 201)
                        htmltext = "grave_of_brave_man_q0126_50.htm";
                    else if (GetMemoState == 202)
                        htmltext = "grave_of_brave_man_q0126_54.htm";
                    else if (GetMemoState == 203)
                        htmltext = "grave_of_brave_man_q0126_58.htm";
                    else if (GetMemoState == 204)
                        htmltext = "grave_of_brave_man_q0126_62.htm";
                    else if (GetMemoState == 300)
                        htmltext = "grave_of_brave_man_q0126_67.htm";
                    else if (GetMemoState == 301)
                        htmltext = "grave_of_brave_man_q0126_71.htm";
                    else if (GetMemoState == 302)
                        htmltext = "grave_of_brave_man_q0126_75.htm";
                    else if (GetMemoState == 303)
                        htmltext = "grave_of_brave_man_q0126_79.htm";
                    else if (GetMemoState == 304)
                        htmltext = "grave_of_brave_man_q0126_83.htm";
                    else if (GetMemoState == 400 && st.ownItemCount(q_ash_flour) == 0) {
                        st.giveItems(q_ash_flour, 1);
                        st.soundEffect(SOUND_ELCROKI_SONG_FULL);
                        htmltext = "grave_of_brave_man_q0126_86a.htm";
                        npc.doCast(SkillTable.getInstance().getSkillEntry(5089, 1), st.getPlayer(), true);
                    } else if (GetMemoState == 400 && st.ownItemCount(q_ash_flour) == 1)
                        htmltext = "grave_of_brave_man_q0126_87a.htm";
                    else if (GetMemoState == 401)
                        htmltext = "grave_of_brave_man_q0126_89.htm";
                    else if (GetMemoState >= 402)
                        htmltext = "grave_of_brave_man_q0126_91.htm";
                } else if (npcId == statue_of_shilen)
                    if (GetMemoState == 402 && st.ownItemCount(q_ash_flour) >= 1)
                        htmltext = "statue_of_shilen_q0126_02.htm";
                    else if (GetMemoState < 402)
                        htmltext = "statue_of_shilen_q0126_03.htm";
                    else if (GetMemoState > 406)
                        htmltext = "statue_of_shilen_q0126_04.htm";
                    else if (GetMemoState == 404)
                        htmltext = "statue_of_shilen_q0126_06.htm";
                    else if (GetMemoState == 405)
                        htmltext = "statue_of_shilen_q0126_14.htm";
                    else if (GetMemoState == 406)
                        htmltext = "statue_of_shilen_q0126_20.htm";
                break;
        }
        return htmltext;
    }
}