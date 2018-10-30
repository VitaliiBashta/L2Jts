package org.mmocore.gameserver.scripts.quests;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.model.base.NpcRace;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.serverpackets.RadarControl;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.1
 * @date 02/02/2015
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _041_GuideMission extends Quest {
    // npc
    private static final int guide_human_cnacelot = 30598;
    private static final int guide_elf_roios = 30599;
    private static final int guide_delf_frankia = 30600;
    private static final int guide_dwarf_gullin = 30601;
    private static final int guide_orc_tanai = 30602;
    private static final int guide_krenisk = 32135;
    // questitem
    // etcitem
    private static final int soulshot_none_for_rookie = 5789;
    private static final int spiritshot_none_for_rookie = 5790;
    private static final int recovery_scroll_none = 8594;
    // Class id
    private static final int orc_mage = 0x31;

    public _041_GuideMission() {
        super(false);
        addFirstTalkId(guide_human_cnacelot, guide_elf_roios, guide_delf_frankia, guide_dwarf_gullin, guide_orc_tanai, guide_krenisk);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        return null;
    }

    @Override
    public String onFirstTalk(NpcInstance npc, Player player) {
        String htmltext = "";
        int npcId = npc.getNpcId();
        boolean isMage = (player.getPlayerClassComponent().getClassId().getRace() != PlayerRace.orc) && player.getPlayerClassComponent().getClassId().isMage();
        // [guide_mission] 41 The Adventurer's Challenge
        QuestState st = player.getQuestState(getId());
        if (st == null) {
            newQuestState(player, STARTED);
            st = player.getQuestState(getId());
        }
        int talker_level = st.getPlayer().getLevel();
        int talker_occupation = st.getPlayer().getPlayerClassComponent().getClassId().getId();
        int class_level = st.getPlayer().getPlayerClassComponent().getClassId().level();
        int GetNRMemoState = st.getInt("guide_mission");
        switch (npcId) {
            case guide_human_cnacelot:
            case guide_elf_roios:
            case guide_delf_frankia:
            case guide_dwarf_gullin:
            case guide_orc_tanai:
            case guide_krenisk:
                if (player.getQuestState(255).getInt("tutorial_quest_ex") < 5) {
                    if (!isMage) {
                        st.playTutorialVoice("tutorial_voice_026");
                        st.giveItems(soulshot_none_for_rookie, 200);
                        st.giveItems(recovery_scroll_none, 2);
                        player.getQuestState(255).setMemoState("tutorial_quest_ex", "5");
                        if (talker_level <= 1)
                            st.addExpAndSp(68, 50);
                        else
                            st.addExpAndSp(0, 50);
                    } else if (isMage) {
                        if (talker_occupation == orc_mage) {
                            st.playTutorialVoice("tutorial_voice_026");
                            st.giveItems(soulshot_none_for_rookie, 200);
                        } else {
                            st.playTutorialVoice("tutorial_voice_027");
                            st.giveItems(spiritshot_none_for_rookie, 100);
                        }
                        st.giveItems(recovery_scroll_none, 2);
                        player.getQuestState(255).setMemoState("tutorial_quest_ex", "5");
                        if (talker_level <= 1)
                            st.addExpAndSp(68, 50);
                        else
                            st.addExpAndSp(0, 50);
                    } else if (talker_level < 6) {
                        if (GetNRMemoState % 10 == 1) {
                            htmltext = "newbie_guide_q0041_02.htm";
                            if (talker_level >= 5) {
                                st.giveItems(ADENA_ID, 695);
                                st.addExpAndSp(3154, 127);
                            } else if (talker_level >= 4) {
                                st.giveItems(ADENA_ID, 1041);
                                st.addExpAndSp(4870, 195);
                            } else if (talker_level >= 3) {
                                st.giveItems(ADENA_ID, 1186);
                                st.addExpAndSp(5675, 227);
                            } else {
                                st.giveItems(ADENA_ID, 1240);
                                st.addExpAndSp(5970, 239);
                            }
                            st.setMemoState("guide_mission", String.valueOf(GetNRMemoState + 10), true);
                        } else {
                            if (npc.getTemplate().getRace() == NpcRace.Humans) {
                                htmltext = "newbie_guide_q0041_01a.htm";
                                ThreadPoolManager.getInstance().schedule(new RadarTask(-84436, 242793, -3729, st.getPlayer()), 200L);
                            } else if (npc.getTemplate().getRace() == NpcRace.Elves) {
                                htmltext = "newbie_guide_q0041_01b.htm";
                                ThreadPoolManager.getInstance().schedule(new RadarTask(42978, 49115, 2994, st.getPlayer()), 200L);
                            } else if (npc.getTemplate().getRace() == NpcRace.DarkElves) {
                                htmltext = "newbie_guide_q0041_01c.htm";
                                ThreadPoolManager.getInstance().schedule(new RadarTask(25790, 10844, -3727, st.getPlayer()), 200L);
                            } else if (npc.getTemplate().getRace() == NpcRace.Orcs) {
                                htmltext = "newbie_guide_q0041_01d.htm";
                                ThreadPoolManager.getInstance().schedule(new RadarTask(-47360, -113791, -237, st.getPlayer()), 200L);
                            } else if (npc.getTemplate().getRace() == NpcRace.Dwarves) {
                                htmltext = "newbie_guide_q0041_01e.htm";
                                ThreadPoolManager.getInstance().schedule(new RadarTask(112656, -174864, -611, st.getPlayer()), 200L);
                            } else if (npc.getTemplate().getRace() == NpcRace.Kamael) {
                                htmltext = "newbie_guide_q0041_01f.htm";
                                ThreadPoolManager.getInstance().schedule(new RadarTask(-119378, 49242, 22, st.getPlayer()), 200L);
                            }
                        }
                    } else if (talker_level < 10) {
                        if (GetNRMemoState % 1000 / 100 == 1 && GetNRMemoState % 10000 / 1000 == 1) {
                            if (npc.getTemplate().getRace() == NpcRace.Humans) {
                                if (!isMage) {
                                    htmltext = "newbie_guide_q0041_05a.htm";
                                    ThreadPoolManager.getInstance().schedule(new RadarTask(-71384, 258304, -3109, st.getPlayer()), 200L);
                                } else {
                                    htmltext = "newbie_guide_q0041_05b.htm";
                                    ThreadPoolManager.getInstance().schedule(new RadarTask(-91008, 248016, -3568, st.getPlayer()), 200L);
                                }
                            } else if (npc.getTemplate().getRace() == NpcRace.Elves) {
                                htmltext = "newbie_guide_q0041_05c.htm";
                                ThreadPoolManager.getInstance().schedule(new RadarTask(47595, 51569, -2996, st.getPlayer()), 200L);
                            } else if (npc.getTemplate().getRace() == NpcRace.DarkElves) {
                                if (!isMage) {
                                    htmltext = "newbie_guide_q0041_05d.htm";
                                    ThreadPoolManager.getInstance().schedule(new RadarTask(10580, 17574, -4554, st.getPlayer()), 200L);
                                } else {
                                    htmltext = "newbie_guide_q0041_05e.htm";
                                    ThreadPoolManager.getInstance().schedule(new RadarTask(10775, 14190, -4242, st.getPlayer()), 200L);
                                }
                            } else if (npc.getTemplate().getRace() == NpcRace.Orcs) {
                                htmltext = "newbie_guide_q0041_05f.htm";
                                ThreadPoolManager.getInstance().schedule(new RadarTask(-46808, -113184, -112, st.getPlayer()), 200L);
                            } else if (npc.getTemplate().getRace() == NpcRace.Dwarves) {
                                htmltext = "newbie_guide_q0041_05g.htm";
                                ThreadPoolManager.getInstance().schedule(new RadarTask(115717, -183488, -1483, st.getPlayer()), 200L);
                            } else if (npc.getTemplate().getRace() == NpcRace.Kamael) {
                                htmltext = "newbie_guide_q0041_05h.htm";
                                ThreadPoolManager.getInstance().schedule(new RadarTask(-118080, 42835, 720, st.getPlayer()), 200L);
                            }
                            if (talker_level >= 9) {
                                st.giveItems(ADENA_ID, 5563);
                                st.addExpAndSp(16851, 711);
                            } else if (talker_level >= 8) {
                                st.giveItems(ADENA_ID, 9290);
                                st.addExpAndSp(28806, 1207);
                            } else if (talker_level >= 7) {
                                st.giveItems(ADENA_ID, 11567);
                                st.addExpAndSp(36942, 1541);
                            } else {
                                st.giveItems(ADENA_ID, 12928);
                                st.addExpAndSp(42191, 1753);
                            }
                            st.setMemoState("guide_mission", String.valueOf(GetNRMemoState + 10000), true);
                        } else if (GetNRMemoState % 1000 / 100 == 1 && GetNRMemoState % 10000 / 1000 != 1) {
                            if (npc.getTemplate().getRace() == NpcRace.Humans) {
                                htmltext = "newbie_guide_q0041_04a.htm";
                                ThreadPoolManager.getInstance().schedule(new RadarTask(-82236, 241573, -3728, st.getPlayer()), 200L);
                            } else if (npc.getTemplate().getRace() == NpcRace.Elves) {
                                htmltext = "newbie_guide_q0041_04b.htm";
                                ThreadPoolManager.getInstance().schedule(new RadarTask(42812, 51138, -2996, st.getPlayer()), 200L);
                            } else if (npc.getTemplate().getRace() == NpcRace.DarkElves) {
                                htmltext = "newbie_guide_q0041_04c.htm";
                                ThreadPoolManager.getInstance().schedule(new RadarTask(7644, 18048, -4377, st.getPlayer()), 200L);
                            } else if (npc.getTemplate().getRace() == NpcRace.Orcs) {
                                htmltext = "newbie_guide_q0041_04d.htm";
                                ThreadPoolManager.getInstance().schedule(new RadarTask(-46802, -114011, -112, st.getPlayer()), 200L);
                            } else if (npc.getTemplate().getRace() == NpcRace.Dwarves) {
                                htmltext = "newbie_guide_q0041_04e.htm";
                                ThreadPoolManager.getInstance().schedule(new RadarTask(116103, -178407, -948, st.getPlayer()), 200L);
                            } else if (npc.getTemplate().getRace() == NpcRace.Kamael) {
                                htmltext = "newbie_guide_q0041_04f.htm";
                                ThreadPoolManager.getInstance().schedule(new RadarTask(-119378, 49242, 22, st.getPlayer()), 200L);
                            }
                        } else
                            htmltext = "newbie_guide_q0041_03.htm";
                    } else {
                        htmltext = "newbie_guide_q0041_06.htm";
                        st.exitQuest(false);
                        // myself::SetOneTimeQuestFlag( talker, @guide_mission, 1 );
                    }
                } else if (player.getQuestState(255).getInt("tutorial_quest_ex") >= 5) {
                    if (talker_level < 6) {
                        if (GetNRMemoState % 10 == 1) {
                            htmltext = "newbie_guide_q0041_08.htm";
                            if (talker_level >= 5) {
                                st.giveItems(ADENA_ID, 695);
                                st.addExpAndSp(3154, 127);
                            } else if (talker_level >= 4) {
                                st.giveItems(ADENA_ID, 1041);
                                st.addExpAndSp(4870, 195);
                            } else if (talker_level >= 3) {
                                st.giveItems(ADENA_ID, 1186);
                                st.addExpAndSp(5675, 227);
                            } else {
                                st.giveItems(ADENA_ID, 1240);
                                st.addExpAndSp(5970, 239);
                            }
                            st.setMemoState("guide_mission", String.valueOf(GetNRMemoState + 10), true);
                        } else {
                            if (npc.getTemplate().getRace() == NpcRace.Humans) {
                                htmltext = "newbie_guide_q0041_07a.htm";
                                ThreadPoolManager.getInstance().schedule(new RadarTask(-84436, 242793, -3729, st.getPlayer()), 200L);
                            } else if (npc.getTemplate().getRace() == NpcRace.Elves) {
                                htmltext = "newbie_guide_q0041_07b.htm";
                                ThreadPoolManager.getInstance().schedule(new RadarTask(42978, 49115, 2994, st.getPlayer()), 200L);
                            } else if (npc.getTemplate().getRace() == NpcRace.DarkElves) {
                                htmltext = "newbie_guide_q0041_07c.htm";
                                ThreadPoolManager.getInstance().schedule(new RadarTask(25790, 10844, -3727, st.getPlayer()), 200L);
                            } else if (npc.getTemplate().getRace() == NpcRace.Orcs) {
                                htmltext = "newbie_guide_q0041_07d.htm";
                                ThreadPoolManager.getInstance().schedule(new RadarTask(-47360, -113791, -237, st.getPlayer()), 200L);
                            } else if (npc.getTemplate().getRace() == NpcRace.Dwarves) {
                                htmltext = "newbie_guide_q0041_07e.htm";
                                ThreadPoolManager.getInstance().schedule(new RadarTask(112656, -174864, -611, st.getPlayer()), 200L);
                            } else if (npc.getTemplate().getRace() == NpcRace.Kamael) {
                                htmltext = "newbie_guide_q0041_07f.htm";
                                ThreadPoolManager.getInstance().schedule(new RadarTask(-119378, 49242, 22, st.getPlayer()), 200L);
                            }
                        }
                    } else if (talker_level < 10) {
                        if (GetNRMemoState % 100000 / 10000 == 1)
                            htmltext = "newbie_guide_q0041_09g.htm";
                        else if (GetNRMemoState % 1000 / 100 == 1 && GetNRMemoState % 10000 / 1000 == 1 && GetNRMemoState % 100000 / 10000 != 1) {
                            if (npc.getTemplate().getRace() == NpcRace.Humans) {
                                if (!isMage) {
                                    htmltext = "newbie_guide_q0041_10a.htm";
                                    ThreadPoolManager.getInstance().schedule(new RadarTask(-71384, 258304, -3109, st.getPlayer()), 200L);
                                } else {
                                    htmltext = "newbie_guide_q0041_10b.htm";
                                    ThreadPoolManager.getInstance().schedule(new RadarTask(-91008, 248016, -3568, st.getPlayer()), 200L);
                                }
                            } else if (npc.getTemplate().getRace() == NpcRace.Elves) {
                                htmltext = "newbie_guide_q0041_10c.htm";
                                ThreadPoolManager.getInstance().schedule(new RadarTask(47595, 51569, -2996, st.getPlayer()), 200L);
                            } else if (npc.getTemplate().getRace() == NpcRace.DarkElves) {
                                if (!isMage) {
                                    htmltext = "newbie_guide_q0041_10d.htm";
                                    ThreadPoolManager.getInstance().schedule(new RadarTask(10580, 17574, -4554, st.getPlayer()), 200L);
                                } else {
                                    htmltext = "newbie_guide_q0041_10e.htm";
                                    ThreadPoolManager.getInstance().schedule(new RadarTask(10775, 14190, -4242, st.getPlayer()), 200L);
                                }
                            } else if (npc.getTemplate().getRace() == NpcRace.Orcs) {
                                htmltext = "newbie_guide_q0041_10f.htm";
                                ThreadPoolManager.getInstance().schedule(new RadarTask(-46808, -113184, -112, st.getPlayer()), 200L);
                            } else if (npc.getTemplate().getRace() == NpcRace.Dwarves) {
                                htmltext = "newbie_guide_q0041_10g.htm";
                                ThreadPoolManager.getInstance().schedule(new RadarTask(115717, -183488, -1483, st.getPlayer()), 200L);
                            } else if (npc.getTemplate().getRace() == NpcRace.Kamael) {
                                htmltext = "newbie_guide_q0041_10h.htm";
                                ThreadPoolManager.getInstance().schedule(new RadarTask(-118080, 42835, 720, st.getPlayer()), 200L);
                            }
                            if (talker_level >= 9) {
                                st.giveItems(ADENA_ID, 5563);
                                st.addExpAndSp(16851, 711);
                            } else if (talker_level >= 8) {
                                st.giveItems(ADENA_ID, 9290);
                                st.addExpAndSp(28806, 1207);
                            } else if (talker_level >= 7) {
                                st.giveItems(ADENA_ID, 11567);
                                st.addExpAndSp(36942, 1541);
                            } else {
                                st.giveItems(ADENA_ID, 12928);
                                st.addExpAndSp(42191, 1753);
                            }
                            st.setMemoState("guide_mission", String.valueOf(GetNRMemoState + 10000), true);
                        } else if (GetNRMemoState % 1000 / 100 == 1 && GetNRMemoState % 10000 / 1000 != 1) {
                            if (npc.getTemplate().getRace() == NpcRace.Humans) {
                                htmltext = "newbie_guide_q0041_09a.htm";
                                ThreadPoolManager.getInstance().schedule(new RadarTask(-82236, 241573, -3728, st.getPlayer()), 200L);
                            } else if (npc.getTemplate().getRace() == NpcRace.Elves) {
                                htmltext = "newbie_guide_q0041_09b.htm";
                                ThreadPoolManager.getInstance().schedule(new RadarTask(42812, 51138, -2996, st.getPlayer()), 200L);
                            } else if (npc.getTemplate().getRace() == NpcRace.DarkElves) {
                                htmltext = "newbie_guide_q0041_09c.htm";
                                ThreadPoolManager.getInstance().schedule(new RadarTask(7644, 18048, -4377, st.getPlayer()), 200L);
                            } else if (npc.getTemplate().getRace() == NpcRace.Orcs) {
                                htmltext = "newbie_guide_q0041_09d.htm";
                                ThreadPoolManager.getInstance().schedule(new RadarTask(-46802, -114011, -112, st.getPlayer()), 200L);
                            } else if (npc.getTemplate().getRace() == NpcRace.Dwarves) {
                                htmltext = "newbie_guide_q0041_09e.htm";
                                ThreadPoolManager.getInstance().schedule(new RadarTask(116103, -178407, -948, st.getPlayer()), 200L);
                            } else if (npc.getTemplate().getRace() == NpcRace.Kamael) {
                                htmltext = "newbie_guide_q0041_09f.htm";
                                ThreadPoolManager.getInstance().schedule(new RadarTask(-119378, 49242, 22, st.getPlayer()), 200L);
                            }
                        } else
                            htmltext = "newbie_guide_q0041_08.htm";
                    } else if (talker_level < 15) {
                        if (GetNRMemoState % 1000000 / 100000 == 1 && GetNRMemoState % 10000000 / 1000000 == 1)
                            htmltext = "newbie_guide_q0041_15.htm";
                        else if (GetNRMemoState % 1000000 / 100000 == 1 && GetNRMemoState % 10000000 / 1000000 != 1) {
                            if (npc.getTemplate().getRace() == NpcRace.Humans) {
                                htmltext = "newbie_guide_q0041_11a.htm";
                                ThreadPoolManager.getInstance().schedule(new RadarTask(-84057, 242832, -3729, st.getPlayer()), 200L);
                            } else if (npc.getTemplate().getRace() == NpcRace.Elves) {
                                htmltext = "newbie_guide_q0041_11b.htm";
                                ThreadPoolManager.getInstance().schedule(new RadarTask(45859, 50827, -3058, st.getPlayer()), 200L);
                            } else if (npc.getTemplate().getRace() == NpcRace.DarkElves) {
                                htmltext = "newbie_guide_q0041_11c.htm";
                                ThreadPoolManager.getInstance().schedule(new RadarTask(11258, 14431, -4242, st.getPlayer()), 200L);
                            } else if (npc.getTemplate().getRace() == NpcRace.Orcs) {
                                htmltext = "newbie_guide_q0041_11d.htm";
                                ThreadPoolManager.getInstance().schedule(new RadarTask(-45863, -112621, -200, st.getPlayer()), 200L);
                            } else if (npc.getTemplate().getRace() == NpcRace.Dwarves) {
                                htmltext = "newbie_guide_q0041_11e.htm";
                                ThreadPoolManager.getInstance().schedule(new RadarTask(116268, -177524, -914, st.getPlayer()), 200L);
                            } else if (npc.getTemplate().getRace() == NpcRace.Kamael) {
                                htmltext = "newbie_guide_q0041_11f.htm";
                                ThreadPoolManager.getInstance().schedule(new RadarTask(-125872, 38208, 1251, st.getPlayer()), 200L);
                            }
                            if (talker_level >= 14) {
                                st.giveItems(ADENA_ID, 13002);
                                st.addExpAndSp(62876, 2891);
                            } else if (talker_level >= 13) {
                                st.giveItems(ADENA_ID, 23468);
                                st.addExpAndSp(113137, 5161);
                            } else if (talker_level >= 12) {
                                st.giveItems(ADENA_ID, 31752);
                                st.addExpAndSp(152653, 6914);
                            } else if (talker_level >= 11) {
                                st.giveItems(ADENA_ID, 38180);
                                st.addExpAndSp(183128, 8242);
                            } else {
                                st.giveItems(ADENA_ID, 43054);
                                st.addExpAndSp(206101, 9227);
                            }
                            st.setMemoState("guide_mission", String.valueOf(GetNRMemoState + 1000000), true);
                        } else if (GetNRMemoState % 1000000 / 100000 != 1) {
                            if (npc.getTemplate().getRace() == NpcRace.Humans) {
                                if (!isMage) {
                                    htmltext = "newbie_guide_q0041_10a.htm";
                                    ThreadPoolManager.getInstance().schedule(new RadarTask(-71384, 258304, -3109, st.getPlayer()), 200L);
                                } else {
                                    htmltext = "newbie_guide_q0041_10b.htm";
                                    ThreadPoolManager.getInstance().schedule(new RadarTask(-91008, 248016, -3568, st.getPlayer()), 200L);
                                }
                            } else if (npc.getTemplate().getRace() == NpcRace.Elves) {
                                htmltext = "newbie_guide_q0041_10c.htm";
                                ThreadPoolManager.getInstance().schedule(new RadarTask(47595, 51569, -2996, st.getPlayer()), 200L);
                            } else if (npc.getTemplate().getRace() == NpcRace.DarkElves) {
                                if (!isMage) {
                                    htmltext = "newbie_guide_q0041_10d.htm";
                                    ThreadPoolManager.getInstance().schedule(new RadarTask(10580, 17574, -4554, st.getPlayer()), 200L);
                                } else {
                                    htmltext = "newbie_guide_q0041_10e.htm";
                                    ThreadPoolManager.getInstance().schedule(new RadarTask(10775, 14190, -4242, st.getPlayer()), 200L);
                                }
                            } else if (npc.getTemplate().getRace() == NpcRace.Orcs) {
                                htmltext = "newbie_guide_q0041_10f.htm";
                                ThreadPoolManager.getInstance().schedule(new RadarTask(-46808, -113184, -112, st.getPlayer()), 200L);
                            } else if (npc.getTemplate().getRace() == NpcRace.Dwarves) {
                                htmltext = "newbie_guide_q0041_10g.htm";
                                ThreadPoolManager.getInstance().schedule(new RadarTask(115717, -183488, -1483, st.getPlayer()), 200L);
                            } else if (npc.getTemplate().getRace() == NpcRace.Kamael) {
                                htmltext = "newbie_guide_q0041_10h.htm";
                                ThreadPoolManager.getInstance().schedule(new RadarTask(-118080, 42835, 720, st.getPlayer()), 200L);
                            }
                        }
                    } else if (talker_level < 18) {
                        if (GetNRMemoState % 100000000 / 10000000 == 1 && GetNRMemoState % 1000000000 / 100000000 == 1) {
                            htmltext = "newbie_guide_q0041_13.htm";
                            st.exitQuest(false);
                            // myself::SetOneTimeQuestFlag( talker, @guide_mission, 1 );
                        } else if (GetNRMemoState % 100000000 / 10000000 == 1 && GetNRMemoState % 1000000000 / 100000000 != 1) {
                            if (talker_level >= 17) {
                                st.giveItems(ADENA_ID, 22996);
                                st.addExpAndSp(113712, 5518);
                            } else if (talker_level >= 16) {
                                st.giveItems(ADENA_ID, 10018);
                                st.addExpAndSp(208133, 42237);
                            } else {
                                st.giveItems(ADENA_ID, 13648);
                                st.addExpAndSp(285670, 58155);
                            }
                            htmltext = "newbie_guide_q0041_12.htm";
                            st.setMemoState("guide_mission", String.valueOf(GetNRMemoState + 100000000), true);
                            // myself::SetOneTimeQuestFlag( talker, @guide_mission, 1 );
                        } else if (GetNRMemoState % 100000000 / 10000000 != 1) {
                            if (npc.getTemplate().getRace() == NpcRace.Humans) {
                                htmltext = "newbie_guide_q0041_11a.htm";
                                ThreadPoolManager.getInstance().schedule(new RadarTask(-84057, 242832, -3729, st.getPlayer()), 200L);
                            } else if (npc.getTemplate().getRace() == NpcRace.Elves) {
                                htmltext = "newbie_guide_q0041_11b.htm";
                                ThreadPoolManager.getInstance().schedule(new RadarTask(45859, 50827, -3058, st.getPlayer()), 200L);
                            } else if (npc.getTemplate().getRace() == NpcRace.DarkElves) {
                                htmltext = "newbie_guide_q0041_11c.htm";
                                ThreadPoolManager.getInstance().schedule(new RadarTask(11258, 14431, -4242, st.getPlayer()), 200L);
                            } else if (npc.getTemplate().getRace() == NpcRace.Orcs) {
                                htmltext = "newbie_guide_q0041_11d.htm";
                                ThreadPoolManager.getInstance().schedule(new RadarTask(-45863, -112621, -200, st.getPlayer()), 200L);
                            } else if (npc.getTemplate().getRace() == NpcRace.Dwarves) {
                                htmltext = "newbie_guide_q0041_11e.htm";
                                ThreadPoolManager.getInstance().schedule(new RadarTask(116268, -177524, -914, st.getPlayer()), 200L);
                            } else if (npc.getTemplate().getRace() == NpcRace.Kamael) {
                                htmltext = "newbie_guide_q0041_11f.htm";
                                ThreadPoolManager.getInstance().schedule(new RadarTask(-125872, 38208, 1251, st.getPlayer()), 200L);
                            }
                        }
                    } else if (class_level == 1) {
                        htmltext = "newbie_guide_q0041_13.htm";
                        st.exitQuest(false);
                        // myself::SetOneTimeQuestFlag( talker, @guide_mission, 1 );
                    } else {
                        htmltext = "newbie_guide_q0041_14.htm";
                        st.exitQuest(false);
                        // myself::SetOneTimeQuestFlag( talker, @guide_mission, 1 );
                    }
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        return null;
    }

    @Override
    public boolean isVisible() {
        return false;
    }

    private class RadarTask extends RunnableImpl {
        private final int x, y, z;
        private final Player player;

        RadarTask(final int x, final int y, final int z, final Player player) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.player = player;
        }

        @Override
        public void runImpl() {
            player.sendPacket(new RadarControl(RadarControl.RadarState.SHOW_RADAR, RadarControl.RadarType.ARROW, x, y, z));
        }
    }
}