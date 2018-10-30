package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.serverpackets.MagicSkillUse;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.ReflectionUtils;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.2
 * @date 07/12/2014
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _128_PailakaSongofIceandFire extends Quest {
    // npc
    private static final int pa36_crystal_pint = 32492;
    private static final int pa36_blazing_brazier = 32493;
    private static final int pa36_intro_npc = 32497;
    private static final int pa36_start_npc = 32500;
    private static final int pa36_enchant_npc = 32507;
    private static final int pa36_reward_npc = 32510;
    // mobs
    private static final int pa36_keym_gargos = 18607;
    private static final int pa36_keym_kinsus = 18608;
    private static final int pa36_keym_pavion = 18609;
    private static final int pa36_keym_healas = 18610;
    private static final int pa36_fish = 18616;
    private static final int adiantum = 18620;
    // weapon
    private static final int pa36_wf_sword = 13034;
    private static final int pa36_wf_sword_10 = 13035;
    private static final int pa36_wf_sword_20 = 13036;
    // questitem
    private static final int pa36_sword_up_first = 13038;
    private static final int pa36_sword_up_second = 13039;
    // etcitem
    private static final int instant_defence = 13032;
    private static final int instant_posion_heal = 13033;
    private static final int pa36_poison_fire = 13040;
    private static final int pa36_poison_water = 13041;
    private static final int pa36_secret_book_1 = 13130;
    private static final int pa36_secret_book_2 = 13131;
    private static final int pa36_secret_book_3 = 13132;
    private static final int pa36_secret_book_4 = 13133;
    private static final int pa36_secret_book_5 = 13134;
    private static final int pa36_secret_book_6 = 13135;
    private static final int pa36_secret_book_7 = 13136;
    // reward
    private static final int scroll_of_escape = 736;
    private static final int pa_pailaka_earring_36 = 13293;
    private static final int pa_pailaka_ring_36 = 13294;
    // pa36_zone_controller
    private static final int inzone_id = 43;

    public _128_PailakaSongofIceandFire() {
        super(false);
        addStartNpc(pa36_intro_npc);
        addTalkId(pa36_start_npc, pa36_enchant_npc, pa36_reward_npc);
        addKillId(pa36_keym_gargos, pa36_keym_kinsus, pa36_keym_pavion, pa36_keym_healas, adiantum, pa36_fish, pa36_crystal_pint, pa36_blazing_brazier);
        addQuestItem(pa36_wf_sword, pa36_wf_sword_10, pa36_wf_sword_20, pa36_sword_up_first, pa36_sword_up_second, instant_defence, instant_posion_heal, pa36_poison_fire, pa36_poison_water, pa36_secret_book_1, pa36_secret_book_2, pa36_secret_book_3, pa36_secret_book_4, pa36_secret_book_5, pa36_secret_book_6, pa36_secret_book_7);
        addLevelCheck(36, 42);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("pairaka_fire_and_ice");
        int talker_level = st.getPlayer().getLevel();
        int npcId = npc.getNpcId();
        if (npcId == pa36_intro_npc) {
            if (event.equalsIgnoreCase("quest_accept")) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        if (talker_level < 36) {
                            htmltext = "pa36_intro_npc_q0128_05.htm";
                            st.exitQuest(true);
                        }
                        if (talker_level > 42) {
                            htmltext = "pa36_intro_npc_q0128_05z.htm";
                            st.exitQuest(true);
                        }
                        break;
                    default:
                        st.setCond(1);
                        st.setMemoState("pairaka_fire_and_ice", String.valueOf(1), true);
                        st.setState(STARTED);
                        st.soundEffect(SOUND_ACCEPT);
                        htmltext = "pa36_intro_npc_q0128_04.htm";
                        break;
                }
            } else if (event.equalsIgnoreCase("reply_1"))
                htmltext = "pa36_intro_npc_q0128_02a.htm";
            else if (event.equalsIgnoreCase("reply_2")) {
                InstantZone_Enter(st.getPlayer());
                htmltext = "pa36_intro_npc_q0128_06a.htm";
            }
        } else if (npcId == pa36_start_npc) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 1)
                    htmltext = "pa36_start_npc_q0128_05.htm";
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 1) {
                    st.setCond(2);
                    st.setMemoState("pairaka_fire_and_ice", String.valueOf(2), true);
                    st.giveItems(pa36_wf_sword, 1);
                    st.giveItems(pa36_secret_book_1, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "pa36_start_npc_q0128_06.htm";
                }
            }
        } else if (npcId == pa36_enchant_npc) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 3 && st.ownItemCount(pa36_wf_sword) >= 1 && st.ownItemCount(pa36_sword_up_first) >= 1) {
                    st.setCond(4);
                    st.setMemoState("pairaka_fire_and_ice", String.valueOf(4), true);
                    st.giveItems(pa36_wf_sword_10, 1);
                    st.giveItems(pa36_secret_book_3, 1);
                    st.takeItems(pa36_wf_sword, -1);
                    st.takeItems(pa36_sword_up_first, -1);
                    st.takeItems(pa36_secret_book_2, -1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "pa36_enchant_npc_q0128_03.htm";
                } else if (GetMemoState == 3 && (st.ownItemCount(pa36_wf_sword) < 1 || st.ownItemCount(pa36_sword_up_first) < 1))
                    htmltext = "pa36_enchant_npc_q0128_04.htm";
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 6 && st.ownItemCount(pa36_wf_sword_10) >= 1 && st.ownItemCount(pa36_sword_up_second) >= 1)
                    htmltext = "pa36_enchant_npc_q0128_07.htm";
                else if (GetMemoState == 6 && (st.ownItemCount(pa36_wf_sword_10) < 1 || st.ownItemCount(pa36_sword_up_second) < 1))
                    htmltext = "pa36_enchant_npc_q0128_04.htm";
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 6 && st.ownItemCount(pa36_wf_sword_10) >= 1 && st.ownItemCount(pa36_sword_up_second) >= 1) {
                    st.setCond(7);
                    st.setMemoState("pairaka_fire_and_ice", String.valueOf(7), true);
                    st.giveItems(pa36_wf_sword_20, 1);
                    st.giveItems(pa36_secret_book_6, 1);
                    st.takeItems(pa36_wf_sword_10, -1);
                    st.takeItems(pa36_sword_up_second, -1);
                    st.takeItems(pa36_secret_book_5, -1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "pa36_enchant_npc_q0128_09.htm";
                } else if (GetMemoState == 6 && (st.ownItemCount(pa36_wf_sword_10) < 1 || st.ownItemCount(pa36_sword_up_second) < 1))
                    htmltext = "pa36_enchant_npc_q0128_04.htm";
            }
        } else if (npcId == pa36_reward_npc) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 9) {
                    st.giveItems(pa_pailaka_earring_36, 1);
                    st.giveItems(pa_pailaka_ring_36, 1);
                    st.giveItems(scroll_of_escape, 1);
                    st.addExpAndSp(810000, 50000);
                    st.getPlayer().broadcastPacket(new MagicSkillUse(npc, st.getPlayer(), 5774, 1, 0, 0));
                    st.getPlayer().getReflection().startCollapseTimer(300000);
                    st.soundEffect(SOUND_FINISH);
                    st.removeMemo("pairaka_fire_and_ice");
                    st.exitQuest(false);
                    htmltext = "pa36_reward_npc_q0128_02z.htm";
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("pairaka_fire_and_ice");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == pa36_intro_npc)
                    htmltext = "pa36_intro_npc_q0128_02.htm";
                break;
            case STARTED:
                if (npcId == pa36_intro_npc) {
                    if (GetMemoState == 1)
                        htmltext = "pa36_intro_npc_q0128_06.htm";
                    else if (GetMemoState >= 2)
                        htmltext = "pa36_intro_npc_q0128_07.htm";
                } else if (npcId == pa36_start_npc) {
                    if (GetMemoState == 1)
                        htmltext = "pa36_start_npc_q0128_01.htm";
                    else if (GetMemoState >= 2)
                        htmltext = "pa36_start_npc_q0128_01a.htm";
                } else if (npcId == pa36_enchant_npc) {
                    if (GetMemoState < 3 && st.ownItemCount(pa36_wf_sword) == 0)
                        htmltext = "pa36_enchant_npc_q0128_01a.htm";
                    else if (GetMemoState < 3 && st.ownItemCount(pa36_wf_sword) >= 1 && st.ownItemCount(pa36_sword_up_first) == 0)
                        htmltext = "pa36_enchant_npc_q0128_01.htm";
                    else if (GetMemoState == 3 && st.ownItemCount(pa36_wf_sword) >= 1 && st.ownItemCount(pa36_sword_up_first) >= 1)
                        htmltext = "pa36_enchant_npc_q0128_02.htm";
                    else if (GetMemoState >= 4 && GetMemoState < 6)
                        htmltext = "pa36_enchant_npc_q0128_05.htm";
                    else if (GetMemoState == 6)
                        htmltext = "pa36_enchant_npc_q0128_06.htm";
                    else if (GetMemoState >= 7)
                        htmltext = "pa36_enchant_npc_q0128_06a.htm";
                } else if (npcId == pa36_reward_npc) {
                    if (GetMemoState == 9)
                        htmltext = "pa36_reward_npc_q0128_01.htm";
                }
                break;
            case COMPLETED:
                if (npcId == pa36_intro_npc)
                    htmltext = "pa36_intro_npc_q0128_02b.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("pairaka_fire_and_ice");
        int refId = st.getPlayer().getReflectionId();
        int npcId = npc.getNpcId();
        if (npcId == pa36_crystal_pint || npcId == pa36_blazing_brazier) {
            switch (Rnd.get(6)) {
                case 0:
                    if (npcId == pa36_crystal_pint)
                        st.dropItem(npc, pa36_poison_water, Rnd.get(7) + 1);
                    break;
                case 1:
                    if (npcId == pa36_blazing_brazier)
                        st.dropItem(npc, pa36_poison_fire, Rnd.get(7) + 1);
                    break;
                case 2:
                case 3:
                    st.dropItem(npc, instant_defence, Rnd.get(10) + 1);
                    break;
                case 4:
                case 5:
                    st.dropItem(npc, instant_posion_heal, Rnd.get(10) + 1);
                    break;
            }
        } else if (npcId == pa36_fish) {
            if (Rnd.get(2) < 1)
                st.dropItem(npc, instant_defence, Rnd.get(7) + 1);
            else
                st.dropItem(npc, instant_posion_heal, Rnd.get(7) + 1);
        } else if (npcId == pa36_keym_gargos) {
            if (GetMemoState == 7) {
                st.setCond(8);
                st.setMemoState("pairaka_fire_and_ice", String.valueOf(8), true);
                st.giveItems(pa36_secret_book_7, 1);
                st.takeItems(pa36_secret_book_6, -1);
                st.soundEffect(SOUND_MIDDLE);
                addSpawnToInstance(adiantum, new Location(-53168, 184996, -4656, 0), 0, refId);
            }
        } else if (npcId == pa36_keym_kinsus) {
            if (GetMemoState == 5) {
                st.setCond(6);
                st.setMemoState("pairaka_fire_and_ice", String.valueOf(6), true);
                st.giveItems(pa36_secret_book_5, 1);
                st.giveItems(pa36_sword_up_second, 1);
                st.takeItems(pa36_secret_book_4, -1);
                st.soundEffect(SOUND_MIDDLE);
            }
        } else if (npcId == pa36_keym_pavion) {
            if (GetMemoState == 4) {
                st.setCond(5);
                st.setMemoState("pairaka_fire_and_ice", String.valueOf(5), true);
                st.giveItems(pa36_secret_book_4, 1);
                st.takeItems(pa36_secret_book_3, -1);
                st.soundEffect(SOUND_MIDDLE);
            }
        } else if (npcId == pa36_keym_healas) {
            if (GetMemoState == 2) {
                st.setCond(3);
                st.setMemoState("pairaka_fire_and_ice", String.valueOf(3), true);
                st.giveItems(pa36_secret_book_2, 1);
                st.giveItems(pa36_sword_up_first, 1);
                st.takeItems(pa36_secret_book_1, -1);
                st.soundEffect(SOUND_MIDDLE);
            }
        } else if (npcId == adiantum) {
            if (GetMemoState == 8) {
                st.setCond(9);
                st.setMemoState("pairaka_fire_and_ice", String.valueOf(9), true);
                st.takeItems(pa36_secret_book_7, -1);
                st.soundEffect(SOUND_MIDDLE);
                addSpawnToInstance(pa36_reward_npc, new Location(npc.getX(), npc.getY(), npc.getZ(), npc.getHeading()), 0, refId);
            }
        }
        return null;
    }

    private void InstantZone_Enter(Player player) {
        ReflectionUtils.simpleEnterInstancedZone(player, inzone_id);
    }
}