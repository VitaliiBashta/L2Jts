package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.base.ClassId;
import org.mmocore.gameserver.model.instances.MonsterInstance;
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
 * @version 1.1
 * @date 04/10/2015
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _417_PathOfTheScavenger extends Quest {
    // npc
    private final static int collector_pipi = 30524;
    private final static int trader_mion = 30519;
    private final static int master_toma = 30556;
    private final static int warehouse_keeper_raut = 30316;
    private final static int torai = 30557;
    private final static int warehouse_chief_yaseni = 31958;
    private final static int zimenf_priest_of_earth = 30538;
    private final static int trader_chali = 30517;
    private final static int head_blacksmith_bronk = 30525;
    // mobs
    private final static int hunter_tarantula = 20403;
    private final static int honey_bear = 27058;
    private final static int plunder_tarantula = 20508;
    private final static int hunter_bear = 20777;
    // questitem
    private final static int ring_of_raven = 1642;
    private final static int pipis_letter = 1643;
    private final static int routs_tp_scroll = 1644;
    private final static int succubus_undies = 1645;
    private final static int mions_letter = 1646;
    private final static int bronks_ingot = 1647;
    private final static int chalis_axe = 1648;
    private final static int zimenfs_potion = 1649;
    private final static int bronks_pay = 1650;
    private final static int chalis_pay = 1651;
    private final static int zimenfs_pay = 1652;
    private final static int bear_pic = 1653;
    private final static int tarantula_pic = 1654;
    private final static int honey_jar = 1655;
    private final static int bead = 1656;
    private final static int bead_parcel = 1657;
    private final static int bead_parcel2 = 8543;

    public _417_PathOfTheScavenger() {
        super(false);
        addStartNpc(collector_pipi);
        addTalkId(trader_mion, master_toma, warehouse_keeper_raut, torai, warehouse_chief_yaseni, zimenf_priest_of_earth, trader_chali, head_blacksmith_bronk);
        addKillId(hunter_tarantula, plunder_tarantula, hunter_bear, honey_bear);
        addQuestItem(pipis_letter, routs_tp_scroll, succubus_undies, mions_letter, bronks_ingot, chalis_axe, zimenfs_potion, bronks_pay, chalis_pay, zimenfs_pay, bear_pic, tarantula_pic, honey_jar, bead, bead_parcel);
        addLevelCheck(18);
        addClassIdCheck(ClassId.dwarven_fighter);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoStateEx = st.getInt("path_to_scavenger_ex");
        int GetMemoState = st.getInt("path_to_scavenger");
        int npcId = npc.getNpcId();
        int talker_level = st.getPlayer().getLevel();
        int GetOneTimeQuestFlag = st.getPlayer().getPlayerVariables().getInt(PlayerVariables.profession_145);
        int talker_occupation = st.getPlayer().getPlayerClassComponent().getClassId().getId();
        int dwarven_fighter = 0x35;
        int scavenger = 0x36;
        if (npcId == collector_pipi) {
            if (event.equalsIgnoreCase("quest_accept")) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = "collector_pipi_q0417_02.htm";
                        st.exitQuest(true);
                        break;
                    case CLASS_ID:
                        if (talker_occupation != dwarven_fighter) {
                            if (talker_occupation == scavenger) {
                                htmltext = "collector_pipi_q0417_02a.htm";
                                st.exitQuest(true);
                            } else {
                                htmltext = "collector_pipi_q0417_08.htm";
                                st.exitQuest(true);
                            }
                        }
                        break;
                    default:
                        if (st.ownItemCount(ring_of_raven) == 1 && talker_occupation == dwarven_fighter)
                            htmltext = "collector_pipi_q0417_04.htm";
                        else {
                            st.setCond(1);
                            st.setMemoState("path_to_scavenger_ex", String.valueOf(0), true);
                            st.giveItems(pipis_letter, 1);
                            st.setState(STARTED);
                            st.soundEffect(SOUND_ACCEPT);
                            htmltext = "collector_pipi_q0417_05.htm";
                        }
                        break;
                }
            }
        } else if (npcId == trader_mion) {
            if (event.equalsIgnoreCase("reply_1") && st.ownItemCount(pipis_letter) > 0) {
                switch (Rnd.get(3)) {
                    case 0:
                        htmltext = "trader_mion_q0417_02.htm";
                        st.takeItems(pipis_letter, 1);
                        st.giveItems(zimenfs_potion, 1);
                        break;
                    case 1:
                        htmltext = "trader_mion_q0417_03.htm";
                        st.takeItems(pipis_letter, 1);
                        st.giveItems(chalis_axe, 1);
                        break;
                    case 2:
                        htmltext = "trader_mion_q0417_04.htm";
                        st.takeItems(pipis_letter, 1);
                        st.giveItems(bronks_ingot, 1);
                        break;
                }
            } else if (event.equalsIgnoreCase("reply_2"))
                htmltext = "trader_mion_q0417_06.htm";
            else if (event.equalsIgnoreCase("reply_3")) {
                st.setMemoState("path_to_scavenger_ex", String.valueOf(GetMemoStateEx + 1), true);
                htmltext = "trader_mion_q0417_07.htm";
            } else if (event.equalsIgnoreCase("reply_4")) {
                switch (Rnd.get(2)) {
                    case 0:
                        htmltext = "trader_mion_q0417_06.htm";
                        break;
                    case 1:
                        htmltext = "trader_mion_q0417_11.htm";
                        break;
                }
            } else if (event.equalsIgnoreCase("reply_5")) {
                if (GetMemoStateEx % 10 < 2) {
                    htmltext = "trader_mion_q0417_07.htm";
                    st.setMemoState("path_to_scavenger_ex", String.valueOf(GetMemoStateEx + 1), true);
                } else if (GetMemoStateEx % 10 == 2 && GetMemoState == 0)
                    htmltext = "trader_mion_q0417_07.htm";
                else if (GetMemoStateEx % 10 == 2 && GetMemoState == 1) {
                    htmltext = "trader_mion_q0417_09.htm";
                    st.setMemoState("path_to_scavenger_ex", String.valueOf(GetMemoStateEx + 1), true);
                } else if (GetMemoStateEx % 10 >= 3 && GetMemoState == 1) {
                    st.setCond(4);
                    st.giveItems(mions_letter, 1);
                    st.takeItems(chalis_axe, -1);
                    st.takeItems(zimenfs_potion, -1);
                    st.takeItems(bronks_ingot, -1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "trader_mion_q0417_10.htm";
                }
            } else if (event.equalsIgnoreCase("reply_6")) {
                switch (Rnd.get(3)) {
                    case 0:
                        htmltext = "trader_mion_q0417_02.htm";
                        st.takeItems(zimenfs_pay, -1);
                        st.takeItems(chalis_pay, -1);
                        st.takeItems(bronks_pay, -1);
                        st.giveItems(zimenfs_potion, 1);
                        break;
                    case 1:
                        htmltext = "trader_mion_q0417_03.htm";
                        st.takeItems(zimenfs_pay, -1);
                        st.takeItems(chalis_pay, -1);
                        st.takeItems(bronks_pay, -1);
                        st.giveItems(chalis_axe, 1);
                        break;
                    case 2:
                        htmltext = "trader_mion_q0417_04.htm";
                        st.takeItems(zimenfs_pay, -1);
                        st.takeItems(chalis_pay, -1);
                        st.takeItems(bronks_pay, -1);
                        st.giveItems(bronks_ingot, 1);
                        break;
                }
            }
        } else if (npcId == master_toma) {
            if (event.equalsIgnoreCase("reply_10") && st.ownItemCount(tarantula_pic) == 1 && st.ownItemCount(bead) >= 20) {
                st.setCond(9);
                st.giveItems(bead_parcel, 1);
                st.takeItems(tarantula_pic, -1);
                st.takeItems(bead, -1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "master_toma_q0417_05b.htm";
            } else if (event.equalsIgnoreCase("reply_11") && st.ownItemCount(tarantula_pic) == 1 && st.ownItemCount(bead) >= 20) {
                st.setCond(12);
                st.setMemoState("path_to_scavenger", String.valueOf(2), true);
                st.giveItems(bead_parcel2, 1);
                st.takeItems(tarantula_pic, -1);
                st.takeItems(bead, -1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "master_toma_q0417_06b.htm";
            }
        } else if (npcId == warehouse_keeper_raut) {
            if (event.equalsIgnoreCase("reply_1") && st.ownItemCount(bead_parcel) > 0) {
                st.setCond(10);
                st.takeItems(bead_parcel, 1);
                st.giveItems(routs_tp_scroll, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "raut_q0417_02.htm";
            } else if (event.equalsIgnoreCase("reply_2") && st.ownItemCount(bead_parcel) > 0) {
                st.setCond(10);
                st.takeItems(bead_parcel, 1);
                st.giveItems(routs_tp_scroll, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "raut_q0417_03.htm";
            }
        } else if (npcId == torai) {
            if (event.equalsIgnoreCase("reply_1"))
                htmltext = "torai_q0417_02.htm";
            else if (event.equalsIgnoreCase("reply_2") && st.ownItemCount(routs_tp_scroll) > 0) {
                st.setCond(11);
                st.takeItems(routs_tp_scroll, 1);
                st.giveItems(succubus_undies, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "torai_q0417_03.htm";
            }
        } else if (npcId == warehouse_chief_yaseni) {
            if (event.equalsIgnoreCase("reply_1") && st.ownItemCount(bead_parcel2) > 0 && GetMemoState == 2) {
                htmltext = "warehouse_chief_yaseni_q0417_02.htm";
                st.giveItems(ring_of_raven, 1);
                if (GetOneTimeQuestFlag == 0) {
                    st.getPlayer().getPlayerVariables().set(PlayerVariables.profession_145, String.valueOf(1), -1);
                    if (talker_level >= 20)
                        st.addExpAndSp(320534, 35412);
                    else if (talker_level == 19)
                        st.addExpAndSp(456128, 42110);
                    else
                        st.addExpAndSp(591724, 48808);
                    st.giveItems(ADENA_ID, 163800);
                }
                st.takeItems(bead_parcel2, -1);
                st.removeMemo("path_to_scavenger");
                st.removeMemo("path_to_scavenger_ex");
                st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(false);
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("path_to_scavenger");
        int GetMemoStateEx = st.getInt("path_to_scavenger_ex");
        int talker_level = st.getPlayer().getLevel();
        int GetOneTimeQuestFlag = st.getPlayer().getPlayerVariables().getInt(PlayerVariables.profession_145);
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == collector_pipi)
                    htmltext = "collector_pipi_q0417_01.htm";
                break;
            case STARTED:
                if (npcId == collector_pipi) {
                    if (st.ownItemCount(pipis_letter) == 1)
                        htmltext = "collector_pipi_q0417_06.htm";
                    else if (st.ownItemCount(pipis_letter) == 0)
                        htmltext = "collector_pipi_q0417_07.htm";
                } else if (npcId == trader_mion) {
                    if (st.ownItemCount(pipis_letter) == 1) {
                        st.setCond(2);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "trader_mion_q0417_01.htm";
                    } else if (st.ownItemCount(chalis_axe) + st.ownItemCount(bronks_ingot) + st.ownItemCount(zimenfs_potion) == 1 && GetMemoStateEx % 10 == 0)
                        htmltext = "trader_mion_q0417_05.htm";
                    else if (st.ownItemCount(chalis_axe) + st.ownItemCount(bronks_ingot) + st.ownItemCount(zimenfs_potion) == 1 && GetMemoStateEx % 10 > 0)
                        htmltext = "trader_mion_q0417_08.htm";
                    else if (st.ownItemCount(chalis_pay) + st.ownItemCount(bronks_pay) + st.ownItemCount(zimenfs_pay) == 1 && GetMemoStateEx < 50)
                        htmltext = "trader_mion_q0417_12.htm";
                    else if (st.ownItemCount(chalis_pay) + st.ownItemCount(bronks_pay) + st.ownItemCount(zimenfs_pay) == 1 && GetMemoStateEx >= 50) {
                        st.setCond(4);
                        st.giveItems(mions_letter, 1);
                        st.takeItems(chalis_pay, -1);
                        st.takeItems(zimenfs_pay, -1);
                        st.takeItems(bronks_pay, -1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "trader_mion_q0417_15.htm";
                    } else if (st.ownItemCount(mions_letter) > 0)
                        htmltext = "trader_mion_q0417_13.htm";
                    else if (st.ownItemCount(bear_pic) > 0 || st.ownItemCount(tarantula_pic) > 0 || st.ownItemCount(bead_parcel) > 0 || st.ownItemCount(routs_tp_scroll) > 0 || st.ownItemCount(succubus_undies) > 0)
                        htmltext = "trader_mion_q0417_14.htm";
                } else if (npcId == master_toma) {
                    if (st.ownItemCount(mions_letter) == 1) {
                        st.setCond(5);
                        st.takeItems(mions_letter, 1);
                        st.giveItems(bear_pic, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "master_toma_q0417_01.htm";
                    } else if (st.ownItemCount(bear_pic) == 1 && st.ownItemCount(honey_jar) < 5)
                        htmltext = "master_toma_q0417_02.htm";
                    else if (st.ownItemCount(bear_pic) == 1 && st.ownItemCount(honey_jar) >= 5) {
                        st.setCond(7);
                        st.takeItems(honey_jar, -1);
                        st.takeItems(bear_pic, 1);
                        st.giveItems(tarantula_pic, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "master_toma_q0417_03.htm";
                    } else if (st.ownItemCount(tarantula_pic) == 1 && st.ownItemCount(bead) < 20)
                        htmltext = "master_toma_q0417_04.htm";
                    else if (st.ownItemCount(tarantula_pic) == 1 && st.ownItemCount(bead) >= 20)
                        htmltext = "master_toma_q0417_05a.htm";
                    else if (st.ownItemCount(bead_parcel) > 0 && st.ownItemCount(bead_parcel2) == 0)
                        htmltext = "master_toma_q0417_06a.htm";
                    else if (st.ownItemCount(bead_parcel2) > 0 && st.ownItemCount(bead_parcel) == 0 && GetMemoState == 2)
                        htmltext = "master_toma_q0417_06c.htm";
                    else if (st.ownItemCount(routs_tp_scroll) > 0 || st.ownItemCount(succubus_undies) > 0)
                        htmltext = "master_toma_q0417_07.htm";
                } else if (npcId == warehouse_keeper_raut) {
                    if (st.ownItemCount(bead_parcel) == 1)
                        htmltext = "raut_q0417_01.htm";
                    else if (st.ownItemCount(routs_tp_scroll) == 1)
                        htmltext = "raut_q0417_04.htm";
                    else if (st.ownItemCount(succubus_undies) == 1) {
                        htmltext = "raut_q0417_05.htm";
                        st.takeItems(succubus_undies, 1);
                        st.giveItems(ring_of_raven, 1);
                        if (GetOneTimeQuestFlag == 0) {
                            st.getPlayer().getPlayerVariables().set(PlayerVariables.profession_145, String.valueOf(1), -1);
                            if (talker_level >= 20)
                                st.addExpAndSp(160267, 17706);
                            else if (talker_level == 19)
                                st.addExpAndSp(228064, 21055);
                            else
                                st.addExpAndSp(295862, 24404);
                            st.giveItems(ADENA_ID, 81900);
                        }
                        st.removeMemo("path_to_scavenger");
                        st.removeMemo("path_to_scavenger_ex");
                        st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                    }
                } else if (npcId == torai) {
                    if (st.ownItemCount(routs_tp_scroll) == 1)
                        htmltext = "torai_q0417_01.htm";
                } else if (npcId == warehouse_chief_yaseni) {
                    if (st.ownItemCount(bead_parcel2) > 0 && st.ownItemCount(bead_parcel) == 0 && GetMemoState == 2)
                        htmltext = "warehouse_chief_yaseni_q0417_01.htm";
                } else if (npcId == zimenf_priest_of_earth) {
                    if (st.ownItemCount(zimenfs_potion) == 1 && GetMemoStateEx < 20) {
                        st.setMemoState("path_to_scavenger_ex", String.valueOf(GetMemoStateEx + 10), true);
                        st.takeItems(zimenfs_potion, 1);
                        st.giveItems(zimenfs_pay, 1);
                        htmltext = "zimenf_priest_of_earth_q0417_01.htm";
                    } else if (st.ownItemCount(zimenfs_potion) == 1 && GetMemoStateEx >= 20) {
                        st.setCond(3);
                        st.setMemoState("path_to_scavenger", String.valueOf(1), true);
                        st.setMemoState("path_to_scavenger_ex", String.valueOf(GetMemoStateEx + 10), true);
                        st.takeItems(zimenfs_potion, 1);
                        st.giveItems(zimenfs_pay, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "zimenf_priest_of_earth_q0417_02.htm";
                    } else if (st.ownItemCount(zimenfs_pay) == 1)
                        htmltext = "zimenf_priest_of_earth_q0417_03.htm";
                } else if (npcId == trader_chali) {
                    if (st.ownItemCount(chalis_axe) == 1 && GetMemoStateEx < 20) {
                        st.setMemoState("path_to_scavenger_ex", String.valueOf(GetMemoStateEx + 10), true);
                        st.takeItems(chalis_axe, 1);
                        st.giveItems(chalis_pay, 1);
                        htmltext = "trader_chali_q0417_01.htm";
                    } else if (st.ownItemCount(chalis_axe) == 1 && GetMemoStateEx >= 20) {
                        st.setCond(3);
                        st.setMemoState("path_to_scavenger", String.valueOf(1), true);
                        st.setMemoState("path_to_scavenger_ex", String.valueOf(GetMemoStateEx + 10), true);
                        st.takeItems(chalis_axe, 1);
                        st.giveItems(chalis_pay, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "trader_chali_q0417_02.htm";
                    } else if (st.ownItemCount(chalis_pay) == 1)
                        htmltext = "trader_chali_q0417_03.htm";
                } else if (npcId == head_blacksmith_bronk) {
                    if (st.ownItemCount(bronks_ingot) == 1 && GetMemoStateEx < 20) {
                        st.setMemoState("path_to_scavenger_ex", String.valueOf(GetMemoStateEx + 10), true);
                        st.takeItems(bronks_ingot, 1);
                        st.giveItems(bronks_pay, 1);
                        htmltext = "head_blacksmith_bronk_q0417_01.htm";
                    } else if (st.ownItemCount(bronks_ingot) == 1 && GetMemoStateEx >= 20) {
                        st.setCond(3);
                        st.setMemoState("path_to_scavenger", String.valueOf(1), true);
                        st.setMemoState("path_to_scavenger_ex", String.valueOf(GetMemoStateEx + 10), true);
                        st.takeItems(bronks_ingot, 1);
                        st.giveItems(bronks_pay, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "head_blacksmith_bronk_q0417_02.htm";
                    } else if (st.ownItemCount(bronks_pay) == 1)
                        htmltext = "head_blacksmith_bronk_q0417_03.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        MonsterInstance mob = (MonsterInstance) npc;
        if (npcId == hunter_bear) {
            if (st.ownItemCount(bear_pic) == 1 && st.ownItemCount(honey_jar) < 5) {
                if (Rnd.get(100) <= 20)
                    st.addSpawn(honey_bear);
            }
        } else if (npcId == honey_bear) {
            if (st.ownItemCount(bear_pic) == 1 && st.ownItemCount(honey_jar) < 5) {
                if (mob.isSpoiled()) {
                    if (st.ownItemCount(honey_jar) == 4) {
                        st.setCond(6);
                        st.giveItems(honey_jar, 1);
                        st.soundEffect(SOUND_MIDDLE);
                    } else {
                        st.giveItems(honey_jar, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        } else if (npcId == hunter_tarantula || npcId == plunder_tarantula) {
            if (st.ownItemCount(tarantula_pic) == 1 && st.ownItemCount(bead) < 20) {
                if (mob.isSpoiled()) {
                    if (Rnd.get(2) < 2) {
                        if (st.ownItemCount(bead) == 19) {
                            st.setCond(8);
                            st.giveItems(bead, 1);
                            st.soundEffect(SOUND_MIDDLE);
                        } else {
                            st.giveItems(bead, 1);
                            st.soundEffect(SOUND_ITEMGET);
                        }
                    }
                }
            }
        }
        return null;
    }
}