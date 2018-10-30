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
public class _310_OnlyWhatRemains extends Quest {
    // npc
    private static final int kintaijin = 32640;

    // mobs
    private static final int n_spike_stakato = 22617;
    private static final int n_spike_stakato_worker = 22618;
    private static final int n_spike_stakato_guard = 22619;
    private static final int n_female_spike_stakato = 22620;
    private static final int n_male_spike_stakato = 22621;
    private static final int n_male_spike_stakato_c = 22622;
    private static final int n_spike_stakato_shaman = 22623;
    private static final int n_cannibal_stakato = 22624;
    private static final int n_leader_canni_stakato = 22625;
    private static final int n_leader_canni_stakato_c = 22626;
    private static final int n_spike_stakato_soldier = 22627;
    private static final int n_spike_stakato_drone = 22628;
    private static final int n_spike_stakato_raider = 22629;
    private static final int n_nurse_spike_stakato = 22630;
    private static final int n_nurse_spike_stakato_c = 22631;
    private static final int n_baby_spike_stakato = 22632;
    private static final int n_spike_stakato_shaman_s = 22633;

    // questitem
    private static final int q310_dirty_bead = 14880;

    // etcitem
    private static final int grow_accelerator = 14832;
    private static final int five_color_ore = 14835;

    public _310_OnlyWhatRemains() {
        super(true);
        addStartNpc(kintaijin);
        addTalkId(kintaijin);
        addKillId(n_spike_stakato, n_spike_stakato_worker, n_spike_stakato_guard, n_female_spike_stakato, n_male_spike_stakato, n_male_spike_stakato_c, n_spike_stakato_shaman, n_cannibal_stakato, n_leader_canni_stakato, n_leader_canni_stakato_c, n_spike_stakato_soldier, n_spike_stakato_drone, n_spike_stakato_raider, n_nurse_spike_stakato, n_nurse_spike_stakato_c, n_baby_spike_stakato, n_spike_stakato_shaman_s);
        addQuestItem(q310_dirty_bead);
        addLevelCheck(81);
        addQuestCompletedCheck(240);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();

        if (npcId == kintaijin)
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("what_on_earth_is_it", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "kintaijin_q0310_06.htm";
            } else if (event.equalsIgnoreCase("reply_1"))
                htmltext = "kintaijin_q0310_04.htm";
            else if (event.equalsIgnoreCase("reply_2"))
                htmltext = "kintaijin_q0310_05.htm";
            else if (event.equalsIgnoreCase("reply_11"))
                htmltext = "kintaijin_q0310_11.htm";
            else if (event.equalsIgnoreCase("reply_12"))
                htmltext = "kintaijin_q0310_12.htm";
            else if (event.equalsIgnoreCase("reply_13")) {
                st.removeMemo("what_on_earth_is_it");
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                htmltext = "kintaijin_q0310_13.htm";
            }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("what_on_earth_is_it");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == kintaijin) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                        case QUEST:
                            htmltext = "kintaijin_q0310_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "kintaijin_q0310_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == kintaijin)
                    if (GetMemoState == 1 && st.ownItemCount(q310_dirty_bead) < 1)
                        htmltext = "kintaijin_q0310_07.htm";
                    else if (GetMemoState == 1 && st.ownItemCount(q310_dirty_bead) >= 1 && st.ownItemCount(q310_dirty_bead) < 500)
                        htmltext = "kintaijin_q0310_08.htm";
                    else if (GetMemoState == 1 && st.ownItemCount(q310_dirty_bead) >= 500) {
                        st.giveItems(grow_accelerator, 1);
                        st.takeItems(q310_dirty_bead, 500);
                        st.giveItems(five_color_ore, Rnd.get(3) + 1);
                        htmltext = "kintaijin_q0310_09.htm";
                    }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("what_on_earth_is_it");
        int npcId = npc.getNpcId();

        if (GetMemoState == 1)
            if (npcId == n_spike_stakato || npcId == n_spike_stakato_worker || npcId == n_spike_stakato_guard || npcId == n_spike_stakato_soldier || npcId == n_spike_stakato_drone || npcId == n_spike_stakato_raider) {
                int i0 = Rnd.get(1000);
                if (i0 < 646) {
                    st.giveItems(q310_dirty_bead, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == n_female_spike_stakato) {
                int i0 = Rnd.get(1000);
                if (i0 < 667) {
                    st.giveItems(q310_dirty_bead, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == n_male_spike_stakato || npcId == n_cannibal_stakato) {
                int i0 = Rnd.get(1000);
                if (i0 < 630) {
                    st.giveItems(q310_dirty_bead, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == n_male_spike_stakato_c || npcId == n_leader_canni_stakato_c) {
                int i0 = Rnd.get(1000);
                if (i0 < 940) {
                    st.giveItems(q310_dirty_bead, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == n_spike_stakato_shaman) {
                int i0 = Rnd.get(1000);
                if (i0 < 622) {
                    st.giveItems(q310_dirty_bead, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == n_leader_canni_stakato) {
                int i0 = Rnd.get(1000);
                if (i0 < 678) {
                    st.giveItems(q310_dirty_bead, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == n_nurse_spike_stakato || npcId == n_spike_stakato_shaman_s) {
                int i0 = Rnd.get(1000);
                if (i0 < 638) {
                    st.giveItems(q310_dirty_bead, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == n_nurse_spike_stakato_c) {
                int i0 = Rnd.get(1000);
                if (i0 < 880) {
                    st.giveItems(q310_dirty_bead, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == n_baby_spike_stakato) {
                int i0 = Rnd.get(1000);
                if (i0 < 722) {
                    st.giveItems(q310_dirty_bead, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        return null;
    }
}