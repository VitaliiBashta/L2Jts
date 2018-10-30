package org.mmocore.gameserver.scripts.quests;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.gameserver.model.base.ClassId;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;

/**
 * Квест на вторую профессию Test Of Duelist
 */
public class _222_TestOfDuelist extends Quest {
    //NPC
    private static final int Kaien = 30623;
    //Quest Items
    private static final int OrderGludio = 2763;
    private static final int OrderDion = 2764;
    private static final int OrderGiran = 2765;
    private static final int OrderOren = 2766;
    private static final int OrderAden = 2767;
    private static final int PunchersShard = 2768;
    private static final int NobleAntsFeeler = 2769;
    private static final int DronesChitin = 2770;
    private static final int DeadSeekerFang = 2771;
    private static final int OverlordNecklace = 2772;
    private static final int FetteredSoulsChain = 2773;
    private static final int ChiefsAmulet = 2774;
    private static final int EnchantedEyeMeat = 2775;
    private static final int TamrinOrcsRing = 2776;
    private static final int TamrinOrcsArrow = 2777;
    private static final int FinalOrder = 2778;
    private static final int ExcurosSkin = 2779;
    private static final int KratorsShard = 2780;
    private static final int GrandisSkin = 2781;
    private static final int TimakOrcsBelt = 2782;
    private static final int LakinsMace = 2783;
    //Items
    private static final int MarkOfDuelist = 2762;
    //MOB
    private static final int Puncher = 20085;
    private static final int NobleAntLeader = 20090;
    private static final int MarshStakatoDrone = 20234;
    private static final int DeadSeeker = 20202;
    private static final int BrekaOrcOverlord = 20270;
    private static final int FetteredSoul = 20552;
    private static final int LetoLizardmanOverlord = 20582;
    private static final int EnchantedMonstereye = 20564;
    private static final int TamlinOrc = 20601;
    private static final int TamlinOrcArcher = 20602;
    private static final int Excuro = 20214;
    private static final int Krator = 20217;
    private static final int Grandis = 20554;
    private static final int TimakOrcOverlord = 20588;
    private static final int Lakin = 20604;
    //Drop Cond
    //# [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]
    private static final int[][] DROPLIST_COND = {
            {
                    2,
                    0,
                    Puncher,
                    0,
                    PunchersShard,
                    10,
                    70,
                    1
            },
            {
                    2,
                    0,
                    NobleAntLeader,
                    0,
                    NobleAntsFeeler,
                    10,
                    70,
                    1
            },
            {
                    2,
                    0,
                    MarshStakatoDrone,
                    0,
                    DronesChitin,
                    10,
                    70,
                    1
            },
            {
                    2,
                    0,
                    DeadSeeker,
                    0,
                    DeadSeekerFang,
                    10,
                    70,
                    1
            },
            {
                    2,
                    0,
                    BrekaOrcOverlord,
                    0,
                    OverlordNecklace,
                    10,
                    70,
                    1
            },
            {
                    2,
                    0,
                    FetteredSoul,
                    0,
                    FetteredSoulsChain,
                    10,
                    70,
                    1
            },
            {
                    2,
                    0,
                    LetoLizardmanOverlord,
                    0,
                    ChiefsAmulet,
                    10,
                    70,
                    1
            },
            {
                    2,
                    0,
                    EnchantedMonstereye,
                    0,
                    EnchantedEyeMeat,
                    10,
                    70,
                    1
            },
            {
                    2,
                    0,
                    TamlinOrc,
                    0,
                    TamrinOrcsRing,
                    10,
                    70,
                    1
            },
            {
                    2,
                    0,
                    TamlinOrcArcher,
                    0,
                    TamrinOrcsArrow,
                    10,
                    70,
                    1
            },
            {
                    4,
                    0,
                    Excuro,
                    0,
                    ExcurosSkin,
                    3,
                    70,
                    1
            },
            {
                    4,
                    0,
                    Krator,
                    0,
                    KratorsShard,
                    3,
                    70,
                    1
            },
            {
                    4,
                    0,
                    Grandis,
                    0,
                    GrandisSkin,
                    3,
                    70,
                    1
            },
            {
                    4,
                    0,
                    TimakOrcOverlord,
                    0,
                    TimakOrcsBelt,
                    3,
                    70,
                    1
            },
            {
                    4,
                    0,
                    Lakin,
                    0,
                    LakinsMace,
                    3,
                    70,
                    1
            }
    };


