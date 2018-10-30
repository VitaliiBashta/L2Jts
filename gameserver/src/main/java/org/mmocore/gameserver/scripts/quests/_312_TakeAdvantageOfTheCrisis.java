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
public class _312_TakeAdvantageOfTheCrisis extends Quest {
    // npc
    private final static int elder_filaur = 30535;
    // mobs
    private final static int robber_summoner = 22678;
    private final static int robber_wizard = 22679;
    private final static int robber_worker = 22680;
    private final static int robber_warrior = 22681;
    private final static int robber_berserker = 22682;
    private final static int robber_s_shadeless = 22683;
    private final static int robber_s_shadow = 22684;
    private final static int robber_s_knight_shadow = 22685;
    private final static int robber_s_spectral_lord = 22686;
    private final static int mine_banshee = 22687;
    private final static int mine_evil_spirit = 22688;
    private final static int mine_bug = 22689;
    private final static int mine_descendants = 22690;
    // etcitem
    private final static int rp_sealed_dynasty_leather_mail_i = 9487;
    private final static int rp_sealed_dynasty_leather_legging_i = 9488;
    private final static int rp_sealed_dynasty_leather_helmet_i = 9489;
    private final static int rp_sealed_dynasty_leather_gloves_i = 9490;
    private final static int rp_sealed_dynasty_leather_boots_i = 9491;
    private final static int rp_sealed_dynasty_shield_i = 9497;
    private final static int renad = 9628;
    private final static int adamantium = 9629;
    private final static int oricalcum = 9630;
    private final static int codex_of_giant_forgetting = 9625;
    private final static int codex_of_giant_training = 9626;
    // questitem
    private final static int q_mineral_debris = 14875;

