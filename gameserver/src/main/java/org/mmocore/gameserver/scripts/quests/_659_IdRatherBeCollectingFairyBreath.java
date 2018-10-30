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
 * @date 23/12/2014
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _659_IdRatherBeCollectingFairyBreath extends Quest {
    // npc
    private final static int high_summoner_galatea = 30634;
    // mobs
    private final static int whispering_wind = 20078;
    private final static int sobing_wind = 21023;
    private final static int babbleing_wind = 21024;
    private final static int giggleing_wind = 21025;
    private final static int singing_wind = 21026;
    // questitem
    private final static int q_wind_slyph_breath = 8286;

    public _659_IdRatherBeCollectingFairyBreath() {
        super(true);
        addStartNpc(high_summoner_galatea);
        addKillId(whispering_wind, sobing_wind, babbleing_wind, giggleing_wind, singing_wind);
        addLevelCheck(26);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetHTMLCookie = st.getInt("gather_sylph_huh_cookie");
        int npcId = npc.getNpcId();
        if (npcId == high_summoner_galatea) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("gather_sylph_huh", String.valueOf(1 * 10 + 1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "high_summoner_galatea_q0659_0103.htm";
            } else if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 2 - 1)
                htmltext = "high_summoner_galatea_q0659_0201.htm";
            else if (event.equalsIgnoreCase("reply_3") && GetHTMLCookie == 2 - 1) {
                if (st.ownItemCount(q_wind_slyph_breath) == 0)
                    htmltext = "high_summoner_galatea_q0659_0202.htm";
                st.takeItems(q_wind_slyph_breath, -1);
                if (st.ownItemCount(q_wind_slyph_breath) >= 10)
                    st.giveItems(ADENA_ID, 5365 + 50 * st.ownItemCount(q_wind_slyph_breath));
                else
                    st.giveItems(ADENA_ID, 50 * st.ownItemCount(q_wind_slyph_breath));
                htmltext = "high_summoner_galatea_q0659_0203.htm";
            } else if (event.equalsIgnoreCase("reply_4") && GetHTMLCookie == 2 - 1) {
                st.removeMemo("gather_sylph_huh");
                st.removeMemo("gather_sylph_huh_cookie");
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                htmltext = "high_summoner_galatea_q0659_0204.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("gather_sylph_huh");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == high_summoner_galatea) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "high_summoner_galatea_q0659_0102.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "high_summoner_galatea_q0659_0101.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == high_summoner_galatea) {
                    if (GetMemoState == 1 * 10 + 1) {
                        if (st.ownItemCount(q_wind_slyph_breath) == 0)
                            htmltext = "high_summoner_galatea_q0659_0106.htm";
                        else {
                            st.setMemoState("gather_sylph_huh_cookie", String.valueOf(1), true);
                            htmltext = "high_summoner_galatea_q0659_0105.htm";
                        }
                    }
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("gather_sylph_huh");
        int npcId = npc.getNpcId();
        if (npcId == whispering_wind) {
            if (GetMemoState == 1 * 10 + 1) {
                int i4 = Rnd.get(100);
                if (i4 < 98) {
                    st.giveItems(q_wind_slyph_breath, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == sobing_wind) {
            if (GetMemoState == 1 * 10 + 1) {
                int i4 = Rnd.get(100);
                if (i4 < 82) {
                    st.giveItems(q_wind_slyph_breath, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == babbleing_wind) {
            if (GetMemoState == 1 * 10 + 1) {
                int i4 = Rnd.get(100);
                if (i4 < 86) {
                    st.giveItems(q_wind_slyph_breath, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == giggleing_wind) {
            if (GetMemoState == 1 * 10 + 1) {
                int i4 = Rnd.get(100);
                if (i4 < 90) {
                    st.giveItems(q_wind_slyph_breath, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == singing_wind) {
            if (GetMemoState == 1 * 10 + 1) {
                int i4 = Rnd.get(100);
                if (i4 < 96) {
                    st.giveItems(q_wind_slyph_breath, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        }
        return null;
    }
}