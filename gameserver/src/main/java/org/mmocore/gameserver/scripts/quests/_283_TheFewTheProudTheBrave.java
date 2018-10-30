package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
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
 * @version 1.1
 * @date 06/01/2015
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _283_TheFewTheProudTheBrave extends Quest {
    // npc
    private static final int subelder_perwan = 32133;
    // mobs
    private static final int carmine_spider = 22244;
    // questitem
    private static final int q_claw_of_carmine_spider = 9747;

    public _283_TheFewTheProudTheBrave() {
        super(false);
        addStartNpc(subelder_perwan);
        addKillId(carmine_spider);
        addQuestItem(q_claw_of_carmine_spider);
        addLevelCheck(15);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        Player player = st.getPlayer();
        if (player == null) {
            return null;
        }
        int GetHTMLCookie = st.getInt("challenge_of_brave_man_cookie");
        int npcId = npc.getNpcId();
        if (npcId == subelder_perwan) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("challenge_of_brave_man", String.valueOf(1 * 10 + 1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "subelder_perwan_q0283_0103.htm";
            } else if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 2 - 1)
                htmltext = "subelder_perwan_q0283_0201.htm";
            else if (event.equalsIgnoreCase("reply_3") && GetHTMLCookie == 2 - 1) {
                if (st.ownItemCount(q_claw_of_carmine_spider) == 0)
                    htmltext = "subelder_perwan_q0283_0202.htm";
                if (st.ownItemCount(q_claw_of_carmine_spider) >= 10)
                    st.giveItems(ADENA_ID, 2187 + 45 * st.ownItemCount(q_claw_of_carmine_spider));
                else
                    st.giveItems(ADENA_ID, 45 * st.ownItemCount(q_claw_of_carmine_spider));
                st.takeItems(q_claw_of_carmine_spider, -1);
                if (player.getQuestState(41) == null) {
                    final Quest q = QuestManager.getQuest(41);
                    q.newQuestState(player, Quest.STARTED);
                    player.getQuestState(41).setMemoState("guide_mission", String.valueOf(100000), true);
                    st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.LAST_DUTY_COMPLETE_GO_FIND_THE_NEWBIE_GUIDE, 5000, ScreenMessageAlign.TOP_CENTER, true));
                } else if (player.getQuestState(41).getInt("guide_mission") % 100000000 / 10000000 != 1) {
                    player.getQuestState(41).setMemoState("guide_mission", String.valueOf(player.getQuestState(41).getInt("guide_mission") + 10000000), true);
                    st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.LAST_DUTY_COMPLETE_GO_FIND_THE_NEWBIE_GUIDE, 5000, ScreenMessageAlign.TOP_CENTER, true));
                }
                htmltext = "subelder_perwan_q0283_0203.htm";
            } else if (event.equalsIgnoreCase("reply_4") && GetHTMLCookie == 2 - 1) {
                htmltext = "subelder_perwan_q0283_0204.htm";
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("challenge_of_brave_man");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == subelder_perwan) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "subelder_perwan_q0283_0102.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "subelder_perwan_q0283_0101.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == subelder_perwan) {
                    if (GetMemoState == 1 * 10 + 1) {
                        if (st.ownItemCount(q_claw_of_carmine_spider) == 0)
                            htmltext = "subelder_perwan_q0283_0106.htm";
                        else {
                            st.setMemoState("challenge_of_brave_man_cookie", String.valueOf(1), true);
                            htmltext = "subelder_perwan_q0283_0105.htm";
                        }
                    }
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("challenge_of_brave_man");
        int npcId = npc.getNpcId();
        if (npcId == carmine_spider) {
            if (GetMemoState == 1 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 600) {
                    st.giveItems(q_claw_of_carmine_spider, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        }
        return null;
    }
}