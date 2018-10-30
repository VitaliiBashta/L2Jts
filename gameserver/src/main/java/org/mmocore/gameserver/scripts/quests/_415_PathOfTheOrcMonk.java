package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.base.ClassId;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.serverpackets.SocialAction;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.templates.item.WeaponTemplate;
import org.mmocore.gameserver.templates.item.WeaponTemplate.WeaponType;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.3
 * @date 05/11/2015
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _415_PathOfTheOrcMonk extends Quest {
    // npc
    private final static int gantaki_zu_urutu = 30587;
    private final static int khavatari_rosheek = 30590;
    private final static int prefect_kasman = 30501;
    private final static int khavatari_toruku = 30591;
    private final static int kavatari_aren = 32056;
    private final static int seer_moirase = 31979;
    // mobs
    private final static int felim_lizardman_warrior = 20014;
    private final static int vuku_orc_fighter = 20017;
    private final static int langk_lizardman_warrior = 20024;
    private final static int ratman_warrior = 20359;
    private final static int scarlet_salamander = 20415;
    private final static int kasha_poker_spider = 20476;
    private final static int kasha_blade_spider = 20478;
    private final static int kasha_bear = 20479;
    private final static int baar_dre_vanul = 21118;
    // questitem
    private final static int pomegranate = 1593;
    private final static int leather_pouch1 = 1594;
    private final static int leather_pouch2 = 1595;
    private final static int leather_pouch3 = 1596;
    private final static int leather_pouch1full = 1597;
    private final static int leather_pouch2full = 1598;
    private final static int leather_pouch3full = 1599;
    private final static int kasha_bear_claw = 1600;
    private final static int kasha_bspider_talon = 1601;
    private final static int s_salamander_scale = 1602;
    private final static int scroll_fiery_spirit = 1603;
    private final static int rosheeks_letter = 1604;
    private final static int gantakis_letter = 1605;
    private final static int fig = 1606;
    private final static int leather_purse4 = 1607;
    private final static int leather_pouch4full = 1608;
    private final static int vuku_tusk = 1609;
    private final static int ratman_fang = 1610;
    private final static int langk_tooth = 1611;
    private final static int felim_tooth = 1612;
    private final static int scroll_iron_will = 1613;
    private final static int torukus_letter = 1614;
    private final static int khavatari_totem = 1615;
    private final static int q_spider_tooth = 8545;
    private final static int q_horn_need_kavatari = 8546;

    public _415_PathOfTheOrcMonk() {
        super(false);
        addStartNpc(gantaki_zu_urutu);
        addTalkId(khavatari_rosheek, prefect_kasman, khavatari_toruku, kavatari_aren, seer_moirase);
        addKillId(felim_lizardman_warrior, vuku_orc_fighter, langk_lizardman_warrior, ratman_warrior, scarlet_salamander, kasha_blade_spider, kasha_bear, kasha_poker_spider, baar_dre_vanul);
        addQuestItem(pomegranate, leather_pouch1, leather_pouch2, leather_pouch3, leather_pouch1full, leather_pouch2full, leather_pouch3full, kasha_bear_claw, kasha_bspider_talon, s_salamander_scale, scroll_fiery_spirit, rosheeks_letter, gantakis_letter, fig, leather_purse4, leather_pouch4full, vuku_tusk, ratman_fang, langk_tooth, felim_tooth, scroll_iron_will, torukus_letter, q_spider_tooth, q_horn_need_kavatari);
        addLevelCheck(18);
        addClassIdCheck(ClassId.orc_fighter);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("path_to_orc_monk");
        int npcId = npc.getNpcId();
        int talker_level = st.getPlayer().getLevel();
        int GetOneTimeQuestFlag = st.getPlayer().getPlayerVariables().getInt(PlayerVariables.profession_145);
        int talker_occupation = st.getPlayer().getPlayerClassComponent().getClassId().getId();
        int orc_fighter = 0x2c;
        int orc_monk = 0x2f;
        if (npcId == gantaki_zu_urutu) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                st.giveItems(pomegranate, 1);
                htmltext = "gantaki_zu_urutu_q0415_06.htm";
            } else if (event.equalsIgnoreCase("reply_1")) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = "gantaki_zu_urutu_q0415_03.htm";
                        st.exitQuest(true);
                        break;
                    case CLASS_ID:
                        if (talker_occupation != orc_fighter) {
                            if (talker_occupation == orc_monk) {
                                htmltext = "gantaki_zu_urutu_q0415_02a.htm";
                                st.exitQuest(true);
                            } else {
                                htmltext = "gantaki_zu_urutu_q0415_02.htm";
                                st.exitQuest(true);
                            }
                        }
                        break;
                    default:
                        if (st.ownItemCount(khavatari_totem) == 1 && talker_occupation == orc_fighter)
                            htmltext = "gantaki_zu_urutu_q0415_04.htm";
                        else
                            htmltext = "gantaki_zu_urutu_q0415_05.htm";
                        break;
                }
            } else if (event.equalsIgnoreCase("reply_2") && st.ownItemCount(scroll_fiery_spirit) == 1 && st.ownItemCount(rosheeks_letter) == 1) {
                st.setCond(9);
                st.takeItems(rosheeks_letter, 1);
                st.giveItems(gantakis_letter, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "gantaki_zu_urutu_q0415_09b.htm";
            } else if (event.equalsIgnoreCase("reply_3") && st.ownItemCount(scroll_fiery_spirit) == 1 && st.ownItemCount(rosheeks_letter) == 1) {
                st.setCond(14);
                st.setMemoState("path_to_orc_monk", String.valueOf(2), true);
                st.takeItems(rosheeks_letter, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "gantaki_zu_urutu_q0415_09c.htm";
            }
        } else if (npcId == kavatari_aren) {
            if (event.equalsIgnoreCase("reply_1") && GetMemoState == 2)
                htmltext = "kavatari_aren_q0415_02.htm";
            else if (event.equalsIgnoreCase("reply_2") && GetMemoState == 2) {
                st.setCond(15);
                st.setMemoState("path_to_orc_monk", String.valueOf(3), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "kavatari_aren_q0415_03.htm";
            } else if (event.equalsIgnoreCase("reply_3") && GetMemoState == 4 && st.ownItemCount(q_horn_need_kavatari) >= 1) {
                st.setCond(19);
                st.setMemoState("path_to_orc_monk", String.valueOf(5), true);
                st.takeItems(q_horn_need_kavatari, -1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "kavatari_aren_q0415_08.htm";
            }
        } else if (npcId == seer_moirase) {
            if (event.equalsIgnoreCase("reply_1") && GetMemoState == 5)
                htmltext = "seer_moirase_q0415_02.htm";
            else if (event.equalsIgnoreCase("reply_2") && GetMemoState == 5) {
                st.giveItems(khavatari_totem, 1);
                st.takeItems(scroll_fiery_spirit, -1);
                if (GetOneTimeQuestFlag == 0) {
                    st.getPlayer().getPlayerVariables().set(PlayerVariables.profession_145, String.valueOf(1), -1);
                    if (talker_level >= 20)
                        st.addExpAndSp(160267, 12646);
                    else if (talker_level == 19)
                        st.addExpAndSp(228064, 15995);
                    else
                        st.addExpAndSp(295862, 19344);
                    st.giveItems(ADENA_ID, 81900);
                }
                htmltext = "seer_moirase_q0415_03.htm";
                st.removeMemo("path_to_orc_monk");
                st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(false);
            } else if (event.equalsIgnoreCase("reply_3") && GetMemoState == 5) {
                st.setCond(20);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "seer_moirase_q0415_04.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("path_to_orc_monk");
        int talker_level = st.getPlayer().getLevel();
        int GetOneTimeQuestFlag = st.getPlayer().getPlayerVariables().getInt(PlayerVariables.profession_145);
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == gantaki_zu_urutu)
                    htmltext = "gantaki_zu_urutu_q0415_01.htm";
                break;
            case STARTED:
                if (npcId == gantaki_zu_urutu) {
                    if (GetMemoState == 2)
                        htmltext = "gantaki_zu_urutu_q0415_09c.htm";
                    else if (st.ownItemCount(scroll_fiery_spirit) == 0 && st.ownItemCount(pomegranate) == 1 && st.ownItemCount(gantakis_letter) == 0 && st.ownItemCount(rosheeks_letter) == 0 && st.ownItemCount(leather_pouch1) + st.ownItemCount(leather_pouch2) + st.ownItemCount(leather_pouch3) + st.ownItemCount(leather_pouch1full) + st.ownItemCount(leather_pouch2full) + st.ownItemCount(leather_pouch3full) == 0)
                        htmltext = "gantaki_zu_urutu_q0415_07.htm";
                    else if (st.ownItemCount(scroll_fiery_spirit) == 0 && st.ownItemCount(pomegranate) == 1 && st.ownItemCount(gantakis_letter) == 0 && st.ownItemCount(rosheeks_letter) == 0 && st.ownItemCount(leather_pouch1) + st.ownItemCount(leather_pouch2) + st.ownItemCount(leather_pouch3) + st.ownItemCount(leather_pouch1full) + st.ownItemCount(leather_pouch2full) + st.ownItemCount(leather_pouch3full) == 1)
                        htmltext = "gantaki_zu_urutu_q0415_08.htm";
                    else if (st.ownItemCount(scroll_fiery_spirit) == 1 && st.ownItemCount(pomegranate) == 0 && st.ownItemCount(gantakis_letter) == 0 && st.ownItemCount(rosheeks_letter) == 1 && st.ownItemCount(leather_pouch1) + st.ownItemCount(leather_pouch2) + st.ownItemCount(leather_pouch3) + st.ownItemCount(leather_pouch1full) + st.ownItemCount(leather_pouch2full) + st.ownItemCount(leather_pouch3full) == 0)
                        htmltext = "gantaki_zu_urutu_q0415_09a.htm";
                    else if (GetMemoState < 2 && st.ownItemCount(scroll_fiery_spirit) == 1 && st.ownItemCount(pomegranate) == 0 && st.ownItemCount(gantakis_letter) == 1 && st.ownItemCount(rosheeks_letter) == 0 && st.ownItemCount(leather_pouch1) + st.ownItemCount(leather_pouch2) + st.ownItemCount(leather_pouch3) + st.ownItemCount(leather_pouch1full) + st.ownItemCount(leather_pouch2full) + st.ownItemCount(leather_pouch3full) == 0)
                        htmltext = "gantaki_zu_urutu_q0415_10.htm";
                    else if (GetMemoState < 2 && st.ownItemCount(scroll_fiery_spirit) == 1 && st.ownItemCount(pomegranate) == 0 && st.ownItemCount(gantakis_letter) == 0 && st.ownItemCount(rosheeks_letter) == 0 && st.ownItemCount(leather_pouch1) + st.ownItemCount(leather_pouch2) + st.ownItemCount(leather_pouch3) + st.ownItemCount(leather_pouch1full) + st.ownItemCount(leather_pouch2full) + st.ownItemCount(leather_pouch3full) == 0)
                        htmltext = "gantaki_zu_urutu_q0415_11.htm";
                } else if (npcId == khavatari_rosheek) {
                    if (st.ownItemCount(pomegranate) > 0) {
                        st.setCond(2);
                        st.takeItems(pomegranate, 1);
                        st.giveItems(leather_pouch1, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "khavatari_rosheek_q0415_01.htm";
                    } else if (st.ownItemCount(leather_pouch1) > 0 && st.ownItemCount(leather_pouch1full) == 0)
                        htmltext = "khavatari_rosheek_q0415_02.htm";
                    else if (st.ownItemCount(leather_pouch1) == 0 && st.ownItemCount(leather_pouch1full) > 0) {
                        st.setCond(4);
                        st.takeItems(leather_pouch1full, 1);
                        st.giveItems(leather_pouch2, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "khavatari_rosheek_q0415_03.htm";
                    } else if (st.ownItemCount(leather_pouch2) == 1 && st.ownItemCount(leather_pouch2full) == 0)
                        htmltext = "khavatari_rosheek_q0415_04.htm";
                    else if (st.ownItemCount(leather_pouch2) == 0 && st.ownItemCount(leather_pouch2full) == 1) {
                        st.setCond(6);
                        st.takeItems(leather_pouch2full, 1);
                        st.giveItems(leather_pouch3, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "khavatari_rosheek_q0415_05.htm";
                    } else if (st.ownItemCount(leather_pouch3) == 1 && st.ownItemCount(leather_pouch3full) == 0)
                        htmltext = "khavatari_rosheek_q0415_06.htm";
                    else if (st.ownItemCount(leather_pouch3) == 0 && st.ownItemCount(leather_pouch3full) == 1) {
                        st.setCond(8);
                        st.takeItems(leather_pouch3full, 1);
                        st.giveItems(scroll_fiery_spirit, 1);
                        st.giveItems(rosheeks_letter, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "khavatari_rosheek_q0415_07.htm";
                    } else if (st.ownItemCount(rosheeks_letter) == 1 && st.ownItemCount(scroll_fiery_spirit) == 1)
                        htmltext = "khavatari_rosheek_q0415_08.htm";
                    else if (st.ownItemCount(rosheeks_letter) == 0 && st.ownItemCount(scroll_fiery_spirit) == 1)
                        htmltext = "khavatari_rosheek_q0415_09.htm";
                } else if (npcId == prefect_kasman) {
                    if (st.ownItemCount(gantakis_letter) > 0) {
                        st.setCond(10);
                        st.takeItems(gantakis_letter, 1);
                        st.giveItems(fig, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "prefect_kasman_q0415_01.htm";
                    } else if (st.ownItemCount(fig) > 0 && (st.ownItemCount(leather_purse4) == 0 || st.ownItemCount(leather_pouch4full) == 0))
                        htmltext = "prefect_kasman_q0415_02.htm";
                    else if (st.ownItemCount(fig) == 0 && (st.ownItemCount(leather_purse4) == 1 || st.ownItemCount(leather_pouch4full) == 1))
                        htmltext = "prefect_kasman_q0415_03.htm";
                    else if (st.ownItemCount(scroll_iron_will) > 0) {
                        st.takeItems(scroll_iron_will, 1);
                        st.takeItems(scroll_fiery_spirit, -1);
                        st.takeItems(torukus_letter, -1);
                        st.giveItems(khavatari_totem, 1);
                        htmltext = "prefect_kasman_q0415_04.htm";
                        if (GetOneTimeQuestFlag == 0) {
                            st.getPlayer().getPlayerVariables().set(PlayerVariables.profession_145, String.valueOf(1), -1);
                            if (talker_level >= 20)
                                st.addExpAndSp(320534, 25292);
                            else if (talker_level == 19)
                                st.addExpAndSp(456128, 31990);
                            else
                                st.addExpAndSp(591724, 38688);
                            st.giveItems(ADENA_ID, 163800);
                        }
                        st.removeMemo("path_to_orc_monk");
                        st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                    }
                } else if (npcId == khavatari_toruku) {
                    if (st.ownItemCount(fig) > 0) {
                        st.setCond(11);
                        st.takeItems(fig, 1);
                        st.giveItems(leather_purse4, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "khavatari_toruku_q0415_01.htm";
                    } else if (st.ownItemCount(leather_purse4) > 0 && st.ownItemCount(leather_pouch4full) == 0)
                        htmltext = "khavatari_toruku_q0415_02.htm";
                    else if (st.ownItemCount(leather_purse4) == 0 && st.ownItemCount(leather_pouch4full) == 1) {
                        st.setCond(13);
                        st.takeItems(leather_pouch4full, 1);
                        st.giveItems(scroll_iron_will, 1);
                        st.giveItems(torukus_letter, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "khavatari_toruku_q0415_03.htm";
                    } else if (st.ownItemCount(scroll_iron_will) == 1 && st.ownItemCount(torukus_letter) == 1)
                        htmltext = "khavatari_toruku_q0415_04.htm";
                } else if (npcId == kavatari_aren) {
                    if (GetMemoState == 2)
                        htmltext = "kavatari_aren_q0415_01.htm";
                    else if (GetMemoState == 3 && st.ownItemCount(q_spider_tooth) < 6)
                        htmltext = "kavatari_aren_q0415_04.htm";
                    else if (GetMemoState == 3 && st.ownItemCount(q_spider_tooth) >= 6) {
                        st.setCond(17);
                        st.setMemoState("path_to_orc_monk", String.valueOf(4), true);
                        st.takeItems(q_spider_tooth, -1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "kavatari_aren_q0415_05.htm";
                    } else if (GetMemoState == 4 && st.ownItemCount(q_horn_need_kavatari) < 1)
                        htmltext = "kavatari_aren_q0415_06.htm";
                    else if (GetMemoState == 4 && st.ownItemCount(q_horn_need_kavatari) >= 1)
                        htmltext = "kavatari_aren_q0415_07.htm";
                    else if (GetMemoState == 5)
                        htmltext = "kavatari_aren_q0415_09.htm";
                } else if (npcId == seer_moirase) {
                    if (GetMemoState == 5)
                        htmltext = "seer_moirase_q0415_01.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        WeaponTemplate target_attack_type = st.getPlayer().getActiveWeaponItem();
        WeaponType AT_FIST = WeaponType.DUALFIST;
        WeaponType AT_DUALFIST = WeaponType.FIST;
        int npcId = npc.getNpcId();
        if (npcId == felim_lizardman_warrior) {
            if (st.ownItemCount(leather_purse4) == 1 && st.ownItemCount(felim_tooth) < 3) {
                if (target_attack_type.getItemType() == AT_FIST || target_attack_type.getItemType() == AT_DUALFIST) {
                    if (st.ownItemCount(ratman_fang) + st.ownItemCount(langk_tooth) + st.ownItemCount(felim_tooth) + st.ownItemCount(vuku_tusk) >= 11) {
                        st.setCond(12);
                        st.takeItems(vuku_tusk, -1);
                        st.takeItems(ratman_fang, -1);
                        st.takeItems(langk_tooth, -1);
                        st.takeItems(felim_tooth, -1);
                        st.takeItems(leather_purse4, 1);
                        st.giveItems(leather_pouch4full, 1);
                        st.soundEffect(SOUND_MIDDLE);
                    } else {
                        st.giveItems(felim_tooth, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        } else if (npcId == vuku_orc_fighter) {
            if (st.ownItemCount(leather_purse4) == 1 && st.ownItemCount(vuku_tusk) < 3) {
                if (target_attack_type.getItemType() == AT_FIST || target_attack_type.getItemType() == AT_DUALFIST) {
                    if (st.ownItemCount(ratman_fang) + st.ownItemCount(langk_tooth) + st.ownItemCount(felim_tooth) + st.ownItemCount(vuku_tusk) >= 11) {
                        st.setCond(12);
                        st.takeItems(vuku_tusk, -1);
                        st.takeItems(ratman_fang, -1);
                        st.takeItems(langk_tooth, -1);
                        st.takeItems(felim_tooth, -1);
                        st.takeItems(leather_purse4, 1);
                        st.giveItems(leather_pouch4full, 1);
                        st.soundEffect(SOUND_MIDDLE);
                    } else {
                        st.giveItems(vuku_tusk, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        } else if (npcId == langk_lizardman_warrior) {
            if (st.ownItemCount(leather_purse4) == 1 && st.ownItemCount(langk_tooth) < 3) {
                if (target_attack_type.getItemType() == AT_FIST || target_attack_type.getItemType() == AT_DUALFIST) {
                    if (st.ownItemCount(ratman_fang) + st.ownItemCount(langk_tooth) + st.ownItemCount(felim_tooth) + st.ownItemCount(vuku_tusk) >= 11) {
                        st.setCond(12);
                        st.takeItems(vuku_tusk, -1);
                        st.takeItems(ratman_fang, -1);
                        st.takeItems(langk_tooth, -1);
                        st.takeItems(felim_tooth, -1);
                        st.takeItems(leather_purse4, 1);
                        st.giveItems(leather_pouch4full, 1);
                        st.soundEffect(SOUND_MIDDLE);
                    } else {
                        st.giveItems(langk_tooth, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        } else if (npcId == ratman_warrior) {
            if (st.ownItemCount(leather_purse4) == 1 && st.ownItemCount(ratman_fang) < 3) {
                if (st.ownItemCount(ratman_fang) + st.ownItemCount(langk_tooth) + st.ownItemCount(felim_tooth) + st.ownItemCount(vuku_tusk) >= 11) {
                    st.setCond(12);
                    st.takeItems(vuku_tusk, -1);
                    st.takeItems(ratman_fang, -1);
                    st.takeItems(langk_tooth, -1);
                    st.takeItems(felim_tooth, -1);
                    st.takeItems(leather_purse4, 1);
                    st.giveItems(leather_pouch4full, 1);
                    st.soundEffect(SOUND_MIDDLE);
                } else {
                    st.giveItems(ratman_fang, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == scarlet_salamander) {
            if (st.ownItemCount(leather_pouch3) == 1) {
                if (target_attack_type.getItemType() == AT_FIST || target_attack_type.getItemType() == AT_DUALFIST) {
                    if (st.ownItemCount(s_salamander_scale) == 4) {
                        st.setCond(7);
                        st.takeItems(s_salamander_scale, -1);
                        st.takeItems(leather_pouch3, -1);
                        st.giveItems(leather_pouch3full, 1);
                        st.soundEffect(SOUND_MIDDLE);
                    } else {
                        st.giveItems(s_salamander_scale, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        } else if (npcId == kasha_blade_spider) {
            if (st.ownItemCount(leather_pouch2) == 1) {
                if (target_attack_type.getItemType() == AT_FIST || target_attack_type.getItemType() == AT_DUALFIST) {
                    if (st.ownItemCount(kasha_bspider_talon) == 4) {
                        st.setCond(5);
                        st.takeItems(kasha_bspider_talon, -1);
                        st.takeItems(leather_pouch2, -1);
                        st.giveItems(leather_pouch2full, 1);
                        st.soundEffect(SOUND_MIDDLE);
                    } else {
                        st.giveItems(kasha_bspider_talon, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
            if (st.ownItemCount(q_spider_tooth) < 6) {
                if (st.ownItemCount(q_spider_tooth) < 6) {
                    if (target_attack_type.getItemType() == AT_FIST || target_attack_type.getItemType() == AT_DUALFIST) {
                        int i0 = Rnd.get(100);
                        if (i0 < 70) {
                            st.giveItems(q_spider_tooth, 1);
                            if (st.ownItemCount(q_spider_tooth) >= 6) {
                                st.setCond(16);
                                st.soundEffect(SOUND_MIDDLE);
                            } else
                                st.soundEffect(SOUND_ITEMGET);
                        }
                    }
                }
            }
        } else if (npcId == kasha_bear) {
            if (st.ownItemCount(leather_pouch1) == 1) {
                if (target_attack_type.getItemType() == AT_FIST || target_attack_type.getItemType() == AT_DUALFIST) {
                    if (st.ownItemCount(kasha_bear_claw) == 4) {
                        st.setCond(7);
                        st.takeItems(kasha_bear_claw, -1);
                        st.takeItems(leather_pouch1, -1);
                        st.giveItems(leather_pouch1full, 1);
                        st.soundEffect(SOUND_MIDDLE);
                    } else {
                        st.giveItems(kasha_bear_claw, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        } else if (npcId == kasha_poker_spider) {
            if (st.ownItemCount(q_spider_tooth) < 6) {
                if (target_attack_type.getItemType() == AT_FIST || target_attack_type.getItemType() == AT_DUALFIST) {
                    int i0 = Rnd.get(100);
                    if (i0 < 70) {
                        st.giveItems(q_spider_tooth, 1);
                        if (st.ownItemCount(q_spider_tooth) >= 6) {
                            st.setCond(16);
                            st.soundEffect(SOUND_MIDDLE);
                        } else
                            st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        } else if (npcId == baar_dre_vanul) {
            if (st.ownItemCount(q_horn_need_kavatari) < 1) {
                if (target_attack_type.getItemType() == AT_FIST || target_attack_type.getItemType() == AT_DUALFIST) {
                    int i0 = Rnd.get(100);
                    if (i0 < 90) {
                        st.setCond(18);
                        st.giveItems(q_horn_need_kavatari, 1);
                        st.soundEffect(SOUND_MIDDLE);
                    }
                }
            }
        }
        return null;
    }
}