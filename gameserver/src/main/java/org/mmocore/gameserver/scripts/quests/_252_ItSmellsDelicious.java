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
public class _252_ItSmellsDelicious extends Quest {
    // npc
    private final static int stan = 30200;
    // mobs
    private final static int xel_private_mage = 22786;
    private final static int xel_private_warrior = 22787;
    private final static int xel_private_sniper = 22788;
    private final static int ol_cooker = 18908;
    // questitem
    private final static int q_diary_of_xel = 15500;
    private final static int q_cooknote_piece = 15501;

    public _252_ItSmellsDelicious() {
        super(false);
        addStartNpc(stan);
        addKillId(xel_private_mage, xel_private_warrior, xel_private_sniper, ol_cooker);
        addQuestItem(q_diary_of_xel, q_cooknote_piece);
        addLevelCheck(82);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("good_smell");
        if (event.equalsIgnoreCase("quest_accept")) {
            st.setCond(1);
            st.setMemoState("good_smell", String.valueOf(1), true);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            htmltext = "stan_q0252_05.htm";
        } else if (event.equalsIgnoreCase("reply_1"))
            htmltext = "stan_q0252_04.htm";
        else if (event.equalsIgnoreCase("reply_2"))
            if (GetMemoState == 1 && st.ownItemCount(q_diary_of_xel) >= 10 && st.ownItemCount(q_cooknote_piece) >= 5) {
                st.giveItems(ADENA_ID, 147656);
                st.addExpAndSp(716238, 78324);
                st.takeItems(q_diary_of_xel, -1);
                st.takeItems(q_cooknote_piece, -1);
                st.removeMemo("good_smell");
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(false);
                htmltext = "stan_q0252_08.htm";
            }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("good_smell");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == stan) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "stan_q0252_02.htm";
                            break;
                        default:
                            htmltext = "stan_q0252_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == stan)
                    if (GetMemoState == 1 && (st.ownItemCount(q_diary_of_xel) < 10 || st.ownItemCount(q_cooknote_piece) < 5))
                        htmltext = "stan_q0252_06.htm";
                    else if (GetMemoState == 1 && st.ownItemCount(q_diary_of_xel) >= 10 && st.ownItemCount(q_cooknote_piece) >= 5)
                        htmltext = "stan_q0252_07.htm";
                break;
            case COMPLETED:
                if (npcId == stan)
                    htmltext = "stan_q0252_03.htm";
                break;
        }
        return htmltext;
    }

    public void checkItems(QuestState st) {
        if (st.ownItemCount(q_diary_of_xel) + st.ownItemCount(q_cooknote_piece) == 15) {
            st.setCond(2);
            st.soundEffect(SOUND_MIDDLE);
        }
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int GetMemoState = st.getInt("good_smell");
        if (npcId == xel_private_mage || npcId == xel_private_warrior || npcId == xel_private_sniper) {
            if (GetMemoState == 1 && st.ownItemCount(q_diary_of_xel) <= 9) {
                int i0 = Rnd.get(1000);
                if (i0 < 599) {
                    st.giveItems(q_diary_of_xel, 1);
                    st.soundEffect(SOUND_ITEMGET);
                    checkItems(st);
                }
            }
        } else if (npcId == ol_cooker) {
            if (GetMemoState == 1 && st.ownItemCount(q_cooknote_piece) <= 4) {
                int i0 = Rnd.get(1000);
                if (i0 < 360) {
                    st.giveItems(q_cooknote_piece, 1);
                    st.soundEffect(SOUND_ITEMGET);
                    checkItems(st);
                }
            }
        }
        return null;
    }
}