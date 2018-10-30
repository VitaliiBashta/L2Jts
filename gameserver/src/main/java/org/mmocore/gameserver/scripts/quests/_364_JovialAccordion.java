package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.utils.Language;

public class _364_JovialAccordion extends Quest {
    //NPCs
    private static final int BARBADO = 30959;
    private static final int SWAN = 30957;
    private static final int SABRIN = 30060;
    private static final int BEER_CHEST = 30960;
    private static final int CLOTH_CHEST = 30961;
    //Items
    private static final int KEY_1 = 4323;
    private static final int KEY_2 = 4324;
    private static final int BEER = 4321;

    public _364_JovialAccordion() {
        super(false);
        addStartNpc(BARBADO);
        addTalkId(SWAN);
        addTalkId(SABRIN);
        addTalkId(BEER_CHEST);
        addTalkId(CLOTH_CHEST);
        addQuestItem(KEY_1);
        addQuestItem(KEY_2);
        addQuestItem(BEER);
        addLevelCheck(15);
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        if (st.getState() == CREATED) {
            if (npcId != BARBADO) {
                return htmltext;
            }
            st.setCond(0);
            st.setMemoState("ok", "0");
        }

        int cond = st.getCond();
        if (npcId == BARBADO) {
            if (cond == 0) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = st.getPlayer().getLanguage() == Language.ENGLISH ? "This quest available for 15 and high level." : "Данный квест доступен для персонажей 15 и выше уровня.";
                        break;
                    default:
                        htmltext = "30959-01.htm";
                        break;
                }
            } else if (cond == 3) {
                htmltext = "30959-03.htm";
                int ECHO = 4421;
                st.giveItems(ECHO, 1);
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
            } else if (cond > 0) {
                htmltext = "30959-02.htm";
            }
        } else if (npcId == SWAN) {
            if (cond == 1) {
                htmltext = "30957-01.htm";
            } else if (cond == 3) {
                htmltext = "30957-05.htm";
            } else if (cond == 2) {
                if (st.getInt("ok") == 1 && st.ownItemCount(KEY_1) == 0) {
                    st.setCond(3);
                    htmltext = "30957-04.htm";
                } else {
                    htmltext = "30957-03.htm";
                }
            }
        } else if (npcId == SABRIN && cond == 2 && st.ownItemCount(BEER) > 0) {
            st.setMemoState("ok", "1");
            st.takeItems(BEER, -1);
            htmltext = "30060-01.htm";
        } else if (npcId == BEER_CHEST && cond == 2) {
            htmltext = "30960-01.htm";
        } else if (npcId == CLOTH_CHEST && cond == 2) {
            htmltext = "30961-01.htm";
        }

        return htmltext;
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int _state = st.getState();
        int cond = st.getCond();
        if (event.equalsIgnoreCase("30959-02.htm") && _state == CREATED && cond == 0) {
            st.setCond(1);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("30957-02.htm") && _state == STARTED && cond == 1) {
            st.setCond(2);
            st.giveItems(KEY_1, 1);
            st.giveItems(KEY_2, 1);
        } else if (event.equalsIgnoreCase("30960-03.htm") && cond == 2 && st.ownItemCount(KEY_2) > 0) {
            st.takeItems(KEY_2, -1);
            st.giveItems(BEER, 1);
            htmltext = "30960-02.htm";
        } else if (event.equalsIgnoreCase("30961-03.htm") && cond == 2 && st.ownItemCount(KEY_1) > 0) {
            st.takeItems(KEY_1, -1);
            htmltext = "30961-02.htm";
        }
        return htmltext;
    }


}
