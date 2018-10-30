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
public class _238_SuccessFailureOfBusiness extends Quest {
    // npc
    private static final int gakka = 32641;

    // mobs
    private static final int brazier_of_peace = 18806;
    private static final int disturbed_waterspirit = 22658;
    private static final int angry_waterspirit = 22659;

    // questitem
    private static final int q_letter_of_confirmation_dwarf = 14865;
    private static final int q_piece_of_torch = 14867;
    private static final int q_piece_of_guardian_spirit = 14868;

    public _238_SuccessFailureOfBusiness() {
        super(false);
        addStartNpc(gakka);
        addKillId(brazier_of_peace, disturbed_waterspirit, angry_waterspirit);
        addQuestItem(q_piece_of_torch, q_piece_of_guardian_spirit);
        addLevelCheck(82);
        addQuestCompletedCheck(237);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("fate_of_business");

        if (event.equalsIgnoreCase("quest_accept")) {
            st.setCond(1);
            st.setMemoState("fate_of_business", String.valueOf(1), true);
            st.setState(STARTED);
            htmltext = "gakka_q0238_06.htm";
        } else if (event.equalsIgnoreCase("reply_1"))
            htmltext = "gakka_q0238_05.htm";
        else if (event.equalsIgnoreCase("reply_2"))
            if (GetMemoState == 3) {
                st.setCond(3);
                st.setMemoState("fate_of_business", String.valueOf(4), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "gakka_q0238_11.htm";
            }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("fate_of_business");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == gakka) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                        case QUEST:
                            htmltext = "gakka_q0238_03.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            if (st.ownItemCount(q_letter_of_confirmation_dwarf) >= 1)
                                htmltext = "gakka_q0238_01.htm";
                            else {
                                htmltext = "gakka_q0238_02.htm";
                                st.exitQuest(true);
                            }
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == gakka)
                    if (GetMemoState == 1 && st.ownItemCount(q_piece_of_torch) < 1)
                        htmltext = "gakka_q0238_07.htm";
                    else if ((GetMemoState == 1 || GetMemoState == 2) && st.ownItemCount(q_piece_of_torch) >= 1 && st.ownItemCount(q_piece_of_torch) < 10) {
                        st.setCond(1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "gakka_q0238_08.htm";
                    } else if (GetMemoState == 2 && st.ownItemCount(q_piece_of_torch) >= 10) {
                        st.takeItems(q_piece_of_torch, -1);
                        st.setMemoState("fate_of_business", String.valueOf(3), true);
                        htmltext = "gakka_q0238_09.htm";
                    } else if (GetMemoState == 3)
                        htmltext = "gakka_q0238_10.htm";
                    else if (GetMemoState == 4 && st.ownItemCount(q_piece_of_guardian_spirit) < 1)
                        htmltext = "gakka_q0238_12.htm";
                    else if ((GetMemoState == 4 || GetMemoState == 5) && st.ownItemCount(q_piece_of_guardian_spirit) >= 1 && st.ownItemCount(q_piece_of_guardian_spirit) < 20) {
                        st.setCond(3);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "gakka_q0238_13.htm";
                    } else if (GetMemoState == 5 && st.ownItemCount(q_piece_of_guardian_spirit) >= 20) {
                        st.giveItems(ADENA_ID, 283346);
                        st.addExpAndSp(1319736, 103553);
                        st.takeItems(q_letter_of_confirmation_dwarf, -1);
                        st.takeItems(q_piece_of_guardian_spirit, -1);
                        st.removeMemo("fate_of_business");
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                        htmltext = "gakka_q0238_14.htm";
                    }
                break;
            case COMPLETED:
                if (npcId == gakka)
                    htmltext = "gakka_q0238_04.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("fate_of_business");
        int npcId = npc.getNpcId();

        if ((GetMemoState == 1 || GetMemoState == 2) && npcId == brazier_of_peace) {
            if (st.ownItemCount(q_piece_of_torch) < 9) {
                st.giveItems(q_piece_of_torch, 1);
                st.soundEffect(SOUND_ITEMGET);
            } else if (st.ownItemCount(q_piece_of_torch) == 9) {
                st.setCond(2);
                st.setMemoState("fate_of_business", String.valueOf(2), true);
                st.giveItems(q_piece_of_torch, 1);
                st.soundEffect(SOUND_MIDDLE);
            }
        } else if ((GetMemoState == 4 || GetMemoState == 5) && npcId == disturbed_waterspirit) {
            int i0 = Rnd.get(1000);
            if (i0 < 800)
                if (st.ownItemCount(q_piece_of_guardian_spirit) < 19) {
                    st.giveItems(q_piece_of_guardian_spirit, 1);
                    st.soundEffect(SOUND_ITEMGET);
                } else if (st.ownItemCount(q_piece_of_guardian_spirit) == 19) {
                    st.setCond(4);
                    st.setMemoState("fate_of_business", String.valueOf(5), true);
                    st.giveItems(q_piece_of_guardian_spirit, 1);
                    st.soundEffect(SOUND_MIDDLE);
                }
        } else if ((GetMemoState == 4 || GetMemoState == 5) && npcId == angry_waterspirit) {
            int i0 = Rnd.get(1000);
            if (i0 < 820)
                if (st.ownItemCount(q_piece_of_guardian_spirit) < 19) {
                    st.giveItems(q_piece_of_guardian_spirit, 1);
                    st.soundEffect(SOUND_ITEMGET);
                } else if (st.ownItemCount(q_piece_of_guardian_spirit) == 19) {
                    st.setCond(4);
                    st.setMemoState("fate_of_business", String.valueOf(5), true);
                    st.giveItems(q_piece_of_guardian_spirit, 1);
                    st.soundEffect(SOUND_MIDDLE);
                }
        }
        return null;
    }
}