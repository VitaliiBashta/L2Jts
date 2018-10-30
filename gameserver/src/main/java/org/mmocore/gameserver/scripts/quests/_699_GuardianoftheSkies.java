package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
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
 * @date 15/03/2015
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _699_GuardianoftheSkies extends Quest {
    // npc
    private static final int engineer_recon = 32557;
    // mobs
    private static final int vulture_rider_1lv = 22614;
    private static final int vulture_rider_2lv = 22615;
    private static final int vulture_rider_3lv = 25633;
    // questitem
    private static final int q_gold_feather_of_vulture = 13871;

    public _699_GuardianoftheSkies() {
        super(true);
        addStartNpc(engineer_recon);
        addKillId(vulture_rider_1lv, vulture_rider_2lv, vulture_rider_3lv);
        addQuestItem(q_gold_feather_of_vulture);
        addLevelCheck(75);
        addQuestCompletedCheck(10273);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("protection_of_safety");
        int npcId = npc.getNpcId();
        if (npcId == engineer_recon) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("protection_of_safety", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "engineer_recon_q0699_04.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=699&reply=1"))
                htmltext = "engineer_recon_q0699_03.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=699&reply=2")) {
                if (GetMemoState == 1)
                    htmltext = "engineer_recon_q0699_08.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=699&reply=3")) {
                if (GetMemoState == 1) {
                    st.exitQuest(true);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "engineer_recon_q0699_09.htm";
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("protection_of_safety");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == engineer_recon) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "engineer_recon_q0699_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "engineer_recon_q0699_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == engineer_recon) {
                    if (GetMemoState == 1 && st.ownItemCount(q_gold_feather_of_vulture) < 1)
                        htmltext = "engineer_recon_q0699_05.htm";
                    else if (GetMemoState == 1 && st.ownItemCount(q_gold_feather_of_vulture) >= 1 && st.ownItemCount(q_gold_feather_of_vulture) < 10) {
                        st.giveItems(ADENA_ID, st.ownItemCount(q_gold_feather_of_vulture) * 1500);
                        st.takeItems(q_gold_feather_of_vulture, -1);
                        htmltext = "engineer_recon_q0699_06.htm";
                    } else if (GetMemoState == 1 && st.ownItemCount(q_gold_feather_of_vulture) >= 10) {
                        st.giveItems(ADENA_ID, st.ownItemCount(q_gold_feather_of_vulture) * 1500 + 8335);
                        st.takeItems(q_gold_feather_of_vulture, -1);
                        htmltext = "engineer_recon_q0699_06.htm";
                    }
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("protection_of_safety");
        int npcId = npc.getNpcId();
        if (GetMemoState == 1) {
            if (npcId == vulture_rider_1lv) {
                int i0 = Rnd.get(1000);
                if (i0 < 840) {
                    st.giveItems(q_gold_feather_of_vulture, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == vulture_rider_2lv) {
                int i0 = Rnd.get(1000);
                if (i0 < 857) {
                    st.giveItems(q_gold_feather_of_vulture, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == vulture_rider_2lv) {
                int i0 = Rnd.get(1000);
                if (i0 < 719) {
                    st.giveItems(q_gold_feather_of_vulture, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        }
        return null;
    }
}