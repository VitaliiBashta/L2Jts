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
public class _251_NoSecrets extends Quest {
    // npc
    private static final int pinaps = 30201;

    // mobs
    private static final int xel_trainer_mage = 22775;
    private static final int xel_trainer_warrior = 22777;
    private static final int xel_trainer_sniper = 22778;
    private static final int xel_recruit_mage = 22780;
    private static final int xel_recruit_high_mage = 22781;
    private static final int xel_recruit_warrior = 22782;
    private static final int xel_recruit_high_warrior = 22783;
    private static final int xel_recruit_sniper = 22784;
    private static final int xel_recruit_high_sniper = 22785;

    // questitem
    private static final int q_training_diary = 15508;
    private static final int q_training_timetablel = 15509;

    public _251_NoSecrets() {
        super(false);
        addStartNpc(pinaps);
        addKillId(xel_trainer_mage, xel_trainer_warrior, xel_trainer_sniper, xel_recruit_mage, xel_recruit_high_mage, xel_recruit_warrior, xel_recruit_high_warrior, xel_recruit_sniper, xel_recruit_high_sniper);
        addQuestItem(q_training_diary, q_training_timetablel);
        addLevelCheck(82);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;

        if (event.equalsIgnoreCase("quest_accept")) {
            st.setCond(1);
            st.setMemoState("no_secret", String.valueOf(1), true);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            htmltext = "pinaps_q0251_05.htm";
        } else if (event.equalsIgnoreCase("reply_1"))
            htmltext = "pinaps_q0251_04.htm";
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("no_secret");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == pinaps) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "pinaps_q0251_02.htm";
                            break;
                        default:
                            htmltext = "pinaps_q0251_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == pinaps)
                    if (GetMemoState == 1 && (st.ownItemCount(q_training_diary) < 10 || st.ownItemCount(q_training_timetablel) < 5))
                        htmltext = "pinaps_q0251_06.htm";
                    else if (GetMemoState == 1 && st.ownItemCount(q_training_diary) >= 10 && st.ownItemCount(q_training_timetablel) >= 5) {
                        st.giveItems(ADENA_ID, 313355);
                        st.addExpAndSp(56787, 160578);
                        st.takeItems(q_training_diary, -1);
                        st.takeItems(q_training_timetablel, -1);
                        st.removeMemo("no_secret");
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                        htmltext = "pinaps_q0251_07.htm";
                    }
                break;
            case COMPLETED:
                if (npcId == pinaps)
                    htmltext = "pinaps_q0251_03.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("no_secret");
        int npcId = npc.getNpcId();

        if (GetMemoState == 1)
            if (npcId == xel_trainer_mage) {
                int i0 = Rnd.get(1000);
                if (i0 < 870) {
                    st.giveItems(q_training_timetablel, 1);
                    st.soundEffect(SOUND_ITEMGET);
                } else if (i0 < 780) {
                    st.giveItems(q_training_timetablel, 1);
                    st.soundEffect(SOUND_ITEMGET);
                    if (st.ownItemCount(q_training_diary) >= 10) {
                        st.setCond(2);
                        st.soundEffect(SOUND_MIDDLE);
                    }
                }
            } else if (npcId == xel_trainer_warrior || npcId == xel_trainer_sniper) {
                int i0 = Rnd.get(1000);
                if (i0 < 870) {
                    st.giveItems(q_training_timetablel, 1);
                    st.soundEffect(SOUND_ITEMGET);
                    if (st.ownItemCount(q_training_diary) >= 10) {
                        st.setCond(2);
                        st.soundEffect(SOUND_MIDDLE);
                    }
                }
            } else if (npcId == xel_recruit_mage) {
                int i0 = Rnd.get(1000);
                if (i0 < 484) {
                    st.giveItems(q_training_diary, 1);
                    st.soundEffect(SOUND_ITEMGET);
                    if (st.ownItemCount(q_training_timetablel) >= 5) {
                        st.setCond(2);
                        st.soundEffect(SOUND_MIDDLE);
                    }
                }
            } else if (npcId == xel_recruit_high_mage) {
                int i0 = Rnd.get(1000);
                if (i0 < 643) {
                    st.giveItems(q_training_diary, 1);
                    st.soundEffect(SOUND_ITEMGET);
                    if (st.ownItemCount(q_training_timetablel) >= 5) {
                        st.setCond(2);
                        st.soundEffect(SOUND_MIDDLE);
                    }
                }
            } else if (npcId == xel_recruit_warrior || npcId == xel_recruit_sniper) {
                int i0 = Rnd.get(1000);
                if (i0 < 484) {
                    st.giveItems(q_training_diary, 1);
                    st.soundEffect(SOUND_ITEMGET);
                    if (st.ownItemCount(q_training_timetablel) >= 5) {
                        st.setCond(2);
                        st.soundEffect(SOUND_MIDDLE);
                    }
                }
            } else if (npcId == xel_recruit_high_warrior) {
                int i0 = Rnd.get(1000);
                if (i0 < 469) {
                    st.giveItems(q_training_diary, 1);
                    st.soundEffect(SOUND_ITEMGET);
                    if (st.ownItemCount(q_training_timetablel) >= 5) {
                        st.setCond(2);
                        st.soundEffect(SOUND_MIDDLE);
                    }
                }
            } else if (npcId == xel_recruit_high_sniper) {
                int i0 = Rnd.get(1000);
                if (i0 < 226) {
                    st.giveItems(q_training_diary, 1);
                    st.soundEffect(SOUND_ITEMGET);
                    if (st.ownItemCount(q_training_timetablel) >= 5) {
                        st.setCond(2);
                        st.soundEffect(SOUND_MIDDLE);
                    }
                }
            }
        return null;
    }
}