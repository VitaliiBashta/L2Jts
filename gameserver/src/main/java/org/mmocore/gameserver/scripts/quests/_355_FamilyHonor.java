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
public class _355_FamilyHonor extends Quest {
    // npc
    private static final int galicbredo = 30181;
    private static final int patrin = 30929;

    // mobs
    private static final int timak_orc_troop_leader = 20767;
    private static final int timak_orc_troop_shaman = 20768;
    private static final int timak_orc_troop_warrior = 20769;
    private static final int timak_orc_troop_archer = 20770;

    // questitem
    private static final int q_ancient_portrait = 4252;
    private static final int q_beronas_sculpture_0 = 4350;
    private static final int q_beronas_sculpture_s = 4351;
    private static final int q_beronas_sculpture_a = 4352;
    private static final int q_beronas_sculpture_b = 4353;
    private static final int q_beronas_sculpture_c = 4354;

    public _355_FamilyHonor() {
        super(false);
        addStartNpc(galicbredo);
        addTalkId(patrin);
        addKillId(timak_orc_troop_leader, timak_orc_troop_shaman, timak_orc_troop_warrior, timak_orc_troop_archer);
        addLevelCheck(36, 49);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();

        if (npcId == galicbredo) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "galicbredo_q0355_04.htm";
            } else if (event.equalsIgnoreCase("reply_1"))
                htmltext = "galicbredo_q0355_03.htm";
            else if (event.equalsIgnoreCase("reply_2")) {
                if (st.ownItemCount(q_ancient_portrait) < 1)
                    htmltext = "galicbredo_q0355_07.htm";
                else if (st.ownItemCount(q_ancient_portrait) >= 100) {
                    st.giveItems(ADENA_ID, st.ownItemCount(q_ancient_portrait) * 120 + 7800);
                    st.takeItems(q_ancient_portrait, -1);
                    htmltext = "galicbredo_q0355_07b.htm";
                } else if (st.ownItemCount(q_ancient_portrait) >= 1 && st.ownItemCount(q_ancient_portrait) < 100) {
                    st.giveItems(ADENA_ID, st.ownItemCount(q_ancient_portrait) * 120 + 2800);
                    st.takeItems(q_ancient_portrait, -1);
                    htmltext = "galicbredo_q0355_07a.htm";
                }
            } else if (event.equalsIgnoreCase("reply_3"))
                htmltext = "galicbredo_q0355_08.htm";
            else if (event.equalsIgnoreCase("reply_4")) {
                if (st.ownItemCount(q_ancient_portrait) > 0)
                    st.giveItems(ADENA_ID, st.ownItemCount(q_ancient_portrait) * 120);
                st.takeItems(q_ancient_portrait, -1);
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                htmltext = "galicbredo_q0355_09.htm";
            }
        } else if (npcId == patrin)
            if (event.equalsIgnoreCase("reply_1")) {
                int i0 = Rnd.get(100);
                if (st.ownItemCount(q_beronas_sculpture_0) < 1)
                    htmltext = "patrin_q0355_02.htm";
                else if (st.ownItemCount(q_beronas_sculpture_0) >= 1 && i0 < 2) {
                    st.giveItems(q_beronas_sculpture_s, 1);
                    st.takeItems(q_beronas_sculpture_0, 1);
                    htmltext = "patrin_q0355_03.htm";
                } else if (st.ownItemCount(q_beronas_sculpture_0) >= 1 && i0 < 32) {
                    st.giveItems(q_beronas_sculpture_a, 1);
                    st.takeItems(q_beronas_sculpture_0, 1);
                    htmltext = "patrin_q0355_04.htm";
                } else if (st.ownItemCount(q_beronas_sculpture_0) >= 1 && i0 < 62) {
                    st.giveItems(q_beronas_sculpture_b, 1);
                    st.takeItems(q_beronas_sculpture_0, 1);
                    htmltext = "patrin_q0355_05.htm";
                } else if (st.ownItemCount(q_beronas_sculpture_0) >= 1 && i0 < 77) {
                    st.giveItems(q_beronas_sculpture_c, 1);
                    st.takeItems(q_beronas_sculpture_0, 1);
                    htmltext = "patrin_q0355_06.htm";
                } else {
                    st.takeItems(q_beronas_sculpture_0, 1);
                    htmltext = "patrin_q0355_07.htm";
                }
            }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == galicbredo) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "galicbredo_q0355_01.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "galicbredo_q0355_02.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == galicbredo) {
                    if (st.ownItemCount(q_beronas_sculpture_0) < 1)
                        htmltext = "galicbredo_q0355_05.htm";
                    else if (st.ownItemCount(q_beronas_sculpture_0) >= 1)
                        htmltext = "galicbredo_q0355_06.htm";
                } else if (npcId == patrin)
                    if (id == 2)
                        htmltext = "patrin_q0355_01.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int id = st.getState();

        if (id == 2)
            if (npcId == timak_orc_troop_leader) {
                int i0 = Rnd.get(1000);
                if (i0 < 560) {
                    st.giveItems(q_ancient_portrait, 1);
                    st.soundEffect(SOUND_ITEMGET);
                } else if (i0 < 684) {
                    st.giveItems(q_beronas_sculpture_0, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == timak_orc_troop_shaman) {
                int i0 = Rnd.get(100);
                if (i0 < 53) {
                    st.giveItems(q_ancient_portrait, 1);
                    st.soundEffect(SOUND_ITEMGET);
                } else if (i0 < 65) {
                    st.giveItems(q_beronas_sculpture_0, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == timak_orc_troop_warrior) {
                int i0 = Rnd.get(1000);

                if (i0 < 420) {
                    st.giveItems(q_ancient_portrait, 1);
                    st.soundEffect(SOUND_ITEMGET);
                } else if (i0 < 516) {
                    st.giveItems(q_beronas_sculpture_0, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == timak_orc_troop_archer) {
                int i0 = Rnd.get(100);
                if (i0 < 44) {
                    st.giveItems(q_ancient_portrait, 1);
                    st.soundEffect(SOUND_ITEMGET);
                } else if (i0 < 56) {
                    st.giveItems(q_beronas_sculpture_0, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        return null;
    }
}