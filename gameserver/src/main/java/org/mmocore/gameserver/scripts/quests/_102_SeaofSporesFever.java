package org.mmocore.gameserver.scripts.quests;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.serverpackets.SocialAction;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.0
 * @date 07/01/2015
 * @tested OK
 */
@ChronicleCheck(Chronicle.FREYA)
public class _102_SeaofSporesFever extends Quest {
    // npc
    private static final int alberryus = 30284;
    private static final int cob = 30156;
    private static final int sentinel_berryos = 30217;
    private static final int sentinel_veltress = 30219;
    private static final int sentinel_rayjien = 30221;
    private static final int sentinel_gartrandell = 30285;
    // mobs
    private static final int dryad = 20013;
    private static final int dryad_ribe = 20019;
    // questitem
    private static final int alberryus_letter = 964;
    private static final int evergreen_amulet = 965;
    private static final int dryad_tears = 966;
    private static final int alberryus_list = 746;
    private static final int cobs_medicine1 = 1130;
    private static final int cobs_medicine2 = 1131;
    private static final int cobs_medicine3 = 1132;
    private static final int cobs_medicine4 = 1133;
    private static final int cobs_medicine5 = 1134;
    // etcitem
    private static final int soulshot_none = 1835;
    private static final int spiritshot_none = 2509;
    private static final int lesser_healing_potion = 1060;
    private static final int echo_crystal_solitude = 4414;
    private static final int echo_crystal_feast = 4415;
    private static final int echo_crystal_celebration = 4416;
    private static final int echo_crystal_love = 4413;
    private static final int echo_crystal_battle = 4412;
    private static final int sword_of_sentinel = 743;
    private static final int staff_of_sentinel = 744;

