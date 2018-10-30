package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.model.base.ClassId;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.serverpackets.SocialAction;
import org.mmocore.gameserver.object.Inventory;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.0
 * @date 21/10/2015
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _229_TestOfWitchcraft extends Quest {
    // npc
    private static final int orim_the_shadow = 30630;
    private static final int alexandria = 30098;
    private static final int iker = 30110;
    private static final int magister_kaira = 30476;
    private static final int lars = 30063;
    private static final int warden_roderik = 30631;
    private static final int trader_nestle = 30314;
    private static final int leopold = 30435;
    private static final int sir_karrel_vasper = 30417;
    private static final int vadin = 30188;
    private static final int fisher_evert = 30633;
    private static final int warden_endrigo = 30632;
    // mobs
    private static final int dire_wyrm = 20557;
    private static final int enchanted_stone_golem = 20565;
    private static final int leto_lizardman = 20577;
    private static final int leto_lizardman_archer = 20578;
    private static final int leto_lizardman_soldier = 20579;
    private static final int leto_lizardman_warrior = 20580;
    private static final int leto_lizardman_shaman = 20581;
    private static final int leto_lizardman_overlord = 20582;
    private static final int tamlin_orc = 20601;
    private static final int tamlin_orc_archer = 20602;
    private static final int nameless_revenant = 27099;
    private static final int skeletal_mercenary = 27100;
    private static final int drevanul_prince_zeruel = 27101;
    // questitem
    private static final int sword_of_binding = 3029;
    private static final int mark_of_witchcraft = 3307;
    private static final int orims_diagram = 3308;
    private static final int alexandrias_book = 3309;
    private static final int ikers_list = 3310;
    private static final int dire_wyrm_fang = 3311;
    private static final int leto_lizardman_charm = 3312;
    private static final int en_golem_heartstone = 3313;
    private static final int lars_memo1 = 3314;
    private static final int nestle_memo1 = 3315;
    private static final int leopolds_journal1 = 3316;
    private static final int aklantos_gem1 = 3317;
    private static final int aklantos_gem2 = 3318;
    private static final int aklantos_gem3 = 3319;
    private static final int aklantos_gem4 = 3320;
    private static final int aklantos_gem5 = 3321;
    private static final int aklantos_gem6 = 3322;
    private static final int brimstone1 = 3323;
    private static final int orims_instructions = 3324;
    private static final int orims_letter1 = 3325;
    private static final int orims_letter2 = 3326;
    private static final int sir_vaspers_letter = 3327;
    private static final int vadins_crucifix = 3328;
    private static final int tamlin_orc_amulet = 3329;
    private static final int vadins_sanctions = 3330;
    private static final int ikers_amulet = 3331;
    private static final int soultrap_crystal = 3332;
    private static final int purgatory_key = 3333;
    private static final int zeruel_bind_crystal = 3334;
    private static final int brimstone2 = 3335;
    // etcitem
    private static final int q_dimension_diamond = 7562;
    // spawn memo
    private int myself_i_quest0 = 0;

    public _229_TestOfWitchcraft() {
        super(false);
        addStartNpc(orim_the_shadow);
        addTalkId(alexandria, iker, magister_kaira, lars, trader_nestle, leopold, sir_karrel_vasper, vadin, fisher_evert, warden_roderik, warden_endrigo);
        addKillId(dire_wyrm, enchanted_stone_golem, leto_lizardman, leto_lizardman_archer, leto_lizardman_soldier, leto_lizardman_warrior, leto_lizardman_shaman, leto_lizardman_overlord, tamlin_orc, tamlin_orc_archer, nameless_revenant, skeletal_mercenary, drevanul_prince_zeruel);
        addQuestItem(orims_diagram, orims_instructions, orims_letter1, orims_letter2, brimstone1, alexandrias_book, ikers_list, aklantos_gem1, soultrap_crystal, ikers_amulet, aklantos_gem2, lars_memo1, nestle_memo1, leopolds_journal1, aklantos_gem4, aklantos_gem5, aklantos_gem6, sir_vaspers_letter, sword_of_binding, vadins_crucifix, vadins_sanctions, brimstone2, purgatory_key, zeruel_bind_crystal, dire_wyrm_fang, en_golem_heartstone, leto_lizardman_charm, aklantos_gem3, tamlin_orc_amulet);
        addAttackId(drevanul_prince_zeruel, nameless_revenant, skeletal_mercenary);
        addLevelCheck(39);
        addClassIdCheck(ClassId.wizard, ClassId.knight, ClassId.temple_knight);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        ClassId talker_occupation = st.getPlayer().getPlayerClassComponent().getClassId();
        int npcId = npc.getNpcId();
        if (npcId == orim_the_shadow) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.giveItems(orims_diagram, 1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                if (!st.getPlayer().getPlayerVariables().getBoolean(PlayerVariables.DD1)) {
                    if (talker_occupation == ClassId.knight || talker_occupation == ClassId.temple_knight)
                        st.giveItems(q_dimension_diamond, 104);
                    else
                        st.giveItems(q_dimension_diamond, 122);
                    st.getPlayer().getPlayerVariables().set(PlayerVariables.DD1, "1", -1);
                    htmltext = "orim_the_shadow_q0229_08a.htm";
                } else
                    htmltext = "orim_the_shadow_q0229_08.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=229&reply=1"))
                htmltext = "orim_the_shadow_q0229_04.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=229&reply=2"))
                htmltext = "orim_the_shadow_q0229_06.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=229&reply=3"))
                htmltext = "orim_the_shadow_q0229_07.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=229&reply=4"))
                htmltext = "orim_the_shadow_q0229_12.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=229&reply=5"))
                htmltext = "orim_the_shadow_q0229_13.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=229&reply=6")) {
                if (myself_i_quest0 == 0) {
                    if (st.ownItemCount(alexandrias_book) >= 1) {
                        st.setCond(4);
                        st.takeItems(alexandrias_book, 1);
                        st.giveItems(brimstone1, 1);
                        st.takeItems(aklantos_gem1, -1);
                        st.takeItems(aklantos_gem2, -1);
                        st.takeItems(aklantos_gem3, -1);
                        st.takeItems(aklantos_gem4, -1);
                        st.takeItems(aklantos_gem5, -1);
                        st.takeItems(aklantos_gem6, -1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "orim_the_shadow_q0229_14.htm";
                        myself_i_quest0 = 1;
                        NpcInstance Zeruel = st.addSpawn(drevanul_prince_zeruel);
                        if (Zeruel != null)
                            Zeruel.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, st.getPlayer(), 300);
                    }
                } else {
                    if (!st.getPlayer().isLangRus())
                        htmltext = "Принц Дре Ванул Зеруэль уже создан.";
                    else
                        htmltext = "Drevanul Prince Zeruel is already spawned.";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=229&reply=7")) {
                if (st.ownItemCount(brimstone1) >= 1) {
                    st.setCond(6);
                    st.takeItems(brimstone1, 1);
                    st.giveItems(orims_instructions, 1);
                    st.giveItems(orims_letter1, 1);
                    st.giveItems(orims_letter2, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "orim_the_shadow_q0229_16.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=229&reply=8"))
                htmltext = "orim_the_shadow_q0229_20.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=229&reply=9"))
                htmltext = "orim_the_shadow_q0229_21.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=229&reply=10")) {
                if (st.ownItemCount(zeruel_bind_crystal) >= 1) {
                    st.takeItems(purgatory_key, -1);
                    st.takeItems(sword_of_binding, -1);
                    st.takeItems(ikers_amulet, -1);
                    st.takeItems(orims_instructions, -1);
                    st.addExpAndSp(2058244, 141240);
                    st.giveItems(ADENA_ID, 372154);
                    st.takeItems(zeruel_bind_crystal, 1);
                    st.giveItems(mark_of_witchcraft, 1);
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                    htmltext = "orim_the_shadow_q0229_22.htm";
                }
            }
        } else if (npcId == iker) {
            if (event.equalsIgnoreCase("menu_select?ask=229&reply=1"))
                htmltext = "iker_q0229_02.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=229&reply=2")) {
                st.giveItems(ikers_list, 1);
                htmltext = "iker_q0229_03.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=229&reply=3")) {
                htmltext = "iker_q0229_08.htm";
                st.giveItems(soultrap_crystal, 1);
                st.giveItems(ikers_amulet, 1);
                st.takeItems(orims_letter2, -1);
                if (st.ownItemCount(sword_of_binding) >= 1) {
                    st.setCond(7);
                    st.soundEffect(SOUND_MIDDLE);
                }
            }
        } else if (npcId == magister_kaira) {
            if (event.equalsIgnoreCase("menu_select?ask=229&reply=1")) {
                htmltext = "magister_kaira_q0229_02.htm";
                st.giveItems(aklantos_gem2, 1);
                if (st.ownItemCount(aklantos_gem1) >= 1 && st.ownItemCount(aklantos_gem3) >= 1 && st.ownItemCount(aklantos_gem4) >= 1 && st.ownItemCount(aklantos_gem5) >= 1 && st.ownItemCount(aklantos_gem6) >= 1) {
                    st.setCond(3);
                    st.soundEffect(SOUND_MIDDLE);
                }
            }
        } else if (npcId == lars) {
            if (event.equalsIgnoreCase("menu_select?ask=229&reply=1")) {
                st.giveItems(lars_memo1, 1);
                htmltext = "lars_q0229_02.htm";
            }
        } else if (npcId == trader_nestle) {
            if (event.equalsIgnoreCase("menu_select?ask=229&reply=1")) {
                st.giveItems(nestle_memo1, 1);
                htmltext = "trader_nestle_q0229_02.htm";
            }
        } else if (npcId == leopold) {
            if (event.equalsIgnoreCase("menu_select?ask=229&reply=1")) {
                if (st.ownItemCount(nestle_memo1) >= 1) {
                    htmltext = "leopold_q0229_02.htm";
                    st.takeItems(nestle_memo1, 1);
                    st.giveItems(leopolds_journal1, 1);
                }
            }
        } else if (npcId == sir_karrel_vasper) {
            if (event.equalsIgnoreCase("menu_select?ask=229&reply=1"))
                htmltext = "sir_karrel_vasper_q0229_02.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=229&reply=2")) {
                if (st.ownItemCount(orims_letter1) >= 1) {
                    htmltext = "sir_karrel_vasper_q0229_03.htm";
                    st.takeItems(orims_letter1, 1);
                    st.giveItems(sir_vaspers_letter, 1);
                }
            }
        } else if (npcId == fisher_evert) {
            if (event.equalsIgnoreCase("menu_select?ask=229&reply=1")) {
                st.setCond(9);
                st.giveItems(brimstone2, 1);
                htmltext = "fisher_evert_q0229_02.htm";
                st.soundEffect(SOUND_MIDDLE);
                if (myself_i_quest0 == 0) {
                    myself_i_quest0 = 1;
                    NpcInstance Zeruel = st.addSpawn(drevanul_prince_zeruel, 13395, 169807, -3708);
                    if (Zeruel != null)
                        Zeruel.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, st.getPlayer(), 300);
                } else {
                    if (!st.getPlayer().isLangRus())
                        htmltext = "Принц Дре Ванул Зеруэль уже создан.";
                    else
                        htmltext = "Drevanul Prince Zeruel is already spawned.";
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        ClassId talker_occupation = st.getPlayer().getPlayerClassComponent().getClassId();
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == orim_the_shadow) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "orim_the_shadow_q0229_02.htm";
                            st.exitQuest(true);
                            break;
                        case CLASS_ID:
                            htmltext = "orim_the_shadow_q0229_01.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            if (talker_occupation == ClassId.wizard)
                                htmltext = "orim_the_shadow_q0229_03.htm";
                            else
                                htmltext = "orim_the_shadow_q0229_05.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == orim_the_shadow) {
                    if (st.ownItemCount(orims_diagram) == 1)
                        htmltext = "orim_the_shadow_q0229_09.htm";
                    else if (st.ownItemCount(alexandrias_book) == 1) {
                        if (st.ownItemCount(aklantos_gem1) >= 1 && st.ownItemCount(aklantos_gem2) >= 1 && st.ownItemCount(aklantos_gem3) >= 1 && st.ownItemCount(aklantos_gem4) >= 1 && st.ownItemCount(aklantos_gem5) >= 1 && st.ownItemCount(aklantos_gem6) >= 1)
                            htmltext = "orim_the_shadow_q0229_11.htm";
                        else
                            htmltext = "orim_the_shadow_q0229_10.htm";
                    } else if (st.ownItemCount(brimstone1) == 1)
                        htmltext = "orim_the_shadow_q0229_15.htm";
                    else if (st.ownItemCount(orims_instructions) == 1 && st.ownItemCount(sword_of_binding) == 0 && st.ownItemCount(soultrap_crystal) == 0)
                        htmltext = "orim_the_shadow_q0229_17.htm";
                    else if (st.ownItemCount(sword_of_binding) >= 1 && st.ownItemCount(soultrap_crystal) >= 1) {
                        st.setCond(8);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "orim_the_shadow_q0229_18.htm";
                    } else if (st.ownItemCount(sword_of_binding) >= 1 && st.ownItemCount(zeruel_bind_crystal) >= 1)
                        htmltext = "orim_the_shadow_q0229_19.htm";
                } else if (npcId == iker) {
                    if (st.ownItemCount(alexandrias_book) == 1 && st.ownItemCount(ikers_list) == 0 && st.ownItemCount(aklantos_gem1) == 0)
                        htmltext = "iker_q0229_01.htm";
                    else if (st.ownItemCount(alexandrias_book) >= 1 && st.ownItemCount(ikers_list) >= 1) {
                        if (st.ownItemCount(dire_wyrm_fang) >= 20 && st.ownItemCount(leto_lizardman_charm) >= 20 && st.ownItemCount(en_golem_heartstone) >= 20) {
                            st.takeItems(ikers_list, 1);
                            st.giveItems(aklantos_gem1, 1);
                            st.takeItems(dire_wyrm_fang, -1);
                            st.takeItems(leto_lizardman_charm, -1);
                            st.takeItems(en_golem_heartstone, -1);
                            htmltext = "iker_q0229_05.htm";
                            if (st.ownItemCount(aklantos_gem2) >= 1 && st.ownItemCount(aklantos_gem3) >= 1 && st.ownItemCount(aklantos_gem4) >= 1 && st.ownItemCount(aklantos_gem5) >= 1 && st.ownItemCount(aklantos_gem6) >= 1) {
                                st.setCond(3);
                                st.soundEffect(SOUND_MIDDLE);
                            }
                        } else
                            htmltext = "iker_q0229_04.htm";
                    } else if (st.ownItemCount(ikers_list) == 0 && st.ownItemCount(alexandrias_book) >= 1 && st.ownItemCount(aklantos_gem1) >= 1)
                        htmltext = "iker_q0229_06.htm";
                    else if (st.ownItemCount(orims_instructions) == 1 && st.ownItemCount(soultrap_crystal) == 0 && st.ownItemCount(zeruel_bind_crystal) == 0)
                        htmltext = "iker_q0229_07.htm";
                    else if (st.ownItemCount(zeruel_bind_crystal) == 0 && st.ownItemCount(orims_instructions) >= 1 && st.ownItemCount(soultrap_crystal) >= 1)
                        htmltext = "iker_q0229_09.htm";
                    else if (st.ownItemCount(soultrap_crystal) == 0 && st.ownItemCount(orims_instructions) >= 1 && st.ownItemCount(zeruel_bind_crystal) >= 1)
                        htmltext = "iker_q0229_10.htm";
                } else if (npcId == magister_kaira) {
                    if (st.ownItemCount(alexandrias_book) == 1 && st.ownItemCount(aklantos_gem2) == 0)
                        htmltext = "magister_kaira_q0229_01.htm";
                    else if (st.ownItemCount(aklantos_gem2) == 1 && st.ownItemCount(alexandrias_book) == 1)
                        htmltext = "magister_kaira_q0229_03.htm";
                    else if (st.ownItemCount(brimstone1) >= 1 || st.ownItemCount(orims_instructions) >= 1)
                        htmltext = "magister_kaira_q0229_04.htm";
                } else if (npcId == lars) {
                    if (st.ownItemCount(alexandrias_book) == 1 && st.ownItemCount(lars_memo1) == 0 && st.ownItemCount(aklantos_gem3) == 0)
                        htmltext = "lars_q0229_01.htm";
                    else if (st.ownItemCount(aklantos_gem3) == 0 && st.ownItemCount(alexandrias_book) >= 1 && st.ownItemCount(lars_memo1) >= 1)
                        htmltext = "lars_q0229_03.htm";
                    else if (st.ownItemCount(lars_memo1) == 0 && st.ownItemCount(alexandrias_book) >= 1 && st.ownItemCount(aklantos_gem3) >= 1)
                        htmltext = "lars_q0229_04.htm";
                    else if (st.ownItemCount(brimstone1) >= 1 || st.ownItemCount(orims_instructions) >= 1)
                        htmltext = "lars_q0229_05.htm";
                } else if (npcId == trader_nestle) {
                    if (st.ownItemCount(leopolds_journal1) == 0 && st.ownItemCount(nestle_memo1) == 0 && st.ownItemCount(alexandrias_book) == 1 && st.ownItemCount(aklantos_gem4) == 0 && st.ownItemCount(aklantos_gem5) == 0 && st.ownItemCount(aklantos_gem6) == 0)
                        htmltext = "trader_nestle_q0229_01.htm";
                    else if (st.ownItemCount(alexandrias_book) >= 1 && st.ownItemCount(nestle_memo1) >= 1 && st.ownItemCount(leopolds_journal1) == 0)
                        htmltext = "trader_nestle_q0229_03.htm";
                    else if (st.ownItemCount(alexandrias_book) == 1 && st.ownItemCount(nestle_memo1) == 0 && (st.ownItemCount(leopolds_journal1) == 1 || st.ownItemCount(aklantos_gem4) >= 1 || st.ownItemCount(aklantos_gem5) >= 1 || st.ownItemCount(aklantos_gem6) >= 1))
                        htmltext = "trader_nestle_q0229_04.htm";
                } else if (npcId == leopold) {
                    if (st.ownItemCount(leopolds_journal1) == 0 && st.ownItemCount(alexandrias_book) >= 1 && st.ownItemCount(nestle_memo1) >= 1)
                        htmltext = "leopold_q0229_01.htm";
                    else if (st.ownItemCount(nestle_memo1) == 0 && st.ownItemCount(alexandrias_book) >= 1 && st.ownItemCount(leopolds_journal1) >= 1)
                        htmltext = "leopold_q0229_03.htm";
                    else if (st.ownItemCount(alexandrias_book) == 1 && st.ownItemCount(aklantos_gem4) >= 1 && st.ownItemCount(aklantos_gem5) >= 1 && st.ownItemCount(aklantos_gem6) >= 1)
                        htmltext = "leopold_q0229_04.htm";
                    else if (st.ownItemCount(brimstone1) >= 1 || st.ownItemCount(orims_instructions) >= 1)
                        htmltext = "leopold_q0229_05.htm";
                } else if (npcId == sir_karrel_vasper) {
                    if (st.ownItemCount(orims_instructions) >= 1 && st.ownItemCount(orims_letter1) >= 1)
                        htmltext = "sir_karrel_vasper_q0229_01.htm";
                    else if (st.ownItemCount(orims_instructions) >= 1 && st.ownItemCount(sir_vaspers_letter) >= 1)
                        htmltext = "sir_karrel_vasper_q0229_04.htm";
                    else if (st.ownItemCount(orims_instructions) >= 1 && st.ownItemCount(vadins_sanctions) >= 1) {
                        htmltext = "sir_karrel_vasper_q0229_05.htm";
                        st.takeItems(vadins_sanctions, 1);
                        st.giveItems(sword_of_binding, 1);
                        if (st.ownItemCount(soultrap_crystal) >= 1) {
                            st.setCond(7);
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    } else if (st.ownItemCount(orims_instructions) >= 1 && st.ownItemCount(sword_of_binding) >= 1)
                        htmltext = "sir_karrel_vasper_q0229_06.htm";
                } else if (npcId == vadin) {
                    if (st.ownItemCount(orims_instructions) == 1 && st.ownItemCount(sir_vaspers_letter) == 1) {
                        st.takeItems(sir_vaspers_letter, 1);
                        st.giveItems(vadins_crucifix, 1);
                        htmltext = "vadin_q0229_01.htm";
                    } else if (st.ownItemCount(vadins_crucifix) == 1) {
                        if (st.ownItemCount(tamlin_orc_amulet) < 20)
                            htmltext = "vadin_q0229_02.htm";
                        else {
                            htmltext = "vadin_q0229_03.htm";
                            st.takeItems(tamlin_orc_amulet, -1);
                            st.takeItems(vadins_crucifix, 1);
                            st.giveItems(vadins_sanctions, 1);
                        }
                    } else if (st.ownItemCount(orims_instructions) == 1 && st.ownItemCount(vadins_sanctions) == 1)
                        htmltext = "vadin_q0229_04.htm";
                    else if (st.ownItemCount(orims_instructions) == 1 && st.ownItemCount(sword_of_binding) == 1)
                        htmltext = "vadin_q0229_05.htm";
                } else if (npcId == fisher_evert) {
                    if (st.ownItemCount(orims_instructions) >= 1 && st.ownItemCount(soultrap_crystal) >= 1 && st.ownItemCount(sword_of_binding) >= 1 && st.ownItemCount(brimstone2) == 0)
                        htmltext = "fisher_evert_q0229_01.htm";
                    else if (st.ownItemCount(orims_instructions) >= 1 && st.ownItemCount(soultrap_crystal) >= 1 && st.ownItemCount(brimstone2) >= 1 && st.ownItemCount(zeruel_bind_crystal) == 0) {
                        htmltext = "fisher_evert_q0229_02.htm";
                        if (myself_i_quest0 == 0) {
                            myself_i_quest0 = 1;
                            NpcInstance Zeruel = st.addSpawn(drevanul_prince_zeruel, 13395, 169807, -3708);
                            if (Zeruel != null)
                                Zeruel.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, st.getPlayer(), 300);
                        } else {
                            if (!st.getPlayer().isLangRus())
                                htmltext = "Принц Дре Ванул Зеруэль уже создан.";
                            else
                                htmltext = "Drevanul Prince Zeruel is already spawned.";
                        }
                    } else if (st.ownItemCount(orims_instructions) >= 1 && st.ownItemCount(zeruel_bind_crystal) >= 1 && st.ownItemCount(soultrap_crystal) == 0 && st.ownItemCount(brimstone2) == 0)
                        htmltext = "fisher_evert_q0229_03.htm";
                } else if (npcId == warden_roderik) {
                    if ((st.ownItemCount(lars_memo1) >= 1 || st.ownItemCount(aklantos_gem3) >= 1) && st.ownItemCount(alexandrias_book) == 1)
                        htmltext = "warden_roderik_q0229_01.htm";
                } else if (npcId == warden_endrigo) {
                    if ((st.ownItemCount(lars_memo1) >= 1 || st.ownItemCount(aklantos_gem3) >= 1) && st.ownItemCount(alexandrias_book) == 1)
                        htmltext = "warden_endrigo_q0229_01.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onAttack(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == drevanul_prince_zeruel) {
            if (st.ownItemCount(brimstone1) == 1) {
                if (npc != null) {
                    Functions.npcSay(npc, NpcString.ILL_TAKE_YOUR_LIVES_LATER);
                    myself_i_quest0 = 0;
                    npc.deleteMe();
                    st.setCond(5);
                    st.soundEffect(SOUND_MIDDLE);
                }
            } else if (st.ownItemCount(orims_instructions) == 1 && st.ownItemCount(brimstone2) == 1 && st.ownItemCount(sword_of_binding) == 1 && st.ownItemCount(soultrap_crystal) == 1) {
                if (myself_i_quest0 == 0 && st.getItemEquipped(Inventory.PAPERDOLL_RHAND) == sword_of_binding) {
                    if (npc != null) {
                        Functions.npcSay(npc, NpcString.THAT_SWORD_IS_REALLY);
                        myself_i_quest0 = 1;
                    }
                }
            }
        } else if (npcId == nameless_revenant) {
            if (st.ownItemCount(alexandrias_book) == 1 && st.ownItemCount(lars_memo1) == 1 && st.ownItemCount(aklantos_gem3) == 0)
                Functions.npcSay(npc, NpcString.I_ABSOLUTELY_CANNOT_GIVE_IT_TO_YOU_IT_IS_MY_PRECIOUS_JEWEL);
        } else if (npcId == skeletal_mercenary) {
            if (st.ownItemCount(leopolds_journal1) == 1 && (st.ownItemCount(aklantos_gem4) == 0 || st.ownItemCount(aklantos_gem5) == 0 || st.ownItemCount(aklantos_gem6) == 0))
                Functions.npcSay(npc, NpcString.I_ABSOLUTELY_CANNOT_GIVE_IT_TO_YOU_IT_IS_MY_PRECIOUS_JEWEL);
        }
        return null;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == dire_wyrm) {
            if (st.ownItemCount(alexandrias_book) == 1 && st.ownItemCount(ikers_list) == 1) {
                if (Rnd.get(100) <= 100 && st.ownItemCount(dire_wyrm_fang) < 20) {
                    st.giveItems(dire_wyrm_fang, 1);
                    if (st.ownItemCount(dire_wyrm_fang) >= 20)
                        st.soundEffect(SOUND_MIDDLE);
                    else
                        st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == enchanted_stone_golem) {
            if (st.ownItemCount(alexandrias_book) == 1 && st.ownItemCount(ikers_list) == 1) {
                if (Rnd.get(100) <= 100 && st.ownItemCount(en_golem_heartstone) < 20) {
                    st.giveItems(en_golem_heartstone, 1);
                    if (st.ownItemCount(en_golem_heartstone) >= 20)
                        st.soundEffect(SOUND_MIDDLE);
                    else
                        st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == leto_lizardman || npcId == leto_lizardman_archer || npcId == leto_lizardman_soldier || npcId == leto_lizardman_warrior || npcId == leto_lizardman_shaman || npcId == leto_lizardman_overlord) {
            if (st.ownItemCount(alexandrias_book) == 1 && st.ownItemCount(ikers_list) == 1) {
                if (Rnd.get(100) <= 100 && st.ownItemCount(leto_lizardman_charm) < 20) {
                    st.giveItems(leto_lizardman_charm, 1);
                    if (st.ownItemCount(leto_lizardman_charm) >= 20)
                        st.soundEffect(SOUND_MIDDLE);
                    else
                        st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == tamlin_orc || npcId == tamlin_orc_archer) {
            if (st.ownItemCount(vadins_crucifix) == 1) {
                if (Rnd.get(100) < 50 && st.ownItemCount(tamlin_orc_amulet) < 20) {
                    st.giveItems(tamlin_orc_amulet, 1);
                    if (st.ownItemCount(tamlin_orc_amulet) >= 20)
                        st.soundEffect(SOUND_MIDDLE);
                    else
                        st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == nameless_revenant) {
            if (st.ownItemCount(alexandrias_book) == 1 && st.ownItemCount(lars_memo1) == 1 && st.ownItemCount(aklantos_gem3) == 0) {
                st.giveItems(aklantos_gem3, 1);
                st.soundEffect(SOUND_ITEMGET);
                st.takeItems(lars_memo1, -1);
                if (st.ownItemCount(aklantos_gem1) >= 1 && st.ownItemCount(aklantos_gem2) >= 1 && st.ownItemCount(aklantos_gem4) >= 1 && st.ownItemCount(aklantos_gem5) >= 1 && st.ownItemCount(aklantos_gem6) >= 1)
                    st.setCond(3);
            }
        } else if (npcId == skeletal_mercenary) {
            if (st.ownItemCount(leopolds_journal1) == 1 && (st.ownItemCount(aklantos_gem4) == 0 || st.ownItemCount(aklantos_gem5) == 0 || st.ownItemCount(aklantos_gem6) == 0)) {
                if (st.ownItemCount(aklantos_gem4) == 0) {
                    st.giveItems(aklantos_gem4, 1);
                    st.soundEffect(SOUND_ITEMGET);
                } else if (st.ownItemCount(aklantos_gem5) == 0) {
                    st.giveItems(aklantos_gem5, 1);
                    st.soundEffect(SOUND_ITEMGET);
                } else if (st.ownItemCount(aklantos_gem6) == 0) {
                    st.giveItems(aklantos_gem6, 1);
                    st.soundEffect(SOUND_ITEMGET);
                    st.takeItems(leopolds_journal1, -1);
                    if (st.ownItemCount(aklantos_gem1) >= 1 && st.ownItemCount(aklantos_gem2) >= 1 && st.ownItemCount(aklantos_gem3) >= 1)
                        st.setCond(3);
                }
            }
        } else if (npcId == drevanul_prince_zeruel) {
            if (st.ownItemCount(orims_instructions) == 1 && st.ownItemCount(brimstone2) == 1 && st.ownItemCount(sword_of_binding) == 1 && st.ownItemCount(soultrap_crystal) == 1) {
                if (st.getItemEquipped(Inventory.PAPERDOLL_RHAND) == sword_of_binding) {
                    Functions.npcSay(npc, NpcString.NO_I_HAVENT_COMPLETELY_FINISHED_THE_COMMAND_FOR_DESTRUCTION_AND_SLAUGHTER_YET);
                    st.giveItems(zeruel_bind_crystal, 1);
                    st.giveItems(purgatory_key, 1);
                    st.soundEffect(SOUND_ITEMGET);
                    st.takeItems(brimstone2, -1);
                    st.takeItems(soultrap_crystal, -1);
                    st.setCond(10);
                    myself_i_quest0 = 0;
                }
            }
        }
        return null;
    }
}