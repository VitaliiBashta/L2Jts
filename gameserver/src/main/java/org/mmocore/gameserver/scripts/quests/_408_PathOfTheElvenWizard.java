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
 * @version 1.2
 * @date 04/10/2015
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _408_PathOfTheElvenWizard extends Quest {
    // npc
    private final static int rogellia = 30414;
    private final static int grain = 30157;
    private final static int thalya = 30371;
    private final static int northwindel = 30423;
    // mobs
    private final static int pincer_spider = 20466;
    private final static int dryad_ribe = 20019;
    private final static int sukar_wererat_leader = 20047;
    // questitem
    private final static int rogellias_letter = 1218;
    private final static int red_down = 1219;
    private final static int magical_powers_ruby = 1220;
    private final static int pure_aquamarine = 1221;
    private final static int appetizing_apple = 1222;
    private final static int gold_leaves = 1223;
    private final static int immortal_love = 1224;
    private final static int amethyst = 1225;
    private final static int nobility_amethyst = 1226;
    private final static int fertility_peridot = 1229;
    private final static int eternity_diamond = 1230;
    private final static int charm_of_grain = 1272;
    private final static int sap_of_world_tree = 1273;
    private final static int lucky_potpouri = 1274;

    public _408_PathOfTheElvenWizard() {
        super(false);
        addStartNpc(rogellia);
        addTalkId(grain, thalya, northwindel);
        addKillId(pincer_spider, dryad_ribe, sukar_wererat_leader);
        addQuestItem(rogellias_letter, red_down, magical_powers_ruby, pure_aquamarine, appetizing_apple, gold_leaves, immortal_love, amethyst, nobility_amethyst, fertility_peridot, charm_of_grain, sap_of_world_tree, lucky_potpouri);
        addLevelCheck(18);
        addClassIdCheck(ClassId.elven_mage);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        int talker_occupation = st.getPlayer().getPlayerClassComponent().getClassId().getId();
        int elven_mage = 0x19;
        int elven_wizard = 0x1a;
        if (npcId == rogellia) {
            if (event.equalsIgnoreCase("quest_accept")) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = "rogellia_q0408_04.htm";
                        st.exitQuest(true);
                        break;
                    case CLASS_ID:
                        if (talker_occupation != elven_mage) {
                            if (talker_occupation == elven_wizard) {
                                htmltext = "rogellia_q0408_02a.htm";
                                st.exitQuest(true);
                            } else {
                                htmltext = "rogellia_q0408_03.htm";
                                st.exitQuest(true);
                            }
                        }
                        break;
                    default:
                        if (st.ownItemCount(eternity_diamond) != 0)
                            htmltext = "rogellia_q0408_05.htm";
                        else {
                            st.setCond(1);
                            st.setState(STARTED);
                            st.soundEffect(SOUND_ACCEPT);
                            if (st.ownItemCount(fertility_peridot) == 0)
                                st.giveItems(fertility_peridot, 1);
                            htmltext = "rogellia_q0408_06.htm";
                        }
                        break;
                }
            } else if (event.equalsIgnoreCase("reply_1")) {
                if (st.ownItemCount(magical_powers_ruby) != 0)
                    htmltext = "rogellia_q0408_10.htm";
                else if (st.ownItemCount(magical_powers_ruby) == 0 && st.ownItemCount(fertility_peridot) != 0) {
                    if (st.ownItemCount(rogellias_letter) == 0)
                        st.giveItems(rogellias_letter, 1);
                    htmltext = "rogellia_q0408_07.htm";
                }
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (st.ownItemCount(pure_aquamarine) != 0)
                    htmltext = "rogellia_q0408_13.htm";
                else if (st.ownItemCount(pure_aquamarine) == 0 && st.ownItemCount(fertility_peridot) != 0) {
                    if (st.ownItemCount(appetizing_apple) == 0)
                        st.giveItems(appetizing_apple, 1);
                    htmltext = "rogellia_q0408_14.htm";
                }
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (st.ownItemCount(nobility_amethyst) != 0)
                    htmltext = "rogellia_q0408_17.htm";
                else if (st.ownItemCount(nobility_amethyst) == 0 && st.ownItemCount(fertility_peridot) != 0) {
                    if (st.ownItemCount(immortal_love) == 0)
                        st.giveItems(immortal_love, 1);
                    htmltext = "rogellia_q0408_18.htm";
                }
            }
        } else if (npcId == grain) {
            if (event.equalsIgnoreCase("reply_4")) {
                if (st.ownItemCount(rogellias_letter) != 0) {
                    st.takeItems(rogellias_letter, -1);
                    if (st.ownItemCount(charm_of_grain) == 0)
                        st.giveItems(charm_of_grain, 1);
                    htmltext = "grain_q0408_02.htm";
                }
            }
        } else if (npcId == thalya) {
            if (event.equalsIgnoreCase("reply_5")) {
                if (st.ownItemCount(appetizing_apple) != 0) {
                    st.takeItems(appetizing_apple, -1);
                    if (st.ownItemCount(sap_of_world_tree) == 0)
                        st.giveItems(sap_of_world_tree, 1);
                    htmltext = "thalya_q0408_02.htm";
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
                if (npcId == rogellia)
                    htmltext = "rogellia_q0408_01.htm";
                break;
            case STARTED:
                if (npcId == rogellia) {
                    if (st.ownItemCount(rogellias_letter) == 0 && st.ownItemCount(appetizing_apple) == 0 && st.ownItemCount(immortal_love) == 0 && st.ownItemCount(charm_of_grain) == 0 && st.ownItemCount(sap_of_world_tree) == 0 && st.ownItemCount(lucky_potpouri) == 0 && st.ownItemCount(fertility_peridot) != 0 && (st.ownItemCount(magical_powers_ruby) == 0 || st.ownItemCount(nobility_amethyst) == 0 || st.ownItemCount(pure_aquamarine) == 0))
                        htmltext = "rogellia_q0408_11.htm";
                    else if (st.ownItemCount(rogellias_letter) != 0)
                        htmltext = "rogellia_q0408_08.htm";
                    else if (st.ownItemCount(charm_of_grain) != 0 && st.ownItemCount(red_down) < 5)
                        htmltext = "rogellia_q0408_09.htm";
                    else if (st.ownItemCount(charm_of_grain) != 0 && st.ownItemCount(red_down) >= 5)
                        htmltext = "rogellia_q0408_25.htm";
                    else if (st.ownItemCount(appetizing_apple) != 0)
                        htmltext = "rogellia_q0408_15.htm";
                    else if (st.ownItemCount(sap_of_world_tree) != 0 && st.ownItemCount(gold_leaves) < 5)
                        htmltext = "rogellia_q0408_16.htm";
                    else if (st.ownItemCount(sap_of_world_tree) != 0 && st.ownItemCount(gold_leaves) >= 5)
                        htmltext = "rogellia_q0408_26.htm";
                    else if (st.ownItemCount(immortal_love) != 0)
                        htmltext = "rogellia_q0408_19.htm";
                    else if (st.ownItemCount(lucky_potpouri) != 0 && st.ownItemCount(amethyst) < 2)
                        htmltext = "rogellia_q0408_20.htm";
                    else if (st.ownItemCount(lucky_potpouri) != 0 && st.ownItemCount(amethyst) >= 2)
                        htmltext = "rogellia_q0408_27.htm";
                    else if (st.ownItemCount(rogellias_letter) == 0 && st.ownItemCount(appetizing_apple) == 0 && st.ownItemCount(immortal_love) == 0 && st.ownItemCount(charm_of_grain) == 0 && st.ownItemCount(sap_of_world_tree) == 0 && st.ownItemCount(lucky_potpouri) == 0 && st.ownItemCount(fertility_peridot) != 0 && st.ownItemCount(magical_powers_ruby) != 0 && st.ownItemCount(nobility_amethyst) != 0 && st.ownItemCount(pure_aquamarine) != 0) {
                        st.takeItems(magical_powers_ruby, -1);
                        st.takeItems(pure_aquamarine, -1);
                        st.takeItems(nobility_amethyst, -1);
                        st.takeItems(fertility_peridot, -1);
                        if (GetOneTimeQuestFlag == 0) {
                            st.getPlayer().getPlayerVariables().set(PlayerVariables.profession_145, String.valueOf(1), -1);
                            if (talker_level >= 20)
                                st.addExpAndSp(320534, 22532);
                            else if (talker_level == 19)
                                st.addExpAndSp(456128, 29230);
                            else
                                st.addExpAndSp(591724, 35928);
                            st.giveItems(ADENA_ID, 163800);
                        }
                        htmltext = "rogellia_q0408_24.htm";
                        player.sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                        if (st.ownItemCount(eternity_diamond) == 0)
                            st.giveItems(eternity_diamond, 1);
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                    }
                } else if (npcId == grain) {
                    if (st.ownItemCount(rogellias_letter) != 0)
                        htmltext = "grain_q0408_01.htm";
                    else if (st.ownItemCount(charm_of_grain) != 0 && st.ownItemCount(red_down) < 5)
                        htmltext = "grain_q0408_03.htm";
                    else if (st.ownItemCount(charm_of_grain) != 0 && st.ownItemCount(red_down) >= 5) {
                        st.takeItems(red_down, -1);
                        st.takeItems(charm_of_grain, -1);
                        if (st.ownItemCount(magical_powers_ruby) == 0)
                            st.giveItems(magical_powers_ruby, 1);
                        htmltext = "grain_q0408_04.htm";
                    }
                } else if (npcId == thalya) {
                    if (st.ownItemCount(appetizing_apple) != 0)
                        htmltext = "thalya_q0408_01.htm";
                    else if (st.ownItemCount(sap_of_world_tree) != 0 && st.ownItemCount(gold_leaves) < 5)
                        htmltext = "thalya_q0408_03.htm";
                    else if (st.ownItemCount(sap_of_world_tree) != 0 && st.ownItemCount(gold_leaves) >= 5) {
                        st.takeItems(gold_leaves, -1);
                        st.takeItems(sap_of_world_tree, -1);
                        if (st.ownItemCount(pure_aquamarine) == 0)
                            st.giveItems(pure_aquamarine, 1);
                        htmltext = "thalya_q0408_04.htm";
                    }
                } else if (npcId == northwindel) {
                    if (st.ownItemCount(immortal_love) != 0) {
                        st.takeItems(immortal_love, -1);
                        if (st.ownItemCount(lucky_potpouri) == 0)
                            st.giveItems(lucky_potpouri, 1);
                        htmltext = "northwindel_q0408_01.htm";
                    } else if (st.ownItemCount(lucky_potpouri) != 0 && st.ownItemCount(amethyst) < 2)
                        htmltext = "northwindel_q0408_02.htm";
                    else if (st.ownItemCount(lucky_potpouri) != 0 && st.ownItemCount(amethyst) >= 2) {
                        st.takeItems(amethyst, -1);
                        st.takeItems(lucky_potpouri, -1);
                        if (st.ownItemCount(nobility_amethyst) == 0)
                            st.giveItems(nobility_amethyst, 1);
                        htmltext = "northwindel_q0408_03.htm`";
                    }
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == pincer_spider) {
            if (st.ownItemCount(charm_of_grain) != 0 && st.ownItemCount(red_down) < 5 && Rnd.get(100) < 70) {
                st.giveItems(red_down, 1);
                if (st.ownItemCount(red_down) == 5)
                    st.soundEffect(SOUND_MIDDLE);
                else
                    st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == dryad_ribe) {
            if (st.ownItemCount(sap_of_world_tree) != 0 && st.ownItemCount(gold_leaves) < 5 && Rnd.get(100) < 40) {
                st.giveItems(gold_leaves, 1);
                if (st.ownItemCount(gold_leaves) == 5)
                    st.soundEffect(SOUND_MIDDLE);
                else
                    st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == sukar_wererat_leader) {
            if (st.ownItemCount(lucky_potpouri) != 0 && st.ownItemCount(amethyst) < 2 && Rnd.get(100) < 40) {
                st.giveItems(amethyst, 1);
                if (st.ownItemCount(amethyst) == 2)
                    st.soundEffect(SOUND_MIDDLE);
                else
                    st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }
}