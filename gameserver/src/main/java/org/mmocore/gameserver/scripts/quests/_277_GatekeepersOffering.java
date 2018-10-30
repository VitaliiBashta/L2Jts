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
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _277_GatekeepersOffering extends Quest {
    // npc
    private static final int gatekeeper_tamil = 30576;

    // mobs
    private static final int greystone_golem = 20333;

    // questitem
    private static final int starstone1 = 1572;

    // etcitem
    private static final int gatekeeper_charm = 1658;

    public _277_GatekeepersOffering() {
        super(false);
        addStartNpc(gatekeeper_tamil);
        addKillId(greystone_golem);
        addQuestItem(starstone1);
        addLevelCheck(15, 21);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();

        if (npcId == gatekeeper_tamil)
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "gatekeeper_tamil_q0277_03.htm";
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
                if (npcId == gatekeeper_tamil) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "gatekeeper_tamil_q0277_01.htm";
                            break;
                        default:
                            htmltext = "gatekeeper_tamil_q0277_02.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == gatekeeper_tamil)
                    if (st.ownItemCount(starstone1) < 20)
                        htmltext = "gatekeeper_tamil_q0277_04.htm";
                    else if (st.ownItemCount(starstone1) >= 20) {
                        st.giveItems(gatekeeper_charm, 2);
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(true);
                        htmltext = "gatekeeper_tamil_q0277_05.htm";
                    }
                break;
        }

        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();

        if (npcId == greystone_golem)
            if (Rnd.get(2) == 0)
                if (st.ownItemCount(starstone1) >= 19) {
                    st.setCond(2);
                    st.giveItems(starstone1, 1);
                    st.soundEffect(SOUND_MIDDLE);
                } else {
                    st.giveItems(starstone1, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }

        return null;
    }
}