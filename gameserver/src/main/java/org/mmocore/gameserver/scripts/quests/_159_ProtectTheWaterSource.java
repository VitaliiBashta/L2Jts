package org.mmocore.gameserver.scripts.quests;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.0
 * @date 22/01/2015
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _159_ProtectTheWaterSource extends Quest {
    // npc
    private static final int ozzy = 30154;
    // mobs
    private static final int plague_zombie = 27017;
    // questitem
    private static final int plague_dust = 1035;
    private static final int hyacinth_charm1 = 1071;
    private static final int hyacinth_charm2 = 1072;

    public _159_ProtectTheWaterSource() {
        super(false);
        addStartNpc(ozzy);
        addKillId(plague_zombie);
        addQuestItem(plague_dust, hyacinth_charm1, hyacinth_charm2);
        addLevelCheck(12, 18);
        addRaceCheck(PlayerRace.elf);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == ozzy) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                if (st.ownItemCount(hyacinth_charm1) == 0)
                    st.giveItems(hyacinth_charm1, 1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "ozzy_q0159_04.htm";
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
                if (npcId == ozzy) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "ozzy_q0159_02.htm";
                            st.exitQuest(true);
                            break;
                        case RACE:
                            htmltext = "ozzy_q0159_00.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "ozzy_q0159_03.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == ozzy) {
                    if (st.ownItemCount(hyacinth_charm1) != 0 && st.ownItemCount(plague_dust) == 0)
                        htmltext = "ozzy_q0159_05.htm";
                    else if (st.ownItemCount(hyacinth_charm1) != 0 && st.ownItemCount(plague_dust) != 0) {
                        st.setCond(3);
                        st.takeItems(plague_dust, -1);
                        st.takeItems(hyacinth_charm1, -1);
                        if (st.ownItemCount(hyacinth_charm2) == 0)
                            st.giveItems(hyacinth_charm2, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "ozzy_q0159_06.htm";
                    } else if (st.ownItemCount(hyacinth_charm2) != 0 && st.ownItemCount(plague_dust) < 5)
                        htmltext = "ozzy_q0159_07.htm";
                    else if (st.ownItemCount(hyacinth_charm2) != 0 && st.ownItemCount(plague_dust) >= 5) {
                        st.takeItems(plague_dust, -1);
                        st.takeItems(hyacinth_charm2, -1);
                        st.giveItems(ADENA_ID, 18250);
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                        htmltext = "ozzy_q0159_08.htm";
                    }
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == plague_zombie) {
            if (st.ownItemCount(hyacinth_charm1) > 0 && Rnd.get(100) < 40 && st.ownItemCount(plague_dust) == 0) {
                st.setCond(2);
                st.giveItems(plague_dust, 1);
                st.soundEffect(SOUND_MIDDLE);
            } else if (st.ownItemCount(hyacinth_charm2) > 0 && Rnd.get(100) < 40 && st.ownItemCount(plague_dust) < 5) {
                if (st.ownItemCount(plague_dust) >= 4) {
                    st.setCond(4);
                    st.giveItems(plague_dust, 1);
                    st.soundEffect(SOUND_MIDDLE);
                } else {
                    st.giveItems(plague_dust, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        }
        return null;
    }
}