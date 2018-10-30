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
public class _042_HelpTheUncle extends Quest {
    // npc
    private static final int pet_manager_waters = 30828;
    private static final int sophia = 30735;
    // mobs
    private static final int beamer = 20068;
    private static final int monstereye_gazer = 20266;
    // items
    private static final int trident = 291;
    private static final int ticket_buffalo_panpipe = 7583;
    // questitem
    private static final int q_dummy_buffalo = 7548;
    private static final int q_dummy_buffalo2 = 7549;

    public _042_HelpTheUncle() {
        super(false);
        addStartNpc(pet_manager_waters);
        addTalkId(pet_manager_waters, sophia);
        addKillId(beamer, monstereye_gazer);
        addLevelCheck(25);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetHTMLCookie = st.getInt("plz_help_my_uncle_cookie");
        int npcId = npc.getNpcId();
        if (npcId == pet_manager_waters) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("plz_help_my_uncle", String.valueOf(1 * 10 + 1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "pet_manager_waters_q0042_0104.htm";
            } else if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 2 - 1)
                if (st.ownItemCount(trident) >= 1) {
                    st.setCond(2);
                    st.takeItems(trident, 1);
                    st.setMemoState("plz_help_my_uncle", String.valueOf(2 * 10 + 1), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "pet_manager_waters_q0042_0201.htm";
                } else
                    htmltext = "pet_manager_waters_q0042_0202.htm";
            else if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 3 - 1) {
                if (st.ownItemCount(q_dummy_buffalo) >= 30) {
                    st.setCond(4);
                    st.takeItems(q_dummy_buffalo, 30);
                    st.giveItems(q_dummy_buffalo2, 1);
                    st.setMemoState("plz_help_my_uncle", String.valueOf(3 * 10 + 1), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "pet_manager_waters_q0042_0301.htm";
                } else
                    htmltext = "pet_manager_waters_q0042_0302.htm";
            } else if (event.equalsIgnoreCase("reply_3") && GetHTMLCookie == 5 - 1) {
                st.giveItems(ticket_buffalo_panpipe, 1);
                st.removeMemo("plz_help_my_uncle");
                st.removeMemo("plz_help_my_uncle_cookie");
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(false);
                htmltext = "pet_manager_waters_q0042_0501.htm";
            }
        } else if (npcId == sophia)
            if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 4 - 1)
                if (st.ownItemCount(q_dummy_buffalo2) >= 1) {
                    st.setCond(5);
                    st.takeItems(q_dummy_buffalo2, 1);
                    st.setMemoState("plz_help_my_uncle", String.valueOf(4 * 10 + 1), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "sophia_q0042_0401.htm";
                } else
                    htmltext = "sophia_q0042_0402.htm";
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("plz_help_my_uncle");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == pet_manager_waters) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "pet_manager_waters_q0042_0103.htm";
                            break;
                        default:
                            htmltext = "pet_manager_waters_q0042_0101.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == pet_manager_waters) {
                    if (GetMemoState == 1 * 10 + 1) {
                        if (st.ownItemCount(trident) >= 1) {
                            st.setMemoState("plz_help_my_uncle_cookie", String.valueOf(1), true);
                            htmltext = "pet_manager_waters_q0042_0105.htm";
                        } else
                            htmltext = "pet_manager_waters_q0042_0106.htm";
                    } else if (GetMemoState <= 2 * 10 + 2 && GetMemoState >= 2 * 10 + 1) {
                        if (GetMemoState == 2 * 10 + 2 && st.ownItemCount(q_dummy_buffalo) >= 30) {
                            st.setMemoState("plz_help_my_uncle_cookie", String.valueOf(2), true);
                            htmltext = "pet_manager_waters_q0042_0203.htm";
                        } else
                            htmltext = "pet_manager_waters_q0042_0204.htm";
                    } else if (GetMemoState == 3 * 10 + 1)
                        htmltext = "pet_manager_waters_q0042_0303.htm";
                    else if (GetMemoState == 4 * 10 + 1) {
                        st.setMemoState("plz_help_my_uncle_cookie", String.valueOf(4), true);
                        htmltext = "pet_manager_waters_q0042_0401.htm";
                    }
                } else if (npcId == sophia)
                    if (st.ownItemCount(q_dummy_buffalo2) >= 1 && GetMemoState == 3 * 10 + 1) {
                        st.setMemoState("plz_help_my_uncle_cookie", String.valueOf(3), true);
                        htmltext = "sophia_q0042_0301.htm";
                    } else if (GetMemoState == 4 * 10 + 1)
                        htmltext = "sophia_q0042_0403.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("plz_help_my_uncle");
        int npcId = npc.getNpcId();
        if (GetMemoState == 2 * 10 + 1)
            if (npcId == beamer || npcId == monstereye_gazer) {
                int i4 = Rnd.get(1000);
                if (i4 < 1000 && 1000 != 0)
                    if (st.ownItemCount(q_dummy_buffalo) + 1 >= 30) {
                        if (st.ownItemCount(q_dummy_buffalo) < 30) {
                            st.giveItems(q_dummy_buffalo, 30 - st.ownItemCount(q_dummy_buffalo));
                            st.soundEffect(SOUND_MIDDLE);
                            st.setCond(3);
                        }
                        st.setMemoState("plz_help_my_uncle", String.valueOf(2 * 10 + 2), true);
                    } else {
                        st.giveItems(q_dummy_buffalo, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            }
        return null;
    }
}