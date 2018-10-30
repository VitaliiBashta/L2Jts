package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _624_TheFinestIngredientsPart1 extends Quest {
    //NPC
    private static int JEREMY = 31521;

    //MOBS
    private static int HOT_SPRINGS_ATROX = 21321;
    private static int HOT_SPRINGS_NEPENTHES = 21319;
    private static int HOT_SPRINGS_ATROXSPAWN = 21317;
    private static int HOT_SPRINGS_BANDERSNATCHLING = 21314;

    //QUEST ITEMS
    private static int SECRET_SPICE = 7204;
    private static int TRUNK_OF_NEPENTHES = 7202;
    private static int FOOT_OF_BANDERSNATCHLING = 7203;
    private static int CRYOLITE = 7080;
    private static int SAUCE = 7205;


    public _624_TheFinestIngredientsPart1() {
        super(true);

        addStartNpc(JEREMY);

        addKillId(HOT_SPRINGS_ATROX);
        addKillId(HOT_SPRINGS_NEPENTHES);
        addKillId(HOT_SPRINGS_ATROXSPAWN);
        addKillId(HOT_SPRINGS_BANDERSNATCHLING);

        addQuestItem(TRUNK_OF_NEPENTHES);
        addQuestItem(FOOT_OF_BANDERSNATCHLING);
        addQuestItem(SECRET_SPICE);
        addLevelCheck(73, 80);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equalsIgnoreCase("jeremy_q0624_0104.htm")) {
            st.setState(STARTED);
            st.setCond(1);
            st.soundEffect(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("jeremy_q0624_0201.htm")) {
            if (st.ownItemCount(TRUNK_OF_NEPENTHES) == 50 && st.ownItemCount(FOOT_OF_BANDERSNATCHLING) == 50 && st.ownItemCount(SECRET_SPICE) == 50) {
                st.takeItems(TRUNK_OF_NEPENTHES, -1);
                st.takeItems(FOOT_OF_BANDERSNATCHLING, -1);
                st.takeItems(SECRET_SPICE, -1);
                st.soundEffect(SOUND_FINISH);
                st.giveItems(SAUCE, 1);
                st.giveItems(CRYOLITE, 1);
                htmltext = "jeremy_q0624_0201.htm";
                st.exitQuest(true);
            } else {
                htmltext = "jeremy_q0624_0202.htm";
                st.setCond(1);
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        if (cond == 0) {
            switch (isAvailableFor(st.getPlayer())) {
                case LEVEL:
                    htmltext = "jeremy_q0624_0103.htm";
                    st.exitQuest(true);
                    break;
                default:
                    htmltext = "jeremy_q0624_0101.htm";
                    break;
            }
        } else if (cond != 3) {
            htmltext = "jeremy_q0624_0106.htm";
        } else {
            htmltext = "jeremy_q0624_0105.htm";
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        if (st.getState() != STARTED) {
            return null;
        }
        int npcId = npc.getNpcId();
        if (st.getCond() == 1) {
            if (npcId == HOT_SPRINGS_NEPENTHES && st.ownItemCount(TRUNK_OF_NEPENTHES) < 50) {
                st.rollAndGive(TRUNK_OF_NEPENTHES, 1, 1, 50, 100);
            } else if (npcId == HOT_SPRINGS_BANDERSNATCHLING && st.ownItemCount(FOOT_OF_BANDERSNATCHLING) < 50) {
                st.rollAndGive(FOOT_OF_BANDERSNATCHLING, 1, 1, 50, 100);
            } else if ((npcId == HOT_SPRINGS_ATROX || npcId == HOT_SPRINGS_ATROXSPAWN) && st.ownItemCount(SECRET_SPICE) < 50) {
                st.rollAndGive(SECRET_SPICE, 1, 1, 50, 100);
            }
            onKillCheck(st);
        }
        return null;
    }

    private void onKillCheck(QuestState st) {
        if (st.ownItemCount(TRUNK_OF_NEPENTHES) == 50 && st.ownItemCount(FOOT_OF_BANDERSNATCHLING) == 50 && st.ownItemCount(SECRET_SPICE) == 50) {
            st.soundEffect(SOUND_MIDDLE);
            st.setCond(3);
        } else {
            st.soundEffect(SOUND_ITEMGET);
        }
    }
}