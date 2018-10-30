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
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.0
 * @date 06/01/2015
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _276_TotemOfTheHestui extends Quest {
    // npc
    private static final int seer_tanapi = 30571;
    // mobs
    private static final int kasha_bear = 20479;
    private static final int kasha_bear_totem = 27044;
    // questitem
    private static final int kasha_parasite = 1480;
    private static final int kasha_crystal = 1481;

    public _276_TotemOfTheHestui() {
        super(false);
        addStartNpc(seer_tanapi);
        addKillId(kasha_bear, kasha_bear_totem);
        addQuestItem(kasha_parasite, kasha_crystal);
        addLevelCheck(15, 21);
        addRaceCheck(PlayerRace.orc);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == seer_tanapi) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "seer_tanapi_q0276_03.htm";
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
            case CREATED:
                if (npcId == seer_tanapi) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "seer_tanapi_q0276_01.htm";
                            st.exitQuest(true);
                            break;
                        case RACE:
                            htmltext = "seer_tanapi_q0276_00.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "seer_tanapi_q0276_02.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == seer_tanapi) {
                    if (st.ownItemCount(kasha_crystal) < 1)
                        htmltext = "seer_tanapi_q0276_04.htm";
                    else if (st.ownItemCount(kasha_crystal) >= 1) {
                        htmltext = "seer_tanapi_q0276_05.htm";
                        st.takeItems(kasha_crystal, -1);
                        st.takeItems(kasha_parasite, -1);
                        int hestuis_totem = 1500;
                        st.giveItems(hestuis_totem, 1);
                        // etcitem
                        int leather_pants = 29;
                        st.giveItems(leather_pants, 1);
                        if (player.getQuestState(41) == null) {
                            final Quest q = QuestManager.getQuest(41);
                            q.newQuestState(player, Quest.STARTED);
                            player.getQuestState(41).setMemoState("guide_mission", String.valueOf(100000), true);
                            st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.LAST_DUTY_COMPLETE_GO_FIND_THE_NEWBIE_GUIDE, 5000, ScreenMessageAlign.TOP_CENTER, true));
                        } else if (player.getQuestState(41).getInt("guide_mission") % 100000000 / 10000000 != 1) {
                            player.getQuestState(41).setMemoState("guide_mission", String.valueOf(player.getQuestState(41).getInt("guide_mission") + 10000000), true);
                            st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.LAST_DUTY_COMPLETE_GO_FIND_THE_NEWBIE_GUIDE, 5000, ScreenMessageAlign.TOP_CENTER, true));
                        }
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(true);
                    }
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == kasha_bear) {
            if (st.ownItemCount(kasha_crystal) < 1) {
                int i1 = Rnd.get(100);
                if (st.ownItemCount(kasha_parasite) >= 79) {
                    st.addSpawn(kasha_bear_totem);
                    st.takeItems(kasha_parasite, -1);
                } else if (st.ownItemCount(kasha_parasite) >= 69) {
                    if (i1 <= 20) {
                        st.addSpawn(kasha_bear_totem);
                        st.takeItems(kasha_parasite, -1);
                    }
                } else if (st.ownItemCount(kasha_parasite) >= 59) {
                    if (i1 <= 15) {
                        st.addSpawn(kasha_bear_totem);
                        st.takeItems(kasha_parasite, -1);
                    }
                } else if (st.ownItemCount(kasha_parasite) >= 49) {
                    if (i1 <= 10) {
                        st.addSpawn(kasha_bear_totem);
                        st.takeItems(kasha_parasite, -1);
                    }
                } else if (st.ownItemCount(kasha_parasite) >= 39) {
                    if (i1 <= 2) {
                        st.addSpawn(kasha_bear_totem);
                        st.takeItems(kasha_parasite, -1);
                    }
                }
                st.giveItems(kasha_parasite, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == kasha_bear_totem) {
            if (st.ownItemCount(kasha_crystal) < 1) {
                st.giveItems(kasha_crystal, 1);
                st.setCond(2);
                st.soundEffect(SOUND_MIDDLE);
            }
        }
        return null;
    }
}