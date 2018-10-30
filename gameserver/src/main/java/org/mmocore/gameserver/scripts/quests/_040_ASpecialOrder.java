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
public class _040_ASpecialOrder extends Quest {
    // NPC
    private static final int helvetia = 30081;
    private static final int fisher_ofulle = 31572;
    private static final int warehouse_chief_gesto = 30511;

    // ITEMS
    private static final int orange_wide_fish = 6452;
    private static final int orange_swift_fish = 6450;
    private static final int orange_ugly_fish = 6451;
    private static final int golden_cobol = 5079;
    private static final int bur_cobol = 5082;
    private static final int great_cobol = 5084;
    private static final int cube_event = 10632;

    // QUEST ITEMS
    private static final int q_box_of_fish = 12764;
    private static final int q_box_of_seed = 12765;

    public _040_ASpecialOrder() {
        super(false);
        addStartNpc(helvetia);
        addTalkId(fisher_ofulle, warehouse_chief_gesto);
        addQuestItem(q_box_of_fish, q_box_of_seed);
        addLevelCheck(40);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("special_order");
        int rand = Rnd.get(1, 2);

        if (event.equals("quest_accept")) {
            if (rand == 1) {
                st.setCond(2);
                st.setMemoState("special_order", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "helvetia_q0040_03.htm";
            } else {
                st.setCond(5);
                st.setMemoState("special_order", String.valueOf(10), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "helvetia_q0040_04.htm";
            }
        } else if (event.equals("reply_1") && GetMemoState == 3) {
            st.giveItems(cube_event, 1);
            st.takeItems(q_box_of_fish, -1);
            htmltext = "helvetia_q0040_07.htm";
            st.removeMemo("special_order");
            st.soundEffect(SOUND_FINISH);
            st.exitQuest(false);
        } else if (event.equals("reply_2") && GetMemoState == 12) {
            st.giveItems(cube_event, 1);
            st.takeItems(q_box_of_seed, -1);
            htmltext = "helvetia_q0040_10.htm";
            st.removeMemo("special_order");
            st.soundEffect(SOUND_FINISH);
            st.exitQuest(false);
        } else if (event.equals("reply_2a") && GetMemoState == 1) {
            st.setCond(3);
            st.setMemoState("special_order", String.valueOf(2), true);
            htmltext = "fisher_ofulle_q0040_03.htm";
            st.soundEffect(SOUND_MIDDLE);
        } else if (event.equals("reply_2b") && GetMemoState == 10) {
            st.setCond(6);
            st.setMemoState("special_order", String.valueOf(11), true);
            htmltext = "warehouse_chief_gesto_q0040_03.htm";
            st.soundEffect(SOUND_MIDDLE);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int GetMemoState = st.getInt("special_order");
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == helvetia) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "helvetia_q0040_02.htm";
                            break;
                        default:
                            htmltext = "helvetia_q0040_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == helvetia) {
                    if (GetMemoState <= 2)
                        htmltext = "helvetia_q0040_05.htm";
                    else if (GetMemoState == 3)
                        htmltext = "helvetia_q0040_06.htm";
                    else if (GetMemoState >= 10 && GetMemoState <= 11)
                        htmltext = "helvetia_q0040_08.htm";
                    else if (GetMemoState == 12)
                        htmltext = "helvetia_q0040_09.htm";
                } else if (npcId == fisher_ofulle) {
                    if (GetMemoState == 1)
                        htmltext = "fisher_ofulle_q0040_01.htm";
                    else if (GetMemoState == 2) {
                        if (st.ownItemCount(orange_swift_fish) >= 10 && st.ownItemCount(orange_ugly_fish) >= 10 && st.ownItemCount(orange_wide_fish) >= 10) {
                            st.setCond(4);
                            st.setMemoState("special_order", String.valueOf(3), true);
                            st.giveItems(q_box_of_fish, 1);
                            st.takeItems(orange_swift_fish, 10);
                            st.takeItems(orange_ugly_fish, 10);
                            st.takeItems(orange_wide_fish, 10);
                            htmltext = "fisher_ofulle_q0040_05.htm";
                            st.soundEffect(SOUND_MIDDLE);
                        } else
                            htmltext = "fisher_ofulle_q0040_04.htm";
                    } else if (GetMemoState == 3)
                        htmltext = "fisher_ofulle_q0040_06.htm";
                } else if (npcId == warehouse_chief_gesto)
                    if (GetMemoState == 10)
                        htmltext = "warehouse_chief_gesto_q0040_01.htm";
                    else if (GetMemoState == 11) {
                        if (st.ownItemCount(golden_cobol) >= 10 && st.ownItemCount(bur_cobol) >= 10 && st.ownItemCount(great_cobol) >= 10) {
                            st.setCond(7);
                            st.setMemoState("special_order", String.valueOf(12), true);
                            st.giveItems(q_box_of_seed, 1);
                            st.takeItems(golden_cobol, 40);
                            st.takeItems(bur_cobol, 40);
                            st.takeItems(great_cobol, 40);
                            htmltext = "warehouse_chief_gesto_q0040_05.htm";
                            st.soundEffect(SOUND_MIDDLE);
                        } else
                            htmltext = "warehouse_chief_gesto_q0040_04.htm";
                    } else if (GetMemoState == 12)
                        htmltext = "warehouse_chief_gesto_q0040_06.htm";
                break;
        }
        return htmltext;
    }
}