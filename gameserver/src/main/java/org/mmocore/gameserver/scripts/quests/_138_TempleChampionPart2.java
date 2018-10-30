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
public class _138_TempleChampionPart2 extends Quest {
    // npc
    private static final int sylvain = 30070;
    private static final int pupina = 30118;
    private static final int grandmaster_angus = 30474;
    private static final int preacher_sla = 30666;

    // questitem
    private static final int q_church_public_statement = 10341;
    private static final int q_darkelf_remain = 10342;
    private static final int q_recommand_darkelf = 10343;
    private static final int q_recommand_pupina = 10344;

    // mobs
    private final static int wyrm = 20176;
    private final static int guardian_basilisk = 20550;
    private final static int road_scavenger = 20551;
    private final static int crimson_bind = 20552;

    public _138_TempleChampionPart2() {
        super(false);
        addStartNpc(sylvain);
        addTalkId(sylvain, pupina, grandmaster_angus, preacher_sla);
        addKillId(wyrm, guardian_basilisk, road_scavenger, crimson_bind);
        addQuestItem(q_church_public_statement, q_darkelf_remain, q_recommand_darkelf, q_recommand_pupina);
        addLevelCheck(36);
        addQuestCompletedCheck(137);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("fallen_angel_2");
        int npcId = npc.getNpcId();

        if (npcId == sylvain) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("fallen_angel_2", String.valueOf(0), true);
                st.giveItems(q_church_public_statement, 1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "sylvain_q0138_04.htm";
            } else if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 0) {
                    st.setCond(2);
                    st.setMemoState("fallen_angel_2", String.valueOf(1), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "sylvain_q0138_06.htm";
                }
            } else if (event.equalsIgnoreCase("reply_2"))
                if (GetMemoState == 13) {
                    st.giveItems(ADENA_ID, 84593);
                    if (st.getPlayer().getLevel() < 42)
                        st.addExpAndSp(187062, 11307);
                    st.removeMemo("fallen_angel_2");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "sylvain_q0138_09.htm";
                }
        } else if (npcId == pupina) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 1) {
                    st.setMemoState("fallen_angel_2", String.valueOf(2), true);
                    htmltext = "pupina_q0138_03.htm";
                }
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 2)
                    htmltext = "pupina_q0138_04.htm";
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 2)
                    htmltext = "pupina_q0138_06.htm";
            } else if (event.equalsIgnoreCase("reply_4")) {
                if (GetMemoState == 2)
                    htmltext = "pupina_q0138_07.htm";
            } else if (event.equalsIgnoreCase("reply_5")) {
                if (GetMemoState == 2) {
                    st.setCond(3);
                    st.setMemoState("fallen_angel_2", String.valueOf(3), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "pupina_q0138_08.htm";
                }
            } else if (event.equalsIgnoreCase("reply_6"))
                if (GetMemoState == 8) {
                    st.setCond(6);
                    st.setMemoState("fallen_angel_2", String.valueOf(9), true);
                    st.giveItems(q_recommand_pupina, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "pupina_q0138_11.htm";
                }
        } else if (npcId == grandmaster_angus) {
            if (event.equalsIgnoreCase("reply_1"))
                if (GetMemoState == 3) {
                    st.setCond(4);
                    st.setMemoState("fallen_angel_2", String.valueOf(4), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "grandmaster_angus_q0138_03.htm";
                }
        } else if (npcId == preacher_sla)
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 9) {
                    st.setMemoState("fallen_angel_2", String.valueOf(10), true);
                    st.takeItems(q_recommand_pupina, -1);
                    htmltext = "preacher_sla_q0138_03.htm";
                }
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 10) {
                    st.setMemoState("fallen_angel_2", String.valueOf(11), true);
                    st.takeItems(q_church_public_statement, -1);
                    htmltext = "preacher_sla_q0138_05.htm";
                }
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 11)
                    htmltext = "preacher_sla_q0138_07.htm";
            } else if (event.equalsIgnoreCase("reply_4")) {
                if (GetMemoState == 11)
                    htmltext = "preacher_sla_q0138_08.htm";
            } else if (event.equalsIgnoreCase("reply_5")) {
                if (GetMemoState == 11) {
                    st.setMemoState("fallen_angel_2", String.valueOf(12), true);
                    htmltext = "preacher_sla_q0138_09.htm";
                }
            } else if (event.equalsIgnoreCase("reply_6")) {
                if (GetMemoState == 12)
                    htmltext = "preacher_sla_q0138_10.htm";
            } else if (event.equalsIgnoreCase("reply_7"))
                if (GetMemoState == 12) {
                    st.setCond(7);
                    st.setMemoState("fallen_angel_2", String.valueOf(13), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "preacher_sla_q0138_12.htm";
                }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("fallen_angel_2");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == sylvain) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "sylvain_q0138_02.htm";
                            st.exitQuest(true);
                            break;
                        case QUEST:
                            htmltext = "sylvain_q0138_03.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "sylvain_q0138_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == sylvain) {
                    if (GetMemoState == 0)
                        htmltext = "sylvain_q0138_05.htm";
                    else if (GetMemoState >= 1 && GetMemoState < 13)
                        htmltext = "sylvain_q0138_07.htm";
                    else if (GetMemoState == 13)
                        htmltext = "sylvain_q0138_08.htm";
                } else if (npcId == pupina) {
                    if (GetMemoState == 0)
                        htmltext = "pupina_q0138_01.htm";
                    else if (GetMemoState == 1)
                        htmltext = "pupina_q0138_02.htm";
                    else if (GetMemoState == 2)
                        htmltext = "pupina_q0138_05.htm";
                    else if (GetMemoState >= 3 && GetMemoState < 7)
                        htmltext = "pupina_q0138_09.htm";
                    else if (GetMemoState == 7) {
                        st.takeItems(q_recommand_darkelf, -1);
                        st.setMemoState("fallen_angel_2", String.valueOf(8), true);
                        htmltext = "pupina_q0138_10.htm";
                    } else if (GetMemoState == 8) {
                        st.setCond(6);
                        st.setMemoState("fallen_angel_2", String.valueOf(9), true);
                        st.giveItems(q_recommand_pupina, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "pupina_q0138_12.htm";
                    } else if (GetMemoState >= 9)
                        htmltext = "pupina_q0138_13.htm";
                } else if (npcId == grandmaster_angus) {
                    if (GetMemoState < 3)
                        htmltext = "grandmaster_angus_q0138_01.htm";
                    else if (GetMemoState == 3)
                        htmltext = "grandmaster_angus_q0138_02.htm";
                    else if (GetMemoState == 4) {
                        if (st.ownItemCount(q_darkelf_remain) >= 10) {
                            st.setCond(5);
                            st.setMemoState("fallen_angel_2", String.valueOf(7), true);
                            st.giveItems(q_recommand_darkelf, 1);
                            st.takeItems(q_darkelf_remain, -1);
                            st.soundEffect(SOUND_MIDDLE);
                            htmltext = "grandmaster_angus_q0138_05.htm";
                        } else
                            htmltext = "grandmaster_angus_q0138_04.htm";
                    } else if (GetMemoState >= 5)
                        htmltext = "grandmaster_angus_q0138_06.htm";
                } else if (npcId == preacher_sla)
                    if (GetMemoState < 9)
                        htmltext = "preacher_sla_q0138_01.htm";
                    else if (GetMemoState == 9)
                        htmltext = "preacher_sla_q0138_02.htm";
                    else if (GetMemoState == 10)
                        htmltext = "preacher_sla_q0138_04.htm";
                    else if (GetMemoState == 11)
                        htmltext = "preacher_sla_q0138_06.htm";
                    else if (GetMemoState == 12)
                        htmltext = "preacher_sla_q0138_11.htm";
                    else if (GetMemoState == 13)
                        htmltext = "preacher_sla_q0138_13.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("fallen_angel_2");
        int npcId = npc.getNpcId();

        if (GetMemoState == 4 && st.ownItemCount(q_darkelf_remain) < 10)
            if (npcId == wyrm) {
                int i0 = Rnd.get(100);
                if (i0 < 42)
                    if (st.ownItemCount(q_darkelf_remain) >= 9) {
                        st.giveItems(q_darkelf_remain, 1);
                        st.soundEffect(SOUND_MIDDLE);
                    } else {
                        st.giveItems(q_darkelf_remain, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            } else if (npcId == guardian_basilisk || npcId == road_scavenger) {
                int i0 = Rnd.get(100);
                if (i0 < 46)
                    if (st.ownItemCount(q_darkelf_remain) >= 9) {
                        st.giveItems(q_darkelf_remain, 1);
                        st.soundEffect(SOUND_MIDDLE);
                    } else {
                        st.giveItems(q_darkelf_remain, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            } else if (npcId == crimson_bind) {
                int i0 = Rnd.get(100);
                if (i0 < 100)
                    if (st.ownItemCount(q_darkelf_remain) >= 9) {
                        st.giveItems(q_darkelf_remain, 1);
                        st.soundEffect(SOUND_MIDDLE);
                    } else {
                        st.giveItems(q_darkelf_remain, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            }
        return null;
    }
}