package org.mmocore.gameserver.scripts.quests;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.manager.QuestManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import org.mmocore.gameserver.network.lineage.serverpackets.SocialAction;
import org.mmocore.gameserver.object.Player;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.0
 * @date 28/01/2015
 * @tested OK
 */
public class _103_SpiritOfCraftsman extends Quest {
    // npc
    private static final int blacksmith_karoyd = 30307;
    private static final int cecon = 30132;
    private static final int harne = 30144;
    // mobs
    private static final int marsh_zombie = 20015;
    private static final int marsh_zombie_pointer = 20020;
    private static final int doom_soldier = 20455;
    private static final int skeleton_hunter = 20517;
    private static final int skeleton_hunter_archer = 20518;
    // questitem
    private static final int karoyds_letter = 968;
    private static final int cecktinons_voucher1 = 969;
    private static final int cecktinons_voucher2 = 970;
    private static final int bone_fragment1 = 1107;
    private static final int soul_catcher = 971;
    private static final int preserve_oil = 972;
    private static final int zombie_head = 973;
    private static final int steelbenders_head = 974;
    private static final int bloodsaber = 975;
    // etcitem
    private static final int soulshot_none_for_rookie = 5789;
    private static final int spiritshot_none_for_rookie = 5790;
    private static final int lesser_healing_potion = 1060;
    private static final int echo_crystal_solitude = 4414;
    private static final int echo_crystal_feast = 4415;
    private static final int echo_crystal_celebration = 4416;
    private static final int echo_crystal_love = 4413;
    private static final int echo_crystal_battle = 4412;

