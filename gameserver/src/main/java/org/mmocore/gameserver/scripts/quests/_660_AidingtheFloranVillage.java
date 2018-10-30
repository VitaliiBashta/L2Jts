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
 * @date 25/01/2015
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _660_AidingtheFloranVillage extends Quest {
    // npc
    private static final int marya = 30608;
    private static final int alankell = 30291;
    // mobs
    private static final int delu_lizardman_shaman = 20781;
    private static final int plain_observer = 21102;
    private static final int oddly_stone_golem = 21103;
    private static final int delu_lizardman_q_master = 21104;
    private static final int delu_lizardman_agent = 21105;
    private static final int cursed_observer = 21106;
    private static final int delu_lizardman_commander = 21107;
    // questitem
    private static final int q_eye_of_observer = 8074;
    private static final int q_part_of_oddy_stone_golem = 8075;
    private static final int q_scale_of_delu_lizardman = 8076;
    // etcitem
    private static final int scrl_of_ench_am_d = 956;
    private static final int scrl_of_ench_wp_d = 955;

    public _660_AidingtheFloranVillage() {
        super(false);
        addStartNpc(marya);
        addTalkId(alankell);
        addKillId(delu_lizardman_shaman, plain_observer, oddly_stone_golem, delu_lizardman_q_master, delu_lizardman_agent, cursed_observer, delu_lizardman_commander);
        addQuestItem(q_eye_of_observer, q_part_of_oddy_stone_golem, q_scale_of_delu_lizardman);
        addLevelCheck(30);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == marya) {
            if (event.equalsIgnoreCase("quest_accept")) {
                if (st.getPlayer().getLevel() >= 30) {
                    st.setCond(1);
                    st.setMemoState("support_ploran_town", String.valueOf(1), true);
                    st.setState(STARTED);
                    st.soundEffect(SOUND_ACCEPT);
                    htmltext = "marya_q0660_06.htm";
                } else
                    htmltext = "marya_q0660_06a.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=660&reply=20"))
                htmltext = "marya_q0660_02.htm";
        } else if (npcId == alankell) {
            if (event.equalsIgnoreCase("menu_select?ask=660&reply=1")) {
                long i1 = st.ownItemCount(q_eye_of_observer);
                long i2 = st.ownItemCount(q_part_of_oddy_stone_golem);
                long i3 = st.ownItemCount(q_scale_of_delu_lizardman);
                long i0 = i1 + i2 + i3;
                if (i0 > 0) {
                    st.giveItems(ADENA_ID, i0 * 100);
                    st.takeItems(q_eye_of_observer, -1);
                    st.takeItems(q_part_of_oddy_stone_golem, -1);
                    st.takeItems(q_scale_of_delu_lizardman, -1);
                    htmltext = "alankell_q0660_06.htm";
                } else
                    htmltext = "alankell_q0660_08.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=660&reply=2"))
                htmltext = "alankell_q0660_09.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=660&reply=3")) {
                st.takeItems(q_eye_of_observer, -1);
                st.takeItems(q_part_of_oddy_stone_golem, -1);
                st.takeItems(q_scale_of_delu_lizardman, -1);
                st.removeMemo("support_ploran_town");
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                htmltext = "alankell_q0660_08a.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=660&reply=10")) {
                long i1 = st.ownItemCount(q_eye_of_observer);
                long i2 = st.ownItemCount(q_part_of_oddy_stone_golem);
                long i3 = st.ownItemCount(q_scale_of_delu_lizardman);
                long i0 = i1 + i2 + i3;
                if (i0 < 100)
                    htmltext = "alankell_q0660_11.htm";
                else {
                    long i4 = 100;
                    if (i1 < i4) {
                        st.takeItems(q_eye_of_observer, i1);
                        i4 = i4 - i1;
                    } else {
                        st.takeItems(q_eye_of_observer, i4);
                        i4 = 0;
                    }
                    if (i2 < i4) {
                        st.takeItems(q_part_of_oddy_stone_golem, i2);
                        i4 = i4 - i2;
                    } else {
                        st.takeItems(q_part_of_oddy_stone_golem, i4);
                        i4 = 0;
                    }
                    if (i3 < i4) {
                        st.takeItems(q_scale_of_delu_lizardman, i3);
                        i4 = i4 - i3;
                    } else {
                        st.takeItems(q_scale_of_delu_lizardman, i4);
                        i4 = 0;
                    }
                    if (Rnd.get(99) > 50) {
                        st.giveItems(scrl_of_ench_am_d, 1);
                        st.giveItems(ADENA_ID, 13000);
                        htmltext = "alankell_q0660_12.htm";
                    } else {
                        st.giveItems(ADENA_ID, 1000);
                        htmltext = "alankell_q0660_13.htm";
                    }
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=660&reply=14")) {
                long i1 = st.ownItemCount(q_eye_of_observer);
                long i2 = st.ownItemCount(q_part_of_oddy_stone_golem);
                long i3 = st.ownItemCount(q_scale_of_delu_lizardman);
                long i0 = i1 + i2 + i3;
                if (i0 < 200)
                    htmltext = "alankell_q0660_15.htm";
                else {
                    long i4 = 200;
                    if (i1 < i4) {
                        st.takeItems(q_eye_of_observer, i1);
                        i4 = i4 - i1;
                    } else {
                        st.takeItems(q_eye_of_observer, i4);
                        i4 = 0;
                    }
                    if (i2 < i4) {
                        st.takeItems(q_part_of_oddy_stone_golem, i2);
                        i4 = i4 - i2;
                    } else {
                        st.takeItems(q_part_of_oddy_stone_golem, i4);
                        i4 = 0;
                    }
                    if (i3 < i4) {
                        st.takeItems(q_scale_of_delu_lizardman, i3);
                        i4 = i4 - i3;
                    } else {
                        st.takeItems(q_scale_of_delu_lizardman, i4);
                        i4 = 0;
                    }
                    if (Rnd.get(100) >= 50) {
                        if (Rnd.get(2) == 0) {
                            st.giveItems(scrl_of_ench_am_d, 1);
                            st.giveItems(ADENA_ID, 20000);
                        } else
                            st.giveItems(scrl_of_ench_wp_d, 1);
                        htmltext = "alankell_q0660_16.htm";
                    } else {
                        st.giveItems(ADENA_ID, 2000);
                        htmltext = "alankell_q0660_17.htm";
                    }
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=660&reply=18")) {
                long i1 = st.ownItemCount(q_eye_of_observer);
                long i2 = st.ownItemCount(q_part_of_oddy_stone_golem);
                long i3 = st.ownItemCount(q_scale_of_delu_lizardman);
                long i0 = i1 + i2 + i3;
                if (i0 < 500)
                    htmltext = "alankell_q0660_19.htm";
                else {
                    long i4 = 500;
                    if (i1 < i4) {
                        st.takeItems(q_eye_of_observer, i1);
                        i4 = i4 - i1;
                    } else {
                        st.takeItems(q_eye_of_observer, i4);
                        i4 = 0;
                    }
                    if (i2 < i4) {
                        st.takeItems(q_part_of_oddy_stone_golem, i2);
                        i4 = i4 - i2;
                    } else {
                        st.takeItems(q_part_of_oddy_stone_golem, i4);
                        i4 = 0;
                    }
                    if (i3 < i4) {
                        st.takeItems(q_scale_of_delu_lizardman, i3);
                        i4 = i4 - i3;
                    } else {
                        st.takeItems(q_scale_of_delu_lizardman, i4);
                        i4 = 0;
                    }
                    if (Rnd.get(100) >= 50) {
                        st.giveItems(scrl_of_ench_wp_d, 1);
                        st.giveItems(ADENA_ID, 45000);
                        htmltext = "alankell_q0660_20.htm";
                    } else {
                        st.giveItems(ADENA_ID, 5000);
                        htmltext = "alankell_q0660_21.htm";
                    }
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=660&reply=20")) {
                long i1 = st.ownItemCount(q_eye_of_observer);
                long i2 = st.ownItemCount(q_part_of_oddy_stone_golem);
                long i3 = st.ownItemCount(q_scale_of_delu_lizardman);
                long i0 = i1 + i2 + i3;
                if (i0 <= 0)
                    htmltext = "alankell_q0660_23.htm";
                else {
                    long i4 = i0 * 100;
                    st.giveItems(ADENA_ID, i4);
                    htmltext = "alankell_q0660_22.htm";
                }
                st.takeItems(q_eye_of_observer, -1);
                st.takeItems(q_part_of_oddy_stone_golem, -1);
                st.takeItems(q_scale_of_delu_lizardman, -1);
                st.removeMemo("support_ploran_town");
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("support_ploran_town");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == marya) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "marya_q0660_04.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "marya_q0660_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == marya) {
                    if (GetMemoState == 1)
                        htmltext = "marya_q0660_05.htm";
                } else if (npcId == alankell) {
                    if (GetMemoState == 1) {
                        st.setCond(2);
                        st.setMemoState("support_ploran_town", String.valueOf(2), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "alankell_q0660_04.htm";
                    } else if (GetMemoState == 2)
                        htmltext = "alankell_q0660_05.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("support_ploran_town");
        int npcId = npc.getNpcId();
        if (npcId == delu_lizardman_shaman || npcId == delu_lizardman_q_master) {
            if (GetMemoState == 2) {
                if (Rnd.get(100) < 65) {
                    st.giveItems(q_scale_of_delu_lizardman, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == plain_observer) {
            if (Rnd.get(100) < 50) {
                st.giveItems(q_eye_of_observer, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == oddly_stone_golem) {
            if (Rnd.get(100) < 52) {
                st.giveItems(q_part_of_oddy_stone_golem, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == delu_lizardman_agent) {
            if (Rnd.get(100) < 75) {
                st.giveItems(q_scale_of_delu_lizardman, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == cursed_observer) {
            if (Rnd.get(100) < 63) {
                st.giveItems(q_eye_of_observer, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == delu_lizardman_commander) {
            if (Rnd.get(100) < 33) {
                st.giveItems(q_scale_of_delu_lizardman, 2);
                st.soundEffect(SOUND_ITEMGET);
            } else {
                st.giveItems(q_scale_of_delu_lizardman, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }
}