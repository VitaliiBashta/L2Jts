package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

/**
 * @author pchayka
 * Repeatable
 */
public class _907_DragonTrophyValakas extends Quest {
    private static final int Klein = 31540;
    private static final int Valakas = 29028;
    private static final int MedalofGlory = 21874;

    public _907_DragonTrophyValakas() {
        super(PARTY_ALL);
        addStartNpc(Klein);
        addKillId(Valakas);
        addLevelCheck(84);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equalsIgnoreCase("klein_q907_04.htm")) {
            st.setState(STARTED);
            st.setCond(1);
            st.soundEffect(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("klein_q907_07.htm")) {
            st.giveItems(MedalofGlory, 30);
            st.setState(COMPLETED);
            st.soundEffect(SOUND_FINISH);
            st.exitQuest(true);
        }

        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npc.getNpcId() == Klein) {
            switch (st.getState()) {
                case CREATED:
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "klein_q907_00.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            if (st.ownItemCount(7267) > 0)
                                htmltext = "klein_q907_01.htm";
                            else
                                htmltext = "klein_q907_00b.htm";
                            break;
                    }
                    break;
                case STARTED:
                    if (cond == 1) {
                        htmltext = "klein_q907_05.htm";
                    } else if (cond == 2) {
                        htmltext = "klein_q907_06.htm";
                    }
                    break;
            }
        }

        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int cond = st.getCond();
        if (cond == 1) {
            if (npc.getNpcId() == Valakas) {
                st.setCond(2);
            }
        }
        return null;
    }


}