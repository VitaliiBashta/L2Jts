package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

/**
 * Квест Keen Claws
 *
 * @author Sergey Ibryaev aka Artful
 */

public class _264_KeenClaws extends Quest {
    //NPC
    private static final int Payne = 30136;
    //Quest Items
    private static final int WolfClaw = 1367;
    //Items
    private static final int LeatherSandals = 36;
    private static final int WoodenHelmet = 43;
    private static final int Stockings = 462;
    private static final int HealingPotion = 1061;
    private static final int ShortGloves = 48;
    private static final int ClothShoes = 35;
    //MOB
    private static final int Goblin = 20003;
    private static final int AshenWolf = 20456;
    //Drop Cond
    //# [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]
    private static final int[][] DROPLIST_COND = {
            {
                    1,
                    2,
                    Goblin,
                    0,
                    WolfClaw,
                    50,
                    50,
                    2
            },
            {
                    1,
                    2,
                    AshenWolf,
                    0,
                    WolfClaw,
                    50,
                    50,
                    2
            }
    };

    public _264_KeenClaws() {
        super(false);

        addStartNpc(Payne);

        addKillId(Goblin);
        addKillId(AshenWolf);

        addQuestItem(WolfClaw);
        addLevelCheck(3, 9);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equalsIgnoreCase("paint_q0264_03.htm")) {
            st.setCond(1);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == Payne) {
            if (cond == 0) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        st.exitQuest(true);
                        return "paint_q0264_01.htm";
                    default:
                        htmltext = "paint_q0264_02.htm";
                        break;
                }
            } else if (cond == 1) {
                htmltext = "paint_q0264_04.htm";
            } else if (cond == 2) {
                st.takeItems(WolfClaw, -1);
                int n = Rnd.get(17);
                if (n == 0) {
                    st.giveItems(WoodenHelmet, 1);
                    st.soundEffect(SOUND_JACKPOT);
                } else if (n < 2) {
                    st.giveItems(ADENA_ID, 1000);
                } else if (n < 5) {
                    st.giveItems(LeatherSandals, 1);
                } else if (n < 8) {
                    st.giveItems(Stockings, 1);
                    st.giveItems(ADENA_ID, 50);
                } else if (n < 11) {
                    st.giveItems(HealingPotion, 1);
                } else if (n < 14) {
                    st.giveItems(ShortGloves, 1);
                } else {
                    st.giveItems(ClothShoes, 1);
                }
                htmltext = "paint_q0264_05.htm";
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
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