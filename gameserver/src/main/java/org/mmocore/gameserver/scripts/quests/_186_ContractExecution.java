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
 * @date 20/03/2016
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _186_ContractExecution extends Quest {
    // npc
    private static final int blueprint_seller_luka = 31437;
    private static final int researcher_lorain = 30673;
    private static final int maestro_nikola = 30621;
    // mobs
    private static final int leto_lizardman = 20577;
    private static final int leto_lizardman_archer = 20578;
    private static final int leto_lizardman_soldier = 20579;
    private static final int leto_lizardman_warrior = 20580;
    private static final int leto_lizardman_shaman = 20581;
    private static final int leto_lizardman_overlord = 20582;
    // questitem
    private static final int q_certificate_of_lorain = 10362;
    private static final int q_slate_of_metal_q0186 = 10366;
    private static final int q_trinket_of_reto_q0186 = 10367;

    public _186_ContractExecution() {
        super(false);
        addStartNpc(researcher_lorain);
        addTalkId(maestro_nikola, blueprint_seller_luka);
        addKillId(leto_lizardman, leto_lizardman_archer, leto_lizardman_soldier, leto_lizardman_warrior, leto_lizardman_shaman, leto_lizardman_overlord);
        addQuestItem(q_certificate_of_lorain, q_slate_of_metal_q0186, q_trinket_of_reto_q0186);
        addLevelCheck(41);
        addQuestCompletedCheck(184);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("perform_a_contract");
        int npcId = npc.getNpcId();
        if (npcId == researcher_lorain) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("perform_a_contract", String.valueOf(1), true);
                st.giveItems(q_slate_of_metal_q0186, 1);
                st.takeItems(q_certificate_of_lorain, -1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "researcher_lorain_q0186_03.htm";
            }
        } else if (npcId == maestro_nikola) {
            if (event.equalsIgnoreCase("menu_select?ask=186&reply=1")) {
                htmltext = "maestro_nikola_q0186_02.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=186&reply=2")) {
                st.setCond(2);
                st.setMemoState("perform_a_contract", String.valueOf(2), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "maestro_nikola_q0186_03.htm";
            }
        } else if (npcId == blueprint_seller_luka) {
            if (event.equalsIgnoreCase("menu_select?ask=186&reply=1")) {
                if (GetMemoState == 2 && st.ownItemCount(q_trinket_of_reto_q0186) > 0)
                    htmltext = "blueprint_seller_luka_q0186_03.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=186&reply=2")) {
                if (GetMemoState == 2 && st.ownItemCount(q_trinket_of_reto_q0186) > 0) {
                    st.setMemoState("perform_a_contract", String.valueOf(3), true);
                    htmltext = "blueprint_seller_luka_q0186_04.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=186&reply=3")) {
                if (GetMemoState == 3) {
                    if (st.getPlayer().getLevel() < 47) {
                        st.giveItems(ADENA_ID, 105083);
                        st.addExpAndSp(285935, 18711);
                    } else {
                        st.giveItems(ADENA_ID, 105083);
                    }
                    st.takeItems(q_slate_of_metal_q0186, -1);
                    st.takeItems(q_trinket_of_reto_q0186, -1);
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "blueprint_seller_luka_q0186_06.htm";
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("perform_a_contract");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == researcher_lorain) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "researcher_lorain_q0186_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "researcher_lorain_q0186_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == researcher_lorain) {
                    if (GetMemoState >= 1)
                        htmltext = "researcher_lorain_q0186_04.htm";
                } else if (npcId == maestro_nikola) {
                    if (GetMemoState == 1)
                        htmltext = "maestro_nikola_q0186_01.htm";
                    else if (GetMemoState == 2)
                        htmltext = "maestro_nikola_q0186_04.htm";
                } else if (npcId == blueprint_seller_luka) {
                    if (GetMemoState == 2) {
                        if (st.ownItemCount(q_trinket_of_reto_q0186) > 0)
                            htmltext = "blueprint_seller_luka_q0186_02.htm";
                        else
                            htmltext = "blueprint_seller_luka_q0186_01.htm";
                    } else if (GetMemoState == 3)
                        htmltext = "blueprint_seller_luka_q0186_05.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("perform_a_contract");
        int npcId = npc.getNpcId();
        if (npcId == leto_lizardman) {
            if (GetMemoState == 2) {
                if (Rnd.get(100) < 40 && st.ownItemCount(q_trinket_of_reto_q0186) < 1) {
                    st.giveItems(q_trinket_of_reto_q0186, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == leto_lizardman_archer) {
            if (GetMemoState == 2) {
                if (Rnd.get(100) < 44 && st.ownItemCount(q_trinket_of_reto_q0186) < 1) {
                    st.giveItems(q_trinket_of_reto_q0186, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == leto_lizardman_soldier) {
            if (GetMemoState == 2) {
                if (Rnd.get(100) < 46 && st.ownItemCount(q_trinket_of_reto_q0186) < 1) {
                    st.giveItems(q_trinket_of_reto_q0186, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == leto_lizardman_warrior) {
            if (GetMemoState == 2) {
                if (Rnd.get(100) < 88 && st.ownItemCount(q_trinket_of_reto_q0186) < 1) {
                    st.giveItems(q_trinket_of_reto_q0186, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == leto_lizardman_shaman) {
            if (GetMemoState == 2) {
                if (Rnd.get(100) < 50 && st.ownItemCount(q_trinket_of_reto_q0186) < 1) {
                    st.giveItems(q_trinket_of_reto_q0186, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == leto_lizardman_overlord) {
            if (GetMemoState == 2) {
                if (Rnd.get(100) < 100 && st.ownItemCount(q_trinket_of_reto_q0186) < 1) {
                    st.giveItems(q_trinket_of_reto_q0186, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        }
        return null;
    }
}