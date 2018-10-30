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
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
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
public class _257_GuardIsBusy extends Quest {
    // npc
    private static final int gilbert = 30039;
    // mobs
    private static final int orc_archer = 20006;
    private static final int orc_fighter = 20093;
    private static final int orc_fighter_sub_leader = 20096;
    private static final int orc_fighter_leader = 20098;
    private static final int orc = 20130;
    private static final int orc_grunt = 20131;
    private static final int werewolf = 20132;
    private static final int werewolf_chieftain = 20342;
    private static final int werewolf_hunter = 20343;
    // questitem
    private static final int gludio_lords_mark = 1084;
    private static final int orc_amulet = 752;
    private static final int orc_necklace = 1085;
    private static final int werewolf_fang = 1086;
    // etcitem
    private static final int soulshot_none_for_rookie = 5789;
    private static final int spiritshot_none_for_rookie = 5790;

    public _257_GuardIsBusy() {
        super(false);
        addStartNpc(gilbert);
        addKillId(orc_archer, orc_fighter, orc_fighter_sub_leader, orc_fighter_leader, orc, orc_grunt, werewolf, werewolf_chieftain, werewolf_hunter);
        addQuestItem(orc_amulet, orc_necklace, werewolf_fang, gludio_lords_mark);
        addLevelCheck(6, 16);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == gilbert) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                st.giveItems(gludio_lords_mark, 1);
                htmltext = "gilbert_q0257_03.htm";
            } else if (event.equalsIgnoreCase("reply_2")) {
                htmltext = "gilbert_q0257_05.htm";
                st.takeItems(gludio_lords_mark, -1);
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
            } else if (event.equalsIgnoreCase("reply_3"))
                htmltext = "gilbert_q0257_06.htm";
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
        boolean isMage = (st.getPlayer().getPlayerClassComponent().getClassId().getRace() != PlayerRace.orc) && st.getPlayer().getPlayerClassComponent().getClassId().isMage();
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == gilbert) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "gilbert_q0257_01.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "gilbert_q0257_02.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == gilbert) {
                    if (st.ownItemCount(orc_amulet) < 1 && st.ownItemCount(orc_necklace) < 1 && st.ownItemCount(werewolf_fang) < 1)
                        htmltext = "gilbert_q0257_04.htm";
                    else if (st.ownItemCount(orc_amulet) > 0 || st.ownItemCount(orc_necklace) > 0 || st.ownItemCount(werewolf_fang) > 0) {
                        htmltext = "gilbert_q0257_07.htm";
                        if (st.ownItemCount(orc_amulet) + st.ownItemCount(orc_necklace) + st.ownItemCount(werewolf_fang) >= 10)
                            st.giveItems(ADENA_ID, 10 * st.ownItemCount(orc_amulet) + 20 * st.ownItemCount(orc_necklace) + 20 * st.ownItemCount(werewolf_fang) + 1000);
                        else
                            st.giveItems(ADENA_ID, 10 * st.ownItemCount(orc_amulet) + 20 * st.ownItemCount(orc_necklace) + 20 * st.ownItemCount(werewolf_fang));
                        if (talker_level < 25 && !isMage && !st.getPlayer().getPlayerVariables().getBoolean(PlayerVariables.NG_57)) {
                            st.playTutorialVoice("tutorial_voice_026");
                            st.giveItems(soulshot_none_for_rookie, 6000);
                            st.getPlayer().getPlayerVariables().set(PlayerVariables.NG_57, "57", -1);
                            st.showQuestionMark(26);
                        }
                        if (talker_level < 25 && isMage && !st.getPlayer().getPlayerVariables().getBoolean(PlayerVariables.NG_57)) {
                            st.playTutorialVoice("tutorial_voice_027");
                            st.giveItems(spiritshot_none_for_rookie, 3000);
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
                        st.takeItems(orc_amulet, -1);
                        st.takeItems(orc_necklace, -1);
                        st.takeItems(werewolf_fang, -1);
                    }
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (st.ownItemCount(gludio_lords_mark) > 0) {
            if (npcId == orc_archer) {
                if (Rnd.get(10) < 2) {
                    st.giveItems(orc_amulet, 2);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(orc_amulet, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == orc_fighter) {
                if (Rnd.get(100) < 85) {
                    st.giveItems(orc_necklace, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == orc_fighter_sub_leader) {
                if (Rnd.get(100) < 95) {
                    st.giveItems(orc_necklace, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == orc_fighter_leader) {
                if (Rnd.get(100) < 100) {
                    st.giveItems(orc_necklace, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == orc) {
                if (Rnd.get(100) < 7) {
                    st.giveItems(orc_amulet, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == orc_grunt) {
                if (Rnd.get(100) < 9) {
                    st.giveItems(orc_amulet, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == werewolf) {
                if (Rnd.get(100) < 7) {
                    st.giveItems(werewolf_fang, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == werewolf_chieftain) {
                st.giveItems(werewolf_fang, 1);
                st.soundEffect(SOUND_ITEMGET);
            } else if (npcId == werewolf_hunter) {
                if (Rnd.get(100) < 85) {
                    st.giveItems(werewolf_fang, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        }
        return null;
    }
}