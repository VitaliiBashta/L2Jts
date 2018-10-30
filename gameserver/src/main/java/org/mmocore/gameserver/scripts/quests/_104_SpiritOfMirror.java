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
 * @version 1.0
 * @date 04/01/2015
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _104_SpiritOfMirror extends Quest {
    // npc
    private static final int gallin = 30017;
    private static final int arnold = 30041;
    private static final int johnson = 30043;
    private static final int ken = 30045;
    // mobs
    private static final int spirit_of_mirrors1 = 27003;
    private static final int spirit_of_mirrors2 = 27004;
    private static final int spirit_of_mirrors3 = 27005;
    // questitem
    private static final int wand_spiritbound1 = 1135;
    private static final int wand_spiritbound2 = 1136;
    private static final int wand_spiritbound3 = 1137;
    // etcitem
    private static final int spiritshot_none_for_rookie = 5790;
    private static final int lesser_healing_potion = 1060;
    private static final int echo_crystal_solitude = 4414;
    private static final int echo_crystal_feast = 4415;
    private static final int echo_crystal_celebration = 4416;
    private static final int echo_crystal_love = 4413;
    private static final int echo_crystal_battle = 4412;
    private static final int gallins_oak_wand = 748;
    private static final int wand_of_adept = 747;

    public _104_SpiritOfMirror() {
        super(false);
        addStartNpc(gallin);
        addTalkId(arnold, johnson, ken);
        addKillId(spirit_of_mirrors1, spirit_of_mirrors2, spirit_of_mirrors3);
        addLevelCheck(10, 15);
        addRaceCheck(PlayerRace.human);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == gallin) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                st.giveItems(gallins_oak_wand, 3);
                htmltext = "gallin_q0104_03.htm";
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
        int GetMemoStateEx = st.getInt("spirit_of_mirror_ex");
        switch (id) {
            case CREATED:
                if (npcId == gallin) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case RACE:
                            htmltext = "gallin_q0104_00.htm";
                            st.exitQuest(true);
                            break;
                        case LEVEL:
                            htmltext = "gallin_q0104_06.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "gallin_q0104_02.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == gallin) {
                    if (st.ownItemCount(gallins_oak_wand) >= 1 && (st.ownItemCount(wand_spiritbound1) == 0 || st.ownItemCount(wand_spiritbound2) == 0 || st.ownItemCount(wand_spiritbound3) == 0))
                        htmltext = "gallin_q0104_04.htm";
                    else if (st.ownItemCount(wand_spiritbound1) == 1 && st.ownItemCount(wand_spiritbound2) == 1 && st.ownItemCount(wand_spiritbound3) == 1) {
                        if (talker_level < 25 && isMage) {
                            st.giveItems(spiritshot_none_for_rookie, 3000);
                            st.playTutorialVoice("tutorial_voice_027");
                        }
                        st.giveItems(lesser_healing_potion, 100);
                        st.giveItems(echo_crystal_solitude, 10);
                        st.giveItems(echo_crystal_feast, 10);
                        st.giveItems(echo_crystal_celebration, 10);
                        st.giveItems(echo_crystal_love, 10);
                        st.giveItems(echo_crystal_battle, 10);
                        st.addExpAndSp(39750, 3407);
                        st.giveItems(ADENA_ID, 16866);
                        if (player.getQuestState(41) == null) {
                            final Quest q = QuestManager.getQuest(41);
                            q.newQuestState(player, Quest.STARTED);
                            player.getQuestState(41).setMemoState("guide_mission", String.valueOf(100000), true);
                            st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.ACQUISITION_OF_RACE_SPECIFIC_WEAPON_COMPLETE_GO_FIND_THE_NEWBIE_GUIDE, 5000, ScreenMessageAlign.TOP_CENTER, true));
                        } else if (player.getQuestState(41).getInt("guide_mission") % 1000000 / 100000 != 1) {
                            player.getQuestState(41).setMemoState("guide_mission", String.valueOf(player.getQuestState(41).getInt("guide_mission") + 100000), true);
                            st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.ACQUISITION_OF_RACE_SPECIFIC_WEAPON_COMPLETE_GO_FIND_THE_NEWBIE_GUIDE, 5000, ScreenMessageAlign.TOP_CENTER, true));
                        }
                        st.takeItems(wand_spiritbound1, 1);
                        st.takeItems(wand_spiritbound2, 1);
                        st.takeItems(wand_spiritbound3, 1);
                        st.giveItems(wand_of_adept, 1);
                        st.removeMemo("spirit_of_mirror");
                        htmltext = "gallin_q0104_05.htm";
                        st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                        st.exitQuest(false);
                        st.soundEffect(SOUND_FINISH);
                    }
                } else if (npcId == johnson) {
                    htmltext = "johnson_q0104_01.htm";
                    st.setMemoState("spirit_of_mirror_ex", String.valueOf(GetMemoStateEx + 1), true);
                    if (GetMemoStateEx == 110) {
                        st.setCond(2);
                        st.soundEffect(SOUND_MIDDLE);
                    }
                } else if (npcId == ken) {
                    htmltext = "ken_q0104_01.htm";
                    st.setMemoState("spirit_of_mirror_ex", String.valueOf(GetMemoStateEx + 10), true);
                    if (GetMemoStateEx == 101) {
                        st.setCond(2);
                        st.soundEffect(SOUND_MIDDLE);
                    }
                } else if (npcId == arnold) {
                    htmltext = "arnold_q0104_01.htm";
                    st.setMemoState("spirit_of_mirror_ex", String.valueOf(GetMemoStateEx + 100), true);
                    if (GetMemoStateEx == 11) {
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
        if (npcId == spirit_of_mirrors1) {
            if (st.getPlayer().getActiveWeaponInstance() != null && st.getPlayer().getActiveWeaponInstance().getItemId() == gallins_oak_wand && st.ownItemCount(wand_spiritbound1) == 0 && st.ownItemCount(gallins_oak_wand) > 0) {
                st.takeItems(gallins_oak_wand, 1);
                st.giveItems(wand_spiritbound1, 1);
                if (st.ownItemCount(wand_spiritbound1) + st.ownItemCount(wand_spiritbound2) + st.ownItemCount(wand_spiritbound3) >= 2)
                    st.setCond(3);
                st.soundEffect(SOUND_MIDDLE);
            }
        } else if (npcId == spirit_of_mirrors2) {
            if (st.getPlayer().getActiveWeaponInstance() != null && st.getPlayer().getActiveWeaponInstance().getItemId() == gallins_oak_wand && st.ownItemCount(wand_spiritbound2) == 0 && st.ownItemCount(gallins_oak_wand) > 0) {
                st.takeItems(gallins_oak_wand, 1);
                st.giveItems(wand_spiritbound2, 1);
                if (st.ownItemCount(wand_spiritbound1) + st.ownItemCount(wand_spiritbound2) + st.ownItemCount(wand_spiritbound3) >= 2)
                    st.setCond(3);
                st.soundEffect(SOUND_MIDDLE);
            }
        } else if (npcId == spirit_of_mirrors3) {
            if (st.getPlayer().getActiveWeaponInstance() != null && st.getPlayer().getActiveWeaponInstance().getItemId() == gallins_oak_wand && st.ownItemCount(wand_spiritbound3) == 0 && st.ownItemCount(gallins_oak_wand) > 0) {
                st.takeItems(gallins_oak_wand, 1);
                st.giveItems(wand_spiritbound3, 1);
                if (st.ownItemCount(wand_spiritbound1) + st.ownItemCount(wand_spiritbound2) + st.ownItemCount(wand_spiritbound3) >= 2)
                    st.setCond(3);
                st.soundEffect(SOUND_MIDDLE);
            }
        }
        return null;
    }
}