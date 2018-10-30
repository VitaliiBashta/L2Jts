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
public class _265_ChainsOfSlavery extends Quest {
    // npc
    private static final int sentry_krpion = 30357;
    // mobs
    private static final int imp = 20004;
    private static final int imp_ribe = 20005;
    // questitem
    private static final int imp_shackles = 1368;
    // etcitem
    private static final int soulshot_none_for_rookie = 5789;
    private static final int spiritshot_none_for_rookie = 5790;

    public _265_ChainsOfSlavery() {
        super(false);
        addStartNpc(sentry_krpion);
        addKillId(imp, imp_ribe);
        addQuestItem(imp_shackles);
        addLevelCheck(6, 11);
        addRaceCheck(PlayerRace.darkelf);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == sentry_krpion) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "sentry_krpion_q0265_03.htm";
            } else if (event.equalsIgnoreCase("reply_1")) {
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                htmltext = "sentry_krpion_q0265_06.htm";
            } else if (event.equalsIgnoreCase("reply_2"))
                htmltext = "sentry_krpion_q0265_07.htm";
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
                if (npcId == sentry_krpion) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "sentry_krpion_q0265_01.htm";
                            st.exitQuest(true);
                            break;
                        case RACE:
                            htmltext = "sentry_krpion_q0265_00.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "sentry_krpion_q0265_02.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == sentry_krpion) {
                    if (st.ownItemCount(imp_shackles) > 0) {
                        if (st.ownItemCount(imp_shackles) >= 10)
                            st.giveItems(ADENA_ID, 12 * st.ownItemCount(imp_shackles) + 500);
                        else
                            st.giveItems(ADENA_ID, 12 * st.ownItemCount(imp_shackles));
                        st.takeItems(imp_shackles, -1);
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
                        htmltext = "sentry_krpion_q0265_05.htm";
                    } else
                        htmltext = "sentry_krpion_q0265_04.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == imp) {
            if (Rnd.get(10) < 5) {
                st.giveItems(imp_shackles, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == imp_ribe) {
            if (Rnd.get(10) < 6) {
                st.giveItems(imp_shackles, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }
}