package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Player;

public class _713_PathToBecomingALordAden extends Quest {
    private static final int Logan = 35274;
    private static final int Orven = 30857;
    private static final int[] Orcs = {
            20669,
            20665
    };

    private static final int AdenCastle = 5;
    private int _mobs = 0;

    public _713_PathToBecomingALordAden() {
        super(false);
        addStartNpc(Logan);
        addTalkId(Orven);
        addKillId(Orcs);
        addLevelCheck(0);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        Castle castle = ResidenceHolder.getInstance().getResidence(AdenCastle);
        if (castle.getOwner() == null) {
            return "Castle has no lord";
        }
        Player castleOwner = castle.getOwner().getLeader().getPlayer();

        switch (event) {
            case "logan_q713_02.htm":
                st.setState(STARTED);
                st.setCond(1);
                st.soundEffect(SOUND_ACCEPT);
                break;
            case "orven_q713_03.htm":
                st.setCond(2);
                break;
            case "logan_q713_05.htm":
                Functions.npcSay(npc, NpcString.S1_HAS_BECOME_THE_LORD_OF_THE_TOWN_OF_ADEN, st.getPlayer().getName());
                castle.getDominion().changeOwner(castleOwner.getClan());
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                break;
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        //int id = st.getState();
        int cond = st.getCond();
        Castle castle = ResidenceHolder.getInstance().getResidence(AdenCastle);
        if (castle.getOwner() == null) {
            return "Castle has no lord";
        }
        Player castleOwner = castle.getOwner().getLeader().getPlayer();

        if (npcId == Logan) {
            if (cond == 0) {
                if (castleOwner == st.getPlayer()) {
                    if (castle.getDominion().getLordObjectId() != st.getPlayer().getObjectId()) {
                        htmltext = "logan_q713_01.htm";
                    } else {
                        htmltext = "logan_q713_00.htm";
                        st.exitQuest(true);
                    }
                } else {
                    htmltext = "logan_q713_00a.htm";
                    st.exitQuest(true);
                }
            } else if (cond == 1) {
                htmltext = "logan_q713_03.htm";
            } else if (cond == 7) {
                htmltext = "logan_q713_04.htm";
            }
        } else if (npcId == Orven) {
            if (cond == 1) {
                htmltext = "orven_q713_01.htm";
            } else if (cond == 2) {
                htmltext = "orven_q713_04.htm";
            } else if (cond == 4) {
                htmltext = "orven_q713_05.htm";
            } else if (cond == 5) {
                st.setCond(7);
                htmltext = "orven_q713_06.htm";
            } else if (cond == 7) {
                htmltext = "orven_q713_06.htm";
            }
        }

        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 4) {
            if (_mobs < 100) {
                _mobs++;
            } else {
                st.setCond(5);
            }
        }
        return null;
    }


}