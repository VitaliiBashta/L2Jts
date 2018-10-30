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
 * @version 1.2
 * @date 04/10/2015
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _405_PathOfTheCleric extends Quest {
    // npc
    private final static int gigon = 30022;
    private final static int trader_simplon = 30253;
    private final static int vivi = 30030;
    private final static int guard_praga = 30333;
    private final static int lemoniell = 30408;
    private final static int gallin = 30017;
    // mobs
    private final static int ruin_zombie = 20026;
    private final static int ruin_zombie_leader = 20029;
    // questitem
    private final static int letter_of_order1 = 1191;
    private final static int letter_of_order2 = 1192;
    private final static int book_of_lemoniell = 1193;
    private final static int book_of_vivi = 1194;
    private final static int book_of_simlon = 1195;
    private final static int book_of_praga = 1196;
    private final static int certificate_of_gallint = 1197;
    private final static int pendant_of_mother = 1198;
    private final static int necklace_of_mother = 1199;
    private final static int lemoniells_covenant = 1200;
    private final static int mark_of_faith = 1201;

    public _405_PathOfTheCleric() {
        super(false);
        addStartNpc(gigon);
        addTalkId(trader_simplon, vivi, guard_praga, lemoniell, gallin);
        addKillId(ruin_zombie, ruin_zombie_leader);
        addQuestItem(letter_of_order1, letter_of_order2, book_of_lemoniell, book_of_vivi, book_of_simlon, book_of_praga, certificate_of_gallint, pendant_of_mother, necklace_of_mother, lemoniells_covenant);
        addLevelCheck(18);
        addClassIdCheck(ClassId.mage);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        int talker_occupation = st.getPlayer().getPlayerClassComponent().getClassId().getId();
        int mage = 0x0a;
        int cleric = 0x0f;
        if (npcId == gigon) {
            if (event.equalsIgnoreCase("quest_accept")) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = "gigon_q0405_03.htm";
                        break;
                    case CLASS_ID:
                        if (talker_occupation == cleric)
                            htmltext = "gigon_q0405_02a.htm";
                        else
                            htmltext = "gigon_q0405_02.htm";
                        break;
                    default:
                        if (st.ownItemCount(mark_of_faith) > 1 && talker_occupation == mage)
                            htmltext = "gigon_q0405_04.htm";
                        else {
                            st.setCond(1);
                            st.giveItems(letter_of_order1, 1);
                            st.setState(STARTED);
                            st.soundEffect(SOUND_ACCEPT);
                            htmltext = "gigon_q0405_05.htm";
                        }
                        break;
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
                if (npcId == gigon) {
                    if (st.ownItemCount(mark_of_faith) == 0)
                        htmltext = "gigon_q0405_01.htm";
                    else
                        htmltext = "gigon_q0405_04.htm";
                }
                break;
            case STARTED:
                if (npcId == gigon) {
                    if (st.ownItemCount(letter_of_order2) == 1 && st.ownItemCount(lemoniells_covenant) == 0)
                        htmltext = "gigon_q0405_07.htm";
                    else if (st.ownItemCount(letter_of_order2) == 1 && st.ownItemCount(lemoniells_covenant) == 1) {
                        st.takeItems(lemoniells_covenant, 1);
                        st.takeItems(letter_of_order2, 1);
                        st.giveItems(mark_of_faith, 1);
                        if (GetOneTimeQuestFlag == 0) {
                            st.getPlayer().getPlayerVariables().set(PlayerVariables.profession_145, String.valueOf(1), -1);
                            if (talker_level >= 20)
                                st.addExpAndSp(320534, 23152);
                            else if (talker_level == 19)
                                st.addExpAndSp(456128, 28630);
                            else
                                st.addExpAndSp(591724, 35328);
                            st.giveItems(ADENA_ID, 163800);
                        }
                        htmltext = "gigon_q0405_09.htm";
                        player.sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                    } else if (st.ownItemCount(letter_of_order1) == 1) {
                        if (st.ownItemCount(book_of_vivi) == 1 && st.ownItemCount(book_of_simlon) > 0 && st.ownItemCount(book_of_praga) == 1) {
                            st.setCond(3);
                            st.takeItems(book_of_praga, 1);
                            st.takeItems(book_of_vivi, 1);
                            st.takeItems(book_of_simlon, 3);
                            st.takeItems(letter_of_order1, 1);
                            st.giveItems(letter_of_order2, 1);
                            st.soundEffect(SOUND_MIDDLE);
                            htmltext = "gigon_q0405_08.htm";
                        } else
                            htmltext = "gigon_q0405_06.htm";
                    }
                } else if (npcId == trader_simplon) {
                    if (st.ownItemCount(letter_of_order1) == 1) {
                        if (st.ownItemCount(book_of_simlon) == 0) {
                            st.giveItems(book_of_simlon, 3);
                            if (st.ownItemCount(book_of_simlon) >= 0 && st.ownItemCount(book_of_vivi) >= 1 && st.ownItemCount(book_of_praga) >= 1) {
                                st.setCond(2);
                                st.soundEffect(SOUND_MIDDLE);
                            }
                            htmltext = "trader_simplon_q0405_01.htm";
                        } else if (st.ownItemCount(book_of_simlon) > 0)
                            htmltext = "trader_simplon_q0405_02.htm";
                    }
                } else if (npcId == vivi) {
                    if (st.ownItemCount(letter_of_order1) == 1) {
                        if (st.ownItemCount(book_of_vivi) == 0) {
                            st.giveItems(book_of_vivi, 1);
                            if (st.ownItemCount(book_of_simlon) >= 3 && st.ownItemCount(book_of_vivi) >= 0 && st.ownItemCount(book_of_praga) >= 1) {
                                st.setCond(2);
                                st.soundEffect(SOUND_MIDDLE);
                            }
                            htmltext = "vivi_q0405_01.htm";
                        } else if (st.ownItemCount(book_of_vivi) == 1)
                            htmltext = "vivi_q0405_02.htm";
                    }
                } else if (npcId == guard_praga) {
                    if (st.ownItemCount(letter_of_order1) == 1) {
                        if (st.ownItemCount(book_of_praga) == 0 && st.ownItemCount(necklace_of_mother) == 0) {
                            htmltext = "guard_praga_q0405_01.htm";
                            st.giveItems(necklace_of_mother, 1);
                        } else if (st.ownItemCount(book_of_praga) == 0 && st.ownItemCount(necklace_of_mother) == 1 && st.ownItemCount(pendant_of_mother) == 0)
                            htmltext = "guard_praga_q0405_02.htm";
                        else if (st.ownItemCount(book_of_praga) == 0 && st.ownItemCount(necklace_of_mother) == 1 && st.ownItemCount(pendant_of_mother) == 1) {
                            htmltext = "guard_praga_q0405_03.htm";
                            st.takeItems(necklace_of_mother, 1);
                            st.takeItems(pendant_of_mother, 1);
                            st.giveItems(book_of_praga, 1);
                            if (st.ownItemCount(book_of_simlon) >= 3 && st.ownItemCount(book_of_vivi) >= 1 && st.ownItemCount(book_of_praga) >= 0) {
                                st.setCond(2);
                                st.soundEffect(SOUND_MIDDLE);
                            }
                        } else if (st.ownItemCount(book_of_praga) > 0)
                            htmltext = "guard_praga_q0405_04.htm";
                    }
                } else if (npcId == lemoniell) {
                    if (st.ownItemCount(letter_of_order2) == 0)
                        htmltext = "lemoniell_q0405_02.htm";
                    else if (st.ownItemCount(letter_of_order2) == 1 && st.ownItemCount(book_of_lemoniell) == 0 && st.ownItemCount(lemoniells_covenant) == 0 && st.ownItemCount(certificate_of_gallint) == 0) {
                        st.setCond(4);
                        st.giveItems(book_of_lemoniell, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "lemoniell_q0405_01.htm";
                    } else if (st.ownItemCount(letter_of_order2) == 1 && st.ownItemCount(book_of_lemoniell) == 1 && st.ownItemCount(lemoniells_covenant) == 0 && st.ownItemCount(certificate_of_gallint) == 0)
                        htmltext = "lemoniell_q0405_03.htm";
                    else if (st.ownItemCount(letter_of_order2) == 1 && st.ownItemCount(book_of_lemoniell) == 0 && st.ownItemCount(lemoniells_covenant) == 0 && st.ownItemCount(certificate_of_gallint) == 1) {
                        st.setCond(6);
                        st.giveItems(certificate_of_gallint, 1);
                        st.giveItems(lemoniells_covenant, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "lemoniell_q0405_04.htm";
                    } else if (st.ownItemCount(letter_of_order2) == 1 && st.ownItemCount(book_of_lemoniell) == 0 && st.ownItemCount(lemoniells_covenant) == 1 && st.ownItemCount(certificate_of_gallint) == 0)
                        htmltext = "lemoniell_q0405_05.htm";
                } else if (npcId == gallin) {
                    if (st.ownItemCount(letter_of_order2) == 1 && st.ownItemCount(lemoniells_covenant) == 0) {
                        if (st.ownItemCount(book_of_lemoniell) == 1 && st.ownItemCount(certificate_of_gallint) == 0) {
                            st.setCond(5);
                            st.takeItems(book_of_lemoniell, 1);
                            st.giveItems(certificate_of_gallint, 1);
                            st.soundEffect(SOUND_MIDDLE);
                            htmltext = "gallin_q0405_01.htm";
                        } else if (st.ownItemCount(book_of_lemoniell) == 0 && st.ownItemCount(certificate_of_gallint) == 1)
                            htmltext = "gallin_q0405_02.htm";
                    }
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == ruin_zombie || npcId == ruin_zombie_leader) {
            if (st.ownItemCount(necklace_of_mother) > 0 && st.ownItemCount(pendant_of_mother) == 0) {
                st.giveItems(pendant_of_mother, 1);
                st.soundEffect(SOUND_MIDDLE);
            }
        }
        return null;
    }
}