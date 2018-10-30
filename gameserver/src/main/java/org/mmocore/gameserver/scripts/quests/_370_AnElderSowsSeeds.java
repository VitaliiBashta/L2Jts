package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.0
 * @date 23/03/2015
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _370_AnElderSowsSeeds extends Quest {
    // npc
    private static final int sage_kasian = 30612;
    // mobs
    private static final int ant_recruit = 20082;
    private static final int ant_patrol = 20084;
    private static final int ant_guard = 20086;
    private static final int noble_ant = 20089;
    private static final int noble_ant_leader = 20090;
    // etcitem
    private static final int page_of_spellbook = 5916;
    private static final int kranvels_spellbook_chpt_fire = 5917;
    private static final int kranvels_spellbook_chpt_water = 5918;
    private static final int kranvels_spellbook_chpt_wind = 5919;
    private static final int kranvels_spellbook_chpt_earth = 5920;

    public _370_AnElderSowsSeeds() {
        super(false);
        addStartNpc(sage_kasian);
        addKillId(ant_recruit, ant_patrol, ant_guard, noble_ant, noble_ant_leader);
        addQuestItem(page_of_spellbook);
        addLevelCheck(28, 42);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == sage_kasian) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("sage_seeds_the_wasteland", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "sage_kasian_q0370_05.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=370&reply=1"))
                htmltext = "sage_kasian_q0370_03.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=370&reply=2"))
                htmltext = "sage_kasian_q0370_04.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=370&reply=3")) {
                if (st.ownItemCount(kranvels_spellbook_chpt_water) == 0 || st.ownItemCount(kranvels_spellbook_chpt_earth) == 0 || st.ownItemCount(kranvels_spellbook_chpt_wind) == 0 || st.ownItemCount(kranvels_spellbook_chpt_fire) == 0)
                    htmltext = "sage_kasian_q0370_07.htm";
                else if (st.ownItemCount(kranvels_spellbook_chpt_water) >= 1 && st.ownItemCount(kranvels_spellbook_chpt_earth) >= 1 && st.ownItemCount(kranvels_spellbook_chpt_wind) >= 1 && st.ownItemCount(kranvels_spellbook_chpt_fire) >= 1) {
                    long i0 = st.ownItemCount(kranvels_spellbook_chpt_water);
                    if (i0 > st.ownItemCount(kranvels_spellbook_chpt_earth)) {
                        i0 = st.ownItemCount(kranvels_spellbook_chpt_earth);
                    }
                    if (i0 > st.ownItemCount(kranvels_spellbook_chpt_wind)) {
                        i0 = st.ownItemCount(kranvels_spellbook_chpt_wind);
                    }
                    if (i0 > st.ownItemCount(kranvels_spellbook_chpt_fire)) {
                        i0 = st.ownItemCount(kranvels_spellbook_chpt_fire);
                    }
                    st.giveItems(ADENA_ID, i0 * 3600);
                    st.takeItems(kranvels_spellbook_chpt_water, i0);
                    st.takeItems(kranvels_spellbook_chpt_earth, i0);
                    st.takeItems(kranvels_spellbook_chpt_wind, i0);
                    st.takeItems(kranvels_spellbook_chpt_fire, i0);
                    htmltext = "sage_kasian_q0370_08.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=370&reply=4"))
                htmltext = "sage_kasian_q0370_09.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=370&reply=5")) {
                if (st.ownItemCount(kranvels_spellbook_chpt_water) == 0 || st.ownItemCount(kranvels_spellbook_chpt_earth) == 0 || st.ownItemCount(kranvels_spellbook_chpt_wind) == 0 || st.ownItemCount(kranvels_spellbook_chpt_fire) == 0) {
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(true);
                    htmltext = "sage_kasian_q0370_10.htm";
                } else if (st.ownItemCount(kranvels_spellbook_chpt_water) >= 1 && st.ownItemCount(kranvels_spellbook_chpt_earth) >= 1 && st.ownItemCount(kranvels_spellbook_chpt_wind) >= 1 && st.ownItemCount(kranvels_spellbook_chpt_fire) >= 1) {
                    long i0 = st.ownItemCount(kranvels_spellbook_chpt_water);
                    if (i0 > st.ownItemCount(kranvels_spellbook_chpt_earth)) {
                        i0 = st.ownItemCount(kranvels_spellbook_chpt_earth);
                    }
                    if (i0 > st.ownItemCount(kranvels_spellbook_chpt_wind)) {
                        i0 = st.ownItemCount(kranvels_spellbook_chpt_wind);
                    }
                    if (i0 > st.ownItemCount(kranvels_spellbook_chpt_fire)) {
                        i0 = st.ownItemCount(kranvels_spellbook_chpt_fire);
                    }
                    st.giveItems(ADENA_ID, i0 * 3600);
                    st.takeItems(kranvels_spellbook_chpt_water, i0);
                    st.takeItems(kranvels_spellbook_chpt_earth, i0);
                    st.takeItems(kranvels_spellbook_chpt_wind, i0);
                    st.takeItems(kranvels_spellbook_chpt_fire, i0);
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(true);
                    htmltext = "sage_kasian_q0370_11.htm";
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("sage_seeds_the_wasteland");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == sage_kasian) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "sage_kasian_q0370_01.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "sage_kasian_q0370_02.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == sage_kasian) {
                    if (GetMemoState == 1)
                        htmltext = "sage_kasian_q0370_06.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("sage_seeds_the_wasteland");
        int npcId = npc.getNpcId();
        if (GetMemoState == 1) {
            if (npcId == ant_recruit || npcId == ant_guard) {
                if (Rnd.get(100) < 9) {
                    st.giveItems(page_of_spellbook, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == ant_patrol) {
                if (Rnd.get(1000) < 101) {
                    st.giveItems(page_of_spellbook, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == noble_ant) {
                if (Rnd.get(100) < 10) {
                    st.giveItems(page_of_spellbook, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == noble_ant_leader) {
                if (Rnd.get(100) < 22) {
                    st.giveItems(page_of_spellbook, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        }
        return null;
    }
}