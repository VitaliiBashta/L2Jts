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
public class _637_ThroughTheGateOnceMore extends Quest {
    // npc
    private final static int falsepriest_flauron = 32010;

    // mobs
    private final static int bone_animator = 21565;
    private final static int skull_animator = 21566;
    private final static int bone_slayer = 21567;

    // questitem
    private final static int q_heart_of_reanimated = 8066;
    private final static int q_mark_of_sacrifice = 8064;
    private final static int q_faded_mark_of_sac = 8065;
    private final static int q_mark_of_heresy = 8067;

    // etcitem
    private final static int q_key_of_anteroom = 8273;

    public _637_ThroughTheGateOnceMore() {
        super(false);
        addStartNpc(falsepriest_flauron);
        addKillId(bone_animator, skull_animator, bone_slayer);
        addQuestItem(q_heart_of_reanimated);
        addLevelCheck(73, 80);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();

        if (npcId == falsepriest_flauron)
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("beyond_the_door_again", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "falsepriest_flauron_q0637_11.htm";
            } else if (event.equalsIgnoreCase("reply_1"))
                htmltext = "falsepriest_flauron_q0637_06.htm";
            else if (event.equalsIgnoreCase("reply_2"))
                htmltext = "falsepriest_flauron_q0637_07.htm";
            else if (event.equalsIgnoreCase("reply_3"))
                htmltext = "falsepriest_flauron_q0637_08.htm";
            else if (event.equalsIgnoreCase("reply_4"))
                htmltext = "falsepriest_flauron_q0637_09.htm";
            else if (event.equalsIgnoreCase("reply_5"))
                htmltext = "falsepriest_flauron_q0637_10.htm";
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("beyond_the_door_again");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == falsepriest_flauron) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "falsepriest_flauron_q0637_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            if (st.ownItemCount(q_faded_mark_of_sac) >= 1 && st.ownItemCount(q_mark_of_heresy) == 0)
                                htmltext = "falsepriest_flauron_q0637_01.htm";
                            else {
                                htmltext = "falsepriest_flauron_q0637_02.htm";
                                st.exitQuest(true);
                            }
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == falsepriest_flauron)
                    if (GetMemoState == 1 && st.ownItemCount(q_mark_of_sacrifice) >= 1 && st.ownItemCount(q_faded_mark_of_sac) == 0 && st.ownItemCount(q_mark_of_heresy) == 0)
                        htmltext = "falsepriest_flauron_q0637_03.htm";
                    else if (GetMemoState == 1 && st.ownItemCount(q_mark_of_sacrifice) == 0 && st.ownItemCount(q_faded_mark_of_sac) == 0 && st.ownItemCount(q_mark_of_heresy) == 0)
                        htmltext = "falsepriest_flauron_q0637_04.htm";
                    else if (GetMemoState == 1 && st.ownItemCount(q_mark_of_heresy) >= 1)
                        htmltext = "falsepriest_flauron_q0637_05.htm";
                    else if (GetMemoState == 1 && st.ownItemCount(q_heart_of_reanimated) < 10)
                        htmltext = "falsepriest_flauron_q0637_12.htm";
                    else if (GetMemoState == 1 && st.ownItemCount(q_heart_of_reanimated) >= 10) {
                        st.giveItems(q_mark_of_heresy, 1);
                        st.giveItems(q_key_of_anteroom, 10);
                        st.takeItems(q_mark_of_sacrifice, -1);
                        st.takeItems(q_faded_mark_of_sac, -1);
                        st.takeItems(q_heart_of_reanimated, -1);
                        st.removeMemo("beyond_the_door_again");
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(true);
                        htmltext = "falsepriest_flauron_q0637_13.htm";
                    }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("beyond_the_door_again");
        int npcId = npc.getNpcId();

        if (GetMemoState == 1 && st.ownItemCount(q_heart_of_reanimated) < 10)
            if (npcId == bone_animator) {
                if (Rnd.get(100) < 84) {
                    st.giveItems(q_heart_of_reanimated, 1);
                    if (st.ownItemCount(q_heart_of_reanimated) >= 9) {
                        st.setCond(2);
                        st.soundEffect(SOUND_MIDDLE);
                    } else
                        st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == skull_animator) {
                if (Rnd.get(100) < 92) {
                    st.giveItems(q_heart_of_reanimated, 1);
                    if (st.ownItemCount(q_heart_of_reanimated) >= 9) {
                        st.setCond(2);
                        st.soundEffect(SOUND_MIDDLE);
                    } else
                        st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == bone_slayer)
                if (Rnd.get(100) < 10) {
                    st.giveItems(q_heart_of_reanimated, 1);
                    if (st.ownItemCount(q_heart_of_reanimated) >= 9) {
                        st.setCond(2);
                        st.soundEffect(SOUND_MIDDLE);
                    } else
                        st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_heart_of_reanimated, 1);
                    if (st.ownItemCount(q_heart_of_reanimated) >= 9) {
                        st.setCond(2);
                        st.soundEffect(SOUND_MIDDLE);
                    } else
                        st.soundEffect(SOUND_ITEMGET);
                }
        return null;
    }
}