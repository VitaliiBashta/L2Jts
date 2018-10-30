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
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.0
 * @date 04/01/2015
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _106_ForgottenTruth extends Quest {
    // npc
    private static final int tetrarch_thifiell = 30358;
    private static final int karta = 30133;
    // mobs
    private static final int tumran_orc_brigand = 27070;
    // questitem
    private static final int onyx_talisman1 = 984;
    private static final int onyx_talisman2 = 985;
    private static final int ancient_scroll = 986;
    private static final int ancient_clay_tablet = 987;
    private static final int kartas_translation = 988;
    // etcitem
    private static final int soulshot_none_for_rookie = 5789;
    private static final int spiritshot_none_for_rookie = 5790;
    private static final int lesser_healing_potion = 1060;
    private static final int echo_crystal_solitude = 4414;
    private static final int echo_crystal_feast = 4415;
    private static final int echo_crystal_celebration = 4416;
    private static final int echo_crystal_love = 4413;
    private static final int echo_crystal_battle = 4412;
    private static final int eldritch_dagger = 989;

    public _106_ForgottenTruth() {
        super(false);
        addStartNpc(tetrarch_thifiell);
        addTalkId(karta);
        addKillId(tumran_orc_brigand);
        addQuestItem(onyx_talisman1, onyx_talisman2, ancient_scroll, ancient_clay_tablet, kartas_translation);
        addLevelCheck(10, 15);
        addRaceCheck(PlayerRace.darkelf);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == tetrarch_thifiell) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                st.giveItems(onyx_talisman1, 1);
                htmltext = "tetrarch_thifiell_q0106_05.htm";
            } else if (event.equalsIgnoreCase("reply_1"))
                htmltext = "tetrarch_thifiell_q0106_04.htm";
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
                if (npcId == tetrarch_thifiell) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "tetrarch_thifiell_q0106_02.htm";
                            st.exitQuest(true);
                            break;
                        case RACE:
                            htmltext = "tetrarch_thifiell_q0106_00.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "tetrarch_thifiell_q0106_03.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == tetrarch_thifiell) {
                    if ((st.ownItemCount(onyx_talisman1) > 0 || st.ownItemCount(onyx_talisman2) > 0) && st.ownItemCount(kartas_translation) == 0)
                        htmltext = "tetrarch_thifiell_q0106_06.htm";
                    else if (st.ownItemCount(kartas_translation) > 0) {
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
                        htmltext = "tetrarch_thifiell_q0106_07.htm";
                        st.takeItems(kartas_translation, 1);
                        st.giveItems(eldritch_dagger, 1);
                        st.addExpAndSp(24195, 2074);
                        st.giveItems(ADENA_ID, 10266);
                        st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                        st.exitQuest(false);
                        st.soundEffect(SOUND_FINISH);
                    }
                } else if (npcId == karta) {
                    if (st.ownItemCount(onyx_talisman1) > 0) {
                        htmltext = "karta_q0106_01.htm";
                        st.takeItems(onyx_talisman1, 1);
                        st.giveItems(onyx_talisman2, 1);
                        st.setCond(2);
                        st.soundEffect(SOUND_MIDDLE);
                    } else if (st.ownItemCount(onyx_talisman2) > 0 && (st.ownItemCount(ancient_scroll) == 0 || st.ownItemCount(ancient_clay_tablet) == 0))
                        htmltext = "karta_q0106_02.htm";
                    else if (st.ownItemCount(ancient_scroll) > 0 && st.ownItemCount(ancient_clay_tablet) > 0) {
                        htmltext = "karta_q0106_03.htm";
                        st.takeItems(onyx_talisman2, -1);
                        st.takeItems(ancient_scroll, 1);
                        st.takeItems(ancient_clay_tablet, 1);
                        st.giveItems(kartas_translation, 1);
                        st.setCond(4);
                        st.soundEffect(SOUND_MIDDLE);
                    } else if (st.ownItemCount(kartas_translation) > 0)
                        htmltext = "karta_q0106_04.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == tumran_orc_brigand) {
            if (st.ownItemCount(onyx_talisman2) > 0 && Rnd.get(100) < 20) {
                if (st.ownItemCount(ancient_scroll) == 0) {
                    st.giveItems(ancient_scroll, 1);
                    st.soundEffect(SOUND_MIDDLE);
                } else if (st.ownItemCount(ancient_clay_tablet) == 0) {
                    st.setCond(3);
                    st.giveItems(ancient_clay_tablet, 1);
                    st.soundEffect(SOUND_MIDDLE);
                }
            }
        }
        return null;
    }
}