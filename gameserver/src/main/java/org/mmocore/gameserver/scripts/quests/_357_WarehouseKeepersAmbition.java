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
public class _357_WarehouseKeepersAmbition extends Quest {
    // npc
    private static final int warehouse_keeper_silva = 30686;
    // Mobs
    private static final int forest_runner = 20594;
    private static final int fline_elder = 20595;
    private static final int liele_elder = 20596;
    private static final int ti_mi_kran_elder = 20597;
    // questitem
    private static final int jade_crystal = 5867;

    public _357_WarehouseKeepersAmbition() {
        super(true);
        addStartNpc(warehouse_keeper_silva);
        addKillId(forest_runner, fline_elder, liele_elder, ti_mi_kran_elder);
        addQuestItem(jade_crystal);
        addLevelCheck(47, 57);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == warehouse_keeper_silva) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "warehouse_keeper_silva_q0357_05.htm";
            } else if (event.equalsIgnoreCase("reply_1"))
                htmltext = "warehouse_keeper_silva_q0357_03.htm";
            else if (event.equalsIgnoreCase("reply_2"))
                htmltext = "warehouse_keeper_silva_q0357_04.htm";
            else if (event.equalsIgnoreCase("reply_3")) {
                if (st.ownItemCount(jade_crystal) < 100 && st.ownItemCount(jade_crystal) > 0) {
                    st.giveItems(ADENA_ID, st.ownItemCount(jade_crystal) * 425 + 13500);
                    st.takeItems(jade_crystal, -1);
                    htmltext = "warehouse_keeper_silva_q0357_08.htm";
                } else if (st.ownItemCount(jade_crystal) >= 100) {
                    st.giveItems(ADENA_ID, st.ownItemCount(jade_crystal) * 425 + 40500);
                    st.takeItems(jade_crystal, -1);
                    htmltext = "warehouse_keeper_silva_q0357_09.htm";
                }
            } else if (event.equalsIgnoreCase("reply_4"))
                htmltext = "warehouse_keeper_silva_q0357_10.htm";
            else if (event.equalsIgnoreCase("reply_5")) {
                if (st.ownItemCount(jade_crystal) < 100 && st.ownItemCount(jade_crystal) > 0) {
                    st.giveItems(ADENA_ID, st.ownItemCount(jade_crystal) * 425);
                } else if (st.ownItemCount(jade_crystal) >= 100) {
                    st.giveItems(ADENA_ID, st.ownItemCount(jade_crystal) * 425 + 40500);
                }
                st.takeItems(jade_crystal, -1);
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                htmltext = "warehouse_keeper_silva_q0357_11.htm";
            }
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
                if (npcId == warehouse_keeper_silva) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "warehouse_keeper_silva_q0357_01.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "warehouse_keeper_silva_q0357_02.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == warehouse_keeper_silva) {
                    if (st.ownItemCount(jade_crystal) < 1)
                        htmltext = "warehouse_keeper_silva_q0357_06.htm";
                    else if (st.ownItemCount(jade_crystal) >= 1)
                        htmltext = "warehouse_keeper_silva_q0357_07.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == forest_runner) {
            if (Rnd.get(1000) < 577) {
                st.giveItems(jade_crystal, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == fline_elder) {
            if (Rnd.get(100) < 60) {
                st.giveItems(jade_crystal, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == liele_elder) {
            if (Rnd.get(1000) < 638) {
                st.giveItems(jade_crystal, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == ti_mi_kran_elder) {
            if (Rnd.get(1000) < 62) {
                st.giveItems(jade_crystal, 2);
                st.soundEffect(SOUND_ITEMGET);
            } else {
                st.giveItems(jade_crystal, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }
}