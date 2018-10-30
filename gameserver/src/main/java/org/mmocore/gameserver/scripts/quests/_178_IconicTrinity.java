package org.mmocore.gameserver.scripts.quests;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.gameserver.data.htm.HtmCache;
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
public class _178_IconicTrinity extends Quest {
    // npc
    private static final int kekrops = 32138;
    private static final int past_statue = 32255;
    private static final int present_statue = 32256;
    private static final int future_statue = 32257;

    // etcitem
    private static final int scrl_of_ench_am_d = 956;

    public _178_IconicTrinity() {
        super(false);
        addStartNpc(kekrops);
        addTalkId(past_statue, present_statue, future_statue);
        addLevelCheck(17, 20);
        addRaceCheck(PlayerRace.kamael);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("three_goddess_statue");
        int GetMemoStateEx = st.getInt("three_goddess_statue_1");

        if (event.equalsIgnoreCase("quest_accept")) {
            st.setCond(1);
            st.setMemoState("three_goddess_statue", String.valueOf(1), true);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            htmltext = "kekrops_q0178_05.htm";
        } else if (event.equalsIgnoreCase("reply_2") && GetMemoState == 10 && st.getPlayer().getLevel() <= 20 && st.getPlayer().getPlayerClassComponent().getClassId().getId() == 1) {
            st.giveItems(scrl_of_ench_am_d, 1, true);
            st.addExpAndSp(20123, 976);
            st.soundEffect(SOUND_FINISH);
            st.exitQuest(false);
            st.removeMemo("three_goddess_statue");
            st.removeMemo("three_goddess_statue_1");
            htmltext = "kekrops_q0178_14.htm";
        } else if (event.equalsIgnoreCase("reply_4") && GetMemoState == 10 && (st.getPlayer().getLevel() > 20 || st.getPlayer().getPlayerClassComponent().getClassId().getId() != 1)) {
            st.giveItems(scrl_of_ench_am_d, 1, true);
            st.soundEffect(SOUND_FINISH);
            st.exitQuest(false);
            st.removeMemo("three_goddess_statue");
            st.removeMemo("three_goddess_statue_1");
            htmltext = "kekrops_q0178_17.htm";
        } else if (event.equalsIgnoreCase("reply_1") && GetMemoState == 1) {
            st.setMemoState("three_goddess_statue", String.valueOf(2), true);
            htmltext = "past_statue_q0178_02.htm";
        } else if (event.equalsIgnoreCase("reply_2a") && GetMemoState == 2) {
            st.setMemoState("three_goddess_statue_1", String.valueOf(0), true);
            htmltext = "past_statue_q0178_03.htm";
        } else if (event.equalsIgnoreCase("reply_3") && GetMemoState == 2) {
            st.setMemoState("three_goddess_statue_1", String.valueOf(GetMemoStateEx + 1));
            htmltext = "past_statue_q0178_04.htm";
        } else if (event.equalsIgnoreCase("reply_4a") && GetMemoState == 2) {
            st.setMemoState("three_goddess_statue_1", String.valueOf(GetMemoStateEx + 10));
            htmltext = "past_statue_q0178_05.htm";
        } else if (event.equalsIgnoreCase("reply_5") && GetMemoState == 2) {
            st.setMemoState("three_goddess_statue_1", String.valueOf(GetMemoStateEx + 100));
            htmltext = "past_statue_q0178_06.htm";
        } else if (event.equalsIgnoreCase("reply_6") && GetMemoState == 2) {
            if (GetMemoStateEx == 111) {
                st.setMemoState("three_goddess_statue", String.valueOf(3), true);
                st.setMemoState("three_goddess_statue_1", String.valueOf(0), true);
                htmltext = "past_statue_q0178_07.htm";
            } else if (GetMemoStateEx != 111)
                htmltext = "past_statue_q0178_08.htm";
        } else if (event.equalsIgnoreCase("reply_11")) {
            htmltext = HtmCache.getInstance().getHtml("quests/_178_IconicTrinity/past_statue_q0178_11.htm", st.getPlayer());
            htmltext = htmltext.replace("<?name1?>", st.getPlayer().getName());
        } else if (event.equalsIgnoreCase("reply_13") && GetMemoState == 3) {
            st.setCond(2);
            st.setMemoState("three_goddess_statue", String.valueOf(4), true);
            htmltext = HtmCache.getInstance().getHtml("quests/_178_IconicTrinity/past_statue_q0178_13.htm", st.getPlayer());
            htmltext = htmltext.replace("<?name1?>", st.getPlayer().getName());
            st.soundEffect(SOUND_MIDDLE);
        } else if (event.equalsIgnoreCase("reply_1a") && GetMemoState == 4) {
            st.setMemoState("three_goddess_statue", String.valueOf(5), true);
            htmltext = "present_statue_q0178_02.htm";
        } else if (event.equalsIgnoreCase("reply_2b") && GetMemoState == 5) {
            st.setMemoState("three_goddess_statue_1", String.valueOf(0), true);
            htmltext = "present_statue_q0178_03.htm";
        } else if (event.equalsIgnoreCase("reply_3a") && GetMemoState == 5) {
            st.setMemoState("three_goddess_statue_1", String.valueOf(GetMemoStateEx + 1));
            htmltext = "present_statue_q0178_04.htm";
        } else if (event.equalsIgnoreCase("reply_4b") && GetMemoState == 5) {
            st.setMemoState("three_goddess_statue_1", String.valueOf(GetMemoStateEx + 10));
            htmltext = "present_statue_q0178_05.htm";
        } else if (event.equalsIgnoreCase("reply_5a") && GetMemoState == 5) {
            st.setMemoState("three_goddess_statue_1", String.valueOf(GetMemoStateEx + 100));
            htmltext = "present_statue_q0178_06.htm";
        } else if (event.equalsIgnoreCase("reply_6a") && GetMemoState == 5) {
            if (GetMemoStateEx == 111) {
                st.setMemoState("three_goddess_statue", String.valueOf(6), true);
                st.setMemoState("three_goddess_statue_1", String.valueOf(0), true);
                htmltext = "present_statue_q0178_07.htm";
            } else if (GetMemoStateEx != 111)
                htmltext = "present_statue_q0178_08.htm";
        } else if (event.equalsIgnoreCase("reply_11a")) {
            htmltext = HtmCache.getInstance().getHtml("quests/_178_IconicTrinity/present_statue_q0178_11.htm", st.getPlayer());
            htmltext = htmltext.replace("<?name1?>", st.getPlayer().getName());
        } else if (event.equalsIgnoreCase("reply_12a")) {
            htmltext = HtmCache.getInstance().getHtml("quests/_178_IconicTrinity/present_statue_q0178_12.htm", st.getPlayer());
            htmltext = htmltext.replace("<?name1?>", st.getPlayer().getName());
        } else if (event.equalsIgnoreCase("reply_13a")) {
            htmltext = HtmCache.getInstance().getHtml("quests/_178_IconicTrinity/present_statue_q0178_13.htm", st.getPlayer());
            htmltext = htmltext.replace("<?name1?>", st.getPlayer().getName());
        } else if (event.equalsIgnoreCase("reply_14a") && GetMemoState == 6) {
            st.setCond(3);
            st.setMemoState("three_goddess_statue", String.valueOf(7), true);
            htmltext = HtmCache.getInstance().getHtml("quests/_178_IconicTrinity/present_statue_q0178_14.htm", st.getPlayer());
            htmltext = htmltext.replace("<?name1?>", st.getPlayer().getName());
            st.soundEffect(SOUND_MIDDLE);
        } else if (event.equalsIgnoreCase("reply_1aa") && GetMemoState == 7) {
            st.setMemoState("three_goddess_statue", String.valueOf(8), true);
            htmltext = "future_statue_q0178_02.htm";
        } else if (event.equalsIgnoreCase("reply_2aa") && GetMemoState == 8) {
            st.setMemoState("three_goddess_statue_1", String.valueOf(0), true);
            htmltext = "future_statue_q0178_03.htm";
        } else if (event.equalsIgnoreCase("reply_3aa") && GetMemoState == 8) {
            st.setMemoState("three_goddess_statue_1", String.valueOf(GetMemoStateEx + 1));
            htmltext = "future_statue_q0178_04.htm";
        } else if (event.equalsIgnoreCase("reply_4aa") && GetMemoState == 8) {
            st.setMemoState("three_goddess_statue_1", String.valueOf(GetMemoStateEx + 10));
            htmltext = "future_statue_q0178_05.htm";
        } else if (event.equalsIgnoreCase("reply_5aa") && GetMemoState == 8) {
            st.setMemoState("three_goddess_statue_1", String.valueOf(GetMemoStateEx + 100));
            htmltext = "future_statue_q0178_06.htm";
        } else if (event.equalsIgnoreCase("reply_6aa") && GetMemoState == 8) {
            st.setMemoState("three_goddess_statue_1", String.valueOf(GetMemoStateEx + 1000));
            htmltext = "future_statue_q0178_07.htm";
        } else if (event.equalsIgnoreCase("reply_7a") && GetMemoState == 8) {
            if (GetMemoStateEx == 1111) {
                st.setMemoState("three_goddess_statue", String.valueOf(9), true);
                st.setMemoState("three_goddess_statue_1", String.valueOf(0), true);
                htmltext = "future_statue_q0178_08.htm";
            } else if (GetMemoStateEx != 1111)
                htmltext = "future_statue_q0178_09.htm";
        } else if (event.equalsIgnoreCase("reply_11aa") && GetMemoState == 9) {
            st.setCond(4);
            st.setMemoState("three_goddess_statue", String.valueOf(10), true);
            htmltext = HtmCache.getInstance().getHtml("quests/_178_IconicTrinity/future_statue_q0178_12.htm", st.getPlayer());
            htmltext = htmltext.replace("<?name1?>", st.getPlayer().getName());
            st.soundEffect(SOUND_MIDDLE);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("three_goddess_statue");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == kekrops) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "kekrops_q0178_02.htm";
                            break;
                        case RACE:
                            htmltext = "kekrops_q0178_03.htm";
                            break;
                        default:
                            htmltext = "kekrops_q0178_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == kekrops) {
                    if (GetMemoState == 1 || GetMemoState == 2)
                        htmltext = "kekrops_q0178_06.htm";
                    else if (GetMemoState == 3)
                        htmltext = "kekrops_q0178_07.htm";
                    else if (GetMemoState == 4 || GetMemoState == 5)
                        htmltext = "kekrops_q0178_08.htm";
                    else if (GetMemoState == 6)
                        htmltext = "kekrops_q0178_09.htm";
                    else if (GetMemoState == 7 || GetMemoState == 8)
                        htmltext = "kekrops_q0178_10.htm";
                    else if (GetMemoState == 9)
                        htmltext = "kekrops_q0178_11.htm";
                    else if (GetMemoState == 10)
                        if (st.getPlayer().getLevel() <= 20 && st.getPlayer().getPlayerClassComponent().getClassId().getId() == 1)
                            htmltext = "kekrops_q0178_12.htm";
                        else
                            htmltext = "kekrops_q0178_15.htm";
                } else if (npcId == past_statue) {
                    if (GetMemoState == 1)
                        htmltext = "past_statue_q0178_01.htm";
                    else if (GetMemoState == 4 || GetMemoState == 5)
                        htmltext = "past_statue_q0178_14.htm";
                } else if (npcId == present_statue) {
                    if (GetMemoState == 4)
                        htmltext = "present_statue_q0178_01.htm";
                    else if (GetMemoState == 7 || GetMemoState == 8)
                        htmltext = "present_statue_q0178_15.htm";
                } else if (npcId == future_statue)
                    if (GetMemoState == 7)
                        htmltext = "future_statue_q0178_01.htm";
                    else if (GetMemoState == 10)
                        htmltext = "future_statue_q0178_13.htm";
                break;
        }
        return htmltext;
    }
}