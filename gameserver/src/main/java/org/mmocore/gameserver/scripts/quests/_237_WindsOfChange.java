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
public class _237_WindsOfChange extends Quest {
    // npc
    private static final int gatekeeper_flauen = 30899;
    private static final int iason_haine = 30969;
    private static final int head_blacksmith_roman = 30897;
    private static final int highpriestess_morelyn = 30925;
    private static final int gakka = 32641;
    private static final int minehr = 32643;
    // questitem
    private static final int q_letter_of_flauen = 14862;
    private static final int q_letter_of_introduction_dwarf = 14863;
    private static final int q_letter_of_introduction_elf = 14864;
    private static final int q_letter_of_confirmation_dwarf = 14865;
    private static final int q_letter_of_confirmation_elf = 14866;

    public _237_WindsOfChange() {
        super(false);
        addStartNpc(gatekeeper_flauen);
        addTalkId(iason_haine, head_blacksmith_roman, highpriestess_morelyn, gakka, minehr);
        addQuestItem(q_letter_of_flauen, q_letter_of_introduction_dwarf, q_letter_of_introduction_elf);
        addLevelCheck(82);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("from_neutrality_to_partialness");
        if (event.equalsIgnoreCase("quest_accept")) {
            st.setCond(1);
            st.setMemoState("from_neutrality_to_partialness", String.valueOf(1), true);
            st.giveItems(q_letter_of_flauen, 1);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            htmltext = "gatekeeper_flauen_q0237_08.htm";
        } else if (event.equalsIgnoreCase("reply_6")) {
            st.setMemoState("from_neutrality_to_partialness", String.valueOf(2), true);
            st.giveItems(q_letter_of_flauen, -1);
            htmltext = "iason_haine_q0237_02.htm";
        } else if (event.equalsIgnoreCase("reply_2") && GetMemoState == 2) {
            st.setCond(2);
            st.setMemoState("from_neutrality_to_partialness", String.valueOf(3), true);
            st.soundEffect(SOUND_MIDDLE);
            htmltext = "iason_haine_q0237_08.htm";
        } else if (event.equalsIgnoreCase("reply_3") && GetMemoState == 5)
            htmltext = "iason_haine_q0237_11.htm";
        else if (event.equalsIgnoreCase("reply_31") && GetMemoState == 5)
            htmltext = "iason_haine_q0237_12.htm";
        else if (event.equalsIgnoreCase("reply_41") && GetMemoState == 5)
            htmltext = "iason_haine_q0237_13.htm";
        else if (event.equalsIgnoreCase("reply_32") && GetMemoState == 5) {
            st.setCond(5);
            st.setMemoState("from_neutrality_to_partialness", String.valueOf(6), true);
            st.giveItems(q_letter_of_introduction_dwarf, 1);
            st.soundEffect(SOUND_MIDDLE);
            htmltext = "iason_haine_q0237_15.htm";
        } else if (event.equalsIgnoreCase("reply_5") && GetMemoState == 5)
            htmltext = "iason_haine_q0237_14.htm";
        else if (event.equalsIgnoreCase("reply_42") && GetMemoState == 5) {
            st.setCond(6);
            st.setMemoState("from_neutrality_to_partialness", String.valueOf(7), true);
            st.giveItems(q_letter_of_introduction_elf, 1);
            st.soundEffect(SOUND_MIDDLE);
            htmltext = "iason_haine_q0237_16.htm";
        } else if (event.equalsIgnoreCase("reply_2a") && GetMemoState == 3) {
            st.setCond(3);
            st.setMemoState("from_neutrality_to_partialness", String.valueOf(4), true);
            st.soundEffect(SOUND_MIDDLE);
            htmltext = "head_blacksmith_roman_q0237_03.htm";
        } else if (event.equalsIgnoreCase("reply_2b") && GetMemoState == 4) {
            st.setCond(4);
            st.setMemoState("from_neutrality_to_partialness", String.valueOf(5), true);
            st.soundEffect(SOUND_MIDDLE);
            htmltext = "highpriestess_morelyn_q0237_03.htm";
        } else if (event.equalsIgnoreCase("reply_1") && GetMemoState == 6 && st.ownItemCount(q_letter_of_introduction_dwarf) >= 1) {
            st.giveItems(q_letter_of_confirmation_dwarf, 1);
            st.giveItems(ADENA_ID, 213876);
            st.addExpAndSp(892773, 60012);
            st.takeItems(q_letter_of_introduction_dwarf, -1);
            st.removeMemo("from_neutrality_to_partialness");
            st.soundEffect(SOUND_FINISH);
            st.exitQuest(false);
            htmltext = "gakka_q0237_02.htm";
        } else if (event.equalsIgnoreCase("reply_1a") && GetMemoState == 7 && st.ownItemCount(q_letter_of_introduction_elf) >= 1) {
            st.giveItems(q_letter_of_confirmation_elf, 1);
            st.giveItems(ADENA_ID, 213876);
            st.addExpAndSp(892773, 60012);
            st.takeItems(q_letter_of_introduction_elf, -1);
            st.removeMemo("from_neutrality_to_partialness");
            st.soundEffect(SOUND_FINISH);
            st.exitQuest(false);
            htmltext = "minehr_q0237_02.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int id = st.getState();
        int GetMemoState = st.getInt("from_neutrality_to_partialness");
        switch (id) {
            case CREATED:
                if (npcId == gatekeeper_flauen) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            st.exitQuest(true);
                            htmltext = "gatekeeper_flauen_q0237_03.htm";
                            break;
                        default:
                            htmltext = "gatekeeper_flauen_q0237_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == gatekeeper_flauen) {
                    if (GetMemoState == 1 || GetMemoState == 2 || GetMemoState == 5)
                        htmltext = "gatekeeper_flauen_q0237_09.htm";
                    else if (GetMemoState == 3)
                        htmltext = "gatekeeper_flauen_q0237_10.htm";
                    else if (GetMemoState == 4)
                        htmltext = "gatekeeper_flauen_q0237_11.htm";
                    else if (GetMemoState == 6 || GetMemoState == 7)
                        htmltext = "gatekeeper_flauen_q0237_12.htm";
                } else if (npcId == iason_haine) {
                    if (GetMemoState == 1)
                        htmltext = "iason_haine_q0237_01.htm";
                    else if (GetMemoState == 1 && st.ownItemCount(q_letter_of_flauen) < 1)
                        htmltext = "iason_haine_q0237_03.htm";
                    else if (GetMemoState == 2)
                        htmltext = "iason_haine_q0237_02a.htm";
                    else if (GetMemoState == 3)
                        htmltext = "iason_haine_q0237_09.htm";
                    else if (GetMemoState == 5)
                        htmltext = "iason_haine_q0237_10.htm";
                    else if (GetMemoState > 5)
                        htmltext = "iason_haine_q0237_17.htm";
                } else if (npcId == head_blacksmith_roman) {
                    if (GetMemoState == 3)
                        htmltext = "head_blacksmith_roman_q0237_01.htm";
                    else if (GetMemoState > 3)
                        htmltext = "head_blacksmith_roman_q0237_04.htm";
                } else if (npcId == highpriestess_morelyn) {
                    if (GetMemoState == 4)
                        htmltext = "highpriestess_morelyn_q0237_01.htm";
                    if (GetMemoState > 4)
                        htmltext = "highpriestess_morelyn_q0237_04.htm";
                } else if (npcId == gakka) {
                    if (GetMemoState == 6)
                        htmltext = "gakka_q0237_01.htm";
                    else if (GetMemoState == 6 && st.ownItemCount(q_letter_of_introduction_dwarf) < 1)
                        htmltext = "gakka_q0237_03.htm";
                    else if (GetMemoState == 7)
                        htmltext = "gakka_q0237_04.htm";
                } else if (npcId == minehr) {
                    if (GetMemoState == 7)
                        htmltext = "minehr_q0237_01.htm";
                    else if (GetMemoState == 7 && st.ownItemCount(q_letter_of_introduction_elf) < 1)
                        htmltext = "minehr_q0237_03.htm";
                    else if (GetMemoState == 6)
                        htmltext = "minehr_q0237_04.htm";
                }
                break;
            case COMPLETED:
                if (npcId == gatekeeper_flauen)
                    htmltext = "gatekeeper_flauen_q0237_02.htm";
                else if (npcId == gakka)
                    htmltext = "gakka_q0237_05.htm";
                else if (npcId == minehr)
                    htmltext = "minehr_q0237_05.htm";
                break;
        }
        return htmltext;
    }
}