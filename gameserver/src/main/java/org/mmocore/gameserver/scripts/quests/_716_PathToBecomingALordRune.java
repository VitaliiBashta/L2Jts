package org.mmocore.gameserver.scripts.quests;


import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pchayka
 * <p/>
 * TODO: удалять квест у доверенного лица
 */
public class _716_PathToBecomingALordRune extends Quest {
    private static final int Frederick = 35509;
    private static final int Agripel = 31348;
    private static final int Innocentin = 31328;

    private static final int RuneCastle = 8;
    private static final List<Integer> Pagans = new ArrayList<Integer>();

    static {
        for (int i = 22138; i <= 22176; i++) {
            Pagans.add(i);
        }
        for (int i = 22188; i <= 22195; i++) {
            Pagans.add(i);
        }
    }

    public _716_PathToBecomingALordRune() {
        super(false);
        addStartNpc(Frederick);
        addTalkId(Agripel, Innocentin);
        addKillId(Pagans);

        addQuestCompletedCheck(021);
        addQuestCompletedCheck(025);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        Castle castle = ResidenceHolder.getInstance().getResidence(RuneCastle);
        if (castle.getOwner() == null) {
            return "Castle has no lord";
        }
        Player castleOwner = castle.getOwner().getLeader().getPlayer();
        String htmltext = event;
        switch (event) {
            case "frederick_q716_03.htm":
                st.setState(STARTED);
                st.setCond(1);
                st.soundEffect(SOUND_ACCEPT);
                break;
            case "agripel_q716_03.htm":
                st.setCond(3);
                break;
            case "frederick_q716_08.htm":
                castleOwner.getQuestState(this.getId()).setMemoState("confidant", String.valueOf(st.getPlayer().getObjectId()), true);
                castleOwner.getQuestState(this.getId()).setCond(5);
                st.setState(STARTED);
                break;
            case "innocentin_q716_03.htm":
                if (castleOwner != null && castleOwner != st.getPlayer() && castleOwner.getQuestState(this.getId()) != null && castleOwner.getQuestState(this.getId()).getCond() == 5) {
                    castleOwner.getQuestState(this.getId()).setCond(6);
                }
                break;
            case "agripel_q716_08.htm":
                st.setCond(8);
                break;
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        Castle castle = ResidenceHolder.getInstance().getResidence(RuneCastle);
        if (castle.getOwner() == null) {
            return "Castle has no lord";
        }
        Player castleOwner = castle.getOwner().getLeader().getPlayer();

        if (npcId == Frederick) {
            if (cond == 0) {
                if (castleOwner == st.getPlayer()) {
                    if (castle.getDominion().getLordObjectId() != st.getPlayer().getObjectId()) {
                        htmltext = "frederick_q716_01.htm";
                    } else {
                        htmltext = "frederick_q716_00.htm";
                        st.exitQuest(true);
                    }
                }
                // Лидер клана в игре, говорящий не лидер, у лидера взят квест и пройден до стадии назначения поверенного
                else if (castleOwner != null && castleOwner != st.getPlayer() && castleOwner.getQuestState(getId()) != null && castleOwner.getQuestState(getId()).getCond() == 4) {
                    if (castleOwner.isInRangeZ(npc, 200)) {
                        htmltext = "frederick_q716_07.htm";
                    } else {
                        htmltext = "frederick_q716_07a.htm";
                    }
                } else if (st.getState() == STARTED) {
                    htmltext = "frederick_q716_00b.htm";
                } else {
                    htmltext = "frederick_q716_00a.htm";
                    st.exitQuest(true);
                }
            } else if (cond == 1) {
                switch (isAvailableFor(st.getPlayer())) {
                    case QUEST:
                        htmltext = "frederick_q716_03.htm";
                        break;
                    default:
                        st.setCond(2);
                        htmltext = "frederick_q716_04.htm";
                        break;
                }
            } else if (cond == 2) {
                htmltext = "frederick_q716_04a.htm";
            } else if (cond == 3) {
                st.setCond(4);
                htmltext = "frederick_q716_05.htm";
            } else if (cond == 4) {
                htmltext = "frederick_q716_06.htm";
            } else if (cond == 5) {
                htmltext = "frederick_q716_09.htm";
            } else if (cond == 6) {
                st.setCond(7);
                htmltext = "frederick_q716_10.htm";
            } else if (cond == 7) {
                htmltext = "frederick_q716_11.htm";
            } else if (cond == 8) {
                Functions.npcSay(npc, NpcString.S1_HAS_BECOME_THE_LORD_OF_THE_TOWN_OF_RUNE, st.getPlayer().getName());
                castle.getDominion().changeOwner(castleOwner.getClan());
                htmltext = "frederick_q716_12.htm";
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
            }
        } else if (npcId == Agripel) {
            if (cond == 2) {
                htmltext = "agripel_q716_01.htm";
            } else if (cond == 7) {
                if (st.get("paganCount") != null && Integer.parseInt(st.get("paganCount")) >= 100) {
                    htmltext = "agripel_q716_07.htm";
                } else {
                    htmltext = "agripel_q716_04.htm";
                }
            } else if (cond == 8) {
                htmltext = "agripel_q716_09.htm";
            }
        } else if (npcId == Innocentin) {
            if (st.getState() == STARTED && st.getCond() == 0) {
                if (castleOwner != null && castleOwner != st.getPlayer() && castleOwner.getQuestState(this.getId()) != null && castleOwner.getQuestState(this.getId()).getCond() == 5) {
                    if (Integer.parseInt(castleOwner.getQuestState(this.getId()).get("confidant")) == st.getPlayer().getObjectId()) {
                        htmltext = "innocentin_q716_01.htm";
                    } else {
                        htmltext = "innocentin_q716_00.htm";
                    }
                } else {
                    htmltext = "innocentin_q716_00a.htm";
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        Castle castle = ResidenceHolder.getInstance().getResidence(RuneCastle);
        Player castleOwner = castle.getOwner().getLeader().getPlayer();
        if (st.getState() == STARTED && st.getCond() == 0) {
            if (castleOwner != null && castleOwner != st.getPlayer() && castleOwner.getQuestState(this.getId()) != null && castleOwner.getQuestState(this.getId()).getCond() == 7) {
                if (castleOwner.getQuestState(this.getId()).get("paganCount") != null) {
                    castleOwner.getQuestState(this.getId()).setMemoState("paganCount", String.valueOf(Integer.parseInt(castleOwner.getQuestState(this.getId()).get("paganCount")) + 1), true);
                } else {
                    castleOwner.getQuestState(this.getId()).setMemoState("paganCount", "1", true);
                }
            }
        }
        return null;
    }

    public void onLoad() {
    }

    public void onReload() {
    }

    public void onShutdown() {
    }
}