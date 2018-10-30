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
 * @date 08/01/2015
 */
@ChronicleCheck(Chronicle.FREYA)
public class _647_InfluxOfMachines extends Quest {
    // npc
    private static final int collecter_gutenhagen = 32069;
    // mobs
    private static final int golem_cannon1_p = 22801;
    private static final int golem_cannon2_p = 22802;
    private static final int golem_cannon3_p = 22803;
    private static final int golem_prop1_p = 22804;
    private static final int golem_prop2_p = 22805;
    private static final int golem_prop3_p = 22806;
    private static final int golem_carrier_p = 22807;
    private static final int golem_mechanic_p = 22808;
    private static final int golem_guardian_p = 22809;
    private static final int golem_micro_p = 22810;
    private static final int golem_steel_p = 22811;
    private static final int golem_boom1_p = 22812;
    // questitem
    private static final int q_broken_golem_re = 15521;
    // etcitem
    private static final int rp_forgotten_blade_i = 6881;
    private static final int rp_basalt_battlehammer_i = 6883;
    private static final int rp_imperial_staff_i = 6885;
    private static final int rp_angel_slayer_i = 6887;
    private static final int rp_draconic_bow_i = 7580;
    private static final int rp_dragon_hunter_axe_i = 6891;
    private static final int rp_saint_spear_i = 6893;
    private static final int rp_demon_splinter_i = 6895;
    private static final int rp_heavens_divider_i = 6897;
    private static final int rp_arcana_mace_i = 6899;

