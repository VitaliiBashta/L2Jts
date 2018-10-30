package org.mmocore.gameserver.scripts.quests;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
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
 * @date 27/10/2015
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _272_WrathOfAncestors extends Quest {
    // npc
    private static final int seer_livina = 30572;
    // mobs
    private static final int goblin_grave_robber = 20319;
    private static final int goblin_tomb_raider = 20320;
    // questitem
    private static final int grave_robbers_head = 1474;

    public _272_WrathOfAncestors() {
        super(false);
        addStartNpc(seer_livina);
        addKillId(goblin_grave_robber, goblin_tomb_raider);
        addQuestItem(grave_robbers_head);
        addLevelCheck(5, 16);
        addRaceCheck(PlayerRace.orc);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == seer_livina) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "seer_livina_q0272_03.htm";
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
                if (npcId == seer_livina) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "seer_livina_q0272_01.htm";
                            st.exitQuest(true);
                            break;
                        case RACE:
                            htmltext = "seer_livina_q0272_00.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "seer_livina_q0272_02.htm";
                            return htmltext;
                    }
                }
                break;
            case STARTED:
                if (npcId == seer_livina) {
                    if (st.ownItemCount(grave_robbers_head) < 50)
                        htmltext = "seer_livina_q0272_04.htm";
                    else {
                        st.giveItems(ADENA_ID, 1500);
                        st.takeItems(grave_robbers_head, -1);
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(true);
                        htmltext = "seer_livina_q0272_05.htm";
                    }
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == goblin_grave_robber || npcId == goblin_tomb_raider) {
            if (st.ownItemCount(grave_robbers_head) < 50) {
                if (st.ownItemCount(grave_robbers_head) < 49)
                    st.soundEffect(SOUND_ITEMGET);
                else {
                    st.setCond(2);
                    st.soundEffect(SOUND_MIDDLE);
                }
                st.giveItems(grave_robbers_head, 1);
            }
        }
        return null;
    }
}