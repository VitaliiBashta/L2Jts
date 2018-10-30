package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ExStartScenePlayer;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.0
 * @date 01/12/2014
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _194_SevenSignsMammonsContract extends Quest {
    // npc
    private final static int sir_gustaf_athebaldt = 30760;
    private final static int isecret_agent_colin = 32571;
    private final static int king_frog = 32572;
    private final static int grandmother_tess = 32573;
    private final static int native_kuda = 32574;
    private final static int claudia_a = 31001;
    // questitem
    private final static int q_voucher_of_atebalt = 13818;
    private final static int q_gloves_of_native = 13819;
    private final static int q_orb_of_king_frog = 13820;
    private final static int q_candy_sac = 13821;

    public _194_SevenSignsMammonsContract() {
        super(false);
        addStartNpc(sir_gustaf_athebaldt);
        addTalkId(isecret_agent_colin, king_frog, grandmother_tess, native_kuda, claudia_a);
        addQuestItem(q_voucher_of_atebalt, q_gloves_of_native, q_orb_of_king_frog, q_candy_sac);
        addLevelCheck(79);
        addQuestCompletedCheck(193);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int talker_transformID = st.getPlayer().getTransformationId();
        int GetMemoState = st.getInt("ssq_contract_of_mammon");
        int npcId = npc.getNpcId();
        if (npcId == sir_gustaf_athebaldt) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("ssq_contract_of_mammon", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "sir_gustaf_athebaldt_q0194_04.htm";
            } else if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 1)
                    htmltext = "sir_gustaf_athebaldt_q0194_06.htm";
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 1) {
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "sir_gustaf_athebaldt_q0194_07.htm";
                }
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 1) {
                    st.setCond(2);
                    st.setMemoState("ssq_contract_of_mammon", String.valueOf(2), true);
                    st.soundEffect(SOUND_MIDDLE);
                    st.getPlayer().showQuestMovie(ExStartScenePlayer.SCENE_SSQ_CONTRACT_OF_MAMMON);
                    return null;
                }
            } else if (event.equalsIgnoreCase("reply_4")) {
                if (GetMemoState == 2)
                    htmltext = "sir_gustaf_athebaldt_q0194_11.htm";
            } else if (event.equalsIgnoreCase("reply_5")) {
                if (GetMemoState == 2) {
                    st.setCond(3);
                    st.setMemoState("ssq_contract_of_mammon", String.valueOf(3), true);
                    st.giveItems(q_voucher_of_atebalt, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "sir_gustaf_athebaldt_q0194_12.htm";
                }
            }
        } else if (npcId == isecret_agent_colin) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 3) {
                    if (st.ownItemCount(q_voucher_of_atebalt) >= 1)
                        htmltext = "secret_agent_colin_q0194_03.htm";
                }
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 3) {
                    if (st.ownItemCount(q_voucher_of_atebalt) >= 1)
                        htmltext = "secret_agent_colin_q0194_04.htm";
                }
            } else if (event.equalsIgnoreCase("reply_14")) {
                if (GetMemoState == 3) {
                    if (st.ownItemCount(q_voucher_of_atebalt) >= 1) {
                        if (st.getPlayer().isTransformed() || st.getPlayer().isMounted()) {
                            st.getPlayer().sendPacket(SystemMsg.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
                            return null;
                        }
                        negateSpeedBuffs(st.getPlayer());
                        SkillTable.getInstance().getSkillEntry(6201, 1).getEffects(npc, st.getPlayer(), false, false);
                        st.setCond(4);
                        st.setMemoState("ssq_contract_of_mammon", String.valueOf(4), true);
                        st.takeItems(q_voucher_of_atebalt, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "secret_agent_colin_q0194_04a.htm";
                    }
                }
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 4 && talker_transformID != 111 && st.ownItemCount(q_orb_of_king_frog) < 1) {
                    if (st.getPlayer().isTransformed() || st.getPlayer().isMounted()) {
                        st.getPlayer().sendPacket(SystemMsg.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
                        return null;
                    }
                    negateSpeedBuffs(st.getPlayer());
                    SkillTable.getInstance().getSkillEntry(6201, 1).getEffects(npc, st.getPlayer(), false, false);
                    htmltext = "secret_agent_colin_q0194_05a.htm";
                }
            } else if (event.equalsIgnoreCase("reply_4")) {
                if (GetMemoState == 4 && talker_transformID == 111 && st.ownItemCount(q_orb_of_king_frog) < 1) {
                    st.getPlayer().stopTransformation();
                    htmltext = "secret_agent_colin_q0194_05c.htm";
                }
            } else if (event.equalsIgnoreCase("reply_5")) {
                if (GetMemoState == 5) {
                    if (st.ownItemCount(q_orb_of_king_frog) >= 1) {
                        st.setCond(6);
                        st.setMemoState("ssq_contract_of_mammon", String.valueOf(6), true);
                        st.takeItems(q_orb_of_king_frog, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "secret_agent_colin_q0194_05e.htm";
                        if (talker_transformID == 111)
                            st.getPlayer().stopTransformation();
                    }
                }
            } else if (event.equalsIgnoreCase("reply_6")) {
                if (GetMemoState == 6) {
                    if (st.getPlayer().isTransformed() || st.getPlayer().isMounted()) {
                        st.getPlayer().sendPacket(SystemMsg.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
                        return null;
                    }
                    negateSpeedBuffs(st.getPlayer());
                    SkillTable.getInstance().getSkillEntry(6202, 1).getEffects(npc, st.getPlayer(), false, false);
                    st.setCond(7);
                    st.setMemoState("ssq_contract_of_mammon", String.valueOf(7), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "secret_agent_colin_q0194_07.htm";
                }
            } else if (event.equalsIgnoreCase("reply_7")) {
                if (GetMemoState == 7 && talker_transformID != 112 && st.ownItemCount(q_candy_sac) < 1) {
                    if (st.getPlayer().isTransformed() || st.getPlayer().isMounted()) {
                        st.getPlayer().sendPacket(SystemMsg.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
                        return null;
                    }
                    negateSpeedBuffs(st.getPlayer());
                    SkillTable.getInstance().getSkillEntry(6202, 1).getEffects(npc, st.getPlayer(), false, false);
                    htmltext = "secret_agent_colin_q0194_08a.htm";
                }
            } else if (event.equalsIgnoreCase("reply_8")) {
                if (GetMemoState == 7 && talker_transformID == 112 && st.ownItemCount(q_candy_sac) < 1) {
                    st.getPlayer().stopTransformation();
                    htmltext = "secret_agent_colin_q0194_08c.htm";
                }
            } else if (event.equalsIgnoreCase("reply_9")) {
                if (GetMemoState == 8) {
                    if (st.ownItemCount(q_candy_sac) >= 1) {
                        st.setCond(9);
                        st.setMemoState("ssq_contract_of_mammon", String.valueOf(9), true);
                        st.takeItems(q_candy_sac, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "secret_agent_colin_q0194_08e.htm";
                        if (talker_transformID == 112)
                            st.getPlayer().stopTransformation();
                    }
                }
            } else if (event.equalsIgnoreCase("reply_10")) {
                if (GetMemoState == 9) {
                    if (st.getPlayer().isTransformed() || st.getPlayer().isMounted()) {
                        st.getPlayer().sendPacket(SystemMsg.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
                        return null;
                    }
                    negateSpeedBuffs(st.getPlayer());
                    SkillTable.getInstance().getSkillEntry(6203, 1).getEffects(npc, st.getPlayer(), false, false);
                    st.setCond(10);
                    st.setMemoState("ssq_contract_of_mammon", String.valueOf(10), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "secret_agent_colin_q0194_10.htm";
                }
            } else if (event.equalsIgnoreCase("reply_11")) {
                if (GetMemoState == 10 && talker_transformID != 101 && st.ownItemCount(q_gloves_of_native) < 1) {
                    if (st.getPlayer().isTransformed() || st.getPlayer().isMounted()) {
                        st.getPlayer().sendPacket(SystemMsg.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
                        return null;
                    }
                    negateSpeedBuffs(st.getPlayer());
                    SkillTable.getInstance().getSkillEntry(6203, 1).getEffects(npc, st.getPlayer(), false, false);
                    htmltext = "secret_agent_colin_q0194_11a.htm";
                }
            } else if (event.equalsIgnoreCase("reply_12")) {
                if (GetMemoState == 10 && talker_transformID == 101 && st.ownItemCount(q_gloves_of_native) < 1) {
                    st.getPlayer().stopTransformation();
                    htmltext = "secret_agent_colin_q0194_11c.htm";
                }
            } else if (event.equalsIgnoreCase("reply_13")) {
                if (GetMemoState == 11) {
                    if (st.ownItemCount(q_gloves_of_native) >= 1) {
                        st.setCond(12);
                        st.setMemoState("ssq_contract_of_mammon", String.valueOf(12), true);
                        st.takeItems(q_gloves_of_native, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "secret_agent_colin_q0194_11e.htm";
                        if (talker_transformID == 101)
                            st.getPlayer().stopTransformation();
                    }
                }
            }
        } else if (npcId == king_frog) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 4) {
                    if (talker_transformID == 111)
                        htmltext = "king_frog_q0194_02.htm";
                    else
                        htmltext = "king_frog_q0194_05.htm";
                }
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 4) {
                    if (talker_transformID == 111)
                        htmltext = "king_frog_q0194_03.htm";
                    else
                        htmltext = "king_frog_q0194_05.htm";
                }
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 4) {
                    if (talker_transformID == 111) {
                        st.setCond(5);
                        st.setMemoState("ssq_contract_of_mammon", String.valueOf(5), true);
                        st.giveItems(q_orb_of_king_frog, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "king_frog_q0194_04.htm";
                    } else
                        htmltext = "king_frog_q0194_05.htm";
                }
            }
        } else if (npcId == grandmother_tess) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 7) {
                    if (talker_transformID == 112)
                        htmltext = "grandmother_tess_q0194_02.htm";
                    else
                        htmltext = "grandmother_tess_q0194_04.htm";
                }
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 7) {
                    if (talker_transformID == 112) {
                        st.setCond(8);
                        st.setMemoState("ssq_contract_of_mammon", String.valueOf(8), true);
                        st.giveItems(q_candy_sac, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "grandmother_tess_q0194_03.htm";
                    } else
                        htmltext = "grandmother_tess_q0194_04.htm";
                }
            }
        } else if (npcId == native_kuda) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 10) {
                    if (talker_transformID == 101)
                        htmltext = "native_kuda_q0194_02.htm";
                    else
                        htmltext = "native_kuda_q0194_05.htm";
                }
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 10) {
                    if (talker_transformID == 101)
                        htmltext = "native_kuda_q0194_03.htm";
                    else
                        htmltext = "native_kuda_q0194_05.htm";
                }
            }
            if (GetMemoState == 10) {
                if (talker_transformID == 101) {
                    st.setCond(11);
                    st.setMemoState("ssq_contract_of_mammon", String.valueOf(11), true);
                    st.giveItems(q_gloves_of_native, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "native_kuda_q0194_04.htm";
                } else
                    htmltext = "native_kuda_q0194_05.htm";
            }
        } else if (npcId == claudia_a) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 12)
                    htmltext = "claudia_a_q0194_10.htm";
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 12) {
                    if (st.getPlayer().getLevel() >= 79) {
                        st.addExpAndSp(52518015, 5817677);
                        st.removeMemo("ssq_contract_of_mammon");
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                        htmltext = "claudia_a_q0194_11.htm";
                    } else
                        htmltext = "level_check_q0192_01.htm";
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int talker_transformID = st.getPlayer().getTransformationId();
        int GetMemoState = st.getInt("ssq_contract_of_mammon");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == sir_gustaf_athebaldt) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "sir_gustaf_athebaldt_q0194_03.htm";
                            st.exitQuest(true);
                            break;
                        case QUEST:
                            htmltext = "sir_gustaf_athebaldt_q0194_03.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            if (!st.getPlayer().getPlayerClassComponent().isBaseExactlyActiveId()) {
                                htmltext = "subclass_forbidden.htm";
                                st.exitQuest(true);
                            } else
                                htmltext = "sir_gustaf_athebaldt_q0194_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == sir_gustaf_athebaldt) {
                    if (GetMemoState == 1)
                        htmltext = "sir_gustaf_athebaldt_q0194_05.htm";
                    else if (GetMemoState == 2)
                        htmltext = "sir_gustaf_athebaldt_q0194_10.htm";
                    else if (GetMemoState == 3) {
                        if (st.ownItemCount(q_voucher_of_atebalt) >= 1)
                            htmltext = "sir_gustaf_athebaldt_q0194_13.htm";
                    }
                } else if (npcId == isecret_agent_colin) {
                    if (GetMemoState < 3)
                        htmltext = "secret_agent_colin_q0194_01.htm";
                    else if (GetMemoState == 3) {
                        if (st.ownItemCount(q_voucher_of_atebalt) >= 1)
                            htmltext = "secret_agent_colin_q0194_02.htm";
                    } else if (GetMemoState == 4 && talker_transformID != 111 && st.ownItemCount(q_orb_of_king_frog) < 1)
                        htmltext = "secret_agent_colin_q0194_05.htm";
                    else if (GetMemoState == 4 && talker_transformID == 111 && st.ownItemCount(q_orb_of_king_frog) < 1)
                        htmltext = "secret_agent_colin_q0194_05b.htm";
                    else if (GetMemoState == 5) {
                        if (st.ownItemCount(q_orb_of_king_frog) >= 1)
                            htmltext = "secret_agent_colin_q0194_05d.htm";
                    } else if (GetMemoState == 6)
                        htmltext = "secret_agent_colin_q0194_06.htm";
                    else if (GetMemoState == 7 && talker_transformID != 112 && st.ownItemCount(q_candy_sac) < 1)
                        htmltext = "secret_agent_colin_q0194_08.htm";
                    else if (GetMemoState == 7 && talker_transformID == 112 && st.ownItemCount(q_candy_sac) < 1)
                        htmltext = "secret_agent_colin_q0194_08b.htm";
                    else if (GetMemoState == 8) {
                        if (st.ownItemCount(q_candy_sac) >= 1)
                            htmltext = "secret_agent_colin_q0194_08d.htm";
                    } else if (GetMemoState == 9)
                        htmltext = "secret_agent_colin_q0194_09.htm";
                    else if (GetMemoState == 10 && talker_transformID != 101 && st.ownItemCount(q_gloves_of_native) < 1)
                        htmltext = "secret_agent_colin_q0194_11.htm";
                    else if (GetMemoState == 10 && talker_transformID == 101 && st.ownItemCount(q_gloves_of_native) < 1)
                        htmltext = "secret_agent_colin_q0194_11b.htm";
                    else if (GetMemoState == 11) {
                        if (st.ownItemCount(q_gloves_of_native) >= 1)
                            htmltext = "secret_agent_colin_q0194_11d.htm";
                    } else if (GetMemoState == 12)
                        htmltext = "secret_agent_colin_q0194_12.htm";
                } else if (npcId == king_frog) {
                    if (GetMemoState == 4) {
                        if (talker_transformID == 111)
                            htmltext = "king_frog_q0194_01.htm";
                        else
                            htmltext = "king_frog_q0194_05.htm";
                    } else if (GetMemoState == 5) {
                        if (st.ownItemCount(q_orb_of_king_frog) >= 1 && talker_transformID == 111)
                            htmltext = "king_frog_q0194_06.htm";
                    } else if (GetMemoState < 4)
                        htmltext = "king_frog_q0194_07.htm";
                } else if (npcId == grandmother_tess) {
                    if (GetMemoState == 7) {
                        if (talker_transformID == 112)
                            htmltext = "grandmother_tess_q0194_01.htm";
                        else
                            htmltext = "grandmother_tess_q0194_04.htm";
                    } else if (GetMemoState == 8) {
                        if (st.ownItemCount(q_candy_sac) >= 1 && talker_transformID == 112)
                            htmltext = "grandmother_tess_q0194_05.htm";
                    } else if (GetMemoState < 7)
                        htmltext = "grandmother_tess_q0194_06.htm";
                } else if (npcId == native_kuda) {
                    if (GetMemoState == 10) {
                        if (talker_transformID == 101)
                            htmltext = "native_kuda_q0194_01.htm";
                        else
                            htmltext = "native_kuda_q0194_05.htm";
                    } else if (GetMemoState == 10 && st.ownItemCount(q_gloves_of_native) >= 1 && talker_transformID == 101)
                        htmltext = "native_kuda_q0194_06.htm";
                    else if (GetMemoState < 10)
                        htmltext = "native_kuda_q0194_07.htm";
                } else if (npcId == claudia_a) {
                    if (GetMemoState == 12)
                        htmltext = "claudia_a_q0194_13.htm";
                }
                break;
        }
        return htmltext;
    }

    private void negateSpeedBuffs(Player p) {
        p.getEffectList().getAllEffects().stream().filter(e -> e.getStackType().equalsIgnoreCase("SpeedUp") && !e.isOffensive()).forEach(org.mmocore.gameserver.model.Effect::exit);
    }
}
