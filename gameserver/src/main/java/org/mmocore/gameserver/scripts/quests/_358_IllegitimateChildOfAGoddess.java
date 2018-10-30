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
public class _358_IllegitimateChildOfAGoddess extends Quest {
    // npc
    private static final int grandmaster_oltlin = 30862;

    // mobs
    private static final int trives = 20672;
    private static final int falibati = 20673;

    // questitem
    private static final int snake_scale = 5868;

    // etcitem
    private static final int rp_sealed_phoenixs_necklace_i = 6329;
    private static final int rp_sealed_phoenixs_earing_i = 6331;
    private static final int rp_sealed_phoenixs_ring_i = 6333;
    private static final int rp_sealed_majestic_necklace_i = 6335;
    private static final int rp_sealed_majestic_earing_i = 6337;
    private static final int rp_sealed_majestic_ring_i = 6339;
    private static final int rp_sealed_dark_crystal_shield_i = 5364;
    private static final int rp_sealed_shield_of_nightmare_i = 5366;

    public _358_IllegitimateChildOfAGoddess() {
        super(false);
        addStartNpc(grandmaster_oltlin);
        addKillId(trives, falibati);
        addQuestItem(snake_scale);
        addLevelCheck(63, 67);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();

        if (npcId == grandmaster_oltlin)
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("an_illegitimate_child_of_godness", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "grandmaster_oltlin_q0358_05.htm";
            } else if (event.equalsIgnoreCase("reply_1"))
                htmltext = "grandmaster_oltlin_q0358_04.htm";
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("an_illegitimate_child_of_godness");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == grandmaster_oltlin) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "grandmaster_oltlin_q0358_01.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "grandmaster_oltlin_q0358_02.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == grandmaster_oltlin)
                    if (GetMemoState == 1 && st.ownItemCount(snake_scale) < 108)
                        htmltext = "grandmaster_oltlin_q0358_06.htm";
                    else if (GetMemoState == 1 && st.ownItemCount(snake_scale) >= 108) {
                        int i0 = Rnd.get(1000);
                        if (i0 < 125)
                            st.giveItems(rp_sealed_phoenixs_earing_i, 1);
                        else if (i0 < 250)
                            st.giveItems(rp_sealed_majestic_earing_i, 1);
                        else if (i0 < 375)
                            st.giveItems(rp_sealed_phoenixs_necklace_i, 1);
                        else if (i0 < 500)
                            st.giveItems(rp_sealed_majestic_necklace_i, 1);
                        else if (i0 < 625)
                            st.giveItems(rp_sealed_phoenixs_ring_i, 1);
                        else if (i0 < 750)
                            st.giveItems(rp_sealed_majestic_ring_i, 1);
                        else if (i0 < 875)
                            st.giveItems(rp_sealed_shield_of_nightmare_i, 1);
                        else
                            st.giveItems(rp_sealed_dark_crystal_shield_i, 1);
                        st.takeItems(snake_scale, -1);
                        st.removeMemo("an_illegitimate_child_of_godness");
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(true);
                        htmltext = "grandmaster_oltlin_q0358_07.htm";
                    }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("an_illegitimate_child_of_godness");
        int npcId = npc.getNpcId();

        if (GetMemoState == 1)
            if (npcId == trives) {
                if (st.ownItemCount(snake_scale) < 108 && Rnd.get(100) < 71) {
                    st.giveItems(snake_scale, 1);
                    st.soundEffect(SOUND_ITEMGET);
                    if (st.ownItemCount(snake_scale) >= 107) {
                        st.setCond(2);
                        st.soundEffect(SOUND_MIDDLE);
                    } else
                        st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == falibati)
                if (st.ownItemCount(snake_scale) < 108 && Rnd.get(100) < 74) {
                    st.giveItems(snake_scale, 1);
                    if (st.ownItemCount(snake_scale) >= 107) {
                        st.setCond(2);
                        st.soundEffect(SOUND_MIDDLE);
                    } else
                        st.soundEffect(SOUND_ITEMGET);
                }
        return null;
    }
}