package org.mmocore.gameserver.scripts.quests;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _316_DestroyPlaguebringers extends Quest {
    //NPCs
    private static final int Ellenia = 30155;
    private static final int Varool_Foulclaw = 27020;
    //Quest Items
    private static final int Wererats_Fang = 1042;
    private static final int Varool_Foulclaws_Fang = 1043;

    public _316_DestroyPlaguebringers() {
        super(false);
        addStartNpc(Ellenia);
        //Mobs
        int sukar_Wererat = 20040;
        addKillId(sukar_Wererat);
        int sukar_Wererat_Leader = 20047;
        addKillId(sukar_Wererat_Leader);
        addKillId(Varool_Foulclaw);
        addQuestItem(Wererats_Fang);
        addQuestItem(Varool_Foulclaws_Fang);
        addLevelCheck(18, 24);
        addRaceCheck(PlayerRace.elf);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int _state = st.getState();
        if (event.equalsIgnoreCase("elliasin_q0316_04.htm") && _state == CREATED && st.getPlayer().getPlayerTemplateComponent().getPlayerRace() == PlayerRace.elf && st.getPlayer().getLevel() >= 18) {
            st.setState(STARTED);
            st.setCond(1);
            st.soundEffect(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("elliasin_q0316_08.htm") && _state == STARTED) {
            st.soundEffect(SOUND_FINISH);
            st.exitQuest(true);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        if (npc.getNpcId() != Ellenia) {
            return htmltext;
        }
        int _state = st.getState();

        if (_state == CREATED) {
            switch (isAvailableFor(st.getPlayer())) {
                case RACE:
                    htmltext = "elliasin_q0316_00.htm";
                    st.exitQuest(true);
                    break;
                case LEVEL:
                    htmltext = "elliasin_q0316_02.htm";
                    st.exitQuest(true);
                    break;
                default:
                    htmltext = "elliasin_q0316_03.htm";
                    st.setCond(0);
                    break;
            }
        } else if (_state == STARTED) {
            long Reward = st.ownItemCount(Wererats_Fang) * 60 + st.ownItemCount(Varool_Foulclaws_Fang) * 10000L;
            if (Reward > 0) {
                htmltext = "elliasin_q0316_07.htm";
                st.takeItems(Wererats_Fang, -1);
                st.takeItems(Varool_Foulclaws_Fang, -1);
                st.giveItems(ADENA_ID, Reward);
                st.soundEffect(SOUND_MIDDLE);
            } else {
                htmltext = "elliasin_q0316_05.htm";
            }
        }

        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState qs) {
        if (qs.getState() != STARTED) {
            return null;
        }

        int varool_Foulclaws_Fang_Chance = 30;//Chances
        int wererats_Fang_Chance = 50;
        if (npc.getNpcId() == Varool_Foulclaw && qs.ownItemCount(Varool_Foulclaws_Fang) == 0 && Rnd.chance(varool_Foulclaws_Fang_Chance)) {
            qs.giveItems(Varool_Foulclaws_Fang, 1);
            qs.soundEffect(SOUND_ITEMGET);
        } else if (Rnd.chance(wererats_Fang_Chance)) {
            qs.giveItems(Wererats_Fang, 1);
            qs.soundEffect(SOUND_ITEMGET);
        }

        return null;
    }


}