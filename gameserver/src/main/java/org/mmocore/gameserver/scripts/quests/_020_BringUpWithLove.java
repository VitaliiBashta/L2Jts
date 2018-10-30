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
public class _020_BringUpWithLove extends Quest {
    // npc
    private static final int beast_herder_tunatun = 31537;
    // questitem
    private static final int q_jewel_of_innocent_re = 15533;
    private static final int q_jewel_of_innocent = 7185;
    // etcitem
    private static final int training_whip = 15473;
    private static final int high_ore_of_water = 9553;

    public _020_BringUpWithLove() {
        super(false);
        addStartNpc(beast_herder_tunatun);
        addTalkId(beast_herder_tunatun);
        addLevelCheck(82);
    }

    @Override
    public String onEvent(final String event, final QuestState st, final NpcInstance npc) {
        String htmltext = event;
        final int GetMemoState = st.getInt("givemelove");
        final int npcId = npc.getNpcId();
        if (npcId == beast_herder_tunatun) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("givemelove", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "beast_herder_tunatun_q0020_14.htm";
            } else if (event.equalsIgnoreCase("reply_1")) {
                htmltext = "beast_herder_tunatun_q0020_03.htm";
            } else if (event.equalsIgnoreCase("reply_12")) {
                htmltext = "beast_herder_tunatun_q0020_06.htm";
            } else if (event.equalsIgnoreCase("reply_13")) {
                htmltext = "beast_herder_tunatun_q0020_07.htm";
            } else if (event.equalsIgnoreCase("reply_14")) {
                htmltext = "beast_herder_tunatun_q0020_08.htm";
            } else if (event.equalsIgnoreCase("reply_11")) {
                htmltext = "beast_herder_tunatun_q0020_09.htm";
            } else if (event.equalsIgnoreCase("reply_4")) {
                htmltext = "beast_herder_tunatun_q0020_10.htm";
            } else if (event.equalsIgnoreCase("reply_3")) {
                htmltext = "beast_herder_tunatun_q0020_11.htm";
            } else if (event.equalsIgnoreCase("reply_7")) {
                htmltext = "beast_herder_tunatun_q0020_12.htm";
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (st.ownItemCount(training_whip) < 1) {
                    st.giveItems(training_whip, 1);
                    htmltext = "beast_herder_tunatun_q0020_04.htm";
                } else {
                    htmltext = "beast_herder_tunatun_q0020_05.htm";
                }
            } else if (event.equalsIgnoreCase("reply_5")) {
                htmltext = "beast_herder_tunatun_q0020_13.htm";
            } else if (event.equalsIgnoreCase("reply_6")) {
                if (GetMemoState == 1 && (st.ownItemCount(q_jewel_of_innocent_re) >= 1 || st.ownItemCount(q_jewel_of_innocent) >= 1)) {
                    st.giveItems(high_ore_of_water, 1);
                    st.takeItems(q_jewel_of_innocent, -1);
                    st.takeItems(q_jewel_of_innocent_re, -1);
                    st.removeMemo("givemelove");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "beast_herder_tunatun_q0020_17.htm";
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(final NpcInstance npc, final QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("givemelove");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED: {
                if (npcId == beast_herder_tunatun) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL: {
                            htmltext = "beast_herder_tunatun_q0020_02.htm";
                            st.exitQuest(true);
                            break;
                        }
                        default: {
                            htmltext = "beast_herder_tunatun_q0020_01.htm";
                            break;
                        }
                    }
                }
                break;
            }
            case STARTED: {
                if (npcId == beast_herder_tunatun) {
                    if (GetMemoState == 1 && st.ownItemCount(q_jewel_of_innocent_re) == 0 && st.ownItemCount(q_jewel_of_innocent) == 0) {
                        htmltext = "beast_herder_tunatun_q0020_15.htm";
                    } else if (GetMemoState == 1 && (st.ownItemCount(q_jewel_of_innocent_re) >= 1 || st.ownItemCount(q_jewel_of_innocent) >= 1)) {
                        htmltext = "beast_herder_tunatun_q0020_16.htm";
                    }
                }
                break;
            }
        }
        return htmltext;
    }
}