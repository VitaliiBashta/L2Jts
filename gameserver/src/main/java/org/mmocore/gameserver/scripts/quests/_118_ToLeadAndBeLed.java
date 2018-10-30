package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _118_ToLeadAndBeLed extends Quest {
    private static final int PINTER = 30298;
    private static final int MAILLE_LIZARDMAN = 20919;
    private static final int BLOOD_OF_MAILLE_LIZARDMAN = 8062;
    private static final int KING_OF_THE_ARANEID = 20927;
    private static final int KING_OF_THE_ARANEID_LEG = 8063;


    public _118_ToLeadAndBeLed() {
        super(false);

        addStartNpc(PINTER);

        addKillId(MAILLE_LIZARDMAN);
        addKillId(KING_OF_THE_ARANEID);

        addQuestItem(BLOOD_OF_MAILLE_LIZARDMAN, KING_OF_THE_ARANEID_LEG);
        addLevelCheck(19, 40);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int d_CRY_COUNT_LIGHT_MAGIC = 604;
        int d_CRY_COUNT_HEAVY = 721;
        int d_CRY = 1458;
        switch (event) {
            case "30298-02.htm":
                st.setCond(1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                break;
            case "30298-05a.htm":
                st.setMemoState("choose", "1");
                st.setCond(3);
                break;
            case "30298-05b.htm":
                st.setMemoState("choose", "2");
                st.setCond(4);
                break;
            case "30298-05c.htm":
                st.setMemoState("choose", "3");
                st.setCond(5);
                break;
            case "30298-08.htm":
                int choose = st.getInt("choose");
                int need_dcry = choose == 1 ? d_CRY_COUNT_HEAVY : d_CRY_COUNT_LIGHT_MAGIC;
                if (st.ownItemCount(d_CRY) < need_dcry) {
                    return "30298-07.htm";
                }
                st.setCond(7);
                st.takeItems(d_CRY, need_dcry);
                st.soundEffect(SOUND_MIDDLE);
                break;
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        if (npc.getNpcId() != PINTER) {
            return "noquest";
        }
        int _state = st.getState();
        if (_state == CREATED) {
            switch (isAvailableFor(st.getPlayer())) {
                case LEVEL:
                    st.exitQuest(true);
                    return "30298-00.htm";
                default:
                    if (st.getPlayer().getClanId() == 0) {
                        st.exitQuest(true);
                        return "30298-00a.htm";
                    }
                    if (st.getPlayer().getSponsor() == 0) {
                        st.exitQuest(true);
                        return "30298-00b.htm";
                    }
                    st.setCond(0);
                    return "30298-01.htm";
            }
        }

        int cond = st.getCond();

        if (cond == 1 && _state == STARTED) {
            return "30298-02a.htm";
        }

        if (cond == 2 && _state == STARTED) {
            if (st.ownItemCount(BLOOD_OF_MAILLE_LIZARDMAN) < 10) {
                st.setCond(1);
                return "30298-02a.htm";
            }
            st.takeItems(BLOOD_OF_MAILLE_LIZARDMAN, -1);
            return "30298-04.htm";
        }

        if (cond == 3 && _state == STARTED) {
            return "30298-05a.htm";
        }

        if (cond == 4 && _state == STARTED) {
            return "30298-05b.htm";
        }

        if (cond == 5 && _state == STARTED) {
            return "30298-05c.htm";
        }

        if (cond == 7 && _state == STARTED) {
            return "30298-08a.htm";
        }

        if (cond == 8 && _state == STARTED) {
            if (st.ownItemCount(KING_OF_THE_ARANEID_LEG) < 8) {
                st.setCond(7);
                return "30298-08a.htm";
            }
            st.takeItems(KING_OF_THE_ARANEID_LEG, -1);
            int CLAN_OATH_HELM = 7850;
            st.giveItems(CLAN_OATH_HELM, 1);
            int choose = st.getInt("choose");
            if (choose == 1) {
                int CLAN_OATH_ARMOR = 7851;
                st.giveItems(CLAN_OATH_ARMOR, 1);
                int CLAN_OATH_GAUNTLETS = 7852;
                st.giveItems(CLAN_OATH_GAUNTLETS, 1);
                int CLAN_OATH_SABATON = 7853;
                st.giveItems(CLAN_OATH_SABATON, 1);
            } else if (choose == 2) {
                int CLAN_OATH_BRIGANDINE = 7854;
                st.giveItems(CLAN_OATH_BRIGANDINE, 1);
                int CLAN_OATH_LEATHER_GLOVES = 7855;
                st.giveItems(CLAN_OATH_LEATHER_GLOVES, 1);
                int CLAN_OATH_BOOTS = 7856;
                st.giveItems(CLAN_OATH_BOOTS, 1);
            } else {
                int CLAN_OATH_AKETON = 7857;
                st.giveItems(CLAN_OATH_AKETON, 1);
                int CLAN_OATH_PADDED_GLOVES = 7858;
                st.giveItems(CLAN_OATH_PADDED_GLOVES, 1);
                int CLAN_OATH_SANDALS = 7859;
                st.giveItems(CLAN_OATH_SANDALS, 1);
            }
            st.removeMemo("cond");
            st.soundEffect(SOUND_FINISH);
            st.exitQuest(false);
            return "30298-09.htm";
        }

        return "noquest";
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == MAILLE_LIZARDMAN && st.ownItemCount(BLOOD_OF_MAILLE_LIZARDMAN) < 10 && cond == 1 && Rnd.chance(50)) {
            st.giveItems(BLOOD_OF_MAILLE_LIZARDMAN, 1);
            if (st.ownItemCount(BLOOD_OF_MAILLE_LIZARDMAN) == 10) {
                st.soundEffect(SOUND_MIDDLE);
                st.setCond(2);
            } else {
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == KING_OF_THE_ARANEID && st.ownItemCount(KING_OF_THE_ARANEID_LEG) < 8 && cond == 7 && Rnd.chance(50)) {
            st.giveItems(KING_OF_THE_ARANEID_LEG, 1);
            if (st.ownItemCount(KING_OF_THE_ARANEID_LEG) == 8) {
                st.soundEffect(SOUND_MIDDLE);
                st.setCond(8);
            } else {
                st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }
}