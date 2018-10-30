package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.ReflectionUtils;

/**
 * Based on official High Five
 *
 * @author Magister
 * @version 1.0
 * @date 13/01/2015
 * @tested OK
 */
public class _10293_SevenSignsForbiddenBook extends Quest {
    // npc
    private static final int ssq2_elcardia_home1 = 32784;
    private static final int ssq2_elcardia_library1 = 32785;
    private static final int director_sophia = 32596;
    private static final int ssq2_director_sophia2 = 32861;
    private static final int ssq2_director_sophia3 = 32863;
    private static final int q10293_ssq2_solina_bio = 17213;
    private static final int ssq2_cl1_library = 32809;
    private static final int ssq2_cl2_library = 32810;
    private static final int ssq2_cl3_library = 32811;
    private static final int ssq2_cl4_library = 32812;
    private static final int ssq2_cl5_library = 32813;

    public _10293_SevenSignsForbiddenBook() {
        super(false);
        addStartNpc(ssq2_elcardia_home1);
        addTalkId(director_sophia, ssq2_director_sophia2, ssq2_elcardia_library1, ssq2_director_sophia3, ssq2_cl1_library, ssq2_cl2_library, ssq2_cl3_library, ssq2_cl4_library, ssq2_cl5_library);
        addQuestItem(q10293_ssq2_solina_bio);
        addLevelCheck(81);
        addQuestCompletedCheck(10292);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("ssq2_forbidden_book");
        int npcId = npc.getNpcId();
        if (npcId == ssq2_elcardia_home1) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("ssq2_forbidden_book", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "ssq2_elcardia_home1_q10293_04.htm";
            } else if (event.equalsIgnoreCase("reply_1"))
                htmltext = "ssq2_elcardia_home1_q10293_03.htm";
            else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 9)
                    htmltext = "ssq2_elcardia_home1_q10293_08.htm";
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 9) {
                    if (!st.getPlayer().getPlayerClassComponent().isBaseExactlyActiveId())
                        htmltext = "ssq2_elcardia_home1_q10293_10.htm";
                    else {
                        st.addExpAndSp(15000000, 1500000);
                        st.removeMemo("ssq2_forbidden_book");
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                        htmltext = "ssq2_elcardia_home1_q10293_09.htm";
                    }
                }
            }
        } else if (npcId == director_sophia) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState < 9)
                    htmltext = "director_sophia_q10293_03.htm";
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState < 9)
                    htmltext = "director_sophia_q10293_04.htm";
            } else if (event.equalsIgnoreCase("reply_3")) {
                InstantZone_Enter(st.getPlayer(), 156);
                return null;
            }
        } else if (npcId == ssq2_elcardia_library1) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 1)
                    htmltext = "ssq2_elcardia_library1_q10293_02.htm";
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 4) {
                    st.setCond(5);
                    st.setMemoState("ssq2_forbidden_book", String.valueOf(5), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "ssq2_elcardia_library1_q10293_03.htm";
                }
            }
        } else if (npcId == ssq2_director_sophia2) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 1)
                    htmltext = "ssq2_director_sophia2_q10293_02.htm";
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 1)
                    htmltext = "ssq2_director_sophia2_q10293_03.htm";
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 1) {
                    st.setCond(2);
                    st.setMemoState("ssq2_forbidden_book", String.valueOf(2), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "ssq2_director_sophia2_q10293_04.htm";
                }
            } else if (event.equalsIgnoreCase("reply_4")) {
                if (GetMemoState == 3)
                    htmltext = "ssq2_director_sophia2_q10293_07.htm";
            } else if (event.equalsIgnoreCase("reply_5")) {
                if (GetMemoState == 3) {
                    st.setCond(4);
                    st.setMemoState("ssq2_forbidden_book", String.valueOf(4), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "ssq2_director_sophia2_q10293_08.htm";
                }
            } else if (event.equalsIgnoreCase("reply_6")) {
                if (GetMemoState == 5) {
                    st.setCond(6);
                    st.setMemoState("ssq2_forbidden_book", String.valueOf(6), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "ssq2_director_sophia2_q10293_11.htm";
                }
            } else if (event.equalsIgnoreCase("reply_7")) {
                Location loc = new Location(37348, -50383, -1164);
                st.getPlayer().teleToLocation(loc);
                teleportElcardia(st.getPlayer());
                return null;
            }
        } else if (npcId == ssq2_director_sophia3) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState > 5 && GetMemoState < 9)
                    htmltext = "ssq2_director_sophia3_q10293_02.htm";
            } else if (event.equalsIgnoreCase("reply_2")) {
                Location loc = new Location(37097, -49828, -1128);
                st.getPlayer().teleToLocation(loc);
                teleportElcardia(st.getPlayer());
                return null;
            }
        } else if (npcId == ssq2_cl1_library) {
            if (event.equalsIgnoreCase("reply_1")) {
                st.setCond(7);
                st.setMemoState("ssq2_forbidden_book", String.valueOf(8), true);
                if (st.ownItemCount(q10293_ssq2_solina_bio) == 0)
                    st.giveItems(q10293_ssq2_solina_bio, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "ssq2_cl1_library_q10293_03.htm";
            }
        } else if (npcId == ssq2_cl2_library) {
            if (event.equalsIgnoreCase("reply_1"))
                htmltext = "ssq2_cl2_library_q10293_04.htm";
        } else if (npcId == ssq2_cl3_library) {
            if (event.equalsIgnoreCase("reply_1"))
                htmltext = "ssq2_cl3_library_q10293_04.htm";
        } else if (npcId == ssq2_cl4_library) {
            if (event.equalsIgnoreCase("reply_1"))
                htmltext = "ssq2_cl4_library_q10293_04.htm";
        } else if (npcId == ssq2_cl5_library) {
            if (event.equalsIgnoreCase("reply_1"))
                htmltext = "ssq2_cl5_library_q10293_04.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("ssq2_forbidden_book");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == ssq2_elcardia_home1) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case QUEST:
                        case LEVEL:
                            htmltext = "ssq2_elcardia_home1_q10293_00.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "ssq2_elcardia_home1_q10293_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == ssq2_elcardia_home1) {
                    if (GetMemoState == 1 && GetMemoState < 9)
                        htmltext = "ssq2_elcardia_home1_q10293_06.htm";
                    else if (GetMemoState == 9)
                        htmltext = "ssq2_elcardia_home1_q10293_07.htm";
                } else if (npcId == director_sophia) {
                    if (GetMemoState == 1)
                        htmltext = "director_sophia_q10293_01.htm";
                    else if (GetMemoState > 1 && GetMemoState < 9)
                        htmltext = "director_sophia_q10293_02.htm";
                    else if (GetMemoState > 8)
                        htmltext = "director_sophia_q10293_05.htm";
                } else if (npcId == ssq2_elcardia_library1) {
                    if (GetMemoState == 1)
                        htmltext = "ssq2_elcardia_library1_q10293_01.htm";
                    else if (GetMemoState == 2) {
                        st.setCond(3);
                        st.setMemoState("ssq2_forbidden_book", String.valueOf(3), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "ssq2_elcardia_library1_q10293_03.htm";
                    } else if (GetMemoState == 3)
                        htmltext = "ssq2_elcardia_library1_q10293_08.htm";
                    else if (GetMemoState == 4)
                        htmltext = "ssq2_elcardia_library1_q10293_04.htm";
                    else if (GetMemoState == 5)
                        htmltext = "ssq2_elcardia_library1_q10293_06.htm";
                    else if (GetMemoState == 6) {
                        st.setMemoState("ssq2_forbidden_book", String.valueOf(7), true);
                        htmltext = "ssq2_elcardia_library1_q10293_09.htm";
                    } else if (GetMemoState == 7)
                        htmltext = "ssq2_elcardia_library1_q10293_10.htm";
                    else if (GetMemoState == 8) {
                        st.setCond(8);
                        st.setMemoState("ssq2_forbidden_book", String.valueOf(9), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "ssq2_elcardia_library1_q10293_11.htm";
                    } else if (GetMemoState == 9)
                        htmltext = "ssq2_elcardia_library1_q10293_12.htm";
                } else if (npcId == ssq2_director_sophia2) {
                    if (GetMemoState == 1)
                        htmltext = "ssq2_director_sophia2_q10293_01.htm";
                    else if (GetMemoState == 2)
                        htmltext = "ssq2_director_sophia2_q10293_05.htm";
                    else if (GetMemoState == 3)
                        htmltext = "ssq2_director_sophia2_q10293_06.htm";
                    else if (GetMemoState == 4)
                        htmltext = "ssq2_director_sophia2_q10293_09.htm";
                    else if (GetMemoState == 5)
                        htmltext = "ssq2_director_sophia2_q10293_10.htm";
                    else if (GetMemoState > 5 && GetMemoState < 9)
                        htmltext = "ssq2_director_sophia2_q10293_12.htm";
                    else if (GetMemoState > 8)
                        htmltext = "ssq2_director_sophia2_q10293_14.htm";
                } else if (npcId == ssq2_director_sophia3) {
                    if (GetMemoState > 5 && GetMemoState < 9)
                        htmltext = "ssq2_director_sophia3_q10293_01.htm";
                    else if (GetMemoState == 9)
                        htmltext = "ssq2_director_sophia3_q10293_04.htm";
                } else if (npcId == ssq2_cl1_library) {
                    if (GetMemoState == 6 || GetMemoState == 7)
                        htmltext = "ssq2_cl1_library_q10293_01.htm";
                    else if (GetMemoState < 6)
                        htmltext = "ssq2_cl1_library_q10293_02.htm";
                } else if (npcId == ssq2_cl2_library) {
                    if (GetMemoState == 6 || GetMemoState == 7)
                        htmltext = "ssq2_cl2_library_q10293_01.htm";
                    else if (GetMemoState < 6)
                        htmltext = "ssq2_cl2_library_q10293_02.htm";
                } else if (npcId == ssq2_cl3_library) {
                    if (GetMemoState == 6 || GetMemoState == 7)
                        htmltext = "ssq2_cl3_library_q10293_01.htm";
                    else if (GetMemoState < 6)
                        htmltext = "ssq2_cl3_library_q10293_02.htm";
                } else if (npcId == ssq2_cl4_library) {
                    if (GetMemoState == 6 || GetMemoState == 7)
                        htmltext = "ssq2_cl4_library_q10293_01.htm";
                    else if (GetMemoState < 6)
                        htmltext = "ssq2_cl4_library_q10293_02.htm";
                } else if (npcId == ssq2_cl5_library) {
                    if (GetMemoState == 6 || GetMemoState == 7)
                        htmltext = "ssq2_cl5_library_q10293_01.htm";
                    else if (GetMemoState < 6)
                        htmltext = "ssq2_cl5_library_q10293_02.htm";
                }
                break;
            case COMPLETED:
                if (npcId == ssq2_elcardia_home1)
                    htmltext = "ssq2_elcardia_home1_q10293_02.htm";
                break;
        }
        return htmltext;
    }

    private void InstantZone_Enter(Player player, int inzone_id) {
        ReflectionUtils.simpleEnterInstancedZone(player, inzone_id);
    }

    private void teleportElcardia(Player player) {
        for (NpcInstance n : player.getReflection().getNpcs()) {
            if (n.getNpcId() == ssq2_elcardia_library1) {
                n.teleToLocation(Location.findPointToStay(player, 60));
            }
        }
    }
}