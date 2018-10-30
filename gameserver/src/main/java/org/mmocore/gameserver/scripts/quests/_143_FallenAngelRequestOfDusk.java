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
public class _143_FallenAngelRequestOfDusk extends Quest {
    // npc
    private final static int warehouse_chief_natools = 30894;
    private final static int master_tobias = 30297;
    private final static int sage_kasian = 30612;
    private final static int stained_rock = 32368;
    private final static int q_fallen_angel_npc = 32369;

    // questitem
    private final static int q_sealed_q_prophetic_book143 = 10354;
    private final static int q_prophetic_book143 = 10355;
    private final static int q_empty_angel_sound = 10356;
    private final static int q_cure_angle = 10357;
    private final static int q_angel_sound = 10358;

    public _143_FallenAngelRequestOfDusk() {
        super(false);
        addTalkId(warehouse_chief_natools, master_tobias, sage_kasian, stained_rock, q_fallen_angel_npc);
        addQuestItem(q_sealed_q_prophetic_book143, q_prophetic_book143, q_empty_angel_sound, q_cure_angle, q_angel_sound);
        addLevelCheck(38);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("fallen_angel_4b");
        int spawned_q_fallen_angel_mon = st.getInt("spawned_q_fallen_angel_npc");
        String q_fallen_angel_npc_player_name = st.get("q_fallen_angel_npc_player_name");
        int npcId = npc.getNpcId();

        if (npcId == warehouse_chief_natools) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("fallen_angel_4b", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "warehouse_chief_natools_q0143_01.htm";
            } else if (event.equalsIgnoreCase("reply_4")) {
                if (GetMemoState == 1)
                    htmltext = "warehouse_chief_natools_q0143_03.htm";
            } else if (event.equalsIgnoreCase("reply_5"))
                if (GetMemoState == 1) {
                    st.setCond(2);
                    st.setMemoState("fallen_angel_4b", String.valueOf(2), true);
                    st.giveItems(q_sealed_q_prophetic_book143, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "warehouse_chief_natools_q0143_04.htm";
                }
        } else if (npcId == master_tobias) {
            if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 2) {
                    st.takeItems(q_sealed_q_prophetic_book143, -1);
                    st.setMemoState("fallen_angel_4b", String.valueOf(3), true);
                    htmltext = "master_tobias_q0143_03.htm";
                }
            } else if (event.equalsIgnoreCase("reply_4")) {
                if (GetMemoState == 3)
                    htmltext = "master_tobias_q0143_04.htm";
            } else if (event.equalsIgnoreCase("reply_5"))
                if (GetMemoState == 3) {
                    st.setCond(3);
                    st.setMemoState("fallen_angel_4b", String.valueOf(4), true);
                    st.giveItems(q_prophetic_book143, 1);
                    st.giveItems(q_empty_angel_sound, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "master_tobias_q0143_05.htm";
                }
        } else if (npcId == sage_kasian) {
            if (event.equalsIgnoreCase("reply_5")) {
                if (GetMemoState == 4) {
                    st.takeItems(q_prophetic_book143, -1);
                    st.setMemoState("fallen_angel_4b", String.valueOf(5), true);
                    htmltext = "sage_kasian_q0143_03.htm";
                }
            } else if (event.equalsIgnoreCase("reply_6")) {
                if (GetMemoState == 5)
                    htmltext = "sage_kasian_q0143_05.htm";
            } else if (event.equalsIgnoreCase("reply_7")) {
                if (GetMemoState == 5)
                    htmltext = "sage_kasian_q0143_06.htm";
            } else if (event.equalsIgnoreCase("reply_8")) {
                if (GetMemoState == 5)
                    htmltext = "sage_kasian_q0143_07.htm";
            } else if (event.equalsIgnoreCase("reply_9")) {
                if (GetMemoState == 5)
                    htmltext = "sage_kasian_q0143_08.htm";
            } else if (event.equalsIgnoreCase("reply_10"))
                if (GetMemoState == 5) {
                    st.setCond(4);
                    st.setMemoState("fallen_angel_4b", String.valueOf(8), true);
                    st.giveItems(q_cure_angle, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "sage_kasian_q0143_09.htm";
                }
        } else if (npcId == stained_rock) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (spawned_q_fallen_angel_mon == 0) {
                    st.setMemoState("spawned_q_fallen_angel_npc", String.valueOf(1), true);
                    st.setMemoState("q_fallen_angel_npc_player_name", st.getPlayer().getName(), true);
                    NpcInstance fallen_angel = st.addSpawn(q_fallen_angel_npc, st.getPlayer().getX() + 100, st.getPlayer().getY() + 100, st.getPlayer().getZ());
                    st.startQuestTimer("14301", 120000, fallen_angel);
                    htmltext = "stained_rock_q0143_05.htm";
                } else if (q_fallen_angel_npc_player_name == st.getPlayer().getName())
                    htmltext = "stained_rock_q0143_04.htm";
                else
                    htmltext = "stained_rock_q0143_03.htm";
            } else if (event.equalsIgnoreCase("14301")) {
                st.removeMemo("spawned_q_fallen_angel_npc");
                if (npc != null)
                    npc.deleteMe();
                return null;
            }
        } else if (npcId == q_fallen_angel_npc)
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 8) {
                    st.takeItems(q_cure_angle, -1);
                    st.setMemoState("fallen_angel_4b", String.valueOf(9), true);
                    htmltext = "q_fallen_angel_npc_q0143_04.htm";
                }
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 9)
                    htmltext = "q_fallen_angel_npc_q0143_06.htm";
            } else if (event.equalsIgnoreCase("reply_4")) {
                if (GetMemoState == 9)
                    htmltext = "q_fallen_angel_npc_q0143_07.htm";
            } else if (event.equalsIgnoreCase("reply_5")) {
                if (GetMemoState == 9) {
                    st.setMemoState("fallen_angel_4b", String.valueOf(10), true);
                    htmltext = "q_fallen_angel_npc_q0143_08.htm";
                }
            } else if (event.equalsIgnoreCase("reply_6")) {
                if (GetMemoState == 10)
                    htmltext = "q_fallen_angel_npc_q0143_10.htm";
            } else if (event.equalsIgnoreCase("reply_7")) {
                if (GetMemoState == 10)
                    htmltext = "q_fallen_angel_npc_q0143_11.htm";
            } else if (event.equalsIgnoreCase("reply_8")) {
                if (GetMemoState == 10)
                    htmltext = "q_fallen_angel_npc_q0143_12.htm";
            } else if (event.equalsIgnoreCase("reply_9")) {
                if (GetMemoState == 10)
                    htmltext = "q_fallen_angel_npc_q0143_13.htm";
            } else if (event.equalsIgnoreCase("reply_10"))
                if (GetMemoState == 10) {
                    st.removeMemo("spawned_q_fallen_angel_npc");
                    if (npc != null)
                        npc.deleteMe();
                    st.giveItems(q_angel_sound, 1);
                    st.takeItems(q_empty_angel_sound, -1);
                    st.setCond(5);
                    st.setMemoState("fallen_angel_4b", String.valueOf(11), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "q_fallen_angel_npc_q0143_14.htm";
                }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("fallen_angel_4b");
        String q_fallen_angel_npc_player_name = st.get("q_fallen_angel_npc_player_name");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case STARTED:
                if (npcId == warehouse_chief_natools) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            return htmltext;
                        default:
                            if (GetMemoState == 1)
                                htmltext = "warehouse_chief_natools_q0143_02.htm";
                            else if (GetMemoState >= 2)
                                htmltext = "warehouse_chief_natools_q0143_05.htm";
                            break;
                    }
                } else if (npcId == master_tobias) {
                    if (GetMemoState < 2)
                        htmltext = "master_tobias_q0143_01.htm";
                    else if (GetMemoState == 2)
                        htmltext = "master_tobias_q0143_02.htm";
                    else if (GetMemoState == 3)
                        htmltext = "master_tobias_q0143_03a.htm";
                    else if (GetMemoState >= 4 && GetMemoState < 11)
                        htmltext = "master_tobias_q0143_06.htm";
                    else if (GetMemoState >= 11) {
                        st.takeItems(q_angel_sound, -1);
                        st.giveItems(ADENA_ID, 89046);
                        if (st.getPlayer().getLevel() < 44)
                            st.addExpAndSp(223036, 13901);
                        st.removeMemo("fallen_angel_4b");
                        st.removeMemo("spawned_q_fallen_angel_npc");
                        st.removeMemo("q_fallen_angel_npc_player_name");
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                        htmltext = "master_tobias_q0143_07.htm";
                    }
                } else if (npcId == sage_kasian) {
                    if (GetMemoState < 4)
                        htmltext = "sage_kasian_q0143_01.htm";
                    else if (GetMemoState == 4)
                        htmltext = "sage_kasian_q0143_02.htm";
                    else if (GetMemoState == 5)
                        htmltext = "sage_kasian_q0143_04.htm";
                    else if (GetMemoState >= 6)
                        htmltext = "sage_kasian_q0143_10.htm";
                } else if (npcId == stained_rock) {
                    if (GetMemoState < 8)
                        htmltext = "stained_rock_q0143_01.htm";
                    else if (GetMemoState >= 8 && GetMemoState < 11)
                        htmltext = "stained_rock_q0143_02.htm";
                    else if (GetMemoState >= 11)
                        htmltext = "stained_rock_q0143_06.htm";
                } else if (npcId == q_fallen_angel_npc)
                    if (GetMemoState < 8)
                        htmltext = "q_fallen_angel_npc_q0143_01.htm";
                    else if (GetMemoState == 8) {
                        if (q_fallen_angel_npc_player_name == st.getPlayer().getName())
                            htmltext = "q_fallen_angel_npc_q0143_03.htm";
                        else
                            htmltext = "q_fallen_angel_npc_q0143_02.htm";
                    } else if (GetMemoState == 9)
                        htmltext = "q_fallen_angel_npc_q0143_05.htm";
                    else if (GetMemoState == 10)
                        htmltext = "q_fallen_angel_npc_q0143_09.htm";
                break;
        }
        return htmltext;
    }
}