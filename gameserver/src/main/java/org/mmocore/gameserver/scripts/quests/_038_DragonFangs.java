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
public class _038_DragonFangs extends Quest {
    // npc
    private final static int magister_rohmer = 30344;
    private final static int guard_luis = 30386;
    private final static int iris = 30034;

    // mobs
    private final static int langk_lizardman_sub_ldr = 20357;
    private final static int langk_lizardman_sentinel = 21100;
    private final static int langk_lizardman_leader = 20356;
    private final static int langk_lizardman_shaman = 21101;

    // questitem
    private final static int q_liz_feather = 7173;
    private final static int q_liz_totem_tooth1 = 7174;
    private final static int q_liz_totem_tooth2 = 7175;
    private final static int q_liz_letter1 = 7176;
    private final static int q_liz_letter2 = 7177;

    // etcitem
    private final static int bone_helmet = 45;
    private final static int aspis = 627;
    private final static int leather_gauntlet = 605;
    private final static int blue_buckskin_boots = 1123;

    public _038_DragonFangs() {
        super(false);
        addStartNpc(guard_luis);
        addTalkId(iris, magister_rohmer);
        addKillId(langk_lizardman_sub_ldr, langk_lizardman_sentinel, langk_lizardman_leader, langk_lizardman_shaman);
        addQuestItem(q_liz_feather, q_liz_totem_tooth1, q_liz_totem_tooth2, q_liz_letter1, q_liz_letter2);
        addLevelCheck(19, 29);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetHTMLCookie = st.getInt("tooth_of_dragon_cookie");
        int npcId = npc.getNpcId();

        if (npcId == guard_luis) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("tooth_of_dragon", String.valueOf(1 * 10 + 1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "guard_luis_q0038_0104.htm";
            } else if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 2 - 1)
                if (st.ownItemCount(q_liz_feather) >= 100) {
                    st.setCond(3);
                    st.setMemoState("tooth_of_dragon", String.valueOf(2 * 10 + 1), true);
                    st.takeItems(q_liz_feather, 100);
                    st.giveItems(q_liz_totem_tooth1, 1);
                    htmltext = "guard_luis_q0038_0201.htm";
                    st.soundEffect(SOUND_MIDDLE);
                } else
                    htmltext = "guard_luis_q0038_0202.htm";
        } else if (npcId == iris) {
            if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 3 - 1) {
                if (st.ownItemCount(q_liz_totem_tooth1) >= 1) {
                    st.setCond(4);
                    st.setMemoState("tooth_of_dragon", String.valueOf(3 * 10 + 1), true);
                    st.takeItems(q_liz_totem_tooth1, 1);
                    st.giveItems(q_liz_letter1, 1);
                    htmltext = "iris_q0038_0301.htm";
                    st.soundEffect(SOUND_MIDDLE);
                } else
                    htmltext = "iris_q0038_0302.htm";
            } else if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 5 - 1) {
                if (st.ownItemCount(q_liz_letter2) >= 1) {
                    st.setCond(6);
                    st.setMemoState("tooth_of_dragon", String.valueOf(5 * 10 + 1), true);
                    st.takeItems(q_liz_letter2, 1);
                    htmltext = "iris_q0038_0501.htm";
                    st.soundEffect(SOUND_MIDDLE);
                } else
                    htmltext = "iris_q0038_0502.htm";
            } else if (event.equalsIgnoreCase("reply_3") && GetHTMLCookie == 6 - 1)
                if (st.ownItemCount(q_liz_totem_tooth2) >= 50) {
                    int i1 = Rnd.get(1000);
                    st.takeItems(q_liz_totem_tooth2, -1);
                    st.addExpAndSp(435117, 23977);
                    if (i1 < 250) {
                        st.giveItems(bone_helmet, 1);
                        st.giveItems(ADENA_ID, 5200);
                    } else if (i1 < 500) {
                        st.giveItems(aspis, 1);
                        st.giveItems(ADENA_ID, 1500);
                    } else if (i1 < 750) {
                        st.giveItems(blue_buckskin_boots, 1);
                        st.giveItems(ADENA_ID, 3200);
                    } else if (i1 < 1000) {
                        st.giveItems(leather_gauntlet, 1);
                        st.giveItems(ADENA_ID, 3200);
                    }
                    st.removeMemo("tooth_of_dragon");
                    st.removeMemo("tooth_of_dragon_cookie");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "iris_q0038_0601.htm";
                } else
                    htmltext = "iris_q0038_0602.htm";
        } else if (npcId == magister_rohmer)
            if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 4 - 1)
                if (st.ownItemCount(q_liz_letter1) >= 1) {
                    st.setCond(5);
                    st.setMemoState("tooth_of_dragon", String.valueOf(4 * 10 + 1), true);
                    st.takeItems(q_liz_letter1, 1);
                    st.giveItems(q_liz_letter2, 1);
                    htmltext = "magister_rohmer_q0038_0401.htm";
                    st.soundEffect(SOUND_MIDDLE);
                } else
                    htmltext = "magister_rohmer_q0038_0402.htm";
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("tooth_of_dragon");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == guard_luis) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "guard_luis_q0038_0103.htm";
                            break;
                        default:
                            htmltext = "guard_luis_q0038_0101.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == guard_luis) {
                    if (GetMemoState >= 1 * 10 + 1 && GetMemoState <= 1 * 10 + 2) {
                        if (GetMemoState == 1 * 10 + 2 && st.ownItemCount(q_liz_feather) >= 100) {
                            st.setMemoState("tooth_of_dragon_cookie", String.valueOf(1), true);
                            htmltext = "guard_luis_q0038_0105.htm";
                        } else
                            htmltext = "guard_luis_q0038_0106.htm";
                    } else if (GetMemoState == 2 * 10 + 1)
                        htmltext = "guard_luis_q0038_0203.htm";
                } else if (npcId == iris) {
                    if (st.ownItemCount(q_liz_totem_tooth1) >= 1 && GetMemoState == 2 * 10 + 1) {
                        st.setMemoState("tooth_of_dragon_cookie", String.valueOf(2), true);
                        htmltext = "iris_q0038_0201.htm";
                    } else if (GetMemoState == 3 * 10 + 1)
                        htmltext = "iris_q0038_0303.htm";
                    else if (st.ownItemCount(q_liz_letter2) >= 1 && GetMemoState == 4 * 10 + 1) {
                        st.setMemoState("tooth_of_dragon_cookie", String.valueOf(4), true);
                        htmltext = "iris_q0038_0401.htm";
                    } else if (GetMemoState <= 5 * 10 + 2 && GetMemoState >= 5 * 10 + 1)
                        if (GetMemoState == 5 * 10 + 2 && st.ownItemCount(q_liz_totem_tooth2) >= 50) {
                            st.setMemoState("tooth_of_dragon_cookie", String.valueOf(5), true);
                            htmltext = "iris_q0038_0503.htm";
                        } else
                            htmltext = "iris_q0038_0504.htm";
                } else if (npcId == magister_rohmer)
                    if (st.ownItemCount(q_liz_letter1) >= 1 && GetMemoState == 3 * 10 + 1) {
                        st.setMemoState("tooth_of_dragon_cookie", String.valueOf(3), true);
                        htmltext = "magister_rohmer_q0038_0301.htm";
                    } else if (GetMemoState == 4 * 10 + 1)
                        htmltext = "magister_rohmer_q0038_0403.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("tooth_of_dragon");
        int npcId = npc.getNpcId();

        if (GetMemoState == 1 * 10 + 1) {
            if (npcId == langk_lizardman_sub_ldr || npcId == langk_lizardman_sentinel)
                if (st.ownItemCount(q_liz_feather) + 1 >= 100) {
                    if (st.ownItemCount(q_liz_feather) < 100) {
                        st.giveItems(q_liz_feather, 100 - st.ownItemCount(q_liz_feather));
                        st.soundEffect(SOUND_MIDDLE);
                    }
                    st.setCond(2);
                    st.setMemoState("tooth_of_dragon", String.valueOf(1 * 10 + 2), true);
                } else {
                    st.giveItems(q_liz_feather, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
        } else if (GetMemoState == 5 * 10 + 1)
            if (npcId == langk_lizardman_leader || npcId == langk_lizardman_shaman) {
                int i4 = Rnd.get(1000);
                if (i4 < 500)
                    if (st.ownItemCount(q_liz_totem_tooth2) + 1 >= 50) {
                        if (st.ownItemCount(q_liz_totem_tooth2) < 50) {
                            st.giveItems(q_liz_totem_tooth2, 50 - st.ownItemCount(q_liz_totem_tooth2));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        st.setCond(7);
                        st.setMemoState("tooth_of_dragon", String.valueOf(5 * 10 + 2), true);
                    } else {
                        st.giveItems(q_liz_totem_tooth2, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            }
        return null;
    }
}