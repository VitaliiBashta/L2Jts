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
public class _354_ConquestofAlligatorIsland extends Quest {
    // npc
    private static final int warehouse_keeper_kluck = 30895;

    //mobs
    private static final int crokian_lad = 20804;
    private static final int dailaon_lad = 20805;
    private static final int crokian_lad_warrior = 20806;
    private static final int farhite_lad = 20807;
    private static final int nos_lad = 20808;
    private static final int tribe_of_swamp = 20991;

    // questitem
    private static final int croc_tooth = 5863;
    private static final int mysterious_map_piece = 5864;

    // etcitem
    private static final int pirates_treasure_map = 5915;

    public _354_ConquestofAlligatorIsland() {
        super(false);
        addStartNpc(warehouse_keeper_kluck);
        addKillId(crokian_lad, dailaon_lad, crokian_lad_warrior, farhite_lad, nos_lad, tribe_of_swamp);
        addQuestItem(croc_tooth, mysterious_map_piece);
        addLevelCheck(38, 49);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();

        if (npcId == warehouse_keeper_kluck)
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "warehouse_keeper_kluck_q0354_03.htm";
            } else if (event.equalsIgnoreCase("reply_1")) {
                if (st.ownItemCount(croc_tooth) >= 100) {
                    st.giveItems(ADENA_ID, st.ownItemCount(croc_tooth) * 220 + 10700);
                    st.takeItems(croc_tooth, -1);
                    htmltext = "warehouse_keeper_kluck_q0354_06b.htm";
                } else if (st.ownItemCount(croc_tooth) > 0 && st.ownItemCount(croc_tooth) < 100) {
                    st.giveItems(ADENA_ID, st.ownItemCount(croc_tooth) * 220 + 3100);
                    st.takeItems(croc_tooth, st.ownItemCount(croc_tooth));
                    htmltext = "warehouse_keeper_kluck_q0354_06a.htm";
                } else if (st.ownItemCount(croc_tooth) == 0)
                    htmltext = "warehouse_keeper_kluck_q0354_06.htm";
            } else if (event.equalsIgnoreCase("reply_2"))
                htmltext = "warehouse_keeper_kluck_q0354_07.htm";
            else if (event.equalsIgnoreCase("reply_3")) {
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                htmltext = "warehouse_keeper_kluck_q0354_08.htm";
            } else if (event.equalsIgnoreCase("reply_4") && st.ownItemCount(mysterious_map_piece) > 0 && st.ownItemCount(mysterious_map_piece) < 10)
                htmltext = "warehouse_keeper_kluck_q0354_09.htm";
            else if (event.equalsIgnoreCase("reply_4") && st.ownItemCount(mysterious_map_piece) >= 10) {
                st.giveItems(pirates_treasure_map, 1);
                st.takeItems(mysterious_map_piece, 10);
                htmltext = "warehouse_keeper_kluck_q0354_10.htm";
            }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == warehouse_keeper_kluck) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "warehouse_keeper_kluck_q0354_01.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "warehouse_keeper_kluck_q0354_02.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == warehouse_keeper_kluck)
                    if (st.ownItemCount(mysterious_map_piece) == 0)
                        htmltext = "warehouse_keeper_kluck_q0354_04.htm";
                    else if (st.ownItemCount(mysterious_map_piece) >= 1)
                        htmltext = "warehouse_keeper_kluck_q0354_05.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();

        if (npcId == crokian_lad) {
            if (Rnd.get(100) < 84) {
                st.giveItems(croc_tooth, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
            if (Rnd.get(10) == 5)
                st.giveItems(mysterious_map_piece, 1);
        } else if (npcId == dailaon_lad) {
            if (Rnd.get(100) < 91) {
                st.giveItems(croc_tooth, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
            if (Rnd.get(10) == 5)
                st.giveItems(mysterious_map_piece, 1);
        } else if (npcId == crokian_lad_warrior) {
            if (Rnd.get(100) < 88) {
                st.giveItems(croc_tooth, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
            if (Rnd.get(10) == 5)
                st.giveItems(mysterious_map_piece, 1);
        } else if (npcId == farhite_lad) {
            if (Rnd.get(100) < 92) {
                st.giveItems(croc_tooth, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
            if (Rnd.get(10) == 5)
                st.giveItems(mysterious_map_piece, 1);
        } else if (npcId == nos_lad) {
            if (Rnd.get(100) < 14) {
                st.giveItems(croc_tooth, 2);
                st.soundEffect(SOUND_ITEMGET);
            } else {
                st.giveItems(croc_tooth, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
            if (Rnd.get(10) == 5)
                st.giveItems(mysterious_map_piece, 1);
        } else if (npcId == tribe_of_swamp) {
            if (Rnd.get(100) < 69) {
                st.giveItems(croc_tooth, 2);
                st.soundEffect(SOUND_ITEMGET);
            } else {
                st.giveItems(croc_tooth, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
            if (Rnd.get(10) == 5)
                st.giveItems(mysterious_map_piece, 1);
        }
        return null;
    }
}