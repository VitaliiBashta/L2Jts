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
public class _351_BlackSwan extends Quest {
    // npc
    private static final int captain_gosta = 30916;
    private static final int iason_haine = 30969;
    private static final int head_blacksmith_roman = 30897;

    // mobs
    private static final int tasaba_lizardman = 20784;
    private static final int tasaba_lizardman_shaman = 20785;
    private static final int tasaba_lizardman_a = 21639;
    private static final int tasaba_lizardman_sham_a = 21640;

    // questitem
    private static final int q0351_order_of_gosta = 4296;
    private static final int q0351_lizard_fang = 4297;
    private static final int q0351_barrel_of_league = 4298;
    private static final int q0351_bill_of_iason = 4310;

    // etcitem
    private static final int animal_skin = 1867;
    private static final int animal_bone = 1872;
    private static final int coal = 1870;
    private static final int charcoal = 1871;
    private static final int leather = 1882;
    private static final int cokes = 1879;
    private static final int coarse_bone_powder = 1881;
    private static final int oriharukon_ore = 1874;
    private static final int stone_of_purity = 1875;
    private static final int crafted_leather = 1894;
    private static final int synthesis_cokes = 1888;
    private static final int varnish_of_purity = 1887;
    private static final int reinforcing_agent = 5220;

