package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _125_TheNameOfEvil1 extends Quest {
    // npc
    private final static int mushika = 32114;
    private final static int shaman_caracawe = 32117;
    private final static int ulu_kaimu_stone = 32119;
    private final static int balu_kaimu_stone = 32120;
    private final static int jiuta_kaimu_stone = 32121;

    // mobs
    private final static int ornithomimus_leader = 22200;
    private final static int ornithomimus = 22201;
    private final static int ornithomimus_s = 22202;
    private final static int deinonychus_leader = 22203;
    private final static int deinonychus = 22204;
    private final static int deinonychus_s = 22205;
    private final static int ornithomimus_n = 22219;
    private final static int deinonychus_n = 22220;
    private final static int ornithomimus_leader2 = 22224;
    private final static int deinonychus_leader2 = 22225;

    // questitem
    private final static int q_claw_of_ornithomimus = 8779;
    private final static int q_bone_of_deinonychus = 8780;
    private final static int q_muzzle_pattem = 8781;
    private final static int q_piece_of_gazk = 8782;

    public _125_TheNameOfEvil1() {
        super(false);
        addStartNpc(mushika);
        addTalkId(shaman_caracawe, ulu_kaimu_stone, balu_kaimu_stone, jiuta_kaimu_stone);
        addKillId(ornithomimus_leader, ornithomimus, ornithomimus_s, deinonychus_leader, deinonychus, deinonychus_s, ornithomimus_n, deinonychus_n, ornithomimus_leader2, deinonychus_leader2);
        addQuestItem(q_claw_of_ornithomimus, q_bone_of_deinonychus);
        addLevelCheck(76);
        addQuestCompletedCheck(124);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("name_of_cruel_god_one");
        int GetMemoStateEx = st.getInt("name_of_cruel_god_one_ex");

        if (event.equalsIgnoreCase("quest_accept")) {
            st.setCond(1);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            htmltext = "mushika_q0125_08.htm";
        } else if (event.equalsIgnoreCase("reply_5")) {
            st.setMemoState("name_of_cruel_god_one", String.valueOf(1), true);
            htmltext = "mushika_q0125_11.htm";
        } else if (event.equalsIgnoreCase("reply_6") && GetMemoState == 1) {
            st.setCond(2);
            st.setMemoState("name_of_cruel_god_one", String.valueOf(2), true);
            st.giveItems(q_piece_of_gazk, 1);
            st.soundEffect(SOUND_MIDDLE);
            htmltext = "mushika_q0125_12.htm";
        } else if (event.equalsIgnoreCase("reply_13") && GetMemoState == 2) {
            st.setCond(3);
            st.setMemoState("name_of_cruel_god_one", String.valueOf(3), true);
            st.soundEffect(SOUND_MIDDLE);
            htmltext = "shaman_caracawe_q0125_09.htm";
        } else if (event.equalsIgnoreCase("reply_17") && GetMemoState == 4) {
            st.setCond(5);
            st.setMemoState("name_of_cruel_god_one", String.valueOf(5), true);
            st.soundEffect(SOUND_MIDDLE);
            htmltext = "shaman_caracawe_q0125_17.htm";
        } else if (event.equalsIgnoreCase("reply_19") && GetMemoState == 5) {
            st.setMemoState("name_of_cruel_god_one_ex", String.valueOf(0), true);
            htmltext = "ulu_kaimu_stone_q0125_04.htm";
        } else if (event.equalsIgnoreCase("reply_1") && GetMemoState == 5) {
            st.setMemoState("name_of_cruel_god_one_ex", String.valueOf(GetMemoStateEx + 1), true);
            htmltext = "ulu_kaimu_stone_q0125_05.htm";
        } else if (event.equalsIgnoreCase("reply_2") && GetMemoState == 5) {
            st.setMemoState("name_of_cruel_god_one_ex", String.valueOf(GetMemoStateEx + 10), true);
            htmltext = "ulu_kaimu_stone_q0125_06.htm";
        } else if (event.equalsIgnoreCase("reply_3") && GetMemoState == 5) {
            st.setMemoState("name_of_cruel_god_one_ex", String.valueOf(GetMemoStateEx + 100), true);
            htmltext = "ulu_kaimu_stone_q0125_07.htm";
        } else if (event.equalsIgnoreCase("reply_4") && GetMemoState == 5) {
            if (GetMemoStateEx != 111)
                htmltext = "ulu_kaimu_stone_q0125_08.htm";
            else {
                st.setMemoState("name_of_cruel_god_one_ex", String.valueOf(0), true);
                st.setMemoState("name_of_cruel_god_one", String.valueOf(6), true);
                htmltext = "ulu_kaimu_stone_q0125_09.htm";
            }
        } else if (event.equalsIgnoreCase("reply_26") && GetMemoState == 6) {
            st.setCond(6);
            st.setMemoState("name_of_cruel_god_one", String.valueOf(7), true);
            st.soundEffect(SOUND_MIDDLE);
            htmltext = "ulu_kaimu_stone_q0125_20.htm";
        } else if (event.equalsIgnoreCase("reply_27") && GetMemoState == 7) {
            st.setMemoState("name_of_cruel_god_one_ex", String.valueOf(0), true);
            htmltext = "balu_kaimu_stone_q0125_04.htm";
        } else if (event.equalsIgnoreCase("reply_1a") && GetMemoState == 7) {
            st.setMemoState("name_of_cruel_god_one_ex", String.valueOf(GetMemoStateEx + 1), true);
            htmltext = "balu_kaimu_stone_q0125_05.htm";
        } else if (event.equalsIgnoreCase("reply_2a") && GetMemoState == 7) {
            st.setMemoState("name_of_cruel_god_one_ex", String.valueOf(GetMemoStateEx + 10), true);
            htmltext = "balu_kaimu_stone_q0125_06.htm";
        } else if (event.equalsIgnoreCase("reply_3a") && GetMemoState == 7) {
            st.setMemoState("name_of_cruel_god_one_ex", String.valueOf(GetMemoStateEx + 100), true);
            htmltext = "balu_kaimu_stone_q0125_07.htm";
        } else if (event.equalsIgnoreCase("reply_4a") && GetMemoState == 7) {
            if (GetMemoStateEx != 111)
                htmltext = "balu_kaimu_stone_q0125_08.htm";
            else {
                st.setMemoState("name_of_cruel_god_one_ex", String.valueOf(0), true);
                st.setMemoState("name_of_cruel_god_one", String.valueOf(8), true);
                htmltext = "balu_kaimu_stone_q0125_09.htm";
            }
        } else if (event.equalsIgnoreCase("reply_34") && GetMemoState == 8) {
            st.setCond(7);
            st.setMemoState("name_of_cruel_god_one", String.valueOf(9), true);
            st.soundEffect(SOUND_MIDDLE);
            htmltext = "balu_kaimu_stone_q0125_19.htm";
        } else if (event.equalsIgnoreCase("reply_35") && GetMemoState == 9) {
            st.setMemoState("name_of_cruel_god_one_ex", String.valueOf(0), true);
            htmltext = "jiuta_kaimu_stone_q0125_04.htm";
        } else if (event.equalsIgnoreCase("reply_1b") && GetMemoState == 9) {
            st.setMemoState("name_of_cruel_god_one_ex", String.valueOf(GetMemoStateEx + 1), true);
            htmltext = "jiuta_kaimu_stone_q0125_05.htm";
        } else if (event.equalsIgnoreCase("reply_2b") && GetMemoState == 9) {
            st.setMemoState("name_of_cruel_god_one_ex", String.valueOf(GetMemoStateEx + 10), true);
            htmltext = "jiuta_kaimu_stone_q0125_06.htm";
        } else if (event.equalsIgnoreCase("reply_3b") && GetMemoState == 9) {
            st.setMemoState("name_of_cruel_god_one_ex", String.valueOf(GetMemoStateEx + 100), true);
            htmltext = "jiuta_kaimu_stone_q0125_07.htm";
        } else if (event.equalsIgnoreCase("reply_4b") && GetMemoState == 9) {
            if (GetMemoStateEx != 111)
                htmltext = "jiuta_kaimu_stone_q0125_08.htm";
            else {
                st.setMemoState("name_of_cruel_god_one_ex", String.valueOf(0), true);
                st.setMemoState("name_of_cruel_god_one", String.valueOf(10), true);
                htmltext = "jiuta_kaimu_stone_q0125_09.htm";
            }
        } else if (event.equalsIgnoreCase("reply_38") && GetMemoState == 10) {
            st.setMemoState("name_of_cruel_god_one", String.valueOf(11), true);
            htmltext = "jiuta_kaimu_stone_q0125_13.htm";
        } else if (event.equalsIgnoreCase("reply_41") && GetMemoState == 11 && st.ownItemCount(q_piece_of_gazk) >= 1) {
            st.setMemoState("name_of_cruel_god_one", String.valueOf(12), true);
            htmltext = "jiuta_kaimu_stone_q0125_19.htm";
        } else if (event.equalsIgnoreCase("reply_42")) {
            st.setMemoState("name_of_cruel_god_one", String.valueOf(13), true);
            htmltext = "jiuta_kaimu_stone_q0125_21.htm";
        } else if (event.equalsIgnoreCase("reply_43") && GetMemoState == 13 && st.ownItemCount(q_piece_of_gazk) >= 1) {
            st.setCond(8);
            st.setMemoState("name_of_cruel_god_one", String.valueOf(14), true);
            st.giveItems(q_muzzle_pattem, 1);
            st.takeItems(q_piece_of_gazk, -1);
            st.soundEffect(SOUND_MIDDLE);
            htmltext = "jiuta_kaimu_stone_q0125_23.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("name_of_cruel_god_one");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == mushika) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "mushika_q0125_02.htm";
                            st.exitQuest(true);
                            break;
                        case QUEST:
                            htmltext = "mushika_q0125_04.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "mushika_q0125_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == mushika) {
                    if (GetMemoState < 1)
                        htmltext = "mushika_q0125_09.htm";
                    else if (GetMemoState == 1)
                        htmltext = "mushika_q0125_11a.htm";
                    else if (GetMemoState == 2)
                        htmltext = "mushika_q0125_13.htm";
                    else if (GetMemoState >= 3 && GetMemoState <= 13)
                        htmltext = "mushika_q0125_14.htm";
                    else if (GetMemoState == 14 && st.ownItemCount(q_muzzle_pattem) >= 1) {
                        st.addExpAndSp(859195, 86603);
                        st.takeItems(q_muzzle_pattem, -1);
                        st.removeMemo("name_of_cruel_god_one");
                        st.removeMemo("name_of_cruel_god_one_ex");
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                        htmltext = "mushika_q0125_15.htm";
                    }
                } else if (npcId == shaman_caracawe) {
                    if (GetMemoState == 2)
                        htmltext = "shaman_caracawe_q0125_01.htm";
                    else if (GetMemoState < 2)
                        htmltext = "shaman_caracawe_q0125_02.htm";
                    else if (GetMemoState == 3 && st.ownItemCount(q_claw_of_ornithomimus) >= 2 && st.ownItemCount(q_bone_of_deinonychus) >= 2) {
                        st.takeItems(q_claw_of_ornithomimus, -1);
                        st.takeItems(q_bone_of_deinonychus, -1);
                        st.setMemoState("name_of_cruel_god_one", String.valueOf(4), true);
                        htmltext = "shaman_caracawe_q0125_11.htm";
                    } else if (GetMemoState == 3 && (st.ownItemCount(q_claw_of_ornithomimus) < 2 || st.ownItemCount(q_bone_of_deinonychus) < 2))
                        htmltext = "shaman_caracawe_q0125_12.htm";
                    else if (GetMemoState == 4)
                        htmltext = "shaman_caracawe_q0125_14.htm";
                    else if (GetMemoState == 5)
                        htmltext = "shaman_caracawe_q0125_18.htm";
                    else if (GetMemoState >= 6 && GetMemoState < 13)
                        htmltext = "shaman_caracawe_q0125_19.htm";
                    else if (GetMemoState == 14 && st.ownItemCount(q_muzzle_pattem) >= 1)
                        htmltext = "shaman_caracawe_q0125_20.htm";
                } else if (npcId == ulu_kaimu_stone) {
                    if (GetMemoState == 5) {
                        npc.doCast(SkillTable.getInstance().getSkillEntry(5089, 1), st.getPlayer(), true);
                        htmltext = "ulu_kaimu_stone_q0125_01.htm";
                    } else if (GetMemoState < 5)
                        htmltext = "ulu_kaimu_stone_q0125_02.htm";
                    else if (GetMemoState > 7)
                        htmltext = "ulu_kaimu_stone_q0125_03.htm";
                    else if (GetMemoState == 6)
                        htmltext = "ulu_kaimu_stone_q0125_11.htm";
                    else if (GetMemoState == 7)
                        htmltext = "ulu_kaimu_stone_q0125_21.htm";
                } else if (npcId == balu_kaimu_stone) {
                    if (GetMemoState == 7) {
                        npc.doCast(SkillTable.getInstance().getSkillEntry(5089, 1), st.getPlayer(), true);
                        htmltext = "balu_kaimu_stone_q0125_01.htm";
                    } else if (GetMemoState < 7)
                        htmltext = "balu_kaimu_stone_q0125_02.htm";
                    else if (GetMemoState > 9)
                        htmltext = "balu_kaimu_stone_q0125_03.htm";
                    else if (GetMemoState == 8)
                        htmltext = "balu_kaimu_stone_q0125_11.htm";
                    else if (GetMemoState == 9)
                        htmltext = "balu_kaimu_stone_q0125_20.htm";
                } else if (npcId == jiuta_kaimu_stone)
                    if (GetMemoState == 9) {
                        npc.doCast(SkillTable.getInstance().getSkillEntry(5089, 1), st.getPlayer(), true);
                        htmltext = "jiuta_kaimu_stone_q0125_01.htm";
                    } else if (GetMemoState < 9)
                        htmltext = "jiuta_kaimu_stone_q0125_02.htm";
                    else if (GetMemoState > 14)
                        htmltext = "jiuta_kaimu_stone_q0125_03.htm";
                    else if (GetMemoState == 10)
                        htmltext = "jiuta_kaimu_stone_q0125_11.htm";
                    else if (GetMemoState == 11)
                        htmltext = "jiuta_kaimu_stone_q0125_14.htm";
                    else if (GetMemoState == 12 && st.ownItemCount(q_piece_of_gazk) >= 1)
                        htmltext = "jiuta_kaimu_stone_q0125_20.htm";
                    else if (GetMemoState == 13 && st.ownItemCount(q_piece_of_gazk) >= 1)
                        htmltext = "jiuta_kaimu_stone_q0125_22.htm";
                    else if (GetMemoState == 14 && st.ownItemCount(q_muzzle_pattem) >= 1)
                        htmltext = "jiuta_kaimu_stone_q0125_24.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("name_of_cruel_god_one");
        int npcId = npc.getNpcId();

        if (GetMemoState == 3)
            if (npcId == ornithomimus_leader || npcId == ornithomimus_s) {
                int i4 = Rnd.get(1000);
                if (i4 < 661 && st.ownItemCount(q_claw_of_ornithomimus) <= 1)
                    if (st.ownItemCount(q_claw_of_ornithomimus) < 1) {
                        st.giveItems(q_claw_of_ornithomimus, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    } else if (st.ownItemCount(q_claw_of_ornithomimus) >= 1) {
                        st.giveItems(q_claw_of_ornithomimus, 1);
                        if (st.ownItemCount(q_bone_of_deinonychus) >= 2) {
                            st.setCond(4);
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    }
            } else if (npcId == ornithomimus) {
                int i4 = Rnd.get(1000);
                if (i4 < 330 && st.ownItemCount(q_claw_of_ornithomimus) <= 1)
                    if (st.ownItemCount(q_claw_of_ornithomimus) < 1) {
                        st.giveItems(q_claw_of_ornithomimus, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    } else if (st.ownItemCount(q_claw_of_ornithomimus) >= 1) {
                        st.giveItems(q_claw_of_ornithomimus, 1);
                        if (st.ownItemCount(q_bone_of_deinonychus) >= 2) {
                            st.setCond(4);
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    }
            } else if (npcId == deinonychus_leader || npcId == deinonychus_s) {
                int i4 = Rnd.get(1000);
                if (i4 < 651 && st.ownItemCount(q_bone_of_deinonychus) <= 1)
                    if (st.ownItemCount(q_bone_of_deinonychus) < 1) {
                        st.giveItems(q_bone_of_deinonychus, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    } else if (st.ownItemCount(q_bone_of_deinonychus) >= 1) {
                        st.giveItems(q_bone_of_deinonychus, 1);
                        if (st.ownItemCount(q_claw_of_ornithomimus) >= 2) {
                            st.setCond(4);
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    }
            } else if (npcId == deinonychus) {
                int i4 = Rnd.get(1000);
                if (i4 < 326 && st.ownItemCount(q_bone_of_deinonychus) <= 1)
                    if (st.ownItemCount(q_bone_of_deinonychus) < 1) {
                        st.giveItems(q_bone_of_deinonychus, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    } else if (st.ownItemCount(q_bone_of_deinonychus) >= 1) {
                        st.giveItems(q_bone_of_deinonychus, 1);
                        if (st.ownItemCount(q_claw_of_ornithomimus) >= 2) {
                            st.setCond(4);
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    }
            } else if (npcId == ornithomimus_n || npcId == ornithomimus_leader2) {
                int i4 = Rnd.get(1000);
                if (i4 < 327 && st.ownItemCount(q_claw_of_ornithomimus) <= 1)
                    if (st.ownItemCount(q_claw_of_ornithomimus) < 1) {
                        st.giveItems(q_claw_of_ornithomimus, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    } else if (st.ownItemCount(q_claw_of_ornithomimus) >= 1) {
                        st.giveItems(q_claw_of_ornithomimus, 1);
                        if (st.ownItemCount(q_bone_of_deinonychus) >= 2) {
                            st.setCond(4);
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    }
            } else if (npcId == deinonychus_n || npcId == deinonychus_leader2) {
                int i4 = Rnd.get(1000);
                if (i4 < 319 && st.ownItemCount(q_bone_of_deinonychus) <= 1)
                    if (st.ownItemCount(q_bone_of_deinonychus) < 1) {
                        st.giveItems(q_bone_of_deinonychus, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    } else if (st.ownItemCount(q_bone_of_deinonychus) >= 1) {
                        st.giveItems(q_bone_of_deinonychus, 1);
                        if (st.ownItemCount(q_claw_of_ornithomimus) >= 2) {
                            st.setCond(4);
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    }
            }
        return null;
    }
}