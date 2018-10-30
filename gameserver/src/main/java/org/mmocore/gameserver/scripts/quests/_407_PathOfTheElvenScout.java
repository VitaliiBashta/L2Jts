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
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _407_PathOfTheElvenScout extends Quest {
    // npc
    private final static int master_reoria = 30328;
    private final static int guard_moretti = 30337;
    private final static int prigun = 30426;
    // mobs
    private final static int ol_mahum_sentry = 27031;
    private final static int ol_mahum_patrol = 20053;
    // questitem
    private final static int reoria_letter2 = 1207;
    private final static int priguns_tear_letter1 = 1208;
    private final static int priguns_tear_letter2 = 1209;
    private final static int priguns_tear_letter3 = 1210;
    private final static int priguns_tear_letter4 = 1211;
    private final static int morettis_herb = 1212;
    private final static int morettis_letter = 1214;
    private final static int priguns_letter = 1215;
    private final static int honorary_guard = 1216;
    private final static int reoria_recommendation = 1217;
    private final static int rusted_key = 1293;

    public _407_PathOfTheElvenScout() {
        super(false);
        addStartNpc(master_reoria);
        addTalkId(guard_moretti, prigun);
        addKillId(ol_mahum_sentry, ol_mahum_patrol);
        addQuestItem(reoria_letter2, priguns_tear_letter1, priguns_tear_letter2, priguns_tear_letter3, priguns_tear_letter4, morettis_herb, morettis_letter, priguns_letter, honorary_guard, rusted_key);
        addLevelCheck(18);
        addClassIdCheck(ClassId.elven_fighter);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        int talker_occupation = st.getPlayer().getPlayerClassComponent().getClassId().getId();
        int elven_fighter = 0x12;
        int elven_scout = 0x16;
        if (npcId == master_reoria) {
            if (event.equalsIgnoreCase("quest_accept")) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = "master_reoria_q0407_03.htm";
                        st.exitQuest(true);
                        break;
                    case CLASS_ID:
                        if (talker_occupation == elven_scout) {
                            htmltext = "master_reoria_q0407_02a.htm";
                            st.exitQuest(true);
                        } else {
                            htmltext = "master_reoria_q0407_02.htm";
                            st.exitQuest(true);
                        }
                        break;
                    default:
                        if (talker_occupation == elven_fighter) {
                            if (st.ownItemCount(reoria_recommendation) > 0) {
                                htmltext = "master_reoria_q0407_04.htm";
                                st.exitQuest(true);
                            } else {
                                st.setCond(1);
                                st.setState(STARTED);
                                st.giveItems(reoria_letter2, 1);
                                st.soundEffect(SOUND_ACCEPT);
                                htmltext = "master_reoria_q0407_05.htm";
                            }
                        }
                        break;
                }
            }
        } else if (npcId == guard_moretti) {
            if (event.equalsIgnoreCase("reply_1") && st.ownItemCount(reoria_letter2) > 0) {
                st.setCond(2);
                st.setMemoState("path_to_elven_scout", String.valueOf(1), true);
                st.takeItems(reoria_letter2, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "guard_moretti_q0407_03.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("path_to_elven_scout");
        Player player = st.getPlayer();
        int talker_level = st.getPlayer().getLevel();
        int GetOneTimeQuestFlag = st.getPlayer().getPlayerVariables().getInt(PlayerVariables.profession_145);
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == master_reoria)
                    htmltext = "master_reoria_q0407_01.htm";
                break;
            case STARTED:
                if (npcId == master_reoria) {
                    if (st.ownItemCount(reoria_letter2) > 0)
                        htmltext = "master_reoria_q0407_06.htm";
                    else if (GetMemoState == 1 && st.ownItemCount(reoria_letter2) == 0 && st.ownItemCount(honorary_guard) == 0)
                        htmltext = "master_reoria_q0407_08.htm";
                    else if (st.ownItemCount(honorary_guard) > 0) {
                        st.takeItems(honorary_guard, 1);
                        st.giveItems(reoria_recommendation, 1);
                        if (GetOneTimeQuestFlag == 0) {
                            st.getPlayer().getPlayerVariables().set(PlayerVariables.profession_145, String.valueOf(1), -1);
                            if (talker_level >= 20)
                                st.addExpAndSp(320534, 19932);
                            else if (talker_level == 19)
                                st.addExpAndSp(456128, 26630);
                            else
                                st.addExpAndSp(591724, 33328);
                            st.giveItems(ADENA_ID, 163800);
                        }
                        htmltext = "master_reoria_q0407_07.htm";
                        st.removeMemo("path_to_elven_scout");
                        player.sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                    }
                } else if (npcId == guard_moretti) {
                    if (st.ownItemCount(reoria_letter2) > 0 && st.ownItemCount(priguns_tear_letter1) + st.ownItemCount(priguns_tear_letter2) + st.ownItemCount(priguns_tear_letter3) + st.ownItemCount(priguns_tear_letter4) == 0)
                        htmltext = "guard_moretti_q0407_01.htm";
                    else if (GetMemoState == 1 && st.ownItemCount(morettis_letter) < 1 && st.ownItemCount(priguns_letter) == 0 && st.ownItemCount(honorary_guard) == 0) {
                        if (st.ownItemCount(priguns_tear_letter1) + st.ownItemCount(priguns_tear_letter2) + st.ownItemCount(priguns_tear_letter3) + st.ownItemCount(priguns_tear_letter4) < 1)
                            htmltext = "guard_moretti_q0407_04.htm";
                        else if (st.ownItemCount(priguns_tear_letter1) + st.ownItemCount(priguns_tear_letter2) + st.ownItemCount(priguns_tear_letter3) + st.ownItemCount(priguns_tear_letter4) < 4)
                            htmltext = "guard_moretti_q0407_05.htm";
                        else if (st.ownItemCount(priguns_tear_letter1) + st.ownItemCount(priguns_tear_letter2) + st.ownItemCount(priguns_tear_letter3) + st.ownItemCount(priguns_tear_letter4) == 4) {
                            st.takeItems(priguns_tear_letter1, 1);
                            st.takeItems(priguns_tear_letter2, 1);
                            st.takeItems(priguns_tear_letter3, 1);
                            st.takeItems(priguns_tear_letter4, 1);
                            st.giveItems(morettis_herb, 1);
                            st.giveItems(morettis_letter, 1);
                            st.setCond(4);
                            st.soundEffect(SOUND_MIDDLE);
                            htmltext = "guard_moretti_q0407_06.htm";
                        }
                    } else if (st.ownItemCount(priguns_letter) > 0) {
                        st.takeItems(priguns_letter, 1);
                        st.giveItems(honorary_guard, 1);
                        st.setCond(8);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "guard_moretti_q0407_07.htm";
                    } else if (st.ownItemCount(morettis_herb) > 0 && st.ownItemCount(morettis_letter) > 0)
                        htmltext = "guard_moretti_q0407_09.htm";
                    else if (st.ownItemCount(honorary_guard) > 0)
                        htmltext = "guard_moretti_q0407_08.htm";
                } else if (npcId == prigun) {
                    if (st.ownItemCount(morettis_letter) > 0 && st.ownItemCount(morettis_herb) > 0 && st.ownItemCount(rusted_key) < 1) {
                        st.setCond(5);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "prigun_q0407_01.htm";
                    } else if (st.ownItemCount(morettis_letter) > 0 && st.ownItemCount(morettis_herb) > 0 && st.ownItemCount(rusted_key) > 0) {
                        st.setCond(7);
                        st.takeItems(rusted_key, 1);
                        st.takeItems(morettis_herb, 1);
                        st.takeItems(morettis_letter, 1);
                        st.giveItems(priguns_letter, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "prigun_q0407_02.htm";
                    } else if (st.ownItemCount(priguns_letter) > 0)
                        htmltext = "prigun_q0407_04.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("path_to_elven_scout");
        int npcId = npc.getNpcId();
        if (npcId == ol_mahum_sentry) {
            if (st.ownItemCount(morettis_herb) == 1 && st.ownItemCount(morettis_letter) == 1 && st.ownItemCount(rusted_key) == 0 && Rnd.get(10) < 6) {
                st.setCond(6);
                st.giveItems(rusted_key, 1);
                st.soundEffect(SOUND_MIDDLE);
            }
        } else if (npcId == ol_mahum_patrol) {
            if (GetMemoState == 1) {
                if (st.ownItemCount(priguns_tear_letter1) + st.ownItemCount(priguns_tear_letter2) + st.ownItemCount(priguns_tear_letter3) + st.ownItemCount(priguns_tear_letter4) < 4) {
                    if (st.ownItemCount(priguns_tear_letter1) < 1) {
                        st.giveItems(priguns_tear_letter1, 1);
                        if (st.ownItemCount(priguns_tear_letter1) + st.ownItemCount(priguns_tear_letter2) + st.ownItemCount(priguns_tear_letter3) + st.ownItemCount(priguns_tear_letter4) >= 4) {
                            st.setCond(3);
                            st.soundEffect(SOUND_MIDDLE);
                        } else
                            st.soundEffect(SOUND_ITEMGET);
                    } else if (st.ownItemCount(priguns_tear_letter2) < 1) {
                        st.giveItems(priguns_tear_letter2, 1);
                        if (st.ownItemCount(priguns_tear_letter1) + st.ownItemCount(priguns_tear_letter2) + st.ownItemCount(priguns_tear_letter3) + st.ownItemCount(priguns_tear_letter4) >= 4) {
                            st.setCond(3);
                            st.soundEffect(SOUND_MIDDLE);
                        } else
                            st.soundEffect(SOUND_ITEMGET);
                    } else if (st.ownItemCount(priguns_tear_letter3) < 1) {
                        st.giveItems(priguns_tear_letter3, 1);
                        if (st.ownItemCount(priguns_tear_letter1) + st.ownItemCount(priguns_tear_letter2) + st.ownItemCount(priguns_tear_letter3) + st.ownItemCount(priguns_tear_letter4) >= 4) {
                            st.setCond(3);
                            st.soundEffect(SOUND_MIDDLE);
                        } else
                            st.soundEffect(SOUND_ITEMGET);
                    } else if (st.ownItemCount(priguns_tear_letter4) < 1) {
                        st.giveItems(priguns_tear_letter4, 1);
                        if (st.ownItemCount(priguns_tear_letter1) + st.ownItemCount(priguns_tear_letter2) + st.ownItemCount(priguns_tear_letter3) + st.ownItemCount(priguns_tear_letter4) >= 4) {
                            st.setCond(3);
                            st.soundEffect(SOUND_MIDDLE);
                        } else
                            st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        }
        return null;
    }
}
