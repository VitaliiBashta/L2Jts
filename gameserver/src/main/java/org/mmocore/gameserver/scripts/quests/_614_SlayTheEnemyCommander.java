package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _614_SlayTheEnemyCommander extends Quest {
    // NPC
    private static final int DURAI = 31377;
    private static final int KETRAS_COMMANDER_TAYR = 25302;

    // etc
    @SuppressWarnings("unused")
    private static final int MARK_OF_VARKA_ALLIANCE1 = 7221;
    @SuppressWarnings("unused")
    private static final int MARK_OF_VARKA_ALLIANCE2 = 7222;
    @SuppressWarnings("unused")
    private static final int MARK_OF_VARKA_ALLIANCE3 = 7223;
    private static final int MARK_OF_VARKA_ALLIANCE4 = 7224;
    private static final int MARK_OF_VARKA_ALLIANCE5 = 7225;
    private static final int HEAD_OF_TAYR = 7241;
    private static final int FEATHER_OF_WISDOM = 7230;


    public _614_SlayTheEnemyCommander() {
        super(true);
        addStartNpc(DURAI);
        addKillId(KETRAS_COMMANDER_TAYR);
        addQuestItem(HEAD_OF_TAYR);
        addLevelCheck(75, 80);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equalsIgnoreCase("quest_accept")) {
            htmltext = "elder_ashas_barka_durai_q0614_0104.htm";
            st.setCond(1);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("614_3")) {
            if (st.ownItemCount(HEAD_OF_TAYR) >= 1) {
                htmltext = "elder_ashas_barka_durai_q0614_0201.htm";
                st.takeItems(HEAD_OF_TAYR, -1);
                st.giveItems(FEATHER_OF_WISDOM, 1);
                st.addExpAndSp(0, 10000);
                st.removeMemo("cond");
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
            } else {
                htmltext = "elder_ashas_barka_durai_q0614_0106.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        if (cond == 0) {
            switch (isAvailableFor(st.getPlayer())) {
                case LEVEL:
                    htmltext = "elder_ashas_barka_durai_q0614_0103.htm";
                    st.exitQuest(true);
                    break;
                default:
                    if (st.ownItemCount(MARK_OF_VARKA_ALLIANCE4) == 1 || st.ownItemCount(MARK_OF_VARKA_ALLIANCE5) == 1)
                        htmltext = "elder_ashas_barka_durai_q0614_0101.htm";
                    else {
                        htmltext = "elder_ashas_barka_durai_q0614_0102.htm";
                        st.exitQuest(true);
                    }
                    break;
            }
        } else if (cond == 1 && st.ownItemCount(HEAD_OF_TAYR) == 0) {
            htmltext = "elder_ashas_barka_durai_q0614_0106.htm";
        } else if (cond == 2 && st.ownItemCount(HEAD_OF_TAYR) >= 1) {
            htmltext = "elder_ashas_barka_durai_q0614_0105.htm";
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 1) {
            st.giveItems(HEAD_OF_TAYR, 1);
            st.setCond(2);
            st.soundEffect(SOUND_ITEMGET);
        }
        return null;
    }
}