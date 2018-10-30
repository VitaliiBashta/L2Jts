package org.mmocore.gameserver.scripts.quests;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.gameserver.manager.QuestManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import org.mmocore.gameserver.object.Player;
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
public class _166_MassOfDarkness extends Quest {
    // npc
    private final static int undres = 30130;
    private final static int doran = 30139;
    private final static int trudy = 30143;
    private final static int iria = 30135;
    // questitem
    private final static int undres_letter = 1088;
    private final static int ceremonial_dagger = 1089;
    private final static int dreviant_wine = 1090;
    private final static int garmiels_scripture = 1091;

    public _166_MassOfDarkness() {
        super(false);
        addStartNpc(undres);
        addTalkId(doran, trudy, iria);
        addQuestItem(ceremonial_dagger, dreviant_wine, garmiels_scripture, undres_letter);
        addLevelCheck(2, 5);
        addRaceCheck(PlayerRace.darkelf);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == undres) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.giveItems(undres_letter, 1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "undres_q0166_04.htm";
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
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == undres) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "undres_q0166_02.htm";
                            st.exitQuest(true);
                            break;
                        case RACE:
                            htmltext = "undres_q0166_00.htm";
                            break;
                        default:
                            htmltext = "undres_q0166_03.htm";
                            return htmltext;
                    }
                }
                break;
            case STARTED:
                if (npcId == undres) {
                    if (st.ownItemCount(undres_letter) == 1 && (st.ownItemCount(garmiels_scripture) < 1 || st.ownItemCount(dreviant_wine) < 1 || st.ownItemCount(ceremonial_dagger) < 1))
                        htmltext = "undres_q0166_05.htm";
                    else if (st.ownItemCount(undres_letter) == 1 && st.ownItemCount(ceremonial_dagger) == 1 && st.ownItemCount(dreviant_wine) == 1 && st.ownItemCount(garmiels_scripture) == 1) {
                        htmltext = "undres_q0166_06.htm";
                        st.takeItems(ceremonial_dagger, 1);
                        st.takeItems(dreviant_wine, 1);
                        st.takeItems(garmiels_scripture, 1);
                        st.takeItems(undres_letter, 1);
                        st.addExpAndSp(5672, 446);
                        st.giveItems(ADENA_ID, 2966);
                        if (player.getQuestState(41) == null) {
                            final Quest q = QuestManager.getQuest(41);
                            q.newQuestState(player, Quest.STARTED);
                            player.getQuestState(41).setMemoState("guide_mission", String.valueOf(1), true);
                            st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.DELIVERY_DUTY_COMPLETE_GO_FING_THE_NEWBIE_GUIDE, 5000, ScreenMessageAlign.TOP_CENTER, true));
                        } else if (player.getQuestState(41).getInt("guide_mission") % 10 != 1) {
                            player.getQuestState(41).setMemoState("guide_mission", String.valueOf(player.getQuestState(41).getInt("guide_mission") + 1), true);
                            st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.DELIVERY_DUTY_COMPLETE_GO_FING_THE_NEWBIE_GUIDE, 5000, ScreenMessageAlign.TOP_CENTER, true));
                        }
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                    }
                } else if (npcId == doran) {
                    if (st.ownItemCount(undres_letter) == 1 && st.ownItemCount(dreviant_wine) == 0) {
                        st.giveItems(dreviant_wine, 1);
                        htmltext = "doran_q0166_01.htm";
                        if (st.ownItemCount(ceremonial_dagger) > 0 && st.ownItemCount(garmiels_scripture) > 0) {
                            st.setCond(2);
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    } else if (st.ownItemCount(dreviant_wine) == 1)
                        htmltext = "doran_q0166_02.htm";
                } else if (npcId == trudy) {
                    if (st.ownItemCount(undres_letter) == 1 && st.ownItemCount(garmiels_scripture) == 0) {
                        st.giveItems(garmiels_scripture, 1);
                        htmltext = "trudy_q0166_01.htm";
                        if (st.ownItemCount(ceremonial_dagger) > 0 && st.ownItemCount(dreviant_wine) > 0) {
                            st.setCond(2);
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    } else if (st.ownItemCount(garmiels_scripture) == 1)
                        htmltext = "trudy_q0166_02.htm";
                } else if (npcId == iria) {
                    if (st.ownItemCount(undres_letter) == 1 && st.ownItemCount(ceremonial_dagger) == 0) {
                        st.giveItems(ceremonial_dagger, 1);
                        htmltext = "iria_q0166_01.htm";
                        if (st.ownItemCount(dreviant_wine) > 0 && st.ownItemCount(garmiels_scripture) > 0) {
                            st.setCond(2);
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    } else if (st.ownItemCount(ceremonial_dagger) == 1)
                        htmltext = "iria_q0166_02.htm";
                }
                break;
        }
        return htmltext;
    }
}