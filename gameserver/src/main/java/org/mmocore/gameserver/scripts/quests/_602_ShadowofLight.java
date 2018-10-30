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
public class _602_ShadowofLight extends Quest {
    // npc
    private static final int eye_of_argos = 31683;

    // mobs
    private static final int buffalo_slave = 21299;
    private static final int grendel_slave = 21304;

    // etcitem
    private static final int sealed_sanddragons_earing_piece = 6698;
    private static final int sealed_ring_of_aurakyria_gem = 6699;
    private static final int sealed_dragon_necklace_wire = 6700;

    // questitem
    private static final int q_dark_eye = 7189;

    public _602_ShadowofLight() {
        super(false);
        addStartNpc(eye_of_argos);
        addKillId(buffalo_slave, grendel_slave);
        addQuestItem(q_dark_eye);
        addLevelCheck(68, 75);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;

        if (event.equalsIgnoreCase("eye_of_argos_q0602_0104.htm")) {
            st.setCond(1);
            st.setMemoState("shadow_of_light", String.valueOf(1 * 10 + 1), true);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            htmltext = "eye_of_argos_q0602_0104.htm";
        } else if (event.equalsIgnoreCase("reply_3"))
            if (st.ownItemCount(q_dark_eye) >= 100) {
                int i1 = Rnd.get(1000);
                st.takeItems(q_dark_eye, 100);
                if (i1 < 200) {
                    st.giveItems(sealed_ring_of_aurakyria_gem, 3);
                    st.giveItems(ADENA_ID, 40000);
                    st.addExpAndSp(120000, 20000);
                } else if (i1 < 200 + 200) {
                    st.giveItems(sealed_sanddragons_earing_piece, 3);
                    st.giveItems(ADENA_ID, 60000);
                    st.addExpAndSp(110000, 15000);
                } else if (i1 < 200 + 200 + 100) {
                    st.giveItems(sealed_dragon_necklace_wire, 3);
                    st.giveItems(ADENA_ID, 40000);
                    st.addExpAndSp(150000, 10000);
                } else if (i1 < 200 + 200 + 100 + 500) {
                    st.giveItems(ADENA_ID, 100000);
                    st.addExpAndSp(140000, 11250);
                }
                st.removeMemo("shadow_of_light");
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                htmltext = "eye_of_argos_q0602_0201.htm";
            } else
                htmltext = "eye_of_argos_q0602_0202.htm";
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("shadow_of_light");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == eye_of_argos) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "eye_of_argos_q0602_0103.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "eye_of_argos_q0602_0101.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == eye_of_argos)
                    if (GetMemoState >= 1 * 10 + 1 && GetMemoState <= 1 * 10 + 2)
                        if (GetMemoState == 1 * 10 + 2 && st.ownItemCount(q_dark_eye) >= 100)
                            htmltext = "eye_of_argos_q0602_0105.htm";
                        else
                            htmltext = "eye_of_argos_q0602_0106.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("shadow_of_light");
        int npcId = npc.getNpcId();

        if (GetMemoState == 1 * 10 + 1)
            if (npcId == buffalo_slave) {
                int i4 = Rnd.get(1000);
                if (i4 < 560)
                    if (st.ownItemCount(q_dark_eye) + 1 >= 100) {
                        st.setCond(2);
                        st.setMemoState("shadow_of_light", String.valueOf(1 * 10 + 2), true);
                        st.giveItems(q_dark_eye, 100 - st.ownItemCount(q_dark_eye));
                        st.soundEffect(SOUND_MIDDLE);
                    } else {
                        st.giveItems(q_dark_eye, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            } else if (npcId == grendel_slave) {
                int i4 = Rnd.get(1000);
                if (i4 < 800)
                    if (st.ownItemCount(q_dark_eye) + 1 >= 100) {
                        st.setCond(2);
                        st.setMemoState("shadow_of_light", String.valueOf(1 * 10 + 2), true);
                        st.giveItems(q_dark_eye, 100 - st.ownItemCount(q_dark_eye));
                        st.soundEffect(SOUND_MIDDLE);
                    } else {
                        st.giveItems(q_dark_eye, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            }
        return null;
    }
}