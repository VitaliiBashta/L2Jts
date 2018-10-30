package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

/**
 * Рейты применены путем увеличения шанса/количества квестовго дропа
 */
public class _331_ArrowForVengeance extends Quest {


    private static final int HARPY_FEATHER = 1452;
    private static final int MEDUSA_VENOM = 1453;
    private static final int WYRMS_TOOTH = 1454;

    public _331_ArrowForVengeance() {
        super(false);
        addStartNpc(30125);

        addKillId(new int[]{
                20145,
                20158,
                20176
        });

        addQuestItem(new int[]{
                HARPY_FEATHER,
                MEDUSA_VENOM,
                WYRMS_TOOTH
        });
        addLevelCheck(32, 39);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equalsIgnoreCase("beltkem_q0331_03.htm")) {
            st.setCond(1);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("beltkem_q0331_06.htm")) {
            st.exitQuest(true);
            st.soundEffect(SOUND_FINISH);
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
                    htmltext = "beltkem_q0331_01.htm";
                    st.exitQuest(true);
                    break;
                default:
                    htmltext = "beltkem_q0331_02.htm";
                    return htmltext;
            }
        } else if (cond == 1) {
            if (st.ownItemCount(HARPY_FEATHER) + st.ownItemCount(MEDUSA_VENOM) + st.ownItemCount(WYRMS_TOOTH) > 0) {
                st.giveItems(ADENA_ID, 80 * st.ownItemCount(HARPY_FEATHER) + 90 * st.ownItemCount(MEDUSA_VENOM) + 100 * st.ownItemCount(WYRMS_TOOTH), false);
                st.takeItems(HARPY_FEATHER, -1);
                st.takeItems(MEDUSA_VENOM, -1);
                st.takeItems(WYRMS_TOOTH, -1);
                htmltext = "beltkem_q0331_05.htm";
            } else {
                htmltext = "beltkem_q0331_04.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() > 0) {
            switch (npc.getNpcId()) {
                case 20145:
                    st.rollAndGive(HARPY_FEATHER, 1, 33);
                    break;
                case 20158:
                    st.rollAndGive(MEDUSA_VENOM, 1, 33);
                    break;
                case 20176:
                    st.rollAndGive(WYRMS_TOOTH, 1, 33);
                    break;
            }
        }
        return null;
    }
}