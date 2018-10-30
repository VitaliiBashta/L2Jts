package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
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
 * @version 1.0
 * @date 03/12/2014
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _662_AGameOfCards extends Quest {
    // npc
    private final static int warehouse_chief_klump = 30845;
    // mobs
    private final static int trives = 20672;
    private final static int falibati = 20673;
    private final static int doom_knight = 20674;
    private final static int tulben = 20677;
    private final static int ghost_war = 20955;
    private final static int blade_death = 20958;
    private final static int dark_guard = 20959;
    private final static int bloody_knight = 20961;
    private final static int bloody_priest = 20962;
    private final static int chimera_piece = 20965;
    private final static int black_shadow = 20966;
    private final static int nonexistent_man = 20968;
    private final static int ancients_shaman = 20972;
    private final static int forgetten_ancients = 20973;
    private final static int doom_scout = 21002;
    private final static int dismal_pole = 21004;
    private final static int doom_servant = 21006;
    private final static int doom_archer = 21008;
    private final static int doom_warrior = 21010;
    private final static int hames_orc_scout = 21109;
    private final static int hames_orc_footman = 21112;
    private final static int cursed_guardian = 21114;
    private final static int hames_orc_chieftain = 21116;
    private final static int antelope = 21278;
    private final static int antelope_a = 21279;
    private final static int antelope_b = 21280;
    private final static int buffalo = 21286;
    private final static int buffalo_a = 21287;
    private final static int buffalo_b = 21288;
    private final static int splinter_stakato = 21508;
    private final static int splinter_stakato_soldier = 21510;
    private final static int needle_stakato_soldier = 21515;
    private final static int brilliant_eye = 21520;
    private final static int brilliant_wisdom = 21526;
    private final static int brilliant_legacy = 21530;
    private final static int brilliant_mark = 21535;
    private final static int bloody_queen = 18001;
    // etcitem
    private final static int scrl_of_ench_wp_s = 959;
    private final static int scrl_of_ench_wp_a = 729;
    private final static int scrl_of_ench_wp_b = 947;
    private final static int scrl_of_ench_wp_c = 951;
    private final static int scrl_of_ench_wp_d = 955;
    private final static int scrl_of_ench_am_d = 956;
    private final static int q_poker_chip = 8765;
    private final static int q_the_jewel = 8868;

    public _662_AGameOfCards() {
        super(true);
        addStartNpc(warehouse_chief_klump);
        addKillId(trives, falibati, doom_knight, tulben, ghost_war, blade_death, dark_guard, bloody_knight, bloody_priest, chimera_piece, black_shadow, nonexistent_man, ancients_shaman, forgetten_ancients, doom_scout, dismal_pole, doom_servant, doom_archer, doom_warrior, hames_orc_scout, hames_orc_footman, cursed_guardian, hames_orc_chieftain, antelope, antelope_a, antelope_b, buffalo, buffalo_a, buffalo_b, splinter_stakato, splinter_stakato_soldier, needle_stakato_soldier, brilliant_eye, brilliant_wisdom, brilliant_legacy, brilliant_mark, bloody_queen);
        addQuestItem(q_poker_chip);
        addLevelCheck(61, 80);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("lets_do_card_game");
        int GetMemoStateEx = st.getInt("lets_do_card_game_ex");
        int npcId = npc.getNpcId();
        if (npcId == warehouse_chief_klump) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "warehouse_chief_klump_q0662_03.htm";
            } else if (event.equalsIgnoreCase("reply_1"))
                htmltext = "warehouse_chief_klump_q0662_06.htm";
            else if (event.equalsIgnoreCase("reply_2")) {
                st.removeMemo("lets_do_card_game");
                st.removeMemo("lets_do_card_game_ex");
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                htmltext = "warehouse_chief_klump_q0662_07.htm";
            } else if (event.equalsIgnoreCase("reply_3"))
                htmltext = "warehouse_chief_klump_q0662_08.htm";
            else if (event.equalsIgnoreCase("reply_4"))
                htmltext = "warehouse_chief_klump_q0662_09.htm";
            else if (event.equalsIgnoreCase("reply_6"))
                htmltext = "warehouse_chief_klump_q0662_09a.htm";
            else if (event.equalsIgnoreCase("reply_8"))
                htmltext = "warehouse_chief_klump_q0662_09b.htm";
            else if (event.equalsIgnoreCase("reply_7")) {
                if (GetMemoState == 0 && GetMemoStateEx == 0 && st.ownItemCount(q_poker_chip) < 50)
                    htmltext = "warehouse_chief_klump_q0662_04.htm";
                else if (GetMemoState == 0 && GetMemoStateEx == 0 && st.ownItemCount(q_poker_chip) >= 50)
                    htmltext = "warehouse_chief_klump_q0662_05.htm";
            } else if (event.equalsIgnoreCase("reply_5"))
                htmltext = "warehouse_chief_klump_q0662_10.htm";
            else if (event.equalsIgnoreCase("reply_10")) {
                if (GetMemoState == 0 && GetMemoStateEx == 0 && st.ownItemCount(q_poker_chip) >= 50) {
                    int i1 = 0;
                    int i2 = 0;
                    int i3 = 0;
                    int i4 = 0;
                    int i5 = 0;
                    while (i1 == i2 || i1 == i3 || i1 == i4 || i1 == i5 || i2 == i3 || i2 == i4 || i2 == i5 || i3 == i4 || i3 == i5 || i4 == i5) {
                        i1 = Rnd.get(70) + 1;
                        i2 = Rnd.get(70) + 1;
                        i3 = Rnd.get(70) + 1;
                        i4 = Rnd.get(70) + 1;
                        i5 = Rnd.get(70) + 1;
                    }
                    if (i1 >= 57)
                        i1 = i1 - 56;
                    else if (i1 >= 43)
                        i1 = i1 - 42;
                    else if (i1 >= 29)
                        i1 = i1 - 28;
                    else if (i1 >= 15)
                        i1 = i1 - 14;
                    if (i2 >= 57)
                        i2 = i2 - 56;
                    else if (i2 >= 43)
                        i2 = i2 - 42;
                    else if (i2 >= 29)
                        i2 = i2 - 28;
                    else if (i2 >= 15)
                        i2 = i2 - 14;
                    if (i3 >= 57)
                        i3 = i3 - 56;
                    else if (i3 >= 43)
                        i3 = i3 - 42;
                    else if (i3 >= 29)
                        i3 = i3 - 28;
                    else if (i3 >= 15)
                        i3 = i3 - 14;
                    if (i4 >= 57)
                        i4 = i4 - 56;
                    else if (i4 >= 43)
                        i4 = i4 - 42;
                    else if (i4 >= 29)
                        i4 = i4 - 28;
                    else if (i4 >= 15)
                        i4 = i4 - 14;
                    if (i5 >= 57)
                        i5 = i5 - 56;
                    else if (i5 >= 43)
                        i5 = i5 - 42;
                    else if (i5 >= 29)
                        i5 = i5 - 28;
                    else if (i5 >= 15)
                        i5 = i5 - 14;
                    st.setMemoState("lets_do_card_game", String.valueOf(i4 * 1000000 + i3 * 10000 + i2 * 100 + i1), true);
                    st.setMemoState("lets_do_card_game_ex", String.valueOf(i5), true);
                    st.takeItems(q_poker_chip, 50);
                    htmltext = "warehouse_chief_klump_q0662_11.htm";
                }
            } else if (event.equalsIgnoreCase("reply_11") || event.equalsIgnoreCase("reply_12") || event.equalsIgnoreCase("reply_13") || event.equalsIgnoreCase("reply_14") || event.equalsIgnoreCase("reply_15")) {
                int i0 = GetMemoState;
                int i1 = GetMemoStateEx;
                int i5 = i1 % 100;
                int i9 = i1 / 100;
                i1 = i0 % 100;
                int i2 = i0 % 10000 / 100;
                int i3 = i0 % 1000000 / 10000;
                int i4 = i0 % 100000000 / 1000000;
                if (event.equalsIgnoreCase("reply_11")) {
                    if (i9 % 2 < 1) {
                        i9 = i9 + 1;
                    }
                    if (i9 % 32 < 31) {
                        st.setMemoState("lets_do_card_game_ex", String.valueOf(i9 * 100 + i5), true);
                    }
                } else if (event.equalsIgnoreCase("reply_12")) {
                    if (i9 % 4 < 2) {
                        i9 = i9 + 2;
                    }
                    if (i9 % 32 < 31) {
                        st.setMemoState("lets_do_card_game_ex", String.valueOf(i9 * 100 + i5), true);
                    }
                } else if (event.equalsIgnoreCase("reply_13")) {
                    if (i9 % 8 < 4) {
                        i9 = i9 + 4;
                    }
                    if (i9 % 32 < 31) {
                        st.setMemoState("lets_do_card_game_ex", String.valueOf(i9 * 100 + i5), true);
                    }
                } else if (event.equalsIgnoreCase("reply_14")) {
                    if (i9 % 16 < 8) {
                        i9 = i9 + 8;
                    }
                    if (i9 % 32 < 31) {
                        st.setMemoState("lets_do_card_game_ex", String.valueOf(i9 * 100 + i5), true);
                    }
                } else if (event.equalsIgnoreCase("reply_15")) {
                    if (i9 % 32 < 16) {
                        i9 = i9 + 16;
                    }
                    if (i9 % 32 < 31) {
                        st.setMemoState("lets_do_card_game_ex", String.valueOf(i9 * 100 + i5), true);
                    }
                }
                if (i9 % 32 < 31) {
                    htmltext = HtmCache.getInstance().getHtml("quests/_662_AGameOfCards/warehouse_chief_klump_q0662_12.htm", st.getPlayer());
                } else if (i9 % 32 == 31) {
                    int i6 = 0;
                    int i8 = 0;
                    if (i1 >= 1 && i1 <= 14 && i2 >= 1 && i2 <= 14 && i3 >= 1 && i3 <= 14 && i4 >= 1 && i4 <= 14 && i5 >= 1 && i5 <= 14) {
                        if (i1 == i2) {
                            i6 = i6 + 10;
                            i8 = i8 + 8;
                        }
                        if (i1 == i3) {
                            i6 = i6 + 10;
                            i8 = i8 + 4;
                        }
                        if (i1 == i4) {
                            i6 = i6 + 10;
                            i8 = i8 + 2;
                        }
                        if (i1 == i5) {
                            i6 = i6 + 10;
                            i8 = i8 + 1;
                        }
                        if (i6 % 100 < 10) {
                            if (i8 % 16 < 8) {
                                if (i8 % 8 < 4) {
                                    if (i2 == i3) {
                                        i6 = i6 + 10;
                                        i8 = i8 + 4;
                                    }
                                }
                                if (i8 % 4 < 2) {
                                    if (i2 == i4) {
                                        i6 = i6 + 10;
                                        i8 = i8 + 2;
                                    }
                                }
                                if (i8 % 2 < 1) {
                                    if (i2 == i5) {
                                        i6 = i6 + 10;
                                        i8 = i8 + 1;
                                    }
                                }
                            }
                        } else if (i6 % 10 == 0) {
                            if (i8 % 16 < 8) {
                                if (i8 % 8 < 4) {
                                    if (i2 == i3) {
                                        i6 = i6 + 1;
                                        i8 = i8 + 4;
                                    }
                                }
                                if (i8 % 4 < 2) {
                                    if (i2 == i4) {
                                        i6 = i6 + 1;
                                        i8 = i8 + 2;
                                    }
                                }
                                if (i8 % 2 < 1) {
                                    if (i2 == i5) {
                                        i6 = i6 + 1;
                                        i8 = i8 + 1;
                                    }
                                }
                            }
                        }
                        if (i6 % 100 < 10) {
                            if (i8 % 8 < 4) {
                                if (i8 % 4 < 2) {
                                    if (i3 == i4) {
                                        i6 = i6 + 10;
                                        i8 = i8 + 2;
                                    }
                                }
                                if (i8 % 2 < 1) {
                                    if (i3 == i5) {
                                        i6 = i6 + 10;
                                        i8 = i8 + 1;
                                    }
                                }
                            }
                        } else if (i6 % 10 == 0) {
                            if (i8 % 8 < 4) {
                                if (i8 % 4 < 2) {
                                    if (i3 == i4) {
                                        i6 = i6 + 1;
                                        i8 = i8 + 2;
                                    }
                                }
                                if (i8 % 2 < 1) {
                                    if (i3 == i5) {
                                        i6 = i6 + 1;
                                        i8 = i8 + 1;
                                    }
                                }
                            }
                        }
                        if (i6 % 100 < 10) {
                            if (i8 % 4 < 2) {
                                if (i8 % 2 < 1) {
                                    if (i4 == i5) {
                                        i6 = i6 + 10;
                                        i8 = i8 + 1;
                                    }
                                }
                            }
                        } else if (i6 % 10 == 0) {
                            if (i8 % 4 < 2) {
                                if (i8 % 2 < 1) {
                                    if (i4 == i5) {
                                        i6 = i6 + 1;
                                        i8 = i8 + 1;
                                    }
                                }
                            }
                        }
                    }
                    if (i6 == 40) {
                        htmltext = HtmCache.getInstance().getHtml("quests/_662_AGameOfCards/warehouse_chief_klump_q0662_13.htm", st.getPlayer());
                        st.setMemoState("lets_do_card_game", String.valueOf(0), true);
                        st.setMemoState("lets_do_card_game_ex", String.valueOf(0), true);
                        st.giveItems(q_the_jewel, 43);
                        st.giveItems(scrl_of_ench_wp_s, 3);
                        st.giveItems(scrl_of_ench_wp_a, 1);
                    } else if (i6 == 30) {
                        htmltext = HtmCache.getInstance().getHtml("quests/_662_AGameOfCards/warehouse_chief_klump_q0662_14.htm", st.getPlayer());
                        st.setMemoState("lets_do_card_game", String.valueOf(0), true);
                        st.setMemoState("lets_do_card_game_ex", String.valueOf(0), true);
                        st.giveItems(scrl_of_ench_wp_s, 2);
                        st.giveItems(scrl_of_ench_wp_c, 2);
                    } else if (i6 == 21 || i6 == 12) {
                        htmltext = HtmCache.getInstance().getHtml("quests/_662_AGameOfCards/warehouse_chief_klump_q0662_15.htm", st.getPlayer());
                        st.setMemoState("lets_do_card_game", String.valueOf(0), true);
                        st.setMemoState("lets_do_card_game_ex", String.valueOf(0), true);
                        st.giveItems(scrl_of_ench_wp_a, 1);
                        st.giveItems(scrl_of_ench_wp_b, 2);
                        st.giveItems(scrl_of_ench_wp_d, 1);
                    } else if (i6 == 20) {
                        htmltext = HtmCache.getInstance().getHtml("quests/_662_AGameOfCards/warehouse_chief_klump_q0662_16.htm", st.getPlayer());
                        st.setMemoState("lets_do_card_game", String.valueOf(0), true);
                        st.setMemoState("lets_do_card_game_ex", String.valueOf(0), true);
                        st.giveItems(scrl_of_ench_wp_c, 2);
                    } else if (i6 == 11) {
                        htmltext = HtmCache.getInstance().getHtml("quests/_662_AGameOfCards/warehouse_chief_klump_q0662_17.htm", st.getPlayer());
                        st.setMemoState("lets_do_card_game", String.valueOf(0), true);
                        st.setMemoState("lets_do_card_game_ex", String.valueOf(0), true);
                        st.giveItems(scrl_of_ench_wp_c, 1);
                    } else if (i6 == 10) {
                        htmltext = HtmCache.getInstance().getHtml("quests/_662_AGameOfCards/warehouse_chief_klump_q0662_18.htm", st.getPlayer());
                        st.setMemoState("lets_do_card_game", String.valueOf(0), true);
                        st.setMemoState("lets_do_card_game_ex", String.valueOf(0), true);
                        st.giveItems(scrl_of_ench_am_d, 2);
                    } else if (i6 == 0) {
                        htmltext = HtmCache.getInstance().getHtml("quests/_662_AGameOfCards/warehouse_chief_klump_q0662_19.htm", st.getPlayer());
                        st.setMemoState("lets_do_card_game", String.valueOf(0), true);
                        st.setMemoState("lets_do_card_game_ex", String.valueOf(0), true);
                    }
                }
                if (i9 % 2 < 1) {
                    htmltext = htmltext.replace("<?FontColor1?>", String.valueOf("ffff00"));
                    htmltext = htmltext.replace("<?Cell1?>", String.valueOf("?"));
                } else {
                    htmltext = htmltext.replace("<?FontColor1?>", String.valueOf("ff6f6f"));
                    if (i1 == 1)
                        htmltext = htmltext.replace("<?Cell1?>", String.valueOf("!"));
                    else if (i1 == 2)
                        htmltext = htmltext.replace("<?Cell1?>", String.valueOf("="));
                    else if (i1 == 3)
                        htmltext = htmltext.replace("<?Cell1?>", String.valueOf("T"));
                    else if (i1 == 4)
                        htmltext = htmltext.replace("<?Cell1?>", String.valueOf("V"));
                    else if (i1 == 5)
                        htmltext = htmltext.replace("<?Cell1?>", String.valueOf("O"));
                    else if (i1 == 6)
                        htmltext = htmltext.replace("<?Cell1?>", String.valueOf("P"));
                    else if (i1 == 7)
                        htmltext = htmltext.replace("<?Cell1?>", String.valueOf("S"));
                    else if (i1 == 8)
                        htmltext = htmltext.replace("<?Cell1?>", String.valueOf("E"));
                    else if (i1 == 9)
                        htmltext = htmltext.replace("<?Cell1?>", String.valueOf("H"));
                    else if (i1 == 10)
                        htmltext = htmltext.replace("<?Cell1?>", String.valueOf("A"));
                    else if (i1 == 11)
                        htmltext = htmltext.replace("<?Cell1?>", String.valueOf("R"));
                    else if (i1 == 12)
                        htmltext = htmltext.replace("<?Cell1?>", String.valueOf("D"));
                    else if (i1 == 13)
                        htmltext = htmltext.replace("<?Cell1?>", String.valueOf("I"));
                    else if (i1 == 14)
                        htmltext = htmltext.replace("<?Cell1?>", String.valueOf("N"));
                }
                if (i9 % 4 < 2) {
                    htmltext = htmltext.replace("<?FontColor2?>", String.valueOf("ffff00"));
                    htmltext = htmltext.replace("<?Cell2?>", String.valueOf("?"));
                } else {
                    htmltext = htmltext.replace("<?FontColor2?>", String.valueOf("ff6f6f"));
                    if (i2 == 1)
                        htmltext = htmltext.replace("<?Cell2?>", String.valueOf("!"));
                    else if (i2 == 2)
                        htmltext = htmltext.replace("<?Cell2?>", String.valueOf("="));
                    else if (i2 == 3)
                        htmltext = htmltext.replace("<?Cell2?>", String.valueOf("T"));
                    else if (i2 == 4)
                        htmltext = htmltext.replace("<?Cell2?>", String.valueOf("V"));
                    else if (i2 == 5)
                        htmltext = htmltext.replace("<?Cell2?>", String.valueOf("O"));
                    else if (i2 == 6)
                        htmltext = htmltext.replace("<?Cell2?>", String.valueOf("P"));
                    else if (i2 == 7)
                        htmltext = htmltext.replace("<?Cell2?>", String.valueOf("S"));
                    else if (i2 == 8)
                        htmltext = htmltext.replace("<?Cell2?>", String.valueOf("E"));
                    else if (i2 == 9)
                        htmltext = htmltext.replace("<?Cell2?>", String.valueOf("H"));
                    else if (i2 == 10)
                        htmltext = htmltext.replace("<?Cell2?>", String.valueOf("A"));
                    else if (i2 == 11)
                        htmltext = htmltext.replace("<?Cell2?>", String.valueOf("R"));
                    else if (i2 == 12)
                        htmltext = htmltext.replace("<?Cell2?>", String.valueOf("D"));
                    else if (i2 == 13)
                        htmltext = htmltext.replace("<?Cell2?>", String.valueOf("I"));
                    else if (i2 == 14)
                        htmltext = htmltext.replace("<?Cell2?>", String.valueOf("N"));
                }
                if (i9 % 8 < 4) {
                    htmltext = htmltext.replace("<?FontColor3?>", String.valueOf("ffff00"));
                    htmltext = htmltext.replace("<?Cell3?>", String.valueOf("?"));
                } else {
                    htmltext = htmltext.replace("<?FontColor3?>", String.valueOf("ff6f6f"));
                    if (i3 == 1)
                        htmltext = htmltext.replace("<?Cell3?>", String.valueOf("!"));
                    else if (i3 == 2)
                        htmltext = htmltext.replace("<?Cell3?>", String.valueOf("="));
                    else if (i3 == 3)
                        htmltext = htmltext.replace("<?Cell3?>", String.valueOf("T"));
                    else if (i3 == 4)
                        htmltext = htmltext.replace("<?Cell3?>", String.valueOf("V"));
                    else if (i3 == 5)
                        htmltext = htmltext.replace("<?Cell3?>", String.valueOf("O"));
                    else if (i3 == 6)
                        htmltext = htmltext.replace("<?Cell3?>", String.valueOf("P"));
                    else if (i3 == 7)
                        htmltext = htmltext.replace("<?Cell3?>", String.valueOf("S"));
                    else if (i3 == 8)
                        htmltext = htmltext.replace("<?Cell3?>", String.valueOf("E"));
                    else if (i3 == 9)
                        htmltext = htmltext.replace("<?Cell3?>", String.valueOf("H"));
                    else if (i3 == 10)
                        htmltext = htmltext.replace("<?Cell3?>", String.valueOf("A"));
                    else if (i3 == 11)
                        htmltext = htmltext.replace("<?Cell3?>", String.valueOf("R"));
                    else if (i3 == 12)
                        htmltext = htmltext.replace("<?Cell3?>", String.valueOf("D"));
                    else if (i3 == 13)
                        htmltext = htmltext.replace("<?Cell3?>", String.valueOf("I"));
                    else if (i3 == 14)
                        htmltext = htmltext.replace("<?Cell3?>", String.valueOf("N"));
                }
                if (i9 % 16 < 8) {
                    htmltext = htmltext.replace("<?FontColor4?>", String.valueOf("ffff00"));
                    htmltext = htmltext.replace("<?Cell4?>", String.valueOf("?"));
                } else {
                    htmltext = htmltext.replace("<?FontColor4?>", String.valueOf("ff6f6f"));
                    if (i4 == 1)
                        htmltext = htmltext.replace("<?Cell4?>", String.valueOf("!"));
                    else if (i4 == 2)
                        htmltext = htmltext.replace("<?Cell4?>", String.valueOf("="));
                    else if (i4 == 3)
                        htmltext = htmltext.replace("<?Cell4?>", String.valueOf("T"));
                    else if (i4 == 4)
                        htmltext = htmltext.replace("<?Cell4?>", String.valueOf("V"));
                    else if (i4 == 5)
                        htmltext = htmltext.replace("<?Cell4?>", String.valueOf("O"));
                    else if (i4 == 6)
                        htmltext = htmltext.replace("<?Cell4?>", String.valueOf("P"));
                    else if (i4 == 7)
                        htmltext = htmltext.replace("<?Cell4?>", String.valueOf("S"));
                    else if (i4 == 8)
                        htmltext = htmltext.replace("<?Cell4?>", String.valueOf("E"));
                    else if (i4 == 9)
                        htmltext = htmltext.replace("<?Cell4?>", String.valueOf("H"));
                    else if (i4 == 10)
                        htmltext = htmltext.replace("<?Cell4?>", String.valueOf("A"));
                    else if (i4 == 11)
                        htmltext = htmltext.replace("<?Cell4?>", String.valueOf("R"));
                    else if (i4 == 12)
                        htmltext = htmltext.replace("<?Cell4?>", String.valueOf("D"));
                    else if (i4 == 13)
                        htmltext = htmltext.replace("<?Cell4?>", String.valueOf("I"));
                    else if (i4 == 14)
                        htmltext = htmltext.replace("<?Cell4?>", String.valueOf("N"));
                }
                if (i9 % 32 < 16) {
                    htmltext = htmltext.replace("<?FontColor5?>", String.valueOf("ffff00"));
                    htmltext = htmltext.replace("<?Cell5?>", String.valueOf("?"));
                } else {
                    htmltext = htmltext.replace("<?FontColor5?>", String.valueOf("ff6f6f"));
                    if (i5 == 1)
                        htmltext = htmltext.replace("<?Cell5?>", String.valueOf("!"));
                    else if (i5 == 2)
                        htmltext = htmltext.replace("<?Cell5?>", String.valueOf("="));
                    else if (i5 == 3)
                        htmltext = htmltext.replace("<?Cell5?>", String.valueOf("T"));
                    else if (i5 == 4)
                        htmltext = htmltext.replace("<?Cell5?>", String.valueOf("V"));
                    else if (i5 == 5)
                        htmltext = htmltext.replace("<?Cell5?>", String.valueOf("O"));
                    else if (i5 == 6)
                        htmltext = htmltext.replace("<?Cell5?>", String.valueOf("P"));
                    else if (i5 == 7)
                        htmltext = htmltext.replace("<?Cell5?>", String.valueOf("S"));
                    else if (i5 == 8)
                        htmltext = htmltext.replace("<?Cell5?>", String.valueOf("E"));
                    else if (i5 == 9)
                        htmltext = htmltext.replace("<?Cell5?>", String.valueOf("H"));
                    else if (i5 == 10)
                        htmltext = htmltext.replace("<?Cell5?>", String.valueOf("A"));
                    else if (i5 == 11)
                        htmltext = htmltext.replace("<?Cell5?>", String.valueOf("R"));
                    else if (i5 == 12)
                        htmltext = htmltext.replace("<?Cell5?>", String.valueOf("D"));
                    else if (i5 == 13)
                        htmltext = htmltext.replace("<?Cell5?>", String.valueOf("I"));
                    else if (i5 == 14)
                        htmltext = htmltext.replace("<?Cell5?>", String.valueOf("N"));
                }
            } else if (event.equalsIgnoreCase("reply_20")) {
                if (st.ownItemCount(q_poker_chip) >= 50)
                    htmltext = "warehouse_chief_klump_q0662_20.htm";
                else if (st.ownItemCount(q_poker_chip) < 50)
                    htmltext = "warehouse_chief_klump_q0662_21.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("lets_do_card_game");
        int GetMemoStateEx = st.getInt("lets_do_card_game_ex");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == warehouse_chief_klump) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "warehouse_chief_klump_q0662_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "warehouse_chief_klump_q0662_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == warehouse_chief_klump) {
                    if (GetMemoState == 0 && GetMemoStateEx == 0 && st.ownItemCount(q_poker_chip) < 50)
                        htmltext = "warehouse_chief_klump_q0662_04.htm";
                    else if (GetMemoState == 0 && GetMemoStateEx == 0 && st.ownItemCount(q_poker_chip) >= 50)
                        htmltext = "warehouse_chief_klump_q0662_05.htm";
                    else if (GetMemoState != 0 || GetMemoStateEx != 0) {
                        int i0 = GetMemoState;
                        int i1 = GetMemoStateEx;
                        int i5 = i1 % 100;
                        int i9 = i1 / 100;
                        i1 = i0 % 100;
                        int i2 = i0 % 10000 / 100;
                        int i3 = i0 % 1000000 / 10000;
                        int i4 = i0 % 100000000 / 1000000;
                        htmltext = HtmCache.getInstance().getHtml("quests/_662_AGameOfCards/warehouse_chief_klump_q0662_11a.htm", st.getPlayer());
                        if (i9 % 2 < 1) {
                            htmltext = htmltext.replace("<?FontColor1?>", String.valueOf("ffff00"));
                            htmltext = htmltext.replace("<?Cell1?>", String.valueOf("?"));
                        } else {
                            htmltext = htmltext.replace("<?FontColor1?>", String.valueOf("ff6f6f"));
                            if (i1 == 1)
                                htmltext = htmltext.replace("<?Cell1?>", String.valueOf("!"));
                            else if (i1 == 2)
                                htmltext = htmltext.replace("<?Cell1?>", String.valueOf("="));
                            else if (i1 == 3)
                                htmltext = htmltext.replace("<?Cell1?>", String.valueOf("T"));
                            else if (i1 == 4)
                                htmltext = htmltext.replace("<?Cell1?>", String.valueOf("V"));
                            else if (i1 == 5)
                                htmltext = htmltext.replace("<?Cell1?>", String.valueOf("O"));
                            else if (i1 == 6)
                                htmltext = htmltext.replace("<?Cell1?>", String.valueOf("P"));
                            else if (i1 == 7)
                                htmltext = htmltext.replace("<?Cell1?>", String.valueOf("S"));
                            else if (i1 == 8)
                                htmltext = htmltext.replace("<?Cell1?>", String.valueOf("E"));
                            else if (i1 == 9)
                                htmltext = htmltext.replace("<?Cell1?>", String.valueOf("H"));
                            else if (i1 == 10)
                                htmltext = htmltext.replace("<?Cell1?>", String.valueOf("A"));
                            else if (i1 == 11)
                                htmltext = htmltext.replace("<?Cell1?>", String.valueOf("R"));
                            else if (i1 == 12)
                                htmltext = htmltext.replace("<?Cell1?>", String.valueOf("D"));
                            else if (i1 == 13)
                                htmltext = htmltext.replace("<?Cell1?>", String.valueOf("I"));
                            else if (i1 == 14)
                                htmltext = htmltext.replace("<?Cell1?>", String.valueOf("N"));
                        }
                        if (i9 % 4 < 2) {
                            htmltext = htmltext.replace("<?FontColor2?>", String.valueOf("ffff00"));
                            htmltext = htmltext.replace("<?Cell2?>", String.valueOf("?"));
                        } else {
                            htmltext = htmltext.replace("<?FontColor2?>", String.valueOf("ff6f6f"));
                            if (i2 == 1)
                                htmltext = htmltext.replace("<?Cell2?>", String.valueOf("!"));
                            else if (i2 == 2)
                                htmltext = htmltext.replace("<?Cell2?>", String.valueOf("="));
                            else if (i2 == 3)
                                htmltext = htmltext.replace("<?Cell2?>", String.valueOf("T"));
                            else if (i2 == 4)
                                htmltext = htmltext.replace("<?Cell2?>", String.valueOf("V"));
                            else if (i2 == 5)
                                htmltext = htmltext.replace("<?Cell2?>", String.valueOf("O"));
                            else if (i2 == 6)
                                htmltext = htmltext.replace("<?Cell2?>", String.valueOf("P"));
                            else if (i2 == 7)
                                htmltext = htmltext.replace("<?Cell2?>", String.valueOf("S"));
                            else if (i2 == 8)
                                htmltext = htmltext.replace("<?Cell2?>", String.valueOf("E"));
                            else if (i2 == 9)
                                htmltext = htmltext.replace("<?Cell2?>", String.valueOf("H"));
                            else if (i2 == 10)
                                htmltext = htmltext.replace("<?Cell2?>", String.valueOf("A"));
                            else if (i2 == 11)
                                htmltext = htmltext.replace("<?Cell2?>", String.valueOf("R"));
                            else if (i2 == 12)
                                htmltext = htmltext.replace("<?Cell2?>", String.valueOf("D"));
                            else if (i2 == 13)
                                htmltext = htmltext.replace("<?Cell2?>", String.valueOf("I"));
                            else if (i2 == 14)
                                htmltext = htmltext.replace("<?Cell2?>", String.valueOf("N"));
                        }
                        if (i9 % 8 < 4) {
                            htmltext = htmltext.replace("<?FontColor3?>", String.valueOf("ffff00"));
                            htmltext = htmltext.replace("<?Cell3?>", String.valueOf("?"));
                        } else {
                            htmltext = htmltext.replace("<?FontColor3?>", String.valueOf("ff6f6f"));
                            if (i3 == 1)
                                htmltext = htmltext.replace("<?Cell3?>", String.valueOf("!"));
                            else if (i3 == 2)
                                htmltext = htmltext.replace("<?Cell3?>", String.valueOf("="));
                            else if (i3 == 3)
                                htmltext = htmltext.replace("<?Cell3?>", String.valueOf("T"));
                            else if (i3 == 4)
                                htmltext = htmltext.replace("<?Cell3?>", String.valueOf("V"));
                            else if (i3 == 5)
                                htmltext = htmltext.replace("<?Cell3?>", String.valueOf("O"));
                            else if (i3 == 6)
                                htmltext = htmltext.replace("<?Cell3?>", String.valueOf("P"));
                            else if (i3 == 7)
                                htmltext = htmltext.replace("<?Cell3?>", String.valueOf("S"));
                            else if (i3 == 8)
                                htmltext = htmltext.replace("<?Cell3?>", String.valueOf("E"));
                            else if (i3 == 9)
                                htmltext = htmltext.replace("<?Cell3?>", String.valueOf("H"));
                            else if (i3 == 10)
                                htmltext = htmltext.replace("<?Cell3?>", String.valueOf("A"));
                            else if (i3 == 11)
                                htmltext = htmltext.replace("<?Cell3?>", String.valueOf("R"));
                            else if (i3 == 12)
                                htmltext = htmltext.replace("<?Cell3?>", String.valueOf("D"));
                            else if (i3 == 13)
                                htmltext = htmltext.replace("<?Cell3?>", String.valueOf("I"));
                            else if (i3 == 14)
                                htmltext = htmltext.replace("<?Cell3?>", String.valueOf("N"));
                        }
                        if (i9 % 16 < 8) {
                            htmltext = htmltext.replace("<?FontColor4?>", String.valueOf("ffff00"));
                            htmltext = htmltext.replace("<?Cell4?>", String.valueOf("?"));
                        } else {
                            htmltext = htmltext.replace("<?FontColor4?>", String.valueOf("ff6f6f"));
                            if (i4 == 1)
                                htmltext = htmltext.replace("<?Cell4?>", String.valueOf("!"));
                            else if (i4 == 2)
                                htmltext = htmltext.replace("<?Cell4?>", String.valueOf("="));
                            else if (i4 == 3)
                                htmltext = htmltext.replace("<?Cell4?>", String.valueOf("T"));
                            else if (i4 == 4)
                                htmltext = htmltext.replace("<?Cell4?>", String.valueOf("V"));
                            else if (i4 == 5)
                                htmltext = htmltext.replace("<?Cell4?>", String.valueOf("O"));
                            else if (i4 == 6)
                                htmltext = htmltext.replace("<?Cell4?>", String.valueOf("P"));
                            else if (i4 == 7)
                                htmltext = htmltext.replace("<?Cell4?>", String.valueOf("S"));
                            else if (i4 == 8)
                                htmltext = htmltext.replace("<?Cell4?>", String.valueOf("E"));
                            else if (i4 == 9)
                                htmltext = htmltext.replace("<?Cell4?>", String.valueOf("H"));
                            else if (i4 == 10)
                                htmltext = htmltext.replace("<?Cell4?>", String.valueOf("A"));
                            else if (i4 == 11)
                                htmltext = htmltext.replace("<?Cell4?>", String.valueOf("R"));
                            else if (i4 == 12)
                                htmltext = htmltext.replace("<?Cell4?>", String.valueOf("D"));
                            else if (i4 == 13)
                                htmltext = htmltext.replace("<?Cell4?>", String.valueOf("I"));
                            else if (i4 == 14)
                                htmltext = htmltext.replace("<?Cell4?>", String.valueOf("N"));
                        }
                        if (i9 % 32 < 16) {
                            htmltext = htmltext.replace("<?FontColor5?>", String.valueOf("ffff00"));
                            htmltext = htmltext.replace("<?Cell5?>", String.valueOf("?"));
                        } else {
                            htmltext = htmltext.replace("<?FontColor5?>", String.valueOf("ff6f6f"));
                            if (i5 == 1)
                                htmltext = htmltext.replace("<?Cell5?>", String.valueOf("!"));
                            else if (i5 == 2)
                                htmltext = htmltext.replace("<?Cell5?>", String.valueOf("="));
                            else if (i5 == 3)
                                htmltext = htmltext.replace("<?Cell5?>", String.valueOf("T"));
                            else if (i5 == 4)
                                htmltext = htmltext.replace("<?Cell5?>", String.valueOf("V"));
                            else if (i5 == 5)
                                htmltext = htmltext.replace("<?Cell5?>", String.valueOf("O"));
                            else if (i5 == 6)
                                htmltext = htmltext.replace("<?Cell5?>", String.valueOf("P"));
                            else if (i5 == 7)
                                htmltext = htmltext.replace("<?Cell5?>", String.valueOf("S"));
                            else if (i5 == 8)
                                htmltext = htmltext.replace("<?Cell5?>", String.valueOf("E"));
                            else if (i5 == 9)
                                htmltext = htmltext.replace("<?Cell5?>", String.valueOf("H"));
                            else if (i5 == 10)
                                htmltext = htmltext.replace("<?Cell5?>", String.valueOf("A"));
                            else if (i5 == 11)
                                htmltext = htmltext.replace("<?Cell5?>", String.valueOf("R"));
                            else if (i5 == 12)
                                htmltext = htmltext.replace("<?Cell5?>", String.valueOf("D"));
                            else if (i5 == 13)
                                htmltext = htmltext.replace("<?Cell5?>", String.valueOf("I"));
                            else if (i5 == 14)
                                htmltext = htmltext.replace("<?Cell5?>", String.valueOf("N"));
                        }
                    }
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == trives) {
            if (Rnd.get(1000) < 357) {
                st.giveItems(q_poker_chip, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == falibati) {
            if (Rnd.get(1000) < 373) {
                st.giveItems(q_poker_chip, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == doom_knight) {
            if (Rnd.get(1000) < 583) {
                st.giveItems(q_poker_chip, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == tulben) {
            if (Rnd.get(1000) < 435) {
                st.giveItems(q_poker_chip, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == tulben || npcId == ghost_war) {
            if (Rnd.get(1000) < 358) {
                st.giveItems(q_poker_chip, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == blade_death) {
            if (Rnd.get(1000) < 283) {
                st.giveItems(q_poker_chip, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == dark_guard) {
            if (Rnd.get(1000) < 455) {
                st.giveItems(q_poker_chip, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == bloody_knight) {
            if (Rnd.get(1000) < 365) {
                st.giveItems(q_poker_chip, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == bloody_priest) {
            if (Rnd.get(1000) < 348) {
                st.giveItems(q_poker_chip, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == chimera_piece) {
            if (Rnd.get(1000) < 457) {
                st.giveItems(q_poker_chip, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == black_shadow) {
            if (Rnd.get(1000) < 493) {
                st.giveItems(q_poker_chip, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == nonexistent_man) {
            if (Rnd.get(1000) < 418) {
                st.giveItems(q_poker_chip, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == ancients_shaman) {
            if (Rnd.get(1000) < 35) {
                st.giveItems(q_poker_chip, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == forgetten_ancients) {
            if (Rnd.get(1000) < 453) {
                st.giveItems(q_poker_chip, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == doom_scout) {
            if (Rnd.get(1000) < 315) {
                st.giveItems(q_poker_chip, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == dismal_pole) {
            if (Rnd.get(1000) < 32) {
                st.giveItems(q_poker_chip, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == doom_servant) {
            if (Rnd.get(1000) < 335) {
                st.giveItems(q_poker_chip, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == doom_archer) {
            if (Rnd.get(1000) < 462) {
                st.giveItems(q_poker_chip, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == doom_warrior) {
            if (Rnd.get(1000) < 397) {
                st.giveItems(q_poker_chip, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == hames_orc_scout) {
            if (Rnd.get(1000) < 507) {
                st.giveItems(q_poker_chip, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == hames_orc_footman) {
            if (Rnd.get(1000) < 552) {
                st.giveItems(q_poker_chip, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == cursed_guardian) {
            if (Rnd.get(1000) < 587) {
                st.giveItems(q_poker_chip, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == hames_orc_chieftain) {
            if (Rnd.get(1000) < 812) {
                st.giveItems(q_poker_chip, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == antelope || npcId == antelope_a || npcId == antelope_b) {
            if (Rnd.get(1000) < 483) {
                st.giveItems(q_poker_chip, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == buffalo || npcId == buffalo_a || npcId == buffalo_b) {
            if (Rnd.get(1000) < 515) {
                st.giveItems(q_poker_chip, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == splinter_stakato) {
            if (Rnd.get(1000) < 493) {
                st.giveItems(q_poker_chip, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == splinter_stakato_soldier) {
            if (Rnd.get(1000) < 527) {
                st.giveItems(q_poker_chip, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == needle_stakato_soldier) {
            if (Rnd.get(1000) < 598) {
                st.giveItems(q_poker_chip, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == brilliant_eye) {
            if (Rnd.get(1000) < 458) {
                st.giveItems(q_poker_chip, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == brilliant_wisdom) {
            if (Rnd.get(1000) < 552) {
                st.giveItems(q_poker_chip, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == brilliant_legacy) {
            if (Rnd.get(1000) < 488) {
                st.giveItems(q_poker_chip, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == brilliant_mark) {
            if (Rnd.get(1000) < 573) {
                st.giveItems(q_poker_chip, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == bloody_queen) {
            if (Rnd.get(1000) < 232) {
                st.giveItems(q_poker_chip, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }
}