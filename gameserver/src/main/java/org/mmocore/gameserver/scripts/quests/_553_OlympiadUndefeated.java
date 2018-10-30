package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.model.entity.olympiad.OlympiadGame;
import org.mmocore.gameserver.model.entity.olympiad.OlympiadTeam;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.object.Player;

public class _553_OlympiadUndefeated extends Quest {
    // NPCs
    private static final int OLYMPIAD_MANAGER = 31688;

    // Items
    private static final int MEDAL_OF_GLORY = 21874;
    private static final int OLYMPIAD_CHEST = 17169;
    private static final int WINS_CONFIRMATION1 = 17244;
    private static final int WINS_CONFIRMATION2 = 17245;
    private static final int WINS_CONFIRMATION3 = 17246;

    public _553_OlympiadUndefeated() {
        super(false);

        addStartNpc(OLYMPIAD_MANAGER);
        addTalkId(OLYMPIAD_MANAGER);
        addQuestItem(WINS_CONFIRMATION1, WINS_CONFIRMATION2, WINS_CONFIRMATION3);
        addLevelCheck(75);
        addClassLevelCheck(4);
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        switch (npcId) {
            case OLYMPIAD_MANAGER:
                Player player = st.getPlayer();

                if (st.isCreated()) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                        case CLASS_LEVEL:
                            return "olympiad_operator_q0553_08.htm";
                        default:
                            if (!player.isNoble())
                                return "olympiad_operator_q0551_08.htm";
                            else if (st.isNowAvailable())
                                return "olympiad_operator_q0553_01.htm";
                            else
                                return "olympiad_operator_q0553_06.htm";
                    }
                } else if (st.isStarted()) {
                    if (st.ownItemCount(WINS_CONFIRMATION1, WINS_CONFIRMATION2, WINS_CONFIRMATION3) == 0) {
                        return "olympiad_operator_q0553_04.htm";
                    }

                    if (st.ownItemCount(WINS_CONFIRMATION3) > 0) {
                        st.giveItems(OLYMPIAD_CHEST, 6);
                        st.giveItems(MEDAL_OF_GLORY, 5);
                        st.takeItems(WINS_CONFIRMATION1, -1);
                        st.takeItems(WINS_CONFIRMATION2, -1);
                        st.takeItems(WINS_CONFIRMATION3, -1);
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(this);
                        return "olympiad_operator_q0553_07.htm";
                    } else {
                        return "olympiad_operator_q0553_05.htm";
                    }
                }
                break;
        }

        return null;
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equalsIgnoreCase("olympiad_operator_q0553_03.htm")) {
            st.setCond(1);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
        }
        if (event.equalsIgnoreCase("olympiad_operator_q0553_07.htm")) {
            if (st.ownItemCount(WINS_CONFIRMATION3) > 0) {
                st.giveItems(OLYMPIAD_CHEST, 6);
                st.giveItems(MEDAL_OF_GLORY, 5);
                st.takeItems(WINS_CONFIRMATION1, -1);
                st.takeItems(WINS_CONFIRMATION2, -1);
                st.takeItems(WINS_CONFIRMATION3, -1);
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(this);
            } else if (st.ownItemCount(WINS_CONFIRMATION2) > 0) {
                st.giveItems(OLYMPIAD_CHEST, 3);
                st.giveItems(MEDAL_OF_GLORY, 3); // от балды
                st.takeItems(WINS_CONFIRMATION1, -1);
                st.takeItems(WINS_CONFIRMATION2, -1);
                st.takeItems(WINS_CONFIRMATION3, -1);
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(this);
            } else if (st.ownItemCount(WINS_CONFIRMATION1) > 0) {
                st.giveItems(OLYMPIAD_CHEST, 1);
                st.takeItems(WINS_CONFIRMATION1, -1);
                st.takeItems(WINS_CONFIRMATION2, -1);
                st.takeItems(WINS_CONFIRMATION3, -1);
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(this);
            }
        }
        return event;
    }

    @Override
    public void onOlympiadEnd(OlympiadGame og, QuestState qs) {
        if (qs.getCond() == 1) {
            int count = qs.getInt("count");
            OlympiadTeam winner = og.getWinnerTeam();
            if (winner != null && winner.contains(qs.getPlayer().getObjectId())) {
                count++;
            } else {
                count = 0;
            }

            qs.setMemoState("count", count);
            if (count == 2 && qs.ownItemCount(WINS_CONFIRMATION1) == 0) {
                qs.giveItems(WINS_CONFIRMATION1, 1);
                qs.soundEffect(SOUND_ITEMGET);
            } else if (count == 5 && qs.ownItemCount(WINS_CONFIRMATION2) == 0) {
                qs.giveItems(WINS_CONFIRMATION2, 1);
                qs.soundEffect(SOUND_ITEMGET);
            } else if (count == 10 && qs.ownItemCount(WINS_CONFIRMATION3) == 0) {
                qs.giveItems(WINS_CONFIRMATION3, 2);
                qs.setCond(2);
                qs.soundEffect(SOUND_MIDDLE);
            }
            if (count < 10 && qs.ownItemCount(WINS_CONFIRMATION3) > 0) {
                qs.takeItems(WINS_CONFIRMATION3, -1);
            }
            if (count < 5 && qs.ownItemCount(WINS_CONFIRMATION2) > 0) {
                qs.takeItems(WINS_CONFIRMATION2, -1);
            }
            if (count < 2 && qs.ownItemCount(WINS_CONFIRMATION1) > 0) {
                qs.takeItems(WINS_CONFIRMATION1, -1);
            }
        }
    }


}
