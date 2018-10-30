package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _311_ExpulsionOfEvilSpirits extends Quest {
    // npc
    private final static int cheiren = 32655;

    //mobs
    private final static int ragna_orc_re = 22691;
    private final static int ragna_orc_warrior_re = 22692;
    private final static int ragna_orc_hero_re = 22693;
    private final static int ragna_orc_commander_re = 22694;
    private final static int ragna_orc_healer_re = 22695;
    private final static int ragna_orc_shaman_re = 22696;
    private final static int ragna_orc_seer_re = 22697;
    private final static int ragna_orc_archer_re = 22698;
    private final static int ragna_orc_sniper_re = 22699;
    private final static int baranka_demon_re = 22701;
    private final static int baranka_destroyer_re = 22702;

    // etcitem
    private final static int q_charm_of_protection = 14848;
    private final static int rp_sealed_dynasty_blast_plate_i = 9482;
    private final static int rp_sealed_dynasty_gaiter_i = 9483;
    private final static int rp_sealed_dynasty_helmet_i = 9484;
    private final static int rp_sealed_dynasty_gauntlet_i = 9485;
    private final static int rp_sealed_dynasty_boots_i = 9486;
    private final static int rp_sealed_dynasty_leather_mail_i = 9487;
    private final static int rp_sealed_dynasty_leather_legging_i = 9488;
    private final static int rp_sealed_dynasty_leather_helmet_i = 9489;
    private final static int rp_sealed_dynasty_leather_gloves_i = 9490;
    private final static int rp_sealed_dynasty_leather_boots_i = 9491;
    private final static int rp_sealed_dynasty_shield_i = 9497;
    private final static int renad = 9628;
    private final static int adamantium = 9629;
    private final static int oricalcum = 9630;
    private final static int codex_of_giant_forgetting = 9625;
    private final static int codex_of_giant_training = 9626;

    // questitem
    private final static int q_evil_soul_core = 14881;
    private final static int q_ragna_orc_totem = 14882;

    public _311_ExpulsionOfEvilSpirits() {
        super(false);
        addStartNpc(cheiren);
        addKillId(ragna_orc_re, ragna_orc_warrior_re, ragna_orc_hero_re, ragna_orc_commander_re, ragna_orc_healer_re, ragna_orc_shaman_re, ragna_orc_seer_re, ragna_orc_archer_re, ragna_orc_sniper_re, baranka_demon_re, baranka_destroyer_re);
        addQuestItem(q_ragna_orc_totem, q_evil_soul_core);
        addLevelCheck(80);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("disperse_the_evil_spirit");

        if (event.equalsIgnoreCase("quest_accept")) {
            st.setCond(1);
            st.setMemoState("disperse_the_evil_spirit", String.valueOf(1), true);
            st.setMemoState("disperse_the_evil_spirit_ex", String.valueOf(0), true);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            htmltext = "cheiren_q0311_04.htm";
        } else if (event.equalsIgnoreCase("reply_1"))
            htmltext = "cheiren_q0311_03.htm";
        else if (event.equalsIgnoreCase("reply_10")) {
            if (GetMemoState == 1 && (st.ownItemCount(q_evil_soul_core) >= 1 || st.ownItemCount(q_ragna_orc_totem) >= 1))
                htmltext = "cheiren_q0311_07.htm";
            else if (GetMemoState == 1 && st.ownItemCount(q_evil_soul_core) < 1 && st.ownItemCount(q_ragna_orc_totem) < 1)
                htmltext = "cheiren_q0311_08.htm";
        } else if (event.equalsIgnoreCase("reply_100")) {
            if (GetMemoState == 1 && (st.ownItemCount(q_evil_soul_core) >= 1 || st.ownItemCount(q_ragna_orc_totem) >= 1))
                htmltext = "cheiren_q0311_07a.htm";
        } else if (event.equalsIgnoreCase("reply_101")) {
            if (GetMemoState == 1 && (st.ownItemCount(q_evil_soul_core) >= 1 || st.ownItemCount(q_ragna_orc_totem) >= 1))
                htmltext = "cheiren_q0311_07b.htm";
        } else if (event.equalsIgnoreCase("reply_102")) {
            if (GetMemoState == 1 && (st.ownItemCount(q_evil_soul_core) >= 1 || st.ownItemCount(q_ragna_orc_totem) >= 1))
                htmltext = "cheiren_q0311_07c.htm";
        } else if (event.equalsIgnoreCase("reply_103")) {
            if (GetMemoState == 1 && (st.ownItemCount(q_evil_soul_core) >= 1 || st.ownItemCount(q_ragna_orc_totem) >= 1))
                htmltext = "cheiren_q0311_07d.htm";
        } else if (event.equalsIgnoreCase("reply_11")) {
            if (st.ownItemCount(q_ragna_orc_totem) >= 326) {
                st.giveItems(rp_sealed_dynasty_blast_plate_i, 1);
                st.takeItems(q_ragna_orc_totem, 326);
                htmltext = "cheiren_q0311_09.htm";
            } else
                htmltext = "cheiren_q0311_10.htm";
        } else if (event.equalsIgnoreCase("reply_12")) {
            if (st.ownItemCount(q_ragna_orc_totem) >= 204) {

                st.giveItems(rp_sealed_dynasty_gaiter_i, 1);
                st.takeItems(q_ragna_orc_totem, 204);
                htmltext = "cheiren_q0311_09.htm";
            } else
                htmltext = "cheiren_q0311_10.htm";
        } else if (event.equalsIgnoreCase("reply_13")) {
            if (st.ownItemCount(q_ragna_orc_totem) >= 122) {

                st.giveItems(rp_sealed_dynasty_helmet_i, 1);
                st.takeItems(q_ragna_orc_totem, 122);
                htmltext = "cheiren_q0311_09.htm";
            } else
                htmltext = "cheiren_q0311_10.htm";
        } else if (event.equalsIgnoreCase("reply_14")) {
            if (st.ownItemCount(q_ragna_orc_totem) >= 82) {

                st.giveItems(rp_sealed_dynasty_gauntlet_i, 1);
                st.takeItems(q_ragna_orc_totem, 82);
                htmltext = "cheiren_q0311_09.htm";
            } else
                htmltext = "cheiren_q0311_10.htm";
        } else if (event.equalsIgnoreCase("reply_15")) {
            if (st.ownItemCount(q_ragna_orc_totem) >= 82) {

                st.giveItems(rp_sealed_dynasty_boots_i, 1);
                st.takeItems(q_ragna_orc_totem, 82);
                htmltext = "cheiren_q0311_09.htm";
            } else
                htmltext = "cheiren_q0311_10.htm";
        } else if (event.equalsIgnoreCase("reply_16")) {
            if (st.ownItemCount(q_ragna_orc_totem) >= 244) {

                st.giveItems(rp_sealed_dynasty_leather_mail_i, 1);
                st.takeItems(q_ragna_orc_totem, 244);
                htmltext = "cheiren_q0311_09.htm";
            } else
                htmltext = "cheiren_q0311_10.htm";
        } else if (event.equalsIgnoreCase("reply_17")) {
            if (st.ownItemCount(q_ragna_orc_totem) >= 153) {

                st.giveItems(rp_sealed_dynasty_leather_legging_i, 1);
                st.takeItems(q_ragna_orc_totem, 153);
                htmltext = "cheiren_q0311_09.htm";
            } else
                htmltext = "cheiren_q0311_10.htm";
        } else if (event.equalsIgnoreCase("reply_18")) {
            if (st.ownItemCount(q_ragna_orc_totem) >= 122) {

                st.giveItems(rp_sealed_dynasty_leather_helmet_i, 1);
                st.takeItems(q_ragna_orc_totem, 122);
                htmltext = "cheiren_q0311_09.htm";
            } else
                htmltext = "cheiren_q0311_10.htm";
        } else if (event.equalsIgnoreCase("reply_19")) {
            if (st.ownItemCount(q_ragna_orc_totem) >= 82) {

                st.giveItems(rp_sealed_dynasty_leather_gloves_i, 1);
                st.takeItems(q_ragna_orc_totem, 82);
                htmltext = "cheiren_q0311_09.htm";
            } else
                htmltext = "cheiren_q0311_10.htm";
        } else if (event.equalsIgnoreCase("reply_20")) {
            if (st.ownItemCount(q_ragna_orc_totem) >= 82) {

                st.giveItems(rp_sealed_dynasty_leather_boots_i, 1);
                st.takeItems(q_ragna_orc_totem, 82);
                htmltext = "cheiren_q0311_09.htm";
            } else
                htmltext = "cheiren_q0311_10.htm";
        } else if (event.equalsIgnoreCase("reply_26")) {
            if (st.ownItemCount(q_ragna_orc_totem) >= 86) {

                st.giveItems(rp_sealed_dynasty_shield_i, 1);
                st.takeItems(q_ragna_orc_totem, 86);
                htmltext = "cheiren_q0311_09.htm";
            } else
                htmltext = "cheiren_q0311_10.htm";
        } else if (event.equalsIgnoreCase("reply_27")) {
            if (st.ownItemCount(q_ragna_orc_totem) >= 24) {

                st.giveItems(renad, 1);
                st.takeItems(q_ragna_orc_totem, 24);
                htmltext = "cheiren_q0311_09.htm";
            } else
                htmltext = "cheiren_q0311_10.htm";
        } else if (event.equalsIgnoreCase("reply_28")) {
            if (st.ownItemCount(q_ragna_orc_totem) >= 43) {

                st.giveItems(adamantium, 1);
                st.takeItems(q_ragna_orc_totem, 43);
                htmltext = "cheiren_q0311_09.htm";
            } else
                htmltext = "cheiren_q0311_10.htm";
        } else if (event.equalsIgnoreCase("reply_29")) {
            if (st.ownItemCount(q_ragna_orc_totem) >= 36) {

                st.giveItems(oricalcum, 1);
                st.takeItems(q_ragna_orc_totem, 36);
                htmltext = "cheiren_q0311_09.htm";
            } else
                htmltext = "cheiren_q0311_10.htm";
        } else if (event.equalsIgnoreCase("reply_30")) {
            if (st.ownItemCount(q_ragna_orc_totem) >= 667) {

                st.giveItems(codex_of_giant_forgetting, 1);
                st.takeItems(q_ragna_orc_totem, 667);
                htmltext = "cheiren_q0311_09.htm";
            } else
                htmltext = "cheiren_q0311_10.htm";
        } else if (event.equalsIgnoreCase("reply_31")) {
            if (st.ownItemCount(q_ragna_orc_totem) >= 1000) {

                st.giveItems(codex_of_giant_training, 1);
                st.takeItems(q_ragna_orc_totem, 1000);
                htmltext = "cheiren_q0311_09.htm";
            } else
                htmltext = "cheiren_q0311_10.htm";
        } else if (event.equalsIgnoreCase("reply_201")) {
            if (GetMemoState == 1 && st.ownItemCount(q_evil_soul_core) >= 10) {
                st.giveItems(q_charm_of_protection, 1);
                st.takeItems(q_evil_soul_core, 10);
                htmltext = "cheiren_q0311_11.htm";
            } else if (GetMemoState == 1 && st.ownItemCount(q_evil_soul_core) < 10)
                htmltext = "cheiren_q0311_12.htm";
        } else if (event.equalsIgnoreCase("reply_2")) {
            if (GetMemoState == 1 && st.ownItemCount(q_evil_soul_core) < 1 && st.ownItemCount(q_ragna_orc_totem) < 10) {
                st.removeMemo("disperse_the_evil_spirit");
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                htmltext = "cheiren_q0311_13.htm";
            } else if (GetMemoState == 1 && (st.ownItemCount(q_evil_soul_core) >= 1 || st.ownItemCount(q_ragna_orc_totem) >= 1))
                htmltext = "cheiren_q0311_14.htm";
        } else if (event.equalsIgnoreCase("reply_3"))
            if (GetMemoState == 1)
                htmltext = "cheiren_q0311_15.htm";
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("disperse_the_evil_spirit");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == cheiren) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "cheiren_q0311_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "cheiren_q0311_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == cheiren)
                    if (GetMemoState == 1 && st.ownItemCount(q_evil_soul_core) < 1 && st.ownItemCount(q_ragna_orc_totem) < 1)
                        htmltext = "cheiren_q0311_05.htm";
                    else if (GetMemoState == 1 && (st.ownItemCount(q_evil_soul_core) >= 1 || st.ownItemCount(q_ragna_orc_totem) >= 1))
                        htmltext = "cheiren_q0311_06.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("disperse_the_evil_spirit");
        int GetMemoStateEx = st.getInt("disperse_the_evil_spirit_ex");
        int npcId = npc.getNpcId();

        if (GetMemoState == 1)
            if (npcId == ragna_orc_re) {
                st.setMemoState("disperse_the_evil_spirit_ex", String.valueOf(GetMemoStateEx + 1), true);
                if (GetMemoStateEx >= 100) {
                    int i1 = Rnd.get(20);
                    if (i1 < GetMemoStateEx % 100 + 1) {
                        st.setMemoState("disperse_the_evil_spirit_ex", String.valueOf(0), true);
                        st.giveItems(q_evil_soul_core, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
                int i0 = Rnd.get(1000);
                if (i0 < 694) {
                    st.giveItems(q_ragna_orc_totem, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == ragna_orc_warrior_re) {
                st.setMemoState("disperse_the_evil_spirit_ex", String.valueOf(GetMemoStateEx + 1), true);
                if (GetMemoStateEx >= 100) {
                    int i1 = Rnd.get(20);
                    if (i1 < GetMemoStateEx % 100 + 1) {
                        st.setMemoState("disperse_the_evil_spirit_ex", String.valueOf(0), true);
                        st.giveItems(q_evil_soul_core, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
                int i0 = Rnd.get(1000);
                if (i0 < 716) {
                    st.giveItems(q_ragna_orc_totem, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == ragna_orc_hero_re) {
                st.setMemoState("disperse_the_evil_spirit_ex", String.valueOf(GetMemoStateEx + 1), true);
                if (GetMemoStateEx >= 100) {
                    int i1 = Rnd.get(20);
                    if (i1 < GetMemoStateEx % 100 + 1) {
                        st.setMemoState("disperse_the_evil_spirit_ex", String.valueOf(0), true);
                        st.giveItems(q_evil_soul_core, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
                int i0 = Rnd.get(1000);
                if (i0 < 736) {
                    st.giveItems(q_ragna_orc_totem, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == ragna_orc_commander_re) {
                st.setMemoState("disperse_the_evil_spirit_ex", String.valueOf(GetMemoStateEx + 1), true);
                if (GetMemoStateEx >= 100) {
                    int i1 = Rnd.get(20);
                    if (i1 < GetMemoStateEx % 100 + 1) {
                        st.setMemoState("disperse_the_evil_spirit_ex", String.valueOf(0), true);
                        st.giveItems(q_evil_soul_core, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
                int i0 = Rnd.get(1000);
                if (i0 < 712) {
                    st.giveItems(q_ragna_orc_totem, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == ragna_orc_healer_re) {
                st.setMemoState("disperse_the_evil_spirit_ex", String.valueOf(GetMemoStateEx + 1), true);
                if (GetMemoStateEx >= 100) {
                    int i1 = Rnd.get(20);
                    if (i1 < GetMemoStateEx % 100 + 1) {
                        st.setMemoState("disperse_the_evil_spirit_ex", String.valueOf(0), true);
                        st.giveItems(q_evil_soul_core, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
                int i0 = Rnd.get(1000);
                if (i0 < 698) {
                    st.giveItems(q_ragna_orc_totem, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == ragna_orc_shaman_re) {
                st.setMemoState("disperse_the_evil_spirit_ex", String.valueOf(GetMemoStateEx + 1), true);
                if (GetMemoStateEx >= 100) {
                    int i1 = Rnd.get(20);
                    if (i1 < GetMemoStateEx % 100 + 1) {
                        st.setMemoState("disperse_the_evil_spirit_ex", String.valueOf(0), true);
                        st.giveItems(q_evil_soul_core, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
                int i0 = Rnd.get(1000);
                if (i0 < 692) {
                    st.giveItems(q_ragna_orc_totem, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == ragna_orc_seer_re) {
                st.setMemoState("disperse_the_evil_spirit_ex", String.valueOf(GetMemoStateEx + 1), true);
                if (GetMemoStateEx >= 100) {
                    int i1 = Rnd.get(20);
                    if (i1 < GetMemoStateEx % 100 + 1) {
                        st.setMemoState("disperse_the_evil_spirit_ex", String.valueOf(0), true);
                        st.giveItems(q_evil_soul_core, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
                int i0 = Rnd.get(1000);
                if (i0 < 640) {
                    st.giveItems(q_ragna_orc_totem, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == ragna_orc_archer_re || npcId == baranka_demon_re) {
                st.setMemoState("disperse_the_evil_spirit_ex", String.valueOf(GetMemoStateEx + 1), true);
                if (GetMemoStateEx >= 100) {
                    int i1 = Rnd.get(20);
                    if (i1 < GetMemoStateEx % 100 + 1) {
                        st.setMemoState("disperse_the_evil_spirit_ex", String.valueOf(0), true);
                        st.giveItems(q_evil_soul_core, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
                int i0 = Rnd.get(1000);
                if (i0 < 716) {
                    st.giveItems(q_ragna_orc_totem, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == ragna_orc_sniper_re) {
                st.setMemoState("disperse_the_evil_spirit_ex", String.valueOf(GetMemoStateEx + 1), true);
                if (GetMemoStateEx >= 100) {
                    int i1 = Rnd.get(20);
                    if (i1 < GetMemoStateEx % 100 + 1) {
                        st.setMemoState("disperse_the_evil_spirit_ex", String.valueOf(0), true);
                        st.giveItems(q_evil_soul_core, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
                int i0 = Rnd.get(1000);
                if (i0 < 752) {
                    st.giveItems(q_ragna_orc_totem, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == baranka_destroyer_re) {
                st.setMemoState("disperse_the_evil_spirit_ex", String.valueOf(GetMemoStateEx + 1), true);
                if (GetMemoStateEx >= 100) {
                    int i1 = Rnd.get(20);
                    if (i1 < GetMemoStateEx % 100 + 1) {
                        st.setMemoState("disperse_the_evil_spirit_ex", String.valueOf(0), true);
                        st.giveItems(q_evil_soul_core, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
                int i0 = Rnd.get(1000);
                if (i0 < 662) {
                    st.giveItems(q_ragna_orc_totem, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        return null;
    }
}