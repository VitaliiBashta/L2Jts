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
 * @date 31/01/2015
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _043_HelpTheSister extends Quest {
    // npc
    private static final int pet_manager_cooper = 30829;
    private static final int galladuchi = 30097;
    // mobs
    private static final int specter = 20171;
    private static final int lemures = 20197;
    // questitem
    private static final int handiwork_dagger = 220;
    private static final int q_dummy_cougar = 7550;
    private static final int q_dummy_cougar2 = 7551;
    private static final int ticket_cougar_chime = 7584;

    public _043_HelpTheSister() {
        super(false);
        addStartNpc(pet_manager_cooper);
        addTalkId(galladuchi);
        addKillId(specter, lemures);
        addQuestItem(q_dummy_cougar);
        addLevelCheck(26);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetHTMLCookie = st.getInt("plz_help_my_sister_cookie");
        int npcId = npc.getNpcId();
        if (npcId == pet_manager_cooper) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("plz_help_my_sister", String.valueOf(1 * 10 + 1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "pet_manager_cooper_q0043_0104.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=43&reply=1") && GetHTMLCookie == 2 - 1) {
                if (st.ownItemCount(handiwork_dagger) >= 1) {
                    st.setCond(2);
                    st.setMemoState("plz_help_my_sister", String.valueOf(2 * 10 + 1), true);
                    st.takeItems(handiwork_dagger, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "pet_manager_cooper_q0043_0201.htm";
                } else
                    htmltext = "pet_manager_cooper_q0043_0202.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=43&reply=1") && GetHTMLCookie == 3 - 1) {
                if (st.ownItemCount(q_dummy_cougar) >= 30) {
                    st.setCond(4);
                    st.setMemoState("plz_help_my_sister", String.valueOf(3 * 10 + 1), true);
                    st.takeItems(q_dummy_cougar, 30);
                    st.giveItems(q_dummy_cougar2, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "pet_manager_cooper_q0043_0301.htm";
                } else
                    htmltext = "pet_manager_cooper_q0043_0302.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=43&reply=3") && GetHTMLCookie == 5 - 1) {
                st.giveItems(ticket_cougar_chime, 1);
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(false);
                htmltext = "pet_manager_cooper_q0043_0501.htm";
            }
        } else if (npcId == galladuchi) {
            if (event.equalsIgnoreCase("menu_select?ask=43&reply=1") && GetHTMLCookie == 4 - 1) {
                if (st.ownItemCount(q_dummy_cougar2) >= 1) {
                    st.setCond(5);
                    st.setMemoState("plz_help_my_sister", String.valueOf(4 * 10 + 1), true);
                    st.takeItems(q_dummy_cougar2, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "galladuchi_q0043_0401.htm";
                } else
                    htmltext = "galladuchi_q0043_0402.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("plz_help_my_sister");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == pet_manager_cooper) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "pet_manager_cooper_q0043_0103.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "pet_manager_cooper_q0043_0101.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == pet_manager_cooper) {
                    if (GetMemoState == 1 * 10 + 1) {
                        if (st.ownItemCount(handiwork_dagger) >= 1) {
                            st.setMemoState("plz_help_my_sister_cookie", String.valueOf(1), true);
                            htmltext = "pet_manager_cooper_q0043_0105.htm";
                        } else
                            htmltext = "pet_manager_cooper_q0043_0106.htm";
                    } else if (GetMemoState <= 2 * 10 + 2 && GetMemoState >= 2 * 10 + 1) {
                        if (GetMemoState == 2 * 10 + 2 && st.ownItemCount(q_dummy_cougar) >= 30) {
                            st.setMemoState("plz_help_my_sister_cookie", String.valueOf(2), true);
                            htmltext = "pet_manager_cooper_q0043_0203.htm";
                        } else
                            htmltext = "pet_manager_cooper_q0043_0204.htm";
                    } else if (GetMemoState == 3 * 10 + 1)
                        htmltext = "pet_manager_cooper_q0043_0303.htm";
                    else if (GetMemoState == 4 * 10 + 1) {
                        st.setMemoState("plz_help_my_sister_cookie", String.valueOf(4), true);
                        htmltext = "pet_manager_cooper_q0043_0401.htm";
                    }
                } else if (npcId == galladuchi) {
                    if (st.ownItemCount(q_dummy_cougar2) >= 1 && GetMemoState == 3 * 10 + 1) {
                        st.setMemoState("plz_help_my_sister_cookie", String.valueOf(3), true);
                        htmltext = "galladuchi_q0043_0301.htm";
                    } else if (GetMemoState == 4 * 10 + 1)
                        htmltext = "galladuchi_q0043_0403.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("plz_help_my_sister");
        int npcId = npc.getNpcId();
        if (npcId == specter || npcId == lemures) {
            if (GetMemoState == 2 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 1000 && 1000 != 0) {
                    if (st.ownItemCount(q_dummy_cougar) + 1 >= 30) {
                        if (st.ownItemCount(q_dummy_cougar) < 30) {
                            st.setCond(3);
                            st.giveItems(q_dummy_cougar, 30 - st.ownItemCount(q_dummy_cougar));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        st.setMemoState("plz_help_my_sister", String.valueOf(2 * 10 + 2), true);
                    } else {
                        st.giveItems(q_dummy_cougar, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        }
        return null;
    }
}