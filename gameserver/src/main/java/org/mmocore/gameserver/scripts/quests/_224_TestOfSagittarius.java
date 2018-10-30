package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.base.ClassId;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.object.Inventory;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;

public class _224_TestOfSagittarius extends Quest {
    private static final int BERNARDS_INTRODUCTION_ID = 3294;
    private static final int LETTER_OF_HAMIL3_ID = 3297;
    private static final int HUNTERS_RUNE2_ID = 3299;
    private static final int MARK_OF_SAGITTARIUS_ID = 3293;
    private static final int CRESCENT_MOON_BOW_ID = 3028;
    private static final int TALISMAN_OF_KADESH_ID = 3300;
    private static final int BLOOD_OF_LIZARDMAN_ID = 3306;
    private static final int LETTER_OF_HAMIL1_ID = 3295;
    private static final int LETTER_OF_HAMIL2_ID = 3296;
    private static final int HUNTERS_RUNE1_ID = 3298;
    private static final int TALISMAN_OF_SNAKE_ID = 3301;
    private static final int MITHRIL_CLIP_ID = 3302;
    private static final int STAKATO_CHITIN_ID = 3303;
    private static final int ST_BOWSTRING_ID = 3304;
    private static final int MANASHENS_HORN_ID = 3305;
    private static final int WOODEN_ARROW_ID = 17;
    private static final int RewardExp = 447444;
    private static final int RewardSP = 30704;
    private static final int RewardAdena = 80903;


