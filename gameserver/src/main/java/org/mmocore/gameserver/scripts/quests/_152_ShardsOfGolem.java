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
 * @date 21/01/2015
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _152_ShardsOfGolem extends Quest {
    // npc
    private static final int harry = 30035;
    private static final int blacksmith_alltran = 30283;
    // mobs
    private static final int stone_golem = 20016;
    // questitem
    private static final int harrys_receipt1 = 1008;
    private static final int harrys_receipt2 = 1009;
    private static final int golem_shard = 1010;
    private static final int tool_box = 1011;
    // etcitem
    private static final int wooden_breastplate = 23;

    public _152_ShardsOfGolem() {
        super(false);
        addStartNpc(harry);
        addTalkId(blacksmith_alltran);
        addKillId(stone_golem);
        addQuestItem(harrys_receipt1, harrys_receipt2, golem_shard, tool_box);
        addLevelCheck(10, 17);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == harry) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                if (st.ownItemCount(harrys_receipt1) == 0)
                    st.giveItems(harrys_receipt1, 1);
                htmltext = "harry_q0152_04.htm";
            }
        } else if (npcId == blacksmith_alltran) {
            if (event.equalsIgnoreCase("reply=2")) {
                if (st.ownItemCount(harrys_receipt1) > 0) {
                    st.setCond(2);
                    st.takeItems(harrys_receipt1, -1);
                    st.soundEffect(SOUND_MIDDLE);
                    if (st.ownItemCount(harrys_receipt2) == 0)
                        st.giveItems(harrys_receipt2, 1);
                    htmltext = "blacksmith_alltran_q0152_02.htm";
                }
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
                if (npcId == harry) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "harry_q0152_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "harry_q0152_03.htm";
                            return htmltext;
                    }
                }
                break;
            case STARTED:
                if (npcId == harry) {
                    if (st.ownItemCount(harrys_receipt1) != 0 && st.ownItemCount(tool_box) == 0)
                        htmltext = "harry_q0152_05a.htm";
                    else if (st.ownItemCount(harrys_receipt2) != 0 && st.ownItemCount(tool_box) == 0)
                        htmltext = "harry_q0152_05.htm";
                    else if (st.ownItemCount(tool_box) != 0) {
                        st.takeItems(tool_box, -1);
                        st.takeItems(harrys_receipt2, -1);
                        st.giveItems(wooden_breastplate, 1);
                        st.addExpAndSp(5000, 0);
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                        htmltext = "harry_q0152_06.htm";
                    }
                } else if (npcId == blacksmith_alltran) {
                    if (st.ownItemCount(harrys_receipt1) != 0)
                        htmltext = "blacksmith_alltran_q0152_01.htm";
                    else if (st.ownItemCount(harrys_receipt2) != 0 && st.ownItemCount(golem_shard) < 5 && st.ownItemCount(tool_box) == 0)
                        htmltext = "blacksmith_alltran_q0152_03.htm";
                    else if (st.ownItemCount(harrys_receipt2) != 0 && st.ownItemCount(golem_shard) >= 5 && st.ownItemCount(tool_box) == 0) {
                        st.setCond(4);
                        st.takeItems(golem_shard, -1);
                        if (st.ownItemCount(tool_box) == 0)
                            st.giveItems(tool_box, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "blacksmith_alltran_q0152_04.htm";
                    } else if (st.ownItemCount(harrys_receipt2) != 0 && st.ownItemCount(tool_box) != 0)
                        htmltext = "blacksmith_alltran_q0152_05.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == stone_golem) {
            if (st.ownItemCount(golem_shard) < 5) {
                if (Rnd.get(100) < 30) {
                    st.giveItems(golem_shard, 1);
                    if (st.ownItemCount(golem_shard) >= 5) {
                        st.setCond(3);
                        st.soundEffect(SOUND_MIDDLE);
                    } else
                        st.soundEffect(SOUND_ITEMGET);
                }
            }
        }
        return null;
    }
}