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
public class _287_FiguringItOut extends Quest {
    // npc
    private static final int laki = 32742;

    // mobs
    private static final int tantaar_lizardman_scout = 22768;
    private static final int tantaar_lizard_warrior = 22769;
    private static final int tantaar_lizard_soldier = 22770;
    private static final int tantaar_lizard_berserker = 22771;
    private static final int tantaar_lizardman_archer = 22772;
    private static final int tantaar_lizardman_wizard = 22773;
    private static final int tantaar_lizard_summoner = 22774;

    // etcitem
    private static final int rp_icarus_accipiter_i = 10381;
    private static final int icarus_accipiter_piece = 10405;
    private static final int rp_sealed_destino_leather_helmet_i = 15776;
    private static final int rp_sealed_destino_houberk_i = 15779;
    private static final int rp_sealed_destino_leather_legging_i = 15782;
    private static final int rp_sealed_destino_leather_gloves_i = 15785;
    private static final int rp_sealed_destino_leather_boots_i = 15788;
    private static final int rp_sealed_destino_ring_i = 15812;
    private static final int rp_sealed_destino_earring_i = 15813;
    private static final int rp_sealed_destino_necklace_i = 15814;
    private static final int sealed_destino_leather_helmet_piece = 15646;
    private static final int sealed_destino_houberk_piece = 15649;
    private static final int sealed_destino_leather_legging_piece = 15652;
    private static final int sealed_destino_leather_gloves_piece = 15655;
    private static final int sealed_destino_leather_boots_piece = 15658;
    private static final int sealed_destino_ring_gem = 15772;
    private static final int sealed_destino_earring_gem = 15773;
    private static final int sealed_destino_necklace_gem = 15774;

    // questitem
    private static final int q_blood_of_lizard_bottle = 15499;

