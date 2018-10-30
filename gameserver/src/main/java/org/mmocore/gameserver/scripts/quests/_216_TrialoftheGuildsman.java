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
 * @version 1.0
 * @date 08/06/2016
 * @lastedit 08/06/2016
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _216_TrialoftheGuildsman extends Quest {
    //npc
    private static final int valkon = 30103;
    private static final int warehouse_keeper_norman = 30210;
    private static final int blacksmith_alltran = 30283;
    private static final int blacksmith_pinter = 30298;
    private static final int blacksmith_duning = 30688;
    //mobs
    private static final int ant = 20079;
    private static final int ant_captain = 20080;
    private static final int ant_overseer = 20081;
    private static final int granitic_golem = 20083;
    private static final int manadragora = 20154;
    private static final int mandragora_a = 20155;
    private static final int mandragora_b = 20156;
    private static final int silenos = 20168;
    private static final int strain = 20200;
    private static final int ghoul = 20201;
    private static final int dead_seeker = 20202;
    private static final int mandragora_sprout = 20223;
    private static final int breka_orc = 20267;
    private static final int breka_orc_archer = 20268;
    private static final int breka_orc_shaman = 20269;
    private static final int breka_orc_overlord = 20270;
    private static final int breka_orc_warrior = 20271;
    //questitem
    private static final int mark_of_guildsman = 3119;
    private static final int valkons_recommend = 3120;
    private static final int mandragora_berry = 3121;
    private static final int alltrans_instructions = 3122;
    private static final int alltrans_recommend1 = 3123;
    private static final int alltrans_recommend2 = 3124;
    private static final int normans_instructions = 3125;
    private static final int normans_receipt = 3126;
    private static final int dunings_instructions = 3127;
    private static final int dunings_key = 3128;
    private static final int normans_list = 3129;
    private static final int gray_bone_powder = 3130;
    private static final int granite_whetstone = 3131;
    private static final int red_pigment = 3132;
    private static final int braided_yarn = 3133;
    private static final int journeyman_gem = 3134;
    private static final int pinters_instructions = 3135;
    private static final int amber_bead = 3136;
    private static final int amber_lump = 3137;
    private static final int journeyman_deco_beads = 3138;
    private static final int journeyman_ring = 3139;
    //etcitem
    private static final int rp_journeyman_ring = 3024;
    private static final int q_dimension_diamond = 7562;
    private static final int rp_amber_bead = 3025;

    public _216_TrialoftheGuildsman() {
        super(false);
        addStartNpc(valkon);
        addTalkId(blacksmith_alltran, warehouse_keeper_norman, blacksmith_pinter, blacksmith_duning);
        addKillId(ant, ant_captain, ant_overseer, granitic_golem, manadragora, mandragora_a, mandragora_b, silenos, strain, ghoul, dead_seeker, mandragora_sprout, breka_orc, breka_orc_archer, breka_orc_shaman, breka_orc_overlord, breka_orc_warrior);
        addQuestItem(valkons_recommend, mandragora_berry, alltrans_instructions, alltrans_recommend1, alltrans_recommend2, normans_instructions, normans_receipt, dunings_instructions, dunings_key, normans_list, gray_bone_powder, granite_whetstone, red_pigment, braided_yarn, journeyman_gem, pinters_instructions, amber_bead, amber_lump, journeyman_deco_beads, journeyman_ring);
        addLevelCheck(35);
        addClassIdCheck(ClassId.scavenger, ClassId.artisan);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == valkon) {
            if (event.equalsIgnoreCase("quest_accept")) {
                if (st.ownItemCount(ADENA_ID) >= 2000) {
                    st.setCond(1);
                    st.setState(STARTED);
                    st.soundEffect(SOUND_ACCEPT);
                    if (st.ownItemCount(valkons_recommend) == 0) {
                        st.giveItems(valkons_recommend, 1);
                    }
                    st.takeItems(ADENA_ID, 2000);
                    if (!st.getPlayer().getPlayerVariables().getBoolean(PlayerVariables.DD1)) {
                        st.giveItems(q_dimension_diamond, 85);
                        st.getPlayer().getPlayerVariables().set(PlayerVariables.DD1, "1", -1);
                        htmltext = "valkon_q0216_06d.htm";
                    } else
                        htmltext = "valkon_q0216_06.htm";
                } else
                    htmltext = "valkon_q0216_05b.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=216&reply=1"))
                htmltext = "valkon_q0216_04.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=216&reply=2"))
                htmltext = "valkon_q0216_05.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=216&reply=3")) {
                if (st.ownItemCount(alltrans_instructions) >= 1 && st.ownItemCount(journeyman_ring) >= 7) {
                    st.addExpAndSp(1029478, 66768);
                    st.giveItems(ADENA_ID, 187606);
                    st.takeItems(journeyman_ring, -1);
                    st.takeItems(alltrans_instructions, -1);
                    st.takeItems(rp_journeyman_ring, -1);
                    st.giveItems(mark_of_guildsman, 1);
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                    htmltext = "valkon_q0216_09a.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=216&reply=4")) {
                if (st.ownItemCount(alltrans_instructions) >= 1 && st.ownItemCount(journeyman_ring) >= 7) {
                    st.addExpAndSp(514739, 33384);
                    st.giveItems(ADENA_ID, 93803);
                    st.takeItems(journeyman_ring, -1);
                    st.takeItems(alltrans_instructions, -1);
                    st.takeItems(rp_journeyman_ring, -1);
                    st.giveItems(mark_of_guildsman, 1);
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                    htmltext = "valkon_q0216_09b.htm";
                }
            }
        } else if (npcId == blacksmith_alltran) {
            if (event.equalsIgnoreCase("menu_select?ask=216&reply=1")) {
                if (st.ownItemCount(valkons_recommend) >= 1 && st.ownItemCount(mandragora_berry) >= 1) {
                    st.setCond(5);
                    st.takeItems(valkons_recommend, 1);
                    st.giveItems(alltrans_instructions, 1);
                    st.takeItems(mandragora_berry, 1);
                    st.giveItems(rp_journeyman_ring, 1);
                    st.giveItems(alltrans_recommend1, 1);
                    st.giveItems(alltrans_recommend2, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "blacksmith_alltran_q0216_03.htm";
                }
            }
        } else if (npcId == warehouse_keeper_norman) {
            if (event.equalsIgnoreCase("menu_select?ask=216&reply=1"))
                htmltext = "warehouse_keeper_norman_q0216_02.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=216&reply=2"))
                htmltext = "warehouse_keeper_norman_q0216_03.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=216&reply=3")) {
                if (st.ownItemCount(alltrans_recommend1) >= 1) {
                    st.takeItems(alltrans_recommend1, 1);
                    st.giveItems(normans_instructions, 1);
                    st.giveItems(normans_receipt, 1);
                    htmltext = "warehouse_keeper_norman_q0216_04.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=216&reply=4"))
                htmltext = "warehouse_keeper_norman_q0216_08.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=216&reply=5"))
                htmltext = "warehouse_keeper_norman_q0216_09.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=216&reply=6")) {
                if (st.ownItemCount(normans_instructions) >= 1) {
                    st.takeItems(dunings_key, -1);
                    st.takeItems(normans_instructions, 1);
                    st.giveItems(normans_list, 1);
                    htmltext = "warehouse_keeper_norman_q0216_10.htm";
                }
            }
        } else if (npcId == blacksmith_pinter) {
            if (event.equalsIgnoreCase("menu_select?ask=216&reply=1"))
                htmltext = "blacksmith_pinter_q0216_03.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=216&reply=2")) {
                if (st.getPlayer().getPlayerClassComponent().getClassId().getId() == 0x36) {
                    if (st.ownItemCount(alltrans_recommend2) >= 1) {
                        st.giveItems(pinters_instructions, 1);
                        st.takeItems(alltrans_recommend2, 1);
                        htmltext = "blacksmith_pinter_q0216_04.htm";
                    }
                } else if (st.ownItemCount(alltrans_recommend2) >= 1) {
                    st.takeItems(alltrans_recommend2, 1);
                    st.giveItems(rp_amber_bead, 1);
                    st.giveItems(pinters_instructions, 1);
                    htmltext = "blacksmith_pinter_q0216_05.htm";
                }
            }

        } else if (npcId == blacksmith_duning) {
            if (event.equalsIgnoreCase("menu_select?ask=216&reply=1")) {
                if (st.ownItemCount(normans_receipt) >= 1) {
                    st.takeItems(normans_receipt, 1);
                    st.giveItems(dunings_instructions, 1);
                    htmltext = "blacksmith_duning_q0216_02.htm";

                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == valkon) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "valkon_q0216_02.htm";
                            st.exitQuest(true);
                            break;
                        case CLASS_ID:
                            htmltext = "valkon_q0216_01.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "valkon_q0216_03.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == valkon) {
                    if (st.ownItemCount(valkons_recommend) == 1) {
                        st.setCond(3);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "valkon_q0216_07.htm";
                    } else if (st.ownItemCount(alltrans_instructions) == 1) {
                        if (st.ownItemCount(journeyman_ring) < 7)
                            htmltext = "valkon_q0216_08.htm";
                        else
                            htmltext = "valkon_q0216_09.htm";
                    }
                } else if (npcId == blacksmith_alltran) {
                    if (st.ownItemCount(valkons_recommend) == 1 && st.ownItemCount(mandragora_berry) == 0) {
                        st.setCond(2);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "blacksmith_alltran_q0216_01.htm";
                    } else if (st.ownItemCount(valkons_recommend) == 1 && st.ownItemCount(mandragora_berry) == 1)
                        htmltext = "blacksmith_alltran_q0216_02.htm";
                    else if (st.ownItemCount(alltrans_instructions) == 1) {
                        if (st.ownItemCount(journeyman_ring) < 7)
                            htmltext = "blacksmith_alltran_q0216_04.htm";
                        else
                            htmltext = "blacksmith_alltran_q0216_05.htm";
                    }
                } else if (npcId == warehouse_keeper_norman) {
                    if (st.ownItemCount(alltrans_instructions) == 1 && st.ownItemCount(alltrans_recommend1) == 1)
                        htmltext = "warehouse_keeper_norman_q0216_01.htm";
                    else if (st.ownItemCount(alltrans_instructions) >= 1 && st.ownItemCount(normans_instructions) >= 1 && st.ownItemCount(normans_receipt) >= 1)
                        htmltext = "warehouse_keeper_norman_q0216_05.htm";
                    else if (st.ownItemCount(alltrans_instructions) >= 1 && st.ownItemCount(normans_instructions) >= 1 && st.ownItemCount(dunings_instructions) >= 1)
                        htmltext = "warehouse_keeper_norman_q0216_06.htm";
                    else if (st.ownItemCount(alltrans_instructions) >= 1 && st.ownItemCount(normans_instructions) >= 1 && st.ownItemCount(dunings_key) >= 30)
                        htmltext = "warehouse_keeper_norman_q0216_07.htm";
                    else if (st.ownItemCount(alltrans_instructions) >= 1 && st.ownItemCount(normans_list) >= 1) {
                        if (st.ownItemCount(gray_bone_powder) >= 70 && st.ownItemCount(granite_whetstone) >= 70 && st.ownItemCount(red_pigment) >= 70 && st.ownItemCount(braided_yarn) >= 70) {
                            st.takeItems(normans_list, -1);
                            st.takeItems(gray_bone_powder, -1);
                            st.takeItems(granite_whetstone, -1);
                            st.takeItems(red_pigment, -1);
                            st.takeItems(braided_yarn, -1);
                            st.giveItems(journeyman_gem, 7);
                            htmltext = "warehouse_keeper_norman_q0216_12.htm";
                            if (st.ownItemCount(journeyman_deco_beads) >= 7) {
                                st.setCond(6);
                                st.soundEffect(SOUND_MIDDLE);
                            }
                        } else
                            htmltext = "warehouse_keeper_norman_q0216_11.htm";
                    } else if (st.ownItemCount(normans_instructions) == 0 && st.ownItemCount(normans_list) == 0 && st.ownItemCount(alltrans_instructions) == 1 && (st.ownItemCount(journeyman_gem) >= 1 || st.ownItemCount(journeyman_ring) >= 1))
                        htmltext = "warehouse_keeper_norman_q0216_13.htm";
                } else if (npcId == blacksmith_pinter) {
                    if (st.ownItemCount(alltrans_instructions) >= 1 && st.ownItemCount(alltrans_recommend2) >= 1) {
                        if (st.getPlayer().getLevel() < 35)
                            htmltext = "blacksmith_pinter_q0216_01.htm";
                        else
                            htmltext = "blacksmith_pinter_q0216_02.htm";
                    } else if (st.ownItemCount(alltrans_instructions) >= 1 && st.ownItemCount(pinters_instructions) >= 1) {
                        if (st.ownItemCount(amber_bead) < 70) {
                            htmltext = "blacksmith_pinter_q0216_06.htm";
                        }
                        st.takeItems(pinters_instructions, -1);
                        st.takeItems(amber_bead, -1);
                        st.takeItems(rp_amber_bead, -1);
                        st.takeItems(amber_lump, -1);
                        st.giveItems(journeyman_deco_beads, 1);
                        if (st.ownItemCount(journeyman_gem) >= 7) {
                            st.setCond(6);
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    } else if (st.ownItemCount(alltrans_instructions) == 1 && st.ownItemCount(pinters_instructions) == 0 && (st.ownItemCount(journeyman_deco_beads) >= 1 || st.ownItemCount(journeyman_ring) >= 1))
                        htmltext = "blacksmith_pinter_q0216_08.htm";
                } else if (npcId == blacksmith_duning) {
                    if (st.ownItemCount(alltrans_instructions) >= 1 && st.ownItemCount(normans_instructions) >= 1 && st.ownItemCount(normans_receipt) >= 1 && st.ownItemCount(dunings_instructions) == 0)
                        htmltext = "blacksmith_duning_q0216_01.htm";
                    else if (st.ownItemCount(alltrans_instructions) >= 1 && st.ownItemCount(normans_instructions) >= 1 && st.ownItemCount(dunings_instructions) >= 1 && st.ownItemCount(normans_receipt) == 0 && st.ownItemCount(dunings_key) < 30)
                        htmltext = "blacksmith_duning_q0216_03.htm";
                    else if (st.ownItemCount(alltrans_instructions) >= 1 && st.ownItemCount(normans_instructions) >= 1 && st.ownItemCount(dunings_key) >= 30 && st.ownItemCount(dunings_instructions) == 0)
                        htmltext = "blacksmith_duning_q0216_04.htm";
                    else if (st.ownItemCount(alltrans_instructions) >= 1 && st.ownItemCount(normans_instructions) == 0 && st.ownItemCount(dunings_instructions) == 0)
                        htmltext = "blacksmith_duning_q0216_05.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        MonsterInstance mob = (MonsterInstance) npc;
        int npcId = npc.getNpcId();
        if (npcId == ant || npcId == ant_captain || npcId == ant_overseer) {
            if (st.ownItemCount(alltrans_instructions) == 1 && st.ownItemCount(pinters_instructions) == 1 && st.ownItemCount(amber_bead) < 70) {
                int i0 = 0;
                if (Rnd.get(100) < 100 && st.getPlayer().getPlayerClassComponent().getClassId().getId() == 0x36 && mob.isSpoiled()) {
                    i0 = i0 + 5;
                }
                if (Rnd.get(100) < 50 && st.getPlayer().getPlayerClassComponent().getClassId().getId() == 0x38) {
                    st.giveItems(amber_lump, 1);
                    st.soundEffect(SOUND_MIDDLE);
                }
                if (Rnd.get(100) < 100 && st.ownItemCount(amber_bead) + i0 < 70) {
                    i0 = i0 + 5;
                }
                if (i0 >= 1) {
                    st.giveItems(amber_bead, i0);
                    if (st.ownItemCount(amber_bead) + i0 >= 70) {
                        st.soundEffect(SOUND_MIDDLE);
                    } else {
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        } else if (npcId == granitic_golem) {
            if (st.ownItemCount(alltrans_instructions) == 1 && st.ownItemCount(normans_list) == 1 && st.ownItemCount(granite_whetstone) < 70) {
                st.giveItems(granite_whetstone, 7);
                if (st.ownItemCount(granite_whetstone) >= 63) {
                    st.soundEffect(SOUND_MIDDLE);
                } else {
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == manadragora || npcId == mandragora_a || npcId == mandragora_b || npcId == mandragora_sprout) {
            if (st.ownItemCount(valkons_recommend) == 1 && st.ownItemCount(mandragora_berry) == 0) {
                if (Rnd.get(100) <= 100) {
                    st.setCond(4);
                    st.giveItems(mandragora_berry, 1);
                    st.soundEffect(SOUND_MIDDLE);
                }
            }
        } else if (npcId == silenos) {
            if (st.ownItemCount(alltrans_instructions) == 1 && st.ownItemCount(normans_list) == 1 && st.ownItemCount(braided_yarn) < 70) {
                st.giveItems(braided_yarn, 10);
                if (st.ownItemCount(braided_yarn) >= 60) {
                    st.soundEffect(SOUND_MIDDLE);
                } else {
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == strain || npcId == ghoul) {
            if (st.ownItemCount(alltrans_instructions) == 1 && st.ownItemCount(normans_list) == 1 && st.ownItemCount(gray_bone_powder) < 70) {
                st.giveItems(gray_bone_powder, 5);
                if (st.ownItemCount(gray_bone_powder) >= 65) {
                    st.soundEffect(SOUND_MIDDLE);
                } else {
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == dead_seeker) {
            if (st.ownItemCount(alltrans_instructions) == 1 && st.ownItemCount(normans_list) == 1 && st.ownItemCount(red_pigment) < 70) {
                st.giveItems(red_pigment, 5);
                if (st.ownItemCount(red_pigment) >= 63) {
                    st.soundEffect(SOUND_MIDDLE);
                } else {
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == breka_orc || npcId == breka_orc_archer || npcId == breka_orc_shaman || npcId == breka_orc_overlord || npcId == breka_orc_warrior) {
            if (st.ownItemCount(alltrans_instructions) == 1 && st.ownItemCount(normans_instructions) == 1 && st.ownItemCount(dunings_instructions) == 1 && st.ownItemCount(dunings_key) < 30) {
                if (st.ownItemCount(dunings_key) >= 29) {
                    st.giveItems(dunings_key, 1);
                    st.takeItems(dunings_instructions, 1);
                    st.soundEffect(SOUND_MIDDLE);
                } else {
                    st.giveItems(dunings_key, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        }
        return null;
    }
}
