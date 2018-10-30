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
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _10290_LandDragonConqueror extends Quest {
    // npc
    private static final int watcher_antaras_theodric = 30755;

    // mobs
    private static final int antaras_max = 29068;

    // questitem
    private final static int q_shabby_necklace = 15522;
    private final static int q_voucher_of_miracle = 15523;
    private final static int q_portal_stone_1 = 3865;

    // etcitem
    private final static int circlet_of_antaras_slayer = 8568;

    public _10290_LandDragonConqueror() {
        super(PARTY_ALL);
        addStartNpc(watcher_antaras_theodric);
        addQuestItem(q_shabby_necklace, q_voucher_of_miracle);
        addKillId(antaras_max);
        addLevelCheck(83);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();

        if (npcId == watcher_antaras_theodric)
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("vanquisher_of_antaras", String.valueOf(1), true);
                st.giveItems(q_shabby_necklace, 1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "watcher_antaras_theodric_q10290_07.htm";
            } else if (event.equalsIgnoreCase("reply_1"))
                htmltext = "watcher_antaras_theodric_q10290_05.htm";
            else if (event.equalsIgnoreCase("reply_2"))
                htmltext = "watcher_antaras_theodric_q10290_06.htm";
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("vanquisher_of_antaras");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == watcher_antaras_theodric)
                    if (st.ownItemCount(q_portal_stone_1) >= 1) {
                        switch (isAvailableFor(st.getPlayer())) {
                            case LEVEL:
                                htmltext = "watcher_antaras_theodric_q10290_02.htm";
                                st.exitQuest(true);
                                break;
                            default:
                                htmltext = "watcher_antaras_theodric_q10290_01.htm";
                                break;
                        }
                    } else {
                        htmltext = "watcher_antaras_theodric_q10290_04.htm";
                        st.exitQuest(true);
                    }
                break;
            case STARTED:
                if (npcId == watcher_antaras_theodric)
                    if (GetMemoState == 1 && st.ownItemCount(q_shabby_necklace) >= 1)
                        htmltext = "watcher_antaras_theodric_q10290_08.htm";
                    else if (GetMemoState == 1 && st.ownItemCount(q_shabby_necklace) == 0 && st.ownItemCount(q_voucher_of_miracle) == 0) {
                        st.giveItems(q_shabby_necklace, 1);
                        htmltext = "watcher_antaras_theodric_q10290_09.htm";
                    } else if (GetMemoState == 2) {
                        st.giveItems(circlet_of_antaras_slayer, 1);
                        st.giveItems(ADENA_ID, 131236);
                        st.addExpAndSp(702557, 76334);
                        st.takeItems(q_voucher_of_miracle, -1);
                        st.removeMemo("vanquisher_of_antaras");
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                        htmltext = "watcher_antaras_theodric_q10290_10.htm";
                    }
                break;
            case COMPLETED:
                if (npcId == watcher_antaras_theodric)
                    htmltext = "watcher_antaras_theodric_q10290_03.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("vanquisher_of_antaras");
        int npcId = npc.getNpcId();

        if (GetMemoState == 1 && st.ownItemCount(q_shabby_necklace) > 0)
            if (npcId == antaras_max) {
                st.setCond(2);
                st.setMemoState("vanquisher_of_antaras", String.valueOf(2), true);
                st.takeItems(q_shabby_necklace, -1);
                st.giveItems(q_voucher_of_miracle, 1);
                st.soundEffect(SOUND_MIDDLE);
            }
        return null;
    }
}