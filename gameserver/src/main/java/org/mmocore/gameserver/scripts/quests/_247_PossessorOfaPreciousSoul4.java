package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.model.entity.olympiad.Olympiad;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.model.quest.startcondition.impl.HasItemCondition;
import org.mmocore.gameserver.model.quest.startcondition.impl.QuestCompletedCondition;
import org.mmocore.gameserver.network.lineage.serverpackets.SkillList;
import org.mmocore.gameserver.network.lineage.serverpackets.SocialAction;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.0
 * @date 02/06/2016
 * @lastedit 02/06/2016
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _247_PossessorOfaPreciousSoul4 extends Quest {
    //npc
    private static final int caradine = 31740;
    private static final int lady_of_the_lake = 31745;
    //etcitem
    private static final int q_caradines_letter2 = 7679;
    private static final int nobless_tiara = 7694;

    public _247_PossessorOfaPreciousSoul4() {
        super(false);
        addStartNpc(caradine);
        addTalkId(lady_of_the_lake);
        addLevelCheck(1, 85);
//        addQuestCompletedCheck(246);
        addOrCond(new HasItemCondition(q_caradines_letter2), new QuestCompletedCondition(246));
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        Player player = st.getPlayer();
        int GetMemoState = st.getInt("noble_soul_noblesse_4");
        int npcId = npc.getNpcId();
        if (npcId == caradine) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("noble_soul_noblesse_4", String.valueOf(1), true);
                st.takeItems(q_caradines_letter2, 1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "caradine_q0247_03.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=247&reply=1")) {
                st.setCond(2);
                st.setMemoState("noble_soul_noblesse_4", String.valueOf(2), true);
                st.getPlayer().teleToLocation(143180, 43930, -3024);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "caradine_q0247_05.htm";
            }
        } else if (npcId == lady_of_the_lake) {
            if (event.equalsIgnoreCase("menu_select?ask=247&reply=1") && GetMemoState == 2)
                htmltext = "lady_of_the_lake_q0247_02.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=247&reply=2") && GetMemoState == 2)
                htmltext = "lady_of_the_lake_q0247_03.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=247&reply=3") && GetMemoState == 2)
                htmltext = "lady_of_the_lake_q0247_04.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=247&reply=4") && GetMemoState == 2) {
                if (st.getPlayer().getLevel() >= 75) {
                    st.giveItems(nobless_tiara, 1);
                    st.addExpAndSp(93836, 0);
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "lady_of_the_lake_q0247_05.htm";
                    player.sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                    npc.doCast(SkillTable.getInstance().getSkillEntry(4339, 1), st.getPlayer(), true);
                    Olympiad.addNoble(st.getPlayer());
                    st.getPlayer().setNoble(true);
                    st.getPlayer().updatePledgeClass();
                    st.getPlayer().updateNobleSkills();
                    st.getPlayer().sendPacket(new SkillList(st.getPlayer()));
                    st.getPlayer().broadcastUserInfo(true);
                } else {
                    htmltext = "lady_of_the_lake_q0247_06.htm";
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        if (!st.getPlayer().getPlayerClassComponent().isSubClassActive()) {
            return "quest_not_subclass001.htm";
        }
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("noble_soul_noblesse_4");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == caradine) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case QUEST:
                        case LEVEL:
                        case OR:
                            htmltext = "caradine_q0247_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "caradine_q0247_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == caradine) {
                    if (GetMemoState == 1)
                        htmltext = "caradine_q0247_04.htm";
                    else if (GetMemoState == 2)
                        htmltext = "caradine_q0247_06.htm";
                } else if (npcId == lady_of_the_lake) {
                    if (GetMemoState == 2)
                        htmltext = "lady_of_the_lake_q0247_01.htm";
                }
                break;
        }
        return htmltext;
    }
}