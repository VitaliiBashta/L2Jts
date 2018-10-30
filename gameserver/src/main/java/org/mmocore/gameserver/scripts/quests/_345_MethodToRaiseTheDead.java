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
public class _345_MethodToRaiseTheDead extends Quest {
    // npc
    private final static int dorothy_the_locksmith = 30970;
    private final static int mad_doctor_orpheus = 30971;
    private final static int magister_xenovia = 30912;
    private final static int medium_jar = 30973;

    // mobs
    private final static int crokian = 20789;
    private final static int crokian_warrior = 20791;

    // questitem
    private final static int q0345_bone_arm = 4274;
    private final static int q0345_bone_leg = 4275;
    private final static int q0345_bone_skull = 4276;
    private final static int q0345_bone_rib = 4277;
    private final static int q0345_bone_spine = 4278;
    private final static int q0345_bone_useless = 4280;
    private final static int q0345_calling_spirit_powder = 4281;

    // etcitem
    private final static int bill_of_iason = 4407;
    private final static int q_loot_13 = 3456;

    public _345_MethodToRaiseTheDead() {
        super(false);
        addStartNpc(dorothy_the_locksmith);
        addTalkId(dorothy_the_locksmith, magister_xenovia, medium_jar, mad_doctor_orpheus);
        addQuestItem(q0345_bone_arm, q0345_bone_leg, q0345_bone_skull, q0345_bone_rib, q0345_bone_spine, q0345_calling_spirit_powder);
        addKillId(crokian, crokian_warrior);
        addLevelCheck(35, 42);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("how_to_face_the_dead");
        int GetMemoStateEx = st.getInt("how_to_face_the_dead_ex");
        int npcId = npc.getNpcId();

        if (npcId == dorothy_the_locksmith) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "dorothy_the_locksmith_q0345_03.htm";
            } else if (event.equalsIgnoreCase("reply_1")) {
                htmltext = "dorothy_the_locksmith_q0345_04.htm";
                st.setMemoState("how_to_face_the_dead", String.valueOf(1), true);
            } else if (event.equalsIgnoreCase("reply_2"))
                if (GetMemoState == 1 && st.ownItemCount(q0345_bone_arm) >= 1 && st.ownItemCount(q0345_bone_leg) >= 1 && st.ownItemCount(q0345_bone_skull) >= 1 && st.ownItemCount(q0345_bone_rib) >= 1 && st.ownItemCount(q0345_bone_spine) >= 1) {
                    st.setCond(2);
                    st.setMemoState("how_to_face_the_dead", String.valueOf(2), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "dorothy_the_locksmith_q0345_07.htm";
                }
        } else if (npcId == magister_xenovia) {
            if (event.equalsIgnoreCase("reply_1"))
                htmltext = "magister_xenovia_q0345_02.htm";
            else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 2)
                    if (st.ownItemCount(ADENA_ID) >= 1000) {
                        st.setCond(3);
                        st.setMemoState("how_to_face_the_dead", String.valueOf(3), true);
                        st.giveItems(q0345_calling_spirit_powder, 1);
                        st.takeItems(ADENA_ID, 1000);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "magister_xenovia_q0345_03.htm";
                    } else
                        htmltext = "magister_xenovia_q0345_04.htm";
            } else if (event.equalsIgnoreCase("reply_4"))
                htmltext = "magister_xenovia_q0345_06.htm";
        } else if (npcId == medium_jar) {
            if (event.equalsIgnoreCase("reply_1"))
                if (GetMemoStateEx == 1)
                    htmltext = "medium_jar_q0345_03.htm";
                else if (GetMemoStateEx == 2)
                    htmltext = "medium_jar_q0345_05.htm";
                else if (GetMemoStateEx == 3)
                    htmltext = "medium_jar_q0345_07.htm";
            if (event.equalsIgnoreCase("reply_2"))
                if (GetMemoState == 7 && GetMemoStateEx == 1) {
                    st.setCond(6);
                    st.setMemoState("how_to_face_the_dead", String.valueOf(8), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "medium_jar_q0345_04.htm";
                }
            if (event.equalsIgnoreCase("reply_3"))
                if (GetMemoState == 7 && GetMemoStateEx == 2) {
                    st.setCond(6);
                    st.setMemoState("how_to_face_the_dead", String.valueOf(8), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "medium_jar_q0345_06.htm";
                }
            if (event.equalsIgnoreCase("reply_4"))
                if (GetMemoState == 7 && GetMemoStateEx == 3) {
                    st.setCond(7);
                    st.setMemoState("how_to_face_the_dead", String.valueOf(8), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "medium_jar_q0345_08.htm";
                }
        } else if (npcId == mad_doctor_orpheus)
            if (event.equalsIgnoreCase("reply_4"))
                htmltext = "mad_doctor_orpheus_q0345_10.htm";
            else if (event.equalsIgnoreCase("reply_5"))
                if (st.ownItemCount(q0345_bone_useless) > 0) {
                    st.giveItems(ADENA_ID, st.ownItemCount(q0345_bone_useless) * 104);
                    st.takeItems(q0345_bone_useless, -1);
                    htmltext = "mad_doctor_orpheus_q0345_11.htm";
                }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("how_to_face_the_dead");
        int GetMemoStateEx = st.getInt("how_to_face_the_dead_ex");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == dorothy_the_locksmith) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "dorothy_the_locksmith_q0345_01.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "dorothy_the_locksmith_q0345_02.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == dorothy_the_locksmith) {
                    if (GetMemoState == 0) {
                        htmltext = "dorothy_the_locksmith_q0345_04.htm";
                        st.setMemoState("how_to_face_the_dead", String.valueOf(1), true);
                    } else if (GetMemoState == 1 && (st.ownItemCount(q0345_bone_arm) == 0 || st.ownItemCount(q0345_bone_leg) == 0 || st.ownItemCount(q0345_bone_skull) == 0 || st.ownItemCount(q0345_bone_rib) == 0 || st.ownItemCount(q0345_bone_spine) == 0))
                        htmltext = "dorothy_the_locksmith_q0345_05.htm";
                    else if (GetMemoState == 1 && st.ownItemCount(q0345_bone_arm) >= 1 && st.ownItemCount(q0345_bone_leg) >= 1 && st.ownItemCount(q0345_bone_skull) >= 1 && st.ownItemCount(q0345_bone_rib) >= 1 && st.ownItemCount(q0345_bone_spine) >= 1)
                        htmltext = "dorothy_the_locksmith_q0345_06.htm";
                    else if (GetMemoState == 2)
                        htmltext = "dorothy_the_locksmith_q0345_08.htm";
                    else if (GetMemoState == 3)
                        htmltext = "dorothy_the_locksmith_q0345_09.htm";
                    else if (GetMemoState == 7)
                        htmltext = "dorothy_the_locksmith_q0345_12.htm";
                    else if (GetMemoState == 8 && (GetMemoStateEx == 1 || GetMemoStateEx == 2)) {
                        st.giveItems(bill_of_iason, 3);
                        st.giveItems(ADENA_ID, 5390 + 70 * st.ownItemCount(q0345_bone_useless));
                        st.takeItems(q0345_bone_useless, -1);
                        st.removeMemo("how_to_face_the_dead");
                        st.removeMemo("how_to_face_the_dead_ex");
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(true);
                        htmltext = "dorothy_the_locksmith_q0345_13.htm";
                    } else if (GetMemoState == 8 && GetMemoStateEx == 3) {
                        int i0 = Rnd.get(100);
                        if (i0 <= 92)
                            st.giveItems(bill_of_iason, 5);
                        else
                            st.giveItems(q_loot_13, 1);
                        st.giveItems(ADENA_ID, 3040 + 70 * st.ownItemCount(q0345_bone_useless));
                        st.takeItems(q0345_bone_useless, -1);
                        st.removeMemo("how_to_face_the_dead");
                        st.removeMemo("how_to_face_the_dead_ex");
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(true);
                        htmltext = "dorothy_the_locksmith_q0345_14.htm";
                    }
                } else if (npcId == magister_xenovia) {
                    if (GetMemoState == 2)
                        htmltext = "magister_xenovia_q0345_01.htm";
                    else if (GetMemoState == 7 || GetMemoState == 8 || st.ownItemCount(q0345_calling_spirit_powder) >= 1)
                        htmltext = "magister_xenovia_q0345_07.htm";
                } else if (npcId == medium_jar) {
                    if (GetMemoState == 3) {
                        st.takeItems(q0345_calling_spirit_powder, -1);
                        st.takeItems(q0345_bone_arm, -1);
                        st.takeItems(q0345_bone_leg, -1);
                        st.takeItems(q0345_bone_skull, -1);
                        st.takeItems(q0345_bone_rib, -1);
                        st.takeItems(q0345_bone_spine, -1);
                        st.setMemoState("how_to_face_the_dead", String.valueOf(7), true);
                        int i0 = Rnd.get(100);
                        if (i0 <= 39)
                            st.setMemoState("how_to_face_the_dead_ex", String.valueOf(1), true);
                        else if (i0 <= 79)
                            st.setMemoState("how_to_face_the_dead_ex", String.valueOf(2), true);
                        else
                            st.setMemoState("how_to_face_the_dead_ex", String.valueOf(3), true);
                        htmltext = "medium_jar_q0345_01.htm";
                    } else if (GetMemoState == 7 && GetMemoStateEx == 1)
                        htmltext = "medium_jar_q0345_03t.htm";
                    else if (GetMemoState == 7 && GetMemoStateEx == 2)
                        htmltext = "medium_jar_q0345_05t.htm";
                    else if (GetMemoState == 7 && GetMemoStateEx == 3)
                        htmltext = "medium_jar_q0345_07t.htm";
                    else if (GetMemoState == 8)
                        htmltext = "medium_jar_q0345_09.htm";
                } else if (npcId == mad_doctor_orpheus)
                    if (st.ownItemCount(q0345_bone_useless) > 0)
                        htmltext = "mad_doctor_orpheus_q0345_08.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("how_to_face_the_dead");
        int npcId = npc.getNpcId();

        if (GetMemoState == 1)
            if (npcId == crokian || npcId == crokian_warrior) {
                int i0 = Rnd.get(100);
                if (i0 <= 5) {
                    if (st.ownItemCount(q0345_bone_arm) == 0)
                        st.giveItems(q0345_bone_arm, 1);
                    else
                        st.giveItems(q0345_bone_useless, 1);
                    st.soundEffect(SOUND_ITEMGET);
                } else if (i0 <= 11) {
                    if (st.ownItemCount(q0345_bone_leg) == 0)
                        st.giveItems(q0345_bone_leg, 1);
                    else
                        st.giveItems(q0345_bone_useless, 1);
                    st.soundEffect(SOUND_ITEMGET);
                } else if (i0 <= 17) {
                    if (st.ownItemCount(q0345_bone_skull) == 0)
                        st.giveItems(q0345_bone_skull, 1);
                    else
                        st.giveItems(q0345_bone_useless, 1);
                    st.soundEffect(SOUND_ITEMGET);
                } else if (i0 <= 23) {
                    if (st.ownItemCount(q0345_bone_rib) == 0)
                        st.giveItems(q0345_bone_rib, 1);
                    else
                        st.giveItems(q0345_bone_useless, 1);
                    st.soundEffect(SOUND_ITEMGET);
                } else if (i0 <= 29) {
                    if (st.ownItemCount(q0345_bone_spine) == 0)
                        st.giveItems(q0345_bone_spine, 1);
                    else
                        st.giveItems(q0345_bone_useless, 1);
                    st.soundEffect(SOUND_ITEMGET);
                } else if (i0 <= 60)
                    st.giveItems(q0345_bone_useless, 1);
            }
        return null;
    }
}