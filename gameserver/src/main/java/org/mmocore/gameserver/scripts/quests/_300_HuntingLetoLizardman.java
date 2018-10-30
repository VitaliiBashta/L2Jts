package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _300_HuntingLetoLizardman extends Quest {
    //NPCs
    private static int RATH = 30126;
    //Items
    private static int BRACELET_OF_LIZARDMAN = 7139;
    private static int ANIMAL_BONE = 1872;
    private static int ANIMAL_SKIN = 1867;
    //Chances
    private static int BRACELET_OF_LIZARDMAN_CHANCE = 70;

    public _300_HuntingLetoLizardman() {
        super(false);
        addStartNpc(RATH);
        for (int lizardman_id = 20577; lizardman_id <= 20582; lizardman_id++) {
            addKillId(lizardman_id);
        }
        addQuestItem(BRACELET_OF_LIZARDMAN);
        addLevelCheck(34, 39);
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        if (npc.getNpcId() != RATH) {
            return htmltext;
        }
        if (st.getState() == CREATED) {
            switch (isAvailableFor(st.getPlayer())) {
                case LEVEL:
                    htmltext = "rarshints_q0300_0103.htm";
                    st.exitQuest(true);
                    break;
                default:
                    htmltext = "rarshints_q0300_0101.htm";
                    st.setCond(0);
                    break;
            }
        } else if (st.ownItemCount(BRACELET_OF_LIZARDMAN) < 60) {
            htmltext = "rarshints_q0300_0106.htm";
            st.setCond(1);
        } else {
            htmltext = "rarshints_q0300_0105.htm";
        }
        return htmltext;
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int _state = st.getState();
        if (event.equalsIgnoreCase("rarshints_q0300_0104.htm") && _state == CREATED) {
            st.setState(STARTED);
            st.setCond(1);
            st.soundEffect(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("rarshints_q0300_0201.htm") && _state == STARTED) {
            if (st.ownItemCount(BRACELET_OF_LIZARDMAN) < 60) {
                htmltext = "rarshints_q0300_0202.htm";
                st.setCond(1);
            } else {
                st.takeItems(BRACELET_OF_LIZARDMAN, -1);
                switch (Rnd.get(3)) {
                    case 0:
                        st.giveItems(ADENA_ID, 30000, true);
                        break;
                    case 1:
                        st.giveItems(ANIMAL_BONE, 50, true);
                        break;
                    case 2:
                        st.giveItems(ANIMAL_SKIN, 50, true);
                        break;
                }
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
            }
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState qs) {
        if (qs.getState() != STARTED) {
            return null;
        }

        long _count = qs.ownItemCount(BRACELET_OF_LIZARDMAN);
        if (_count < 60 && Rnd.chance(BRACELET_OF_LIZARDMAN_CHANCE)) {
            qs.giveItems(BRACELET_OF_LIZARDMAN, 1);
            if (_count == 59) {
                qs.setCond(2);
                qs.soundEffect(SOUND_MIDDLE);
            } else {
                qs.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }
}