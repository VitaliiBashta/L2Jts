package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _363_SorrowfulSoundofFlute extends Quest {
    //NPC
    public final int NANARIN = 30956;
    public final int BARBADO = 30959;
    public final int POITAN = 30458;
    public final int HOLVAS = 30058;
    //Mobs

    //Quest Item
    public final int MUSICAL_SCORE = 4420;
    public final int EVENT_CLOTHES = 4318;
    public final int NANARINS_FLUTE = 4319;
    public final int SABRINS_BLACK_BEER = 4320;
    public final int Musical_Score = 4420;
    //Item

    public _363_SorrowfulSoundofFlute() {
        super(false);

        addStartNpc(NANARIN);
        addTalkId(NANARIN);
        addTalkId(POITAN);
        addTalkId(HOLVAS);
        addTalkId(BARBADO);
        addLevelCheck(15);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equalsIgnoreCase("30956_2.htm")) {
            st.setCond(1);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            st.takeItems(EVENT_CLOTHES, -1);
            st.takeItems(NANARINS_FLUTE, -1);
            st.takeItems(SABRINS_BLACK_BEER, -1);
        } else if (event.equalsIgnoreCase("30956_4.htm")) {
            st.giveItems(NANARINS_FLUTE, 1);
            st.soundEffect(SOUND_MIDDLE);
            st.setCond(3);
        } else if (event.equalsIgnoreCase("answer1")) {
            st.giveItems(EVENT_CLOTHES, 1);
            st.soundEffect(SOUND_MIDDLE);
            st.setCond(3);
            htmltext = "30956_6.htm";
        } else if (event.equalsIgnoreCase("answer2")) {
            st.giveItems(SABRINS_BLACK_BEER, 1);
            st.soundEffect(SOUND_MIDDLE);
            st.setCond(3);
            htmltext = "30956_6.htm";
        } else if (event.equalsIgnoreCase("30956_7.htm")) {
            st.giveItems(Musical_Score, 1);
            st.soundEffect(SOUND_FINISH);
            st.exitQuest(true);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npcId == NANARIN) {
            if (cond == 0) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = "30956-00.htm";
                        st.exitQuest(true);
                        break;
                    default:
                        htmltext = "30956_1.htm";
                        break;
                }
            } else if (cond == 1) {
                htmltext = "30956_8.htm";
            } else if (cond == 2) {
                htmltext = "30956_3.htm";
            } else if (cond == 3) {
                htmltext = "30956_6.htm";
            } else if (cond == 4) {
                htmltext = "30956_5.htm";
            }
        } else if (npcId == BARBADO) {
            if (cond == 3) {
                if (st.ownItemCount(EVENT_CLOTHES) > 0) {
                    st.takeItems(EVENT_CLOTHES, -1);
                    htmltext = "30959_2.htm";
                    st.exitQuest(true);
                } else if (st.ownItemCount(SABRINS_BLACK_BEER) > 0) {
                    st.takeItems(SABRINS_BLACK_BEER, -1);
                    htmltext = "30959_2.htm";
                    st.exitQuest(true);
                } else {
                    st.takeItems(NANARINS_FLUTE, -1);
                    st.setCond(4);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "30959_1.htm";
                }
            } else if (cond == 4) {
                htmltext = "30959_3.htm";
            }
        } else if (npcId == HOLVAS && (cond == 1 || cond == 2)) {
            st.setCond(2);
            if (Rnd.chance(60)) {
                htmltext = "30058_2.htm";
            } else {
                htmltext = "30058_1.htm";
            }
        } else if (npcId == POITAN && (cond == 1 || cond == 2)) {
            st.setCond(2);
            if (Rnd.chance(60)) {
                htmltext = "30458_2.htm";
            } else {
                htmltext = "30458_1.htm";
            }
        }
        return htmltext;
    }

}
