package org.mmocore.gameserver.scripts.quests;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.model.base.ClassId;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.serverpackets.RadarControl;
import org.mmocore.gameserver.network.lineage.serverpackets.SocialAction;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.2
 * @date 02/03/2015
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _064_CertifiedBerserker extends Quest {
    // npc
    private static final int master_orkurus = 32207;
    private static final int master_tenain = 32215;
    private static final int gort = 32252;
    private static final int master_entiens = 32200;
    private static final int harlkil_0064 = 32253;
    // mobs
    private static final int dead_seeker = 20202;
    private static final int marsh_stakato_drone = 20234;
    private static final int breka_orc = 20267;
    private static final int breka_orc_archer = 20268;
    private static final int breka_orc_shaman = 20269;
    private static final int breka_orc_overlord = 20270;
    private static final int breka_orc_warrior = 20271;
    private static final int road_scavenger = 20551;
    private static final int emisarry_of_einhasad = 27323;
    // questitem
    private static final int q_breka_orc_head = 9754;
    private static final int q_msg_plate = 9755;
    private static final int q_east_report = 9756;
    private static final int q_north_report = 9757;
    private static final int q_harlkil_letter = 9758;
    private static final int q_recom_of_tenain = 9759;
    private static final int q_recom_of_orkurus = 9760;
    // etcitem
    private static final int q_dimension_diamond = 7562;

    public _064_CertifiedBerserker() {
        super(false);
        addStartNpc(master_orkurus);
        addTalkId(master_tenain, gort, master_entiens, harlkil_0064);
        addKillId(dead_seeker, marsh_stakato_drone, breka_orc, breka_orc_archer, breka_orc_shaman, breka_orc_overlord, breka_orc_warrior, road_scavenger, emisarry_of_einhasad);
        addQuestItem(q_breka_orc_head, q_msg_plate, q_east_report, q_north_report, q_harlkil_letter, q_recom_of_tenain);
        addLevelCheck(39);
        addClassIdCheck(ClassId.trooper);
        addRaceCheck(PlayerRace.kamael);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("proof_of_berserker");
        int npcId = npc.getNpcId();
        if (npcId == master_orkurus) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("proof_of_berserker", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                if (!st.getPlayer().getPlayerVariables().getBoolean(PlayerVariables.DD1)) {
                    st.giveItems(q_dimension_diamond, 48);
                    st.getPlayer().getPlayerVariables().set(PlayerVariables.DD1, "1", -1);
                    htmltext = "master_orkurus_q0064_06.htm";
                } else
                    htmltext = "master_orkurus_q0064_06a.htm";
            } else if (event.equalsIgnoreCase("reply_1") && GetMemoState == 11)
                htmltext = "master_orkurus_q0064_10.htm";
            else if (event.equalsIgnoreCase("reply_2") && GetMemoState == 11) {
                st.giveItems(q_recom_of_orkurus, 1);
                if (!st.getPlayer().getPlayerVariables().getBoolean(PlayerVariables.PROF2_1)) {
                    st.addExpAndSp(349006, 23948);
                    st.giveItems(ADENA_ID, 63104);
                }
                st.takeItems(q_recom_of_tenain, -1);
                st.removeMemo("proof_of_berserker");
                st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(false);
                htmltext = "master_orkurus_q0064_11.htm";
            }
        } else if (npcId == master_tenain) {
            if (event.equalsIgnoreCase("reply_1") && GetMemoState == 1) {
                st.setCond(2);
                st.setMemoState("proof_of_berserker", String.valueOf(2), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "master_tenain_q0064_02.htm";
            } else if (event.equalsIgnoreCase("reply_2") && GetMemoState == 5)
                htmltext = "master_tenain_q0064_07.htm";
            else if (event.equalsIgnoreCase("reply_3") && GetMemoState == 5)
                htmltext = "master_tenain_q0064_08.htm";
            else if (event.equalsIgnoreCase("reply_4") && GetMemoState == 5)
                htmltext = "master_tenain_q0064_09.htm";
            else if (event.equalsIgnoreCase("reply_5") && GetMemoState == 5) {
                st.setCond(8);
                st.setMemoState("proof_of_berserker", String.valueOf(6), true);
                st.takeItems(q_msg_plate, -1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "master_tenain_q0064_10.htm";
            } else if (event.equalsIgnoreCase("reply_6") && GetMemoState == 10) {
                st.setCond(14);
                st.setMemoState("proof_of_berserker", String.valueOf(11), true);
                st.giveItems(q_recom_of_tenain, 1);
                st.takeItems(q_harlkil_letter, -1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "master_tenain_q0064_15.htm";
            }
        } else if (npcId == gort) {
            if (event.equalsIgnoreCase("reply_1") && GetMemoState == 3) {
                st.setCond(5);
                st.setMemoState("proof_of_berserker", String.valueOf(4), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "gort_q0064_02.htm";
            }
        } else if (npcId == harlkil_0064) {
            if (event.equalsIgnoreCase("reply_1") && GetMemoState == 9) {
                st.setCond(13);
                st.setMemoState("proof_of_berserker", String.valueOf(10), true);
                st.giveItems(q_harlkil_letter, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "harlkil_0064_q0064_02.htm";
                if (st.isRunningQuestTimer("6401"))
                    st.cancelQuestTimer("6401");
            }
        } else if (event.equalsIgnoreCase("6401")) {
            if (npc != null)
                npc.deleteMe();
            return null;
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("proof_of_berserker");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == master_orkurus) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "master_orkurus_q0064_02.htm";
                            st.exitQuest(true);
                            break;
                        case CLASS_ID:
                            htmltext = "master_orkurus_q0064_03.htm";
                            st.exitQuest(true);
                            break;
                        case RACE:
                            htmltext = "master_orkurus_q0064_04.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "master_orkurus_q0064_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == master_orkurus) {
                    if (GetMemoState == 1)
                        htmltext = "master_orkurus_q0064_07.htm";
                    else if (GetMemoState >= 2 && GetMemoState < 11)
                        htmltext = "master_orkurus_q0064_08.htm";
                    else if (GetMemoState == 11)
                        htmltext = "master_orkurus_q0064_09.htm";
                } else if (npcId == master_tenain) {
                    if (GetMemoState == 1)
                        htmltext = "master_tenain_q0064_01.htm";
                    else if (GetMemoState == 2 && st.ownItemCount(q_breka_orc_head) < 20)
                        htmltext = "master_tenain_q0064_03.htm";
                    else if (GetMemoState == 2 && st.ownItemCount(q_breka_orc_head) >= 20) {
                        st.setCond(4);
                        st.setMemoState("proof_of_berserker", String.valueOf(3), true);
                        st.takeItems(q_breka_orc_head, -1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "master_tenain_q0064_04.htm";
                    } else if (GetMemoState == 3)
                        htmltext = "master_tenain_q0064_05.htm";
                    else if (GetMemoState == 5)
                        htmltext = "master_tenain_q0064_06.htm";
                    else if (GetMemoState == 6)
                        htmltext = "master_tenain_q0064_11.htm";
                    else if (GetMemoState == 8) {
                        st.setCond(12);
                        st.setMemoState("proof_of_berserker", String.valueOf(9), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "master_tenain_q0064_12.htm";
                    } else if (GetMemoState == 9)
                        htmltext = "master_tenain_q0064_13.htm";
                    else if (GetMemoState == 10)
                        htmltext = "master_tenain_q0064_14.htm";
                    else if (GetMemoState == 11)
                        htmltext = "master_tenain_q0064_16.htm";
                } else if (npcId == gort) {
                    if (GetMemoState == 3)
                        htmltext = "gort_q0064_01.htm";
                    else if (GetMemoState == 4 && st.ownItemCount(q_msg_plate) == 0)
                        htmltext = "gort_q0064_03.htm";
                    else if (GetMemoState == 4 && st.ownItemCount(q_msg_plate) >= 1) {
                        st.setCond(7);
                        st.setMemoState("proof_of_berserker", String.valueOf(5), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "gort_q0064_04.htm";
                    } else if (GetMemoState == 5)
                        htmltext = "gort_q0064_05.htm";
                } else if (npcId == master_entiens) {
                    if (GetMemoState == 6) {
                        st.setCond(9);
                        st.setMemoState("proof_of_berserker", String.valueOf(7), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "master_entiens_q0064_01.htm";
                        st.getPlayer().sendPacket(new RadarControl(RadarControl.RadarState.SHOW_RADAR, RadarControl.RadarType.ARROW, 27956, 106003, -3831)); // Указывает на местонахождения marsh_stakato_drone.
                        st.getPlayer().sendPacket(new RadarControl(RadarControl.RadarState.SHOW_RADAR, RadarControl.RadarType.FLAG_ON_MAP, 50568, 152408, -2656)); // разобраться с указателем на другого моба. TODO[K] - пока ставим флаг на карте на 2го моба
                    } else if (GetMemoState == 7 && (st.ownItemCount(q_east_report) == 0 || st.ownItemCount(q_north_report) == 0))
                        htmltext = "master_entiens_q0064_02.htm";
                    else if (GetMemoState == 7 && st.ownItemCount(q_east_report) >= 1 && st.ownItemCount(q_north_report) >= 1) {
                        st.setCond(11);
                        st.setMemoState("proof_of_berserker", String.valueOf(8), true);
                        st.takeItems(q_east_report, -1);
                        st.takeItems(q_north_report, -1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "master_entiens_q0064_03.htm";
                    } else if (GetMemoState == 8)
                        htmltext = "master_entiens_q0064_04.htm";
                } else if (npcId == harlkil_0064) {
                    if (GetMemoState == 9)
                        htmltext = "harlkil_0064_q0064_01.htm";
                    else if (GetMemoState == 10)
                        htmltext = "harlkil_0064_q0064_03.htm";
                }
                break;
            case COMPLETED:
                if (npcId == master_orkurus)
                    htmltext = "master_orkurus_q0064_05.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("proof_of_berserker");
        int npcId = npc.getNpcId();
        if (npcId == dead_seeker) {
            if (GetMemoState == 7 && st.ownItemCount(q_east_report) == 0) {
                int i0 = Rnd.get(100);
                if (i0 < 20) {
                    st.giveItems(q_east_report, 1);
                    if (st.ownItemCount(q_north_report) > 0) {
                        st.setCond(10);
                        st.soundEffect(SOUND_MIDDLE);
                    } else
                        st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == marsh_stakato_drone) {
            if (GetMemoState == 7 && st.ownItemCount(q_north_report) == 0) {
                int i0 = Rnd.get(100);
                if (i0 < 20) {
                    st.giveItems(q_north_report, 1);
                    if (st.ownItemCount(q_east_report) > 0) {
                        st.setCond(10);
                        st.soundEffect(SOUND_MIDDLE);
                    } else
                        st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == breka_orc || npcId == breka_orc_archer || npcId == breka_orc_shaman || npcId == breka_orc_overlord || npcId == breka_orc_warrior) {
            if (GetMemoState == 2 && st.ownItemCount(q_breka_orc_head) < 20) {
                if (st.ownItemCount(q_breka_orc_head) >= 19) {
                    st.setCond(3);
                    st.giveItems(q_breka_orc_head, 1);
                    st.soundEffect(SOUND_MIDDLE);
                } else {
                    st.giveItems(q_breka_orc_head, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == road_scavenger) {
            if (GetMemoState == 4 && st.ownItemCount(q_msg_plate) == 0) {
                int i0 = Rnd.get(100);
                if (i0 < 20) {
                    st.setCond(6);
                    st.giveItems(q_msg_plate, 1);
                    st.soundEffect(SOUND_MIDDLE);
                }
            }
        } else if (npcId == emisarry_of_einhasad) {
            if (GetMemoState == 9) {
                int i0 = Rnd.get(100);
                if (i0 < 20) {
                    NpcInstance harlkil = st.addSpawn(harlkil_0064, st.getPlayer().getX() + 10, st.getPlayer().getY() + 10, st.getPlayer().getZ() + 10);
                    Functions.npcSay(harlkil, NpcString.S1_DID_YOU_COME_TO_HELP_ME, st.getPlayer().getName());
                    st.startQuestTimer("6401", 60000, harlkil);
                    st.soundEffect(SOUND_MIDDLE);
                }
            }
        }
        return null;
    }
}