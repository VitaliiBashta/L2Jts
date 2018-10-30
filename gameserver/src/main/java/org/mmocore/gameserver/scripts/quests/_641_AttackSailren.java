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
 * @version 1.0
 * @date 04/12/2014
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _641_AttackSailren extends Quest {
    // npc
    private final static int statue_of_shilen = 32109;
    // mobs
    private final static int velociraptor_leader = 22196;
    private final static int velociraptor = 22197;
    private final static int velociraptor_s = 22198;
    private final static int rhamphorhynchus = 22199;
    private final static int velociraptor_n = 22218;
    private final static int velociraptor_leader2 = 22223;
    // questitem
    private final static int q_piece_of_gazk = 8782;
    private final static int q_head_of_gazk = 8784;

    public _641_AttackSailren() {
        super(true);
        addStartNpc(statue_of_shilen);
        addKillId(velociraptor_leader, velociraptor, velociraptor_s, rhamphorhynchus, velociraptor_n, velociraptor_leader2);
        addQuestItem(q_piece_of_gazk);
        addLevelCheck(77);
        addQuestCompletedCheck(126);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == statue_of_shilen) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("rush_the_cyrenn", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "statue_of_shilen_q0641_05.htm";
            } else if (event.equalsIgnoreCase("reply_2"))
                htmltext = "statue_of_shilen_q0641_04.htm";
            else if (event.equalsIgnoreCase("reply_4")) {
                if (st.ownItemCount(q_piece_of_gazk) >= 30) {
                    st.giveItems(q_head_of_gazk, 1);
                    st.takeItems(q_piece_of_gazk, -1);
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(true);
                    st.removeMemo("rush_the_cyrenn");
                    htmltext = "statue_of_shilen_q0641_08.htm";
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == statue_of_shilen) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "statue_of_shilen_q0641_03.htm";
                            st.exitQuest(true);
                            break;
                        case QUEST:
                            htmltext = "statue_of_shilen_q0641_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "statue_of_shilen_q0641_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == statue_of_shilen) {
                    if (st.ownItemCount(q_piece_of_gazk) < 30)
                        htmltext = "statue_of_shilen_q0641_06.htm";
                    else if (st.ownItemCount(q_piece_of_gazk) >= 30)
                        htmltext = "statue_of_shilen_q0641_07.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("rush_the_cyrenn");
        int npcId = npc.getNpcId();
        if (GetMemoState == 1) {
            if (npcId == velociraptor_leader || npcId == velociraptor || npcId == velociraptor_s || npcId == rhamphorhynchus || npcId == velociraptor_n || npcId == velociraptor_leader2) {
                int i4 = Rnd.get(1000);
                if (i4 < 1000 && st.ownItemCount(q_piece_of_gazk) <= 29) {
                    if (st.ownItemCount(q_piece_of_gazk) < 29) {
                        st.giveItems(q_piece_of_gazk, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    } else if (st.ownItemCount(q_piece_of_gazk) == 29) {
                        st.setCond(2);
                        st.giveItems(q_piece_of_gazk, 1);
                        st.soundEffect(SOUND_MIDDLE);
                    }
                }
            }
        }
        return null;
    }
}