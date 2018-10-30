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
public class _356_DigUpTheSeaOfSpores extends Quest {
    // npc
    private static final int magister_gauen = 30717;
    // mobs
    private static final int spore_zombie = 20562;
    private static final int rot_tree = 20558;
    // questitem
    private static final int carnivore_spore = 5865;
    private static final int herbivorous_spore = 5866;

    public _356_DigUpTheSeaOfSpores() {
        super(false);
        addStartNpc(magister_gauen);
        addKillId(spore_zombie, rot_tree);
        addQuestItem(carnivore_spore, herbivorous_spore);
        addLevelCheck(43, 51);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == magister_gauen) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "magister_gauen_q0356_06.htm";
            } else if (event.equalsIgnoreCase("reply_1"))
                htmltext = "magister_gauen_q0356_05.htm";
            else if (event.equalsIgnoreCase("reply_2")) {
                st.setCond(1);
                htmltext = "magister_gauen_q0356_11.htm";
            } else if (event.equalsIgnoreCase("reply_3") && st.ownItemCount(herbivorous_spore) >= 50) {
                st.addExpAndSp(31850, 0);
                st.takeItems(herbivorous_spore, -1);
                htmltext = "magister_gauen_q0356_12.htm";
            } else if (event.equalsIgnoreCase("reply_4") && st.ownItemCount(carnivore_spore) >= 50) {
                st.addExpAndSp(0, 1820);
                st.takeItems(carnivore_spore, -1);
                htmltext = "magister_gauen_q0356_13.htm";
            } else if (event.equalsIgnoreCase("reply_5") && st.ownItemCount(carnivore_spore) >= 50 && st.ownItemCount(herbivorous_spore) >= 50) {
                st.addExpAndSp(45500, 2600);
                st.takeItems(herbivorous_spore, -1);
                st.takeItems(carnivore_spore, -1);
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                htmltext = "magister_gauen_q0356_14.htm";
            } else if (event.equalsIgnoreCase("reply_6"))
                htmltext = "magister_gauen_q0356_15.htm";
            else if (event.equalsIgnoreCase("reply_7") && st.ownItemCount(carnivore_spore) >= 50 && st.ownItemCount(herbivorous_spore) >= 50) {
                int i0 = Rnd.get(100);
                st.takeItems(herbivorous_spore, -1);
                st.takeItems(carnivore_spore, -1);
                if (i0 < 20) {
                    st.giveItems(ADENA_ID, 44000);
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(true);
                    htmltext = "magister_gauen_q0356_16.htm";
                } else if (i0 < 70) {
                    st.giveItems(ADENA_ID, 20950);
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(true);
                    htmltext = "magister_gauen_q0356_17.htm";
                } else if (i0 >= 70) {
                    st.giveItems(ADENA_ID, 10400);
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(true);
                    htmltext = "magister_gauen_q0356_18.htm";
                }
            } else if (event.equalsIgnoreCase("reply_8")) {
                st.takeItems(herbivorous_spore, -1);
                st.takeItems(carnivore_spore, -1);
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                htmltext = "magister_gauen_q0356_19.htm";
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
                if (npcId == magister_gauen) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "magister_gauen_q0356_01.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "magister_gauen_q0356_02.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == magister_gauen) {
                    if (st.ownItemCount(herbivorous_spore) < 50 && st.ownItemCount(carnivore_spore) < 50)
                        htmltext = "magister_gauen_q0356_07.htm";
                    else if (st.ownItemCount(herbivorous_spore) >= 50 && st.ownItemCount(carnivore_spore) < 50)
                        htmltext = "magister_gauen_q0356_08.htm";
                    else if (st.ownItemCount(herbivorous_spore) < 50 && st.ownItemCount(carnivore_spore) >= 50)
                        htmltext = "magister_gauen_q0356_09.htm";
                    else if (st.ownItemCount(herbivorous_spore) >= 50 && st.ownItemCount(carnivore_spore) >= 50)
                        htmltext = "magister_gauen_q0356_10.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == spore_zombie && st.ownItemCount(carnivore_spore) < 50) {
            if (Rnd.get(100) < 94) {
                st.giveItems(carnivore_spore, 1);
                st.soundEffect(SOUND_ITEMGET);
                if (st.ownItemCount(herbivorous_spore) >= 50 && st.ownItemCount(carnivore_spore) >= 49) {
                    st.setCond(3);
                    st.soundEffect(SOUND_MIDDLE);
                } else if (st.ownItemCount(carnivore_spore) >= 49) {
                    st.setCond(2);
                    st.soundEffect(SOUND_MIDDLE);
                } else
                    st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == rot_tree && st.ownItemCount(herbivorous_spore) < 50) {
            if (Rnd.get(100) < 73) {
                st.giveItems(herbivorous_spore, 1);
                st.soundEffect(SOUND_ITEMGET);
                if (st.ownItemCount(herbivorous_spore) >= 49 && st.ownItemCount(carnivore_spore) >= 50) {
                    st.setCond(3);
                    st.soundEffect(SOUND_MIDDLE);
                } else if (st.ownItemCount(herbivorous_spore) >= 49) {
                    st.setCond(2);
                    st.soundEffect(SOUND_MIDDLE);
                } else
                    st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }
}