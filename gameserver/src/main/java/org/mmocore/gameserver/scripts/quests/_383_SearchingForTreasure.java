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
public class _383_SearchingForTreasure extends Quest {
    // npc
    private static final int trader_espen = 30890;
    private static final int pirates_t_chest = 31148;

    // questitem
    private static final int pirates_treasure_map = 5915;
    private static final int key_of_thief = 1661;

    // etcitem
    private static final int elven_mithril_gloves = 2450;
    private static final int sages_worn_gloves = 2451;
    private static final int scrl_of_ench_am_d = 956;
    private static final int scrl_of_ench_am_c = 952;
    private static final int dye_s1c3_c = 4481;
    private static final int dye_s1d3_c = 4482;
    private static final int dye_c1s3_c = 4483;
    private static final int dye_c1c3_c = 4484;
    private static final int dye_d1s3_c = 4485;
    private static final int dye_d1c3_c = 4486;
    private static final int dye_i1m3_c = 4487;
    private static final int dye_i1w3_c = 4488;
    private static final int dye_m1i3_c = 4489;
    private static final int dye_m1w3_c = 4490;
    private static final int dye_w1i3_c = 4491;
    private static final int dye_w1m3_c = 4492;
    private static final int emerald = 1337;
    private static final int blue_onyx = 1338;
    private static final int onyx = 1339;
    private static final int q_loot_4 = 3447;
    private static final int q_loot_7 = 3450;
    private static final int q_loot_10 = 3453;
    private static final int q_loot_13 = 3456;
    private static final int q_musicnote_love = 4408;
    private static final int q_musicnote_battle = 4409;
    private static final int q_musicnote_celebration = 4418;
    private static final int q_musicnote_comedy = 4419;


    public _383_SearchingForTreasure() {
        super(false);
        addStartNpc(trader_espen);
        addTalkId(pirates_t_chest);
        addQuestItem(pirates_treasure_map);
        addLevelCheck(42, 50);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("treasure_hunt");
        int npcId = npc.getNpcId();

        if (npcId == trader_espen) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("treasure_hunt", String.valueOf(1), true);
                st.takeItems(pirates_treasure_map, 1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "trader_espen_q0383_08.htm";
            } else if (event.equalsIgnoreCase("reply_1"))
                htmltext = "trader_espen_q0383_04.htm";
            else if (event.equalsIgnoreCase("reply_2") && st.ownItemCount(pirates_treasure_map) > 0) {
                st.giveItems(ADENA_ID, 1000);
                st.removeMemo("treasure_hunt");
                st.takeItems(pirates_treasure_map, 1);
                htmltext = "trader_espen_q0383_05.htm";
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (st.ownItemCount(pirates_treasure_map) > 0)
                    htmltext = "trader_espen_q0383_06.htm";
                else
                    htmltext = "trader_espen_q0383_07.htm";
            } else if (event.equalsIgnoreCase("reply_4"))
                htmltext = "trader_espen_q0383_09.htm";
            else if (event.equalsIgnoreCase("reply_5"))
                htmltext = "trader_espen_q0383_10.htm";
            else if (event.equalsIgnoreCase("reply_6"))
                htmltext = "trader_espen_q0383_11.htm";
            else if (event.equalsIgnoreCase("reply_7"))
                if (GetMemoState == 1) {
                    st.setCond(2);
                    st.setMemoState("treasure_hunt", String.valueOf(2), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "trader_espen_q0383_12.htm";
                }
        } else if (npcId == pirates_t_chest)
            if (event.equalsIgnoreCase("reply_1"))
                if (st.ownItemCount(key_of_thief) == 0)
                    htmltext = "pirates_t_chest_q0383_02.htm";
                else if (GetMemoState == 2 && st.ownItemCount(key_of_thief) >= 1) {
                    st.takeItems(key_of_thief, 1);
                    st.removeMemo("treasure_hunt");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(true);
                    htmltext = "pirates_t_chest_q0383_03.htm";
                    int i1 = 0;
                    int i0 = Rnd.get(100);
                    if (i0 < 5)
                        st.giveItems(elven_mithril_gloves, 1);
                    else if (i0 < 6)
                        st.giveItems(sages_worn_gloves, 1);
                    else if (i0 < 18)
                        st.giveItems(scrl_of_ench_am_d, 1);
                    else if (i0 < 28)
                        st.giveItems(scrl_of_ench_am_c, 1);
                    else
                        i1 = i1 + 500;
                    int i2 = Rnd.get(1000);
                    if (i2 < 25)
                        st.giveItems(dye_s1c3_c, 1);
                    else if (i2 < 50)
                        st.giveItems(dye_s1d3_c, 1);
                    else if (i2 < 75)
                        st.giveItems(dye_c1s3_c, 1);
                    else if (i2 < 100)
                        st.giveItems(dye_c1c3_c, 1);
                    else if (i2 < 125)
                        st.giveItems(dye_d1s3_c, 1);
                    else if (i2 < 150)
                        st.giveItems(dye_d1c3_c, 1);
                    else if (i2 < 175)
                        st.giveItems(dye_i1m3_c, 1);
                    else if (i2 < 200)
                        st.giveItems(dye_i1w3_c, 1);
                    else if (i2 < 225)
                        st.giveItems(dye_m1i3_c, 1);
                    else if (i2 < 250)
                        st.giveItems(dye_m1w3_c, 1);
                    else if (i2 < 275)
                        st.giveItems(dye_w1i3_c, 1);
                    else if (i2 < 300)
                        st.giveItems(dye_w1m3_c, 1);
                    else
                        i1 = i1 + 300;
                    int i3 = Rnd.get(100);
                    if (i3 < 4)
                        st.giveItems(emerald, 1);
                    else if (i3 < 8)
                        st.giveItems(blue_onyx, 2);
                    else if (i3 < 12)
                        st.giveItems(onyx, 2);
                    else if (i3 < 16)
                        st.giveItems(q_loot_4, 2);
                    else if (i3 < 20)
                        st.giveItems(q_loot_7, 1);
                    else if (i3 < 25)
                        st.giveItems(q_loot_10, 1);
                    else if (i3 < 27)
                        st.giveItems(q_loot_13, 1);
                    else
                        i1 = i1 + 500;
                    int i4 = Rnd.get(100);
                    if (i4 < 20)
                        st.giveItems(q_musicnote_love, 1);
                    else if (i4 < 40)
                        st.giveItems(q_musicnote_battle, 1);
                    else if (i4 < 60)
                        st.giveItems(q_musicnote_celebration, 1);
                    else if (i4 < 80)
                        st.giveItems(q_musicnote_comedy, 1);
                    else
                        i1 = i1 + 500;
                    st.giveItems(ADENA_ID, i1);
                }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("treasure_hunt");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == trader_espen) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "trader_espen_q0383_01.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            if (st.ownItemCount(pirates_treasure_map) == 0) {
                                htmltext = "trader_espen_q0383_02.htm";
                                st.exitQuest(true);
                            } else if (st.ownItemCount(pirates_treasure_map) > 0)
                                htmltext = "trader_espen_q0383_03.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == trader_espen) {
                    if (GetMemoState == 1)
                        htmltext = "trader_espen_q0383_13.htm";
                    else if (GetMemoState == 2)
                        htmltext = "trader_espen_q0383_14.htm";
                } else if (npcId == pirates_t_chest)
                    if (GetMemoState == 2)
                        htmltext = "pirates_t_chest_q0383_01.htm";
                break;
        }
        return htmltext;
    }
}