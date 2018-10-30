package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.0
 * @date 27/01/2015
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _326_VanquishRemnants extends Quest {
    // npc
    private static final int leopold = 30435;
    // mobs
    private static final int ol_mahum_patrol = 20053;
    private static final int ol_mahum_guard = 20058;
    private static final int ol_mahum_remnants = 20061;
    private static final int ol_mahum_shooter = 20063;
    private static final int ol_mahum_captain = 20066;
    private static final int ol_mahum_supplier = 20436;
    private static final int ol_mahum_recruit = 20437;
    private static final int ol_mahum_general = 20438;
    private static final int ol_mahum_officer = 20439;
    // questitem
    private static final int red_cross_badge = 1359;
    private static final int blue_cross_badge = 1360;
    private static final int black_cross_badge = 1361;
    private static final int black_lion_mark = 1369;

    public _326_VanquishRemnants() {
        super(false);
        addStartNpc(leopold);
        addKillId(ol_mahum_patrol, ol_mahum_guard, ol_mahum_remnants, ol_mahum_shooter, ol_mahum_captain, ol_mahum_supplier, ol_mahum_recruit, ol_mahum_general, ol_mahum_officer);
        addQuestItem(red_cross_badge, blue_cross_badge, black_cross_badge);
        addLevelCheck(21, 30);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == leopold) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("vanquish_remnants", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "leopold_q0326_03.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=326&reply=1")) {
                st.removeMemo("vanquish_remnants");
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                htmltext = "leopold_q0326_07.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=326&reply=2"))
                htmltext = "leopold_q0326_08.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("vanquish_remnants");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == leopold) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "leopold_q0326_01.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "leopold_q0326_02.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == leopold) {
                    if (GetMemoState == 1) {
                        if (st.ownItemCount(red_cross_badge) + st.ownItemCount(blue_cross_badge) + st.ownItemCount(black_cross_badge) == 0)
                            htmltext = "leopold_q0326_04.htm";
                        else if (st.ownItemCount(red_cross_badge) + st.ownItemCount(blue_cross_badge) + st.ownItemCount(black_cross_badge) < 100 && st.ownItemCount(red_cross_badge) + st.ownItemCount(blue_cross_badge) + st.ownItemCount(black_cross_badge) > 0) {
                            if (st.ownItemCount(red_cross_badge) + st.ownItemCount(blue_cross_badge) + st.ownItemCount(black_cross_badge) >= 10)
                                st.giveItems(ADENA_ID, 4320 + 46 * st.ownItemCount(red_cross_badge) + 52 * st.ownItemCount(blue_cross_badge) + 58 * st.ownItemCount(black_cross_badge));
                            else
                                st.giveItems(ADENA_ID, 46 * st.ownItemCount(red_cross_badge) + 52 * st.ownItemCount(blue_cross_badge) + 58 * st.ownItemCount(black_cross_badge));
                            st.takeItems(red_cross_badge, -1);
                            st.takeItems(blue_cross_badge, -1);
                            st.takeItems(black_cross_badge, -1);
                            htmltext = "leopold_q0326_05.htm";
                        } else if (st.ownItemCount(red_cross_badge) + st.ownItemCount(blue_cross_badge) + st.ownItemCount(black_cross_badge) >= 100 && st.ownItemCount(black_lion_mark) == 0) {
                            st.giveItems(black_lion_mark, 1);
                            st.giveItems(ADENA_ID, 4320 + 46 * st.ownItemCount(red_cross_badge) + 52 * st.ownItemCount(blue_cross_badge) + 58 * st.ownItemCount(black_cross_badge));
                            st.takeItems(red_cross_badge, -1);
                            st.takeItems(blue_cross_badge, -1);
                            st.takeItems(black_cross_badge, -1);
                            htmltext = "leopold_q0326_06.htm";
                        } else if (st.ownItemCount(red_cross_badge) + st.ownItemCount(blue_cross_badge) + st.ownItemCount(black_cross_badge) >= 100 && st.ownItemCount(black_lion_mark) > 0) {
                            st.giveItems(ADENA_ID, 4320 + 46 * st.ownItemCount(red_cross_badge) + 52 * st.ownItemCount(blue_cross_badge) + 58 * st.ownItemCount(black_cross_badge));
                            st.takeItems(red_cross_badge, -1);
                            st.takeItems(blue_cross_badge, -1);
                            st.takeItems(black_cross_badge, -1);
                            htmltext = "leopold_q0326_09.htm";
                        }
                    }
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("vanquish_remnants");
        int npcId = npc.getNpcId();
        if (npcId == ol_mahum_patrol || npcId == ol_mahum_guard) {
            if (GetMemoState == 1) {
                if (Rnd.get(100) < 61) {
                    st.giveItems(red_cross_badge, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == ol_mahum_remnants) {
            if (GetMemoState == 1) {
                if (Rnd.get(100) < 57) {
                    st.giveItems(blue_cross_badge, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == ol_mahum_shooter) {
            if (GetMemoState == 1) {
                if (Rnd.get(100) < 63) {
                    st.giveItems(blue_cross_badge, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == ol_mahum_captain) {
            if (GetMemoState == 1) {
                if (Rnd.get(100) < 59) {
                    st.giveItems(black_cross_badge, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == ol_mahum_supplier) {
            if (GetMemoState == 1) {
                if (Rnd.get(100) < 55) {
                    st.giveItems(blue_cross_badge, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == ol_mahum_recruit) {
            if (GetMemoState == 1) {
                if (Rnd.get(100) < 59) {
                    st.giveItems(red_cross_badge, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == ol_mahum_general) {
            if (GetMemoState == 1) {
                if (Rnd.get(100) < 60) {
                    st.giveItems(black_cross_badge, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == ol_mahum_officer) {
            if (GetMemoState == 1) {
                if (Rnd.get(100) < 62) {
                    st.giveItems(blue_cross_badge, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        }
        return null;
    }
}