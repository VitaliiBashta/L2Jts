package org.mmocore.gameserver.scripts.quests;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.ReflectionUtils;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _236_SeedsOfChaos extends Quest {
    // npc
    private static final int kekrops = 32138;
    private static final int shadow_hardin = 31522;
    private static final int keitnat_q0236 = 32235;
    private static final int keitnat_q0236a = 32332;
    private static final int keitnat_q0236b = 32333;
    private static final int stone_q0236 = 32238;
    private static final int harlkil_q0236 = 32236;
    private static final int harlkil_q0236a = 32334;
    private static final int subelder_maotric = 32190;
    private static final int roden_q0236 = 32237;
    private static final int mother_nornil_q0236 = 32239;
    // mobs
    private static final int needle_stakato_drone = 21516;
    private static final int vampire_wizard_a = 21589;
    private static final int brilliant_cry = 21532;
    private static final int brilliant_will = 21533;
    private static final int brilliant_will_1 = 21534;
    private static final int brilliant_mark = 21535;
    private static final int brilliant_crown = 21536;
    private static final int brilliant_fang = 21537;
    private static final int brilliant_fang_1 = 21538;
    private static final int brilliant_anguish = 21539;
    private static final int brilliant_anguish_1 = 21540;
    // questitem
    private static final int medal_of_light = 9743;
    private static final int dark_song_crystal = 9745;
    private static final int q_star_of_destiny = 5011;
    private static final int blood_jewel = 9744;
    // etcitem
    private static final int scrl_of_ench_wp_a = 729;
    // zone_controller
    private static final int inzone_id = 12;

    public _236_SeedsOfChaos() {
        super(false);
        addStartNpc(kekrops);
        addTalkId(shadow_hardin, keitnat_q0236, keitnat_q0236a, keitnat_q0236b, stone_q0236, harlkil_q0236, harlkil_q0236a, subelder_maotric, roden_q0236, mother_nornil_q0236);
        addKillId(needle_stakato_drone, vampire_wizard_a, brilliant_cry, brilliant_will, brilliant_will_1, brilliant_mark, brilliant_crown, brilliant_fang, brilliant_fang_1, brilliant_anguish, brilliant_anguish_1);
        addQuestItem(medal_of_light, dark_song_crystal, blood_jewel);
        addLevelCheck(75);
        addQuestCompletedCheck(234);
        addRaceCheck(PlayerRace.kamael);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("seed_of_chaos");
        int GetMemoStateEx = st.getInt("seed_of_chaos_ex");
        int spawned_keitnat_q0236 = st.getInt("spawned_keitnat_q0236");
        int spawned_keitnat_q0236a = st.getInt("spawned_keitnat_q0236a");
        int spawned_keitnat_q0236b = st.getInt("spawned_keitnat_q0236b");
        int spawned_harlkil_q0236 = st.getInt("spawned_harlkil_q0236");
        int spawned_harlkil_q0236a = st.getInt("spawned_harlkil_q0236a");
        String keitnat_q0236_player_name = st.get("keitnat_q0236_player_name");
        String keitnat_q0236a_player_name = st.get("keitnat_q0236a_player_name");
        String keitnat_q0236b_player_name = st.get("keitnat_q0236b_player_name");
        String harlkil_q0236_player_name = st.get("harlkil_q0236_player_name");
        String harlkil_q0236a_player_name = st.get("harlkil_q0236a_player_name");
        int npcId = npc.getNpcId();
        if (npcId == kekrops) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("seed_of_chaos", String.valueOf(1), true);
                st.setMemoState("spawned_keitnat_q0236", String.valueOf(0), true);
                st.setMemoState("spawned_keitnat_q0236a", String.valueOf(0), true);
                st.setMemoState("spawned_keitnat_q0236b", String.valueOf(0), true);
                st.setMemoState("spawned_harlkil_q0236", String.valueOf(0), true);
                st.setMemoState("spawned_harlkil_q0236a", String.valueOf(0), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "kekrops_q0236_04.htm";
            } else if (event.equalsIgnoreCase("reply_1"))
                htmltext = "kekrops_q0236_03.htm";
            else if (event.equalsIgnoreCase("reply_34")) {
                st.setCond(15);
                st.setMemoState("seed_of_chaos", String.valueOf(40), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "kekrops_q0236_12.htm";
            }
        } else if (npcId == shadow_hardin) {
            if (event.equalsIgnoreCase("reply_1") && GetMemoState == 1)
                htmltext = "shadow_hardin_q0236_04a.htm";
            else if (event.equalsIgnoreCase("reply_3") && GetMemoState == 1) {
                st.setCond(2);
                st.setMemoState("seed_of_chaos", String.valueOf(2), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "shadow_hardin_q0236_05a.htm";
            } else if (event.equalsIgnoreCase("reply_2"))
                htmltext = "shadow_hardin_q0236_04b.htm";
            else if (event.equalsIgnoreCase("reply_4") && GetMemoState == 1) {
                st.setCond(6);
                st.setMemoState("seed_of_chaos", String.valueOf(3), true);
                st.setMemoState("seed_of_chaos_ex", String.valueOf(0), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "shadow_hardin_q0236_05b.htm";
            } else if (event.equalsIgnoreCase("reply_6") && GetMemoState == 2 && st.ownItemCount(dark_song_crystal) >= 1) {
                st.setCond(4);
                st.setMemoState("seed_of_chaos", String.valueOf(6), true);
                st.takeItems(dark_song_crystal, -1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "shadow_hardin_q0236_09a.htm";
            } else if (event.equalsIgnoreCase("reply_7") && GetMemoState == 6) {
                if (spawned_keitnat_q0236 == 0) {
                    st.setMemoState("spawned_keitnat_q0236", String.valueOf(1), true);
                    st.setMemoState("keitnat_q0236_player_name", st.getPlayer().getName(), true);
                    NpcInstance keitnat_236 = st.addSpawn(keitnat_q0236, st.getPlayer().getX() + 10, st.getPlayer().getY() + 10, st.getPlayer().getZ() + 10);
                    Functions.npcSay(keitnat_236, NpcString.S1_FINALLY_WE_MEET, st.getPlayer().getName());
                    st.startQuestTimer("23611", 120000, keitnat_236);
                    htmltext = "shadow_hardin_q0236_12a.htm";
                } else if (keitnat_q0236_player_name == st.getPlayer().getName())
                    htmltext = "shadow_hardin_q0236_13a.htm";
                else
                    htmltext = "shadow_hardin_q0236_14a.htm";
            } else if (event.equalsIgnoreCase("reply_101") && GetMemoState == 3 && GetMemoStateEx == 2) {
                if (spawned_keitnat_q0236a == 0) {
                    st.setMemoState("spawned_keitnat_q0236a", String.valueOf(1), true);
                    st.setMemoState("keitnat_q0236a_player_name", st.getPlayer().getName(), true);
                    NpcInstance keitnat_236a = st.addSpawn(keitnat_q0236a, st.getPlayer().getX() + 10, st.getPlayer().getY() + 10, st.getPlayer().getZ() + 10);
                    Functions.npcSay(keitnat_236a, NpcString.S1_DID_YOU_WAIT_FOR_LONG, st.getPlayer().getName());
                    st.startQuestTimer("23612", 120000, keitnat_236a);
                    htmltext = "shadow_hardin_q0236_09b.htm";
                } else if (keitnat_q0236a_player_name == st.getPlayer().getName())
                    htmltext = "shadow_hardin_q0236_10b.htm";
                else
                    htmltext = "shadow_hardin_q0236_11b.htm";
            } else if (event.equalsIgnoreCase("reply_201") && GetMemoState == 7 && st.ownItemCount(blood_jewel) >= 1) {
                if (spawned_keitnat_q0236b == 0) {
                    st.setMemoState("spawned_keitnat_q0236b", String.valueOf(1), true);
                    st.setMemoState("keitnat_q0236b_player_name", st.getPlayer().getName(), true);
                    NpcInstance keitnat_236b = st.addSpawn(keitnat_q0236b, st.getPlayer().getX() + 10, st.getPlayer().getY() + 10, st.getPlayer().getZ() + 10);
                    Functions.npcSay(keitnat_236b, NpcString.DID_YOU_BRING_WHAT_I_ASKED_S1, st.getPlayer().getName());
                    st.startQuestTimer("23613", 120000, keitnat_236b);
                    htmltext = "shadow_hardin_q0236_09b.htm";
                } else if (keitnat_q0236b_player_name == st.getPlayer().getName())
                    htmltext = "shadow_hardin_q0236_15b.htm";
                else
                    htmltext = "shadow_hardin_q0236_15bz.htm";
            } else if (event.equalsIgnoreCase("23611")) {
                if (npc != null && spawned_keitnat_q0236 == 1) {
                    st.setMemoState("spawned_keitnat_q0236", String.valueOf(0), true);
                    Functions.npcSay(npc, NpcString.HMM_WHERE_DID_MY_FRIEND_GO);
                }
                npc.deleteMe();
                return null;
            } else if (event.equalsIgnoreCase("23612")) {
                if (npc != null && spawned_keitnat_q0236a == 1) {
                    st.setMemoState("spawned_keitnat_q0236a", String.valueOf(0), true);
                    Functions.npcSay(npc, NpcString.HMM_WHERE_DID_MY_FRIEND_GO);
                }
                npc.deleteMe();
                return null;
            } else if (event.equalsIgnoreCase("23613")) {
                if (npc != null && spawned_keitnat_q0236b == 1) {
                    st.setMemoState("spawned_keitnat_q0236b", String.valueOf(0), true);
                    Functions.npcSay(npc, NpcString.HMM_WHERE_DID_MY_FRIEND_GO);
                }
                npc.deleteMe();
                return null;
            }
        } else if (npcId == keitnat_q0236) {
            if (event.equalsIgnoreCase("reply_6") && GetMemoState == 6) {
                st.setCond(5);
                st.setMemoState("seed_of_chaos", String.valueOf(20), true);
                st.setMemoState("seed_of_chaos_ex", String.valueOf(0), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "keitnat_q0236_q0236_09a.htm";
            } else if (event.equalsIgnoreCase("reply_8") && GetMemoState == 20 && GetMemoStateEx == 0) {
                if (st.isRunningQuestTimer("23611"))
                    st.cancelQuestTimer("23611");
                if (npc != null && spawned_keitnat_q0236 == 1) {
                    st.setMemoState("spawned_keitnat_q0236", String.valueOf(0), true);
                    Functions.npcSay(npc, NpcString.BEST_OF_LUCK_WITH_YOUR_FUTURE_ENDEAVOURS);
                }
                npc.deleteMe();
                return null;
            }
        } else if (npcId == keitnat_q0236a) {
            if (event.equalsIgnoreCase("reply_5") && GetMemoState == 3 && GetMemoStateEx == 2) {
                st.setCond(8);
                st.setMemoState("seed_of_chaos", String.valueOf(7), true);
                st.setMemoState("seed_of_chaos_ex", String.valueOf(0), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "keitnat_q0236a_q0236_05b.htm";
            } else if (event.equalsIgnoreCase("reply_50")) {
                if (st.isRunningQuestTimer("23612"))
                    st.cancelQuestTimer("23612");
                if (npc != null && spawned_keitnat_q0236a == 1) {
                    st.setMemoState("spawned_keitnat_q0236a", String.valueOf(0), true);
                    Functions.npcSay(npc, NpcString.BEST_OF_LUCK_WITH_YOUR_FUTURE_ENDEAVOURS);
                }
                npc.deleteMe();
                return null;
            }
        } else if (npcId == keitnat_q0236b) {
            if (event.equalsIgnoreCase("reply_99") && GetMemoState == 11 && st.ownItemCount(blood_jewel) >= 1) {
                if (st.isRunningQuestTimer("23613"))
                    st.cancelQuestTimer("23613");
                if (npc != null && spawned_keitnat_q0236b == 1) {
                    st.setMemoState("spawned_keitnat_q0236a", String.valueOf(0), true);
                    Functions.npcSay(npc, NpcString.BEST_OF_LUCK_WITH_YOUR_FUTURE_ENDEAVOURS);
                }
                npc.deleteMe();
                return null;
            }
        } else if (npcId == stone_q0236) {
            if (event.equalsIgnoreCase("reply_1") && GetMemoState == 20) {
                if (spawned_harlkil_q0236 == 0) {
                    st.setMemoState("spawned_harlkil_q0236", String.valueOf(1), true);
                    st.setMemoState("harlkil_q0236_player_name", st.getPlayer().getName(), true);
                    NpcInstance harlkil_236 = st.addSpawn(harlkil_q0236, 71722, -78853, -4464);
                    Functions.npcSay(harlkil_236, NpcString.HMM_IS_SOMEONE_APPROACHING);
                    st.startQuestTimer("23614", 120000, harlkil_236);
                    htmltext = "stone_q0236_q0236_02.htm";
                } else if (harlkil_q0236_player_name == st.getPlayer().getName())
                    htmltext = "stone_q0236_q0236_03.htm";
                else
                    htmltext = "stone_q0236_q0236_04z.htm";
            } else if (event.equalsIgnoreCase("reply_2") && GetMemoState == 22) {
                if (spawned_harlkil_q0236a == 0) {
                    st.setMemoState("spawned_harlkil_q0236a", String.valueOf(1), true);
                    st.setMemoState("harlkil_q0236a_player_name", st.getPlayer().getName(), true);
                    NpcInstance harlkil_236a = st.addSpawn(harlkil_q0236a, 71722, -78853, -4464);
                    Functions.npcSay(harlkil_236a, NpcString.S1_HAS_EVERYTHING_BEEN_FOUND, st.getPlayer().getName());
                    st.startQuestTimer("23615", 120000, harlkil_236a);
                    htmltext = "stone_q0236_q0236_06.htm";
                } else if (harlkil_q0236a_player_name == st.getPlayer().getName())
                    htmltext = "stone_q0236_q0236_07.htm";
                else
                    htmltext = "stone_q0236_q0236_08.htm";
            } else if (event.equalsIgnoreCase("23614")) {
                if (npc != null && spawned_harlkil_q0236 == 1) {
                    st.setMemoState("spawned_harlkil_q0236", String.valueOf(0), true);
                    Functions.npcSay(npc, NpcString.GRAAAH_WERE_BEING_ATTACKED);
                }
                npc.deleteMe();
                return null;
            } else if (event.equalsIgnoreCase("23615")) {
                if (npc != null && spawned_harlkil_q0236a == 1) {
                    st.setMemoState("spawned_harlkil_q0236a", String.valueOf(0), true);
                    Functions.npcSay(npc, NpcString.GRAAAH_WERE_BEING_ATTACKED);
                }
                npc.deleteMe();
                return null;
            }
        } else if (npcId == harlkil_q0236) {
            if (event.equalsIgnoreCase("reply_2") && GetMemoState == 20) {
                if (harlkil_q0236_player_name == st.getPlayer().getName()) {
                    st.setCond(12);
                    st.setMemoState("seed_of_chaos", String.valueOf(21), true);
                    st.setMemoState("seed_of_chaos_ex", String.valueOf(0), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "harlkil_q0236_q0236_07.htm";
                }
            } else if (event.equalsIgnoreCase("reply_3") && GetMemoState == 21) {
                if (st.isRunningQuestTimer("23614"))
                    st.cancelQuestTimer("23614");
                if (npc != null && spawned_harlkil_q0236 == 1) {
                    st.setMemoState("spawned_harlkil_q0236", String.valueOf(0), true);
                    Functions.npcSay(npc, NpcString.IN_THAT_CASE_I_WISH_YOU_GOOD_LUCK);
                }
                npc.deleteMe();
                return null;
            }
        } else if (npcId == harlkil_q0236a) {
            if (event.equalsIgnoreCase("reply_4") && GetMemoState == 22) {
                if (harlkil_q0236a_player_name == st.getPlayer().getName()) {
                    st.setCond(14);
                    st.setMemoState("seed_of_chaos", String.valueOf(30), true);
                    st.takeItems(medal_of_light, -1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "harlkil_q0236a_q0236_17.htm";
                }
            } else if (event.equalsIgnoreCase("reply_5") && GetMemoState == 30) {
                if (st.isRunningQuestTimer("23615"))
                    st.cancelQuestTimer("23615");
                if (npc != null && spawned_harlkil_q0236a == 1) {
                    st.setMemoState("spawned_harlkil_q0236a", String.valueOf(0), true);
                    Functions.npcSay(npc, NpcString.SAFE_TRAVELS);
                }
                npc.deleteMe();
                return null;
            }
        } else if (npcId == subelder_maotric) {
            if (event.equalsIgnoreCase("reply_1") && GetMemoState >= 40 && GetMemoState <= 45) {
                st.setCond(16);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "subelder_maotric_q0236_02.htm";
                InstantZone_Enter(st.getPlayer());
            }
        } else if (npcId == roden_q0236) {
            if (event.equalsIgnoreCase("reply_1") && GetMemoState == 40)
                htmltext = "roden_q0236_q0236_10.htm";
            else if (event.equalsIgnoreCase("reply_2") && GetMemoState == 40) {
                st.setCond(17);
                st.setMemoState("seed_of_chaos", String.valueOf(42), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "roden_q0236_q0236_11.htm";
            } else if (event.equalsIgnoreCase("reply_4") && GetMemoState == 43) {
                st.setCond(19);
                st.setMemoState("seed_of_chaos", String.valueOf(44), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "roden_q0236_q0236_13.htm";
            } else if (event.equalsIgnoreCase("reply_5"))
                htmltext = "roden_q0236_q0236_16.htm";
            else if (event.equalsIgnoreCase("reply_6") && GetMemoState == 45) {
                st.giveItems(scrl_of_ench_wp_a, 1);
                st.takeItems(q_star_of_destiny, -1);
                st.removeMemo("seed_of_chaos");
                st.removeMemo("seed_of_chaos_ex");
                st.removeMemo("spawned_keitnat_q0236");
                st.removeMemo("spawned_keitnat_q0236a");
                st.removeMemo("spawned_keitnat_q0236b");
                st.removeMemo("spawned_harlkil_q0236");
                st.removeMemo("spawned_harlkil_q0236a");
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(false);
                htmltext = "roden_q0236_q0236_17.htm";
            }
        } else if (npcId == mother_nornil_q0236) {
            if (event.equalsIgnoreCase("reply_4") && GetMemoState == 42) {
                st.setCond(18);
                st.setMemoState("seed_of_chaos", String.valueOf(43), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "mother_nornil_q0236_q0236_04.htm";
            } else if (event.equalsIgnoreCase("reply_5"))
                htmltext = "mother_nornil_q0236_q0236_07.htm";
            else if (event.equalsIgnoreCase("reply_6") && GetMemoState == 44) {
                st.setCond(20);
                st.setMemoState("seed_of_chaos", String.valueOf(45), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "mother_nornil_q0236_q0236_08.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("seed_of_chaos");
        int GetMemoStateEx = st.getInt("seed_of_chaos_ex");
        String keitnat_q0236_player_name = st.get("keitnat_q0236_player_name");
        String keitnat_q0236a_player_name = st.get("keitnat_q0236a_player_name");
        String keitnat_q0236b_player_name = st.get("keitnat_q0236b_player_name");
        String harlkil_q0236_player_name = st.get("harlkil_q0236_player_name");
        String harlkil_q0236a_player_name = st.get("harlkil_q0236a_player_name");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == kekrops) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "kekrops_q0236_01z.htm";
                            st.exitQuest(true);
                            break;
                        case RACE:
                            htmltext = "kekrops_q0236_01y.htm";
                            st.exitQuest(true);
                            break;
                        case QUEST:
                            htmltext = "kekrops_q0236_01x.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "kekrops_q0236_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == kekrops) {
                    if (GetMemoState == 1)
                        htmltext = "kekrops_q0236_05.htm";
                    else if (GetMemoState == 30)
                        htmltext = "kekrops_q0236_06.htm";
                    else if (GetMemoState == 40)
                        htmltext = "kekrops_q0236_13.htm";
                } else if (npcId == shadow_hardin) {
                    if (GetMemoState == 1)
                        htmltext = "shadow_hardin_q0236_01.htm";
                    else if (GetMemoState == 2 && st.ownItemCount(dark_song_crystal) == 0)
                        htmltext = "shadow_hardin_q0236_06a.htm";
                    else if (GetMemoState == 2 && st.ownItemCount(dark_song_crystal) >= 1)
                        htmltext = "shadow_hardin_q0236_07a.htm";
                    else if (GetMemoState == 6)
                        htmltext = "shadow_hardin_q0236_10a.htm";
                    else if (GetMemoState == 20 && GetMemoStateEx == 0)
                        htmltext = "shadow_hardin_q0236_15a.htm";
                    else if (GetMemoState == 3 && GetMemoStateEx == 0) {
                        st.setMemoState("seed_of_chaos_ex", String.valueOf(1), true);
                        htmltext = "shadow_hardin_q0236_06b.htm";
                    } else if (GetMemoState == 3 && GetMemoStateEx == 1) {
                        st.setCond(7);
                        st.setMemoState("seed_of_chaos_ex", String.valueOf(2), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "shadow_hardin_q0236_07b.htm";
                    } else if (GetMemoState == 3 && GetMemoStateEx == 2)
                        htmltext = "shadow_hardin_q0236_08b.htm";
                    else if (GetMemoState == 7 && st.ownItemCount(blood_jewel) == 0)
                        htmltext = "shadow_hardin_q0236_12b.htm";
                    else if (GetMemoState == 7 && st.ownItemCount(blood_jewel) >= 1)
                        htmltext = "shadow_hardin_q0236_13b.htm";
                    else if (GetMemoState == 11)
                        htmltext = "shadow_hardin_q0236_16b.htm";
                } else if (npcId == keitnat_q0236) {
                    if (GetMemoState == 6) {
                        if (keitnat_q0236_player_name == st.getPlayer().getName())
                            htmltext = "keitnat_q0236_q0236_01a.htm";
                        else
                            htmltext = "keitnat_q0236_q0236_01z.htm";
                    } else if (GetMemoState == 20 && GetMemoStateEx == 0)
                        htmltext = "keitnat_q0236_q0236_09z.htm";
                } else if (npcId == keitnat_q0236a) {
                    if (GetMemoState == 3 && GetMemoStateEx == 2) {
                        if (keitnat_q0236a_player_name == st.getPlayer().getName())
                            htmltext = "keitnat_q0236a_q0236_01b.htm";
                        else
                            htmltext = "keitnat_q0236a_q0236_01z.htm";
                    } else if (GetMemoState == 7 && st.ownItemCount(blood_jewel) == 0)
                        htmltext = "keitnat_q0236a_q0236_05z.htm";
                } else if (npcId == keitnat_q0236b) {
                    if (GetMemoState == 7 && st.ownItemCount(blood_jewel) >= 1) {
                        if (keitnat_q0236b_player_name == st.getPlayer().getName()) {
                            st.setCond(10);
                            st.setMemoState("seed_of_chaos", String.valueOf(11), true);
                            st.soundEffect(SOUND_MIDDLE);
                            htmltext = "keitnat_q0236b_q0236_06bz.htm";
                        } else {
                            st.setCond(10);
                            st.setMemoState("seed_of_chaos", String.valueOf(11), true);
                            st.soundEffect(SOUND_MIDDLE);
                            htmltext = "keitnat_q0236b_q0236_06b.htm";
                        }
                    } else if (GetMemoState == 11)
                        htmltext = "keitnat_q0236b_q0236_06b.htm";
                } else if (npcId == stone_q0236) {
                    if (GetMemoState == 20)
                        htmltext = "stone_q0236_q0236_01.htm";
                    else if (GetMemoState == 21)
                        htmltext = "stone_q0236_q0236_04.htm";
                    else if (GetMemoState == 22)
                        htmltext = "stone_q0236_q0236_05.htm";
                    else if (GetMemoState == 30)
                        htmltext = "stone_q0236_q0236_09.htm";
                } else if (npcId == harlkil_q0236) {
                    if (GetMemoState == 20) {
                        if (harlkil_q0236_player_name == st.getPlayer().getName())
                            htmltext = "harlkil_q0236_q0236_01.htm";
                        else
                            htmltext = "harlkil_q0236_q0236_02.htm";
                    } else if (GetMemoState == 21)
                        htmltext = "harlkil_q0236_q0236_07z.htm";
                    else if (GetMemoState == 22)
                        htmltext = "harlkil_q0236_q0236_08z.htm";
                } else if (npcId == harlkil_q0236a) {
                    if (GetMemoState == 22) {
                        if (harlkil_q0236a_player_name == st.getPlayer().getName())
                            htmltext = "harlkil_q0236a_q0236_08.htm";
                        else
                            htmltext = "harlkil_q0236a_q0236_09.htm";
                    } else if (GetMemoState == 30)
                        htmltext = "harlkil_q0236a_q0236_18.htm";
                } else if (npcId == subelder_maotric) {
                    if (GetMemoState >= 40 && GetMemoState <= 45)
                        htmltext = "subelder_maotric_q0236_01.htm";
                } else if (npcId == roden_q0236) {
                    if (GetMemoState == 40)
                        htmltext = "roden_q0236_q0236_01.htm";
                    else if (GetMemoState == 42)
                        htmltext = "roden_q0236_q0236_11a.htm";
                    else if (GetMemoState == 43)
                        htmltext = "roden_q0236_q0236_12.htm";
                    else if (GetMemoState == 44)
                        htmltext = "roden_q0236_q0236_14.htm";
                    else if (GetMemoState == 45)
                        htmltext = "roden_q0236_q0236_15.htm";
                } else if (npcId == mother_nornil_q0236) {
                    if (GetMemoState == 40)
                        htmltext = "mother_nornil_q0236_q0236_01.htm";
                    else if (GetMemoState == 42)
                        htmltext = "mother_nornil_q0236_q0236_02.htm";
                    else if (GetMemoState == 43)
                        htmltext = "mother_nornil_q0236_q0236_05.htm";
                    else if (GetMemoState == 44)
                        htmltext = "mother_nornil_q0236_q0236_06.htm";
                    else if (GetMemoState == 45)
                        htmltext = "mother_nornil_q0236_q0236_09.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("seed_of_chaos");
        int npcId = npc.getNpcId();
        if (npcId == needle_stakato_drone) {
            if (GetMemoState == 2 && st.ownItemCount(dark_song_crystal) == 0) {
                int i0 = Rnd.get(100);
                if (i0 < 20) {
                    st.setCond(3);
                    st.giveItems(dark_song_crystal, 1);
                    st.soundEffect(SOUND_MIDDLE);
                }
            }
        } else if (npcId == vampire_wizard_a) {
            if (GetMemoState == 7 && st.ownItemCount(blood_jewel) == 0) {
                int i0 = Rnd.get(100);
                if (i0 < 8) {
                    st.setCond(9);
                    st.giveItems(blood_jewel, 1);
                    st.soundEffect(SOUND_MIDDLE);
                }
            }
        } else if (npcId == brilliant_cry || npcId == brilliant_will || npcId == brilliant_will_1 || npcId == brilliant_mark || npcId == brilliant_crown || npcId == brilliant_fang || npcId == brilliant_fang_1 || npcId == brilliant_anguish || npcId == brilliant_anguish_1) {
            if (GetMemoState == 21 && st.ownItemCount(medal_of_light) < 62) {
                int i0 = Rnd.get(100);
                if (i0 < 70) {
                    st.giveItems(medal_of_light, 1);
                    if (st.ownItemCount(medal_of_light) >= 61) {
                        st.setCond(13);
                        st.setMemoState("seed_of_chaos", String.valueOf(22), true);
                        st.soundEffect(SOUND_MIDDLE);
                    } else
                        st.soundEffect(SOUND_ITEMGET);
                }
            }
        }
        return null;
    }

    private void InstantZone_Enter(Player player) {
        ReflectionUtils.simpleEnterInstancedZone(player, _236_SeedsOfChaos.inzone_id);
    }
}