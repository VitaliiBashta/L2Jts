package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.ReflectionUtils;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _10270_BirthOfTheSeed extends Quest {
    // npc
    private static final int wharf_soldier_plenos = 32563;
    private static final int warmage_artius = 32559;
    private static final int silen_priest_relrikia = 32567;
    private static final int soldier_jinbi = 32566;

    // questitem
    private static final int q_badge_clodecus = 13868;
    private static final int q_badge_clanicus = 13869;
    private static final int q_lich_crystal = 13870;

    // MOB's
    private static final int is1_clodecus = 25665;
    private static final int is1_clanicus = 25666;
    private static final int sboss_cohemenes = 25634;

    // zone_controller
    final int inzone_id = 117;

    public _10270_BirthOfTheSeed() {
        super(false);
        addStartNpc(wharf_soldier_plenos);
        addTalkId(warmage_artius, soldier_jinbi, silen_priest_relrikia);
        addKillId(is1_clodecus, is1_clanicus, sboss_cohemenes);
        addQuestItem(q_badge_clodecus, q_badge_clanicus, q_lich_crystal);
        addLevelCheck(75);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("pregnancy_of_seed");
        int npcId = npc.getNpcId();

        if (npcId == wharf_soldier_plenos) {
            if (event.equalsIgnoreCase("reply_1"))
                htmltext = "wharf_soldier_plenos_q10270_04.htm";
            else if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("pregnancy_of_seed", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "wharf_soldier_plenos_q10270_05.htm";
            }
        } else if (npcId == warmage_artius) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 1) {
                    st.setCond(2);
                    st.setMemoState("pregnancy_of_seed", String.valueOf(2), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "warmage_artius_q10270_03.htm";
                }
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 3 && (st.get("broken_piece_of_light") != null && st.getInt("broken_piece_of_light") < 10 || st.get("broken_piece_of_light") == null)) {
                    st.setCond(4);
                    st.setMemoState("pregnancy_of_seed", String.valueOf(4), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "warmage_artius_q10270_09.htm";
                }
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 3 && (st.get("broken_piece_of_light") != null && st.getInt("broken_piece_of_light") >= 10 || st.getPlayer().getQuestState(10272).isCompleted())) {
                    st.setCond(4);
                    st.setMemoState("pregnancy_of_seed", String.valueOf(4), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "warmage_artius_q10270_10.htm";
                }
            } else if (event.equalsIgnoreCase("reply_10"))
                if (GetMemoState == 20) {
                    st.giveItems(ADENA_ID, 133590);
                    st.addExpAndSp(625343, 48222);
                    st.removeMemo("pregnancy_of_seed");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "warmage_artius_q10270_13.htm";
                }
        } else if (npcId == soldier_jinbi) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 4 && (st.get("broken_piece_of_light") != null && st.getInt("broken_piece_of_light") < 10 || st.get("broken_piece_of_light") == null))
                    htmltext = "soldier_jinbi_q10270_03.htm";
                else if (GetMemoState == 4 && (st.get("broken_piece_of_light") != null && st.getInt("broken_piece_of_light") >= 10 || st.getPlayer().getQuestState(10272).isCompleted()))
                    htmltext = "soldier_jinbi_q10270_04.htm";
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (st.ownItemCount(ADENA_ID) < 10000) {
                    if (GetMemoState == 4)
                        htmltext = "soldier_jinbi_q10270_04a.htm";
                } else if (GetMemoState == 4) {
                    st.takeItems(ADENA_ID, 10000);
                    st.setMemoState("pregnancy_of_seed", String.valueOf(5), true);
                    htmltext = "soldier_jinbi_q10270_05.htm";
                }
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 5)
                    htmltext = "soldier_jinbi_q10270_06.htm";
            } else if (event.equalsIgnoreCase("reply_4"))
                if (GetMemoState >= 5 && GetMemoState < 20) {
                    st.setMemoState("pregnancy_of_seed", String.valueOf(10), true);
                    InstantZone_Enter(st.getPlayer(), inzone_id);
                    htmltext = "soldier_jinbi_q10270_08.htm";
                }
        } else if (npcId == silen_priest_relrikia)
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 10)
                    htmltext = "silen_priest_relrikia_q10270_02.htm";
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 10)
                    htmltext = "silen_priest_relrikia_q10270_03.htm";
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 10) {
                    st.setCond(5);
                    st.setMemoState("pregnancy_of_seed", String.valueOf(11), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "silen_priest_relrikia_q10270_05.htm";
                }
            } else if (event.equalsIgnoreCase("reply_4"))
                if (GetMemoState == 11) {
                    st.setMemoState("pregnancy_of_seed", String.valueOf(20), true);
                    st.getPlayer().getReflection().collapse();
                    htmltext = "silen_priest_relrikia_q10270_07.htm";
                }

        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("pregnancy_of_seed");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == wharf_soldier_plenos) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "wharf_soldier_plenos_q10270_02.htm";
                            break;
                        default:
                            htmltext = "wharf_soldier_plenos_q10270_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == wharf_soldier_plenos) {
                    if (GetMemoState == 1)
                        htmltext = "wharf_soldier_plenos_q10270_06.htm";
                } else if (npcId == warmage_artius) {
                    if (GetMemoState == 1)
                        htmltext = "warmage_artius_q10270_01.htm";
                    else if (GetMemoState == 2 && st.ownItemCount(q_badge_clodecus) < 1 && st.ownItemCount(q_badge_clanicus) < 1 && st.ownItemCount(q_lich_crystal) < 1)
                        htmltext = "warmage_artius_q10270_04.htm";
                    else if (GetMemoState == 2 && (st.ownItemCount(q_badge_clodecus) < 1 || st.ownItemCount(q_badge_clanicus) < 1 || st.ownItemCount(q_lich_crystal) < 1))
                        htmltext = "warmage_artius_q10270_05.htm";
                    else if (GetMemoState == 2 && st.ownItemCount(q_badge_clodecus) == 1 && st.ownItemCount(q_badge_clanicus) == 1 && st.ownItemCount(q_lich_crystal) == 1) {
                        st.setCond(3);
                        st.setMemoState("pregnancy_of_seed", String.valueOf(3), true);
                        st.takeItems(q_badge_clodecus, -1);
                        st.takeItems(q_badge_clanicus, -1);
                        st.takeItems(q_lich_crystal, -1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "warmage_artius_q10270_06.htm";
                    } else if (GetMemoState == 3 && (st.get("broken_piece_of_light") != null && st.getInt("broken_piece_of_light") < 10 || st.get("broken_piece_of_light") == null))
                        htmltext = "warmage_artius_q10270_07.htm";
                    else if (GetMemoState == 3 && (st.get("broken_piece_of_light") != null && st.getInt("broken_piece_of_light") >= 10 || st.getPlayer().getQuestState(10272).isCompleted()))
                        htmltext = "warmage_artius_q10270_08.htm";
                    else if (GetMemoState == 4)
                        htmltext = "warmage_artius_q10270_11.htm";
                    else if (GetMemoState == 20)
                        htmltext = "warmage_artius_q10270_12.htm";
                } else if (npcId == soldier_jinbi) {
                    if (GetMemoState == 4)
                        htmltext = "soldier_jinbi_q10270_01.htm";
                    else if (GetMemoState < 4)
                        htmltext = "soldier_jinbi_q10270_02.htm";
                    else if (GetMemoState == 5)
                        htmltext = "soldier_jinbi_q10270_07.htm";
                    else if (GetMemoState >= 10 && GetMemoState < 20)
                        htmltext = "soldier_jinbi_q10270_10.htm";
                    else if (GetMemoState == 20)
                        htmltext = "soldier_jinbi_q10270_12.htm";
                } else if (npcId == silen_priest_relrikia)
                    if (GetMemoState == 10)
                        htmltext = "silen_priest_relrikia_q10270_01.htm";
                    else if (GetMemoState == 11)
                        htmltext = "silen_priest_relrikia_q10270_06.htm";
                break;
            case COMPLETED:
                if (npcId == wharf_soldier_plenos)
                    htmltext = "wharf_soldier_plenos_q10270_03.htm";
                else if (npcId == warmage_artius)
                    htmltext = "warmage_artius_q10270_02.htm";
                break;
        }

        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("pregnancy_of_seed");
        int npcId = npc.getNpcId();

        if (GetMemoState == 2 && st.ownItemCount(q_badge_clodecus) < 1) {
            if (npcId == is1_clodecus) {
                st.giveItems(q_badge_clodecus, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (GetMemoState == 2 && st.ownItemCount(q_badge_clanicus) < 1) {
            if (npcId == is1_clanicus) {
                st.giveItems(q_badge_clanicus, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (GetMemoState == 2 && st.ownItemCount(q_lich_crystal) < 1)
            if (npcId == sboss_cohemenes) {
                st.giveItems(q_lich_crystal, 1);
                st.soundEffect(SOUND_ITEMGET);
            }

        return null;
    }

    private void InstantZone_Enter(Player player, int inzone_id) {
        ReflectionUtils.simpleEnterInstancedZone(player, inzone_id);
    }
}