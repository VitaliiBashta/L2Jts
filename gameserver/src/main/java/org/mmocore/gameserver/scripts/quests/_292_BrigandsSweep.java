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
 * @date 27/10/2015
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _292_BrigandsSweep extends Quest {
    // npc
    private static final int elder_spiron = 30532;
    private static final int elder_balanki = 30533;
    // mobs
    private static final int goblin_brigand = 20322;
    private static final int goblin_brigand_leader = 20323;
    private static final int goblin_brigand_sub_ldr = 20324;
    private static final int goblin_snooper = 20327;
    private static final int goblin_lord = 20528;
    // questitem
    private static final int goblin_necklace = 1483;
    private static final int goblin_pendant = 1484;
    private static final int goblin_lord_pendant = 1485;
    private static final int suspicious_memo = 1486;
    private static final int suspicious_contract = 1487;

    public _292_BrigandsSweep() {
        super(false);
        addStartNpc(elder_spiron);
        addTalkId(elder_balanki);
        addKillId(goblin_brigand, goblin_brigand_leader, goblin_brigand_sub_ldr, goblin_snooper, goblin_lord);
        addQuestItem(goblin_necklace, goblin_pendant, goblin_lord_pendant, suspicious_memo, suspicious_contract);
        addLevelCheck(5, 18);
        addRaceCheck(PlayerRace.dwarf);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == elder_spiron) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "elder_spiron_q0292_03.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=292&reply=1")) {
                st.takeItems(suspicious_memo, -1);
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                htmltext = "elder_spiron_q0292_06.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=292&reply=2"))
                htmltext = "elder_spiron_q0292_07.htm";
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
                if (npcId == elder_spiron) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "elder_spiron_q0292_01.htm";
                            st.exitQuest(true);
                            break;
                        case RACE:
                            htmltext = "elder_spiron_q0292_00.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "elder_spiron_q0292_02.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == elder_spiron) {
                    if (st.ownItemCount(goblin_necklace) < 1 && st.ownItemCount(goblin_pendant) < 1 && st.ownItemCount(goblin_lord_pendant) < 1 && st.ownItemCount(suspicious_memo) == 0 && st.ownItemCount(suspicious_contract) == 0)
                        htmltext = "elder_spiron_q0292_04.htm";
                    else if (st.ownItemCount(goblin_necklace) + st.ownItemCount(goblin_pendant) + st.ownItemCount(goblin_lord_pendant) >= 1 && st.ownItemCount(suspicious_memo) == 0 && st.ownItemCount(suspicious_contract) == 0) {
                        htmltext = "elder_spiron_q0292_05.htm";
                        if (st.ownItemCount(goblin_necklace) + st.ownItemCount(goblin_pendant) + st.ownItemCount(goblin_lord_pendant) >= 10)
                            st.giveItems(ADENA_ID, st.ownItemCount(goblin_necklace) * 12 + st.ownItemCount(goblin_pendant) * 36 + st.ownItemCount(goblin_lord_pendant) * 33 + 1000);
                        else
                            st.giveItems(ADENA_ID, st.ownItemCount(goblin_necklace) * 12 + st.ownItemCount(goblin_pendant) * 36 + st.ownItemCount(goblin_lord_pendant) * 33);
                        st.takeItems(goblin_necklace, -1);
                        st.takeItems(goblin_pendant, -1);
                        st.takeItems(goblin_lord_pendant, -1);
                    } else if (st.ownItemCount(suspicious_memo) == 1 && st.ownItemCount(suspicious_contract) == 0) {
                        htmltext = "elder_spiron_q0292_08.htm";
                        if (st.ownItemCount(goblin_necklace) + st.ownItemCount(goblin_pendant) + st.ownItemCount(goblin_lord_pendant) >= 1) {
                            if (st.ownItemCount(goblin_necklace) + st.ownItemCount(goblin_pendant) + st.ownItemCount(goblin_lord_pendant) >= 10)
                                st.giveItems(ADENA_ID, st.ownItemCount(goblin_necklace) * 12 + st.ownItemCount(goblin_pendant) * 36 + st.ownItemCount(goblin_lord_pendant) * 33 + 1000);
                            else
                                st.giveItems(ADENA_ID, st.ownItemCount(goblin_necklace) * 12 + st.ownItemCount(goblin_pendant) * 36 + st.ownItemCount(goblin_lord_pendant) * 33);
                        }
                        st.takeItems(goblin_necklace, -1);
                        st.takeItems(goblin_pendant, -1);
                        st.takeItems(goblin_lord_pendant, -1);
                    } else if (st.ownItemCount(suspicious_memo) >= 2 && st.ownItemCount(suspicious_contract) == 0) {
                        htmltext = "elder_spiron_q0292_09.htm";
                        if (st.ownItemCount(goblin_necklace) + st.ownItemCount(goblin_pendant) + st.ownItemCount(goblin_lord_pendant) >= 1) {
                            if (st.ownItemCount(goblin_necklace) + st.ownItemCount(goblin_pendant) + st.ownItemCount(goblin_lord_pendant) >= 10)
                                st.giveItems(ADENA_ID, st.ownItemCount(goblin_necklace) * 12 + st.ownItemCount(goblin_pendant) * 36 + st.ownItemCount(goblin_lord_pendant) * 33 + 1000);
                            else
                                st.giveItems(ADENA_ID, st.ownItemCount(goblin_necklace) * 12 + st.ownItemCount(goblin_pendant) * 36 + st.ownItemCount(goblin_lord_pendant) * 33);
                        }
                        st.takeItems(goblin_necklace, -1);
                        st.takeItems(goblin_pendant, -1);
                        st.takeItems(goblin_lord_pendant, -1);
                    } else if (st.ownItemCount(suspicious_memo) == 0 && st.ownItemCount(suspicious_contract) == 1) {
                        htmltext = "elder_spiron_q0292_10.htm";
                        if (st.ownItemCount(goblin_necklace) + st.ownItemCount(goblin_pendant) + st.ownItemCount(goblin_lord_pendant) >= 1) {
                            if (st.ownItemCount(goblin_necklace) + st.ownItemCount(goblin_pendant) + st.ownItemCount(goblin_lord_pendant) >= 10)
                                st.giveItems(ADENA_ID, st.ownItemCount(goblin_necklace) * 12 + st.ownItemCount(goblin_pendant) * 36 + st.ownItemCount(goblin_lord_pendant) * 33 + 1000);
                            else
                                st.giveItems(ADENA_ID, st.ownItemCount(goblin_necklace) * 12 + st.ownItemCount(goblin_pendant) * 36 + st.ownItemCount(goblin_lord_pendant) * 33);
                        }
                        st.giveItems(ADENA_ID, st.ownItemCount(suspicious_contract) * 120 + 1000);
                        st.takeItems(goblin_necklace, -1);
                        st.takeItems(goblin_pendant, -1);
                        st.takeItems(goblin_lord_pendant, -1);
                        st.takeItems(suspicious_contract, -1);
                    }
                } else if (npcId == elder_balanki) {
                    if (st.ownItemCount(suspicious_contract) == 0)
                        htmltext = "balanki_q0292_01.htm";
                    else if (st.ownItemCount(suspicious_contract) == 1) {
                        st.giveItems(ADENA_ID, st.ownItemCount(suspicious_contract) * 120 + 500);
                        st.takeItems(suspicious_contract, -1);
                        htmltext = "balanki_q0292_02.htm";
                    }
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == goblin_brigand || npcId == goblin_brigand_leader || npcId == goblin_brigand_sub_ldr || npcId == goblin_snooper || npcId == goblin_lord) {
            int i0 = Rnd.get(10);
            if (i0 > 5) {
                st.giveItems(goblin_necklace, 1);
                st.soundEffect(SOUND_ITEMGET);
            } else if (i0 > 4) {
                if (st.ownItemCount(suspicious_contract) < 1 && st.ownItemCount(suspicious_memo) < 3) {
                    st.giveItems(suspicious_memo, 1);
                    st.soundEffect(SOUND_ITEMGET);
                } else if (st.ownItemCount(suspicious_contract) < 1 && st.ownItemCount(suspicious_memo) == 3) {
                    st.setCond(2);
                    st.giveItems(suspicious_contract, 1);
                    st.takeItems(suspicious_memo, -1);
                    st.soundEffect(SOUND_MIDDLE);
                }
            }
        }
        return null;
    }
}