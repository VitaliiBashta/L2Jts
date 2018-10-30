package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.NpcUtils;

public class _171_ActsOfEvil extends Quest {
    //NPC
    private static final int Alvah = 30381;
    private static final int Tyra = 30420;
    private static final int Arodin = 30207;
    private static final int Rolento = 30437;
    private static final int Neti = 30425;
    private static final int Burai = 30617;
    //Quest Item
    private static final int BladeMold = 4239;
    private static final int OlMahumCaptainHead = 4249;
    private static final int TyrasBill = 4240;
    private static final int RangerReportPart1 = 4241;
    private static final int RangerReportPart2 = 4242;
    private static final int RangerReportPart3 = 4243;
    private static final int RangerReportPart4 = 4244;
    private static final int WeaponsTradeContract = 4245;
    private static final int AttackDirectives = 4246;
    private static final int CertificateOfTheSilverScaleGuild = 4247;
    private static final int RolentoCargobox = 4248;
    //MOB
    private static final int TurekOrcArcher = 20496;
    private static final int TurekOrcSkirmisher = 20497;
    private static final int TurekOrcSupplier = 20498;
    private static final int TurekOrcFootman = 20499;
    private static final int TumranBugbear = 20062;
    private static final int OlMahumGeneral = 20438;
    private static final int OlMahumCaptain = 20066;
    private static final int OlMahumSupportTroop = 27190;
    //Drop Cond
    //# [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]
    private static final int[][] DROPLIST_COND = {
            {
                    2,
                    0,
                    TurekOrcArcher,
                    0,
                    BladeMold,
                    20,
                    50,
                    1
            },
            {
                    2,
                    0,
                    TurekOrcSkirmisher,
                    0,
                    BladeMold,
                    20,
                    50,
                    1
            },
            {
                    2,
                    0,
                    TurekOrcSupplier,
                    0,
                    BladeMold,
                    20,
                    50,
                    1
            },
            {
                    2,
                    0,
                    TurekOrcFootman,
                    0,
                    BladeMold,
                    20,
                    50,
                    1
            },
            {
                    10,
                    0,
                    OlMahumGeneral,
                    0,
                    OlMahumCaptainHead,
                    30,
                    100,
                    1
            },
            {
                    10,
                    0,
                    OlMahumCaptain,
                    0,
                    OlMahumCaptainHead,
                    30,
                    100,
                    1
            }
    };
    //Chance Add
    private static final int CHANCE2 = 100;
    private static final int CHANCE21 = 20;
    private static final int CHANCE22 = 20;
    private static final int CHANCE23 = 20;
    private static final int CHANCE24 = 10;
    private static final int CHANCE25 = 10;


    public NpcInstance OlMahumSupportTroop_Spawn;

    public _171_ActsOfEvil() {
        super(false);

        addStartNpc(Alvah);
        addTalkId(Arodin);
        addTalkId(Tyra);
        addTalkId(Rolento);
        addTalkId(Neti);
        addTalkId(Burai);

        addKillId(TumranBugbear);
        addKillId(OlMahumGeneral);
        addKillId(OlMahumSupportTroop);

        addQuestItem(new int[]{
                RolentoCargobox,
                TyrasBill,
                CertificateOfTheSilverScaleGuild,
                RangerReportPart1,
                RangerReportPart2,
                RangerReportPart3,
                RangerReportPart4,
                WeaponsTradeContract,
                AttackDirectives,
                BladeMold,
                OlMahumCaptainHead
        });

        for (int i = 0; i < DROPLIST_COND.length; i++) {
            addKillId(DROPLIST_COND[i][2]);
        }
        addLevelCheck(27, 32);
    }

    private void Despawn_OlMahumSupportTroop() {
        if (OlMahumSupportTroop_Spawn != null) {
            OlMahumSupportTroop_Spawn.deleteMe();
        }
        OlMahumSupportTroop_Spawn = null;
    }

