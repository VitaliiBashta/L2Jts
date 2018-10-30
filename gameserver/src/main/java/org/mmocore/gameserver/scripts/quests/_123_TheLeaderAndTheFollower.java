package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Language;

public class _123_TheLeaderAndTheFollower extends Quest {
    int NEWYEAR = 31961;
    int BRUIN_LIZARDMEN = 27321;
    int BRUIN_BLOOD = 8549;
    int PICOT_ARANEID = 27322;
    int PICOT_LEG = 8550;
    int D_CRY = 1458;
    int D_CRY_COUNT_HEAVY = 721;
    int D_CRY_COUNT_LIGHT_MAGIC = 604;

    int CLAN_OATH_HELM = 7850;

    int CLAN_OATH_ARMOR = 7851;
    int CLAN_OATH_GAUNTLETS = 7852;
    int CLAN_OATH_SABATON = 7853;

    int CLAN_OATH_BRIGANDINE = 7854;
    int CLAN_OATH_LEATHER_GLOVES = 7855;
    int CLAN_OATH_BOOTS = 7856;

    int CLAN_OATH_AKETON = 7857;
    int CLAN_OATH_PADDED_GLOVES = 7858;
    int CLAN_OATH_SANDALS = 7859;


    public _123_TheLeaderAndTheFollower() {
        super(false);

        addStartNpc(NEWYEAR);

        addKillId(BRUIN_LIZARDMEN);
        addKillId(PICOT_ARANEID);

        addQuestItem(BRUIN_BLOOD, PICOT_LEG);
        addLevelCheck(19, 40);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equals("31961-03.htm")) {
            st.setCond(1);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
        } else if (event.equals("31961-05.htm")) {
            st.setMemoState("choose", "1");
            st.setCond(3);
        } else if (event.equals("31961-06.htm")) {
            st.setMemoState("choose", "2");
            st.setCond(4);
        } else if (event.equals("31961-07.htm")) {
            st.setMemoState("choose", "3");
            st.setCond(5);
        } else if (event.equals("31961-08.htm")) {
            int choose = st.getInt("choose");
            int D_CRY_COUNT = D_CRY_COUNT_LIGHT_MAGIC;
            if (choose == 1) {
                D_CRY_COUNT = D_CRY_COUNT_HEAVY;
            }
            if (st.ownItemCount(D_CRY) >= D_CRY_COUNT) {
                st.setCond(7);
                st.takeItems(D_CRY, D_CRY_COUNT);
            } else {
                htmltext = "<html><body>771 D Cry!</body></html>";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        Player player = st.getPlayer();
        if (cond == 0) {
            switch (isAvailableFor(st.getPlayer())) {
                case LEVEL:
                    htmltext = player.getLanguage() == Language.ENGLISH ? "<html><body>Your level is too low</body></html>" : "<html><body>Ваш уровень слишком мал</body></html>";
                    return htmltext;
                default:
                    if (player.getClanId() == 0) {
                        htmltext = player.getLanguage() == Language.ENGLISH ? "<html><body>You are not in clan</body></html>" : "<html><body>Вы не состоите в клане</body></html>";
                        return htmltext;
                    } else if (player.getSponsor() == 0) {
                        htmltext = player.getLanguage() == Language.ENGLISH ? "<html><body>You have no sponsor</body></html>" : "<html><body>У вас нет наставника</body></html>";
                        return htmltext;
                    } else
                        htmltext = "31961-00.htm";
                    break;
            }
        } else if (cond == 1) {
            htmltext = "<html><body>Bring me 10 Bruin Lizardmen blood.</body></html>";
        } else if (cond == 2) {
            st.takeItems(BRUIN_BLOOD, 10);
            htmltext = "31961-04.htm";
        } else if (cond == 3) {
            htmltext = "31961-05.htm";
        } else if (cond == 4) {
            htmltext = "31961-06.htm";
        } else if (cond == 5) {
            htmltext = "31961-07.htm";
        } else if (cond == 7) {
            htmltext = "<html><body>Bring me 8 Picot Legs.</body></html>";
        } else if (cond == 8) {
            st.takeItems(PICOT_LEG, 8);
            int choose = st.getInt("choose");
            st.giveItems(CLAN_OATH_HELM, 1);
            if (choose == 1) {
                st.giveItems(CLAN_OATH_ARMOR, 1);
                st.giveItems(CLAN_OATH_GAUNTLETS, 1);
                st.giveItems(CLAN_OATH_SABATON, 1);
            } else if (choose == 2) {
                st.giveItems(CLAN_OATH_BRIGANDINE, 1);
                st.giveItems(CLAN_OATH_LEATHER_GLOVES, 1);
                st.giveItems(CLAN_OATH_BOOTS, 1);
            } else if (choose == 3) {
                st.giveItems(CLAN_OATH_AKETON, 1);
                st.giveItems(CLAN_OATH_PADDED_GLOVES, 1);
                st.giveItems(CLAN_OATH_SANDALS, 1);
            }
            st.setCond(0);
            st.soundEffect(SOUND_FINISH);
            htmltext = "<html><body>OK!</body></html>";
            st.exitQuest(false);
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == BRUIN_LIZARDMEN && st.ownItemCount(BRUIN_BLOOD) < 10 && cond == 1 && Rnd.chance(50)) {
            st.giveItems(BRUIN_BLOOD, 1);
            if (st.ownItemCount(BRUIN_BLOOD) == 10) {
                st.soundEffect(SOUND_MIDDLE);
                st.setCond(2);
            }
        } else if (npcId == PICOT_ARANEID && st.ownItemCount(PICOT_LEG) < 8 && cond == 7 && Rnd.chance(50)) {
            st.giveItems(PICOT_LEG, 1);
            if (st.ownItemCount(PICOT_LEG) == 8) {
                st.soundEffect(SOUND_MIDDLE);
                st.setCond(8);
            }
        }
        return null;
    }
}