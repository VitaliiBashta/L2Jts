package org.mmocore.gameserver.scripts.quests;

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
public class _10283_RequestOfIceMerchant extends Quest {
    // npc
    private final static int repre = 32020;
    private final static int keier = 32022;
    private final static int jinia_npc = 32760;

    public _10283_RequestOfIceMerchant() {
        super(false);
        addStartNpc(repre);
        addTalkId(keier, jinia_npc);
        addLevelCheck(82);
        addQuestCompletedCheck(115);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("request_of_repre");
        int spawned_jinia_npc = st.getInt("spawned_jinia_npc");
        String jinia_npc_player_name = st.get("jinia_npc_player_name");
        int npcId = npc.getNpcId();

        if (npcId == repre) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("request_of_repre", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "repre_q10283_05.htm";
            } else if (event.equalsIgnoreCase("reply_1"))
                htmltext = "repre_q10283_04.htm";
            else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 1)
                    htmltext = "repre_q10283_07.htm";
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 1)
                    htmltext = "repre_q10283_08.htm";
            } else if (event.equalsIgnoreCase("reply_4"))
                if (GetMemoState == 1) {
                    st.setCond(2);
                    st.setMemoState("request_of_repre", String.valueOf(2), true);
                    htmltext = "repre_q10283_09.htm";
                }
        } else if (npcId == keier) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 2)
                    htmltext = "keier_q10283_01.htm";
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 2)
                    if (spawned_jinia_npc == 0) {
                        st.setCond(3);
                        st.setMemoState("spawned_jinia_npc", String.valueOf(1), true);
                        st.setMemoState("jinia_npc_player_name", st.getPlayer().getName(), true);
                        NpcInstance jinia_npc_spawn = st.addSpawn(jinia_npc, 104476, -107535, -3688, 44954, 0, 0);
                        st.startQuestTimer("528352", 1000 * 3 * 60, jinia_npc_spawn);
                        return null;
                    } else if (spawned_jinia_npc == 1)
                        if (jinia_npc_player_name == st.getPlayer().getName())
                            htmltext = "keier_q10283_03.htm";
                        else
                            htmltext = "keier_q10283_02.htm";
            } else if (event.equalsIgnoreCase("528352")) {
                st.removeMemo("spawned_jinia_npc");
                if (npc != null)
                    npc.deleteMe();
                return null;
            }
        } else if (npcId == jinia_npc)
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 2)
                    htmltext = "jinia_npc_q10283_01.htm";
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 2)
                    htmltext = "jinia_npc_q10283_02.htm";
            } else if (event.equalsIgnoreCase("reply_3")) {
                st.giveItems(ADENA_ID, 190000);
                st.addExpAndSp(627000, 50300);
                st.removeMemo("request_of_repre");
                st.removeMemo("spawned_jinia_npc");
                st.removeMemo("jinia_npc_player_name");
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(false);
                if (st.isRunningQuestTimer("528352"))
                    st.cancelQuestTimer("528352");
                st.startQuestTimer("528351", 1000 * 2);
                htmltext = "jinia_npc_q10283_03.htm";
            } else if (event.equalsIgnoreCase("528351")) {
                st.removeMemo("spawned_jinia_npc");
                if (npc != null)
                    npc.deleteMe();
                return null;
            }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("request_of_repre");
        String jinia_npc_player_name = st.get("jinia_npc_player_name");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == repre) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                        case QUEST:
                            htmltext = "repre_q10283_03.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "repre_q10283_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == repre) {
                    if (GetMemoState == 1)
                        htmltext = "repre_q10283_06.htm";
                    else if (GetMemoState == 2)
                        htmltext = "repre_q10283_10.htm";
                } else if (npcId == keier) {
                    if (GetMemoState == 2)
                        htmltext = "keier003.htm";
                } else if (npcId == jinia_npc)
                    if (jinia_npc_player_name == st.getPlayer().getName()) {
                        if (GetMemoState == 2)
                            htmltext = "jinia_npc001a.htm";
                    } else
                        htmltext = "jinia_npc001b.htm";
                break;
            case COMPLETED:
                if (npcId == repre)
                    htmltext = "repre_q10283_02.htm";
                else if (npcId == keier)
                    htmltext = "keier004.htm";
                else if (npcId == jinia_npc)
                    htmltext = "jinia_npc_q10283_04.htm";
                break;
        }
        return htmltext;
    }
}