    private void Spawn_OlMahumSupportTroop(QuestState st) {
        OlMahumSupportTroop_Spawn = NpcUtils.spawnSingle(OlMahumSupportTroop, Location.findPointToStay(st.getPlayer(), 50, 100));
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int cond = st.getCond();
        if (event.equals("30381-02.htm") && cond == 0) {
            st.setCond(1);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
        } else if (event.equals("30207-02.htm") && cond == 1) {
            st.setCond(2);
            st.setState(STARTED);
        } else if (event.equals("30381-04.htm") && cond == 4) {
            st.setCond(5);
            st.setState(STARTED);
        } else if (event.equals("30381-07.htm") && cond == 6) {
            st.setCond(7);
            st.setState(STARTED);
            st.takeItems(WeaponsTradeContract, -1);
            st.soundEffect(SOUND_MIDDLE);
        } else if (event.equals("30437-03.htm") && cond == 8) {
            st.giveItems(RolentoCargobox, 1);
            st.giveItems(CertificateOfTheSilverScaleGuild, 1);
            st.setCond(9);
            st.setState(STARTED);
        } else if (event.equals("30617-04.htm") && cond == 9) {
            st.takeItems(CertificateOfTheSilverScaleGuild, -1);
            st.takeItems(AttackDirectives, -1);
            st.takeItems(RolentoCargobox, -1);
            st.setCond(10);
            st.setState(STARTED);
        } else if (event.equals("Wait1")) {
            Despawn_OlMahumSupportTroop();
            st.cancelQuestTimer("Wait1");
            return null;
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npcId == Alvah) {
            if (cond == 0) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = "30381-01a.htm";
                        st.exitQuest(true);
                        break;
                    default:
                        htmltext = "30381-01.htm";
                        break;
                }
            } else if (cond == 1) {
                htmltext = "30381-02a.htm";
            } else if (cond == 4) {
                htmltext = "30381-03.htm";
            } else if (cond == 5) {
                if (st.ownItemCount(RangerReportPart1) > 0 && st.ownItemCount(RangerReportPart2) > 0 && st.ownItemCount(RangerReportPart3) > 0 && st.ownItemCount(RangerReportPart4) > 0) {
                    htmltext = "30381-05.htm";
                    st.takeItems(RangerReportPart1, -1);
                    st.takeItems(RangerReportPart2, -1);
                    st.takeItems(RangerReportPart3, -1);
                    st.takeItems(RangerReportPart4, -1);
                    st.setCond(6);
                    st.setState(STARTED);
                } else {
                    htmltext = "30381-04a.htm";
                }
            } else if (cond == 6) {
                if (st.ownItemCount(WeaponsTradeContract) > 0 && st.ownItemCount(AttackDirectives) > 0) {
                    htmltext = "30381-06.htm";
                } else {
                    htmltext = "30381-05a.htm";
                }
            } else if (cond == 7) {
                htmltext = "30381-07a.htm";
            } else if (cond == 11) {
                htmltext = "30381-08.htm";
                st.giveItems(ADENA_ID, 95000);
                st.addExpAndSp(159820, 9182);
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(false);
            }
        } else if (npcId == Arodin) {
            if (cond == 1) {
                htmltext = "30207-01.htm";
            } else if (cond == 2) {
                htmltext = "30207-01a.htm";
            } else if (cond == 3) {
                if (st.ownItemCount(TyrasBill) > 0) {
                    st.takeItems(TyrasBill, -1);
                    htmltext = "30207-03.htm";
                    st.setCond(4);
                    st.setState(STARTED);
                } else {
                    htmltext = "30207-01a.htm";
                }
            } else if (cond == 4) {
                htmltext = "30207-03a.htm";
            }
        } else if (npcId == Tyra) {
            if (cond == 2) {
                if (st.ownItemCount(BladeMold) >= 20) {
                    st.takeItems(BladeMold, -1);
                    st.giveItems(TyrasBill, 1);
                    htmltext = "30420-01.htm";
                    st.setCond(3);
                    st.setState(STARTED);
                } else {
                    htmltext = "30420-01b.htm";
                }
            } else if (cond == 3) {
                htmltext = "30420-01a.htm";
            } else if (cond > 3) {
                htmltext = "30420-02.htm";
            }
        } else if (npcId == Neti) {
            if (cond == 7) {
                htmltext = "30425-01.htm";
                st.setCond(8);
                st.setState(STARTED);
            } else if (cond == 8) {
                htmltext = "30425-02.htm";
            }
        } else if (npcId == Rolento) {
            if (cond == 8) {
                htmltext = "30437-01.htm";
            } else if (cond == 9) {
                htmltext = "30437-03a.htm";
            }
        } else if (npcId == Burai) {
            if (cond == 9 && st.ownItemCount(CertificateOfTheSilverScaleGuild) > 0 && st.ownItemCount(RolentoCargobox) > 0 && st.ownItemCount(AttackDirectives) > 0) {
                htmltext = "30617-01.htm";
            }
            if (cond == 10) {
                if (st.ownItemCount(OlMahumCaptainHead) >= 30) {
                    htmltext = "30617-05.htm";
                    st.giveItems(ADENA_ID, 8000);
                    st.takeItems(OlMahumCaptainHead, -1);
                    st.setCond(11);
                    st.setState(STARTED);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    htmltext = "30617-04a.htm";
                }
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
        if (npcId == OlMahumSupportTroop) {
            Despawn_OlMahumSupportTroop();
        } else if (cond == 2 && Rnd.chance(10)) {
            if (OlMahumSupportTroop_Spawn == null) {
                Spawn_OlMahumSupportTroop(st);
            } else if (!st.isRunningQuestTimer("Wait1")) {
                st.startQuestTimer("Wait1", 300000);
            }
        } else if (cond == 5 && npcId == TumranBugbear) {
            if (st.ownItemCount(RangerReportPart1) == 0 && Rnd.chance(CHANCE2)) {
                st.giveItems(RangerReportPart1, 1);
                st.soundEffect(SOUND_ITEMGET);
            } else if (st.ownItemCount(RangerReportPart2) == 0 && Rnd.chance(CHANCE21)) {
                st.giveItems(RangerReportPart2, 1);
                st.soundEffect(SOUND_ITEMGET);
            } else if (st.ownItemCount(RangerReportPart3) == 0 && Rnd.chance(CHANCE22)) {
                st.giveItems(RangerReportPart3, 1);
                st.soundEffect(SOUND_ITEMGET);
            } else if (st.ownItemCount(RangerReportPart4) == 0 && Rnd.chance(CHANCE23)) {
                st.giveItems(RangerReportPart4, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (cond == 6 && npcId == OlMahumGeneral) {
            if (st.ownItemCount(WeaponsTradeContract) == 0 && Rnd.chance(CHANCE24)) {
                st.giveItems(WeaponsTradeContract, 1);
                st.soundEffect(SOUND_ITEMGET);
            } else if (st.ownItemCount(AttackDirectives) == 0 && Rnd.chance(CHANCE25)) {
                st.giveItems(AttackDirectives, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }
}