package org.mmocore.gameserver.scripts.quests;

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
 * @date 27/01/2015
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _319_ScentOfDeath extends Quest {
    // npc
    private static final int mina = 30138;
    // mobs
    private static final int marsh_zombie = 20015;
    private static final int marsh_zombie_pointer = 20020;
    // questitem
    private static final int zombie_skin = 1045;
    // etcitem
    private static final int lesser_healing_potion = 1060;

    public _319_ScentOfDeath() {
        super(false);
        addStartNpc(mina);
        addKillId(marsh_zombie, marsh_zombie_pointer);
        addQuestItem(zombie_skin);
        addLevelCheck(11, 18);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == mina) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "mina_q0319_04.htm";
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
                if (npcId == mina) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "mina_q0319_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "mina_q0319_03.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == mina) {
                    if (st.ownItemCount(zombie_skin) < 5)
                        htmltext = "mina_q0319_05.htm";
                    else if (st.ownItemCount(zombie_skin) >= 5) {
                        st.giveItems(ADENA_ID, 3350);
                        st.giveItems(lesser_healing_potion, 1);
                        st.takeItems(zombie_skin, -1);
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(true);
                        htmltext = "mina_q0319_06.htm";
                    }
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == marsh_zombie || npcId == marsh_zombie_pointer) {
            if (Rnd.get(10) > 7) {
                st.giveItems(zombie_skin, 1);
                if (st.ownItemCount(zombie_skin) >= 5) {
                    st.setCond(2);
                    st.soundEffect(SOUND_MIDDLE);
                } else
                    st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }
}
