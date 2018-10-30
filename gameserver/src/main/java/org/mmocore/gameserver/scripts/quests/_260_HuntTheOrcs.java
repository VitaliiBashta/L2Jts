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
public class _260_HuntTheOrcs extends Quest {
    // npc
    private static final int sentinel_rayjien = 30221;
    // mobs
    private static final int kaboo_orc = 20468;
    private static final int kaboo_orc_archer = 20469;
    private static final int kaboo_orc_grunt = 20470;
    private static final int kaboo_orc_fighter = 20471;
    private static final int kaboo_orc_ft_leader = 20472;
    private static final int kaboo_orc_ft_sub_ldr = 20473;
    // questitem
    private static final int orc_amulet1 = 1114;
    private static final int orc_necklace1 = 1115;
    // etcitem
    private static final int soulshot_none_for_rookie = 5789;
    private static final int spiritshot_none_for_rookie = 5790;

    public _260_HuntTheOrcs() {
        super(false);
        addStartNpc(sentinel_rayjien);
        addKillId(kaboo_orc, kaboo_orc_archer, kaboo_orc_grunt, kaboo_orc_fighter, kaboo_orc_ft_leader, kaboo_orc_ft_sub_ldr);
        addQuestItem(orc_amulet1, orc_necklace1);
        addLevelCheck(6, 16);
        addRaceCheck(PlayerRace.elf);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == sentinel_rayjien) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "sentinel_rayjien_q0260_03.htm";
            } else if (event.equalsIgnoreCase("reply_1"))
                htmltext = "sentinel_rayjien_q0260_07.htm";
            else if (event.equalsIgnoreCase("reply_2")) {
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                htmltext = "sentinel_rayjien_q0260_06.htm";
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
        boolean isMage = (st.getPlayer().getPlayerClassComponent().getClassId().getRace() != PlayerRace.orc) && st.getPlayer().getPlayerClassComponent().getClassId().isMage();
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == sentinel_rayjien) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "sentinel_rayjien_q0260_01.htm";
                            st.exitQuest(true);
                            break;
                        case RACE:
                            htmltext = "sentinel_rayjien_q0260_00.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "sentinel_rayjien_q0260_02.htm";
                            return htmltext;
                    }
                }
                break;
            case STARTED:
                if (npcId == sentinel_rayjien) {
                    if (st.ownItemCount(orc_amulet1) == 0 && st.ownItemCount(orc_necklace1) == 0)
                        htmltext = "sentinel_rayjien_q0260_04.htm";
                    else if (st.ownItemCount(orc_amulet1) > 0 || st.ownItemCount(orc_necklace1) > 0) {
                        htmltext = "sentinel_rayjien_q0260_05.htm";
                        if (st.ownItemCount(orc_amulet1) + st.ownItemCount(orc_necklace1) >= 10)
                            st.giveItems(ADENA_ID, st.ownItemCount(orc_amulet1) * 12 + st.ownItemCount(orc_necklace1) * 30 + 1000);
                        else
                            st.giveItems(ADENA_ID, st.ownItemCount(orc_amulet1) * 12 + st.ownItemCount(orc_necklace1) * 30);
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
                        st.takeItems(orc_amulet1, -1);
                        st.takeItems(orc_necklace1, -1);
                    }
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == kaboo_orc || npcId == kaboo_orc_archer || npcId == kaboo_orc_grunt) {
            if (Rnd.get(10) > 4) {
                st.giveItems(orc_amulet1, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == kaboo_orc_fighter || npcId == kaboo_orc_ft_leader || npcId == kaboo_orc_ft_sub_ldr) {
            if (Rnd.get(10) > 4) {
                st.giveItems(orc_necklace1, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }
}