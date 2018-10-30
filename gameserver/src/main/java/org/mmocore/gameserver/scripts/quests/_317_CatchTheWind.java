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
 * @date 25/10/2015
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _317_CatchTheWind extends Quest {
    // npc
    private final static int rizraell = 30361;
    // mobs
    private final static int lirein = 20036;
    private final static int lirein_ribe = 20044;
    // questitem
    private final static int wind_shard = 1078;

    public _317_CatchTheWind() {
        super(false);
        addStartNpc(rizraell);
        addKillId(lirein, lirein_ribe);
        addQuestItem(wind_shard);
        addLevelCheck(18, 23);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == rizraell) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "rizraell_q0317_04.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=317&reply=2")) {
                if (st.ownItemCount(wind_shard) > 0) {
                    if (st.ownItemCount(wind_shard) >= 10)
                        st.giveItems(ADENA_ID, 2988 + 40 * st.ownItemCount(wind_shard));
                    else
                        st.giveItems(ADENA_ID, 40 * st.ownItemCount(wind_shard));
                }
                st.takeItems(wind_shard, -1);
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                htmltext = "rizraell_q0317_08.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=317&reply=3")) {
                if (st.ownItemCount(wind_shard) > 0) {
                    if (st.ownItemCount(wind_shard) >= 10)
                        st.giveItems(ADENA_ID, 2988 + 40 * st.ownItemCount(wind_shard));
                    else
                        st.giveItems(ADENA_ID, 40 * st.ownItemCount(wind_shard));
                }
                st.takeItems(wind_shard, -1);
                htmltext = "rizraell_q0317_09.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == rizraell) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "rizraell_q0317_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "rizraell_q0317_03.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == rizraell) {
                    if (st.ownItemCount(wind_shard) == 0)
                        htmltext = "rizraell_q0317_05.htm";
                    else if (st.ownItemCount(wind_shard) != 0)
                        htmltext = "rizraell_q0317_07.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == lirein || npcId == lirein_ribe) {
            int i0 = Rnd.get(100);
            if (i0 < 50) {
                st.giveItems(wind_shard, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }
}