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
 * @date 17/09/2015
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _627_HeartInSearchOfPower extends Quest {
    // npc
    private final static int dark_necromancer = 31518;
    private final static int enfeux = 31519;
    // mobs
    private final static int brilliant_eye = 21520;
    private final static int brilliant_light = 21523;
    private final static int brilliant_blade = 21524;
    private final static int brilliant_blade_1 = 21525;
    private final static int brilliant_wisdom = 21526;
    private final static int brilliant_soul = 21529;
    private final static int brilliant_legacy = 21530;
    private final static int brilliant_vengeance = 21531;
    private final static int brilliant_cry = 21532;
    private final static int brilliant_mark = 21535;
    private final static int brilliant_crown = 21536;
    private final static int brilliant_anguish = 21539;
    private final static int brilliant_anguish_1 = 21540;
    private final static int brilliant_vengeance_1 = 21658;
    // questitem
    private final static int q_seal_of_light = 7170;
    private final static int q_gem_of_submission = 7171;
    private final static int q_gem_of_saints = 7172;
    // etcitem
    private final static int mold_hardener = 4041;
    private final static int enria = 4042;
    private final static int asofe = 4043;
    private final static int thons = 4044;

    public _627_HeartInSearchOfPower() {
        super(true);
        addStartNpc(dark_necromancer);
        addTalkId(enfeux);
        addQuestItem(q_gem_of_submission);
        addKillId(brilliant_eye, brilliant_light, brilliant_blade, brilliant_blade_1, brilliant_wisdom, brilliant_soul, brilliant_legacy, brilliant_vengeance, brilliant_cry, brilliant_mark, brilliant_crown, brilliant_anguish, brilliant_anguish_1, brilliant_vengeance_1);
        addLevelCheck(60, 71);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetHTMLCookie = st.getInt("temptation_of_power_cookie");
        int npcId = npc.getNpcId();
        if (npcId == dark_necromancer) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("temptation_of_power", String.valueOf(1 * 10 + 1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "dark_necromancer_q0627_0104.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=627&reply=1") && GetHTMLCookie == 2 - 1) {
                if (st.ownItemCount(q_gem_of_submission) >= 300) {
                    st.setCond(3);
                    st.setMemoState("temptation_of_power", String.valueOf(2 * 10 + 1), true);
                    st.takeItems(q_gem_of_submission, 300);
                    st.giveItems(q_seal_of_light, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "dark_necromancer_q0627_0201.htm";
                } else
                    htmltext = "dark_necromancer_q0627_0202.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=627&reply=3") && GetHTMLCookie == 4 - 1)
                htmltext = "dark_necromancer_q0627_0401.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=627&reply=11") && GetHTMLCookie == 4 - 1) {
                if (st.ownItemCount(q_gem_of_saints) >= 1) {
                    st.takeItems(q_gem_of_saints, -1);
                    st.giveItems(ADENA_ID, 100000);
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(true);
                    htmltext = "dark_necromancer_q0627_0402.htm";
                } else
                    htmltext = "dark_necromancer_q0627_0403.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=627&reply=12") && GetHTMLCookie == 4 - 1) {
                if (st.ownItemCount(q_gem_of_saints) >= 1) {
                    st.takeItems(q_gem_of_saints, -1);
                    st.giveItems(asofe, 13);
                    st.giveItems(ADENA_ID, 6400);
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(true);
                    htmltext = "dark_necromancer_q0627_0402.htm";
                } else
                    htmltext = "dark_necromancer_q0627_0403.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=627&reply=13") && GetHTMLCookie == 4 - 1) {
                if (st.ownItemCount(q_gem_of_saints) >= 1) {
                    st.takeItems(q_gem_of_saints, -1);
                    st.giveItems(thons, 13);
                    st.giveItems(ADENA_ID, 6400);
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(true);
                    htmltext = "dark_necromancer_q0627_0402.htm";
                } else
                    htmltext = "dark_necromancer_q0627_0403.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=627&reply=14") && GetHTMLCookie == 4 - 1) {
                if (st.ownItemCount(q_gem_of_saints) >= 1) {
                    st.takeItems(q_gem_of_saints, -1);
                    st.giveItems(enria, 6);
                    st.giveItems(ADENA_ID, 13600);
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(true);
                    htmltext = "dark_necromancer_q0627_0402.htm";
                } else
                    htmltext = "dark_necromancer_q0627_0403.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=627&reply=15") && GetHTMLCookie == 4 - 1) {
                if (st.ownItemCount(q_gem_of_saints) >= 1) {
                    st.takeItems(q_gem_of_saints, -1);
                    st.giveItems(mold_hardener, 3);
                    st.giveItems(ADENA_ID, 17200);
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(true);
                    htmltext = "dark_necromancer_q0627_0402.htm";
                } else
                    htmltext = "dark_necromancer_q0627_0403.htm";
            }
        } else if (npcId == enfeux) {
            if (event.equalsIgnoreCase("menu_select?ask=627&reply=1") && GetHTMLCookie == 3 - 1) {
                if (st.ownItemCount(q_seal_of_light) >= 1) {
                    st.setCond(4);
                    st.setMemoState("temptation_of_power", String.valueOf(3 * 10 + 1), true);
                    st.takeItems(q_seal_of_light, 1);
                    st.giveItems(q_gem_of_saints, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "enfeux_q0627_0301.htm";
                } else
                    htmltext = "enfeux_q0627_0302.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("temptation_of_power");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == dark_necromancer) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "dark_necromancer_q0627_0103.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "dark_necromancer_q0627_0101.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == dark_necromancer) {
                    if (GetMemoState >= 1 * 10 + 1 && GetMemoState <= 1 * 10 + 2) {
                        if (GetMemoState == 1 * 10 + 2 && st.ownItemCount(q_gem_of_submission) >= 300) {
                            st.setMemoState("temptation_of_power_cookie", String.valueOf(1), true);
                            htmltext = "dark_necromancer_q0627_0105.htm";
                        } else
                            htmltext = "dark_necromancer_q0627_0106.htm";
                    } else if (GetMemoState == 2 * 10 + 1)
                        htmltext = "dark_necromancer_q0627_0203.htm";
                    else if (st.ownItemCount(q_gem_of_saints) >= 1 && GetMemoState == 3 * 10 + 1) {
                        st.setMemoState("temptation_of_power_cookie", String.valueOf(3), true);
                        htmltext = "dark_necromancer_q0627_0301.htm";
                    }
                } else if (npcId == enfeux) {
                    if (st.ownItemCount(q_seal_of_light) >= 1 && GetMemoState == 2 * 10 + 1) {
                        st.setMemoState("temptation_of_power_cookie", String.valueOf(2), true);
                        htmltext = "enfeux_q0627_0201.htm";
                    } else if (GetMemoState == 3 * 10 + 1)
                        htmltext = "enfeux_q0627_0303.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("temptation_of_power");
        int npcId = npc.getNpcId();
        if (npcId == brilliant_eye) {
            if (GetMemoState == 1 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 661) {
                    if (st.ownItemCount(q_gem_of_submission) + 1 >= 300) {
                        if (st.ownItemCount(q_gem_of_submission) < 300) {
                            st.setCond(2);
                            st.setMemoState("temptation_of_power", String.valueOf(1 * 10 + 2), true);
                            st.giveItems(q_gem_of_submission, 300 - st.ownItemCount(q_gem_of_submission));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    } else {
                        st.giveItems(q_gem_of_submission, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        } else if (npcId == brilliant_light) {
            if (GetMemoState == 1 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 668) {
                    if (st.ownItemCount(q_gem_of_submission) + 1 >= 300) {
                        if (st.ownItemCount(q_gem_of_submission) < 300) {
                            st.setCond(2);
                            st.setMemoState("temptation_of_power", String.valueOf(1 * 10 + 2), true);
                            st.giveItems(q_gem_of_submission, 300 - st.ownItemCount(q_gem_of_submission));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    } else {
                        st.giveItems(q_gem_of_submission, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        } else if (npcId == brilliant_blade || npcId == brilliant_blade_1) {
            if (GetMemoState == 1 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 714) {
                    if (st.ownItemCount(q_gem_of_submission) + 1 >= 300) {
                        if (st.ownItemCount(q_gem_of_submission) < 300) {
                            st.setCond(2);
                            st.setMemoState("temptation_of_power", String.valueOf(1 * 10 + 2), true);
                            st.giveItems(q_gem_of_submission, 300 - st.ownItemCount(q_gem_of_submission));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    } else {
                        st.giveItems(q_gem_of_submission, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        } else if (npcId == brilliant_wisdom) {
            if (GetMemoState == 1 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 796) {
                    if (st.ownItemCount(q_gem_of_submission) + 1 >= 300) {
                        if (st.ownItemCount(q_gem_of_submission) < 300) {
                            st.setCond(2);
                            st.setMemoState("temptation_of_power", String.valueOf(1 * 10 + 2), true);
                            st.giveItems(q_gem_of_submission, 300 - st.ownItemCount(q_gem_of_submission));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    } else {
                        st.giveItems(q_gem_of_submission, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        } else if (npcId == brilliant_soul) {
            if (GetMemoState == 1 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 659) {
                    if (st.ownItemCount(q_gem_of_submission) + 1 >= 300) {
                        if (st.ownItemCount(q_gem_of_submission) < 300) {
                            st.setCond(2);
                            st.setMemoState("temptation_of_power", String.valueOf(1 * 10 + 2), true);
                            st.giveItems(q_gem_of_submission, 300 - st.ownItemCount(q_gem_of_submission));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    } else {
                        st.giveItems(q_gem_of_submission, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        } else if (npcId == brilliant_legacy) {
            if (GetMemoState == 1 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 704) {
                    if (st.ownItemCount(q_gem_of_submission) + 1 >= 300) {
                        if (st.ownItemCount(q_gem_of_submission) < 300) {
                            st.setCond(2);
                            st.setMemoState("temptation_of_power", String.valueOf(1 * 10 + 2), true);
                            st.giveItems(q_gem_of_submission, 300 - st.ownItemCount(q_gem_of_submission));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    } else {
                        st.giveItems(q_gem_of_submission, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        } else if (npcId == brilliant_vengeance || npcId == brilliant_vengeance_1) {
            if (GetMemoState == 1 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 791) {
                    if (st.ownItemCount(q_gem_of_submission) + 1 >= 300) {
                        if (st.ownItemCount(q_gem_of_submission) < 300) {
                            st.setCond(2);
                            st.setMemoState("temptation_of_power", String.valueOf(1 * 10 + 2), true);
                            st.giveItems(q_gem_of_submission, 300 - st.ownItemCount(q_gem_of_submission));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    } else {
                        st.giveItems(q_gem_of_submission, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        } else if (npcId == brilliant_cry) {
            if (GetMemoState == 1 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 820) {
                    if (st.ownItemCount(q_gem_of_submission) + 1 >= 300) {
                        if (st.ownItemCount(q_gem_of_submission) < 300) {
                            st.setCond(2);
                            st.setMemoState("temptation_of_power", String.valueOf(1 * 10 + 2), true);
                            st.giveItems(q_gem_of_submission, 300 - st.ownItemCount(q_gem_of_submission));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    } else {
                        st.giveItems(q_gem_of_submission, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        } else if (npcId == brilliant_mark) {
            if (GetMemoState == 1 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 827) {
                    if (st.ownItemCount(q_gem_of_submission) + 1 >= 300) {
                        if (st.ownItemCount(q_gem_of_submission) < 300) {
                            st.setCond(2);
                            st.setMemoState("temptation_of_power", String.valueOf(1 * 10 + 2), true);
                            st.giveItems(q_gem_of_submission, 300 - st.ownItemCount(q_gem_of_submission));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    } else {
                        st.giveItems(q_gem_of_submission, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        } else if (npcId == brilliant_crown) {
            if (GetMemoState == 1 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 798) {
                    if (st.ownItemCount(q_gem_of_submission) + 1 >= 300) {
                        if (st.ownItemCount(q_gem_of_submission) < 300) {
                            st.setCond(2);
                            st.setMemoState("temptation_of_power", String.valueOf(1 * 10 + 2), true);
                            st.giveItems(q_gem_of_submission, 300 - st.ownItemCount(q_gem_of_submission));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    } else {
                        st.giveItems(q_gem_of_submission, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        } else if (npcId == brilliant_anguish || npcId == brilliant_anguish_1) {
            if (GetMemoState == 1 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 875) {
                    if (st.ownItemCount(q_gem_of_submission) + 1 >= 300) {
                        if (st.ownItemCount(q_gem_of_submission) < 300) {
                            st.setCond(2);
                            st.setMemoState("temptation_of_power", String.valueOf(1 * 10 + 2), true);
                            st.giveItems(q_gem_of_submission, 300 - st.ownItemCount(q_gem_of_submission));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    } else {
                        st.giveItems(q_gem_of_submission, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        }
        return null;
    }
}