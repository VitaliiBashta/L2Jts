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
 * @version 1.0
 * @date 27/10/2015
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _634_InSearchofDimensionalFragments extends Quest {
    // npc
    private static final int dimension_keeper_1 = 31494;
    private static final int dimension_keeper_2 = 31495;
    private static final int dimension_keeper_3 = 31496;
    private static final int dimension_keeper_4 = 31497;
    private static final int dimension_keeper_5 = 31498;
    private static final int dimension_keeper_6 = 31499;
    private static final int dimension_keeper_7 = 31500;
    private static final int dimension_keeper_8 = 31501;
    private static final int dimension_keeper_9 = 31502;
    private static final int dimension_keeper_10 = 31503;
    private static final int dimension_keeper_11 = 31504;
    private static final int dimension_keeper_12 = 31505;
    private static final int dimension_keeper_13 = 31506;
    private static final int dimension_keeper_14 = 31507;
    // mobs
    private static final int pit_tomb_stalker = 21144;
    private static final int pugatory_stalker = 21156;
    private static final int martyrium_guardian = 21208;
    private static final int martyrium_seer = 21209;
    private static final int vault_guardian = 21210;
    private static final int vault_seer = 21211;
    private static final int martyrium_monk = 21213;
    private static final int vault_sentinel = 21214;
    private static final int vault_monk = 21215;
    private static final int martyrium_priest = 21217;
    private static final int vault_warlord = 21218;
    private static final int vault_priest = 21219;
    private static final int mausoleum_inquisitor = 21221;
    private static final int sepulcher_archon = 21222;
    private static final int sepulcher_inquisitor = 21223;
    private static final int mausoleum_defender = 21224;
    private static final int mausoleum_sage = 21225;
    private static final int sepulcher_defender = 21226;
    private static final int sepulcher_sage = 21227;
    private static final int mausoleum_highguard = 21228;
    private static final int mausoleum_oracle = 21229;
    private static final int sepulcher_highguard = 21230;
    private static final int sepulcher_oracle = 21231;
    private static final int barrow_sentinel = 21236;
    private static final int barrow_monk = 21237;
    private static final int grave_sentinel = 21238;
    private static final int grave_monk = 21239;
    private static final int barrow_warlord = 21240;
    private static final int barrow_priest = 21241;
    private static final int grave_warlord = 21242;
    private static final int grave_priest = 21243;
    private static final int crypt_archon = 21244;
    private static final int crypt_inquisitor = 21245;
    private static final int tomb_archon = 21246;
    private static final int tomb_inquisitor = 21247;
    private static final int crypt_defender = 21248;
    private static final int crypt_sage = 21249;
    private static final int tomb_defender = 21250;
    private static final int tomb_sage = 21251;
    private static final int crypt_highguard = 21252;
    private static final int crypt_oracle = 21253;
    private static final int tomb_highguard = 21254;
    private static final int tomb_oracle = 21255;
    // etcitem
    private static final int q_piece_of_bubble = 7079;

    public _634_InSearchofDimensionalFragments() {
        super(true);
        addStartNpc(dimension_keeper_1, dimension_keeper_2, dimension_keeper_3, dimension_keeper_4, dimension_keeper_5, dimension_keeper_6, dimension_keeper_7, dimension_keeper_8, dimension_keeper_9, dimension_keeper_10, dimension_keeper_11, dimension_keeper_12, dimension_keeper_13, dimension_keeper_14);
        addKillId(pit_tomb_stalker, pugatory_stalker, martyrium_guardian, martyrium_seer, vault_guardian, vault_seer, martyrium_monk, vault_sentinel, vault_monk, martyrium_priest, vault_warlord, vault_priest, mausoleum_inquisitor, sepulcher_archon, sepulcher_inquisitor, mausoleum_defender, mausoleum_sage, sepulcher_defender, sepulcher_sage, mausoleum_highguard, mausoleum_oracle, sepulcher_highguard, sepulcher_oracle, barrow_sentinel, barrow_monk, grave_sentinel, grave_monk, barrow_warlord, barrow_priest, grave_warlord, grave_priest, crypt_archon, crypt_inquisitor, tomb_archon, tomb_inquisitor, crypt_defender, crypt_sage, tomb_defender, tomb_sage, crypt_highguard, crypt_oracle, tomb_highguard, tomb_oracle);
        addLevelCheck(20);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == dimension_keeper_1 || npcId == dimension_keeper_2 || npcId == dimension_keeper_3 || npcId == dimension_keeper_4 || npcId == dimension_keeper_5 || npcId == dimension_keeper_6 || npcId == dimension_keeper_7 || npcId == dimension_keeper_8 || npcId == dimension_keeper_9 || npcId == dimension_keeper_10 || npcId == dimension_keeper_11 || npcId == dimension_keeper_12 || npcId == dimension_keeper_13 || npcId == dimension_keeper_14) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "dimension_keeper_1_q0634_03.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=634&reply=1"))
                htmltext = "dimension_keeper_1_q0634_05.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=634&reply=2")) {
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                htmltext = "dimension_keeper_1_q0634_06.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=634&reply=3"))
                htmltext = "dimension_keeper_1_q0634_07.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == dimension_keeper_1 || npcId == dimension_keeper_2 || npcId == dimension_keeper_3 || npcId == dimension_keeper_4 || npcId == dimension_keeper_5 || npcId == dimension_keeper_6 || npcId == dimension_keeper_7 || npcId == dimension_keeper_8 || npcId == dimension_keeper_9 || npcId == dimension_keeper_10 || npcId == dimension_keeper_11 || npcId == dimension_keeper_12 || npcId == dimension_keeper_13 || npcId == dimension_keeper_14) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "dimension_keeper_1_q0634_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "dimension_keeper_1_q0634_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == dimension_keeper_1 || npcId == dimension_keeper_2 || npcId == dimension_keeper_3 || npcId == dimension_keeper_4 || npcId == dimension_keeper_5 || npcId == dimension_keeper_6 || npcId == dimension_keeper_7 || npcId == dimension_keeper_8 || npcId == dimension_keeper_9 || npcId == dimension_keeper_10 || npcId == dimension_keeper_11 || npcId == dimension_keeper_12 || npcId == dimension_keeper_13 || npcId == dimension_keeper_14) {
                    htmltext = "dimension_keeper_1_q0634_04.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int i0 = Math.round(0.150000f * npc.getLevel() + 1.600000f);
        if (Rnd.get(100) >= 10) {
            i0 = 0;
        }
        if (i0 > 0) {
            st.giveItems(q_piece_of_bubble, i0);
        }
        return null;
    }
}