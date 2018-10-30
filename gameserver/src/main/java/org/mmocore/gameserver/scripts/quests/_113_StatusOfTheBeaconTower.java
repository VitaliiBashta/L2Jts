package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.0
 * @date 31/01/2015
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _113_StatusOfTheBeaconTower extends Quest {
    // npc
    private static final int seer_moirase = 31979;
    private static final int torant = 32016;
    // questitem
    private static final int q_box_of_sacred_fire_re = 14860;

    public _113_StatusOfTheBeaconTower() {
        super(false);
        addStartNpc(seer_moirase);
        addTalkId(torant);
        addQuestItem(q_box_of_sacred_fire_re);
        addLevelCheck(80);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("present_condition_of_beacon");
        int GetHTMLCookie = st.getInt("present_condition_of_beacon_cookie");
        int npcId = npc.getNpcId();
        if (npcId == seer_moirase) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("present_condition_of_beacon", String.valueOf(1 * 10 + 1), true);
                st.giveItems(q_box_of_sacred_fire_re, 1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "seer_moirase_q0113_0104.htm";
            }
        } else if (npcId == torant) {
            if (event.equalsIgnoreCase("menu_select?ask=113&reply=3") && GetHTMLCookie == 2 - 1) {
                if (GetMemoState >= (2 - 1) * 10 + 1) {
                    if (st.ownItemCount(q_box_of_sacred_fire_re) >= 1) {
                        st.addExpAndSp(619300, 44200);
                        st.giveItems(ADENA_ID, 154800);
                        st.takeItems(q_box_of_sacred_fire_re, -1);
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                        htmltext = "torant_q0113_0201.htm";
                    }
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("present_condition_of_beacon");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == seer_moirase) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "seer_moirase_q0113_0103.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "seer_moirase_q0113_0101.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == seer_moirase) {
                    if (GetMemoState == 1 * 10 + 1)
                        htmltext = "seer_moirase_q0113_0105.htm";
                } else if (npcId == torant) {
                    if (GetMemoState == 1 * 10 + 1 && st.ownItemCount(q_box_of_sacred_fire_re) >= 1) {
                        st.setMemoState("present_condition_of_beacon_cookie", String.valueOf(1), true);
                        htmltext = "torant_q0113_0101.htm";
                    }
                }
                break;
        }
        return htmltext;
    }
}