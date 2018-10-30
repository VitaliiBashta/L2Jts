package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.base.ClassId;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
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
 * @date 29/01/2015
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _212_TrialOfDuty extends Quest {
    // npc
    private static final int hannavalt = 30109;
    private static final int sir_aron_tanford = 30653;
    private static final int sir_kiel_nighthawk = 30654;
    private static final int spirit_of_sir_talianus = 30656;
    private static final int dustin = 30116;
    private static final int sir_collin_windawood = 30311;
    private static final int isael_silvershadow = 30655;
    // mobs
    private static final int hanged_man_ripper = 20144;
    private static final int skeleton_rapid_shooter = 20190;
    private static final int skeleton_raider = 20191;
    private static final int strain = 20200;
    private static final int ghoul = 20201;
    private static final int breka_orc_overlord = 20270;
    private static final int leto_lizardman = 20577;
    private static final int leto_lizardman_archer = 20578;
    private static final int leto_lizardman_soldier = 20579;
    private static final int leto_lizardman_warrior = 20580;
    private static final int leto_lizardman_shaman = 20581;
    private static final int leto_lizardman_overlord = 20582;
    private static final int spirit_of_sir_herod = 27119;
    // questitem
    private static final int mark_of_duty = 2633;
    private static final int letter_of_dustin = 2634;
    private static final int knights_tear = 2635;
    private static final int mirror_of_orpic = 2636;
    private static final int tear_of_confession = 2637;
    private static final int report_piece = 2638;
    private static final int talianuss_report = 2639;
    private static final int tear_of_loyalty = 2640;
    private static final int militas_article = 2641;
    private static final int saints_ashes_urn = 2642;
    private static final int atebalts_skull = 2643;
    private static final int atebalts_ribs = 2644;
    private static final int atebalts_shin = 2645;
    private static final int letter_of_windawood = 2646;
    // etcitem
    private static final int old_knight_sword = 3027;
    private static final int q_dimension_diamond = 7562;

    public _212_TrialOfDuty() {
        super(false);
        addStartNpc(hannavalt);
        addTalkId(sir_aron_tanford, sir_kiel_nighthawk, spirit_of_sir_talianus, dustin, sir_collin_windawood, isael_silvershadow);
        addKillId(hanged_man_ripper, skeleton_rapid_shooter, skeleton_raider, strain, ghoul, breka_orc_overlord, leto_lizardman, leto_lizardman_archer, leto_lizardman_soldier, leto_lizardman_warrior, leto_lizardman_shaman, leto_lizardman_overlord, spirit_of_sir_herod);
        addQuestItem(letter_of_dustin, old_knight_sword, knights_tear, talianuss_report, mirror_of_orpic, tear_of_confession, tear_of_loyalty, atebalts_skull, atebalts_ribs, atebalts_shin, saints_ashes_urn, letter_of_windawood, militas_article, report_piece);
        addLevelCheck(35);
        addClassIdCheck(ClassId.knight, ClassId.elven_knight, ClassId.palus_knight);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == hannavalt) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("trial_of_duty", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                if (!st.getPlayer().getPlayerVariables().getBoolean(PlayerVariables.DD1)) {
                    st.giveItems(q_dimension_diamond, 61);
                    st.getPlayer().getPlayerVariables().set(PlayerVariables.DD1, "1", -1);
                    htmltext = "hannavalt_q0212_04a.htm";
                } else
                    htmltext = "hannavalt_q0212_04.htm";
            }
        } else if (npcId == dustin) {
            if (event.equalsIgnoreCase("menu_select?ask=212&reply=1"))
                htmltext = "dustin_q0212_02.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=212&reply=2"))
                htmltext = "dustin_q0212_03.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=212&reply=3"))
                htmltext = "dustin_q0212_04.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=212&reply=4") && st.ownItemCount(tear_of_loyalty) > 0) {
                st.setCond(14);
                st.setMemoState("trial_of_duty", String.valueOf(11), true);
                st.takeItems(tear_of_loyalty, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "dustin_q0212_05.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("trial_of_duty");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == hannavalt) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "hannavalt_q0212_01.htm";
                            st.exitQuest(true);
                            break;
                        case CLASS_ID:
                            htmltext = "hannavalt_q0212_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "hannavalt_q0212_03.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == hannavalt) {
                    if (GetMemoState == 1)
                        htmltext = "hannavalt_q0212_04.htm";
                    else if (st.ownItemCount(letter_of_dustin) > 0 && GetMemoState == 14) {
                        st.addExpAndSp(762576, 49458);
                        st.giveItems(ADENA_ID, 138968);
                        st.giveItems(mark_of_duty, 1);
                        st.takeItems(letter_of_dustin, 1);
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                        st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                        st.removeMemo("trial_of_duty");
                        htmltext = "hannavalt_q0212_05.htm";
                    }
                } else if (npcId == sir_aron_tanford) {
                    if (GetMemoState == 1) {
                        st.setCond(2);
                        st.setMemoState("trial_of_duty", String.valueOf(2), true);
                        if (st.ownItemCount(old_knight_sword) == 0)
                            st.giveItems(old_knight_sword, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "sir_aron_tanford_q0212_01.htm";
                    } else if (st.ownItemCount(knights_tear) == 0 && GetMemoState == 2)
                        htmltext = "sir_aron_tanford_q0212_02.htm";
                    else if (st.ownItemCount(knights_tear) > 0 && GetMemoState == 3) {
                        st.setCond(4);
                        st.setMemoState("trial_of_duty", String.valueOf(4), true);
                        st.takeItems(knights_tear, 1);
                        st.takeItems(old_knight_sword, -1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "sir_aron_tanford_q0212_03.htm";
                    } else if (GetMemoState == 4)
                        htmltext = "sir_aron_tanford_q0212_04.htm";
                } else if (npcId == sir_kiel_nighthawk) {
                    if (GetMemoState == 4) {
                        st.setCond(5);
                        st.setMemoState("trial_of_duty", String.valueOf(5), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "sir_kiel_nighthawk_q0212_01.htm";
                    } else if (GetMemoState == 5 && st.ownItemCount(talianuss_report) == 0)
                        htmltext = "sir_kiel_nighthawk_q0212_02.htm";
                    else if (GetMemoState == 5 && st.ownItemCount(talianuss_report) > 0) {
                        st.setCond(7);
                        st.setMemoState("trial_of_duty", String.valueOf(6), true);
                        st.giveItems(mirror_of_orpic, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "sir_kiel_nighthawk_q0212_03.htm";
                    } else if (GetMemoState == 6)
                        htmltext = "sir_kiel_nighthawk_q0212_04.htm";
                    else if (GetMemoState == 7 && st.ownItemCount(tear_of_confession) > 0) {
                        st.setCond(10);
                        st.setMemoState("trial_of_duty", String.valueOf(8), true);
                        st.takeItems(tear_of_confession, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "sir_kiel_nighthawk_q0212_03.htm";
                    } else if (GetMemoState == 8)
                        htmltext = "sir_kiel_nighthawk_q0212_06.htm";
                } else if (npcId == spirit_of_sir_talianus) {
                    if (GetMemoState == 6 && st.ownItemCount(mirror_of_orpic) > 0) {
                        st.setCond(9);
                        st.setMemoState("trial_of_duty", String.valueOf(7), true);
                        st.takeItems(mirror_of_orpic, 1);
                        st.takeItems(talianuss_report, -1);
                        st.giveItems(tear_of_confession, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "spirit_of_sir_talianus_q0212_01.htm";
                        if (npc != null)
                            npc.deleteMe();
                    }
                } else if (npcId == dustin) {
                    if (GetMemoState == 10 && st.ownItemCount(tear_of_loyalty) > 0)
                        htmltext = "dustin_q0212_01.htm";
                    else if (GetMemoState == 11 && (st.ownItemCount(atebalts_skull) == 0 || st.ownItemCount(atebalts_ribs) == 0 || st.ownItemCount(atebalts_shin) == 0))
                        htmltext = "dustin_q0212_06.htm";
                    else if (GetMemoState == 11 && st.ownItemCount(atebalts_skull) > 0 && st.ownItemCount(atebalts_ribs) > 0 && st.ownItemCount(atebalts_shin) > 0) {
                        st.setCond(16);
                        st.setMemoState("trial_of_duty", String.valueOf(12), true);
                        st.takeItems(atebalts_skull, 1);
                        st.takeItems(atebalts_ribs, 1);
                        st.takeItems(atebalts_shin, 1);
                        st.giveItems(saints_ashes_urn, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "dustin_q0212_07.htm";
                    } else if (GetMemoState == 13 && st.ownItemCount(letter_of_windawood) > 0) {
                        st.setCond(18);
                        st.setMemoState("trial_of_duty", String.valueOf(14), true);
                        st.takeItems(letter_of_windawood, 1);
                        st.giveItems(letter_of_dustin, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "dustin_q0212_08.htm";
                    } else if (GetMemoState == 12)
                        htmltext = "dustin_q0212_09.htm";
                    else if (GetMemoState == 14)
                        htmltext = "dustin_q0212_10.htm";
                } else if (npcId == sir_collin_windawood) {
                    if (GetMemoState == 12 && st.ownItemCount(saints_ashes_urn) > 0) {
                        st.setCond(17);
                        st.setMemoState("trial_of_duty", String.valueOf(13), true);
                        st.takeItems(saints_ashes_urn, 1);
                        st.giveItems(letter_of_windawood, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "sir_collin_windawood_q0212_01.htm";
                    } else if (GetMemoState == 13)
                        htmltext = "sir_collin_windawood_q0212_02.htm";
                } else if (npcId == isael_silvershadow) {
                    if (GetMemoState == 8) {
                        if (st.getPlayer().getLevel() >= 35) {
                            st.setCond(11);
                            st.setMemoState("trial_of_duty", String.valueOf(9), true);
                            st.soundEffect(SOUND_MIDDLE);
                            htmltext = "isael_silvershadow_q0212_02.htm";
                        } else
                            htmltext = "isael_silvershadow_q0212_01.htm";
                    } else if (GetMemoState == 9) {
                        if (st.ownItemCount(militas_article) < 20)
                            htmltext = "isael_silvershadow_q0212_03.htm";
                        else {
                            st.setCond(13);
                            st.setMemoState("trial_of_duty", String.valueOf(10), true);
                            st.giveItems(tear_of_loyalty, 1);
                            st.takeItems(militas_article, -1);
                            st.soundEffect(SOUND_MIDDLE);
                            htmltext = "isael_silvershadow_q0212_04.htm";
                        }
                    } else if (GetMemoState == 10)
                        htmltext = "isael_silvershadow_q0212_05.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("trial_of_duty");
        int npcId = npc.getNpcId();
        if (npcId == hanged_man_ripper) {
            if (GetMemoState == 6) {
                if (Rnd.get(100) < 33) {
                    st.setCond(8);
                    st.addSpawn(spirit_of_sir_talianus, npc.getX(), npc.getY(), npc.getZ());
                    st.soundEffect(SOUND_MIDDLE);
                }
            }
        } else if (npcId == skeleton_rapid_shooter || npcId == skeleton_raider) {
            if (GetMemoState == 2) {
                if (Rnd.get(100) < 10) {
                    st.addSpawn(spirit_of_sir_herod, npc.getX(), npc.getY(), npc.getZ());
                    st.soundEffect(SOUND_BEFORE_BATTLE);
                }
            }
        } else if (npcId == strain || npcId == ghoul) {
            if (GetMemoState == 5 && st.ownItemCount(report_piece) < 10 && st.ownItemCount(talianuss_report) == 0) {
                if (st.ownItemCount(report_piece) == 9) {
                    if (Rnd.get(2) <= 1) {
                        st.setCond(6);
                        st.giveItems(talianuss_report, 1);
                        st.takeItems(report_piece, -1);
                        st.soundEffect(SOUND_MIDDLE);
                    }
                } else if (Rnd.get(2) <= 1) {
                    st.giveItems(report_piece, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == breka_orc_overlord) {
            if (GetMemoState == 11) {
                if (Rnd.get(2) <= 1) {
                    if (st.ownItemCount(atebalts_skull) == 0) {
                        st.giveItems(atebalts_skull, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    } else if (st.ownItemCount(atebalts_ribs) == 0) {
                        st.giveItems(atebalts_ribs, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    } else if (st.ownItemCount(atebalts_shin) == 0) {
                        st.setCond(15);
                        st.giveItems(atebalts_shin, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        } else if (npcId == leto_lizardman || npcId == leto_lizardman_archer || npcId == leto_lizardman_soldier || npcId == leto_lizardman_warrior || npcId == leto_lizardman_shaman || npcId == leto_lizardman_overlord) {
            if (GetMemoState == 9 && st.ownItemCount(militas_article) < 20) {
                if (st.ownItemCount(militas_article) == 19) {
                    st.setCond(12);
                    st.giveItems(militas_article, 1);
                    st.soundEffect(SOUND_MIDDLE);
                } else {
                    st.giveItems(militas_article, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == spirit_of_sir_herod) {
            if (GetMemoState == 2 && st.getItemEquipped(Inventory.PAPERDOLL_RHAND) == old_knight_sword) {
                st.setCond(3);
                st.setMemoState("trial_of_duty", String.valueOf(3), true);
                st.giveItems(knights_tear, 1);
                st.soundEffect(SOUND_MIDDLE);
            }
        }
        return null;
    }
}