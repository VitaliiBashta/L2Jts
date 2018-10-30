package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _432_BirthdayPartySong extends Quest {
    //NPC
    private static int MELODY_MAESTRO_OCTAVIA = 31043;
    //MOB
    private static int ROUGH_HEWN_ROCK_GOLEMS = 21103;
    //Quest items
    private static int RED_CRYSTALS = 7541;
    private static int BIRTHDAY_ECHO_CRYSTAL = 7061;


    public _432_BirthdayPartySong() {
        super(false);

        addStartNpc(MELODY_MAESTRO_OCTAVIA);

        addKillId(ROUGH_HEWN_ROCK_GOLEMS);

        addQuestItem(RED_CRYSTALS);
        addLevelCheck(31, 36);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equalsIgnoreCase("muzyko_q0432_0104.htm")) {
            st.setState(STARTED);
            st.setCond(1);
            st.soundEffect(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("muzyko_q0432_0201.htm")) {
            if (st.ownItemCount(RED_CRYSTALS) == 50) {
                st.takeItems(RED_CRYSTALS, -1);
                st.giveItems(BIRTHDAY_ECHO_CRYSTAL, 25);
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
            } else {
                htmltext = "muzyko_q0432_0202.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int condition = st.getCond();
        int npcId = npc.getNpcId();
        if (npcId == MELODY_MAESTRO_OCTAVIA) {
            if (condition == 0) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = "muzyko_q0432_0103.htm";
                        st.exitQuest(true);
                        break;
                    default:
                        htmltext = "muzyko_q0432_0101.htm";
                        break;
                }
            } else if (condition == 1) {
                htmltext = "muzyko_q0432_0106.htm";
            } else if (condition == 2 && st.ownItemCount(RED_CRYSTALS) == 50) {
                htmltext = "muzyko_q0432_0105.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        if (st.getState() != STARTED) {
            return null;
        }
        int npcId = npc.getNpcId();

        if (npcId == ROUGH_HEWN_ROCK_GOLEMS) {
            if (st.getCond() == 1 && st.ownItemCount(RED_CRYSTALS) < 50) {
                st.giveItems(RED_CRYSTALS, 1);

                if (st.ownItemCount(RED_CRYSTALS) == 50) {
                    st.soundEffect(SOUND_MIDDLE);
                    st.setCond(2);
                } else {
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        }
        return null;
    }
}