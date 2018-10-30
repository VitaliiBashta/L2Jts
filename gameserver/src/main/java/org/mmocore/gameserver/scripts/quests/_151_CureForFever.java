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
 * @date 28/12/2014
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _151_CureForFever extends Quest {
    // npc
    private static final int elias = 30050;
    private static final int yohan = 30032;
    // mobs
    private static final int giant_spider = 20103;
    private static final int poker = 20106;
    private static final int blader = 20108;
    // questitem
    private static final int poison_sac = 703;
    private static final int fever_medicine = 704;
    // etcitem
    private static final int round_shield = 102;

    public _151_CureForFever() {
        super(false);
        addStartNpc(elias);
        addTalkId(yohan);
        addKillId(giant_spider, poker, blader);
        addQuestItem(poison_sac, fever_medicine);
        addLevelCheck(15, 21);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == elias) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "elias_q0151_03.htm";
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
                if (npcId == elias) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "elias_q0151_01.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "elias_q0151_02.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == elias) {
                    if (st.ownItemCount(fever_medicine) == 1) {
                        htmltext = "elias_q0151_06.htm";
                        st.giveItems(round_shield, 1);
                        st.takeItems(fever_medicine, 1);
                        st.addExpAndSp(13106, 613);
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
                        st.exitQuest(false);
                    } else if (st.ownItemCount(poison_sac) == 1)
                        htmltext = "elias_q0151_05.htm";
                    else if (st.ownItemCount(poison_sac) == 0 && st.ownItemCount(fever_medicine) == 0)
                        htmltext = "elias_q0151_04.htm";
                } else if (npcId == yohan) {
                    if (st.ownItemCount(poison_sac) > 0) {
                        if (st.ownItemCount(poison_sac) == 1) {
                            st.setCond(3);
                            st.giveItems(fever_medicine, 1);
                            st.takeItems(poison_sac, 1);
                            st.soundEffect(SOUND_MIDDLE);
                            htmltext = "yohan_q0151_01.htm";
                        } else if (st.ownItemCount(fever_medicine) == 1)
                            htmltext = "yohan_q0151_02.htm";
                    } else if (st.ownItemCount(fever_medicine) > 0)
                        htmltext = "yohan_q0151_02.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (st.ownItemCount(poison_sac) == 0) {
            if (npcId == giant_spider || npcId == poker || npcId == blader) {
                if (Rnd.get(5) == 0) {
                    st.setCond(2);
                    st.giveItems(poison_sac, 1);
                    st.soundEffect(SOUND_MIDDLE);
                }
            }
        }
        return null;
    }
}