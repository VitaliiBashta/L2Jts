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
public class _273_InvadersOfTheHolyLand extends Quest {
    // npc
    private static final int atuba_chief_varkees = 30566;
    // mobs
    private static final int rakeclaw_imp = 20311;
    private static final int rakeclaw_imp_hunter = 20312;
    private static final int rakeclaw_imp_chieftain = 20313;
    // questitem
    private static final int black_soulstone = 1475;
    private static final int red_soulstone = 1476;
    // etcitem
    private static final int soulshot_none_for_rookie = 5789;
    private static final int spiritshot_none_for_rookie = 5790;

    public _273_InvadersOfTheHolyLand() {
        super(false);
        addStartNpc(atuba_chief_varkees);
        addKillId(rakeclaw_imp, rakeclaw_imp_hunter, rakeclaw_imp_chieftain);
        addQuestItem(black_soulstone, red_soulstone);
        addLevelCheck(6, 14);
        addRaceCheck(PlayerRace.orc);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == atuba_chief_varkees) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "atuba_chief_varkees_q0273_03.htm";
            } else if (event.equalsIgnoreCase("reply_1")) {
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                htmltext = "atuba_chief_varkees_q0273_07.htm";
            } else if (event.equalsIgnoreCase("reply_2")) {
                htmltext = "atuba_chief_varkees_q0273_08.htm";
                st.soundEffect(SOUND_ACCEPT);
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
                if (npcId == atuba_chief_varkees) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "atuba_chief_varkees_q0273_01.htm";
                            st.exitQuest(true);
                            break;
                        case RACE:
                            htmltext = "atuba_chief_varkees_q0273_00.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "atuba_chief_varkees_q0273_02.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == atuba_chief_varkees) {
                    if (st.ownItemCount(black_soulstone) + st.ownItemCount(red_soulstone) == 0)
                        htmltext = "atuba_chief_varkees_q0273_04.htm";
                    else if (st.ownItemCount(red_soulstone) == 0) {
                        htmltext = "atuba_chief_varkees_q0273_05.htm";
                        if (st.ownItemCount(black_soulstone) >= 10)
                            st.giveItems(ADENA_ID, st.ownItemCount(black_soulstone) * 3 + 1500);
                        else
                            st.giveItems(ADENA_ID, st.ownItemCount(black_soulstone) * 3);
                        st.takeItems(black_soulstone, -1);
                        if (talker_level < 25 && !st.getPlayer().getPlayerVariables().getBoolean(PlayerVariables.NG_57)) {
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
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(true);
                    } else {
                        htmltext = "atuba_chief_varkees_q0273_06.htm";
                        if (st.ownItemCount(black_soulstone) + st.ownItemCount(red_soulstone) >= 10)
                            st.giveItems(ADENA_ID, st.ownItemCount(red_soulstone) * 10 + st.ownItemCount(black_soulstone) * 3 + 1800);
                        else
                            st.giveItems(ADENA_ID, st.ownItemCount(red_soulstone) * 10 + st.ownItemCount(black_soulstone) * 3);
                        st.takeItems(black_soulstone, -1);
                        st.takeItems(red_soulstone, -1);
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
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(true);
                    }
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == rakeclaw_imp) {
            int i0 = Rnd.get(100);
            if (i0 <= 90)
                st.giveItems(black_soulstone, 1);
            else
                st.giveItems(red_soulstone, 1);
            st.soundEffect(SOUND_ITEMGET);
        } else if (npcId == rakeclaw_imp_hunter) {
            int i0 = Rnd.get(100);
            if (i0 <= 87)
                st.giveItems(black_soulstone, 1);
            else
                st.giveItems(red_soulstone, 1);
            st.soundEffect(SOUND_ITEMGET);
        } else if (npcId == rakeclaw_imp_chieftain) {
            int i0 = Rnd.get(100);
            if (i0 <= 77)
                st.giveItems(black_soulstone, 1);
            else
                st.giveItems(red_soulstone, 1);
            st.soundEffect(SOUND_ITEMGET);
        }
        return null;
    }
}