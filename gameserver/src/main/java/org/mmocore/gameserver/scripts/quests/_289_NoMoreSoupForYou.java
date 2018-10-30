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
public class _289_NoMoreSoupForYou extends Quest {
    // npc
    private static final int stan = 30200;

    // etcitem
    private static final int q_worst_taste_fruit = 15507;
    private static final int rp_icarus_trident_i = 10377;
    private static final int icarus_trident_piece = 10401;
    private static final int rp_sealed_destino_helmet_i = 15775;
    private static final int rp_sealed_destino_cuirass_i = 15778;
    private static final int rp_sealed_destino_gaiter_i = 15781;
    private static final int rp_sealed_destino_gauntlet_i = 15784;
    private static final int rp_sealed_destino_boots_i = 15787;
    private static final int rp_sealed_destino_shield_i = 15791;
    private static final int rp_sealed_destino_ring_i = 15812;
    private static final int rp_sealed_destino_earring_i = 15813;
    private static final int rp_sealed_destino_necklace_i = 15814;
    private static final int sealed_destino_helmet_piece = 15645;
    private static final int sealed_destino_cuirass_piece = 15648;
    private static final int sealed_destino_gaiter_piece = 15651;
    private static final int sealed_destino_gauntlet_piece = 15654;
    private static final int sealed_destino_boots_piece = 15657;
    private static final int sealed_destino_shield_piece = 15693;
    private static final int sealed_destino_ring_gem = 15772;
    private static final int sealed_destino_earring_gem = 15773;
    private static final int sealed_destino_necklace_gem = 15774;

    // questitem
    private static final int q_full_barrel_of_xel = 15712;
    private static final int q_empty_barrel_of_xel = 15713;

    // mobs
    private static final int ol_cooker = 18908;
    private static final int ol_cooker_guard = 22779;
    private static final int xel_private_mage = 22786;
    private static final int xel_private_warrior = 22787;
    private static final int xel_private_sniper = 22788;

