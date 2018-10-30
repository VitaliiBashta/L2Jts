package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Player;

public class _710_PathToBecomingALordGiran extends Quest {
    private static final int Saul = 35184;
    private static final int Gesto = 30511;
    private static final int Felton = 30879;
    private static final int CargoBox = 32243;

    private static final int FreightChest = 13014;
    private static final int GestoBox = 13013;

    private static final int[] Mobs = {20832, 20833, 20835, 21602, 21603, 21604, 21605, 21606, 21607, 21608, 21609};

    private static final int GiranCastle = 3;

    public _710_PathToBecomingALordGiran() {
        super(false);
        addStartNpc(Saul);
        addTalkId(Gesto, Felton, CargoBox);
        addQuestItem(FreightChest, GestoBox);
        addKillId(Mobs);
        addLevelCheck(0);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        Castle castle = ResidenceHolder.getInstance().getResidence(GiranCastle);
        if (castle.getOwner() == null) {
            return "Castle has no lord";
        }
        Player castleOwner = castle.getOwner().getLeader().getPlayer();
        if (event.equals("saul_q710_03.htm")) {
            st.setState(STARTED);
            st.setCond(1);
            st.soundEffect(SOUND_ACCEPT);
        } else if (event.equals("gesto_q710_03.htm")) {
            st.setCond(3);
        } else if (event.equals("felton_q710_02.htm")) {
            st.setCond(4);
        } else if (event.equals("saul_q710_07.htm")) {
            Functions.npcSay(npc, NpcString.S1_HAS_BECOME_THE_LORD_OF_THE_TOWN_OF_GIRAN, st.getPlayer().getName());
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
        Castle castle = ResidenceHolder.getInstance().getResidence(GiranCastle);
        if (castle.getOwner() == null) {
            return "Castle has no lord";
        }
        Player castleOwner = castle.getOwner().getLeader().getPlayer();
        if (npcId == Saul) {
            if (cond == 0) {
                if (castleOwner == st.getPlayer()) {
                    if (castle.getDominion().getLordObjectId() != st.getPlayer().getObjectId()) {
                        htmltext = "saul_q710_01.htm";
                    } else {
                        htmltext = "saul_q710_00.htm";
                        st.exitQuest(true);
                    }
                } else {
                    htmltext = "saul_q710_00a.htm";
                    st.exitQuest(true);
                }
            } else if (cond == 1) {
                st.setCond(2);
                htmltext = "saul_q710_04.htm";
            } else if (cond == 2) {
                htmltext = "saul_q710_05.htm";
            } else if (cond == 9) {
                htmltext = "saul_q710_06.htm";
            }
        } else if (npcId == Gesto) {
            if (cond == 2) {
                htmltext = "gesto_q710_01.htm";
            } else if (cond == 3 || cond == 4) {
                htmltext = "gesto_q710_04.htm";
            } else if (cond == 5) {
                st.takeAllItems(FreightChest);
                st.setCond(7);
                htmltext = "gesto_q710_05.htm";
            } else if (cond == 7) {
                htmltext = "gesto_q710_06.htm";
            } else if (cond == 8) {
                st.takeAllItems(GestoBox);
                st.setCond(9);
                htmltext = "gesto_q710_07.htm";
            } else if (cond == 9) {
                htmltext = "gesto_q710_07.htm";
            }

        } else if (npcId == Felton) {
            if (cond == 3) {
                htmltext = "felton_q710_01.htm";
            } else if (cond == 4) {
                htmltext = "felton_q710_03.htm";
            }
        } else if (npcId == CargoBox) {
            if (cond == 4) {
                st.setCond(5);
                st.giveItems(FreightChest, 1);
                htmltext = "box_q710_01.htm";
            } else if (cond == 5) {
                htmltext = "box_q710_02.htm";
            }
        }

        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 7) {
            if (st.ownItemCount(GestoBox) < 300) {
                st.giveItems(GestoBox, 1);
            }
            if (st.ownItemCount(GestoBox) >= 300) {
                st.setCond(8);
            }
        }
        return null;
    }


}