package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.manager.QuestManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _001_LettersOfLove extends Quest {
    // npc
    private final static int daring = 30048;
    private final static int rapunzel = 30006;
    private final static int baul = 30033;
    // questitem
    private final static int darings_letter = 687;
    private final static int rapunzels_kerchief = 688;
    private final static int darings_receipt = 1079;
    private final static int bauls_potion = 1080;
    // accessary
    private final static int necklace_of_knowledge = 906;

    public _001_LettersOfLove() {
        super(false);
        addStartNpc(daring);
        addTalkId(rapunzel, baul);
        addQuestItem(darings_letter, rapunzels_kerchief, darings_receipt, bauls_potion);
        addLevelCheck(2, 5);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equalsIgnoreCase("quest_accept")) {
            st.setCond(1);
            st.setState(STARTED);
            if (st.ownItemCount(darings_letter) == 0) {
                st.giveItems(darings_letter, 1);
            }
            st.soundEffect(SOUND_ACCEPT);
            htmltext = "daring_q0001_06.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        Player player = st.getPlayer();
        if (player == null) {
            return null;
        }
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == daring) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL: {
                            htmltext = "daring_q0001_01.htm";
                            break;
                        }
                        default: {
                            htmltext = "daring_q0001_02.htm";
                            break;
                        }
                    }
                }
                break;
            case STARTED:
                if (npcId == daring) {
                    if (st.ownItemCount(rapunzels_kerchief) == 1) {
                        st.setCond(3);
                        st.takeItems(rapunzels_kerchief, 1);
                        st.giveItems(darings_receipt, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "daring_q0001_08.htm";
                    } else if (st.ownItemCount(rapunzels_kerchief) == 0) {
                        if (st.ownItemCount(darings_receipt) > 0) {
                            htmltext = "daring_q0001_09.htm";
                        } else if (st.ownItemCount(bauls_potion) > 0) {
                            htmltext = "daring_q0001_10.htm";
                            st.takeItems(bauls_potion, 1);
                            if (player.getQuestState(41) == null) {
                                final Quest q = QuestManager.getQuest(41);
                                q.newQuestState(player, Quest.STARTED);
                                player.getQuestState(41).setMemoState("guide_mission", String.valueOf(1), true);
                                st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.DELIVERY_DUTY_COMPLETE_GO_FING_THE_NEWBIE_GUIDE, 5000, ScreenMessageAlign.TOP_CENTER, true));
                            } else if (player.getQuestState(41).getInt("guide_mission") % 10 != 1) {
                                player.getQuestState(41).setMemoState("guide_mission", String.valueOf(player.getQuestState(41).getInt("guide_mission") + 1), true);
                                st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.DELIVERY_DUTY_COMPLETE_GO_FING_THE_NEWBIE_GUIDE, 5000, ScreenMessageAlign.TOP_CENTER, true));
                            }
                            st.giveItems(necklace_of_knowledge, 1);
                            st.addExpAndSp(5672, 446);
                            st.giveItems(ADENA_ID, 2466);
                            st.removeMemo("letters_of_love1");
                            st.soundEffect(SOUND_FINISH);
                            st.exitQuest(false);
                        } else {
                            htmltext = "daring_q0001_07.htm";
                        }
                    }
                } else if (npcId == rapunzel) {
                    if (st.ownItemCount(rapunzels_kerchief) == 0 && st.ownItemCount(darings_letter) == 1) {
                        st.setCond(2);
                        st.giveItems(rapunzels_kerchief, 1);
                        st.takeItems(darings_letter, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "rapunzel_q0001_01.htm";
                    } else if (st.ownItemCount(bauls_potion) > 0 || st.ownItemCount(darings_receipt) > 0) {
                        htmltext = "rapunzel_q0001_03.htm";
                    } else if (st.ownItemCount(rapunzels_kerchief) > 0) {
                        htmltext = "rapunzel_q0001_02.htm";
                    }
                } else if (npcId == baul) {
                    if (st.ownItemCount(darings_receipt) > 0 || st.ownItemCount(bauls_potion) > 0) {
                        if (st.ownItemCount(darings_receipt) > 0) {
                            st.setCond(4);
                            st.takeItems(darings_receipt, 1);
                            st.giveItems(bauls_potion, 1);
                            st.soundEffect(SOUND_MIDDLE);
                            htmltext = "baul_q0001_01.htm";
                        } else if (st.ownItemCount(bauls_potion) > 0) {
                            htmltext = "baul_q0001_02.htm";
                        }
                    }
                }
                break;
        }
        return htmltext;
    }
}