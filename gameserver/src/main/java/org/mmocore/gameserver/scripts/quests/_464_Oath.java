package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author KilRoy
 * @date 18/12/2014
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _464_Oath extends Quest {
    // npc
    private static final int director_sophia = 32596;
    private static final int cardinal_seresin = 30657;
    private static final int trader_holly = 30839;
    private static final int gatekeeper_flauen = 30899;
    private static final int falsepriest_dominic = 31350;
    private static final int chichirin = 30539;
    private static final int master_tobias = 30297;
    private static final int blacksmith_buryun = 31960;
    private static final int saint_agnes = 31588;
    // questitem
    private static final int q_book_of_silence_1 = 15538;
    private static final int q_book_of_silence_2 = 15539;
    // etcitem
    private static final int strongbox_of_promise = 15537;

    public _464_Oath() {
        super(0);
        addTalkId(director_sophia, cardinal_seresin, trader_holly, gatekeeper_flauen, falsepriest_dominic, chichirin, master_tobias, blacksmith_buryun, saint_agnes);
        addQuestItem(q_book_of_silence_1, q_book_of_silence_2);
        addLevelCheck(82);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equalsIgnoreCase("quest_accept")) {
            final long count = ItemFunctions.getItemCount(st.getPlayer(), strongbox_of_promise);
            if (count > 0) {
                switch (this.isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = "strongbox_of_promise_q0464_02.htm";
                        break;
                    default:
                        st.setCond(1);
                        st.setMemoState("promise", String.valueOf(1), true);
                        st.setState(STARTED);
                        st.soundEffect(SOUND_ACCEPT);
                        st.takeAllItems(strongbox_of_promise);
                        st.giveItems(q_book_of_silence_1, 1);
                        htmltext = "strongbox_of_promise_q0464_03.htm";
                        break;
                }
            } else {
                htmltext = "strongbox_of_promise_q0464_01.htm";
            }
        }
        if (event.equalsIgnoreCase("reply_3")) {
            switch (Rnd.get(2, 9)) {
                case 2:
                    st.setCond(2);
                    st.setMemoState("promise", String.valueOf(2), true);
                    htmltext = "director_sophia_q0464_04.htm";
                    break;
                case 3:
                    st.setCond(3);
                    st.setMemoState("promise", String.valueOf(3), true);
                    htmltext = "director_sophia_q0464_04a.htm";
                    break;
                case 4:
                    st.setCond(4);
                    st.setMemoState("promise", String.valueOf(4), true);
                    htmltext = "director_sophia_q0464_04b.htm";
                    break;
                case 5:
                    st.setCond(5);
                    st.setMemoState("promise", String.valueOf(5), true);
                    htmltext = "director_sophia_q0464_04c.htm";
                    break;
                case 6:
                    st.setCond(6);
                    st.setMemoState("promise", String.valueOf(6), true);
                    htmltext = "director_sophia_q0464_04d.htm";
                    break;
                case 7:
                    st.setCond(7);
                    st.setMemoState("promise", String.valueOf(7), true);
                    htmltext = "director_sophia_q0464_04e.htm";
                    break;
                case 8:
                    st.setCond(8);
                    st.setMemoState("promise", String.valueOf(8), true);
                    htmltext = "director_sophia_q0464_04f.htm";
                    break;
                case 9:
                    st.setCond(9);
                    st.setMemoState("promise", String.valueOf(9), true);
                    htmltext = "director_sophia_q0464_04g.htm";
                    break;
            }
            st.takeAllItems(q_book_of_silence_1);
            st.giveItems(q_book_of_silence_2, 1);
            st.soundEffect(SOUND_MIDDLE);
        } else if (event.equalsIgnoreCase("reply_1")) {
            switch (npc.getNpcId()) {
                case cardinal_seresin:
                    st.giveItems(ADENA_ID, 42910);
                    st.addExpAndSp(15449, 17696);
                    htmltext = "cardinal_seresin_q0464_02.htm";
                    break;
                case trader_holly:
                    st.giveItems(ADENA_ID, 52599);
                    st.addExpAndSp(189377, 21692);
                    htmltext = "trader_holly_q0464_02.htm";
                    break;
                case gatekeeper_flauen:
                    st.giveItems(ADENA_ID, 69210);
                    st.addExpAndSp(249180, 28542);
                    htmltext = "gatekeeper_flauen_q0464_02.htm";
                    break;
                case falsepriest_dominic:
                    st.giveItems(ADENA_ID, 69210);
                    st.addExpAndSp(249180, 28542);
                    htmltext = "falsepriest_dominic_q0464_02.htm";
                    break;
                case chichirin:
                    st.giveItems(ADENA_ID, 169442);
                    st.addExpAndSp(19408, 47062);
                    htmltext = "chichirin_q0464_02.htm";
                    break;
                case master_tobias:
                    st.giveItems(ADENA_ID, 210806);
                    st.addExpAndSp(24146, 58551);
                    htmltext = "master_tobias_q0464_02.htm";
                    break;
                case blacksmith_buryun:
                    st.giveItems(ADENA_ID, 42910);
                    st.addExpAndSp(15449, 17696);
                    htmltext = "blacksmith_buryun_q0464_02.htm";
                    break;
                case saint_agnes:
                    st.giveItems(ADENA_ID, 42910);
                    st.addExpAndSp(15449, 17696);
                    htmltext = "saint_agnes_q0464_02.htm";
                    break;
            }
            st.removeMemo("promise");
            st.takeAllItems(q_book_of_silence_2);
            st.soundEffect(SOUND_FINISH);
            st.exitQuest(true);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("promise");
        int npcId = npc.getNpcId();
        if (npcId == director_sophia) {
            switch (GetMemoState) {
                case 1:
                    htmltext = "director_sophia_q0464_01.htm";
                    break;
                case 2:
                    htmltext = "director_sophia_q0464_05.htm";
                    break;
                case 3:
                    htmltext = "director_sophia_q0464_05a.htm";
                    break;
                case 4:
                    htmltext = "director_sophia_q0464_05b.htm";
                    break;
                case 5:
                    htmltext = "director_sophia_q0464_05c.htm";
                    break;
                case 6:
                    htmltext = "director_sophia_q0464_05d.htm";
                    break;
                case 7:
                    htmltext = "director_sophia_q0464_05e.htm";
                    break;
                case 8:
                    htmltext = "director_sophia_q0464_05f.htm";
                    break;
                case 9:
                    htmltext = "director_sophia_q0464_05g.htm";
                    break;
            }
        } else if (npcId == cardinal_seresin) {
            if (GetMemoState == 2) {
                htmltext = "cardinal_seresin_q0464_01.htm";
            }
        } else if (npcId == trader_holly) {
            if (GetMemoState == 3) {
                htmltext = "trader_holly_q0464_01.htm";
            }
        } else if (npcId == gatekeeper_flauen) {
            if (GetMemoState == 4) {
                htmltext = "gatekeeper_flauen_q0464_01.htm";
            }
        } else if (npcId == falsepriest_dominic) {
            if (GetMemoState == 5) {
                htmltext = "falsepriest_dominic_q0464_01.htm";
            }
        } else if (npcId == chichirin) {
            if (GetMemoState == 6) {
                htmltext = "chichirin_q0464_01.htm";
            }
        } else if (npcId == master_tobias) {
            if (GetMemoState == 7) {
                htmltext = "master_tobias_q0464_01.htm";
            }
        } else if (npcId == blacksmith_buryun) {
            if (GetMemoState == 8) {
                htmltext = "blacksmith_buryun_q0464_01.htm";
            }
        } else if (npcId == saint_agnes) {
            if (GetMemoState == 9) {
                htmltext = "saint_agnes_q0464_01.htm";
            }
        }
        return htmltext;
    }
}