    public _312_TakeAdvantageOfTheCrisis() {
        super(false);
        addStartNpc(elder_filaur);
        addKillId(robber_summoner, robber_wizard, robber_worker, robber_warrior, robber_berserker, robber_s_shadeless, robber_s_shadow, robber_s_knight_shadow, robber_s_spectral_lord, mine_banshee, mine_evil_spirit, mine_bug, mine_descendants);
        addQuestItem(q_mineral_debris);
        addLevelCheck(80);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("from_crisis_to_opportunity");
        if (event.equalsIgnoreCase("quest_accept")) {
            st.setCond(1);
            st.setMemoState("from_crisis_to_opportunity", String.valueOf(1), true);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            htmltext = "elder_filaur_q0312_04.htm";
        } else if (event.equalsIgnoreCase("reply_1"))
            htmltext = "elder_filaur_q0312_03.htm";
        else if (event.equalsIgnoreCase("reply_3")) {
            if (GetMemoState == 1)
                htmltext = "elder_filaur_q0312_08.htm";
        } else if (event.equalsIgnoreCase("reply_4")) {
            if (GetMemoState == 1 && st.ownItemCount(q_mineral_debris) >= 1)
                htmltext = "elder_filaur_q0312_09.htm";
        } else if (event.equalsIgnoreCase("reply_5")) {
            if (GetMemoState == 1) {
                st.removeMemo("from_crisis_to_opportunity");
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                htmltext = "elder_filaur_q0312_10.htm";
            }
        } else if (event.equalsIgnoreCase("reply_6"))
            htmltext = "elder_filaur_q0312_01a.htm";
        else if (event.equalsIgnoreCase("reply_7"))
            htmltext = "elder_filaur_q0312_01b.htm";
        else if (event.equalsIgnoreCase("reply_8"))
            htmltext = "elder_filaur_q0312_01c.htm";
        else if (event.equalsIgnoreCase("reply_100")) {
            if (GetMemoState == 1 && st.ownItemCount(q_mineral_debris) >= 1)
                htmltext = "elder_filaur_q0312_06a.htm";
        } else if (event.equalsIgnoreCase("reply_101")) {
            if (GetMemoState == 1 && st.ownItemCount(q_mineral_debris) >= 1)
                htmltext = "elder_filaur_q0312_06b.htm";
        } else if (event.equalsIgnoreCase("reply_102")) {
            if (GetMemoState == 1 && st.ownItemCount(q_mineral_debris) >= 1)
                htmltext = "elder_filaur_q0312_06c.htm";
        } else if (event.equalsIgnoreCase("reply_103")) {
            if (GetMemoState == 1 && st.ownItemCount(q_mineral_debris) >= 1)
                htmltext = "elder_filaur_q0312_06d.htm";
        } else if (event.equalsIgnoreCase("reply_16")) {
            if (st.ownItemCount(q_mineral_debris) >= 244) {
                st.giveItems(rp_sealed_dynasty_leather_mail_i, 1);
                st.takeItems(q_mineral_debris, 244);
                htmltext = "elder_filaur_q0312_07.htm";
            } else
                htmltext = "elder_filaur_q0312_07a.htm";
        } else if (event.equalsIgnoreCase("reply_17")) {
            if (st.ownItemCount(q_mineral_debris) >= 153) {
                st.giveItems(rp_sealed_dynasty_leather_legging_i, 1);
                st.takeItems(q_mineral_debris, 153);
                htmltext = "elder_filaur_q0312_07.htm";
            } else
                htmltext = "elder_filaur_q0312_07a.htm";
        } else if (event.equalsIgnoreCase("reply_18")) {
            if (st.ownItemCount(q_mineral_debris) >= 122) {
                st.giveItems(rp_sealed_dynasty_leather_helmet_i, 1);
                st.takeItems(q_mineral_debris, 122);
                htmltext = "elder_filaur_q0312_07.htm";
            } else
                htmltext = "elder_filaur_q0312_07a.htm";
        } else if (event.equalsIgnoreCase("reply_19")) {
            if (st.ownItemCount(q_mineral_debris) >= 82) {
                st.giveItems(rp_sealed_dynasty_leather_gloves_i, 1);
                st.takeItems(q_mineral_debris, 82);
                htmltext = "elder_filaur_q0312_07.htm";
            } else
                htmltext = "elder_filaur_q0312_07a.htm";
        } else if (event.equalsIgnoreCase("reply_20")) {
            if (st.ownItemCount(q_mineral_debris) >= 82) {
                st.giveItems(rp_sealed_dynasty_leather_boots_i, 1);
                st.takeItems(q_mineral_debris, 82);
                htmltext = "elder_filaur_q0312_07.htm";
            } else
                htmltext = "elder_filaur_q0312_07a.htm";
        } else if (event.equalsIgnoreCase("reply_26")) {
            if (st.ownItemCount(q_mineral_debris) >= 86) {
                st.giveItems(rp_sealed_dynasty_shield_i, 1);
                st.takeItems(q_mineral_debris, 86);
                htmltext = "elder_filaur_q0312_07.htm";
            } else
                htmltext = "elder_filaur_q0312_07a.htm";
        } else if (event.equalsIgnoreCase("reply_27")) {
            if (st.ownItemCount(q_mineral_debris) >= 24) {
                st.giveItems(renad, 1);
                st.takeItems(q_mineral_debris, 24);
                htmltext = "elder_filaur_q0312_07.htm";
            } else
                htmltext = "elder_filaur_q0312_07a.htm";
        } else if (event.equalsIgnoreCase("reply_28")) {
            if (st.ownItemCount(q_mineral_debris) >= 43) {
                st.giveItems(adamantium, 1);
                st.takeItems(q_mineral_debris, 43);
                htmltext = "elder_filaur_q0312_07.htm";
            } else
                htmltext = "elder_filaur_q0312_07a.htm";
        } else if (event.equalsIgnoreCase("reply_29")) {
            if (st.ownItemCount(q_mineral_debris) >= 36) {
                st.giveItems(oricalcum, 1);
                st.takeItems(q_mineral_debris, 36);
                htmltext = "elder_filaur_q0312_07.htm";
            } else
                htmltext = "elder_filaur_q0312_07a.htm";
        } else if (event.equalsIgnoreCase("reply_30")) {
            if (st.ownItemCount(q_mineral_debris) >= 667) {
                st.giveItems(codex_of_giant_forgetting, 1);
                st.takeItems(q_mineral_debris, 667);
                htmltext = "elder_filaur_q0312_07.htm";
            } else
                htmltext = "elder_filaur_q0312_07a.htm";
        } else if (event.equalsIgnoreCase("reply_31")) {
            if (st.ownItemCount(q_mineral_debris) >= 1000) {
                st.giveItems(codex_of_giant_training, 1);
                st.takeItems(q_mineral_debris, 1000);
                htmltext = "elder_filaur_q0312_07.htm";
            } else
                htmltext = "elder_filaur_q0312_07a.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("from_crisis_to_opportunity");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == elder_filaur) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "elder_filaur_q0312_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "elder_filaur_q0312_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == elder_filaur)
                    if (GetMemoState == 1 && st.ownItemCount(q_mineral_debris) < 1)
                        htmltext = "elder_filaur_q0312_05.htm";
                    else if (GetMemoState == 1 && st.ownItemCount(q_mineral_debris) >= 1)
                        htmltext = "elder_filaur_q0312_06.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("from_crisis_to_opportunity");
        int npcId = npc.getNpcId();
        if (GetMemoState == 1)
            if (npcId == robber_summoner) {
                int i0 = Rnd.get(1000);
                if (i0 < 291) {
                    st.giveItems(q_mineral_debris, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == robber_wizard) {
                int i0 = Rnd.get(1000);
                if (i0 < 596) {
                    st.giveItems(q_mineral_debris, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == robber_worker) {
                int i0 = Rnd.get(1000);
                if (i0 < 610) {
                    st.giveItems(q_mineral_debris, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == robber_warrior || npcId == robber_s_knight_shadow || npcId == robber_s_spectral_lord) {
                int i0 = Rnd.get(1000);
                if (i0 < 626) {
                    st.giveItems(q_mineral_debris, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == robber_berserker) {
                int i0 = Rnd.get(1000);
                if (i0 < 692) {
                    st.giveItems(q_mineral_debris, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == robber_s_shadeless) {
                int i0 = Rnd.get(1000);
                if (i0 < 650) {
                    st.giveItems(q_mineral_debris, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == robber_s_shadow) {
                int i0 = Rnd.get(1000);
                if (i0 < 310) {
                    st.giveItems(q_mineral_debris, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == mine_banshee) {
                int i0 = Rnd.get(1000);
                if (i0 < 308) {
                    st.giveItems(q_mineral_debris, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == mine_evil_spirit) {
                int i0 = Rnd.get(1000);
                if (i0 < 416) {
                    st.giveItems(q_mineral_debris, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == mine_bug) {
                int i0 = Rnd.get(1000);
                if (i0 < 212) {
                    st.giveItems(q_mineral_debris, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == mine_descendants) {
                int i0 = Rnd.get(1000);
                if (i0 < 748) {
                    st.giveItems(q_mineral_debris, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        return null;
    }
}