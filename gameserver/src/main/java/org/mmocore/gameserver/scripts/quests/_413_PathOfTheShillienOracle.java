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
public class _413_PathOfTheShillienOracle extends Quest {
    // npc
    private final static int master_sidra = 30330;
    private final static int magister_talbot = 30377;
    private final static int priest_adonius = 30375;
    // mobs
    private final static int zombie_soldier = 20457;
    private final static int zombie_warrior = 20458;
    private final static int shield_skeleton = 20514;
    private final static int skeleton_infantry = 20515;
    private final static int dark_succubus = 20776;
    // questitem
    private final static int sidras_letter1 = 1262;
    private final static int blank_sheet1 = 1263;
    private final static int bloody_rune1 = 1264;
    private final static int garmiel_book = 1265;
    private final static int prayer_of_adon = 1266;
    private final static int penitents_mark = 1267;
    private final static int ashen_bones = 1268;
    private final static int andariel_book = 1269;
    private final static int orb_of_abyss = 1270;

    public _413_PathOfTheShillienOracle() {
        super(false);
        addStartNpc(master_sidra);
        addTalkId(magister_talbot, priest_adonius);
        addKillId(zombie_soldier, zombie_warrior, shield_skeleton, skeleton_infantry, dark_succubus);
        addQuestItem(sidras_letter1, blank_sheet1, bloody_rune1, garmiel_book, prayer_of_adon, penitents_mark, ashen_bones, andariel_book);
        addLevelCheck(18);
        addClassIdCheck(ClassId.dark_mage);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        int talker_occupation = st.getPlayer().getPlayerClassComponent().getClassId().getId();
        int dark_mage = 0x26;
        int shillien_oracle = 0x2a;
        if (npcId == master_sidra) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                st.giveItems(sidras_letter1, 1);
                htmltext = "master_sidra_q0413_06.htm";
            } else if (event.equalsIgnoreCase("reply_1")) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = "master_sidra_q0413_02.htm";
                        st.exitQuest(true);
                        break;
                    case CLASS_ID:
                        if (talker_occupation != dark_mage) {
                            if (talker_occupation == shillien_oracle) {
                                htmltext = "master_sidra_q0413_02a.htm";
                                st.exitQuest(true);
                            } else {
                                htmltext = "master_sidra_q0413_03.htm";
                                st.exitQuest(true);
                            }
                        }
                        break;
                    default:
                        if (st.ownItemCount(orb_of_abyss) > 1)
                            htmltext = "master_sidra_q0413_04.htm";
                        else
                            htmltext = "master_sidra_q0413_05.htm";
                        break;
                }
            }
        } else if (npcId == magister_talbot) {
            if (event.equalsIgnoreCase("reply_1")) {
                st.setCond(2);
                st.takeItems(sidras_letter1, 1);
                st.giveItems(blank_sheet1, 5);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "magister_talbot_q0413_02.htm";
            }
        } else if (npcId == priest_adonius) {
            if (event.equalsIgnoreCase("reply_1"))
                htmltext = "priest_adonius_q0413_02.htm";
            else if (event.equalsIgnoreCase("reply_2"))
                htmltext = "priest_adonius_q0413_03.htm";
            else if (event.equalsIgnoreCase("reply_3") && st.ownItemCount(prayer_of_adon) > 0) {
                st.setCond(5);
                st.takeItems(prayer_of_adon, 1);
                st.giveItems(penitents_mark, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "priest_adonius_q0413_04.htm";
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
                if (npcId == master_sidra)
                    htmltext = "master_sidra_q0413_01.htm";
                break;
            case STARTED:
                if (npcId == master_sidra) {
                    if (st.ownItemCount(sidras_letter1) == 1)
                        htmltext = "master_sidra_q0413_07.htm";
                    else if (st.ownItemCount(blank_sheet1) > 0 || st.ownItemCount(bloody_rune1) > 0)
                        htmltext = "master_sidra_q0413_08.htm";
                    else if (st.ownItemCount(andariel_book) == 0 && st.ownItemCount(prayer_of_adon) + st.ownItemCount(garmiel_book) + st.ownItemCount(penitents_mark) + st.ownItemCount(ashen_bones) > 0)
                        htmltext = "master_sidra_q0413_09.htm";
                    else if (st.ownItemCount(andariel_book) == 1 && st.ownItemCount(garmiel_book) == 1) {
                        st.takeItems(andariel_book, 1);
                        st.takeItems(garmiel_book, 1);
                        st.giveItems(orb_of_abyss, 1);
                        htmltext = "master_sidra_q0413_10.htm";
                        if (GetOneTimeQuestFlag == 0) {
                            st.getPlayer().getPlayerVariables().set(PlayerVariables.profession_145, String.valueOf(1), -1);
                            if (talker_level >= 20)
                                st.addExpAndSp(320534, 26532);
                            else if (talker_level == 19)
                                st.addExpAndSp(456128, 33230);
                            else
                                st.addExpAndSp(591724, 39928);
                            st.giveItems(ADENA_ID, 163800);
                        }
                        st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                    }
                } else if (npcId == magister_talbot) {
                    if (st.ownItemCount(sidras_letter1) == 1)
                        htmltext = "magister_talbot_q0413_01.htm";
                    else if (st.ownItemCount(blank_sheet1) == 5 && st.ownItemCount(bloody_rune1) == 0)
                        htmltext = "magister_talbot_q0413_03.htm";
                    else if (st.ownItemCount(bloody_rune1) > 0 && st.ownItemCount(bloody_rune1) < 5)
                        htmltext = "magister_talbot_q0413_04.htm";
                    else if (st.ownItemCount(bloody_rune1) >= 5) {
                        st.setCond(4);
                        st.takeItems(bloody_rune1, -1);
                        st.giveItems(garmiel_book, 1);
                        st.giveItems(prayer_of_adon, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "magister_talbot_q0413_05.htm";
                    } else if (st.ownItemCount(prayer_of_adon) + st.ownItemCount(penitents_mark) + st.ownItemCount(ashen_bones) > 0)
                        htmltext = "magister_talbot_q0413_06.htm";
                    else if (st.ownItemCount(andariel_book) == 1 && st.ownItemCount(garmiel_book) == 1)
                        htmltext = "magister_talbot_q0413_07.htm";
                } else if (npcId == priest_adonius) {
                    if (st.ownItemCount(prayer_of_adon) == 1)
                        htmltext = "priest_adonius_q0413_01.htm";
                    else if (st.ownItemCount(penitents_mark) == 1 && st.ownItemCount(ashen_bones) == 0 && st.ownItemCount(andariel_book) == 0)
                        htmltext = "priest_adonius_q0413_05.htm";
                    else if (st.ownItemCount(penitents_mark) == 1 && st.ownItemCount(ashen_bones) < 10 && st.ownItemCount(ashen_bones) > 0)
                        htmltext = "priest_adonius_q0413_06.htm";
                    else if (st.ownItemCount(penitents_mark) == 1 && st.ownItemCount(ashen_bones) >= 10) {
                        st.setCond(7);
                        st.takeItems(ashen_bones, -1);
                        st.takeItems(penitents_mark, -1);
                        st.giveItems(andariel_book, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "priest_adonius_q0413_07.htm";
                    } else if (st.ownItemCount(andariel_book) == 1)
                        htmltext = "priest_adonius_q0413_08.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == dark_succubus) {
            if (st.ownItemCount(blank_sheet1) > 0) {
                st.giveItems(bloody_rune1, 1);
                if (st.ownItemCount(blank_sheet1) == 0)
                    st.soundEffect(SOUND_MIDDLE);
                else
                    st.soundEffect(SOUND_ITEMGET);
                st.takeItems(blank_sheet1, 1);
                if (st.ownItemCount(bloody_rune1) >= 5)
                    st.setCond(3);
            }
        } else if (npcId == zombie_soldier || npcId == zombie_warrior || npcId == shield_skeleton || npcId == skeleton_infantry) {
            if (st.ownItemCount(penitents_mark) == 1 && st.ownItemCount(ashen_bones) < 10) {
                st.giveItems(ashen_bones, 1);
                if (st.ownItemCount(ashen_bones) >= 10) {
                    st.setCond(6);
                    st.soundEffect(SOUND_MIDDLE);
                } else
                    st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }
}