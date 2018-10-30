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
public class _601_WatchingEyes extends Quest {
    // npc
    private final static int eye_of_argos = 31683;

    // mobs
    private final static int apostle_defender = 21306;
    private final static int apostle_avenger = 21308;
    private final static int apostle_avenger_a = 21309;
    private final static int apostle_magistrate = 21310;
    private final static int apostle_magistrate_a = 21311;

    // etcitem
    private final static int sealed_ring_of_aurakyria_gem = 6699;
    private final static int sealed_sanddragons_earing_piece = 6698;
    private final static int sealed_dragon_necklace_wire = 6700;

    // questitem
    private static final int q_proof_of_avenger = 7188;

    public _601_WatchingEyes() {
        super(false);
        addStartNpc(eye_of_argos);
        addKillId(apostle_defender, apostle_avenger, apostle_avenger_a, apostle_magistrate, apostle_magistrate_a);
        addQuestItem(q_proof_of_avenger);
        addLevelCheck(71, 78);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;

        if (event.equalsIgnoreCase("quest_accept")) {
            st.setCond(1);
            st.setMemoState("argoss_favor", String.valueOf(1 * 10 + 1), true);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            htmltext = "eye_of_argos_q0601_0104.htm";
        } else if (event.equalsIgnoreCase("reply_3"))
            if (st.ownItemCount(q_proof_of_avenger) >= 100) {
                int i1 = Rnd.get(1000);
                st.takeItems(q_proof_of_avenger, 100);
                if (i1 < 200) {
                    st.giveItems(sealed_ring_of_aurakyria_gem, 5);
                    st.giveItems(ADENA_ID, 90000);
                    st.addExpAndSp(120000, 10000);
                } else if (i1 < 200 + 200) {
                    st.giveItems(sealed_sanddragons_earing_piece, 5);
                    st.giveItems(ADENA_ID, 80000);
                    st.addExpAndSp(120000, 10000);
                } else if (i1 < 200 + 200 + 100) {
                    st.giveItems(sealed_dragon_necklace_wire, 5);
                    st.giveItems(ADENA_ID, 40000);
                    st.addExpAndSp(120000, 10000);
                } else if (i1 < 200 + 200 + 100 + 500)
                    st.giveItems(ADENA_ID, 230000);
                st.removeMemo("argoss_favor");
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                htmltext = "eye_of_argos_q0601_0201.htm";
            } else
                htmltext = "eye_of_argos_q0601_0202.htm";
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("argoss_favor");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == eye_of_argos) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "eye_of_argos_q0601_0103.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "eye_of_argos_q0601_0101.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == eye_of_argos)
                    if (GetMemoState >= 1 * 10 + 1 && GetMemoState <= 1 * 10 + 2)
                        if (GetMemoState == 1 * 10 + 2 && st.ownItemCount(q_proof_of_avenger) >= 100)
                            htmltext = "eye_of_argos_q0601_0105.htm";
                        else
                            htmltext = "eye_of_argos_q0601_0106.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("argoss_favor");
        int npcId = npc.getNpcId();

        if (GetMemoState == 1 * 10 + 1)
            if (npcId == apostle_defender) {
                int i4 = Rnd.get(1000);
                if (i4 < 850)
                    if (st.ownItemCount(q_proof_of_avenger) + 1 >= 100) {
                        if (st.ownItemCount(q_proof_of_avenger) < 100) {
                            st.setCond(2);
                            st.setMemoState("argoss_favor", String.valueOf(1 * 10 + 2), true);
                            st.giveItems(q_proof_of_avenger, 100 - st.ownItemCount(q_proof_of_avenger));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    } else {
                        st.giveItems(q_proof_of_avenger, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            } else if (npcId == apostle_avenger) {
                int i4 = Rnd.get(1000);
                if (i4 < 790)
                    if (st.ownItemCount(q_proof_of_avenger) + 1 >= 100) {
                        if (st.ownItemCount(q_proof_of_avenger) < 100) {
                            st.setCond(2);
                            st.setMemoState("argoss_favor", String.valueOf(1 * 10 + 2), true);
                            st.giveItems(q_proof_of_avenger, 100 - st.ownItemCount(q_proof_of_avenger));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    } else {
                        st.giveItems(q_proof_of_avenger, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            } else if (npcId == apostle_avenger_a) {
                int i4 = Rnd.get(1000);
                if (i4 < 820)
                    if (st.ownItemCount(q_proof_of_avenger) + 1 >= 100) {
                        if (st.ownItemCount(q_proof_of_avenger) < 100) {
                            st.setCond(2);
                            st.setMemoState("argoss_favor", String.valueOf(1 * 10 + 2), true);
                            st.giveItems(q_proof_of_avenger, 100 - st.ownItemCount(q_proof_of_avenger));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    } else {
                        st.giveItems(q_proof_of_avenger, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            } else if (npcId == apostle_magistrate) {
                int i4 = Rnd.get(1000);
                if (i4 < 680)
                    if (st.ownItemCount(q_proof_of_avenger) + 1 >= 100) {
                        if (st.ownItemCount(q_proof_of_avenger) < 100) {
                            st.setCond(2);
                            st.setMemoState("argoss_favor", String.valueOf(1 * 10 + 2), true);
                            st.giveItems(q_proof_of_avenger, 100 - st.ownItemCount(q_proof_of_avenger));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    } else {
                        st.giveItems(q_proof_of_avenger, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            } else if (npcId == apostle_magistrate_a) {
                int i4 = Rnd.get(1000);
                if (i4 < 630)
                    if (st.ownItemCount(q_proof_of_avenger) + 1 >= 100) {
                        if (st.ownItemCount(q_proof_of_avenger) < 100) {
                            st.setCond(2);
                            st.setMemoState("argoss_favor", String.valueOf(1 * 10 + 2), true);
                            st.giveItems(q_proof_of_avenger, 100 - st.ownItemCount(q_proof_of_avenger));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    } else {
                        st.giveItems(q_proof_of_avenger, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            }
        return null;
    }
}