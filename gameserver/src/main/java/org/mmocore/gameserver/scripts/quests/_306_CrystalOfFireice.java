package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _306_CrystalOfFireice extends Quest {
    //NPCs
    private static final int Katerina = 30004;
    //Mobs
    private static final int Salamander = 20109;
    private static final int Undine = 20110;
    private static final int Salamander_Elder = 20112;
    private static final int Undine_Elder = 20113;
    private static final int Salamander_Noble = 20114;
    private static final int Undine_Noble = 20115;
    //Quest Items
    private static final int Flame_Shard = 1020;
    private static final int Ice_Shard = 1021;

    public _306_CrystalOfFireice() {
        super(false);
        addStartNpc(Katerina);
        addKillId(Salamander);
        addKillId(Undine);
        addKillId(Salamander_Elder);
        addKillId(Undine_Elder);
        addKillId(Salamander_Noble);
        addKillId(Undine_Noble);
        addQuestItem(Flame_Shard);
        addQuestItem(Ice_Shard);
        addLevelCheck(17, 23);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int _state = st.getState();
        if (event.equalsIgnoreCase("katrine_q0306_04.htm") && _state == CREATED) {
            st.setState(STARTED);
            st.setCond(1);
            st.soundEffect(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("katrine_q0306_08.htm") && _state == STARTED) {
            st.soundEffect(SOUND_FINISH);
            st.exitQuest(true);
        }

        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        if (npc.getNpcId() != Katerina) {
            return htmltext;
        }
        int _state = st.getState();

        if (_state == CREATED) {
            switch (isAvailableFor(st.getPlayer())) {
                case LEVEL:
                    htmltext = "katrine_q0306_02.htm";
                    st.exitQuest(true);
                    break;
                default:
                    htmltext = "katrine_q0306_03.htm";
                    st.setCond(0);
                    break;
            }
        } else if (_state == STARTED) {
            long Shrads_count = st.ownItemCount(Flame_Shard) + st.ownItemCount(Ice_Shard);
            long Reward = Shrads_count * 30 + (Shrads_count >= 10 ? 5000 : 0);
            if (Reward > 0) {
                htmltext = "katrine_q0306_07.htm";
                st.takeItems(Flame_Shard, -1);
                st.takeItems(Ice_Shard, -1);
                st.giveItems(ADENA_ID, Reward);
            } else {
                htmltext = "katrine_q0306_05.htm";
            }
        }

        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState qs) {
        if (qs.getState() != STARTED) {
            return null;
        }
        int npcId = npc.getNpcId();

        //Chances
        int chance = 30;
        if ((npcId == Salamander || npcId == Undine) && !Rnd.chance(chance)) {
            return null;
        }
        int elder_Chance = 40;
        if ((npcId == Salamander_Elder || npcId == Undine_Elder) && !Rnd.chance(elder_Chance)) {
            return null;
        }
        int noble_Chance = 50;
        if ((npcId == Salamander_Noble || npcId == Undine_Noble) && !Rnd.chance(noble_Chance)) {
            return null;
        }
        qs.giveItems(npcId == Salamander || npcId == Salamander_Elder || npcId == Salamander_Noble ? Flame_Shard : Ice_Shard, 1);
        qs.soundEffect(SOUND_ITEMGET);
        return null;
    }


}