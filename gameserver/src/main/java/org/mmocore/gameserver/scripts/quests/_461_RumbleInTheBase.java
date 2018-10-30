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
 * @date 11/03/2015
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _461_RumbleInTheBase extends Quest {
    // npc
    private static final int stan = 30200;
    // npc
    private static final int ol_cooker = 18908;
    private static final int xel_recruit_mage = 22780;
    private static final int xel_recruit_high_mage = 22781;
    private static final int xel_recruit_warrior = 22782;
    private static final int xel_recruit_high_warrior = 22783;
    private static final int xel_recruit_sniper = 22784;
    private static final int xel_recruit_high_sniper = 22785;
    // questitem
    private static final int q_shoestring_of_xel = 16382;
    private static final int q_blingbling_salmon = 15503;

    public _461_RumbleInTheBase() {
        super(false);
        addStartNpc(stan);
        addQuestItem(q_shoestring_of_xel, q_blingbling_salmon);
        addKillId(ol_cooker, xel_recruit_mage, xel_recruit_high_mage, xel_recruit_warrior, xel_recruit_high_warrior, xel_recruit_sniper, xel_recruit_high_sniper);
        addLevelCheck(82);
        addQuestCompletedCheck(252);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == stan) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("tackle_for_foundation", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "stan_q0461_05.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=461&reply=1"))
                htmltext = "stan_q0461_04.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("tackle_for_foundation");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == stan) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                        case QUEST:
                            htmltext = "stan_q0461_02.htm";
                            break;
                        default:
                            if (st.isNowAvailable())
                                htmltext = "stan_q0461_01.htm";
                            else
                                htmltext = "stan_q0461_03.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == stan) {
                    if (GetMemoState == 1 && (st.ownItemCount(q_shoestring_of_xel) < 10 || st.ownItemCount(q_blingbling_salmon) < 5))
                        htmltext = "stan_q0461_06.htm";
                    else if (GetMemoState == 1 && st.ownItemCount(q_shoestring_of_xel) >= 10 && st.ownItemCount(q_blingbling_salmon) >= 5) {
                        st.takeItems(q_shoestring_of_xel, -1);
                        st.takeItems(q_blingbling_salmon, -1);
                        st.addExpAndSp(224784, 342528);
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(this);
                        htmltext = "stan_q0461_07.htm";
                    }
                }
                break;
        }
        return htmltext;
    }

    public void checkItems(QuestState st) {
        if (st.ownItemCount(q_blingbling_salmon) + st.ownItemCount(q_shoestring_of_xel) == 15) {
            st.setCond(2);
            st.soundEffect(SOUND_MIDDLE);
        }
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("tackle_for_foundation");
        int npcId = npc.getNpcId();
        if (npcId == ol_cooker) {
            if (GetMemoState == 1 && st.ownItemCount(q_blingbling_salmon) <= 4) {
                int i0 = Rnd.get(1000);
                if (i0 < 782) {
                    st.giveItems(q_blingbling_salmon, 1);
                    st.soundEffect(SOUND_ITEMGET);
                    checkItems(st);
                }
            }
        } else if (npcId == xel_recruit_mage || npcId == xel_recruit_warrior || npcId == xel_recruit_sniper) {
            if (GetMemoState == 1 && st.ownItemCount(q_shoestring_of_xel) <= 9) {
                int i0 = Rnd.get(1000);
                if (i0 < 581) {
                    st.giveItems(q_shoestring_of_xel, 1);
                    st.soundEffect(SOUND_ITEMGET);
                    checkItems(st);
                }
            }
        } else if (npcId == xel_recruit_high_mage) {
            if (GetMemoState == 1 && st.ownItemCount(q_shoestring_of_xel) <= 9) {
                int i0 = Rnd.get(1000);
                if (i0 < 772) {
                    st.giveItems(q_shoestring_of_xel, 1);
                    st.soundEffect(SOUND_ITEMGET);
                    checkItems(st);
                }
            }
        } else if (npcId == xel_recruit_high_warrior) {
            if (GetMemoState == 1 && st.ownItemCount(q_shoestring_of_xel) <= 9) {
                int i0 = Rnd.get(1000);
                if (i0 < 563) {
                    st.giveItems(q_shoestring_of_xel, 1);
                    st.soundEffect(SOUND_ITEMGET);
                    checkItems(st);
                }
            }
        } else if (npcId == xel_recruit_high_sniper) {
            if (GetMemoState == 1 && st.ownItemCount(q_shoestring_of_xel) <= 9) {
                int i0 = Rnd.get(1000);
                if (i0 < 271) {
                    st.giveItems(q_shoestring_of_xel, 1);
                    st.soundEffect(SOUND_ITEMGET);
                    checkItems(st);
                }
            }
        }
        return null;
    }
}