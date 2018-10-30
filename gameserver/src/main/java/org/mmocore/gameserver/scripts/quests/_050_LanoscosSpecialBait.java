package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _050_LanoscosSpecialBait extends Quest {
    // NPC
    final int Lanosco = 31570;
    final int SingingWind = 21026;
    // Items
    final int EssenceofWind = 7621;
    final int WindFishingLure = 7610;
    // Skill
    final Integer FishSkill = 1315;


    public _050_LanoscosSpecialBait() {
        super(false);

        addStartNpc(Lanosco);

        addTalkId(Lanosco);

        addKillId(SingingWind);

        addQuestItem(EssenceofWind);
        addLevelCheck(27, 29);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equals("fisher_lanosco_q0050_0104.htm")) {
            st.setState(STARTED);
            st.setCond(1);
            st.soundEffect(SOUND_ACCEPT);
        } else if (event.equals("fisher_lanosco_q0050_0201.htm")) {
            if (st.ownItemCount(EssenceofWind) < 100) {
                htmltext = "fisher_lanosco_q0050_0202.htm";
            } else {
                st.removeMemo("cond");
                st.takeItems(EssenceofWind, -1);
                st.giveItems(WindFishingLure, 4);
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
        if (npcId == Lanosco) {
            if (id == CREATED) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = "fisher_lanosco_q0050_0103.htm";
                        st.exitQuest(true);
                        break;
                    default:
                        if (st.getPlayer().getSkillLevel(FishSkill) < 8) {
                            htmltext = "fisher_lanosco_q0050_0102.htm";
                            st.exitQuest(true);
                        } else
                            htmltext = "fisher_lanosco_q0050_0101.htm";
                        break;
                }
            } else if (cond == 1 || cond == 2) {
                if (st.ownItemCount(EssenceofWind) < 100) {
                    htmltext = "fisher_lanosco_q0050_0106.htm";
                    st.setCond(1);
                } else {
                    htmltext = "fisher_lanosco_q0050_0105.htm";
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == SingingWind && st.getCond() == 1) {
            if (st.ownItemCount(EssenceofWind) < 100 && Rnd.chance(30)) {
                st.giveItems(EssenceofWind, 1);
                if (st.ownItemCount(EssenceofWind) == 100) {
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