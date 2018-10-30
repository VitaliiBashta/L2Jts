package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.base.Element;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.object.Inventory;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _10275_ContainingTheAttributePower extends Quest {
    // npc
    private final static int trader_holly = 30839;
    private final static int trader_weber = 31307;
    private final static int attribute_manager1 = 32325;
    private final static int attribute_manager2 = 32326;

    // mobs
    private final static int insane_elemental_water = 27380;
    private final static int insane_elemental_wind = 27381;

    // questitem
    private final static int q_Attribute_manager_weapon1 = 13845;
    private final static int q_Attribute_manager_weapon2 = 13881;
    private final static int q_elemental_piece_water = 13861;
    private final static int q_elemental_piece_wind = 13862;

    // etcitem
    private final static int q_ore_of_fire = 10521;
    private final static int q_ore_of_water = 10522;
    private final static int q_ore_of_earth = 10523;
    private final static int q_ore_of_wind = 10524;
    private final static int q_ore_of_unholy = 10525;
    private final static int q_ore_of_holy = 10526;

    // skill_name
    private final static int s_item_attribute_blessing_of_fire1 = 2635;
    private final static int s_item_attribute_blessing_of_earth1 = 2636;

    public _10275_ContainingTheAttributePower() {
        super(false);
        addStartNpc(trader_holly, trader_weber);
        addTalkId(attribute_manager1, attribute_manager2);
        addKillId(insane_elemental_wind, insane_elemental_water);
        addQuestItem(q_Attribute_manager_weapon1, q_Attribute_manager_weapon2, q_elemental_piece_water, q_elemental_piece_wind);
        addLevelCheck(76);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("power_of_the_high_ore");
        int GetMemoStateEx = st.getInt("power_of_the_high_ore_1");

        if (event.equalsIgnoreCase("quest_accept_holly") && GetMemoStateEx != 2) {
            st.setCond(1);
            st.setMemoState("power_of_the_high_ore", String.valueOf(1), true);
            st.setMemoState("power_of_the_high_ore_1", String.valueOf(1), true);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            htmltext = "trader_holly_q10275_01a.htm";
        } else if (event.equalsIgnoreCase("reply_2") && GetMemoState == 1 && GetMemoStateEx == 1) {
            st.setCond(2);
            st.setMemoState("power_of_the_high_ore", String.valueOf(2), true);
            st.soundEffect(SOUND_MIDDLE);
            htmltext = "trader_holly_q10275_06.htm";
        }
        if (event.equalsIgnoreCase("quest_accept_weber") && GetMemoStateEx != 1) {
            st.setCond(1);
            st.setMemoState("power_of_the_high_ore", String.valueOf(7), true);
            st.setMemoState("power_of_the_high_ore_1", String.valueOf(2), true);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            htmltext = "trader_weber_q10275_01a.htm";
        } else if (event.equalsIgnoreCase("reply_2a") && GetMemoState == 7 && GetMemoStateEx == 2) {
            st.setCond(7);
            st.setMemoState("power_of_the_high_ore", String.valueOf(8), true);
            st.soundEffect(SOUND_MIDDLE);
            htmltext = "trader_weber_q10275_06.htm";
        } else if (event.equalsIgnoreCase("reply_2b") && GetMemoState == 2 && GetMemoStateEx == 1) {
            st.setCond(3);
            st.setMemoState("power_of_the_high_ore", String.valueOf(3), true);
            st.giveItems(q_Attribute_manager_weapon1, 1, Element.FIRE, 10);
            st.soundEffect(SOUND_MIDDLE);
            htmltext = "attribute_manager1_q10275_04.htm";
        } else if (event.equalsIgnoreCase("reply_4") && GetMemoState == 3 && GetMemoStateEx == 1) {
            if (st.ownItemCount(q_elemental_piece_water) < 6 && st.ownItemCount(q_Attribute_manager_weapon1) == 0) {
                st.giveItems(q_Attribute_manager_weapon1, 1, Element.FIRE, 10);
                htmltext = "attribute_manager1_q10275_07.htm";
            } else if (st.ownItemCount(q_elemental_piece_water) < 6 && st.ownItemCount(q_Attribute_manager_weapon1) == 1) {
                st.takeItems(q_Attribute_manager_weapon1, -1);
                st.giveItems(q_Attribute_manager_weapon1, 1, Element.FIRE, 10);
                htmltext = "attribute_manager1_q10275_08.htm";
            }
        } else if (event.equalsIgnoreCase("reply_5") && GetMemoState == 4 && GetMemoStateEx == 1) {
            st.setCond(5);
            st.setMemoState("power_of_the_high_ore", String.valueOf(5), true);
            SkillTable.getInstance().getSkillEntry(s_item_attribute_blessing_of_fire1, 1).getEffects(st.getPlayer(), st.getPlayer(), false, false);
            st.giveItems(q_Attribute_manager_weapon1, 1, Element.FIRE, 10);
            st.takeItems(q_elemental_piece_water, -1);
            st.soundEffect(SOUND_MIDDLE);
            htmltext = "attribute_manager1_q10275_10.htm";
        } else if (event.equalsIgnoreCase("reply_7") && GetMemoState == 5 && GetMemoStateEx == 1) {
            if (st.ownItemCount(q_elemental_piece_water) < 6 && st.ownItemCount(q_Attribute_manager_weapon1) == 0) {
                st.giveItems(q_Attribute_manager_weapon1, 1, Element.FIRE, 10);
                SkillTable.getInstance().getSkillEntry(s_item_attribute_blessing_of_fire1, 1).getEffects(st.getPlayer(), st.getPlayer(), false, false);
                htmltext = "attribute_manager1_q10275_13.htm";
            } else if (st.ownItemCount(q_elemental_piece_water) < 6 && st.ownItemCount(q_Attribute_manager_weapon1) >= 1) {
                st.takeItems(q_Attribute_manager_weapon1, -1);
                st.giveItems(q_Attribute_manager_weapon1, 1, Element.FIRE, 10);
                SkillTable.getInstance().getSkillEntry(s_item_attribute_blessing_of_fire1, 1).getEffects(st.getPlayer(), st.getPlayer(), false, false);
                htmltext = "attribute_manager1_q10275_14.htm";
            }
        } else if (event.equalsIgnoreCase("reply_11") && GetMemoState == 6 && GetMemoStateEx == 1) {
            st.giveItems(q_ore_of_holy, 2);
            st.takeItems(q_Attribute_manager_weapon1, -1);
            st.takeItems(q_elemental_piece_water, -1);
            st.addExpAndSp(202160, 20375);
            st.exitQuest(false);
            st.soundEffect(SOUND_FINISH);
            st.removeMemo("power_of_the_high_ore");
            st.removeMemo("power_of_the_high_ore_1");
            htmltext = "attribute_manager1_q10275_16.htm";
        } else if (event.equalsIgnoreCase("reply_12") && GetMemoState == 6 && GetMemoStateEx == 1) {
            st.giveItems(q_ore_of_unholy, 2);
            st.takeItems(q_Attribute_manager_weapon1, -1);
            st.takeItems(q_elemental_piece_water, -1);
            st.addExpAndSp(202160, 20375);
            st.exitQuest(false);
            st.soundEffect(SOUND_FINISH);
            st.removeMemo("power_of_the_high_ore");
            st.removeMemo("power_of_the_high_ore_1");
            htmltext = "attribute_manager1_q10275_17.htm";
        } else if (event.equalsIgnoreCase("reply_13") && GetMemoState == 6 && GetMemoStateEx == 1) {
            st.giveItems(q_ore_of_fire, 2);
            st.takeItems(q_Attribute_manager_weapon1, -1);
            st.takeItems(q_elemental_piece_water, -1);
            st.addExpAndSp(202160, 20375);
            st.exitQuest(false);
            st.soundEffect(SOUND_FINISH);
            st.removeMemo("power_of_the_high_ore");
            st.removeMemo("power_of_the_high_ore_1");
            htmltext = "attribute_manager1_q10275_18.htm";
        } else if (event.equalsIgnoreCase("reply_14") && GetMemoState == 6 && GetMemoStateEx == 1) {
            st.giveItems(q_ore_of_water, 2);
            st.takeItems(q_Attribute_manager_weapon1, -1);
            st.takeItems(q_elemental_piece_water, -1);
            st.addExpAndSp(202160, 20375);
            st.exitQuest(false);
            st.soundEffect(SOUND_FINISH);
            st.removeMemo("power_of_the_high_ore");
            st.removeMemo("power_of_the_high_ore_1");
            htmltext = "attribute_manager1_q10275_19.htm";
        } else if (event.equalsIgnoreCase("reply_15") && GetMemoState == 6 && GetMemoStateEx == 1) {
            st.giveItems(q_ore_of_wind, 2);
            st.takeItems(q_Attribute_manager_weapon1, -1);
            st.takeItems(q_elemental_piece_water, -1);
            st.addExpAndSp(202160, 20375);
            st.exitQuest(false);
            st.soundEffect(SOUND_FINISH);
            st.removeMemo("power_of_the_high_ore");
            st.removeMemo("power_of_the_high_ore_1");
            htmltext = "attribute_manager1_q10275_20.htm";
        } else if (event.equalsIgnoreCase("reply_16") && GetMemoState == 6 && GetMemoStateEx == 1) {
            st.giveItems(q_ore_of_earth, 2);
            st.takeItems(q_Attribute_manager_weapon1, -1);
            st.takeItems(q_elemental_piece_water, -1);
            st.addExpAndSp(202160, 20375);
            st.exitQuest(false);
            st.soundEffect(SOUND_FINISH);
            st.removeMemo("power_of_the_high_ore");
            st.removeMemo("power_of_the_high_ore_1");
            htmltext = "attribute_manager1_q10275_21.htm";
        } else if (event.equalsIgnoreCase("reply_2c") && GetMemoState == 8 && GetMemoStateEx == 2) {
            st.setCond(8);
            st.setMemoState("power_of_the_high_ore", String.valueOf(9), true);
            st.giveItems(q_Attribute_manager_weapon2, 1, Element.EARTH, 10);
            st.soundEffect(SOUND_MIDDLE);
            htmltext = "attribute_manager2_q10275_04.htm";
        } else if (event.equalsIgnoreCase("reply_4a") && GetMemoState == 9 && GetMemoStateEx == 2) {
            if (st.ownItemCount(q_elemental_piece_wind) < 6 && st.ownItemCount(q_Attribute_manager_weapon2) == 0) {
                st.giveItems(q_Attribute_manager_weapon2, 1, Element.EARTH, 10);
                htmltext = "attribute_manager2_q10275_07.htm";
            } else if (st.ownItemCount(q_elemental_piece_wind) < 6 && st.ownItemCount(q_Attribute_manager_weapon2) == 1) {
                st.takeItems(q_Attribute_manager_weapon2, -1);
                st.giveItems(q_Attribute_manager_weapon2, 1, Element.EARTH, 10);
                htmltext = "attribute_manager2_q10275_08.htm";
            }
        } else if (event.equalsIgnoreCase("reply_5a") && GetMemoState == 10 && GetMemoStateEx == 2) {
            st.setCond(10);
            st.setMemoState("power_of_the_high_ore", String.valueOf(11), true);
            SkillTable.getInstance().getSkillEntry(s_item_attribute_blessing_of_earth1, 1).getEffects(st.getPlayer(), st.getPlayer(), false, false);
            st.giveItems(q_Attribute_manager_weapon2, 1, Element.EARTH, 10);
            st.takeItems(q_elemental_piece_wind, -1);
            st.soundEffect(SOUND_MIDDLE);
            htmltext = "attribute_manager2_q10275_10.htm";
        } else if (event.equalsIgnoreCase("reply_7a") && GetMemoState == 11 && GetMemoStateEx == 2) {
            if (st.ownItemCount(q_elemental_piece_wind) < 6 && st.ownItemCount(q_Attribute_manager_weapon2) == 0) {
                st.giveItems(q_Attribute_manager_weapon2, 1, Element.EARTH, 10);
                SkillTable.getInstance().getSkillEntry(s_item_attribute_blessing_of_earth1, 1).getEffects(st.getPlayer(), st.getPlayer(), false, false);
                htmltext = "attribute_manager2_q10275_13.htm";
            } else if (st.ownItemCount(q_elemental_piece_wind) < 6 && st.ownItemCount(q_Attribute_manager_weapon2) == 1) {
                st.takeItems(q_Attribute_manager_weapon2, -1);
                st.giveItems(q_Attribute_manager_weapon2, 1, Element.EARTH, 10);
                SkillTable.getInstance().getSkillEntry(s_item_attribute_blessing_of_earth1, 1).getEffects(st.getPlayer(), st.getPlayer(), false, false);
                htmltext = "attribute_manager2_q10275_14.htm";
            }
        } else if (event.equalsIgnoreCase("reply_11a") && GetMemoState == 12 && GetMemoStateEx == 2) {
            st.giveItems(q_ore_of_holy, 2);
            st.takeItems(q_Attribute_manager_weapon2, -1);
            st.takeItems(q_elemental_piece_wind, -1);
            st.addExpAndSp(202160, 20375);
            st.exitQuest(false);
            st.soundEffect(SOUND_FINISH);
            st.removeMemo("power_of_the_high_ore");
            st.removeMemo("power_of_the_high_ore_1");
            htmltext = "attribute_manager2_q10275_16.htm";
        } else if (event.equalsIgnoreCase("reply_12a") && GetMemoState == 12 && GetMemoStateEx == 2) {
            st.giveItems(q_ore_of_unholy, 2);
            st.takeItems(q_Attribute_manager_weapon2, -1);
            st.takeItems(q_elemental_piece_wind, -1);
            st.addExpAndSp(202160, 20375);
            st.exitQuest(false);
            st.soundEffect(SOUND_FINISH);
            st.removeMemo("power_of_the_high_ore");
            st.removeMemo("power_of_the_high_ore_1");
            htmltext = "attribute_manager2_q10275_17.htm";
        } else if (event.equalsIgnoreCase("reply_13a") && GetMemoState == 12 && GetMemoStateEx == 2) {
            st.giveItems(q_ore_of_fire, 2);
            st.takeItems(q_Attribute_manager_weapon2, -1);
            st.takeItems(q_elemental_piece_wind, -1);
            st.addExpAndSp(202160, 20375);
            st.exitQuest(false);
            st.soundEffect(SOUND_FINISH);
            st.removeMemo("power_of_the_high_ore");
            st.removeMemo("power_of_the_high_ore_1");
            htmltext = "attribute_manager2_q10275_18.htm";
        } else if (event.equalsIgnoreCase("reply_14a") && GetMemoState == 12 && GetMemoStateEx == 2) {
            st.giveItems(q_ore_of_water, 2);
            st.takeItems(q_Attribute_manager_weapon2, -1);
            st.takeItems(q_elemental_piece_wind, -1);
            st.addExpAndSp(202160, 20375);
            st.exitQuest(false);
            st.soundEffect(SOUND_FINISH);
            st.removeMemo("power_of_the_high_ore");
            st.removeMemo("power_of_the_high_ore_1");
            htmltext = "attribute_manager2_q10275_19.htm";
        } else if (event.equalsIgnoreCase("reply_15a") && GetMemoState == 12 && GetMemoStateEx == 2) {
            st.giveItems(q_ore_of_wind, 2);
            st.takeItems(q_Attribute_manager_weapon2, -1);
            st.takeItems(q_elemental_piece_wind, -1);
            st.addExpAndSp(202160, 20375);
            st.exitQuest(false);
            st.soundEffect(SOUND_FINISH);
            st.removeMemo("power_of_the_high_ore");
            st.removeMemo("power_of_the_high_ore_1");
            htmltext = "attribute_manager2_q10275_20.htm";
        } else if (event.equalsIgnoreCase("reply_16a") && GetMemoState == 12 && GetMemoStateEx == 2) {
            st.giveItems(q_ore_of_earth, 2);
            st.takeItems(q_Attribute_manager_weapon2, -1);
            st.takeItems(q_elemental_piece_wind, -1);
            st.addExpAndSp(202160, 20375);
            st.exitQuest(false);
            st.soundEffect(SOUND_FINISH);
            st.removeMemo("power_of_the_high_ore");
            st.removeMemo("power_of_the_high_ore_1");
            htmltext = "attribute_manager2_q10275_21.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("power_of_the_high_ore");
        int npcId = npc.getNpcId();
        int id = st.getState();
        int GetMemoStateEx = st.getInt("power_of_the_high_ore_1");

        switch (id) {
            case CREATED:
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        if (npcId == trader_holly && GetMemoStateEx != 2) {
                            htmltext = "trader_holly_q10275_02.htm";
                            st.exitQuest(true);
                        } else if (npcId == trader_weber && GetMemoStateEx != 1) {
                            htmltext = "trader_weber_q10275_02.htm";
                            st.exitQuest(true);
                        }
                        break;
                    default:
                        if (npcId == trader_holly && GetMemoStateEx != 2)
                            htmltext = "trader_holly_q10275_01.htm";
                        else if (npcId == trader_weber && GetMemoStateEx != 1)
                            htmltext = "trader_weber_q10275_01.htm";
                        break;
                }
                break;
            case STARTED:
                if (npcId == trader_holly) {
                    if (GetMemoState == 1 && GetMemoStateEx == 1)
                        htmltext = "trader_holly_q10275_01b.htm";
                    else if (GetMemoStateEx == 2)
                        htmltext = "trader_holly_q10275_05.htm";
                    else if (GetMemoState == 2 && GetMemoStateEx == 1)
                        htmltext = "trader_holly_q10275_07.htm";
                } else if (npcId == trader_weber) {
                    if (GetMemoState == 7 && GetMemoStateEx == 2)
                        htmltext = "trader_weber_q10275_01b.htm";
                    else if (GetMemoStateEx == 1)
                        htmltext = "trader_weber_q10275_05.htm";
                    else if (GetMemoState == 8 && GetMemoStateEx == 2)
                        htmltext = "trader_weber_q10275_07.htm";
                } else if (npcId == attribute_manager1) {
                    if (GetMemoState == 2 && GetMemoStateEx == 1)
                        htmltext = "attribute_manager1_q10275_01.htm";
                    else if (GetMemoState == 8 && GetMemoStateEx == 2)
                        htmltext = "attribute_manager1_q10275_03.htm";
                    else if (GetMemoState == 3 && st.ownItemCount(q_elemental_piece_water) < 6 && GetMemoStateEx == 1)
                        htmltext = "attribute_manager1_q10275_05.htm";
                    else if (GetMemoState == 4 && GetMemoStateEx == 1) {
                        st.takeItems(q_Attribute_manager_weapon1, -1);
                        htmltext = "attribute_manager1_q10275_09.htm";
                    } else if (GetMemoState == 5 && st.ownItemCount(q_elemental_piece_water) < 6 && GetMemoStateEx == 1)
                        htmltext = "attribute_manager1_q10275_11.htm";
                    else if (GetMemoState == 6 && GetMemoStateEx == 1)
                        htmltext = "attribute_manager1_q10275_15.htm";
                } else if (npcId == attribute_manager2)
                    if (GetMemoState == 8 && GetMemoStateEx == 2)
                        htmltext = "attribute_manager2_q10275_01.htm";
                    else if (GetMemoState == 2 && GetMemoStateEx == 1)
                        htmltext = "attribute_manager2_q10275_03.htm";
                    else if (GetMemoState == 9 && st.ownItemCount(q_elemental_piece_wind) < 6 && GetMemoStateEx == 2)
                        htmltext = "attribute_manager2_q10275_05.htm";
                    else if (GetMemoState == 10 && GetMemoStateEx == 2) {
                        st.takeItems(q_Attribute_manager_weapon2, -1);
                        htmltext = "attribute_manager2_q10275_09.htm";
                    } else if (GetMemoState == 11 && st.ownItemCount(q_elemental_piece_wind) < 6 && GetMemoStateEx == 2)
                        htmltext = "attribute_manager2_q10275_11.htm";
                    else if (GetMemoState == 12 && GetMemoStateEx == 2)
                        htmltext = "attribute_manager2_q10275_15.htm";
                break;
            case COMPLETED:
                if (npcId == trader_holly)
                    htmltext = "trader_holly_q10275_03.htm";
                else if (npcId == trader_weber)
                    htmltext = "trader_weber_q10275_03.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("power_of_the_high_ore");
        int npcId = npc.getNpcId();
        int GetMemoStateEx = st.getInt("power_of_the_high_ore_1");

        if (npcId == insane_elemental_water && GetMemoStateEx == 1 && (GetMemoState == 3 || GetMemoState == 5)) {
            if (st.getItemEquipped(Inventory.PAPERDOLL_RHAND) == q_Attribute_manager_weapon1)
                if (Rnd.get(1000) < 766)
                    if (st.ownItemCount(q_elemental_piece_water) == 5) {
                        st.giveItems(q_elemental_piece_water, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        if (GetMemoState == 3) {
                            st.setCond(4);
                            st.setMemoState("power_of_the_high_ore", String.valueOf(4), true);
                        } else if (GetMemoState == 5) {
                            st.setCond(6);
                            st.setMemoState("power_of_the_high_ore", String.valueOf(6), true);
                        }
                    } else {
                        st.giveItems(q_elemental_piece_water, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
        } else if (npcId == insane_elemental_wind && GetMemoStateEx == 2 && (GetMemoState == 9 || GetMemoState == 11))
            if (st.getItemEquipped(Inventory.PAPERDOLL_RHAND) == q_Attribute_manager_weapon2)
                if (Rnd.get(1000) < 766)
                    if (st.ownItemCount(q_elemental_piece_wind) == 5) {
                        st.giveItems(q_elemental_piece_wind, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        if (GetMemoState == 9) {
                            st.setCond(9);
                            st.setMemoState("power_of_the_high_ore", String.valueOf(10), true);
                        } else if (GetMemoState == 11) {
                            st.setCond(11);
                            st.setMemoState("power_of_the_high_ore", String.valueOf(12), true);
                        }
                    } else {
                        st.giveItems(q_elemental_piece_wind, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
        return null;
    }
}