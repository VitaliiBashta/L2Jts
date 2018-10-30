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
 * @version 1.1
 * @date 13/04/2016
 * @lastedit 13/04/2016
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _240_ImTheOnlyOneYouCanTrust extends Quest {
    // npc
    private static final int kintaijin = 32640;
    // mobs
    private static final int n_spike_stakato = 22617;
    private static final int n_cannibal_stakato = 22624;
    private static final int n_leader_canni_stakato = 22625;
    private static final int n_leader_canni_stakato_c = 22626;
    // questitem
    private static final int q240_claw_of_stakato = 14879;

    public _240_ImTheOnlyOneYouCanTrust() {
        super(false);
        addStartNpc(kintaijin);
        addKillId(n_spike_stakato, n_cannibal_stakato, n_leader_canni_stakato, n_leader_canni_stakato_c);
        addQuestItem(q240_claw_of_stakato);
        addLevelCheck(81);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == kintaijin) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("cannot_believe_anyone", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "kintaijin_q0240_09.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int GetMemoState = st.getInt("cannot_believe_anyone");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == kintaijin) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "kintaijin_q0240_02.htm";
                            break;
                        default:
                            htmltext = "kintaijin_q0240_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == kintaijin) {
                    if (GetMemoState == 1 && st.ownItemCount(q240_claw_of_stakato) == 0)
                        htmltext = "kintaijin_q0240_10.htm";
                    else if (GetMemoState == 1 && st.ownItemCount(q240_claw_of_stakato) >= 1 && st.ownItemCount(q240_claw_of_stakato) < 25)
                        htmltext = "kintaijin_q0240_11.htm";
                    else if (GetMemoState == 2 && st.ownItemCount(q240_claw_of_stakato) >= 25) {
                        st.giveItems(ADENA_ID, 147200);
                        st.addExpAndSp(589542, 36800);
                        st.takeItems(q240_claw_of_stakato, -1);
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                        htmltext = "kintaijin_q0240_12.htm";
                    }
                }
                break;
            case COMPLETED:
                if (npcId == kintaijin)
                    htmltext = "kintaijin_q0240_03.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int GetMemoState = st.getInt("cannot_believe_anyone");
        if (npcId == n_spike_stakato) {
            if (GetMemoState == 1) {
                int i0 = Rnd.get(1000);
                if (i0 < 804)
                    if (st.ownItemCount(q240_claw_of_stakato) == 24) {
                        st.giveItems(q240_claw_of_stakato, 1);
                        st.setCond(2);
                        st.setMemoState("cannot_believe_anyone", String.valueOf(2), true);
                        st.soundEffect(SOUND_MIDDLE);
                    } else {
                        st.giveItems(q240_claw_of_stakato, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            }
        } else if (npcId == n_cannibal_stakato) {
            if (GetMemoState == 1) {
                int i0 = Rnd.get(1000);
                if (i0 < 785)
                    if (st.ownItemCount(q240_claw_of_stakato) == 24) {
                        st.giveItems(q240_claw_of_stakato, 1);
                        st.setCond(2);
                        st.setMemoState("cannot_believe_anyone", String.valueOf(2), true);
                        st.soundEffect(SOUND_MIDDLE);
                    } else {
                        st.giveItems(q240_claw_of_stakato, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            }
        } else if (npcId == n_leader_canni_stakato) {
            if (GetMemoState == 1) {
                int i0 = Rnd.get(1000);
                if (i0 < 845)
                    if (st.ownItemCount(q240_claw_of_stakato) == 24) {
                        st.giveItems(q240_claw_of_stakato, 1);
                        st.setCond(2);
                        st.setMemoState("cannot_believe_anyone", String.valueOf(2), true);
                        st.soundEffect(SOUND_MIDDLE);
                    } else {
                        st.giveItems(q240_claw_of_stakato, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            }
        } else if (npcId == n_leader_canni_stakato_c) {
            if (GetMemoState == 1) {
                int i0 = Rnd.get(1000);
                if (i0 < 170) {
                    if (st.ownItemCount(q240_claw_of_stakato) == 24) {
                        st.giveItems(q240_claw_of_stakato, 1);
                        st.setCond(2);
                        st.setMemoState("cannot_believe_anyone", String.valueOf(2), true);
                        st.soundEffect(SOUND_MIDDLE);
                    } else if (st.ownItemCount(q240_claw_of_stakato) == 23) {
                        st.giveItems(q240_claw_of_stakato, 2);
                        st.setCond(2);
                        st.setMemoState("cannot_believe_anyone", String.valueOf(2), true);
                        st.soundEffect(SOUND_MIDDLE);
                    } else {
                        st.giveItems(q240_claw_of_stakato, 2);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                } else if (st.ownItemCount(q240_claw_of_stakato) == 24) {
                    st.giveItems(q240_claw_of_stakato, 1);
                    st.setCond(2);
                    st.setMemoState("cannot_believe_anyone", String.valueOf(2), true);
                    st.soundEffect(SOUND_MIDDLE);
                } else {
                    st.giveItems(q240_claw_of_stakato, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        }
        return null;
    }
}