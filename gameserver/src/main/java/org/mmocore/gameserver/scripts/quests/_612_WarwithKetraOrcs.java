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
public class _612_WarwithKetraOrcs extends Quest {
    // npc
    private static final int elder_ashas_barka_durai = 31377;

    // mobs
    private static final int ketra_orc_footman = 21324;
    private static final int ketra_orc_trooper = 21327;
    private static final int ketra_orc_scout = 21328;
    private static final int ketra_orc_shaman = 21329;
    private static final int ketra_orc_warrior = 21331;
    private static final int ketra_orc_captain = 21332;
    private static final int ketra_orc_medium = 21334;
    private static final int ketra_orc_centurion = 21336;
    private static final int ketra_orc_seer = 21338;
    private static final int ketra_orc_officer = 21339;
    private static final int ketra_orc_praefect = 21340;
    private static final int ketra_orc_overseer = 21342;
    private static final int ketra_orc_legatus = 21343;
    private static final int ketra_high_shaman = 21345;
    private static final int ketra_soothsayer = 21347;

    // questitem
    private static final int q_ketra_molar = 7234;
    private static final int q_nephentes_seed = 7187;

    public _612_WarwithKetraOrcs() {
        super(true);
        addStartNpc(elder_ashas_barka_durai);
        addKillId(ketra_orc_footman, ketra_orc_trooper, ketra_orc_scout, ketra_orc_shaman, ketra_orc_warrior, ketra_orc_captain, ketra_orc_medium, ketra_orc_centurion, ketra_orc_seer, ketra_orc_officer, ketra_orc_praefect, ketra_orc_overseer, ketra_orc_legatus, ketra_high_shaman, ketra_soothsayer);
        addQuestItem(q_ketra_molar);
        addLevelCheck(74, 80);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;

        if (event.equalsIgnoreCase("quest_accept")) {
            st.setCond(1);
            st.setMemoState("war_with_ketra_orcs", String.valueOf(1 * 10 + 1), true);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            htmltext = "elder_ashas_barka_durai_q0612_0104.htm";
        } else if (event.equalsIgnoreCase("reply_1"))
            htmltext = "elder_ashas_barka_durai_q0612_0201.htm";
        else if (event.equalsIgnoreCase("reply_3")) {
            if (st.ownItemCount(q_ketra_molar) >= 100) {
                st.takeItems(q_ketra_molar, 100);
                st.giveItems(q_nephentes_seed, 20);
                htmltext = "elder_ashas_barka_durai_q0612_0202.htm";
            } else
                htmltext = "elder_ashas_barka_durai_q0612_0203.htm";
        } else if (event.equalsIgnoreCase("reply_4")) {
            st.takeItems(q_ketra_molar, -1);
            st.removeMemo("war_with_ketra_orcs");
            st.soundEffect(SOUND_FINISH);
            st.exitQuest(true);
            htmltext = "elder_ashas_barka_durai_q0612_0204.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("war_with_ketra_orcs");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == elder_ashas_barka_durai) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "elder_ashas_barka_durai_q0612_0103.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "elder_ashas_barka_durai_q0612_0101.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == elder_ashas_barka_durai)
                    if (GetMemoState == 1 * 10 + 1)
                        if (st.ownItemCount(q_ketra_molar) == 0)
                            htmltext = "elder_ashas_barka_durai_q0612_0106.htm";
                        else
                            htmltext = "elder_ashas_barka_durai_q0612_0105.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("war_with_ketra_orcs");
        int npcId = npc.getNpcId();

        if (GetMemoState == 1 * 10 + 1)
            if (npcId == ketra_orc_footman) {
                int i4 = Rnd.get(1000);
                if (i4 < 500) {
                    st.giveItems(q_ketra_molar, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == ketra_orc_trooper) {
                int i4 = Rnd.get(1000);
                if (i4 < 510) {
                    st.giveItems(q_ketra_molar, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == ketra_orc_scout) {
                int i4 = Rnd.get(1000);
                if (i4 < 522) {
                    st.giveItems(q_ketra_molar, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == ketra_orc_shaman) {
                int i4 = Rnd.get(1000);
                if (i4 < 519) {
                    st.giveItems(q_ketra_molar, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == ketra_orc_warrior || npcId == ketra_orc_captain) {
                int i4 = Rnd.get(1000);
                if (i4 < 529) {
                    st.giveItems(q_ketra_molar, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == ketra_orc_medium) {
                int i4 = Rnd.get(1000);
                if (i4 < 539) {
                    st.giveItems(q_ketra_molar, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == ketra_orc_centurion) {
                int i4 = Rnd.get(1000);
                if (i4 < 548) {
                    st.giveItems(q_ketra_molar, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == ketra_orc_seer) {
                int i4 = Rnd.get(1000);
                if (i4 < 558) {
                    st.giveItems(q_ketra_molar, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == ketra_orc_officer || npcId == ketra_orc_praefect) {
                int i4 = Rnd.get(1000);
                if (i4 < 568) {
                    st.giveItems(q_ketra_molar, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == ketra_orc_overseer) {
                int i4 = Rnd.get(1000);
                if (i4 < 578) {
                    st.giveItems(q_ketra_molar, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == ketra_orc_legatus) {
                int i4 = Rnd.get(1000);
                if (i4 < 664) {
                    st.giveItems(q_ketra_molar, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == ketra_high_shaman) {
                int i4 = Rnd.get(1000);
                if (i4 < 713) {
                    st.giveItems(q_ketra_molar, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == ketra_soothsayer) {
                int i4 = Rnd.get(1000);
                if (i4 < 738) {
                    st.giveItems(q_ketra_molar, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        return null;
    }
}