package org.mmocore.gameserver.scripts.quests;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
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
public class _003_WilltheSealbeBroken extends Quest {
    // npc
    private final static int redry = 30141;
    // mobs
    private final static int onyx_beast = 20031;
    private final static int tainted_zombie = 20041;
    private final static int stink_zombie = 20046;
    private final static int least_succubus = 20048;
    private final static int least_succubus_turen = 20052;
    private final static int least_succubus_tilfo = 20057;
    // etcitem
    private final static int scrl_of_ench_am_d = 956;
    // questitem
    private final static int onyx_beast_eye = 1081;
    private final static int taint_stone = 1082;
    private final static int succubus_blood = 1083;

    public _003_WilltheSealbeBroken() {
        super(false);
        addStartNpc(redry);
        addKillId(onyx_beast, tainted_zombie, stink_zombie, least_succubus, least_succubus_turen, least_succubus_tilfo);
        addQuestItem(onyx_beast_eye, taint_stone, succubus_blood);
        addLevelCheck(16, 26);
        addRaceCheck(PlayerRace.darkelf);
    }

    @Override
    public String onEvent(final String event, final QuestState st, final NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == redry) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("release_darkelf_elder1", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "redry_q0003_03.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(final NpcInstance npc, final QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("release_darkelf_elder1");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED: {
                if (npcId == redry) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "redry_q0003_01.htm";
                            break;
                        case RACE:
                            htmltext = "redry_q0003_00.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "redry_q0003_02.htm";
                            break;
                    }
                }
                break;
            }
            case STARTED: {
                if (npcId == redry) {
                    if (GetMemoState == 1 && st.ownItemCount(onyx_beast_eye) >= 1 && st.ownItemCount(taint_stone) >= 1 && st.ownItemCount(succubus_blood) >= 1) {
                        st.giveItems(scrl_of_ench_am_d, 1);
                        st.takeItems(onyx_beast_eye, -1);
                        st.takeItems(taint_stone, -1);
                        st.takeItems(succubus_blood, -1);
                        st.removeMemo("release_darkelf_elder1");
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                        htmltext = "redry_q0003_06.htm";
                    } else {
                        htmltext = "redry_q0003_04.htm";
                    }
                }
                break;
            }
        }
        return htmltext;
    }

    @Override
    public String onKill(final NpcInstance npc, final QuestState st) {
        final int GetMemoState = st.getInt("release_darkelf_elder1");
        final int npcId = npc.getNpcId();
        if (GetMemoState == 1) {
            if (npcId == onyx_beast) {
                st.giveItems(onyx_beast_eye, 1);
                st.soundEffect(SOUND_MIDDLE);
                if (st.ownItemCount(taint_stone) >= 1 && st.ownItemCount(succubus_blood) >= 1) {
                    st.setCond(2);
                }
            } else if (npcId == tainted_zombie || npcId == stink_zombie) {
                st.giveItems(taint_stone, 1);
                st.soundEffect(SOUND_MIDDLE);
                if (st.ownItemCount(onyx_beast_eye) >= 1 && st.ownItemCount(succubus_blood) >= 1) {
                    st.setCond(2);
                }
            } else if (npcId == least_succubus || npcId == least_succubus_turen || npcId == least_succubus_tilfo) {
                st.giveItems(succubus_blood, 1);
                st.soundEffect(SOUND_MIDDLE);
                if (st.ownItemCount(onyx_beast_eye) >= 1 && st.ownItemCount(taint_stone) >= 1) {
                    st.setCond(2);
                }
            }
        }
        return null;
    }
}