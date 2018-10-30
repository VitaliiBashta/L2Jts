package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.database.dao.impl.CharacterQuestDAO;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.world.GameObjectsStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class _501_ProofOfClanAlliance extends Quest {
    // Quest Npcs
    private static final int SIR_KRISTOF_RODEMAI = 30756;
    private static final int STATUE_OF_OFFERING = 30757;
    private static final int WITCH_ATHREA = 30758;
    private static final int WITCH_KALIS = 30759;

    // Quest Items
    private static final int HERB_OF_HARIT = 3832;
    private static final int HERB_OF_VANOR = 3833;
    private static final int HERB_OF_OEL_MAHUM = 3834;
    private static final int BLOOD_OF_EVA = 3835;
    private static final int SYMBOL_OF_LOYALTY = 3837;
    private static final int PROOF_OF_ALLIANCE = 3874;
    private static final int VOUCHER_OF_FAITH = 3873;
    private static final int ANTIDOTE_RECIPE = 3872;
    private static final int POTION_OF_RECOVERY = 3889;

    // Quest mobs, drop, rates and prices
    private static final List<Integer> CHESTS = Arrays.asList(
            27173,
            27174,
            27175,
            27176,
            27177
    );
    private static final int[][] MOBS = {
            {
                    20685,
                    HERB_OF_VANOR
            },
            {
                    20644,
                    HERB_OF_HARIT
            },
            {
                    20576,
                    HERB_OF_OEL_MAHUM
            }
    };

    private static final int RATE = 35;
    // stackable items paid to retry chest game: (default 10k adena)
    private static final int RETRY_PRICE = 10000;


    public _501_ProofOfClanAlliance() {
        super(PARTY_NONE);

        addStartNpc(SIR_KRISTOF_RODEMAI);
        addStartNpc(STATUE_OF_OFFERING);
        addStartNpc(WITCH_ATHREA);

        addTalkId(WITCH_KALIS);

        addQuestItem(SYMBOL_OF_LOYALTY);
        addQuestItem(ANTIDOTE_RECIPE);

        for (int[] i : MOBS) {
            addKillId(i[0]);
            addQuestItem(i[1]);
        }

        addKillId(CHESTS);
        addLevelCheck(0);
    }

    public QuestState getLeader(QuestState st) {
        Clan clan = st.getPlayer().getClan();
        QuestState leader = null;
        if (clan != null && clan.getLeader() != null && clan.getLeader().getPlayer() != null) {
            leader = clan.getLeader().getPlayer().getQuestState(getId());
        }
        return leader;
    }

    public void removeQuestFromMembers(QuestState st, boolean leader) {
        CharacterQuestDAO.getInstance().Q501_removeQuestFromOfflineMembers(st);
        removeQuestFromOnlineMembers(st, leader);
    }

    public void removeQuestFromOnlineMembers(QuestState st, boolean leader) {
        if (st.getPlayer() == null || st.getPlayer().getClan() == null) {
            st.exitQuest(true);
            return;
        }

        QuestState l;
        Player pleader = null;

        if (leader) {
            l = getLeader(st);
            if (l != null) {
                pleader = l.getPlayer();
            }
        }

        if (pleader != null) {
            if (pleader.isImmobilized()) {
                pleader.stopImmobilized();
            }
            pleader.getEffectList().stopEffect(4082);
        }
        for (Player pl : st.getPlayer().getClan().getOnlineMembers(st.getPlayer().getClan().getLeaderId())) {
            if (pl != null && pl.getQuestState(getId()) != null) {
                pl.getQuestState(getId()).exitQuest(true);
            }
        }
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (st.getPlayer() == null || st.getPlayer().getClan() == null) {
            st.exitQuest(true);
            return "noquest";
        }

        QuestState leader = getLeader(st);
        if (leader == null) {
            removeQuestFromMembers(st, true);
            return "Quest Failed";
        }

        String htmltext = event;

        /* ##### Leaders area ###### */
        if (st.getPlayer().isClanLeader())
        // SIR_KRISTOF_RODEMAI
        {
            if (event.equalsIgnoreCase("30756-03.htm")) {
                st.setCond(1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
            }

            // WITCH_KALIS
            else if (event.equalsIgnoreCase("30759-03.htm")) {
                st.setCond(2);
                st.setMemoState("dead_list", " ");
            } else if (event.equalsIgnoreCase("30759-07.htm")) {
                st.takeItems(SYMBOL_OF_LOYALTY, -1);
                st.giveItems(ANTIDOTE_RECIPE, 1);
                st.addNotifyOfDeath(st.getPlayer(), false);
                st.setCond(3);
                st.setMemoState("chest_count", "0");
                st.setMemoState("chest_game", "0");
                st.setMemoState("chest_try", "0");
                st.startQuestTimer("poison_timer", 3600000);
                st.getPlayer().altUseSkill(SkillTable.getInstance().getSkillEntry(4082, 1), st.getPlayer());
                htmltext = "30759-07.htm";
            }
        }

        // Timers
        if (event.equalsIgnoreCase("poison_timer")) {
            removeQuestFromMembers(st, true);
            htmltext = "30759-09.htm";
        } else if (event.equalsIgnoreCase("chest_timer")) {
            htmltext = "";
            if (leader.getInt("chest_game") < 2) {
                stop_chest_game(st);
            }
        }

        /* ##### Members area ###### */

        // STATUE_OF_OFFERING
        else if (event.equalsIgnoreCase("30757-04.htm")) {
            List<String> deadlist = new ArrayList<String>();
            deadlist.addAll(Arrays.asList(leader.get("dead_list").split(" ")));
            deadlist.add(st.getPlayer().getName());
            String deadstr = "";
            for (String s : deadlist) {
                deadstr += s + ' ';
            }
            leader.setMemoState("dead_list", deadstr);
            st.addNotifyOfDeath(leader.getPlayer(), false);
            if (Rnd.chance(50)) {
                st.getPlayer().reduceCurrentHp(st.getPlayer().getCurrentHp() * 8, st.getPlayer(), null, true, true, false, false, false, false, false);
            }
            st.giveItems(SYMBOL_OF_LOYALTY, 1);
            st.soundEffect(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("30757-05.htm")) {
            st.exitQuest(true);
        }

        // WITCH_ATHREA
        else if (event.equalsIgnoreCase("30758-03.htm")) {
            start_chest_game(st);
        } else if (event.equalsIgnoreCase("30758-07.htm")) {
            if (st.ownItemCount(ADENA_ID) < RETRY_PRICE) {
                htmltext = "30758-06.htm";
            } else {
                st.takeItems(ADENA_ID, RETRY_PRICE);
            }
        }

        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();

        if (st.getPlayer() == null || st.getPlayer().getClan() == null) {
            st.exitQuest(true);
            return htmltext;
        }

        QuestState leader = getLeader(st);
        if (leader == null) {
            removeQuestFromMembers(st, true);
            return "Quest Failed";
        }

        int npcId = npc.getNpcId();
        if (npcId == SIR_KRISTOF_RODEMAI) {
            if (!st.getPlayer().isClanLeader()) {
                st.exitQuest(true);
                return "30756-10.htm";
            } else if (st.getPlayer().getClan().getLevel() <= 2) {
                st.exitQuest(true);
                return "30756-08.htm";
            } else if (st.getPlayer().getClan().getLevel() >= 4) {
                st.exitQuest(true);
                return "30756-09.htm";
            } else if (st.ownItemCount(VOUCHER_OF_FAITH) > 0) {
                st.soundEffect(SOUND_FANFARE2);
                st.takeItems(VOUCHER_OF_FAITH, -1);
                st.giveItems(PROOF_OF_ALLIANCE, 1);
                st.addExpAndSp(0, 120000);
                htmltext = "30756-07.htm";
                st.exitQuest(true);
            } else if (cond == 1 || cond == 2) {
                return "30756-06.htm";
            } else if (st.ownItemCount(PROOF_OF_ALLIANCE) == 0) {
                st.setCond(0);
                return "30756-01.htm";
            } else {
                st.exitQuest(true);
                return htmltext;
            }
        } else if (npcId == WITCH_KALIS) {
            if (st.getPlayer().isClanLeader()) {
                if (cond == 1) {
                    return "30759-01.htm";
                } else if (cond == 2) {
                    htmltext = "30759-05.htm";
                    if (st.ownItemCount(SYMBOL_OF_LOYALTY) == 3) {
                        int deads = 0;
                        try {
                            deads = st.get("dead_list").split(" ").length;
                        } finally {
                            if (deads == 3) {
                                htmltext = "30759-06.htm";
                            }
                        }
                    }
                } else if (cond == 3) {
                    if (st.ownItemCount(HERB_OF_HARIT) > 0 && st.ownItemCount(HERB_OF_VANOR) > 0 && st.ownItemCount(HERB_OF_OEL_MAHUM) > 0 && st.ownItemCount(BLOOD_OF_EVA) > 0 && st.ownItemCount(ANTIDOTE_RECIPE) > 0) {
                        st.takeItems(ANTIDOTE_RECIPE, 1);
                        st.takeItems(HERB_OF_HARIT, 1);
                        st.takeItems(HERB_OF_VANOR, 1);
                        st.takeItems(HERB_OF_OEL_MAHUM, 1);
                        st.takeItems(BLOOD_OF_EVA, 1);
                        st.giveItems(POTION_OF_RECOVERY, 1);
                        st.giveItems(VOUCHER_OF_FAITH, 1);
                        st.cancelQuestTimer("poison_timer");
                        removeQuestFromMembers(st, false);
                        st.getPlayer().getEffectList().stopEffect(4082);
                        st.setCond(4);
                        st.soundEffect(SOUND_FINISH);
                        return "30759-08.htm";
                    } else if (st.ownItemCount(VOUCHER_OF_FAITH) == 0) {
                        return "30759-10.htm";
                    }
                }
            } else if (leader.getCond() == 3) {
                return "30759-11.htm";
            }
        } else if (npcId == STATUE_OF_OFFERING) {
            if (st.getPlayer().isClanLeader()) {
                return "30757-03.htm";
            } else if (st.getPlayer().getLevel() <= 39) {
                st.exitQuest(true);
                return "30757-02.htm";
            } else {
                String[] dlist;
                int deads;
                try {
                    dlist = leader.get("dead_list").split(" ");
                    deads = dlist.length;
                } catch (Exception e) {
                    removeQuestFromMembers(st, true);
                    return "Who are you?";
                }
                if (deads < 3) {
                    for (String str : dlist) {
                        if (st.getPlayer().getName().equalsIgnoreCase(str)) {
                            return "you cannot die again!";
                        }
                    }
                    return "30757-01.htm";
                }
            }
        } else if (npcId == WITCH_ATHREA) {
            if (st.getPlayer().isClanLeader()) {
                return "30757-03.htm";
            }

            // Проверяем, участвует ли в квесте
            String[] dlist;
            try {
                dlist = leader.get("dead_list").split(" ");
            } catch (Exception e) {
                st.exitQuest(true);
                return "Who are you?";
            }
            Boolean flag = false;
            if (dlist != null) {
                for (String str : dlist) {
                    if (st.getPlayer().getName().equalsIgnoreCase(str)) {
                        flag = true;
                    }
                }
            }
            if (!flag) {
                st.exitQuest(true);
                return "Who are you?";
            }

            int game_state = leader.getInt("chest_game");
            if (game_state == 0) {
                if (leader.getInt("chest_try") == 0) {
                    return "30758-01.htm";
                }
                return "30758-05.htm";
            } else if (game_state == 1) {
                return "30758-09.htm";
            } else if (game_state == 2) {
                st.soundEffect(SOUND_FINISH);
                st.giveItems(BLOOD_OF_EVA, 1);
                st.cancelQuestTimer("chest_timer");
                stop_chest_game(st);
                leader.setMemoState("chest_game", "3");
                return "30758-08.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        if (st.getPlayer() == null || st.getPlayer().getClan() == null) {
            st.exitQuest(true);
            return "noquest";
        }

        QuestState leader = getLeader(st);
        if (leader == null) {
            removeQuestFromMembers(st, true);
            return "Quest Failed";
        }

        // first part, general checking
        int npcId = npc.getNpcId();

        if (!leader.isRunningQuestTimer("poison_timer")) {
            stop_chest_game(st);
            return "Quest Failed";
        }

        // second part, herbs gathering
        for (int[] m : MOBS) {
            if (npcId == m[0] && st.getInt(String.valueOf(m[1])) == 0) {
                if (Rnd.chance(RATE)) {
                    st.giveItems(m[1], 1);
                    leader.setMemoState(String.valueOf(m[1]), "1");
                    st.soundEffect(SOUND_MIDDLE);
                    return null;
                }
            }
        }

        // third part, chest game
        for (int i : CHESTS) {
            if (npcId == i) {
                if (!leader.isRunningQuestTimer("chest_timer")) {
                    stop_chest_game(st);
                    return "Time is up!";
                }
                if (Rnd.chance(25)) {
                    Functions.npcSay(npc, NpcString.BINGO);
                    int count = leader.getInt("chest_count");
                    if (count < 4) {
                        count += 1;
                        leader.setMemoState("chest_count", String.valueOf(count));
                    }
                    if (count >= 4) {
                        stop_chest_game(st);
                        leader.setMemoState("chest_game", "2");
                        leader.cancelQuestTimer("chest_timer");
                        st.soundEffect(SOUND_MIDDLE);
                    } else {
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
                return null;
            }
        }
        return null;
    }

    public void start_chest_game(QuestState st) {
        if (st.getPlayer() == null || st.getPlayer().getClan() == null) {
            st.exitQuest(true);
            return;
        }

        QuestState leader = getLeader(st);
        if (leader == null) {
            removeQuestFromMembers(st, true);
            return;
        }

        leader.setMemoState("chest_game", "1");
        leader.setMemoState("chest_count", "0");
        int attempts = leader.getInt("chest_try");
        leader.setMemoState("chest_try", String.valueOf(attempts + 1));

        for (NpcInstance npc : GameObjectsStorage.getAllByNpcId(CHESTS, false)) {
            npc.deleteMe();
        }

        for (int n = 1; n <= 5; n++) {
            for (int i : CHESTS)
                leader.addSpawn(i, 102100, 103450, -3400, 0, 100, 60000);
        }
        leader.startQuestTimer("chest_timer", 60000);
    }

    public void stop_chest_game(QuestState st) {
        QuestState leader = getLeader(st);
        if (leader == null) {
            removeQuestFromMembers(st, true);
            return;
        }

        for (NpcInstance npc : GameObjectsStorage.getAllByNpcId(CHESTS, false)) {
            npc.deleteMe();
        }

        leader.setMemoState("chest_game", "0");
    }

    @Override
    public String onDeath(Creature npc, Creature pc, QuestState st) {
        if (st.getPlayer() == null || st.getPlayer().getClan() == null) {
            st.exitQuest(true);
            return null;
        }

        QuestState leader = getLeader(st);
        if (leader == null) {
            removeQuestFromMembers(st, true);
            return null;
        }

        if (st.getPlayer() == pc) {
            leader.cancelQuestTimer("poison_timer");
            leader.cancelQuestTimer("chest_timer");

            removeQuestFromMembers(st, true);
        }
        return null;
    }
}