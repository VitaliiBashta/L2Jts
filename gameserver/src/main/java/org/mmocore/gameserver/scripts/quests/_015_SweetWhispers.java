package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _015_SweetWhispers extends Quest {
    // npc
    private static final int trader_vladimir = 31302;
    private static final int dark_presbyter = 31517;
    private static final int dark_necromancer = 31518;

    public _015_SweetWhispers() {
        super(false);
        addStartNpc(trader_vladimir);
        addTalkId(dark_presbyter, dark_necromancer);
        addLevelCheck(60);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetHTMLCookie = st.getInt("sweet_whisper_cookie");
        int npcId = npc.getNpcId();
        if (npcId == trader_vladimir) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("sweet_whisper", String.valueOf(1 * 10 + 1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "trader_vladimir_q0015_0104.htm";
            }
        } else if (npcId == dark_presbyter) {
            if (event.equalsIgnoreCase("reply_3") && GetHTMLCookie == 3 - 1) {
                st.addExpAndSp(350531, 28204);
                st.removeMemo("sweet_whisper_cookie");
                st.removeMemo("sweet_whisper");
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(false);
                htmltext = "dark_presbyter_q0015_0301.htm";
            }
        } else if (npcId == dark_necromancer) {
            if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 2 - 1) {
                st.setCond(2);
                st.setMemoState("sweet_whisper", String.valueOf(2 * 10 + 1), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "dark_necromancer_q0015_0201.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("sweet_whisper");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED: {
                if (npcId == trader_vladimir) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "trader_vladimir_q0015_0103.htm";
                            break;
                        default:
                            htmltext = "trader_vladimir_q0015_0101.htm";
                            break;
                    }
                }
                break;
            }
            case STARTED: {
                if (npcId == trader_vladimir) {
                    if (GetMemoState == 1 * 10 + 1)
                        htmltext = "trader_vladimir_q0015_0105.htm";
                } else if (npcId == dark_presbyter) {
                    if (GetMemoState == 2 * 10 + 1) {
                        st.setMemoState("sweet_whisper_cookie", String.valueOf(2), true);
                        htmltext = "dark_presbyter_q0015_0201.htm";
                    }
                } else if (npcId == dark_necromancer) {
                    if (GetMemoState == 1 * 10 + 1) {
                        st.setMemoState("sweet_whisper_cookie", String.valueOf(1), true);
                        htmltext = "dark_necromancer_q0015_0101.htm";
                    } else if (GetMemoState == 2 * 10 + 1)
                        htmltext = "dark_necromancer_q0015_0202.htm";
                }
            }
        }
        return htmltext;
    }
}