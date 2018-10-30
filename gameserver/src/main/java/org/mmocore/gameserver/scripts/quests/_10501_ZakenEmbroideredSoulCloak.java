package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

/**
 * Based on official High Five
 *
 * @author Magister
 * @version 1.0
 * @date 15/01/2015
 */
public class _10501_ZakenEmbroideredSoulCloak extends Quest {
    // npc
    private static final int weaver_wolf_adams = 32612;
    // mobs
    private static final int zaken_day_83 = 29181;
    // questitem
    private static final int g_q_soulpiece_of_zaken = 21722;
    // etcitem
    private static final int g_zaken_cloak_b = 21719;

    public _10501_ZakenEmbroideredSoulCloak() {
        super(PARTY_CC);
        addStartNpc(weaver_wolf_adams);
        addKillId(zaken_day_83);
        addQuestItem(g_q_soulpiece_of_zaken);
        addLevelCheck(78);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == weaver_wolf_adams) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("cloak_of_soul_1", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "weaver_wolf_adams_q10501_04.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("cloak_of_soul_1");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == weaver_wolf_adams) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "weaver_wolf_adams_q10501_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "weaver_wolf_adams_q10501_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == weaver_wolf_adams) {
                    if (GetMemoState == 1 && st.ownItemCount(g_q_soulpiece_of_zaken) <= 19)
                        htmltext = "weaver_wolf_adams_q10501_05.htm";
                    else if (GetMemoState == 1 && st.ownItemCount(g_q_soulpiece_of_zaken) >= 20) {
                        st.giveItems(g_zaken_cloak_b, 1);
                        st.takeItems(g_q_soulpiece_of_zaken, -1);
                        st.removeMemo("cloak_of_soul_1");
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                        htmltext = "weaver_wolf_adams_q10501_06.htm";
                    }
                }
                break;
            case COMPLETED:
                if (npcId == weaver_wolf_adams)
                    htmltext = "weaver_wolf_adams_q10501_03.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("cloak_of_soul_1");
        int npcId = npc.getNpcId();
        if (npcId == zaken_day_83) {
            if (GetMemoState == 1 && st.ownItemCount(g_q_soulpiece_of_zaken) < 20) {
                if (st.ownItemCount(g_q_soulpiece_of_zaken) == 19) {
                    st.setCond(2);
                    st.giveItems(g_q_soulpiece_of_zaken, 1);
                    st.soundEffect(SOUND_MIDDLE);
                } else if (Rnd.get(2) == 0) {
                    if (st.ownItemCount(g_q_soulpiece_of_zaken) == 18) {
                        st.setCond(2);
                        st.giveItems(g_q_soulpiece_of_zaken, 2);
                        st.soundEffect(SOUND_MIDDLE);
                    } else {
                        st.giveItems(g_q_soulpiece_of_zaken, 2);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                } else {
                    st.giveItems(g_q_soulpiece_of_zaken, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        }
        return null;
    }
}