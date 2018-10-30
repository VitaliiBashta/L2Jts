package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.base.ClassId;
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
 * @date 22/01/2015
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _211_TrialOfChallenger extends Quest {
    // npc
    private static final int kash = 30644;
    private static final int martian = 30645;
    private static final int elder_filaur = 30535;
    private static final int raldo = 30646;
    private static final int chest_of_shyslassys = 30647;
    // mobs
    private static final int shyslassys = 27110;
    private static final int gorr = 27112;
    private static final int baraham = 27113;
    private static final int queen_of_succubus = 27114;
    // questitem
    private static final int letter_of_kash = 2628;
    private static final int watchers_eye1 = 2629;
    private static final int watchers_eye2 = 2630;
    private static final int scroll_of_shyslassy = 2631;
    private static final int broken_key = 2632;
    // etcitem
    private static final int mithril_scale_gaiters_material = 2918;
    private static final int brigandine_gauntlet_pattern = 2927;
    private static final int manticor_skin_gaiters_pattern = 1943;
    private static final int gauntlet_of_repose_of_the_soul_pattern = 1946;
    private static final int iron_boots_design = 1940;
    private static final int tome_of_blood_page = 2030;
    private static final int elven_necklace_beads = 1904;
    private static final int white_tunic_pattern = 1936;
    private static final int mark_of_challenger = 2627;
    private static final int q_dimension_diamond = 7562;

    public _211_TrialOfChallenger() {
        super(false);
        addStartNpc(kash);
        addTalkId(martian, elder_filaur, raldo, chest_of_shyslassys);
        addKillId(shyslassys, gorr, baraham, queen_of_succubus);
        addQuestItem(letter_of_kash, watchers_eye1, watchers_eye2, scroll_of_shyslassy, broken_key);
        addLevelCheck(35);
        addClassIdCheck(ClassId.warrior, ClassId.elven_knight, ClassId.palus_knight, ClassId.orc_raider, ClassId.orc_monk);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == kash) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("trial_of_challenger", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                if (!st.getPlayer().getPlayerVariables().getBoolean(PlayerVariables.DD1)) {
                    st.giveItems(q_dimension_diamond, 61);
                    st.getPlayer().getPlayerVariables().set(PlayerVariables.DD1, "1", -1);
                    htmltext = "kash_q0211_05a.htm";
                } else
                    htmltext = "kash_q0211_05.htm";
            } else if (event.equalsIgnoreCase("reply=1"))
                htmltext = "kash_q0211_04.htm";
        } else if (npcId == martian) {
            if (event.equalsIgnoreCase("reply=1")) {
                if (st.ownItemCount(letter_of_kash) > 0) {
                    st.setCond(4);
                    st.setMemoState("trial_of_challenger", String.valueOf(4), true);
                    st.takeItems(letter_of_kash, -1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "martian_q0211_02.htm";
                }
            }
        } else if (npcId == raldo) {
            if (event.equalsIgnoreCase("reply=1"))
                htmltext = "raldo_q0211_02.htm";
            else if (event.equalsIgnoreCase("reply=2"))
                htmltext = "raldo_q0211_03.htm";
            else if (event.equalsIgnoreCase("reply=3")) {
                if (st.ownItemCount(watchers_eye2) > 0) {
                    st.setCond(8);
                    st.setMemoState("trial_of_challenger", String.valueOf(7), true);
                    st.takeItems(watchers_eye2, -1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "raldo_q0211_04.htm";
                }
            } else if (event.equalsIgnoreCase("reply=4")) {
                if (st.ownItemCount(watchers_eye2) > 0) {
                    st.setCond(8);
                    st.setMemoState("trial_of_challenger", String.valueOf(7), true);
                    st.takeItems(watchers_eye2, -1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "raldo_q0211_06.htm";
                }
            }
        } else if (npcId == chest_of_shyslassys) {
            if (event.equalsIgnoreCase("reply=1")) {
                if (st.ownItemCount(broken_key) == 1) {
                    if (Rnd.get(10) < 2) {
                        htmltext = "chest_of_shyslassys_q0211_03.htm";
                        st.takeItems(broken_key, 1);
                        st.soundEffect(SOUND_JACKPOT);
                        int i0 = Rnd.get(100);
                        if (i0 > 90) {
                            st.giveItems(mithril_scale_gaiters_material, 1);
                            st.giveItems(brigandine_gauntlet_pattern, 1);
                            st.giveItems(manticor_skin_gaiters_pattern, 1);
                            st.giveItems(gauntlet_of_repose_of_the_soul_pattern, 1);
                            st.giveItems(iron_boots_design, 1);
                        } else if (i0 > 70) {
                            st.giveItems(tome_of_blood_page, 1);
                            st.giveItems(elven_necklace_beads, 1);
                        } else if (i0 > 40)
                            st.giveItems(white_tunic_pattern, 1);
                        else
                            st.giveItems(iron_boots_design, 1);
                    } else {
                        htmltext = "chest_of_shyslassys_q0211_02.htm";
                        int i1 = Rnd.get(1000) + 1;
                        st.giveItems(ADENA_ID, i1);
                        st.takeItems(broken_key, 1);
                    }
                } else
                    htmltext = "chest_of_shyslassys_q0211_04.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("trial_of_challenger");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == kash) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "kash_q0211_01.htm";
                            st.exitQuest(true);
                            break;
                        case CLASS_ID:
                            htmltext = "kash_q0211_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "kash_q0211_03.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == kash) {
                    if (GetMemoState == 1)
                        htmltext = "kash_q0211_06.htm";
                    else if (st.ownItemCount(scroll_of_shyslassy) == 1) {
                        st.setCond(3);
                        st.setMemoState("trial_of_challenger", String.valueOf(3), true);
                        st.giveItems(letter_of_kash, 1);
                        st.takeItems(scroll_of_shyslassy, -1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "kash_q0211_07.htm";
                    } else if (st.ownItemCount(letter_of_kash) == 1)
                        htmltext = "kash_q0211_08.htm";
                    else if (GetMemoState >= 7)
                        htmltext = "kash_q0211_09.htm";
                } else if (npcId == martian) {
                    if (st.ownItemCount(letter_of_kash) == 1)
                        htmltext = "martian_q0211_01.htm";
                    else if (GetMemoState == 4 && st.ownItemCount(watchers_eye1) == 0)
                        htmltext = "martian_q0211_03.htm";
                    else if (st.ownItemCount(watchers_eye1) > 0) {
                        st.setCond(6);
                        st.setMemoState("trial_of_challenger", String.valueOf(5), true);
                        st.takeItems(watchers_eye1, -1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "martian_q0211_04.htm";
                    } else if (GetMemoState == 5)
                        htmltext = "martian_q0211_05.htm";
                    else if (GetMemoState >= 7)
                        htmltext = "martian_q0211_06.htm";
                    else if (GetMemoState == 6)
                        htmltext = "martian_q0211_07.htm";
                } else if (npcId == elder_filaur) {
                    if (GetMemoState == 7) {
                        if (st.getPlayer().getLevel() >= 35) {
                            st.setCond(9);
                            st.setMemoState("trial_of_challenger", String.valueOf(8), true);
                            st.soundEffect(SOUND_MIDDLE);
                            htmltext = "elder_filaur_q0211_01.htm";
                        } else
                            htmltext = "elder_filaur_q0211_03.htm";
                    } else if (GetMemoState == 8) {
                        st.getPlayer().addRadar(151589, -174823, -1776);
                        htmltext = "elder_filaur_q0211_02.htm";
                    } else if (GetMemoState == 9)
                        htmltext = "elder_filaur_q0211_04.htm";
                } else if (npcId == raldo) {
                    if (st.ownItemCount(watchers_eye2) > 0)
                        htmltext = "raldo_q0211_01.htm";
                    else if (GetMemoState == 7)
                        htmltext = "raldo_q0211_06a.htm";
                    else if (GetMemoState == 9) {
                        st.takeItems(broken_key, -1);
                        st.giveItems(mark_of_challenger, 1);
                        st.addExpAndSp(1067606, 69242);
                        st.giveItems(ADENA_ID, 194556);
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                        st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                        st.removeMemo("trial_of_challenger");
                        htmltext = "raldo_q0211_07.htm";
                    }
                } else if (npcId == chest_of_shyslassys) {
                    if (GetMemoState == 2)
                        htmltext = "chest_of_shyslassys_q0211_01.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("trial_of_challenger");
        int npcId = npc.getNpcId();
        if (npcId == shyslassys) {
            if (GetMemoState == 1 && st.ownItemCount(scroll_of_shyslassy) == 0 && st.ownItemCount(broken_key) == 0) {
                st.setCond(2);
                st.setMemoState("trial_of_challenger", String.valueOf(2), true);
                st.soundEffect(SOUND_MIDDLE);
                st.giveItems(scroll_of_shyslassy, 1);
                st.giveItems(broken_key, 1);
                st.addSpawn(chest_of_shyslassys, npc.getX(), npc.getY(), npc.getZ(), 200000);
            }
        } else if (npcId == gorr) {
            if (GetMemoState == 4 && st.ownItemCount(watchers_eye1) == 0) {
                st.setCond(5);
                st.setMemoState("trial_of_challenger", String.valueOf(4), true);
                st.giveItems(watchers_eye1, 1);
                st.soundEffect(SOUND_MIDDLE);
            }
        } else if (npcId == baraham) {
            if (GetMemoState == 5 && st.ownItemCount(watchers_eye2) == 0) {
                st.setCond(7);
                st.setMemoState("trial_of_challenger", String.valueOf(6), true);
                st.giveItems(watchers_eye2, 1);
                st.soundEffect(SOUND_MIDDLE);
                st.addSpawn(raldo, npc.getX(), npc.getY(), npc.getZ(), 100000);
            }
        } else if (npcId == queen_of_succubus) {
            if (GetMemoState == 8) {
                st.setCond(10);
                st.setMemoState("trial_of_challenger", String.valueOf(9), true);
                st.soundEffect(SOUND_MIDDLE);
                st.addSpawn(raldo, npc.getX(), npc.getY(), npc.getZ(), 100000);
            }
        }
        return null;
    }
}