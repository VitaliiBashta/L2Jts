package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.manager.HellboundManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _131_BirdInACage extends Quest {
    // npc
    private static int priest_kanis = 32264;
    private static int parme_131y = 32271;

    // questitem
    private static int q_parme_sound_crystal = 9783;
    private static int q_letter_of_parme = 9784;

    // etcitem
    private static int ore_of_fire = 9546;
    private static int ore_of_water = 9547;
    private static int ore_of_earth = 9548;
    private static int ore_of_wind = 9549;

    public _131_BirdInACage() {
        super(false);
        addStartNpc(priest_kanis);
        addTalkId(parme_131y);
        addQuestItem(q_parme_sound_crystal, q_letter_of_parme);
        addLevelCheck(78);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("the_bird_of_cage");
        int npcId = npc.getNpcId();

        if (npcId == priest_kanis) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("the_bird_of_cage", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "priest_kanis_q0131_04.htm";
            }
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 1)
                    htmltext = "priest_kanis_q0131_06.htm";
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 1) {
                    st.setMemoState("the_bird_of_cage", String.valueOf(2), true);
                    htmltext = "priest_kanis_q0131_07.htm";
                }
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 2)
                    htmltext = "priest_kanis_q0131_09.htm";
            } else if (event.equalsIgnoreCase("reply_4")) {
                if (GetMemoState == 2)
                    htmltext = "priest_kanis_q0131_10.htm";
            } else if (event.equalsIgnoreCase("reply_5")) {
                if (GetMemoState == 2)
                    htmltext = "priest_kanis_q0131_11.htm";
            } else if (event.equalsIgnoreCase("reply_6")) {
                if (GetMemoState == 2) {
                    st.setCond(2);
                    st.setMemoState("the_bird_of_cage", String.valueOf(3), true);
                    st.giveItems(q_parme_sound_crystal, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "priest_kanis_q0131_12.htm";
                }
            } else if (event.equalsIgnoreCase("reply_7")) {
                if (GetMemoState == 3)
                    htmltext = "priest_kanis_q0131_14.htm";
            } else if (event.equalsIgnoreCase("reply_10")) {
                if (GetMemoState == 3)
                    htmltext = "priest_kanis_q0131_15.htm";
            } else if (event.equalsIgnoreCase("reply_11")) {
                if (GetMemoState == 4) {
                    st.takeItems(q_letter_of_parme, -1);
                    st.setMemoState("the_bird_of_cage", String.valueOf(5), true);
                    htmltext = "priest_kanis_q0131_17.htm";
                }
            } else if (event.equalsIgnoreCase("reply_12"))
                if (GetMemoState == 5) {
                    st.addExpAndSp(250677, 25019);
                    st.takeItems(q_parme_sound_crystal, -1);
                    st.removeMemo("the_bird_of_cage");
                    st.soundEffect(SOUND_FINISH);
                    if (HellboundManager.getHellboundLevel() == 0)
                        HellboundManager.setConfidence(1);
                    htmltext = "priest_kanis_q0131_19.htm";
                    switch (Rnd.get(4)) {
                        case 0: {
                            st.giveItems(ore_of_fire, 4);
                            break;
                        }
                        case 1: {
                            st.giveItems(ore_of_water, 4);
                            break;
                        }
                        case 2: {
                            st.giveItems(ore_of_earth, 4);
                            break;
                        }
                        case 3: {
                            st.giveItems(ore_of_wind, 4);
                            break;
                        }
                    }
                    st.exitQuest(false);
                }
        } else if (npcId == parme_131y)
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 3)
                    htmltext = "parme_131y_q0131_03.htm";
            } else if (event.equalsIgnoreCase("reply_2"))
                if (GetMemoState == 3) {
                    st.setCond(3);
                    st.setMemoState("the_bird_of_cage", String.valueOf(4), true);
                    st.giveItems(q_letter_of_parme, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "parme_131y_q0131_04.htm";
                    switch (Rnd.get(4)) {
                        case 0: {
                            st.getPlayer().teleToLocation(153565, 141290, -12736);
                            break;
                        }
                        case 1: {
                            st.getPlayer().teleToLocation(154359, 142077, -12736);
                            break;
                        }
                        case 2: {
                            st.getPlayer().teleToLocation(153570, 142853, -12736);
                            break;
                        }
                        case 3: {
                            st.getPlayer().teleToLocation(152793, 142080, -12736);
                            break;
                        }
                    }
                }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("the_bird_of_cage");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == priest_kanis) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "priest_kanis_q0131_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "priest_kanis_q0131_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == priest_kanis) {
                    if (GetMemoState == 1)
                        htmltext = "priest_kanis_q0131_05.htm";
                    else if (GetMemoState == 2)
                        htmltext = "priest_kanis_q0131_08.htm";
                    else if (GetMemoState == 3)
                        htmltext = "priest_kanis_q0131_13.htm";
                    else if (GetMemoState == 4)
                        htmltext = "priest_kanis_q0131_16.htm";
                    else if (GetMemoState == 5)
                        htmltext = "priest_kanis_q0131_18.htm";
                } else if (npcId == parme_131y)
                    if (GetMemoState < 3)
                        htmltext = "parme_131y_q0131_01.htm";
                    else if (GetMemoState == 3)
                        htmltext = "parme_131y_q0131_02.htm";
                break;
        }
        return htmltext;
    }
}