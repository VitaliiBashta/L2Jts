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
public class _352_HelpRoodRaiseANewPet extends Quest {
    // npc
    private static final int pet_manager_rood = 31067;

    // mobs
    private static final int lienrik = 20786;
    private static final int lienrik_lad = 20787;

    // questitem
    private static final int lienlik_egg1 = 5860;
    private static final int lienlik_egg2 = 5861;

    public _352_HelpRoodRaiseANewPet() {
        super(false);
        addStartNpc(pet_manager_rood);
        addKillId(lienrik, lienrik_lad);
        addQuestItem(lienlik_egg1, lienlik_egg2);
        addLevelCheck(39, 44);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();

        if (npcId == pet_manager_rood)
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("how_about_new_pet", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "pet_manager_rood_q0352_05.htm";
            } else if (event.equalsIgnoreCase("reply_1"))
                htmltext = "pet_manager_rood_q0352_09.htm";
            else if (event.equalsIgnoreCase("reply_2"))
                htmltext = "pet_manager_rood_q0352_10.htm";
            else if (event.equalsIgnoreCase("reply_3")) {
                st.removeMemo("how_about_new_pet");
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                htmltext = "pet_manager_rood_q0352_11.htm";
            } else if (event.equalsIgnoreCase("reply_4"))
                htmltext = "pet_manager_rood_q0352_04.htm";
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("how_about_new_pet");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == pet_manager_rood) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "pet_manager_rood_q0352_01.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "pet_manager_rood_q0352_02.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == pet_manager_rood)
                    if (GetMemoState == 1)
                        if (st.ownItemCount(lienlik_egg1) < 1 && st.ownItemCount(lienlik_egg2) < 1)
                            htmltext = "pet_manager_rood_q0352_06.htm";
                        else if (st.ownItemCount(lienlik_egg1) >= 1 && st.ownItemCount(lienlik_egg2) < 1) {
                            if (st.ownItemCount(lienlik_egg1) >= 10)
                                st.giveItems(ADENA_ID, st.ownItemCount(lienlik_egg1) * 34 + 4000);
                            else
                                st.giveItems(ADENA_ID, st.ownItemCount(lienlik_egg1) * 34 + 2000);
                            st.takeItems(lienlik_egg1, -1);
                            htmltext = "pet_manager_rood_q0352_07.htm";
                        } else if (st.ownItemCount(lienlik_egg2) >= 1) {
                            st.giveItems(ADENA_ID, 4000 + (st.ownItemCount(lienlik_egg1) * 34 + st.ownItemCount(lienlik_egg2) * 1025));

                            st.takeItems(lienlik_egg2, -1);
                            st.takeItems(lienlik_egg1, -1);
                            htmltext = "pet_manager_rood_q0352_08.htm";
                        }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("how_about_new_pet");
        int npcId = npc.getNpcId();

        if (GetMemoState == 1)
            if (npcId == lienrik) {
                int i0 = Rnd.get(100);
                if (i0 < 46) {
                    st.giveItems(lienlik_egg1, 1);
                    st.soundEffect(SOUND_ITEMGET);
                } else if (i0 < 48) {
                    st.giveItems(lienlik_egg2, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == lienrik_lad) {
                int i0 = Rnd.get(100);
                if (i0 < 69) {
                    st.giveItems(lienlik_egg1, 1);
                    st.soundEffect(SOUND_ITEMGET);
                } else if (i0 < 71) {
                    st.giveItems(lienlik_egg2, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        return null;
    }
}