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
public class _203_ElfTutorial extends Quest {
    // npc
    private static final int nerupa = 30370; // Grand Master
    private static final int mother_temp_a = 30400; // Newbie Helper
    // mobs
    private static final int tutorial_gremlin = 18342;
    // questitem
    private static final int leaf_of_mothertree = 1069;
    private static final int tutorial_blue_gem = 6353;
    // etcitem
    private static final int soulshot_none_for_rookie = 5789;
    private static final int spiritshot_none_for_rookie = 5790;
    // Tutorial Quest Event
    private static final int TUTORIAL_PICK_BLUE_GEM = 0x00100000;

    public _203_ElfTutorial() {
        super(false);
        addTalkId(nerupa, mother_temp_a);
        addFirstTalkId(nerupa, mother_temp_a);
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
        int class_level = st.getPlayer().getPlayerClassComponent().getClassId().level();
        boolean isMage = (player.getPlayerClassComponent().getClassId().getRace() != PlayerRace.orc) && player.getPlayerClassComponent().getClassId().isMage();
        // EventHandler TIMER_FIRED_EX
        if (event.equalsIgnoreCase("timer_newbie_helper")) {
            if (GetMemoStateEx == 0) {
                if (class_level == 0)
                    st.playTutorialVoice("tutorial_voice_009a");
                else
                    st.playTutorialVoice("tutorial_voice_009b");
                qs.setMemoState("tutorial_quest_ex", String.valueOf(1), true);
            }
            if (GetMemoStateEx == 3)
                st.playTutorialVoice("tutorial_voice_010c");
            return null;
        } else if (event.equalsIgnoreCase("timer_grand_master")) {
            if (GetMemoStateEx >= 4) {
                st.showQuestionMark(7);
                st.soundEffect(SOUND_TUTORIAL);
                st.playTutorialVoice("tutorial_voice_025");
            }
            return null;
        } else if (event.equalsIgnoreCase("reply_31") && st.ownItemCount(leaf_of_mothertree) > 0) {
            if (!isMage && st.ownItemCount(soulshot_none_for_rookie) <= 200) {
                st.giveItems(soulshot_none_for_rookie, 200);
                st.playTutorialVoice("tutorial_voice_026");
                st.addExpAndSp(0, 50);
            } else if (isMage && st.ownItemCount(spiritshot_none_for_rookie) <= 100) {
                st.giveItems(spiritshot_none_for_rookie, 100);
                st.playTutorialVoice("tutorial_voice_027");
                st.addExpAndSp(0, 50);
            }
            htmltext = "nerupa002.htm";
            st.takeItems(leaf_of_mothertree, -1);
            st.startQuestTimer("timer_grand_master", 60000);
            if (GetMemoStateEx <= 3)
                qs.setMemoState("tutorial_quest_ex", String.valueOf(4), true);
        } else if (event.equalsIgnoreCase("reply_41")) {
            htmltext = "nerupa005.htm";
            ThreadPoolManager.getInstance().schedule(new RadarTask(-119692, 44504, 380, st.getPlayer()), 200L);
            st.getPlayer().teleToLocation(-120050, 44500, 360);
        } else if (event.equalsIgnoreCase("reply_42")) {
            htmltext = "nerupa006.htm";
            ThreadPoolManager.getInstance().schedule(new RadarTask(45475, 48359, -3060, st.getPlayer()), 200L);
        }
        return htmltext;
    }

    @Override
    public String onFirstTalk(NpcInstance npc, Player player) {
        String htmltext = "";
        int npcId = npc.getNpcId();
        boolean isMage = (player.getPlayerClassComponent().getClassId().getRace() != PlayerRace.orc) && player.getPlayerClassComponent().getClassId().isMage();
        // tutorial_quest_npc - главный квест
        QuestState qs = player.getQuestState(255);
        if (qs == null) {
            return htmltext;
        }
        // [elf_tutorial] 203 Elf's Tutorial
        QuestState st = player.getQuestState(getId());
        if (st == null) {
            newQuestState(player, STARTED);
            st = player.getQuestState(getId());
        }
        int GetMemoStateEx = qs.getInt("tutorial_quest_ex");
        int GetMemoState = qs.getInt("tutorial_quest");
        switch (npcId) {
            case mother_temp_a:
                int i0 = GetMemoState & 2147483392;
                if (GetMemoStateEx < 0) {
                    if (player.getPlayerTemplateComponent().getPlayerRace() == PlayerRace.elf) {
                        st.startQuestTimer("timer_newbie_helper", 30000);
                        if (!isMage)
                            htmltext = "carl001.htm";
                        else
                            htmltext = "doff001.htm";
                        qs.setMemoState("tutorial_quest_ex", "0");
                        qs.onTutorialClientEvent(i0 | TUTORIAL_PICK_BLUE_GEM);
                    } else
                        htmltext = "carl006.htm";
                } else if ((GetMemoStateEx == 1 || GetMemoStateEx == 2 || GetMemoStateEx == 0) && st.ownItemCount(tutorial_blue_gem) < 1) {
                    if (!isMage)
                        htmltext = "carl002.htm";
                    else
                        htmltext = "doff002.htm";
                } else if ((GetMemoStateEx == 1 || GetMemoStateEx == 2 || GetMemoStateEx == 0) && st.ownItemCount(tutorial_blue_gem) > 0) {
                    st.takeItems(tutorial_blue_gem, -1);
                    qs.setMemoState("tutorial_quest_ex", String.valueOf(3), true);
                    st.giveItems(leaf_of_mothertree, 1);
                    st.startQuestTimer("timer_newbie_helper", 30000);
                    qs.setMemoState("tutorial_quest", String.valueOf(i0 | 4), true);
                    if (!isMage && st.ownItemCount(soulshot_none_for_rookie) <= 0) {
                        st.giveItems(soulshot_none_for_rookie, 200);
                        st.playTutorialVoice("tutorial_voice_026");
                        htmltext = "mother_temp_a003f.htm";
                    }
                    if (isMage && st.ownItemCount(spiritshot_none_for_rookie) <= 0) {
                        st.playTutorialVoice("tutorial_voice_027");
                        st.giveItems(spiritshot_none_for_rookie, 100);
                        htmltext = "mother_temp_a003m.htm";
                    }
                } else if (GetMemoStateEx == 3)
                    htmltext = "mother_temp_a004.htm";
                else if (GetMemoStateEx > 3)
                    htmltext = "carl005.htm";
                break;
            case nerupa:
                if (st.ownItemCount(leaf_of_mothertree) > 0)
                    htmltext = "nerupa001.htm";
                else if (st.ownItemCount(leaf_of_mothertree) == 0 && GetMemoStateEx > 3)
                    htmltext = "nerupa004.htm";
                else if (st.ownItemCount(leaf_of_mothertree) == 0 && GetMemoStateEx <= 3)
                    htmltext = "nerupa003.htm";
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

        RadarTask(final int x, final int y, final int z, final Player player) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.player = player;
        }

        @Override
        public void runImpl() {
            player.sendPacket(new RadarControl(RadarControl.RadarState.SHOW_RADAR, RadarControl.RadarType.ARROW, x, y, z));
        }
    }
}