    public _103_SpiritOfCraftsman() {
        super(false);
        addStartNpc(blacksmith_karoyd);
        addTalkId(cecon, harne);
        addKillId(marsh_zombie, marsh_zombie_pointer, doom_soldier, skeleton_hunter, skeleton_hunter_archer);
        addQuestItem(karoyds_letter, cecktinons_voucher1, cecktinons_voucher2, bone_fragment1, soul_catcher, preserve_oil, zombie_head, steelbenders_head);
        addLevelCheck(10, 17);
        addRaceCheck(PlayerRace.darkelf);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == blacksmith_karoyd) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.giveItems(karoyds_letter, 1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "blacksmith_karoyd_q0103_05.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        Player player = st.getPlayer();
        if (player == null) {
            return null;
        }
        int talker_level = st.getPlayer().getLevel();
        boolean isMage = st.getPlayer().getPlayerClassComponent().getClassId().isMage();
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == blacksmith_karoyd) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "blacksmith_karoyd_q0103_02.htm";
                            st.exitQuest(true);
                            break;
                        case RACE:
                            htmltext = "blacksmith_karoyd_q0103_00.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "blacksmith_karoyd_q0103_03.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == blacksmith_karoyd) {
                    if (st.ownItemCount(karoyds_letter) >= 1 || st.ownItemCount(cecktinons_voucher1) >= 1 || st.ownItemCount(cecktinons_voucher2) >= 1)
                        htmltext = "blacksmith_karoyd_q0103_06.htm";
                    else if (st.ownItemCount(steelbenders_head) == 1) {
                        if (talker_level < 25 && !isMage) {
                            st.giveItems(soulshot_none_for_rookie, 7000);
                            st.playTutorialVoice("tutorial_voice_026");
                        }
                        if (talker_level < 25 && isMage) {
                            st.giveItems(spiritshot_none_for_rookie, 3000);
                            st.playTutorialVoice("tutorial_voice_027");
                        }
                        st.giveItems(lesser_healing_potion, 100);
                        st.giveItems(echo_crystal_solitude, 10);
                        st.giveItems(echo_crystal_feast, 10);
                        st.giveItems(echo_crystal_celebration, 10);
                        st.giveItems(echo_crystal_love, 10);
                        st.giveItems(echo_crystal_battle, 10);
                        if (player.getQuestState(41) == null) {
                            final Quest q = QuestManager.getQuest(41);
                            q.newQuestState(player, Quest.STARTED);
                            player.getQuestState(41).setMemoState("guide_mission", String.valueOf(100000), true);
                            st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.ACQUISITION_OF_RACE_SPECIFIC_WEAPON_COMPLETE_GO_FIND_THE_NEWBIE_GUIDE, 5000, ScreenMessageAlign.TOP_CENTER, true));
                        } else if (player.getQuestState(41).getInt("guide_mission") % 1000000 / 100000 != 1) {
                            player.getQuestState(41).setMemoState("guide_mission", String.valueOf(player.getQuestState(41).getInt("guide_mission") + 100000), true);
                            st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.ACQUISITION_OF_RACE_SPECIFIC_WEAPON_COMPLETE_GO_FIND_THE_NEWBIE_GUIDE, 5000, ScreenMessageAlign.TOP_CENTER, true));
                        }
                        st.addExpAndSp(46663, 3999);
                        st.giveItems(ADENA_ID, 19799);
                        st.takeItems(steelbenders_head, 1);
                        st.giveItems(bloodsaber, 1);
                        st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                        st.exitQuest(false);
                        st.soundEffect(SOUND_FINISH);
                        htmltext = "blacksmith_karoyd_q0103_07.htm";
                    }
                } else if (npcId == cecon) {
                    if (st.ownItemCount(karoyds_letter) == 1) {
                        st.setCond(2);
                        st.takeItems(karoyds_letter, 1);
                        st.giveItems(cecktinons_voucher1, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "cecon_q0103_01.htm";
                    } else if (st.ownItemCount(cecktinons_voucher1) >= 1 || st.ownItemCount(cecktinons_voucher2) >= 1)
                        htmltext = "cecon_q0103_02.htm";
                    else if (st.ownItemCount(soul_catcher) == 1) {
                        st.setCond(6);
                        st.takeItems(soul_catcher, 1);
                        st.giveItems(preserve_oil, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "cecon_q0103_03.htm";
                    } else if (st.ownItemCount(preserve_oil) == 1 && st.ownItemCount(zombie_head) == 0 && st.ownItemCount(steelbenders_head) == 0)
                        htmltext = "cecon_q0103_04.htm";
                    else if (st.ownItemCount(zombie_head) == 1) {
                        st.setCond(8);
                        st.takeItems(zombie_head, 1);
                        st.giveItems(steelbenders_head, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "cecon_q0103_05.htm";
                    } else if (st.ownItemCount(steelbenders_head) == 1)
                        htmltext = "cecon_q0103_06.htm";
                } else if (npcId == harne) {
                    if (st.ownItemCount(cecktinons_voucher1) >= 1) {
                        st.setCond(3);
                        st.takeItems(cecktinons_voucher1, 1);
                        st.giveItems(cecktinons_voucher2, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "harne_q0103_01.htm";
                    } else if (st.ownItemCount(cecktinons_voucher2) >= 1 && st.ownItemCount(bone_fragment1) < 10)
                        htmltext = "harne_q0103_02.htm";
                    else if (st.ownItemCount(cecktinons_voucher2) == 1 && st.ownItemCount(bone_fragment1) >= 10) {
                        st.setCond(5);
                        st.takeItems(cecktinons_voucher2, 1);
                        st.takeItems(bone_fragment1, 10);
                        st.giveItems(soul_catcher, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "harne_q0103_03.htm";
                    } else if (st.ownItemCount(soul_catcher) == 1)
                        htmltext = "harne_q0103_04.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == marsh_zombie || npcId == marsh_zombie_pointer) {
            if (Rnd.get(10) < 5) {
                st.setCond(7);
                st.giveItems(zombie_head, 1);
                st.takeItems(preserve_oil, 1);
                st.soundEffect(SOUND_MIDDLE);
            }
        } else if (npcId == doom_soldier || npcId == skeleton_hunter || npcId == skeleton_hunter_archer) {
            if (Rnd.get(10) < 10) {
                st.giveItems(bone_fragment1, 1);
                if (st.ownItemCount(bone_fragment1) >= 10) {
                    st.setCond(4);
                    st.soundEffect(SOUND_MIDDLE);
                } else
                    st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }
}
