package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.2
 * @date 13/04/2016
 * @lastedit 07/07/2016
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _337_AudienceWithLandDragon extends Quest {
    // npc
    private final static int warehouse_chief_moke = 30498;
    private final static int blacksmith_helton = 30678;
    private final static int prefect_chakiris = 30705;
    private final static int magister_kaiena = 30720;
    private final static int gabrielle = 30753;
    private final static int watcher_antaras_gilmore = 30754;
    private final static int watcher_antaras_theodric = 30755;
    private final static int master_kendra = 30851;
    private final static int highpriest_orven = 30857;
    // mobs
    private final static int cave_maiden = 20134;
    private final static int cave_keeper = 20246;
    private final static int cave_keeper_hold = 20277;
    private final static int cave_maiden_hold = 20287;
    private final static int harit_lizardman_shaman = 20644;
    private final static int harit_lizardm_matriarch = 20645;
    private final static int hamrut = 20649;
    private final static int kranrot = 20650;
    private final static int marsh_stalker = 20679;
    private final static int marsh_drake = 20680;
    private final static int abyssal_jewel_1 = 27165;
    private final static int abyssal_jewel_2 = 27166;
    private final static int abyssal_jewel_3 = 27167;
    private final static int jewel_guardian_mara = 27168;
    private final static int jewel_guardian_musfel = 27169;
    private final static int jewel_guardian_pyton = 27170;
    private final static int sacrifice_of_sacrificed = 27171;
    private final static int harit_lizardman_zealot = 27172;
    private final static int bloody_queen = 18001;
    // questitem
    private final static int q_feather_of_gabrielle = 3852;
    private final static int q_marsh_stalker_horn = 3853;
    private final static int q_marsh_drake_talon = 3854;
    private final static int q_remains_of_sacrificed = 3857;
    private final static int q_totem_of_earthdragon = 3858;
    private final static int q_hamrut_leg = 3856;
    private final static int q_kranrot_skin = 3855;
    private final static int q_mara_fang = 3862;
    private final static int q_musfel_fang = 3863;
    private final static int q_frag_of_abyss_jewel1 = 3859;
    private final static int q_frag_of_abyss_jewel2 = 3860;
    private final static int q_frag_of_abyss_jewel3 = 3861;
    private final static int q_herald_of_slayer = 3890;
    private final static int q_mark_of_watchers = 3864;
    private final static int q_portal_stone_1 = 3865;

    private final Map<String, List<NpcInstance>> mobs = new HashMap<>();

    public _337_AudienceWithLandDragon() {
        super(false);
        addStartNpc(gabrielle);
        addTalkId(magister_kaiena, highpriest_orven, master_kendra, prefect_chakiris, warehouse_chief_moke, blacksmith_helton, watcher_antaras_gilmore, watcher_antaras_theodric);
        addKillId(cave_maiden, cave_keeper, cave_keeper_hold, cave_maiden_hold, harit_lizardman_shaman, harit_lizardm_matriarch, hamrut, kranrot, marsh_stalker, marsh_drake, abyssal_jewel_1, abyssal_jewel_2, abyssal_jewel_3, jewel_guardian_mara, jewel_guardian_musfel, jewel_guardian_pyton, sacrifice_of_sacrificed, harit_lizardman_zealot, bloody_queen);
        addAttackId(abyssal_jewel_1, abyssal_jewel_2, abyssal_jewel_3);
        addQuestItem(q_feather_of_gabrielle, q_herald_of_slayer, q_marsh_stalker_horn, q_marsh_drake_talon, q_remains_of_sacrificed, q_totem_of_earthdragon, q_hamrut_leg, q_kranrot_skin, q_mara_fang, q_frag_of_abyss_jewel1, q_musfel_fang, q_frag_of_abyss_jewel2, q_frag_of_abyss_jewel3, q_mark_of_watchers);
        addLevelCheck(50, 64);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == gabrielle) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("audience_with_earth_dragon", String.valueOf(20000), true);
                st.giveItems(q_feather_of_gabrielle, 1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "gabrielle_q0337_05.htm";
            } else if (event.equalsIgnoreCase("reply_1"))
                htmltext = "gabrielle_q0337_03.htm";
            else if (event.equalsIgnoreCase("reply_2"))
                htmltext = "gabrielle_q0337_04.htm";
            else if (event.equalsIgnoreCase("reply_3")) {
                st.setCond(2);
                st.setMemoState("audience_with_earth_dragon", String.valueOf(40000), true);
                st.takeItems(q_mark_of_watchers, -1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "gabrielle_q0337_09.htm";
            }
        } else if (npcId == warehouse_chief_moke) {
            if (event.equalsIgnoreCase("reply_1"))
                htmltext = "warehouse_chief_moke_q0337_02.htm";
        } else if (npcId == blacksmith_helton) {
            if (event.equalsIgnoreCase("reply_1"))
                htmltext = "blacksmith_helton_q0337_1a.htm";
        } else if (npcId == watcher_antaras_gilmore) {
            if (event.equalsIgnoreCase("reply_1")) {
                st.setCond(4);
                st.setMemoState("audience_with_earth_dragon", String.valueOf(70000), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "watcher_antaras_gilmore_q0337_03.htm";
            }
        } else if (npcId == watcher_antaras_theodric) {
            if (event.equalsIgnoreCase("reply_1")) {
                st.giveItems(q_portal_stone_1, 1);
                st.takeItems(q_frag_of_abyss_jewel3, -1);
                st.takeItems(q_herald_of_slayer, -1);
                st.removeMemo("audience_with_earth_dragon");
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                mobs.clear();
                htmltext = "watcher_antaras_theodric_q0337_05.htm";
            }
        } else if (event.equalsIgnoreCase("233721")) {
            if (npc != null)
                npc.deleteMe();
            return null;
        } else if (event.equalsIgnoreCase("233704")) {
            List<NpcInstance> list = mobs.get("spawned_jewel_guardian_pyton");
            if (list != null && !list.isEmpty()) {
                list.stream().filter(n -> n != null).forEach(NpcInstance::deleteMe);
            }
            st.removeMemo("spawned_jewel_guardian_pyton");
            return null;
        } else if (event.equalsIgnoreCase("233731")) {
            List<NpcInstance> list = mobs.get("spawned_harit_lizardman_zealot");
            if (list != null && !list.isEmpty()) {
                list.stream().filter(n -> n != null).forEach(NpcInstance::deleteMe);
            }
            st.removeMemo("spawned_harit_lizardman_zealot");
            return null;
        } else if (event.equalsIgnoreCase("233751") || event.equalsIgnoreCase("233753")) {
            List<NpcInstance> list = mobs.get("spawned_jewel_guardian_mara");
            if (list != null && !list.isEmpty()) {
                list.stream().filter(n -> n != null).forEach(NpcInstance::deleteMe);
            }
            st.removeMemo("spawned_jewel_guardian_mara");
            return null;
        } else if (event.equalsIgnoreCase("233752") || event.equalsIgnoreCase("233754")) {
            List<NpcInstance> list = mobs.get("spawned_jewel_guardian_musfel");
            if (list != null && !list.isEmpty()) {
                list.stream().filter(n -> n != null).forEach(NpcInstance::deleteMe);
            }
            st.removeMemo("spawned_jewel_guardian_musfel");
            return null;
        } else if (event.equalsIgnoreCase("233701")) {
            List<NpcInstance> list = mobs.get("spawned_sacrifice_of_sacrificed");
            if (list != null && !list.isEmpty()) {
                list.stream().filter(n -> n != null).forEach(NpcInstance::deleteMe);
            }
            st.removeMemo("spawned_sacrifice_of_sacrificed");
            return null;
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("audience_with_earth_dragon");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == gabrielle) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "gabrielle_q0337_01.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "gabrielle_q0337_02.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == gabrielle) {
                    if (GetMemoState >= 20000 && GetMemoState < 30000)
                        htmltext = "gabrielle_q0337_06.htm";
                    else if (GetMemoState == 30000)
                        htmltext = "gabrielle_q0337_08.htm";
                    else if (GetMemoState >= 40000 && GetMemoState < 50000)
                        htmltext = "gabrielle_q0337_10.htm";
                    else if (GetMemoState == 50000) {
                        st.setCond(3);
                        st.setMemoState("audience_with_earth_dragon", String.valueOf(60000), true);
                        st.giveItems(q_herald_of_slayer, 1);
                        st.takeItems(q_feather_of_gabrielle, -1);
                        st.takeItems(q_mark_of_watchers, -1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "gabrielle_q0337_11.htm";
                    } else if (GetMemoState == 60000)
                        htmltext = "gabrielle_q0337_12.htm";
                    else if (GetMemoState == 70000)
                        htmltext = "gabrielle_q0337_13.htm";
                } else if (npcId == magister_kaiena) {
                    if ((GetMemoState == 20111 || GetMemoState == 20110 || GetMemoState == 20101 || GetMemoState == 20100 || GetMemoState == 20010 || GetMemoState == 20011 || GetMemoState == 20001 || GetMemoState == 20000) && (st.ownItemCount(q_marsh_stalker_horn) == 0 || st.ownItemCount(q_marsh_drake_talon) == 0))
                        htmltext = "magister_kaiena_q0337_01.htm";
                    else if ((GetMemoState == 20111 || GetMemoState == 20110 || GetMemoState == 20101 || GetMemoState == 20100 || GetMemoState == 20010 || GetMemoState == 20011 || GetMemoState == 20001 || GetMemoState == 20000) && (st.ownItemCount(q_marsh_stalker_horn) == 1 || st.ownItemCount(q_marsh_drake_talon) == 1)) {
                        st.giveItems(q_mark_of_watchers, 1);
                        st.takeItems(q_marsh_stalker_horn, -1);
                        st.takeItems(q_marsh_drake_talon, -1);
                        if (GetMemoState + 1000 == 21111)
                            st.setMemoState("audience_with_earth_dragon", String.valueOf(30000), true);
                        else
                            st.setMemoState("audience_with_earth_dragon", String.valueOf(GetMemoState + 1000), true);
                        htmltext = "magister_kaiena_q0337_02.htm";
                    } else if (GetMemoState == 21110 || GetMemoState == 21101 || GetMemoState == 21100 || GetMemoState == 21011 || GetMemoState == 21010 || GetMemoState == 21001 || GetMemoState == 21000)
                        htmltext = "magister_kaiena_q0337_03.htm";
                    else if (GetMemoState >= 30000)
                        htmltext = "magister_kaiena_q0337_04.htm";
                } else if (npcId == highpriest_orven) {
                    if ((GetMemoState == 21011 || GetMemoState == 21010 || GetMemoState == 21001 || GetMemoState == 21000 || GetMemoState == 20011 || GetMemoState == 20010 || GetMemoState == 20001 || GetMemoState == 20000) && st.ownItemCount(q_remains_of_sacrificed) == 0)
                        htmltext = "highpriest_orven_q0337_01.htm";
                    else if ((GetMemoState == 21011 || GetMemoState == 21010 || GetMemoState == 21001 || GetMemoState == 21000 || GetMemoState == 20011 || GetMemoState == 20010 || GetMemoState == 20001 || GetMemoState == 20000) && st.ownItemCount(q_remains_of_sacrificed) == 1) {
                        st.giveItems(q_mark_of_watchers, 1);
                        st.takeItems(q_remains_of_sacrificed, -1);
                        if (GetMemoState + 100 == 21111)
                            st.setMemoState("audience_with_earth_dragon", String.valueOf(30000), true);
                        else
                            st.setMemoState("audience_with_earth_dragon", String.valueOf(GetMemoState + 100), true);
                        htmltext = "highpriest_orven_q0337_02.htm";
                    } else if (GetMemoState == 21110 || GetMemoState == 21101 || GetMemoState == 21100 || GetMemoState == 20111 || GetMemoState == 20110 || GetMemoState == 20101 || GetMemoState == 20100)
                        htmltext = "highpriest_orven_q0337_03.htm";
                    else if (GetMemoState >= 30000)
                        htmltext = "highpriest_orven_q0337_04.htm";
                } else if (npcId == master_kendra) {
                    if ((GetMemoState == 21110 || GetMemoState == 21100 || GetMemoState == 21010 || GetMemoState == 21000 || GetMemoState == 20110 || GetMemoState == 20100 || GetMemoState == 20010 || GetMemoState == 20000) && st.ownItemCount(q_totem_of_earthdragon) == 0)
                        htmltext = "master_kendra_q0337_01.htm";
                    else if ((GetMemoState == 21110 || GetMemoState == 21100 || GetMemoState == 21010 || GetMemoState == 21000 || GetMemoState == 20110 || GetMemoState == 20100 || GetMemoState == 20010 || GetMemoState == 20000) && st.ownItemCount(q_totem_of_earthdragon) == 1) {
                        st.giveItems(q_mark_of_watchers, 1);
                        st.takeItems(q_totem_of_earthdragon, -1);
                        if (GetMemoState + 1 == 21111)
                            st.setMemoState("audience_with_earth_dragon", String.valueOf(30000), true);
                        else
                            st.setMemoState("audience_with_earth_dragon", String.valueOf(GetMemoState + 1), true);
                        htmltext = "master_kendra_q0337_02.htm";
                    } else if (GetMemoState == 21101 || GetMemoState == 21011 || GetMemoState == 21001 || GetMemoState == 20111 || GetMemoState == 20101 || GetMemoState == 20011 || GetMemoState == 20001)
                        htmltext = "master_kendra_q0337_03.htm";
                    else if (GetMemoState >= 30000)
                        htmltext = "master_kendra_q0337_04.htm";
                } else if (npcId == prefect_chakiris) {
                    if ((GetMemoState == 21101 || GetMemoState == 21000 || GetMemoState == 21100 || GetMemoState == 21001 || GetMemoState == 20101 || GetMemoState == 20100 || GetMemoState == 20001 || GetMemoState == 20000) && (st.ownItemCount(q_kranrot_skin) == 0 || st.ownItemCount(q_hamrut_leg) == 0))
                        htmltext = "prefect_chakiris_q0337_01.htm";
                    else if ((GetMemoState == 21101 || GetMemoState == 21000 || GetMemoState == 21100 || GetMemoState == 21001 || GetMemoState == 20101 || GetMemoState == 20100 || GetMemoState == 20001 || GetMemoState == 20000) && (st.ownItemCount(q_kranrot_skin) == 1 || st.ownItemCount(q_hamrut_leg) == 1)) {
                        st.giveItems(q_mark_of_watchers, 1);
                        st.takeItems(q_kranrot_skin, -1);
                        st.takeItems(q_hamrut_leg, -1);
                        if (GetMemoState + 10 == 21111)
                            st.setMemoState("audience_with_earth_dragon", String.valueOf(30000), true);
                        else
                            st.setMemoState("audience_with_earth_dragon", String.valueOf(GetMemoState + 10), true);
                        htmltext = "prefect_chakiris_q0337_02.htm";
                    } else if (GetMemoState == 21110 || GetMemoState == 21011 || GetMemoState == 21010 || GetMemoState == 20111 || GetMemoState == 20110 || GetMemoState == 20011 || GetMemoState == 20010)
                        htmltext = "prefect_chakiris_q0337_03.htm";
                    else if (GetMemoState >= 30000)
                        htmltext = "prefect_chakiris_q0337_04.htm";
                } else if (npcId == warehouse_chief_moke) {
                    if ((GetMemoState == 40000 || GetMemoState == 40001) && (st.ownItemCount(q_frag_of_abyss_jewel1) == 0 || st.ownItemCount(q_mara_fang) == 0))
                        htmltext = "warehouse_chief_moke_q0337_01.htm";
                    else if ((GetMemoState == 40000 || GetMemoState == 40001) && (st.ownItemCount(q_frag_of_abyss_jewel1) >= 1 || st.ownItemCount(q_mara_fang) >= 1)) {
                        st.giveItems(q_mark_of_watchers, 1);
                        st.takeItems(q_frag_of_abyss_jewel1, -1);
                        st.takeItems(q_mara_fang, -1);
                        htmltext = "warehouse_chief_moke_q0337_03.htm";
                        if (GetMemoState == 40001)
                            st.setMemoState("audience_with_earth_dragon", String.valueOf(50000), true);
                        else
                            st.setMemoState("audience_with_earth_dragon", String.valueOf(40010), true);
                    } else if (GetMemoState == 40010)
                        htmltext = "warehouse_chief_moke_q0337_04.htm";
                    else if (GetMemoState >= 50000)
                        htmltext = "warehouse_chief_moke_q0337_05.htm";
                } else if (npcId == blacksmith_helton) {
                    if ((GetMemoState == 40000 || GetMemoState == 40010) && (st.ownItemCount(q_frag_of_abyss_jewel2) == 0 || st.ownItemCount(q_musfel_fang) == 0))
                        htmltext = "blacksmith_helton_q0337_01.htm";
                    else if ((GetMemoState == 40000 || GetMemoState == 40010) && (st.ownItemCount(q_frag_of_abyss_jewel2) >= 1 || st.ownItemCount(q_musfel_fang) >= 1)) {
                        st.giveItems(q_mark_of_watchers, 1);
                        st.takeItems(q_frag_of_abyss_jewel2, -1);
                        st.takeItems(q_musfel_fang, -1);
                        htmltext = "blacksmith_helton_q0337_02.htm";
                        if (GetMemoState == 40010)
                            st.setMemoState("audience_with_earth_dragon", String.valueOf(50000), true);
                        else
                            st.setMemoState("audience_with_earth_dragon", String.valueOf(40001), true);
                    } else if (GetMemoState == 40001)
                        htmltext = "blacksmith_helton_q0337_03.htm";
                    else if (GetMemoState >= 50000)
                        htmltext = "blacksmith_helton_q0337_04.htm";
                } else if (npcId == watcher_antaras_gilmore) {
                    if (GetMemoState < 60000)
                        htmltext = "watcher_antaras_gilmore_q0337_01.htm";
                    else if (GetMemoState == 60000)
                        htmltext = "watcher_antaras_gilmore_q0337_02.htm";
                    else if (GetMemoState == 70000)
                        if (st.ownItemCount(q_frag_of_abyss_jewel3) >= 1)
                            htmltext = "watcher_antaras_gilmore_q0337_05.htm";
                        else
                            htmltext = "watcher_antaras_gilmore_q0337_04.htm";
                } else if (npcId == watcher_antaras_theodric) {
                    if (GetMemoState < 60000)
                        htmltext = "watcher_antaras_theodric_q0337_01.htm";
                    else if (GetMemoState == 60000)
                        htmltext = "watcher_antaras_theodric_q0337_02.htm";
                    else if (GetMemoState == 70000 && st.ownItemCount(q_frag_of_abyss_jewel3) == 0)
                        htmltext = "watcher_antaras_theodric_q0337_03.htm";
                    else if (GetMemoState == 70000 && st.ownItemCount(q_frag_of_abyss_jewel3) >= 1)
                        htmltext = "watcher_antaras_theodric_q0337_04.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onAttack(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("audience_with_earth_dragon");
        int spawned_jewel_guardian_pyton = st.getInt("spawned_jewel_guardian_pyton");
        int spawned_jewel_guardian_mara = st.getInt("spawned_jewel_guardian_mara");
        int spawned_jewel_guardian_musfel = st.getInt("spawned_jewel_guardian_musfel");
        int npcId = npc.getNpcId();
        if (npcId == abyssal_jewel_3) {
            if (GetMemoState == 70000) {
                if (npc.getCurrentHp() < npc.getMaxHp() * 0.800000 && spawned_jewel_guardian_pyton == 0) {
                    List<NpcInstance> list = mobs.get("spawned_jewel_guardian_pyton");
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    list.add(st.addSpawn(jewel_guardian_pyton));
                    list.add(st.addSpawn(jewel_guardian_pyton));
                    list.add(st.addSpawn(jewel_guardian_pyton));
                    list.add(st.addSpawn(jewel_guardian_pyton));
                    st.startQuestTimer("233704", 1000 * 3 * 60, npc);
                    st.setMemoState("spawned_jewel_guardian_pyton", String.valueOf(1), true);
                }
                if (npc.getCurrentHp() < npc.getMaxHp() * 0.400000 && st.ownItemCount(q_frag_of_abyss_jewel3) == 0) {
                    st.giveItems(q_frag_of_abyss_jewel3, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
                if (npc.getCurrentHp() < npc.getMaxHp() * 0.100000) {
                    if (npc != null)
                        npc.deleteMe();
                }
            }
        } else if (npcId == abyssal_jewel_1) {
            if (GetMemoState == 40000 || GetMemoState == 40001) {
                if (npc.getCurrentHp() < npc.getMaxHp() * 0.800000 && spawned_jewel_guardian_mara == 0) {
                    List<NpcInstance> list = mobs.get("spawned_jewel_guardian_mara");
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    list.add(st.addSpawn(jewel_guardian_mara));
                    list.add(st.addSpawn(jewel_guardian_mara));
                    list.add(st.addSpawn(jewel_guardian_mara));
                    list.add(st.addSpawn(jewel_guardian_mara));
                    list.add(st.addSpawn(jewel_guardian_mara));
                    list.add(st.addSpawn(jewel_guardian_mara));
                    list.add(st.addSpawn(jewel_guardian_mara));
                    list.add(st.addSpawn(jewel_guardian_mara));
                    list.add(st.addSpawn(jewel_guardian_mara));
                    list.add(st.addSpawn(jewel_guardian_mara));
                    list.add(st.addSpawn(jewel_guardian_mara));
                    list.add(st.addSpawn(jewel_guardian_mara));
                    list.add(st.addSpawn(jewel_guardian_mara));
                    list.add(st.addSpawn(jewel_guardian_mara));
                    list.add(st.addSpawn(jewel_guardian_mara));
                    list.add(st.addSpawn(jewel_guardian_mara));
                    list.add(st.addSpawn(jewel_guardian_mara));
                    list.add(st.addSpawn(jewel_guardian_mara));
                    list.add(st.addSpawn(jewel_guardian_mara));
                    list.add(st.addSpawn(jewel_guardian_mara));
                    st.setMemoState("spawned_jewel_guardian_mara", String.valueOf(1), true);
                    st.startQuestTimer("233753", 1000 * 60 * 15, npc);
                }
                if (npc.getCurrentHp() < npc.getMaxHp() * 0.400000 && st.ownItemCount(q_frag_of_abyss_jewel1) == 0) {
                    st.giveItems(q_frag_of_abyss_jewel1, 1);
                    st.soundEffect(SOUND_ITEMGET);
                    st.startQuestTimer("233751", 1000 * 60 * 4, npc);
                }
                if (npc.getCurrentHp() < npc.getMaxHp() * 0.100000) {
                    if (npc != null)
                        npc.deleteMe();
                }
            }
        } else if (npcId == abyssal_jewel_2) {
            if (GetMemoState == 40000 || GetMemoState == 40001) {
                if (npc.getCurrentHp() < npc.getMaxHp() * 0.800000 && spawned_jewel_guardian_musfel == 0) {
                    List<NpcInstance> list = mobs.get("spawned_jewel_guardian_musfel");
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    list.add(st.addSpawn(jewel_guardian_musfel));
                    list.add(st.addSpawn(jewel_guardian_musfel));
                    list.add(st.addSpawn(jewel_guardian_musfel));
                    list.add(st.addSpawn(jewel_guardian_musfel));
                    list.add(st.addSpawn(jewel_guardian_musfel));
                    list.add(st.addSpawn(jewel_guardian_musfel));
                    list.add(st.addSpawn(jewel_guardian_musfel));
                    list.add(st.addSpawn(jewel_guardian_musfel));
                    list.add(st.addSpawn(jewel_guardian_musfel));
                    list.add(st.addSpawn(jewel_guardian_musfel));
                    list.add(st.addSpawn(jewel_guardian_musfel));
                    list.add(st.addSpawn(jewel_guardian_musfel));
                    list.add(st.addSpawn(jewel_guardian_musfel));
                    list.add(st.addSpawn(jewel_guardian_musfel));
                    list.add(st.addSpawn(jewel_guardian_musfel));
                    list.add(st.addSpawn(jewel_guardian_musfel));
                    list.add(st.addSpawn(jewel_guardian_musfel));
                    list.add(st.addSpawn(jewel_guardian_musfel));
                    list.add(st.addSpawn(jewel_guardian_musfel));
                    list.add(st.addSpawn(jewel_guardian_musfel));
                    st.setMemoState("spawned_jewel_guardian_musfel", String.valueOf(1), true);
                    st.startQuestTimer("233754", 1000 * 60 * 15, npc);
                }
                if (npc.getCurrentHp() < npc.getMaxHp() * 0.400000 && st.ownItemCount(q_frag_of_abyss_jewel2) == 0) {
                    st.giveItems(q_frag_of_abyss_jewel2, 1);
                    st.soundEffect(SOUND_ITEMGET);
                    st.startQuestTimer("233752", 1000 * 60 * 4, npc);
                }
                if (npc.getCurrentHp() < npc.getMaxHp() * 0.100000) {
                    if (npc != null)
                        npc.deleteMe();
                }
            }
        }
        return null;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("audience_with_earth_dragon");
        int spawned_harit_lizardman_zealot = st.getInt("spawned_harit_lizardman_zealot");
        int spawned_sacrifice_of_sacrificed = st.getInt("spawned_sacrifice_of_sacrificed");
        int npcId = npc.getNpcId();
        if (npcId == cave_maiden || npcId == cave_keeper || npcId == cave_keeper_hold || npcId == cave_maiden_hold) {
            if (GetMemoState == 70000 && st.ownItemCount(q_frag_of_abyss_jewel3) == 0) {
                if (Rnd.get(5) == 0) {
                    NpcInstance jewel_3 = st.addSpawn(abyssal_jewel_3);
                    st.startQuestTimer("233721", 1000 * 3 * 60, jewel_3);
                }
            }
        } else if (npcId == harit_lizardman_shaman || npcId == harit_lizardm_matriarch) {
            if ((GetMemoState == 21110 || GetMemoState == 21100 || GetMemoState == 21010 || GetMemoState == 21000 || GetMemoState == 20110 || GetMemoState == 20100 || GetMemoState == 20010 || GetMemoState == 20000) && st.ownItemCount(q_totem_of_earthdragon) == 0 && spawned_harit_lizardman_zealot == 0) {
                if (Rnd.get(5) == 0) {
                    List<NpcInstance> list = mobs.get("spawned_harit_lizardman_zealot");
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    list.add(st.addSpawn(harit_lizardman_zealot));
                    list.add(st.addSpawn(harit_lizardman_zealot));
                    list.add(st.addSpawn(harit_lizardman_zealot));
                    st.setMemoState("spawned_harit_lizardman_zealot", String.valueOf(1), true);
                    st.startQuestTimer("233731", 180000, npc);
                }
            }
        } else if (npcId == harit_lizardman_zealot) {
            if ((GetMemoState == 21110 || GetMemoState == 21100 || GetMemoState == 21010 || GetMemoState == 21000 || GetMemoState == 20110 || GetMemoState == 20100 || GetMemoState == 20010 || GetMemoState == 20000) && st.ownItemCount(q_totem_of_earthdragon) == 0) {
                st.giveItems(q_totem_of_earthdragon, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == hamrut) {
            if ((GetMemoState == 21101 || GetMemoState == 21100 || GetMemoState == 21001 || GetMemoState == 21000 || GetMemoState == 20101 || GetMemoState == 20100 || GetMemoState == 20001 || GetMemoState == 20000) && st.ownItemCount(q_hamrut_leg) == 0) {
                st.giveItems(q_hamrut_leg, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == kranrot) {
            if ((GetMemoState == 21101 || GetMemoState == 21100 || GetMemoState == 21001 || GetMemoState == 21000 || GetMemoState == 20101 || GetMemoState == 20100 || GetMemoState == 20001 || GetMemoState == 20000) && st.ownItemCount(q_kranrot_skin) == 0) {
                st.giveItems(q_kranrot_skin, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == marsh_stalker) {
            if ((GetMemoState == 20111 || GetMemoState == 20110 || GetMemoState == 20101 || GetMemoState == 20100 || GetMemoState == 20011 || GetMemoState == 20010 || GetMemoState == 20001 || GetMemoState == 20000) && st.ownItemCount(q_marsh_stalker_horn) == 0) {
                st.giveItems(q_marsh_stalker_horn, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == marsh_drake) {
            if ((GetMemoState == 20111 || GetMemoState == 20110 || GetMemoState == 20101 || GetMemoState == 20100 || GetMemoState == 20011 || GetMemoState == 20010 || GetMemoState == 20001 || GetMemoState == 20000) && st.ownItemCount(q_marsh_drake_talon) == 0) {
                st.giveItems(q_marsh_drake_talon, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == jewel_guardian_musfel) {
            if ((GetMemoState == 40000 || GetMemoState == 40010) && st.ownItemCount(q_musfel_fang) == 0) {
                st.giveItems(q_musfel_fang, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == jewel_guardian_mara) {
            if ((GetMemoState == 40000 || GetMemoState == 40010) && st.ownItemCount(q_mara_fang) == 0) {
                st.giveItems(q_mara_fang, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == sacrifice_of_sacrificed) {
            if ((GetMemoState == 21011 || GetMemoState == 21010 || GetMemoState == 21001 || GetMemoState == 21000 || GetMemoState == 20011 || GetMemoState == 20010 || GetMemoState == 20001 || GetMemoState == 20000) && st.ownItemCount(q_remains_of_sacrificed) == 0) {
                st.giveItems(q_remains_of_sacrificed, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == bloody_queen) {
            if ((GetMemoState == 21011 || GetMemoState == 21010 || GetMemoState == 21001 || GetMemoState == 21000 || GetMemoState == 20011 || GetMemoState == 20010 || GetMemoState == 20001 || GetMemoState == 20000) && st.ownItemCount(q_remains_of_sacrificed) == 0 && spawned_sacrifice_of_sacrificed == 0) {
                List<NpcInstance> list = mobs.get("spawned_sacrifice_of_sacrificed");
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(st.addSpawn(sacrifice_of_sacrificed));
                list.add(st.addSpawn(sacrifice_of_sacrificed));
                list.add(st.addSpawn(sacrifice_of_sacrificed));
                list.add(st.addSpawn(sacrifice_of_sacrificed));
                list.add(st.addSpawn(sacrifice_of_sacrificed));
                list.add(st.addSpawn(sacrifice_of_sacrificed));
                list.add(st.addSpawn(sacrifice_of_sacrificed));
                list.add(st.addSpawn(sacrifice_of_sacrificed));
                st.setMemoState("spawned_sacrifice_of_sacrificed", String.valueOf(1), true);
                st.startQuestTimer("233701", 1000 * 3 * 60, npc);
            }
        }
        return null;
    }
}