package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.serverpackets.ExStartScenePlayer;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _192_SevenSignSeriesOfDoubt extends Quest {
    // npc
    private final static int warehouse_chief_croop = 30676;
    private final static int heiac = 30197;
    private final static int stan = 30200;
    private final static int contractor_jacob = 32568;
    private final static int hollin = 30191;
    // questitem
    private final static int q_voucher_of_croop = 13813;
    private final static int q_necklace_of_jacob = 13814;
    private final static int q_letter_of_croop = 13815;

    public _192_SevenSignSeriesOfDoubt() {
        super(false);
        addStartNpc(warehouse_chief_croop);
        addTalkId(heiac, stan, contractor_jacob, hollin);
        addQuestItem(q_voucher_of_croop, q_necklace_of_jacob, q_letter_of_croop);
        addLevelCheck(79);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("ssq_suspicious_deaths");
        int StartScenePlayer = ExStartScenePlayer.SCENE_SSQ_SUSPICIOUS_DEATH;
        int npcId = npc.getNpcId();
        if (npcId == warehouse_chief_croop) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("ssq_suspicious_deaths", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "warehouse_chief_croop_q0192_05.htm";
            } else if (event.equalsIgnoreCase("reply_1"))
                htmltext = "warehouse_chief_croop_q0192_04.htm";
            else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 1) {
                    st.setCond(2);
                    st.setMemoState("ssq_suspicious_deaths", String.valueOf(2), true);
                    st.soundEffect(SOUND_MIDDLE);
                    st.getPlayer().showQuestMovie(StartScenePlayer);
                    final Location loc = st.getPlayer().getLoc();
                    ThreadPoolManager.getInstance().schedule(new AdTimerEx(st.getPlayer(), loc), 26500);
                    return null;
                }
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 6)
                    if (st.ownItemCount(q_necklace_of_jacob) >= 1)
                        htmltext = "warehouse_chief_croop_q0192_11.htm";
            } else if (event.equalsIgnoreCase("reply_4")) {
                if (GetMemoState == 6)
                    if (st.ownItemCount(q_necklace_of_jacob) >= 1)
                        htmltext = "warehouse_chief_croop_q0192_12.htm";
            } else if (event.equalsIgnoreCase("reply_5")) {
                if (GetMemoState == 6)
                    if (st.ownItemCount(q_necklace_of_jacob) >= 1)
                        htmltext = "warehouse_chief_croop_q0192_13.htm";
            } else if (event.equalsIgnoreCase("reply_6")) {
                if (GetMemoState == 6)
                    if (st.ownItemCount(q_necklace_of_jacob) >= 1)
                        htmltext = "warehouse_chief_croop_q0192_14.htm";
            } else if (event.equalsIgnoreCase("reply_7"))
                if (GetMemoState == 6)
                    if (st.ownItemCount(q_necklace_of_jacob) >= 1) {
                        st.setCond(7);
                        st.setMemoState("ssq_suspicious_deaths", String.valueOf(7), true);
                        st.giveItems(q_letter_of_croop, 1);
                        st.takeItems(q_necklace_of_jacob, -1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "warehouse_chief_croop_q0192_15.htm";
                    }
        } else if (npcId == heiac) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 3)
                    if (st.ownItemCount(q_voucher_of_croop) >= 1)
                        htmltext = "heiac_q0192_02.htm";
            } else if (event.equalsIgnoreCase("reply_2"))
                if (GetMemoState == 3)
                    if (st.ownItemCount(q_voucher_of_croop) >= 1) {
                        st.setCond(4);
                        st.setMemoState("ssq_suspicious_deaths", String.valueOf(4), true);
                        st.takeItems(q_voucher_of_croop, -1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "heiac_q0192_03.htm";
                    }
        } else if (npcId == stan) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 4)
                    htmltext = "stan_q0192_02.htm";
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 4)
                    htmltext = "stan_q0192_03.htm";
            } else if (event.equalsIgnoreCase("reply_3"))
                if (GetMemoState == 4) {
                    st.setCond(5);
                    st.setMemoState("ssq_suspicious_deaths", String.valueOf(5), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "stan_q0192_04.htm";
                }
        } else if (npcId == contractor_jacob) {
            if (event.equalsIgnoreCase("reply_1"))
                if (GetMemoState == 5) {
                    st.setCond(6);
                    st.setMemoState("ssq_suspicious_deaths", String.valueOf(6), true);
                    st.giveItems(q_necklace_of_jacob, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "contractor_jacob_q0192_02.htm";
                }
        } else if (npcId == hollin)
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 7)
                    if (st.ownItemCount(q_letter_of_croop) >= 1)
                        htmltext = "hollin_q0192_02.htm";
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 7) {
                    if (st.ownItemCount(q_letter_of_croop) >= 1 && st.getPlayer().getLevel() >= 79) {
                        st.addExpAndSp(52518015, 5817677);
                        st.takeItems(q_letter_of_croop, -1);
                        st.removeMemo("ssq_suspicious_deaths");
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                        htmltext = "hollin_q0192_03.htm";
                    } else if (st.ownItemCount(q_letter_of_croop) >= 1 && st.getPlayer().getLevel() < 79)
                        htmltext = "level_check_q0192_01.htm";
                }
            }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("ssq_suspicious_deaths");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == warehouse_chief_croop) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "warehouse_chief_croop_q0192_03.htm";
                            break;
                        default:
                            htmltext = "warehouse_chief_croop_q0192_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == warehouse_chief_croop) {
                    if (GetMemoState == 1)
                        htmltext = "warehouse_chief_croop_q0192_16.htm";
                    else if (GetMemoState == 2) {
                        st.setCond(3);
                        st.setMemoState("ssq_suspicious_deaths", String.valueOf(3), true);
                        st.giveItems(q_voucher_of_croop, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "warehouse_chief_croop_q0192_07.htm";
                    } else if (GetMemoState > 2 && GetMemoState < 6)
                        htmltext = "warehouse_chief_croop_q0192_09.htm";
                    else if (GetMemoState == 6)
                        if (st.ownItemCount(q_necklace_of_jacob) >= 1)
                            htmltext = "warehouse_chief_croop_q0192_10.htm";
                } else if (npcId == heiac) {
                    if (GetMemoState == 3) {
                        if (st.ownItemCount(q_voucher_of_croop) >= 1)
                            htmltext = "heiac_q0192_01.htm";
                    } else if (GetMemoState > 3)
                        htmltext = "heiac_q0192_04.htm";
                } else if (npcId == stan) {
                    if (GetMemoState == 4)
                        htmltext = "stan_q0192_01.htm";
                    else if (GetMemoState > 4)
                        htmltext = "stan_q0192_05.htm";
                } else if (npcId == contractor_jacob) {
                    if (GetMemoState == 5)
                        htmltext = "contractor_jacob_q0192_01.htm";
                    else if (GetMemoState < 5)
                        htmltext = "contractor_jacob_q0192_03.htm";
                } else if (npcId == hollin)
                    if (GetMemoState == 7)
                        if (st.ownItemCount(q_letter_of_croop) >= 1)
                            htmltext = "hollin_q0192_01.htm";
                break;
            case COMPLETED:
                if (npcId == warehouse_chief_croop)
                    htmltext = "warehouse_chief_croop_q0192_02.htm";
                break;
        }
        return htmltext;
    }

    private class AdTimerEx extends RunnableImpl {
        private final Player player;
        private final Location loc;

        AdTimerEx(final Player player, final Location loc) {
            this.player = player;
            this.loc = loc;
        }

        public void runImpl() {
            if (player != null) {
                player.teleToLocation(loc);
            }
        }
    }
}