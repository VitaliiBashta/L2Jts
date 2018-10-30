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
 * @version 1.0
 * @date 15/04/2016
 * @lastedit 15/04/2016
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _344_1000YearsEndofLamentation extends Quest {
    // npc
    private static final int watcher_antaras_gilmore = 30754;
    private static final int sir_kristof_rodemai = 30756;
    private static final int highpriest_orven = 30857;
    private static final int duelist_kaien = 30623;
    private static final int high_prefect_garvarentz = 30704;

    // mobs
    private static final int cave_servant = 20236;
    private static final int cave_servant_archer = 20237;
    private static final int cave_servant_warrior = 20238;
    private static final int cave_servant_captain = 20239;
    private static final int cave_servant_hold = 20272;
    private static final int cave_servant_archer_hold = 20273;
    private static final int h_cave_servant_warrior = 20274;
    private static final int h_cave_servant_captain = 20275;

    // questitem
    private static final int q0344_left_thing_of_heroes = 4269;
    private static final int q0344_old_key = 4270;
    private static final int q0344_old_hilt = 4271;
    private static final int q0344_totem_necklace = 4272;
    private static final int q0344_holy_symbol_of_light = 4273;

    // etcitem
    private static final int thons = 4044;
    private static final int asofe = 4043;
    private static final int enria = 4042;
    private static final int cokes = 1879;
    private static final int scrl_of_ench_wp_c = 951;
    private static final int ring_of_ages = 885;
    private static final int stone_of_purity = 1875;
    private static final int scrl_of_ench_am_c = 952;
    private static final int drake_leather_boots = 2437;
    private static final int oriharukon_ore = 1874;
    private static final int varnish_of_purity = 1887;
    private static final int raid_sword = 133;
    private static final int leather = 1882;
    private static final int coarse_bone_powder = 1881;
    private static final int heavy_doom_hammer = 191;

    public _344_1000YearsEndofLamentation() {
        super(false);
        addStartNpc(watcher_antaras_gilmore);
        addTalkId(sir_kristof_rodemai, highpriest_orven, duelist_kaien, high_prefect_garvarentz);
        addKillId(cave_servant, cave_servant_archer, cave_servant_warrior, cave_servant_captain, cave_servant_hold, cave_servant_archer_hold, h_cave_servant_warrior, h_cave_servant_captain);
        addQuestItem(q0344_left_thing_of_heroes, q0344_old_key, q0344_old_hilt, q0344_totem_necklace, q0344_holy_symbol_of_light);

        addLevelCheck(48, 55);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("end_of_millennial_regret");
        int npcId = npc.getNpcId();
        if (npcId == watcher_antaras_gilmore) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("end_of_millennial_regret", String.valueOf(0), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "watcher_antaras_gilmore_q0344_03.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=344&reply=1")) {
                st.setMemoState("end_of_millennial_regret", String.valueOf(1), true);
                htmltext = "watcher_antaras_gilmore_q0344_04.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=344&reply=2")) {
                if (st.ownItemCount(q0344_left_thing_of_heroes) > 0) {
                    if (Rnd.get(1000) >= st.ownItemCount(q0344_left_thing_of_heroes)) {
                        st.giveItems(ADENA_ID, st.ownItemCount(q0344_left_thing_of_heroes) * 60);
                        st.takeItems(q0344_left_thing_of_heroes, -1);
                        htmltext = "watcher_antaras_gilmore_q0344_07.htm";
                    } else {
                        st.setMemoState("end_of_millennial_regret_ex", String.valueOf(st.ownItemCount(q0344_left_thing_of_heroes)), true);
                        st.takeItems(q0344_left_thing_of_heroes, -1);
                        st.setMemoState("end_of_millennial_regret", String.valueOf(3), true);
                        htmltext = "watcher_antaras_gilmore_q0344_08.htm";
                    }
                } else {
                    htmltext = "watcher_antaras_gilmore_q0344_06t.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=344&reply=5")) {
                if (GetMemoState == 3) {
                    int i0 = Rnd.get(100);
                    if (i0 <= 24) {
                        st.giveItems(q0344_old_key, 1);
                        st.setMemoState("end_of_millennial_regret", String.valueOf(4), true);
                        st.setCond(2);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "watcher_antaras_gilmore_q0344_09.htm";
                    } else if (i0 <= 49) {
                        st.giveItems(q0344_old_hilt, 1);
                        st.setMemoState("end_of_millennial_regret", String.valueOf(4), true);
                        st.setCond(2);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "watcher_antaras_gilmore_q0344_10.htm";
                    } else if (i0 <= 74) {
                        st.giveItems(q0344_totem_necklace, 1);
                        st.setMemoState("end_of_millennial_regret", String.valueOf(4), true);
                        st.setCond(2);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "watcher_antaras_gilmore_q0344_11.htm";
                    } else {
                        st.giveItems(q0344_holy_symbol_of_light, 1);
                        st.setMemoState("end_of_millennial_regret", String.valueOf(4), true);
                        st.setCond(2);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "watcher_antaras_gilmore_q0344_12.htm";
                    }
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=344&reply=3")) {
                st.setMemoState("end_of_millennial_regret", String.valueOf(1), true);
                htmltext = "watcher_antaras_gilmore_q0344_15.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=344&reply=4")) {
                st.exitQuest(true);
                st.soundEffect(SOUND_FINISH);
                htmltext = "watcher_antaras_gilmore_q0344_16.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("end_of_millennial_regret");
        int GetMemoStateEx = st.getInt("end_of_millennial_regret_ex");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == watcher_antaras_gilmore) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "watcher_antaras_gilmore_q0344_01.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "watcher_antaras_gilmore_q0344_02.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == watcher_antaras_gilmore) {
                    if (GetMemoState == 0) {
                        st.setMemoState("end_of_millennial_regret", String.valueOf(1), true);
                        htmltext = "watcher_antaras_gilmore_q0344_04.htm";
                    } else if (GetMemoState == 1 || (GetMemoState == 2 && st.ownItemCount(q0344_left_thing_of_heroes) == 0))
                        htmltext = "watcher_antaras_gilmore_q0344_05.htm";
                    else if (GetMemoState == 2 && st.ownItemCount(q0344_left_thing_of_heroes) > 0)
                        htmltext = "watcher_antaras_gilmore_q0344_06.htm";
                    else if (GetMemoState == 3) {
                        int i0 = Rnd.get(100);
                        if (i0 <= 24) {
                            st.giveItems(q0344_old_key, 1);
                            st.setMemoState("end_of_millennial_regret", String.valueOf(4), true);
                            st.setCond(2);
                            st.soundEffect(SOUND_MIDDLE);
                            htmltext = "watcher_antaras_gilmore_q0344_09.htm";
                        } else if (i0 <= 49) {
                            st.giveItems(q0344_old_hilt, 1);
                            st.setMemoState("end_of_millennial_regret", String.valueOf(4), true);
                            st.setCond(2);
                            st.soundEffect(SOUND_MIDDLE);
                            htmltext = "watcher_antaras_gilmore_q0344_10.htm";
                        } else if (i0 <= 74) {
                            st.giveItems(q0344_totem_necklace, 1);
                            st.setMemoState("end_of_millennial_regret", String.valueOf(4), true);
                            st.setCond(2);
                            st.soundEffect(SOUND_MIDDLE);
                            htmltext = "watcher_antaras_gilmore_q0344_11.htm";
                        } else {
                            st.giveItems(q0344_holy_symbol_of_light, 1);
                            st.setMemoState("end_of_millennial_regret", String.valueOf(4), true);
                            st.setCond(2);
                            st.soundEffect(SOUND_MIDDLE);
                            htmltext = "watcher_antaras_gilmore_q0344_12.htm";
                        }
                    } else if (GetMemoState == 4)
                        htmltext = "watcher_antaras_gilmore_q0344_13.htm";
                    else if (GetMemoState >= 5 && GetMemoState <= 8) {
                        if (GetMemoState == 5) {
                            st.giveItems(ADENA_ID, GetMemoStateEx * 50 + 1500);
                        } else if (GetMemoState == 6) {
                            st.giveItems(ADENA_ID, GetMemoStateEx * 50);
                            st.giveItems(thons, 1);
                        } else if (GetMemoState == 7) {
                            st.giveItems(ADENA_ID, GetMemoStateEx * 50);
                            st.giveItems(asofe, 1);
                        } else if (GetMemoState == 8) {
                            st.giveItems(ADENA_ID, GetMemoStateEx * 50);
                            st.giveItems(enria, 1);
                        }
                        st.setMemoState("end_of_millennial_regret_ex", String.valueOf(0), true);
                        st.setMemoState("end_of_millennial_regret", String.valueOf(1), true);
                        htmltext = "watcher_antaras_gilmore_q0344_14.htm";
                    }
                } else if (npcId == sir_kristof_rodemai) {
                    if (st.ownItemCount(q0344_old_key) >= 1) {
                        int i0 = Rnd.get(100);
                        if (i0 <= 39) {
                            st.giveItems(cokes, 55);
                        } else if (i0 <= 89) {
                            st.giveItems(scrl_of_ench_wp_c, 1);
                        } else {
                            st.giveItems(ring_of_ages, 1);
                        }
                        st.takeItems(q0344_old_key, -1);
                        st.setMemoState("end_of_millennial_regret", String.valueOf(5), true);
                        htmltext = "sir_kristof_rodemai_q0344_01.htm";
                    } else if (GetMemoState == 5)
                        htmltext = "sir_kristof_rodemai_q0344_02.htm";
                } else if (npcId == highpriest_orven) {
                    if (st.ownItemCount(q0344_holy_symbol_of_light) >= 1) {
                        int i0 = Rnd.get(100);
                        if (i0 <= 49) {
                            st.giveItems(stone_of_purity, 19);
                        } else if (i0 <= 69) {
                            st.giveItems(scrl_of_ench_am_c, 5);
                        } else {
                            st.giveItems(drake_leather_boots, 1);
                        }
                        st.takeItems(q0344_holy_symbol_of_light, -1);
                        st.setMemoState("end_of_millennial_regret", String.valueOf(8), true);
                        htmltext = "highpriest_orven_q0344_01.htm";
                    } else if (GetMemoState == 8)
                        htmltext = "highpriest_orven_q0344_02.htm";
                } else if (npcId == duelist_kaien) {
                    if (st.ownItemCount(q0344_old_hilt) >= 1) {
                        int i0 = Rnd.get(100);
                        if (i0 <= 52) {
                            st.giveItems(oriharukon_ore, 25);
                        } else if (i0 <= 76) {
                            st.giveItems(varnish_of_purity, 10);
                        } else if (i0 <= 98) {
                            st.giveItems(scrl_of_ench_wp_c, 1);
                        } else {
                            st.giveItems(raid_sword, 1);
                        }
                        st.takeItems(q0344_old_hilt, -1);
                        st.setMemoState("end_of_millennial_regret", String.valueOf(6), true);
                        htmltext = "duelist_kaien_q0344_01.htm";
                    } else if (GetMemoState == 6)
                        htmltext = "duelist_kaien_q0344_02.htm";
                } else if (npcId == high_prefect_garvarentz) {
                    if (st.ownItemCount(q0344_totem_necklace) >= 1) {
                        int i0 = Rnd.get(100);
                        if (i0 <= 47) {
                            st.giveItems(leather, 70);
                        } else if (i0 <= 97) {
                            st.giveItems(coarse_bone_powder, 50);
                        } else {
                            st.giveItems(heavy_doom_hammer, 1);
                        }
                        st.takeItems(q0344_totem_necklace, -1);
                        st.setMemoState("end_of_millennial_regret", String.valueOf(7), true);
                        htmltext = "high_prefect_garvarentz_q0344_01.htm";
                    } else if (GetMemoState == 7)
                        htmltext = "high_prefect_garvarentz_q0344_02.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("end_of_millennial_regret");
        int npcId = npc.getNpcId();
        if (npcId == cave_servant || npcId == cave_servant_hold) {
            if (GetMemoState == 1 || GetMemoState == 2) {
                if (Rnd.get(100) < 58) {
                    st.giveItems(q0344_left_thing_of_heroes, 1);
                    st.soundEffect(SOUND_ITEMGET);
                    st.setMemoState("end_of_millennial_regret", String.valueOf(2), true);
                }
            }
        } else if (npcId == cave_servant_archer || npcId == cave_servant_archer_hold) {
            if (GetMemoState == 1 || GetMemoState == 2) {
                if (Rnd.get(100) < 78) {
                    st.giveItems(q0344_left_thing_of_heroes, 1);
                    st.soundEffect(SOUND_ITEMGET);
                    st.setMemoState("end_of_millennial_regret", String.valueOf(2), true);
                }
            }
        } else if (npcId == cave_servant_warrior || npcId == h_cave_servant_warrior) {
            if (GetMemoState == 1 || GetMemoState == 2) {
                if (Rnd.get(100) < 75) {
                    st.giveItems(q0344_left_thing_of_heroes, 1);
                    st.soundEffect(SOUND_ITEMGET);
                    st.setMemoState("end_of_millennial_regret", String.valueOf(2), true);
                }
            }
        } else if (npcId == cave_servant_captain || npcId == h_cave_servant_captain) {
            if (GetMemoState == 1 || GetMemoState == 2) {
                if (Rnd.get(100) < 79) {
                    st.giveItems(q0344_left_thing_of_heroes, 1);
                    st.soundEffect(SOUND_ITEMGET);
                    st.setMemoState("end_of_millennial_regret", String.valueOf(2), true);
                }
            }
        }
        return null;
    }
}