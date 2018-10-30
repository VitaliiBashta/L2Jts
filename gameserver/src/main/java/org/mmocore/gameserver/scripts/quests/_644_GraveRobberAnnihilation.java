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
public class _644_GraveRobberAnnihilation extends Quest {
    // npc
    private final static int karuda = 32017;

    // mobs
    private final static int thief_patroller_archer = 22003;
    private final static int thief_patroller = 22004;
    private final static int thief_guard = 22005;
    private final static int grave_robber_swordscout = 22006;
    private final static int thief_fighter = 22008;

    // questitem
    private final static int q_grave_goods_of_orc = 8088;

    // etcitem
    private final static int varnish = 1865;
    private final static int animal_skin = 1867;
    private final static int animal_bone = 1872;
    private final static int charcoal = 1871;
    private final static int coal = 1870;
    private final static int iron_ore = 1869;

    public _644_GraveRobberAnnihilation() {
        super(false);
        addStartNpc(karuda);
        addKillId(thief_patroller_archer, thief_patroller, thief_guard, grave_robber_swordscout, thief_fighter);
        addQuestItem(q_grave_goods_of_orc);
        addLevelCheck(20, 33);

    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("sweep_the_snatcher");
        int GetHTMLCookie = st.getInt("sweep_the_snatcher_cookie");
        int npcId = npc.getNpcId();

        if (npcId == karuda)
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("sweep_the_snatcher", String.valueOf(1 * 10 + 1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "karuda_q0644_0103.htm";
            } else if (event.equalsIgnoreCase("reply_3") && GetHTMLCookie == 2 - 1 && GetMemoState >= (2 - 1) * 10 + 1)
                htmltext = "karuda_q0644_0201.htm";
            else if (GetMemoState >= (2 - 1) * 10 + 1)
                if (st.ownItemCount(q_grave_goods_of_orc) >= 120) {
                    st.takeItems(q_grave_goods_of_orc, 120);
                    if (event.equalsIgnoreCase("reply_11"))
                        st.giveItems(varnish, 30);
                    else if (event.equalsIgnoreCase("reply_12"))
                        st.giveItems(animal_skin, 40);
                    else if (event.equalsIgnoreCase("reply_13"))
                        st.giveItems(animal_bone, 40);
                    else if (event.equalsIgnoreCase("reply_14"))
                        st.giveItems(charcoal, 30);
                    else if (event.equalsIgnoreCase("reply_15"))
                        st.giveItems(coal, 30);
                    else if (event.equalsIgnoreCase("reply_16"))
                        st.giveItems(iron_ore, 30);
                    st.removeMemo("sweep_the_snatcher");
                    st.removeMemo("sweep_the_snatcher_cookie");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(true);
                    htmltext = "karuda_q0644_0202.htm";
                }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("sweep_the_snatcher");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == karuda) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "karuda_q0644_0102.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "karuda_q0644_0101.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == karuda)
                    if (GetMemoState >= 1 * 10 + 1 && GetMemoState <= 1 * 10 + 2)
                        if (GetMemoState == 1 * 10 + 2 && st.ownItemCount(q_grave_goods_of_orc) >= 120) {
                            st.setMemoState("sweep_the_snatcher_cookie", String.valueOf(1), true);
                            htmltext = "karuda_q0644_0105.htm";
                        } else
                            htmltext = "karuda_q0644_0106.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("sweep_the_snatcher");
        int npcId = npc.getNpcId();

        if (GetMemoState == 1 * 10 + 1)
            if (npcId == thief_patroller_archer) {
                int i4 = Rnd.get(1000);
                if (i4 < 714)
                    if (st.ownItemCount(q_grave_goods_of_orc) + 1 >= 120) {
                        if (st.ownItemCount(q_grave_goods_of_orc) < 120) {
                            st.giveItems(q_grave_goods_of_orc, 120 - st.ownItemCount(q_grave_goods_of_orc));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        st.setCond(2);
                        st.setMemoState("sweep_the_snatcher", String.valueOf(1 * 10 + 2), true);
                    } else {
                        st.giveItems(q_grave_goods_of_orc, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            } else if (npcId == thief_patroller) {
                int i4 = Rnd.get(1000);
                if (i4 < 841)
                    if (st.ownItemCount(q_grave_goods_of_orc) + 1 >= 120) {
                        if (st.ownItemCount(q_grave_goods_of_orc) < 120) {
                            st.giveItems(q_grave_goods_of_orc, 120 - st.ownItemCount(q_grave_goods_of_orc));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        st.setCond(2);
                        st.setMemoState("sweep_the_snatcher", String.valueOf(1 * 10 + 2), true);
                    } else {
                        st.giveItems(q_grave_goods_of_orc, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            } else if (npcId == thief_guard) {
                int i4 = Rnd.get(1000);
                if (i4 < 746)
                    if (st.ownItemCount(q_grave_goods_of_orc) + 1 >= 120) {
                        if (st.ownItemCount(q_grave_goods_of_orc) < 120) {
                            st.giveItems(q_grave_goods_of_orc, 120 - st.ownItemCount(q_grave_goods_of_orc));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        st.setCond(2);
                        st.setMemoState("sweep_the_snatcher", String.valueOf(1 * 10 + 2), true);
                    } else {
                        st.giveItems(q_grave_goods_of_orc, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            } else if (npcId == grave_robber_swordscout) {
                int i4 = Rnd.get(1000);
                if (i4 < 778)
                    if (st.ownItemCount(q_grave_goods_of_orc) + 1 >= 120) {
                        if (st.ownItemCount(q_grave_goods_of_orc) < 120) {
                            st.giveItems(q_grave_goods_of_orc, 120 - st.ownItemCount(q_grave_goods_of_orc));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        st.setCond(2);
                        st.setMemoState("sweep_the_snatcher", String.valueOf(1 * 10 + 2), true);
                    } else {
                        st.giveItems(q_grave_goods_of_orc, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            } else if (npcId == thief_fighter) {
                int i4 = Rnd.get(1000);
                if (i4 < 810)
                    if (st.ownItemCount(q_grave_goods_of_orc) + 1 >= 120) {
                        if (st.ownItemCount(q_grave_goods_of_orc) < 120) {
                            st.giveItems(q_grave_goods_of_orc, 120 - st.ownItemCount(q_grave_goods_of_orc));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        st.setCond(2);
                        st.setMemoState("sweep_the_snatcher", String.valueOf(1 * 10 + 2), true);
                    } else {
                        st.giveItems(q_grave_goods_of_orc, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            }
        return null;
    }
}