package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.ReflectionUtils;

/**
 * Based on official High Five
 *
 * @author Magister
 * @version 1.0
 * @date 11/01/2015
 * @tested OK
 */
public class _10292_SevenSignsGirlOfDoubt extends Quest {
    // count kill
    public static final String test_mobs = "test_mobs";
    // npc
    private static final int priest_wood = 32593;
    private static final int inzone_frantz = 32597;
    private static final int ssq2_elcardia_home1 = 32784;
    private static final int hardin = 30832;
    // mobs
    private static final int golem_cannon1_p = 22801;
    private static final int golem_cannon2_p = 22802;
    private static final int golem_cannon3_p = 22803;
    private static final int golem_prop1_p = 22804;
    private static final int golem_prop2_p = 22805;
    private static final int golem_prop3_p = 22806;
    private static final int ssq2_test_monster2 = 27422;
    private static final int ssq2_test_monster4 = 27424;
    // questitem
    private static final int q10292_ssq2_token1 = 17226;
    // zone_controller
    private static final int inzone_id = 145;
    // spawn
    private static int av_quest0 = 0;

    public _10292_SevenSignsGirlOfDoubt() {
        super(false);
        addStartNpc(priest_wood);
        addTalkId(inzone_frantz, ssq2_elcardia_home1, hardin);
        addKillId(golem_cannon1_p, golem_cannon2_p, golem_cannon3_p, golem_prop1_p, golem_prop2_p, golem_prop3_p);
        addKillNpcWithLog(5, test_mobs, 2, ssq2_test_monster2, ssq2_test_monster4);
        addQuestItem(q10292_ssq2_token1);
        addLevelCheck(81);
        addQuestCompletedCheck(198);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("ssq2_mystery_girl");
        int npcId = npc.getNpcId();
        if (npcId == priest_wood) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("ssq2_mystery_girl", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "priest_wood_q10292_05.htm";
            } else if (event.equalsIgnoreCase("reply_1"))
                htmltext = "priest_wood_q10292_04.htm";
            else if (event.equalsIgnoreCase("reply_2")) {
                InstantZone_Enter(st.getPlayer());
                htmltext = "priest_wood_q0198_06.htm";
            }
        } else if (npcId == inzone_frantz) {
            if (event.equalsIgnoreCase("reply_1"))
                htmltext = "inzone_frantz_q10292_02.htm";
            else if (event.equalsIgnoreCase("reply_2")) {
                st.setCond(2);
                st.setMemoState("ssq2_mystery_girl", String.valueOf(2), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "inzone_frantz_q10292_07.htm";
            }
        } else if (npcId == ssq2_elcardia_home1) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 2)
                    htmltext = "ssq2_elcardia_home1_q10292_02.htm";
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 2) {
                    st.setCond(3);
                    st.setMemoState("ssq2_mystery_girl", String.valueOf(3), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "ssq2_elcardia_home1_q10292_03.htm";
                }
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 4)
                    htmltext = "ssq2_elcardia_home1_q10292_06.htm";
            } else if (event.equalsIgnoreCase("reply_4")) {
                if (GetMemoState == 4) {
                    st.setCond(5);
                    st.setMemoState("ssq2_mystery_girl", String.valueOf(5), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "ssq2_elcardia_home1_q10292_07.htm";
                }
            } else if (event.equalsIgnoreCase("reply_5")) {
                if (av_quest0 == 0) {
                    av_quest0++;
                    addSpawnToInstance(ssq2_test_monster2, 89440, -238016, -9632, 0, 0, st.getPlayer().getReflectionId());
                    addSpawnToInstance(ssq2_test_monster4, 89524, -238131, -9632, 0, 0, st.getPlayer().getReflectionId());
                    return null;
                } else
                    htmltext = "ssq2_elcardia_home1_q10292_16.htm";
            } else if (event.equalsIgnoreCase("reply_6")) {
                if (GetMemoState == 6)
                    htmltext = "ssq2_elcardia_home1_q10292_11.htm";
            } else if (event.equalsIgnoreCase("reply_7")) {
                if (GetMemoState == 6) {
                    st.setCond(7);
                    st.setMemoState("ssq2_mystery_girl", String.valueOf(7), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "ssq2_elcardia_home1_q10292_13.htm";
                }
            }
        } else if (npcId == hardin) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 7) {
                    st.setCond(8);
                    st.setMemoState("ssq2_mystery_girl", String.valueOf(8), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "hardin_q10292_02.htm";
                }
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 8)
                    htmltext = "hardin_q10292_03.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("ssq2_mystery_girl");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == priest_wood) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case QUEST:
                        case LEVEL:
                            htmltext = "priest_wood_q10292_03.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "priest_wood_q10292_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == priest_wood) {
                    if (GetMemoState > 0 && GetMemoState < 9)
                        htmltext = "priest_wood_q10292_07.htm";
                } else if (npcId == inzone_frantz) {
                    if (GetMemoState == 1)
                        htmltext = "inzone_frantz_q10292_01.htm";
                    else if (GetMemoState == 2 && GetMemoState < 7)
                        htmltext = "inzone_frantz_q10292_03.htm";
                    else if (GetMemoState == 7)
                        htmltext = "inzone_frantz_q10292_04.htm";
                } else if (npcId == ssq2_elcardia_home1) {
                    if (GetMemoState == 2)
                        htmltext = "ssq2_elcardia_home1_q10292_01.htm";
                    else if (GetMemoState == 3 && st.ownItemCount(q10292_ssq2_token1) < 10)
                        htmltext = "ssq2_elcardia_home1_q10292_04.htm";
                    else if (GetMemoState == 3 && st.ownItemCount(q10292_ssq2_token1) >= 10) {
                        st.setCond(4);
                        st.setMemoState("ssq2_mystery_girl", String.valueOf(4), true);
                        st.takeItems(q10292_ssq2_token1, -1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "ssq2_elcardia_home1_q10292_05.htm";
                    } else if (GetMemoState == 4)
                        htmltext = "ssq2_elcardia_home1_q10292_18.htm";
                    else if (GetMemoState == 5)
                        htmltext = "ssq2_elcardia_home1_q10292_08.htm";
                    else if (GetMemoState == 6)
                        htmltext = "ssq2_elcardia_home1_q10292_10.htm";
                    else if (GetMemoState == 7)
                        htmltext = "ssq2_elcardia_home1_q10292_14.htm";
                    else if (GetMemoState == 8) {
                        if (!st.getPlayer().getPlayerClassComponent().isBaseExactlyActiveId())
                            htmltext = "ssq2_elcardia_home1_q10292_17.htm";
                        else {
                            st.addExpAndSp(10000000, 1000000);
                            st.removeMemo("ssq2_mystery_girl");
                            st.soundEffect(SOUND_FINISH);
                            st.exitQuest(false);
                            htmltext = "ssq2_elcardia_home1_q10292_15.htm";
                        }
                    }
                } else if (npcId == hardin) {
                    if (GetMemoState == 7)
                        htmltext = "hardin_q10292_01.htm";
                    else if (GetMemoState == 8)
                        htmltext = "hardin_q10292_04.htm";
                }
                break;
            case COMPLETED:
                if (npcId == priest_wood)
                    htmltext = "priest_wood_q10292_02.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("ssq2_mystery_girl");
        int npcId = npc.getNpcId();
        boolean doneKill = updateKill(npc, st);
        if (npcId == golem_cannon1_p || npcId == golem_cannon2_p || npcId == golem_cannon3_p || npcId == golem_prop1_p || npcId == golem_prop2_p || npcId == golem_prop3_p) {
            if (GetMemoState == 3 && st.ownItemCount(q10292_ssq2_token1) < 10) {
                if (Rnd.get(1000) < 700) {
                    if (st.ownItemCount(q10292_ssq2_token1) == 9) {
                        st.setCond(4);
                        st.giveItems(q10292_ssq2_token1, 1);
                        st.soundEffect(SOUND_MIDDLE);
                    } else {
                        st.giveItems(q10292_ssq2_token1, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        } else if (doneKill) {
            av_quest0 = 0;
            st.removeMemo(test_mobs);
            st.setCond(6);
            st.setMemoState("ssq2_mystery_girl", String.valueOf(6), true);
            st.soundEffect(SOUND_MIDDLE);
        }
        return null;
    }

    private void InstantZone_Enter(Player player) {
        ReflectionUtils.simpleEnterInstancedZone(player, inzone_id);
    }
}