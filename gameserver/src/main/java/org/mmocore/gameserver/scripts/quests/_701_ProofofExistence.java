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
public class _701_ProofofExistence extends Quest {
    // NPC's
    private static int warmage_artius = 32559;

    // ITEMS
    private static int q_undead_bone_piece = 13875;
    private static int q_banshee_eyeball = 13876;

    // MOB's
    private static int banshee_queen = 25625;
    private static int floating_skeleton_1lv = 22606;
    private static int floating_skeleton_2lv = 22607;
    private static int floating_zombie_1lv = 22608;
    private static int floating_zombie_2lv = 22609;

    public _701_ProofofExistence() {
        super(PARTY_ALL);
        addStartNpc(warmage_artius);
        addTalkId(warmage_artius);
        addKillId(banshee_queen, floating_skeleton_1lv, floating_skeleton_2lv, floating_zombie_1lv, floating_zombie_2lv);
        addQuestItem(q_undead_bone_piece, q_banshee_eyeball);
        addLevelCheck(78);
        addQuestCompletedCheck(10273);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("disproof_of_existence");

        if (event.equals("quest_accept")) {
            st.setCond(1);
            st.setMemoState("disproof_of_existence", String.valueOf(1), true);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            htmltext = "warmage_artius_q0701_04.htm";
        } else if (event.equals("reply_3") && GetMemoState == 1) {
            st.soundEffect(SOUND_FINISH);
            st.exitQuest(true);
            st.removeMemo("disproof_of_existence");
            htmltext = "warmage_artius_q0701_09.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int GetMemoState = st.getInt("disproof_of_existence");

        if (npcId == warmage_artius) {
            switch (isAvailableFor(st.getPlayer())) {
                case LEVEL:
                case QUEST:
                    htmltext = "warmage_artius_q0701_02.htm";
                    st.exitQuest(true);
                    break;
                default:
                    htmltext = "warmage_artius_q0701_01.htm";
                    break;
            }
        } else if (GetMemoState == 1 && st.ownItemCount(q_undead_bone_piece) < 1 && st.ownItemCount(q_banshee_eyeball) < 1)
            htmltext = "warmage_artius_q0701_05.htm";
        else if (GetMemoState == 1 && st.ownItemCount(q_undead_bone_piece) >= 1 && st.ownItemCount(q_banshee_eyeball) < 1) {
            st.giveItems(ADENA_ID, st.ownItemCount(q_undead_bone_piece) * 2500);
            st.takeItems(q_undead_bone_piece, -1);
            htmltext = "warmage_artius_q0701_06.htm";
        } else if (GetMemoState == 1 && st.ownItemCount(q_banshee_eyeball) >= 1) {
            st.giveItems(ADENA_ID, st.ownItemCount(q_undead_bone_piece) * 2500 + st.ownItemCount(q_banshee_eyeball) * 50000 + 23835);
            st.takeItems(q_banshee_eyeball, -1);
            st.takeItems(q_undead_bone_piece, -1);
            htmltext = "warmage_artius_q0701_07.htm";
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int GetMemoState = st.getInt("disproof_of_existence");

        if (GetMemoState == 1 && npcId == floating_skeleton_1lv) {
            int i0 = Rnd.get(1000);
            if (i0 < 518) {
                st.giveItems(q_undead_bone_piece, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (GetMemoState == 1 && npcId == floating_skeleton_2lv) {
            int i1 = Rnd.get(1000);
            if (i1 < 858) {
                st.giveItems(q_undead_bone_piece, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (GetMemoState == 1 && npcId == floating_zombie_1lv) {
            int i2 = Rnd.get(1000);
            if (i2 < 482) {
                st.giveItems(q_undead_bone_piece, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (GetMemoState == 1 && npcId == floating_zombie_2lv) {
            int i3 = Rnd.get(1000);
            if (i3 < 466) {
                st.giveItems(q_undead_bone_piece, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (GetMemoState == 1 && npcId == banshee_queen) {
            int i4 = Rnd.get(1000);
            if (i4 < 708) {
                st.giveItems(q_banshee_eyeball, Rnd.get(2) + 1);
                st.soundEffect(SOUND_ITEMGET);
            } else if (i4 < 978) {
                st.giveItems(q_banshee_eyeball, Rnd.get(3) + 3);
                st.soundEffect(SOUND_ITEMGET);
            } else if (i4 < 994) {
                st.giveItems(q_banshee_eyeball, Rnd.get(4) + 6);
                st.soundEffect(SOUND_ITEMGET);
            } else if (i4 < 998) {
                st.giveItems(q_banshee_eyeball, Rnd.get(4) + 10);
                st.soundEffect(SOUND_ITEMGET);
            } else if (i4 < 1000) {
                st.giveItems(q_banshee_eyeball, Rnd.get(5) + 14);
                st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }
}