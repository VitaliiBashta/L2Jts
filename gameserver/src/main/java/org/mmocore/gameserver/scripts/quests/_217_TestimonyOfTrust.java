package org.mmocore.gameserver.scripts.quests;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.serverpackets.SocialAction;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.0
 * @date 12/09/2016
 * @lastedit 14/09/2016
 * @Tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _217_TestimonyOfTrust extends Quest {
    //npc
    private static final int cardinal_seresin = 30657;
    private static final int first_elder_lockirin = 30531;
    private static final int hollin = 30191;
    private static final int kakai_the_lord_of_flame = 30565;
    private static final int maestro_nikola = 30621;
    private static final int magister_clayton = 30464;
    private static final int ozzy = 30154;
    private static final int quilt = 30031;
    private static final int seer_manakia = 30515;
    private static final int tetrarch_thifiell = 30358;
    //mobs
    private static final int dryad = 20013;
    private static final int dryad_ribe = 20019;
    private static final int lirein = 20036;
    private static final int lirein_ribe = 20044;
    private static final int ant_recruit = 20082;
    private static final int ant_patrol = 20084;
    private static final int ant_guard = 20086;
    private static final int ant_soldier = 20087;
    private static final int ant_warrior_captain = 20088;
    private static final int marsh_stakato = 20157;
    private static final int porta = 20213;
    private static final int marsh_stakato_worker = 20230;
    private static final int marsh_stakato_soldier = 20232;
    private static final int marsh_stakato_drone = 20234;
    private static final int guardian_basilisk = 20550;
    private static final int windsus = 20553;
    private static final int luell_of_zephyr_winds = 27120;
    private static final int actea_of_verdant_wilds = 27121;
    //questitem
    private static final int mark_of_trust = 2734;
    private static final int letter_to_elf = 2735;
    private static final int letter_to_darkelf = 2736;
    private static final int letter_to_dwarf = 2737;
    private static final int letter_to_orc = 2738;
    private static final int letter_to_seresin = 2739;
    private static final int scroll_of_darkelf_trust = 2740;
    private static final int scroll_of_elf_trust = 2741;
    private static final int scroll_of_dwarf_trust = 2742;
    private static final int scroll_of_orc_trust = 2743;
    private static final int recommendation_of_hollin = 2744;
    private static final int order_of_ozzy = 2745;
    private static final int breath_of_winds = 2746;
    private static final int seed_of_verdure = 2747;
    private static final int letter_of_thifiell = 2748;
    private static final int blood_of_guardian_basilisk = 2749;
    private static final int giant_aphid = 2750;
    private static final int stakatos_fluids = 2751;
    private static final int basilisk_plasma = 2752;
    private static final int honey_dew = 2753;
    private static final int stakato_ichor = 2754;
    private static final int order_of_clayton = 2755;
    private static final int parasite_of_lota = 2756;
    private static final int letter_to_manakia = 2757;
    private static final int letter_of_manakia = 2758;
    private static final int letter_to_nichola = 2759;
    private static final int order_of_nichola = 2760;
    private static final int heart_of_porta = 2761;
    //etcitem
    private static final int q_dimension_diamond = 7562;

    private static int c1_flag;

    public _217_TestimonyOfTrust() {
        super(false);
        addStartNpc(hollin);
        addTalkId(ozzy, tetrarch_thifiell, magister_clayton, cardinal_seresin, kakai_the_lord_of_flame, seer_manakia, first_elder_lockirin, maestro_nikola, quilt);
        addKillId(dryad, dryad_ribe, lirein, lirein_ribe, ant_recruit, ant_patrol, ant_guard, ant_soldier, ant_warrior_captain, marsh_stakato, porta, marsh_stakato_worker, marsh_stakato_soldier, marsh_stakato_drone, guardian_basilisk, windsus, luell_of_zephyr_winds, actea_of_verdant_wilds);
        addQuestItem(letter_to_elf, letter_to_darkelf, letter_to_dwarf, letter_to_orc, letter_to_seresin, scroll_of_darkelf_trust, scroll_of_elf_trust, scroll_of_dwarf_trust, scroll_of_orc_trust, recommendation_of_hollin, order_of_ozzy, breath_of_winds, seed_of_verdure, letter_of_thifiell, blood_of_guardian_basilisk, giant_aphid, stakatos_fluids, basilisk_plasma, honey_dew, stakato_ichor, order_of_clayton, parasite_of_lota, letter_to_manakia, letter_of_manakia, letter_to_nichola, order_of_nichola, heart_of_porta);
        addLevelCheck(37);
        addClassLevelCheck(1);
        addRaceCheck(PlayerRace.human);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("testimoney_of_trust");
        int npcId = npc.getNpcId();
        if (npcId == hollin) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("testimoney_of_trust", String.valueOf(1), true);
                st.giveItems(letter_to_elf, 1);
                st.giveItems(letter_to_darkelf, 1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                if (!st.getPlayer().getPlayerVariables().getBoolean(PlayerVariables.DD2)) {
                    st.giveItems(q_dimension_diamond, 96);
                    st.getPlayer().getPlayerVariables().set(PlayerVariables.DD2, "1", -1);
                    htmltext = "hollin_q0217_04a.htm";
                } else
                    htmltext = "hollin_q0217_04.htm";
            }
        } else if (npcId == ozzy) {
            if (event.equalsIgnoreCase("menu_select?ask=217&reply=1"))
                htmltext = "ozzy_q0217_02.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=217&reply=2") && st.ownItemCount(letter_to_elf) > 0) {
                st.setCond(2);
                st.setMemoState("testimoney_of_trust", String.valueOf(2), true);
                st.giveItems(order_of_ozzy, 1);
                st.takeItems(letter_to_elf, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "ozzy_q0217_03.htm";
            }
        } else if (npcId == tetrarch_thifiell) {
            if (event.equalsIgnoreCase("menu_select?ask=217&reply=1") && st.ownItemCount(letter_to_darkelf) > 0) {
                st.setCond(5);
                st.setMemoState("testimoney_of_trust", String.valueOf(5), true);
                st.giveItems(letter_of_thifiell, 1);
                st.takeItems(letter_to_darkelf, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "tetrarch_thifiell_q0217_02.htm";
            }
        } else if (npcId == cardinal_seresin) {
            if (event.equalsIgnoreCase("menu_select?ask=217&reply=1")) {
                if (GetMemoState == 8 && st.ownItemCount(letter_to_seresin) > 0) {
                    st.setCond(12);
                    st.setMemoState("testimoney_of_trust", String.valueOf(9), true);
                    st.giveItems(letter_to_orc, 1);
                    st.giveItems(letter_to_dwarf, 1);
                    st.takeItems(letter_to_seresin, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "cardinal_seresin_q0217_03.htm";
                }
            }
        } else if (npcId == kakai_the_lord_of_flame) {
            if (event.equalsIgnoreCase("menu_select?ask=217&reply=1") && st.ownItemCount(letter_to_orc) > 0) {
                st.setCond(13);
                st.setMemoState("testimoney_of_trust", String.valueOf(10), true);
                st.giveItems(letter_to_manakia, 1);
                st.takeItems(letter_to_orc, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "kakai_the_lord_of_flame_q0217_02.htm";
            }
        } else if (npcId == seer_manakia) {
            if (event.equalsIgnoreCase("menu_select?ask=217&reply=1") && st.ownItemCount(letter_to_manakia) > 0) {
                st.setCond(14);
                st.setMemoState("testimoney_of_trust", String.valueOf(11), true);
                st.takeItems(letter_to_manakia, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "seer_manakia_q0217_02.htm";
            }
        } else if (npcId == first_elder_lockirin) {
            if (event.equalsIgnoreCase("menu_select?ask=217&reply=1") && st.ownItemCount(letter_to_dwarf) > 0) {
                st.setCond(18);
                st.setMemoState("testimoney_of_trust", String.valueOf(15), true);
                st.giveItems(letter_to_nichola, 1);
                st.takeItems(letter_to_dwarf, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "first_elder_lockirin_q0217_02.htm";
            }
        } else if (npcId == maestro_nikola) {
            if (event.equalsIgnoreCase("menu_select?ask=217&reply=1") && st.ownItemCount(letter_to_nichola) > 0) {
                st.setCond(19);
                st.setMemoState("testimoney_of_trust", String.valueOf(16), true);
                st.giveItems(order_of_nichola, 1);
                st.takeItems(letter_to_nichola, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "maestro_nikola_q0217_02.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("testimoney_of_trust");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == hollin) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "hollin_q0217_01.htm";
                            st.exitQuest(true);
                            break;
                        case CLASS_LEVEL:
                            htmltext = "hollin_q0217_01a.htm";
                            break;
                        case RACE:
                            htmltext = "hollin_q0217_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "hollin_q0217_03.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == hollin) {
                    if (GetMemoState == 7 && st.ownItemCount(scroll_of_elf_trust) > 0 && st.ownItemCount(scroll_of_darkelf_trust) > 0) {
                        st.setCond(10);
                        st.setMemoState("testimoney_of_trust", String.valueOf(8), true);
                        st.giveItems(letter_to_seresin, 1);
                        st.takeItems(scroll_of_darkelf_trust, 1);
                        st.takeItems(scroll_of_elf_trust, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "hollin_q0217_05.htm";
                    } else if (GetMemoState == 18 && st.ownItemCount(scroll_of_dwarf_trust) > 0 && st.ownItemCount(scroll_of_orc_trust) > 0) {
                        st.setCond(23);
                        st.setMemoState("testimoney_of_trust", String.valueOf(19), true);
                        st.giveItems(recommendation_of_hollin, 1);
                        st.takeItems(scroll_of_dwarf_trust, 1);
                        st.takeItems(scroll_of_orc_trust, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "hollin_q0217_06.htm";
                    } else if (GetMemoState == 19)
                        htmltext = "hollin_q0217_07.htm";
                    else if (GetMemoState == 1)
                        htmltext = "hollin_q0217_08.htm";
                    else if (GetMemoState == 8)
                        htmltext = "hollin_q0217_09.htm";
                } else if (npcId == ozzy) {
                    if (GetMemoState == 1 && st.ownItemCount(letter_to_elf) > 0)
                        htmltext = "ozzy_q0217_01.htm";
                    else if (GetMemoState == 2 && st.ownItemCount(order_of_ozzy) > 0)
                        htmltext = "ozzy_q0217_04.htm";
                    else if (GetMemoState == 3 && st.ownItemCount(breath_of_winds) > 0 && st.ownItemCount(seed_of_verdure) > 0) {
                        st.setCond(4);
                        st.setMemoState("testimoney_of_trust", String.valueOf(4), true);
                        st.giveItems(scroll_of_elf_trust, 1);
                        st.takeItems(breath_of_winds, 1);
                        st.takeItems(seed_of_verdure, 1);
                        st.takeItems(order_of_ozzy, -1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "ozzy_q0217_05.htm";
                    } else if (GetMemoState == 4)
                        htmltext = "ozzy_q0217_06.htm";
                } else if (npcId == tetrarch_thifiell) {
                    if (GetMemoState == 4 && st.ownItemCount(letter_to_darkelf) > 0)
                        htmltext = "tetrarch_thifiell_q0217_01.htm";
                    else if (GetMemoState == 6 && st.ownItemCount(order_of_clayton) > 0 && st.ownItemCount(stakato_ichor) + st.ownItemCount(honey_dew) + st.ownItemCount(basilisk_plasma) == 3) {
                        st.setCond(9);
                        st.setMemoState("testimoney_of_trust", String.valueOf(7), true);
                        st.giveItems(scroll_of_darkelf_trust, 1);
                        st.takeItems(order_of_clayton, -1);
                        st.takeItems(basilisk_plasma, -1);
                        st.takeItems(stakato_ichor, -1);
                        st.takeItems(honey_dew, -1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "tetrarch_thifiell_q0217_03.htm";
                    } else if (GetMemoState == 7)
                        htmltext = "tetrarch_thifiell_q0217_04.htm";
                    else if (GetMemoState == 5)
                        htmltext = "tetrarch_thifiell_q0217_05.htm";
                } else if (npcId == magister_clayton) {
                    if (GetMemoState == 5 && st.ownItemCount(letter_of_thifiell) > 0) {
                        st.setCond(6);
                        st.setMemoState("testimoney_of_trust", String.valueOf(6), true);
                        st.giveItems(order_of_clayton, 1);
                        st.takeItems(letter_of_thifiell, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "magister_clayton_q0217_01.htm";
                    } else if (GetMemoState == 6 && st.ownItemCount(order_of_clayton) > 0 && st.ownItemCount(stakato_ichor) + st.ownItemCount(honey_dew) + st.ownItemCount(basilisk_plasma) < 3)
                        htmltext = "magister_clayton_q0217_02.htm";
                    else if (GetMemoState == 6 && st.ownItemCount(order_of_clayton) > 0 && st.ownItemCount(stakato_ichor) + st.ownItemCount(honey_dew) + st.ownItemCount(basilisk_plasma) == 3) {
                        st.setCond(8);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "magister_clayton_q0217_03.htm";
                    }
                } else if (npcId == cardinal_seresin) {
                    if (GetMemoState == 8 && st.ownItemCount(letter_to_seresin) > 0)
                        htmltext = "cardinal_seresin_q0217_01.htm";
                    else if (GetMemoState == 9)
                        htmltext = "cardinal_seresin_q0217_04.htm";
                    else if (GetMemoState == 18)
                        htmltext = "cardinal_seresin_q0217_05.htm";
                } else if (npcId == kakai_the_lord_of_flame) {
                    if (GetMemoState == 9 && st.ownItemCount(letter_to_orc) > 0)
                        htmltext = "kakai_the_lord_of_flame_q0217_01.htm";
                    else if (GetMemoState == 10)
                        htmltext = "kakai_the_lord_of_flame_q0217_03.htm";
                    else if (GetMemoState == 13) {
                        st.setCond(17);
                        st.setMemoState("testimoney_of_trust", String.valueOf(14), true);
                        st.giveItems(scroll_of_orc_trust, 1);
                        st.takeItems(letter_of_manakia, -1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "kakai_the_lord_of_flame_q0217_04.htm";
                    } else if (GetMemoState == 14)
                        htmltext = "kakai_the_lord_of_flame_q0217_05.htm";
                } else if (npcId == seer_manakia) {
                    if (st.ownItemCount(letter_to_manakia) > 0)
                        htmltext = "seer_manakia_q0217_01.htm";
                    else if (GetMemoState == 11)
                        htmltext = "seer_manakia_q0217_03.htm";
                    else if (GetMemoState == 12 && st.ownItemCount(parasite_of_lota) == 10) {
                        st.setCond(16);
                        st.setMemoState("testimoney_of_trust", String.valueOf(13), true);
                        st.giveItems(letter_of_manakia, 1);
                        st.takeItems(parasite_of_lota, -1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "seer_manakia_q0217_04.htm";
                    } else if (GetMemoState == 13)
                        htmltext = "seer_manakia_q0217_05.htm";
                } else if (npcId == first_elder_lockirin) {
                    if (GetMemoState == 14 && st.ownItemCount(letter_to_dwarf) > 0)
                        htmltext = "first_elder_lockirin_q0217_01.htm";
                    else if (GetMemoState == 15)
                        htmltext = "first_elder_lockirin_q0217_03.htm";
                    else if (GetMemoState == 17) {
                        st.setCond(22);
                        st.setMemoState("testimoney_of_trust", String.valueOf(18), true);
                        st.giveItems(scroll_of_dwarf_trust, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "first_elder_lockirin_q0217_04.htm";
                    } else if (GetMemoState == 18)
                        htmltext = "first_elder_lockirin_q0217_05.htm";
                } else if (npcId == maestro_nikola) {
                    if (GetMemoState == 15 && st.ownItemCount(letter_to_nichola) > 0)
                        htmltext = "maestro_nikola_q0217_01.htm";
                    else if (GetMemoState == 16 && st.ownItemCount(heart_of_porta) < 1)
                        htmltext = "maestro_nikola_q0217_03.htm";
                    else if (GetMemoState == 16 && st.ownItemCount(heart_of_porta) == 1) {
                        st.setCond(21);
                        st.setMemoState("testimoney_of_trust", String.valueOf(17), true);
                        st.takeItems(heart_of_porta, -1);
                        st.takeItems(order_of_nichola, -1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "maestro_nikola_q0217_04.htm";
                    } else if (GetMemoState == 17)
                        htmltext = "maestro_nikola_q0217_05.htm";
                } else if (npcId == quilt) {
                    if (GetMemoState == 19 && st.ownItemCount(recommendation_of_hollin) > 0) {
                        st.addExpAndSp(1390298, 92782);
                        st.giveItems(ADENA_ID, 252212);
                        st.takeItems(recommendation_of_hollin, 1);
                        st.giveItems(mark_of_trust, 1);
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                        st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                        htmltext = "quilt_q0217_01.htm";
                    }
                }
                break;
            case COMPLETED:
                if (npcId == hollin) {
                    htmltext = "hollin_q0217_01b.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("testimoney_of_trust");
        int npcId = npc.getNpcId();
        if (npcId == dryad || npcId == dryad_ribe) {
            if (GetMemoState == 2) {
                c1_flag++;
                if (Rnd.get(100) < c1_flag * 33) {
                    st.addSpawn(actea_of_verdant_wilds);
                    st.soundEffect(SOUND_BEFORE_BATTLE);
                    c1_flag = 0;
                }
            }
        } else if (npcId == lirein || npcId == lirein_ribe) {
            if (GetMemoState == 2) {
                c1_flag++;
                if (Rnd.get(100) < c1_flag * 33) {
                    st.addSpawn(luell_of_zephyr_winds);
                    st.soundEffect(SOUND_BEFORE_BATTLE);
                    c1_flag = 0;
                }
            }
        } else if (npcId == ant_recruit) {
            if (GetMemoState == 6 && st.ownItemCount(giant_aphid) < 5 && st.ownItemCount(order_of_clayton) > 0 && st.ownItemCount(honey_dew) == 0) {
                if (st.ownItemCount(giant_aphid) >= 4) {
                    st.giveItems(honey_dew, 1);
                    st.takeItems(giant_aphid, -1);
                    st.soundEffect(SOUND_MIDDLE);
                    if (st.ownItemCount(basilisk_plasma) >= 1 && st.ownItemCount(stakato_ichor) >= 1) {
                        st.setCond(7);
                    }
                } else {
                    st.giveItems(giant_aphid, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == ant_patrol) {
            if (GetMemoState == 6 && st.ownItemCount(giant_aphid) < 10 && st.ownItemCount(order_of_clayton) > 0 && st.ownItemCount(honey_dew) == 0) {
                if (st.ownItemCount(giant_aphid) >= 4) {
                    st.giveItems(honey_dew, 1);
                    st.takeItems(giant_aphid, -1);
                    st.soundEffect(SOUND_MIDDLE);
                    if (st.ownItemCount(basilisk_plasma) >= 1 && st.ownItemCount(stakato_ichor) >= 1) {
                        st.setCond(7);
                    }
                } else {
                    st.giveItems(giant_aphid, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == ant_guard) {
            if (GetMemoState == 6 && st.ownItemCount(giant_aphid) < 5 && st.ownItemCount(order_of_clayton) > 0 && st.ownItemCount(honey_dew) == 0) {
                if (st.ownItemCount(giant_aphid) >= 4) {
                    st.giveItems(honey_dew, 1);
                    st.takeItems(giant_aphid, -1);
                    st.soundEffect(SOUND_MIDDLE);
                    if (st.ownItemCount(basilisk_plasma) >= 1 && st.ownItemCount(stakato_ichor) >= 1) {
                        st.setCond(7);
                    }
                } else {
                    st.giveItems(giant_aphid, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == ant_soldier) {
            if (GetMemoState == 6 && st.ownItemCount(giant_aphid) < 10 && st.ownItemCount(order_of_clayton) > 0 && st.ownItemCount(honey_dew) == 0) {
                if (st.ownItemCount(giant_aphid) >= 4) {
                    st.giveItems(honey_dew, 1);
                    st.takeItems(giant_aphid, -1);
                    st.soundEffect(SOUND_MIDDLE);
                    if (st.ownItemCount(basilisk_plasma) >= 1 && st.ownItemCount(stakato_ichor) >= 1) {
                        st.setCond(7);
                    }
                } else {
                    st.giveItems(giant_aphid, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == ant_warrior_captain) {
            if (GetMemoState == 6 && st.ownItemCount(giant_aphid) < 10 && st.ownItemCount(order_of_clayton) > 0 && st.ownItemCount(honey_dew) == 0) {
                if (st.ownItemCount(giant_aphid) >= 4) {
                    st.giveItems(honey_dew, 1);
                    st.takeItems(giant_aphid, -1);
                    st.soundEffect(SOUND_MIDDLE);
                    if (st.ownItemCount(basilisk_plasma) >= 1 && st.ownItemCount(stakato_ichor) >= 1) {
                        st.setCond(7);
                    }
                } else {
                    st.giveItems(giant_aphid, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == marsh_stakato || npcId == marsh_stakato_worker) {
            if (GetMemoState == 6 && st.ownItemCount(stakatos_fluids) < 10 && st.ownItemCount(order_of_clayton) > 0 && st.ownItemCount(stakato_ichor) == 0) {
                if (st.ownItemCount(stakatos_fluids) >= 4) {
                    st.giveItems(stakato_ichor, 1);
                    st.takeItems(stakatos_fluids, -1);
                    st.soundEffect(SOUND_MIDDLE);
                    if (st.ownItemCount(basilisk_plasma) >= 1 && st.ownItemCount(stakato_ichor) >= 1) {
                        st.setCond(7);
                    }
                } else {
                    st.giveItems(stakatos_fluids, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == porta) {
            if (GetMemoState == 16 && st.ownItemCount(heart_of_porta) < 1) {
                if (st.ownItemCount(heart_of_porta) >= 0) {
                    st.setCond(20);
                    st.giveItems(heart_of_porta, 1);
                    st.soundEffect(SOUND_MIDDLE);
                } else {
                    st.giveItems(heart_of_porta, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == marsh_stakato_soldier || npcId == marsh_stakato_drone) {
            if (GetMemoState == 6 && st.ownItemCount(stakatos_fluids) < 5 && st.ownItemCount(order_of_clayton) > 0 && st.ownItemCount(stakato_ichor) == 0) {
                if (st.ownItemCount(stakatos_fluids) >= 4) {
                    st.giveItems(stakato_ichor, 1);
                    st.takeItems(stakatos_fluids, -1);
                    st.soundEffect(SOUND_MIDDLE);
                    if (st.ownItemCount(basilisk_plasma) >= 1 && st.ownItemCount(stakato_ichor) >= 1) {
                        st.setCond(7);
                    }
                } else {
                    st.giveItems(stakatos_fluids, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == guardian_basilisk) {
            if (GetMemoState == 6 && st.ownItemCount(blood_of_guardian_basilisk) < 10 && st.ownItemCount(order_of_clayton) > 0 && st.ownItemCount(basilisk_plasma) == 0) {
                if (st.ownItemCount(blood_of_guardian_basilisk) >= 4) {
                    st.giveItems(basilisk_plasma, 1);
                    st.takeItems(blood_of_guardian_basilisk, -1);
                    st.soundEffect(SOUND_MIDDLE);
                    if (st.ownItemCount(stakato_ichor) >= 1 && st.ownItemCount(honey_dew) >= 1) {
                        st.setCond(7);
                    }
                } else {
                    st.giveItems(blood_of_guardian_basilisk, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == windsus) {
            if (GetMemoState == 11 && st.ownItemCount(parasite_of_lota) < 10) {
                if (Rnd.get(2) <= 1) {
                    if (st.ownItemCount(parasite_of_lota) >= 8) {
                        st.setCond(15);
                        st.setMemoState("testimoney_of_trust", String.valueOf(12), true);
                        st.giveItems(parasite_of_lota, 2);
                        st.soundEffect(SOUND_MIDDLE);
                    } else {
                        st.giveItems(parasite_of_lota, 2);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        } else if (npcId == luell_of_zephyr_winds) {
            if (GetMemoState == 2 && st.ownItemCount(breath_of_winds) == 0) {
                if (st.ownItemCount(seed_of_verdure) > 0) {
                    st.setCond(3);
                    st.setMemoState("testimoney_of_trust", String.valueOf(3), true);
                    st.giveItems(breath_of_winds, 1);
                    st.soundEffect(SOUND_MIDDLE);
                } else {
                    st.giveItems(breath_of_winds, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == actea_of_verdant_wilds) {
            if (GetMemoState == 2 && st.ownItemCount(seed_of_verdure) == 0) {
                if (st.ownItemCount(breath_of_winds) > 0) {
                    st.giveItems(seed_of_verdure, 1);
                    st.setMemoState("testimoney_of_trust", String.valueOf(3), true);
                    st.soundEffect(SOUND_MIDDLE);
                } else {
                    st.giveItems(seed_of_verdure, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        }
        return null;
    }
}