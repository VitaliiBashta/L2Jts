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
public class _371_ShriekOfGhosts extends Quest {
    // npc
    private static int seer_reva = 30867;
    private static int patrin = 30929;

    // mobs
    private static int hallates_warrior = 20818;
    private static int hallates_knight = 20820;
    private static int hallates_commander = 20824;

    // etcitem
    private static int ancient_porcelain = 6002;
    private static int ancient_porcelain_s = 6003;
    private static int ancient_porcelain_a = 6004;
    private static int ancient_porcelain_b = 6005;
    private static int ancient_porcelain_c = 6006;

    // questitem
    private static int ancient_funeral_urn = 5903;

    public _371_ShriekOfGhosts() {
        super(true);
        addStartNpc(seer_reva);
        addTalkId(patrin);
        addKillId(hallates_warrior, hallates_knight, hallates_commander);
        addQuestItem(ancient_funeral_urn);
        addLevelCheck(59, 71);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();

        if (npcId == seer_reva) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("spirits_cry_secrets", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "seer_reva_q0371_03.htm";
            } else if (event.equalsIgnoreCase("reply_1")) {
                if (st.ownItemCount(ancient_funeral_urn) < 1)
                    htmltext = "seer_reva_q0371_06.htm";
                else if (st.ownItemCount(ancient_funeral_urn) >= 1 && st.ownItemCount(ancient_funeral_urn) < 100) {
                    st.giveItems(ADENA_ID, st.ownItemCount(ancient_funeral_urn) * 1000 + 15000);
                    st.takeItems(ancient_funeral_urn, -1);
                    htmltext = "seer_reva_q0371_07.htm";
                } else if (st.ownItemCount(ancient_funeral_urn) >= 100) {
                    st.giveItems(ADENA_ID, st.ownItemCount(ancient_funeral_urn) * 1000 + 37700);
                    st.takeItems(ancient_funeral_urn, -1);
                    htmltext = "seer_reva_q0371_08.htm";
                }
            } else if (event.equalsIgnoreCase("reply_2"))
                htmltext = "seer_reva_q0371_09.htm";
            else if (event.equalsIgnoreCase("reply_3")) {
                if (st.ownItemCount(ancient_funeral_urn) > 0)
                    st.giveItems(ADENA_ID, st.ownItemCount(ancient_funeral_urn) * 1000);
                st.takeItems(ancient_funeral_urn, -1);
                st.removeMemo("spirits_cry_secrets");
                st.exitQuest(true);
                htmltext = "seer_reva_q0371_10.htm";
            }
        } else if (npcId == patrin)
            if (event.equalsIgnoreCase("reply_1"))
                if (st.ownItemCount(ancient_porcelain) < 1)
                    htmltext = "patrin_q0371_02.htm";
                else if (st.ownItemCount(ancient_porcelain) >= 1) {
                    int i0 = Rnd.get(100);
                    if (i0 < 2) {
                        st.giveItems(ancient_porcelain_s, 1);
                        st.takeItems(ancient_porcelain, 1);
                        htmltext = "patrin_q0371_03.htm";
                    } else if (i0 < 32) {
                        st.giveItems(ancient_porcelain_a, 1);
                        st.takeItems(ancient_porcelain, 1);
                        htmltext = "patrin_q0371_04.htm";
                    } else if (i0 < 62) {
                        st.giveItems(ancient_porcelain_b, 1);
                        st.takeItems(ancient_porcelain, 1);
                        htmltext = "patrin_q0371_05.htm";
                    } else if (i0 < 77) {
                        st.giveItems(ancient_porcelain_c, 1);
                        st.takeItems(ancient_porcelain, 1);
                        htmltext = "patrin_q0371_06.htm";
                    } else {
                        st.giveItems(ancient_porcelain, 1);
                        htmltext = "patrin_q0371_07.htm";
                    }
                }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("spirits_cry_secrets");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == seer_reva) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "seer_reva_q0371_01.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "seer_reva_q0371_02.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == seer_reva) {
                    if (GetMemoState == 1 && st.ownItemCount(ancient_porcelain) < 1)
                        htmltext = "seer_reva_q0371_04.htm";
                    else if (GetMemoState == 1 && st.ownItemCount(ancient_porcelain) >= 1)
                        htmltext = "seer_reva_q0371_05.htm";
                } else if (npcId == patrin)
                    if (GetMemoState == 1)
                        htmltext = "patrin_q0371_01.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("spirits_cry_secrets");
        int npcId = npc.getNpcId();

        if (GetMemoState == 1)
            if (npcId == hallates_warrior) {
                int i4 = Rnd.get(1000);
                if (i4 < 350) {
                    st.giveItems(ancient_funeral_urn, 1);
                    st.soundEffect(SOUND_ITEMGET);
                } else if (i4 < 400) {
                    st.giveItems(ancient_porcelain, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == hallates_knight) {
                int i4 = Rnd.get(1000);
                if (i4 < 583) {
                    st.giveItems(ancient_funeral_urn, 1);
                    st.soundEffect(SOUND_ITEMGET);
                } else if (i4 < 673) {
                    st.giveItems(ancient_porcelain, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == hallates_commander) {
                int i4 = Rnd.get(1000);
                if (i4 < 458) {
                    st.giveItems(ancient_funeral_urn, 1);
                    st.soundEffect(SOUND_ITEMGET);
                } else if (i4 < 538) {
                    st.giveItems(ancient_porcelain, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        return null;
    }
}