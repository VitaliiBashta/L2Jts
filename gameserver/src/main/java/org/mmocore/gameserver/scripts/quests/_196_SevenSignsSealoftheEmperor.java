package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ExStartScenePlayer;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.ReflectionUtils;
import org.mmocore.gameserver.world.GameObjectsStorage;

public class _196_SevenSignsSealoftheEmperor extends Quest {
    //Doors
    private static final int door11 = 17240111;
    private static final int izId = 112;
    // NPCs
    private static int IasonHeine = 30969;
    private static int MerchantofMammon = 32584;
    private static int PromiseofMammon = 32585;
    private static int Shunaiman = 32586;
    private static int Leon = 32587;
    private static int DisciplesGatekeeper = 32657;
    private static int CourtMagician = 32598;
    //private static int EmperorsSealDevice = 27384;
    private static int Wood = 32593;
    // ITEMS
    private static int ElmoredenHolyWater = 13808;
    private static int CourtMagiciansMagicStaff = 13809;
    private static int SealOfBinding = 13846;
    private static int SacredSwordofEinhasad = 15310;
    private NpcInstance MerchantofMammonSpawn;

    public _196_SevenSignsSealoftheEmperor() {
        super(false);

        addStartNpc(IasonHeine);
        addTalkId(IasonHeine, MerchantofMammon, PromiseofMammon, Shunaiman, Leon, DisciplesGatekeeper, CourtMagician, Wood);
        addQuestItem(ElmoredenHolyWater, CourtMagiciansMagicStaff, SealOfBinding, SacredSwordofEinhasad);

        addLevelCheck(79);
        addQuestCompletedCheck(195);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        Player player = st.getPlayer();
        String htmltext = event;
        Reflection ref = player.getReflection();

        if (event.equalsIgnoreCase("iasonheine_q196_1d.htm")) {
            st.setCond(1);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("iasonheine_q196_2.htm")) {
            if (GameObjectsStorage.getAllByNpcId(MerchantofMammon, false).isEmpty()) {
                MerchantofMammonSpawn = st.addSpawn(MerchantofMammon, 109763, 219944, -3512, 16384, 0, 120 * 1000);
                Functions.npcSay(MerchantofMammonSpawn, NpcString.WHO_DARES_SUMMON_THE_MERCHANT_OF_MAMMON);
            }
        } else if (event.equalsIgnoreCase("merchantofmammon_q196_2.htm")) {
            if (MerchantofMammonSpawn != null) {
                MerchantofMammonSpawn.deleteMe();
                MerchantofMammonSpawn = null;
            }
            st.setCond(2);
            st.soundEffect(SOUND_MIDDLE);
        } else if (event.equalsIgnoreCase("teleport_instance")) {
            if ((st.getCond() == 3 || st.getCond() == 4)) {
                enterInstance(player);
            } else {
                player.sendMessage("You can only access the Necropolis of Dawn while carrying Seal of the Emperor quest.");
            }
            return null;
        } else if (event.equalsIgnoreCase("collapse_instance")) {
            ref.collapse();
            htmltext = "leon_q196_1.htm";
        } else if (event.equalsIgnoreCase("shunaiman_q196_2.htm")) {
            st.setCond(4);
            st.soundEffect(SOUND_MIDDLE);
            player.sendPacket(SystemMsg.BY_USING_THE_SKILL_OF_EINHASAD_S_HOLY_SWORD_DEFEAT_THE_EVIL_LILIMS);
            player.sendPacket(SystemMsg.BY_USING_THE_HOLY_WATER_OF_EINHASAD_OPEN_THE_DOOR_POSSESSED_BY_THE_CURSE_OF_FLAMES);
            st.giveItems(SacredSwordofEinhasad, 1);
            st.giveItems(ElmoredenHolyWater, 1);
        } else if (event.equalsIgnoreCase("courtmagician_q196_2.htm")) {
            st.soundEffect(SOUND_ITEMGET);
            st.giveItems(CourtMagiciansMagicStaff, 1);
            player.sendPacket(SystemMsg.BY_USING_THE_COURT_MAGICIAN_S_MAGIC_STAFF_OPEN_THE_DOOR_ON_WHICH_THE_MAGICIAN_S_BARRIER_IS);
        } else if (event.equalsIgnoreCase("free_anakim")) {
            player.showQuestMovie(ExStartScenePlayer.SCENE_SSQ_SEALING_EMPEROR_1ST);
            player.sendPacket(SystemMsg.IN_ORDER_TO_HELP_ANAKIM_ACTIVATE_THE_SEALING_DEVICE_OF_THE_EMPEROR_WHO_IS_POSSESED_BY_THE_EVIL);
            ref.openDoor(door11);
            ThreadPoolManager.getInstance().schedule(new SpawnLilithRoom(ref), 17000);
            return null;
        } else if (event.equalsIgnoreCase("shunaiman_q196_4.htm")) {
            st.setCond(5);
            st.soundEffect(SOUND_MIDDLE);
            st.takeItems(SealOfBinding, -1);
            st.takeItems(ElmoredenHolyWater, -1);
            st.takeItems(CourtMagiciansMagicStaff, -1);
            st.takeItems(SacredSwordofEinhasad, -1);
        } else if (event.equalsIgnoreCase("leon_q196_2.htm")) {
            player.getReflection().collapse();
        } else if (event.equalsIgnoreCase("iasonheine_q196_6.htm")) {
            st.setCond(6);
            st.soundEffect(SOUND_MIDDLE);
        } else if (event.equalsIgnoreCase("wood_q196_2.htm")) {
            st.addExpAndSp(25000000, 2500000);
            st.setState(COMPLETED);
            st.exitQuest(false);
            st.soundEffect(SOUND_FINISH);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        String htmltext = "noquest";
        if (npcId == IasonHeine) {
            if (cond == 0) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                    case QUEST:
                        htmltext = "iasonheine_q196_0.htm";
                        st.exitQuest(true);
                        break;
                    default:
                        htmltext = "iasonheine_q196_1.htm";
                        break;
                }
            } else if (cond == 1) {
                htmltext = "iasonheine_q196_1a.htm";
            } else if (cond == 2) {
                st.setCond(3);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "iasonheine_q196_3.htm";
            } else if (cond == 3 || cond == 4) {
                htmltext = "iasonheine_q196_4.htm";
            } else if (cond == 5) {
                htmltext = "iasonheine_q196_5.htm";
            } else if (cond == 6) {
                htmltext = "iasonheine_q196_6a.htm";
            }
        } else if (npcId == MerchantofMammon) {
            if (cond == 1 && MerchantofMammonSpawn != null) {
                htmltext = "merchantofmammon_q196_1.htm";
            } else {
                htmltext = "merchantofmammon_q196_0.htm";
            }
        } else if (npcId == Shunaiman) {
            if (cond == 3) {
                htmltext = "shunaiman_q196_1.htm";
            } else if (cond == 4 && st.ownItemCount(SealOfBinding) >= 4) {
                htmltext = "shunaiman_q196_3.htm";
            } else if (cond == 4 && st.ownItemCount(SealOfBinding) < 4) {
                htmltext = "shunaiman_q196_3a.htm";
            } else if (cond == 5) {
                htmltext = "shunaiman_q196_4a.htm";
            }
        } else if (npcId == CourtMagician) {
            if (cond == 4 && st.ownItemCount(CourtMagiciansMagicStaff) < 1) {
                htmltext = "courtmagician_q196_1.htm";
            } else {
                htmltext = "courtmagician_q196_1a.htm";
            }
        } else if (npcId == DisciplesGatekeeper) {
            if (cond == 4) {
                htmltext = "disciplesgatekeeper_q196_1.htm";
            }
        } else if (npcId == Leon) {
            if (cond == 5) {
                htmltext = "leon_q196_1.htm";
            } else {
                htmltext = "leon_q196_1a.htm";
            }
        } else if (npcId == Wood) {
            if (cond == 6) {
                htmltext = "wood_q196_1.htm";
            }
        }
        return htmltext;
    }

    private void enterInstance(Player player) {
        ReflectionUtils.simpleEnterInstancedZone(player, izId);
    }

    private static class SpawnLilithRoom extends RunnableImpl {
        Reflection _r;

        public SpawnLilithRoom(Reflection r) {
            _r = r;
        }

        @Override
        public void runImpl() throws Exception {
            if (_r != null) {
                _r.addSpawnWithoutRespawn(32715, new Location(-83175, 217021, -7504, 49151), 0); //Lilith
                _r.addSpawnWithoutRespawn(32718, new Location(-83179, 216479, -7504, 16384), 0); //Anakim
                _r.addSpawnWithoutRespawn(32717, new Location(-83222, 217055, -7504, 49151), 0); //liliths_shadow_guard_ssq
                _r.addSpawnWithoutRespawn(32716, new Location(-83127, 217056, -7504, 49151), 0); //liliths_agent_wizard_ssq
                _r.addSpawnWithoutRespawn(32719, new Location(-83227, 216443, -7504, 16384), 0); //anakims_holly_ssq
                _r.addSpawnWithoutRespawn(32721, new Location(-83179, 216432, -7504, 16384), 0); //anakims_sacred_ssq
                _r.addSpawnWithoutRespawn(32720, new Location(-83134, 216443, -7504, 16384), 0); //anakims_divine_ssq
            }
        }
    }


}