    public _351_BlackSwan() {
        super(false);
        addStartNpc(captain_gosta);
        addTalkId(iason_haine, head_blacksmith_roman);
        addKillId(tasaba_lizardman, tasaba_lizardman_shaman, tasaba_lizardman_a, tasaba_lizardman_sham_a);
        addQuestItem(q0351_order_of_gosta, q0351_lizard_fang, q0351_barrel_of_league);
        addLevelCheck(32, 36);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();

        if (npcId == captain_gosta) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("black_swan", String.valueOf(1), true);
                st.giveItems(q0351_order_of_gosta, 1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "captain_gosta_q0351_04.htm";
            } else if (event.equalsIgnoreCase("reply_1"))
                htmltext = "captain_gosta_q0351_03.htm";
        } else if (npcId == iason_haine) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (st.ownItemCount(q0351_lizard_fang) == 0)
                    htmltext = "iason_haine_q0351_02.htm";
                else if (st.ownItemCount(q0351_lizard_fang) >= 10)
                    st.giveItems(ADENA_ID, 3880 + 20 * st.ownItemCount(q0351_lizard_fang));
                else
                    st.giveItems(ADENA_ID, 20 * st.ownItemCount(q0351_lizard_fang));
                st.takeItems(q0351_lizard_fang, -1);
                htmltext = "iason_haine_q0351_03.htm";
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (st.ownItemCount(q0351_barrel_of_league) == 0)
                    htmltext = "iason_haine_q0351_04.htm";
                st.setCond(2);
                st.giveItems(q0351_bill_of_iason, st.ownItemCount(q0351_barrel_of_league));
                st.giveItems(ADENA_ID, 3880);
                st.takeItems(q0351_barrel_of_league, -1);
                htmltext = "iason_haine_q0351_05.htm";
            } else if (event.equalsIgnoreCase("reply_3"))
                htmltext = "iason_haine_q0351_06.htm";
            else if (event.equalsIgnoreCase("reply_4")) {
                if (st.ownItemCount(q0351_barrel_of_league) == 0 && st.ownItemCount(q0351_lizard_fang) == 0)
                    htmltext = "iason_haine_q0351_07.htm";
                else
                    htmltext = "iason_haine_q0351_08.htm";
            } else if (event.equalsIgnoreCase("reply_5")) {
                if (st.ownItemCount(q0351_order_of_gosta) > 0)
                    st.takeItems(q0351_order_of_gosta, 1);
                st.removeMemo("black_swan");
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                htmltext = "iason_haine_q0351_09.htm";
            }
        } else if (npcId == head_blacksmith_roman)
            if (event.equalsIgnoreCase("reply_1")) {
                if (st.ownItemCount(q0351_bill_of_iason) > 0) {
                    st.giveItems(ADENA_ID, 700);
                    st.takeItems(q0351_bill_of_iason, 1);
                    htmltext = "head_blacksmith_roman_q0351_03.htm";
                } else
                    htmltext = "head_blacksmith_roman_q0351_04.htm";
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (st.ownItemCount(q0351_bill_of_iason) >= 3) {
                    st.giveItems(animal_skin, 20);
                    st.takeItems(q0351_bill_of_iason, 3);
                    htmltext = "head_blacksmith_roman_q0351_03.htm";
                } else
                    htmltext = "head_blacksmith_roman_q0351_04.htm";
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (st.ownItemCount(q0351_bill_of_iason) >= 3) {
                    st.giveItems(animal_bone, 20);
                    st.takeItems(q0351_bill_of_iason, 3);
                    htmltext = "head_blacksmith_roman_q0351_03.htm";
                } else
                    htmltext = "head_blacksmith_roman_q0351_04.htm";
            } else if (event.equalsIgnoreCase("reply_4")) {
                if (st.ownItemCount(q0351_bill_of_iason) >= 2) {
                    st.giveItems(coal, 10);
                    st.takeItems(q0351_bill_of_iason, 2);
                    htmltext = "head_blacksmith_roman_q0351_03.htm";
                } else
                    htmltext = "head_blacksmith_roman_q0351_04.htm";
            } else if (event.equalsIgnoreCase("reply_5")) {
                if (st.ownItemCount(q0351_bill_of_iason) >= 2) {
                    st.giveItems(charcoal, 10);
                    st.takeItems(q0351_bill_of_iason, 2);
                    htmltext = "head_blacksmith_roman_q0351_03.htm";
                } else
                    htmltext = "head_blacksmith_roman_q0351_04.htm";
            } else if (event.equalsIgnoreCase("reply_6")) {
                if (st.ownItemCount(q0351_bill_of_iason) >= 9) {
                    st.giveItems(leather, 10);
                    st.takeItems(q0351_bill_of_iason, 9);
                    htmltext = "head_blacksmith_roman_q0351_03.htm";
                } else
                    htmltext = "head_blacksmith_roman_q0351_04.htm";
            } else if (event.equalsIgnoreCase("reply_7")) {
                if (st.ownItemCount(q0351_bill_of_iason) >= 5) {
                    st.giveItems(cokes, 6);
                    st.takeItems(q0351_bill_of_iason, 5);
                    htmltext = "head_blacksmith_roman_q0351_03.htm";
                } else
                    htmltext = "head_blacksmith_roman_q0351_04.htm";
            } else if (event.equalsIgnoreCase("reply_8")) {
                if (st.ownItemCount(q0351_bill_of_iason) >= 3) {
                    st.giveItems(coarse_bone_powder, 2);
                    st.takeItems(q0351_bill_of_iason, 3);
                    htmltext = "head_blacksmith_roman_q0351_03.htm";
                } else
                    htmltext = "head_blacksmith_roman_q0351_04.htm";
            } else if (event.equalsIgnoreCase("reply_9")) {
                if (st.ownItemCount(q0351_bill_of_iason) >= 3) {
                    st.giveItems(oriharukon_ore, 1);
                    st.takeItems(q0351_bill_of_iason, 3);
                    htmltext = "head_blacksmith_roman_q0351_03.htm";
                } else
                    htmltext = "head_blacksmith_roman_q0351_04.htm";
            } else if (event.equalsIgnoreCase("reply_10")) {
                if (st.ownItemCount(q0351_bill_of_iason) >= 3) {
                    st.giveItems(stone_of_purity, 1);
                    st.takeItems(q0351_bill_of_iason, 3);
                    htmltext = "head_blacksmith_roman_q0351_03.htm";
                } else
                    htmltext = "head_blacksmith_roman_q0351_04.htm";
            } else if (event.equalsIgnoreCase("reply_11")) {
                if (st.ownItemCount(q0351_bill_of_iason) >= 6) {
                    st.giveItems(crafted_leather, 1);
                    st.giveItems(ADENA_ID, 210);
                    st.takeItems(q0351_bill_of_iason, 6);
                    htmltext = "head_blacksmith_roman_q0351_03.htm";
                } else
                    htmltext = "head_blacksmith_roman_q0351_04.htm";
            } else if (event.equalsIgnoreCase("reply_12")) {
                if (st.ownItemCount(q0351_bill_of_iason) >= 7) {
                    st.giveItems(synthesis_cokes, 1);
                    st.giveItems(ADENA_ID, 280);
                    st.takeItems(q0351_bill_of_iason, 7);
                    htmltext = "head_blacksmith_roman_q0351_03.htm";
                } else
                    htmltext = "head_blacksmith_roman_q0351_04.htm";
            } else if (event.equalsIgnoreCase("reply_13")) {
                if (st.ownItemCount(q0351_bill_of_iason) >= 9) {
                    st.giveItems(varnish_of_purity, 1);
                    st.giveItems(ADENA_ID, 630);
                    st.takeItems(q0351_bill_of_iason, 9);
                    htmltext = "head_blacksmith_roman_q0351_03.htm";
                } else
                    htmltext = "head_blacksmith_roman_q0351_04.htm";
            } else if (event.equalsIgnoreCase("reply_15")) {
                if (st.ownItemCount(q0351_bill_of_iason) >= 5) {
                    st.giveItems(reinforcing_agent, 1);
                    st.takeItems(q0351_bill_of_iason, 5);
                    htmltext = "head_blacksmith_roman_q0351_03.htm";
                } else
                    htmltext = "head_blacksmith_roman_q0351_04.htm";
            } else if (event.equalsIgnoreCase("reply_14"))
                htmltext = "head_blacksmith_roman_q0351_05.htm";

        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("black_swan");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == captain_gosta) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "captain_gosta_q0351_01.htm";
                            break;
                        default:
                            htmltext = "captain_gosta_q0351_02.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == captain_gosta) {
                    if (GetMemoState >= 0)
                        htmltext = "captain_gosta_q0351_05.htm";
                } else if (npcId == iason_haine) {
                    if (GetMemoState == 1)
                        htmltext = "iason_haine_q0351_01.htm";
                } else if (npcId == head_blacksmith_roman)
                    if (st.ownItemCount(q0351_bill_of_iason) >= 1)
                        htmltext = "head_blacksmith_roman_q0351_01.htm";
                    else if (st.ownItemCount(q0351_bill_of_iason) == 0)
                        htmltext = "head_blacksmith_roman_q0351_02.htm";
                break;
        }

        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();

        if (st.ownItemCount(q0351_order_of_gosta) > 0)
            if (npcId == tasaba_lizardman || npcId == tasaba_lizardman_a) {
                int i0 = Rnd.get(100);
                if (i0 < 10) {
                    st.giveItems(q0351_lizard_fang, 1);
                    st.soundEffect(SOUND_ITEMGET);
                    if (Rnd.get(20) == 0)
                        st.giveItems(q0351_barrel_of_league, 1);
                } else if (i0 < 15) {
                    st.giveItems(q0351_lizard_fang, 2);
                    st.soundEffect(SOUND_ITEMGET);
                    if (Rnd.get(20) == 0)
                        st.giveItems(q0351_barrel_of_league, 1);
                } else if (Rnd.get(100) < 4) {
                    st.giveItems(q0351_barrel_of_league, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == tasaba_lizardman_shaman || npcId == tasaba_lizardman_sham_a) {
                int i0 = Rnd.get(20);
                if (i0 < 10) {
                    st.giveItems(q0351_lizard_fang, 1);
                    st.soundEffect(SOUND_ITEMGET);
                    if (Rnd.get(20) == 0)
                        st.giveItems(q0351_barrel_of_league, 1);
                } else if (i0 < 15) {
                    st.giveItems(q0351_lizard_fang, 2);
                    st.soundEffect(SOUND_ITEMGET);
                    if (Rnd.get(20) == 0)
                        st.giveItems(q0351_barrel_of_league, 1);
                } else if (Rnd.get(100) < 3) {
                    st.giveItems(q0351_barrel_of_league, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }

        return null;
    }
}