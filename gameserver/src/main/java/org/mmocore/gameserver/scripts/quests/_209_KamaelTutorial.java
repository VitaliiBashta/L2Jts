package org.mmocore.gameserver.scripts.quests;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.serverpackets.RadarControl;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.1
 * @date 27/12/2014
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _209_KamaelTutorial extends Quest {
    // npc
    private static final int subelder_perwan = 32133; // Grand Master
    private static final int helper_krenisk = 32134; // Newbie Helper
    // mobs
    private static final int tutorial_gremlin = 18342;
    // questitem
    private static final int q_recommend_of_kamael = 9881;
    private static final int tutorial_blue_gem = 6353;
    // etcitem
    private static final int soulshot_none_for_rookie = 5789;
    // Class id
    private static final int kamael_m_soldier = 0x7b;
    private static final int kamael_f_soldier = 0x7c;
    // Tutorial Quest Event
    private static final int TUTORIAL_PICK_BLUE_GEM = 0x00100000;

    public _209_KamaelTutorial() {
        super(false);
        addTalkId(subelder_perwan, helper_krenisk);
        addFirstTalkId(subelder_perwan, helper_krenisk);
        addKillId(tutorial_gremlin);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        // tutorial_quest_npc - главный квест
        QuestState qs = st.getPlayer().getQuestState(255);
        if (qs == null || st == null) {
            return null;
        }
        Player player = st.getPlayer();
        if (player == null) {
            return null;
        }
        int GetMemoStateEx = qs.getInt("tutorial_quest_ex");
        int talker_occupation = st.getPlayer().getPlayerClassComponent().getClassId().getId();
        // EventHandler TIMER_FIRED_EX
        if (event.equalsIgnoreCase("timer_newbie_helper")) {
            if (GetMemoStateEx == 0) {
                st.playTutorialVoice("tutorial_voice_009a");
                qs.setMemoState("tutorial_quest_ex", String.valueOf(1), true);
            }
            if (GetMemoStateEx == 3)
                st.playTutorialVoice("tutorial_voice_010g");
            return null;
        } else if (event.equalsIgnoreCase("timer_grand_master")) {
            if (GetMemoStateEx >= 4) {
                st.showQuestionMark(7);
                st.soundEffect(SOUND_TUTORIAL);
                st.playTutorialVoice("tutorial_voice_025");
            }
            return null;
        } else if (event.equalsIgnoreCase("reply_31") && st.ownItemCount(q_recommend_of_kamael) > 0) {
            if ((talker_occupation == kamael_m_soldier || talker_occupation == kamael_f_soldier) && GetMemoStateEx <= 3) {
                qs.setMemoState("tutorial_quest_ex", String.valueOf(4), true);
                st.giveItems(soulshot_none_for_rookie, 200);
                st.playTutorialVoice("tutorial_voice_026");
                st.addExpAndSp(0, 50);
            }
            htmltext = "subelder_perwan002.htm";
            st.takeItems(q_recommend_of_kamael, 1);
            st.startQuestTimer("timer_grand_master", 60000);
            ThreadPoolManager.getInstance().schedule(new RadarTask(st.getPlayer()), 200L);
        }
        return htmltext;
    }

    @Override
    public String onFirstTalk(NpcInstance npc, Player player) {
        String htmltext = "";
        int npcId = npc.getNpcId();
        // tutorial_quest_npc - главный квест
        QuestState qs = player.getQuestState(255);
        if (qs == null) {
            return htmltext;
        }
        // [kamarel_tutorial] 209 Kamael Tutorial
        QuestState st = player.getQuestState(getId());
        if (st == null) {
            newQuestState(player, STARTED);
            st = player.getQuestState(getId());
        }
        int talker_occupation = st.getPlayer().getPlayerClassComponent().getClassId().getId();
        int GetMemoStateEx = qs.getInt("tutorial_quest_ex");
        int GetMemoState = qs.getInt("tutorial_quest");
        switch (npcId) {
            case helper_krenisk:
                int i0 = GetMemoState & 2147483392;
                if (GetMemoStateEx < 0) {
                    if (player.getPlayerTemplateComponent().getPlayerRace() == PlayerRace.kamael) {
                        st.startQuestTimer("timer_newbie_helper", 30000);
                        htmltext = "helper_krenisk001.htm";
                        qs.setMemoState("tutorial_quest_ex", "0");
                        qs.onTutorialClientEvent(i0 | TUTORIAL_PICK_BLUE_GEM);
                    } else
                        htmltext = "carl006.htm";
                } else if ((GetMemoStateEx == 1 || GetMemoStateEx == 2 || GetMemoStateEx == 0) && st.ownItemCount(tutorial_blue_gem) < 1)
                    htmltext = "helper_krenisk002.htm";
                else if ((GetMemoStateEx == 1 || GetMemoStateEx == 2 || GetMemoStateEx == 0) && st.ownItemCount(tutorial_blue_gem) > 0) {
                    htmltext = "helper_krenisk003.htm";
                    st.takeItems(tutorial_blue_gem, -1);
                    qs.setMemoState("tutorial_quest_ex", String.valueOf(3), true);
                    st.giveItems(q_recommend_of_kamael, 1);
                    st.startQuestTimer("timer_newbie_helper", 30000);
                    qs.setMemoState("tutorial_quest", String.valueOf(i0 | 4), true);
                    if ((talker_occupation == kamael_m_soldier || talker_occupation == kamael_f_soldier) && st.ownItemCount(soulshot_none_for_rookie) <= 0) {
                        st.giveItems(soulshot_none_for_rookie, 200);
                        st.playTutorialVoice("tutorial_voice_026");
                    }
                } else if (GetMemoStateEx == 3)
                    htmltext = "helper_krenisk004.htm";
                else if (GetMemoStateEx > 3)
                    htmltext = "helper_krenisk005.htm";
                break;
            case subelder_perwan:
                if (st.ownItemCount(q_recommend_of_kamael) > 0)
                    htmltext = "subelder_perwan001.htm";
                else if (st.ownItemCount(q_recommend_of_kamael) == 0 && GetMemoStateEx > 3)
                    htmltext = "subelder_perwan004.htm";
                else if (st.ownItemCount(q_recommend_of_kamael) == 0 && GetMemoStateEx <= 3)
                    htmltext = "subelder_perwan003.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        QuestState qs = st.getPlayer().getQuestState(255);
        if (qs == null) {
            return null;
        }
        int GetMemoStateEx = qs.getInt("tutorial_quest_ex");
        if ((GetMemoStateEx == 1 || GetMemoStateEx == 0) && !st.getPlayer().getPlayerVariables().getBoolean(PlayerVariables.QUEST_STATE_OF_255_TUTORIAL_VOICE_0)) {
            st.playTutorialVoice("tutorial_voice_011");
            st.showQuestionMark(3);
            qs.setMemoState("tutorial_quest", String.valueOf(2), true);
            st.getPlayer().getPlayerVariables().set(PlayerVariables.QUEST_STATE_OF_255_TUTORIAL_VOICE_0, "true", -1);
        }
        if ((GetMemoStateEx == 1 || GetMemoStateEx == 2 || GetMemoStateEx == 0) && st.ownItemCount(tutorial_blue_gem) < 1 && Rnd.get(2) <= 1) {
            st.dropItemDelay(npc, tutorial_blue_gem, 1);
            if (!st.getPlayer().getPlayerVariables().getBoolean(PlayerVariables.QUEST_STATE_OF_255_TUTORIAL_VOICE_1)) {
                st.soundEffect(SOUND_TUTORIAL);
                st.getPlayer().getPlayerVariables().set(PlayerVariables.QUEST_STATE_OF_255_TUTORIAL_VOICE_1, "true", -1);
            }
        }
        return null;
    }

    @Override
    public boolean isVisible() {
        return false;
    }

    private class RadarTask extends RunnableImpl {
        private final int x, y, z;
        private final Player player;

        RadarTask(final Player player) {
            this.x = -119692;
            this.y = 44504;
            this.z = 380;
            this.player = player;
        }

        @Override
        public void runImpl() {
            player.sendPacket(new RadarControl(RadarControl.RadarState.SHOW_RADAR, RadarControl.RadarType.ARROW, x, y, z));
        }
    }
}