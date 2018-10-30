package org.mmocore.gameserver.scripts.quests;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
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
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _063_PathOfTheWarder extends Quest {
    // npc
    private final static int master_sione = 32195;
    private final static int master_gobie = 32198;
    private final static int captain_bathia = 30332;
    private final static int master_tobias = 30297;
    // mobs
    private final static int ol_mahum_patrol = 20053;
    private final static int ol_mahum_rookie = 20782;
    private final static int olmahum_takin = 27337;
    private final static int male_lizardman = 20919;
    private final static int male_lizardman_scout = 20920;
    private final static int male_lizardman_guard = 20921;
    // questitem
    private final static int q_almaum_order_doc = 9762;
    private final static int q_almaum_army_chart = 9763;
    private final static int q_order_doc_of_gobie = 9764;
    private final static int q_army_msg_to_human = 9765;
    private final static int q_army_msg_to_human_ret = 9766;
    private final static int q_army_msg_to_darkelf = 9767;
    private final static int q_army_msg_to_darkelf_ret = 9768;
    private final static int q_army_msg_to_sione = 9769;
    private final static int q_cature_orb = 9770;
    private final static int q_takin_catured_orb = 9771;
    private final static int q_still_pfeeil_estate = 9772;

    public _063_PathOfTheWarder() {
        super(false);
        addStartNpc(master_sione);
        addTalkId(master_gobie, captain_bathia, master_tobias);
        addKillId(ol_mahum_patrol, ol_mahum_rookie, olmahum_takin, male_lizardman, male_lizardman_scout, male_lizardman_guard);
        addQuestItem(q_almaum_order_doc, q_almaum_army_chart, q_order_doc_of_gobie, q_army_msg_to_human, q_army_msg_to_human_ret, q_army_msg_to_darkelf, q_army_msg_to_darkelf_ret, q_army_msg_to_sione, q_cature_orb);
        addLevelCheck(18);
        addClassIdCheck(ClassId.kamael_f_soldier);
        addRaceCheck(PlayerRace.kamael);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("way_of_wader");
        int npcId = npc.getNpcId();
        if (npcId == master_sione) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("way_of_wader", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "master_sione_q0063_05.htm";
            } else if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 1)
                    htmltext = "master_sione_q0063_06.htm";
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 1) {
                    st.setCond(2);
                    st.setMemoState("way_of_wader", String.valueOf(2), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "master_sione_q0063_08.htm";
                }
            }
        } else if (npcId == master_gobie) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 3) {
                    st.setCond(5);
                    st.setMemoState("way_of_wader", String.valueOf(4), true);
                    st.takeItems(q_order_doc_of_gobie, -1);
                    st.giveItems(q_army_msg_to_human, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "master_gobie_q0063_03.htm";
                }
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 7)
                    htmltext = "master_gobie_q0063_07.htm";
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 7) {
                    st.setCond(7);
                    st.setMemoState("way_of_wader", String.valueOf(8), true);
                    st.giveItems(q_army_msg_to_darkelf, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "master_gobie_q0063_08.htm";
                }
            } else if (event.equalsIgnoreCase("reply_4")) {
                if (GetMemoState == 12) {
                    st.setCond(9);
                    st.setMemoState("way_of_wader", String.valueOf(13), true);
                    st.giveItems(q_army_msg_to_sione, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "master_gobie_q0063_12.htm";
                }
            } else if (event.equalsIgnoreCase("reply_5")) {
                if (GetMemoState == 14) {
                    st.setMemoState("way_of_wader", String.valueOf(15), true);
                    htmltext = "master_gobie_q0063_15.htm";
                }
            } else if (event.equalsIgnoreCase("reply_6")) {
                if (GetMemoState == 15) {
                    st.setCond(11);
                    st.setMemoState("way_of_wader", String.valueOf(16), true);
                    st.setMemoState("way_of_wader_ex", String.valueOf(0), true);
                    st.giveItems(q_cature_orb, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "master_gobie_q0063_16.htm";
                }
            }
        } else if (npcId == captain_bathia) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 4) {
                    st.setMemoState("way_of_wader", String.valueOf(5), true);
                    st.takeItems(q_army_msg_to_human, -1);
                    st.giveItems(q_army_msg_to_human_ret, 1);
                    htmltext = "captain_bathia_q0063_03.htm";
                }
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 5)
                    htmltext = "captain_bathia_q0063_05.htm";
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 5) {
                    st.setCond(6);
                    st.setMemoState("way_of_wader", String.valueOf(6), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "captain_bathia_q0063_06.htm";
                }
            }
        } else if (npcId == master_tobias) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 9)
                    htmltext = "master_tobias_q0063_03.htm";
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 9) {
                    st.setMemoState("way_of_wader", String.valueOf(10), true);
                    htmltext = "master_tobias_q0063_04.htm";
                }
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 10) {
                    st.setCond(8);
                    st.setMemoState("way_of_wader", String.valueOf(11), true);
                    st.giveItems(q_army_msg_to_darkelf_ret, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "master_tobias_q0063_06.htm";
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int GetMemoState = st.getInt("way_of_wader");
        int talker_level = st.getPlayer().getLevel();
        int GetOneTimeQuestFlag = st.getPlayer().getPlayerVariables().getInt(PlayerVariables.profession_145);
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == master_sione) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "master_sione_q0063_02.htm";
                            st.exitQuest(true);
                            break;
                        case CLASS_ID:
                            htmltext = "master_sione_q0063_03.htm";
                            st.exitQuest(true);
                            break;
                        case RACE:
                            htmltext = "master_sione_q0063_04.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "master_sione_q0063_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == master_sione) {
                    if (GetMemoState == 1)
                        htmltext = "master_sione_q0063_07.htm";
                    else if (GetMemoState == 2 && (st.ownItemCount(q_almaum_order_doc) < 10 || st.ownItemCount(q_almaum_army_chart) < 5))
                        htmltext = "master_sione_q0063_09.htm";
                    else if (GetMemoState == 2 && st.ownItemCount(q_almaum_order_doc) >= 10 && st.ownItemCount(q_almaum_army_chart) >= 5) {
                        st.setCond(4);
                        st.setMemoState("way_of_wader", String.valueOf(3), true);
                        st.takeItems(q_almaum_order_doc, -1);
                        st.takeItems(q_almaum_army_chart, -1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "master_sione_q0063_10.htm";
                    } else if (GetMemoState == 3)
                        htmltext = "master_sione_q0063_11.htm";
                    else if (GetMemoState > 3 && GetMemoState != 13)
                        htmltext = "master_sione_q0063_12.htm";
                    else if (GetMemoState == 13) {
                        st.setCond(10);
                        st.setMemoState("way_of_wader", String.valueOf(14), true);
                        st.takeItems(q_army_msg_to_sione, -1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "master_sione_q0063_13.htm";
                    }
                } else if (npcId == master_gobie) {
                    if (GetMemoState < 3)
                        htmltext = "master_gobie_q0063_01.htm";
                    else if (GetMemoState == 3)
                        htmltext = "master_gobie_q0063_02.htm";
                    else if (GetMemoState == 4 || GetMemoState == 5)
                        htmltext = "master_gobie_q0063_04.htm";
                    else if (GetMemoState == 6) {
                        st.takeItems(q_army_msg_to_human_ret, -1);
                        st.setMemoState("way_of_wader", String.valueOf(7), true);
                        htmltext = "master_gobie_q0063_05.htm";
                    } else if (GetMemoState == 7)
                        htmltext = "master_gobie_q0063_06.htm";
                    else if (GetMemoState == 8)
                        htmltext = "master_gobie_q0063_09.htm";
                    else if (GetMemoState == 11) {
                        st.takeItems(q_army_msg_to_darkelf_ret, -1);
                        st.setMemoState("way_of_wader", String.valueOf(12), true);
                        htmltext = "master_gobie_q0063_10.htm";
                    } else if (GetMemoState == 12) {
                        st.giveItems(q_army_msg_to_sione, 1);
                        st.setMemoState("way_of_wader", String.valueOf(13), true);
                        htmltext = "master_gobie_q0063_11.htm";
                    } else if (GetMemoState == 13)
                        htmltext = "master_gobie_q0063_13.htm";
                    else if (GetMemoState == 14)
                        htmltext = "master_gobie_q0063_14.htm";
                    else if (GetMemoState == 15) {
                        st.setCond(11);
                        st.setMemoState("way_of_wader", String.valueOf(16), true);
                        st.setMemoState("way_of_wader_ex", String.valueOf(0), true);
                        st.giveItems(q_cature_orb, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "master_gobie_q0063_17.htm";
                    } else if (GetMemoState == 16 && st.ownItemCount(q_takin_catured_orb) < 1) {
                        st.setMemoState("way_of_wader_ex", String.valueOf(0), true);
                        htmltext = "master_gobie_q0063_19.htm";
                    } else if (GetMemoState == 16 && st.ownItemCount(q_takin_catured_orb) >= 1) {
                        st.takeItems(q_takin_catured_orb, -1);
                        st.giveItems(q_still_pfeeil_estate, 1);
                        htmltext = "master_gobie_q0063_20.htm";
                        if (GetOneTimeQuestFlag == 0) {
                            st.getPlayer().getPlayerVariables().set(PlayerVariables.profession_145, String.valueOf(1), -1);
                            if (talker_level >= 20)
                                st.addExpAndSp(320534, 22046);
                            else if (talker_level == 19)
                                st.addExpAndSp(456128, 28744);
                            else
                                st.addExpAndSp(591724, 35442);
                            st.giveItems(ADENA_ID, 163800);
                        }
                        st.removeMemo("way_of_wader");
                        st.removeMemo("way_of_wader_ex");
                        st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                    } else if (st.ownItemCount(q_still_pfeeil_estate) > 0)
                        htmltext = "master_gobie_q0063_21.htm";
                } else if (npcId == captain_bathia) {
                    if (GetMemoState == 4)
                        htmltext = "captain_bathia_q0063_01.htm";
                    else if (GetMemoState < 4)
                        htmltext = "captain_bathia_q0063_02.htm";
                    else if (GetMemoState == 5)
                        htmltext = "captain_bathia_q0063_04.htm";
                    else if (GetMemoState == 6)
                        htmltext = "captain_bathia_q0063_07.htm";
                    else if (GetMemoState > 6)
                        htmltext = "captain_bathia_q0063_08.htm";
                } else if (npcId == master_tobias) {
                    if (GetMemoState == 8) {
                        st.takeItems(q_army_msg_to_darkelf, -1);
                        st.setMemoState("way_of_wader", String.valueOf(9), true);
                        htmltext = "master_tobias_q0063_01.htm";
                    } else if (GetMemoState == 9)
                        htmltext = "master_tobias_q0063_02.htm";
                    else if (GetMemoState == 10) {
                        st.setCond(8);
                        st.setMemoState("way_of_wader", String.valueOf(11), true);
                        st.giveItems(q_army_msg_to_darkelf_ret, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "master_tobias_q0063_05.htm";
                    } else if (GetMemoState >= 11)
                        htmltext = "master_tobias_q0063_07.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("way_of_wader");
        int GetMemoStateEx = st.getInt("way_of_wader_ex");
        int npcId = npc.getNpcId();
        if (npcId == ol_mahum_patrol) {
            if (GetMemoState == 2 && st.ownItemCount(q_almaum_army_chart) < 5) {
                if (st.ownItemCount(q_almaum_army_chart) < 5) {
                    if (st.ownItemCount(q_almaum_order_doc) >= 10 && st.ownItemCount(q_almaum_army_chart) >= 4) {
                        st.setCond(3);
                        st.soundEffect(SOUND_MIDDLE);
                    } else
                        st.soundEffect(SOUND_ITEMGET);
                    st.giveItems(q_almaum_army_chart, 1);
                }
            }
        } else if (npcId == ol_mahum_rookie) {
            if (GetMemoState == 2 && st.ownItemCount(q_almaum_order_doc) < 5) {
                if (st.ownItemCount(q_almaum_order_doc) < 10) {
                    if (st.ownItemCount(q_almaum_order_doc) >= 9 && st.ownItemCount(q_almaum_army_chart) >= 5) {
                        st.setCond(3);
                        st.soundEffect(SOUND_MIDDLE);
                    } else
                        st.soundEffect(SOUND_ITEMGET);
                    st.giveItems(q_almaum_order_doc, 1);
                }
            }
        } else if (npcId == olmahum_takin) {
            if (GetMemoState == 16 && st.ownItemCount(q_takin_catured_orb) < 1) {
                st.setCond(12);
                st.giveItems(q_takin_catured_orb, 1);
                st.takeItems(q_cature_orb, -1);
                st.soundEffect(SOUND_MIDDLE);
            }
        } else if (npcId == male_lizardman || npcId == male_lizardman_scout || npcId == male_lizardman_guard) {
            if (GetMemoState == 16 && st.ownItemCount(q_takin_catured_orb) < 1) {
                if (GetMemoStateEx < 4)
                    st.setMemoState("way_of_wader_ex", String.valueOf(GetMemoStateEx + 1), true);
                else {
                    st.setMemoState("way_of_wader_ex", String.valueOf(0), true);
                    st.addSpawn(olmahum_takin, st.getPlayer().getX(), st.getPlayer().getY(), st.getPlayer().getZ());
                }
            }
        }
        return null;
    }
}