package org.mmocore.gameserver.scripts.quests;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.gameserver.model.base.ClassLevel;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.0
 * @date 22/02/2015
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _179_IntoTheLargeCavern extends Quest {
    // npc
    private static final int kekrops = 32138;
    private static final int mother_nornil = 32258;
    private static final int future_door_cont = 32262;
    private static final int past_door_cont = 32260;
    private static final int present_door_cont = 32261;
    // etcitem
    private static final int puma_skin_shirt_q = 10021;
    private static final int puma_skin_gaiters_q = 10022;
    private static final int red_cresent_earing_q = 10122;
    private static final int necklace_of_devotion_q = 10123;
    private static final int ring_of_devotion_q = 10124;

    public _179_IntoTheLargeCavern() {
        super(true);
        addStartNpc(kekrops);
        addTalkId(mother_nornil, future_door_cont, past_door_cont, present_door_cont);
        addLevelCheck(17, 21);
        addRaceCheck(PlayerRace.kamael);
        addQuestCompletedCheck(178);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("into_the_big_cave");
        int GetMemoStateEx = st.getInt("into_the_big_cave_ex");
        int npcId = npc.getNpcId();
        if (npcId == kekrops) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("into_the_big_cave", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "kekrops_q0179_06.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=179&reply=1"))
                htmltext = "kekrops_q0179_05.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=179&reply=2")) {
                if (GetMemoState == 1)
                    htmltext = "kekrops_q0179_08.htm";
            }
        } else if (npcId == mother_nornil) {
            if (event.equalsIgnoreCase("menu_select?ask=179&reply=1")) {
                if (GetMemoState == 1)
                    htmltext = "mother_nornil_q0179_02.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=179&reply=2")) {
                if (GetMemoState == 1)
                    htmltext = "mother_nornil_q0179_03.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=179&reply=3")) {
                if (GetMemoState == 1)
                    htmltext = "mother_nornil_q0179_04.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=179&reply=4")) {
                if (GetMemoState == 1)
                    htmltext = "mother_nornil_q0179_05.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=179&reply=5")) {
                if (GetMemoState == 1)
                    htmltext = "mother_nornil_q0179_06.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=179&reply=6")) {
                if (GetMemoState == 1)
                    htmltext = "mother_nornil_q0179_07.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=179&reply=7")) {
                st.getPlayer().getReflection().collapse();
                st.exitQuest(false);
                return null;
            } else if (event.equalsIgnoreCase("menu_select?ask=179&reply=9")) {
                if (GetMemoState == 1) {
                    st.giveItems(puma_skin_shirt_q, 1);
                    st.giveItems(puma_skin_gaiters_q, 1);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "mother_nornil_q0179_08.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=179&reply=10")) {
                if (GetMemoState == 1) {
                    st.giveItems(red_cresent_earing_q, 2);
                    st.giveItems(necklace_of_devotion_q, 1);
                    st.giveItems(ring_of_devotion_q, 2);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "mother_nornil_q0179_09.htm";
                }
            }
        } else if (npcId == future_door_cont) {
            if (event.equalsIgnoreCase("menu_select?ask=179&reply=1")) {
                if (GetMemoState == 1) {
                    st.setMemoState("into_the_big_cave_ex", String.valueOf(0), true);
                    htmltext = "future_door_cont_q0179_03.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=179&reply=2")) {
                if (GetMemoState == 1) {
                    st.setMemoState("into_the_big_cave_ex", String.valueOf(GetMemoStateEx + 1), true);
                    htmltext = "future_door_cont_q0179_04.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=179&reply=3")) {
                if (GetMemoState == 1) {
                    st.setMemoState("into_the_big_cave_ex", String.valueOf(GetMemoStateEx + 10), true);
                    htmltext = "future_door_cont_q0179_05.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=179&reply=4")) {
                if (GetMemoState == 1) {
                    st.setMemoState("into_the_big_cave_ex", String.valueOf(GetMemoStateEx + 100), true);
                    htmltext = "future_door_cont_q0179_06.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=179&reply=5")) {
                if (GetMemoState == 1) {
                    st.setMemoState("into_the_big_cave_ex", String.valueOf(GetMemoStateEx + 1000), true);
                    htmltext = "future_door_cont_q0179_07.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=179&reply=6")) {
                if (GetMemoState == 1) {
                    if (GetMemoStateEx == 1111) {
                        st.getPlayer().getReflection().openDoor(16200016);
                        st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.CHAOS_AND_TIME, 1500, ScreenMessageAlign.TOP_CENTER, true));
                        st.setMemoState("into_the_big_cave_ex", String.valueOf(0), true);
                        return null;
                    } else if (GetMemoStateEx != 1111)
                        htmltext = "future_door_cont_q0179_08.htm";
                }
            }
        } else if (npcId == past_door_cont) {
            if (event.equalsIgnoreCase("menu_select?ask=179&reply=1")) {
                if (GetMemoState == 1) {
                    st.setMemoState("into_the_big_cave_ex", String.valueOf(0), true);
                    htmltext = "past_door_cont_q0179_03.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=179&reply=2")) {
                if (GetMemoState == 1) {
                    st.setMemoState("into_the_big_cave_ex", String.valueOf(GetMemoStateEx + 1), true);
                    htmltext = "past_door_cont_q0179_04.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=179&reply=3")) {
                if (GetMemoState == 1) {
                    st.setMemoState("into_the_big_cave_ex", String.valueOf(GetMemoStateEx + 10), true);
                    htmltext = "past_door_cont_q0179_05.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=179&reply=4")) {
                if (GetMemoState == 1) {
                    st.setMemoState("into_the_big_cave_ex", String.valueOf(GetMemoStateEx + 100), true);
                    htmltext = "past_door_cont_q0179_06.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=179&reply=5")) {
                if (GetMemoState == 1) {
                    if (GetMemoStateEx == 111) {
                        st.getPlayer().getReflection().openDoor(16200014);
                        st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.THE_VEILED_CREATOR, 1500, ScreenMessageAlign.TOP_CENTER, true));
                        st.setMemoState("into_the_big_cave_ex", String.valueOf(0), true);
                        return null;
                    } else if (GetMemoStateEx != 111)
                        htmltext = "past_door_cont_q0179_07.htm";
                }
            }
        } else if (npcId == present_door_cont) {
            if (event.equalsIgnoreCase("menu_select?ask=179&reply=1")) {
                if (GetMemoState == 1) {
                    st.setMemoState("into_the_big_cave_ex", String.valueOf(0), true);
                    htmltext = "present_door_cont_q0179_03.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=179&reply=2")) {
                if (GetMemoState == 1) {
                    st.setMemoState("into_the_big_cave_ex", String.valueOf(GetMemoStateEx + 1), true);
                    htmltext = "present_door_cont_q0179_04.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=179&reply=3")) {
                if (GetMemoState == 1) {
                    st.setMemoState("into_the_big_cave_ex", String.valueOf(GetMemoStateEx + 10), true);
                    htmltext = "present_door_cont_q0179_05.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=179&reply=4")) {
                if (GetMemoState == 1) {
                    st.setMemoState("into_the_big_cave_ex", String.valueOf(GetMemoStateEx + 100), true);
                    htmltext = "present_door_cont_q0179_06.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=179&reply=7")) {
                if (GetMemoState == 1) {
                    if (GetMemoStateEx == 111) {
                        st.getPlayer().getReflection().openDoor(16200015);
                        st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.THE_CONSPIRACY_OF_THE_ANCIENT_RACE, 1500, ScreenMessageAlign.TOP_CENTER, true));
                        st.setMemoState("into_the_big_cave_ex", String.valueOf(0), true);
                        return null;
                    } else if (GetMemoStateEx != 111)
                        htmltext = "present_door_cont_q0179_07.htm";
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("into_the_big_cave");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == kekrops) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            if (!st.getPlayer().getPlayerClassComponent().getClassId().isOfLevel(ClassLevel.First)) {
                                htmltext = "kekrops_q0179_02a.htm";
                                st.exitQuest(true);
                            } else {
                                htmltext = "kekrops_q0179_02.htm";
                                st.exitQuest(true);
                            }
                            break;
                        case RACE:
                            htmltext = "kekrops_q0179_04.htm";
                            st.exitQuest(true);
                            break;
                        case QUEST:
                            htmltext = "kekrops_q0179_03.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "kekrops_q0179_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == kekrops) {
                    if (GetMemoState == 1)
                        htmltext = "kekrops_q0179_07.htm";
                } else if (npcId == mother_nornil) {
                    if (GetMemoState == 1)
                        htmltext = "mother_nornil_q0179_01.htm";
                } else if (npcId == future_door_cont) {
                    if (GetMemoState == 1) {
                        st.setMemoState("into_the_big_cave_ex", String.valueOf(0), true);
                        htmltext = "future_door_cont_q0179_01.htm";
                    }
                } else if (npcId == past_door_cont) {
                    if (GetMemoState == 1) {
                        st.setMemoState("into_the_big_cave_ex", String.valueOf(0), true);
                        htmltext = "past_door_cont_q0179_01.htm";
                    }
                } else if (npcId == present_door_cont) {
                    if (GetMemoState == 1) {
                        st.setMemoState("into_the_big_cave_ex", String.valueOf(0), true);
                        htmltext = "present_door_cont_q0179_01.htm";
                    }
                }
                break;
        }
        return htmltext;
    }
}