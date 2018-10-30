package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.data.xml.holder.MultiSellHolder;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

import java.util.HashMap;
import java.util.Map;

public class _382_KailsMagicCoin extends Quest {
    //MOBs and CHANCES
    private static final Map<Integer, int[]> MOBS = new HashMap<Integer, int[]>();
    //Quest items
    private static final int ROYAL_MEMBERSHIP = 5898;

    static {
        MOBS.put(21017, new int[]{5961}); // Fallen Orc
        MOBS.put(21019, new int[]{5962}); // Fallen Orc Archer
        MOBS.put(21020, new int[]{5963}); // Fallen Orc Shaman
        MOBS.put(21022, new int[]{
                5961,
                5962,
                5963
        }); // Fallen Orc Captain
        //MOBS.put(21258, new int[] { 5961, 5962, 5963 }); // Fallen Orc Shaman - WereTiger
        //MOBS.put(21259, new int[] { 5961, 5962, 5963 }); // Fallen Orc Shaman - WereTiger, transformed
    }


    public _382_KailsMagicCoin() {
        super(false);

        //NPCs
        int VERGARA = 30687;
        addStartNpc(VERGARA);

        for (int mobId : MOBS.keySet()) {
            addKillId(mobId);
        }
        addLevelCheck(55, 60);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equalsIgnoreCase("head_blacksmith_vergara_q0382_03.htm")) {
            if (st.getPlayer().getLevel() >= 55 && st.ownItemCount(ROYAL_MEMBERSHIP) > 0) {
                st.setCond(1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
            } else {
                htmltext = "head_blacksmith_vergara_q0382_01.htm";
                st.exitQuest(true);
            }
        } else if (event.equalsIgnoreCase("list")) {
            MultiSellHolder.getInstance().SeparateAndSend(382, st.getPlayer(), npc != null ? npc.getObjectId() : -1, 0);
            htmltext = null;
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
                    htmltext = "head_blacksmith_vergara_q0382_01.htm";
                    st.exitQuest(true);
                    break;
                default:
                    if (st.ownItemCount(ROYAL_MEMBERSHIP) == 0)
                        htmltext = "head_blacksmith_vergara_q0382_01.htm";
                    else
                        htmltext = "head_blacksmith_vergara_q0382_02.htm";
                    break;
            }

        } else {
            htmltext = "head_blacksmith_vergara_q0382_04.htm";
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        if (st.getState() != STARTED || st.ownItemCount(ROYAL_MEMBERSHIP) == 0) {
            return null;
        }

        int[] droplist = MOBS.get(npc.getNpcId());
        st.rollAndGive(droplist[Rnd.get(droplist.length)], 1, 10);
        return null;
    }
}