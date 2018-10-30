package org.mmocore.gameserver.scripts.quests;

import org.jts.dataparser.data.holder.petdata.PetUtils.PetId;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.object.Servitor;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.0
 * @date 16/01/2016
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _422_RepentYourSins extends Quest {
    // npc
    private final static int black_judge = 30981;
    private final static int katari = 30668;
    private final static int piotur = 30597;
    private final static int sage_kasian = 30612;
    private final static int magister_joan = 30718;
    private final static int blacksmith_pushkin = 30300;
    // mobs
    private final static int scavenger_wererat = 20039;
    private final static int turek_war_hound = 20494;
    private final static int tyrant_kingpin = 20193;
    private final static int trisalim_tote = 20561;
    // questitem
    private final static int q0422_rats_head = 4326;
    private final static int q0422_dogs_tail = 4327;
    private final static int q0422_tyrants_heart = 4328;
    private final static int q0422_trisalims_poison = 4329;
    private final static int q0422_manual_of_manacles = 4331;
    private final static int q0422_manacles = 4330;
    private final static int manacles_of_redeemed = 4426;
    // etcitem
    private final static int manacles_of_redemption = 4425;
    private final static int silver_nugget = 1873;
    private final static int admantite_nugget = 1877;
    private final static int blacksmiths_frame = 1892;
    private final static int cokes = 1879;
    private final static int steel = 1880;
    private final static int sin_eater = PetId.SIN_EATER_ID;

    public _422_RepentYourSins() {
        super(false);
        addStartNpc(black_judge);
        addTalkId(katari, piotur, sage_kasian, magister_joan, blacksmith_pushkin);
        addKillId(scavenger_wererat, turek_war_hound, tyrant_kingpin, trisalim_tote);
        addQuestItem(q0422_rats_head, q0422_dogs_tail, q0422_tyrants_heart, q0422_trisalims_poison, q0422_manacles, q0422_manual_of_manacles, manacles_of_redemption);
        addLevelCheck(0);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        ItemInstance item0;
        int i1;
        Servitor c0 = st.getPlayer().getServitor();
        int npcId = npc.getNpcId();
        int talker_level = st.getPlayer().getLevel();
        int talker_pk_count = st.getPlayer().getPkKills();
        int GetMemoState = st.getInt("brother_chained_to_you");
        int GetMemoStateEx = st.getInt("brother_chained_to_you_ex");
        if (npcId == black_judge) {
            if (event.equalsIgnoreCase("quest_accept")) {
                if (talker_level > 20 && talker_level < 31) {
                    st.setCond(3);
                    st.setMemoState("brother_chained_to_you", String.valueOf(2), true);
                    st.setState(STARTED);
                    st.soundEffect(SOUND_ACCEPT);
                    htmltext = "black_judge_q0422_04.htm";
                } else if (talker_level < 21) {
                    st.setCond(1);
                    st.setCond(2);
                    st.setMemoState("brother_chained_to_you", String.valueOf(1), true);
                    st.setMemoState("brother_chained_to_you_ex", String.valueOf(0), true);
                    st.setState(STARTED);
                    st.soundEffect(SOUND_ACCEPT);
                    htmltext = "black_judge_q0422_03.htm";
                } else if (talker_level > 30 && talker_level < 41) {
                    st.setCond(4);
                    st.setMemoState("brother_chained_to_you", String.valueOf(3), true);
                    st.setState(STARTED);
                    st.soundEffect(SOUND_ACCEPT);
                    htmltext = "black_judge_q0422_05.htm";
                } else {
                    st.setCond(5);
                    st.setMemoState("brother_chained_to_you", String.valueOf(4), true);
                    st.setState(STARTED);
                    st.soundEffect(SOUND_ACCEPT);
                    htmltext = "black_judge_q0422_06.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=422&reply=1")) {
                if (GetMemoState >= 9 && GetMemoState <= 12 && (st.ownItemCount(manacles_of_redeemed) >= 1 || st.ownItemCount(q0422_manacles) >= 1)) {
                    if (st.ownItemCount(q0422_manacles) > 0) {
                        st.takeItems(q0422_manacles, 1);
                    }
                    if (st.ownItemCount(manacles_of_redeemed) > 0) {
                        st.takeItems(manacles_of_redeemed, 1);
                    }
                    st.setCond(16);
                    st.giveItems(manacles_of_redemption, 1);
                    st.setMemoState("brother_chained_to_you_ex", String.valueOf(talker_level), true);
                    htmltext = "black_judge_q0422_11.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=422&reply=2")) {
                if (GetMemoState >= 9 && GetMemoState <= 12)
                    htmltext = "black_judge_q0422_14.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=422&reply=3")) {
                item0 = st.getPlayer().getInventory().getItemByItemId(manacles_of_redemption);
                if (GetMemoState >= 9 && GetMemoState <= 12 && item0.getEnchantLevel() > GetMemoStateEx) {
                    if (c0 != null && c0.getNpcId() == sin_eater) {
                        htmltext = "black_judge_q0422_15t.htm";
                    } else {
                        if (talker_level > GetMemoStateEx) {
                            i1 = item0.getEnchantLevel() - GetMemoStateEx - (talker_level - GetMemoStateEx);
                        } else {
                            i1 = item0.getEnchantLevel() - GetMemoStateEx;
                        }
                        if (i1 < 0) {
                            i1 = 0;
                        }
                        int i0 = Rnd.get(10 * i1) + 1;
                        if (talker_pk_count <= i0) {
                            if (st.ownItemCount(manacles_of_redeemed) < 1)
                                st.giveItems(manacles_of_redeemed, 1);
                            st.getPlayer().setPkKills(0);
                            st.soundEffect(SOUND_FINISH);
                            st.exitQuest(true);
                            htmltext = "black_judge_q0422_15.htm";
                        } else {
                            st.takeItems(manacles_of_redemption, 1);
                            st.giveItems(manacles_of_redeemed, 1);
                            st.setMemoState("brother_chained_to_you_ex", String.valueOf(0), true);
                            st.getPlayer().setPkKills(st.getPlayer().getPkKills() - i0);
                            htmltext = "black_judge_q0422_16.htm";
                        }
                    }
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=422&reply=4")) {
                if (GetMemoState >= 9 && GetMemoState <= 12)
                    htmltext = "black_judge_q0422_17.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=422&reply=5")) {
                if (GetMemoState >= 9 && GetMemoState <= 12) {
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(true);
                    htmltext = "black_judge_q0422_18.htm";
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        ItemInstance item0;
        int npcId = npc.getNpcId();
        int GetMemoState = st.getInt("brother_chained_to_you");
        int GetMemoStateEx = st.getInt("brother_chained_to_you_ex");
        int id = st.getState();
        int talker_pk_count = st.getPlayer().getPkKills();
        switch (id) {
            case CREATED:
                if (npcId == black_judge) {
                    if (talker_pk_count == 0) {
                        System.out.println("Count PK: " + talker_pk_count);
                        htmltext = "black_judge_q0422_01.htm";
                    } else {
                        System.out.println("Accept Quest, Count PK: " + talker_pk_count);
                        htmltext = "black_judge_q0422_02.htm";
                    }
                }
                break;
            case STARTED:
                if (npcId == black_judge) {
                    if (GetMemoState == 1000)
                        st.takeItems(manacles_of_redemption, 1);
                    else if (GetMemoState < 9)
                        htmltext = "black_judge_q0422_07.htm";
                    else if (GetMemoState >= 9 && GetMemoState <= 12 && st.ownItemCount(q0422_manual_of_manacles) == 0 && st.ownItemCount(manacles_of_redeemed) == 0 && st.ownItemCount(q0422_manacles) == 0 && st.ownItemCount(manacles_of_redemption) == 0) {
                        st.setCond(14);
                        st.giveItems(q0422_manual_of_manacles, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "black_judge_q0422_08.htm";
                    } else if (GetMemoState >= 9 && GetMemoState <= 12 && st.ownItemCount(q0422_manual_of_manacles) >= 1 && st.ownItemCount(manacles_of_redeemed) == 0 && st.ownItemCount(q0422_manacles) == 0 && st.ownItemCount(manacles_of_redemption) == 0)
                        htmltext = "black_judge_q0422_09.htm";
                    else if (GetMemoState >= 9 && GetMemoState <= 12 && st.ownItemCount(q0422_manual_of_manacles) == 0 && st.ownItemCount(manacles_of_redeemed) == 0 && st.ownItemCount(q0422_manacles) >= 1 && st.ownItemCount(manacles_of_redemption) == 0)
                        htmltext = "black_judge_q0422_10.htm";
                    else if (GetMemoState >= 9 && GetMemoState <= 12 && st.ownItemCount(manacles_of_redemption) >= 1) {
                        item0 = st.getPlayer().getInventory().getItemByItemId(manacles_of_redemption);
                        if (item0 != null) {
                            if (item0.getEnchantLevel() < GetMemoStateEx + 1)
                                htmltext = "black_judge_q0422_12.htm";
                            else
                                htmltext = "black_judge_q0422_13.htm";
                        }
                    } else if (GetMemoState >= 9 && GetMemoState <= 12 && st.ownItemCount(manacles_of_redeemed) >= 1 && st.ownItemCount(manacles_of_redemption) == 0)
                        htmltext = "black_judge_q0422_16t.htm";
                } else if (npcId == katari) {
                    if (GetMemoState == 1) {
                        st.setCond(6);
                        st.setMemoState("brother_chained_to_you", String.valueOf(5), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "katari_q0422_01.htm";
                    } else if (GetMemoState == 5 && st.ownItemCount(q0422_rats_head) < 10)
                        htmltext = "katari_q0422_02.htm";
                    else if (GetMemoState == 5 && st.ownItemCount(q0422_rats_head) >= 10) {
                        st.setCond(10);
                        st.setMemoState("brother_chained_to_you", String.valueOf(9), true);
                        st.takeItems(q0422_rats_head, -1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "katari_q0422_03.htm";
                    } else if (GetMemoState == 9)
                        htmltext = "katari_q0422_04.htm";
                } else if (npcId == piotur) {
                    if (GetMemoState == 2) {
                        st.setCond(7);
                        st.setMemoState("brother_chained_to_you", String.valueOf(6), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "piotur_q0422_01.htm";
                    } else if (GetMemoState == 6 && st.ownItemCount(q0422_dogs_tail) < 10)
                        htmltext = "piotur_q0422_02.htm";
                    else if (GetMemoState == 6 && st.ownItemCount(q0422_dogs_tail) >= 10) {
                        st.setCond(11);
                        st.setMemoState("brother_chained_to_you", String.valueOf(10), true);
                        st.takeItems(q0422_dogs_tail, -1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "piotur_q0422_03.htm";
                    } else if (GetMemoState == 10)
                        htmltext = "piotur_q0422_04.htm";
                } else if (npcId == sage_kasian) {
                    if (GetMemoState == 3) {
                        st.setCond(8);
                        st.setMemoState("brother_chained_to_you", String.valueOf(7), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "sage_kasian_q0422_01.htm";
                    } else if (GetMemoState == 7 && st.ownItemCount(q0422_dogs_tail) < 1)
                        htmltext = "sage_kasian_q0422_02.htm";
                    else if (GetMemoState == 7 && st.ownItemCount(q0422_dogs_tail) >= 1) {
                        st.setCond(12);
                        st.setMemoState("brother_chained_to_you", String.valueOf(11), true);
                        st.takeItems(q0422_tyrants_heart, -1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "sage_kasian_q0422_03.htm";
                    } else if (GetMemoState == 11)
                        htmltext = "sage_kasian_q0422_04.htm";
                } else if (npcId == magister_joan) {
                    if (GetMemoState == 4) {
                        st.setCond(9);
                        st.setMemoState("brother_chained_to_you", String.valueOf(8), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "magister_joan_q0422_01.htm";
                    } else if (GetMemoState == 8 && st.ownItemCount(q0422_trisalims_poison) < 3)
                        htmltext = "magister_joan_q0422_02.htm";
                    else if (GetMemoState == 8 && st.ownItemCount(q0422_trisalims_poison) >= 3) {
                        st.setCond(13);
                        st.setMemoState("brother_chained_to_you", String.valueOf(12), true);
                        st.takeItems(q0422_trisalims_poison, -1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "magister_joan_q0422_03.htm";
                    } else if (GetMemoState == 12)
                        htmltext = "magister_joan_q0422_04.htm";
                } else if (npcId == blacksmith_pushkin) {
                    if (GetMemoState >= 9 && GetMemoState <= 12 && st.ownItemCount(q0422_manual_of_manacles) >= 1 && st.ownItemCount(blacksmiths_frame) > 0 && st.ownItemCount(steel) >= 5 && st.ownItemCount(admantite_nugget) >= 2 && st.ownItemCount(silver_nugget) >= 10 && st.ownItemCount(cokes) >= 10 && st.ownItemCount(q0422_manacles) == 0 && st.ownItemCount(manacles_of_redemption) == 0 && st.ownItemCount(manacles_of_redeemed) == 0) {
                        st.setCond(15);
                        st.takeItems(q0422_manual_of_manacles, 1);
                        st.takeItems(blacksmiths_frame, 1);
                        st.takeItems(steel, 5);
                        st.takeItems(admantite_nugget, 2);
                        st.takeItems(silver_nugget, 10);
                        st.takeItems(cokes, 10);
                        st.giveItems(q0422_manacles, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "blacksmith_pushkin_q0422_01.htm";
                    } else if (GetMemoState >= 9 && GetMemoState <= 12 && st.ownItemCount(q0422_manual_of_manacles) >= 1 && (st.ownItemCount(blacksmiths_frame) == 0 || st.ownItemCount(steel) < 5 || st.ownItemCount(admantite_nugget) < 2 || st.ownItemCount(silver_nugget) < 10 || st.ownItemCount(cokes) < 10) && st.ownItemCount(q0422_manacles) == 0 && st.ownItemCount(manacles_of_redemption) == 0 && st.ownItemCount(manacles_of_redeemed) == 0) {
                        htmltext = "blacksmith_pushkin_q0422_02.htm";
                    } else if (GetMemoState >= 9 && GetMemoState <= 12 && (st.ownItemCount(q0422_manacles) == 1 || st.ownItemCount(manacles_of_redemption) == 1 || st.ownItemCount(manacles_of_redeemed) == 1)) {
                        htmltext = "blacksmith_pushkin_q0422_03.htm";
                    }
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int GetMemoState = st.getInt("brother_chained_to_you");
        if (npcId == scavenger_wererat) {
            if (GetMemoState == 5 && st.ownItemCount(q0422_rats_head) < 10) {
                if (st.ownItemCount(q0422_rats_head) == 9) {
                    st.giveItems(q0422_rats_head, 1);
                    st.soundEffect(SOUND_MIDDLE);
                } else {
                    st.giveItems(q0422_rats_head, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == turek_war_hound) {
            if (GetMemoState == 6 && st.ownItemCount(q0422_dogs_tail) < 10) {
                if (st.ownItemCount(q0422_dogs_tail) == 9) {
                    st.giveItems(q0422_dogs_tail, 1);
                    st.soundEffect(SOUND_MIDDLE);
                } else {
                    st.giveItems(q0422_dogs_tail, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == tyrant_kingpin) {
            if (GetMemoState == 7 && st.ownItemCount(q0422_tyrants_heart) < 1) {
                st.giveItems(q0422_tyrants_heart, 1);
                st.soundEffect(SOUND_MIDDLE);
            }
        } else if (npcId == trisalim_tote) {
            if (GetMemoState == 8 && st.ownItemCount(q0422_trisalims_poison) < 3) {
                if (st.ownItemCount(q0422_trisalims_poison) == 2) {
                    st.giveItems(q0422_trisalims_poison, 1);
                    st.soundEffect(SOUND_MIDDLE);
                } else {
                    st.giveItems(q0422_trisalims_poison, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        }
        return null;
    }
}