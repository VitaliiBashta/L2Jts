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
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.0
 * @date 28/12/2014
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _293_TheHiddenVeins extends Quest {
    // npc
    private static final int elder_filaur = 30535;
    private static final int chichirin = 30539;
    // mobs
    private static final int utuku_orc = 20446;
    private static final int utuku_orc_archer = 20447;
    private static final int utuku_orc_grunt = 20448;
    // questitem
    private static final int oriharukon_ore_1 = 1488;
    private static final int torn_map_fragment = 1489;
    private static final int hidden_vein_map = 1490;
    // etcitem
    private static final int soulshot_none_for_rookie = 5789;

    public _293_TheHiddenVeins() {
        super(false);
        addStartNpc(elder_filaur);
        addTalkId(chichirin);
        addKillId(utuku_orc, utuku_orc_archer, utuku_orc_grunt);
        addQuestItem(oriharukon_ore_1, torn_map_fragment, hidden_vein_map);
        addLevelCheck(6, 15);
        addRaceCheck(PlayerRace.dwarf);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == elder_filaur) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "elder_filaur_q0293_03.htm";
            } else if (event.equalsIgnoreCase("reply_1")) {
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                htmltext = "elder_filaur_q0293_06.htm";
            } else if (event.equalsIgnoreCase("reply_2"))
                htmltext = "elder_filaur_q0293_07.htm";
        } else if (npcId == chichirin) {
            if (event.equalsIgnoreCase("reply_1") && st.ownItemCount(torn_map_fragment) < 4)
                htmltext = "chichirin_q0293_02.htm";
            else if (event.equalsIgnoreCase("reply_1") && st.ownItemCount(torn_map_fragment) >= 4) {
                st.giveItems(hidden_vein_map, 1);
                st.takeItems(torn_map_fragment, 4);
                htmltext = "chichirin_q0293_03.htm";
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
        boolean class_level = st.getPlayer().getPlayerClassComponent().getClassId().isOfLevel(ClassLevel.First);
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == elder_filaur) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "elder_filaur_q0293_01.htm";
                            st.exitQuest(true);
                            break;
                        case RACE:
                            htmltext = "elder_filaur_q0293_00.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "elder_filaur_q0293_02.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == elder_filaur) {
                    if (st.ownItemCount(oriharukon_ore_1) < 1 && st.ownItemCount(hidden_vein_map) < 1)
                        htmltext = "elder_filaur_q0293_04.htm";
                    else if (st.ownItemCount(oriharukon_ore_1) < 1 && st.ownItemCount(hidden_vein_map) >= 1) {
                        htmltext = "elder_filaur_q0293_08.htm";
                        if (st.ownItemCount(hidden_vein_map) >= 10)
                            st.giveItems(ADENA_ID, st.ownItemCount(hidden_vein_map) * 500 + 2000);
                        else
                            st.giveItems(ADENA_ID, st.ownItemCount(hidden_vein_map) * 500);
                        st.takeItems(hidden_vein_map, -1);
                        if (talker_level < 25 && class_level && !st.getPlayer().getPlayerVariables().getBoolean(PlayerVariables.NG_57)) {
                            st.playTutorialVoice("tutorial_voice_026");
                            st.giveItems(soulshot_none_for_rookie, 6000);
                            st.getPlayer().getPlayerVariables().set(PlayerVariables.NG_57, "57", -1);
                            st.showQuestionMark(26);
                        }
                        if (player.getQuestState(41) == null) {
                            final Quest q = QuestManager.getQuest(41);
                            q.newQuestState(player, Quest.STARTED);
                            player.getQuestState(41).setMemoState("guide_mission", String.valueOf(1000), true);
                            st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.ACQUISITION_OF_SOULSHOT_FOR_BEGINNERS_COMPLETE_GO_FIND_THE_NEWBIE_GUIDE, 5000, ScreenMessageAlign.TOP_CENTER, true));
                        } else if (player.getQuestState(41).getInt("guide_mission") % 10000 / 1000 != 1) {
                            player.getQuestState(41).setMemoState("guide_mission", String.valueOf(player.getQuestState(41).getInt("guide_mission") + 1000), true);
                            st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.ACQUISITION_OF_SOULSHOT_FOR_BEGINNERS_COMPLETE_GO_FIND_THE_NEWBIE_GUIDE, 5000, ScreenMessageAlign.TOP_CENTER, true));
                        }
                    } else if (st.ownItemCount(oriharukon_ore_1) >= 1 && st.ownItemCount(hidden_vein_map) < 1) {
                        htmltext = "elder_filaur_q0293_05.htm";
                        if (st.ownItemCount(oriharukon_ore_1) >= 10)
                            st.giveItems(ADENA_ID, st.ownItemCount(oriharukon_ore_1) * 5 + 2000);
                        else
                            st.giveItems(ADENA_ID, st.ownItemCount(oriharukon_ore_1) * 5);
                        st.takeItems(oriharukon_ore_1, -1);
                        if (talker_level < 25 && class_level && !st.getPlayer().getPlayerVariables().getBoolean(PlayerVariables.NG_57)) {
                            st.playTutorialVoice("tutorial_voice_026");
                            st.giveItems(soulshot_none_for_rookie, 6000);
                            st.getPlayer().getPlayerVariables().set(PlayerVariables.NG_57, "57", -1);
                            st.showQuestionMark(26);
                        }
                        if (player.getQuestState(41) == null) {
                            final Quest q = QuestManager.getQuest(41);
                            q.newQuestState(player, Quest.STARTED);
                            player.getQuestState(41).setMemoState("guide_mission", String.valueOf(1000), true);
                            st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.ACQUISITION_OF_SOULSHOT_FOR_BEGINNERS_COMPLETE_GO_FIND_THE_NEWBIE_GUIDE, 5000, ScreenMessageAlign.TOP_CENTER, true));
                        } else if (player.getQuestState(41).getInt("guide_mission") % 10000 / 1000 != 1) {
                            player.getQuestState(41).setMemoState("guide_mission", String.valueOf(player.getQuestState(41).getInt("guide_mission") + 1000), true);
                            st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.ACQUISITION_OF_SOULSHOT_FOR_BEGINNERS_COMPLETE_GO_FIND_THE_NEWBIE_GUIDE, 5000, ScreenMessageAlign.TOP_CENTER, true));
                        }
                    } else if (st.ownItemCount(oriharukon_ore_1) >= 1 && st.ownItemCount(hidden_vein_map) >= 1) {
                        htmltext = "elder_filaur_q0293_09.htm";
                        if (st.ownItemCount(oriharukon_ore_1) + st.ownItemCount(hidden_vein_map) >= 10)
                            st.giveItems(ADENA_ID, st.ownItemCount(oriharukon_ore_1) * 5 + st.ownItemCount(hidden_vein_map) * 500 + 2000);
                        else
                            st.giveItems(ADENA_ID, st.ownItemCount(oriharukon_ore_1) * 5 + st.ownItemCount(hidden_vein_map) * 500);
                        st.takeItems(hidden_vein_map, -1);
                        st.takeItems(oriharukon_ore_1, -1);
                        if (talker_level < 25 && class_level && !st.getPlayer().getPlayerVariables().getBoolean(PlayerVariables.NG_57)) {
                            st.playTutorialVoice("tutorial_voice_026");
                            st.giveItems(soulshot_none_for_rookie, 6000);
                            st.getPlayer().getPlayerVariables().set(PlayerVariables.NG_57, "57", -1);
                            st.showQuestionMark(26);
                        }
                        if (player.getQuestState(41) == null) {
                            final Quest q = QuestManager.getQuest(41);
                            q.newQuestState(player, Quest.STARTED);
                            player.getQuestState(41).setMemoState("guide_mission", String.valueOf(1000), true);
                            st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.ACQUISITION_OF_SOULSHOT_FOR_BEGINNERS_COMPLETE_GO_FIND_THE_NEWBIE_GUIDE, 5000, ScreenMessageAlign.TOP_CENTER, true));
                        } else if (player.getQuestState(41).getInt("guide_mission") % 10000 / 1000 != 1) {
                            player.getQuestState(41).setMemoState("guide_mission", String.valueOf(player.getQuestState(41).getInt("guide_mission") + 1000), true);
                            st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.ACQUISITION_OF_SOULSHOT_FOR_BEGINNERS_COMPLETE_GO_FIND_THE_NEWBIE_GUIDE, 5000, ScreenMessageAlign.TOP_CENTER, true));
                        }
                    }
                } else if (npcId == chichirin)
                    htmltext = "chichirin_q0293_01.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == utuku_orc || npcId == utuku_orc_archer || npcId == utuku_orc_grunt) {
            int i0 = Rnd.get(100);
            if (i0 > 50) {
                st.giveItems(oriharukon_ore_1, 1);
                st.soundEffect(SOUND_ITEMGET);
            } else if (i0 < 5) {
                st.giveItems(torn_map_fragment, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }
}