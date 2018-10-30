package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.manager.QuestManager;
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
public class _141_ShadowFoxPart3 extends Quest {
    // NPC
    private final static int warehouse_chief_natools = 30894;

    // Items
    private final static int q_ex_agency_doc = 10350;

    // Monsters
    private final static int crokian_warrior = 20791;
    private final static int farhite = 20792;
    private final static int crocodile = 20135;

    public _141_ShadowFoxPart3() {
        super(false);
        addStartNpc(warehouse_chief_natools);
        addTalkId(warehouse_chief_natools);
        addQuestItem(q_ex_agency_doc);
        addKillId(crokian_warrior, farhite, crocodile);
        addLevelCheck(37);
        addQuestCompletedCheck(140);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("fallen_angel_3");

        if (event.equalsIgnoreCase("quest_accept")) {
            st.setCond(1);
            st.setMemoState("fallen_angel_3", String.valueOf(1), true);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            htmltext = "warehouse_chief_natools_q0141_03.htm";
        } else if (event.equalsIgnoreCase("reply_2") && GetMemoState == 1) {
            st.setCond(2);
            st.setMemoState("fallen_angel_3", String.valueOf(2), true);
            st.soundEffect(SOUND_MIDDLE);
            htmltext = "warehouse_chief_natools_q0141_06.htm";
        } else if (event.equalsIgnoreCase("reply_8") && GetMemoState == 3) {
            st.setMemoState("fallen_angel_3", String.valueOf(4), true);
            htmltext = "warehouse_chief_natools_q0141_15.htm";
        } else if (event.equalsIgnoreCase("reply_11") && GetMemoState == 4) {
            st.setCond(4);
            st.setMemoState("fallen_angel_3", String.valueOf(5), true);
            st.soundEffect(SOUND_MIDDLE);
            htmltext = "warehouse_chief_natools_q0141_19.htm";
        } else if (event.equalsIgnoreCase("reply_14") && GetMemoState == 5) {
            st.giveItems(ADENA_ID, 88888);
            if (st.getPlayer().getLevel() < 43)
                st.addExpAndSp(278005, 17058);
            st.soundEffect(SOUND_FINISH);
            st.exitQuest(false);
            st.removeMemo("fallen_angel_3");
            htmltext = "warehouse_chief_natools_q0141_23.htm";
        } else if (event.equalsIgnoreCase("reply_15")) {
            if (st.getPlayer().getLevel() >= 38)
                htmltext = "warehouse_chief_natools_q0141_25.htm";
            else
                htmltext = "warehouse_chief_natools_q0141_24.htm";
        } else if (event.equalsIgnoreCase("quest_accept_142")) {
            Quest q1 = QuestManager.getQuest(_142_FallenAngelRequestOfDawn.class);
            if (q1 != null) {
                st.exitQuest(false);
                QuestState qs1 = q1.newQuestState(st.getPlayer(), STARTED);
                q1.notifyEvent("start", qs1, npc);
                return null;
            }
        } else if (event.equalsIgnoreCase("quest_accept_143")) {
            Quest q1 = QuestManager.getQuest(_143_FallenAngelRequestOfDusk.class);
            if (q1 != null) {
                st.exitQuest(false);
                QuestState qs1 = q1.newQuestState(st.getPlayer(), STARTED);
                q1.notifyEvent("start", qs1, npc);
                return null;
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int GetMemoState = st.getInt("fallen_angel_3");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == warehouse_chief_natools) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "warehouse_chief_natools_q0141_02a.htm";
                            st.exitQuest(true);
                            break;
                        case QUEST:
                            htmltext = "warehouse_chief_natools_q0141_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "warehouse_chief_natools_q0141_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == warehouse_chief_natools)
                    if (GetMemoState == 1)
                        htmltext = "warehouse_chief_natools_q0141_04.htm";
                    else if (GetMemoState == 2) {
                        if (st.ownItemCount(q_ex_agency_doc) >= 30) {
                            st.setMemoState("fallen_angel_3", String.valueOf(3), true);
                            st.takeItems(q_ex_agency_doc, -1);
                            htmltext = "warehouse_chief_natools_q0141_08.htm";
                        } else
                            htmltext = "warehouse_chief_natools_q0141_07.htm";
                    } else if (GetMemoState == 3)
                        htmltext = "warehouse_chief_natools_q0141_09.htm";
                    else if (GetMemoState == 4)
                        htmltext = "warehouse_chief_natools_q0141_16.htm";
                    else if (GetMemoState == 5)
                        htmltext = "warehouse_chief_natools_q0141_20.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("fallen_angel_3");
        int npcId = npc.getNpcId();

        if (GetMemoState == 2)
            if (npcId == crokian_warrior) {
                int i0 = Rnd.get(100);
                if (i0 < 100)
                    if (st.ownItemCount(q_ex_agency_doc) >= 29) {
                        st.giveItems(q_ex_agency_doc, 1);
                        st.soundEffect(SOUND_MIDDLE);
                    } else {
                        st.giveItems(q_ex_agency_doc, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            } else if (npcId == farhite) {
                int i0 = Rnd.get(100);
                if (i0 < 92)
                    if (st.ownItemCount(q_ex_agency_doc) >= 29) {
                        st.giveItems(q_ex_agency_doc, 1);
                        st.soundEffect(SOUND_MIDDLE);
                    } else {
                        st.giveItems(q_ex_agency_doc, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            } else if (npcId == crocodile) {
                int i0 = Rnd.get(100);
                if (i0 < 53)
                    if (st.ownItemCount(q_ex_agency_doc) >= 29) {
                        st.giveItems(q_ex_agency_doc, 1);
                        st.soundEffect(SOUND_MIDDLE);
                    } else {
                        st.giveItems(q_ex_agency_doc, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            }
        return null;
    }
}