    public _287_FiguringItOut() {
        super(false);
        addStartNpc(laki);
        addKillId(tantaar_lizardman_scout, tantaar_lizard_warrior, tantaar_lizard_soldier, tantaar_lizard_berserker, tantaar_lizardman_archer, tantaar_lizardman_wizard, tantaar_lizard_summoner);
        addQuestItem(q_blood_of_lizard_bottle);
        addLevelCheck(82);
        addQuestCompletedCheck(250);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("i_will_know");

        if (event.equalsIgnoreCase("quest_accept")) {
            st.setCond(1);
            st.setMemoState("i_will_know", String.valueOf(1), true);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            htmltext = "laki_q0287_03.htm";
        } else if (event.equalsIgnoreCase("reply_1"))
            htmltext = "laki_q0287_02.htm";
        else if (event.equalsIgnoreCase("reply_2")) {
            if (GetMemoState == 1 && st.ownItemCount(q_blood_of_lizard_bottle) >= 500) {
                st.takeItems(q_blood_of_lizard_bottle, 500);
                int i0 = Rnd.get(5);
                if (i0 == 0)
                    st.giveItems(rp_icarus_accipiter_i, 1);
                else if (i0 == 1)
                    st.giveItems(icarus_accipiter_piece, 1);
                else if (i0 == 2)
                    st.giveItems(icarus_accipiter_piece, 4);
                else if (i0 == 3)
                    st.giveItems(icarus_accipiter_piece, 4);
                else
                    st.giveItems(icarus_accipiter_piece, 6);
                st.soundEffect(SOUND_FINISH);
                htmltext = "laki_q0287_06.htm";
            } else if (GetMemoState == 1 && st.ownItemCount(q_blood_of_lizard_bottle) < 500)
                htmltext = "laki_q0287_07.htm";
        } else if (event.equalsIgnoreCase("reply_3")) {
            if (GetMemoState == 1 && st.ownItemCount(q_blood_of_lizard_bottle) >= 100) {
                st.takeItems(q_blood_of_lizard_bottle, 100);
                int i0 = Rnd.get(10);
                if (i0 == 0)
                    st.giveItems(rp_sealed_destino_leather_helmet_i, 1);
                else if (i0 == 1)
                    st.giveItems(rp_sealed_destino_houberk_i, 1);
                else if (i0 == 2)
                    st.giveItems(rp_sealed_destino_leather_legging_i, 1);
                else if (i0 == 3) {
                    int i1 = Rnd.get(2);
                    if (i1 == 0)
                        st.giveItems(rp_sealed_destino_leather_gloves_i, 1);
                    else
                        st.giveItems(rp_sealed_destino_leather_boots_i, 1);
                } else if (i0 == 4) {
                    int i1 = Rnd.get(10);
                    if (i1 < 4)
                        st.giveItems(rp_sealed_destino_ring_i, 1);
                    else if (i1 >= 4 && i1 < 8)
                        st.giveItems(rp_sealed_destino_earring_i, 1);
                    else
                        st.giveItems(rp_sealed_destino_necklace_i, 1);
                } else if (i0 == 5)
                    st.giveItems(sealed_destino_leather_helmet_piece, 5);
                else if (i0 == 6)
                    st.giveItems(sealed_destino_houberk_piece, 5);
                else if (i0 == 7)
                    st.giveItems(sealed_destino_leather_legging_piece, 5);
                else if (i0 == 8) {
                    int i1 = Rnd.get(2);
                    if (i1 == 0)
                        st.giveItems(sealed_destino_leather_gloves_piece, 5);
                    else
                        st.giveItems(sealed_destino_leather_boots_piece, 5);
                } else {
                    int i1 = Rnd.get(10);
                    if (i1 < 4)
                        st.giveItems(sealed_destino_ring_gem, 1);
                    else if (i1 >= 4 && i1 < 7)
                        st.giveItems(sealed_destino_earring_gem, 1);
                    else
                        st.giveItems(sealed_destino_necklace_gem, 1);
                }
                st.soundEffect(SOUND_FINISH);
                htmltext = "laki_q0287_08.htm";
            } else if (GetMemoState == 1 && st.ownItemCount(q_blood_of_lizard_bottle) < 100)
                htmltext = "laki_q0287_09.htm";
        } else if (event.equalsIgnoreCase("reply_4")) {
            if (GetMemoState == 1) {
                htmltext = "laki_q0287_10.htm";
                st.setMemoState("i_will_know", String.valueOf(1), true);
            }
        } else if (event.equalsIgnoreCase("reply_5")) {
            if (GetMemoState == 1 && st.ownItemCount(q_blood_of_lizard_bottle) >= 1)
                htmltext = "laki_q0287_11.htm";
            else if (GetMemoState == 1 && st.ownItemCount(q_blood_of_lizard_bottle) == 0) {
                st.takeItems(q_blood_of_lizard_bottle, -1);
                st.removeMemo("i_will_know");
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                htmltext = "laki_q0287_12.htm";
            }
        } else if (event.equalsIgnoreCase("reply_6"))
            if (GetMemoState == 1) {
                st.takeItems(q_blood_of_lizard_bottle, -1);
                st.removeMemo("i_will_know");
                st.soundEffect(SOUND_FINISH);
                htmltext = "laki_q0287_13.htm";
                st.exitQuest(true);
            }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("i_will_know");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == laki) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                        case QUEST:
                            htmltext = "laki_q0287_14.htm";
                            break;
                        default:
                            htmltext = "laki_q0287_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == laki)
                    if (GetMemoState == 1 && st.ownItemCount(q_blood_of_lizard_bottle) < 100)
                        htmltext = "laki_q0287_04.htm";
                    else if (GetMemoState == 1 && st.ownItemCount(q_blood_of_lizard_bottle) >= 100)
                        htmltext = "laki_q0287_05.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int GetMemoState = st.getInt("i_will_know");

        if (GetMemoState == 1)
            if (npcId == tantaar_lizardman_scout) {
                int i0 = Rnd.get(1000);
                if (i0 < 509) {
                    st.giveItems(q_blood_of_lizard_bottle, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == tantaar_lizard_warrior) {
                int i0 = Rnd.get(1000);
                if (i0 < 689) {
                    st.giveItems(q_blood_of_lizard_bottle, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == tantaar_lizard_soldier) {
                int i0 = Rnd.get(1000);
                if (i0 < 123) {
                    st.giveItems(q_blood_of_lizard_bottle, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == tantaar_lizard_berserker) {
                int i0 = Rnd.get(1000);
                if (i0 < 159) {
                    st.giveItems(q_blood_of_lizard_bottle, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == tantaar_lizardman_archer) {
                int i0 = Rnd.get(1000);
                if (i0 < 739) {
                    st.giveItems(q_blood_of_lizard_bottle, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == tantaar_lizardman_wizard) {
                int i0 = Rnd.get(1000);
                if (i0 < 737) {
                    st.giveItems(q_blood_of_lizard_bottle, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == tantaar_lizard_summoner) {
                int i0 = Rnd.get(1000);
                if (i0 < 261) {
                    st.giveItems(q_blood_of_lizard_bottle, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        return null;
    }
}