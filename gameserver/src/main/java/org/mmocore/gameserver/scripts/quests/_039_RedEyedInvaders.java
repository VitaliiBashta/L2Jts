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
public class _039_RedEyedInvaders extends Quest {
    // npc
    private final static int guard_babenco = 30334;
    private final static int captain_bathia = 30332;

    // mobs
    private final static int male_lizardman = 20919;
    private final static int male_lizardman_scout = 20920;
    private final static int male_lizardman_guard = 20921;
    private final static int giant_araneid = 20925;

    // questitem
    private final static int q_mel_liz_necklace_a = 7178;
    private final static int q_mel_liz_necklace_b = 7179;
    private final static int q_mel_liz_perfume = 7180;
    private final static int q_mel_liz_gem = 7181;

    // etcitem
    private final static int green_lure_high = 6521;
    private final static int fp_babyduck_rod = 6529;
    private final static int fishing_shot_none = 6535;

    public _039_RedEyedInvaders() {
        super(false);
        addStartNpc(guard_babenco);
        addTalkId(captain_bathia);
        addKillId(male_lizardman, male_lizardman_scout, male_lizardman_guard, giant_araneid);
        addQuestItem(q_mel_liz_necklace_a, q_mel_liz_necklace_b, q_mel_liz_perfume, q_mel_liz_gem);
        addLevelCheck(20, 28);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetHTMLCookie = st.getInt("red_eyed_invaders_cookie");
        int npcId = npc.getNpcId();

        if (npcId == guard_babenco) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("red_eyed_invaders", String.valueOf(1 * 10 + 1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "guard_babenco_q0039_0104.htm";
            }
        } else if (npcId == captain_bathia) {
            if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 2 - 1) {
                st.setCond(2);
                st.setMemoState("red_eyed_invaders", String.valueOf(2 * 10 + 1), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "captain_bathia_q0039_0201.htm";
            } else if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 3 - 1) {
                if (st.ownItemCount(q_mel_liz_necklace_a) >= 100 && st.ownItemCount(q_mel_liz_necklace_b) >= 100) {
                    st.setCond(4);
                    st.setMemoState("red_eyed_invaders", String.valueOf(3 * 10 + 1), true);
                    st.takeItems(q_mel_liz_necklace_a, 100);
                    st.takeItems(q_mel_liz_necklace_b, 100);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "captain_bathia_q0039_0301.htm";
                } else
                    htmltext = "captain_bathia_q0039_0302.htm";
            } else if (event.equalsIgnoreCase("reply_3") && GetHTMLCookie == 4 - 1) {
                if (st.ownItemCount(q_mel_liz_perfume) >= 30 && st.ownItemCount(q_mel_liz_gem) >= 30) {
                    st.takeItems(q_mel_liz_perfume, -1);
                    st.takeItems(q_mel_liz_gem, -1);
                    st.giveItems(fp_babyduck_rod, 1);
                    st.giveItems(fishing_shot_none, 500);
                    st.giveItems(green_lure_high, 60);
                    st.addExpAndSp(62366, 2783);
                    st.removeMemo("red_eyed_invaders");
                    st.removeMemo("red_eyed_invaders_cookie");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "captain_bathia_q0039_0401.htm";
                } else
                    htmltext = "captain_bathia_q0039_0402.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("red_eyed_invaders");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == guard_babenco) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "guard_babenco_q0039_0103.htm";
                            break;
                        default:
                            htmltext = "guard_babenco_q0039_0101.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == guard_babenco) {
                    if (GetMemoState == 1 * 10 + 1)
                        htmltext = "guard_babenco_q0039_0105.htm";
                } else if (npcId == captain_bathia) {
                    if (GetMemoState == 1 * 10 + 1) {
                        st.setMemoState("red_eyed_invaders_cookie", String.valueOf(1), true);
                        htmltext = "captain_bathia_q0039_0101.htm";
                    } else if (GetMemoState <= 2 * 10 + 2 && GetMemoState >= 2 * 10 + 1) {
                        if (GetMemoState == 2 * 10 + 2 && st.ownItemCount(q_mel_liz_necklace_a) >= 100 && st.ownItemCount(q_mel_liz_necklace_b) >= 100) {
                            st.setMemoState("red_eyed_invaders_cookie", String.valueOf(2), true);
                            htmltext = "captain_bathia_q0039_0202.htm";
                        } else
                            htmltext = "captain_bathia_q0039_0203.htm";
                    } else if (GetMemoState <= 3 * 10 + 2 && GetMemoState >= 3 * 10 + 1) {
                        if (GetMemoState == 3 * 10 + 2 && st.ownItemCount(q_mel_liz_perfume) >= 30 && st.ownItemCount(q_mel_liz_gem) >= 30) {
                            st.setMemoState("red_eyed_invaders_cookie", String.valueOf(3), true);
                            htmltext = "captain_bathia_q0039_0303.htm";
                        } else
                            htmltext = "captain_bathia_q0039_0304.htm";
                    }
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("red_eyed_invaders");
        int npcId = npc.getNpcId();

        if (GetMemoState == 2 * 10 + 1) {
            if (npcId == male_lizardman || npcId == male_lizardman_scout) {
                int i4 = Rnd.get(1000);
                if (i4 < 500) {
                    if (st.ownItemCount(q_mel_liz_necklace_a) + 1 >= 100) {
                        if (st.ownItemCount(q_mel_liz_necklace_a) < 100) {
                            st.giveItems(q_mel_liz_necklace_a, 100 - st.ownItemCount(q_mel_liz_necklace_a));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        if (st.ownItemCount(q_mel_liz_necklace_b) >= 100) {
                            st.setCond(3);
                            st.setMemoState("red_eyed_invaders", String.valueOf(2 * 10 + 2), true);
                        }
                    } else {
                        st.giveItems(q_mel_liz_necklace_a, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            } else if (npcId == male_lizardman_guard) {
                int i4 = Rnd.get(1000);
                if (i4 < 500) {
                    if (st.ownItemCount(q_mel_liz_necklace_b) + 1 >= 100) {
                        if (st.ownItemCount(q_mel_liz_necklace_b) < 100) {
                            st.giveItems(q_mel_liz_necklace_b, 100 - st.ownItemCount(q_mel_liz_necklace_b));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        if (st.ownItemCount(q_mel_liz_necklace_a) >= 100) {
                            st.setCond(3);
                            st.setMemoState("red_eyed_invaders", String.valueOf(2 * 10 + 2), true);
                        }
                    } else {
                        st.giveItems(q_mel_liz_necklace_b, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        } else if (GetMemoState == 3 * 10 + 1) {
            if (npcId == giant_araneid) {
                int i4 = Rnd.get(1000);
                if (i4 < 500) {
                    if (st.ownItemCount(q_mel_liz_gem) + 1 >= 30) {
                        if (st.ownItemCount(q_mel_liz_gem) < 30) {
                            st.giveItems(q_mel_liz_gem, 30 - st.ownItemCount(q_mel_liz_gem));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        if (st.ownItemCount(q_mel_liz_perfume) >= 30) {
                            st.setCond(5);
                            st.setMemoState("red_eyed_invaders", String.valueOf(3 * 10 + 2), true);
                        }
                    } else {
                        st.giveItems(q_mel_liz_gem, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            } else if (npcId == male_lizardman_guard || npcId == male_lizardman_guard) {
                int i4 = Rnd.get(1000);
                if (i4 < 500) {
                    if (st.ownItemCount(q_mel_liz_perfume) + 1 >= 30) {
                        if (st.ownItemCount(q_mel_liz_perfume) < 30) {
                            st.giveItems(q_mel_liz_perfume, 30 - st.ownItemCount(q_mel_liz_perfume));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        if (st.ownItemCount(q_mel_liz_gem) >= 30) {
                            st.setCond(5);
                            st.setMemoState("red_eyed_invaders", String.valueOf(3 * 10 + 2), true);
                        }
                    } else {
                        st.giveItems(q_mel_liz_perfume, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        }
        return null;
    }
}