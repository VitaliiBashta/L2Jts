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
public class _10274_CollectingInTheAir extends Quest {
    // npc
    private final static int engineer_recon = 32557;

    // etcitem
    private final static int sb_star_stone_extraction_lv1 = 13728;

    // etcitem
    private final static int q_aerial_gathering_scroll = 13844;

    // questitem
    private final static int q_star_stone_piece_red = 13858;
    private final static int q_star_stone_piece_blue = 13859;
    private final static int q_star_stone_piece_green = 13860;

    public _10274_CollectingInTheAir() {
        super(false);
        addStartNpc(engineer_recon);
        addLevelCheck(75);
        addQuestCompletedCheck(10273);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;

        if (event.equalsIgnoreCase("quest_accept")) {
            st.setCond(1);
            st.setMemoState("lets_try_aerial_gathering", String.valueOf(1), true);
            st.giveItems(q_aerial_gathering_scroll, 8);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            htmltext = "engineer_recon_q10274_05.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("lets_try_aerial_gathering");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == engineer_recon) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                        case QUEST:
                            htmltext = "engineer_recon_q10274_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "engineer_recon_q10274_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == engineer_recon)
                    if (GetMemoState == 1 && st.ownItemCount(q_star_stone_piece_red) + st.ownItemCount(q_star_stone_piece_blue) + st.ownItemCount(q_star_stone_piece_green) < 1)
                        htmltext = "engineer_recon_q10274_06.htm";
                    else if (GetMemoState == 1 && st.ownItemCount(q_star_stone_piece_red) + st.ownItemCount(q_star_stone_piece_blue) + st.ownItemCount(q_star_stone_piece_green) >= 1 && st.ownItemCount(q_star_stone_piece_red) + st.ownItemCount(q_star_stone_piece_blue) + st.ownItemCount(q_star_stone_piece_green) < 8)
                        htmltext = "engineer_recon_q10274_07.htm";
                    else if (GetMemoState == 1 && st.ownItemCount(q_star_stone_piece_red) + st.ownItemCount(q_star_stone_piece_blue) + st.ownItemCount(q_star_stone_piece_green) >= 8) {
                        st.giveItems(sb_star_stone_extraction_lv1, 1);
                        st.addExpAndSp(25160, 2525);
                        st.takeItems(q_star_stone_piece_red, -1);
                        st.takeItems(q_star_stone_piece_blue, -1);
                        st.takeItems(q_star_stone_piece_green, -1);
                        st.exitQuest(false);
                        st.soundEffect(SOUND_FINISH);
                        st.removeMemo("lets_try_aerial_gathering");
                        htmltext = "engineer_recon_q10274_08.htm";
                    }
                break;
            case COMPLETED:
                if (npcId == engineer_recon)
                    htmltext = "engineer_recon_q10274_03.htm";
                break;
        }
        return htmltext;
    }
}