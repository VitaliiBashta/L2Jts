package org.mmocore.gameserver.scripts.quests;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

/**
 * Квест Proof Of Valor
 *
 * @author Sergey Ibryaev aka Artful
 */

public class _271_ProofOfValor extends Quest {
    //NPC
    private static final int RUKAIN = 30577;
    //Quest Item
    private static final int KASHA_WOLF_FANG_ID = 1473;
    private static final int NECKLACE_OF_VALOR_ID = 1507;
    private static final int NECKLACE_OF_COURAGE_ID = 1506;
    //Drop Cond
    //# [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]
    private static final int[][] DROPLIST_COND = {
            {
                    1,
                    2,
                    20475,
                    0,
                    KASHA_WOLF_FANG_ID,
                    50,
                    25,
                    2
            }
    };


    public _271_ProofOfValor() {
        super(false);

        addStartNpc(RUKAIN);
        addTalkId(RUKAIN);

        //Mob Drop
        for (int i = 0; i < DROPLIST_COND.length; i++) {
            addKillId(DROPLIST_COND[i][2]);
        }

        addQuestItem(KASHA_WOLF_FANG_ID);
        addLevelCheck(4, 8);
        addRaceCheck(PlayerRace.orc);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equalsIgnoreCase("praetorian_rukain_q0271_03.htm")) {
            st.soundEffect(SOUND_ACCEPT);
            if (st.ownItemCount(NECKLACE_OF_COURAGE_ID) > 0 || st.ownItemCount(NECKLACE_OF_VALOR_ID) > 0) {
                htmltext = "praetorian_rukain_q0271_07.htm";
            }
            st.setCond(1);
            st.setState(STARTED);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npcId == RUKAIN) {
            if (cond == 0) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = "praetorian_rukain_q0271_01.htm";
                        st.exitQuest(true);
                        break;
                    case RACE:
                        htmltext = "praetorian_rukain_q0271_00.htm";
                        st.exitQuest(true);
                        break;
                    default:
                        if (st.ownItemCount(NECKLACE_OF_COURAGE_ID) > 0 || st.ownItemCount(NECKLACE_OF_VALOR_ID) > 0) {
                            htmltext = "praetorian_rukain_q0271_06.htm";
                            st.exitQuest(true);
                        } else
                            htmltext = "praetorian_rukain_q0271_02.htm";
                        break;
                }
            } else if (cond == 1) {
                htmltext = "praetorian_rukain_q0271_04.htm";
            } else if (cond == 2 && st.ownItemCount(KASHA_WOLF_FANG_ID) == 50) {
                st.takeItems(KASHA_WOLF_FANG_ID, -1);
                if (Rnd.chance(14)) {
                    st.takeItems(NECKLACE_OF_VALOR_ID, -1);
                    st.giveItems(NECKLACE_OF_VALOR_ID, 1);
                } else {
                    st.takeItems(NECKLACE_OF_COURAGE_ID, -1);
                    st.giveItems(NECKLACE_OF_COURAGE_ID, 1);
                }
                htmltext = "praetorian_rukain_q0271_05.htm";
                st.exitQuest(true);
            } else if (cond == 2 && st.ownItemCount(KASHA_WOLF_FANG_ID) < 50) {
                htmltext = "praetorian_rukain_q0271_04.htm";
                st.setCond(1);
                st.setState(STARTED);
            }
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        for (int i = 0; i < DROPLIST_COND.length; i++) {
            if (cond == DROPLIST_COND[i][0] && npcId == DROPLIST_COND[i][2]) {
                if (DROPLIST_COND[i][3] == 0 || st.ownItemCount(DROPLIST_COND[i][3]) > 0) {
                    if (DROPLIST_COND[i][5] == 0) {
                        st.rollAndGive(DROPLIST_COND[i][4], DROPLIST_COND[i][7], DROPLIST_COND[i][6]);
                    } else if (st.rollAndGive(DROPLIST_COND[i][4], DROPLIST_COND[i][7], DROPLIST_COND[i][7], DROPLIST_COND[i][5], DROPLIST_COND[i][6])) {
                        if (DROPLIST_COND[i][1] != cond && DROPLIST_COND[i][1] != 0) {
                            st.setCond(Integer.valueOf(DROPLIST_COND[i][1]));
                            st.setState(STARTED);
                        }
                    }
                }
            }
        }
        return null;
    }
}
