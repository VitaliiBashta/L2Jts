package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.1
 * @date 31/03/2016
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _185_NikolasCooperation extends Quest {
    // npc
    private static final int maestro_nikola = 30621;
    private static final int researcher_lorain = 30673;
    private static final int broken_controller = 32366;
    private static final int alarm_of_giant_q0184 = 32367;
    // questitem
    private static final int q_slate_of_metal_q0185 = 10363;
    private static final int q_fragment_q0185 = 10364;
    private static final int q_map_of_nikola_q0185 = 10365;
    private static final int q_certificate_of_lorain = 10362;
    // spawn memo
    private int myself_i_quest0 = 0;
    // player objectId
    private int myself_i_quest1 = 0;

    public _185_NikolasCooperation() {
        super(false);
        addStartNpc(maestro_nikola);
        addTalkId(researcher_lorain, broken_controller, alarm_of_giant_q0184);
        addQuestItem(q_map_of_nikola_q0185, q_fragment_q0185, q_slate_of_metal_q0185);
        addLevelCheck(40);
        addQuestCompletedCheck(183);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("care_of_nikola");
        int GetMemoStateEx = st.getInt("care_of_nikola_ex");
        int npcId = npc.getNpcId();
        if (npcId == maestro_nikola) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("care_of_nikola", String.valueOf(1), true);
                st.giveItems(q_map_of_nikola_q0185, 1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "maestro_nikola_q0185_06.htm";
            } else if (event.equalsIgnoreCase("reply=1")) {
                if (st.getPlayer().getLevel() >= 40)
                    htmltext = "maestro_nikola_q0185_03.htm";
                else {
                    htmltext = "maestro_nikola_q0185_03a.htm";
                    st.exitQuest(true);
                }
            } else if (event.equalsIgnoreCase("reply=2"))
                htmltext = "maestro_nikola_q0185_04.htm";
            else if (event.equalsIgnoreCase("reply=3"))
                htmltext = "maestro_nikola_q0185_05.htm";
        } else if (npcId == researcher_lorain) {
            if (event.equalsIgnoreCase("reply=1")) {
                if (GetMemoState == 1)
                    htmltext = "researcher_lorain_q0185_02.htm";
            } else if (event.equalsIgnoreCase("reply=2")) {
                if (GetMemoState == 1) {
                    st.setCond(2);
                    st.setMemoState("care_of_nikola", String.valueOf(2), true);
                    st.takeItems(q_map_of_nikola_q0185, -1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "researcher_lorain_q0185_03.htm";
                }
            } else if (event.equalsIgnoreCase("reply=3")) {
                if (GetMemoState == 2) {
                    st.setCond(3);
                    st.setMemoState("care_of_nikola", String.valueOf(3), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "researcher_lorain_q0185_05.htm";
                }
            } else if (event.equalsIgnoreCase("reply=4")) {
                if (GetMemoState == 6)
                    htmltext = "researcher_lorain_q0185_08.htm";
            } else if (event.equalsIgnoreCase("reply=5")) {
                if (GetMemoState == 6) {
                    if (st.ownItemCount(q_slate_of_metal_q0185) >= 1) {
                        st.giveItems(q_certificate_of_lorain, 1);
                        st.takeItems(q_slate_of_metal_q0185, -1);
                        st.removeMemo("care_of_nikola");
                        st.removeMemo("care_of_nikola_ex");
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                        htmltext = "researcher_lorain_q0185_09.htm";
                    } else {
                        st.giveItems(ADENA_ID, 72527);
                        if (st.getPlayer().getLevel() < 46)
                            st.addExpAndSp(203717, 14032);
                        st.takeItems(q_fragment_q0185, -1);
                        st.removeMemo("care_of_nikola");
                        st.removeMemo("care_of_nikola_ex");
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                        htmltext = "researcher_lorain_q0185_10.htm";
                    }
                }
            }
        } else if (npcId == broken_controller) {
            if (event.equalsIgnoreCase("reply=1")) {
                if (GetMemoState == 3) {
                    if (myself_i_quest0 == 0) {
                        myself_i_quest0 = 1;
                        myself_i_quest1 = st.getPlayer().getObjectId();
                        NpcInstance alarm_of_giant = st.addSpawn(alarm_of_giant_q0184, npc.getX() + 80, npc.getY() + 65, npc.getZ(), 16384, 0, 0);
                        Functions.npcSay(alarm_of_giant, NpcString.INTRUDER_ALERT_THE_ALARM_WILL_SELF_DESTRUCT_IN_2_MINUTES);
                        st.startQuestTimer("18401", 1000 * 60, alarm_of_giant);
                        st.soundEffect(SOUND_SYS_SIREN);
                        htmltext = "broken_controller_q0185_03.htm";
                    }
                }
            } else if (event.equalsIgnoreCase("reply=2")) {
                if (GetMemoState == 4) {
                    st.setCond(4);
                    st.setMemoState("care_of_nikola", String.valueOf(6), true);
                    st.giveItems(q_slate_of_metal_q0185, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "broken_controller_q0185_06.htm";
                }
            } else if (event.equalsIgnoreCase("reply=3")) {
                if (GetMemoState == 5) {
                    st.setCond(5);
                    st.setMemoState("care_of_nikola", String.valueOf(6), true);
                    st.giveItems(q_fragment_q0185, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "broken_controller_q0185_08.htm";
                }
            }
        } else if (npcId == alarm_of_giant_q0184) {
            if (event.equalsIgnoreCase("18404")) {
                if (npc != null && myself_i_quest0 == 1) {
                    myself_i_quest0 = 0;
                    myself_i_quest1 = 0;
                    st.setMemoState("care_of_nikola_ex", String.valueOf(0), true);
                    Functions.npcSay(npc, NpcString.RECORDER_CRUSHED);
                    npc.deleteMe();
                }
                return null;
            } else if (event.equalsIgnoreCase("18401")) {
                st.startQuestTimer("18402", 1000 * 30, npc);
                Functions.npcSay(npc, NpcString.THE_ALARM_WILL_SELF_DESTRUCT_IN_60_SECOND_ENTER_PASSCODE_TO_OVERRIDE);
                return null;
            } else if (event.equalsIgnoreCase("18402")) {
                st.startQuestTimer("18403", 1000 * 20, npc);
                Functions.npcSay(npc, NpcString.THE_ALARM_WILL_SELF_DESTRUCT_IN_30_SECOND_ENTER_PASSCODE_TO_OVERRIDE);
                return null;
            } else if (event.equalsIgnoreCase("18403")) {
                st.startQuestTimer("18404", 1000 * 10, npc);
                Functions.npcSay(npc, NpcString.THE_ALARM_WILL_SELF_DESTRUCT_IN_10_SECOND_ENTER_PASSCODE_TO_OVERRIDE);
                return null;
            } else if (event.equalsIgnoreCase("reply=2")) {
                if (GetMemoState == 3) {
                    if (myself_i_quest1 == st.getPlayer().getObjectId())
                        htmltext = "alarm_of_giant_q0184_q0185_02.htm";
                    else
                        htmltext = "alarm_of_giant_q0184002.htm";
                }
            } else if (event.equalsIgnoreCase("reply=3")) {
                if (GetMemoState == 3) {
                    st.setMemoState("care_of_nikola_ex", String.valueOf(1), true);
                    htmltext = "alarm_of_giant_q0184_q0185_04.htm";
                }
            } else if (event.equalsIgnoreCase("reply=4")) {
                if (GetMemoState == 3) {
                    st.setMemoState("care_of_nikola_ex", String.valueOf(GetMemoStateEx + 1), true);
                    htmltext = "alarm_of_giant_q0184_q0185_06.htm";
                }
            } else if (event.equalsIgnoreCase("reply=5")) {
                if (GetMemoState == 3) {
                    st.setMemoState("care_of_nikola_ex", String.valueOf(GetMemoStateEx + 1), true);
                    htmltext = "alarm_of_giant_q0184_q0185_08.htm";
                }
            } else if (event.equalsIgnoreCase("reply=6")) {
                if (GetMemoState == 3) {
                    if (GetMemoStateEx >= 3) {
                        if (npc != null && myself_i_quest0 == 1) {
                            myself_i_quest0 = 0;
                            myself_i_quest1 = 0;
                            st.setMemoState("care_of_nikola", String.valueOf(4), true);
                            htmltext = "alarm_of_giant_q0184_q0185_09.htm";
                            npc.deleteMe();
                        }
                    } else {
                        st.setMemoState("care_of_nikola_ex", String.valueOf(0), true);
                        htmltext = "alarm_of_giant_q0184_q0185_10.htm";
                    }
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("care_of_nikola");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == maestro_nikola) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case QUEST:
                        case LEVEL:
                            htmltext = "maestro_nikola_q0185_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "maestro_nikola_q0185_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == maestro_nikola) {
                    if (GetMemoState == 1)
                        htmltext = "maestro_nikola_q0185_07.htm";
                } else if (npcId == researcher_lorain) {
                    if (GetMemoState == 1)
                        htmltext = "researcher_lorain_q0185_01.htm";
                    else if (GetMemoState == 2)
                        htmltext = "researcher_lorain_q0185_04.htm";
                    else if (GetMemoState >= 3 && GetMemoState <= 5)
                        htmltext = "researcher_lorain_q0185_06.htm";
                    else if (GetMemoState == 6)
                        htmltext = "researcher_lorain_q0185_07.htm";
                } else if (npcId == broken_controller) {
                    if (GetMemoState == 3) {
                        if (myself_i_quest0 == 0)
                            htmltext = "broken_controller_q0185_01.htm";
                        else if (myself_i_quest1 == st.getPlayer().getObjectId())
                            htmltext = "broken_controller_q0185_03.htm";
                        else
                            htmltext = "broken_controller_q0185_04.htm";
                    } else if (GetMemoState == 4)
                        htmltext = "broken_controller_q0185_05.htm";
                    else if (GetMemoState == 5)
                        htmltext = "broken_controller_q0185_07.htm";
                } else if (npcId == alarm_of_giant_q0184) {
                    if (GetMemoState == 3)
                        htmltext = "alarm_of_giant_q0184001.htm";
                }
                break;
        }
        return htmltext;
    }
}