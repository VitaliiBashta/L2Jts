package org.mmocore.gameserver.scripts.quests;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _294_CovertBusiness extends Quest {
    public static final int BatFang = 1491;
    public static final int RingOfRaccoon = 1508;

    public static final int BarbedBat = 20370;
    public static final int BladeBat = 20480;

    public static final int Keef = 30534;


    public _294_CovertBusiness() {
        super(false);

        addStartNpc(Keef);
        addTalkId(Keef);

        addKillId(BarbedBat);
        addKillId(BladeBat);

        addQuestItem(BatFang);
        addLevelCheck(10, 16);
        addRaceCheck(PlayerRace.dwarf);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equalsIgnoreCase("elder_keef_q0294_03.htm")) {
            st.setCond(1);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int id = st.getState();

        if (id == CREATED) {
            switch (isAvailableFor(st.getPlayer())) {
                case LEVEL:
                    htmltext = "elder_keef_q0294_01.htm";
                    st.exitQuest(true);
                    break;
                case RACE:
                    htmltext = "elder_keef_q0294_00.htm";
                    st.exitQuest(true);
                    break;
                default:
                    htmltext = "elder_keef_q0294_02.htm";
                    return htmltext;
            }
        } else if (st.ownItemCount(BatFang) < 100) {
            htmltext = "elder_keef_q0294_04.htm";
        } else {
            if (st.ownItemCount(RingOfRaccoon) < 1) {
                st.giveItems(RingOfRaccoon, 1);
                htmltext = "elder_keef_q0294_05.htm";
            } else {
                st.giveItems(ADENA_ID, 2400);
                htmltext = "elder_keef_q0294_06.htm";
            }
            st.addExpAndSp(0, 600);
            st.soundEffect(SOUND_FINISH);
            st.exitQuest(true);
        }

        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 1) {
            st.rollAndGive(BatFang, 1, 2, 100, 100);
        }
        return null;
    }
}