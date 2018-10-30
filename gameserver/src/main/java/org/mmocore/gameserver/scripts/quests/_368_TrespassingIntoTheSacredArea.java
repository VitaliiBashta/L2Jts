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
 * @date 01/07/2015
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _368_TrespassingIntoTheSacredArea extends Quest {
    // npc
    private static final int priestess_restina = 30926;
    // mobs
    private static final int blade_stakato = 20794;
    private static final int blade_stakato_worker = 20795;
    private static final int blade_stakato_soldier = 20796;
    private static final int blade_stakato_drone = 20797;
    // questitem
    private static final int q_blade_stakatos_fang = 5881;

    public _368_TrespassingIntoTheSacredArea() {
        super(true);
        addStartNpc(priestess_restina);
        addKillId(blade_stakato, blade_stakato_worker, blade_stakato_soldier, blade_stakato_drone);
        addLevelCheck(36, 48);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == priestess_restina) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "priestess_restina_q0368_03.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=368&reply=1")) {
                st.exitQuest(true);
                st.soundEffect(SOUND_FINISH);
                htmltext = "priestess_restina_q0368_06.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=368&reply=2"))
                htmltext = "priestess_restina_q0368_07.htm";
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
                if (npcId == priestess_restina) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "priestess_restina_q0368_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "priestess_restina_q0368_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == priestess_restina) {
                    if (st.ownItemCount(q_blade_stakatos_fang) < 1)
                        htmltext = "priestess_restina_q0368_04.htm";
                    else if (st.ownItemCount(q_blade_stakatos_fang) >= 1) {
                        if (st.ownItemCount(q_blade_stakatos_fang) >= 10)
                            st.giveItems(ADENA_ID, st.ownItemCount(q_blade_stakatos_fang) * 250 + 9450);
                        else
                            st.giveItems(ADENA_ID, st.ownItemCount(q_blade_stakatos_fang) * 250 + 2000);
                        st.takeItems(q_blade_stakatos_fang, -1);
                        htmltext = "priestess_restina_q0368_05.htm";
                    }
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == blade_stakato) {
            if (Rnd.get(100) < 60) {
                st.giveItems(q_blade_stakatos_fang, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == blade_stakato_worker) {
            if (Rnd.get(100) < 57) {
                st.giveItems(q_blade_stakatos_fang, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == blade_stakato_soldier) {
            if (Rnd.get(100) < 61) {
                st.giveItems(q_blade_stakatos_fang, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == blade_stakato_drone) {
            if (Rnd.get(100) < 93) {
                st.giveItems(q_blade_stakatos_fang, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }
}
