package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.listener.actor.OnDeathListener;
import org.mmocore.gameserver.manager.ServerVariables;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.world.GameObjectsStorage;

public class _610_MagicalPowerofWater2 extends Quest {
    // NPC
    private static final int ASEFA = 31372;
    private static final int VARKAS_HOLY_ALTAR = 31560;

    // Quest items
    private static final int GREEN_TOTEM = 7238;
    private static final int Reward_First = 4589;
    private static final int Reward_Last = 4594;
    private static final int SoulOfWaterAshutar = 25316;
    final int ICE_HEART_OF_ASHUTAR = 7239;
    private NpcInstance SoulOfWaterAshutarSpawn = null;


    public _610_MagicalPowerofWater2() {
        super(true);

        addStartNpc(ASEFA);

        addTalkId(VARKAS_HOLY_ALTAR);

        addKillId(SoulOfWaterAshutar);

        addQuestItem(ICE_HEART_OF_ASHUTAR);
        addLevelCheck(75, 80);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        NpcInstance isQuest = GameObjectsStorage.getByNpcId(SoulOfWaterAshutar);
        String htmltext = event;
        if (event.equalsIgnoreCase("quest_accept")) {
            htmltext = "shaman_asefa_q0610_0104.htm";
            st.setCond(1);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("610_1")) {
            if (ServerVariables.getLong(_610_MagicalPowerofWater2.class.getSimpleName(), 0) + 3 * 60 * 60 * 1000 > System.currentTimeMillis()) {
                htmltext = "totem_of_barka_q0610_0204.htm";
            } else if (st.ownItemCount(GREEN_TOTEM) >= 1 && isQuest == null) {
                st.takeItems(GREEN_TOTEM, 1);
                SoulOfWaterAshutarSpawn = st.addSpawn(SoulOfWaterAshutar, 104825, -36926, -1136);
                SoulOfWaterAshutarSpawn.addListener(new DeathListener());
                st.soundEffect(SOUND_MIDDLE);
            } else {
                htmltext = "totem_of_barka_q0610_0203.htm";
            }
        } else if (event.equalsIgnoreCase("610_3")) {
            if (st.ownItemCount(ICE_HEART_OF_ASHUTAR) >= 1) {
                st.takeItems(ICE_HEART_OF_ASHUTAR, -1);
                st.giveItems(Rnd.get(Reward_First, Reward_Last), 5, true);
                st.soundEffect(SOUND_FINISH);
                htmltext = "shaman_asefa_q0610_0301.htm";
                st.exitQuest(true);
            } else {
                htmltext = "shaman_asefa_q0610_0302.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        NpcInstance isQuest = GameObjectsStorage.getByNpcId(SoulOfWaterAshutar);
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == ASEFA) {
            if (cond == 0) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = "shaman_asefa_q0610_0103.htm";
                        st.exitQuest(true);
                        break;
                    default:
                        if (st.ownItemCount(GREEN_TOTEM) >= 1) {
                            htmltext = "shaman_asefa_q0610_0101.htm";
                        } else {
                            htmltext = "shaman_asefa_q0610_0102.htm";
                            st.exitQuest(true);
                        }
                        break;
                }
            } else if (cond == 1) {
                htmltext = "shaman_asefa_q0610_0105.htm";
            } else if (cond == 2) {
                htmltext = "shaman_asefa_q0610_0202.htm";
            } else if (cond == 3 && st.ownItemCount(ICE_HEART_OF_ASHUTAR) >= 1) {
                htmltext = "shaman_asefa_q0610_0201.htm";
            }
        } else if (npcId == VARKAS_HOLY_ALTAR) {
            if (!npc.isBusy()) {
                if (ServerVariables.getLong(_610_MagicalPowerofWater2.class.getSimpleName(), 0) + 3 * 60 * 60 * 1000 > System.currentTimeMillis()) {
                    htmltext = "totem_of_barka_q0610_0204.htm";
                } else if (cond == 1) {
                    htmltext = "totem_of_barka_q0610_0101.htm";
                } else if (cond == 2 && isQuest == null) {
                    SoulOfWaterAshutarSpawn = st.addSpawn(SoulOfWaterAshutar, 104825, -36926, -1136);
                    SoulOfWaterAshutarSpawn.addListener(new DeathListener());
                    htmltext = "totem_of_barka_q0610_0204.htm";
                }
            } else {
                htmltext = "totem_of_barka_q0610_0202.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        if (st.ownItemCount(ICE_HEART_OF_ASHUTAR) == 0 && npc.getNpcId() == SoulOfWaterAshutar) {
            st.giveItems(ICE_HEART_OF_ASHUTAR, 1);
            st.setCond(3);
            if (SoulOfWaterAshutarSpawn != null) {
                SoulOfWaterAshutarSpawn.deleteMe();
            }
            SoulOfWaterAshutarSpawn = null;
        }
        return null;
    }

    private static class DeathListener implements OnDeathListener {
        @Override
        public void onDeath(Creature actor, Creature killer) {
            ServerVariables.set(_610_MagicalPowerofWater2.class.getSimpleName(), String.valueOf(System.currentTimeMillis()));
        }
    }
}