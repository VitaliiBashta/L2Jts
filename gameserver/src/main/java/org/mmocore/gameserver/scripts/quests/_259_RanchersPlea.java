package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _259_RanchersPlea extends Quest {


    private static final int GIANT_SPIDER_SKIN_ID = 1495;
    private static final int HEALING_POTION_ID = 1061;
    private static final int WOODEN_ARROW_ID = 17;
    private static final int SSNG_ID = 1835;
    private static final int SPSSNG_ID = 2905;

    public _259_RanchersPlea() {
        super(false);
        addStartNpc(30497);

        addKillId(new int[]{
                20103,
                20106,
                20108
        });

        addQuestItem(GIANT_SPIDER_SKIN_ID);
        addLevelCheck(15, 21);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        switch (event) {
            case "1":
                st.setMemoState("id", "0");
                st.setCond(1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "edmond_q0259_03.htm";
                break;
            case "30497_1":
                htmltext = "edmond_q0259_06.htm";
                st.setCond(0);
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                break;
            case "30497_2":
                htmltext = "edmond_q0259_07.htm";
                break;
            case "30405_1":
                htmltext = "marius_q0259_03.htm";
                break;
            case "30405_2":
                htmltext = "marius_q0259_04.htm";
                st.giveItems(HEALING_POTION_ID, 2, false);
                st.takeItems(GIANT_SPIDER_SKIN_ID, 10);
                break;
            case "30405_3":
                htmltext = "marius_q0259_05.htm";
                st.giveItems(WOODEN_ARROW_ID, 50, false);
                st.takeItems(GIANT_SPIDER_SKIN_ID, 10);
                break;
            case "30405_8":
                htmltext = "marius_q0259_05a.htm";
                st.giveItems(SSNG_ID, 60, false);
                st.takeItems(GIANT_SPIDER_SKIN_ID, 10);
                break;
            case "30405_8a":
                htmltext = "marius_q0259_05a.htm";
                break;
            case "30405_9":
                htmltext = "marius_q0259_05c.htm";
                st.giveItems(SPSSNG_ID, 30, false);
                st.takeItems(GIANT_SPIDER_SKIN_ID, 10);
                break;
            case "30405_9a":
                htmltext = "marius_q0259_05d.htm";
                break;
            case "30405_4":
                if (st.ownItemCount(GIANT_SPIDER_SKIN_ID) >= 10) {
                    htmltext = "marius_q0259_06.htm";
                } else if (st.ownItemCount(GIANT_SPIDER_SKIN_ID) < 10) {
                    htmltext = "marius_q0259_07.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        if (npcId == 30497 && st.getCond() == 0) {
            if (st.getCond() < 15) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = "edmond_q0259_01.htm";
                        st.exitQuest(true);
                        break;
                    default:
                        htmltext = "edmond_q0259_02.htm";
                        return htmltext;
                }
            } else {
                htmltext = "edmond_q0259_01.htm";
                st.exitQuest(true);
            }
        } else if (npcId == 30497 && st.getCond() == 1 && st.ownItemCount(GIANT_SPIDER_SKIN_ID) < 1) {
            htmltext = "edmond_q0259_04.htm";
        } else if (npcId == 30497 && st.getCond() == 1 && st.ownItemCount(GIANT_SPIDER_SKIN_ID) >= 1) {
            htmltext = "edmond_q0259_05.htm";
            st.giveItems(ADENA_ID, st.ownItemCount(GIANT_SPIDER_SKIN_ID) * 25, false);
            st.takeItems(GIANT_SPIDER_SKIN_ID, st.ownItemCount(GIANT_SPIDER_SKIN_ID));
        } else if (npcId == 30405 && st.getCond() == 1 && st.ownItemCount(GIANT_SPIDER_SKIN_ID) < 10) {
            htmltext = "marius_q0259_01.htm";
        } else if (npcId == 30405 && st.getCond() == 1 && st.ownItemCount(GIANT_SPIDER_SKIN_ID) >= 10) {
            htmltext = "marius_q0259_02.htm";
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() > 0) {
            st.rollAndGive(GIANT_SPIDER_SKIN_ID, 1, 100);
        }
        return null;
    }
}