    public _102_SeaofSporesFever() {
        super(false);
        addStartNpc(alberryus);
        addTalkId(cob, sentinel_berryos, sentinel_veltress, sentinel_rayjien, sentinel_gartrandell);
        addKillId(dryad, dryad_ribe);
        addKillId(20019);
        addQuestItem(alberryus_letter, evergreen_amulet, dryad_tears, cobs_medicine1, cobs_medicine2, cobs_medicine3, cobs_medicine4, cobs_medicine5, alberryus_list);
        addLevelCheck(12, 18);
        addRaceCheck(PlayerRace.elf);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == alberryus) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setState(STARTED);
                st.giveItems(alberryus_letter, 1);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "alberryus_q0102_02.htm";
            }
        } else if (npcId == cob) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (st.ownItemCount(alberryus_letter) > 0) {
                    st.setCond(2);
                    st.giveItems(evergreen_amulet, 1);
                    st.takeItems(alberryus_letter, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "cob_q0102_03.htm";
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("fungus_fever");
        boolean isMage = st.getPlayer().getPlayerClassComponent().getClassId().isMage();
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == alberryus) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "alberryus_q0102_08.htm";
                            st.exitQuest(true);
                            break;
                        case RACE:
                            htmltext = "alberryus_q0102_00.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "alberryus_q0102_07.htm";
                            return htmltext;
                    }
                }
                break;
            case STARTED:
                if (npcId == alberryus) {
                    if (st.ownItemCount(alberryus_letter) == 1)
                        htmltext = "alberryus_q0102_03.htm";
                    else if (st.ownItemCount(evergreen_amulet) == 1)
                        htmltext = "alberryus_q0102_09.htm";
                    else if (st.ownItemCount(alberryus_list) == 0 && st.ownItemCount(cobs_medicine1) == 1) {
                        st.takeItems(cobs_medicine1, 1);
                        st.giveItems(alberryus_list, 1);
                        htmltext = "alberryus_q0102_04.htm";
                        st.setCond(5);
                        st.soundEffect(SOUND_MIDDLE);
                    } else if (st.ownItemCount(alberryus_list) == 1 && (st.ownItemCount(cobs_medicine1) == 1 || st.ownItemCount(cobs_medicine2) == 1 || st.ownItemCount(cobs_medicine3) == 1 || st.ownItemCount(cobs_medicine4) == 1 || st.ownItemCount(cobs_medicine5) == 1))
                        htmltext = "alberryus_q0102_05.htm";
                    else if (st.ownItemCount(alberryus_list) == 1 && GetMemoState == 1 && st.ownItemCount(cobs_medicine1) == 0 && st.ownItemCount(cobs_medicine2) == 0 && st.ownItemCount(cobs_medicine3) == 0 && st.ownItemCount(cobs_medicine4) == 0 && st.ownItemCount(cobs_medicine5) == 0) {
                        st.takeItems(alberryus_list, 1);
                        htmltext = "alberryus_q0102_06.htm";
                        st.giveItems(lesser_healing_potion, 100);
                        st.giveItems(echo_crystal_solitude, 10);
                        st.giveItems(echo_crystal_feast, 10);
                        st.giveItems(echo_crystal_celebration, 10);
                        st.giveItems(echo_crystal_love, 10);
                        st.giveItems(echo_crystal_battle, 10);
                        st.addExpAndSp(30202, 1339);
                        st.giveItems(ADENA_ID, 6331);
                        if (!isMage) {
                            st.giveItems(sword_of_sentinel, 1);
                            st.giveItems(soulshot_none, 1000);
                        } else {
                            st.giveItems(staff_of_sentinel, 1);
                            st.giveItems(spiritshot_none, 500);
                        }
                        st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                        st.exitQuest(false);
                        st.soundEffect(SOUND_FINISH);
                    }
                } else if (npcId == cob) {
                    if (st.ownItemCount(alberryus_letter) == 1)
                        htmltext = "cob_q0102_01.htm";
                    else if (st.ownItemCount(evergreen_amulet) > 0 && st.ownItemCount(dryad_tears) < 10)
                        htmltext = "cob_q0102_04.htm";
                    else if (st.ownItemCount(alberryus_list) > 0)
                        htmltext = "cob_q0102_07.htm";
                    else if (st.ownItemCount(evergreen_amulet) > 0 && st.ownItemCount(dryad_tears) >= 10) {
                        st.setCond(4);
                        st.takeItems(evergreen_amulet, 1);
                        st.takeItems(dryad_tears, -1);
                        st.giveItems(cobs_medicine1, 1);
                        st.giveItems(cobs_medicine2, 1);
                        st.giveItems(cobs_medicine3, 1);
                        st.giveItems(cobs_medicine4, 1);
                        st.giveItems(cobs_medicine5, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "cob_q0102_05.htm";
                    } else if (st.ownItemCount(alberryus_list) == 0 && (st.ownItemCount(cobs_medicine1) == 1 || st.ownItemCount(cobs_medicine2) == 1 || st.ownItemCount(cobs_medicine3) == 1 || st.ownItemCount(cobs_medicine4) == 1 || st.ownItemCount(cobs_medicine5) == 1))
                        htmltext = "cob_q0102_06.htm";
                } else if (npcId == sentinel_berryos) {
                    if (st.ownItemCount(alberryus_list) == 1 && st.ownItemCount(cobs_medicine2) == 1) {
                        st.setMemoState("fungus_fever", String.valueOf(st.ownItemCount(cobs_medicine1) + st.ownItemCount(cobs_medicine2) + st.ownItemCount(cobs_medicine3) + st.ownItemCount(cobs_medicine4) + st.ownItemCount(cobs_medicine5)), true);
                        st.takeItems(cobs_medicine2, 1);
                        if (st.ownItemCount(cobs_medicine1) + st.ownItemCount(cobs_medicine3) + st.ownItemCount(cobs_medicine4) + st.ownItemCount(cobs_medicine5) < 1) {
                            st.setCond(6);
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        htmltext = "sentinel_berryos_q0102_01.htm";
                    }
                } else if (npcId == sentinel_gartrandell) {
                    if (st.ownItemCount(alberryus_list) == 1 && st.ownItemCount(cobs_medicine5) == 1) {
                        st.setMemoState("fungus_fever", String.valueOf(st.ownItemCount(cobs_medicine1) + st.ownItemCount(cobs_medicine2) + st.ownItemCount(cobs_medicine3) + st.ownItemCount(cobs_medicine4) + st.ownItemCount(cobs_medicine5)), true);
                        st.takeItems(cobs_medicine5, 1);
                        if (st.ownItemCount(cobs_medicine1) + st.ownItemCount(cobs_medicine2) + st.ownItemCount(cobs_medicine3) + st.ownItemCount(cobs_medicine4) < 1) {
                            st.setCond(6);
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        htmltext = "sentinel_gartrandell_q0102_01.htm";
                    }
                } else if (npcId == sentinel_veltress) {
                    if (st.ownItemCount(alberryus_list) == 1 && st.ownItemCount(cobs_medicine3) == 1) {
                        st.setMemoState("fungus_fever", String.valueOf(st.ownItemCount(cobs_medicine1) + st.ownItemCount(cobs_medicine2) + st.ownItemCount(cobs_medicine3) + st.ownItemCount(cobs_medicine4) + st.ownItemCount(cobs_medicine5)), true);
                        st.takeItems(cobs_medicine3, 1);
                        if (st.ownItemCount(cobs_medicine1) + st.ownItemCount(cobs_medicine2) + st.ownItemCount(cobs_medicine4) + st.ownItemCount(cobs_medicine5) < 1) {
                            st.setCond(6);
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        htmltext = "sentinel_veltress_q0102_01.htm";
                    }
                } else if (npcId == sentinel_rayjien) {
                    if (st.ownItemCount(alberryus_list) == 1 && st.ownItemCount(cobs_medicine4) == 1) {
                        st.setMemoState("fungus_fever", String.valueOf(st.ownItemCount(cobs_medicine1) + st.ownItemCount(cobs_medicine2) + st.ownItemCount(cobs_medicine3) + st.ownItemCount(cobs_medicine4) + st.ownItemCount(cobs_medicine5)), true);
                        st.takeItems(cobs_medicine4, 1);
                        if (st.ownItemCount(cobs_medicine1) + st.ownItemCount(cobs_medicine2) + st.ownItemCount(cobs_medicine3) + st.ownItemCount(cobs_medicine5) < 1) {
                            st.setCond(6);
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        htmltext = "sentinel_rayjien_q0102_01.htm";
                    }
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == dryad || npcId == dryad_ribe) {
            if (Rnd.get(10) < 3) {
                st.giveItems(dryad_tears, 1);
                if (st.ownItemCount(dryad_tears) >= 10) {
                    st.setCond(3);
                    st.soundEffect(SOUND_MIDDLE);
                } else
                    st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }
}
