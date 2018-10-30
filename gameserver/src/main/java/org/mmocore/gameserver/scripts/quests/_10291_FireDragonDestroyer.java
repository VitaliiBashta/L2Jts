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
 * @date 07/03/2015
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _10291_FireDragonDestroyer extends Quest {
    // npc
    private static final int watcher_valakas_klein = 31540;
    // mobs
    private static final int valakas = 29028;
    // questitem
    private static final int q_floating_stone = 7267;
    private static final int q_poor_necklace = 15524;
    private static final int q_voucher_of_valor = 15525;
    // etcitem
    private static final int circlet_of_valakas_slayer = 8567;

    public _10291_FireDragonDestroyer() {
        super(PARTY_ALL);
        addStartNpc(watcher_valakas_klein);
        addQuestItem(q_poor_necklace, q_voucher_of_valor);
        addKillId(valakas);
        addLevelCheck(83);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == watcher_valakas_klein) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("conquer_the_valakas", String.valueOf(1), true);
                st.giveItems(q_poor_necklace, 1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "watcher_valakas_klein_q10291_07.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=10291&reply=1"))
                htmltext = "watcher_valakas_klein_q10291_05.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=10291&reply=2"))
                htmltext = "watcher_valakas_klein_q10291_06.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("conquer_the_valakas");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == watcher_valakas_klein) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "klein_q10291_00.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            if (st.ownItemCount(q_floating_stone) >= 1)
                                htmltext = "watcher_valakas_klein_q10291_01.htm";
                            else {
                                htmltext = "watcher_valakas_klein_q10291_04.htm";
                                st.exitQuest(true);
                            }
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == watcher_valakas_klein) {
                    if (GetMemoState == 1 && st.ownItemCount(q_poor_necklace) >= 1)
                        htmltext = "watcher_valakas_klein_q10291_08.htm";
                    else if (GetMemoState == 1 && st.ownItemCount(q_poor_necklace) == 0 && st.ownItemCount(q_voucher_of_valor) == 0) {
                        st.giveItems(q_poor_necklace, 1);
                        htmltext = "watcher_valakas_klein_q10291_09.htm";
                    } else if (GetMemoState == 2) {
                        st.takeItems(q_voucher_of_valor, -1);
                        st.giveItems(ADENA_ID, 126549);
                        st.addExpAndSp(717291, 77397);
                        st.giveItems(circlet_of_valakas_slayer, 1);
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                        htmltext = "watcher_valakas_klein_q10291_10.htm";
                    }
                }
                break;
            case COMPLETED:
                if (npcId == watcher_valakas_klein)
                    htmltext = "watcher_valakas_klein_q10291_03.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("conquer_the_valakas");
        int npcId = npc.getNpcId();
        if (npcId == valakas) {
            if (GetMemoState == 1 && st.ownItemCount(q_poor_necklace) > 0) {
                st.setCond(2);
                st.setMemoState("conquer_the_valakas", String.valueOf(2), true);
                st.takeItems(q_poor_necklace, -1);
                st.giveItems(q_voucher_of_valor, 1);
                st.soundEffect(SOUND_MIDDLE);
            }
        }
        return null;
    }
}