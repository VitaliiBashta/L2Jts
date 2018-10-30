package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.object.Player;

public class _646_SignsOfRevolt extends Quest {
    // NPCs
    private static final int TORRANT = 32016;
    // Quest Items
    private static final int CURSED_DOLL = 8087;

    public _646_SignsOfRevolt() {
        super(false);
        addStartNpc(TORRANT);
        // Last in Range
        int ragna_Orc_Sorcerer = 22044;// Mobs
// First in Range
        int ragna_Orc = 22029;
        for (int Ragna_Orc_id = ragna_Orc; Ragna_Orc_id <= ragna_Orc_Sorcerer; Ragna_Orc_id++) {
            addKillId(Ragna_Orc_id);
        }
        int guardian_of_the_Ghost_Town = 22047;
        addKillId(guardian_of_the_Ghost_Town);
        int varangkas_Succubus = 22049;
        addKillId(varangkas_Succubus);
        addQuestItem(CURSED_DOLL);
        addLevelCheck(40, 51);
    }

    private static String doReward(QuestState st, int reward_id, int _count) {
        if (st.ownItemCount(CURSED_DOLL) < 180) {
            return null;
        }
        st.takeItems(CURSED_DOLL, -1);
        st.giveItems(reward_id, _count, true);
        st.soundEffect(SOUND_FINISH);
        st.exitQuest(true);
        return "torant_q0646_0202.htm";
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int _state = st.getState();
        if (event.equalsIgnoreCase("torant_q0646_0103.htm") && _state == CREATED) {
            st.setState(STARTED);
            st.setCond(1);
            st.soundEffect(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("reward_adena") && _state == STARTED) {
            return doReward(st, ADENA_ID, 21600);
        } else if (event.equalsIgnoreCase("reward_cbp") && _state == STARTED) {
            int coarse_Bone_Powder = 1881;
            return doReward(st, coarse_Bone_Powder, 12);
        } else if (event.equalsIgnoreCase("reward_steel") && _state == STARTED) {
            // Items
            int steel = 1880;
            return doReward(st, steel, 9);
        } else if (event.equalsIgnoreCase("reward_leather") && _state == STARTED) {
            int leather = 1882;
            return doReward(st, leather, 20);
        }

        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        if (npc.getNpcId() != TORRANT) {
            return htmltext;
        }
        int _state = st.getState();

        if (_state == CREATED) {
            switch (isAvailableFor(st.getPlayer())) {
                case LEVEL:
                    htmltext = "torant_q0646_0102.htm";
                    st.exitQuest(true);
                    break;
                default:
                    htmltext = "torant_q0646_0101.htm";
                    st.setCond(0);
                    break;
            }
        } else if (_state == STARTED) {
            htmltext = st.ownItemCount(CURSED_DOLL) >= 180 ? "torant_q0646_0105.htm" : "torant_q0646_0106.htm";
        }

        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState qs) {
        Player player = qs.getRandomPartyMember(STARTED, AllSettingsConfig.ALT_PARTY_DISTRIBUTION_RANGE);
        if (player == null) {
            return null;
        }
        QuestState st = player.getQuestState(qs.getQuest().getId());

        long CURSED_DOLL_COUNT = st.ownItemCount(CURSED_DOLL);
        // Chances
        int CURSED_DOLL_Chance = 75;
        if (CURSED_DOLL_COUNT < 180 && Rnd.chance(CURSED_DOLL_Chance)) {
            st.giveItems(CURSED_DOLL, 1);
            if (CURSED_DOLL_COUNT == 179) {
                st.soundEffect(SOUND_MIDDLE);
                st.setCond(2);
            } else {
                st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }


}