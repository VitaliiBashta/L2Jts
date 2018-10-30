package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.model.base.ClassId;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.serverpackets.SocialAction;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.1
 * @date 04/10/2015
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _411_PathOfTheAssassin extends Quest {
    // npc
    private final static int triskel = 30416;
    private final static int arkenia = 30419;
    private final static int guard_leikan = 30382;
    // mobs
    private final static int moonstone_beast = 20369;
    private final static int calpico = 27036;
    // questitem
    private final static int shilens_call = 1245;
    private final static int arkenias_letter = 1246;
    private final static int leikans_note = 1247;
    private final static int onyx_beasts_molar = 1248;
    private final static int leikans_knife = 1249;
    private final static int shilens_tears = 1250;
    private final static int arkenia_recommend = 1251;
    private final static int iron_heart = 1252;

    public _411_PathOfTheAssassin() {
        super(false);
        addStartNpc(triskel);
        addTalkId(arkenia, guard_leikan);
        addKillId(moonstone_beast, calpico);
        addQuestItem(shilens_call, arkenias_letter, leikans_note, onyx_beasts_molar, leikans_knife, shilens_tears, arkenia_recommend);
        addLevelCheck(18);
        addClassIdCheck(ClassId.dark_fighter);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        int talker_occupation = st.getPlayer().getPlayerClassComponent().getClassId().getId();
        int dark_fighter = 0x1f;
        int assassin = 0x23;
        if (npcId == triskel) {
            if (event.equalsIgnoreCase("quest_accept")) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = "triskel_q0411_03.htm";
                        st.exitQuest(true);
                        break;
                    case CLASS_ID:
                        if (talker_occupation != dark_fighter) {
                            if (talker_occupation == assassin) {
                                htmltext = "triskel_q0411_02a.htm";
                                st.exitQuest(true);
                            } else {
                                htmltext = "triskel_q0411_02.htm";
                                st.exitQuest(true);
                            }
                        }
                        break;
                    default:
                        if (st.ownItemCount(iron_heart) == 1 && talker_occupation == dark_fighter)
                            htmltext = "triskel_q0411_04.htm";
                        else {
                            st.setCond(1);
                            st.setState(STARTED);
                            st.soundEffect(SOUND_ACCEPT);
                            st.giveItems(shilens_call, 1);
                            htmltext = "triskel_q0411_05.htm";
                        }
                        break;
                }
            }
        } else if (npcId == arkenia) {
            if (event.equalsIgnoreCase("reply_1") && st.ownItemCount(shilens_call) > 0) {
                st.setCond(2);
                st.takeItems(shilens_call, 1);
                st.giveItems(arkenias_letter, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "arkenia_q0411_05.htm";
            }
        } else if (npcId == guard_leikan) {
            if (event.equalsIgnoreCase("reply_1") && st.ownItemCount(arkenias_letter) > 0) {
                st.setCond(3);
                st.takeItems(arkenias_letter, 1);
                st.giveItems(leikans_note, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "guard_leikan_q0411_03.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int talker_level = st.getPlayer().getLevel();
        int GetOneTimeQuestFlag = st.getPlayer().getPlayerVariables().getInt(PlayerVariables.profession_145);
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == triskel) {
                    if (st.ownItemCount(iron_heart) == 0)
                        htmltext = "triskel_q0411_01.htm";
                    else
                        htmltext = "triskel_q0411_04.htm";
                }
                break;
            case STARTED:
                if (npcId == triskel) {
                    if (st.ownItemCount(arkenias_letter) == 0 && st.ownItemCount(leikans_note) == 0 && st.ownItemCount(shilens_tears) == 0 && st.ownItemCount(arkenia_recommend) == 1 && st.ownItemCount(iron_heart) == 0) {
                        st.takeItems(arkenia_recommend, 1);
                        st.giveItems(iron_heart, 1);
                        htmltext = "triskel_q0411_06.htm";
                        if (GetOneTimeQuestFlag == 0) {
                            st.getPlayer().getPlayerVariables().set(PlayerVariables.profession_145, String.valueOf(1), -1);
                            if (talker_level >= 20)
                                st.addExpAndSp(320534, 35830);
                            else if (talker_level == 19)
                                st.addExpAndSp(456128, 35830);
                            else
                                st.addExpAndSp(591724, 42528);
                            st.giveItems(ADENA_ID, 163800);
                        }
                        st.removeMemo("path_to_assassin");
                        st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                    } else if (st.ownItemCount(arkenias_letter) == 1 && st.ownItemCount(leikans_note) == 0 && st.ownItemCount(shilens_tears) == 0 && st.ownItemCount(arkenia_recommend) == 0 && st.ownItemCount(iron_heart) == 0 && st.ownItemCount(shilens_call) == 0)
                        htmltext = "triskel_q0411_07.htm";
                    else if (st.ownItemCount(arkenias_letter) == 0 && st.ownItemCount(leikans_note) == 1 && st.ownItemCount(shilens_tears) == 0 && st.ownItemCount(arkenia_recommend) == 0 && st.ownItemCount(iron_heart) == 0 && st.ownItemCount(shilens_call) == 0)
                        htmltext = "triskel_q0411_08.htm";
                    else if (st.ownItemCount(arkenias_letter) == 0 && st.ownItemCount(leikans_note) == 0 && st.ownItemCount(shilens_tears) == 0 && st.ownItemCount(arkenia_recommend) == 0 && st.ownItemCount(iron_heart) == 0 && st.ownItemCount(shilens_call) == 0)
                        htmltext = "triskel_q0411_09.htm";
                    else if (st.ownItemCount(arkenias_letter) == 0 && st.ownItemCount(leikans_note) == 0 && st.ownItemCount(shilens_tears) == 1 && st.ownItemCount(arkenia_recommend) == 0 && st.ownItemCount(iron_heart) == 0 && st.ownItemCount(shilens_call) == 0)
                        htmltext = "triskel_q0411_10.htm";
                    else if (st.ownItemCount(arkenias_letter) == 0 && st.ownItemCount(leikans_note) == 0 && st.ownItemCount(shilens_tears) == 0 && st.ownItemCount(arkenia_recommend) == 0 && st.ownItemCount(iron_heart) == 0 && st.ownItemCount(shilens_call) == 1)
                        htmltext = "triskel_q0411_11.htm";
                } else if (npcId == arkenia) {
                    if (st.ownItemCount(arkenias_letter) == 0 && st.ownItemCount(leikans_note) == 0 && st.ownItemCount(shilens_tears) == 0 && st.ownItemCount(arkenia_recommend) == 0 && st.ownItemCount(iron_heart) == 0 && st.ownItemCount(shilens_call) == 1)
                        htmltext = "arkenia_q0411_01.htm";
                    else if (st.ownItemCount(arkenias_letter) == 1 && st.ownItemCount(leikans_note) == 0 && st.ownItemCount(shilens_tears) == 0 && st.ownItemCount(arkenia_recommend) == 0 && st.ownItemCount(iron_heart) == 0 && st.ownItemCount(shilens_call) == 0)
                        htmltext = "arkenia_q0411_07.htm";
                    else if (st.ownItemCount(arkenias_letter) == 0 && st.ownItemCount(leikans_note) == 0 && st.ownItemCount(shilens_tears) == 1 && st.ownItemCount(arkenia_recommend) == 0 && st.ownItemCount(iron_heart) == 0 && st.ownItemCount(shilens_call) == 0) {
                        st.setCond(7);
                        st.takeItems(shilens_tears, 1);
                        st.giveItems(arkenia_recommend, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "arkenia_q0411_08.htm";
                    } else if (st.ownItemCount(arkenias_letter) == 0 && st.ownItemCount(leikans_note) == 0 && st.ownItemCount(shilens_tears) == 0 && st.ownItemCount(arkenia_recommend) == 1 && st.ownItemCount(iron_heart) == 0 && st.ownItemCount(shilens_call) == 0)
                        htmltext = "arkenia_q0411_09.htm";
                    else if (st.ownItemCount(arkenias_letter) == 0 && st.ownItemCount(leikans_note) == 1 && st.ownItemCount(shilens_tears) == 0 && st.ownItemCount(arkenia_recommend) == 0 && st.ownItemCount(iron_heart) == 0 && st.ownItemCount(shilens_call) == 0)
                        htmltext = "arkenia_q0411_10.htm";
                    else if (st.ownItemCount(arkenias_letter) == 0 && st.ownItemCount(leikans_note) == 0 && st.ownItemCount(shilens_tears) == 0 && st.ownItemCount(arkenia_recommend) == 0 && st.ownItemCount(iron_heart) == 0 && st.ownItemCount(shilens_call) == 0)
                        htmltext = "arkenia_q0411_11.htm";
                } else if (npcId == guard_leikan) {
                    if (st.ownItemCount(arkenias_letter) == 1 && st.ownItemCount(leikans_note) == 0 && st.ownItemCount(shilens_tears) == 0 && st.ownItemCount(arkenia_recommend) == 0 && st.ownItemCount(iron_heart) == 0 && st.ownItemCount(shilens_call) == 0 && st.ownItemCount(onyx_beasts_molar) == 0)
                        htmltext = "guard_leikan_q0411_01.htm";
                    else if (st.ownItemCount(arkenias_letter) == 0 && st.ownItemCount(leikans_note) == 1 && st.ownItemCount(shilens_tears) == 0 && st.ownItemCount(arkenia_recommend) == 0 && st.ownItemCount(iron_heart) == 0 && st.ownItemCount(shilens_call) == 0 && st.ownItemCount(onyx_beasts_molar) == 0)
                        htmltext = "guard_leikan_q0411_05.htm";
                    else if (st.ownItemCount(arkenias_letter) == 0 && st.ownItemCount(leikans_note) == 1 && st.ownItemCount(shilens_tears) == 0 && st.ownItemCount(arkenia_recommend) == 0 && st.ownItemCount(iron_heart) == 0 && st.ownItemCount(shilens_call) == 0 && st.ownItemCount(onyx_beasts_molar) < 10 && st.ownItemCount(onyx_beasts_molar) > 0)
                        htmltext = "guard_leikan_q0411_06.htm";
                    else if (st.ownItemCount(arkenias_letter) == 0 && st.ownItemCount(leikans_note) == 1 && st.ownItemCount(shilens_tears) == 0 && st.ownItemCount(arkenia_recommend) == 0 && st.ownItemCount(iron_heart) == 0 && st.ownItemCount(shilens_call) == 0 && st.ownItemCount(onyx_beasts_molar) >= 10) {
                        st.setCond(5);
                        st.setMemoState("path_to_assassin", String.valueOf(1), true);
                        st.takeItems(onyx_beasts_molar, -1);
                        st.takeItems(leikans_note, 1);
                        htmltext = "guard_leikan_q0411_07.htm";
                    } else if (st.ownItemCount(shilens_tears) == 1)
                        htmltext = "guard_leikan_q0411_08.htm";
                    else if (st.ownItemCount(arkenias_letter) == 0 && st.ownItemCount(leikans_note) == 0 && st.ownItemCount(shilens_tears) == 0 && st.ownItemCount(arkenia_recommend) == 0 && st.ownItemCount(iron_heart) == 0 && st.ownItemCount(shilens_call) == 0 && st.ownItemCount(onyx_beasts_molar) == 0)
                        htmltext = "guard_leikan_q0411_09.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("path_to_assassin");
        int npcId = npc.getNpcId();
        if (npcId == moonstone_beast) {
            if (st.ownItemCount(leikans_note) == 1 && st.ownItemCount(onyx_beasts_molar) < 10) {
                st.giveItems(onyx_beasts_molar, 1);
                if (st.ownItemCount(onyx_beasts_molar) >= 10) {
                    st.setCond(4);
                    st.soundEffect(SOUND_MIDDLE);
                } else
                    st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == calpico) {
            if (st.ownItemCount(shilens_tears) == 0 && GetMemoState == 1) {
                st.setCond(6);
                st.giveItems(shilens_tears, 1);
                st.soundEffect(SOUND_MIDDLE);
            }
        }
        return null;
    }
}