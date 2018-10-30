package org.mmocore.gameserver.scripts.quests;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _235_MimirsElixir extends Quest {
    // npc
    private final static int magister_ladd = 30721;
    private final static int magister_joan = 30718;
    private final static int alchemical_mixing_jar = 31149;

    // mobs
    private final static int chimera_piece = 20965;
    private final static int bloody_guardian = 21090;

    // questitem
    private final static int q_star_of_destiny = 5011;
    private final static int magisters_mixing_stone = 5905;
    private final static int bloodfire = 6318;
    private final static int mimirs_elixir = 6319;
    private final static int pure_silver = 6320;
    private final static int true_gold = 6321;
    private final static int philosophers_stone = 6322;

    // etcitem
    private final static int scrl_of_ench_wp_a = 729;

    public _235_MimirsElixir() {
        super(false);
        addStartNpc(magister_ladd);
        addTalkId(magister_ladd, magister_joan, alchemical_mixing_jar);
        addKillId(chimera_piece, bloody_guardian);
        addLevelCheck(75);
        addRaceCheck(PlayerRace.kamael);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("water_of_mimir");
        int npcId = npc.getNpcId();

        if (npcId == magister_ladd) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("water_of_mimir", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "magister_ladd_q0235_06.htm";
            } else if (event.equalsIgnoreCase("reply_1"))
                htmltext = "magister_ladd_q0235_02.htm";
            else if (event.equalsIgnoreCase("reply_2"))
                htmltext = "magister_ladd_q0235_03.htm";
            else if (event.equalsIgnoreCase("reply_3"))
                htmltext = "magister_ladd_q0235_04.htm";
            else if (event.equalsIgnoreCase("reply_4"))
                htmltext = "magister_ladd_q0235_05.htm";
            else if (event.equalsIgnoreCase("reply_5") && GetMemoState == 1) {
                st.setCond(2);
                st.setMemoState("water_of_mimir", String.valueOf(2), true);
                htmltext = "magister_ladd_q0235_09.htm";
            } else if (event.equalsIgnoreCase("reply_6") && GetMemoState == 5) {
                st.setCond(6);
                st.setMemoState("water_of_mimir", String.valueOf(6), true);
                st.giveItems(magisters_mixing_stone, 1);
                htmltext = "magister_ladd_q0235_12.htm";
            } else if (event.equalsIgnoreCase("reply_7") && GetMemoState == 8)
                htmltext = "magister_ladd_q0235_15.htm";
            else if (event.equalsIgnoreCase("reply_8") && GetMemoState == 8 && st.ownItemCount(magisters_mixing_stone) >= 1 && st.ownItemCount(mimirs_elixir) >= 1) {
                npc.doCast(SkillTable.getInstance().getSkillEntry(4339, 1), st.getPlayer(), true);
                st.takeItems(magisters_mixing_stone, -1);
                st.takeItems(mimirs_elixir, -1);
                st.takeItems(q_star_of_destiny, -1);
                st.giveItems(scrl_of_ench_wp_a, 1);
                st.removeMemo("water_of_mimir");
                st.exitQuest(false);
                st.soundEffect(SOUND_FINISH);
                htmltext = "magister_ladd_q0235_16.htm";
            }
        } else if (npcId == magister_joan) {
            if (event.equalsIgnoreCase("reply_1"))
                htmltext = "magister_joan_q0235_02.htm";
            else if (event.equalsIgnoreCase("reply_2")) {
                st.setCond(3);
                st.setMemoState("water_of_mimir", String.valueOf(3), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "magister_joan_q0235_03.htm";
            } else if (event.equalsIgnoreCase("reply_3") && GetMemoState == 4 && st.ownItemCount(philosophers_stone) >= 1) {
                st.setCond(5);
                st.setMemoState("water_of_mimir", String.valueOf(5), true);
                st.giveItems(true_gold, 1);
                st.takeItems(philosophers_stone, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "magister_joan_q0235_06.htm";
            }
        } else if (npcId == alchemical_mixing_jar)
            if (event.equalsIgnoreCase("reply_1"))
                htmltext = "alchemical_mixing_jar_q0235_02.htm";
            else if (event.equalsIgnoreCase("reply_2")) {
                if (st.ownItemCount(pure_silver) < 1)
                    htmltext = "alchemical_mixing_jar_q0235_03.htm";
                else
                    htmltext = "alchemical_mixing_jar_q0235_04.htm";
            } else if (event.equalsIgnoreCase("reply_3"))
                htmltext = "alchemical_mixing_jar_q0235_05.htm";
            else if (event.equalsIgnoreCase("reply_4")) {
                if (st.ownItemCount(true_gold) >= 1)
                    htmltext = "alchemical_mixing_jar_q0235_06.htm";
                else
                    htmltext = "alchemical_mixing_jar_q0235_06a.htm";
            } else if (event.equalsIgnoreCase("reply_5"))
                htmltext = "alchemical_mixing_jar_q0235_07.htm";
            else if (event.equalsIgnoreCase("reply_6")) {
                if (st.ownItemCount(bloodfire) < 1)
                    htmltext = "alchemical_mixing_jar_q0235_08.htm";
                else
                    htmltext = "alchemical_mixing_jar_q0235_09.htm";
            } else if (event.equalsIgnoreCase("reply_7"))
                htmltext = "alchemical_mixing_jar_q0235_10.htm";
            else if (event.equalsIgnoreCase("reply_8"))
                htmltext = "alchemical_mixing_jar_q0235_11.htm";
            else if (event.equalsIgnoreCase("reply_9") && st.ownItemCount(bloodfire) >= 1 && st.ownItemCount(pure_silver) >= 1 && st.ownItemCount(true_gold) >= 1 && GetMemoState == 7) {
                st.setCond(8);
                st.setMemoState("water_of_mimir", String.valueOf(8), true);
                st.giveItems(mimirs_elixir, 1);
                st.takeItems(pure_silver, -1);
                st.takeItems(true_gold, -1);
                st.takeItems(bloodfire, -1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "alchemical_mixing_jar_q0235_12.htm";
            }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("water_of_mimir");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == magister_ladd) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "magister_ladd_q0235_01a.htm";
                            break;
                        case RACE:
                            if (st.ownItemCount(q_star_of_destiny) < 1)
                                htmltext = "magister_ladd_q0235_01b.htm";
                            else
                                htmltext = "magister_ladd_q0235_01.htm";
                            break;
                        default:
                            htmltext = "magister_ladd_q0235_01c.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == magister_ladd) {
                    if (GetMemoState == 1 && st.ownItemCount(pure_silver) < 1)
                        htmltext = "magister_ladd_q0235_07.htm";
                    else if (GetMemoState == 1 && st.ownItemCount(pure_silver) >= 1)
                        htmltext = "magister_ladd_q0235_08.htm";
                    else if (GetMemoState >= 2 && GetMemoState < 5)
                        htmltext = "magister_ladd_q0235_10.htm";
                    else if (GetMemoState == 5)
                        htmltext = "magister_ladd_q0235_11.htm";
                    else if (GetMemoState >= 6 && GetMemoState < 8)
                        htmltext = "magister_ladd_q0235_13.htm";
                    else if (GetMemoState == 8)
                        htmltext = "magister_ladd_q0235_14.htm";
                } else if (npcId == magister_joan) {
                    if (GetMemoState == 2)
                        htmltext = "magister_joan_q0235_01.htm";
                    else if (GetMemoState == 3)
                        htmltext = "magister_joan_q0235_04.htm";
                    else if (GetMemoState == 4)
                        htmltext = "magister_joan_q0235_05.htm";
                } else if (npcId == alchemical_mixing_jar)
                    if (GetMemoState == 7 && st.ownItemCount(magisters_mixing_stone) >= 1)
                        htmltext = "alchemical_mixing_jar_q0235_01.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("water_of_mimir");
        int npcId = npc.getNpcId();

        if (GetMemoState == 3 && npcId == chimera_piece && st.ownItemCount(philosophers_stone) == 0) {
            if (Rnd.get(5) == 0) {
                st.setCond(4);
                st.setMemoState("water_of_mimir", String.valueOf(4), true);
                st.giveItems(philosophers_stone, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (GetMemoState == 6 && npcId == bloody_guardian && st.ownItemCount(bloodfire) == 0)
            if (Rnd.get(5) == 0) {
                st.setCond(7);
                st.setMemoState("water_of_mimir", String.valueOf(7), true);
                st.giveItems(bloodfire, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        return null;
    }
}