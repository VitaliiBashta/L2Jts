package org.mmocore.gameserver.scripts.quests;

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
public class _250_WatchWhatYouEat extends Quest {
    // npc
    private static final int cute_harry = 32743;

    // questitem
    private static final int q_smash_fungus_spore = 15493;
    private static final int q_rug_fungus_spore = 15494;
    private static final int q_rosehip_fragrant_leaf = 15495;

    // mobs
    private static final int smash_fungus = 18864;
    private static final int rug_fungus = 18865;
    private static final int rosehip_fragrant = 18868;

    public _250_WatchWhatYouEat() {
        super(false);
        addStartNpc(cute_harry);
        addTalkId(cute_harry);
        addKillId(smash_fungus, rug_fungus, rosehip_fragrant);
        addLevelCheck(82);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;

        if (event.equalsIgnoreCase("quest_accept")) {
            st.setCond(1);
            st.setMemoState("please_do_not_eat", String.valueOf(1), true);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            htmltext = "cute_harry_q0250_03.htm";
        } else if (event.equalsIgnoreCase("reply_2")) {
            st.giveItems(ADENA_ID, 135661, true);
            st.addExpAndSp(698334, 76369);
            st.removeMemo("please_do_not_eat");
            st.exitQuest(false);
            st.soundEffect(SOUND_FINISH);
            htmltext = "cute_harry_q0250_10.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("please_do_not_eat");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == cute_harry) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "cute_harry_q0250_11.htm";
                            break;
                        default:
                            htmltext = "cute_harry_q0250_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == cute_harry)
                    if (GetMemoState == 1 && (st.ownItemCount(q_smash_fungus_spore) < 1 || st.ownItemCount(q_rug_fungus_spore) < 1 || st.ownItemCount(q_rosehip_fragrant_leaf) < 1))
                        htmltext = "cute_harry_q0250_04.htm";
                    else if (GetMemoState == 1 && st.ownItemCount(q_smash_fungus_spore) >= 1 && st.ownItemCount(q_rug_fungus_spore) >= 1 && st.ownItemCount(q_rosehip_fragrant_leaf) >= 1) {
                        st.takeItems(q_smash_fungus_spore, -1);
                        st.takeItems(q_rug_fungus_spore, -1);
                        st.takeItems(q_rosehip_fragrant_leaf, -1);
                        st.setMemoState("please_do_not_eat", String.valueOf(2), true);
                        htmltext = "cute_harry_q0250_05.htm";
                    } else if (GetMemoState == 2)
                        htmltext = "cute_harry_q0250_06.htm";
                break;
            case COMPLETED:
                if (npcId == cute_harry)
                    htmltext = "cute_harry_q0250_12.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int GetMemoState = st.getInt("please_do_not_eat");

        if (GetMemoState == 1)
            if (npcId == smash_fungus && st.ownItemCount(q_smash_fungus_spore) < 1) {
                st.giveItems(q_smash_fungus_spore, 1);
                st.soundEffect(SOUND_ITEMGET);

                if (st.ownItemCount(q_rug_fungus_spore) >= 1 && st.ownItemCount(q_rosehip_fragrant_leaf) >= 1) {
                    st.setCond(2);
                    st.soundEffect(SOUND_MIDDLE);
                }
            } else if (npcId == rug_fungus && st.ownItemCount(q_rug_fungus_spore) < 1) {
                st.giveItems(q_rug_fungus_spore, 1);
                st.soundEffect(SOUND_ITEMGET);

                if (st.ownItemCount(q_smash_fungus_spore) >= 1 && st.ownItemCount(q_rosehip_fragrant_leaf) >= 1) {
                    st.setCond(2);
                    st.soundEffect(SOUND_MIDDLE);
                }
            } else if (npcId == rosehip_fragrant && st.ownItemCount(q_rosehip_fragrant_leaf) < 1) {
                st.giveItems(q_rosehip_fragrant_leaf, 1);
                st.soundEffect(SOUND_ITEMGET);

                if (st.ownItemCount(q_smash_fungus_spore) >= 1 && st.ownItemCount(q_rug_fungus_spore) >= 1) {
                    st.setCond(2);
                    st.soundEffect(SOUND_MIDDLE);
                }
            }
        return null;
    }
}