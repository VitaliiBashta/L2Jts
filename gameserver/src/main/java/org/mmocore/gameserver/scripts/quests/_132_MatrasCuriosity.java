package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _132_MatrasCuriosity extends Quest {
    // npc
    private static final int matras = 32245;

    // mobs
    private static final int duke_devil = 25540;
    private static final int ranku = 25542;

    // etcitem
    private static final int q_ore_of_fire = 10521;
    private static final int q_ore_of_water = 10522;
    private static final int q_ore_of_earth = 10523;
    private static final int q_ore_of_wind = 10524;
    private static final int q_ore_of_unholy = 10525;
    private static final int q_ore_of_holy = 10526;

    // questitem
    private static final int q_blue_print_of_ranku = 9800;
    private static final int q_blue_print_of_duke = 9801;

    public _132_MatrasCuriosity() {
        super(PARTY_ALL);
        addStartNpc(matras);
        addKillId(ranku, duke_devil);
        addQuestItem(q_blue_print_of_ranku, q_blue_print_of_duke);
        addLevelCheck(76);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("curiosity_of_matras");

        if (event.equalsIgnoreCase("quest_accept")) {
            st.setCond(1);
            st.setMemoState("curiosity_of_matras", String.valueOf(1), true);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            String is_given = st.getPlayer().getPlayerVariables().get(PlayerVariables.QUEST_STATE_OF_132_ORE_GIVEN);
            if (is_given != null)
                htmltext = "matras_q0132_03a.htm";
            else {
                st.giveItems(q_ore_of_fire, 1);
                st.giveItems(q_ore_of_water, 1);
                st.giveItems(q_ore_of_earth, 1);
                st.giveItems(q_ore_of_wind, 1);
                st.giveItems(q_ore_of_unholy, 1);
                st.giveItems(q_ore_of_holy, 1);
                st.getPlayer().getPlayerVariables().set(PlayerVariables.QUEST_STATE_OF_132_ORE_GIVEN, "1", -1);
                htmltext = "matras_q0132_03.htm";
            }
        } else if (event.equalsIgnoreCase("reply_1") && GetMemoState == 2) {
            st.removeMemo("curiosity_of_matras");
            st.soundEffect(SOUND_FINISH);
            st.giveItems(ADENA_ID, 65884);
            st.addExpAndSp(50541, 5094);
            st.exitQuest(false);
            htmltext = "matras_q0132_07.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("curiosity_of_matras");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == matras) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "matras_q0132_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "matras_q0132_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == matras)
                    if (GetMemoState == 1) {
                        if (st.ownItemCount(q_blue_print_of_ranku) >= 1 && st.ownItemCount(q_blue_print_of_duke) >= 1) {
                            st.takeItems(q_blue_print_of_ranku, -1);
                            st.takeItems(q_blue_print_of_duke, -1);
                            st.setMemoState("curiosity_of_matras", String.valueOf(2), true);
                            htmltext = "matras_q0132_05.htm";
                            st.setCond(3);
                            st.soundEffect(SOUND_MIDDLE);
                        } else
                            htmltext = "matras_q0132_04.htm";
                    } else if (GetMemoState == 2)
                        htmltext = "matras_q0132_06.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("curiosity_of_matras");
        int npcId = npc.getNpcId();

        if (GetMemoState == 1)
            if (npcId == duke_devil) {
                if (st.ownItemCount(q_blue_print_of_ranku) == 0) {
                    st.giveItems(q_blue_print_of_duke, 1);
                    st.soundEffect(SOUND_ITEMGET);
                } else if (st.ownItemCount(q_blue_print_of_ranku) >= 1) {
                    st.giveItems(q_blue_print_of_duke, 1);
                    st.setCond(2);
                    st.soundEffect(SOUND_MIDDLE);
                }
            } else if (npcId == ranku)
                if (st.ownItemCount(q_blue_print_of_duke) == 0) {
                    st.giveItems(q_blue_print_of_ranku, 1);
                    st.soundEffect(SOUND_ITEMGET);
                } else if (st.ownItemCount(q_blue_print_of_duke) >= 1) {
                    st.giveItems(q_blue_print_of_ranku, 1);
                    st.setCond(2);
                    st.soundEffect(SOUND_MIDDLE);
                }
        return null;
    }
}