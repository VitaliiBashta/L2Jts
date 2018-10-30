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
public class _690_JudesRequest extends Quest {
    // npc
    private final static int jude = 32356;

    // mobs
    private final static int malignant_lower = 22398;
    private final static int malignant_upper = 22399;

    // questitem
    private final static int q_weapon_of_malignant = 10327;

    public _690_JudesRequest() {
        super(true);
        addStartNpc(jude);
        addTalkId(jude);
        addKillId(malignant_lower, malignant_upper);
        addQuestItem(q_weapon_of_malignant);
        addLevelCheck(78);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;

        if (event.equalsIgnoreCase("quest_accept")) {
            st.setCond(1);
            st.setMemoState("request_of_jude", String.valueOf(1), true);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            htmltext = "jude_q0690_03.htm";
        } else if (event.equalsIgnoreCase("reply_1"))
            htmltext = "jude_q0690_06.htm";
        else if (event.equalsIgnoreCase("reply_3")) {
            if (st.ownItemCount(q_weapon_of_malignant) >= 200) {
                int i0 = Rnd.get(9);
                switch (i0) {
                    case 0: {
                        // etcitem
                        int rp_icarus_sowsword_i = 10373;
                        st.giveItems(rp_icarus_sowsword_i, 1);
                        break;
                    }
                    case 1: {
                        int rp_icarus_disperser_i = 10374;
                        st.giveItems(rp_icarus_disperser_i, 1);
                        break;
                    }
                    case 2: {
                        int rp_icarus_spirits_i = 10375;
                        st.giveItems(rp_icarus_spirits_i, 1);
                        break;
                    }
                    case 3: {
                        int rp_icarus_heavy_arms_i = 10376;
                        st.giveItems(rp_icarus_heavy_arms_i, 1);
                        break;
                    }
                    case 4: {
                        int rp_icarus_trident_i = 10377;
                        st.giveItems(rp_icarus_trident_i, 1);
                        break;
                    }
                    case 5: {
                        int rp_icarus_chopper_i = 10378;
                        st.giveItems(rp_icarus_chopper_i, 1);
                        break;
                    }
                    case 6: {
                        int rp_icarus_knuckle_i = 10379;
                        st.giveItems(rp_icarus_knuckle_i, 1);
                        break;
                    }
                    case 7: {
                        int rp_icarus_wand_i = 10380;
                        st.giveItems(rp_icarus_wand_i, 1);
                        break;
                    }
                    case 8: {
                        int rp_icarus_accipiter_i = 10381;
                        st.giveItems(rp_icarus_accipiter_i, 1);
                        break;
                    }
                }
                st.takeItems(q_weapon_of_malignant, 200);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "jude_q0690_07.htm";
            } else
                htmltext = "jude_q0690_07a.htm";
        } else if (event.equalsIgnoreCase("reply_4")) {
            st.takeItems(q_weapon_of_malignant, -1);
            st.removeMemo("request_of_jude");
            st.soundEffect(SOUND_FINISH);
            htmltext = "jude_q0690_08.htm";
            st.exitQuest(true);
        }
        if (event.equalsIgnoreCase("reply_5"))
            if (st.ownItemCount(q_weapon_of_malignant) >= 5) {
                int i0 = Rnd.get(9);
                switch (i0) {
                    case 0: {
                        int icarus_sowsword_piece = 10397;
                        st.giveItems(icarus_sowsword_piece, 3);
                        break;
                    }
                    case 1: {
                        int icarus_disperser_piece = 10398;
                        st.giveItems(icarus_disperser_piece, 3);
                        break;
                    }
                    case 2: {
                        int icarus_spirits_piece = 10399;
                        st.giveItems(icarus_spirits_piece, 3);
                        break;
                    }
                    case 3: {
                        int icarus_heavy_arms_piece = 10400;
                        st.giveItems(icarus_heavy_arms_piece, 3);
                        break;
                    }
                    case 4: {
                        int icarus_trident_piece = 10401;
                        st.giveItems(icarus_trident_piece, 3);
                        break;
                    }
                    case 5: {
                        int icarus_chopper_piece = 10402;
                        st.giveItems(icarus_chopper_piece, 3);
                        break;
                    }
                    case 6: {
                        int icarus_knuckle_piece = 10403;
                        st.giveItems(icarus_knuckle_piece, 3);
                        break;
                    }
                    case 7: {
                        int icarus_wand_piece = 10404;
                        st.giveItems(icarus_wand_piece, 3);
                        break;
                    }
                    case 8: {
                        int icarus_accipiter_piece = 10405;
                        st.giveItems(icarus_accipiter_piece, 3);
                        break;
                    }
                }
                st.takeItems(q_weapon_of_malignant, 5);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "jude_q0690_09.htm";
            } else
                htmltext = "jude_q0690_09a.htm";

        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("request_of_jude");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == jude) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "jude_q0690_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "jude_q0690_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == jude)
                    if (GetMemoState == 1)
                        if (st.ownItemCount(q_weapon_of_malignant) >= 200)
                            htmltext = "jude_q0690_04.htm";
                        else if (st.ownItemCount(q_weapon_of_malignant) < 5)
                            htmltext = "jude_q0690_05a.htm";
                        else if (st.ownItemCount(q_weapon_of_malignant) < 200)
                            htmltext = "jude_q0690_05.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("request_of_jude");
        int npcId = npc.getNpcId();

        if (GetMemoState == 1)
            if (npcId == malignant_lower) {
                int i4 = Rnd.get(1000);
                if (i4 < 173) {
                    st.giveItems(q_weapon_of_malignant, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == malignant_upper) {
                int i4 = Rnd.get(1000);
                if (i4 < 246) {
                    st.giveItems(q_weapon_of_malignant, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        return null;
    }
}