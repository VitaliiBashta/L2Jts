package org.mmocore.gameserver.scripts.quests;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _168_DeliverSupplies extends Quest {
    final int JENNIES_LETTER_ID = 1153;
    final int SENTRY_BLADE1_ID = 1154;
    final int SENTRY_BLADE2_ID = 1155;
    final int SENTRY_BLADE3_ID = 1156;
    final int OLD_BRONZE_SWORD_ID = 1157;


    public _168_DeliverSupplies() {
        super(false);

        addStartNpc(30349);

        addTalkId(30349);
        addTalkId(30355);
        addTalkId(30357);
        addTalkId(30360);

        addQuestItem(new int[]{
                SENTRY_BLADE1_ID,
                OLD_BRONZE_SWORD_ID,
                JENNIES_LETTER_ID,
                SENTRY_BLADE2_ID,
                SENTRY_BLADE3_ID
        });
        addLevelCheck(3, 6);
        addRaceCheck(PlayerRace.darkelf);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {

        String htmltext = event;
        if (event.equals("1")) {
            st.setMemoState("id", "0");
            st.setCond(1);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            htmltext = "30349-03.htm";
            st.giveItems(JENNIES_LETTER_ID, 1);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {

        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npcId == 30349 && cond == 0) {
            if (cond < 15) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = "30349-01.htm";
                        st.exitQuest(true);
                        break;
                    case RACE:
                        htmltext = "30349-00.htm";
                        break;
                    default:
                        htmltext = "30349-02.htm";
                        break;
                }
            } else {
                htmltext = "30349-01.htm";
                st.exitQuest(true);
            }
        } else if (npcId == 30349 && cond == 1 && st.ownItemCount(JENNIES_LETTER_ID) > 0) {
            htmltext = "30349-04.htm";
        } else if (npcId == 30349 && cond == 2 && st.ownItemCount(SENTRY_BLADE1_ID) == 1 && st.ownItemCount(SENTRY_BLADE2_ID) == 1 && st.ownItemCount(SENTRY_BLADE3_ID) == 1) {
            htmltext = "30349-05.htm";
            st.takeItems(SENTRY_BLADE1_ID, 1);
            st.setCond(3);
        } else if (npcId == 30349 && cond == 3 && st.ownItemCount(SENTRY_BLADE1_ID) == 0 && (st.ownItemCount(SENTRY_BLADE2_ID) == 1 || st.ownItemCount(SENTRY_BLADE3_ID) == 1)) {
            htmltext = "30349-07.htm";
        } else if (npcId == 30349 && cond == 4 && st.ownItemCount(OLD_BRONZE_SWORD_ID) == 2) {
            htmltext = "30349-06.htm";
            st.takeItems(OLD_BRONZE_SWORD_ID, 2);
            st.removeMemo("cond");
            st.soundEffect(SOUND_FINISH);
            st.giveItems(ADENA_ID, 820);
            st.exitQuest(false);
        } else if (npcId == 30360 && cond == 1 && st.ownItemCount(JENNIES_LETTER_ID) == 1) {
            htmltext = "30360-01.htm";
            st.takeItems(JENNIES_LETTER_ID, 1);
            st.giveItems(SENTRY_BLADE1_ID, 1);
            st.giveItems(SENTRY_BLADE2_ID, 1);
            st.giveItems(SENTRY_BLADE3_ID, 1);
            st.setCond(2);
        } else if (npcId == 30360 && (cond == 2 || cond == 3) && st.ownItemCount(SENTRY_BLADE1_ID) + st.ownItemCount(SENTRY_BLADE2_ID) + st.ownItemCount(SENTRY_BLADE3_ID) > 0) {
            htmltext = "30360-02.htm";
        } else if (npcId == 30355 && cond == 3 && st.ownItemCount(SENTRY_BLADE2_ID) == 1 && st.ownItemCount(SENTRY_BLADE1_ID) == 0) {
            htmltext = "30355-01.htm";
            st.takeItems(SENTRY_BLADE2_ID, 1);
            st.giveItems(OLD_BRONZE_SWORD_ID, 1);
            if (st.ownItemCount(SENTRY_BLADE3_ID) == 0) {
                st.setCond(4);
            }
        } else if (npcId == 30355 && (cond == 4 || cond == 3) && st.ownItemCount(SENTRY_BLADE2_ID) == 0) {
            htmltext = "30355-02.htm";
        } else if (npcId == 30357 && cond == 3 && st.ownItemCount(SENTRY_BLADE3_ID) == 1 && st.ownItemCount(SENTRY_BLADE1_ID) == 0) {
            htmltext = "30357-01.htm";
            st.takeItems(SENTRY_BLADE3_ID, 1);
            st.giveItems(OLD_BRONZE_SWORD_ID, 1);
            if (st.ownItemCount(SENTRY_BLADE2_ID) == 0) {
                st.setCond(4);
            }
        } else if (npcId == 30357 && (cond == 4 || cond == 5) && st.ownItemCount(SENTRY_BLADE3_ID) == 0) {
            htmltext = "30357-02.htm";
        }
        return htmltext;
    }
}