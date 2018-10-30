package org.mmocore.gameserver.scripts.quests;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _165_ShilensHunt extends Quest {
    private static final int DARK_BEZOAR = 1160;
    private static final int LESSER_HEALING_POTION = 1060;


    public _165_ShilensHunt() {
        super(false);

        addStartNpc(30348);

        addTalkId(30348);

        addKillId(20456);
        addKillId(20529);
        addKillId(20532);
        addKillId(20536);

        addQuestItem(DARK_BEZOAR);
        addLevelCheck(3, 7);
        addRaceCheck(PlayerRace.darkelf);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equals("1")) {
            st.setCond(1);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            htmltext = "30348-03.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();

        if (cond == 0) {
            switch (isAvailableFor(st.getPlayer())) {
                case LEVEL:
                    htmltext = "30348-01.htm";
                    st.exitQuest(true);
                    break;
                case RACE:
                    htmltext = "30348-00.htm";
                    break;
                default:
                    htmltext = "30348-02.htm";
                    return htmltext;
            }
        } else if (cond == 1 || st.ownItemCount(DARK_BEZOAR) < 13) {
            htmltext = "30348-04.htm";
        } else if (cond == 2) {
            htmltext = "30348-05.htm";
            st.takeItems(DARK_BEZOAR, -1);
            st.giveItems(LESSER_HEALING_POTION, 5);
            st.addExpAndSp(1000, 0);
            st.soundEffect(SOUND_FINISH);
            st.exitQuest(true);
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int cond = st.getCond();
        if (cond == 1 && st.ownItemCount(DARK_BEZOAR) < 13 && Rnd.chance(90)) {
            st.giveItems(DARK_BEZOAR, 1);
            if (st.ownItemCount(DARK_BEZOAR) == 13) {
                st.setCond(2);
                st.soundEffect(SOUND_MIDDLE);
            } else {
                st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }
}