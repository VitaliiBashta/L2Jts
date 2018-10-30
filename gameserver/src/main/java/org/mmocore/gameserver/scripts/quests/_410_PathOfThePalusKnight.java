package org.mmocore.gameserver.scripts.quests;

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
public class _410_PathOfThePalusKnight extends Quest {
    // npc
    private final static int master_virgil = 30329;
    private final static int kalinta = 30422;
    // mobs
    private final static int poison_spider = 20038;
    private final static int bind_poison_spider = 20043;
    private final static int lycanthrope = 20049;
    // questitem
    private final static int pallus_talisman = 1237;
    private final static int lycanthrope_skull = 1238;
    private final static int virgils_letter = 1239;
    private final static int morte_talisman = 1240;
    private final static int predator_carapace = 1241;
    private final static int trimden_silk = 1242;
    private final static int coffin_eternal_rest = 1243;
    private final static int gaze_of_abyss = 1244;

    public _410_PathOfThePalusKnight() {
        super(false);
        addStartNpc(master_virgil);
        addTalkId(kalinta);
        addKillId(poison_spider, bind_poison_spider, lycanthrope);
        addQuestItem(pallus_talisman, lycanthrope_skull, virgils_letter, morte_talisman, predator_carapace, trimden_silk, coffin_eternal_rest);
        addLevelCheck(18);
        addClassIdCheck(ClassId.dark_fighter);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        int talker_occupation = st.getPlayer().getPlayerClassComponent().getClassId().getId();
        int dark_fighter = 0x1f;
        int palus_knight = 0x20;
        if (npcId == master_virgil) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                st.giveItems(pallus_talisman, 1);
                htmltext = "master_virgil_q0410_06.htm";
            } else if (event.equalsIgnoreCase("reply_1")) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = "master_virgil_q0410_02.htm";
                        break;
                    case CLASS_ID:
                        if (talker_occupation != dark_fighter) {
                            if (talker_occupation == palus_knight) {
                                htmltext = "master_virgil_q0410_02a.htm";
                                st.exitQuest(true);
                            } else {
                                htmltext = "master_virgil_q0410_03.htm";
                                st.exitQuest(true);
                            }
                        }
                        break;
                    default:
                        if (st.ownItemCount(gaze_of_abyss) == 1 && talker_occupation == dark_fighter)
                            htmltext = "master_virgil_q0410_04.htm";
                        else
                            htmltext = "master_virgil_q0410_05.htm";
                        break;
                }
            } else if (event.equalsIgnoreCase("reply_2") && st.ownItemCount(pallus_talisman) > 0 && st.ownItemCount(lycanthrope_skull) > 0) {
                st.setCond(3);
                st.soundEffect(SOUND_MIDDLE);
                st.takeItems(pallus_talisman, 1);
                st.takeItems(lycanthrope_skull, -1);
                st.giveItems(virgils_letter, 1);
                htmltext = "master_virgil_q0410_10.htm";
            }
        }
        if (npcId == kalinta) {
            if (event.equalsIgnoreCase("reply_1") && st.ownItemCount(virgils_letter) > 0) {
                st.setCond(4);
                st.takeItems(virgils_letter, 1);
                st.giveItems(morte_talisman, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "kalinta_q0410_02.htm";
            } else if (event.equalsIgnoreCase("reply_2") && st.ownItemCount(morte_talisman) > 0 && st.ownItemCount(trimden_silk) > 0 && st.ownItemCount(predator_carapace) > 0) {
                st.setCond(6);
                st.takeItems(morte_talisman, 1);
                st.takeItems(trimden_silk, -1);
                st.takeItems(predator_carapace, -1);
                st.giveItems(coffin_eternal_rest, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "kalinta_q0410_06.htm";
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
                if (npcId == master_virgil)
                    htmltext = "master_virgil_q0410_01.htm";
                break;
            case STARTED:
                if (npcId == master_virgil) {
                    if (st.ownItemCount(pallus_talisman) == 1 && st.ownItemCount(lycanthrope_skull) == 0)
                        htmltext = "master_virgil_q0410_07.htm";
                    else if (st.ownItemCount(pallus_talisman) == 1 && st.ownItemCount(lycanthrope_skull) > 0 && st.ownItemCount(lycanthrope_skull) < 13)
                        htmltext = "master_virgil_q0410_08.htm";
                    else if (st.ownItemCount(pallus_talisman) == 1 && st.ownItemCount(lycanthrope_skull) >= 13)
                        htmltext = "master_virgil_q0410_09.htm";
                    else if (st.ownItemCount(coffin_eternal_rest) == 1) {
                        st.takeItems(coffin_eternal_rest, 1);
                        st.takeItems(morte_talisman, -1);
                        st.giveItems(gaze_of_abyss, 1);
                        htmltext = "master_virgil_q0410_11.htm";
                        if (GetOneTimeQuestFlag == 0) {
                            st.getPlayer().getPlayerVariables().set(PlayerVariables.profession_145, String.valueOf(1), -1);
                            if (talker_level >= 20)
                                st.addExpAndSp(320534, 26212);
                            else if (talker_level == 19)
                                st.addExpAndSp(456128, 32910);
                            else
                                st.addExpAndSp(591724, 39608);
                            st.giveItems(ADENA_ID, 163800);
                        }
                        player.sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                    } else if (st.ownItemCount(morte_talisman) > 0 || st.ownItemCount(virgils_letter) > 0)
                        htmltext = "master_virgil_q0410_12.htm";
                }
                if (npcId == kalinta) {
                    if (st.ownItemCount(virgils_letter) > 0)
                        htmltext = "kalinta_q0410_01.htm";
                    else if (st.ownItemCount(morte_talisman) > 0 && st.ownItemCount(trimden_silk) == 0 && st.ownItemCount(predator_carapace) == 0)
                        htmltext = "kalinta_q0410_03.htm";
                    else if (st.ownItemCount(morte_talisman) > 0 && st.ownItemCount(trimden_silk) == 0 && st.ownItemCount(predator_carapace) > 0)
                        htmltext = "kalinta_q0410_04.htm";
                    else if (st.ownItemCount(morte_talisman) > 0 && st.ownItemCount(trimden_silk) >= 5 && st.ownItemCount(predator_carapace) > 0)
                        htmltext = "kalinta_q0410_05.htm";
                    else if (st.ownItemCount(morte_talisman) < 0 && st.ownItemCount(trimden_silk) > 0 && st.ownItemCount(predator_carapace) >= 0)
                        htmltext = "kalinta_q0410_04.htm";
                    else if (st.ownItemCount(coffin_eternal_rest) > 0)
                        htmltext = "kalinta_q0410_06.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == lycanthrope) {
            if (st.ownItemCount(pallus_talisman) == 1 && st.ownItemCount(lycanthrope_skull) < 13) {
                st.giveItems(lycanthrope_skull, 1);
                if (st.ownItemCount(lycanthrope_skull) >= 13) {
                    st.setCond(2);
                    st.soundEffect(SOUND_MIDDLE);
                } else
                    st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == poison_spider) {
            if (st.ownItemCount(morte_talisman) == 1 && st.ownItemCount(predator_carapace) < 1) {
                st.giveItems(predator_carapace, 1);
                st.soundEffect(SOUND_MIDDLE);
                if (st.ownItemCount(trimden_silk) >= 5)
                    st.setCond(5);
            }
        } else if (npcId == bind_poison_spider) {
            if (st.ownItemCount(morte_talisman) == 1 && st.ownItemCount(trimden_silk) < 5) {
                st.giveItems(trimden_silk, 1);
                if (st.ownItemCount(trimden_silk) >= 5 && st.ownItemCount(predator_carapace) == 1) {
                    st.soundEffect(SOUND_MIDDLE);
                    if (st.ownItemCount(trimden_silk) >= 5)
                        st.setCond(5);
                } else
                    st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }
}