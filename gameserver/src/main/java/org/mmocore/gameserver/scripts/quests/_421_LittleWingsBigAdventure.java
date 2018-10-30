package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Servitor;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.0
 * @date 24/01/2015
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _421_LittleWingsBigAdventure extends Quest {
    // npc
    private static final int sage_cronos = 30610;
    private static final int fairy_mymyu = 30747;
    // mobs
    private static final int tree_q0421_1 = 27185;
    private static final int tree_q0421_2 = 27186;
    private static final int tree_q0421_3 = 27187;
    private static final int tree_q0421_4 = 27188;
    // questitem
    private static final int q0421_fairy_leaf = 4325;
    // etcitem
    private static final int dragonflute_of_wind = 3500;
    private static final int dragonflute_of_star = 3501;
    private static final int dragonflute_of_twilight = 3502;
    private static final int dragon_bugle_wind = 4422;
    private static final int dragon_bugle_star = 4423;
    private static final int dragon_bugle_dusk = 4424;
    // memo
    int c1_flag = 0;

    public _421_LittleWingsBigAdventure() {
        super(false);
        addStartNpc(sage_cronos);
        addTalkId(fairy_mymyu);
        addAttackId(tree_q0421_1, tree_q0421_2, tree_q0421_3, tree_q0421_4);
        addQuestItem(q0421_fairy_leaf);
        addLevelCheck(45);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        ItemInstance item0;
        Servitor c0 = st.getPlayer().getServitor();
        int GetMemoStateEx = st.getInt("adventure_of_the_little_wings_ex");
        int npcId = npc.getNpcId();
        if (npcId == sage_cronos) {
            if (event.equalsIgnoreCase("quest_accept")) {
                if (st.ownItemCount(dragonflute_of_wind) + st.ownItemCount(dragonflute_of_star) + st.ownItemCount(dragonflute_of_twilight) == 1) {
                    if (st.ownItemCount(dragonflute_of_wind) == 1) {
                        item0 = st.getPlayer().getInventory().getItemByItemId(dragonflute_of_wind);
                        if (item0 != null) {
                            if (item0.getEnchantLevel() < 55)
                                htmltext = "sage_cronos_q0421_06.htm";
                            else {
                                st.setCond(1);
                                st.setMemoState("adventure_of_the_little_wings", String.valueOf(100), true);
                                st.setMemoState("adventure_of_the_little_wings_ex", String.valueOf(item0.getObjectId()), true); // item0.dbid
                                st.setState(STARTED);
                                st.soundEffect(SOUND_ACCEPT);
                                htmltext = "sage_cronos_q0421_05.htm";
                            }
                        }
                    } else if (st.ownItemCount(dragonflute_of_star) == 1) {
                        item0 = st.getPlayer().getInventory().getItemByItemId(dragonflute_of_star);
                        if (item0 != null) {
                            if (item0.getEnchantLevel() < 55)
                                htmltext = "sage_cronos_q0421_06.htm";
                            else {
                                st.setCond(1);
                                st.setMemoState("adventure_of_the_little_wings", String.valueOf(100), true);
                                st.setMemoState("adventure_of_the_little_wings_ex", String.valueOf(item0.getObjectId()), true); // item0.dbid
                                st.setState(STARTED);
                                st.soundEffect(SOUND_ACCEPT);
                                htmltext = "sage_cronos_q0421_05.htm";
                            }
                        }
                    } else if (st.ownItemCount(dragonflute_of_twilight) == 1) {
                        item0 = st.getPlayer().getInventory().getItemByItemId(dragonflute_of_twilight);
                        if (item0 != null) {
                            if (item0.getEnchantLevel() < 55)
                                htmltext = "sage_cronos_q0421_06.htm";
                            else {
                                st.setCond(1);
                                st.setMemoState("adventure_of_the_little_wings", String.valueOf(100), true);
                                st.setMemoState("adventure_of_the_little_wings_ex", String.valueOf(item0.getObjectId()), true); // item0.dbid
                                st.setState(STARTED);
                                st.soundEffect(SOUND_ACCEPT);
                                htmltext = "sage_cronos_q0421_05.htm";
                            }
                        }
                    }
                } else
                    htmltext = "sage_cronos_q0421_06.htm";
            }
        } else if (npcId == fairy_mymyu) {
            if (event.equalsIgnoreCase("menu_select?ask=421&reply=1")) {
                if (c0 != null) {
                    if ((st.getPlayer().getInventory().getCountOf(dragonflute_of_wind) == 1 && GetMemoStateEx == st.getPlayer().getInventory().getItemByItemId(dragonflute_of_wind).getObjectId()) || (st.getPlayer().getInventory().getCountOf(dragonflute_of_star) == 1 && GetMemoStateEx == st.getPlayer().getInventory().getItemByItemId(dragonflute_of_star).getObjectId()) || (st.getPlayer().getInventory().getCountOf(dragonflute_of_twilight) == 1 && GetMemoStateEx == st.getPlayer().getInventory().getItemByItemId(dragonflute_of_twilight).getObjectId()))
                        htmltext = "fairy_mymyu_q0421_04.htm";
                    else
                        htmltext = "fairy_mymyu_q0421_03.htm";
                } else
                    htmltext = "fairy_mymyu_q0421_02.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=421&reply=2")) {
                if (c0 != null) {
                    if ((st.getPlayer().getInventory().getCountOf(dragonflute_of_wind) == 1 && GetMemoStateEx == st.getPlayer().getInventory().getItemByItemId(dragonflute_of_wind).getObjectId()) || (st.getPlayer().getInventory().getCountOf(dragonflute_of_star) == 1 && GetMemoStateEx == st.getPlayer().getInventory().getItemByItemId(dragonflute_of_star).getObjectId()) || (st.getPlayer().getInventory().getCountOf(dragonflute_of_twilight) == 1 && GetMemoStateEx == st.getPlayer().getInventory().getItemByItemId(dragonflute_of_twilight).getObjectId())) {
                        st.setCond(2);
                        st.setMemoState("adventure_of_the_little_wings", String.valueOf(0), true);
                        st.giveItems(q0421_fairy_leaf, 4);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "fairy_mymyu_q0421_05.htm";
                    } else
                        htmltext = "fairy_mymyu_q0421_06.htm";
                } else
                    htmltext = "fairy_mymyu_q0421_06.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=421&reply=3"))
                htmltext = "fairy_mymyu_q0421_07.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=421&reply=4"))
                htmltext = "fairy_mymyu_q0421_08.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=421&reply=5"))
                htmltext = "fairy_mymyu_q0421_09.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=421&reply=6"))
                htmltext = "fairy_mymyu_q0421_10.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        ItemInstance item0;
        Servitor c0 = st.getPlayer().getServitor();
        int GetMemoState = st.getInt("adventure_of_the_little_wings");
        int GetMemoStateEx = st.getInt("adventure_of_the_little_wings_ex");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == sage_cronos) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "sage_cronos_q0421_01.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            if (st.ownItemCount(dragonflute_of_wind) + st.ownItemCount(dragonflute_of_star) + st.ownItemCount(dragonflute_of_twilight) >= 2)
                                htmltext = "sage_cronos_q0421_02.htm";
                            else {
                                if (st.ownItemCount(dragonflute_of_wind) + st.ownItemCount(dragonflute_of_star) + st.ownItemCount(dragonflute_of_twilight) == 1) {
                                    if (st.ownItemCount(dragonflute_of_wind) == 1) {
                                        item0 = st.getPlayer().getInventory().getItemByItemId(dragonflute_of_wind);
                                        if (item0 != null) {
                                            if (item0.getEnchantLevel() < 55)
                                                htmltext = "sage_cronos_q0421_03.htm";
                                            else
                                                htmltext = "sage_cronos_q0421_04.htm";
                                        }
                                    }
                                }
                                if (st.ownItemCount(dragonflute_of_star) == 1) {
                                    item0 = st.getPlayer().getInventory().getItemByItemId(dragonflute_of_star);
                                    if (item0 != null) {
                                        if (item0.getEnchantLevel() < 55)
                                            htmltext = "sage_cronos_q0421_03.htm";
                                        else
                                            htmltext = "sage_cronos_q0421_04.htm";
                                    }
                                }
                                if (st.ownItemCount(dragonflute_of_twilight) == 1) {
                                    item0 = st.getPlayer().getInventory().getItemByItemId(dragonflute_of_twilight);
                                    if (item0 != null) {
                                        if (item0.getEnchantLevel() < 55)
                                            htmltext = "sage_cronos_q0421_03.htm";
                                        else
                                            htmltext = "sage_cronos_q0421_04.htm";
                                    }
                                }
                            }
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == sage_cronos) {
                    if (GetMemoState == 100 || GetMemoState == 200 || (GetMemoState >= 0 && GetMemoState <= 16))
                        htmltext = "sage_cronos_q0421_07.htm";
                } else if (npcId == fairy_mymyu) {
                    if (GetMemoState == 100) {
                        st.setMemoState("adventure_of_the_little_wings", String.valueOf(200), true);
                        htmltext = "fairy_mymyu_q0421_01.htm";
                    } else if (GetMemoState == 200) {
                        if (c0 != null) {
                            if ((st.getPlayer().getInventory().getCountOf(dragonflute_of_wind) == 1 && GetMemoStateEx == st.getPlayer().getInventory().getItemByItemId(dragonflute_of_wind).getObjectId()) || (st.getPlayer().getInventory().getCountOf(dragonflute_of_star) == 1 && GetMemoStateEx == st.getPlayer().getInventory().getItemByItemId(dragonflute_of_star).getObjectId()) || (st.getPlayer().getInventory().getCountOf(dragonflute_of_twilight) == 1 && GetMemoStateEx == st.getPlayer().getInventory().getItemByItemId(dragonflute_of_twilight).getObjectId()))
                                htmltext = "fairy_mymyu_q0421_04.htm";
                            else
                                htmltext = "fairy_mymyu_q0421_03.htm";
                        } else
                            htmltext = "fairy_mymyu_q0421_02.htm";
                    } else if (GetMemoState == 0)
                        htmltext = "fairy_mymyu_q0421_07.htm";
                    else if (GetMemoState > 0 && GetMemoState < 15 && st.ownItemCount(q0421_fairy_leaf) >= 1)
                        htmltext = "fairy_mymyu_q0421_11.htm";
                    else if (GetMemoState == 15 && st.ownItemCount(q0421_fairy_leaf) == 0) {
                        if (c0 != null) {
                            if ((st.getPlayer().getInventory().getCountOf(dragonflute_of_wind) == 1 && GetMemoStateEx == st.getPlayer().getInventory().getItemByItemId(dragonflute_of_wind).getObjectId()) || (st.getPlayer().getInventory().getCountOf(dragonflute_of_star) == 1 && GetMemoStateEx == st.getPlayer().getInventory().getItemByItemId(dragonflute_of_star).getObjectId()) || (st.getPlayer().getInventory().getCountOf(dragonflute_of_twilight) == 1 && GetMemoStateEx == st.getPlayer().getInventory().getItemByItemId(dragonflute_of_twilight).getObjectId())) {
                                st.setMemoState("adventure_of_the_little_wings", String.valueOf(16), true);
                                htmltext = "fairy_mymyu_q0421_13.htm";
                            } else
                                htmltext = "fairy_mymyu_q0421_14.htm";
                        } else
                            htmltext = "fairy_mymyu_q0421_12.htm";
                    } else if (GetMemoState == 16 && st.ownItemCount(q0421_fairy_leaf) == 0) {
                        if (c0 == null)
                            htmltext = "fairy_mymyu_q0421_15.htm";
                        else if (st.ownItemCount(dragonflute_of_wind) + st.ownItemCount(dragonflute_of_star) + st.ownItemCount(dragonflute_of_twilight) == 1) {
                            if (st.ownItemCount(dragonflute_of_wind) == 1) {
                                item0 = st.getPlayer().getInventory().getItemByItemId(dragonflute_of_wind);
                                if (item0 != null) {
                                    if (GetMemoStateEx == item0.getObjectId()) {
                                        if (st.getPlayer().getInventory().getCountOf(dragonflute_of_wind) == 1) {
                                            if (GetMemoStateEx == item0.getObjectId()) {
                                                c0.unSummon(false, false);
                                                st.getPlayer().getInventory().destroyItemByObjectId(GetMemoStateEx, 1);
                                                st.giveItems(dragon_bugle_wind, 1);
                                                st.removeMemo("adventure_of_the_little_wings");
                                                st.removeMemo("adventure_of_the_little_wings_ex");
                                                st.soundEffect(SOUND_FINISH);
                                                st.exitQuest(true);
                                                htmltext = "fairy_mymyu_q0421_16.htm";
                                            }
                                        }
                                    } else {
                                        npc.doCast(SkillTable.getInstance().getSkillEntry(4167, 1), st.getPlayer(), true);
                                        htmltext = "fairy_mymyu_q0421_18.htm";
                                    }
                                }
                            } else if (st.ownItemCount(dragonflute_of_star) == 1) {
                                item0 = st.getPlayer().getInventory().getItemByItemId(dragonflute_of_star);
                                if (item0 != null) {
                                    if (GetMemoStateEx == item0.getObjectId()) {
                                        if (st.getPlayer().getInventory().getCountOf(dragonflute_of_star) == 1) {
                                            if (GetMemoStateEx == item0.getObjectId()) {
                                                c0.unSummon(false, false);
                                                st.getPlayer().getInventory().destroyItemByObjectId(GetMemoStateEx, 1);
                                                st.giveItems(dragon_bugle_star, 1);
                                                st.removeMemo("adventure_of_the_little_wings");
                                                st.removeMemo("adventure_of_the_little_wings_ex");
                                                st.soundEffect(SOUND_FINISH);
                                                st.exitQuest(true);
                                                htmltext = "fairy_mymyu_q0421_16.htm";
                                            }
                                        }
                                    } else {
                                        npc.doCast(SkillTable.getInstance().getSkillEntry(4167, 1), st.getPlayer(), true);
                                        htmltext = "fairy_mymyu_q0421_18.htm";
                                    }
                                }
                            } else if (st.ownItemCount(dragonflute_of_twilight) == 1) {
                                item0 = st.getPlayer().getInventory().getItemByItemId(dragonflute_of_twilight);
                                if (item0 != null) {
                                    if (GetMemoStateEx == item0.getObjectId()) {
                                        if (st.getPlayer().getInventory().getCountOf(dragonflute_of_twilight) == 1) {
                                            if (GetMemoStateEx == item0.getObjectId()) {
                                                c0.unSummon(false, false);
                                                st.getPlayer().getInventory().destroyItemByObjectId(GetMemoStateEx, 1);
                                                st.giveItems(dragon_bugle_dusk, 1);
                                                st.removeMemo("adventure_of_the_little_wings");
                                                st.removeMemo("adventure_of_the_little_wings_ex");
                                                st.soundEffect(SOUND_FINISH);
                                                st.exitQuest(true);
                                                htmltext = "fairy_mymyu_q0421_16.htm";
                                            }
                                        }
                                    } else {
                                        npc.doCast(SkillTable.getInstance().getSkillEntry(4167, 1), st.getPlayer(), true);
                                        htmltext = "fairy_mymyu_q0421_18.htm";
                                    }
                                }
                            }
                        } else if (st.ownItemCount(dragonflute_of_wind) + st.ownItemCount(dragonflute_of_star) + st.ownItemCount(dragonflute_of_twilight) >= 2)
                            htmltext = "fairy_mymyu_q0421_17.htm";
                    }
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onAttack(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("adventure_of_the_little_wings");
        int GetMemoStateEx = st.getInt("adventure_of_the_little_wings_ex");
        int npcId = npc.getNpcId();
        if (npcId == tree_q0421_1) {
            if (GetMemoState <= 16) {
                if (Rnd.get(100) <= 29)
                    npc.doCast(SkillTable.getInstance().getSkillEntry(4243, 1), st.getPlayer().getServitor(), true);
                if (GetMemoState % 2 == 0) {
                    if ((st.getPlayer().getInventory().getCountOf(dragonflute_of_wind) == 1 && GetMemoStateEx == st.getPlayer().getInventory().getItemByItemId(dragonflute_of_wind).getObjectId()) || (st.getPlayer().getInventory().getCountOf(dragonflute_of_star) == 1 && GetMemoStateEx == st.getPlayer().getInventory().getItemByItemId(dragonflute_of_star).getObjectId()) || (st.getPlayer().getInventory().getCountOf(dragonflute_of_twilight) == 1 && GetMemoStateEx == st.getPlayer().getInventory().getItemByItemId(dragonflute_of_twilight).getObjectId())) {
                        if (Rnd.get(100) <= 2) {
                            if (st.ownItemCount(q0421_fairy_leaf) >= 1) {
                                Functions.npcSay(npc, NpcString.GIVE_ME_A_FAIRY_LEAF);
                                st.takeItems(q0421_fairy_leaf, 1);
                                st.setMemoState("adventure_of_the_little_wings", String.valueOf(GetMemoState + 1), true);
                                st.soundEffect(SOUND_MIDDLE);
                                if (GetMemoState >= 15 && st.ownItemCount(q0421_fairy_leaf) <= 1)
                                    st.setCond(3);
                            }
                        }
                    }
                } else {
                    int i0 = Rnd.get(3);
                    if (i0 == 0)
                        Functions.npcSay(npc, NpcString.WHY_DO_YOU_BOTHER_ME_AGAIN);
                    else if (i0 == 1)
                        Functions.npcSay(npc, NpcString.HEY_YOUVE_ALREADY_DRUNK_THE_ESSENCE_OF_WIND);
                    else
                        Functions.npcSay(npc, NpcString.LEAVE_NOW_BEFORE_YOU_INCUR_THE_WRATH_OF_THE_GUARDIAN_GHOST);
                }
            }
        } else if (npcId == tree_q0421_2) {
            if (GetMemoState <= 16) {
                if (Rnd.get(100) <= 29)
                    npc.doCast(SkillTable.getInstance().getSkillEntry(4243, 1), st.getPlayer().getServitor(), true);
                if (GetMemoState % 4 < 2) {
                    if ((st.getPlayer().getInventory().getCountOf(dragonflute_of_wind) == 1 && GetMemoStateEx == st.getPlayer().getInventory().getItemByItemId(dragonflute_of_wind).getObjectId()) || (st.getPlayer().getInventory().getCountOf(dragonflute_of_star) == 1 && GetMemoStateEx == st.getPlayer().getInventory().getItemByItemId(dragonflute_of_star).getObjectId()) || (st.getPlayer().getInventory().getCountOf(dragonflute_of_twilight) == 1 && GetMemoStateEx == st.getPlayer().getInventory().getItemByItemId(dragonflute_of_twilight).getObjectId())) {
                        if (Rnd.get(100) <= 1) {
                            if (st.ownItemCount(q0421_fairy_leaf) >= 1) {
                                Functions.npcSay(npc, NpcString.GIVE_ME_A_FAIRY_LEAF);
                                st.takeItems(q0421_fairy_leaf, 1);
                                st.setMemoState("adventure_of_the_little_wings", String.valueOf(GetMemoState + 2), true);
                                st.soundEffect(SOUND_MIDDLE);
                                if (GetMemoState >= 15 && st.ownItemCount(q0421_fairy_leaf) <= 1)
                                    st.setCond(3);
                            }
                        }
                    }
                } else {
                    int i0 = Rnd.get(3);
                    if (i0 == 0)
                        Functions.npcSay(npc, NpcString.WHY_DO_YOU_BOTHER_ME_AGAIN);
                    else if (i0 == 1)
                        Functions.npcSay(npc, NpcString.HEY_YOUVE_ALREADY_DRUNK_THE_ESSENCE_OF_A_STAR);
                    else
                        Functions.npcSay(npc, NpcString.LEAVE_NOW_BEFORE_YOU_INCUR_THE_WRATH_OF_THE_GUARDIAN_GHOST);
                }
            }
        } else if (npcId == tree_q0421_3) {
            if (GetMemoState <= 16) {
                if (Rnd.get(100) <= 29)
                    npc.doCast(SkillTable.getInstance().getSkillEntry(4243, 1), st.getPlayer().getServitor(), true);
                if (GetMemoState % 8 < 4) {
                    if ((st.getPlayer().getInventory().getCountOf(dragonflute_of_wind) == 1 && GetMemoStateEx == st.getPlayer().getInventory().getItemByItemId(dragonflute_of_wind).getObjectId()) || (st.getPlayer().getInventory().getCountOf(dragonflute_of_star) == 1 && GetMemoStateEx == st.getPlayer().getInventory().getItemByItemId(dragonflute_of_star).getObjectId()) || (st.getPlayer().getInventory().getCountOf(dragonflute_of_twilight) == 1 && GetMemoStateEx == st.getPlayer().getInventory().getItemByItemId(dragonflute_of_twilight).getObjectId())) {
                        if (Rnd.get(100) <= 1) {
                            if (st.ownItemCount(q0421_fairy_leaf) >= 1) {
                                Functions.npcSay(npc, NpcString.GIVE_ME_A_FAIRY_LEAF);
                                st.takeItems(q0421_fairy_leaf, 1);
                                st.setMemoState("adventure_of_the_little_wings", String.valueOf(GetMemoState + 4), true);
                                st.soundEffect(SOUND_MIDDLE);
                                if (GetMemoState >= 15 && st.ownItemCount(q0421_fairy_leaf) <= 1)
                                    st.setCond(3);
                            }
                        }
                    }
                } else {
                    int i0 = Rnd.get(3);
                    if (i0 == 0)
                        Functions.npcSay(npc, NpcString.WHY_DO_YOU_BOTHER_ME_AGAIN);
                    else if (i0 == 1)
                        Functions.npcSay(npc, NpcString.HEY_YOUVE_ALREADY_DRUNK_THE_ESSENCE_OF_DUSK);
                    else
                        Functions.npcSay(npc, NpcString.LEAVE_NOW_BEFORE_YOU_INCUR_THE_WRATH_OF_THE_GUARDIAN_GHOST);
                }
            }
        } else if (npcId == tree_q0421_4) {
            if (GetMemoState <= 16) {
                if (Rnd.get(100) <= 29)
                    npc.doCast(SkillTable.getInstance().getSkillEntry(4243, 1), st.getPlayer().getServitor(), true);
                if (GetMemoState % 16 < 8) {
                    if ((st.getPlayer().getInventory().getCountOf(dragonflute_of_wind) == 1 && GetMemoStateEx == st.getPlayer().getInventory().getItemByItemId(dragonflute_of_wind).getObjectId()) || (st.getPlayer().getInventory().getCountOf(dragonflute_of_star) == 1 && GetMemoStateEx == st.getPlayer().getInventory().getItemByItemId(dragonflute_of_star).getObjectId()) || (st.getPlayer().getInventory().getCountOf(dragonflute_of_twilight) == 1 && GetMemoStateEx == st.getPlayer().getInventory().getItemByItemId(dragonflute_of_twilight).getObjectId())) {
                        c1_flag++;
                        if (c1_flag < 270) {
                            if (Rnd.get(100) <= 1)
                                npc.doCast(SkillTable.getInstance().getSkillEntry(1201, 33), st.getPlayer(), true);
                        } else if (Rnd.get(100) <= 1) {
                            if (st.ownItemCount(q0421_fairy_leaf) >= 1) {
                                Functions.npcSay(npc, NpcString.GIVE_ME_A_FAIRY_LEAF);
                                st.takeItems(q0421_fairy_leaf, 1);
                                st.setMemoState("adventure_of_the_little_wings", String.valueOf(GetMemoState + 8), true);
                                st.soundEffect(SOUND_MIDDLE);
                                c1_flag = 0;
                                if (GetMemoState >= 15 && st.ownItemCount(q0421_fairy_leaf) <= 1)
                                    st.setCond(3);
                            }
                        }
                    }
                } else {
                    int i0 = Rnd.get(3);
                    if (i0 == 0)
                        Functions.npcSay(npc, NpcString.WHY_DO_YOU_BOTHER_ME_AGAIN);
                    else if (i0 == 1)
                        Functions.npcSay(npc, NpcString.HEY_YOUVE_ALREADY_DRUNK_THE_ESSENCE_OF_THE_ABYSS);
                    else
                        Functions.npcSay(npc, NpcString.LEAVE_NOW_BEFORE_YOU_INCUR_THE_WRATH_OF_THE_GUARDIAN_GHOST);
                }
            }
        }
        return null;
    }
}