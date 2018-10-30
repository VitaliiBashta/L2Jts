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
 * @version 1.1
 * @date 08/03/2015
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _380_BringOutTheFlavorOfIngredients extends Quest {
    // npc
    private static final int rollant = 30069;
    // mobs
    private static final int dire_wolf = 20205;
    private static final int kadif_werewolf = 20206;
    private static final int mist_giant_leech = 20225;
    // questitem
    private static final int ritron = 5895;
    private static final int moon_face_flower = 5896;
    private static final int body_fluid_of_leech = 5897;
    // etcitem
    private static final int antidote = 1831;
    private static final int recipe_of_ritron = 5959;
    private static final int dessert_of_ritron = 5960;

    public _380_BringOutTheFlavorOfIngredients() {
        super(false);
        addStartNpc(rollant);
        addKillId(dire_wolf, kadif_werewolf, mist_giant_leech);
        addLevelCheck(24, 29);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("find_wonderful_taste");
        int npcId = npc.getNpcId();
        if (npcId == rollant) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("find_wonderful_taste", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "rollant_q0380_05.htm";
            } else if (event.equalsIgnoreCase("reply_1"))
                htmltext = "rollant_q0380_04.htm";
            else if (event.equalsIgnoreCase("reply_2") && GetMemoState == 6) {
                st.giveItems(recipe_of_ritron, 1);
                st.removeMemo("find_wonderful_taste");
                st.exitQuest(true);
                st.soundEffect(SOUND_FINISH);
                htmltext = "rollant_q0380_12.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("find_wonderful_taste");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == rollant) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "rollant_q0380_01.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "rollant_q0380_02.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == rollant) {
                    if (GetMemoState == 1 && (st.ownItemCount(body_fluid_of_leech) < 10 || st.ownItemCount(moon_face_flower) < 20 || st.ownItemCount(ritron) < 4 || st.ownItemCount(antidote) < 2))
                        htmltext = "rollant_q0380_06.htm";
                    else if (GetMemoState == 1 && st.ownItemCount(body_fluid_of_leech) >= 10 && st.ownItemCount(moon_face_flower) >= 20 && st.ownItemCount(ritron) >= 4 && st.ownItemCount(antidote) >= 2) {
                        st.takeItems(ritron, -1);
                        st.takeItems(moon_face_flower, -1);
                        st.takeItems(body_fluid_of_leech, -1);
                        st.takeItems(antidote, 2);
                        st.setCond(3);
                        st.setMemoState("find_wonderful_taste", String.valueOf(2), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "rollant_q0380_07.htm";
                    } else if (GetMemoState == 2) {
                        st.setCond(4);
                        st.setMemoState("find_wonderful_taste", String.valueOf(3), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "rollant_q0380_08.htm";
                    } else if (GetMemoState == 3) {
                        st.setCond(5);
                        st.setMemoState("find_wonderful_taste", String.valueOf(4), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "rollant_q0380_09.htm";
                    } else if (GetMemoState == 4) {
                        st.setCond(6);
                        st.setMemoState("find_wonderful_taste", String.valueOf(5), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "rollant_q0380_10.htm";
                    } else if (GetMemoState == 5) {
                        if (Rnd.get(100) < 56) {
                            st.giveItems(dessert_of_ritron, 1);
                            st.setMemoState("find_wonderful_taste", String.valueOf(6), true);
                            htmltext = "rollant_q0380_11.htm";
                        } else {
                            st.giveItems(dessert_of_ritron, 1);
                            st.removeMemo("find_wonderful_taste");
                            st.exitQuest(true);
                            st.soundEffect(SOUND_FINISH);
                            htmltext = "rollant_q0380_14.htm";
                        }
                    } else if (GetMemoState == 6) {
                        st.giveItems(recipe_of_ritron, 1);
                        st.removeMemo("find_wonderful_taste");
                        st.exitQuest(true);
                        st.soundEffect(SOUND_FINISH);
                        htmltext = "rollant_q0380_13.htm";
                    }
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == dire_wolf) {
            if (Rnd.get(10) == 0 && st.ownItemCount(ritron) < 4) {
                st.giveItems(ritron, 1);
                if (st.ownItemCount(moon_face_flower) >= 20 && st.ownItemCount(ritron) >= 4 && st.ownItemCount(body_fluid_of_leech) >= 10) {
                    st.setCond(2);
                    st.soundEffect(SOUND_MIDDLE);
                } else
                    st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == kadif_werewolf) {
            if (Rnd.get(10) < 5 && st.ownItemCount(moon_face_flower) < 20) {
                st.giveItems(moon_face_flower, 1);
                if (st.ownItemCount(moon_face_flower) >= 20 && st.ownItemCount(ritron) >= 4 && st.ownItemCount(body_fluid_of_leech) >= 10) {
                    st.setCond(2);
                    st.soundEffect(SOUND_MIDDLE);
                } else
                    st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == mist_giant_leech) {
            if (Rnd.get(10) < 5) {
                st.giveItems(body_fluid_of_leech, 1);
                if (st.ownItemCount(moon_face_flower) >= 20 && st.ownItemCount(ritron) >= 4 && st.ownItemCount(body_fluid_of_leech) >= 10) {
                    st.setCond(2);
                    st.soundEffect(SOUND_MIDDLE);
                } else
                    st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }
}