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
public class _702_ATrapForRevenge extends Quest {
    // npc
    private final static int wharf_soldier_plenos = 32563;
    private final static int soldier_tenis = 32555;
    private final static int engineer_recon = 32557;

    // mobs
    private final static int drake_wing_knave_1lv = 22610;
    private final static int drake_wing_knave_2lv = 22611;
    private final static int drac_1lv = 22612;
    private final static int drac_2lv = 22613;
    private final static int drake_wing_knave_3lv = 25631;
    private final static int drac_3lv = 25632;

    // questitem
    private final static int q_drake_flesh = 13877;
    private final static int q_decayed_blood = 13878;
    private final static int q_horn_of_m_drake_wing = 13880;

    // etcitem
    private final static int drake_wing_lure = 13879;
    private final static int red_star_stone = 14009;
    private final static int renad = 9628;
    private final static int adamantium = 9629;
    private final static int oricalcum = 9630;

    public _702_ATrapForRevenge() {
        super(true);
        addStartNpc(wharf_soldier_plenos);
        addTalkId(soldier_tenis, engineer_recon);
        addKillId(drake_wing_knave_1lv, drake_wing_knave_2lv, drac_1lv, drac_2lv, drake_wing_knave_3lv, drac_3lv);
        addLevelCheck(78);
        addQuestCompletedCheck(10273);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("trap_for_revenge");
        int npcId = npc.getNpcId();

        if (npcId == wharf_soldier_plenos) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("trap_for_revenge", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "wharf_soldier_plenos_q0702_04.htm";
            } else if (event.equalsIgnoreCase("reply_1"))
                htmltext = "wharf_soldier_plenos_q0702_03.htm";
            else if (event.equalsIgnoreCase("reply_10")) {
                if (GetMemoState == 2 && st.ownItemCount(q_drake_flesh) < 1)
                    htmltext = "wharf_soldier_plenos_q0702_07.htm";
                else if (GetMemoState == 2 && st.ownItemCount(q_drake_flesh) >= 1)
                    htmltext = "wharf_soldier_plenos_q0702_08.htm";
            } else if (event.equalsIgnoreCase("reply_11")) {
                if (GetMemoState == 2 && st.ownItemCount(q_drake_flesh) >= 1) {
                    st.giveItems(ADENA_ID, st.ownItemCount(q_drake_flesh) * 100);
                    st.takeItems(q_drake_flesh, -1);
                    htmltext = "wharf_soldier_plenos_q0702_09.htm";
                }
            } else if (event.equalsIgnoreCase("reply_12")) {
                if (GetMemoState == 2 && st.ownItemCount(q_drake_flesh) >= 1)
                    htmltext = "wharf_soldier_plenos_q0702_10.htm";
            } else if (event.equalsIgnoreCase("reply_20")) {
                if (GetMemoState == 2 && st.ownItemCount(q_horn_of_m_drake_wing) < 1)
                    htmltext = "wharf_soldier_plenos_q0702_11.htm";
                else if (GetMemoState == 2 && st.ownItemCount(q_horn_of_m_drake_wing) >= 1) {
                    st.giveItems(ADENA_ID, st.ownItemCount(q_horn_of_m_drake_wing) * 200000);
                    st.takeItems(q_horn_of_m_drake_wing, -1);
                    htmltext = "wharf_soldier_plenos_q0702_12.htm";
                }
            } else if (event.equalsIgnoreCase("reply_21")) {
                if (GetMemoState == 2)
                    htmltext = "wharf_soldier_plenos_q0702_13.htm";
            } else if (event.equalsIgnoreCase("reply_22"))
                if (GetMemoState == 2) {
                    st.removeMemo("trap_for_revenge");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(true);
                    htmltext = "wharf_soldier_plenos_q0702_14.htm";
                }
        } else if (npcId == soldier_tenis) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 1)
                    htmltext = "soldier_tenis_q0702_02.htm";
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 1) {
                    st.setCond(2);
                    st.setMemoState("trap_for_revenge", String.valueOf(2), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "soldier_tenis_q0702_03.htm";
                }
            } else if (event.equalsIgnoreCase("reply_10")) {
                if (GetMemoState == 2) {
                    st.removeMemo("trap_for_revenge");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(true);
                    htmltext = "soldier_tenis_q0702_05.htm";
                }
            } else if (event.equalsIgnoreCase("reply_20")) {
                if (GetMemoState == 2 && st.ownItemCount(q_drake_flesh) < 100)
                    htmltext = "soldier_tenis_q0702_06.htm";
                else if (GetMemoState == 2 && st.ownItemCount(q_drake_flesh) >= 100)
                    htmltext = "soldier_tenis_q0702_07.htm";
            } else if (event.equalsIgnoreCase("reply_21")) {
                if (GetMemoState == 2 && st.ownItemCount(q_drake_flesh) >= 100) {
                    st.giveItems(q_decayed_blood, 1);
                    st.takeItems(q_drake_flesh, 100);
                    htmltext = "soldier_tenis_q0702_08.htm";
                }
            } else if (event.equalsIgnoreCase("reply_22")) {
                if (GetMemoState == 2 && st.ownItemCount(q_drake_flesh) >= 100)
                    htmltext = "soldier_tenis_q0702_09.htm";
            } else if (event.equalsIgnoreCase("reply_30")) {
                if (GetMemoState == 2 && st.ownItemCount(q_horn_of_m_drake_wing) < 1)
                    htmltext = "soldier_tenis_q0702_10.htm";
                else if (GetMemoState == 2 && st.ownItemCount(q_horn_of_m_drake_wing) >= 1)
                    htmltext = "soldier_tenis_q0702_11.htm";
            } else if (event.equalsIgnoreCase("reply_31")) {
                if (GetMemoState == 2 && st.ownItemCount(q_horn_of_m_drake_wing) >= 1)
                    htmltext = "soldier_tenis_q0702_12.htm";
            } else if (event.equalsIgnoreCase("reply_32")) {
                if (GetMemoState == 2 && st.ownItemCount(q_horn_of_m_drake_wing) >= 1)
                    htmltext = "soldier_tenis_q0702_13.htm";
            } else if (event.equalsIgnoreCase("reply_40")) {
                if (GetMemoState == 2 && st.ownItemCount(q_horn_of_m_drake_wing) >= 1)
                    htmltext = "soldier_tenis_q0702_14.htm";
            } else if (event.equalsIgnoreCase("reply_41"))
                if (GetMemoState == 2 && st.ownItemCount(q_horn_of_m_drake_wing) >= 1) {
                    int i0 = Rnd.get(1000);
                    int i1 = Rnd.get(1000);
                    if (i0 >= 500 && i1 >= 600) {
                        st.giveItems(ADENA_ID, Rnd.get(49917) + 125000);
                        if (i1 < 720) {
                            st.giveItems(renad, Rnd.get(3) + 1);
                            st.giveItems(adamantium, Rnd.get(3) + 1);
                        } else if (i1 < 840) {
                            st.giveItems(adamantium, Rnd.get(3) + 1);
                            st.giveItems(oricalcum, Rnd.get(3) + 1);
                        } else if (i1 < 960) {
                            st.giveItems(renad, Rnd.get(3) + 1);
                            st.giveItems(oricalcum, Rnd.get(3) + 1);
                        } else if (i1 < 1000) {
                            st.giveItems(renad, Rnd.get(3) + 1);
                            st.giveItems(adamantium, Rnd.get(3) + 1);
                            st.giveItems(oricalcum, Rnd.get(3) + 1);
                        }
                        htmltext = "soldier_tenis_q0702_15.htm";
                    } else if (i0 >= 500 && i1 < 600) {
                        st.giveItems(ADENA_ID, Rnd.get(49917) + 125000);
                        if (i1 < 210) {
                        } else if (i1 < 340)
                            st.giveItems(renad, Rnd.get(3) + 1);
                        else if (i1 < 470)
                            st.giveItems(adamantium, Rnd.get(3) + 1);
                        else if (i1 < 600)
                            st.giveItems(oricalcum, Rnd.get(3) + 1);
                        htmltext = "soldier_tenis_q0702_16.htm";
                    } else if (i0 < 500 && i1 >= 600) {
                        st.giveItems(ADENA_ID, Rnd.get(49917) + 25000);
                        if (i1 < 720) {
                            st.giveItems(renad, Rnd.get(3) + 1);
                            st.giveItems(adamantium, Rnd.get(3) + 1);
                        } else if (i1 < 840) {
                            st.giveItems(adamantium, Rnd.get(3) + 1);
                            st.giveItems(oricalcum, Rnd.get(3) + 1);
                        } else if (i1 < 960) {
                            st.giveItems(renad, Rnd.get(3) + 1);
                            st.giveItems(oricalcum, Rnd.get(3) + 1);
                        } else if (i1 < 1000) {
                            st.giveItems(renad, Rnd.get(3) + 1);
                            st.giveItems(adamantium, Rnd.get(3) + 1);
                            st.giveItems(oricalcum, Rnd.get(3) + 1);
                        }
                        htmltext = "soldier_tenis_q0702_17.htm";
                    } else if (i0 < 500 && i1 < 600) {
                        st.giveItems(ADENA_ID, Rnd.get(49917) + 25000);
                        if (i1 < 210) {
                        } else if (i1 < 340)
                            st.giveItems(renad, Rnd.get(3) + 1);
                        else if (i1 < 470)
                            st.giveItems(adamantium, Rnd.get(3) + 1);
                        else if (i1 < 600)
                            st.giveItems(oricalcum, Rnd.get(3) + 1);
                        htmltext = "soldier_tenis_q0702_18.htm";
                    }
                    st.takeItems(q_horn_of_m_drake_wing, 1);
                }
        } else if (npcId == engineer_recon)
            if (event.equalsIgnoreCase("reply_1"))
                if (GetMemoState == 2 && st.ownItemCount(q_decayed_blood) < 1 && st.ownItemCount(red_star_stone) < 100)
                    htmltext = "engineer_recon_q0702_03.htm";
                else if (GetMemoState == 2 && st.ownItemCount(q_decayed_blood) >= 1 && st.ownItemCount(red_star_stone) < 100)
                    htmltext = "engineer_recon_q0702_04.htm";
                else if (GetMemoState == 2 && st.ownItemCount(q_decayed_blood) < 1 && st.ownItemCount(red_star_stone) >= 100)
                    htmltext = "engineer_recon_q0702_05.htm";
                else if (GetMemoState == 2 && st.ownItemCount(q_decayed_blood) >= 1 && st.ownItemCount(red_star_stone) >= 100) {
                    st.giveItems(drake_wing_lure, 1);
                    st.takeItems(q_decayed_blood, 1);
                    st.takeItems(red_star_stone, 100);
                    htmltext = "engineer_recon_q0702_06.htm";
                }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("trap_for_revenge");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == wharf_soldier_plenos) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                        case QUEST:
                            htmltext = "wharf_soldier_plenos_q0702_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "wharf_soldier_plenos_q0702_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == wharf_soldier_plenos) {
                    if (GetMemoState == 1)
                        htmltext = "wharf_soldier_plenos_q0702_05.htm";
                    else if (GetMemoState == 2)
                        htmltext = "wharf_soldier_plenos_q0702_06.htm";
                } else if (npcId == soldier_tenis) {
                    if (GetMemoState == 1)
                        htmltext = "soldier_tenis_q0702_01.htm";
                    else if (GetMemoState == 2)
                        htmltext = "soldier_tenis_q0702_04.htm";
                } else if (npcId == engineer_recon)
                    if (GetMemoState == 1)
                        htmltext = "engineer_recon_q0702_01.htm";
                    else if (GetMemoState == 2)
                        htmltext = "engineer_recon_q0702_02.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("trap_for_revenge");
        int npcId = npc.getNpcId();

        if (GetMemoState == 2)
            if (npcId == drake_wing_knave_1lv || npcId == drake_wing_knave_3lv) {
                int i0 = Rnd.get(1000);
                if (i0 < 485)
                    st.giveItems(q_drake_flesh, 2);
                else
                    st.giveItems(q_drake_flesh, 1);
                st.soundEffect(SOUND_ITEMGET);
            } else if (npcId == drake_wing_knave_2lv) {
                int i0 = Rnd.get(1000);
                if (i0 < 451)
                    st.giveItems(q_drake_flesh, 2);
                else
                    st.giveItems(q_drake_flesh, 1);
                st.soundEffect(SOUND_ITEMGET);
            } else if (npcId == drac_1lv) {
                int i0 = Rnd.get(1000);
                if (i0 < 413)
                    st.giveItems(q_drake_flesh, 2);
                else
                    st.giveItems(q_drake_flesh, 1);
                st.soundEffect(SOUND_ITEMGET);
            } else if (npcId == drac_2lv) {
                int i0 = Rnd.get(1000);
                if (i0 < 440)
                    st.giveItems(q_drake_flesh, 2);
                else
                    st.giveItems(q_drake_flesh, 1);
                st.soundEffect(SOUND_ITEMGET);
            } else if (npcId == drac_3lv) {
                int i0 = Rnd.get(1000);
                if (i0 < 996) {
                    st.giveItems(q_drake_flesh, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        return null;
    }
}