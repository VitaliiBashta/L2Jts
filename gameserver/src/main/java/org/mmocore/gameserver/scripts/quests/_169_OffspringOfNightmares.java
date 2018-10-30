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
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.0
 * @date 05/01/2015
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _169_OffspringOfNightmares extends Quest {
    // npc
    private static final int vlasti = 30145;
    // mobs
    private static final int dark_horror = 20105;
    private static final int lesser_dark_horror = 20025;
    // questitem
    private static final int cracked_skull = 1030;
    private static final int perfect_skull = 1031;
    // etcitem
    private static final int bone_gaiters = 31;

    public _169_OffspringOfNightmares() {
        super(false);
        addStartNpc(vlasti);
        addKillId(dark_horror, lesser_dark_horror);
        addQuestItem(cracked_skull, perfect_skull);
        addLevelCheck(15, 20);
        addRaceCheck(PlayerRace.darkelf);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        Player player = st.getPlayer();
        if (player == null) {
            return null;
        }
        int npcId = npc.getNpcId();
        if (npcId == vlasti) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "vlasti_q0169_04.htm";
            } else if (event.equalsIgnoreCase("reply_1")) {
                if (st.ownItemCount(perfect_skull) >= 1) {
                    htmltext = "vlasti_q0169_08.htm";
                    st.giveItems(bone_gaiters, 1);
                    st.giveItems(ADENA_ID, 17030 + 10 * st.ownItemCount(cracked_skull));
                    st.getPlayer().addExpAndSp(17475, 818);
                    st.takeItems(cracked_skull, -1);
                    st.takeItems(perfect_skull, -1);
                    if (player.getQuestState(41) == null) {
                        final Quest q = QuestManager.getQuest(41);
                        q.newQuestState(player, Quest.STARTED);
                        player.getQuestState(41).setMemoState("guide_mission", String.valueOf(100000), true);
                        st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.LAST_DUTY_COMPLETE_GO_FIND_THE_NEWBIE_GUIDE, 5000, ScreenMessageAlign.TOP_CENTER, true));
                    } else if (player.getQuestState(41).getInt("guide_mission") % 100000000 / 10000000 != 1) {
                        player.getQuestState(41).setMemoState("guide_mission", String.valueOf(player.getQuestState(41).getInt("guide_mission") + 10000000), true);
                        st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.LAST_DUTY_COMPLETE_GO_FIND_THE_NEWBIE_GUIDE, 5000, ScreenMessageAlign.TOP_CENTER, true));
                    }
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == vlasti) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "vlasti_q0169_02.htm";
                            st.exitQuest(true);
                            break;
                        case RACE:
                            htmltext = "vlasti_q0169_00.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "vlasti_q0169_03.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == vlasti) {
                    if (st.ownItemCount(cracked_skull) >= 1 && st.ownItemCount(perfect_skull) == 0)
                        htmltext = "vlasti_q0169_06.htm";
                    else if (st.ownItemCount(perfect_skull) >= 1)
                        htmltext = "vlasti_q0169_07.htm";
                    else if (st.ownItemCount(cracked_skull) == 0 && st.ownItemCount(perfect_skull) == 0)
                        htmltext = "vlasti_q0169_05.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == dark_horror || npcId == lesser_dark_horror) {
            if (Rnd.get(10) > 7 && st.ownItemCount(perfect_skull) == 0) {
                st.giveItems(perfect_skull, 1);
                st.setCond(2);
                st.soundEffect(SOUND_MIDDLE);
            } else if (Rnd.get(10) > 4 && st.ownItemCount(cracked_skull) == 0) {
                st.giveItems(cracked_skull, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }
}