    public _222_TestOfDuelist() {
        super(false);
        addStartNpc(Kaien);
        //Mob Drop
        for (int i = 0; i < DROPLIST_COND.length; i++) {
            addKillId(DROPLIST_COND[i][2]);
            addQuestItem(DROPLIST_COND[i][4]);
        }
        addQuestItem(OrderGludio, OrderDion, OrderGiran, OrderOren, OrderAden, FinalOrder);
        addLevelCheck(39);
        addClassIdCheck(ClassId.warrior, ClassId.orc_monk, ClassId.elven_knight, ClassId.palus_knight);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equalsIgnoreCase("30623-04.htm") && st.getPlayer().getPlayerTemplateComponent().getPlayerRace() == PlayerRace.orc) {
            htmltext = "30623-05.htm";
        } else if (event.equalsIgnoreCase("30623-07.htm")) {
            st.setCond(2);
            st.setState(STARTED);
            st.giveItems(OrderGludio, 1);
            st.giveItems(OrderDion, 1);
            st.giveItems(OrderGiran, 1);
            st.giveItems(OrderOren, 1);
            st.giveItems(OrderAden, 1);
            if (!st.getPlayer().getPlayerVariables().getBoolean(PlayerVariables.DD3)) {
                st.giveItems(7562, 72, false);
                st.getPlayer().getPlayerVariables().set(PlayerVariables.DD3, "1", -1);
            }
            st.soundEffect(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("30623-16.htm")) {
            st.takeItems(PunchersShard, -1);
            st.takeItems(NobleAntsFeeler, -1);
            st.takeItems(DronesChitin, -1);
            st.takeItems(DeadSeekerFang, -1);
            st.takeItems(OverlordNecklace, -1);
            st.takeItems(FetteredSoulsChain, -1);
            st.takeItems(ChiefsAmulet, -1);
            st.takeItems(EnchantedEyeMeat, -1);
            st.takeItems(TamrinOrcsRing, -1);
            st.takeItems(TamrinOrcsArrow, -1);
            st.takeItems(OrderGludio, -1);
            st.takeItems(OrderDion, -1);
            st.takeItems(OrderGiran, -1);
            st.takeItems(OrderOren, -1);
            st.takeItems(OrderAden, -1);
            st.giveItems(FinalOrder, 1);
            st.setCond(4);
            st.setState(STARTED);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npcId == Kaien) {
            if (st.ownItemCount(MarkOfDuelist) != 0) {
                htmltext = "completed";
                st.exitQuest(true);
            } else if (cond == 0) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = "30623-01.htm";
                        st.exitQuest(true);
                        break;
                    case RACE:
                        htmltext = "30623-02.htm";
                        st.exitQuest(true);
                        break;
                    default:
                        htmltext = "30623-03.htm";
                        break;
                }
            } else if (cond == 2) {
                htmltext = "30623-14.htm";
            } else if (cond == 3) {
                htmltext = "30623-13.htm";
            } else if (cond == 4) {
                htmltext = "30623-17.htm";
            } else if (cond == 5) {
                st.giveItems(MarkOfDuelist, 1);
                if (!st.getPlayer().getPlayerVariables().getBoolean(PlayerVariables.PROF2_3)) {
                    st.addExpAndSp(474444, 30704);
                    st.giveItems(ADENA_ID, 80000); // FIXME: цифра с потолка
                    st.getPlayer().getPlayerVariables().set(PlayerVariables.PROF2_3, "1", -1);
                }
                htmltext = "30623-18.htm";
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
            }
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        for (int i = 0; i < DROPLIST_COND.length; i++) {
            if (cond == DROPLIST_COND[i][0] && npcId == DROPLIST_COND[i][2]) {
                if (DROPLIST_COND[i][3] == 0 || st.ownItemCount(DROPLIST_COND[i][3]) > 0) {
                    if (DROPLIST_COND[i][5] == 0) {
                        st.rollAndGive(DROPLIST_COND[i][4], DROPLIST_COND[i][7], DROPLIST_COND[i][6]);
                    } else if (st.rollAndGive(DROPLIST_COND[i][4], DROPLIST_COND[i][7], DROPLIST_COND[i][7], DROPLIST_COND[i][5], DROPLIST_COND[i][6])) {
                        if (DROPLIST_COND[i][1] != cond && DROPLIST_COND[i][1] != 0) {
                            st.setCond(Integer.valueOf(DROPLIST_COND[i][1]));
                            st.setState(STARTED);
                        }
                    }
                }
            }
        }
        if (cond == 2 && st.ownItemCount(PunchersShard) >= 10 && st.ownItemCount(NobleAntsFeeler) >= 10 && st.ownItemCount(DronesChitin) >= 10 && st.ownItemCount(DeadSeekerFang) >= 10 && st.ownItemCount(OverlordNecklace) >= 10 && st.ownItemCount(FetteredSoulsChain) >= 10 && st.ownItemCount(ChiefsAmulet) >= 10 && st.ownItemCount(EnchantedEyeMeat) >= 10 && st.ownItemCount(TamrinOrcsRing) >= 10 && st.ownItemCount(TamrinOrcsArrow) >= 10) {
            st.setCond(3);
            st.setState(STARTED);
        } else if (cond == 4 && st.ownItemCount(ExcurosSkin) >= 3 && st.ownItemCount(KratorsShard) >= 3 && st.ownItemCount(LakinsMace) >= 3 && st.ownItemCount(GrandisSkin) >= 3 && st.ownItemCount(TimakOrcsBelt) >= 3) {
            st.setCond(5);
            st.setState(STARTED);
        }
        return null;
    }
}