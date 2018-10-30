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
 * @version 1.1
 * @date 04/10/2015
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _418_PathOfTheArtisan extends Quest {
    // npc
    private final static int blacksmith_silvery = 30527;
    private final static int blacksmith_kluto = 30317;
    private final static int blacksmith_pinter = 30298;
    private final static int first_elder_lockirin = 30531;
    private final static int mineral_trader_hittchi = 31963;
    private final static int railman_obi = 32052;
    private final static int warehouse_keeper_rydie = 31956;
    // mobs
    private final static int boogle_ratman = 20389;
    private final static int boogle_ratman_leader = 20390;
    private final static int vuku_orc_fighter = 20017;
    // questitem
    private final static int totem_spirit_claw = 1622;
    private final static int tatarus_letter = 1623;
    private final static int silverys_ring = 1632;
    private final static int pass_1st = 1633;
    private final static int pass_2nd = 1634;
    private final static int pass_final = 1635;
    private final static int ratman_tooth = 1636;
    private final static int big_ratman_tooth = 1637;
    private final static int klutos_letter = 1638;
    private final static int footprint = 1639;
    private final static int secret_box1 = 1640;
    private final static int secret_box2 = 1641;

    public _418_PathOfTheArtisan() {
        super(false);
        addStartNpc(blacksmith_silvery);
        addTalkId(blacksmith_kluto, blacksmith_pinter, first_elder_lockirin, mineral_trader_hittchi, railman_obi, warehouse_keeper_rydie);
        addKillId(boogle_ratman, boogle_ratman_leader, vuku_orc_fighter);
        addQuestItem(silverys_ring, ratman_tooth, big_ratman_tooth, pass_1st, klutos_letter, footprint, secret_box1, pass_2nd, secret_box2, totem_spirit_claw, tatarus_letter);
        addLevelCheck(18);
        addClassIdCheck(ClassId.dwarven_fighter);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("path_to_artisan");
        int npcId = npc.getNpcId();
        int talker_level = st.getPlayer().getLevel();
        int GetOneTimeQuestFlag = st.getPlayer().getPlayerVariables().getInt(PlayerVariables.profession_145);
        int talker_occupation = st.getPlayer().getPlayerClassComponent().getClassId().getId();
        int dwarven_fighter = 0x35;
        int artisan = 0x38;
        if (npcId == blacksmith_silvery) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.giveItems(silverys_ring, 1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "blacksmith_silvery_q0418_06.htm";
            } else if (event.equalsIgnoreCase("reply_1")) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = "blacksmith_silvery_q0418_03.htm";
                        st.exitQuest(true);
                        break;
                    case CLASS_ID:
                        if (talker_occupation != dwarven_fighter) {
                            if (talker_occupation == artisan) {
                                htmltext = "blacksmith_silvery_q0418_02a.htm";
                                st.exitQuest(true);
                            } else {
                                htmltext = "blacksmith_silvery_q0418_02.htm";
                                st.exitQuest(true);
                            }
                        }
                        break;
                    default:
                        if (st.ownItemCount(pass_final) != 0)
                            htmltext = "blacksmith_silvery_q0418_04.htm";
                        else
                            htmltext = "blacksmith_silvery_q0418_05.htm";
                        break;
                }
            } else if (event.equalsIgnoreCase("reply_2")) {
                st.takeItems(totem_spirit_claw, -1);
                st.giveItems(tatarus_letter, 1);
                htmltext = "blacksmith_silvery_q0418_11.htm";
            } else if (event.equalsIgnoreCase("reply_10")) {
                st.setCond(3);
                st.takeItems(silverys_ring, -1);
                st.takeItems(ratman_tooth, -1);
                st.takeItems(big_ratman_tooth, -1);
                st.giveItems(pass_1st, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "blacksmith_silvery_q0418_08b.htm";
            } else if (event.equalsIgnoreCase("reply_20")) {
                st.setCond(8);
                st.setMemoState("path_to_artisan", String.valueOf(10), true);
                st.takeItems(silverys_ring, -1);
                st.takeItems(ratman_tooth, -1);
                st.takeItems(big_ratman_tooth, -1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "blacksmith_silvery_q0418_08c.htm";
            }
        } else if (npcId == blacksmith_kluto) {
            if (event.equalsIgnoreCase("reply_1"))
                htmltext = "blacksmith_kluto_q0418_02.htm";
            else if (event.equalsIgnoreCase("reply_2"))
                htmltext = "blacksmith_kluto_q0418_05.htm";
            else if (event.equalsIgnoreCase("reply_3"))
                htmltext = "blacksmith_kluto_q0418_03.htm";
            else if (event.equalsIgnoreCase("reply_4")) {
                st.setCond(4);
                st.giveItems(klutos_letter, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "blacksmith_kluto_q0418_04.htm";
            } else if (event.equalsIgnoreCase("reply_5"))
                htmltext = "blacksmith_kluto_q0418_06.htm";
            else if (event.equalsIgnoreCase("reply_6")) {
                st.setCond(4);
                st.giveItems(klutos_letter, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "blacksmith_kluto_q0418_07.htm";
            } else if (event.equalsIgnoreCase("reply_7") && st.ownItemCount(pass_1st) > 0 && st.ownItemCount(pass_2nd) > 0 && st.ownItemCount(secret_box2) > 0) {
                htmltext = "blacksmith_kluto_q0418_10.htm";
                st.takeItems(pass_1st, 1);
                st.takeItems(pass_2nd, 1);
                st.takeItems(secret_box2, 1);
                st.giveItems(pass_final, 1);
                if (GetOneTimeQuestFlag == 0) {
                    st.getPlayer().getPlayerVariables().set(PlayerVariables.profession_145, String.valueOf(1), -1);
                    if (talker_level >= 20)
                        st.addExpAndSp(320534, 32452);
                    else if (talker_level == 19)
                        st.addExpAndSp(456128, 30150);
                    else
                        st.addExpAndSp(591724, 36848);
                    st.giveItems(ADENA_ID, 163800);
                }
                st.removeMemo("path_to_artisan");
                st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(false);
            } else if (event.equalsIgnoreCase("reply_8"))
                htmltext = "blacksmith_kluto_q0418_11.htm";
            else if (event.equalsIgnoreCase("reply_9") && st.ownItemCount(pass_1st) > 0 && st.ownItemCount(pass_2nd) > 0 && st.ownItemCount(secret_box2) > 0) {
                htmltext = "blacksmith_kluto_q0418_12.htm";
                st.takeItems(pass_1st, 1);
                st.takeItems(pass_2nd, 1);
                st.takeItems(secret_box2, 1);
                st.giveItems(pass_final, 1);
                if (GetOneTimeQuestFlag == 0) {
                    st.getPlayer().getPlayerVariables().set(PlayerVariables.profession_145, String.valueOf(1), -1);
                    if (talker_level >= 20)
                        st.addExpAndSp(160267, 11726);
                    else if (talker_level == 19)
                        st.addExpAndSp(228064, 15075);
                    else
                        st.addExpAndSp(295862, 18424);
                    st.giveItems(ADENA_ID, 81900);
                }
                st.removeMemo("path_to_artisan");
                st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(false);
            }
        } else if (npcId == blacksmith_pinter) {
            if (event.equalsIgnoreCase("reply_1"))
                htmltext = "blacksmith_pinter_q0418_02.htm";
            else if (event.equalsIgnoreCase("reply_2") && st.ownItemCount(klutos_letter) > 0) {
                st.setCond(5);
                st.takeItems(klutos_letter, 1);
                st.giveItems(footprint, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "blacksmith_pinter_q0418_03.htm";
            } else if (event.equalsIgnoreCase("reply_3") && st.ownItemCount(footprint) > 0 && st.ownItemCount(secret_box1) > 0) {
                st.setCond(7);
                st.takeItems(secret_box1, 1);
                st.takeItems(footprint, 1);
                st.giveItems(secret_box2, 1);
                st.giveItems(pass_2nd, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "blacksmith_pinter_q0418_06.htm";
            }
        } else if (npcId == first_elder_lockirin) {
            if (event.equalsIgnoreCase("reply_1") && GetMemoState == 101) {
                htmltext = "first_elder_lockirin_q0418_05.htm";
                st.giveItems(pass_final, 1);
                if (GetOneTimeQuestFlag == 0) {
                    st.getPlayer().getPlayerVariables().set(PlayerVariables.profession_145, String.valueOf(1), -1);
                    if (talker_level >= 20)
                        st.addExpAndSp(160267, 11726);
                    else if (talker_level == 19)
                        st.addExpAndSp(228064, 15075);
                    else
                        st.addExpAndSp(295862, 18424);
                    st.giveItems(ADENA_ID, 81900);
                }
                st.removeMemo("path_to_artisan");
                st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(false);
            }
        } else if (npcId == mineral_trader_hittchi) {
            if (event.equalsIgnoreCase("reply_1") && GetMemoState == 100)
                htmltext = "mineral_trader_hittchi_q0418_02.htm";
            else if (event.equalsIgnoreCase("reply_101") && GetMemoState == 100) {
                st.setCond(10);
                st.setMemoState("path_to_artisan", String.valueOf(101), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "mineral_trader_hittchi_q0418_03.htm";
            } else if (event.equalsIgnoreCase("reply_102") && GetMemoState == 100) {
                st.setCond(11);
                st.setMemoState("path_to_artisan", String.valueOf(102), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "mineral_trader_hittchi_q0418_05.htm";
            } else if (event.equalsIgnoreCase("reply_2") && GetMemoState == 100)
                htmltext = "mineral_trader_hittchi_q0418_06.htm";
            else if (event.equalsIgnoreCase("reply_201") && GetMemoState == 100) {
                st.setCond(12);
                st.setMemoState("path_to_artisan", String.valueOf(201), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "mineral_trader_hittchi_q0418_07.htm";
            } else if (event.equalsIgnoreCase("reply_202") && GetMemoState == 100) {
                st.setMemoState("path_to_artisan", String.valueOf(202), true);
                htmltext = "mineral_trader_hittchi_q0418_09.htm";
            } else if (event.equalsIgnoreCase("reply_203") && GetMemoState == 202) {
                htmltext = "mineral_trader_hittchi_q0418_10.htm";
                st.giveItems(pass_final, 1);
                if (GetOneTimeQuestFlag == 0) {
                    st.getPlayer().getPlayerVariables().set(PlayerVariables.profession_145, String.valueOf(1), -1);
                    if (talker_level >= 20)
                        st.addExpAndSp(160267, 11726);
                    else if (talker_level == 19)
                        st.addExpAndSp(228064, 15075);
                    else
                        st.addExpAndSp(295862, 18424);
                    st.giveItems(ADENA_ID, 81900);
                }
                st.removeMemo("path_to_artisan");
                st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(false);
            }
        } else if (npcId == railman_obi) {
            if (event.equalsIgnoreCase("reply_1") && GetMemoState == 10) {
                st.setCond(9);
                st.setMemoState("path_to_artisan", String.valueOf(100), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "railman_obi_q0418_07.htm";
            } else if (event.equalsIgnoreCase("reply_2") && GetMemoState == 102) {
                htmltext = "railman_obi_q0418_13.htm";
                st.giveItems(pass_final, 1);
                if (GetOneTimeQuestFlag == 0) {
                    st.getPlayer().getPlayerVariables().set(PlayerVariables.profession_145, String.valueOf(1), -1);
                    if (talker_level >= 20)
                        st.addExpAndSp(160267, 11726);
                    else if (talker_level == 19)
                        st.addExpAndSp(228064, 15075);
                    else
                        st.addExpAndSp(295862, 18424);
                    st.giveItems(ADENA_ID, 81900);
                }
                st.removeMemo("path_to_artisan");
                st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(false);
            }
        } else if (npcId == warehouse_keeper_rydie) {
            if (event.equalsIgnoreCase("reply_1") && GetMemoState == 201) {
                htmltext = "warehouse_keeper_rydie_q0418_04.htm";
                st.giveItems(pass_final, 1);
                if (GetOneTimeQuestFlag == 0) {
                    st.getPlayer().getPlayerVariables().set(PlayerVariables.profession_145, String.valueOf(1), -1);
                    if (talker_level >= 20)
                        st.addExpAndSp(160267, 11726);
                    else if (talker_level == 19)
                        st.addExpAndSp(228064, 15075);
                    else
                        st.addExpAndSp(295862, 18424);
                    st.giveItems(ADENA_ID, 81900);
                }
                st.removeMemo("path_to_artisan");
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
        int GetMemoState = st.getInt("path_to_artisan");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == blacksmith_silvery)
                    htmltext = "blacksmith_silvery_q0418_01.htm";
                break;
            case STARTED:
                if (npcId == blacksmith_silvery) {
                    if (st.ownItemCount(silverys_ring) == 1 && st.ownItemCount(ratman_tooth) + st.ownItemCount(big_ratman_tooth) < 12)
                        htmltext = "blacksmith_silvery_q0418_07.htm";
                    else if (st.ownItemCount(silverys_ring) == 1 && st.ownItemCount(ratman_tooth) >= 10 && st.ownItemCount(big_ratman_tooth) >= 2)
                        htmltext = "blacksmith_silvery_q0418_08a.htm";
                    else if (st.ownItemCount(pass_1st) == 1)
                        htmltext = "blacksmith_silvery_q0418_09.htm";
                    else if (st.ownItemCount(pass_1st) == 0 && GetMemoState == 10)
                        htmltext = "blacksmith_silvery_q0418_09a.htm";
                } else if (npcId == blacksmith_kluto) {
                    if (st.ownItemCount(footprint) == 0 && st.ownItemCount(klutos_letter) == 0 && st.ownItemCount(pass_1st) > 0 && st.ownItemCount(pass_2nd) == 0 && st.ownItemCount(secret_box2) == 0)
                        htmltext = "blacksmith_kluto_q0418_01.htm";
                    else if (st.ownItemCount(pass_1st) > 0 && (st.ownItemCount(klutos_letter) > 0 || st.ownItemCount(footprint) > 0))
                        htmltext = "blacksmith_kluto_q0418_08.htm";
                    else if (st.ownItemCount(pass_1st) > 0 && st.ownItemCount(pass_2nd) > 0 && st.ownItemCount(secret_box2) > 0)
                        htmltext = "blacksmith_kluto_q0418_09.htm";
                } else if (npcId == blacksmith_pinter) {
                    if (st.ownItemCount(pass_1st) > 0 && st.ownItemCount(klutos_letter) > 0)
                        htmltext = "blacksmith_pinter_q0418_01.htm";
                    else if (st.ownItemCount(pass_1st) > 0 && st.ownItemCount(footprint) > 0 && st.ownItemCount(secret_box1) == 0)
                        htmltext = "blacksmith_pinter_q0418_04.htm";
                    else if (st.ownItemCount(pass_1st) > 0 && st.ownItemCount(footprint) > 0 && st.ownItemCount(secret_box1) > 0)
                        htmltext = "blacksmith_pinter_q0418_05.htm";
                    else if (st.ownItemCount(pass_1st) > 0 && st.ownItemCount(pass_2nd) > 0 && st.ownItemCount(secret_box2) > 0)
                        htmltext = "blacksmith_pinter_q0418_07.htm";
                } else if (npcId == first_elder_lockirin) {
                    if (GetMemoState == 101)
                        htmltext = "first_elder_lockirin_q0418_01.htm";
                } else if (npcId == mineral_trader_hittchi) {
                    if (GetMemoState == 100)
                        htmltext = "mineral_trader_hittchi_q0418_01.htm";
                    else if (GetMemoState == 101)
                        htmltext = "mineral_trader_hittchi_q0418_04.htm";
                    else if (GetMemoState == 102)
                        htmltext = "mineral_trader_hittchi_q0418_06a.htm";
                    else if (GetMemoState == 201)
                        htmltext = "mineral_trader_hittchi_q0418_08.htm";
                    else if (GetMemoState == 202)
                        htmltext = "mineral_trader_hittchi_q0418_11.htm";
                } else if (npcId == railman_obi) {
                    if (GetMemoState == 10)
                        htmltext = "railman_obi_q0418_01.htm";
                    else if (GetMemoState == 100)
                        htmltext = "railman_obi_q0418_08.htm";
                    else if (GetMemoState == 102)
                        htmltext = "railman_obi_q0418_09.htm";
                } else if (npcId == warehouse_keeper_rydie) {
                    if (GetMemoState == 201)
                        htmltext = "warehouse_keeper_rydie_q0418_01.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == boogle_ratman) {
            if (st.ownItemCount(silverys_ring) == 1 && st.ownItemCount(ratman_tooth) < 10) {
                if (Rnd.get(10) < 7) {
                    if (st.ownItemCount(ratman_tooth) == 9) {
                        st.giveItems(ratman_tooth, 1);
                        if (st.ownItemCount(big_ratman_tooth) >= 2)
                            st.setCond(2);
                        st.soundEffect(SOUND_MIDDLE);
                    } else {
                        st.giveItems(ratman_tooth, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        } else if (npcId == boogle_ratman_leader) {
            if (st.ownItemCount(silverys_ring) == 1 && st.ownItemCount(big_ratman_tooth) < 2) {
                if (Rnd.get(10) < 5) {
                    if (st.ownItemCount(big_ratman_tooth) == 1) {
                        st.giveItems(big_ratman_tooth, 1);
                        if (st.ownItemCount(ratman_tooth) >= 10)
                            st.setCond(2);
                        st.soundEffect(SOUND_MIDDLE);
                    } else {
                        st.giveItems(big_ratman_tooth, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        } else if (npcId == vuku_orc_fighter) {
            if (st.ownItemCount(footprint) == 1 && st.ownItemCount(secret_box1) < 1) {
                if (Rnd.get(10) < 2) {
                    st.setCond(6);
                    st.giveItems(secret_box1, 1);
                    st.soundEffect(SOUND_MIDDLE);
                }
            }
        }
        return null;
    }
}