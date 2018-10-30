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
 * @version 1.0
 * @date 05/01/2015
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _296_TarantulasSpiderSilk extends Quest {
    // npc
    private static final int trader_mion = 30519;
    private static final int defender_nathan = 30548;
    // mobs
    private static final int crimson_tarantula = 20394;
    private static final int hunter_tarantula = 20403;
    private static final int plunder_tarantula = 20508;
    // questitem
    private static final int tarantula_spider_silk = 1493;
    private static final int tarantula_spinnerette = 1494;
    // etcitem
    private static final int ring_of_raccoon = 1508;
    private static final int ring_of_firefly = 1509;

    public _296_TarantulasSpiderSilk() {
        super(false);
        addStartNpc(trader_mion);
        addTalkId(defender_nathan);
        addKillId(crimson_tarantula, hunter_tarantula, plunder_tarantula);
        addQuestItem(tarantula_spider_silk, tarantula_spinnerette);
        addLevelCheck(15, 21);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == trader_mion) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "trader_mion_q0296_03.htm";
            } else if (event.equalsIgnoreCase("reply_1")) {
                st.takeItems(tarantula_spinnerette, -1);
                htmltext = "trader_mion_q0296_06.htm";
                st.exitQuest(true);
                st.soundEffect(SOUND_FINISH);
            } else if (event.equalsIgnoreCase("reply_2"))
                htmltext = "trader_mion_q0296_07.htm";
        } else if (npcId == defender_nathan) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (st.ownItemCount(tarantula_spinnerette) >= 1) {
                    htmltext = "defender_nathan_q0296_03.htm";
                    st.giveItems(tarantula_spider_silk, (15 + Rnd.get(9)) * st.ownItemCount(tarantula_spinnerette));
                    st.takeItems(tarantula_spinnerette, -1);
                } else
                    htmltext = "defender_nathan_q0296_02.htm";
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
                if (npcId == trader_mion) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "trader_mion_q0296_01.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            if (st.ownItemCount(ring_of_raccoon) > 0 || st.ownItemCount(ring_of_firefly) > 0)
                                htmltext = "trader_mion_q0296_02.htm";
                            else
                                htmltext = "trader_mion_q0296_08.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == trader_mion) {
                    if (st.ownItemCount(tarantula_spider_silk) < 1)
                        htmltext = "trader_mion_q0296_04.htm";
                    else if (st.ownItemCount(tarantula_spider_silk) >= 1) {
                        htmltext = "trader_mion_q0296_05.htm";
                        if (st.ownItemCount(tarantula_spider_silk) >= 10)
                            st.giveItems(ADENA_ID, st.ownItemCount(tarantula_spider_silk) * 30 + 2000);
                        else
                            st.giveItems(ADENA_ID, st.ownItemCount(tarantula_spider_silk) * 30);
                        st.takeItems(tarantula_spider_silk, -1);
                        if (player.getQuestState(41) == null) {
                            final Quest q = QuestManager.getQuest(41);
                            q.newQuestState(player, Quest.STARTED);
                            player.getQuestState(41).setMemoState("guide_mission", String.valueOf(100000), true);
                            st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.LAST_DUTY_COMPLETE_GO_FIND_THE_NEWBIE_GUIDE, 5000, ScreenMessageAlign.TOP_CENTER, true));
                        } else if (player.getQuestState(41).getInt("guide_mission") % 100000000 / 10000000 != 1) {
                            player.getQuestState(41).setMemoState("guide_mission", String.valueOf(player.getQuestState(41).getInt("guide_mission") + 10000000), true);
                            st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.LAST_DUTY_COMPLETE_GO_FIND_THE_NEWBIE_GUIDE, 5000, ScreenMessageAlign.TOP_CENTER, true));
                        }
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(true);
                    }
                }
                if (npcId == defender_nathan)
                    htmltext = "defender_nathan_q0296_01.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == crimson_tarantula || npcId == hunter_tarantula || npcId == plunder_tarantula) {
            int i0 = Rnd.get(100);
            if (i0 > 95) {
                st.giveItems(tarantula_spinnerette, 1);
                st.soundEffect(SOUND_ITEMGET);
            } else if (i0 > 45) {
                st.giveItems(tarantula_spider_silk, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }
}