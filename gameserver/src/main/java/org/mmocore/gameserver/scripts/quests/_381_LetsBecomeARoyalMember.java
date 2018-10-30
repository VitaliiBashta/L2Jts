package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _381_LetsBecomeARoyalMember extends Quest {
    //Quest items
    private static final int KAILS_COIN = 5899;
    private static final int COIN_ALBUM = 5900;
    private static final int CLOVER_COIN = 7569;
    //NPCs
    private static final int SORINT = 30232;
    //MOBs
    private static final int ANCIENT_GARGOYLE = 21018;
    private static final int VEGUS = 27316;


    public _381_LetsBecomeARoyalMember() {
        super(false);

        addStartNpc(SORINT);
        int SANDRA = 30090;
        addTalkId(SANDRA);

        addKillId(ANCIENT_GARGOYLE);
        addKillId(VEGUS);

        addQuestItem(KAILS_COIN);
        addQuestItem(COIN_ALBUM);
        addQuestItem(CLOVER_COIN);
        addLevelCheck(55, 65);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equalsIgnoreCase("warehouse_keeper_sorint_q0381_02.htm")) {
            st.setCond(1);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            htmltext = "warehouse_keeper_sorint_q0381_03.htm";
        } else if (event.equalsIgnoreCase("sandra_q0381_02.htm")) {
            if (st.getCond() == 1) {
                st.setMemoState("id", "1");
                st.soundEffect(SOUND_ACCEPT);
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";

        int cond = st.getCond();
        int npcId = npc.getNpcId();
        long album = st.ownItemCount(COIN_ALBUM);

        if (npcId == SORINT) {
            if (cond == 0) {
                int MEMBERSHIP_1 = 3813;
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = "warehouse_keeper_sorint_q0381_02.htm";
                        st.exitQuest(true);
                        break;
                    default:
                        if (st.ownItemCount(MEMBERSHIP_1) > 0)
                            htmltext = "warehouse_keeper_sorint_q0381_01.htm";
                        else
                            htmltext = "warehouse_keeper_sorint_q0381_02.htm";
                        break;
                }

            } else if (cond == 1) {
                long coin = st.ownItemCount(KAILS_COIN);
                if (coin > 0 && album > 0) {
                    st.takeItems(KAILS_COIN, -1);
                    st.takeItems(COIN_ALBUM, -1);
                    int ROYAL_MEMBERSHIP = 5898;
                    st.giveItems(ROYAL_MEMBERSHIP, 1);
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(true);
                    htmltext = "warehouse_keeper_sorint_q0381_06.htm";
                } else if (album == 0) {
                    htmltext = "warehouse_keeper_sorint_q0381_05.htm";
                } else if (coin == 0) {
                    htmltext = "warehouse_keeper_sorint_q0381_04.htm";
                }
            }
        } else {
            long clover = st.ownItemCount(CLOVER_COIN);
            if (album > 0) {
                htmltext = "sandra_q0381_05.htm";
            } else if (clover > 0) {
                st.takeItems(CLOVER_COIN, -1);
                st.giveItems(COIN_ALBUM, 1);
                st.soundEffect(SOUND_ITEMGET);
                htmltext = "sandra_q0381_04.htm";
            } else if (st.getInt("id") == 0) {
                htmltext = "sandra_q0381_01.htm";
            } else {
                htmltext = "sandra_q0381_03.htm";
            }
        }

        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        if (st.getState() != STARTED) {
            return null;
        }
        int npcId = npc.getNpcId();

        long album = st.ownItemCount(COIN_ALBUM);
        long coin = st.ownItemCount(KAILS_COIN);
        long clover = st.ownItemCount(CLOVER_COIN);

        if (npcId == ANCIENT_GARGOYLE && coin == 0) {
            //CHANCES (custom values, feel free to change them)
            int GARGOYLE_CHANCE = 5;
            if (Rnd.chance(GARGOYLE_CHANCE)) {
                st.giveItems(KAILS_COIN, 1);
                if (album > 0 || clover > 0) {
                    st.soundEffect(SOUND_MIDDLE);
                } else {
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == VEGUS && clover + album == 0 && st.getInt("id") != 0) {
            int VEGUS_CHANCE = 100;
            if (Rnd.chance(VEGUS_CHANCE)) {
                st.giveItems(CLOVER_COIN, 1);
                if (coin > 0) {
                    st.soundEffect(SOUND_MIDDLE);
                } else {
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        }
        return null;
    }
}