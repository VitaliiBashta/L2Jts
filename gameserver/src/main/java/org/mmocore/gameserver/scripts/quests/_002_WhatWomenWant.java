package org.mmocore.gameserver.scripts.quests;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
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
 * @version 1.1
 * @date 05/01/2015
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _002_WhatWomenWant extends Quest {
    // npc
    private final static int arujien = 30223;
    private final static int mint = 30146;
    private final static int green = 30150;
    private final static int grain = 30157;
    // questitem
    private final static int arujiens_letter1 = 1092;
    private final static int arujiens_letter2 = 1093;
    private final static int arujiens_letter3 = 1094;
    private final static int poetry_book = 689;
    private final static int greenis_letter = 693;
    // accessary
    private final static int mage_earing = 113;

    public _002_WhatWomenWant() {
        super(false);
        addStartNpc(arujien);
        addTalkId(mint, green, grain);
        addQuestItem(greenis_letter, arujiens_letter3, arujiens_letter1, arujiens_letter2, poetry_book);
        addLevelCheck(2, 5);
        addRaceCheck(PlayerRace.elf, PlayerRace.human);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        Player player = st.getPlayer();
        if (player == null) {
            return null;
        }
        int npcId = npc.getNpcId();
        if (npcId == arujien) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setState(STARTED);
                if (st.ownItemCount(arujiens_letter1) == 0 && st.ownItemCount(arujiens_letter2) == 0 && st.ownItemCount(arujiens_letter3) == 0)
                    st.giveItems(arujiens_letter1, 1);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "arujien_q0002_04.htm";
            } else if (event.equalsIgnoreCase("reply_1") && st.ownItemCount(arujiens_letter3) > 0) {
                st.setCond(4);
                st.takeItems(arujiens_letter3, 1);
                st.giveItems(poetry_book, 1);
                htmltext = "arujien_q0002_08.htm";
            } else if (event.equalsIgnoreCase("reply_2") && st.ownItemCount(arujiens_letter3) > 0) {
                htmltext = "arujien_q0002_09.htm";
                st.takeItems(arujiens_letter3, 1);
                st.giveItems(ADENA_ID, 450);
                if (player.getQuestState(41) == null) {
                    final Quest q = QuestManager.getQuest(41);
                    q.newQuestState(player, Quest.STARTED);
                    player.getQuestState(41).setMemoState("guide_mission", String.valueOf(1), true);
                    st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.DELIVERY_DUTY_COMPLETE_GO_FING_THE_NEWBIE_GUIDE, 5000, ScreenMessageAlign.TOP_CENTER, true));
                } else if (player.getQuestState(41).getInt("guide_mission") % 10 != 1) {
                    player.getQuestState(41).setMemoState("guide_mission", String.valueOf(player.getQuestState(41).getInt("guide_mission") + 1), true);
                    st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.DELIVERY_DUTY_COMPLETE_GO_FING_THE_NEWBIE_GUIDE, 5000, ScreenMessageAlign.TOP_CENTER, true));
                }
                st.addExpAndSp(4254, 335);
                st.giveItems(ADENA_ID, 1850);
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(false);
            }
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
            case CREATED: {
                if (npcId == arujien) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL: {
                            htmltext = "arujien_q0002_01.htm";
                            break;
                        }
                        case RACE: {
                            htmltext = "arujien_q0002_00.htm";
                            break;
                        }
                        default: {
                            htmltext = "arujien_q0002_02.htm";
                            break;
                        }
                    }
                }
                break;
            }
            case STARTED: {
                if (npcId == arujien) {
                    if (st.ownItemCount(arujiens_letter1) > 0) {
                        htmltext = "arujien_q0002_05.htm";
                    } else if (st.ownItemCount(arujiens_letter3) > 0) {
                        htmltext = "arujien_q0002_07.htm";
                    } else if (st.ownItemCount(arujiens_letter2) > 0) {
                        htmltext = "arujien_q0002_06.htm";
                    } else if (st.ownItemCount(poetry_book) > 0) {
                        htmltext = "arujien_q0002_11.htm";
                    } else if (st.ownItemCount(greenis_letter) > 0) {
                        htmltext = "arujien_q0002_10.htm";
                        st.giveItems(mage_earing, 1);
                        st.takeItems(greenis_letter, 1);
                        if (player.getQuestState(41) == null) {
                            final Quest q = QuestManager.getQuest(41);
                            q.newQuestState(player, Quest.STARTED);
                            player.getQuestState(41).setMemoState("guide_mission", String.valueOf(1), true);
                            st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.DELIVERY_DUTY_COMPLETE_GO_FING_THE_NEWBIE_GUIDE, 5000, ScreenMessageAlign.TOP_CENTER, true));
                        } else if (player.getQuestState(41).getInt("guide_mission") % 10 != 1) {
                            player.getQuestState(41).setMemoState("guide_mission", String.valueOf(player.getQuestState(41).getInt("guide_mission") + 1), true);
                            st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.DELIVERY_DUTY_COMPLETE_GO_FING_THE_NEWBIE_GUIDE, 5000, ScreenMessageAlign.TOP_CENTER, true));
                        }
                        st.addExpAndSp(4254, 335);
                        st.giveItems(ADENA_ID, 1850);
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                    }
                } else if (npcId == mint) {
                    if (st.ownItemCount(arujiens_letter1) > 0) {
                        st.setCond(2);
                        st.takeItems(arujiens_letter1, 1);
                        st.giveItems(arujiens_letter2, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "mint_q0002_01.htm";
                    } else if (st.ownItemCount(arujiens_letter2) > 0 || st.ownItemCount(arujiens_letter3) == 0 || st.ownItemCount(poetry_book) == 0 || st.ownItemCount(greenis_letter) == 0) {
                        htmltext = "mint_q0002_02.htm";
                    }
                } else if (npcId == green) {
                    if (st.ownItemCount(arujiens_letter2) > 0) {
                        st.setCond(3);
                        st.takeItems(arujiens_letter2, 1);
                        st.giveItems(arujiens_letter3, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "green_q0002_01.htm";
                    } else if (st.ownItemCount(arujiens_letter3) > 0 || st.ownItemCount(poetry_book) == 0 || st.ownItemCount(greenis_letter) == 0) {
                        htmltext = "green_q0002_02.htm";
                    }
                } else if (npcId == grain) {
                    if (st.ownItemCount(poetry_book) > 0) {
                        st.setCond(5);
                        st.takeItems(poetry_book, 1);
                        st.giveItems(greenis_letter, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "grain_q0002_02.htm";
                    } else if (st.ownItemCount(greenis_letter) > 0) {
                        htmltext = "grain_q0002_03.htm";
                    } else if (st.ownItemCount(arujiens_letter1) == 0 || st.ownItemCount(arujiens_letter2) == 0 || st.ownItemCount(arujiens_letter3) == 0) {
                        htmltext = "grain_q0002_01.htm";
                    }
                }
                break;
            }
        }
        return htmltext;
    }
}
