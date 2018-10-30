package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _295_DreamsOfTheSkies extends Quest {
    public static final int FLOATING_STONE = 1492;
    public static final int RING_OF_FIREFLY = 1509;

    public static final int Arin = 30536;
    public static final int MagicalWeaver = 20153;


    public _295_DreamsOfTheSkies() {
        super(false);

        addStartNpc(Arin);
        addTalkId(Arin);
        addKillId(MagicalWeaver);

        addQuestItem(FLOATING_STONE);
        addLevelCheck(11, 15);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equalsIgnoreCase("elder_arin_q0295_03.htm")) {
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
            st.setCond(0);
        }
        int cond = st.getCond();
        if (cond == 0) {
            switch (isAvailableFor(st.getPlayer())) {
                case LEVEL:
                    htmltext = "elder_arin_q0295_01.htm";
                    st.exitQuest(true);
                    break;
                default:
                    htmltext = "elder_arin_q0295_02.htm";
                    return htmltext;
            }
        } else if (cond == 1 || st.ownItemCount(FLOATING_STONE) < 50) {
            htmltext = "elder_arin_q0295_04.htm";
        } else if (cond == 2 && st.ownItemCount(FLOATING_STONE) == 50) {
            st.addExpAndSp(0, 500);
            st.soundEffect(SOUND_FINISH);
            st.exitQuest(true);
            if (st.ownItemCount(RING_OF_FIREFLY) < 1) {
                htmltext = "elder_arin_q0295_05.htm";
                st.giveItems(RING_OF_FIREFLY, 1);
            } else {
                htmltext = "elder_arin_q0295_06.htm";
                st.giveItems(ADENA_ID, 2400);
            }
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 1 && st.ownItemCount(FLOATING_STONE) < 50) {
            if (Rnd.chance(25)) {
                st.giveItems(FLOATING_STONE, 1);
                if (st.ownItemCount(FLOATING_STONE) == 50) {
                    st.soundEffect(SOUND_MIDDLE);
                    st.setCond(2);
                } else {
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (st.ownItemCount(FLOATING_STONE) >= 48) {
                st.giveItems(FLOATING_STONE, 50 - st.ownItemCount(FLOATING_STONE));
                st.soundEffect(SOUND_MIDDLE);
                st.setCond(2);
            } else {
                st.giveItems(FLOATING_STONE, 2);
                st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }
}