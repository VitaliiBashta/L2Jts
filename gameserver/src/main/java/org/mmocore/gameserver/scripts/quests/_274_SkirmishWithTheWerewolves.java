package org.mmocore.gameserver.scripts.quests;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _274_SkirmishWithTheWerewolves extends Quest {
    private static final int MARAKU_WEREWOLF_HEAD = 1477;
    private static final int NECKLACE_OF_VALOR = 1507;
    private static final int NECKLACE_OF_COURAGE = 1506;
    private static final int MARAKU_WOLFMEN_TOTEM = 1501;

    public _274_SkirmishWithTheWerewolves() {
        super(false);
        addStartNpc(30569);

        addKillId(20363);
        addKillId(20364);

        addQuestItem(MARAKU_WEREWOLF_HEAD);
        addLevelCheck(9, 18);
        addRaceCheck(PlayerRace.orc);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equals("prefect_brukurse_q0274_03.htm")) {
            st.setCond(1);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
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
                    htmltext = "prefect_brukurse_q0274_01.htm";
                    st.exitQuest(true);
                    break;
                case RACE:
                    htmltext = "prefect_brukurse_q0274_00.htm";
                    st.exitQuest(true);
                    break;
                default:
                    if (st.ownItemCount(NECKLACE_OF_VALOR) > 0 || st.ownItemCount(NECKLACE_OF_COURAGE) > 0) {
                        htmltext = "prefect_brukurse_q0274_02.htm";
                        return htmltext;
                    } else
                        htmltext = "prefect_brukurse_q0274_07.htm";
                    break;
            }
        } else if (cond == 1) {
            htmltext = "prefect_brukurse_q0274_04.htm";
        } else if (cond == 2) {
            if (st.ownItemCount(MARAKU_WEREWOLF_HEAD) < 40) {
                htmltext = "prefect_brukurse_q0274_04.htm";
            } else {
                st.takeItems(MARAKU_WEREWOLF_HEAD, -1);
                st.giveItems(ADENA_ID, 3500, true);
                if (st.ownItemCount(MARAKU_WOLFMEN_TOTEM) >= 1) {
                    st.giveItems(ADENA_ID, st.ownItemCount(MARAKU_WOLFMEN_TOTEM) * 600, true);
                    st.takeItems(MARAKU_WOLFMEN_TOTEM, -1);
                }
                htmltext = "prefect_brukurse_q0274_05.htm";
                st.exitQuest(true);
                st.soundEffect(SOUND_FINISH);
            }
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 1 && st.ownItemCount(MARAKU_WEREWOLF_HEAD) < 40) {
            if (st.ownItemCount(MARAKU_WEREWOLF_HEAD) < 39) {
                st.soundEffect(SOUND_ITEMGET);
            } else {
                st.soundEffect(SOUND_MIDDLE);
                st.setCond(2);
            }
            st.giveItems(MARAKU_WEREWOLF_HEAD, 1);
        }
        if (Rnd.chance(5)) {
            st.giveItems(MARAKU_WOLFMEN_TOTEM, 1);
        }
        return null;
    }
}