package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.object.Player;

public class _646_SignsOfRevolt extends Quest {
    // NPCs
    private static int TORRANT = 32016;
    // Mobs
    private static int Ragna_Orc = 22029; // First in Range
    private static int Ragna_Orc_Sorcerer = 22044; // Last in Range
    private static int Guardian_of_the_Ghost_Town = 22047;
    private static int Varangkas_Succubus = 22049;
    // Items
    private static int Steel = 1880;
    private static int Coarse_Bone_Powder = 1881;
    private static int Leather = 1882;
    // Quest Items
    private static int CURSED_DOLL = 8087;
    // Chances
    private static int CURSED_DOLL_Chance = 75;

    public _646_SignsOfRevolt() {
        super(false);
        addStartNpc(TORRANT);
        for (int Ragna_Orc_id = Ragna_Orc; Ragna_Orc_id <= Ragna_Orc_Sorcerer; Ragna_Orc_id++) {
            addKillId(Ragna_Orc_id);
        }
        addKillId(Guardian_of_the_Ghost_Town);
        addKillId(Varangkas_Succubus);
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
            return doReward(st, Coarse_Bone_Powder, 12);
        } else if (event.equalsIgnoreCase("reward_steel") && _state == STARTED) {
            return doReward(st, Steel, 9);
        } else if (event.equalsIgnoreCase("reward_leather") && _state == STARTED) {
            return doReward(st, Leather, 20);
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