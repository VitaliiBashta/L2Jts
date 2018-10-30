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
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _628_HuntOfTheGoldenRamMercenaryForce extends Quest {
    // npc
    private final static int merc_kahmun = 31554;

    // mobs
    private final static int splinter_stakato = 21508;
    private final static int splinter_stakato_worker = 21509;
    private final static int splinter_stakato_soldier = 21510;
    private final static int splinter_stakato_drone = 21511;
    private final static int splinter_stakato_drone_a = 21512;
    private final static int needle_stakato = 21513;
    private final static int needle_stakato_worker = 21514;
    private final static int needle_stakato_soldier = 21515;
    private final static int needle_stakato_drone = 21516;
    private final static int needle_stakato_drone_a = 21517;

    // questitem
    private static int q_goldenram_badge1 = 7246;
    private static int q_goldenram_badge2 = 7247;
    private static int q_splinter_chitin = 7248;
    private static int q_needle_chitin = 7249;

    public _628_HuntOfTheGoldenRamMercenaryForce() {
        super(true);
        addStartNpc(merc_kahmun);
        addKillId(splinter_stakato, splinter_stakato_worker, splinter_stakato_soldier, splinter_stakato_drone, splinter_stakato_drone_a, needle_stakato, needle_stakato_worker, needle_stakato_soldier, needle_stakato_drone, needle_stakato_drone_a);
        addQuestItem(q_splinter_chitin, q_needle_chitin);
        addLevelCheck(66, 78);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;

        if (event.equalsIgnoreCase("quest_accept")) {
            if (st.ownItemCount(q_goldenram_badge1) < 1 && st.ownItemCount(q_goldenram_badge2) < 1) {
                st.setCond(1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "merc_kahmun_q0628_03.htm";
            } else if (st.ownItemCount(q_goldenram_badge1) >= 1 && st.ownItemCount(q_goldenram_badge2) < 1) {
                st.setCond(2);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "merc_kahmun_q0628_04.htm";
            } else if (st.ownItemCount(q_goldenram_badge2) >= 1) {
                st.setCond(3);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "merc_kahmun_q0628_05.htm";
            }
        } else if (event.equalsIgnoreCase("reply_1")) {
            if (st.ownItemCount(q_goldenram_badge1) < 1 && st.ownItemCount(q_goldenram_badge2) < 1 && st.ownItemCount(q_splinter_chitin) >= 100) {
                st.setCond(2);
                st.giveItems(q_goldenram_badge1, 1);
                st.takeItems(q_splinter_chitin, -1);
                st.getPlayer().updateRam();
                htmltext = "merc_kahmun_q0628_08.htm";
            }
        } else if (event.equalsIgnoreCase("reply_3")) {
            st.takeItems(q_goldenram_badge1, -1);
            st.takeItems(q_goldenram_badge2, -1);
            st.takeItems(q_splinter_chitin, -1);
            st.takeItems(q_needle_chitin, -1);
            st.getPlayer().updateRam();
            st.soundEffect(SOUND_FINISH);
            st.exitQuest(true);
            htmltext = "merc_kahmun_q0628_13.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == merc_kahmun) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "merc_kahmun_q0628_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "merc_kahmun_q0628_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == merc_kahmun)
                    if (st.ownItemCount(q_goldenram_badge1) < 1 && st.ownItemCount(q_goldenram_badge2) < 1 && st.ownItemCount(q_splinter_chitin) < 100)
                        htmltext = "merc_kahmun_q0628_06.htm";
                    else if (st.ownItemCount(q_goldenram_badge1) < 1 && st.ownItemCount(q_goldenram_badge2) < 1 && st.ownItemCount(q_splinter_chitin) >= 100)
                        htmltext = "merc_kahmun_q0628_07.htm";
                    else if (st.ownItemCount(q_goldenram_badge1) >= 1 && st.ownItemCount(q_goldenram_badge2) < 1 && (st.ownItemCount(q_splinter_chitin) < 100 || st.ownItemCount(q_needle_chitin) < 100))
                        htmltext = "merc_kahmun_q0628_09.htm";
                    else if (st.ownItemCount(q_goldenram_badge1) >= 1 && st.ownItemCount(q_goldenram_badge2) < 1 && st.ownItemCount(q_splinter_chitin) >= 100 && st.ownItemCount(q_needle_chitin) >= 100) {
                        st.setCond(3);
                        st.giveItems(q_goldenram_badge2, 1);
                        st.takeItems(q_goldenram_badge1, -1);
                        st.takeItems(q_splinter_chitin, -1);
                        st.takeItems(q_needle_chitin, -1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "merc_kahmun_q0628_10.htm";
                    } else if (st.ownItemCount(q_goldenram_badge2) >= 1)
                        htmltext = "merc_kahmun_q0628_11.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        if (st.getState() != STARTED)
            return null;

        int npcId = npc.getNpcId();

        if (npcId == splinter_stakato) {
            if (st.ownItemCount(q_goldenram_badge2) < 1 && st.ownItemCount(q_splinter_chitin) < 100) {
                int i0 = Rnd.get(100);
                if (i0 < 50)
                    if (st.ownItemCount(q_goldenram_badge1) >= 1) {
                        if (st.ownItemCount(q_splinter_chitin) >= 99) {
                            st.giveItems(q_splinter_chitin, 1);
                            st.soundEffect(SOUND_MIDDLE);
                        } else {
                            st.giveItems(q_splinter_chitin, 1);
                            st.soundEffect(SOUND_ITEMGET);
                        }
                    } else if (st.ownItemCount(q_splinter_chitin) >= 99) {
                        st.giveItems(q_splinter_chitin, 1);
                        st.soundEffect(SOUND_MIDDLE);
                    } else {
                        st.giveItems(q_splinter_chitin, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            }
        } else if (npcId == splinter_stakato_worker) {
            if (st.ownItemCount(q_goldenram_badge2) < 1 && st.ownItemCount(q_splinter_chitin) < 100) {
                int i0 = Rnd.get(100);
                if (i0 < 43)
                    if (st.ownItemCount(q_goldenram_badge1) >= 1) {
                        if (st.ownItemCount(q_splinter_chitin) >= 99) {
                            st.giveItems(q_splinter_chitin, 1);
                            st.soundEffect(SOUND_MIDDLE);
                        } else {
                            st.giveItems(q_splinter_chitin, 1);
                            st.soundEffect(SOUND_ITEMGET);
                        }
                    } else if (st.ownItemCount(q_splinter_chitin) >= 99) {
                        st.giveItems(q_splinter_chitin, 1);
                        st.soundEffect(SOUND_MIDDLE);
                    } else {
                        st.giveItems(q_splinter_chitin, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            }
        } else if (npcId == splinter_stakato_soldier) {
            if (st.ownItemCount(q_goldenram_badge2) < 1 && st.ownItemCount(q_splinter_chitin) < 100) {
                int i0 = Rnd.get(1000);
                if (i0 < 521)
                    if (st.ownItemCount(q_goldenram_badge1) >= 1) {
                        if (st.ownItemCount(q_splinter_chitin) >= 99) {
                            st.giveItems(q_splinter_chitin, 1);
                            st.soundEffect(SOUND_MIDDLE);
                        } else {
                            st.giveItems(q_splinter_chitin, 1);
                            st.soundEffect(SOUND_ITEMGET);
                        }
                    } else if (st.ownItemCount(q_splinter_chitin) >= 99) {
                        st.giveItems(q_splinter_chitin, 1);
                        st.soundEffect(SOUND_MIDDLE);
                    } else {
                        st.giveItems(q_splinter_chitin, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            }
        } else if (npcId == splinter_stakato_drone) {
            if (st.ownItemCount(q_goldenram_badge2) < 1 && st.ownItemCount(q_splinter_chitin) < 100) {
                int i0 = Rnd.get(1000);
                if (i0 < 575)
                    if (st.ownItemCount(q_goldenram_badge1) >= 1) {
                        if (st.ownItemCount(q_splinter_chitin) >= 99) {
                            st.giveItems(q_splinter_chitin, 1);
                            st.soundEffect(SOUND_MIDDLE);
                        } else {
                            st.giveItems(q_splinter_chitin, 1);
                            st.soundEffect(SOUND_ITEMGET);
                        }
                    } else if (st.ownItemCount(q_splinter_chitin) >= 99) {
                        st.giveItems(q_splinter_chitin, 1);
                        st.soundEffect(SOUND_MIDDLE);
                    } else {
                        st.giveItems(q_splinter_chitin, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            }
        } else if (npcId == splinter_stakato_drone_a) {
            if (st.ownItemCount(q_goldenram_badge2) < 1 && st.ownItemCount(q_splinter_chitin) < 100) {
                int i0 = Rnd.get(1000);
                if (i0 < 746)
                    if (st.ownItemCount(q_goldenram_badge1) >= 1) {
                        if (st.ownItemCount(q_splinter_chitin) >= 99) {
                            st.giveItems(q_splinter_chitin, 1);
                            st.soundEffect(SOUND_MIDDLE);
                        } else {
                            st.giveItems(q_splinter_chitin, 1);
                            st.soundEffect(SOUND_ITEMGET);
                        }
                    } else if (st.ownItemCount(q_splinter_chitin) >= 99) {
                        st.giveItems(q_splinter_chitin, 1);
                        st.soundEffect(SOUND_MIDDLE);
                    } else {
                        st.giveItems(q_splinter_chitin, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            }
        } else if (npcId == needle_stakato) {
            if (st.ownItemCount(q_goldenram_badge2) < 1 && st.ownItemCount(q_goldenram_badge1) >= 1 && st.ownItemCount(q_needle_chitin) < 100) {
                int i0 = Rnd.get(100);
                if (i0 < 50)
                    if (st.ownItemCount(q_needle_chitin) >= 99) {
                        st.giveItems(q_needle_chitin, 1);
                        st.soundEffect(SOUND_MIDDLE);
                    } else {
                        st.giveItems(q_needle_chitin, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            }
        } else if (npcId == needle_stakato_worker) {
            if (st.ownItemCount(q_goldenram_badge2) < 1 && st.ownItemCount(q_goldenram_badge1) >= 1 && st.ownItemCount(q_needle_chitin) < 100) {
                int i0 = Rnd.get(100);
                if (i0 < 43)
                    if (st.ownItemCount(q_needle_chitin) >= 99) {
                        st.giveItems(q_needle_chitin, 1);
                        st.soundEffect(SOUND_MIDDLE);
                    } else {
                        st.giveItems(q_needle_chitin, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            }
        } else if (npcId == needle_stakato_soldier) {
            if (st.ownItemCount(q_goldenram_badge2) < 1 && st.ownItemCount(q_goldenram_badge1) >= 1 && st.ownItemCount(q_needle_chitin) < 100) {
                int i0 = Rnd.get(100);
                if (i0 < 52)
                    if (st.ownItemCount(q_needle_chitin) >= 99) {
                        st.giveItems(q_needle_chitin, 1);
                        st.soundEffect(SOUND_MIDDLE);
                    } else {
                        st.giveItems(q_needle_chitin, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            }
        } else if (npcId == needle_stakato_drone) {
            if (st.ownItemCount(q_goldenram_badge2) < 1 && st.ownItemCount(q_goldenram_badge1) >= 1 && st.ownItemCount(q_needle_chitin) < 100) {
                int i0 = Rnd.get(1000);
                if (i0 < 531)
                    if (st.ownItemCount(q_needle_chitin) >= 99) {
                        st.giveItems(q_needle_chitin, 1);
                        st.soundEffect(SOUND_MIDDLE);
                    } else {
                        st.giveItems(q_needle_chitin, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            }
        } else if (npcId == needle_stakato_drone_a)
            if (st.ownItemCount(q_goldenram_badge2) < 1 && st.ownItemCount(q_goldenram_badge1) >= 1 && st.ownItemCount(q_needle_chitin) < 100) {
                int i0 = Rnd.get(1000);
                if (i0 < 744)
                    if (st.ownItemCount(q_needle_chitin) >= 99) {
                        st.giveItems(q_needle_chitin, 1);
                        st.soundEffect(SOUND_MIDDLE);
                    } else {
                        st.giveItems(q_needle_chitin, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            }
        return null;
    }
}