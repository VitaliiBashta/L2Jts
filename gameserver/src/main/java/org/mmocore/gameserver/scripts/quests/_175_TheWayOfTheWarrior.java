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
import org.mmocore.gameserver.network.lineage.serverpackets.MagicSkillUse;
import org.mmocore.gameserver.network.lineage.serverpackets.SocialAction;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.1
 * @date 06/01/2015
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _175_TheWayOfTheWarrior extends Quest {
    // npc
    private final static int kekrops = 32138;
    private final static int subelder_perwan = 32133;
    // mobs
    private final static int wolfman_kamael = 22235;
    private final static int mostro_bowgun = 22236;
    private final static int mostro_guard = 22239;
    private final static int mostro_ranger = 22240;
    private final static int mostro_fighter = 22242;
    private final static int mostro_guard_chief = 22243;
    private final static int mostro_sub_leader = 22245;
    private final static int mostro_leader = 22246;
    // questitem
    private final static int q_tail_of_wolf = 9807;
    private final static int q_claw_of_mostro_q175 = 9808;
    // weapon
    private final static int q_sword_of_worrior = 9720;
    // etcitem
    private final static int soulshot_none_for_rookie = 5789;
    private final static int lesser_healing_potion = 1060;
    private final static int echo_crystal_battle = 4412;
    private final static int echo_crystal_love = 4413;
    private final static int echo_crystal_solitude = 4414;
    private final static int echo_crystal_feast = 4415;
    private final static int echo_crystal_celebration = 4416;

    public _175_TheWayOfTheWarrior() {
        super(false);
        addStartNpc(kekrops);
        addTalkId(subelder_perwan);
        addQuestItem(q_tail_of_wolf, q_claw_of_mostro_q175);
        addKillId(wolfman_kamael, mostro_bowgun, mostro_guard, mostro_ranger, mostro_fighter, mostro_guard_chief, mostro_sub_leader, mostro_leader);
        addLevelCheck(10);
        addRaceCheck(PlayerRace.kamael);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        Player player = st.getPlayer();
        if (player == null) {
            return null;
        }
        int GetMemoState = st.getInt("ceremony_of_warrior");
        int talker_level = st.getPlayer().getLevel();
        int npcId = npc.getNpcId();
        if (npcId == kekrops) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("ceremony_of_warrior", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "kekrops_q0175_05.htm";
            } else if (event.equalsIgnoreCase("reply_1"))
                htmltext = "kekrops_q0175_02.htm";
            else if (event.equalsIgnoreCase("reply_2") && GetMemoState == 5) {
                st.setCond(7);
                st.setMemoState("ceremony_of_warrior", String.valueOf(6), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "kekrops_q0175_10.htm";
            } else if (event.equalsIgnoreCase("reply_3") && GetMemoState == 6 && st.ownItemCount(q_claw_of_mostro_q175) >= 10) {
                st.takeItems(q_claw_of_mostro_q175, -1);
                st.giveItems(q_sword_of_worrior, 1);
                st.removeMemo("ceremony_of_warrior");
                st.soundEffect(SOUND_FINISH);
                st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                st.exitQuest(false);
                htmltext = "kekrops_q0175_13.htm";
                if (talker_level < 25) {
                    st.playTutorialVoice("tutorial_voice_026");
                    st.giveItems(soulshot_none_for_rookie, 7000);
                }
                if (player.getQuestState(41) == null) {
                    final Quest q = QuestManager.getQuest(41);
                    q.newQuestState(player, Quest.STARTED);
                    player.getQuestState(41).setMemoState("guide_mission", String.valueOf(100000), true);
                    st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.ACQUISITION_OF_RACE_SPECIFIC_WEAPON_COMPLETE_GO_FIND_THE_NEWBIE_GUIDE, 5000, ScreenMessageAlign.TOP_CENTER, true));
                } else if (player.getQuestState(41).getInt("guide_mission") % 1000000 / 100000 != 1) {
                    player.getQuestState(41).setMemoState("guide_mission", String.valueOf(player.getQuestState(41).getInt("guide_mission") + 100000), true);
                    st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.ACQUISITION_OF_RACE_SPECIFIC_WEAPON_COMPLETE_GO_FIND_THE_NEWBIE_GUIDE, 5000, ScreenMessageAlign.TOP_CENTER, true));
                }
                st.addExpAndSp(20739, 1777);
                st.giveItems(ADENA_ID, 8799);
                st.giveItems(lesser_healing_potion, 100);
                st.giveItems(echo_crystal_solitude, 10);
                st.giveItems(echo_crystal_feast, 10);
                st.giveItems(echo_crystal_celebration, 10);
                st.giveItems(echo_crystal_love, 10);
                st.giveItems(echo_crystal_battle, 10);
            }
        } else if (npcId == subelder_perwan) {
            if (event.equalsIgnoreCase("reply_1") && GetMemoState == 4) {
                st.setCond(6);
                st.setMemoState("ceremony_of_warrior", String.valueOf(5), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "subelder_perwan_q0175_06.htm";
                st.getPlayer().broadcastPacket(new MagicSkillUse(npc, st.getPlayer(), 4546, 1, 6000, 1));
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("ceremony_of_warrior");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == kekrops) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "kekrops_q0175_03.htm";
                            st.exitQuest(true);
                            break;
                        case RACE:
                            htmltext = "kekrops_q0175_04.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "kekrops_q0175_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == kekrops) {
                    if (GetMemoState >= 1 && GetMemoState < 3)
                        htmltext = "kekrops_q0175_06.htm";
                    else if (GetMemoState == 3) {
                        st.setCond(5);
                        st.setMemoState("ceremony_of_warrior", String.valueOf(4), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "kekrops_q0175_07.htm";
                    } else if (GetMemoState == 4)
                        htmltext = "kekrops_q0175_08.htm";
                    else if (GetMemoState == 5)
                        htmltext = "kekrops_q0175_09.htm";
                    else if (GetMemoState == 6) {
                        if (st.ownItemCount(q_claw_of_mostro_q175) >= 10)
                            htmltext = "kekrops_q0175_12.htm";
                        else
                            htmltext = "kekrops_q0175_11.htm";
                    }
                } else if (npcId == subelder_perwan) {
                    if (GetMemoState == 1) {
                        st.setCond(2);
                        st.setMemoState("ceremony_of_warrior", String.valueOf(2), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "subelder_perwan_q0175_01.htm";
                    } else if (GetMemoState == 2) {
                        if (st.ownItemCount(q_tail_of_wolf) >= 5) {
                            st.setCond(4);
                            st.setMemoState("ceremony_of_warrior", String.valueOf(3), true);
                            st.takeItems(q_tail_of_wolf, -1);
                            st.soundEffect(SOUND_MIDDLE);
                            htmltext = "subelder_perwan_q0175_03.htm";
                        } else
                            htmltext = "subelder_perwan_q0175_02.htm";
                    } else if (GetMemoState == 3)
                        htmltext = "subelder_perwan_q0175_04.htm";
                    else if (GetMemoState == 4)
                        htmltext = "subelder_perwan_q0175_05.htm";
                    else if (GetMemoState == 5)
                        htmltext = "subelder_perwan_q0175_07.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("ceremony_of_warrior");
        int npcId = npc.getNpcId();
        if (npcId == wolfman_kamael) {
            if (GetMemoState == 2 && st.ownItemCount(q_tail_of_wolf) < 5) {
                int i0 = Rnd.get(100);
                if (i0 < 50) {
                    st.giveItems(q_tail_of_wolf, 1);
                    if (st.ownItemCount(q_tail_of_wolf) >= 5) {
                        st.setCond(3);
                        st.soundEffect(SOUND_MIDDLE);
                    } else
                        st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == mostro_bowgun || npcId == mostro_guard || npcId == mostro_ranger || npcId == mostro_fighter || npcId == mostro_guard_chief || npcId == mostro_sub_leader || npcId == mostro_leader) {
            if (GetMemoState == 6 && st.ownItemCount(q_claw_of_mostro_q175) < 10) {
                st.giveItems(q_claw_of_mostro_q175, 1);
                st.soundEffect(SOUND_ITEMGET);
                if (st.ownItemCount(q_claw_of_mostro_q175) >= 10) {
                    st.setCond(8);
                    st.soundEffect(SOUND_MIDDLE);
                }
            }
        }
        return null;
    }
}