    public _289_NoMoreSoupForYou() {
        super(false);
        addStartNpc(stan);
        addQuestItem(q_worst_taste_fruit, q_full_barrel_of_xel, q_empty_barrel_of_xel);
        addKillId(ol_cooker, ol_cooker_guard, xel_private_mage, xel_private_warrior, xel_private_sniper);
        addLevelCheck(82);
        addQuestCompletedCheck(252);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("delicious_foods_are_mine");
        int npcId = npc.getNpcId();
        if (npcId == stan) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("delicious_foods_are_mine", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                st.giveItems(q_worst_taste_fruit, 500);
                /**
                 * Временный кастыль для Евы. Убрать после оффлайк правки

                 if (Rnd.chance(33))
                 st.giveItems(q_empty_barrel_of_xel, Rnd.get(1, 3));
                 if (Rnd.chance(30))
                 st.giveItems(q_full_barrel_of_xel, 1);
                 */
                htmltext = "stan_q0289_04.htm";
            } else if (event.equalsIgnoreCase("reply_1"))
                htmltext = "stan_q0289_03.htm";
            else if (event.equalsIgnoreCase("reply_7")) {
                if (GetMemoState == 1) {
                    st.giveItems(q_worst_taste_fruit, 500);
                    /**
                     * Временный кастыль для Евы. Убрать после оффлайк правки

                     if (Rnd.chance(33))
                     st.giveItems(q_empty_barrel_of_xel, Rnd.get(1, 3));
                     if (Rnd.chance(30))
                     st.giveItems(q_full_barrel_of_xel, 1);
                     */
                    htmltext = "stan_q0289_06.htm";
                }
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 1 && st.ownItemCount(q_full_barrel_of_xel) < 500)
                    htmltext = "stan_q0289_08.htm";
                else if (GetMemoState == 1 && st.ownItemCount(q_full_barrel_of_xel) >= 500) {
                    st.takeItems(q_full_barrel_of_xel, 500);
                    int i0 = Rnd.get(5);
                    switch (i0) {
                        case 0: {
                            st.giveItems(rp_icarus_trident_i, 1);
                            break;
                        }
                        case 1: {
                            st.giveItems(icarus_trident_piece, 3);
                            break;
                        }
                        case 2: {
                            st.giveItems(icarus_trident_piece, 4);
                            break;
                        }
                        case 3: {
                            st.giveItems(icarus_trident_piece, 5);
                            break;
                        }
                        case 4: {
                            st.giveItems(icarus_trident_piece, 6);
                            break;
                        }
                    }
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "stan_q0289_09.htm";
                }
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 1 && st.ownItemCount(q_full_barrel_of_xel) < 100)
                    htmltext = "stan_q0289_10.htm";
                else if (GetMemoState == 1 && st.ownItemCount(q_full_barrel_of_xel) >= 100) {
                    st.takeItems(q_full_barrel_of_xel, 100);
                    int i0 = Rnd.get(10);
                    switch (i0) {
                        case 0: {
                            int i1 = Rnd.get(10);
                            if (i1 < 4)
                                st.giveItems(rp_sealed_destino_helmet_i, 1);
                            else if (i1 >= 4 && i1 < 7)
                                st.giveItems(rp_sealed_destino_cuirass_i, 1);
                            else
                                st.giveItems(rp_sealed_destino_gaiter_i, 1);
                            break;
                        }
                        case 1: {
                            st.giveItems(rp_sealed_destino_gauntlet_i, 1);
                            break;
                        }
                        case 2: {
                            st.giveItems(rp_sealed_destino_boots_i, 1);
                            break;
                        }
                        case 3: {
                            st.giveItems(rp_sealed_destino_shield_i, 1);
                            break;
                        }
                        case 4: {
                            int i1 = Rnd.get(10);
                            if (i1 < 4)
                                st.giveItems(rp_sealed_destino_ring_i, 1);
                            else if (i1 >= 4 && i1 < 8)
                                st.giveItems(rp_sealed_destino_earring_i, 1);
                            else
                                st.giveItems(rp_sealed_destino_necklace_i, 1);
                            break;
                        }
                        case 5: {
                            int i1 = Rnd.get(10);
                            if (i1 < 4)
                                st.giveItems(sealed_destino_helmet_piece, 3);
                            else if (i1 >= 4 && i1 < 7)
                                st.giveItems(sealed_destino_cuirass_piece, 3);
                            else
                                st.giveItems(sealed_destino_gaiter_piece, 3);
                            break;
                        }
                        case 6: {
                            st.giveItems(sealed_destino_gauntlet_piece, 3);
                            break;
                        }
                        case 7: {
                            st.giveItems(sealed_destino_boots_piece, 3);
                            break;
                        }
                        case 8: {
                            st.giveItems(sealed_destino_shield_piece, 3);
                            break;
                        }
                        case 9: {
                            int i1 = Rnd.get(10);
                            if (i1 < 4)
                                st.giveItems(sealed_destino_ring_gem, 3);
                            else if (i1 >= 4 && i1 < 8)
                                st.giveItems(sealed_destino_earring_gem, 3);
                            else
                                st.giveItems(sealed_destino_necklace_gem, 3);
                            break;
                        }
                    }
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "stan_q0289_11.htm";
                }
            } else if (event.equalsIgnoreCase("reply_4")) {
                if (GetMemoState == 1) {
                    htmltext = "stan_q0289_12.htm";
                    st.setMemoState("delicious_foods_are_mine", String.valueOf(1), true);
                }
            } else if (event.equalsIgnoreCase("reply_5")) {
                if (GetMemoState == 1 && (st.ownItemCount(q_full_barrel_of_xel) >= 1 || st.ownItemCount(q_empty_barrel_of_xel) >= 1))
                    htmltext = "stan_q0289_13.htm";
                else if (GetMemoState == 1) {
                    st.takeItems(q_full_barrel_of_xel, -1);
                    st.takeItems(q_empty_barrel_of_xel, -1);
                    st.takeItems(q_worst_taste_fruit, -1);
                    st.removeMemo("delicious_foods_are_mine");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(true);
                    htmltext = "stan_q0289_14.htm";
                }
            } else if (event.equalsIgnoreCase("reply_6")) {
                if (GetMemoState == 1) {
                    st.takeItems(q_full_barrel_of_xel, -1);
                    st.takeItems(q_empty_barrel_of_xel, -1);
                    st.takeItems(q_worst_taste_fruit, -1);
                    st.removeMemo("delicious_foods_are_mine");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(true);
                    htmltext = "stan_q0289_15.htm";
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("delicious_foods_are_mine");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == stan) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                        case QUEST:
                            htmltext = "stan_q0289_02.htm";
                            break;
                        default:
                            htmltext = "stan_q0289_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == stan) {
                    if (GetMemoState == 1 && st.ownItemCount(q_full_barrel_of_xel) + st.ownItemCount(q_empty_barrel_of_xel) * 2 < 100)
                        htmltext = "stan_q0289_05.htm";
                    else if (GetMemoState == 1 && st.ownItemCount(q_full_barrel_of_xel) + st.ownItemCount(q_empty_barrel_of_xel) * 2 >= 100) {
                        if (st.ownItemCount(q_empty_barrel_of_xel) > 0) {
                            long i9 = st.ownItemCount(q_empty_barrel_of_xel);
                            long i0 = i9 / 2;
                            long i1 = i0 * 2;
                            if (i1 < i9)
                                st.takeItems(q_empty_barrel_of_xel, i1);
                            else
                                st.takeItems(q_empty_barrel_of_xel, -1);
                            st.giveItems(q_full_barrel_of_xel, i0);
                        }
                        htmltext = "stan_q0289_07.htm";
                    }
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("delicious_foods_are_mine");
        int npcId = npc.getNpcId();
        if (npcId == ol_cooker) {
            if (GetMemoState == 1) {
                if (Rnd.get(1000) < 853) {
                    st.takeItems(q_full_barrel_of_xel, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == ol_cooker_guard) {
            if (GetMemoState == 1) {
                if (Rnd.get(1000) < 599) {
                    st.takeItems(q_empty_barrel_of_xel, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == xel_private_mage || npcId == xel_private_warrior || npcId == xel_private_sniper) {
            if (GetMemoState == 1) {
                if (Rnd.get(1000) < 699) {
                    st.takeItems(q_full_barrel_of_xel, 1);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.takeItems(q_empty_barrel_of_xel, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        }
        return null;
    }
}