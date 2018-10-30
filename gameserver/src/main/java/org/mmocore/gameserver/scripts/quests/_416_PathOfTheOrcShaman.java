package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.model.base.ClassId;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.serverpackets.SocialAction;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.3
 * @date 04/10/2015
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _416_PathOfTheOrcShaman extends Quest {
    // npc
    private final static int tataru_zu_hestui = 30585;
    private final static int hestui_totem_spirit = 30592;
    private final static int seer_umos = 30502;
    private final static int dudamara_totem_spirit = 30593;
    private final static int seer_moirase = 31979;
    private final static int totem_spirit_gandi = 32057;
    private final static int dead_leopard = 32090;
    // mobs
    private final static int kasha_bear = 20479;
    private final static int kasha_blade_spider = 20478;
    private final static int scarlet_salamander = 20415;
    private final static int grizzly_bear = 20335;
    private final static int poison_spider = 20038;
    private final static int bind_poison_spider = 20043;
    private final static int durka_spirit = 27056;
    private final static int q_black_leopard = 27319;
    // questitem
    private final static int fire_charm = 1616;
    private final static int kasha_bear_pelt = 1617;
    private final static int kasha_bspider_husk = 1618;
    private final static int fiery_egg1 = 1619;
    private final static int hestui_mask = 1620;
    private final static int fiery_egg2 = 1621;
    private final static int totem_spirit_claw = 1622;
    private final static int tatarus_letter = 1623;
    private final static int flame_charm = 1624;
    private final static int grizzly_blood = 1625;
    private final static int blood_cauldron = 1626;
    private final static int spirit_net = 1627;
    private final static int bound_durka_spirit = 1628;
    private final static int durka_parasite = 1629;
    private final static int totem_spirit_blood = 1630;
    private final static int mask_of_medium = 1631;

    public _416_PathOfTheOrcShaman() {
        super(false);
        addStartNpc(tataru_zu_hestui);
        addTalkId(hestui_totem_spirit, seer_umos, dudamara_totem_spirit, seer_moirase, totem_spirit_gandi, dead_leopard);
        addKillId(kasha_bear, kasha_blade_spider, scarlet_salamander, grizzly_bear, poison_spider, bind_poison_spider, durka_spirit, q_black_leopard);
        addQuestItem(fire_charm, kasha_bear_pelt, kasha_bspider_husk, fiery_egg1, hestui_mask, fiery_egg2, totem_spirit_claw, tatarus_letter, flame_charm, grizzly_blood, blood_cauldron, spirit_net, bound_durka_spirit, durka_parasite, totem_spirit_blood);
        addLevelCheck(18);
        addClassIdCheck(ClassId.orc_mage);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("path_to_orc_shaman");
        int npcId = npc.getNpcId();
        int talker_level = st.getPlayer().getLevel();
        int GetOneTimeQuestFlag = st.getPlayer().getPlayerVariables().getInt(PlayerVariables.profession_145);
        int talker_occupation = st.getPlayer().getPlayerClassComponent().getClassId().getId();
        int orc_mage = 0x31;
        int orc_shaman = 0x32;
        if (npcId == tataru_zu_hestui) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.giveItems(fire_charm, 1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "tataru_zu_hestui_q0416_06.htm";
            } else if (event.equalsIgnoreCase("reply_1")) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = "tataru_zu_hestui_q0416_03.htm";
                        st.exitQuest(true);
                        break;
                    case CLASS_ID:
                        if (talker_occupation != orc_mage) {
                            if (talker_occupation == orc_shaman) {
                                htmltext = "tataru_zu_hestui_q0416_02a.htm";
                                st.exitQuest(true);
                            } else {
                                htmltext = "tataru_zu_hestui_q0416_02.htm";
                                st.exitQuest(true);
                            }
                        }
                        break;
                    default:
                        if (st.ownItemCount(mask_of_medium) == 1 && talker_occupation == orc_mage)
                            htmltext = "tataru_zu_hestui_q0416_04.htm";
                        else
                            htmltext = "tataru_zu_hestui_q0416_05.htm";
                        break;
                }
            } else if (event.equalsIgnoreCase("reply_2") && st.ownItemCount(totem_spirit_claw) > 0)
                htmltext = "tataru_zu_hestui_q0416_11a.htm";
            else if (event.equalsIgnoreCase("reply_11") && st.ownItemCount(totem_spirit_claw) > 0) {
                st.setCond(5);
                st.takeItems(totem_spirit_claw, 1);
                st.giveItems(tatarus_letter, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "tataru_zu_hestui_q0416_11b.htm";
            } else if (event.equalsIgnoreCase("reply_12") && st.ownItemCount(totem_spirit_claw) > 0) {
                st.setCond(12);
                st.setMemoState("path_to_orc_shaman", String.valueOf(100), true);
                st.takeItems(totem_spirit_claw, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "tataru_zu_hestui_q0416_11c.htm";
            }
        } else if (npcId == hestui_totem_spirit) {
            if (event.equalsIgnoreCase("reply_1"))
                htmltext = "hestui_totem_spirit_q0416_02.htm";
            else if (event.equalsIgnoreCase("reply_2") && st.ownItemCount(hestui_mask) > 0 && st.ownItemCount(fiery_egg2) > 0) {
                st.setCond(4);
                st.takeItems(hestui_mask, 1);
                st.takeItems(fiery_egg2, 1);
                st.giveItems(totem_spirit_claw, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "hestui_totem_spirit_q0416_03.htm";
            }
        } else if (npcId == seer_umos) {
            if (event.equalsIgnoreCase("reply_2") && st.ownItemCount(totem_spirit_blood) > 0) {
                st.takeItems(totem_spirit_blood, -1);
                st.giveItems(mask_of_medium, 1);
                htmltext = "seer_umos_q0416_07.htm";
                if (GetOneTimeQuestFlag == 0) {
                    st.getPlayer().getPlayerVariables().set(PlayerVariables.profession_145, String.valueOf(1), -1);
                    if (talker_level >= 20)
                        st.addExpAndSp(320534, 22992);
                    else if (talker_level == 19)
                        st.addExpAndSp(456128, 29690);
                    else
                        st.addExpAndSp(591724, 36388);
                    st.giveItems(ADENA_ID, 163800);
                }
                st.removeMemo("path_to_orc_shaman");
                st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(false);
            }
        } else if (npcId == dudamara_totem_spirit) {
            if (event.equalsIgnoreCase("reply_1"))
                htmltext = "dudamara_totem_spirit_q0416_02.htm";
            else if (event.equalsIgnoreCase("reply_2") && st.ownItemCount(blood_cauldron) > 0) {
                st.setCond(9);
                st.takeItems(blood_cauldron, 1);
                st.giveItems(spirit_net, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "dudamara_totem_spirit_q0416_03.htm";
            }
        } else if (npcId == totem_spirit_gandi) {
            if (event.equalsIgnoreCase("reply_13") && GetMemoState == 101) {
                st.setCond(14);
                st.setMemoState("path_to_orc_shaman", String.valueOf(102), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "totem_spirit_gandi_q0416_02.htm";
            } else if (event.equalsIgnoreCase("reply_14") && GetMemoState == 109) {
                st.setCond(21);
                st.setMemoState("path_to_orc_shaman", String.valueOf(110), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "totem_spirit_gandi_q0416_05.htm";
            }
        } else if (npcId == dead_leopard) {
            if (event.equalsIgnoreCase("reply_1") && GetMemoState == 106) {
                st.setCond(18);
                st.setMemoState("path_to_orc_shaman", String.valueOf(107), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "dead_leopard_q0416_04.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("path_to_orc_shaman");
        int talker_level = st.getPlayer().getLevel();
        int GetOneTimeQuestFlag = st.getPlayer().getPlayerVariables().getInt(PlayerVariables.profession_145);
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == tataru_zu_hestui)
                    htmltext = "tataru_zu_hestui_q0416_01.htm";
                break;
            case STARTED:
                if (npcId == tataru_zu_hestui) {
                    if (st.ownItemCount(fire_charm) == 1 && st.ownItemCount(kasha_bear_pelt) + st.ownItemCount(kasha_bspider_husk) + st.ownItemCount(fiery_egg1) < 3)
                        htmltext = "tataru_zu_hestui_q0416_07.htm";
                    else if (st.ownItemCount(fire_charm) == 1 && st.ownItemCount(kasha_bear_pelt) + st.ownItemCount(kasha_bspider_husk) + st.ownItemCount(fiery_egg1) >= 3) {
                        st.setCond(3);
                        st.takeItems(fire_charm, -1);
                        st.takeItems(kasha_bear_pelt, -1);
                        st.takeItems(kasha_bspider_husk, -1);
                        st.takeItems(fiery_egg1, -1);
                        st.giveItems(hestui_mask, 1);
                        st.giveItems(fiery_egg2, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "tataru_zu_hestui_q0416_08.htm";
                    } else if (st.ownItemCount(hestui_mask) == 1 && st.ownItemCount(fiery_egg2) == 1)
                        htmltext = "tataru_zu_hestui_q0416_09.htm";
                    else if (st.ownItemCount(totem_spirit_claw) == 1)
                        htmltext = "tataru_zu_hestui_q0416_10.htm";
                    else if (st.ownItemCount(tatarus_letter) == 1)
                        htmltext = "tataru_zu_hestui_q0416_12.htm";
                    else if (st.ownItemCount(grizzly_blood) > 0 || st.ownItemCount(flame_charm) > 0 || st.ownItemCount(blood_cauldron) > 0 || st.ownItemCount(spirit_net) > 0 || st.ownItemCount(bound_durka_spirit) > 0 || st.ownItemCount(totem_spirit_blood) > 0)
                        htmltext = "tataru_zu_hestui_q0416_13.htm";
                    else if (GetMemoState == 100)
                        htmltext = "tataru_zu_hestui_q0416_11c.htm";
                } else if (npcId == hestui_totem_spirit) {
                    if (st.ownItemCount(hestui_mask) > 0 && st.ownItemCount(fiery_egg2) > 0)
                        htmltext = "hestui_totem_spirit_q0416_01.htm";
                    else if (st.ownItemCount(totem_spirit_claw) > 0)
                        htmltext = "hestui_totem_spirit_q0416_04.htm";
                    else if (st.ownItemCount(grizzly_blood) > 0 || st.ownItemCount(flame_charm) > 0 || st.ownItemCount(blood_cauldron) > 0 || st.ownItemCount(spirit_net) > 0 || st.ownItemCount(bound_durka_spirit) > 0 || st.ownItemCount(totem_spirit_blood) > 0 || st.ownItemCount(tatarus_letter) > 0)
                        htmltext = "hestui_totem_spirit_q0416_05.htm";
                } else if (npcId == seer_umos) {
                    if (st.ownItemCount(tatarus_letter) > 0) {
                        st.setCond(6);
                        st.takeItems(tatarus_letter, -1);
                        st.giveItems(flame_charm, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "seer_umos_q0416_01.htm";
                    } else if (st.ownItemCount(flame_charm) == 1 && st.ownItemCount(grizzly_blood) < 3)
                        htmltext = "seer_umos_q0416_02.htm";
                    else if (st.ownItemCount(flame_charm) == 1 && st.ownItemCount(grizzly_blood) >= 3) {
                        st.setCond(8);
                        st.takeItems(flame_charm, -1);
                        st.takeItems(grizzly_blood, -1);
                        st.giveItems(blood_cauldron, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "seer_umos_q0416_03.htm";
                    } else if (st.ownItemCount(blood_cauldron) == 1)
                        htmltext = "seer_umos_q0416_04.htm";
                    else if (st.ownItemCount(bound_durka_spirit) == 1 || st.ownItemCount(spirit_net) == 1)
                        htmltext = "seer_umos_q0416_05.htm";
                    else if (st.ownItemCount(totem_spirit_blood) == 1)
                        htmltext = "seer_umos_q0416_06.htm";
                } else if (npcId == dudamara_totem_spirit) {
                    if (st.ownItemCount(blood_cauldron) > 0)
                        htmltext = "dudamara_totem_spirit_q0416_01.htm";
                    else if (st.ownItemCount(spirit_net) > 0 && st.ownItemCount(bound_durka_spirit) == 0)
                        htmltext = "dudamara_totem_spirit_q0416_04.htm";
                    else if (st.ownItemCount(spirit_net) == 0 && st.ownItemCount(bound_durka_spirit) > 0) {
                        st.setCond(11);
                        st.takeItems(bound_durka_spirit, -1);
                        st.giveItems(totem_spirit_blood, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "dudamara_totem_spirit_q0416_05.htm";
                    } else if (st.ownItemCount(totem_spirit_blood) > 0)
                        htmltext = "dudamara_totem_spirit_q0416_06.htm";
                } else if (npcId == seer_moirase) {
                    if (GetMemoState == 100) {
                        st.setCond(13);
                        st.setMemoState("path_to_orc_shaman", String.valueOf(101), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "seer_moirase_q0416_01.htm";
                    } else if (GetMemoState >= 101 && GetMemoState < 108)
                        htmltext = "seer_moirase_q0416_02.htm";
                    else if (GetMemoState == 110) {
                        st.giveItems(mask_of_medium, 1);
                        htmltext = "seer_moirase_q0416_03.htm";
                        if (GetOneTimeQuestFlag == 0) {
                            st.getPlayer().getPlayerVariables().set(PlayerVariables.profession_145, String.valueOf(1), -1);
                            if (talker_level >= 20)
                                st.addExpAndSp(160267, 11496);
                            else if (talker_level == 19)
                                st.addExpAndSp(228064, 14845);
                            else
                                st.addExpAndSp(295862, 18194);
                            st.giveItems(ADENA_ID, 81900);
                        }
                        st.removeMemo("path_to_orc_shaman");
                        st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                    }
                } else if (npcId == totem_spirit_gandi) {
                    if (GetMemoState == 101)
                        htmltext = "totem_spirit_gandi_q0416_01.htm";
                    else if (GetMemoState == 102)
                        htmltext = "totem_spirit_gandi_q0416_03.htm";
                    else if (GetMemoState == 109)
                        htmltext = "totem_spirit_gandi_q0416_04.htm";
                } else if (npcId == dead_leopard) {
                    if (GetMemoState == 104) {
                        st.setCond(16);
                        st.setMemoState("path_to_orc_shaman", String.valueOf(105), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "dead_leopard_q0416_01.htm";
                    } else if (GetMemoState == 106)
                        htmltext = "dead_leopard_q0416_02.htm";
                    else if (GetMemoState == 107)
                        htmltext = "dead_leopard_q0416_05.htm";
                    else if (GetMemoState == 108) {
                        st.setCond(20);
                        st.setMemoState("path_to_orc_shaman", String.valueOf(109), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "dead_leopard_q0416_06.htm";
                    }
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("path_to_orc_shaman");
        int npcId = npc.getNpcId();
        if (npcId == kasha_bear) {
            if (st.ownItemCount(fire_charm) == 1 && st.ownItemCount(kasha_bear_pelt) < 1) {
                if (st.ownItemCount(kasha_bear_pelt) < 1 && st.ownItemCount(kasha_bspider_husk) >= 1 && st.ownItemCount(fiery_egg1) >= 1) {
                    st.giveItems(kasha_bear_pelt, 1);
                    st.setCond(2);
                    st.soundEffect(SOUND_MIDDLE);
                } else {
                    st.giveItems(kasha_bear_pelt, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == kasha_blade_spider) {
            if (st.ownItemCount(fire_charm) == 1 && st.ownItemCount(kasha_bspider_husk) < 1) {
                if (st.ownItemCount(kasha_bear_pelt) >= 1 && st.ownItemCount(kasha_bspider_husk) < 1 && st.ownItemCount(fiery_egg1) >= 1) {
                    st.giveItems(kasha_bspider_husk, 1);
                    st.setCond(2);
                    st.soundEffect(SOUND_MIDDLE);
                } else {
                    st.giveItems(kasha_bspider_husk, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == scarlet_salamander) {
            if (st.ownItemCount(fire_charm) == 1 && st.ownItemCount(fiery_egg1) < 1) {
                if (st.ownItemCount(kasha_bear_pelt) >= 1 && st.ownItemCount(kasha_bspider_husk) >= 1 && st.ownItemCount(fiery_egg1) < 1) {
                    st.giveItems(fiery_egg1, 1);
                    st.setCond(2);
                    st.soundEffect(SOUND_MIDDLE);
                } else {
                    st.giveItems(fiery_egg1, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == grizzly_bear) {
            if (st.ownItemCount(flame_charm) == 1 && st.ownItemCount(grizzly_blood) < 3) {
                st.giveItems(grizzly_blood, 1);
                if (st.ownItemCount(grizzly_blood) >= 3) {
                    st.setCond(7);
                    st.soundEffect(SOUND_MIDDLE);
                }
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == poison_spider) {
            if (st.ownItemCount(spirit_net) == 1 && st.ownItemCount(bound_durka_spirit) == 0 && st.ownItemCount(durka_parasite) <= 8) {
                int i0 = Rnd.get(10);
                if (st.ownItemCount(durka_parasite) == 5 && i0 < 1) {
                    st.takeItems(durka_parasite, -1);
                    st.addSpawn(durka_spirit);
                    st.soundEffect(SOUND_BEFORE_BATTLE);
                } else if (st.ownItemCount(durka_parasite) == 6 && i0 < 2) {
                    st.takeItems(durka_parasite, -1);
                    st.soundEffect(SOUND_BEFORE_BATTLE);
                    st.addSpawn(durka_spirit);
                } else if (st.ownItemCount(durka_parasite) == 7 && i0 < 2) {
                    st.takeItems(durka_parasite, -1);
                    st.soundEffect(SOUND_BEFORE_BATTLE);
                    st.addSpawn(durka_spirit);
                } else if (st.ownItemCount(durka_parasite) >= 8) {
                    st.takeItems(durka_parasite, -1);
                    st.soundEffect(SOUND_BEFORE_BATTLE);
                    st.addSpawn(durka_spirit);
                } else {
                    st.giveItems(durka_parasite, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == durka_spirit) {
            if (st.ownItemCount(spirit_net) == 1 && st.ownItemCount(bound_durka_spirit) == 0) {
                st.giveItems(bound_durka_spirit, 1);
                st.takeItems(spirit_net, 1);
                st.takeItems(durka_parasite, -1);
                st.setCond(10);
                st.soundEffect(SOUND_MIDDLE);
            }
        } else if (npcId == q_black_leopard) {
            if (GetMemoState >= 102 && GetMemoState <= 107) {
                if (GetMemoState == 102)
                    st.setMemoState("path_to_orc_shaman", String.valueOf(103), true);
                else if (GetMemoState == 103) {
                    st.setCond(15);
                    st.setMemoState("path_to_orc_shaman", String.valueOf(104), true);
                    st.soundEffect(SOUND_MIDDLE);
                    if (Rnd.get(100) < 66)
                        Functions.npcSay(npc, NpcString.MY_DEAR_FRIEND_OF_S1_WHO_HAS_GONE_ON_AHEAD_OF_ME, st.getPlayer().getName());
                } else if (GetMemoState == 105) {
                    st.setCond(17);
                    st.setMemoState("path_to_orc_shaman", String.valueOf(106), true);
                    st.soundEffect(SOUND_MIDDLE);
                    if (Rnd.get(100) < 66)
                        Functions.npcSay(npc, NpcString.LISTEN_TO_TEJAKAR_GANDI_YOUNG_OROKA_THE_SPIRIT_OF_THE_SLAIN_LEOPARD_IS_CALLING_YOU_S1, st.getPlayer().getName());
                } else if (GetMemoState == 107) {
                    st.setCond(19);
                    st.setMemoState("path_to_orc_shaman", String.valueOf(108), true);
                    st.soundEffect(SOUND_MIDDLE);
                }
            }
        }
        return null;
    }
}