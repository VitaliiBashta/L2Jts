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
 * @version 1.1
 * @date 06/01/2015
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _281_HeadForTheHills extends Quest {
    // npc
    private static final int zerstorer_morsell = 32173;
    // mobs
    private static final int goblin_kamael = 22234;
    private static final int wolfman_kamael = 22235;
    private static final int mostro_bowgun = 22236;
    private static final int goldhill_fungus = 22237;
    private static final int wolfman_chief_kamael = 22238;
    private static final int mostro_guard = 22239;
    // questitem
    private static final int q_claw_of_golden_sloopmob = 9796;
    // Items
    private static final int scroll_of_escape = 736;
    private static final int soulshot_none_for_rookie = 5789;
    private static final int spiritshot_none_for_rookie = 5790;
    private static final int earing_of_wisdom = 115;
    private static final int ring_of_anguish = 876;
    private static final int necklace_of_anguish = 907;
    private static final int leather_shirt = 22;
    private static final int feriotic_tunic = 428;
    private static final int cotton_tunic = 1100;
    private static final int leather_pants = 29;
    private static final int feriotic_hose = 463;
    private static final int cotton_hose = 1103;

    public _281_HeadForTheHills() {
        super(false);
        addStartNpc(zerstorer_morsell);
        addKillId(goblin_kamael, wolfman_kamael, mostro_bowgun, goldhill_fungus, wolfman_chief_kamael, mostro_guard);
        addQuestItem(q_claw_of_golden_sloopmob);
        addLevelCheck(6);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        Player player = st.getPlayer();
        if (player == null) {
            return null;
        }
        int GetMemoState = st.getInt("secure_southern_road");
        int talker_level = st.getPlayer().getLevel();
        boolean class_level = st.getPlayer().getPlayerClassComponent().getClassId().isOfLevel(ClassLevel.First);
        boolean isMage = (st.getPlayer().getPlayerClassComponent().getClassId().getRace() != PlayerRace.orc) && st.getPlayer().getPlayerClassComponent().getClassId().isMage();
        int npcId = npc.getNpcId();
        if (npcId == zerstorer_morsell) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("secure_southern_road", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "zerstorer_morsell_q0281_03.htm";
            } else if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 1 && st.ownItemCount(q_claw_of_golden_sloopmob) > 0) {
                    if (st.ownItemCount(q_claw_of_golden_sloopmob) >= 10)
                        st.giveItems(ADENA_ID, 400);
                    st.giveItems(ADENA_ID, 23 * st.ownItemCount(q_claw_of_golden_sloopmob));
                    st.takeItems(q_claw_of_golden_sloopmob, -1);
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
                    htmltext = "zerstorer_morsell_q0281_06.htm";
                }
                if (GetMemoState == 1 && st.ownItemCount(q_claw_of_golden_sloopmob) <= 0)
                    htmltext = "zerstorer_morsell_q0281_07.htm";
            } else if (event.equalsIgnoreCase("reply_2"))
                htmltext = "zerstorer_morsell_q0281_08.htm";
            else if (event.equalsIgnoreCase("reply_3")) {
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                htmltext = "zerstorer_morsell_q0281_09.htm";
            } else if (event.equalsIgnoreCase("reply_4")) {
                if (GetMemoState == 1 && st.ownItemCount(q_claw_of_golden_sloopmob) >= 50) {
                    st.takeItems(q_claw_of_golden_sloopmob, 50);
                    int i1 = Rnd.get(1000);
                    if (i1 <= 360) {
                        i1 = Rnd.get(9);
                        if (i1 == 0)
                            st.giveItems(earing_of_wisdom, 1);
                        if (i1 == 1)
                            st.giveItems(ring_of_anguish, 1);
                        if (i1 == 2)
                            st.giveItems(necklace_of_anguish, 1);
                        if (i1 == 3)
                            st.giveItems(leather_shirt, 1);
                        if (i1 == 4)
                            st.giveItems(feriotic_tunic, 1);
                        if (i1 == 5)
                            st.giveItems(cotton_tunic, 1);
                        if (i1 == 6)
                            st.giveItems(leather_pants, 1);
                        if (i1 == 7)
                            st.giveItems(feriotic_hose, 1);
                        if (i1 == 8)
                            st.giveItems(cotton_hose, 1);
                    } else
                        st.giveItems(scroll_of_escape, 1);
                    if (talker_level < 25 && class_level && !st.getPlayer().getPlayerVariables().getBoolean(PlayerVariables.NG_57)) {
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
                    htmltext = "zerstorer_morsell_q0281_11.htm";
                }
                if (GetMemoState == 1 && st.ownItemCount(q_claw_of_golden_sloopmob) < 50)
                    htmltext = "zerstorer_morsell_q0281_10.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("secure_southern_road");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == zerstorer_morsell) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "zerstorer_morsell_q0281_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "zerstorer_morsell_q0281_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == zerstorer_morsell) {
                    if (GetMemoState == 1 && st.ownItemCount(q_claw_of_golden_sloopmob) <= 0)
                        htmltext = "zerstorer_morsell_q0281_04.htm";
                    else if (GetMemoState == 1 && st.ownItemCount(q_claw_of_golden_sloopmob) > 0)
                        htmltext = "zerstorer_morsell_q0281_05.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == goblin_kamael) {
            int i4 = Rnd.get(1000);
            if (i4 <= 390) {
                st.giveItems(q_claw_of_golden_sloopmob, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == wolfman_kamael) {
            int i4 = Rnd.get(1000);
            if (i4 <= 450) {
                st.giveItems(q_claw_of_golden_sloopmob, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == mostro_bowgun) {
            int i4 = Rnd.get(1000);
            if (i4 <= 650) {
                st.giveItems(q_claw_of_golden_sloopmob, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == goldhill_fungus) {
            int i4 = Rnd.get(1000);
            if (i4 <= 720) {
                st.giveItems(q_claw_of_golden_sloopmob, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == wolfman_chief_kamael) {
            int i4 = Rnd.get(1000);
            if (i4 <= 920) {
                st.giveItems(q_claw_of_golden_sloopmob, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == mostro_guard) {
            int i4 = Rnd.get(1000);
            if (i4 <= 990) {
                st.giveItems(q_claw_of_golden_sloopmob, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }
}