    public _647_InfluxOfMachines() {
        super(true);
        addStartNpc(collecter_gutenhagen);
        addKillId(golem_cannon1_p, golem_cannon2_p, golem_cannon3_p, golem_prop1_p, golem_prop2_p, golem_prop3_p, golem_carrier_p, golem_mechanic_p, golem_guardian_p, golem_micro_p, golem_steel_p, golem_boom1_p);
        addQuestItem(q_broken_golem_re);
        addLevelCheck(70);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("mad_machine");
        int GetHTMLCookie = st.getInt("mad_machine_cookie");
        int npcId = npc.getNpcId();
        if (npcId == collecter_gutenhagen) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("mad_machine", String.valueOf(1 * 10 + 1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "collecter_gutenhagen_q0647_0103.htm";
            } else if (event.equalsIgnoreCase("reply_3") && GetHTMLCookie == 2 - 1) {
                if (GetMemoState >= (2 - 1) * 10 + 1) {
                    int i1 = Rnd.get(10);
                    st.takeItems(q_broken_golem_re, -1);
                    if (i1 == 0)
                        st.giveItems(rp_forgotten_blade_i, 1);
                    else if (i1 == 1)
                        st.giveItems(rp_basalt_battlehammer_i, 1);
                    else if (i1 == 2)
                        st.giveItems(rp_imperial_staff_i, 1);
                    else if (i1 == 3)
                        st.giveItems(rp_angel_slayer_i, 1);
                    else if (i1 == 4)
                        st.giveItems(rp_draconic_bow_i, 1);
                    else if (i1 == 5)
                        st.giveItems(rp_dragon_hunter_axe_i, 1);
                    else if (i1 == 6)
                        st.giveItems(rp_saint_spear_i, 1);
                    else if (i1 == 7)
                        st.giveItems(rp_demon_splinter_i, 1);
                    else if (i1 == 8)
                        st.giveItems(rp_heavens_divider_i, 1);
                    else if (i1 == 9)
                        st.giveItems(rp_arcana_mace_i, 1);
                    st.removeMemo("mad_machine");
                    st.removeMemo("mad_machine_cookie");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(true);
                    htmltext = "collecter_gutenhagen_q0647_0201.htm";
                } else
                    htmltext = "collecter_gutenhagen_q0647_0202.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("mad_machine");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == collecter_gutenhagen) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "collecter_gutenhagen_q0647_0102.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "collecter_gutenhagen_q0647_0101.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == collecter_gutenhagen) {
                    if (GetMemoState >= 1 * 10 + 1 && GetMemoState <= 1 * 10 + 2) {
                        if (GetMemoState == 1 * 10 + 2 && st.ownItemCount(q_broken_golem_re) >= 500) {
                            st.setMemoState("mad_machine_cookie", String.valueOf(1), true);
                            htmltext = "collecter_gutenhagen_q0647_0105.htm";
                        } else
                            htmltext = "collecter_gutenhagen_q0647_0106.htm";
                    }
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("mad_machine");
        int npcId = npc.getNpcId();
        if (npcId == golem_cannon1_p) {
            if (GetMemoState == 1 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 280) {
                    if (st.ownItemCount(q_broken_golem_re) + 1 >= 500) {
                        if (st.ownItemCount(q_broken_golem_re) < 500) {
                            st.giveItems(q_broken_golem_re, 500 - st.ownItemCount(q_broken_golem_re));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        st.setCond(2);
                        st.setMemoState("mad_machine", String.valueOf(1 * 10 + 2), true);
                    } else {
                        st.giveItems(q_broken_golem_re, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        } else if (npcId == golem_cannon2_p) {
            if (GetMemoState == 1 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 227) {
                    if (st.ownItemCount(q_broken_golem_re) + 1 >= 500) {
                        if (st.ownItemCount(q_broken_golem_re) < 500) {
                            st.giveItems(q_broken_golem_re, 500 - st.ownItemCount(q_broken_golem_re));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        st.setCond(2);
                        st.setMemoState("mad_machine", String.valueOf(1 * 10 + 2), true);
                    } else {
                        st.giveItems(q_broken_golem_re, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        } else if (npcId == golem_cannon3_p) {
            if (GetMemoState == 1 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 286) {
                    if (st.ownItemCount(q_broken_golem_re) + 1 >= 500) {
                        if (st.ownItemCount(q_broken_golem_re) < 500) {
                            st.giveItems(q_broken_golem_re, 500 - st.ownItemCount(q_broken_golem_re));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        st.setCond(2);
                        st.setMemoState("mad_machine", String.valueOf(1 * 10 + 2), true);
                    } else {
                        st.giveItems(q_broken_golem_re, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        } else if (npcId == golem_prop1_p) {
            if (GetMemoState == 1 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 288) {
                    if (st.ownItemCount(q_broken_golem_re) + 1 >= 500) {
                        if (st.ownItemCount(q_broken_golem_re) < 500) {
                            st.giveItems(q_broken_golem_re, 500 - st.ownItemCount(q_broken_golem_re));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        st.setCond(2);
                        st.setMemoState("mad_machine", String.valueOf(1 * 10 + 2), true);
                    } else {
                        st.giveItems(q_broken_golem_re, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        } else if (npcId == golem_prop2_p) {
            if (GetMemoState == 1 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 235) {
                    if (st.ownItemCount(q_broken_golem_re) + 1 >= 500) {
                        if (st.ownItemCount(q_broken_golem_re) < 500) {
                            st.giveItems(q_broken_golem_re, 500 - st.ownItemCount(q_broken_golem_re));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        st.setCond(2);
                        st.setMemoState("mad_machine", String.valueOf(1 * 10 + 2), true);
                    } else {
                        st.giveItems(q_broken_golem_re, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        } else if (npcId == golem_prop3_p) {
            if (GetMemoState == 1 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 295) {
                    if (st.ownItemCount(q_broken_golem_re) + 1 >= 500) {
                        if (st.ownItemCount(q_broken_golem_re) < 500) {
                            st.giveItems(q_broken_golem_re, 500 - st.ownItemCount(q_broken_golem_re));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        st.setCond(2);
                        st.setMemoState("mad_machine", String.valueOf(1 * 10 + 2), true);
                    } else {
                        st.giveItems(q_broken_golem_re, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        } else if (npcId == golem_carrier_p) {
            if (GetMemoState == 1 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 273) {
                    if (st.ownItemCount(q_broken_golem_re) + 1 >= 500) {
                        if (st.ownItemCount(q_broken_golem_re) < 500) {
                            st.giveItems(q_broken_golem_re, 500 - st.ownItemCount(q_broken_golem_re));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        st.setCond(2);
                        st.setMemoState("mad_machine", String.valueOf(1 * 10 + 2), true);
                    } else {
                        st.giveItems(q_broken_golem_re, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        } else if (npcId == golem_mechanic_p) {
            if (GetMemoState == 1 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 143) {
                    if (st.ownItemCount(q_broken_golem_re) + 1 >= 500) {
                        if (st.ownItemCount(q_broken_golem_re) < 500) {
                            st.giveItems(q_broken_golem_re, 500 - st.ownItemCount(q_broken_golem_re));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        st.setCond(2);
                        st.setMemoState("mad_machine", String.valueOf(1 * 10 + 2), true);
                    } else {
                        st.giveItems(q_broken_golem_re, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        } else if (npcId == golem_guardian_p) {
            if (GetMemoState == 1 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 629) {
                    if (st.ownItemCount(q_broken_golem_re) + 1 >= 500) {
                        if (st.ownItemCount(q_broken_golem_re) < 500) {
                            st.giveItems(q_broken_golem_re, 500 - st.ownItemCount(q_broken_golem_re));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        st.setCond(2);
                        st.setMemoState("mad_machine", String.valueOf(1 * 10 + 2), true);
                    } else {
                        st.giveItems(q_broken_golem_re, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        } else if (npcId == golem_micro_p) {
            if (GetMemoState == 1 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 465) {
                    if (st.ownItemCount(q_broken_golem_re) + 1 >= 500) {
                        if (st.ownItemCount(q_broken_golem_re) < 500) {
                            st.giveItems(q_broken_golem_re, 500 - st.ownItemCount(q_broken_golem_re));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        st.setCond(2);
                        st.setMemoState("mad_machine", String.valueOf(1 * 10 + 2), true);
                    } else {
                        st.giveItems(q_broken_golem_re, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        } else if (npcId == golem_steel_p) {
            if (GetMemoState == 1 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 849) {
                    if (st.ownItemCount(q_broken_golem_re) + 1 >= 500) {
                        if (st.ownItemCount(q_broken_golem_re) < 500) {
                            st.giveItems(q_broken_golem_re, 500 - st.ownItemCount(q_broken_golem_re));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        st.setCond(2);
                        st.setMemoState("mad_machine", String.valueOf(1 * 10 + 2), true);
                    } else {
                        st.giveItems(q_broken_golem_re, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        } else if (npcId == golem_boom1_p) {
            if (GetMemoState == 1 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 463) {
                    if (st.ownItemCount(q_broken_golem_re) + 1 >= 500) {
                        if (st.ownItemCount(q_broken_golem_re) < 500) {
                            st.giveItems(q_broken_golem_re, 500 - st.ownItemCount(q_broken_golem_re));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        st.setCond(2);
                        st.setMemoState("mad_machine", String.valueOf(1 * 10 + 2), true);
                    } else {
                        st.giveItems(q_broken_golem_re, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        }
        return null;
    }
}