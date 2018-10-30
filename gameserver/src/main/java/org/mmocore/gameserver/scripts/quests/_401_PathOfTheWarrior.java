package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.base.ClassId;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.serverpackets.SocialAction;
import org.mmocore.gameserver.object.Inventory;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.1
 * @date 04/10/2015
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _401_PathOfTheWarrior extends Quest {
    // npc
    private final static int ein = 30010;
    private final static int trader_simplon = 30253;
    // mobs
    private final static int tracker_skeleton = 20035;
    private final static int poison_spider = 20038;
    private final static int tracker_skeleton_leader = 20042;
    private final static int bind_poison_spider = 20043;
    // questitem
    private final static int eins_letter = 1138;
    private final static int warrior_guild_mark = 1139;
    private final static int rusted_bronze_sword1 = 1140;
    private final static int rusted_bronze_sword2 = 1141;
    private final static int rusted_bronze_sword3 = 1142;
    private final static int simplons_letter = 1143;
    private final static int poison_spider_leg2 = 1144;
    private final static int medallion_of_warrior = 1145;

    public _401_PathOfTheWarrior() {
        super(false);
        addStartNpc(ein);
        addTalkId(trader_simplon);
        addKillId(tracker_skeleton, poison_spider, tracker_skeleton_leader, bind_poison_spider);
        addQuestItem(eins_letter, warrior_guild_mark, rusted_bronze_sword1, rusted_bronze_sword2, simplons_letter, poison_spider_leg2, rusted_bronze_sword3);
        addLevelCheck(18);
        addClassIdCheck(ClassId.fighter);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        int talker_occupation = st.getPlayer().getPlayerClassComponent().getClassId().getId();
        int fighter = 0x00;
        int warrior = 0x01;
        if (npcId == ein) {
            if (event.equalsIgnoreCase("quest_accept")) {
                if (st.ownItemCount(eins_letter) == 0) {
                    st.setCond(1);
                    st.giveItems(eins_letter, 1);
                    st.setState(STARTED);
                    st.soundEffect(SOUND_ACCEPT);
                    htmltext = "ein_q0401_06.htm";
                }
            } else if (event.equalsIgnoreCase("401_reply_1")) {
                if (talker_occupation == fighter) {
                    if (st.ownItemCount(medallion_of_warrior) > 0)
                        htmltext = "ein_q0401_04.htm";
                    else
                        htmltext = "ein_q0401_05.htm";
                } else if (talker_occupation == warrior)
                    htmltext = "ein_q0401_02a.htm";
                else
                    htmltext = "ein_q0401_03.htm";
            } else if (event.equalsIgnoreCase("401_reply_2"))
                htmltext = "ein_q0401_10.htm";
            else if (event.equalsIgnoreCase("401_reply_3") && st.ownItemCount(simplons_letter) > 0 && st.ownItemCount(rusted_bronze_sword2) > 0) {
                st.setCond(5);
                st.takeItems(simplons_letter, 1);
                st.takeItems(rusted_bronze_sword2, 1);
                st.giveItems(rusted_bronze_sword3, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "ein_q0401_11.htm";
            }
        } else if (npcId == trader_simplon) {
            if (event.equalsIgnoreCase("401_reply_1")) {
                st.setCond(2);
                st.takeItems(eins_letter, 1);
                st.giveItems(warrior_guild_mark, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "trader_simplon_q0401_02.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        Player player = st.getPlayer();
        int npcId = npc.getNpcId();
        int id = st.getState();
        int GetOneTimeQuestFlag = st.getPlayer().getPlayerVariables().getInt(PlayerVariables.profession_145);
        switch (id) {
            case CREATED:
                if (npcId == ein) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "ein_q0401_02.htm";
                            st.exitQuest(true);
                            break;
                        case CLASS_ID:
                            htmltext = "ein_q0401_03.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "ein_q0401_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == ein) {
                    if (st.ownItemCount(eins_letter) > 0)
                        htmltext = "ein_q0401_07.htm";
                    else if (st.ownItemCount(warrior_guild_mark) == 1)
                        htmltext = "ein_q0401_08.htm";
                    else if (st.ownItemCount(simplons_letter) > 0 && st.ownItemCount(rusted_bronze_sword2) > 0 && st.ownItemCount(warrior_guild_mark) == 0 && st.ownItemCount(eins_letter) == 0)
                        htmltext = "ein_q0401_09.htm";
                    else if (st.ownItemCount(rusted_bronze_sword3) > 0 && st.ownItemCount(warrior_guild_mark) == 0 && st.ownItemCount(eins_letter) == 0) {
                        if (st.ownItemCount(poison_spider_leg2) < 20)
                            htmltext = "ein_q0401_12.htm";
                        else if (st.ownItemCount(poison_spider_leg2) > 19) {
                            st.takeItems(poison_spider_leg2, -1);
                            st.takeItems(rusted_bronze_sword3, 1);
                            st.giveItems(medallion_of_warrior, 1);
                            if (GetOneTimeQuestFlag == 0) {
                                st.getPlayer().getPlayerVariables().set(PlayerVariables.profession_145, String.valueOf(1), -1);
                                if (st.getPlayer().getLevel() >= 20)
                                    st.addExpAndSp(320534, 21012);
                                else if (st.getPlayer().getLevel() == 19)
                                    st.addExpAndSp(456128, 27710);
                                else
                                    st.addExpAndSp(160267, 34408);
                                st.giveItems(ADENA_ID, 163800);
                            }
                            htmltext = "ein_q0401_13.htm";
                            player.sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                            st.soundEffect(SOUND_FINISH);
                            st.exitQuest(false);
                        }
                    }
                } else if (npcId == trader_simplon) {
                    if (st.ownItemCount(eins_letter) > 0)
                        htmltext = "trader_simplon_q0401_01.htm";
                    else if (st.ownItemCount(warrior_guild_mark) > 0) {
                        if (st.ownItemCount(rusted_bronze_sword1) < 1)
                            htmltext = "trader_simplon_q0401_03.htm";
                        else if (st.ownItemCount(rusted_bronze_sword1) < 10)
                            htmltext = "trader_simplon_q0401_04.htm";
                        else if (st.ownItemCount(rusted_bronze_sword1) >= 10) {
                            st.setCond(4);
                            st.takeItems(warrior_guild_mark, 1);
                            st.takeItems(rusted_bronze_sword1, -1);
                            st.giveItems(rusted_bronze_sword2, 1);
                            st.giveItems(simplons_letter, 1);
                            htmltext = "trader_simplon_q0401_05.htm";
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    } else if (st.ownItemCount(simplons_letter) > 0)
                        htmltext = "trader_simplon_q0401_06.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == tracker_skeleton || npcId == tracker_skeleton_leader) {
            if (st.ownItemCount(rusted_bronze_sword1) < 10 && st.ownItemCount(warrior_guild_mark) > 0) {
                if (Rnd.get(10) < 4) {
                    st.giveItems(rusted_bronze_sword1, 1);
                    if (st.ownItemCount(rusted_bronze_sword1) >= 10) {
                        st.setCond(3);
                        st.soundEffect(SOUND_MIDDLE);
                    } else
                        st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == poison_spider || npcId == bind_poison_spider) {
            if (st.ownItemCount(poison_spider_leg2) < 20 && st.ownItemCount(rusted_bronze_sword3) == 1 && st.getItemEquipped(Inventory.PAPERDOLL_RHAND) == rusted_bronze_sword3) {
                st.giveItems(poison_spider_leg2, 1);
                if (st.ownItemCount(poison_spider_leg2) >= 20) {
                    st.setCond(6);
                    st.soundEffect(SOUND_MIDDLE);
                } else
                    st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }
}