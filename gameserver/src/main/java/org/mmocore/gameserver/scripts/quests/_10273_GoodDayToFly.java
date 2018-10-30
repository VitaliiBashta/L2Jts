package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SkillList;
import org.mmocore.gameserver.object.Servitor;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _10273_GoodDayToFly extends Quest {
    // npc
    private final static int engineer_recon = 32557;

    // mob
    private final static int vulture_rider_1lv = 22614;
    private final static int vulture_rider_2lv = 22615;
    private final static int vulture_rider_3lv = 25633;

    // questitem
    private final static int q_necklace_of_riders = 13856;
    private final static int q_certificate_of_recon = 13857;

    // etcitem
    private final static int sb_transform_dash = 13553;
    private final static int sb_transform_shooting = 13554;

    // skill_name
    private final static int s_quest_flying_form_dash1 = 5982;
    private final static int s_quest_flying_form_shooting1 = 5983;

    public _10273_GoodDayToFly() {
        super(false);
        addStartNpc(engineer_recon);
        addKillId(vulture_rider_1lv, vulture_rider_2lv, vulture_rider_3lv);
        addQuestItem(q_necklace_of_riders);
        addLevelCheck(75);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        Servitor c0 = st.getPlayer().getServitor();
        int GetMemoState = st.getInt("good_day_to_fly");
        int GetMemoStateEx = st.getInt("good_day_to_fly_1");

        if (event.equalsIgnoreCase("quest_accept")) {
            st.setCond(1);
            st.setMemoState("good_day_to_fly", String.valueOf(1), true);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            htmltext = "engineer_recon_q10273_07.htm";
        } else if (event.equalsIgnoreCase("reply_4")) {
            if (c0 != null)
                htmltext = "engineer_recon_q10273_09c.htm";
            else {
                st.setMemoState("good_day_to_fly", String.valueOf(2), true);
                st.setMemoState("good_day_to_fly_1", String.valueOf(1), true);
                if (st.getPlayer().isTransformed())
                    st.getPlayer().stopTransformation();
                SkillTable.getInstance().getSkillEntry(s_quest_flying_form_dash1, 1).getEffects(st.getPlayer(), st.getPlayer(), false, false);
                st.getPlayer().sendPacket(new SkillList(st.getPlayer()));
                htmltext = "engineer_recon_q10273_09a.htm";
            }
        } else if (event.equalsIgnoreCase("reply_5") && GetMemoState == 1) {
            if (c0 != null)
                htmltext = "engineer_recon_q10273_09c.htm";
            else {
                st.setMemoState("good_day_to_fly", String.valueOf(2), true);
                st.setMemoState("good_day_to_fly_1", String.valueOf(2), true);
                if (st.getPlayer().isTransformed())
                    st.getPlayer().stopTransformation();
                SkillTable.getInstance().getSkillEntry(s_quest_flying_form_shooting1, 1).getEffects(st.getPlayer(), st.getPlayer(), false, false);
                st.getPlayer().sendPacket(new SkillList(st.getPlayer()));
                htmltext = "engineer_recon_q10273_09b.htm";
            }
        } else if (event.equalsIgnoreCase("reply_6") && GetMemoState == 1)
            htmltext = "engineer_recon_q10273_10.htm";
        else if (event.equalsIgnoreCase("reply_7") && GetMemoState == 2) {
            if (c0 != null)
                htmltext = "engineer_recon_q10273_09c.htm";
            else {
                if (GetMemoStateEx == 1) {
                    if (st.getPlayer().isTransformed() || st.getPlayer().isMounted()) {
                        st.getPlayer().sendPacket(SystemMsg.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
                        return null;
                    }
                    SkillTable.getInstance().getSkillEntry(s_quest_flying_form_dash1, 1).getEffects(st.getPlayer(), st.getPlayer(), false, false);
                    st.getPlayer().sendPacket(new SkillList(st.getPlayer()));
                } else if (GetMemoStateEx == 2) {
                    if (st.getPlayer().isTransformed() || st.getPlayer().isMounted()) {
                        st.getPlayer().sendPacket(SystemMsg.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
                        return null;
                    }
                    SkillTable.getInstance().getSkillEntry(s_quest_flying_form_shooting1, 1).getEffects(st.getPlayer(), st.getPlayer(), false, false);
                    st.getPlayer().sendPacket(new SkillList(st.getPlayer()));
                }
                htmltext = "engineer_recon_q10273_11a.htm";
            }
        } else if (event.equalsIgnoreCase("reply_8") && GetMemoState == 2)
            htmltext = "engineer_recon_q10273_11b.htm";
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("good_day_to_fly");
        int npcId = npc.getNpcId();
        int id = st.getState();
        int GetMemoStateEx = st.getInt("good_day_to_fly_1");

        switch (id) {
            case CREATED:
                if (npcId == engineer_recon) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "engineer_recon_q10273_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "engineer_recon_q10273_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == engineer_recon) {
                    if (GetMemoState == 1)
                        htmltext = "engineer_recon_q10273_08.htm";
                    else if (GetMemoState == 2)
                        htmltext = "engineer_recon_q10273_11.htm";
                    else if (GetMemoState == 3) {
                        if (GetMemoStateEx == 1)
                            st.giveItems(sb_transform_dash, 1);
                        else if (GetMemoStateEx == 2)
                            st.giveItems(sb_transform_shooting, 1);
                        st.giveItems(q_certificate_of_recon, 1);
                        st.addExpAndSp(25160, 2525);
                        st.takeItems(q_necklace_of_riders, -1);
                        st.soundEffect(SOUND_FINISH);
                        st.removeMemo("good_day_to_fly");
                        st.removeMemo("good_day_to_fly_1");
                        st.exitQuest(false);
                        htmltext = "engineer_recon_q10273_12.htm";
                    }
                }
                break;
            case COMPLETED:
                if (npcId == engineer_recon)
                    htmltext = "engineer_recon_q10273_03.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int GetMemoState = st.getInt("good_day_to_fly");

        if (GetMemoState == 2) {
            if (npcId == vulture_rider_1lv || npcId == vulture_rider_2lv || npcId == vulture_rider_3lv) {
                if (st.ownItemCount(q_necklace_of_riders) == 4) {
                    st.giveItems(q_necklace_of_riders, 1);
                    st.setCond(2);
                    st.setMemoState("good_day_to_fly", String.valueOf(3), true);
                    st.soundEffect(SOUND_MIDDLE);
                } else {
                    st.giveItems(q_necklace_of_riders, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        }
        return null;
    }
}