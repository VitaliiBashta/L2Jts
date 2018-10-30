package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.base.ClassId;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.serverpackets.SocialAction;
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
public class _404_PathOfTheHumanWizard extends Quest {
    // npc
    private final static int parina = 30391;
    private final static int earth_snake = 30409;
    private final static int lizardman_of_wasteland = 30410;
    private final static int flame_salamander = 30411;
    private final static int wind_sylph = 30412;
    private final static int water_undine = 30413;
    // mobs
    private final static int red_bear = 20021;
    private final static int ratman_warrior = 20359;
    private final static int water_seer = 27030;
    // questitem
    private final static int map_of_luster = 1280;
    private final static int key_of_flame = 1281;
    private final static int flame_earing = 1282;
    private final static int broken_bronze_mirror = 1283;
    private final static int wind_feather = 1284;
    private final static int wind_bangel = 1285;
    private final static int ramas_diary = 1286;
    private final static int sparkle_pebble = 1287;
    private final static int water_necklace = 1288;
    private final static int rust_gold_coin = 1289;
    private final static int red_soil = 1290;
    private final static int earth_ring = 1291;
    private final static int bead_of_season = 1292;

    public _404_PathOfTheHumanWizard() {
        super(false);
        addStartNpc(parina);
        addTalkId(earth_snake, lizardman_of_wasteland, flame_salamander, wind_sylph, water_undine);
        addKillId(red_bear, ratman_warrior, water_seer);
        addQuestItem(map_of_luster, key_of_flame, wind_feather, broken_bronze_mirror, sparkle_pebble, ramas_diary, red_soil, rust_gold_coin, flame_earing, wind_bangel, water_necklace, earth_ring);
        addLevelCheck(18);
        addClassIdCheck(ClassId.mage);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        int talker_level = st.getPlayer().getLevel();
        int talker_occupation = st.getPlayer().getPlayerClassComponent().getClassId().getId();
        int wizard = 0x0b;
        int mage = 0x0a;
        if (npcId == parina) {
            if (event.equalsIgnoreCase("quest_accept")) {
                if (talker_occupation == mage) {
                    if (talker_level >= 18) {
                        if (st.ownItemCount(bead_of_season) > 0)
                            htmltext = "parina_q0404_03.htm";
                        else {
                            st.setCond(1);
                            st.setState(STARTED);
                            st.soundEffect(SOUND_ACCEPT);
                            htmltext = "parina_q0404_08.htm";
                        }
                    } else
                        htmltext = "parina_q0404_02.htm";
                } else if (talker_occupation == wizard)
                    htmltext = "parina_q0404_02a.htm";
                else
                    htmltext = "parina_q0404_01.htm";
            }
        } else if (npcId == lizardman_of_wasteland) {
            if (event.equalsIgnoreCase("404_reply_1")) {
                if (st.ownItemCount(wind_feather) == 0) {
                    st.setCond(6);
                    st.giveItems(wind_feather, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "lizardman_of_wasteland_q0404_03.htm";
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        Player player = st.getPlayer();
        int talker_level = st.getPlayer().getLevel();
        int GetOneTimeQuestFlag = st.getPlayer().getPlayerVariables().getInt(PlayerVariables.profession_145);
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == parina)
                    htmltext = "parina_q0404_04.htm";
                break;
            case STARTED:
                if (npcId == parina) {
                    if (st.ownItemCount(flame_earing) == 0 || st.ownItemCount(wind_bangel) == 0 || st.ownItemCount(water_necklace) == 0 || st.ownItemCount(earth_ring) == 0)
                        htmltext = "parina_q0404_05.htm";
                    else if (st.ownItemCount(flame_earing) != 0 && st.ownItemCount(wind_bangel) != 0 && st.ownItemCount(water_necklace) != 0 && st.ownItemCount(earth_ring) != 0) {
                        st.takeItems(flame_earing, -1);
                        st.takeItems(wind_bangel, -1);
                        st.takeItems(water_necklace, -1);
                        st.takeItems(earth_ring, -1);
                        if (GetOneTimeQuestFlag == 0) {
                            st.getPlayer().getPlayerVariables().set(PlayerVariables.profession_145, String.valueOf(1), -1);
                            if (talker_level >= 20)
                                st.addExpAndSp(320534, 23152);
                            else if (talker_level >= 19)
                                st.addExpAndSp(456128, 29850);
                            else
                                st.addExpAndSp(591724, 36548);
                            st.giveItems(ADENA_ID, 163800);
                        }
                        if (st.ownItemCount(bead_of_season) == 0)
                            st.giveItems(bead_of_season, 1);
                        htmltext = "parina_q0404_06.htm";
                        player.sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                    }
                } else if (npcId == earth_snake) {
                    if (st.ownItemCount(water_necklace) != 0 && st.ownItemCount(rust_gold_coin) == 0 && st.ownItemCount(earth_ring) == 0) {
                        if (st.ownItemCount(rust_gold_coin) == 0)
                            st.giveItems(rust_gold_coin, 1);
                        htmltext = "earth_snake_q0404_01.htm";
                        st.setCond(11);
                        st.soundEffect(SOUND_MIDDLE);
                    } else if (st.ownItemCount(rust_gold_coin) != 0 && st.ownItemCount(red_soil) == 0)
                        htmltext = "earth_snake_q0404_02.htm";
                    else if (st.ownItemCount(rust_gold_coin) != 0 && st.ownItemCount(red_soil) != 0) {
                        st.setCond(13);
                        st.soundEffect(SOUND_MIDDLE);
                        st.takeItems(red_soil, -1);
                        st.takeItems(rust_gold_coin, -1);
                        if (st.ownItemCount(earth_ring) == 0)
                            st.giveItems(earth_ring, 1);
                        htmltext = "earth_snake_q0404_03.htm";
                    } else if (st.ownItemCount(earth_ring) != 0)
                        htmltext = "earth_snake_q0404_04.htm";
                } else if (npcId == lizardman_of_wasteland) {
                    if (st.ownItemCount(broken_bronze_mirror) != 0 && st.ownItemCount(wind_feather) == 0)
                        htmltext = "lizardman_of_wasteland_q0404_01.htm";
                    else if (st.ownItemCount(broken_bronze_mirror) != 0 && st.ownItemCount(wind_feather) != 0)
                        htmltext = "lizardman_of_wasteland_q0404_04.htm";
                } else if (npcId == flame_salamander) {
                    if (st.ownItemCount(map_of_luster) == 0 && st.ownItemCount(flame_earing) == 0) {
                        if (st.ownItemCount(map_of_luster) == 0)
                            st.giveItems(map_of_luster, 1);
                        st.setCond(2);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "flame_salamander_q0404_01.htm";
                    } else if (st.ownItemCount(map_of_luster) != 0 && st.ownItemCount(key_of_flame) == 0)
                        htmltext = "flame_salamander_q0404_02.htm";
                    else if (st.ownItemCount(map_of_luster) != 0 && st.ownItemCount(key_of_flame) != 0) {
                        st.takeItems(key_of_flame, -1);
                        st.takeItems(map_of_luster, -1);
                        if (st.ownItemCount(flame_earing) == 0)
                            st.giveItems(flame_earing, 1);
                        st.setCond(4);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "flame_salamander_q0404_03.htm";
                    } else if (st.ownItemCount(flame_earing) != 0)
                        htmltext = "flame_salamander_q0404_04.htm";
                } else if (npcId == wind_sylph) {
                    if (st.ownItemCount(flame_earing) != 0 && st.ownItemCount(broken_bronze_mirror) == 0 && st.ownItemCount(wind_bangel) == 0) {
                        if (st.ownItemCount(broken_bronze_mirror) == 0)
                            st.giveItems(broken_bronze_mirror, 1);
                        st.setCond(5);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "wind_sylph_q0404_01.htm";
                    } else if (st.ownItemCount(broken_bronze_mirror) != 0 && st.ownItemCount(wind_feather) == 0)
                        htmltext = "wind_sylph_q0404_02.htm";
                    else if (st.ownItemCount(broken_bronze_mirror) != 0 && st.ownItemCount(wind_feather) != 0) {
                        st.takeItems(wind_feather, -1);
                        st.takeItems(broken_bronze_mirror, -1);
                        if (st.ownItemCount(wind_bangel) == 0)
                            st.giveItems(wind_bangel, 1);
                        st.setCond(7);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "wind_sylph_q0404_03.htm";
                    } else if (st.ownItemCount(wind_bangel) != 0)
                        htmltext = "wind_sylph_q0404_04.htm";
                } else if (npcId == water_undine) {
                    if (st.ownItemCount(wind_bangel) != 0 && st.ownItemCount(ramas_diary) == 0 && st.ownItemCount(water_necklace) == 0) {
                        if (st.ownItemCount(ramas_diary) == 0)
                            st.giveItems(ramas_diary, 1);
                        st.setCond(8);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "water_undine_q0404_01.htm";
                    } else if (st.ownItemCount(ramas_diary) != 0 && st.ownItemCount(sparkle_pebble) < 2)
                        htmltext = "water_undine_q0404_02.htm";
                    else if (st.ownItemCount(ramas_diary) != 0 && st.ownItemCount(sparkle_pebble) >= 2) {
                        st.takeItems(sparkle_pebble, -1);
                        st.takeItems(ramas_diary, -1);
                        if (st.ownItemCount(water_necklace) == 0)
                            st.giveItems(water_necklace, 1);
                        st.setCond(10);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "water_undine_q0404_03.htm";
                    } else if (st.ownItemCount(water_necklace) != 0)
                        htmltext = "water_undine_q0404_04.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == red_bear) {
            if (st.ownItemCount(rust_gold_coin) != 0 && st.ownItemCount(red_soil) == 0 && Rnd.get(100) < 20) {
                st.giveItems(red_soil, 1);
                st.setCond(12);
                st.soundEffect(SOUND_MIDDLE);
            }
        } else if (npcId == ratman_warrior) {
            if (st.ownItemCount(map_of_luster) != 0 && st.ownItemCount(key_of_flame) == 0 && Rnd.get(100) < 80) {
                st.giveItems(key_of_flame, 1);
                st.setCond(3);
                st.soundEffect(SOUND_MIDDLE);
            }
        } else if (npcId == water_seer) {
            if (st.ownItemCount(ramas_diary) != 0 && st.ownItemCount(sparkle_pebble) < 2 && Rnd.get(100) < 80) {
                st.giveItems(sparkle_pebble, 1);
                if (st.ownItemCount(sparkle_pebble) >= 2) {
                    st.soundEffect(SOUND_MIDDLE);
                    st.setCond(9);
                } else
                    st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }
}
