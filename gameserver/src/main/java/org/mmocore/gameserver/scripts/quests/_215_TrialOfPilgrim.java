package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.base.ClassId;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.serverpackets.SocialAction;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.0
 * @date 03/06/2016
 * @lastedit 03/06/2016
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _215_TrialOfPilgrim extends Quest {
    //npc
    private static final int ancestor_martankus = 30649;
    private static final int andellria = 30362;
    private static final int gauri_twinklerock = 30550;
    private static final int gerald_priest_of_earth = 30650;
    private static final int hermit_santiago = 30648;
    private static final int potter = 30036;
    private static final int primoz = 30117;
    private static final int sage_kasian = 30612;
    private static final int seer_tanapi = 30571;
    private static final int uruha = 30652;
    private static final int wanderer_dorf = 30651;
    //mobs
    private static final int lava_salamander = 27116;
    private static final int nahir = 27117;
    private static final int black_willow = 27118;
    //questitem
    private static final int mark_of_pilgrim = 2721;
    private static final int book_of_sage = 2722;
    private static final int voucher_of_trial = 2723;
    private static final int spirit_of_flame = 2724;
    private static final int essense_of_flame = 2725;
    private static final int book_of_gerald = 2726;
    private static final int grey_badge = 2727;
    private static final int picture_of_nahir = 2728;
    private static final int hair_of_nahir = 2729;
    private static final int statue_of_Einhasad = 2730;
    private static final int book_of_darkness = 2731;
    private static final int debris_of_willow = 2732;
    private static final int tag_of_rumor = 2733;
    // etcitem
    private static final int q_dimension_diamond = 7562;

    public _215_TrialOfPilgrim() {
        super(false);
        addStartNpc(hermit_santiago);
        addTalkId(seer_tanapi, ancestor_martankus, gauri_twinklerock, gerald_priest_of_earth, wanderer_dorf, primoz, potter, andellria, uruha, sage_kasian);
        addKillId(lava_salamander, nahir, black_willow);
        addQuestItem(book_of_sage, voucher_of_trial, essense_of_flame, spirit_of_flame, book_of_gerald, grey_badge, picture_of_nahir, hair_of_nahir, statue_of_Einhasad, book_of_darkness, debris_of_willow, tag_of_rumor);
        addLevelCheck(35);
        addClassIdCheck(ClassId.cleric, ClassId.oracle, ClassId.shillien_oracle, ClassId.orc_shaman);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("trial_of_pilgrim");
        int npcId = npc.getNpcId();
        if (npcId == hermit_santiago) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("trial_of_pilgrim", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                if (!st.getPlayer().getPlayerVariables().getBoolean(PlayerVariables.DD1)) {
                    st.giveItems(q_dimension_diamond, 49);
                    st.getPlayer().getPlayerVariables().set(PlayerVariables.DD1, "1", -1);
                    htmltext = "hermit_santiago_q0215_04a.htm";
                } else
                    htmltext = "hermit_santiago_q0215_04.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=215&reply=1"))
                htmltext = "hermit_santiago_q0215_05.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=215&reply=2"))
                htmltext = "hermit_santiago_q0215_06.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=215&reply=3"))
                htmltext = "hermit_santiago_q0215_07.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=215&reply=4"))
                htmltext = "hermit_santiago_q0215_08.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=215&reply=5"))
                htmltext = "hermit_santiago_q0215_05.htm";
        } else if (npcId == ancestor_martankus) {
            if (event.equalsIgnoreCase("menu_select?ask=215&reply=1") && GetMemoState == 4) {
                if (st.ownItemCount(essense_of_flame) > 0) {
                    st.setCond(5);
                    st.setMemoState("trial_of_pilgrim", String.valueOf(5), true);
                    st.giveItems(spirit_of_flame, 1);
                    st.takeItems(essense_of_flame, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "ancestor_martankus_q0215_04.htm";
                }
            }
        } else if (npcId == gerald_priest_of_earth) {
            if (event.equalsIgnoreCase("menu_select?ask=215&reply=1") && GetMemoState == 6 && st.ownItemCount(tag_of_rumor) > 0) {
                if (st.ownItemCount(ADENA_ID) >= 100000) {
                    st.setMemoState("trial_of_pilgrim", String.valueOf(7), true);
                    st.takeItems(ADENA_ID, 100000);
                    st.giveItems(book_of_gerald, 1);
                    htmltext = "gerald_priest_of_earth_q0215_02.htm";
                } else
                    htmltext = "gerald_priest_of_earth_q0215_03.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=215&reply=2") && GetMemoState == 6 && st.ownItemCount(tag_of_rumor) > 0)
                htmltext = "gerald_priest_of_earth_q0215_03.htm";
        } else if (npcId == andellria) {
            if (event.equalsIgnoreCase("menu_select?ask=215&reply=1") && GetMemoState == 15 && st.ownItemCount(book_of_darkness) > 0) {
                st.setCond(16);
                st.setMemoState("trial_of_pilgrim", String.valueOf(16), true);
                st.takeItems(book_of_darkness, -1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "andellria_q0215_05.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=215&reply=2") && GetMemoState == 15 && st.ownItemCount(book_of_darkness) > 0) {
                st.setCond(16);
                st.setMemoState("trial_of_pilgrim", String.valueOf(16), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "andellria_q0215_04.htm";
            }
        } else if (npcId == uruha) {
            if (event.equalsIgnoreCase("menu_select?ask=215&reply=1") && GetMemoState == 14 && st.ownItemCount(debris_of_willow) > 0) {
                st.setCond(15);
                st.setMemoState("trial_of_pilgrim", String.valueOf(15), true);
                st.giveItems(book_of_darkness, 1);
                st.takeItems(debris_of_willow, -1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "uruha_q0215_02.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("trial_of_pilgrim");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == hermit_santiago) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "hermit_santiago_q0215_01.htm";
                            st.exitQuest(true);
                            break;
                        case CLASS_ID:
                            htmltext = "hermit_santiago_q0215_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "hermit_santiago_q0215_03.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == hermit_santiago) {
                    if (GetMemoState >= 1 && st.ownItemCount(book_of_sage) == 0)
                        htmltext = "hermit_santiago_q0215_09.htm";
                    else if (st.ownItemCount(book_of_sage) > 0) {
                        st.addExpAndSp(1258250, 81606);
                        st.giveItems(ADENA_ID, 229298);
                        st.giveItems(mark_of_pilgrim, 1);
                        st.takeItems(book_of_sage, 1);
                        st.takeItems(book_of_gerald, -1);
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                        st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                        htmltext = "hermit_santiago_q0215_10.htm";
                    }
                } else if (npcId == seer_tanapi) {
                    if (GetMemoState == 1 && st.ownItemCount(voucher_of_trial) > 0) {
                        st.setCond(2);
                        st.setMemoState("trial_of_pilgrim", String.valueOf(2), true);
                        st.takeItems(voucher_of_trial, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "seer_tanapi_q0215_01.htm";
                    } else if (GetMemoState == 2)
                        htmltext = "seer_tanapi_q0215_02.htm";
                    else if (GetMemoState == 5 && st.ownItemCount(spirit_of_flame) > 0) {
                        st.setCond(6);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "seer_tanapi_q0215_03.htm";
                    }
                } else if (npcId == ancestor_martankus) {
                    if (GetMemoState == 2) {
                        st.setCond(3);
                        st.setMemoState("trial_of_pilgrim", String.valueOf(3), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "ancestor_martankus_q0215_01.htm";
                    } else if (GetMemoState == 3)
                        htmltext = "ancestor_martankus_q0215_02.htm";
                    else if (GetMemoState == 4 && st.ownItemCount(essense_of_flame) > 0)
                        htmltext = "ancestor_martankus_q0215_03.htm";
                } else if (npcId == gauri_twinklerock) {
                    if (GetMemoState == 5 && st.ownItemCount(spirit_of_flame) > 0) {
                        st.setCond(7);
                        st.setMemoState("trial_of_pilgrim", String.valueOf(6), true);
                        st.giveItems(tag_of_rumor, 1);
                        st.takeItems(spirit_of_flame, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "gauri_twinklerock_q0215_01.htm";
                    } else if (GetMemoState == 6)
                        htmltext = "gauri_twinklerock_q0215_02.htm";
                } else if (npcId == gerald_priest_of_earth) {
                    if (GetMemoState == 6 && st.ownItemCount(tag_of_rumor) > 0)
                        htmltext = "gerald_priest_of_earth_q0215_01.htm";
                    else if (st.ownItemCount(grey_badge) > 0 && st.ownItemCount(book_of_gerald) > 0) {
                        st.giveItems(ADENA_ID, 100000);
                        st.takeItems(book_of_gerald, 1);
                        htmltext = "gerald_priest_of_earth_q0215_04.htm";
                    }
                } else if (npcId == wanderer_dorf) {
                    if (GetMemoState == 6 && st.ownItemCount(tag_of_rumor) > 0) {
                        st.setMemoState("trial_of_pilgrim", String.valueOf(8), true);
                        st.giveItems(grey_badge, 1);
                        st.takeItems(tag_of_rumor, 1);
                        htmltext = "wanderer_dorf_q0215_01.htm";
                    } else if (GetMemoState == 7 && st.ownItemCount(tag_of_rumor) > 0) {
                        st.setMemoState("trial_of_pilgrim", String.valueOf(8), true);
                        st.giveItems(grey_badge, 1);
                        st.takeItems(tag_of_rumor, 1);
                        htmltext = "wanderer_dorf_q0215_02.htm";
                    } else if (GetMemoState == 8) {
                        st.setCond(8);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "wanderer_dorf_q0215_03.htm";
                    }
                } else if (npcId == primoz) {
                    if (GetMemoState == 8) {
                        st.setCond(9);
                        st.setMemoState("trial_of_pilgrim", String.valueOf(9), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "primoz_q0215_01.htm";
                    } else if (GetMemoState == 9) {
                        st.setCond(9);
                        st.setMemoState("trial_of_pilgrim", String.valueOf(9), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "primoz_q0215_02.htm";
                    }
                } else if (npcId == potter) {
                    if (GetMemoState == 9) {
                        st.setCond(10);
                        st.setMemoState("trial_of_pilgrim", String.valueOf(10), true);
                        st.giveItems(picture_of_nahir, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "potter_q0215_01.htm";
                    } else if (GetMemoState == 10)
                        htmltext = "potter_q0215_02.htm";
                    else if (GetMemoState == 11) {
                        st.setCond(12);
                        st.setMemoState("trial_of_pilgrim", String.valueOf(12), true);
                        st.giveItems(statue_of_Einhasad, 1);
                        st.takeItems(picture_of_nahir, -1);
                        st.takeItems(hair_of_nahir, -1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "potter_q0215_03.htm";
                    } else if (GetMemoState == 12 && st.ownItemCount(statue_of_Einhasad) > 0)
                        htmltext = "potter_q0215_04.htm";
                } else if (npcId == andellria) {
                    if (GetMemoState == 12) {
                        if (st.getPlayer().getLevel() >= 35) {
                            st.setCond(13);
                            st.setMemoState("trial_of_pilgrim", String.valueOf(13), true);
                            st.soundEffect(SOUND_MIDDLE);
                            htmltext = "andellria_q0215_01.htm";
                        } else
                            htmltext = "andellria_q0215_01a.htm";
                    } else if (GetMemoState == 13)
                        htmltext = "andellria_q0215_02.htm";
                    else if (GetMemoState == 14)
                        htmltext = "andellria_q0215_02a.htm";
                    else if (GetMemoState == 15 && st.ownItemCount(book_of_darkness) > 0)
                        htmltext = "andellria_q0215_03.htm";
                    else if (GetMemoState == 16)
                        htmltext = "andellria_q0215_06.htm";
                    else if (GetMemoState == 15 && st.ownItemCount(book_of_darkness) == 0)
                        htmltext = "andellria_q0215_07.htm";
                } else if (npcId == uruha) {
                    if (GetMemoState == 14 && st.ownItemCount(debris_of_willow) > 0)
                        htmltext = "uruha_q0215_01.htm";
                    else if (GetMemoState == 15 && st.ownItemCount(book_of_darkness) > 0)
                        htmltext = "uruha_q0215_03.htm";
                } else if (npcId == sage_kasian) {
                    if (GetMemoState == 16) {
                        st.setMemoState("trial_of_pilgrim", String.valueOf(17), true);
                        st.takeItems(grey_badge, -1);
                        st.takeItems(spirit_of_flame, -1);
                        st.takeItems(statue_of_Einhasad, -1);
                        if (st.ownItemCount(book_of_sage) == 0) {
                            st.giveItems(book_of_sage, 1);
                        }
                        if (st.ownItemCount(book_of_darkness) > 0) {
                            st.addExpAndSp(5000, 500);
                            st.takeItems(book_of_darkness, 1);
                        }
                        htmltext = "sage_kasian_q0215_01.htm";
                    } else if (GetMemoState == 17) {
                        st.setCond(17);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "sage_kasian_q0215_02.htm";
                    }
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("trial_of_pilgrim");
        int npcId = npc.getNpcId();
        if (npcId == lava_salamander) {
            if (GetMemoState == 3 && st.ownItemCount(essense_of_flame) == 0) {
                if (Rnd.get(5) <= 5) {
                    st.setCond(4);
                    st.setMemoState("trial_of_pilgrim", String.valueOf(4), true);
                    st.giveItems(essense_of_flame, 1);
                    st.soundEffect(SOUND_MIDDLE);
                }
            }
        } else if (npcId == nahir) {
            if (GetMemoState == 10 && st.ownItemCount(hair_of_nahir) == 0) {
                if (Rnd.get(5) <= 5) {
                    st.setCond(11);
                    st.setMemoState("trial_of_pilgrim", String.valueOf(11), true);
                    st.giveItems(hair_of_nahir, 1);
                    st.soundEffect(SOUND_MIDDLE);
                }
            }
        } else if (npcId == black_willow) {
            if (GetMemoState == 13 && st.ownItemCount(debris_of_willow) == 0) {
                if (Rnd.get(5) <= 5) {
                    st.setCond(14);
                    st.setMemoState("trial_of_pilgrim", String.valueOf(14), true);
                    st.giveItems(debris_of_willow, 1);
                    st.soundEffect(SOUND_MIDDLE);
                }
            }
        }
        return null;
    }
}