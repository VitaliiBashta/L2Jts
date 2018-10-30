package org.mmocore.gameserver.scripts.quests;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.commons.utils.Rnd;
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
 * @version 1.0
 * @date 04/01/2015
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _105_SkirmishWithOrcs extends Quest {
    // npc
    private static final int sentinel_kendnell = 30218;
    // mobs
    private static final int kaboo_chief_uoph = 27059;
    private static final int kaboo_chief_kracha = 27060;
    private static final int kaboo_chief_batoh = 27061;
    private static final int kaboo_chief_tanukia = 27062;
    private static final int kaboo_chief_turel = 27064;
    private static final int kaboo_chief_roko = 27065;
    private static final int kaboo_chief_kamut = 27067;
    private static final int kaboo_chief_murtika = 27068;
    // questitem
    private static final int kendnells_order1 = 1836;
    private static final int kendnells_order2 = 1837;
    private static final int kendnells_order3 = 1838;
    private static final int kendnells_order4 = 1839;
    private static final int kendnells_order5 = 1840;
    private static final int kendnells_order6 = 1841;
    private static final int kendnells_order7 = 1842;
    private static final int kendnells_order8 = 1843;
    private static final int kaboo_chief_torc1 = 1844;
    private static final int kaboo_chief_torc2 = 1845;
    // etcitem
    private static final int soulshot_none_for_rookie = 5789;
    private static final int spiritshot_none_for_rookie = 5790;
    private static final int lesser_healing_potion = 1060;
    private static final int echo_crystal_solitude = 4414;
    private static final int echo_crystal_feast = 4415;
    private static final int echo_crystal_celebration = 4416;
    private static final int echo_crystal_love = 4413;
    private static final int echo_crystal_battle = 4412;
    private static final int red_sunset_sword = 981;
    private static final int red_sunset_staff = 754;

    public _105_SkirmishWithOrcs() {
        super(false);
        addStartNpc(sentinel_kendnell);
        addKillId(kaboo_chief_uoph, kaboo_chief_kracha, kaboo_chief_batoh, kaboo_chief_tanukia, kaboo_chief_turel, kaboo_chief_roko, kaboo_chief_kamut, kaboo_chief_murtika);
        addQuestItem(kendnells_order1, kendnells_order2, kendnells_order3, kendnells_order4, kendnells_order5, kendnells_order6, kendnells_order7, kendnells_order8, kaboo_chief_torc1, kaboo_chief_torc2);
        addLevelCheck(10, 15);
        addRaceCheck(PlayerRace.elf);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == sentinel_kendnell) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "sentinel_kendnell_q0105_03.htm";
                if (st.ownItemCount(kendnells_order1) + st.ownItemCount(kendnells_order2) + st.ownItemCount(kendnells_order3) + st.ownItemCount(kendnells_order4) == 0) {
                    int i0 = Rnd.get(100);
                    if (i0 < 25)
                        st.giveItems(kendnells_order1, 1);
                    else if (i0 < 50)
                        st.giveItems(kendnells_order2, 1);
                    else if (i0 < 75)
                        st.giveItems(kendnells_order3, 1);
                    else
                        st.giveItems(kendnells_order4, 1);
                }
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
        boolean isMage = st.getPlayer().getPlayerClassComponent().getClassId().isMage();
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == sentinel_kendnell) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "sentinel_kendnell_q0105_10.htm";
                            st.exitQuest(true);
                            break;
                        case RACE:
                            htmltext = "sentinel_kendnell_q0105_00.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "sentinel_kendnell_q0105_02.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == sentinel_kendnell) {
                    if (st.ownItemCount(kaboo_chief_torc1) > 0) {
                        htmltext = "sentinel_kendnell_q0105_06.htm";
                        st.setCond(3);
                        if (st.ownItemCount(kendnells_order1) > 0)
                            st.takeItems(kendnells_order1, 1);
                        if (st.ownItemCount(kendnells_order2) > 0)
                            st.takeItems(kendnells_order2, 1);
                        if (st.ownItemCount(kendnells_order3) > 0)
                            st.takeItems(kendnells_order3, 1);
                        if (st.ownItemCount(kendnells_order4) > 0)
                            st.takeItems(kendnells_order4, 1);
                        st.takeItems(kaboo_chief_torc1, 1);
                        int i0 = Rnd.get(100);
                        if (i0 < 25)
                            st.giveItems(kendnells_order5, 1);
                        else if (i0 < 50)
                            st.giveItems(kendnells_order6, 1);
                        else if (i0 < 75)
                            st.giveItems(kendnells_order7, 1);
                        else
                            st.giveItems(kendnells_order8, 1);
                    } else if (st.ownItemCount(kendnells_order1) > 0 || st.ownItemCount(kendnells_order2) > 0 || st.ownItemCount(kendnells_order3) > 0 || st.ownItemCount(kendnells_order4) > 0)
                        htmltext = "sentinel_kendnell_q0105_05.htm";
                    else if (st.ownItemCount(kaboo_chief_torc2) > 0) {
                        htmltext = "sentinel_kendnell_q0105_08.htm";
                        if (st.ownItemCount(kendnells_order5) > 0)
                            st.takeItems(kendnells_order5, 1);
                        if (st.ownItemCount(kendnells_order6) > 0)
                            st.takeItems(kendnells_order6, 1);
                        if (st.ownItemCount(kendnells_order7) > 0)
                            st.takeItems(kendnells_order7, 1);
                        if (st.ownItemCount(kendnells_order8) > 0)
                            st.takeItems(kendnells_order8, 1);
                        st.takeItems(kaboo_chief_torc2, 1);
                        st.giveItems(lesser_healing_potion, 100);
                        st.giveItems(echo_crystal_solitude, 10);
                        st.giveItems(echo_crystal_feast, 10);
                        st.giveItems(echo_crystal_celebration, 10);
                        st.giveItems(echo_crystal_love, 10);
                        st.giveItems(echo_crystal_battle, 10);
                        if (!isMage)
                            st.giveItems(red_sunset_sword, 1);
                        else if (isMage)
                            st.giveItems(red_sunset_staff, 1);
                        if (talker_level < 25 && !isMage) {
                            st.giveItems(soulshot_none_for_rookie, 7000);
                            st.playTutorialVoice("tutorial_voice_026");
                        }
                        if (talker_level < 25 && isMage) {
                            st.giveItems(spiritshot_none_for_rookie, 3000);
                            st.playTutorialVoice("tutorial_voice_027");
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
                        st.addExpAndSp(41478, 3555);
                        st.giveItems(ADENA_ID, 17599);
                        st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                        st.exitQuest(false);
                        st.soundEffect(SOUND_FINISH);
                    }
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == kaboo_chief_uoph) {
            if (st.ownItemCount(kendnells_order1) > 0 && st.ownItemCount(kaboo_chief_torc1) == 0) {
                st.setCond(2);
                st.giveItems(kaboo_chief_torc1, 1);
                st.soundEffect(SOUND_MIDDLE);
            }
        } else if (npcId == kaboo_chief_kracha) {
            if (st.ownItemCount(kendnells_order2) > 0 && st.ownItemCount(kaboo_chief_torc1) == 0) {
                st.setCond(2);
                st.giveItems(kaboo_chief_torc1, 1);
                st.soundEffect(SOUND_MIDDLE);
            }
        } else if (npcId == kaboo_chief_batoh) {
            if (st.ownItemCount(kendnells_order3) > 0 && st.ownItemCount(kaboo_chief_torc1) == 0) {
                st.setCond(2);
                st.giveItems(kaboo_chief_torc1, 1);
                st.soundEffect(SOUND_MIDDLE);
            }
        } else if (npcId == kaboo_chief_tanukia) {
            if (st.ownItemCount(kendnells_order4) > 0 && st.ownItemCount(kaboo_chief_torc1) == 0) {
                st.setCond(2);
                st.giveItems(kaboo_chief_torc1, 1);
                st.soundEffect(SOUND_MIDDLE);
            }
        } else if (npcId == kaboo_chief_turel) {
            if (st.ownItemCount(kendnells_order5) > 0 && st.ownItemCount(kaboo_chief_torc2) == 0) {
                st.setCond(4);
                st.giveItems(kaboo_chief_torc2, 1);
                st.soundEffect(SOUND_MIDDLE);
            }
        } else if (npcId == kaboo_chief_roko) {
            if (st.ownItemCount(kendnells_order6) > 0 && st.ownItemCount(kaboo_chief_torc2) == 0) {
                st.setCond(4);
                st.giveItems(kaboo_chief_torc2, 1);
                st.soundEffect(SOUND_MIDDLE);
            }
        } else if (npcId == kaboo_chief_kamut) {
            if (st.ownItemCount(kendnells_order7) > 0 && st.ownItemCount(kaboo_chief_torc2) == 0) {
                st.setCond(4);
                st.giveItems(kaboo_chief_torc2, 1);
                st.soundEffect(SOUND_MIDDLE);
            }
        } else if (npcId == kaboo_chief_murtika) {
            if (st.ownItemCount(kendnells_order8) > 0 && st.ownItemCount(kaboo_chief_torc2) == 0) {
                st.setCond(4);
                st.giveItems(kaboo_chief_torc2, 1);
                st.soundEffect(SOUND_MIDDLE);
            }
        }
        return null;
    }
}
