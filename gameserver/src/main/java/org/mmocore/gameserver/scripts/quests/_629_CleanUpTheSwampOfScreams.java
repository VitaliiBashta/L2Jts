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
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _629_CleanUpTheSwampOfScreams extends Quest {
    // npc
    private static final int merc_cap_peace = 31553;

    // mobs
    private static final int splinter_stakato = 21508;
    private static final int splinter_stakato_worker = 21509;
    private static final int splinter_stakato_soldier = 21510;
    private static final int splinter_stakato_drone = 21511;
    private static final int splinter_stakato_drone_a = 21512;
    private static final int needle_stakato = 21513;
    private static final int needle_stakato_worker = 21514;
    private static final int needle_stakato_soldier = 21515;
    private static final int needle_stakato_drone = 21516;
    private static final int needle_stakato_drone_a = 21517;

    // questitem
    private static final int q_stakato_talons = 7250;
    private static final int q_goldenram_coin = 7251;

    public _629_CleanUpTheSwampOfScreams() {
        super(true);
        addStartNpc(merc_cap_peace);
        addKillId(splinter_stakato, splinter_stakato_worker, splinter_stakato_soldier, splinter_stakato_drone, splinter_stakato_drone_a, needle_stakato, needle_stakato_worker, needle_stakato_soldier, needle_stakato_drone, needle_stakato_drone_a);
        addQuestItem(q_stakato_talons);
        addLevelCheck(66, 80);
        addQuestCompletedCheck(628);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetHTMLCookie = st.getInt("clean_up_swamp_cookie");
        int npcId = npc.getNpcId();

        if (npcId == merc_cap_peace)
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("clean_up_swamp", String.valueOf(1 * 10 + 1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "merc_cap_peace_q0629_0104.htm";
            } else if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 2 - 1)
                htmltext = "merc_cap_peace_q0629_0201.htm";
            else if (event.equalsIgnoreCase("reply_3") && GetHTMLCookie == 2 - 1) {
                if (st.ownItemCount(q_stakato_talons) >= 100) {
                    int i1 = Rnd.get(1000);
                    st.takeItems(q_stakato_talons, 100);
                    if (i1 < 1000)
                        st.giveItems(q_goldenram_coin, 20);
                    htmltext = "merc_cap_peace_q0629_0202.htm";
                } else
                    htmltext = "merc_cap_peace_q0629_0203.htm";
            } else if (event.equalsIgnoreCase("reply_4") && GetHTMLCookie == 2 - 1) {
                st.removeMemo("clean_up_swamp");
                st.removeMemo("clean_up_swamp_cookie");
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                htmltext = "merc_cap_peace_q0629_0204.htm";
            }

        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("clean_up_swamp");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == merc_cap_peace) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                        case QUEST:
                            htmltext = "merc_cap_peace_q0629_0103.htm";
                            break;
                        default:
                            htmltext = "merc_cap_peace_q0629_0101.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == merc_cap_peace)
                    if (GetMemoState == 1 * 10 + 1)
                        if (st.ownItemCount(q_stakato_talons) == 0)
                            htmltext = "merc_cap_peace_q0629_0106.htm";
                        else {
                            st.setMemoState("clean_up_swamp_cookie", String.valueOf(1), true);
                            htmltext = "merc_cap_peace_q0629_0105.htm";
                        }
                break;
        }

        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("clean_up_swamp");
        int npcId = npc.getNpcId();

        if (GetMemoState == 1 * 10 + 1)
            if (npcId == splinter_stakato) {
                int i4 = Rnd.get(1000);
                if (i4 < 599 && 599 != 0) {
                    st.giveItems(q_stakato_talons, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == splinter_stakato_worker) {
                int i4 = Rnd.get(1000);
                if (i4 < 524 && 524 != 0) {
                    st.giveItems(q_stakato_talons, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == splinter_stakato_soldier) {
                int i4 = Rnd.get(1000);
                if (i4 < 640 && 640 != 0) {
                    st.giveItems(q_stakato_talons, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == splinter_stakato_drone) {
                int i4 = Rnd.get(1000);
                if (i4 < 830 && 830 != 0) {
                    st.giveItems(q_stakato_talons, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == splinter_stakato_drone_a) {
                int i4 = Rnd.get(1000);
                if (i4 < 970 && 970 != 0) {
                    st.giveItems(q_stakato_talons, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == needle_stakato) {
                int i4 = Rnd.get(1000);
                if (i4 < 682 && 682 != 0) {
                    st.giveItems(q_stakato_talons, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == needle_stakato_worker) {
                int i4 = Rnd.get(1000);
                if (i4 < 595 && 595 != 0) {
                    st.giveItems(q_stakato_talons, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == needle_stakato_soldier) {
                int i4 = Rnd.get(1000);
                if (i4 < 727 && 727 != 0) {
                    st.giveItems(q_stakato_talons, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == needle_stakato_drone) {
                int i4 = Rnd.get(1000);
                if (i4 < 879 && 879 != 0) {
                    st.giveItems(q_stakato_talons, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == needle_stakato_drone_a) {
                int i4 = Rnd.get(1000);
                if (i4 < 999 && 999 != 0) {
                    st.giveItems(q_stakato_talons, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }

        return null;
    }
}