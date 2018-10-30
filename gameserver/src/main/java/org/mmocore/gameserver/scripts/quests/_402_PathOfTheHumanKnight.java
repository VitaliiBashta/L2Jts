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
public class _402_PathOfTheHumanKnight extends Quest {
    // npc
    public final int sir_karrel_vasper = 30417;
    public final int quilt = 30031;
    public final int levian = 30037;
    public final int gilbert = 30039;
    public final int bishop_raimund = 30289;
    public final int sir_collin_windawood = 30311;
    public final int captain_bathia = 30332;
    public final int captain_bezique = 30379;
    public final int sir_aron_tanford = 30653;
    // mobs
    public final int bugbear_raider = 20775;
    public final int undead_priest = 27024;
    public final int poison_spider = 20038;
    public final int bind_poison_spider = 20043;
    public final int poison_predator = 20050;
    public final int langk_lizardman = 20030;
    public final int langk_lizardman_scout = 20027;
    public final int langk_lizardman_warrior = 20024;
    public final int giant_spider = 20103;
    public final int poker = 20106;
    public final int blader = 20108;
    public final int silent_horror = 20404;
    // questitem
    public final int sword_of_ritual = 1161;
    public final int coin_of_lords1 = 1162;
    public final int coin_of_lords2 = 1163;
    public final int coin_of_lords3 = 1164;
    public final int coin_of_lords4 = 1165;
    public final int coin_of_lords5 = 1166;
    public final int coin_of_lords6 = 1167;
    public final int gludio_guards_mark1 = 1168;
    public final int bugbear_necklace = 1169;
    public final int einhasad_church_mark1 = 1170;
    public final int einhasad_crucifix = 1171;
    public final int gludio_guards_mark2 = 1172;
    public final int poison_spider_leg1 = 1173;
    public final int einhasad_church_mark2 = 1174;
    public final int lizardman_totem = 1175;
    public final int gludio_guards_mark3 = 1176;
    public final int giant_spider_husk = 1177;
    public final int einhasad_church_mark3 = 1178;
    public final int horrible_skull = 1179;
    public final int mark_of_esquire = 1271;

