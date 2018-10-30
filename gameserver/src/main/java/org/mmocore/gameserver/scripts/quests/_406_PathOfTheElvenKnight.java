package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.base.ClassId;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
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
public class _406_PathOfTheElvenKnight extends Quest {
    // npc
    private final static int master_sorius = 30327;
    private final static int blacksmith_kluto = 30317;
    // mobs
    private final static int tracker_skeleton = 20035;
    private final static int tracker_skeleton_leader = 20042;
    private final static int scout_skeleton = 20045;
    private final static int sniper_skeleton = 20051;
    private final static int ruin_spartoi = 20054;
    private final static int raging_spartoi = 20060;
    private final static int ol_mahum_rookie = 20782;
    // questitem
    private final static int sorius_letter1 = 1202;
    private final static int kluto_box = 1203;
    private final static int elven_knight_brooch = 1204;
    private final static int topaz_piece = 1205;
    private final static int emerald_piece = 1206;
    private final static int kluto_memo = 1276;

    public _406_PathOfTheElvenKnight() {
        super(false);
        addStartNpc(master_sorius);
        addTalkId(blacksmith_kluto);
        addKillId(tracker_skeleton, tracker_skeleton_leader, scout_skeleton, sniper_skeleton, ruin_spartoi, raging_spartoi, ol_mahum_rookie);
        addQuestItem(sorius_letter1, kluto_box, topaz_piece, emerald_piece, kluto_memo);
        addLevelCheck(18);
        addClassIdCheck(ClassId.elven_fighter);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        int talker_occupation = st.getPlayer().getPlayerClassComponent().getClassId().getId();
        int elven_fighter = 0x12;
        int elven_knight = 0x13;
        if (npcId == master_sorius) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "master_sorius_q0406_06.htm";
            } else if (event.equalsIgnoreCase("reply_1")) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = "master_sorius_q0406_03.htm";
                        st.exitQuest(true);
                        break;
                    case CLASS_ID:
                        if (talker_occupation != elven_fighter) {
                            if (talker_occupation == elven_knight) {
                                htmltext = "master_sorius_q0406_02a.htm";
                                st.exitQuest(true);
                            } else {
                                htmltext = "master_sorius_q0406_02.htm";
                                st.exitQuest(true);
                            }
                        }
                        break;
                    default:
                        if (st.ownItemCount(elven_knight_brooch) > 0) {
                            htmltext = "master_sorius_q0406_04.htm";
                            st.exitQuest(true);
                        } else
                            htmltext = "master_sorius_q0406_05.htm";
                        break;
                }
            }
        } else if (npcId == blacksmith_kluto) {
            if (event.equalsIgnoreCase("reply_1")) {
                st.takeItems(sorius_letter1, -1);
                if (st.ownItemCount(kluto_memo) == 0)
                    st.giveItems(kluto_memo, 1);
                st.setCond(4);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "blacksmith_kluto_q0406_02.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        Player player = st.getPlayer();
        int talker_level = st.getPlayer().getLevel();
        int GetOneTimeQuestFlag = st.getPlayer().getPlayerVariables().getInt(PlayerVariables.profession_145);
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == master_sorius)
                    htmltext = "master_sorius_q0406_01.htm";
                break;
            case STARTED:
                if (npcId == master_sorius) {
                    if (st.ownItemCount(kluto_box) == 0 && st.ownItemCount(topaz_piece) == 0)
                        htmltext = "master_sorius_q0406_07.htm";
                    else if (st.ownItemCount(kluto_box) == 0 && st.ownItemCount(topaz_piece) > 0 && st.ownItemCount(topaz_piece) < 20)
                        htmltext = "master_sorius_q0406_08.htm";
                    else if (st.ownItemCount(kluto_box) == 0 && st.ownItemCount(topaz_piece) >= 20 && st.ownItemCount(sorius_letter1) == 0 && st.ownItemCount(kluto_memo) == 0) {
                        st.setCond(3);
                        if (st.ownItemCount(sorius_letter1) == 0)
                            st.giveItems(sorius_letter1, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "master_sorius_q0406_09.htm";
                    } else if (st.ownItemCount(kluto_box) == 0 && st.ownItemCount(topaz_piece) >= 20 && st.ownItemCount(sorius_letter1) != 0)
                        htmltext = "master_sorius_q0406_11.htm";
                    else if (st.ownItemCount(kluto_box) == 0 && st.ownItemCount(topaz_piece) >= 20 && st.ownItemCount(kluto_memo) != 0)
                        htmltext = "master_sorius_q0406_11.htm";
                    else if (st.ownItemCount(kluto_box) != 0) {
                        st.takeItems(kluto_box, -1);
                        if (GetOneTimeQuestFlag == 0) {
                            st.getPlayer().getPlayerVariables().set(PlayerVariables.profession_145, String.valueOf(1), -1);
                            if (talker_level >= 20)
                                st.addExpAndSp(320534, 23152);
                            else if (talker_level == 19)
                                st.addExpAndSp(456128, 29850);
                            else
                                st.addExpAndSp(591724, 33328);
                            st.giveItems(ADENA_ID, 163800);
                        }
                        if (st.ownItemCount(elven_knight_brooch) == 0)
                            st.giveItems(elven_knight_brooch, 1);
                        htmltext = "master_sorius_q0406_10.htm";
                        player.sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                    }
                } else if (npcId == blacksmith_kluto) {
                    if (st.ownItemCount(kluto_box) == 0 && st.ownItemCount(topaz_piece) >= 20 && st.ownItemCount(sorius_letter1) != 0)
                        htmltext = "blacksmith_kluto_q0406_01.htm";
                    else if (st.ownItemCount(kluto_box) == 0 && st.ownItemCount(topaz_piece) >= 20 && st.ownItemCount(kluto_memo) != 0 && st.ownItemCount(emerald_piece) == 0)
                        htmltext = "blacksmith_kluto_q0406_03.htm";
                    else if (st.ownItemCount(kluto_box) == 0 && st.ownItemCount(topaz_piece) >= 20 && st.ownItemCount(kluto_memo) != 0 && st.ownItemCount(emerald_piece) > 0 && st.ownItemCount(emerald_piece) < 20)
                        htmltext = "blacksmith_kluto_q0406_04.htm";
                    else if (st.ownItemCount(kluto_box) == 0 && st.ownItemCount(topaz_piece) >= 20 && st.ownItemCount(kluto_memo) != 0 && st.ownItemCount(emerald_piece) >= 20) {
                        st.takeItems(emerald_piece, -1);
                        st.takeItems(topaz_piece, -1);
                        if (st.ownItemCount(kluto_box) == 0) {
                            st.giveItems(kluto_box, 1);
                            st.takeItems(kluto_memo, 1);
                        }
                        st.setCond(6);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "blacksmith_kluto_q0406_05.htm";
                    } else if (st.ownItemCount(kluto_box) != 0)
                        htmltext = "blacksmith_kluto_q0406_06.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == tracker_skeleton || npcId == tracker_skeleton_leader || npcId == scout_skeleton || npcId == sniper_skeleton || npcId == ruin_spartoi || npcId == raging_spartoi) {
            if (st.ownItemCount(kluto_box) == 0 && st.ownItemCount(topaz_piece) < 20 && Rnd.get(100) < 70) {
                st.giveItems(topaz_piece, 1);
                if (st.ownItemCount(topaz_piece) >= 20) {
                    st.setCond(2);
                    st.soundEffect(SOUND_MIDDLE);
                } else
                    st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == ol_mahum_rookie) {
            if (st.ownItemCount(kluto_memo) != 0 && st.ownItemCount(emerald_piece) < 20 && Rnd.get(100) < 50) {
                st.giveItems(emerald_piece, 1);
                if (st.ownItemCount(emerald_piece) >= 20) {
                    st.setCond(5);
                    st.soundEffect(SOUND_MIDDLE);
                } else
                    st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }
}
