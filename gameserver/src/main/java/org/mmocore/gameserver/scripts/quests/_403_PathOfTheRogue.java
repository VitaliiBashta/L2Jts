package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.model.base.ClassId;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.serverpackets.SocialAction;
import org.mmocore.gameserver.object.Inventory;
import org.mmocore.gameserver.object.Player;
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
public class _403_PathOfTheRogue extends Quest {
    // npc
    private final static int captain_bezique = 30379;
    private final static int neti = 30425;
    // mobs
    private final static int tracker_skeleton = 20035;
    private final static int tracker_skeleton_leader = 20042;
    private final static int scout_skeleton = 20045;
    private final static int sniper_skeleton = 20051;
    private final static int ruin_spartoi = 20054;
    private final static int raging_spartoi = 20060;
    private final static int catseye_bandit = 27038;
    // questitem
    private final static int beziques_letter = 1180;
    private final static int spatois_bones = 1183;
    private final static int horseshoe_of_light = 1184;
    private final static int wanted_bill = 1185;
    private final static int stolen_jewelry = 1186;
    private final static int stolen_tomes = 1187;
    private final static int stolen_ring = 1188;
    private final static int stolen_necklace = 1189;
    private final static int beziques_recommendation = 1190;
    // items
    private final static int netis_bow = 1181;
    private final static int netis_dagger = 1182;

