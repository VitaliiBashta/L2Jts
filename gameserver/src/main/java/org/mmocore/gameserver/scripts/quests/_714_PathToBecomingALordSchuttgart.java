package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Player;

public class _714_PathToBecomingALordSchuttgart extends Quest {
    private static final int August = 35555;
    private static final int Newyear = 31961;
    private static final int Yasheni = 31958;
    private static final int GolemShard = 17162;

    private static final int ShuttgartCastle = 9;

    public _714_PathToBecomingALordSchuttgart() {
        super(false);
        addStartNpc(August);
        addTalkId(Newyear, Yasheni);
        for (int i = 22801; i < 22812; i++) {
            addKillId(i);
        }
        addQuestItem(GolemShard);
        addLevelCheck(0);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        Castle castle = ResidenceHolder.getInstance().getResidence(ShuttgartCastle);
        if (castle.getOwner() == null) {
            return "Castle has no lord";
        }
        Player castleOwner = castle.getOwner().getLeader().getPlayer();

        if (event.equals("august_q714_03.htm")) {
            st.setState(STARTED);
            st.setCond(1);
            st.soundEffect(SOUND_ACCEPT);
        } else if (event.equals("august_q714_05.htm")) {
            st.setCond(2);
        } else if (event.equals("newyear_q714_03.htm")) {
            st.setCond(3);
        } else if (event.equals("yasheni_q714_02.htm")) {
            st.setCond(5);
        } else if (event.equals("august_q714_08.htm")) {
            Functions.npcSay(npc, NpcString.S1_HAS_BECOME_THE_LORD_OF_THE_TOWN_OF_SCHUTTGART, st.getPlayer().getName());
            castle.getDominion().changeOwner(castleOwner.getClan());
            st.soundEffect(SOUND_FINISH);
            st.exitQuest(true);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        //int id = st.getState();
        int cond = st.getCond();
        Castle castle = ResidenceHolder.getInstance().getResidence(ShuttgartCastle);
        if (castle.getOwner() == null) {
            return "Castle has no lord";
        }
        Player castleOwner = castle.getOwner().getLeader().getPlayer();

        if (npcId == August) {
            if (cond == 0) {
                if (castleOwner == st.getPlayer()) {
                    if (castle.getDominion().getLordObjectId() != st.getPlayer().getObjectId()) {
                        htmltext = "august_q714_01.htm";
                    } else {
                        htmltext = "august_q714_00.htm";
                        st.exitQuest(true);
                    }
                } else {
                    htmltext = "august_q714_00a.htm";
                    st.exitQuest(true);
                }
            } else if (cond == 1) {
                htmltext = "august_q714_04.htm";
            } else if (cond == 2) {
                htmltext = "august_q714_06.htm";
            } else if (cond == 7) {
                htmltext = "august_q714_07.htm";
            }

        } else if (npcId == Newyear) {
            if (cond == 2) {
                htmltext = "newyear_q714_01.htm";
            } else if (cond == 3) {
                QuestState q1 = st.getPlayer().getQuestState(114);
                QuestState q2 = st.getPlayer().getQuestState(120);
                QuestState q3 = st.getPlayer().getQuestState(121);
                if (q3 != null && q3.isCompleted()) {
                    if (q1 != null && q1.isCompleted()) {
                        if (q2 != null && q2.isCompleted()) {
                            st.setCond(4);
                            htmltext = "newyear_q714_04.htm";
                        } else {
                            htmltext = "newyear_q714_04a.htm";
                        }
                    } else {
                        htmltext = "newyear_q714_04b.htm";
                    }
                } else {
                    htmltext = "newyear_q714_04c.htm";
                }
            }
        } else if (npcId == Yasheni) {
            if (cond == 4) {
                htmltext = "yasheni_q714_01.htm";
            } else if (cond == 5) {
                htmltext = "yasheni_q714_03.htm";
            } else if (cond == 6) {
                st.takeAllItems(GolemShard);
                st.setCond(7);
                htmltext = "yasheni_q714_04.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 5) {
            if (st.ownItemCount(GolemShard) < 300) {
                st.giveItems(GolemShard, 1);
            }
            if (st.ownItemCount(GolemShard) >= 300) {
                st.setCond(6);
            }
        }
        return null;
    }


}