    public _402_PathOfTheHumanKnight() {
        super(false);
        addStartNpc(sir_karrel_vasper);
        addTalkId(quilt, levian, gilbert, bishop_raimund, sir_collin_windawood, captain_bathia, captain_bezique, sir_aron_tanford);
        addKillId(bugbear_raider, undead_priest, poison_spider, bind_poison_spider, poison_predator, langk_lizardman, langk_lizardman_scout, langk_lizardman_warrior, giant_spider, poker, blader, silent_horror);
        addQuestItem(bugbear_necklace, einhasad_crucifix, poison_spider_leg1, lizardman_totem, giant_spider_husk, horrible_skull);
        addLevelCheck(18);
        addClassIdCheck(ClassId.fighter);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        Player player = st.getPlayer();
        int npcId = npc.getNpcId();
        int talker_occupation = st.getPlayer().getPlayerClassComponent().getClassId().getId();
        int GetOneTimeQuestFlag = st.getPlayer().getPlayerVariables().getInt(PlayerVariables.profession_145);
        int fighter = 0x00;
        int knight = 0x04;
        if (npcId == sir_karrel_vasper) {
            if (event.equalsIgnoreCase("quest_accept")) {
                if (st.ownItemCount(mark_of_esquire) == 0) {
                    st.giveItems(mark_of_esquire, 1);
                    st.setCond(1);
                    st.setState(STARTED);
                    st.soundEffect(SOUND_ACCEPT);
                    htmltext = "sir_karrel_vasper_q0402_08.htm";
                }
            } else if (event.equalsIgnoreCase("402_reply_1")) {
                if (talker_occupation == fighter) {
                    if (st.ownItemCount(sword_of_ritual) > 0)
                        htmltext = "sir_karrel_vasper_q0402_04.htm";
                    else
                        htmltext = "sir_karrel_vasper_q0402_05.htm";
                } else if (talker_occupation == knight)
                    htmltext = "sir_karrel_vasper_q0402_02a.htm";
                else
                    htmltext = "sir_karrel_vasper_q0402_03.htm";
            } else if (event.equalsIgnoreCase("402_reply_2"))
                htmltext = "sir_karrel_vasper_q0402_07.htm";
            else if (event.equalsIgnoreCase("402_reply_3"))
                htmltext = "sir_karrel_vasper_q0402_15.htm";
            else if (event.equalsIgnoreCase("402_reply_4") && st.ownItemCount(mark_of_esquire) > 0 && st.ownItemCount(coin_of_lords1) + st.ownItemCount(coin_of_lords2) + st.ownItemCount(coin_of_lords3) + st.ownItemCount(coin_of_lords4) + st.ownItemCount(coin_of_lords5) + st.ownItemCount(coin_of_lords6) == 3) {
                if (GetOneTimeQuestFlag == 0) {
                    st.getPlayer().getPlayerVariables().set(PlayerVariables.profession_145, String.valueOf(1), -1);
                    if (st.getPlayer().getLevel() >= 20) {
                        st.addExpAndSp(160267, 11576);
                        st.giveItems(ADENA_ID, 81900);
                    } else if (st.getPlayer().getLevel() >= 19) {
                        st.addExpAndSp(228064, 14925);
                        st.giveItems(ADENA_ID, 81900);
                    } else {
                        st.addExpAndSp(295862, 18274);
                        st.giveItems(ADENA_ID, 81900);
                    }
                }
                st.takeItems(coin_of_lords1, -1);
                st.takeItems(coin_of_lords2, -1);
                st.takeItems(coin_of_lords3, -1);
                st.takeItems(coin_of_lords4, -1);
                st.takeItems(coin_of_lords5, -1);
                st.takeItems(coin_of_lords6, -1);
                st.takeItems(gludio_guards_mark1, -1);
                st.takeItems(gludio_guards_mark2, -1);
                st.takeItems(gludio_guards_mark3, -1);
                st.takeItems(einhasad_church_mark1, -1);
                st.takeItems(einhasad_church_mark2, -1);
                st.takeItems(einhasad_church_mark3, -1);
                st.takeItems(bugbear_necklace, -1);
                st.takeItems(einhasad_crucifix, -1);
                st.takeItems(poison_spider_leg1, -1);
                st.takeItems(lizardman_totem, -1);
                st.takeItems(giant_spider_husk, -1);
                st.takeItems(horrible_skull, -1);
                st.takeItems(mark_of_esquire, -1);
                st.giveItems(sword_of_ritual, 1);
                htmltext = "sir_karrel_vasper_q0402_13.htm";
                player.sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(false);
            } else if (event.equalsIgnoreCase("402_reply_5") && st.ownItemCount(mark_of_esquire) > 0 && st.ownItemCount(coin_of_lords1) + st.ownItemCount(coin_of_lords2) + st.ownItemCount(coin_of_lords3) + st.ownItemCount(coin_of_lords4) + st.ownItemCount(coin_of_lords5) + st.ownItemCount(coin_of_lords6) > 3 && st.ownItemCount(coin_of_lords1) + st.ownItemCount(coin_of_lords2) + st.ownItemCount(coin_of_lords3) + st.ownItemCount(coin_of_lords4) + st.ownItemCount(coin_of_lords5) + st.ownItemCount(coin_of_lords6) < 6) {
                if (GetOneTimeQuestFlag == 0) {
                    st.getPlayer().getPlayerVariables().set(PlayerVariables.profession_145, String.valueOf(1), -1);
                    if (st.getPlayer().getLevel() >= 20) {
                        st.addExpAndSp(160267, 11576);
                        st.giveItems(ADENA_ID, 81900);
                    } else if (st.getPlayer().getLevel() >= 19) {
                        st.addExpAndSp(228064, 14925);
                        st.giveItems(ADENA_ID, 81900);
                    } else {
                        st.addExpAndSp(295862, 18274);
                        st.giveItems(ADENA_ID, 81900);
                    }
                }
                st.takeItems(coin_of_lords1, -1);
                st.takeItems(coin_of_lords2, -1);
                st.takeItems(coin_of_lords3, -1);
                st.takeItems(coin_of_lords4, -1);
                st.takeItems(coin_of_lords5, -1);
                st.takeItems(coin_of_lords6, -1);
                st.takeItems(gludio_guards_mark1, -1);
                st.takeItems(gludio_guards_mark2, -1);
                st.takeItems(gludio_guards_mark3, -1);
                st.takeItems(einhasad_church_mark1, -1);
                st.takeItems(einhasad_church_mark2, -1);
                st.takeItems(einhasad_church_mark3, -1);
                st.takeItems(bugbear_necklace, -1);
                st.takeItems(einhasad_crucifix, -1);
                st.takeItems(poison_spider_leg1, -1);
                st.takeItems(lizardman_totem, -1);
                st.takeItems(giant_spider_husk, -1);
                st.takeItems(horrible_skull, -1);
                st.takeItems(mark_of_esquire, -1);
                st.giveItems(sword_of_ritual, 1);
                htmltext = "sir_karrel_vasper_q0402_14.htm";
                player.sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(false);
            }
        } else if (npcId == quilt) {
            if (event.equalsIgnoreCase("402_reply_1")) {
                st.giveItems(einhasad_church_mark3, 1);
                htmltext = "quilt_q0402_02.htm";
            }
        } else if (npcId == levian) {
            if (event.equalsIgnoreCase("402_reply_1")) {
                st.giveItems(einhasad_church_mark2, 1);
                htmltext = "levian_q0402_02.htm";
            }
        } else if (npcId == gilbert) {
            if (event.equalsIgnoreCase("402_reply_1")) {
                st.giveItems(gludio_guards_mark3, 1);
                htmltext = "gilbert_q0402_02.htm";
            }
        } else if (npcId == bishop_raimund) {
            if (event.equalsIgnoreCase("402_reply_1")) {
                st.giveItems(einhasad_church_mark1, 1);
                htmltext = "bishop_raimund_q0402_03.htm";
            }
        } else if (npcId == captain_bathia) {
            if (event.equalsIgnoreCase("402_reply_1")) {
                st.giveItems(gludio_guards_mark1, 1);
                htmltext = "captain_bathia_q0402_02.htm";
            }
        } else if (npcId == captain_bezique) {
            if (event.equalsIgnoreCase("402_reply_1")) {
                st.giveItems(gludio_guards_mark2, 1);
                htmltext = "captain_bezique_q0402_02.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        Player player = st.getPlayer();
        int npcId = npc.getNpcId();
        int id = st.getState();
        int GetOneTimeQuestFlag = st.getPlayer().getPlayerVariables().getInt(PlayerVariables.profession_145);
        switch (id) {
            case CREATED:
                if (npcId == sir_karrel_vasper) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "sir_karrel_vasper_q0402_02.htm";
                            break;
                        case CLASS_ID:
                            htmltext = "sir_karrel_vasper_q0402_03.htm";
                            break;
                        default:
                            htmltext = "sir_karrel_vasper_q0402_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == sir_karrel_vasper) {
                    if (st.ownItemCount(mark_of_esquire) > 0 && st.ownItemCount(coin_of_lords1) + st.ownItemCount(coin_of_lords2) + st.ownItemCount(coin_of_lords3) + st.ownItemCount(coin_of_lords4) + st.ownItemCount(coin_of_lords5) + st.ownItemCount(coin_of_lords6) < 3)
                        htmltext = "sir_karrel_vasper_q0402_09.htm";
                    else if (st.ownItemCount(mark_of_esquire) > 0 && st.ownItemCount(coin_of_lords1) + st.ownItemCount(coin_of_lords2) + st.ownItemCount(coin_of_lords3) + st.ownItemCount(coin_of_lords4) + st.ownItemCount(coin_of_lords5) + st.ownItemCount(coin_of_lords6) == 3)
                        htmltext = "sir_karrel_vasper_q0402_10.htm";
                    else if (st.ownItemCount(mark_of_esquire) > 0 && st.ownItemCount(coin_of_lords1) + st.ownItemCount(coin_of_lords2) + st.ownItemCount(coin_of_lords3) + st.ownItemCount(coin_of_lords4) + st.ownItemCount(coin_of_lords5) + st.ownItemCount(coin_of_lords6) > 3 && st.ownItemCount(coin_of_lords1) + st.ownItemCount(coin_of_lords2) + st.ownItemCount(coin_of_lords3) + st.ownItemCount(coin_of_lords4) + st.ownItemCount(coin_of_lords5) + st.ownItemCount(coin_of_lords6) < 6)
                        htmltext = "sir_karrel_vasper_q0402_11.htm";
                    else if (st.ownItemCount(mark_of_esquire) > 0 && st.ownItemCount(coin_of_lords1) + st.ownItemCount(coin_of_lords2) + st.ownItemCount(coin_of_lords3) + st.ownItemCount(coin_of_lords4) + st.ownItemCount(coin_of_lords5) + st.ownItemCount(coin_of_lords6) == 6) {
                        if (GetOneTimeQuestFlag == 0) {
                            st.getPlayer().getPlayerVariables().set(PlayerVariables.profession_145, String.valueOf(1), -1);
                            if (st.getPlayer().getLevel() >= 20)
                                st.addExpAndSp(320534, 23152);
                            else if (st.getPlayer().getLevel() == 19)
                                st.addExpAndSp(456128, 29850);
                            else
                                st.addExpAndSp(591724, 36542);
                            st.giveItems(ADENA_ID, 163800);
                        }
                        st.takeItems(coin_of_lords1, -1);
                        st.takeItems(coin_of_lords2, -1);
                        st.takeItems(coin_of_lords3, -1);
                        st.takeItems(coin_of_lords4, -1);
                        st.takeItems(coin_of_lords5, -1);
                        st.takeItems(coin_of_lords6, -1);
                        st.takeItems(gludio_guards_mark1, -1);
                        st.takeItems(gludio_guards_mark2, -1);
                        st.takeItems(gludio_guards_mark3, -1);
                        st.takeItems(einhasad_church_mark1, -1);
                        st.takeItems(einhasad_church_mark2, -1);
                        st.takeItems(einhasad_church_mark3, -1);
                        st.takeItems(bugbear_necklace, -1);
                        st.takeItems(einhasad_crucifix, -1);
                        st.takeItems(poison_spider_leg1, -1);
                        st.takeItems(lizardman_totem, -1);
                        st.takeItems(giant_spider_husk, -1);
                        st.takeItems(horrible_skull, -1);
                        st.takeItems(mark_of_esquire, -1);
                        st.giveItems(sword_of_ritual, 1);
                        htmltext = "sir_karrel_vasper_q0402_12.htm";
                        player.sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                    }
                } else if (npcId == quilt) {
                    if (st.ownItemCount(einhasad_church_mark3) == 0 && st.ownItemCount(mark_of_esquire) > 0 && st.ownItemCount(coin_of_lords6) == 0)
                        htmltext = "quilt_q0402_01.htm";
                    else if (st.ownItemCount(einhasad_church_mark3) > 0) {
                        if (st.ownItemCount(horrible_skull) < 10)
                            htmltext = "quilt_q0402_03.htm";
                        else {
                            htmltext = "quilt_q0402_04.htm";
                            st.takeItems(horrible_skull, -1);
                            st.takeItems(einhasad_church_mark3, 1);
                            st.giveItems(coin_of_lords6, 1);
                        }
                    } else if (st.ownItemCount(coin_of_lords6) > 0)
                        htmltext = "quilt_q0402_05.htm";
                } else if (npcId == levian) {
                    if (st.ownItemCount(einhasad_church_mark2) == 0 && st.ownItemCount(mark_of_esquire) > 0 && st.ownItemCount(coin_of_lords4) == 0)
                        htmltext = "levian_q0402_01.htm";
                    else if (st.ownItemCount(einhasad_church_mark2) > 0) {
                        if (st.ownItemCount(lizardman_totem) < 20)
                            htmltext = "levian_q0402_03.htm";
                        else {
                            htmltext = "levian_q0402_04.htm";
                            st.takeItems(lizardman_totem, -1);
                            st.takeItems(einhasad_church_mark2, 1);
                            st.giveItems(coin_of_lords4, 1);
                        }
                    } else if (st.ownItemCount(coin_of_lords4) > 0)
                        htmltext = "levian_q0402_05.htm";
                } else if (npcId == gilbert) {
                    if (st.ownItemCount(gludio_guards_mark3) == 0 && st.ownItemCount(mark_of_esquire) > 0 && st.ownItemCount(coin_of_lords5) == 0)
                        htmltext = "gilbert_q0402_01.htm";
                    else if (st.ownItemCount(gludio_guards_mark3) > 0) {
                        if (st.ownItemCount(giant_spider_husk) < 20)
                            htmltext = "gilbert_q0402_03.htm";
                        else {
                            htmltext = "gilbert_q0402_04.htm";
                            st.takeItems(giant_spider_husk, -1);
                            st.takeItems(gludio_guards_mark3, 1);
                            st.giveItems(coin_of_lords5, 1);
                        }
                    } else if (st.ownItemCount(coin_of_lords5) > 0)
                        htmltext = "gilbert_q0402_05.htm";
                } else if (npcId == bishop_raimund) {
                    if (st.ownItemCount(einhasad_church_mark1) == 0 && st.ownItemCount(coin_of_lords2) == 0 && st.ownItemCount(mark_of_esquire) > 0)
                        htmltext = "bishop_raimund_q0402_01.htm";
                    else if (st.ownItemCount(einhasad_church_mark1) > 0) {
                        if (st.ownItemCount(einhasad_crucifix) < 12)
                            htmltext = "bishop_raimund_q0402_04.htm";
                        else {
                            htmltext = "bishop_raimund_q0402_05.htm";
                            st.takeItems(einhasad_crucifix, -1);
                            st.takeItems(einhasad_church_mark1, 1);
                            st.giveItems(coin_of_lords2, 1);
                        }
                    } else if (st.ownItemCount(coin_of_lords2) > 0)
                        htmltext = "bishop_raimund_q0402_06.htm";
                } else if (npcId == sir_collin_windawood) {
                    if (st.ownItemCount(mark_of_esquire) > 0)
                        htmltext = "sir_collin_windawood_q0402_01.htm";
                } else if (npcId == captain_bathia) {
                    if (st.ownItemCount(gludio_guards_mark1) == 0 && st.ownItemCount(mark_of_esquire) > 0 && st.ownItemCount(coin_of_lords1) == 0)
                        htmltext = "captain_bathia_q0402_01.htm";
                    else if (st.ownItemCount(gludio_guards_mark1) > 0) {
                        if (st.ownItemCount(bugbear_necklace) < 10)
                            htmltext = "captain_bathia_q0402_03.htm";
                        else {
                            htmltext = "captain_bathia_q0402_04.htm";
                            st.takeItems(bugbear_necklace, -1);
                            st.takeItems(gludio_guards_mark1, 1);
                            st.giveItems(coin_of_lords1, 1);
                        }
                    } else if (st.ownItemCount(coin_of_lords1) > 0)
                        htmltext = "captain_bathia_q0402_05.htm";
                } else if (npcId == captain_bezique) {
                    if (st.ownItemCount(mark_of_esquire) > 0 && st.ownItemCount(coin_of_lords3) == 0 && st.ownItemCount(gludio_guards_mark2) == 0)
                        htmltext = "captain_bezique_q0402_01.htm";
                    else if (st.ownItemCount(gludio_guards_mark2) > 0) {
                        if (st.ownItemCount(poison_spider_leg1) < 20)
                            htmltext = "captain_bezique_q0402_03.htm";
                        else {
                            htmltext = "captain_bezique_q0402_04.htm";
                            st.takeItems(poison_spider_leg1, -1);
                            st.takeItems(gludio_guards_mark2, 1);
                            st.giveItems(coin_of_lords3, 1);
                        }
                    } else if (st.ownItemCount(coin_of_lords3) > 0)
                        htmltext = "captain_bezique_q0402_05.htm";
                } else if (npcId == sir_aron_tanford) {
                    if (st.ownItemCount(mark_of_esquire) > 0)
                        htmltext = "sir_aron_tanford_q0402_01.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == bugbear_raider) {
            if (st.ownItemCount(gludio_guards_mark1) > 0 && st.ownItemCount(bugbear_necklace) < 10) {
                st.giveItems(bugbear_necklace, 1);
                if (st.ownItemCount(bugbear_necklace) == 10)
                    st.soundEffect(SOUND_MIDDLE);
                else
                    st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == undead_priest) {
            if (st.ownItemCount(einhasad_church_mark1) > 0 && st.ownItemCount(einhasad_crucifix) < 12 && Rnd.get(10) < 5) {
                st.giveItems(einhasad_crucifix, 1);
                if (st.ownItemCount(einhasad_crucifix) == 12)
                    st.soundEffect(SOUND_MIDDLE);
                else
                    st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == poison_spider) {
            if (st.ownItemCount(gludio_guards_mark2) > 0 && st.ownItemCount(poison_spider_leg1) < 20) {
                st.giveItems(poison_spider_leg1, 1);
                if (st.ownItemCount(poison_spider_leg1) == 20)
                    st.soundEffect(SOUND_MIDDLE);
                else
                    st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == bind_poison_spider) {
            if (st.ownItemCount(gludio_guards_mark2) > 0 && st.ownItemCount(poison_spider_leg1) < 20) {
                st.giveItems(poison_spider_leg1, 1);
                if (st.ownItemCount(poison_spider_leg1) == 20)
                    st.soundEffect(SOUND_MIDDLE);
                else
                    st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == poison_predator) {
            if (st.ownItemCount(gludio_guards_mark2) > 0 && st.ownItemCount(poison_spider_leg1) < 20) {
                st.giveItems(poison_spider_leg1, 1);
                if (st.ownItemCount(poison_spider_leg1) == 20)
                    st.soundEffect(SOUND_MIDDLE);
                else
                    st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == langk_lizardman) {
            if (st.ownItemCount(einhasad_church_mark2) > 0 && st.ownItemCount(lizardman_totem) < 20 && Rnd.get(10) < 5) {
                st.giveItems(lizardman_totem, 1);
                if (st.ownItemCount(lizardman_totem) == 20)
                    st.soundEffect(SOUND_MIDDLE);
                else
                    st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == langk_lizardman_scout) {
            if (st.ownItemCount(einhasad_church_mark2) > 0 && st.ownItemCount(lizardman_totem) < 20 && Rnd.get(10) < 5) {
                st.giveItems(lizardman_totem, 1);
                if (st.ownItemCount(lizardman_totem) == 20)
                    st.soundEffect(SOUND_MIDDLE);
                else
                    st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == langk_lizardman_warrior) {
            if (st.ownItemCount(einhasad_church_mark2) > 0 && st.ownItemCount(lizardman_totem) < 20 && Rnd.get(10) < 5) {
                st.giveItems(lizardman_totem, 1);
                if (st.ownItemCount(lizardman_totem) == 20)
                    st.soundEffect(SOUND_MIDDLE);
                else
                    st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == giant_spider) {
            if (st.ownItemCount(gludio_guards_mark3) > 0 && Rnd.get(10) < 4 && st.ownItemCount(giant_spider_husk) < 20) {
                st.giveItems(giant_spider_husk, 1);
                if (st.ownItemCount(giant_spider_husk) == 20)
                    st.soundEffect(SOUND_MIDDLE);
                else
                    st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == poker) {
            if (st.ownItemCount(gludio_guards_mark3) > 0 && Rnd.get(10) < 4 && st.ownItemCount(giant_spider_husk) < 20) {
                st.giveItems(giant_spider_husk, 1);
                if (st.ownItemCount(giant_spider_husk) == 20)
                    st.soundEffect(SOUND_MIDDLE);
                else
                    st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == blader) {
            if (st.ownItemCount(gludio_guards_mark3) > 0 && Rnd.get(10) < 4 && st.ownItemCount(giant_spider_husk) < 20) {
                st.giveItems(giant_spider_husk, 1);
                if (st.ownItemCount(giant_spider_husk) == 20)
                    st.soundEffect(SOUND_MIDDLE);
                else
                    st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == silent_horror) {
            if (st.ownItemCount(einhasad_church_mark3) > 0 && st.ownItemCount(horrible_skull) < 10 && Rnd.get(10) < 4) {
                st.giveItems(horrible_skull, 1);
                if (st.ownItemCount(horrible_skull) == 10)
                    st.soundEffect(SOUND_MIDDLE);
                else
                    st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }
}