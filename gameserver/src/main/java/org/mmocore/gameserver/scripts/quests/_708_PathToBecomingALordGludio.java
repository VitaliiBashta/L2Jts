package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Player;

public class _708_PathToBecomingALordGludio extends Quest {
    private static final int Sayres = 35100;
    private static final int Pinter = 30298;
    private static final int Bathis = 30332;

    private static final int HeadlessKnightsArmor = 13848;

    private static final int[] Mobs = {20045, 20051, 20099};

    private static final int GludioCastle = 1;

    public _708_PathToBecomingALordGludio() {
        super(false);
        addStartNpc(Sayres);
        addTalkId(Pinter, Bathis);
        addQuestItem(HeadlessKnightsArmor);
        addKillId(Mobs);
        addLevelCheck(0);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        Castle castle = ResidenceHolder.getInstance().getResidence(GludioCastle);
        if (castle.getOwner() == null) {
            return "Castle has no lord";
        }
        Player castleOwner = castle.getOwner().getLeader().getPlayer();
        switch (event) {
            case "sayres_q708_03.htm":
                st.setState(STARTED);
                st.setCond(1);
                st.soundEffect(SOUND_ACCEPT);
                break;
            case "sayres_q708_05.htm":
                st.setCond(2);
                break;
            case "sayres_q708_08.htm":
                if (isLordAvailable(2, st)) {
                    castleOwner.getQuestState(getId()).setMemoState("confidant", String.valueOf(st.getPlayer().getObjectId()), true);
                    castleOwner.getQuestState(getId()).setCond(3);
                    st.setState(STARTED);
                } else {
                    htmltext = "sayres_q708_05a.htm";
                }
                break;
            case "pinter_q708_03.htm":
                if (isLordAvailable(3, st)) {
                    castleOwner.getQuestState(getId()).setCond(4);
                } else {
                    htmltext = "pinter_q708_03a.htm";
                }
                break;
            case "bathis_q708_02.htm":
                st.setCond(6);
                break;
            case "bathis_q708_05.htm":
                st.setCond(8);
                Functions.npcSay(npc, NpcString.LISTEN_YOU_VILLAGERS_OUR_LIEGE_WHO_WILL_SOON_BECAME_A_LORD_HAS_DEFEATED_THE_HEADLESS_KNIGHT);
                break;
            case "pinter_q708_05.htm":
                if (isLordAvailable(8, st)) {
                    st.takeItems(1867, 100);
                    st.takeItems(1865, 100);
                    st.takeItems(1869, 100);
                    st.takeItems(1879, 50);
                    castleOwner.getQuestState(getId()).setCond(9);
                } else {
                    htmltext = "pinter_q708_03a.htm";
                }
                break;
            case "sayres_q708_12.htm":
                Functions.npcSay(npc, NpcString.S1_HAS_BECOME_THE_LORD_OF_THE_TOWN_OF_GLUDIO, st.getPlayer().getName());
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
        Castle castle = ResidenceHolder.getInstance().getResidence(GludioCastle);
        if (castle.getOwner() == null) {
            return "Castle has no lord";
        }
        Player castleOwner = castle.getOwner().getLeader().getPlayer();
        if (npcId == Sayres) {
            if (cond == 0) {
                if (castleOwner == st.getPlayer()) {
                    if (castle.getDominion().getLordObjectId() != st.getPlayer().getObjectId()) {
                        htmltext = "sayres_q708_01.htm";
                    } else {
                        htmltext = "sayres_q708_00.htm";
                        st.exitQuest(true);
                    }
                } else if (isLordAvailable(2, st)) {
                    if (castleOwner.isInRangeZ(npc, 200)) {
                        htmltext = "sayres_q708_07.htm";
                    } else {
                        htmltext = "sayres_q708_05a.htm";
                    }
                } else if (st.getState() == STARTED) {
                    htmltext = "sayres_q708_08a.htm";
                } else {
                    htmltext = "sayres_q708_00a.htm";
                    st.exitQuest(true);
                }
            } else if (cond == 1) {
                htmltext = "sayres_q708_04.htm";
            } else if (cond == 2) {
                htmltext = "sayres_q708_06.htm";
            } else if (cond == 4) {
                st.setCond(5);
                htmltext = "sayres_q708_09.htm";
            } else if (cond == 5) {
                htmltext = "sayres_q708_10.htm";
            } else if (cond > 5 && cond < 9) {
                htmltext = "sayres_q708_08.htm";
            } else if (cond == 9) {
                htmltext = "sayres_q708_11.htm";
            }

        } else if (npcId == Pinter) {
            if (st.getState() == STARTED && cond == 0 && isLordAvailable(3, st)) {
                if (Integer.parseInt(castleOwner.getQuestState(getId()).get("confidant")) == st.getPlayer().getObjectId()) {
                    htmltext = "pinter_q708_01.htm";
                }
            } else if (st.getState() == STARTED && cond == 0 && isLordAvailable(8, st)) {
                if (st.ownItemCount(1867) >= 100 && st.ownItemCount(1865) >= 100 && st.ownItemCount(1869) >= 100 && st.ownItemCount(1879) >= 50) {
                    htmltext = "pinter_q708_04.htm";
                } else {
                    htmltext = "pinter_q708_04a.htm";
                }
            } else if (st.getState() == STARTED && cond == 0 && isLordAvailable(9, st)) {
                htmltext = "pinter_q708_06.htm";
            }

        } else if (npcId == Bathis) {
            if (cond == 5) {
                htmltext = "bathis_q708_01.htm";
            } else if (cond == 6) {
                htmltext = "bathis_q708_03.htm";
            } else if (cond == 7) {
                htmltext = "bathis_q708_04.htm";
            } else if (cond == 8) {
                htmltext = "sophia_q709_06.htm";
            }

        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 6) {
            if (Rnd.chance(10)) {
                st.giveItems(HeadlessKnightsArmor, 1);
                st.setCond(7);
            }
        }
        return null;
    }

    private boolean isLordAvailable(int cond, QuestState st) {
        Castle castle = ResidenceHolder.getInstance().getResidence(GludioCastle);
        Clan owner = castle.getOwner();
        Player castleOwner = castle.getOwner().getLeader().getPlayer();
        if (owner != null) {
            if (castleOwner != null && castleOwner != st.getPlayer() && owner == st.getPlayer().getClan() && castleOwner.getQuestState(getId()) != null && castleOwner.getQuestState(getId()).getCond() == cond) {
                return true;
            }
        }
        return false;
    }


}