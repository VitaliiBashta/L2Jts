package org.mmocore.gameserver.scripts.quests;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _267_WrathOfVerdure extends Quest {
    //NPCs
    private static final int Treant_Bremec = 31853;
    //Quest Items
    private static final int Goblin_Club = 1335;

    public _267_WrathOfVerdure() {
        super(false);
        addStartNpc(Treant_Bremec);
        //Mobs
        int goblin_Raider = 20325;
        addKillId(goblin_Raider);
        addQuestItem(Goblin_Club);
        addLevelCheck(4, 9);
        addRaceCheck(PlayerRace.elf);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int _state = st.getState();
        if (event.equalsIgnoreCase("bri_mec_tran_q0267_03.htm") && _state == CREATED && st.getPlayer().getPlayerTemplateComponent().getPlayerRace() == PlayerRace.elf && st.getPlayer().getLevel() >= 4) {
            st.setState(STARTED);
            st.setCond(1);
            st.soundEffect(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("bri_mec_tran_q0267_06.htm") && _state == STARTED) {
            st.soundEffect(SOUND_FINISH);
            st.exitQuest(false);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        if (npc.getNpcId() != Treant_Bremec) {
            return htmltext;
        }
        int _state = st.getState();
        if (_state == CREATED) {
            switch (isAvailableFor(st.getPlayer())) {
                case LEVEL:
                    htmltext = "bri_mec_tran_q0267_01.htm";
                    st.exitQuest(true);
                    break;
                case RACE:
                    htmltext = "bri_mec_tran_q0267_00.htm";
                    st.exitQuest(true);
                    break;
                default:
                    htmltext = "bri_mec_tran_q0267_02.htm";
                    st.setCond(0);
                    break;
            }
        } else if (_state == STARTED) {
            long Goblin_Club_Count = st.ownItemCount(Goblin_Club);
            if (Goblin_Club_Count > 0) {
                htmltext = "bri_mec_tran_q0267_05.htm";
                st.takeItems(Goblin_Club, -1);
                //Items
                int silvery_Leaf = 1340;
                st.giveItems(silvery_Leaf, Goblin_Club_Count);
                st.soundEffect(SOUND_MIDDLE);
            } else {
                htmltext = "bri_mec_tran_q0267_04.htm";
            }
        }

        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState qs) {
        if (qs.getState() != STARTED) {
            return null;
        }

        //Chances
        int goblin_Club_Chance = 50;
        if (Rnd.chance(goblin_Club_Chance)) {
            qs.giveItems(Goblin_Club, 1);
            qs.soundEffect(SOUND_ITEMGET);
        }
        return null;
    }


}
