package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _286_FabulousFeathers extends Quest {
    //NPCs
    private static int ERINU = 32164;
    //Mobs
    private static int Shady_Muertos_Captain = 22251;
    private static int Shady_Muertos_Warrior = 22253;
    private static int Shady_Muertos_Archer = 22254;
    private static int Shady_Muertos_Commander = 22255;
    private static int Shady_Muertos_Wizard = 22256;
    //Quest Items
    private static int Commanders_Feather = 9746;
    //Chances
    private static int Commanders_Feather_Chance = 66;

    public _286_FabulousFeathers() {
        super(false);
        addStartNpc(ERINU);
        addKillId(Shady_Muertos_Captain);
        addKillId(Shady_Muertos_Warrior);
        addKillId(Shady_Muertos_Archer);
        addKillId(Shady_Muertos_Commander);
        addKillId(Shady_Muertos_Wizard);
        addQuestItem(Commanders_Feather);
        addLevelCheck(17, 23);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int _state = st.getState();
        if (event.equalsIgnoreCase("trader_erinu_q0286_0103.htm") && _state == CREATED) {
            st.setState(STARTED);
            st.setCond(1);
            st.soundEffect(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("trader_erinu_q0286_0201.htm") && _state == STARTED) {
            st.takeItems(Commanders_Feather, -1);
            st.giveItems(ADENA_ID, 4160);
            st.soundEffect(SOUND_FINISH);
            st.exitQuest(true);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        if (npc.getNpcId() != ERINU) {
            return htmltext;
        }
        int _state = st.getState();

        if (_state == CREATED) {
            switch (isAvailableFor(st.getPlayer())) {
                case LEVEL:
                    htmltext = "trader_erinu_q0286_0102.htm";
                    st.exitQuest(true);
                    break;
                default:
                    htmltext = "trader_erinu_q0286_0101.htm";
                    st.setCond(0);
                    break;
            }
        } else if (_state == STARTED) {
            htmltext = st.ownItemCount(Commanders_Feather) >= 80 ? "trader_erinu_q0286_0105.htm" : "trader_erinu_q0286_0106.htm";
        }

        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState qs) {
        if (qs.getState() != STARTED) {
            return null;
        }

        long Commanders_Feather_count = qs.ownItemCount(Commanders_Feather);
        if (Commanders_Feather_count < 80 && Rnd.chance(Commanders_Feather_Chance)) {
            qs.giveItems(Commanders_Feather, 1);
            if (Commanders_Feather_count == 79) {
                qs.setCond(2);
                qs.soundEffect(SOUND_MIDDLE);
            } else {
                qs.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }


}