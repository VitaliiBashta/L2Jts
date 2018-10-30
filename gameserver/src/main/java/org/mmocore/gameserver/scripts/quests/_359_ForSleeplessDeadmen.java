package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.object.Player;

public class _359_ForSleeplessDeadmen extends Quest {

    //Variables
    private static final int DROP_RATE = 10;

    private static final int REQUIRED = 60; //how many items will be paid for a reward

    //Quest items
    private static final int REMAINS = 5869;

    //Rewards
    private static final int PhoenixEarrPart = 6341;
    private static final int MajEarrPart = 6342;
    private static final int PhoenixNeclPart = 6343;
    private static final int MajNeclPart = 6344;
    private static final int PhoenixRingPart = 6345;
    private static final int MajRingPart = 6346;

    private static final int DarkCryShieldPart = 5494;
    private static final int NightmareShieldPart = 5495;

    //NPCs
    private static final int ORVEN = 30857;

    //Mobs
    private static final int DOOMSERVANT = 21006;
    private static final int DOOMGUARD = 21007;
    private static final int DOOMARCHER = 21008;
    private static final int DOOMTROOPER = 21009;


    public _359_ForSleeplessDeadmen() {
        super(false);
        addStartNpc(ORVEN);

        addKillId(DOOMSERVANT);
        addKillId(DOOMGUARD);
        addKillId(DOOMARCHER);
        addKillId(DOOMTROOPER);

        addQuestItem(REMAINS);
        addLevelCheck(60, 67);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equalsIgnoreCase("30857-06.htm")) {
            st.setState(STARTED);
            st.setCond(1);
            st.soundEffect(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("30857-07.htm")) {
            // 713 quest hook
            Castle castle = ResidenceHolder.getInstance().getResidence(5);
            if (castle.getOwner() != null) {
                Player castleOwner = castle.getOwner().getLeader().getPlayer();
                if (castleOwner != null && castleOwner != st.getPlayer() && castleOwner.getClan() == st.getPlayer().getClan() && castleOwner.getQuestState(713) != null && castleOwner.getQuestState(713).getCond() == 2) {
                    if (castleOwner.getQuestState(713).get("questsDone") != null) {
                        if (Integer.parseInt(castleOwner.getQuestState(713).get("questsDone")) < 5) {
                            castleOwner.getQuestState(713).setMemoState("questsDone", String.valueOf(Integer.parseInt(castleOwner.getQuestState(713).get("questsDone")) + 1), true);
                        } else {
                            castleOwner.getQuestState(713).setCond(4);
                        }
                    } else {
                        castleOwner.getQuestState(713).setMemoState("questsDone", "1", true);
                    }

                }
            }
            //---------------
            st.exitQuest(true);
            st.soundEffect(SOUND_FINISH);
        } else if (event.equalsIgnoreCase("30857-08.htm")) {
            st.setCond(1);
            //Vibor nagradi
            int chance = Rnd.get(100);
            int item;
            if (chance <= 16) {
                item = PhoenixNeclPart;
            } else if (chance <= 33) {
                item = PhoenixEarrPart;
            } else if (chance <= 50) {
                item = PhoenixRingPart;
            } else if (chance <= 58) {
                item = MajNeclPart;
            } else if (chance <= 67) {
                item = MajEarrPart;
            } else if (chance <= 76) {
                item = MajRingPart;
            } else if (chance <= 84) {
                item = DarkCryShieldPart;
            } else {
                item = NightmareShieldPart;
            }
            st.giveItems(item, 4, true);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int id = st.getState();
        int cond = st.getCond();
        if (id == CREATED) {
            switch (isAvailableFor(st.getPlayer())) {
                case LEVEL:
                    htmltext = "30857-01.htm";
                    st.exitQuest(true);
                    break;
                default:
                    htmltext = "30857-02.htm";
                    break;
            }
        } else if (id == STARTED) {
            if (cond == 3) {
                htmltext = "30857-03.htm";
            } else if (cond == 2 && st.ownItemCount(REMAINS) >= REQUIRED) {
                st.takeItems(REMAINS, REQUIRED);
                st.setCond(3);
                htmltext = "30857-04.htm";
            }
        } else {
            htmltext = "30857-05.htm";
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        long count = st.ownItemCount(REMAINS);
        if (count < REQUIRED && Rnd.chance(DROP_RATE)) {
            st.giveItems(REMAINS, 1);
            if (count + 1 >= REQUIRED) {
                st.soundEffect(SOUND_MIDDLE);
                st.setCond(2);
            } else {
                st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }
}