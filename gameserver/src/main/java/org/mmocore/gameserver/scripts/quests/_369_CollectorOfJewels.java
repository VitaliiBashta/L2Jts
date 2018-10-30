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
public class _369_CollectorOfJewels extends Quest {
    // mobs
    private final static int salamander_lakin = 20609;
    private final static int salamander_rowin = 20612;
    private final static int undine_lakin = 20616;
    private final static int undine_rowin = 20619;
    private final static int roxide = 20747;
    private final static int death_fire = 20749;
    // npc
    private static int magister_nell = 30376;
    // questitem
    private static int flair_shard = 5882;
    private static int freezing_shard = 5883;

    public _369_CollectorOfJewels() {
        super(false);
        addStartNpc(magister_nell);
        addKillId(salamander_lakin, salamander_rowin, undine_lakin, undine_rowin, roxide, death_fire);
        addQuestItem(flair_shard, freezing_shard);
        addLevelCheck(25, 37);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();

        if (npcId == magister_nell)
            if (event.equalsIgnoreCase("quest_accept"))
                if (event.equalsIgnoreCase("reply_1")) {
                    st.setCond(3);
                    st.setMemoState("man_collect_element_gem", String.valueOf(3), true);
                    htmltext = "magister_nell_q0369_07.htm";
                } else if (event.equalsIgnoreCase("reply_2")) {
                    st.takeItems(flair_shard, -1);
                    st.takeItems(freezing_shard, -1);
                    st.removeMemo("man_collect_element_gem");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(true);
                    htmltext = "magister_nell_q0369_08.htm";
                }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("man_collect_element_gem");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == magister_nell) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "magister_nell_q0369_01.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "magister_nell_q0369_02.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == magister_nell)
                    if ((st.ownItemCount(freezing_shard) < 50 || st.ownItemCount(flair_shard) < 50) && GetMemoState == 1)
                        htmltext = "magister_nell_q0369_04.htm";
                    else if (st.ownItemCount(freezing_shard) >= 50 && st.ownItemCount(flair_shard) >= 50 && GetMemoState == 1) {
                        st.giveItems(ADENA_ID, 31810);
                        st.takeItems(flair_shard, -1);
                        st.takeItems(freezing_shard, -1);
                        st.setMemoState("man_collect_element_gem", String.valueOf(2), true);
                        htmltext = "magister_nell_q0369_05.htm";
                    } else if (GetMemoState == 2)
                        htmltext = "magister_nell_q0369_09.htm";
                    else if (GetMemoState == 3 && (st.ownItemCount(freezing_shard) < 200 || st.ownItemCount(flair_shard) < 200))
                        htmltext = "magister_nell_q0369_10.htm";
                    else if (GetMemoState == 3 && st.ownItemCount(freezing_shard) >= 200 && st.ownItemCount(flair_shard) >= 200) {
                        st.giveItems(ADENA_ID, 84415);
                        st.takeItems(flair_shard, -1);
                        st.takeItems(freezing_shard, -1);
                        st.removeMemo("man_collect_element_gem");
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(true);
                        htmltext = "magister_nell_q0369_11.htm";
                    }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("man_collect_element_gem");
        int npcId = npc.getNpcId();

        if (npcId == salamander_lakin) {
            if (Rnd.get(100) < 75) {
                st.giveItems(flair_shard, 1);
                if (GetMemoState == 1 && st.ownItemCount(freezing_shard) >= 50 && st.ownItemCount(flair_shard) >= 49) {
                    st.setCond(2);
                    st.soundEffect(SOUND_MIDDLE);
                } else if (GetMemoState == 3 && st.ownItemCount(freezing_shard) >= 200 && st.ownItemCount(flair_shard) >= 199) {
                    st.setCond(4);
                    st.soundEffect(SOUND_MIDDLE);
                } else
                    st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == salamander_rowin) {
            if (Rnd.get(100) < 91) {
                st.giveItems(flair_shard, 1);
                if (GetMemoState == 1 && st.ownItemCount(freezing_shard) >= 50 && st.ownItemCount(flair_shard) >= 49) {
                    st.setCond(2);
                    st.soundEffect(SOUND_MIDDLE);
                } else if (GetMemoState == 3 && st.ownItemCount(freezing_shard) >= 200 && st.ownItemCount(flair_shard) >= 199) {
                    st.setCond(4);
                    st.soundEffect(SOUND_MIDDLE);
                } else
                    st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == undine_lakin) {
            if (Rnd.get(100) < 80) {
                st.giveItems(flair_shard, 1);
                if (GetMemoState == 1 && st.ownItemCount(freezing_shard) >= 50 && st.ownItemCount(flair_shard) >= 49) {
                    st.setCond(2);
                    st.soundEffect(SOUND_MIDDLE);
                } else if (GetMemoState == 3 && st.ownItemCount(freezing_shard) >= 200 && st.ownItemCount(flair_shard) >= 199) {
                    st.setCond(4);
                    st.soundEffect(SOUND_MIDDLE);
                } else
                    st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == undine_rowin) {
            if (Rnd.get(100) < 87) {
                st.giveItems(flair_shard, 1);
                if (GetMemoState == 1 && st.ownItemCount(freezing_shard) >= 50 && st.ownItemCount(flair_shard) >= 49) {
                    st.setCond(2);
                    st.soundEffect(SOUND_MIDDLE);
                } else if (GetMemoState == 3 && st.ownItemCount(freezing_shard) >= 200 && st.ownItemCount(flair_shard) >= 199) {
                    st.setCond(4);
                    st.soundEffect(SOUND_MIDDLE);
                } else
                    st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == roxide || npcId == death_fire)
            if (Rnd.get(100) < 2) {
                if (GetMemoState == 1 && st.ownItemCount(freezing_shard) >= 49)
                    st.giveItems(freezing_shard, 1);
                else if (GetMemoState == 3 && st.ownItemCount(freezing_shard) >= 199)
                    st.giveItems(freezing_shard, 1);
                else
                    st.giveItems(freezing_shard, 2);
                if (GetMemoState == 1 && st.ownItemCount(freezing_shard) >= 49 && st.ownItemCount(flair_shard) >= 50) {
                    st.setCond(2);
                    st.soundEffect(SOUND_MIDDLE);
                } else if (GetMemoState == 3 && st.ownItemCount(freezing_shard) >= 199 && st.ownItemCount(flair_shard) >= 200) {
                    st.setCond(4);
                    st.soundEffect(SOUND_MIDDLE);
                } else
                    st.soundEffect(SOUND_ITEMGET);
            } else {
                st.giveItems(freezing_shard, 1);
                if (GetMemoState == 1 && st.ownItemCount(freezing_shard) >= 49 && st.ownItemCount(flair_shard) >= 50) {
                    st.setCond(2);
                    st.soundEffect(SOUND_MIDDLE);
                } else if (GetMemoState == 3 && st.ownItemCount(freezing_shard) >= 199 && st.ownItemCount(flair_shard) >= 200) {
                    st.setCond(4);
                    st.soundEffect(SOUND_MIDDLE);
                } else
                    st.soundEffect(SOUND_ITEMGET);
            }
        return null;
    }
}