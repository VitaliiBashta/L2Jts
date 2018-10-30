package org.mmocore.gameserver.scripts.quests;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.gameserver.manager.QuestManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import org.mmocore.gameserver.network.lineage.serverpackets.SocialAction;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.1
 * @date 06/01/2015
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _107_MercilessPunishment extends Quest {
    // npc
    private static final int urutu_chief_hatos = 30568;
    private static final int centurion_parugon = 30580;
    // mobs
    private static final int baranka_messenger = 27041;
    // questitem
    private static final int hatoss_order1 = 1553;
    private static final int hatoss_order2 = 1554;
    private static final int hatoss_order3 = 1555;
    private static final int letter_to_darkelf1 = 1556;
    private static final int letter_to_human = 1557;
    private static final int letter_to_elf1 = 1558;
    // etcitem
    private static final int soulshot_none_for_rookie = 5789;
    private static final int lesser_healing_potion = 1060;
    private static final int echo_crystal_solitude = 4414;
    private static final int echo_crystal_feast = 4415;
    private static final int echo_crystal_celebration = 4416;
    private static final int echo_crystal_love = 4413;
    private static final int echo_crystal_battle = 4412;
    private static final int butcher = 1510;

    public _107_MercilessPunishment() {
        super(false);
        addStartNpc(urutu_chief_hatos);
        addTalkId(centurion_parugon);
        addKillId(baranka_messenger);
        addQuestItem(hatoss_order1, hatoss_order2, hatoss_order3, letter_to_darkelf1, letter_to_human, letter_to_elf1);
        addLevelCheck(10, 16);
        addRaceCheck(PlayerRace.orc);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == urutu_chief_hatos) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                st.giveItems(hatoss_order1, 1);
                htmltext = "urutu_chief_hatos_q0107_03.htm";
            } else if (event.equalsIgnoreCase("reply_1") && (st.ownItemCount(hatoss_order1) > 0 || st.ownItemCount(hatoss_order2) > 0 || st.ownItemCount(hatoss_order3) > 0) && st.ownItemCount(letter_to_elf1) + st.ownItemCount(letter_to_human) + st.ownItemCount(letter_to_darkelf1) == 1) {
                htmltext = "urutu_chief_hatos_q0107_06.htm";
                st.takeItems(hatoss_order2, -1);
                st.takeItems(letter_to_darkelf1, -1);
                st.takeItems(letter_to_human, -1);
                st.takeItems(letter_to_elf1, -1);
                st.takeItems(hatoss_order1, -1);
                st.takeItems(hatoss_order3, -1);
                st.giveItems(ADENA_ID, 100);
                st.soundEffect(SOUND_GIVEUP);
                st.exitQuest(true); // FIXME: Нужно ли?
            } else if (event.equalsIgnoreCase("reply_2") && (st.ownItemCount(hatoss_order1) > 0 || st.ownItemCount(hatoss_order2) > 0 || st.ownItemCount(hatoss_order3) > 0) && st.ownItemCount(letter_to_elf1) + st.ownItemCount(letter_to_human) + st.ownItemCount(letter_to_darkelf1) == 1) {
                htmltext = "urutu_chief_hatos_q0107_07.htm";
                st.setCond(4);
                st.takeItems(hatoss_order1, -1);
                if (st.ownItemCount(hatoss_order2) == 0)
                    st.giveItems(hatoss_order2, 1);
            } else if (event.equalsIgnoreCase("reply_3") && (st.ownItemCount(hatoss_order1) > 0 || st.ownItemCount(hatoss_order2) > 0 || st.ownItemCount(hatoss_order3) > 0) && st.ownItemCount(letter_to_elf1) + st.ownItemCount(letter_to_human) + st.ownItemCount(letter_to_darkelf1) == 2) {
                htmltext = "urutu_chief_hatos_q0107_06.htm";
                st.takeItems(hatoss_order2, -1);
                st.takeItems(letter_to_darkelf1, -1);
                st.takeItems(letter_to_human, -1);
                st.takeItems(letter_to_elf1, -1);
                st.takeItems(hatoss_order1, -1);
                st.takeItems(hatoss_order3, -1);
                st.giveItems(ADENA_ID, 200);
                st.soundEffect(SOUND_GIVEUP);
                st.exitQuest(true); // FIXME: Нужно ли?
            } else if (event.equalsIgnoreCase("reply_4") && (st.ownItemCount(hatoss_order1) > 0 || st.ownItemCount(hatoss_order2) > 0 || st.ownItemCount(hatoss_order3) > 0) && st.ownItemCount(letter_to_elf1) + st.ownItemCount(letter_to_human) + st.ownItemCount(letter_to_darkelf1) == 2) {
                htmltext = "urutu_chief_hatos_q0107_09.htm";
                st.setCond(6);
                st.takeItems(hatoss_order2, -1);
                if (st.ownItemCount(hatoss_order3) == 0)
                    st.giveItems(hatoss_order3, 1);
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
        int talker_level = st.getPlayer().getLevel();
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == urutu_chief_hatos) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "urutu_chief_hatos_q0107_01.htm";
                            st.exitQuest(true);
                            break;
                        case RACE:
                            htmltext = "urutu_chief_hatos_q0107_00.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "urutu_chief_hatos_q0107_02.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == urutu_chief_hatos) {
                    if (((st.ownItemCount(hatoss_order1) > 0 || st.ownItemCount(hatoss_order2) > 0 || st.ownItemCount(hatoss_order3) > 0) && st.ownItemCount(letter_to_elf1) + st.ownItemCount(letter_to_human) + st.ownItemCount(letter_to_darkelf1) == 0))
                        htmltext = "urutu_chief_hatos_q0107_04.htm";
                    else if (((st.ownItemCount(hatoss_order1) > 0 || st.ownItemCount(hatoss_order2) > 0 || st.ownItemCount(hatoss_order3) > 0) && st.ownItemCount(letter_to_elf1) + st.ownItemCount(letter_to_human) + st.ownItemCount(letter_to_darkelf1) == 1))
                        htmltext = "urutu_chief_hatos_q0107_05.htm";
                    else if (((st.ownItemCount(hatoss_order1) > 0 || st.ownItemCount(hatoss_order2) > 0 || st.ownItemCount(hatoss_order3) > 0) && st.ownItemCount(letter_to_elf1) + st.ownItemCount(letter_to_human) + st.ownItemCount(letter_to_darkelf1) == 2))
                        htmltext = "urutu_chief_hatos_q0107_08.htm";
                    else if (((st.ownItemCount(hatoss_order1) > 0 || st.ownItemCount(hatoss_order2) > 0 || st.ownItemCount(hatoss_order3) > 0) && st.ownItemCount(letter_to_elf1) + st.ownItemCount(letter_to_human) + st.ownItemCount(letter_to_darkelf1) == 3)) {
                        if (talker_level < 25) {
                            st.giveItems(soulshot_none_for_rookie, 7000);
                            st.playTutorialVoice("tutorial_voice_026");
                        }
                        if (player.getQuestState(41) == null) {
                            final Quest q = QuestManager.getQuest(41);
                            q.newQuestState(player, Quest.STARTED);
                            player.getQuestState(41).setMemoState("guide_mission", String.valueOf(100000), true);
                            st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.ACQUISITION_OF_RACE_SPECIFIC_WEAPON_COMPLETE_GO_FIND_THE_NEWBIE_GUIDE, 5000, ScreenMessageAlign.TOP_CENTER, true));
                        } else if (player.getQuestState(41).getInt("guide_mission") % 1000000 / 100000 != 1) {
                            player.getQuestState(41).setMemoState("guide_mission", String.valueOf(player.getQuestState(41).getInt("guide_mission") + 100000), true);
                            st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.ACQUISITION_OF_RACE_SPECIFIC_WEAPON_COMPLETE_GO_FIND_THE_NEWBIE_GUIDE, 5000, ScreenMessageAlign.TOP_CENTER, true));
                        }
                        st.addExpAndSp(34565, 2962);
                        st.giveItems(ADENA_ID, 14666);
                        htmltext = "urutu_chief_hatos_q0107_10.htm";
                        st.giveItems(lesser_healing_potion, 100);
                        st.giveItems(echo_crystal_solitude, 10);
                        st.giveItems(echo_crystal_feast, 10);
                        st.giveItems(echo_crystal_celebration, 10);
                        st.giveItems(echo_crystal_love, 10);
                        st.giveItems(echo_crystal_battle, 10);
                        st.takeItems(letter_to_darkelf1, -1);
                        st.takeItems(letter_to_human, -1);
                        st.takeItems(letter_to_elf1, -1);
                        st.takeItems(hatoss_order3, -1);
                        st.giveItems(butcher, 1);
                        st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                        st.exitQuest(false);
                        st.soundEffect(SOUND_FINISH);
                    }
                } else if (npcId == centurion_parugon) {
                    if (st.ownItemCount(hatoss_order1) > 0 || st.ownItemCount(hatoss_order2) > 0 || st.ownItemCount(hatoss_order3) > 0) {
                        htmltext = "centurion_parugon_q0107_01.htm";
                        st.setCond(2);
                        st.soundEffect(SOUND_MIDDLE);
                    }
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == baranka_messenger) {
            if (st.ownItemCount(hatoss_order1) > 0 && st.ownItemCount(letter_to_human) == 0) {
                st.giveItems(letter_to_human, 1);
                st.setCond(3);
                st.soundEffect(SOUND_ITEMGET);
            }
            if (st.ownItemCount(hatoss_order2) > 0 && st.ownItemCount(letter_to_darkelf1) == 0) {
                st.giveItems(letter_to_darkelf1, 1);
                st.setCond(5);
                st.soundEffect(SOUND_ITEMGET);
            }
            if (st.ownItemCount(hatoss_order3) > 0 && st.ownItemCount(letter_to_elf1) == 0) {
                st.giveItems(letter_to_elf1, 1);
                st.setCond(7);
                st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }
}