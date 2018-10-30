package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.data.htm.HtmCache;
import org.mmocore.gameserver.model.base.ClassId;
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
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _061_LawEnforcement extends Quest {
    // npc
    private static final int grandmaste_piane = 32222;
    private static final int kekrops = 32138;
    private static final int subelder_aientburg = 32469;

    public _061_LawEnforcement() {
        super(false);
        addStartNpc(grandmaste_piane);
        addTalkId(kekrops, subelder_aientburg);
        addLevelCheck(76);
        addClassIdCheck(ClassId.inspector);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("rule_of_judicator");
        int npcId = npc.getNpcId();
        if (npcId == grandmaste_piane) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("rule_of_judicator", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "grandmaste_piane_q0061_05.htm";
            } else if (event.equalsIgnoreCase("reply_1"))
                htmltext = "grandmaste_piane_q0061_04.htm";
        } else if (npcId == kekrops) {
            if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 1) {
                    st.setMemoState("rule_of_judicator", String.valueOf(2), true);
                    htmltext = "kekrops_q0061_03.htm";
                }
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 1) {
                    st.setMemoState("rule_of_judicator", String.valueOf(3), true);
                    htmltext = "kekrops_q0061_04.htm";
                }
            } else if (event.equalsIgnoreCase("reply_4")) {
                if (GetMemoState >= 2 && GetMemoState <= 3)
                    htmltext = "kekrops_q0061_06.htm";
            } else if (event.equalsIgnoreCase("reply_5")) {
                if (GetMemoState >= 2 && GetMemoState <= 3)
                    htmltext = "kekrops_q0061_07.htm";
            } else if (event.equalsIgnoreCase("reply_6")) {
                if (GetMemoState >= 2 && GetMemoState <= 3)
                    htmltext = "kekrops_q0061_08.htm";
            } else if (event.equalsIgnoreCase("reply_7"))
                if (GetMemoState >= 2 && GetMemoState <= 3) {
                    st.setCond(2);
                    st.setMemoState("rule_of_judicator", String.valueOf(4), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "kekrops_q0061_09.htm";
                }
        } else if (npcId == subelder_aientburg)
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 4) {
                    st.setMemoState("rule_of_judicator", String.valueOf(5), true);
                    htmltext = "subelder_aientburg_q0061_02.htm";
                }
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 5) {
                    st.giveItems(ADENA_ID, 26000);
                    st.getPlayer().getPlayerClassComponent().setClassId(ClassId.judicator.ordinal(), false, true);
                    st.getPlayer().broadcastCharInfo();
                    st.removeMemo("rule_of_judicator");
                    st.exitQuest(false);
                    htmltext = "subelder_aientburg_q0061_08.htm";
                }
            } else if (event.equalsIgnoreCase("reply_3"))
                if (GetMemoState == 5) {
                    st.giveItems(ADENA_ID, 26000);
                    st.getPlayer().getPlayerClassComponent().setClassId(ClassId.judicator.ordinal(), false, true);
                    st.getPlayer().broadcastCharInfo();
                    st.removeMemo("rule_of_judicator");
                    st.exitQuest(false);
                    htmltext = "subelder_aientburg_q0061_09.htm";
                }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("rule_of_judicator");
        String talker_name = st.getPlayer().getName();
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == grandmaste_piane) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "grandmaste_piane_q0061_02.htm";
                            st.exitQuest(true);
                            break;
                        case CLASS_ID:
                            htmltext = "grandmaste_piane_q0061_03.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = HtmCache.getInstance().getHtml("quests/_061_LawEnforcement/grandmaste_piane_q0061_01.htm", st.getPlayer());
                            htmltext = htmltext.replace("<?name?>", talker_name);
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == grandmaste_piane) {
                    if (GetMemoState == 1)
                        htmltext = "grandmaste_piane_q0061_06.htm";
                } else if (npcId == kekrops) {
                    if (GetMemoState == 1)
                        htmltext = "kekrops_q0061_01.htm";
                    else if (GetMemoState == 2)
                        htmltext = "kekrops_q0061_03.htm";
                    else if (GetMemoState == 3)
                        htmltext = "kekrops_q0061_04.htm";
                    else if (GetMemoState == 4)
                        htmltext = "kekrops_q0061_10.htm";
                } else if (npcId == subelder_aientburg)
                    if (GetMemoState == 4) {
                        htmltext = HtmCache.getInstance().getHtml("quests/_061_LawEnforcement/subelder_aientburg_q0061_01.htm", st.getPlayer());
                        htmltext = htmltext.replace("<?name?>", talker_name);
                    } else if (GetMemoState == 5)
                        htmltext = "subelder_aientburg_q0061_02.htm";
                break;
        }
        return htmltext;
    }
}