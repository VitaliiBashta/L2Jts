package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _10503_CapeEmbroideredSoulThree extends Quest {
    // NPC's
    private static final int OLF_ADAMS = 32612;
    // Mob's
    private static final int FRINTEZZA = 29047;
    // Quest Item's
    private static final int SOUL_FRINTEZZA = 21724;
    // Item's
    private static final int CLOAK_FRINTEZZA = 21721;


    public _10503_CapeEmbroideredSoulThree() {
        super(PARTY_CC);
        addStartNpc(OLF_ADAMS);
        addTalkId(OLF_ADAMS);
        addKillId(FRINTEZZA);
        addQuestItem(SOUL_FRINTEZZA);
        addLevelCheck(80);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equalsIgnoreCase("olf_adams_q10503_02.htm")) {
            st.setCond(1);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        if (cond == 0) {
            switch (isAvailableFor(st.getPlayer())) {
                case LEVEL:
                    htmltext = "olf_adams_q10503_00.htm";
                    st.exitQuest(true);
                    break;
                default:
                    htmltext = "olf_adams_q10503_01.htm";
                    break;
            }
        } else if (cond == 1) {
            htmltext = "olf_adams_q10503_03.htm";
        } else if (cond == 2) {
            if (st.ownItemCount(SOUL_FRINTEZZA) < 20) {
                st.setCond(1);
                htmltext = "olf_adams_q10503_03.htm";
            } else {
                st.takeItems(SOUL_FRINTEZZA, -1);
                st.giveItems(CLOAK_FRINTEZZA, 1);
                st.soundEffect(SOUND_FINISH);
                htmltext = "olf_adams_q10503_04.htm";
                st.exitQuest(false);
            }
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (cond == 1 && npcId == FRINTEZZA) {
            if (st.ownItemCount(SOUL_FRINTEZZA) < 20) {
                st.giveItems(SOUL_FRINTEZZA, Rnd.get(1, 3), false);
            }
            if (st.ownItemCount(SOUL_FRINTEZZA) >= 20) {
                st.setCond(2);
                st.soundEffect(SOUND_MIDDLE);
            }
        }
        return null;
    }
}