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
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _157_RecoverSmuggledGoods extends Quest {
    // npcs
    private static final int wilph = 30005;

    // mobs
    private static final int giant_toad = 20121;

    // questitem
    private static final int adamantite_ore = 1024;

    // items
    private static final int buckler = 20;

    public _157_RecoverSmuggledGoods() {
        super(false);
        addStartNpc(wilph);
        addTalkId(wilph);
        addKillId(giant_toad);
        addQuestItem(adamantite_ore);
        addLevelCheck(5, 9);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equals("quest_accept")) {
            st.setCond(1);
            st.setMemoState("recover_smuggled", String.valueOf(1), true);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            htmltext = "wilph_q0157_05.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("recover_smuggled");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == wilph) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "wilph_q0157_02.htm";
                            break;
                        default:
                            htmltext = "wilph_q0157_03.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == wilph && GetMemoState == 1)
                    if (st.ownItemCount(adamantite_ore) < 20)
                        htmltext = "wilph_q0157_06.htm";
                    else if (st.ownItemCount(adamantite_ore) >= 20) {
                        st.takeItems(adamantite_ore, -1);
                        st.giveItems(buckler, 1);
                        st.soundEffect(SOUND_FINISH);
                        st.removeMemo("recover_smuggled");
                        htmltext = "wilph_q0157_07.htm";
                        st.exitQuest(false);
                    }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int GetMemoState = st.getInt("recover_smuggled");

        if (npcId == giant_toad && GetMemoState == 1 && st.ownItemCount(adamantite_ore) < 20 && Rnd.get(10) < 4) {
            st.giveItems(adamantite_ore, 1);
            if (st.ownItemCount(adamantite_ore) >= 20) {
                st.setCond(2);
                st.soundEffect(SOUND_MIDDLE);
            } else
                st.soundEffect(SOUND_ITEMGET);
        }
        return null;
    }
}