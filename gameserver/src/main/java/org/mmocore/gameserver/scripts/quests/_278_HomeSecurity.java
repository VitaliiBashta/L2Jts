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
public class _278_HomeSecurity extends Quest {
    // npc
    private static final int beast_herder_tunatun = 31537;

    // mobs
    private static final int farm_plunderer = 18906;
    private static final int farm_invader = 18907;

    // etcitem
    private static final int scrl_of_ench_am_s = 960;
    private static final int high_ore_of_water = 9553;
    private static final int scrl_of_ench_wp_s = 959;

    // questitem
    private static final int q_mane_of_farm_marauder = 15531;

    public _278_HomeSecurity() {
        super(false);
        addStartNpc(beast_herder_tunatun);
        addKillId(farm_plunderer, farm_invader);
        addQuestItem(q_mane_of_farm_marauder);
        addLevelCheck(82);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("keep_the_safe_in_love");

        if (event.equalsIgnoreCase("quest_accept")) {
            st.setCond(1);
            st.setMemoState("keep_the_safe_in_love", String.valueOf(1), true);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            htmltext = "beast_herder_tunatun_q0278_04.htm";
        } else if (event.equalsIgnoreCase("reply_1"))
            htmltext = "beast_herder_tunatun_q0278_02.htm";
        else if (event.equalsIgnoreCase("reply_2"))
            if (GetMemoState == 2 && st.ownItemCount(q_mane_of_farm_marauder) >= 300) {
                int i0 = Rnd.get(100);
                if (i0 < 10)
                    st.giveItems(scrl_of_ench_am_s, 1);
                else if (i0 < 19)
                    st.giveItems(scrl_of_ench_am_s, 2);
                else if (i0 < 27)
                    st.giveItems(scrl_of_ench_am_s, 3);
                else if (i0 < 34)
                    st.giveItems(scrl_of_ench_am_s, 4);
                else if (i0 < 40)
                    st.giveItems(scrl_of_ench_am_s, 5);
                else if (i0 < 45)
                    st.giveItems(scrl_of_ench_am_s, 6);
                else if (i0 < 49)
                    st.giveItems(scrl_of_ench_am_s, 7);
                else if (i0 < 52)
                    st.giveItems(scrl_of_ench_am_s, 8);
                else if (i0 < 54)
                    st.giveItems(scrl_of_ench_am_s, 9);
                else if (i0 < 55)
                    st.giveItems(scrl_of_ench_am_s, 10);
                else if (i0 < 75)
                    st.giveItems(high_ore_of_water, 1);
                else if (i0 < 90)
                    st.giveItems(high_ore_of_water, 2);
                else
                    st.giveItems(scrl_of_ench_wp_s, 1);
                st.takeItems(q_mane_of_farm_marauder, -1);
                st.removeMemo("keep_the_safe_in_love");
                st.soundEffect(SOUND_FINISH);
                htmltext = "beast_herder_tunatun_q0278_07.htm";
                st.exitQuest(true);
            }

        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("keep_the_safe_in_love");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == beast_herder_tunatun) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "beast_herder_tunatun_q0278_03.htm";
                            break;
                        default:
                            htmltext = "beast_herder_tunatun_q0278_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == beast_herder_tunatun)
                    if (GetMemoState == 2 && st.ownItemCount(q_mane_of_farm_marauder) >= 300)
                        htmltext = "beast_herder_tunatun_q0278_05.htm";
                    else if (GetMemoState == 1 && st.ownItemCount(q_mane_of_farm_marauder) < 300)
                        htmltext = "beast_herder_tunatun_q0278_06.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("keep_the_safe_in_love");
        int npcId = npc.getNpcId();

        if (GetMemoState == 1)
            if (npcId == farm_plunderer || npcId == farm_invader) {
                int i0 = Rnd.get(1000);
                if (i0 < 85)
                    if (st.ownItemCount(q_mane_of_farm_marauder) == 299) {
                        st.setCond(2);
                        st.setMemoState("keep_the_safe_in_love", String.valueOf(2), true);
                        st.soundEffect(SOUND_MIDDLE);
                        st.giveItems(q_mane_of_farm_marauder, 1);
                    } else {
                        st.giveItems(q_mane_of_farm_marauder, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            }
        return null;
    }
}