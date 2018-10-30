package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.manager.QuestManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.0
 * @date 05/01/2015
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _261_CollectorsDream extends Quest {
    // npc
    private static final int moneylender_alshupes = 30222;
    // mobs
    private static final int hook_spider = 20308;
    private static final int crimson_spider = 20460;
    private static final int pincer_spider = 20466;
    // questitem
    private static final int giant_spider_leg = 1087;

    public _261_CollectorsDream() {
        super(false);
        addStartNpc(moneylender_alshupes);
        addKillId(hook_spider, crimson_spider, pincer_spider);
        addQuestItem(giant_spider_leg);
        addLevelCheck(15, 21);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == moneylender_alshupes) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "moneylender_alshupes_q0261_03.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        Player player = st.getPlayer();
        if (player == null) {
            return null;
        }
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == moneylender_alshupes) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "moneylender_alshupes_q0261_01.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "moneylender_alshupes_q0261_02.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == moneylender_alshupes) {
                    if (st.ownItemCount(giant_spider_leg) >= 8) {
                        st.giveItems(ADENA_ID, 1000);
                        st.addExpAndSp(2000, 0);
                        st.takeItems(giant_spider_leg, -1);
                        if (player.getQuestState(41) == null) {
                            final Quest q = QuestManager.getQuest(41);
                            q.newQuestState(player, Quest.STARTED);
                            player.getQuestState(41).setMemoState("guide_mission", String.valueOf(100000), true);
                            st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.LAST_DUTY_COMPLETE_GO_FIND_THE_NEWBIE_GUIDE, 5000, ScreenMessageAlign.TOP_CENTER, true));
                        } else if (player.getQuestState(41).getInt("guide_mission") % 100000000 / 10000000 != 1) {
                            player.getQuestState(41).setMemoState("guide_mission", String.valueOf(player.getQuestState(41).getInt("guide_mission") + 10000000), true);
                            st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.LAST_DUTY_COMPLETE_GO_FIND_THE_NEWBIE_GUIDE, 5000, ScreenMessageAlign.TOP_CENTER, true));
                        }
                        htmltext = "moneylender_alshupes_q0261_05.htm";
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                    } else
                        htmltext = "moneylender_alshupes_q0261_04.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == hook_spider || npcId == crimson_spider || npcId == pincer_spider) {
            if (st.ownItemCount(giant_spider_leg) < 8) {
                st.giveItems(giant_spider_leg, 1);
                if (st.ownItemCount(giant_spider_leg) >= 8) {
                    st.setCond(2);
                    st.soundEffect(SOUND_MIDDLE);
                } else
                    st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }
}