    public _403_PathOfTheRogue() {
        super(false);
        addStartNpc(captain_bezique);
        addTalkId(neti);
        addKillId(catseye_bandit, tracker_skeleton, tracker_skeleton_leader, scout_skeleton, sniper_skeleton, ruin_spartoi, raging_spartoi);
        addAttackId(catseye_bandit, tracker_skeleton, tracker_skeleton_leader, scout_skeleton, sniper_skeleton, ruin_spartoi, raging_spartoi);
        addQuestItem(beziques_letter, spatois_bones, horseshoe_of_light, wanted_bill, stolen_jewelry, stolen_tomes, stolen_ring, stolen_necklace, beziques_recommendation, netis_bow, netis_dagger);
        addLevelCheck(18);
        addClassIdCheck(ClassId.fighter);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        int talker_occupation = st.getPlayer().getPlayerClassComponent().getClassId().getId();
        int fighter = 0x00;
        int rogue = 0x07;
        if (npcId == captain_bezique) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                st.giveItems(beziques_letter, 1);
                htmltext = "captain_bezique_q0403_06.htm";
            } else if (event.equalsIgnoreCase("403_reply_2")) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = "captain_bezique_q0403_03.htm";
                        st.exitQuest(true);
                        break;
                    case CLASS_ID:
                        if (talker_occupation != fighter) {
                            if (talker_occupation == rogue) {
                                htmltext = "captain_bezique_q0403_02a.htm";
                                st.exitQuest(true);
                            } else {
                                htmltext = "captain_bezique_q0403_02.htm";
                                st.exitQuest(true);
                            }
                        }
                        break;
                    default:
                        if (st.ownItemCount(beziques_recommendation) > 0)
                            htmltext = "captain_bezique_q0403_04.htm";
                        else
                            htmltext = "captain_bezique_q0403_05.htm";
                        break;
                }
            }
        } else if (npcId == neti) {
            if (event.equalsIgnoreCase("403_reply_1")) {
                if (st.ownItemCount(beziques_letter) > 0) {
                    st.takeItems(beziques_letter, 1);
                    if (st.ownItemCount(netis_bow) == 0)
                        st.giveItems(netis_bow, 1);
                    if (st.ownItemCount(netis_dagger) == 0)
                        st.giveItems(netis_dagger, 1);
                    st.setCond(2);
                    htmltext = "neti_q0403_05.htm";
                    st.soundEffect(SOUND_MIDDLE);
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        Player player = st.getPlayer();
        int npcId = npc.getNpcId();
        int id = st.getState();
        int talker_level = st.getPlayer().getLevel();
        int GetOneTimeQuestFlag = st.getPlayer().getPlayerVariables().getInt(PlayerVariables.profession_145);
        switch (id) {
            case CREATED:
                if (npcId == captain_bezique)
                    htmltext = "captain_bezique_q0403_01.htm";
                break;
            case STARTED:
                if (npcId == captain_bezique) {
                    if (st.ownItemCount(horseshoe_of_light) == 0 && st.ownItemCount(stolen_jewelry) > 0 && st.ownItemCount(stolen_tomes) > 0 && st.ownItemCount(stolen_ring) > 0 && st.ownItemCount(stolen_necklace) > 0) {
                        st.takeItems(netis_bow, -1);
                        st.takeItems(netis_dagger, -1);
                        st.takeItems(stolen_jewelry, 1);
                        st.takeItems(stolen_tomes, 1);
                        st.takeItems(stolen_ring, 1);
                        st.takeItems(stolen_necklace, 1);
                        st.takeItems(wanted_bill, -1);
                        st.giveItems(beziques_recommendation, 1);
                        if (GetOneTimeQuestFlag == 0) {
                            st.getPlayer().getPlayerVariables().set(PlayerVariables.profession_145, String.valueOf(1), -1);
                            if (talker_level >= 20)
                                st.addExpAndSp(320534, 20232);
                            else if (talker_level == 19)
                                st.addExpAndSp(456128, 26930);
                            else
                                st.addExpAndSp(591724, 33628);
                            st.giveItems(ADENA_ID, 163800);
                        }
                        htmltext = "captain_bezique_q0403_09.htm";
                        player.sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                    } else if (st.ownItemCount(horseshoe_of_light) == 0 && st.ownItemCount(beziques_letter) > 0)
                        htmltext = "captain_bezique_q0403_07.htm";
                    else if (st.ownItemCount(horseshoe_of_light) > 0) {
                        htmltext = "captain_bezique_q0403_08.htm";
                        st.takeItems(horseshoe_of_light, 1);
                        st.giveItems(wanted_bill, 1);
                        st.setCond(5);
                    } else if (st.ownItemCount(netis_bow) > 0 && st.ownItemCount(netis_dagger) > 0 && st.ownItemCount(wanted_bill) == 0)
                        htmltext = "captain_bezique_q0403_10.htm";
                    else if (st.ownItemCount(wanted_bill) > 0)
                        htmltext = "captain_bezique_q0403_11.htm";
                } else if (npcId == neti) {
                    if (st.ownItemCount(beziques_letter) > 0)
                        htmltext = "neti_q0403_01.htm";
                    else if (st.ownItemCount(horseshoe_of_light) == 0 && st.ownItemCount(beziques_letter) == 0) {
                        if (st.ownItemCount(wanted_bill) > 0)
                            htmltext = "neti_q0403_08.htm";
                        else if (st.ownItemCount(spatois_bones) < 10)
                            htmltext = "neti_q0403_06.htm";
                        else if (st.ownItemCount(spatois_bones) >= 10) {
                            st.setCond(4);
                            st.takeItems(spatois_bones, -1);
                            st.giveItems(horseshoe_of_light, 1);
                            htmltext = "neti_q0403_07.htm";
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    } else if (st.ownItemCount(horseshoe_of_light) > 0)
                        htmltext = "neti_q0403_08.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int GetMemoState = st.getInt("myself_i_quest2_cookie");
        if (npcId == catseye_bandit) {
            Functions.npcSay(npc, NpcString.I_MUST_DO_SOMETHING_ABOUT_THIS_SHAMEFUL_INCIDENT);
            if (st.ownItemCount(wanted_bill) > 0 && GetMemoState == 1) {
                switch (Rnd.get(4)) {
                    case 0: {
                        if (st.ownItemCount(stolen_jewelry) == 0) {
                            st.giveItems(stolen_jewelry, 1);
                            if (st.ownItemCount(stolen_jewelry) + st.ownItemCount(stolen_tomes) + st.ownItemCount(stolen_ring) + st.ownItemCount(stolen_necklace) >= 4) {
                                st.setCond(6);
                                st.soundEffect(SOUND_MIDDLE);
                            } else
                                st.soundEffect(SOUND_ITEMGET);
                        }
                        break;
                    }
                    case 1: {
                        if (st.ownItemCount(stolen_tomes) == 0) {
                            st.giveItems(stolen_tomes, 1);
                            if (st.ownItemCount(stolen_jewelry) + st.ownItemCount(stolen_tomes) + st.ownItemCount(stolen_ring) + st.ownItemCount(stolen_necklace) >= 4) {
                                st.setCond(6);
                                st.soundEffect(SOUND_MIDDLE);
                            } else
                                st.soundEffect(SOUND_ITEMGET);
                        }
                        break;
                    }
                    case 2: {
                        if (st.ownItemCount(stolen_ring) == 0) {
                            st.giveItems(stolen_ring, 1);
                            if (st.ownItemCount(stolen_jewelry) + st.ownItemCount(stolen_tomes) + st.ownItemCount(stolen_ring) + st.ownItemCount(stolen_necklace) >= 4) {
                                st.setCond(6);
                                st.soundEffect(SOUND_MIDDLE);
                            } else
                                st.soundEffect(SOUND_ITEMGET);
                        }
                        break;
                    }
                    case 3: {
                        if (st.ownItemCount(stolen_necklace) == 0) {
                            st.giveItems(stolen_necklace, 1);
                            if (st.ownItemCount(stolen_jewelry) + st.ownItemCount(stolen_tomes) + st.ownItemCount(stolen_ring) + st.ownItemCount(stolen_necklace) >= 4) {
                                st.setCond(6);
                                st.soundEffect(SOUND_MIDDLE);
                            } else
                                st.soundEffect(SOUND_ITEMGET);
                        }
                        break;
                    }
                }
            }
        } else if (npcId == tracker_skeleton && npcId == scout_skeleton && npcId == sniper_skeleton) {
            if (st.ownItemCount(spatois_bones) < 10 && GetMemoState == 1 && Rnd.get(10) < 2) {
                st.giveItems(spatois_bones, 1);
                if (st.ownItemCount(spatois_bones) >= 10) {
                    st.setCond(3);
                    st.soundEffect(SOUND_MIDDLE);
                } else
                    st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == tracker_skeleton_leader) {
            if (st.ownItemCount(spatois_bones) < 10 && GetMemoState == 1 && Rnd.get(10) < 3) {
                st.giveItems(spatois_bones, 1);
                if (st.ownItemCount(spatois_bones) >= 10) {
                    st.setCond(3);
                    st.soundEffect(SOUND_MIDDLE);
                } else
                    st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == ruin_spartoi && npcId == raging_spartoi) {
            if (st.ownItemCount(spatois_bones) < 10 && GetMemoState == 1 && Rnd.get(10) < 8) {
                st.giveItems(spatois_bones, 1);
                if (st.ownItemCount(spatois_bones) >= 10) {
                    st.setCond(3);
                    st.soundEffect(SOUND_MIDDLE);
                } else
                    st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }

    @Override
    public String onAttack(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("myself_i_quest2_cookie");
        if (st.getItemEquipped(Inventory.PAPERDOLL_RHAND) != netis_bow && st.getItemEquipped(Inventory.PAPERDOLL_RHAND) != netis_dagger) {
            st.setMemoState("myself_i_quest2_cookie", "0");
        } else if (GetMemoState == 0) {
            st.setMemoState("myself_i_quest2_cookie", "1");
            Functions.npcSay(npc, NpcString.YOU_CHILDISH_FOOL_DO_YOU_THINK_YOU_CAN_CATCH_ME);
        }
        return null;
    }
}