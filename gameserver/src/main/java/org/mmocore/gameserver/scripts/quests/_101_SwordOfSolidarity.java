package org.mmocore.gameserver.scripts.quests;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.manager.QuestManager;
import org.mmocore.gameserver.model.base.ClassLevel;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import org.mmocore.gameserver.network.lineage.serverpackets.SocialAction;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.1
 * @date 28/12/2014
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _101_SwordOfSolidarity extends Quest {
    // npc
    private static final int roien = 30008;
    private static final int blacksmith_alltran = 30283;
    // mobs
    private static final int lennunt_orc_sniper = 20361;
    private static final int lennunt_orc_warrior = 20362;
    // questitem
    private static final int roiens_letter = 796;
    private static final int howtogo_ruins = 937;
    private static final int broken_sword_handle = 739;
    private static final int broken_blade_bottom = 740;
    private static final int broken_blade_top = 741;
    private static final int alltrans_note = 742;
    // etcitem
    private static final int sword_of_solidarity = 738;
    private static final int soulshot_none_for_rookie = 5789;
    private static final int lesser_healing_potion = 1060;
    private static final int echo_crystal_solitude = 4414;
    private static final int echo_crystal_feast = 4415;
    private static final int echo_crystal_celebration = 4416;
    private static final int echo_crystal_love = 4413;
    private static final int echo_crystal_battle = 4412;

    public _101_SwordOfSolidarity() {
        super(false);
        addStartNpc(roien);
        addTalkId(blacksmith_alltran);
        addKillId(lennunt_orc_sniper, lennunt_orc_warrior);
        addQuestItem(alltrans_note, howtogo_ruins, broken_blade_top, broken_blade_bottom, roiens_letter, broken_sword_handle);
        addLevelCheck(9, 16);
        addRaceCheck(PlayerRace.human);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        Player player = st.getPlayer();
        if (player == null) {
            return null;
        }
        int talker_level = st.getPlayer().getLevel();
        boolean class_level = st.getPlayer().getPlayerClassComponent().getClassId().isOfLevel(ClassLevel.First);
        int npcId = npc.getNpcId();
        if (npcId == roien) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                st.giveItems(roiens_letter, 1);
                htmltext = "roien_q0101_04.htm";
            }
        } else if (npcId == blacksmith_alltran) {
            if (event.equalsIgnoreCase("reply_1")) {
                st.setCond(2);
                st.takeItems(roiens_letter, -1);
                st.giveItems(howtogo_ruins, 1);
                htmltext = "blacksmith_alltran_q0101_02.htm";
            } else if (event.equalsIgnoreCase("reply_3") && st.ownItemCount(broken_sword_handle) > 0) {
                if (talker_level < 25 && class_level) {
                    st.giveItems(soulshot_none_for_rookie, 7000);
                    st.playTutorialVoice("tutorial_voice_026");
                }
                st.takeItems(broken_sword_handle, -1);
                st.giveItems(sword_of_solidarity, 1);
                st.giveItems(lesser_healing_potion, 100);
                st.giveItems(echo_crystal_solitude, 10);
                st.giveItems(echo_crystal_feast, 10);
                st.giveItems(echo_crystal_celebration, 10);
                st.giveItems(echo_crystal_love, 10);
                st.giveItems(echo_crystal_battle, 10);
                st.addExpAndSp(25747, 2171);
                st.giveItems(ADENA_ID, 10981);
                if (player.getQuestState(41) == null) {
                    final Quest q = QuestManager.getQuest(41);
                    q.newQuestState(player, Quest.STARTED);
                    player.getQuestState(41).setMemoState("guide_mission", String.valueOf(100000), true);
                    st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.ACQUISITION_OF_RACE_SPECIFIC_WEAPON_COMPLETE_GO_FIND_THE_NEWBIE_GUIDE, 5000, ScreenMessageAlign.TOP_CENTER, true));
                } else if (player.getQuestState(41).getInt("guide_mission") % 1000000 / 100000 != 1) {
                    player.getQuestState(41).setMemoState("guide_mission", String.valueOf(player.getQuestState(41).getInt("guide_mission") + 100000), true);
                    st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.ACQUISITION_OF_RACE_SPECIFIC_WEAPON_COMPLETE_GO_FIND_THE_NEWBIE_GUIDE, 5000, ScreenMessageAlign.TOP_CENTER, true));
                }
                htmltext = "blacksmith_alltran_q0101_07.htm";
                st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                st.exitQuest(false);
                st.soundEffect(SOUND_FINISH);
            }
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
                if (npcId == roien) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "roien_q0101_08.htm";
                            st.exitQuest(true);
                            break;
                        case RACE:
                            htmltext = "roien_q0101_00.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "roien_q0101_02.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == roien) {
                    if (st.ownItemCount(roiens_letter) == 1)
                        htmltext = "roien_q0101_05.htm";
                    else if (st.ownItemCount(roiens_letter) == 0 && st.ownItemCount(alltrans_note) == 0) {
                        if (st.ownItemCount(broken_blade_top) > 0 && st.ownItemCount(broken_blade_bottom) > 0)
                            htmltext = "roien_q0101_12.htm";
                        else if (st.ownItemCount(broken_blade_top) > 0 && st.ownItemCount(broken_blade_bottom) == 0)
                            htmltext = "roien_q0101_11.htm";
                        else if (st.ownItemCount(broken_blade_top) == 0 && st.ownItemCount(broken_blade_bottom) > 0)
                            htmltext = "roien_q0101_11.htm";
                        else if (st.ownItemCount(broken_sword_handle) > 0)
                            htmltext = "roien_q0101_07.htm";
                        else if (st.ownItemCount(howtogo_ruins) == 1 && st.ownItemCount(broken_blade_top) == 0 && st.ownItemCount(broken_blade_bottom) == 0)
                            htmltext = "roien_q0101_10.htm";
                    } else if (st.ownItemCount(roiens_letter) == 0 && st.ownItemCount(alltrans_note) > 0) {
                        st.setCond(5);
                        st.takeItems(alltrans_note, -1);
                        st.giveItems(broken_sword_handle, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "roien_q0101_06.htm";
                    }
                } else if (npcId == blacksmith_alltran) {
                    if (st.ownItemCount(roiens_letter) > 0)
                        htmltext = "blacksmith_alltran_q0101_01.htm";
                    else if (st.ownItemCount(roiens_letter) == 0 && st.ownItemCount(howtogo_ruins) > 0) {
                        if (st.ownItemCount(broken_blade_top) > 0 && st.ownItemCount(broken_blade_bottom) == 0)
                            htmltext = "blacksmith_alltran_q0101_08.htm";
                        if (st.ownItemCount(broken_blade_top) == 0 && st.ownItemCount(broken_blade_bottom) > 0)
                            htmltext = "blacksmith_alltran_q0101_08.htm";
                        if (st.ownItemCount(broken_blade_top) == 0 && st.ownItemCount(broken_blade_bottom) == 0)
                            htmltext = "blacksmith_alltran_q0101_03.htm";
                        if (st.ownItemCount(broken_blade_top) > 0 && st.ownItemCount(broken_blade_bottom) > 0) {
                            st.setCond(4);
                            st.takeItems(howtogo_ruins, -1);
                            st.takeItems(broken_blade_top, -1);
                            st.takeItems(broken_blade_bottom, -1);
                            st.giveItems(alltrans_note, 1);
                            st.soundEffect(SOUND_MIDDLE);
                            htmltext = "blacksmith_alltran_q0101_04.htm";
                        }
                    } else if (st.ownItemCount(alltrans_note) > 0 && st.ownItemCount(howtogo_ruins) == 0)
                        htmltext = "blacksmith_alltran_q0101_05.htm";
                    else if (st.ownItemCount(broken_sword_handle) > 0)
                        htmltext = "blacksmith_alltran_q0101_06.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == lennunt_orc_sniper || npcId == lennunt_orc_warrior) {
            if (st.ownItemCount(broken_blade_top) == 0) {
                if (Rnd.get(5) == 0) {
                    st.giveItems(broken_blade_top, 1);
                    if (st.ownItemCount(broken_blade_top) >= 1) {
                        st.setCond(3);
                        st.soundEffect(SOUND_MIDDLE);
                    } else
                        st.soundEffect(SOUND_ITEMGET);
                }
            } else if (st.ownItemCount(broken_blade_bottom) == 0) {
                if (Rnd.get(5) == 0) {
                    st.giveItems(broken_blade_bottom, 1);
                    if (st.ownItemCount(broken_blade_top) >= 1) {
                        st.setCond(3);
                        st.soundEffect(SOUND_MIDDLE);
                    } else
                        st.soundEffect(SOUND_ITEMGET);
                }
            }
        }
        return null;
    }
}