package org.mmocore.gameserver.scripts.quests;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.model.base.ClassLevel;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.0
 * @date 13/03/2015
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _060_GoodWorksReward extends Quest {
    // npc
    private static final int blueprint_seller_daeger = 31435;
    private static final int mark_q0060_1 = 32487;
    private static final int helvetia = 30081;
    private static final int marketeer_of_mammon = 31092;
    // mobs
    private static final int chaser_q0060 = 27340;
    // etcitem
    private static final int q_red_patch_q0060 = 10867;
    private static final int q_antidote_of_hel = 10868;
    // questitem
    private static final int mark_of_challenger = 2627;
    private static final int mark_of_trust = 2734;
    private static final int mark_of_duelist = 2762;
    private static final int mark_of_champion = 3276;
    private static final int mark_of_duty = 2633;
    private static final int mark_of_healer = 2820;
    private static final int mark_of_witchcraft = 3307;
    private static final int mark_of_seeker = 2673;
    private static final int mark_of_searcher = 2809;
    private static final int mark_of_sagittarius = 3293;
    private static final int mark_of_scholar = 2674;
    private static final int mark_of_magus = 2840;
    private static final int mark_of_summoner = 3336;
    private static final int mark_of_pilgrim = 2721;
    private static final int mark_of_reformer = 2821;
    private static final int mark_of_life = 3140;
    private static final int mark_of_fate = 3172;
    private static final int mark_of_glory = 3203;
    private static final int mark_of_lord = 3390;
    private static final int mark_of_warspirit = 2879;
    private static final int mark_of_guildsman = 3119;
    private static final int mark_of_prosperity = 3238;
    private static final int mark_of_maestro = 2867;
    // Second class id
    private static final int warrior = 0x01;
    private static final int knight = 0x04;
    private static final int rogue = 0x07;
    private static final int wizard = 0x0b;
    private static final int cleric = 0x0f;
    private static final int elven_knight = 0x13;
    private static final int elven_scout = 0x16;
    private static final int elven_wizard = 0x1a;
    private static final int oracle = 0x1d;
    private static final int orc_raider = 0x2d;
    private static final int orc_monk = 0x2f;
    private static final int orc_shaman = 0x32;
    private static final int scavenger = 0x36;
    private static final int artisan = 0x38;
    private static final int palus_knight = 0x20;
    private static final int assassin = 0x23;
    private static final int dark_wizard = 0x27;
    private static final int shillien_oracle = 0x2a;
    // spawn
    public static int myself_i_quest0 = 0;
    // player object_id
    public static int myself_i_quest1;

    public _060_GoodWorksReward() {
        super(false);
        addStartNpc(blueprint_seller_daeger);
        addTalkId(mark_q0060_1, helvetia, marketeer_of_mammon);
        addKillId(chaser_q0060);
        addQuestItem(q_red_patch_q0060, q_antidote_of_hel);
        addLevelCheck(39);
        addRaceCheck(PlayerRace.elf, PlayerRace.human, PlayerRace.darkelf, PlayerRace.orc, PlayerRace.dwarf);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        boolean second_class_group = st.getPlayer().getPlayerClassComponent().getClassId().isOfLevel(ClassLevel.Second);
        int talker_occupation = st.getPlayer().getPlayerClassComponent().getClassId().getId();
        int GetMemoState = st.getInt("result_of_good_deed");
        int GetMemoStateEx = st.getInt("result_of_good_deed_ex");
        int GetOneTimeQuestFlag = st.getInt("result_of_good_deed_flag");
        int npcId = npc.getNpcId();
        // QuestState
        QuestState trial_of_challenger = st.getPlayer().getQuestState(211);
        QuestState trial_of_duty = st.getPlayer().getQuestState(212);
        QuestState trial_of_seeker = st.getPlayer().getQuestState(213);
        QuestState trial_of_scholar = st.getPlayer().getQuestState(214);
        QuestState trial_of_pilgrim = st.getPlayer().getQuestState(215);
        QuestState trial_of_guildsman = st.getPlayer().getQuestState(215);
        QuestState testimoney_of_trust = st.getPlayer().getQuestState(217);
        QuestState testimony_of_life = st.getPlayer().getQuestState(218);
        QuestState testimony_of_fate = st.getPlayer().getQuestState(219);
        QuestState testimony_of_glory = st.getPlayer().getQuestState(220);
        QuestState testimony_of_prosperity = st.getPlayer().getQuestState(221);
        QuestState test_of_duelist = st.getPlayer().getQuestState(222);
        QuestState test_of_champion = st.getPlayer().getQuestState(223);
        QuestState test_of_sagittarius = st.getPlayer().getQuestState(224);
        QuestState test_of_searcher = st.getPlayer().getQuestState(225);
        QuestState test_of_healer = st.getPlayer().getQuestState(226);
        QuestState test_of_reformer = st.getPlayer().getQuestState(227);
        QuestState test_of_magus = st.getPlayer().getQuestState(228);
        QuestState test_of_witchcraft = st.getPlayer().getQuestState(229);
        QuestState test_of_summoner = st.getPlayer().getQuestState(230);
        QuestState test_of_maestro = st.getPlayer().getQuestState(231);
        QuestState test_of_lord = st.getPlayer().getQuestState(232);
        QuestState test_of_warspirit = st.getPlayer().getQuestState(233);
        if (npcId == blueprint_seller_daeger) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("result_of_good_deed", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "blueprint_seller_daeger_q0060_07.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=60&reply=1"))
                htmltext = "blueprint_seller_daeger_q0060_02.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=60&reply=2")) {
                if (GetMemoState == 3) {
                    st.setCond(4);
                    st.setMemoState("result_of_good_deed", String.valueOf(4), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "blueprint_seller_daeger_q0060_10.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=60&reply=3")) {
                if (GetMemoState == 8) {
                    st.setCond(9);
                    st.setMemoState("result_of_good_deed", String.valueOf(9), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "blueprint_seller_daeger_q0060_14.htm";
                }
            }
        } else if (npcId == mark_q0060_1) {
            if (event.equalsIgnoreCase("menu_select?ask=60&reply=1")) {
                if (GetMemoState == 1) {
                    if (myself_i_quest0 == 0) {
                        htmltext = "mark_q0060_1_q0060_04.htm";
                        myself_i_quest0 = 1;
                        myself_i_quest1 = st.getPlayer().getObjectId();
                        NpcInstance chaser = st.addSpawn(chaser_q0060, npc.getX() + 50, npc.getY() + 50, npc.getZ());
                        Functions.npcSay(chaser, NpcString.S1_I_MUST_KILL_YOU_BLAME_YOUR_OWN_CURIOSITY, st.getPlayer().getName());
                        chaser.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, st.getPlayer(), 10000);
                        st.startQuestTimer("6001", 1000 * 60, chaser);
                    } else
                        htmltext = "mark_q0060_1_q0060_05.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=60&reply=3")) {
                if (GetMemoState == 7) {
                    st.setCond(8);
                    st.setMemoState("result_of_good_deed", String.valueOf(8), true);
                    st.takeItems(q_antidote_of_hel, -1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "mark_q0060_1_q0060_10.htm";
                    if (myself_i_quest0 == 1)
                        myself_i_quest0 = 0;
                }
            }
        } else if (event.equalsIgnoreCase("6001")) {
            Functions.npcSay(npc, NpcString.YOU_HAVE_GOOD_LUCK_I_SHALL_RETURN);
            myself_i_quest0 = 0;
            myself_i_quest1 = 0;
            if (npc != null)
                npc.deleteMe();
            return null;
        } else if (npcId == helvetia) {
            if (event.equalsIgnoreCase("menu_select?ask=60&reply=1")) {
                if (GetMemoState == 4)
                    htmltext = "helvetia_q0060_02.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=60&reply=2")) {
                if (GetMemoState == 4) {
                    st.setCond(5);
                    st.setMemoState("result_of_good_deed", String.valueOf(5), true);
                    st.takeItems(q_red_patch_q0060, -1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "helvetia_q0060_03.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=60&reply=3")) {
                if (GetMemoState >= 5 && GetMemoState <= 6) {
                    if (st.ownItemCount(ADENA_ID) >= 3000000) {
                        st.setCond(7);
                        st.setMemoState("result_of_good_deed", String.valueOf(7), true);
                        st.giveItems(q_antidote_of_hel, 1);
                        st.takeItems(ADENA_ID, 3000000);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "helvetia_q0060_05.htm";
                    } else {
                        st.setCond(6);
                        st.setMemoState("result_of_good_deed", String.valueOf(6), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "helvetia_q0060_06.htm";
                    }
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=60&reply=4")) {
                if (GetMemoState == 5) {
                    st.setCond(6);
                    st.setMemoState("result_of_good_deed", String.valueOf(6), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "helvetia_q0060_07.htm";
                }
            }
        } else if (npcId == marketeer_of_mammon) {
            if (event.equalsIgnoreCase("menu_select?ask=60&reply=1")) {
                if (GetMemoState == 10) {
                    htmltext = "marketeer_of_mammon_q0060_02.htm";
                    if (trial_of_challenger != null && trial_of_challenger.isCompleted() || trial_of_duty != null && trial_of_duty.isCompleted() || trial_of_seeker != null && trial_of_seeker.isCompleted() || trial_of_scholar != null && trial_of_scholar.isCompleted() || trial_of_pilgrim != null && trial_of_pilgrim.isCompleted() || trial_of_guildsman != null && trial_of_guildsman.isCompleted()) {
                        if (testimoney_of_trust != null && testimoney_of_trust.isCompleted() || testimony_of_life != null && testimony_of_life.isCompleted() || testimony_of_fate != null && testimony_of_fate.isCompleted() || testimony_of_glory != null && testimony_of_glory.isCompleted() || testimony_of_prosperity != null && testimony_of_prosperity.isCompleted()) {
                            if (test_of_duelist != null && test_of_duelist.isCompleted() || test_of_champion != null && test_of_champion.isCompleted() || test_of_sagittarius != null && test_of_sagittarius.isCompleted() || test_of_searcher != null && test_of_searcher.isCompleted() || test_of_healer != null && test_of_healer.isCompleted() || test_of_reformer != null && test_of_reformer.isCompleted() || test_of_magus != null && test_of_magus.isCompleted() || test_of_witchcraft != null && test_of_witchcraft.isCompleted() || test_of_summoner != null && test_of_summoner.isCompleted() || test_of_maestro != null && test_of_maestro.isCompleted() || test_of_lord != null && test_of_lord.isCompleted() || test_of_warspirit != null && test_of_warspirit.isCompleted()) {
                                st.setMemoState("result_of_good_deed_ex", String.valueOf(3), true);
                            } else {
                                st.setMemoState("result_of_good_deed_ex", String.valueOf(2), true);
                            }
                        } else if (test_of_duelist != null && test_of_duelist.isCompleted() || test_of_champion != null && test_of_champion.isCompleted() || test_of_sagittarius != null && test_of_sagittarius.isCompleted() || test_of_searcher != null && test_of_searcher.isCompleted() || test_of_healer != null && test_of_healer.isCompleted() || test_of_reformer != null && test_of_reformer.isCompleted() || test_of_magus != null && test_of_magus.isCompleted() || test_of_witchcraft != null && test_of_witchcraft.isCompleted() || test_of_summoner != null && test_of_summoner.isCompleted() || test_of_maestro != null && test_of_maestro.isCompleted() || test_of_lord != null && test_of_lord.isCompleted() || test_of_warspirit != null && test_of_warspirit.isCompleted()) {
                            st.setMemoState("result_of_good_deed_ex", String.valueOf(2), true);
                        } else {
                            st.setMemoState("result_of_good_deed_ex", String.valueOf(1), true);
                        }
                    } else if (testimoney_of_trust != null && testimoney_of_trust.isCompleted() || testimony_of_life != null && testimony_of_life.isCompleted() || testimony_of_fate != null && testimony_of_fate.isCompleted() || testimony_of_glory != null && testimony_of_glory.isCompleted() || testimony_of_prosperity != null && testimony_of_prosperity.isCompleted()) {
                        if (test_of_duelist != null && test_of_duelist.isCompleted() || test_of_champion != null && test_of_champion.isCompleted() || test_of_sagittarius != null && test_of_sagittarius.isCompleted() || test_of_searcher != null && test_of_searcher.isCompleted() || test_of_healer != null && test_of_healer.isCompleted() || test_of_reformer != null && test_of_reformer.isCompleted() || test_of_magus != null && test_of_magus.isCompleted() || test_of_witchcraft != null && test_of_witchcraft.isCompleted() || test_of_summoner != null && test_of_summoner.isCompleted() || test_of_maestro != null && test_of_maestro.isCompleted() || test_of_lord != null && test_of_lord.isCompleted() || test_of_warspirit != null && test_of_warspirit.isCompleted()) {
                            st.setMemoState("result_of_good_deed_ex", String.valueOf(2), true);
                        } else {
                            st.setMemoState("result_of_good_deed_ex", String.valueOf(1), true);
                        }
                    } else if (test_of_duelist != null && test_of_duelist.isCompleted() || test_of_champion != null && test_of_champion.isCompleted() || test_of_sagittarius != null && test_of_sagittarius.isCompleted() || test_of_searcher != null && test_of_searcher.isCompleted() || test_of_healer != null && test_of_healer.isCompleted() || test_of_reformer != null && test_of_reformer.isCompleted() || test_of_magus != null && test_of_magus.isCompleted() || test_of_witchcraft != null && test_of_witchcraft.isCompleted() || test_of_summoner != null && test_of_summoner.isCompleted() || test_of_maestro != null && test_of_maestro.isCompleted() || test_of_lord != null && test_of_lord.isCompleted() || test_of_warspirit != null && test_of_warspirit.isCompleted()) {
                        st.setMemoState("result_of_good_deed_ex", String.valueOf(1), true);
                    }
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=60&reply=2")) {
                if (GetMemoState == 10) {
                    if (GetMemoStateEx >= 3) {
                        htmltext = "marketeer_of_mammon_q0060_03b.htm";
                    } else if (GetMemoStateEx >= 1) {
                        htmltext = "marketeer_of_mammon_q0060_03.htm";
                    } else {
                        htmltext = "marketeer_of_mammon_q0060_03a.htm";
                    }
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=60&reply=3")) {
                if (GetMemoState == 10) {
                    if (GetMemoStateEx >= 3) {
                        st.giveItems(ADENA_ID, 3000000);
                        htmltext = "marketeer_of_mammon_q0060_04a.htm";
                    } else if (GetMemoStateEx == 2) {
                        st.giveItems(ADENA_ID, 2000000);
                        htmltext = "marketeer_of_mammon_q0060_04b.htm";
                    } else if (GetMemoStateEx == 1) {
                        st.giveItems(ADENA_ID, 1000000);
                        htmltext = "marketeer_of_mammon_q0060_04b.htm";
                    }
                    st.setMemoState("result_of_good_deed_flag", String.valueOf(1), true);
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=60&reply=4")) {
                if (GetMemoState == 10) {
                    if (talker_occupation == warrior) {
                        htmltext = "marketeer_of_mammon_q0060_05.htm";
                    } else if (talker_occupation == knight) {
                        htmltext = "marketeer_of_mammon_q0060_06.htm";
                    } else if (talker_occupation == rogue) {
                        htmltext = "marketeer_of_mammon_q0060_07.htm";
                    } else if (talker_occupation == wizard) {
                        htmltext = "marketeer_of_mammon_q0060_08.htm";
                    } else if (talker_occupation == cleric) {
                        htmltext = "marketeer_of_mammon_q0060_09.htm";
                    } else if (talker_occupation == elven_knight) {
                        htmltext = "marketeer_of_mammon_q0060_10.htm";
                    } else if (talker_occupation == elven_scout) {
                        htmltext = "marketeer_of_mammon_q0060_11.htm";
                    } else if (talker_occupation == elven_wizard) {
                        htmltext = "marketeer_of_mammon_q0060_12.htm";
                    } else if (talker_occupation == oracle) {
                        htmltext = "marketeer_of_mammon_q0060_13.htm";
                    } else if (talker_occupation == palus_knight) {
                        htmltext = "marketeer_of_mammon_q0060_14.htm";
                    } else if (talker_occupation == assassin) {
                        htmltext = "marketeer_of_mammon_q0060_15.htm";
                    } else if (talker_occupation == dark_wizard) {
                        htmltext = "marketeer_of_mammon_q0060_16.htm";
                    } else if (talker_occupation == shillien_oracle) {
                        htmltext = "marketeer_of_mammon_q0060_17.htm";
                    } else if (talker_occupation == orc_raider) {
                        htmltext = "marketeer_of_mammon_q0060_18.htm";
                    } else if (talker_occupation == orc_monk) {
                        htmltext = "marketeer_of_mammon_q0060_19.htm";
                    } else if (talker_occupation == orc_shaman) {
                        htmltext = "marketeer_of_mammon_q0060_20.htm";
                    } else if (talker_occupation == scavenger) {
                        htmltext = "marketeer_of_mammon_q0060_21.htm";
                    } else if (talker_occupation == artisan) {
                        htmltext = "marketeer_of_mammon_q0060_22.htm";
                    }
                    st.setMemoState("result_of_good_deed_flag", String.valueOf(1), true);
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=60&reply=5")) {
                if (GetOneTimeQuestFlag == 1 && second_class_group) {
                    if (talker_occupation == warrior) {
                        htmltext = "marketeer_of_mammon_q0060_05a.htm";
                    } else if (talker_occupation == knight) {
                        htmltext = "marketeer_of_mammon_q0060_06a.htm";
                    } else if (talker_occupation == rogue) {
                        htmltext = "marketeer_of_mammon_q0060_07a.htm";
                    } else if (talker_occupation == wizard) {
                        htmltext = "marketeer_of_mammon_q0060_08a.htm";
                    } else if (talker_occupation == cleric) {
                        htmltext = "marketeer_of_mammon_q0060_09a.htm";
                    } else if (talker_occupation == elven_knight) {
                        htmltext = "marketeer_of_mammon_q0060_10a.htm";
                    } else if (talker_occupation == elven_scout) {
                        htmltext = "marketeer_of_mammon_q0060_11a.htm";
                    } else if (talker_occupation == elven_wizard) {
                        htmltext = "marketeer_of_mammon_q0060_12a.htm";
                    } else if (talker_occupation == oracle) {
                        htmltext = "marketeer_of_mammon_q0060_13a.htm";
                    } else if (talker_occupation == palus_knight) {
                        htmltext = "marketeer_of_mammon_q0060_14a.htm";
                    } else if (talker_occupation == assassin) {
                        htmltext = "marketeer_of_mammon_q0060_15a.htm";
                    } else if (talker_occupation == dark_wizard) {
                        htmltext = "marketeer_of_mammon_q0060_16a.htm";
                    } else if (talker_occupation == shillien_oracle) {
                        htmltext = "marketeer_of_mammon_q0060_17a.htm";
                    } else if (talker_occupation == orc_raider) {
                        htmltext = "marketeer_of_mammon_q0060_18a.htm";
                    } else if (talker_occupation == orc_monk) {
                        htmltext = "marketeer_of_mammon_q0060_19a.htm";
                    } else if (talker_occupation == orc_shaman) {
                        htmltext = "marketeer_of_mammon_q0060_20a.htm";
                    } else if (talker_occupation == scavenger) {
                        htmltext = "marketeer_of_mammon_q0060_21a.htm";
                    } else if (talker_occupation == artisan) {
                        htmltext = "marketeer_of_mammon_q0060_22a.htm";
                    }
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=60&reply=11")) {
                if (talker_occupation == warrior) {
                    if (st.ownItemCount(mark_of_challenger) == 0) {
                        st.giveItems(mark_of_challenger, 1);
                    }
                    if (st.ownItemCount(mark_of_trust) == 0) {
                        st.giveItems(mark_of_trust, 1);
                    }
                    if (st.ownItemCount(mark_of_duelist) == 0) {
                        st.giveItems(mark_of_duelist, 1);
                    }
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "marketeer_of_mammon_q0060_25.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=60&reply=12")) {
                if (talker_occupation == warrior) {
                    if (st.ownItemCount(mark_of_challenger) == 0) {
                        st.giveItems(mark_of_challenger, 1);
                    }
                    if (st.ownItemCount(mark_of_trust) == 0) {
                        st.giveItems(mark_of_trust, 1);
                    }
                    if (st.ownItemCount(mark_of_champion) == 0) {
                        st.giveItems(mark_of_champion, 1);
                    }
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "marketeer_of_mammon_q0060_25.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=60&reply=13")) {
                if (talker_occupation == knight) {
                    if (st.ownItemCount(mark_of_duty) == 0) {
                        st.giveItems(mark_of_duty, 1);
                    }
                    if (st.ownItemCount(mark_of_trust) == 0) {
                        st.giveItems(mark_of_trust, 1);
                    }
                    if (st.ownItemCount(mark_of_healer) == 0) {
                        st.giveItems(mark_of_healer, 1);
                    }
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "marketeer_of_mammon_q0060_25.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=60&reply=14")) {
                if (talker_occupation == knight) {
                    if (st.ownItemCount(mark_of_duty) == 0) {
                        st.giveItems(mark_of_duty, 1);
                    }
                    if (st.ownItemCount(mark_of_trust) == 0) {
                        st.giveItems(mark_of_trust, 1);
                    }
                    if (st.ownItemCount(mark_of_witchcraft) == 0) {
                        st.giveItems(mark_of_witchcraft, 1);
                    }
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "marketeer_of_mammon_q0060_25.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=60&reply=15")) {
                if (talker_occupation == rogue) {
                    if (st.ownItemCount(mark_of_seeker) == 0) {
                        st.giveItems(mark_of_seeker, 1);
                    }
                    if (st.ownItemCount(mark_of_trust) == 0) {
                        st.giveItems(mark_of_trust, 1);
                    }
                    if (st.ownItemCount(mark_of_searcher) == 0) {
                        st.giveItems(mark_of_searcher, 1);
                    }
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "marketeer_of_mammon_q0060_25.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=60&reply=16")) {
                if (talker_occupation == rogue) {
                    if (st.ownItemCount(mark_of_seeker) == 0) {
                        st.giveItems(mark_of_seeker, 1);
                    }
                    if (st.ownItemCount(mark_of_trust) == 0) {
                        st.giveItems(mark_of_trust, 1);
                    }
                    if (st.ownItemCount(mark_of_sagittarius) == 0) {
                        st.giveItems(mark_of_sagittarius, 1);
                    }
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "marketeer_of_mammon_q0060_25.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=60&reply=17")) {
                if (talker_occupation == wizard) {
                    if (st.ownItemCount(mark_of_scholar) == 0) {
                        st.giveItems(mark_of_scholar, 1);
                    }
                    if (st.ownItemCount(mark_of_trust) == 0) {
                        st.giveItems(mark_of_trust, 1);
                    }
                    if (st.ownItemCount(mark_of_magus) == 0) {
                        st.giveItems(mark_of_magus, 1);
                    }
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "marketeer_of_mammon_q0060_25.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=60&reply=18")) {
                if (talker_occupation == wizard) {
                    if (st.ownItemCount(mark_of_scholar) == 0) {
                        st.giveItems(mark_of_scholar, 1);
                    }
                    if (st.ownItemCount(mark_of_trust) == 0) {
                        st.giveItems(mark_of_trust, 1);
                    }
                    if (st.ownItemCount(mark_of_witchcraft) == 0) {
                        st.giveItems(mark_of_witchcraft, 1);
                    }
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "marketeer_of_mammon_q0060_25.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=60&reply=19")) {
                if (talker_occupation == wizard) {
                    if (st.ownItemCount(mark_of_scholar) == 0) {
                        st.giveItems(mark_of_scholar, 1);
                    }
                    if (st.ownItemCount(mark_of_trust) == 0) {
                        st.giveItems(mark_of_trust, 1);
                    }
                    if (st.ownItemCount(mark_of_summoner) == 0) {
                        st.giveItems(mark_of_summoner, 1);
                    }
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "marketeer_of_mammon_q0060_25.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=60&reply=20")) {
                if (talker_occupation == cleric) {
                    if (st.ownItemCount(mark_of_pilgrim) == 0) {
                        st.giveItems(mark_of_pilgrim, 1);
                    }
                    if (st.ownItemCount(mark_of_trust) == 0) {
                        st.giveItems(mark_of_trust, 1);
                    }
                    if (st.ownItemCount(mark_of_healer) == 0) {
                        st.giveItems(mark_of_healer, 1);
                    }
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "marketeer_of_mammon_q0060_25.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=60&reply=21")) {
                if (talker_occupation == cleric) {
                    if (st.ownItemCount(mark_of_pilgrim) == 0) {
                        st.giveItems(mark_of_pilgrim, 1);
                    }
                    if (st.ownItemCount(mark_of_trust) == 0) {
                        st.giveItems(mark_of_trust, 1);
                    }
                    if (st.ownItemCount(mark_of_reformer) == 0) {
                        st.giveItems(mark_of_reformer, 1);
                    }
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "marketeer_of_mammon_q0060_25.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=60&reply=22")) {
                if (talker_occupation == elven_knight) {
                    if (st.ownItemCount(mark_of_duty) == 0) {
                        st.giveItems(mark_of_duty, 1);
                    }
                    if (st.ownItemCount(mark_of_life) == 0) {
                        st.giveItems(mark_of_life, 1);
                    }
                    if (st.ownItemCount(mark_of_healer) == 0) {
                        st.giveItems(mark_of_healer, 1);
                    }
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "marketeer_of_mammon_q0060_25.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=60&reply=23")) {
                if (talker_occupation == elven_knight) {
                    if (st.ownItemCount(mark_of_challenger) == 0) {
                        st.giveItems(mark_of_challenger, 1);
                    }
                    if (st.ownItemCount(mark_of_life) == 0) {
                        st.giveItems(mark_of_life, 1);
                    }
                    if (st.ownItemCount(mark_of_duelist) == 0) {
                        st.giveItems(mark_of_duelist, 1);
                    }
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "marketeer_of_mammon_q0060_25.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=60&reply=24")) {
                if (talker_occupation == elven_scout) {
                    if (st.ownItemCount(mark_of_seeker) == 0) {
                        st.giveItems(mark_of_seeker, 1);
                    }
                    if (st.ownItemCount(mark_of_life) == 0) {
                        st.giveItems(mark_of_life, 1);
                    }
                    if (st.ownItemCount(mark_of_searcher) == 0) {
                        st.giveItems(mark_of_searcher, 1);
                    }
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "marketeer_of_mammon_q0060_25.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=60&reply=25")) {
                if (talker_occupation == elven_scout) {
                    if (st.ownItemCount(mark_of_seeker) == 0) {
                        st.giveItems(mark_of_seeker, 1);
                    }
                    if (st.ownItemCount(mark_of_life) == 0) {
                        st.giveItems(mark_of_life, 1);
                    }
                    if (st.ownItemCount(mark_of_sagittarius) == 0) {
                        st.giveItems(mark_of_sagittarius, 1);
                    }
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "marketeer_of_mammon_q0060_25.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=60&reply=26")) {
                if (talker_occupation == elven_wizard) {
                    if (st.ownItemCount(mark_of_scholar) == 0) {
                        st.giveItems(mark_of_scholar, 1);
                    }
                    if (st.ownItemCount(mark_of_life) == 0) {
                        st.giveItems(mark_of_life, 1);
                    }
                    if (st.ownItemCount(mark_of_magus) == 0) {
                        st.giveItems(mark_of_magus, 1);
                    }
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "marketeer_of_mammon_q0060_25.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=60&reply=27")) {
                if (talker_occupation == elven_wizard) {
                    if (st.ownItemCount(mark_of_scholar) == 0) {
                        st.giveItems(mark_of_scholar, 1);
                    }
                    if (st.ownItemCount(mark_of_life) == 0) {
                        st.giveItems(mark_of_life, 1);
                    }
                    if (st.ownItemCount(mark_of_summoner) == 0) {
                        st.giveItems(mark_of_summoner, 1);
                    }
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "marketeer_of_mammon_q0060_25.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=60&reply=28")) {
                if (talker_occupation == oracle) {
                    if (st.ownItemCount(mark_of_pilgrim) == 0) {
                        st.giveItems(mark_of_pilgrim, 1);
                    }
                    if (st.ownItemCount(mark_of_life) == 0) {
                        st.giveItems(mark_of_life, 1);
                    }
                    if (st.ownItemCount(mark_of_healer) == 0) {
                        st.giveItems(mark_of_healer, 1);
                    }
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "marketeer_of_mammon_q0060_25.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=60&reply=29")) {
                if (talker_occupation == palus_knight) {
                    if (st.ownItemCount(mark_of_duty) == 0) {
                        st.giveItems(mark_of_duty, 1);
                    }
                    if (st.ownItemCount(mark_of_fate) == 0) {
                        st.giveItems(mark_of_fate, 1);
                    }
                    if (st.ownItemCount(mark_of_witchcraft) == 0) {
                        st.giveItems(mark_of_witchcraft, 1);
                    }
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "marketeer_of_mammon_q0060_25.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=60&reply=30")) {
                if (talker_occupation == palus_knight) {
                    if (st.ownItemCount(mark_of_challenger) == 0) {
                        st.giveItems(mark_of_challenger, 1);
                    }
                    if (st.ownItemCount(mark_of_fate) == 0) {
                        st.giveItems(mark_of_fate, 1);
                    }
                    if (st.ownItemCount(mark_of_duelist) == 0) {
                        st.giveItems(mark_of_duelist, 1);
                    }
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "marketeer_of_mammon_q0060_25.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=60&reply=31")) {
                if (talker_occupation == assassin) {
                    if (st.ownItemCount(mark_of_seeker) == 0) {
                        st.giveItems(mark_of_seeker, 1);
                    }
                    if (st.ownItemCount(mark_of_fate) == 0) {
                        st.giveItems(mark_of_fate, 1);
                    }
                    if (st.ownItemCount(mark_of_searcher) == 0) {
                        st.giveItems(mark_of_searcher, 1);
                    }
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "marketeer_of_mammon_q0060_25.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=60&reply=32")) {
                if (talker_occupation == assassin) {
                    if (st.ownItemCount(mark_of_seeker) == 0) {
                        st.giveItems(mark_of_seeker, 1);
                    }
                    if (st.ownItemCount(mark_of_fate) == 0) {
                        st.giveItems(mark_of_fate, 1);
                    }
                    if (st.ownItemCount(mark_of_sagittarius) == 0) {
                        st.giveItems(mark_of_sagittarius, 1);
                    }
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "marketeer_of_mammon_q0060_25.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=60&reply=33")) {
                if (talker_occupation == dark_wizard) {
                    if (st.ownItemCount(mark_of_scholar) == 0) {
                        st.giveItems(mark_of_scholar, 1);
                    }
                    if (st.ownItemCount(mark_of_fate) == 0) {
                        st.giveItems(mark_of_fate, 1);
                    }
                    if (st.ownItemCount(mark_of_magus) == 0) {
                        st.giveItems(mark_of_magus, 1);
                    }
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "marketeer_of_mammon_q0060_25.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=60&reply=34")) {
                if (talker_occupation == dark_wizard) {
                    if (st.ownItemCount(mark_of_scholar) == 0) {
                        st.giveItems(mark_of_scholar, 1);
                    }
                    if (st.ownItemCount(mark_of_fate) == 0) {
                        st.giveItems(mark_of_fate, 1);
                    }
                    if (st.ownItemCount(mark_of_summoner) == 0) {
                        st.giveItems(mark_of_summoner, 1);
                    }
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "marketeer_of_mammon_q0060_25.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=60&reply=35")) {
                if (talker_occupation == shillien_oracle) {
                    if (st.ownItemCount(mark_of_pilgrim) == 0) {
                        st.giveItems(mark_of_pilgrim, 1);
                    }
                    if (st.ownItemCount(mark_of_fate) == 0) {
                        st.giveItems(mark_of_fate, 1);
                    }
                    if (st.ownItemCount(mark_of_reformer) == 0) {
                        st.giveItems(mark_of_reformer, 1);
                    }
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "marketeer_of_mammon_q0060_25.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=60&reply=36")) {
                if (talker_occupation == orc_raider) {
                    if (st.ownItemCount(mark_of_challenger) == 0) {
                        st.giveItems(mark_of_challenger, 1);
                    }
                    if (st.ownItemCount(mark_of_glory) == 0) {
                        st.giveItems(mark_of_glory, 1);
                    }
                    if (st.ownItemCount(mark_of_champion) == 0) {
                        st.giveItems(mark_of_champion, 1);
                    }
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "marketeer_of_mammon_q0060_25.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=60&reply=37")) {
                if (talker_occupation == orc_monk) {
                    if (st.ownItemCount(mark_of_challenger) == 0) {
                        st.giveItems(mark_of_challenger, 1);
                    }
                    if (st.ownItemCount(mark_of_glory) == 0) {
                        st.giveItems(mark_of_glory, 1);
                    }
                    if (st.ownItemCount(mark_of_duelist) == 0) {
                        st.giveItems(mark_of_duelist, 1);
                    }
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "marketeer_of_mammon_q0060_25.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=60&reply=38")) {
                if (talker_occupation == orc_shaman) {
                    if (st.ownItemCount(mark_of_pilgrim) == 0) {
                        st.giveItems(mark_of_pilgrim, 1);
                    }
                    if (st.ownItemCount(mark_of_glory) == 0) {
                        st.giveItems(mark_of_glory, 1);
                    }
                    if (st.ownItemCount(mark_of_lord) == 0) {
                        st.giveItems(mark_of_lord, 1);
                    }
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "marketeer_of_mammon_q0060_25.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=60&reply=39")) {
                if (talker_occupation == orc_shaman) {
                    if (st.ownItemCount(mark_of_pilgrim) == 0) {
                        st.giveItems(mark_of_pilgrim, 1);
                    }
                    if (st.ownItemCount(mark_of_glory) == 0) {
                        st.giveItems(mark_of_glory, 1);
                    }
                    if (st.ownItemCount(mark_of_warspirit) == 0) {
                        st.giveItems(mark_of_warspirit, 1);
                    }
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "marketeer_of_mammon_q0060_25.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=60&reply=40")) {
                if (talker_occupation == scavenger) {
                    if (st.ownItemCount(mark_of_guildsman) == 0) {
                        st.giveItems(mark_of_guildsman, 1);
                    }
                    if (st.ownItemCount(mark_of_prosperity) == 0) {
                        st.giveItems(mark_of_prosperity, 1);
                    }
                    if (st.ownItemCount(mark_of_searcher) == 0) {
                        st.giveItems(mark_of_searcher, 1);
                    }
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "marketeer_of_mammon_q0060_25.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=60&reply=41")) {
                if (talker_occupation == artisan) {
                    if (st.ownItemCount(mark_of_guildsman) == 0) {
                        st.giveItems(mark_of_guildsman, 1);
                    }
                    if (st.ownItemCount(mark_of_prosperity) == 0) {
                        st.giveItems(mark_of_prosperity, 1);
                    }
                    if (st.ownItemCount(mark_of_maestro) == 0) {
                        st.giveItems(mark_of_maestro, 1);
                    }
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "marketeer_of_mammon_q0060_25.htm";
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        boolean second_class_group = st.getPlayer().getPlayerClassComponent().getClassId().isOfLevel(ClassLevel.Second);
        int GetMemoState = st.getInt("result_of_good_deed");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == blueprint_seller_daeger) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "blueprint_seller_daeger_q0060_03.htm";
                            st.exitQuest(true);
                            break;
                        case RACE:
                            htmltext = "blueprint_seller_daeger_q0060_06.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            if (second_class_group)
                                htmltext = "blueprint_seller_daeger_q0060_01.htm";
                            else
                                htmltext = "blueprint_seller_daeger_q0060_04.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == blueprint_seller_daeger) {
                    if (GetMemoState <= 2)
                        htmltext = "blueprint_seller_daeger_q0060_08.htm";
                    else if (GetMemoState == 3)
                        htmltext = "blueprint_seller_daeger_q0060_09.htm";
                    else if (GetMemoState == 4)
                        htmltext = "blueprint_seller_daeger_q0060_11.htm";
                    else if (GetMemoState > 4 && GetMemoState < 8)
                        htmltext = "blueprint_seller_daeger_q0060_12.htm";
                    else if (GetMemoState == 8)
                        htmltext = "blueprint_seller_daeger_q0060_13.htm";
                    else if (GetMemoState == 9) {
                        st.setCond(10);
                        st.setMemoState("result_of_good_deed", String.valueOf(10), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "blueprint_seller_daeger_q0060_15.htm";
                    } else if (GetMemoState == 10)
                        htmltext = "blueprint_seller_daeger_q0060_16.htm";
                } else if (npcId == mark_q0060_1) {
                    if (GetMemoState == 1) {
                        if (myself_i_quest0 == 0)
                            htmltext = "mark_q0060_1_q0060_01.htm";
                        else if (myself_i_quest0 == 1 && myself_i_quest1 == st.getPlayer().getObjectId())
                            htmltext = "mark_q0060_1_q0060_03.htm";
                        else if (myself_i_quest0 == 1 && myself_i_quest1 != st.getPlayer().getObjectId())
                            htmltext = "mark_q0060_1_q0060_02.htm";
                    } else if (GetMemoState == 2) {
                        st.setCond(3);
                        st.setMemoState("result_of_good_deed", String.valueOf(3), true);
                        st.giveItems(q_red_patch_q0060, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "mark_q0060_1_q0060_06.htm";
                    } else if (GetMemoState >= 3 && GetMemoState < 7)
                        htmltext = "mark_q0060_1_q0060_07.htm";
                    else if (GetMemoState == 7)
                        htmltext = "mark_q0060_1_q0060_09.htm";
                } else if (npcId == helvetia) {
                    if (GetMemoState == 4)
                        htmltext = "helvetia_q0060_01.htm";
                    else if (GetMemoState == 5)
                        htmltext = "helvetia_q0060_04.htm";
                    else if (GetMemoState == 6)
                        htmltext = "helvetia_q0060_08.htm";
                    else if (GetMemoState == 7) {
                        if (st.ownItemCount(q_antidote_of_hel) == 0) {
                            st.giveItems(q_antidote_of_hel, 1);
                            htmltext = "helvetia_q0060_09.htm";
                        } else
                            htmltext = "helvetia_q0060_10.htm";
                    }
                } else if (npcId == marketeer_of_mammon) {
                    if (GetMemoState == 10) {
                        if (second_class_group) {
                            st.setMemoState("result_of_good_deed_ex", String.valueOf(0), true);
                            htmltext = "marketeer_of_mammon_q0060_01.htm";
                        } else {
                            htmltext = "marketeer_of_mammon_q0060_01a.htm";
                            st.giveItems(ADENA_ID, 3000000);
                            st.soundEffect(SOUND_FINISH);
                            st.exitQuest(false);
                        }
                    }
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("result_of_good_deed");
        int npcId = npc.getNpcId();
        if (npcId == chaser_q0060) {
            if (GetMemoState == 1) {
                if (myself_i_quest1 == st.getPlayer().getObjectId()) {
                    st.setCond(2);
                    st.setMemoState("result_of_good_deed", String.valueOf(2), true);
                    st.soundEffect(SOUND_MIDDLE);
                    Functions.npcSay(npc, NpcString.YOU_ARE_STRONG_THIS_WAS_A_MISTAKE);
                    if (st.isRunningQuestTimer("6001"))
                        st.cancelQuestTimer("6001");
                    myself_i_quest1 = 0;
                    myself_i_quest0 = 0;
                } else {
                    Functions.npcSay(npc, NpcString.WHO_ARE_YOU_TO_JOIN_IN_THE_BATTLE_HOW_UPSETTING);
                    if (st.isRunningQuestTimer("6001"))
                        st.cancelQuestTimer("6001");
                    myself_i_quest1 = 0;
                    myself_i_quest0 = 0;
                }
            }
        }
        return null;
    }
}