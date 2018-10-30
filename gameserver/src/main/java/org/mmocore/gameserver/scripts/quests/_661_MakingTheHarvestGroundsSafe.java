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
public class _661_MakingTheHarvestGroundsSafe extends Quest {
    // npc
    private final static int warehouse_keeper_norman = 30210;

    // mobs
    private final static int giant_poison_bee = 21095;
    private final static int cloudy_beast = 21096;
    private final static int young_araneid = 21097;

    // questitem
    private final static int q_big_hornet_sting = 8283;
    private final static int q_cloud_gem = 8284;
    private final static int q_young_araneid_claw = 8285;

    public _661_MakingTheHarvestGroundsSafe() {
        super(false);
        addStartNpc(warehouse_keeper_norman);
        addKillId(giant_poison_bee, cloudy_beast, young_araneid);
        addQuestItem(q_big_hornet_sting, q_cloud_gem, q_young_araneid_claw);
        addLevelCheck(21);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetHTMLCookie = st.getInt("clear_gathering_site_cookie");
        int npcId = npc.getNpcId();

        if (npcId == warehouse_keeper_norman)
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("clear_gathering_site", String.valueOf(1 * 10 + 1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "warehouse_keeper_norman_q0661_0103.htm";
            } else if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 2 - 1)
                htmltext = "warehouse_keeper_norman_q0661_0201.htm";
            else if (event.equalsIgnoreCase("reply_3") && GetHTMLCookie == 2 - 1) {
                if (st.ownItemCount(q_big_hornet_sting) == 0 && st.ownItemCount(q_cloud_gem) == 0 && st.ownItemCount(q_young_araneid_claw) == 0)
                    htmltext = "warehouse_keeper_norman_q0661_0202.htm";
                st.takeItems(q_big_hornet_sting, -1);
                st.takeItems(q_cloud_gem, -1);
                st.takeItems(q_young_araneid_claw, -1);
                if (st.ownItemCount(q_big_hornet_sting) + st.ownItemCount(q_cloud_gem) + st.ownItemCount(q_young_araneid_claw) >= 10)
                    st.giveItems(ADENA_ID, 5773 + 57 * st.ownItemCount(q_big_hornet_sting) + 56 * st.ownItemCount(q_cloud_gem) + 60 * st.ownItemCount(q_young_araneid_claw));
                else
                    st.giveItems(ADENA_ID, 57 * st.ownItemCount(q_big_hornet_sting) + 56 * st.ownItemCount(q_cloud_gem) + 60 * st.ownItemCount(q_young_araneid_claw));
                htmltext = "warehouse_keeper_norman_q0661_0203.htm";
            } else if (event.equalsIgnoreCase("reply_4") && GetHTMLCookie == 2 - 1) {
                htmltext = "warehouse_keeper_norman_q0661_0204.htm";
                st.removeMemo("clear_gathering_site_cookie");
                st.removeMemo("clear_gathering_site");
                st.exitQuest(true);
                st.soundEffect(SOUND_FINISH);
            }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("clear_gathering_site");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == warehouse_keeper_norman) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "warehouse_keeper_norman_q0661_0102.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "warehouse_keeper_norman_q0661_0101.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == warehouse_keeper_norman)
                    if (GetMemoState == 1 * 10 + 1)
                        if (st.ownItemCount(q_big_hornet_sting) == 0 && st.ownItemCount(q_cloud_gem) == 0 && st.ownItemCount(q_young_araneid_claw) == 0)
                            htmltext = "warehouse_keeper_norman_q0661_0106.htm";
                        else {
                            st.setMemoState("clear_gathering_site_cookie", String.valueOf(1), true);
                            htmltext = "warehouse_keeper_norman_q0661_0105.htm";
                        }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("clear_gathering_site");
        int npcId = npc.getNpcId();

        if (GetMemoState == 1 * 10 + 1)
            if (npcId == giant_poison_bee) {
                int i4 = Rnd.get(1000);
                if (i4 < 508) {
                    st.giveItems(q_big_hornet_sting, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == cloudy_beast) {
                int i4 = Rnd.get(1000);
                if (i4 < 500) {
                    st.giveItems(q_cloud_gem, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == young_araneid) {
                int i4 = Rnd.get(1000);
                if (i4 < 516) {
                    st.giveItems(q_young_araneid_claw, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        return null;
    }
}