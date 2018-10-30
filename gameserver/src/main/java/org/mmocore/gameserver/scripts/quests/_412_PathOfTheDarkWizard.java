package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
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
 * @version 1.2
 * @date 04/10/2015
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _412_PathOfTheDarkWizard extends Quest {
    // npc
    private final static int varika = 30421;
    private final static int annsery = 30418;
    private final static int charkeren = 30415;
    private final static int arkenia = 30419;
    // mobs
    private final static int marsh_zombie = 20015;
    private final static int thrill_sucker = 20022;
    private final static int scout_skeleton = 20045;
    private final static int skeleton_hunter = 20517;
    private final static int skeleton_hunter_archer = 20518;
    // questitem
    private final static int seeds_of_anger = 1253;
    private final static int seeds_of_despair = 1254;
    private final static int seeds_of_horror = 1255;
    private final static int seeds_of_lunacy = 1256;
    private final static int familys_ashes = 1257;
    private final static int knee_bone = 1259;
    private final static int heart_of_lunacy = 1260;
    private final static int jewel_of_darkness = 1261;
    private final static int lucky_key = 1277;
    private final static int candle = 1278;
    private final static int hub_scent = 1279;

    public _412_PathOfTheDarkWizard() {
        super(false);
        addStartNpc(varika);
        addTalkId(annsery, charkeren, arkenia);
        addQuestItem(seeds_of_anger, seeds_of_despair, seeds_of_horror, seeds_of_lunacy, familys_ashes, knee_bone, heart_of_lunacy, lucky_key, candle, hub_scent);
        addKillId(marsh_zombie, thrill_sucker, scout_skeleton, skeleton_hunter, skeleton_hunter_archer);
        addLevelCheck(18);
        addClassIdCheck(ClassId.dark_mage);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        int talker_occupation = st.getPlayer().getPlayerClassComponent().getClassId().getId();
        int dark_mage = 0x26;
        int dark_wizard = 0x27;
        if (npcId == varika) {
            if (event.equalsIgnoreCase("quest_accept")) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = "varika_q0412_02.htm";
                        st.exitQuest(true);
                        break;
                    case CLASS_ID:
                        if (talker_occupation != dark_mage) {
                            if (talker_occupation == dark_wizard) {
                                htmltext = "varika_q0412_02a.htm";
                                st.exitQuest(true);
                            } else {
                                htmltext = "varika_q0412_03.htm";
                                st.exitQuest(true);
                            }
                        }
                        break;
                    default:
                        if (st.ownItemCount(jewel_of_darkness) > 1)
                            htmltext = "varika_q0412_04.htm";
                        else {
                            st.setCond(1);
                            st.setState(STARTED);
                            st.soundEffect(SOUND_ACCEPT);
                            st.giveItems(seeds_of_despair, 1);
                            htmltext = "varika_q0412_05.htm";
                        }
                        break;
                }
            } else if (event.equalsIgnoreCase("reply_1")) {
                if (st.ownItemCount(seeds_of_anger) > 0)
                    htmltext = "varika_q0412_06.htm";
                else {
                    st.setMemoState("path_to_darkwizard", String.valueOf(1), true);
                    htmltext = "varika_q0412_07.htm";
                }
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (st.ownItemCount(seeds_of_horror) > 0)
                    htmltext = "varika_q0412_09.htm";
                else {
                    st.setMemoState("path_to_darkwizard", String.valueOf(2), true);
                    htmltext = "varika_q0412_10.htm";
                }
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (st.ownItemCount(seeds_of_lunacy) > 0)
                    htmltext = "varika_q0412_12.htm";
                else if (st.ownItemCount(seeds_of_lunacy) == 0 && st.ownItemCount(seeds_of_despair) > 0) {
                    st.setMemoState("path_to_darkwizard", String.valueOf(3), true);
                    htmltext = "varika_q0412_13.htm";
                }
            }
        } else if (npcId == annsery) {
            if (event.equalsIgnoreCase("reply_1")) {
                st.giveItems(candle, 1);
                htmltext = "annsery_q0412_02.htm";
            }
        } else if (npcId == charkeren) {
            if (event.equalsIgnoreCase("reply_4")) {
                st.giveItems(lucky_key, 1);
                htmltext = "charkeren_q0412_03.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("path_to_darkwizard");
        int talker_level = st.getPlayer().getLevel();
        int GetOneTimeQuestFlag = st.getPlayer().getPlayerVariables().getInt(PlayerVariables.profession_145);
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == varika) {
                    if (st.ownItemCount(jewel_of_darkness) == 0)
                        htmltext = "varika_q0412_01.htm";
                    else
                        htmltext = "varika_q0412_04.htm";
                }
                break;
            case STARTED:
                if (npcId == varika) {
                    if (st.ownItemCount(seeds_of_despair) > 0 && st.ownItemCount(seeds_of_horror) > 0 && st.ownItemCount(seeds_of_lunacy) > 0 && st.ownItemCount(seeds_of_anger) > 0) {
                        htmltext = "varika_q0412_16.htm";
                        st.takeItems(seeds_of_horror, 1);
                        st.takeItems(seeds_of_anger, 1);
                        st.takeItems(seeds_of_lunacy, 1);
                        st.takeItems(seeds_of_despair, 1);
                        st.giveItems(jewel_of_darkness, 1);
                        if (GetOneTimeQuestFlag == 0) {
                            st.getPlayer().getPlayerVariables().set(PlayerVariables.profession_145, String.valueOf(1), -1);
                            if (talker_level >= 20)
                                st.addExpAndSp(320534, 28630);
                            else if (talker_level == 19)
                                st.addExpAndSp(456128, 28630);
                            else
                                st.addExpAndSp(591724, 35328);
                            st.giveItems(ADENA_ID, 163800);
                        }
                        st.removeMemo("path_to_darkwizard");
                        st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                    } else if (st.ownItemCount(seeds_of_despair) == 1 && st.ownItemCount(familys_ashes) == 0 && st.ownItemCount(lucky_key) == 0 && st.ownItemCount(candle) == 0 && st.ownItemCount(hub_scent) == 0 && st.ownItemCount(knee_bone) == 0 && st.ownItemCount(heart_of_lunacy) == 0)
                        htmltext = "varika_q0412_17.htm";
                    else if (st.ownItemCount(seeds_of_despair) == 1 && GetMemoState == 1 && st.ownItemCount(seeds_of_anger) == 0)
                        htmltext = "varika_q0412_08.htm";
                    else if (st.ownItemCount(seeds_of_despair) == 1 && GetMemoState == 2 && st.ownItemCount(seeds_of_horror) == 0)
                        htmltext = "varika_q0412_19.htm";
                    else if (st.ownItemCount(seeds_of_despair) == 1 && GetMemoState == 3 && st.ownItemCount(seeds_of_lunacy) == 0)
                        htmltext = "varika_q0412_13.htm";
                } else if (npcId == annsery) {
                    if (st.ownItemCount(seeds_of_horror) == 0) {
                        if (st.ownItemCount(seeds_of_despair) == 1 && st.ownItemCount(candle) == 0 && st.ownItemCount(knee_bone) == 0)
                            htmltext = "annsery_q0412_01.htm";
                        else if (st.ownItemCount(seeds_of_despair) == 1 && st.ownItemCount(candle) == 1 && st.ownItemCount(knee_bone) < 2)
                            htmltext = "annsery_q0412_03.htm";
                        else if (st.ownItemCount(seeds_of_despair) == 1 && st.ownItemCount(candle) == 1 && st.ownItemCount(knee_bone) >= 2) {
                            st.giveItems(seeds_of_horror, 1);
                            st.takeItems(candle, 1);
                            st.takeItems(knee_bone, 2);
                            htmltext = "annsery_q0412_04.htm";
                        }
                    }
                } else if (npcId == charkeren) {
                    if (st.ownItemCount(seeds_of_anger) == 0) {
                        if (st.ownItemCount(seeds_of_despair) == 1 && st.ownItemCount(familys_ashes) == 0 && st.ownItemCount(lucky_key) == 0)
                            htmltext = "charkeren_q0412_01.htm";
                        else if (st.ownItemCount(seeds_of_despair) == 1 && st.ownItemCount(familys_ashes) < 3 && st.ownItemCount(lucky_key) == 1)
                            htmltext = "charkeren_q0412_04.htm";
                        else if (st.ownItemCount(seeds_of_despair) == 1 && st.ownItemCount(familys_ashes) >= 3 && st.ownItemCount(lucky_key) == 1) {
                            st.giveItems(seeds_of_anger, 1);
                            st.takeItems(familys_ashes, 3);
                            st.takeItems(lucky_key, 1);
                            htmltext = "charkeren_q0412_05.htm";
                        }
                    }
                } else if (npcId == arkenia) {
                    if (st.ownItemCount(seeds_of_lunacy) == 0) {
                        if (st.ownItemCount(hub_scent) == 0 && st.ownItemCount(heart_of_lunacy) == 0) {
                            st.giveItems(hub_scent, 1);
                            htmltext = "arkenia_q0412_01.htm";
                        } else if (st.ownItemCount(hub_scent) == 0 && st.ownItemCount(heart_of_lunacy) < 3)
                            htmltext = "arkenia_q0412_02.htm";
                        else if (st.ownItemCount(hub_scent) == 0 && st.ownItemCount(heart_of_lunacy) >= 3) {
                            st.giveItems(seeds_of_lunacy, 1);
                            st.takeItems(heart_of_lunacy, 3);
                            st.takeItems(hub_scent, 1);
                            htmltext = "arkenia_q0412_03.htm";
                        }
                    }
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == marsh_zombie) {
            if (st.ownItemCount(lucky_key) == 1 && st.ownItemCount(familys_ashes) < 3) {
                if (Rnd.get(2) == 0) {
                    st.giveItems(familys_ashes, 1);
                    if (st.ownItemCount(familys_ashes) == 3)
                        st.soundEffect(SOUND_MIDDLE);
                    else
                        st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == thrill_sucker || npcId == skeleton_hunter || npcId == skeleton_hunter_archer) {
            if (st.ownItemCount(candle) == 1 && st.ownItemCount(knee_bone) < 2) {
                if (Rnd.get(2) == 0) {
                    st.giveItems(knee_bone, 1);
                    if (st.ownItemCount(knee_bone) == 2)
                        st.soundEffect(SOUND_MIDDLE);
                    else
                        st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == scout_skeleton) {
            if (st.ownItemCount(hub_scent) == 1 && st.ownItemCount(heart_of_lunacy) < 3) {
                if (Rnd.get(2) == 0) {
                    st.giveItems(heart_of_lunacy, 1);
                    if (st.ownItemCount(heart_of_lunacy) == 3)
                        st.soundEffect(SOUND_MIDDLE);
                    else
                        st.soundEffect(SOUND_ITEMGET);
                }
            }
        }
        return null;
    }
}