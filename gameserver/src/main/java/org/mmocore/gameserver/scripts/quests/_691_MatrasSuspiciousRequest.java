package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.data.htm.HtmCache;
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
public final class _691_MatrasSuspiciousRequest extends Quest {
    // npc
    private static final int matras = 32245;

    // mobs
    private static final int body_destroy = 22363;
    private static final int soul_invader = 22364;
    private static final int guard_of_maze = 22365;
    private static final int guard_of_maze_1 = 22366;
    private static final int mage_of_maze = 22367;
    private static final int guardcap_of_maze = 22368;
    private static final int bishop_of_maze = 22369;
    private static final int guardcap_of_pathway = 22370;
    private static final int guard_of_pathway = 22371;
    private static final int mage_of_pathway = 22372;

    // questitem
    private static final int q_red_stone_q0691 = 10372;

    // etcitem
    private static final int soul_of_dynasty_II = 10413;

    public _691_MatrasSuspiciousRequest() {
        super(true);
        addStartNpc(matras);
        addTalkId(matras);
        addKillId(body_destroy, soul_invader, guard_of_maze, guard_of_maze_1, mage_of_maze, guardcap_of_maze, bishop_of_maze, guardcap_of_pathway, guard_of_pathway, mage_of_pathway);
        addLevelCheck(76);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("suspicious_request_of_matras");
        int GetMemoStateEx = st.getInt("suspicious_request_of_matras_ex");

        if (event.equalsIgnoreCase("quest_accept")) {
            st.setCond(1);
            st.setMemoState("suspicious_request_of_matras", String.valueOf(1), true);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            htmltext = "matras_q0691_04.htm";

        } else if (event.equalsIgnoreCase("reply_1"))
            htmltext = "matras_q0691_02.htm";
        else if (event.equalsIgnoreCase("reply_2")) {
            if (GetMemoState == 1) {
                st.setMemoState("suspicious_request_of_matras_ex", String.valueOf(GetMemoStateEx + st.ownItemCount(q_red_stone_q0691)), true);
                htmltext = HtmCache.getInstance().getHtml("quests/_691_MatrasSuspiciousRequest/matras_q0691_08.htm", st.getPlayer());
                htmltext = htmltext.replace("<?itemcount?>", String.valueOf(GetMemoStateEx + st.ownItemCount(q_red_stone_q0691)));
                st.takeItems(q_red_stone_q0691, -1);
            }
        } else if (event.equalsIgnoreCase("reply_3")) {
            if (GetMemoState == 1)
                if (GetMemoStateEx >= 744) {
                    st.setMemoState("suspicious_request_of_matras_ex", String.valueOf(GetMemoStateEx - 744), true);
                    st.giveItems(soul_of_dynasty_II, 1);
                    htmltext = "matras_q0691_09.htm";
                } else {
                    htmltext = HtmCache.getInstance().getHtml("quests/_691_MatrasSuspiciousRequest/matras_q0691_10.htm", st.getPlayer());
                    htmltext = htmltext.replace("<?itemcount?>", String.valueOf(GetMemoStateEx));
                }
        } else if (event.equalsIgnoreCase("reply_4"))
            htmltext = "matras_q0691_11.htm";
        else if (event.equalsIgnoreCase("reply_5")) {
            st.giveItems(ADENA_ID, GetMemoStateEx * 10000);
            st.removeMemo("suspicious_request_of_matras");
            st.removeMemo("suspicious_request_of_matras_ex");
            st.soundEffect(SOUND_FINISH);
            htmltext = "matras_q0691_12.htm";
            st.exitQuest(true);
        }
        return htmltext;
    }

    @Override
    public final String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("suspicious_request_of_matras");
        int GetMemoStateEx = st.getInt("suspicious_request_of_matras_ex");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == matras) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "matras_q0691_03.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "matras_q0691_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == matras)
                    if (GetMemoState == 1)
                        if (st.ownItemCount(q_red_stone_q0691) >= 1)
                            htmltext = "matras_q0691_05.htm";
                        else if (GetMemoStateEx == 0)
                            htmltext = "matras_q0691_06.htm";
                        else if (GetMemoStateEx >= 1) {
                            htmltext = HtmCache.getInstance().getHtml("quests/_691_MatrasSuspiciousRequest/matras_q0691_07.htm", st.getPlayer());
                            htmltext = htmltext.replace("<?itemcount?>", String.valueOf(GetMemoStateEx));
                        }
                break;
        }
        return htmltext;
    }

    @Override
    public final String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int GetMemoState = st.getInt("suspicious_request_of_matras");

        if (GetMemoState == 1)
            if (npcId == body_destroy) {
                int i0 = Rnd.get(1000);
                if (i0 <= 897) {
                    st.giveItems(q_red_stone_q0691, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == soul_invader) {
                int i0 = Rnd.get(1000);
                if (i0 <= 261) {
                    st.giveItems(q_red_stone_q0691, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == guard_of_maze || npcId == guard_of_maze_1) {
                int i0 = Rnd.get(1000);
                if (i0 <= 560) {
                    st.giveItems(q_red_stone_q0691, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == mage_of_maze) {
                int i0 = Rnd.get(1000);
                if (i0 <= 190) {
                    st.giveItems(q_red_stone_q0691, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == bishop_of_maze) {
                int i0 = Rnd.get(1000);
                if (i0 <= 210) {
                    st.giveItems(q_red_stone_q0691, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == guardcap_of_pathway) {
                int i0 = Rnd.get(1000);
                if (i0 <= 787) {
                    st.giveItems(q_red_stone_q0691, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == guard_of_pathway) {
                int i0 = Rnd.get(1000);
                if (i0 <= 257) {
                    st.giveItems(q_red_stone_q0691, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == mage_of_pathway) {
                int i0 = Rnd.get(1000);
                if (i0 <= 656) {
                    st.giveItems(q_red_stone_q0691, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == guardcap_of_maze) {
                int i0 = Rnd.get(1000);
                if (i0 <= 129) {
                    st.giveItems(q_red_stone_q0691, 3);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_red_stone_q0691, 2);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        return null;
    }
}