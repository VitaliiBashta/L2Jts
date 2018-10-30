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
 * @version 1.0
 * @date 11/12/2014
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _129_PailakaDevilsLegacy extends Quest {
    // npc
    private static final int pa61_intro_npc = 32498;
    private static final int pa61_start_npc = 32501;
    private static final int pa61_enchant_npc = 32508;
    private static final int pa61_reward_npc = 32511;
    private static final int pa61_treasure = 32495;
    // mobs
    private static final int pa61_keym_kams = 18629;
    private static final int pa61_keym_alpaso = 18631;
    private static final int rematan = 18633;
    // etcitem
    private static final int instant_defence = 13032;
    private static final int instant_posion_heal = 13033;
    private static final int pa61_poison_break = 13048;
    private static final int pa61_holy_weapon = 13049;
    private static final int pa73_long_dd_defence = 13059;
    private static final int pa61_key_item = 13150;
    // weapon
    private static final int pa61_ancient_sword = 13042;
    private static final int pa61_ancient_sword_10 = 13043;
    private static final int pa61_ancient_sword_20 = 13044;
    // questitem
    private static final int pa61_sword_up_first = 13046;
    private static final int pa61_sword_up_second = 13047;
    // reward
    private static final int scroll_of_escape = 736;
    private static final int pa_pailaka_bracelet_61 = 13295;
    // pa61_zone_controller
    private static final int inzone_id = 44;

    public _129_PailakaDevilsLegacy() {
        super(false);
        addStartNpc(pa61_intro_npc);
        addTalkId(pa61_start_npc, pa61_enchant_npc, pa61_reward_npc);
        addKillId(pa61_keym_kams, pa61_keym_alpaso, rematan, pa61_treasure);
        addQuestItem(instant_defence, instant_posion_heal, pa61_poison_break, pa61_holy_weapon, pa73_long_dd_defence, pa61_key_item, pa61_ancient_sword, pa61_ancient_sword_10, pa61_ancient_sword_20, pa61_sword_up_first, pa61_sword_up_second);
        addLevelCheck(61, 67);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("pairaka_relic_of_devils");
        int talker_level = st.getPlayer().getLevel();
        int npcId = npc.getNpcId();
        if (npcId == pa61_intro_npc) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("pairaka_relic_of_devils", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "pa61_intro_npc_q0129_07.htm";
            } else if (event.equalsIgnoreCase("reply_1")) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        if (talker_level < 61) {
                            htmltext = "pa61_intro_npc_q0129_03.htm";
                            st.exitQuest(true);
                        }
                        if (talker_level >= 67) {
                            htmltext = "pa61_intro_npc_q0129_04z.htm";
                            st.exitQuest(true);
                        }
                        break;
                    default:
                        htmltext = "pa61_intro_npc_q0129_04.htm";
                        break;
                }
            } else if (event.equalsIgnoreCase("reply_2"))
                htmltext = "pa61_intro_npc_q0129_06.htm";
            else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState <= 1) {
                    st.setCond(2);
                    st.setMemoState("pairaka_relic_of_devils", String.valueOf(2), true);
                    st.soundEffect(SOUND_MIDDLE);
                    InstantZone_Enter(st.getPlayer());
                    htmltext = "pa61_intro_npc_q0129_09.htm";
                } else
                    htmltext = "pa61_intro_npc_q0129_11.htm";
            }
        } else if (npcId == pa61_start_npc) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 2) {
                    st.setCond(3);
                    st.setMemoState("pairaka_relic_of_devils", String.valueOf(3), true);
                    st.giveItems(pa61_ancient_sword, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "pa61_start_npc_q0129_03.htm";
                }
            }
        } else if (npcId == pa61_enchant_npc) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (st.getPlayer().getServitor() != null)
                    htmltext = "pa61_enchant_npc_q0129_03a.htm";
                else if (st.ownItemCount(pa61_ancient_sword) >= 1 && st.ownItemCount(pa61_sword_up_first) >= 1) {
                    st.takeItems(pa61_ancient_sword, -1);
                    st.takeItems(pa61_sword_up_first, -1);
                    st.giveItems(pa61_ancient_sword_10, 1);
                    htmltext = "pa61_enchant_npc_q0129_02.htm";
                } else if (st.ownItemCount(pa61_ancient_sword_10) >= 1 && st.ownItemCount(pa61_sword_up_second) >= 1) {
                    st.takeItems(pa61_ancient_sword_10, -1);
                    st.takeItems(pa61_sword_up_second, -1);
                    st.giveItems(pa61_ancient_sword_20, 1);
                    htmltext = "pa61_enchant_npc_q0129_03.htm";
                } else if (st.ownItemCount(pa61_ancient_sword_10) >= 1 && st.ownItemCount(pa61_sword_up_second) == 0)
                    htmltext = "pa61_enchant_npc_q0129_04.htm";
                else if (st.ownItemCount(pa61_ancient_sword) >= 1 && st.ownItemCount(pa61_sword_up_first) == 0)
                    htmltext = "pa61_enchant_npc_q0129_05.htm";
                else if (st.ownItemCount(pa61_ancient_sword_20) >= 1)
                    htmltext = "pa61_enchant_npc_q0129_06.htm";
                else if (st.ownItemCount(pa61_ancient_sword) == 0)
                    htmltext = "pa61_enchant_npc_q0129_01a.htm";
            }
        } else if (npcId == pa61_reward_npc) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (st.getPlayer().getServitor() != null)
                    htmltext = "pa61_reward_npc_q0129_02a.htm";
                else if (GetMemoState == 4) {
                    st.giveItems(pa_pailaka_bracelet_61, 1);
                    st.giveItems(scroll_of_escape, 1);
                    st.addExpAndSp(10800000, 950000);
                    st.getPlayer().broadcastPacket(new MagicSkillUse(npc, st.getPlayer(), 5774, 2, 0, 0));
                    st.removeMemo("pairaka_relic_of_devils");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "pa61_reward_npc_q0129_02z.htm";
                    st.getPlayer().getReflection().startCollapseTimer(300000);
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("pairaka_relic_of_devils");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == pa61_intro_npc)
                    htmltext = "pa61_intro_npc_q0129_01.htm";
                break;
            case STARTED:
                if (npcId == pa61_intro_npc) {
                    if (GetMemoState == 1)
                        htmltext = "pa61_intro_npc_q0129_08.htm";
                    else if (GetMemoState > 1)
                        htmltext = "pa61_intro_npc_q0129_10.htm";
                } else if (npcId == pa61_start_npc) {
                    if (GetMemoState == 2)
                        htmltext = "pa61_start_npc_q0129_01.htm";
                    else if (GetMemoState > 2)
                        htmltext = "pa61_start_npc_q0129_04.htm";
                }
                break;
            case COMPLETED:
                if (npcId == pa61_intro_npc)
                    htmltext = "pa61_intro_npc_q0129_02.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("pairaka_relic_of_devils");
        int refId = st.getPlayer().getReflectionId();
        int npcId = npc.getNpcId();
        if (npcId == pa61_keym_kams) {
            st.giveItems(pa61_sword_up_first, 1);
        } else if (npcId == pa61_keym_alpaso) {
            st.giveItems(pa61_sword_up_second, 1);
        } else if (npcId == rematan) {
            if (GetMemoState == 3) {
                st.setCond(4);
                st.setMemoState("pairaka_relic_of_devils", String.valueOf(4), true);
                st.soundEffect(SOUND_MIDDLE);
                addSpawnToInstance(pa61_reward_npc, new Location(84983, -208736, -3328, 49915), 0, refId);
            }
        } else if (npcId == pa61_treasure) {
            switch (Rnd.get(7)) {
                case 0:
                case 1:
                    st.dropItem(npc, pa61_poison_break, Rnd.get(10) + 1);
                    break;
                case 2:
                    st.dropItem(npc, pa61_holy_weapon, Rnd.get(5) + 1);
                    break;
                case 3:
                    st.dropItem(npc, pa61_key_item, Rnd.get(1) + 1);
                    break;
                case 4:
                    st.dropItem(npc, pa73_long_dd_defence, Rnd.get(7) + 1);
                    break;
                case 5:
                    st.dropItem(npc, instant_defence, Rnd.get(10) + 1);
                    break;
                case 6:
                    st.dropItem(npc, instant_posion_heal, Rnd.get(10) + 1);
                    break;
            }
        }
        return null;
    }

    private void InstantZone_Enter(Player player) {
        ReflectionUtils.simpleEnterInstancedZone(player, inzone_id);
    }
}