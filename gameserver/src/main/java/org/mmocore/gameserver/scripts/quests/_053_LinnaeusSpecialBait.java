package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _053_LinnaeusSpecialBait extends Quest {
    final int Linnaeu = 31577;
    final int CrimsonDrake = 20670;
    final int HeartOfCrimsonDrake = 7624;
    final int FlameFishingLure = 7613;
    final Integer FishSkill = 1315;


    public _053_LinnaeusSpecialBait() {
        super(false);

        addStartNpc(Linnaeu);

        addTalkId(Linnaeu);

        addKillId(CrimsonDrake);

        addQuestItem(HeartOfCrimsonDrake);
        addLevelCheck(60, 62);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equals("fisher_linneaus_q0053_0104.htm")) {
            st.setState(STARTED);
            st.setCond(1);
            st.soundEffect(SOUND_ACCEPT);
        } else if (event.equals("fisher_linneaus_q0053_0201.htm")) {
            if (st.ownItemCount(HeartOfCrimsonDrake) < 100) {
                htmltext = "fisher_linneaus_q0053_0202.htm";
            } else {
                st.removeMemo("cond");
                st.takeItems(HeartOfCrimsonDrake, -1);
                st.giveItems(FlameFishingLure, 4);
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
        if (npcId == Linnaeu) {
            if (id == CREATED) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = "fisher_linneaus_q0053_0103.htm";
                        st.exitQuest(true);
                        break;
                    default:
                        if (st.getPlayer().getSkillLevel(FishSkill) < 21) {
                            htmltext = "fisher_linneaus_q0053_0102.htm";
                            st.exitQuest(true);
                        } else
                            htmltext = "fisher_linneaus_q0053_0101.htm";
                        break;
                }
            } else if (cond == 1 || cond == 2) {
                if (st.ownItemCount(HeartOfCrimsonDrake) < 100) {
                    htmltext = "fisher_linneaus_q0053_0106.htm";
                    st.setCond(1);
                } else {
                    htmltext = "fisher_linneaus_q0053_0105.htm";
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == CrimsonDrake && st.getCond() == 1) {
            if (st.ownItemCount(HeartOfCrimsonDrake) < 100 && Rnd.chance(30)) {
                st.giveItems(HeartOfCrimsonDrake, 1);
                if (st.ownItemCount(HeartOfCrimsonDrake) == 100) {
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