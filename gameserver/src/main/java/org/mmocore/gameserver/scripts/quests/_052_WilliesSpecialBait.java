package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _052_WilliesSpecialBait extends Quest {
    private final static int Willie = 31574;
    private final static int[] TarlkBasilisks = {
            20573,
            20574
    };
    private final static int EyeOfTarlkBasilisk = 7623;
    private final static int EarthFishingLure = 7612;
    private final static Integer FishSkill = 1315;


    public _052_WilliesSpecialBait() {
        super(false);

        addStartNpc(Willie);

        addKillId(TarlkBasilisks);

        addQuestItem(EyeOfTarlkBasilisk);
        addLevelCheck(48, 50);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equals("fisher_willeri_q0052_0104.htm")) {
            st.setState(STARTED);
            st.setCond(1);
            st.soundEffect(SOUND_ACCEPT);
        } else if (event.equals("fisher_willeri_q0052_0201.htm")) {
            if (st.ownItemCount(EyeOfTarlkBasilisk) < 100) {
                htmltext = "fisher_willeri_q0052_0202.htm";
            } else {
                st.removeMemo("cond");
                st.takeItems(EyeOfTarlkBasilisk, -1);
                st.giveItems(EarthFishingLure, 4);
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(false);
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        int id = st.getState();
        if (npcId == Willie) {
            if (id == CREATED) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = "fisher_willeri_q0052_0103.htm";
                        st.exitQuest(true);
                        break;
                    default:
                        if (st.getPlayer().getSkillLevel(FishSkill) < 16) {
                            htmltext = "fisher_willeri_q0052_0102.htm";
                            st.exitQuest(true);
                        } else
                            htmltext = "fisher_willeri_q0052_0101.htm";
                        break;
                }
            } else if (cond == 1 || cond == 2) {
                if (st.ownItemCount(EyeOfTarlkBasilisk) < 100) {
                    htmltext = "fisher_willeri_q0052_0106.htm";
                    st.setCond(1);
                } else {
                    htmltext = "fisher_willeri_q0052_0105.htm";
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == TarlkBasilisks[0] || npcId == TarlkBasilisks[1] && st.getCond() == 1) {
            if (st.ownItemCount(EyeOfTarlkBasilisk) < 100 && Rnd.chance(30)) {
                st.giveItems(EyeOfTarlkBasilisk, 1);
                if (st.ownItemCount(EyeOfTarlkBasilisk) == 100) {
                    st.soundEffect(SOUND_MIDDLE);
                    st.setCond(2);
                } else {
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        }
        return null;
    }
}