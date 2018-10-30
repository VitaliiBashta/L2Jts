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
public class _160_NerupasRequest extends Quest {
    // npc
    private final static int nerupa = 30370;
    private final static int uno = 30147;
    private final static int cel = 30149;
    private final static int jud = 30152;

    // questitem
    private final static int silvery_spidersilk = 1026;
    private final static int unos_receipt = 1027;
    private final static int cels_ticket = 1028;
    private final static int nightshade_leaf = 1029;

    // etcitem
    private final static int lesser_healing_potion = 1060;

    public _160_NerupasRequest() {
        super(false);
        addStartNpc(nerupa);
        addTalkId(uno, cel, jud);
        addQuestItem(silvery_spidersilk, unos_receipt, cels_ticket, nightshade_leaf);
        addLevelCheck(3, 7);
        addRaceCheck(PlayerRace.elf);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();

        if (npcId == nerupa) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                if (st.ownItemCount(silvery_spidersilk) == 0)
                    st.giveItems(silvery_spidersilk, 1);
                htmltext = "nerupa_q0160_04.htm";
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
                if (npcId == nerupa) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "nerupa_q0160_02.htm";
                            st.exitQuest(true);
                            break;
                        case RACE:
                            htmltext = "nerupa_q0160_00.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "nerupa_q0160_03.htm";
                            return htmltext;
                    }
                }
                break;
            case STARTED:
                if (npcId == nerupa) {
                    if (st.ownItemCount(silvery_spidersilk) != 0 || st.ownItemCount(unos_receipt) != 0 || st.ownItemCount(cels_ticket) != 0)
                        htmltext = "nerupa_q0160_05.htm";
                    else if (st.ownItemCount(nightshade_leaf) != 0) {
                        st.takeItems(nightshade_leaf, -1);
                        st.giveItems(lesser_healing_potion, 5);
                        st.addExpAndSp(1000, 0);
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                        htmltext = "nerupa_q0160_06.htm";
                    }
                } else if (npcId == uno) {
                    if (st.ownItemCount(silvery_spidersilk) != 0) {
                        st.setCond(2);
                        st.takeItems(silvery_spidersilk, -1);
                        if (st.ownItemCount(unos_receipt) == 0)
                            st.giveItems(unos_receipt, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "uno_q0160_01.htm";
                    } else if (st.ownItemCount(unos_receipt) != 0)
                        htmltext = "uno_q0160_02.htm";
                    else if (st.ownItemCount(nightshade_leaf) != 0)
                        htmltext = "uno_q0160_03.htm";
                } else if (npcId == cel) {
                    if (st.ownItemCount(unos_receipt) != 0) {
                        st.setCond(3);
                        st.takeItems(unos_receipt, -1);
                        if (st.ownItemCount(cels_ticket) == 0)
                            st.giveItems(cels_ticket, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "cel_q0160_01.htm";
                    } else if (st.ownItemCount(cels_ticket) != 0)
                        htmltext = "cel_q0160_02.htm";
                    else if (st.ownItemCount(nightshade_leaf) != 0)
                        htmltext = "cel_q0160_03.htm";
                } else if (npcId == jud)
                    if (st.ownItemCount(cels_ticket) != 0) {

                        st.takeItems(cels_ticket, -1);
                        if (st.ownItemCount(nightshade_leaf) == 0) {
                            st.setCond(4);
                            st.giveItems(nightshade_leaf, 1);
                            st.soundEffect(SOUND_MIDDLE);
                            htmltext = "jud_q0160_01.htm";
                        }
                    } else if (st.ownItemCount(nightshade_leaf) != 0)
                        htmltext = "jud_q0160_02.htm";
                break;
        }

        return htmltext;
    }
}