package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _324_SweetestVenom extends Quest {
    //NPCs
    private static final int ASTARON = 30351;
    //Mobs
    private static final int Prowler = 20034;
    //Items
    private static final int VENOM_SAC = 1077;

    public _324_SweetestVenom() {
        super(false);
        addStartNpc(ASTARON);
        addKillId(Prowler);
        int venomous_Spider = 20038;
        addKillId(venomous_Spider);
        int arachnid_Tracker = 20043;
        addKillId(arachnid_Tracker);
        addQuestItem(VENOM_SAC);
        addLevelCheck(18, 23);
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        if (npc.getNpcId() != ASTARON) {
            return htmltext;
        }
        int _state = st.getState();

        if (_state == CREATED) {
            switch (isAvailableFor(st.getPlayer())) {
                case LEVEL:
                    htmltext = "astaron_q0324_02.htm";
                    st.exitQuest(true);
                    break;
                default:
                    htmltext = "astaron_q0324_03.htm";
                    st.setCond(0);
                    break;
            }
        } else if (_state == STARTED) {
            long _count = st.ownItemCount(VENOM_SAC);
            if (_count >= 10) {
                htmltext = "astaron_q0324_06.htm";
                st.takeItems(VENOM_SAC, -1);
                st.giveItems(ADENA_ID, 5810);
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
            } else {
                htmltext = "astaron_q0324_05.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equalsIgnoreCase("astaron_q0324_04.htm") && st.getState() == CREATED) {
            st.setState(STARTED);
            st.setCond(1);
            st.soundEffect(SOUND_ACCEPT);
        }
        return event;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState qs) {
        if (qs.getState() != STARTED) {
            return null;
        }

        long _count = qs.ownItemCount(VENOM_SAC);
        //Chances
        int VENOM_SAC_BASECHANCE = 60;
        int _chance = VENOM_SAC_BASECHANCE + (npc.getNpcId() - Prowler) / 4 * 12;

        if (_count < 10 && Rnd.chance(_chance)) {
            qs.giveItems(VENOM_SAC, 1);
            if (_count == 9) {
                qs.setCond(2);
                qs.soundEffect(SOUND_MIDDLE);
            } else {
                qs.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }
}