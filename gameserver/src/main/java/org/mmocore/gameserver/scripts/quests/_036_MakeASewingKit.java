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
 * @version 1.1
 * @date 26/07/2016
 * @lastedit 26/07/2016
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _036_MakeASewingKit extends Quest {
    // npc
    private final static int head_blacksmith_ferris = 30847;
    // mobs
    private final static int enchanted_iron_golem = 20566;
    // questitem
    private final static int q_piece_of_steel = 7163;
    private final static int q_seal_of_stock = 7164;
    // etcitem
    private final static int artisans_frame = 1891;
    private final static int oriharukon = 1893;
    private final static int q_workbox = 7078;

    public _036_MakeASewingKit() {
        super(true);
        addStartNpc(head_blacksmith_ferris);
        addKillId(enchanted_iron_golem);
        addQuestItem(q_piece_of_steel);
        addLevelCheck(60);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetHTMLCookie = st.getInt("make_a_workbox_cookie");
        int npcId = npc.getNpcId();
        if (npcId == head_blacksmith_ferris) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("make_a_workbox", String.valueOf(1 * 10 + 1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "head_blacksmith_ferris_q0036_0104.htm";
            } else if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 2 - 1) {
                if (st.ownItemCount(q_piece_of_steel) >= 5) {
                    st.setCond(3);
                    st.setMemoState("make_a_workbox", String.valueOf(2 * 10 + 1), true);
                    st.takeItems(q_piece_of_steel, 5);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "head_blacksmith_ferris_q0036_0201.htm";
                } else
                    htmltext = "head_blacksmith_ferris_q0036_0202.htm";
            } else if (event.equalsIgnoreCase("reply_3") && GetHTMLCookie == 3 - 1) {
                if (st.ownItemCount(artisans_frame) >= 10 && st.ownItemCount(oriharukon) >= 10) {
                    st.takeItems(artisans_frame, 10);
                    st.takeItems(oriharukon, 10);
                    st.giveItems(q_workbox, 1);
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "head_blacksmith_ferris_q0036_0301.htm";
                } else
                    htmltext = "head_blacksmith_ferris_q0036_0302.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("make_a_workbox");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == head_blacksmith_ferris) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "head_blacksmith_ferris_q0036_0103.htm";
                            break;
                        default:
                            if (st.getPlayer().getQuestState(37) != null && st.getPlayer().getQuestState(37).isStarted() && st.ownItemCount(q_seal_of_stock) >= 1)
                                htmltext = "head_blacksmith_ferris_q0036_0101.htm";
                            else
                                htmltext = "head_blacksmith_ferris_q0036_0102.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == head_blacksmith_ferris) {
                    if (GetMemoState >= 1 * 10 + 1 && GetMemoState <= 1 * 10 + 2) {
                        if (GetMemoState == 1 * 10 + 2 && st.ownItemCount(q_piece_of_steel) >= 5) {
                            st.setMemoState("make_a_workbox_cookie", String.valueOf(1), true);
                            htmltext = "head_blacksmith_ferris_q0036_0105.htm";
                        } else
                            htmltext = "head_blacksmith_ferris_q0036_0106.htm";
                    } else if (GetMemoState == 2 * 10 + 1) {
                        if (st.ownItemCount(artisans_frame) >= 10 && st.ownItemCount(oriharukon) >= 10) {
                            st.setMemoState("make_a_workbox_cookie", String.valueOf(2), true);
                            htmltext = "head_blacksmith_ferris_q0036_0203.htm";
                        } else
                            htmltext = "head_blacksmith_ferris_q0036_0204.htm";
                    }
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("make_a_workbox");
        int npcId = npc.getNpcId();
        if (npcId == enchanted_iron_golem) {
            if (GetMemoState == 1 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 500) {
                    if (st.ownItemCount(q_piece_of_steel) + 1 >= 5) {
                        if (st.ownItemCount(q_piece_of_steel) < 5) {
                            st.giveItems(q_piece_of_steel, 5 - st.ownItemCount(q_piece_of_steel));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        st.setCond(2);
                        st.setMemoState("make_a_workbox", String.valueOf(1 * 10 + 2), true);
                    } else {
                        st.giveItems(q_piece_of_steel, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        }
        return null;
    }
}