    public _224_TestOfSagittarius() {
        super(false);
        addStartNpc(30702);
        addTalkId(30514);
        addTalkId(30626);
        addTalkId(30653);
        addTalkId(30702);
        addTalkId(30717);

        addKillId(20230);
        addKillId(20232);
        addKillId(20233);
        addKillId(20234);
        addKillId(20269);
        addKillId(20270);
        addKillId(27090);
        addKillId(20551);
        addKillId(20563);
        addKillId(20577);
        addKillId(20578);
        addKillId(20579);
        addKillId(20580);
        addKillId(20581);
        addKillId(20582);
        addKillId(20079);
        addKillId(20080);
        addKillId(20081);
        addKillId(20082);
        addKillId(20084);
        addKillId(20086);
        addKillId(20089);
        addKillId(20090);

        addQuestItem(new int[]{
                HUNTERS_RUNE2_ID,
                CRESCENT_MOON_BOW_ID,
                TALISMAN_OF_KADESH_ID,
                BLOOD_OF_LIZARDMAN_ID,
                BERNARDS_INTRODUCTION_ID,
                HUNTERS_RUNE1_ID,
                LETTER_OF_HAMIL1_ID,
                TALISMAN_OF_SNAKE_ID,
                LETTER_OF_HAMIL2_ID,
                LETTER_OF_HAMIL3_ID,
                MITHRIL_CLIP_ID,
                STAKATO_CHITIN_ID,
                ST_BOWSTRING_ID,
                MANASHENS_HORN_ID
        });
        addLevelCheck(39);
        addClassIdCheck(ClassId.rogue, ClassId.elven_scout, ClassId.assassin);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        switch (event) {
            case "1":
                htmltext = "30702-04.htm";
                st.setCond(1);
                st.setState(STARTED);
                if (!st.getPlayer().getPlayerVariables().getBoolean(PlayerVariables.DD3)) {
                    st.giveItems(7562, 96);
                    st.getPlayer().getPlayerVariables().set(PlayerVariables.DD3, "1", -1);
                }
                st.soundEffect(SOUND_ACCEPT);
                st.giveItems(BERNARDS_INTRODUCTION_ID, 1);
                break;
            case "30626_1":
                htmltext = "30626-02.htm";
                break;
            case "30626_2":
                htmltext = "30626-03.htm";
                st.takeItems(BERNARDS_INTRODUCTION_ID, st.ownItemCount(BERNARDS_INTRODUCTION_ID));
                st.giveItems(LETTER_OF_HAMIL1_ID, 1);
                st.setCond(2);
                break;
            case "30626_3":
                htmltext = "30626-06.htm";
                break;
            case "30626_4":
                htmltext = "30626-07.htm";
                st.takeItems(HUNTERS_RUNE1_ID, st.ownItemCount(HUNTERS_RUNE1_ID));
                st.giveItems(LETTER_OF_HAMIL2_ID, 1);
                st.setCond(5);
                break;
            case "30653_1":
                htmltext = "30653-02.htm";
                st.takeItems(LETTER_OF_HAMIL1_ID, st.ownItemCount(LETTER_OF_HAMIL1_ID));
                st.setCond(3);
                break;
            case "30514_1":
                htmltext = "30514-02.htm";
                st.takeItems(LETTER_OF_HAMIL2_ID, st.ownItemCount(LETTER_OF_HAMIL2_ID));
                st.setCond(6);
                break;
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        if (st.ownItemCount(MARK_OF_SAGITTARIUS_ID) > 0) {
            st.exitQuest(true);
            return "completed";
        }
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int id = st.getState();
        if (id == CREATED) {
            st.setState(STARTED);
            st.setCond(0);
            st.setMemoState("id", "0");
        }
        if (npcId == 30702 && st.getCond() == 0) {
            switch (isAvailableFor(st.getPlayer())) {
                case LEVEL:
                    htmltext = "30702-01.htm";
                    st.exitQuest(true);
                case CLASS_ID:
                    htmltext = "30702-02.htm";
                    st.exitQuest(true);
                default:
                    htmltext = "30702-03.htm";
                    break;
            }
        } else if (npcId == 30702 && st.getCond() == 1 && st.ownItemCount(BERNARDS_INTRODUCTION_ID) > 0) {
            htmltext = "30702-05.htm";
        } else if (npcId == 30626 && st.getCond() == 1 && st.ownItemCount(BERNARDS_INTRODUCTION_ID) > 0) {
            htmltext = "30626-01.htm";
        } else if (npcId == 30626 && st.getCond() == 2 && st.ownItemCount(LETTER_OF_HAMIL1_ID) > 0) {
            htmltext = "30626-04.htm";
        } else if (npcId == 30626 && st.getCond() == 4 && st.ownItemCount(HUNTERS_RUNE1_ID) == 10) {
            htmltext = "30626-05.htm";
        } else if (npcId == 30626 && st.getCond() == 5 && st.ownItemCount(LETTER_OF_HAMIL2_ID) > 0) {
            htmltext = "30626-08.htm";
        } else if (npcId == 30626 && st.getCond() == 8) {
            htmltext = "30626-09.htm";
            st.giveItems(LETTER_OF_HAMIL3_ID, 1);
            st.setCond(9);
        } else if (npcId == 30626 && st.getCond() == 9 && st.ownItemCount(LETTER_OF_HAMIL3_ID) > 0) {
            htmltext = "30626-10.htm";
        } else if (npcId == 30626 && st.getCond() == 12 && st.ownItemCount(CRESCENT_MOON_BOW_ID) > 0) {
            htmltext = "30626-11.htm";
            st.setCond(13);
        } else if (npcId == 30626 && st.getCond() == 13) {
            htmltext = "30626-12.htm";
        } else if (npcId == 30626 && st.getCond() == 14 && st.ownItemCount(TALISMAN_OF_KADESH_ID) > 0) {
            htmltext = "30626-13.htm";
            st.takeItems(CRESCENT_MOON_BOW_ID, -1);
            st.takeItems(TALISMAN_OF_KADESH_ID, -1);
            st.takeItems(BLOOD_OF_LIZARDMAN_ID, -1);
            st.giveItems(MARK_OF_SAGITTARIUS_ID, 1);
            if (!st.getPlayer().getPlayerVariables().getBoolean(PlayerVariables.PROF2_3)) {
                st.addExpAndSp(RewardExp, RewardSP);
                st.giveItems(ADENA_ID, RewardAdena);
                st.getPlayer().getPlayerVariables().set(PlayerVariables.PROF2_3, "1", -1);
            }
            st.soundEffect(SOUND_FINISH);
            st.removeMemo("cond");
            st.exitQuest(false);
        } else if (npcId == 30653 && st.getCond() == 2 && st.ownItemCount(LETTER_OF_HAMIL1_ID) > 0) {
            htmltext = "30653-01.htm";
        } else if (npcId == 30653 && st.getCond() == 3) {
            htmltext = "30653-03.htm";
        } else if (npcId == 30514 && st.getCond() == 5 && st.ownItemCount(LETTER_OF_HAMIL2_ID) > 0) {
            htmltext = "30514-01.htm";
        } else if (npcId == 30514 && st.getCond() == 6) {
            htmltext = "30514-03.htm";
        } else if (npcId == 30514 && st.getCond() == 7 && st.ownItemCount(TALISMAN_OF_SNAKE_ID) > 0) {
            htmltext = "30514-04.htm";
            st.takeItems(TALISMAN_OF_SNAKE_ID, st.ownItemCount(TALISMAN_OF_SNAKE_ID));
            st.setCond(8);
        } else if (npcId == 30514 && st.getCond() == 8) {
            htmltext = "30514-05.htm";
        } else if (npcId == 30717 && st.getCond() == 9 && st.ownItemCount(LETTER_OF_HAMIL3_ID) > 0) {
            htmltext = "30717-01.htm";
            st.takeItems(LETTER_OF_HAMIL3_ID, st.ownItemCount(LETTER_OF_HAMIL3_ID));
            st.setCond(10);
        } else if (npcId == 30717 && st.getCond() == 10) {
            htmltext = "30717-03.htm";
        } else if (npcId == 30717 && st.getCond() == 12) {
            htmltext = "30717-04.htm";
        } else if (npcId == 30717 && st.getCond() == 11 && st.ownItemCount(STAKATO_CHITIN_ID) > 0 && st.ownItemCount(MITHRIL_CLIP_ID) > 0 && st.ownItemCount(ST_BOWSTRING_ID) > 0 && st.ownItemCount(MANASHENS_HORN_ID) > 0) {
            htmltext = "30717-02.htm";
            st.takeItems(MITHRIL_CLIP_ID, st.ownItemCount(MITHRIL_CLIP_ID));
            st.takeItems(STAKATO_CHITIN_ID, st.ownItemCount(STAKATO_CHITIN_ID));
            st.takeItems(ST_BOWSTRING_ID, st.ownItemCount(ST_BOWSTRING_ID));
            st.takeItems(MANASHENS_HORN_ID, st.ownItemCount(MANASHENS_HORN_ID));
            st.giveItems(CRESCENT_MOON_BOW_ID, 1);
            st.giveItems(WOODEN_ARROW_ID, 10);
            st.setCond(12);
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == 20079 || npcId == 20080 || npcId == 20081 || npcId == 20084 || npcId == 20086 || npcId == 20089 || npcId == 20090) {
            if (st.getCond() == 3 && st.ownItemCount(HUNTERS_RUNE1_ID) < 10 && Rnd.chance(50)) {
                st.giveItems(HUNTERS_RUNE1_ID, 1);
                if (st.ownItemCount(HUNTERS_RUNE1_ID) == 10) {
                    st.setCond(4);
                    st.soundEffect(SOUND_MIDDLE);
                } else {
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == 20269 || npcId == 20270) {
            if (st.getCond() == 6 && st.ownItemCount(HUNTERS_RUNE2_ID) < 10 && Rnd.chance(50)) {
                st.giveItems(HUNTERS_RUNE2_ID, 1);
                if (st.ownItemCount(HUNTERS_RUNE2_ID) == 10) {
                    st.takeItems(HUNTERS_RUNE2_ID, 10);
                    st.giveItems(TALISMAN_OF_SNAKE_ID, 1);
                    st.setCond(7);
                    st.soundEffect(SOUND_MIDDLE);
                } else {
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == 20230 || npcId == 20232 || npcId == 20234) {
            if (st.getCond() == 10 && st.ownItemCount(STAKATO_CHITIN_ID) == 0 && Rnd.chance(10)) {
                st.giveItems(STAKATO_CHITIN_ID, 1);
                if (st.ownItemCount(MITHRIL_CLIP_ID) > 0 && st.ownItemCount(ST_BOWSTRING_ID) > 0 && st.ownItemCount(MANASHENS_HORN_ID) > 0) {
                    st.setCond(11);
                    st.soundEffect(SOUND_MIDDLE);
                } else {
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == 20563) {
            if (st.getCond() == 10 && st.ownItemCount(MANASHENS_HORN_ID) == 0 && Rnd.chance(10)) {
                st.giveItems(MANASHENS_HORN_ID, 1);
                if (st.ownItemCount(MITHRIL_CLIP_ID) > 0 && st.ownItemCount(ST_BOWSTRING_ID) > 0 && st.ownItemCount(STAKATO_CHITIN_ID) > 0) {
                    st.setCond(11);
                    st.soundEffect(SOUND_MIDDLE);
                } else {
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == 20233) {
            if (st.getCond() == 10 && st.ownItemCount(ST_BOWSTRING_ID) == 0 && Rnd.chance(10)) {
                st.giveItems(ST_BOWSTRING_ID, 1);
                if (st.ownItemCount(MITHRIL_CLIP_ID) > 0 && st.ownItemCount(MANASHENS_HORN_ID) > 0 && st.ownItemCount(STAKATO_CHITIN_ID) > 0) {
                    st.setCond(11);
                    st.soundEffect(SOUND_MIDDLE);
                } else {
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == 20551) {
            if (st.getCond() == 10 && st.ownItemCount(MITHRIL_CLIP_ID) == 0 && Rnd.chance(10)) {
                st.giveItems(MITHRIL_CLIP_ID, 1);
                if (st.ownItemCount(ST_BOWSTRING_ID) > 0 && st.ownItemCount(MANASHENS_HORN_ID) > 0 && st.ownItemCount(STAKATO_CHITIN_ID) > 0) {
                    st.setCond(11);
                    st.soundEffect(SOUND_MIDDLE);
                } else {
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == 20551) {
            if (st.getCond() == 10 && st.ownItemCount(MITHRIL_CLIP_ID) == 0 && Rnd.chance(10)) {
                if (st.ownItemCount(ST_BOWSTRING_ID) > 0 && st.ownItemCount(MANASHENS_HORN_ID) > 0 && st.ownItemCount(STAKATO_CHITIN_ID) > 0) {
                    st.giveItems(MITHRIL_CLIP_ID, 1);
                    st.setCond(11);
                    st.soundEffect(SOUND_MIDDLE);
                } else {
                    st.giveItems(MITHRIL_CLIP_ID, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == 20577 || npcId == 20578 || npcId == 20579 || npcId == 20580 || npcId == 20581 || npcId == 20582) {
            if (st.getCond() == 13) {
                if (Rnd.chance((st.ownItemCount(BLOOD_OF_LIZARDMAN_ID) - 120) * 5)) {
                    st.addSpawn(27090);
                    st.takeItems(BLOOD_OF_LIZARDMAN_ID, st.ownItemCount(BLOOD_OF_LIZARDMAN_ID));
                    st.soundEffect(SOUND_BEFORE_BATTLE);
                } else {
                    st.giveItems(BLOOD_OF_LIZARDMAN_ID, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == 27090) {
            if (st.getCond() == 13 && st.ownItemCount(TALISMAN_OF_KADESH_ID) == 0) {
                if (st.getItemEquipped(Inventory.PAPERDOLL_RHAND) == CRESCENT_MOON_BOW_ID) {
                    st.giveItems(TALISMAN_OF_KADESH_ID, 1);
                    st.setCond(14);
                    st.soundEffect(SOUND_MIDDLE);
                } else {
                    st.addSpawn(27090);
                }
            }
        }
        return null;
    }
}