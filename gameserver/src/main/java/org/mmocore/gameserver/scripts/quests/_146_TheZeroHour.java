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
 * @version 1.0
 * @date 04/01/2015
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _146_TheZeroHour extends Quest {
    // npc
    private static final int merc_kahmun = 31554;
    // mobs
    private static final int n_spike_stakato_qn_shyid = 25671;
    // questitem
    private static final int q_claw_of_shyid_re = 14859;
    // etcitem
    private static final int q_box_of_merc_kahmun = 14849;

    public _146_TheZeroHour() {
        super(true);
        addStartNpc(merc_kahmun);
        addKillId(n_spike_stakato_qn_shyid);
        addQuestItem(q_claw_of_shyid_re);
        addLevelCheck(81);
        addQuestCompletedCheck(109);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == merc_kahmun) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("the_decisive_stage_of_war_re", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "merc_kahmun_q0146_103a.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("the_decisive_stage_of_war_re");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == merc_kahmun) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "merc_kahmun_q0146_102a.htm";
                            st.exitQuest(true);
                            break;
                        case QUEST:
                            htmltext = "merc_kahmun_q0146_104a.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "merc_kahmun_q0146_101a.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == merc_kahmun) {
                    if (GetMemoState == 2 && st.ownItemCount(q_claw_of_shyid_re) >= 1) {
                        st.takeItems(q_claw_of_shyid_re, -1);
                        st.giveItems(q_box_of_merc_kahmun, 1);
                        st.addExpAndSp(154616, 12500);
                        st.removeMemo("the_decisive_stage_of_war_re");
                        st.exitQuest(false);
                        st.soundEffect(SOUND_FINISH);
                        htmltext = "merc_kahmun_q0146_105a.htm";
                    } else if (GetMemoState == 2 && st.ownItemCount(q_claw_of_shyid_re) < 1)
                        htmltext = "merc_kahmun_q0146_106a.htm";
                }
                break;
            case COMPLETED:
                if (npcId == merc_kahmun)
                    htmltext = "merc_kahmun_q0146_101b.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("the_decisive_stage_of_war_re");
        int npcId = npc.getNpcId();
        if (npcId == n_spike_stakato_qn_shyid) {
            if (GetMemoState == 1) {
                if (st.ownItemCount(q_claw_of_shyid_re) < 1) {
                    st.setCond(2);
                    st.setMemoState("the_decisive_stage_of_war_re", String.valueOf(2), true);
                    st.giveItems(q_claw_of_shyid_re, 1);
                    st.soundEffect(SOUND_MIDDLE);
                }
            }
        }
        return null;
    }
}