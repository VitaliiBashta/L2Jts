package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.model.base.ClassId;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.serverpackets.SocialAction;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.1
 * @date 04/10/2015
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _409_PathOfTheElvenOracle extends Quest {
    // npc
    private final static int father_manuell = 30293;
    private final static int allana = 30424;
    private final static int perrin = 30428;
    // mobs
    private final static int q409_lizardman_warrior = 27032;
    private final static int q409_lizardman_scout = 27033;
    private final static int q409_lizardman = 27034;
    private final static int tamato = 27035;
    // questitem
    private final static int crystal_medallion = 1231;
    private final static int money_of_swindler = 1232;
    private final static int dairy_of_allana = 1233;
    private final static int lizard_captain_order = 1234;
    private final static int leaf_of_oracle = 1235;
    private final static int half_of_dairy = 1236;
    private final static int tamatos_necklace = 1275;

    public _409_PathOfTheElvenOracle() {
        super(false);
        addStartNpc(father_manuell);
        addTalkId(allana, perrin);
        addKillId(q409_lizardman_warrior, q409_lizardman_scout, q409_lizardman, tamato);
        addQuestItem(crystal_medallion, money_of_swindler, dairy_of_allana, lizard_captain_order, half_of_dairy, tamatos_necklace);
        addLevelCheck(18);
        addClassIdCheck(ClassId.elven_mage);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("path_to_oracle");
        int npcId = npc.getNpcId();
        int talker_occupation = st.getPlayer().getPlayerClassComponent().getClassId().getId();
        int elven_mage = 0x19;
        int oracle = 0x1d;
        if (npcId == father_manuell) {
            if (event.equalsIgnoreCase("quest_accept")) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = "father_manuell_q0409_03.htm";
                        st.exitQuest(true);
                        break;
                    case CLASS_ID:
                        if (talker_occupation != elven_mage) {
                            if (talker_occupation == oracle) {
                                htmltext = "father_manuell_q0409_02a.htm";
                                st.exitQuest(true);
                            } else {
                                htmltext = "father_manuell_q0409_02.htm";
                                st.exitQuest(true);
                            }
                        }
                        break;
                    default:
                        if (st.ownItemCount(leaf_of_oracle) > 0 && talker_occupation == elven_mage)
                            htmltext = "father_manuell_q0409_04.htm";
                        else {
                            st.setCond(1);
                            st.setMemoState("path_to_oracle", String.valueOf(1), true);
                            st.setState(STARTED);
                            st.soundEffect(SOUND_ACCEPT);
                            st.giveItems(crystal_medallion, 1);
                            htmltext = "father_manuell_q0409_05.htm";
                        }
                        break;
                }
            }
        } else if (npcId == allana) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 1)
                    htmltext = "allana_q0409_07.htm";
            } else if (event.equalsIgnoreCase("reply_2"))
                htmltext = "allana_q0409_08.htm";
            else if (event.equalsIgnoreCase("reply_3"))
                htmltext = "allana_q0409_09.htm";
            else if (event.equalsIgnoreCase("reply_4")) {
                st.addSpawn(q409_lizardman_warrior);
                st.addSpawn(q409_lizardman_scout);
                st.addSpawn(q409_lizardman);
                st.setMemoState("path_to_oracle", String.valueOf(2), true);
                return null;
            }
        } else if (npcId == perrin) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 2)
                    htmltext = "perrin_q0409_02.htm";
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 2)
                    htmltext = "perrin_q0409_03.htm";
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 2) {
                    st.addSpawn(tamato);
                    st.setMemoState("path_to_oracle", String.valueOf(3), true);
                    return null;
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        Player player = st.getPlayer();
        int GetMemoState = st.getInt("path_to_oracle");
        int talker_level = st.getPlayer().getLevel();
        int GetOneTimeQuestFlag = st.getPlayer().getPlayerVariables().getInt(PlayerVariables.profession_145);
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == father_manuell) {
                    if (st.ownItemCount(leaf_of_oracle) == 0)
                        htmltext = "father_manuell_q0409_01.htm";
                    else
                        htmltext = "father_manuell_q0409_04.htm";
                }
                break;
            case STARTED:
                if (npcId == father_manuell) {
                    if (st.ownItemCount(crystal_medallion) > 0) {
                        if (st.ownItemCount(money_of_swindler) == 0 && st.ownItemCount(dairy_of_allana) == 0 && st.ownItemCount(lizard_captain_order) == 0 && st.ownItemCount(half_of_dairy) == 0) {
                            if (GetMemoState == 2) {
                                st.setCond(8);
                                st.setMemoState("path_to_oracle", String.valueOf(1), true);
                                htmltext = "father_manuell_q0409_09.htm";
                            } else {
                                st.setMemoState("path_to_oracle", String.valueOf(1), true);
                                htmltext = "father_manuell_q0409_06.htm";
                            }
                        } else if (st.ownItemCount(money_of_swindler) == 1 && st.ownItemCount(dairy_of_allana) == 1 && st.ownItemCount(lizard_captain_order) == 1 && st.ownItemCount(half_of_dairy) == 0) {
                            st.takeItems(money_of_swindler, 1);
                            st.takeItems(dairy_of_allana, 1);
                            st.takeItems(lizard_captain_order, 1);
                            st.takeItems(crystal_medallion, 1);
                            st.giveItems(leaf_of_oracle, 1);
                            htmltext = "father_manuell_q0409_08.htm";
                            if (GetOneTimeQuestFlag == 0) {
                                st.getPlayer().getPlayerVariables().set(PlayerVariables.profession_145, String.valueOf(1), -1);
                                if (talker_level >= 20)
                                    st.addExpAndSp(320534, 20392);
                                else if (talker_level == 19)
                                    st.addExpAndSp(456128, 27090);
                                else
                                    st.addExpAndSp(591724, 33788);
                                st.giveItems(ADENA_ID, 163800);
                            }
                            player.sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                            st.removeMemo("path_to_oracle");
                            st.soundEffect(SOUND_FINISH);
                            st.exitQuest(false);
                        } else
                            htmltext = "father_manuell_q0409_07.htm";
                    }
                } else if (npcId == allana) {
                    if (st.ownItemCount(crystal_medallion) > 0) {
                        if (st.ownItemCount(money_of_swindler) == 0 && st.ownItemCount(dairy_of_allana) == 0 && st.ownItemCount(lizard_captain_order) == 0 && st.ownItemCount(half_of_dairy) == 0) {
                            if (GetMemoState == 2)
                                htmltext = "allana_q0409_05.htm";
                            else if (GetMemoState == 1) {
                                st.setCond(2);
                                st.soundEffect(SOUND_MIDDLE);
                                htmltext = "allana_q0409_01.htm";
                            }
                        } else if (st.ownItemCount(money_of_swindler) == 0 && st.ownItemCount(dairy_of_allana) == 0 && st.ownItemCount(lizard_captain_order) == 1 && st.ownItemCount(half_of_dairy) == 0) {
                            st.setCond(4);
                            st.setMemoState("path_to_oracle", String.valueOf(2), true);
                            st.giveItems(half_of_dairy, 1);
                            st.soundEffect(SOUND_MIDDLE);
                            htmltext = "allana_q0409_02.htm";
                        } else if (st.ownItemCount(money_of_swindler) == 0 && st.ownItemCount(dairy_of_allana) == 0 && st.ownItemCount(lizard_captain_order) == 1 && st.ownItemCount(half_of_dairy) == 1) {
                            if (GetMemoState == 3 && st.ownItemCount(tamatos_necklace) == 0) {
                                st.setCond(4);
                                st.setMemoState("path_to_oracle", String.valueOf(2), true);
                                st.soundEffect(SOUND_MIDDLE);
                                htmltext = "allana_q0409_06.htm";
                            } else
                                htmltext = "allana_q0409_03.htm";
                        } else if (st.ownItemCount(money_of_swindler) == 1 && st.ownItemCount(dairy_of_allana) == 0 && st.ownItemCount(lizard_captain_order) == 1 && st.ownItemCount(half_of_dairy) == 1) {
                            st.setCond(9);
                            st.takeItems(half_of_dairy, 1);
                            st.giveItems(dairy_of_allana, 1);
                            st.soundEffect(SOUND_MIDDLE);
                            htmltext = "allana_q0409_04.htm";
                        } else if (st.ownItemCount(money_of_swindler) == 1 && st.ownItemCount(lizard_captain_order) == 1 && st.ownItemCount(half_of_dairy) == 0 && st.ownItemCount(dairy_of_allana) > 0) {
                            st.setCond(7);
                            st.soundEffect(SOUND_MIDDLE);
                            htmltext = "allana_q0409_05.htm";
                        }
                    }
                } else if (npcId == perrin) {
                    if (st.ownItemCount(crystal_medallion) > 0 && st.ownItemCount(lizard_captain_order) > 0 && st.ownItemCount(half_of_dairy) > 0) {
                        if (st.ownItemCount(tamatos_necklace) == 1) {
                            st.setCond(6);
                            st.soundEffect(SOUND_MIDDLE);
                            st.takeItems(tamatos_necklace, 1);
                            st.giveItems(money_of_swindler, 1);
                            htmltext = "perrin_q0409_04.htm";
                        } else if (st.ownItemCount(money_of_swindler) > 0)
                            htmltext = "perrin_q0409_05.htm";
                        else if (GetMemoState == 3)
                            htmltext = "perrin_q0409_06.htm";
                        else
                            htmltext = "perrin_q0409_01.htm";
                    }
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onAttack(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == q409_lizardman_warrior)
            Functions.npcSay(npc, NpcString.THE_SACRED_FLAME_IS_OURS);
        else if (npcId == q409_lizardman_scout)
            Functions.npcSay(npc, NpcString.THE_SACRED_FLAME_IS_OURS);
        else if (npcId == q409_lizardman)
            Functions.npcSay(npc, NpcString.THE_SACRED_FLAME_IS_OURS);
        else if (npcId == tamato)
            Functions.npcSay(npc, NpcString.AS_YOU_WISH_MASTER);
        return null;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == q409_lizardman_warrior || npcId == q409_lizardman_scout || npcId == q409_lizardman) {
            if (st.ownItemCount(lizard_captain_order) == 0) {
                if (npcId == q409_lizardman_warrior)
                    Functions.npcSay(npc, NpcString.ARRGHH_WE_SHALL_NEVER_SURRENDER);
                st.giveItems(lizard_captain_order, 1);
                st.soundEffect(SOUND_MIDDLE);
                st.setCond(3);
            }
        } else if (npcId == tamato) {
            if (st.ownItemCount(tamatos_necklace) == 0) {
                st.giveItems(tamatos_necklace, 1);
                st.soundEffect(SOUND_MIDDLE);
                st.setCond(5);
            }
        }
        return null;
    }
}