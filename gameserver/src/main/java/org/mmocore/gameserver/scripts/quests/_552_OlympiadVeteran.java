package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.model.entity.olympiad.OlympiadGame;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.object.Player;

public class _552_OlympiadVeteran extends Quest {
    // NPCs
    private static final int OLYMPIAD_MANAGER = 31688;

    // Items
    //private static final int MEDAL_OF_GLORY = 21874;
    private static final int OLYMPIAD_CHEST = 17169;
    private static final int TEAM_CERTIFICATE = 17241;
    private static final int CLASS_FREE_CERTIFICATE = 17242;
    private static final int CLASS_CERTIFICATE = 17243;

    public _552_OlympiadVeteran() {
        super(false);

        addStartNpc(OLYMPIAD_MANAGER);
        addTalkId(OLYMPIAD_MANAGER);
        addQuestItem(TEAM_CERTIFICATE, CLASS_FREE_CERTIFICATE, CLASS_CERTIFICATE);
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
                            return "olympiad_operator_q0552_08.htm";
                        default:
                            if (!player.isNoble())
                                return "olympiad_operator_q0551_08.htm";
                            else if (st.isNowAvailable())
                                return "olympiad_operator_q0552_01.htm";
                            else
                                return "olympiad_operator_q0552_06.htm";
                    }
                } else if (st.isStarted()) {
                    if (st.ownItemCount(TEAM_CERTIFICATE, CLASS_FREE_CERTIFICATE, CLASS_CERTIFICATE) == 0) {
                        return "olympiad_operator_q0552_04.htm";
                    } else if (st.ownItemCount(TEAM_CERTIFICATE) > 0 && st.ownItemCount(CLASS_FREE_CERTIFICATE) > 0 && st.ownItemCount(CLASS_CERTIFICATE) > 0) {
                        st.giveItems(OLYMPIAD_CHEST, 3);
                        st.takeItems(TEAM_CERTIFICATE, -1);
                        st.takeItems(CLASS_FREE_CERTIFICATE, -1);
                        st.takeItems(CLASS_CERTIFICATE, -1);
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(this);
                        return "olympiad_operator_q0552_07.htm";
                    }
                    return "olympiad_operator_q0552_05.htm";
                }
                break;
        }

        return null;
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equalsIgnoreCase("olympiad_operator_q0552_03.htm")) {
            st.setCond(1);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("olympiad_operator_q0552_07.htm")) {
            int count = 0;
            if (st.ownItemCount(TEAM_CERTIFICATE) > 0) {
                count++;
            }
            if (st.ownItemCount(CLASS_FREE_CERTIFICATE) > 0) {
                count++;
            }
            if (st.ownItemCount(CLASS_CERTIFICATE) > 0) {
                count++;
            }
            if (count > 0) {
                st.giveItems(OLYMPIAD_CHEST, count);
                st.takeItems(TEAM_CERTIFICATE, -1);
                st.takeItems(CLASS_FREE_CERTIFICATE, -1);
                st.takeItems(CLASS_CERTIFICATE, -1);
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(this);
            }
        }
        return event;
    }

    @Override
    public void onOlympiadEnd(OlympiadGame og, QuestState qs) {
        if (qs.getCond() != 1) {
            return;
        }

        switch (og.getType()) {
            case TEAM:
                int count1 = qs.getInt("count1") + 1;
                qs.setMemoState("count1", count1);
                if (count1 == 5) {
                    qs.giveItems(TEAM_CERTIFICATE, 1);
                    if (qs.getInt("count2") >= 5 && qs.getInt("count3") >= 5) {
                        qs.setCond(2);
                        qs.soundEffect(SOUND_MIDDLE);
                    } else {
                        qs.soundEffect(SOUND_ITEMGET);
                    }
                }
                break;
            case CLASSED:
                int count2 = qs.getInt("count2") + 1;
                qs.setMemoState("count2", count2);
                if (count2 == 5) {
                    qs.giveItems(CLASS_CERTIFICATE, 1);
                    if (qs.getInt("count1") >= 5 && qs.getInt("count3") >= 5) {
                        qs.setCond(2);
                        qs.soundEffect(SOUND_MIDDLE);
                    } else {
                        qs.soundEffect(SOUND_ITEMGET);
                    }
                }
                break;
            case NON_CLASSED:
                int count3 = qs.getInt("count3") + 1;
                qs.setMemoState("count3", count3);
                if (count3 == 5) {
                    qs.giveItems(CLASS_FREE_CERTIFICATE, 1);
                    if (qs.getInt("count1") >= 5 && qs.getInt("count2") >= 5) {
                        qs.setCond(2);
                        qs.soundEffect(SOUND_MIDDLE);
                    } else {
                        qs.soundEffect(SOUND_ITEMGET);
                    }
                }
                break;
        }
    }


}
