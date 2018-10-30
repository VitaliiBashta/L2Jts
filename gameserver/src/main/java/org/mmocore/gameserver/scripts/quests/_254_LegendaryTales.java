package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

/**
 * Based on official High Five
 *
 * @author Magister
 * @version 1.0
 * @date 14/02/2015
 * @tested OK
 */
public class _254_LegendaryTales extends Quest {
    // npc
    private static final int watcher_antaras_gilmore = 30754;
    // mobs
    private static final int blackdagger_wing = 25721;
    private static final int bleeding_fly = 25720;
    private static final int dust_rider = 25719;
    private static final int emerald_horn = 25718;
    private static final int muscle_bomber = 25724;
    private static final int shadow_summoner = 25722;
    private static final int spike_slasher = 25723;
    // questitem
    private static final int great_dragon_bone = 17249;
    // etcitem
    private static final int vesper_cutter = 13457;
    private static final int vesper_slasher = 13458;
    private static final int vesper_burster = 13459;
    private static final int vesper_shaper = 13460;
    private static final int vesper_fighter = 13461;
    private static final int vesper_stormer = 13462;
    private static final int vesper_avenger = 13463;
    private static final int vesper_retributer = 13464;
    private static final int vesper_caster = 13465;
    private static final int vesper_singer = 13466;
    private static final int vesper_thrower = 13467;
    // memo
    private static int boss_id0;
    private static int boss_id1;
    private static int boss_id2;
    private static int boss_id3;
    private static int boss_id4;
    private static int boss_id5;
    private static int boss_id6;

