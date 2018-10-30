package org.mmocore.gameserver.scripts.quests;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.data.htm.HtmCache;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.model.base.ClassId;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.serverpackets.SocialAction;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.0
 * @date 08/12/2014
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _065_CertifiedSoulBreaker extends Quest {
    // npc
    private final static int box_of_secret_q065 = 32243;
    private final static int grandmaster_holst = 32199;
    private final static int grandmaster_meldina = 32214;
    private final static int grandmaster_vitus = 32213;
    private final static int hitsran = 30074;
    private final static int jaycub = 30073;
    private final static int keit_nat_q0065 = 32242;
    private final static int kekrops = 32138;
    private final static int listto = 30076;
    private final static int lucas = 30071;
    private final static int subelder_casca = 32139;
    private final static int vesa = 30123;
    private final static int wharf_manager_felton = 30879;
    private final static int xaber = 30075;
    private final static int zerome = 30124;
    private final static int nasty_piece_of_work = 32244;
    // mobs
    private final static int wyrm = 20176;
    private final static int guardian_angel_q065 = 27332;
    // items
    private final static int q_dimension_diamond = 7562;
    private final static int q_sealing_paper = 9803;
    private final static int q_heart_of_wyrm = 9804;
    private final static int q_recommend_of_kekrops = 9805;
    private final static int q_certificate_of_vitus = 9806;
    // spawn_current
    public static int spawn_current = 0;

    public _065_CertifiedSoulBreaker() {
        super(false);
        addStartNpc(grandmaster_vitus);
        addTalkId(kekrops, subelder_casca, grandmaster_holst, hitsran, jaycub, lucas, xaber, listto, zerome, vesa, grandmaster_meldina, wharf_manager_felton, box_of_secret_q065, keit_nat_q0065);
        addKillId(wyrm, guardian_angel_q065);
        addLevelCheck(39);
        addClassIdCheck(ClassId.trooper, ClassId.warder);
        addRaceCheck(PlayerRace.kamael);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("proof_of_soul_breaker");
        int npcId = npc.getNpcId();
        if (npcId == grandmaster_vitus) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("proof_of_soul_breaker", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                if (!st.getPlayer().getPlayerVariables().getBoolean(PlayerVariables.DD1)) {
                    st.giveItems(q_dimension_diamond, 47);
                    st.getPlayer().getPlayerVariables().set(PlayerVariables.DD1, "1", -1);
                    htmltext = "grandmaster_vitus_q0065_05.htm";
                } else
                    htmltext = "grandmaster_vitus_q0065_06.htm";
            } else if (event.equalsIgnoreCase("reply_1"))
                htmltext = "grandmaster_vitus_q0065_04.htm";
        } else if (npcId == kekrops) {
            if (event.equalsIgnoreCase("reply_1") && GetMemoState == 1)
                htmltext = "kekrops_q0065_02.htm";
            else if (event.equalsIgnoreCase("reply_2") && GetMemoState == 1)
                htmltext = "kekrops_q0065_03.htm";
            else if (event.equalsIgnoreCase("reply_3") && GetMemoState == 1) {
                st.setCond(2);
                st.setMemoState("proof_of_soul_breaker", String.valueOf(2), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "kekrops_q0065_04.htm";
            } else if (event.equalsIgnoreCase("reply_4") && GetMemoState == 21) {
                st.setCond(15);
                st.setMemoState("proof_of_soul_breaker", String.valueOf(22), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "kekrops_q0065_07.htm";
            } else if (event.equalsIgnoreCase("reply_5") && GetMemoState == 23)
                htmltext = "kekrops_q0065_10.htm";
            else if (event.equalsIgnoreCase("reply_6") && GetMemoState == 23)
                htmltext = "kekrops_q0065_11.htm";
            else if (event.equalsIgnoreCase("reply_7") && GetMemoState == 23) {
                st.setCond(17);
                st.setMemoState("proof_of_soul_breaker", String.valueOf(24), true);
                st.giveItems(q_recommend_of_kekrops, 1);
                st.takeItems(q_heart_of_wyrm, -1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "kekrops_q0065_12.htm";
            }
        } else if (npcId == subelder_casca) {
            if (event.equalsIgnoreCase("reply_1") && GetMemoState == 2) {
                st.setCond(3);
                st.setMemoState("proof_of_soul_breaker", String.valueOf(3), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "subelder_casca_q0065_02.htm";
            } else if (event.equalsIgnoreCase("reply_2") && GetMemoState == 3) {
                st.setCond(4);
                st.setMemoState("proof_of_soul_breaker", String.valueOf(4), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "subelder_casca_q0065_04.htm";
            } else if (event.equalsIgnoreCase("reply_3") && GetMemoState == 14)
                htmltext = "subelder_casca_q0065_07.htm";
            else if (event.equalsIgnoreCase("reply_4") && GetMemoState == 14) {
                st.setCond(14);
                st.setMemoState("proof_of_soul_breaker", String.valueOf(21), true);
                st.takeItems(q_sealing_paper, -1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "subelder_casca_q0065_08.htm";
            }
        } else if (npcId == grandmaster_holst) {
            if (event.equalsIgnoreCase("reply_1") && GetMemoState == 4) {
                st.setCond(5);
                st.setMemoState("proof_of_soul_breaker", String.valueOf(5), true);
                st.soundEffect(SOUND_MIDDLE);
                NpcInstance nasty = st.addSpawn(nasty_piece_of_work, 16489, 146249, -3112);
                if (nasty != null) {
                    Functions.npcSay(nasty, NpcString.DRATS_HOW_COULD_I_BE_SO_WRONG);
                    nasty.setWalking();
                    nasty.moveToLocation(16490, 145839, -3080, 0, false);
                }
                st.startQuestTimer("6501", 5000, nasty);
                htmltext = "grandmaster_holst_q0065_02.htm";
            }
        } else if (npcId == lucas) {
            if (event.equalsIgnoreCase("reply_1") && GetMemoState == 7) {
                st.setCond(8);
                st.setMemoState("proof_of_soul_breaker", String.valueOf(8), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "lucas_q0065_02.htm";
            }
        } else if (npcId == grandmaster_meldina) {
            if (event.equalsIgnoreCase("reply_1") && GetMemoState == 10) {
                st.setCond(11);
                st.setMemoState("proof_of_soul_breaker", String.valueOf(11), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "grandmaster_meldina_q0065_02.htm";
            }
        } else if (npcId == wharf_manager_felton) {
            if (event.equalsIgnoreCase("reply_1") && GetMemoState == 11)
                htmltext = "wharf_manager_felton_q0065_02.htm";
            else if (event.equalsIgnoreCase("reply_2") && GetMemoState == 11) {
                st.setCond(12);
                st.setMemoState("proof_of_soul_breaker", String.valueOf(12), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "wharf_manager_felton_q0065_03.htm";
            }
        } else if (npcId == keit_nat_q0065) {
            if (event.equalsIgnoreCase("reply_1") && GetMemoState == 14) {
                if (npc != null) {
                    Functions.npcSay(npc, NpcString.GOOD_LUCK);
                    if (st.isRunningQuestTimer("6512"))
                        st.cancelQuestTimer("6512");
                    spawn_current = 0;
                    npc.deleteMe();
                }
                return null;
            }
        } else if (event.equalsIgnoreCase("6501")) {
            if (npc != null)
                npc.deleteMe();
            return null;
        } else if (event.equalsIgnoreCase("6511")) {
            if (npc != null) {
                Functions.npcSay(npc, NpcString.S1_I_WILL_BE_BACK_SOON_STAY_THERE_AND_DONT_YOU_DARE_WANDER_OFF, st.getPlayer().getName());
                st.removeMemo("q0065_player_name");
                spawn_current = 0;
                npc.deleteMe();
            }
            return null;
        } else if (event.equalsIgnoreCase("6512")) {
            if (npc != null) {
                st.removeMemo("q0065_player_name");
                spawn_current = 0;
                npc.deleteMe();
            }
            return null;
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("proof_of_soul_breaker");
        int GetMemoStateEx = st.getInt("proof_of_soul_breaker_ex");
        String talker_dbid = st.get("q0065_player_name");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == grandmaster_vitus) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "grandmaster_vitus_q0065_03.htm";
                            st.exitQuest(true);
                            break;
                        case CLASS_ID:
                            htmltext = "grandmaster_vitus_q0065_03.htm";
                            st.exitQuest(true);
                            break;
                        case RACE:
                            htmltext = "grandmaster_vitus_q0065_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "grandmaster_vitus_q0065_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == grandmaster_vitus) {
                    if (GetMemoState == 1)
                        htmltext = "grandmaster_vitus_q0065_07.htm";
                    else if (GetMemoState > 1 && GetMemoState < 24)
                        htmltext = "grandmaster_vitus_q0065_08.htm";
                    else if (GetMemoState == 24) {
                        st.giveItems(q_certificate_of_vitus, 1);
                        st.takeItems(q_recommend_of_kekrops, -1);
                        if (!st.getPlayer().getPlayerVariables().getBoolean(PlayerVariables.PROF2_1)) {
                            st.addExpAndSp(393750, 27020);
                            st.giveItems(ADENA_ID, 71194);
                        }
                        st.removeMemo("proof_of_soul_breaker");
                        st.removeMemo("proof_of_soul_breaker_ex");
                        st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                        htmltext = "grandmaster_vitus_q0065_10.htm";
                    }
                } else if (npcId == kekrops) {
                    if (GetMemoState == 1) {
                        htmltext = HtmCache.getInstance().getHtml("quests/_065_CertifiedSoulBreaker/kekrops_q0065_01.htm", st.getPlayer());
                        htmltext = htmltext.replace("<?name?>", st.getPlayer().getName());
                    } else if (GetMemoState == 2)
                        htmltext = "kekrops_q0065_05.htm";
                    else if (GetMemoState == 21)
                        htmltext = "kekrops_q0065_06.htm";
                    else if (GetMemoState == 22)
                        htmltext = "kekrops_q0065_08.htm";
                    else if (GetMemoState == 23)
                        htmltext = "kekrops_q0065_09.htm";
                    else if (GetMemoState == 24)
                        htmltext = "kekrops_q0065_13.htm";
                } else if (npcId == subelder_casca) {
                    if (GetMemoState == 2)
                        htmltext = "subelder_casca_q0065_01.htm";
                    else if (GetMemoState == 3)
                        htmltext = "subelder_casca_q0065_03.htm";
                    else if (GetMemoState == 4)
                        htmltext = "subelder_casca_q0065_05.htm";
                    else if (GetMemoState == 14)
                        htmltext = "subelder_casca_q0065_06.htm";
                    else if (GetMemoState == 21)
                        htmltext = "subelder_casca_q0065_09.htm";
                } else if (npcId == grandmaster_holst) {
                    if (GetMemoState == 4)
                        htmltext = "grandmaster_holst_q0065_01.htm";
                    else if (GetMemoState == 5) {
                        st.setCond(6);
                        st.setMemoState("proof_of_soul_breaker", String.valueOf(6), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "grandmaster_holst_q0065_03.htm";
                    } else if (GetMemoState == 6)
                        htmltext = "grandmaster_holst_q0065_04.htm";
                } else if (npcId == hitsran) {
                    if (GetMemoState == 6 && GetMemoStateEx == 0) {
                        st.setMemoState("proof_of_soul_breaker_ex", String.valueOf(1), true);
                        htmltext = "hitsran_q0065_01.htm";
                    } else if (GetMemoState == 6 && GetMemoStateEx == 1)
                        htmltext = "hitsran_q0065_01a.htm";
                    else if (GetMemoState == 6 && GetMemoStateEx == 10) {
                        st.setCond(7);
                        st.setMemoState("proof_of_soul_breaker", String.valueOf(7), true);
                        st.setMemoState("proof_of_soul_breaker_ex", String.valueOf(0), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "hitsran_q0065_02.htm";
                    } else if (GetMemoState == 7)
                        htmltext = "hitsran_q0065_03.htm";
                } else if (npcId == jaycub) {
                    if (GetMemoState == 6 && GetMemoStateEx == 0) {
                        st.setMemoState("proof_of_soul_breaker_ex", String.valueOf(10), true);
                        htmltext = "jaycub_q0065_01.htm";
                    } else if (GetMemoState == 6 && GetMemoStateEx == 10)
                        htmltext = "jaycub_q0065_01a.htm";
                    else if (GetMemoState == 6 && GetMemoStateEx == 1) {
                        st.setCond(7);
                        st.setMemoState("proof_of_soul_breaker", String.valueOf(7), true);
                        st.setMemoState("proof_of_soul_breaker_ex", String.valueOf(0), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "jaycub_q0065_02.htm";
                    } else if (GetMemoState == 7)
                        htmltext = "jaycub_q0065_03.htm";
                } else if (npcId == lucas) {
                    if (GetMemoState == 7)
                        htmltext = "lucas_q0065_01.htm";
                    else if (GetMemoState == 8)
                        htmltext = "lucas_q0065_03.htm";
                } else if (npcId == xaber) {
                    if (GetMemoState == 8 && GetMemoStateEx == 0) {
                        st.setMemoState("proof_of_soul_breaker_ex", String.valueOf(1), true);
                        htmltext = "xaber_q0065_01.htm";
                    } else if (GetMemoState == 8 && GetMemoStateEx == 1)
                        htmltext = "xaber_q0065_01a.htm";
                    else if (GetMemoState == 8 && GetMemoStateEx == 10) {
                        st.setCond(9);
                        st.setMemoState("proof_of_soul_breaker", String.valueOf(9), true);
                        st.setMemoState("proof_of_soul_breaker_ex", String.valueOf(0), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "xaber_q0065_02.htm";
                    } else if (GetMemoState == 9)
                        htmltext = "xaber_q0065_03.htm";
                } else if (npcId == listto) {
                    if (GetMemoState == 8 && GetMemoStateEx == 0) {
                        st.setMemoState("proof_of_soul_breaker_ex", String.valueOf(10), true);
                        htmltext = "listto_q0065_01.htm";
                    } else if (GetMemoState == 8 && GetMemoStateEx == 10)
                        htmltext = "listto_q0065_01a.htm";
                    else if (GetMemoState == 8 && GetMemoStateEx == 1) {
                        st.setCond(9);
                        st.setMemoState("proof_of_soul_breaker", String.valueOf(9), true);
                        st.setMemoState("proof_of_soul_breaker_ex", String.valueOf(0), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "listto_q0065_02.htm";
                    } else if (GetMemoState == 9)
                        htmltext = "listto_q0065_03.htm";
                } else if (npcId == zerome) {
                    if (GetMemoState == 9 && GetMemoStateEx == 0) {
                        st.setMemoState("proof_of_soul_breaker_ex", String.valueOf(1), true);
                        htmltext = "zerome_q0065_01.htm";
                    } else if (GetMemoState == 9 && GetMemoStateEx == 1)
                        htmltext = "zerome_q0065_01a.htm";
                    else if (GetMemoState == 9 && GetMemoStateEx == 10) {
                        st.setCond(10);
                        st.setMemoState("proof_of_soul_breaker", String.valueOf(10), true);
                        st.setMemoState("proof_of_soul_breaker_ex", String.valueOf(0), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "zerome_q0065_02.htm";
                    } else if (GetMemoState == 10)
                        htmltext = "zerome_q0065_03.htm";
                } else if (npcId == vesa) {
                    if (GetMemoState == 9 && GetMemoStateEx == 0) {
                        st.setMemoState("proof_of_soul_breaker_ex", String.valueOf(1), true);
                        htmltext = "vesa_q0065_01.htm";
                    } else if (GetMemoState == 9 && GetMemoStateEx == 10)
                        htmltext = "vesa_q0065_01a.htm";
                    else if (GetMemoState == 9 && GetMemoStateEx == 1) {
                        st.setCond(10);
                        st.setMemoState("proof_of_soul_breaker", String.valueOf(10), true);
                        st.setMemoState("proof_of_soul_breaker_ex", String.valueOf(0), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "vesa_q0065_02.htm";
                    } else if (GetMemoState == 10)
                        htmltext = "vesa_q0065_03.htm";
                } else if (npcId == grandmaster_meldina) {
                    if (GetMemoState == 10)
                        htmltext = "grandmaster_meldina_q0065_01.htm";
                    else if (GetMemoState == 11)
                        htmltext = "grandmaster_meldina_q0065_03.htm";
                } else if (npcId == wharf_manager_felton) {
                    if (GetMemoState == 11)
                        htmltext = "wharf_manager_felton_q0065_01.htm";
                    else if (GetMemoState == 12)
                        htmltext = "wharf_manager_felton_q0065_04.htm";
                } else if (npcId == box_of_secret_q065) {
                    if (GetMemoState == 12) {
                        if (spawn_current == 0) {
                            spawn_current++;
                            st.setMemoState("q0065_player_name", st.getPlayer().getName(), true);
                            htmltext = "box_of_secret_q065_q0065_01.htm";
                            NpcInstance guardian_angel = st.addSpawn(guardian_angel_q065, 36110, 191921, -3712);
                            if (guardian_angel != null) {
                                Functions.npcSay(guardian_angel, NpcString.S1_STEP_BACK_FROM_THE_CONFOUNDED_BOX_I_WILL_TAKE_IT_MYSELF, st.getPlayer().getName());
                                guardian_angel.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, st.getPlayer(), 10000);
                            }
                            st.startQuestTimer("6511", 70000, guardian_angel);
                        } else if (talker_dbid == st.getPlayer().getName())
                            htmltext = "box_of_secret_q065_q0065_03.htm";
                        else
                            htmltext = "box_of_secret_q065_q0065_02.htm";
                    } else if (GetMemoState == 13) {
                        if (spawn_current == 0) {
                            spawn_current++;
                            st.setMemoState("q0065_player_name", st.getPlayer().getName(), true);
                            htmltext = "box_of_secret_q065_q0065_06.htm";
                            NpcInstance keit = st.addSpawn(keit_nat_q0065, st.getPlayer().getX() + 20, st.getPlayer().getY() + 20, st.getPlayer().getZ());
                            if (keit != null)
                                Functions.npcSay(keit, NpcString.I_AM_LATE);
                            st.startQuestTimer("6512", 50000, keit);
                        } else if (talker_dbid == st.getPlayer().getName())
                            htmltext = "box_of_secret_q065_q0065_04.htm";
                        else
                            htmltext = "box_of_secret_q065_q0065_05.htm";
                    } else if (GetMemoState == 7)
                        htmltext = "box_of_secret_q065_q0065_07.htm";
                } else if (npcId == keit_nat_q0065) {
                    if (GetMemoState == 12)
                        htmltext = "keit_nat_q0065_q0065_01.htm";
                    else if (GetMemoState == 13) {
                        if (talker_dbid == st.getPlayer().getName()) {
                            st.setCond(13);
                            st.setMemoState("proof_of_soul_breaker", String.valueOf(14), true);
                            st.soundEffect(SOUND_MIDDLE);
                            htmltext = "keit_nat_q0065_q0065_02.htm";
                        } else {
                            st.setCond(13);
                            st.setMemoState("proof_of_soul_breaker", String.valueOf(14), true);
                            st.soundEffect(SOUND_MIDDLE);
                            htmltext = "keit_nat_q0065_q0065_03.htm";
                        }
                        if (st.ownItemCount(q_sealing_paper) == 0)
                            st.giveItems(q_sealing_paper, 1);
                    } else if (GetMemoState == 14)
                        htmltext = "keit_nat_q0065_q0065_04.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        String talker_dbid = st.get("q0065_player_name");
        int GetMemoState = st.getInt("proof_of_soul_breaker");
        int npcId = npc.getNpcId();
        if (npcId == guardian_angel_q065) {
            if (GetMemoState == 12 && talker_dbid == st.getPlayer().getName()) {
                st.setMemoState("proof_of_soul_breaker", String.valueOf(13), true);
                Functions.npcSay(npc, NpcString.GRR_IVE_BEEN_HIT);
                if (st.isRunningQuestTimer("6511"))
                    st.cancelQuestTimer("6511");
                spawn_current = 0;
            } else {
                Functions.npcSay(npc, NpcString.GRR_WHO_ARE_YOU_AND_WHY_HAVE_YOU_STOPPED_ME);
                spawn_current = 0;
            }
        } else if (npcId == wyrm) {
            if (GetMemoState == 22 && st.ownItemCount(q_heart_of_wyrm) < 10) {
                int i0 = Rnd.get(100);
                if (i0 < 20) {
                    st.giveItems(q_heart_of_wyrm, 1);
                    if (st.ownItemCount(q_heart_of_wyrm) >= 9) {
                        st.setCond(16);
                        st.setMemoState("proof_of_soul_breaker", String.valueOf(23), true);
                        st.soundEffect(SOUND_MIDDLE);
                    } else
                        st.soundEffect(SOUND_ITEMGET);
                }
            }
        }
        return null;
    }
}