    public _254_LegendaryTales() {
        super(PARTY_ALL);
        addStartNpc(watcher_antaras_gilmore);
        addKillId(blackdagger_wing, bleeding_fly, dust_rider, emerald_horn, muscle_bomber, shadow_summoner, spike_slasher);
        addQuestItem(great_dragon_bone);
        addLevelCheck(80);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("the_legendary_heroes");
        int npcId = npc.getNpcId();
        if (npcId == watcher_antaras_gilmore) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("the_legendary_heroes", String.valueOf(1), true);
                st.setMemoState("the_legendary_heroes_ex", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "watcher_antaras_gilmore_q0254_07.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=254&reply=1")) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = "watcher_antaras_gilmore_q0254_03.htm";
                        st.exitQuest(true);
                        break;
                    default:
                        htmltext = "watcher_antaras_gilmore_q0254_04.htm";
                        break;
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=254&reply=2"))
                htmltext = "watcher_antaras_gilmore_q0254_05.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=254&reply=3"))
                htmltext = "watcher_antaras_gilmore_q0254_06.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=254&reply=4")) {
                if (GetMemoState == 1 && st.ownItemCount(great_dragon_bone) >= 7)
                    htmltext = "watcher_antaras_gilmore_q0254_11.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=254&reply=11")) {
                if (GetMemoState == 1 && st.ownItemCount(great_dragon_bone) < 7)
                    htmltext = "watcher_antaras_gilmore_q0254_12.htm";
                else if (GetMemoState == 1 && st.ownItemCount(great_dragon_bone) >= 7) {
                    st.giveItems(vesper_cutter, 1);
                    st.takeItems(great_dragon_bone, -1);
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "watcher_antaras_gilmore_q0254_13.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=254&reply=12")) {
                if (GetMemoState == 1 && st.ownItemCount(great_dragon_bone) < 7)
                    htmltext = "watcher_antaras_gilmore_q0254_12.htm";
                else if (GetMemoState == 1 && st.ownItemCount(great_dragon_bone) >= 7) {
                    st.giveItems(vesper_slasher, 1);
                    st.takeItems(great_dragon_bone, -1);
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "watcher_antaras_gilmore_q0254_13.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=254&reply=13")) {
                if (GetMemoState == 1 && st.ownItemCount(great_dragon_bone) < 7)
                    htmltext = "watcher_antaras_gilmore_q0254_12.htm";
                else if (GetMemoState == 1 && st.ownItemCount(great_dragon_bone) >= 7) {
                    st.giveItems(vesper_burster, 1);
                    st.takeItems(great_dragon_bone, -1);
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "watcher_antaras_gilmore_q0254_13.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=254&reply=14")) {
                if (GetMemoState == 1 && st.ownItemCount(great_dragon_bone) < 7)
                    htmltext = "watcher_antaras_gilmore_q0254_12.htm";
                else if (GetMemoState == 1 && st.ownItemCount(great_dragon_bone) >= 7) {
                    st.giveItems(vesper_shaper, 1);
                    st.takeItems(great_dragon_bone, -1);
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "watcher_antaras_gilmore_q0254_13.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=254&reply=15")) {
                if (GetMemoState == 1 && st.ownItemCount(great_dragon_bone) < 7)
                    htmltext = "watcher_antaras_gilmore_q0254_12.htm";
                else if (GetMemoState == 1 && st.ownItemCount(great_dragon_bone) >= 7) {
                    st.giveItems(vesper_fighter, 1);
                    st.takeItems(great_dragon_bone, -1);
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "watcher_antaras_gilmore_q0254_13.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=254&reply=16")) {
                if (GetMemoState == 1 && st.ownItemCount(great_dragon_bone) < 7)
                    htmltext = "watcher_antaras_gilmore_q0254_12.htm";
                else if (GetMemoState == 1 && st.ownItemCount(great_dragon_bone) >= 7) {
                    st.giveItems(vesper_stormer, 1);
                    st.takeItems(great_dragon_bone, -1);
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "watcher_antaras_gilmore_q0254_13.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=254&reply=17")) {
                if (GetMemoState == 1 && st.ownItemCount(great_dragon_bone) < 7)
                    htmltext = "watcher_antaras_gilmore_q0254_12.htm";
                else if (GetMemoState == 1 && st.ownItemCount(great_dragon_bone) >= 7) {
                    st.giveItems(vesper_avenger, 1);
                    st.takeItems(great_dragon_bone, -1);
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "watcher_antaras_gilmore_q0254_13.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=254&reply=18")) {
                if (GetMemoState == 1 && st.ownItemCount(great_dragon_bone) < 7)
                    htmltext = "watcher_antaras_gilmore_q0254_12.htm";
                else if (GetMemoState == 1 && st.ownItemCount(great_dragon_bone) >= 7) {
                    st.giveItems(vesper_retributer, 1);
                    st.takeItems(great_dragon_bone, -1);
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "watcher_antaras_gilmore_q0254_13.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=254&reply=19")) {
                if (GetMemoState == 1 && st.ownItemCount(great_dragon_bone) < 7)
                    htmltext = "watcher_antaras_gilmore_q0254_12.htm";
                else if (GetMemoState == 1 && st.ownItemCount(great_dragon_bone) >= 7) {
                    st.giveItems(vesper_caster, 1);
                    st.takeItems(great_dragon_bone, -1);
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "watcher_antaras_gilmore_q0254_13.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=254&reply=20")) {
                if (GetMemoState == 1 && st.ownItemCount(great_dragon_bone) < 7)
                    htmltext = "watcher_antaras_gilmore_q0254_12.htm";
                else if (GetMemoState == 1 && st.ownItemCount(great_dragon_bone) >= 7) {
                    st.giveItems(vesper_singer, 1);
                    st.takeItems(great_dragon_bone, -1);
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "watcher_antaras_gilmore_q0254_13.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=254&reply=21")) {
                if (GetMemoState == 1 && st.ownItemCount(great_dragon_bone) < 7)
                    htmltext = "watcher_antaras_gilmore_q0254_12.htm";
                else if (GetMemoState == 1 && st.ownItemCount(great_dragon_bone) >= 7) {
                    st.giveItems(vesper_thrower, 1);
                    st.takeItems(great_dragon_bone, -1);
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "watcher_antaras_gilmore_q0254_13.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=254&reply=30")) {
                if (GetMemoState == 1 && st.ownItemCount(great_dragon_bone) >= 1 && st.ownItemCount(great_dragon_bone) < 7)
                    htmltext = "watcher_antaras_gilmore_q0254_14.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=254&reply=31")) {
                if (GetMemoState == 1 && st.ownItemCount(great_dragon_bone) >= 1 && st.ownItemCount(great_dragon_bone) < 7)
                    htmltext = "watcher_antaras_gilmore_q0254_15.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=254&reply=32")) {
                if (GetMemoState == 1 && st.ownItemCount(great_dragon_bone) >= 1 && st.ownItemCount(great_dragon_bone) < 7) {
                    if (emerald_horn == boss_id3)
                        htmltext = "watcher_antaras_gilmore_q0254_22.htm";
                    else {
                        int i1 = Rnd.get(4);
                        if (i1 == 0)
                            htmltext = "watcher_antaras_gilmore_q0254_16.htm";
                        else if (i1 == 1)
                            htmltext = "watcher_antaras_gilmore_q0254_17.htm";
                        else if (i1 == 2)
                            htmltext = "watcher_antaras_gilmore_q0254_18.htm";
                        else
                            htmltext = "watcher_antaras_gilmore_q0254_19.htm";
                    }
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=254&reply=33")) {
                if (GetMemoState == 1 && st.ownItemCount(great_dragon_bone) >= 1 && st.ownItemCount(great_dragon_bone) < 7) {
                    if (dust_rider == boss_id2)
                        htmltext = "watcher_antaras_gilmore_q0254_23.htm";
                    else {
                        int i1 = Rnd.get(4);
                        if (i1 == 0)
                            htmltext = "watcher_antaras_gilmore_q0254_16.htm";
                        else if (i1 == 1)
                            htmltext = "watcher_antaras_gilmore_q0254_17.htm";
                        else if (i1 == 2)
                            htmltext = "watcher_antaras_gilmore_q0254_18.htm";
                        else
                            htmltext = "watcher_antaras_gilmore_q0254_19.htm";
                    }
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=254&reply=34")) {
                if (GetMemoState == 1 && st.ownItemCount(great_dragon_bone) >= 1 && st.ownItemCount(great_dragon_bone) < 7) {
                    if (bleeding_fly == boss_id1)
                        htmltext = "watcher_antaras_gilmore_q0254_24.htm";
                    else {
                        int i1 = Rnd.get(4);
                        if (i1 == 0)
                            htmltext = "watcher_antaras_gilmore_q0254_16.htm";
                        else if (i1 == 1)
                            htmltext = "watcher_antaras_gilmore_q0254_17.htm";
                        else if (i1 == 2)
                            htmltext = "watcher_antaras_gilmore_q0254_18.htm";
                        else
                            htmltext = "watcher_antaras_gilmore_q0254_19.htm";
                    }
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=254&reply=35")) {
                if (GetMemoState == 1 && st.ownItemCount(great_dragon_bone) >= 1 && st.ownItemCount(great_dragon_bone) < 7) {
                    if (blackdagger_wing == boss_id0)
                        htmltext = "watcher_antaras_gilmore_q0254_25.htm";
                    else {
                        int i1 = Rnd.get(4);
                        if (i1 == 0)
                            htmltext = "watcher_antaras_gilmore_q0254_16.htm";
                        else if (i1 == 1)
                            htmltext = "watcher_antaras_gilmore_q0254_17.htm";
                        else if (i1 == 2)
                            htmltext = "watcher_antaras_gilmore_q0254_18.htm";
                        else
                            htmltext = "watcher_antaras_gilmore_q0254_19.htm";
                    }
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=254&reply=36")) {
                if (GetMemoState == 1 && st.ownItemCount(great_dragon_bone) >= 1 && st.ownItemCount(great_dragon_bone) < 7) {
                    if (shadow_summoner == boss_id5)
                        htmltext = "watcher_antaras_gilmore_q0254_26.htm";
                    else {
                        int i1 = Rnd.get(4);
                        if (i1 == 0)
                            htmltext = "watcher_antaras_gilmore_q0254_16.htm";
                        else if (i1 == 1)
                            htmltext = "watcher_antaras_gilmore_q0254_17.htm";
                        else if (i1 == 2)
                            htmltext = "watcher_antaras_gilmore_q0254_18.htm";
                        else
                            htmltext = "watcher_antaras_gilmore_q0254_19.htm";
                    }
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=254&reply=37")) {
                if (GetMemoState == 1 && st.ownItemCount(great_dragon_bone) >= 1 && st.ownItemCount(great_dragon_bone) < 7) {
                    if (spike_slasher == boss_id6)
                        htmltext = "watcher_antaras_gilmore_q0254_27.htm";
                    else {
                        int i1 = Rnd.get(4);
                        if (i1 == 0)
                            htmltext = "watcher_antaras_gilmore_q0254_16.htm";
                        else if (i1 == 1)
                            htmltext = "watcher_antaras_gilmore_q0254_17.htm";
                        else if (i1 == 2)
                            htmltext = "watcher_antaras_gilmore_q0254_18.htm";
                        else
                            htmltext = "watcher_antaras_gilmore_q0254_19.htm";
                    }
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=254&reply=38")) {
                if (GetMemoState == 1 && st.ownItemCount(great_dragon_bone) >= 1 && st.ownItemCount(great_dragon_bone) < 7) {
                    if (muscle_bomber == boss_id4)
                        htmltext = "watcher_antaras_gilmore_q0254_28.htm";
                    else {
                        int i1 = Rnd.get(4);
                        if (i1 == 0)
                            htmltext = "watcher_antaras_gilmore_q0254_16.htm";
                        else if (i1 == 1)
                            htmltext = "watcher_antaras_gilmore_q0254_17.htm";
                        else if (i1 == 2)
                            htmltext = "watcher_antaras_gilmore_q0254_18.htm";
                        else
                            htmltext = "watcher_antaras_gilmore_q0254_19.htm";
                    }
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=254&reply=39"))
                htmltext = "watcher_antaras_gilmore_q0254_20.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=254&reply=40")) {
                if (GetMemoState == 1 && st.ownItemCount(great_dragon_bone) >= 1 && st.ownItemCount(great_dragon_bone) < 7)
                    htmltext = "watcher_antaras_gilmore_q0254_21.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("the_legendary_heroes");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == watcher_antaras_gilmore)
                    htmltext = "watcher_antaras_gilmore_q0254_01.htm";
                break;
            case STARTED:
                if (npcId == watcher_antaras_gilmore) {
                    if (GetMemoState == 1 && st.ownItemCount(great_dragon_bone) == 0)
                        htmltext = "watcher_antaras_gilmore_q0254_08.htm";
                    else if (GetMemoState == 1 && st.ownItemCount(great_dragon_bone) >= 1 && st.ownItemCount(great_dragon_bone) < 7)
                        htmltext = "watcher_antaras_gilmore_q0254_09.htm";
                    else if (GetMemoState == 1 && st.ownItemCount(great_dragon_bone) >= 7)
                        htmltext = "watcher_antaras_gilmore_q0254_10.htm";
                }
                break;
            case COMPLETED:
                if (npcId == watcher_antaras_gilmore)
                    htmltext = "watcher_antaras_gilmore_q0254_02.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("the_legendary_heroes");
        int GetMemoStateEx = st.getInt("the_legendary_heroes_ex");
        int npcId = npc.getNpcId();
        if (npcId == blackdagger_wing) {
            if (GetMemoState == 1) {
                if (blackdagger_wing == boss_id0) {
                    return null;
                } else if (GetMemoStateEx * 7 == 510510) {
                    st.setCond(2);
                    st.giveItems(great_dragon_bone, 1);
                    boss_id0 = npc.getNpcId();
                    st.setMemoState("the_legendary_heroes_ex", String.valueOf(GetMemoStateEx * 7), true);
                    st.soundEffect(SOUND_MIDDLE);
                } else {
                    st.giveItems(great_dragon_bone, 1);
                    boss_id0 = npc.getNpcId();
                    st.setMemoState("the_legendary_heroes_ex", String.valueOf(GetMemoStateEx * 7), true);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == bleeding_fly) {
            if (GetMemoState == 1) {
                if (bleeding_fly == boss_id1) {
                    return null;
                } else if (GetMemoStateEx * 5 == 510510) {
                    st.setCond(2);
                    st.giveItems(great_dragon_bone, 1);
                    boss_id1 = npc.getNpcId();
                    st.setMemoState("the_legendary_heroes_ex", String.valueOf(GetMemoStateEx * 5), true);
                    st.soundEffect(SOUND_MIDDLE);
                } else {
                    st.giveItems(great_dragon_bone, 1);
                    boss_id1 = npc.getNpcId();
                    st.setMemoState("the_legendary_heroes_ex", String.valueOf(GetMemoStateEx * 5), true);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == dust_rider) {
            if (GetMemoState == 1) {
                if (dust_rider == boss_id2) {
                    return null;
                } else if (GetMemoStateEx * 3 == 510510) {
                    st.setCond(2);
                    st.giveItems(great_dragon_bone, 1);
                    boss_id2 = npc.getNpcId();
                    st.setMemoState("the_legendary_heroes_ex", String.valueOf(GetMemoStateEx * 3), true);
                    st.soundEffect(SOUND_MIDDLE);
                } else {
                    st.giveItems(great_dragon_bone, 1);
                    boss_id2 = npc.getNpcId();
                    st.setMemoState("the_legendary_heroes_ex", String.valueOf(GetMemoStateEx * 3), true);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == emerald_horn) {
            if (GetMemoState == 1) {
                if (emerald_horn == boss_id3) {
                    return null;
                } else if (GetMemoStateEx * 2 == 510510) {
                    st.setCond(2);
                    st.giveItems(great_dragon_bone, 1);
                    boss_id3 = npc.getNpcId();
                    st.setMemoState("the_legendary_heroes_ex", String.valueOf(GetMemoStateEx * 2), true);
                    st.soundEffect(SOUND_MIDDLE);
                } else {
                    st.giveItems(great_dragon_bone, 1);
                    boss_id3 = npc.getNpcId();
                    st.setMemoState("the_legendary_heroes_ex", String.valueOf(GetMemoStateEx * 2), true);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == muscle_bomber) {
            if (GetMemoState == 1) {
                if (muscle_bomber == boss_id4) {
                    return null;
                } else if (GetMemoStateEx * 17 == 510510) {
                    st.setCond(2);
                    st.giveItems(great_dragon_bone, 1);
                    boss_id4 = npc.getNpcId();
                    st.setMemoState("the_legendary_heroes_ex", String.valueOf(GetMemoStateEx * 17), true);
                    st.soundEffect(SOUND_MIDDLE);
                } else {
                    st.giveItems(great_dragon_bone, 1);
                    boss_id4 = npc.getNpcId();
                    st.setMemoState("the_legendary_heroes_ex", String.valueOf(GetMemoStateEx * 17), true);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == shadow_summoner) {
            if (GetMemoState == 1) {
                if (shadow_summoner == boss_id5) {
                    return null;
                } else if (GetMemoStateEx * 11 == 510510) {
                    st.setCond(2);
                    st.giveItems(great_dragon_bone, 1);
                    boss_id5 = npc.getNpcId();
                    st.setMemoState("the_legendary_heroes_ex", String.valueOf(GetMemoStateEx * 11), true);
                    st.soundEffect(SOUND_MIDDLE);
                } else {
                    st.giveItems(great_dragon_bone, 1);
                    boss_id5 = npc.getNpcId();
                    st.setMemoState("the_legendary_heroes_ex", String.valueOf(GetMemoStateEx * 11), true);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == spike_slasher) {
            if (GetMemoState == 1) {
                if (spike_slasher == boss_id6) {
                    return null;
                } else if (GetMemoStateEx * 13 == 510510) {
                    st.setCond(2);
                    st.giveItems(great_dragon_bone, 1);
                    st.setMemoState("the_legendary_heroes_ex", String.valueOf(GetMemoStateEx * 13), true);
                    boss_id6 = npc.getNpcId();
                    st.soundEffect(SOUND_MIDDLE);
                } else {
                    st.giveItems(great_dragon_bone, 1);
                    st.setMemoState("the_legendary_heroes_ex", String.valueOf(GetMemoStateEx * 13), true);
                    boss_id6 = npc.getNpcId();
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        }
        return null;
    }
}