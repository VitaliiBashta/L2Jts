package org.mmocore.gameserver.scripts.quests;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.listener.actor.OnCurrentHpDamageListener;
import org.mmocore.gameserver.listener.actor.OnDeathListener;
import org.mmocore.gameserver.listener.actor.player.OnItemPickupListener;
import org.mmocore.gameserver.listener.actor.player.OnLevelUpListener;
import org.mmocore.gameserver.listener.actor.player.OnPlayerEnterListener;
import org.mmocore.gameserver.manager.QuestManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.serverpackets.RadarControl;
import org.mmocore.gameserver.network.lineage.serverpackets.TutorialCloseHtml;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.creatures.listeners.CharListenerList;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.0
 * @date 26/12/2014
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _255_Tutorial extends Quest {
    // questitem
    private static final int tutorial_blue_gem = 6353;
    private static final int tutorial_guide = 5588;
    // First class id
    private static final int fighter = 0x0;
    private static final int mage = 0xa;
    private static final int elven_fighter = 0x12;
    private static final int elven_mage = 0x19;
    private static final int dark_fighter = 0x1f;
    private static final int dark_mage = 0x26;
    private static final int orc_fighter = 0x2c;
    private static final int orc_mage = 0x31;
    private static final int dwarven_fighter = 0x35;
    private static final int kamael_m_soldier = 0x7b;
    private static final int kamael_f_soldier = 0x7c;
    private static final int trooper = 0x7d;
    private static final int warder = 0x7e;
    // Second class id
    private static final int warrior = 0x01;
    private static final int knight = 0x04;
    private static final int rogue = 0x07;
    private static final int wizard = 0x0b;
    private static final int cleric = 0x0f;
    private static final int elven_knight = 0x13;
    private static final int elven_scout = 0x16;
    private static final int elven_wizard = 0x1a;
    private static final int oracle = 0x1d;
    private static final int orc_raider = 0x2d;
    private static final int orc_monk = 0x2f;
    private static final int orc_shaman = 0x32;
    private static final int scavenger = 0x36;
    private static final int artisan = 0x38;
    private static final int palus_knight = 0x20;
    private static final int assassin = 0x23;
    private static final int dark_wizard = 0x27;
    private static final int shillien_oracle = 0x2a;
    // Tutorial Events
    private static final int TUTORIAL_MOVE_CLICK = 0x00000001;
    private static final int TUTORIAL_VIEW_CHANGE = 0x00000002;
    private static final int TUTORIAL_RIGHT_CLICK = 0x00000008;
    private static final int TUTORIAL_HP_UNDER_1over3 = 0x00000100;
    private static final int TUTORIAL_DIE_1_9 = 0x00000200;
    private static final int TUTORIAL_LEVEL_UP_5 = 0x00000400;
    private static final int TUTORIAL_LEVEL_UP_6 = 0x08000000;
    private static final int TUTORIAL_LEVEL_UP_7 = 0x00000800;
    private static final int TUTORIAL_LEVEL_UP_9 = 0x10000000;
    private static final int TUTORIAL_LEVEL_UP_10 = 0x20000000;
    private static final int TUTORIAL_LEVEL_UP_12 = 0x40000000;
    private static final int TUTORIAL_LEVEL_UP_15 = 0x04000000;
    private static final int TUTORIAL_LEVEL_UP_20 = 0x00001000;
    private static final int TUTORIAL_LEVEL_UP_35 = 0x01000000;
    private static final int TUTORIAL_LEVEL_UP_39 = 0x00004000;
    private static final int TUTORIAL_LEVEL_UP_75 = 0x02000000;
    private static final int TUTORIAL_LEVEL_UP_76 = 0x00008000;
    private static final int TUTORIAL_LEVEL_UP_36 = 0x00000020;
    private static final int TUTORIAL_LEVEL_UP_61 = 0x00000040;
    private static final int TUTORIAL_LEVEL_UP_73 = 0x00000080;
    private static final int TUTORIAL_PICK_ADENA = 0x00200000;
    private static final int TUTORIAL_PICK_BLUE_GEM = 0x00100000;
    private static final int TUTORIAL_SIT_DOWN = 0x00800000;
    private static TutorialShowListener _tutorialShowListener;

    public _255_Tutorial() {
        super(false);
        CharListenerList.addGlobal(new OnPlayerEnterListenerImpl());
        CharListenerList.addGlobal(new OnItemPickupListenerImpl());
        CharListenerList.addGlobal(new OnLevelUpListenerImpl());
        CharListenerList.addGlobal(new OnDeathListenerImpl());
        _tutorialShowListener = new TutorialShowListener();
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String html = "";
        int talker_occupation = st.getPlayer().getPlayerClassComponent().getClassId().getId();
        int GetMemoState = st.getInt("tutorial_quest");
        int GetMemoStateEx = st.getInt("tutorial_quest_ex");
        int talker_level = st.getPlayer().getLevel();
        int i1;
        int i0;
        // EventHandler USER_CONNECTED
        if (event.startsWith("UC")) {
            if (talker_level < 6) {
                switch (GetMemoState) {
                    case 0:
                        st.startQuestTimer("QT", 10000);
                        st.setMemoState("tutorial_quest", String.valueOf(1 | GetMemoState & ~(8388608 | 1048576)));
                        st.setMemoState("tutorial_quest_ex", "-2");
                        break;
                    case 1:
                        st.showQuestionMark(1);
                        st.playTutorialVoice("tutorial_voice_006");
                        st.soundEffect(SOUND_TUTORIAL);
                        st.showQuestionMark(1);
                        break;
                    case 2:
                        st.showQuestionMark(2);
                        st.soundEffect(SOUND_TUTORIAL);
                        break;
                    case 3:
                        int i3 = 0;
                        if (st.ownItemCount(tutorial_blue_gem) == 1) {
                            i3 = 3;
                        }
                        if (GetMemoStateEx == 2) {
                            i3 = 1;
                        } else if (i3 == 1) {
                            st.showQuestionMark(3);
                            st.soundEffect(SOUND_TUTORIAL);
                        } else if (i3 == 2) {
                            st.showQuestionMark(4);
                            st.soundEffect(SOUND_TUTORIAL);
                        } else if (i3 == 3) {
                            st.showQuestionMark(5);
                            st.soundEffect(SOUND_TUTORIAL);
                        }
                        break;
                    case 4:
                        st.showQuestionMark(12);
                        st.soundEffect(SOUND_TUTORIAL);
                        break;
                }
                // st.onTutorialClientEvent(GetMemoState);
            } else {
                if (talker_level == 18 && st.getPlayer().getQuestState(10276) == null) {
                    st.showQuestionMark(33);
                    st.soundEffect(SOUND_TUTORIAL);
                } else if (talker_level == 28 && st.getPlayer().getQuestState(10277) == null) {
                    st.showQuestionMark(33);
                    st.soundEffect(SOUND_TUTORIAL);
                } else if (talker_level == 38 && st.getPlayer().getQuestState(10278) == null) {
                    st.showQuestionMark(33);
                    st.soundEffect(SOUND_TUTORIAL);
                } else if (talker_level == 48 && st.getPlayer().getQuestState(10279) == null) {
                    st.showQuestionMark(33);
                    st.soundEffect(SOUND_TUTORIAL);
                } else if (talker_level == 58 && st.getPlayer().getQuestState(10280) == null) {
                    st.showQuestionMark(33);
                    st.soundEffect(SOUND_TUTORIAL);
                } else if (talker_level == 68 && st.getPlayer().getQuestState(10281) == null) {
                    st.showQuestionMark(33);
                    st.soundEffect(SOUND_TUTORIAL);
                } else if (talker_level == 79 && st.getPlayer().getQuestState(192) == null) {
                    st.showQuestionMark(34);
                    st.soundEffect(SOUND_TUTORIAL);
                }
            }
        }
        // EventHandler TIMER_FIRED_EX
        else if (event.startsWith("QT")) {
            if (GetMemoStateEx == -2) {
                if (talker_occupation == fighter) {
                    st.playTutorialVoice("tutorial_voice_001a");
                    st.showTutorialHTML("tutorial_human_fighter001.htm");
                } else if (talker_occupation == mage) {
                    st.playTutorialVoice("tutorial_voice_001b");
                    st.showTutorialHTML("tutorial_human_mage001.htm");
                } else if (talker_occupation == elven_fighter) {
                    st.playTutorialVoice("tutorial_voice_001c");
                    st.showTutorialHTML("tutorial_elven_fighter001.htm");
                } else if (talker_occupation == elven_mage) {
                    st.playTutorialVoice("tutorial_voice_001d");
                    st.showTutorialHTML("tutorial_elven_mage001.htm");
                } else if (talker_occupation == dark_fighter) {
                    st.playTutorialVoice("tutorial_voice_001e");
                    st.showTutorialHTML("tutorial_delf_fighter001.htm");
                } else if (talker_occupation == dark_mage) {
                    st.playTutorialVoice("tutorial_voice_001f");
                    st.showTutorialHTML("tutorial_delf_mage001.htm");
                } else if (talker_occupation == orc_fighter) {
                    st.playTutorialVoice("tutorial_voice_001g");
                    st.showTutorialHTML("tutorial_orc_fighter001.htm");
                } else if (talker_occupation == orc_mage) {
                    st.playTutorialVoice("tutorial_voice_001h");
                    st.showTutorialHTML("tutorial_orc_mage001.htm");
                } else if (talker_occupation == dwarven_fighter) {
                    st.playTutorialVoice("tutorial_voice_001i");
                    st.showTutorialHTML("tutorial_dwarven_fighter001.htm");
                } else if (talker_occupation == kamael_m_soldier || talker_occupation == kamael_f_soldier) {
                    st.playTutorialVoice("tutorial_voice_001k");
                    st.showTutorialHTML("tutorial_kamael001.htm");
                }
                if (st.ownItemCount(tutorial_guide) == 0)
                    st.giveItems(tutorial_guide, 1);
                st.cancelQuestTimer("QT"); // нужно?
                st.startQuestTimer("QT", 30000);
                st.setMemoState("tutorial_quest_ex", "-3");
            } else if (GetMemoStateEx == -3)
                st.playTutorialVoice("tutorial_voice_002");
            else if (GetMemoStateEx == -4) {
                st.playTutorialVoice("tutorial_voice_008");
                st.setMemoState("tutorial_quest_ex", "-5");
            }
        }
        // EventHandler tutorial_close_
        else if (event.startsWith("tutorial_close_")) {
            int event_id = 0;
            if (!event.equalsIgnoreCase("tutorial_close_")) {
                event_id = Integer.valueOf(event.substring(15));
            }
            switch (event_id) {
                case 0:
                    st.getPlayer().sendPacket(TutorialCloseHtml.STATIC);
                    break;
                case 1:
                    st.getPlayer().sendPacket(TutorialCloseHtml.STATIC);
                    st.playTutorialVoice("tutorial_voice_006");
                    st.showQuestionMark(1);
                    st.soundEffect(SOUND_TUTORIAL);
                    st.startQuestTimer("QT", 30000);
                    if (GetMemoStateEx < 0)
                        st.setMemoState("tutorial_quest_ex", "-4");
                    break;
                case 2:
                    st.playTutorialVoice("tutorial_voice_003");
                    st.showTutorialHTML("tutorial_02.htm");
                    st.onTutorialClientEvent(GetMemoState & 2147483632 | 1);
                    if (GetMemoStateEx < 0)
                        st.setMemoState("tutorial_quest_ex", "-5");
                    break;
                case 3:
                    st.showTutorialHTML("tutorial_03.htm");
                    st.onTutorialClientEvent(GetMemoState & 2147483632 | 2);
                    break;
                case 4: // FIXME: не используется, проверить!
                    st.showTutorialHTML("tutorial_04.htm");
                    st.onTutorialClientEvent(GetMemoState & 2147483632 | 4);
                    break;
                case 5:
                    st.showTutorialHTML("tutorial_05.htm");
                    st.onTutorialClientEvent(GetMemoState & 2147483632 | 8);
                    break;
                case 6: // FIXME: не используется, проверить!
                    st.showTutorialHTML("tutorial_06.htm");
                    st.onTutorialClientEvent(GetMemoState & 2147483632 | 16);
                    break;
                case 7:
                    st.showTutorialHTML("tutorial_100.htm");
                    st.onTutorialClientEvent(GetMemoState & 2147483632);
                    break;
                case 8:
                    st.showTutorialHTML("tutorial_101.htm");
                    st.onTutorialClientEvent(GetMemoState & 2147483632);
                    break;
                case 9:
                    st.showTutorialHTML("tutorial_102.htm");
                    st.onTutorialClientEvent(GetMemoState & 2147483632);
                    break;
                case 10:
                    st.showTutorialHTML("tutorial_103.htm");
                    st.onTutorialClientEvent(GetMemoState & 2147483632);
                    break;
                case 11:
                    st.showTutorialHTML("tutorial_104.htm");
                    st.onTutorialClientEvent(GetMemoState & 2147483632);
                    break;
                case 12:
                    st.getPlayer().sendPacket(TutorialCloseHtml.STATIC);
                    break;
            }
        }
        // Client TUTORIAL_EVENT
        else if (event.startsWith("CE")) {
            int event_id = Integer.valueOf(event.substring(2));
            i1 = GetMemoState;
            i0 = i1 & 2147483632;
            switch (event_id) {
                case TUTORIAL_MOVE_CLICK:
                    if (talker_level < 6) {
                        st.playTutorialVoice("tutorial_voice_004");
                        st.soundEffect(SOUND_TUTORIAL);
                        st.showTutorialHTML("tutorial_03.htm");
                        st.onTutorialClientEvent(i0 | TUTORIAL_VIEW_CHANGE);
                    }
                    break;
                case TUTORIAL_VIEW_CHANGE:
                    if (talker_level < 6) {
                        st.playTutorialVoice("tutorial_voice_005");
                        st.soundEffect(SOUND_TUTORIAL);
                        st.showTutorialHTML("tutorial_05.htm");
                        st.onTutorialClientEvent(i0 | TUTORIAL_RIGHT_CLICK);
                    }
                    break;
                case TUTORIAL_RIGHT_CLICK:
                    if (talker_level < 6) {
                        if (talker_occupation == fighter)
                            ThreadPoolManager.getInstance().schedule(new RadarTask(-71424, 258336, -3109, st.getPlayer()), 500L);
                        else if (talker_occupation == mage)
                            ThreadPoolManager.getInstance().schedule(new RadarTask(-91036, 248044, -3568, st.getPlayer()), 500L);
                        else if (talker_occupation == elven_fighter || talker_occupation == elven_mage)
                            ThreadPoolManager.getInstance().schedule(new RadarTask(46112, 41200, -3504, st.getPlayer()), 500L);
                        else if (talker_occupation == dark_fighter || talker_occupation == dark_mage)
                            ThreadPoolManager.getInstance().schedule(new RadarTask(28384, 11056, -4233, st.getPlayer()), 500L);
                        else if (talker_occupation == orc_fighter || talker_occupation == orc_mage)
                            ThreadPoolManager.getInstance().schedule(new RadarTask(-56736, -113680, -672, st.getPlayer()), 500L);
                        else if (talker_occupation == dwarven_fighter)
                            ThreadPoolManager.getInstance().schedule(new RadarTask(108567, -173994, -406, st.getPlayer()), 500L);
                        else if (talker_occupation == kamael_m_soldier || talker_occupation == kamael_f_soldier)
                            ThreadPoolManager.getInstance().schedule(new RadarTask(-125872, 38016, 1251, st.getPlayer()), 500L);
                        st.showTutorialHTML("tutorial_human_fighter007.htm");
                        st.soundEffect(SOUND_TUTORIAL);
                        st.playTutorialVoice("tutorial_voice_007");
                        st.setMemoState("tutorial_quest", String.valueOf(i0 | 2));
                        if (GetMemoStateEx < 0)
                            st.setMemoState("tutorial_quest_ex", "-5");
                    }
                    break;
                case TUTORIAL_HP_UNDER_1over3:
                    if (talker_level < 6) {
                        st.playTutorialVoice("tutorial_voice_017");
                        st.showQuestionMark(10);
                        st.soundEffect(SOUND_TUTORIAL);
                        st.setMemoState("tutorial_quest", String.valueOf(i1 & ~256));
                        st.onTutorialClientEvent(i0 & ~TUTORIAL_HP_UNDER_1over3 | TUTORIAL_SIT_DOWN);
                    }
                    break;
                case TUTORIAL_DIE_1_9:
                    st.playTutorialVoice("tutorial_voice_016");
                    st.showQuestionMark(8);
                    st.soundEffect(SOUND_TUTORIAL);
                    st.setMemoState("tutorial_quest", String.valueOf(i1 & ~TUTORIAL_DIE_1_9));
                    break;
                case TUTORIAL_LEVEL_UP_5:
                    st.setMemoState("tutorial_quest", String.valueOf(i1 & ~TUTORIAL_LEVEL_UP_5));
                    if (talker_occupation == fighter)
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-83020, 242553, -3718, st.getPlayer()), 500L);
                    else if (talker_occupation == elven_fighter)
                        ThreadPoolManager.getInstance().schedule(new RadarTask(45061, 52468, -2796, st.getPlayer()), 500L);
                    else if (talker_occupation == dark_fighter)
                        ThreadPoolManager.getInstance().schedule(new RadarTask(10447, 14620, -4242, st.getPlayer()), 500L);
                    else if (talker_occupation == orc_fighter)
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-46389, -113905, -21, st.getPlayer()), 500L);
                    else if (talker_occupation == dwarven_fighter)
                        ThreadPoolManager.getInstance().schedule(new RadarTask(115271, -182692, -1445, st.getPlayer()), 500L);
                    else if (talker_occupation == kamael_m_soldier || talker_occupation == kamael_f_soldier)
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-118132, 42788, 723, st.getPlayer()), 500L);
                    if (!st.getPlayer().getPlayerClassComponent().getClassId().isMage() || talker_occupation == orc_mage) {
                        st.playTutorialVoice("tutorial_voice_014");
                        st.showQuestionMark(9);
                        st.soundEffect(SOUND_TUTORIAL);
                    }
                    st.onTutorialClientEvent(i0 | TUTORIAL_LEVEL_UP_6);
                    break;
                case TUTORIAL_LEVEL_UP_6:
                    st.showQuestionMark(24);
                    st.playTutorialVoice("tutorial_voice_020");
                    st.soundEffect(SOUND_TUTORIAL);
                    st.onTutorialClientEvent(i0 & ~134217728);
                    st.setMemoState("tutorial_quest", String.valueOf(i1 & ~TUTORIAL_LEVEL_UP_6));
                    st.onTutorialClientEvent(i0 | TUTORIAL_LEVEL_UP_7);
                    break;
                case TUTORIAL_LEVEL_UP_7:
                    if (st.getPlayer().getPlayerClassComponent().getClassId().isMage()) {
                        st.playTutorialVoice("tutorial_voice_019");
                        st.showQuestionMark(11);
                        st.soundEffect(SOUND_TUTORIAL);
                        if (talker_occupation == mage)
                            ThreadPoolManager.getInstance().schedule(new RadarTask(-84981, 244764, -3726, st.getPlayer()), 500L);
                        else if (talker_occupation == elven_mage)
                            ThreadPoolManager.getInstance().schedule(new RadarTask(45701, 52459, -2796, st.getPlayer()), 500L);
                        else if (talker_occupation == dark_mage)
                            ThreadPoolManager.getInstance().schedule(new RadarTask(10344, 14445, -4242, st.getPlayer()), 500L);
                        else if (talker_occupation == orc_mage)
                            ThreadPoolManager.getInstance().schedule(new RadarTask(-46225, -113312, -21, st.getPlayer()), 500L);
                        st.setMemoState("tutorial_quest", String.valueOf(i1 & ~TUTORIAL_LEVEL_UP_7));
                    }
                    st.onTutorialClientEvent(i0 | TUTORIAL_LEVEL_UP_9);
                    break;
                case TUTORIAL_LEVEL_UP_9:
                    if (talker_occupation == fighter) {
                        st.playTutorialVoice("tutorial_voice_021");
                        st.showQuestionMark(25);
                        st.soundEffect(SOUND_TUTORIAL);
                        st.setMemoState("tutorial_quest", String.valueOf(i1 & ~TUTORIAL_LEVEL_UP_9));
                    }
                    st.onTutorialClientEvent(i0 | TUTORIAL_LEVEL_UP_10);
                    break;
                case TUTORIAL_LEVEL_UP_10:
                    if (talker_occupation == dwarven_fighter || talker_occupation == mage || talker_occupation == elven_fighter || talker_occupation == elven_mage || talker_occupation == dark_mage || talker_occupation == dark_fighter || talker_occupation == kamael_m_soldier || talker_occupation == kamael_f_soldier) {
                        st.playTutorialVoice("tutorial_voice_021");
                        st.showQuestionMark(25);
                        st.soundEffect(SOUND_TUTORIAL);
                        st.setMemoState("tutorial_quest", String.valueOf(i1 & ~TUTORIAL_LEVEL_UP_10));
                    } else {
                        st.playTutorialVoice("tutorial_voice_030");
                        st.showQuestionMark(27);
                        st.soundEffect(SOUND_TUTORIAL);
                        st.setMemoState("tutorial_quest", String.valueOf(i1 & ~TUTORIAL_LEVEL_UP_10));
                    }
                    st.onTutorialClientEvent(i0 | TUTORIAL_LEVEL_UP_12);
                    break;
                case TUTORIAL_LEVEL_UP_12:
                    if (talker_occupation == orc_fighter || talker_occupation == orc_mage) {
                        st.playTutorialVoice("tutorial_voice_021");
                        st.showQuestionMark(25);
                        st.soundEffect(SOUND_TUTORIAL);
                        st.setMemoState("tutorial_quest", String.valueOf(i1 & ~TUTORIAL_LEVEL_UP_12));
                    }
                    break;
                case TUTORIAL_LEVEL_UP_15:
                    st.showQuestionMark(17);
                    st.soundEffect(SOUND_TUTORIAL);
                    st.setMemoState("tutorial_quest", String.valueOf(i1 & ~TUTORIAL_LEVEL_UP_15));
                    st.onTutorialClientEvent(i0 | TUTORIAL_LEVEL_UP_20);
                    break;
                case TUTORIAL_LEVEL_UP_20:
                    st.showQuestionMark(13);
                    st.soundEffect(SOUND_TUTORIAL);
                    st.setMemoState("tutorial_quest", String.valueOf(i1 & ~TUTORIAL_LEVEL_UP_20));
                    st.onTutorialClientEvent(i0 | TUTORIAL_LEVEL_UP_35);
                    break;
                case TUTORIAL_LEVEL_UP_35:
                    if (st.getPlayer().getPlayerTemplateComponent().getPlayerRace() != PlayerRace.kamael) {
                        st.playTutorialVoice("tutorial_voice_023");
                        st.showQuestionMark(15);
                        st.soundEffect(SOUND_TUTORIAL);
                        st.setMemoState("tutorial_quest", String.valueOf(i1 & ~TUTORIAL_LEVEL_UP_35));
                    }
                    st.onTutorialClientEvent(i0 | TUTORIAL_LEVEL_UP_36);
                    break;
                case TUTORIAL_LEVEL_UP_39:
                    if (st.getPlayer().getPlayerTemplateComponent().getPlayerRace() == PlayerRace.kamael && st.getPlayer().getPlayerClassComponent().getClassId().level() == 1) {
                        st.playTutorialVoice("tutorial_voice_028");
                        st.showQuestionMark(15);
                        st.soundEffect(SOUND_TUTORIAL);
                        st.setMemoState("tutorial_quest", String.valueOf(i1 & ~TUTORIAL_LEVEL_UP_39));
                    }
                    st.onTutorialClientEvent(i0 | TUTORIAL_LEVEL_UP_61);
                    break;
                case TUTORIAL_LEVEL_UP_75:
                    if (st.getPlayer().getQuestState(234) == null) {
                        st.playTutorialVoice("tutorial_voice_024");
                        st.showQuestionMark(16);
                        st.soundEffect(SOUND_TUTORIAL);
                        st.setMemoState("tutorial_quest", String.valueOf(i1 & ~TUTORIAL_LEVEL_UP_75));
                    }
                    st.onTutorialClientEvent(i0 | TUTORIAL_LEVEL_UP_76);
                    break;
                case TUTORIAL_LEVEL_UP_76:
                    if (st.getPlayer().getQuestState(234) != null && st.getPlayer().getQuestState(234).isCompleted()) // FIXME[K] - возможно тут не так нужно, хз (для Magister)
                    {
                        st.showQuestionMark(29);
                        st.soundEffect(SOUND_TUTORIAL);
                        st.setMemoState("tutorial_quest", String.valueOf(i1 & ~TUTORIAL_LEVEL_UP_76));
                    }
                    break;
                case TUTORIAL_LEVEL_UP_36:
                    if (st.getPlayer().getQuestState(128) == null) {
                        st.showQuestionMark(30);
                        st.soundEffect(SOUND_TUTORIAL);
                        st.setMemoState("tutorial_quest", String.valueOf(i1 & ~TUTORIAL_LEVEL_UP_36));
                    }
                    st.onTutorialClientEvent(i0 | TUTORIAL_LEVEL_UP_39);
                    break;
                case TUTORIAL_LEVEL_UP_61:
                    if (st.getPlayer().getQuestState(129) == null) {
                        st.showQuestionMark(31);
                        st.soundEffect(SOUND_TUTORIAL);
                        st.setMemoState("tutorial_quest", String.valueOf(i1 & ~TUTORIAL_LEVEL_UP_61));
                    }
                    st.onTutorialClientEvent(i0 | TUTORIAL_LEVEL_UP_73);
                    break;
                case TUTORIAL_LEVEL_UP_73:
                    if (st.getPlayer().getQuestState(144) == null) {
                        st.showQuestionMark(32);
                        st.soundEffect(SOUND_TUTORIAL);
                        st.setMemoState("tutorial_quest", String.valueOf(i1 & ~TUTORIAL_LEVEL_UP_73));
                    }
                    st.onTutorialClientEvent(i0 | TUTORIAL_LEVEL_UP_75);
                    break;
                case TUTORIAL_PICK_ADENA:
                    if (talker_level < 6) {
                        st.showQuestionMark(23);
                        st.playTutorialVoice("tutorial_voice_012");
                        st.soundEffect(SOUND_TUTORIAL);
                        st.setMemoState("tutorial_quest", String.valueOf(i1 & ~TUTORIAL_PICK_ADENA));
                    }
                    break;
                case TUTORIAL_PICK_BLUE_GEM:
                    if (talker_level < 6) {
                        st.showQuestionMark(5);
                        st.playTutorialVoice("tutorial_voice_013");
                        st.soundEffect(SOUND_TUTORIAL);
                        st.setMemoState("tutorial_quest", String.valueOf(i1 & ~TUTORIAL_PICK_BLUE_GEM));
                    }
                    break;
                case TUTORIAL_SIT_DOWN: // проверить работу!
                    if (talker_level < 6) {
                        st.playTutorialVoice("tutorial_voice_018");
                        st.showTutorialHTML("tutorial_21z.htm");
                        st.setMemoState("tutorial_quest", String.valueOf(i1 & ~TUTORIAL_SIT_DOWN));
                    }
                    break;
            }
        }
        // Question mark clicked
        else if (event.startsWith("QM")) {
            i0 = GetMemoState & 2147483392;
            int MarkId = Integer.valueOf(event.substring(2));
            switch (MarkId) {
                case 1:
                    st.playTutorialVoice("tutorial_voice_007");
                    if (GetMemoStateEx < 0) {
                        st.setMemoState("tutorial_quest_ex", "-5");
                    }
                    if (talker_occupation == fighter) {
                        st.showTutorialHTML("tutorial_human_fighter007.htm");
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-71424, 258336, -3109, st.getPlayer()), 500L);
                    } else if (talker_occupation == mage) {
                        st.showTutorialHTML("tutorial_human_fighter007.htm");
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-91036, 248044, -3568, st.getPlayer()), 500L);
                    } else if (talker_occupation == elven_fighter || talker_occupation == elven_mage) {
                        st.showTutorialHTML("tutorial_human_fighter007.htm");
                        ThreadPoolManager.getInstance().schedule(new RadarTask(46112, 41200, -3504, st.getPlayer()), 500L);
                    } else if (talker_occupation == dark_fighter || talker_occupation == dark_mage) {
                        st.showTutorialHTML("tutorial_human_fighter007.htm");
                        ThreadPoolManager.getInstance().schedule(new RadarTask(28384, 11056, -4233, st.getPlayer()), 500L);
                    } else if (talker_occupation == orc_fighter || talker_occupation == orc_mage) {
                        st.showTutorialHTML("tutorial_human_fighter007.htm");
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-56736, -113680, -672, st.getPlayer()), 500L);
                    } else if (talker_occupation == dwarven_fighter) {
                        st.showTutorialHTML("tutorial_human_fighter007.htm");
                        ThreadPoolManager.getInstance().schedule(new RadarTask(108567, -173994, -406, st.getPlayer()), 500L);
                    } else if (talker_occupation == kamael_m_soldier || talker_occupation == kamael_f_soldier) {
                        st.showTutorialHTML("tutorial_human_fighter007.htm");
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-125872, 38016, 1251, st.getPlayer()), 500L);
                    }
                    st.setMemoState("tutorial_quest", String.valueOf(i0 | 2));
                    break;
                case 2:
                    if (talker_occupation == fighter)
                        st.showTutorialHTML("tutorial_human_fighter008.htm");
                    else if (talker_occupation == mage)
                        st.showTutorialHTML("tutorial_human_mage008.htm");
                    else if (talker_occupation == elven_fighter || talker_occupation == elven_mage)
                        st.showTutorialHTML("tutorial_elf008.htm");
                    else if (talker_occupation == dark_fighter || talker_occupation == dark_mage)
                        st.showTutorialHTML("tutorial_delf008.htm");
                    else if (talker_occupation == orc_fighter || talker_occupation == orc_mage)
                        st.showTutorialHTML("tutorial_orc008.htm");
                    else if (talker_occupation == dwarven_fighter)
                        st.showTutorialHTML("tutorial_dwarven_fighter008.htm");
                    else if (talker_occupation == kamael_m_soldier || talker_occupation == kamael_f_soldier)
                        st.showTutorialHTML("tutorial_kamael008.htm");
                    break;
                case 3:
                    st.showTutorialHTML("tutorial_09.htm");
                    st.onTutorialClientEvent(i0 | TUTORIAL_PICK_BLUE_GEM);
                    break;
                case 4:
                    st.showTutorialHTML("tutorial_10.htm");
                    break;
                case 5:
                    if (talker_occupation == fighter)
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-71424, 258336, -3109, st.getPlayer()), 500L);
                    else if (talker_occupation == mage)
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-91036, 248044, -3568, st.getPlayer()), 500L);
                    else if (talker_occupation == elven_fighter || talker_occupation == elven_mage)
                        ThreadPoolManager.getInstance().schedule(new RadarTask(46112, 41200, -3504, st.getPlayer()), 500L);
                    else if (talker_occupation == dark_fighter || talker_occupation == dark_mage)
                        ThreadPoolManager.getInstance().schedule(new RadarTask(28384, 11056, -4233, st.getPlayer()), 500L);
                    else if (talker_occupation == orc_fighter || talker_occupation == orc_mage)
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-56736, -113680, -672, st.getPlayer()), 500L);
                    else if (talker_occupation == dwarven_fighter)
                        ThreadPoolManager.getInstance().schedule(new RadarTask(108567, -173994, -406, st.getPlayer()), 500L);
                    else if (talker_occupation == kamael_m_soldier || talker_occupation == kamael_f_soldier)
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-125872, 38016, 1251, st.getPlayer()), 500L);
                    st.showTutorialHTML("tutorial_11.htm");
                    break;
                case 7:
                    st.showTutorialHTML("tutorial_15.htm");
                    st.setMemoState("tutorial_quest", String.valueOf(i0 | 5));
                    break;
                case 8:
                    st.showTutorialHTML("tutorial_18.htm");
                    break;
                case 9:
                    if ((st.getPlayer().getPlayerTemplateComponent().getPlayerRace() == PlayerRace.human || st.getPlayer().getPlayerTemplateComponent().getPlayerRace() == PlayerRace.elf || st.getPlayer().getPlayerTemplateComponent().getPlayerRace() == PlayerRace.darkelf) && st.getPlayer().getPlayerClassComponent().getClassId().level() == 0)
                        st.showTutorialHTML("tutorial_fighter017.htm");
                    if (st.getPlayer().getPlayerTemplateComponent().getPlayerRace() == PlayerRace.dwarf && st.getPlayer().getPlayerClassComponent().getClassId().level() == 0)
                        st.showTutorialHTML("tutorial_fighter_dwarf017.htm");
                    if (st.getPlayer().getPlayerTemplateComponent().getPlayerRace() == PlayerRace.orc && st.getPlayer().getPlayerClassComponent().getClassId().level() == 0)
                        st.showTutorialHTML("tutorial_fighter_orc017.htm");
                    if (st.getPlayer().getPlayerTemplateComponent().getPlayerRace() == PlayerRace.kamael)
                        st.showTutorialHTML("tutorial_kamael017.htm");
                    break;
                case 10:
                    st.showTutorialHTML("tutorial_19.htm");
                    break;
                case 11:
                    if (st.getPlayer().getPlayerTemplateComponent().getPlayerRace() == PlayerRace.human)
                        st.showTutorialHTML("tutorial_mage020.htm");
                    if (st.getPlayer().getPlayerTemplateComponent().getPlayerRace() == PlayerRace.elf || st.getPlayer().getPlayerTemplateComponent().getPlayerRace() == PlayerRace.darkelf)
                        st.showTutorialHTML("tutorial_mage_elf020.htm");
                    if (st.getPlayer().getPlayerTemplateComponent().getPlayerRace() == PlayerRace.orc)
                        st.showTutorialHTML("tutorial_mage_orc020.htm");
                    break;
                case 12:
                    st.showTutorialHTML("tutorial_15.htm");
                    break;
                case 13:
                    if (talker_occupation == fighter)
                        st.showTutorialHTML("tutorial_21.htm");
                    else if (talker_occupation == mage)
                        st.showTutorialHTML("tutorial_21a.htm");
                    else if (talker_occupation == elven_fighter)
                        st.showTutorialHTML("tutorial_21b.htm");
                    else if (talker_occupation == elven_mage)
                        st.showTutorialHTML("tutorial_21c.htm");
                    else if (talker_occupation == orc_fighter)
                        st.showTutorialHTML("tutorial_21d.htm");
                    else if (talker_occupation == orc_mage)
                        st.showTutorialHTML("tutorial_21e.htm");
                    else if (talker_occupation == dwarven_fighter)
                        st.showTutorialHTML("tutorial_21f.htm");
                    else if (talker_occupation == dark_fighter)
                        st.showTutorialHTML("tutorial_21g.htm");
                    else if (talker_occupation == dark_mage)
                        st.showTutorialHTML("tutorial_21h.htm");
                    else if (talker_occupation == kamael_m_soldier)
                        st.showTutorialHTML("tutorial_21i.htm");
                    else if (talker_occupation == kamael_f_soldier)
                        st.showTutorialHTML("tutorial_21j.htm");
                    break;
                case 15:
                    if (st.getPlayer().getPlayerTemplateComponent().getPlayerRace() != PlayerRace.kamael)
                        st.showTutorialHTML("tutorial_28.htm");
                    else if (talker_occupation == trooper)
                        st.showTutorialHTML("tutorial_28a.htm");
                    else if (talker_occupation == warder)
                        st.showTutorialHTML("tutorial_28b.htm");
                    break;
                case 16:
                    st.showTutorialHTML("tutorial_30.htm");
                    break;
                case 17:
                    st.showTutorialHTML("tutorial_27.htm");
                    break;
                case 19: // FIXME: не используется.
                    st.showTutorialHTML("tutorial_07.htm");
                    break;
                case 20: // FIXME: не используется.
                    st.showTutorialHTML("tutorial_14.htm");
                    break;
                case 21: // FIXME: не используется. и нет такой htm!
                    st.showTutorialHTML("tutorial_newbie001.htm");
                    break;
                case 22: // FIXME: не используется.
                    st.showTutorialHTML("tutorial_14.htm");
                    break;
                case 23:
                    st.showTutorialHTML("tutorial_24.htm");
                    break;
                case 24:
                    if (st.getPlayer().getPlayerTemplateComponent().getPlayerRace() == PlayerRace.human)
                        st.showTutorialHTML("tutorial_newbie003a.htm");
                    if (st.getPlayer().getPlayerTemplateComponent().getPlayerRace() == PlayerRace.elf)
                        st.showTutorialHTML("tutorial_newbie003b.htm");
                    if (st.getPlayer().getPlayerTemplateComponent().getPlayerRace() == PlayerRace.darkelf)
                        st.showTutorialHTML("tutorial_newbie003c.htm");
                    if (st.getPlayer().getPlayerTemplateComponent().getPlayerRace() == PlayerRace.orc)
                        st.showTutorialHTML("tutorial_newbie003d.htm");
                    if (st.getPlayer().getPlayerTemplateComponent().getPlayerRace() == PlayerRace.dwarf)
                        st.showTutorialHTML("tutorial_newbie003e.htm");
                    if (st.getPlayer().getPlayerTemplateComponent().getPlayerRace() == PlayerRace.kamael)
                        st.showTutorialHTML("tutorial_newbie003f.htm");
                    break;
                case 25:
                    if (talker_occupation == fighter)
                        st.showTutorialHTML("tutorial_newbie002a.htm");
                    if (talker_occupation == mage)
                        st.showTutorialHTML("tutorial_newbie002b.htm");
                    if (talker_occupation == elven_fighter || talker_occupation == elven_mage)
                        st.showTutorialHTML("tutorial_newbie002c.htm");
                    if (talker_occupation == dark_mage)
                        st.showTutorialHTML("tutorial_newbie002d.htm");
                    if (talker_occupation == dark_fighter)
                        st.showTutorialHTML("tutorial_newbie002e.htm");
                    if (talker_occupation == dwarven_fighter)
                        st.showTutorialHTML("tutorial_newbie002g.htm");
                    if (talker_occupation == orc_mage || talker_occupation == orc_fighter)
                        st.showTutorialHTML("tutorial_newbie002f.htm");
                    if (talker_occupation == kamael_m_soldier || talker_occupation == kamael_f_soldier)
                        st.showTutorialHTML("tutorial_newbie002i.htm");
                    break;
                case 26:
                    if (st.getPlayer().getPlayerClassComponent().getClassId().level() == 0 || talker_occupation == orc_mage)
                        st.showTutorialHTML("tutorial_newbie004a.htm");
                    else
                        st.showTutorialHTML("tutorial_newbie004b.htm");
                    break;
                case 27:
                    if (talker_occupation == fighter || talker_occupation == orc_mage || talker_occupation == orc_fighter)
                        st.showTutorialHTML("tutorial_newbie002h.htm");
                    break;
                case 28: // FIXME: не используется.
                    st.showTutorialHTML("tutorial_31.htm");
                    break;
                case 29:
                    st.showTutorialHTML("tutorial_32.htm");
                    break;
                case 30:
                    st.showTutorialHTML("tutorial_33.htm");
                    break;
                case 31:
                    st.showTutorialHTML("tutorial_34.htm");
                    break;
                case 32:
                    st.showTutorialHTML("tutorial_35.htm");
                    break;
                case 33:
                    if (talker_level == 18) {
                        st.showTutorialHTML("kanooth_gludio.htm");
                    }
                    if (talker_level == 28) {
                        st.showTutorialHTML("kanooth_dion.htm");
                    }
                    if (talker_level == 38) {
                        st.showTutorialHTML("kanooth_heiness.htm");
                    }
                    if (talker_level == 48) {
                        st.showTutorialHTML("kanooth_oren.htm");
                    }
                    if (talker_level == 58) {
                        st.showTutorialHTML("kanooth_shuttgart.htm");
                    }
                    if (talker_level == 68) {
                        st.showTutorialHTML("kanooth_rune.htm");
                    }
                    break;
                case 34:
                    if (talker_level == 79)
                        st.showTutorialHTML("ssq_tutorial_q0192_02.htm");
                    break;
            }
        } else if (event.equalsIgnoreCase("reply_1"))
            st.showTutorialHTML("tutorial_22g.htm");
        else if (event.equalsIgnoreCase("reply_2"))
            st.showTutorialHTML("tutorial_22w.htm");
        else if (event.equalsIgnoreCase("reply_3"))
            st.showTutorialHTML("tutorial_22ap.htm");
        else if (event.equalsIgnoreCase("reply_4"))
            st.showTutorialHTML("tutorial_22ad.htm");
        else if (event.equalsIgnoreCase("reply_5"))
            st.showTutorialHTML("tutorial_22bt.htm");
        else if (event.equalsIgnoreCase("reply_6"))
            st.showTutorialHTML("tutorial_22bh.htm");
        else if (event.equalsIgnoreCase("reply_7"))
            st.showTutorialHTML("tutorial_22cs.htm");
        else if (event.equalsIgnoreCase("reply_8"))
            st.showTutorialHTML("tutorial_22cn.htm");
        else if (event.equalsIgnoreCase("reply_9"))
            st.showTutorialHTML("tutorial_22cw.htm");
        else if (event.equalsIgnoreCase("reply_10"))
            st.showTutorialHTML("tutorial_22db.htm");
        else if (event.equalsIgnoreCase("reply_11"))
            st.showTutorialHTML("tutorial_22dp.htm");
        else if (event.equalsIgnoreCase("reply_12"))
            st.showTutorialHTML("tutorial_22et.htm");
        else if (event.equalsIgnoreCase("reply_13"))
            st.showTutorialHTML("tutorial_22es.htm");
        else if (event.equalsIgnoreCase("reply_14"))
            st.showTutorialHTML("tutorial_22fp.htm");
        else if (event.equalsIgnoreCase("reply_15"))
            st.showTutorialHTML("tutorial_22fs.htm");
        else if (event.equalsIgnoreCase("reply_16"))
            st.showTutorialHTML("tutorial_22gs.htm");
        else if (event.equalsIgnoreCase("reply_17"))
            st.showTutorialHTML("tutorial_22ge.htm");
        else if (event.equalsIgnoreCase("reply_18"))
            st.showTutorialHTML("tutorial_22ko.htm");
        else if (event.equalsIgnoreCase("reply_19"))
            st.showTutorialHTML("tutorial_22kw.htm");
        else if (event.equalsIgnoreCase("reply_20"))
            st.showTutorialHTML("tutorial_22ns.htm");
        else if (event.equalsIgnoreCase("reply_21"))
            st.showTutorialHTML("tutorial_22nb.htm");
        else if (event.equalsIgnoreCase("reply_22"))
            st.showTutorialHTML("tutorial_22oa.htm");
        else if (event.equalsIgnoreCase("reply_23"))
            st.showTutorialHTML("tutorial_22op.htm");
        else if (event.equalsIgnoreCase("reply_24"))
            st.showTutorialHTML("tutorial_22ps.htm");
        else if (event.equalsIgnoreCase("reply_24"))
            st.showTutorialHTML("tutorial_22pp.htm");
        else if (event.equalsIgnoreCase("reply_26")) {
            if (talker_occupation == warrior)
                st.showTutorialHTML("tutorial_22.htm");
            else if (talker_occupation == knight)
                st.showTutorialHTML("tutorial_22a.htm"); // FIXME: нет htm!
            else if (talker_occupation == rogue)
                st.showTutorialHTML("tutorial_22b.htm");
            else if (talker_occupation == wizard)
                st.showTutorialHTML("tutorial_22c.htm");
            else if (talker_occupation == cleric)
                st.showTutorialHTML("tutorial_22d.htm");
            else if (talker_occupation == elven_knight)
                st.showTutorialHTML("tutorial_22e.htm");
            else if (talker_occupation == elven_scout)
                st.showTutorialHTML("tutorial_22f.htm");
            else if (talker_occupation == elven_wizard)
                st.showTutorialHTML("tutorial_22g.htm");
            else if (talker_occupation == oracle)
                st.showTutorialHTML("tutorial_22h.htm");
            else if (talker_occupation == orc_raider)
                st.showTutorialHTML("tutorial_22i.htm");
            else if (talker_occupation == orc_monk)
                st.showTutorialHTML("tutorial_22j.htm");
            else if (talker_occupation == orc_shaman)
                st.showTutorialHTML("tutorial_22k.htm");
            else if (talker_occupation == scavenger)
                st.showTutorialHTML("tutorial_22l.htm");
            else if (talker_occupation == artisan)
                st.showTutorialHTML("tutorial_22m.htm");
            else if (talker_occupation == palus_knight)
                st.showTutorialHTML("tutorial_22n.htm");
            else if (talker_occupation == assassin)
                st.showTutorialHTML("tutorial_22o.htm");
            else if (talker_occupation == dark_wizard)
                st.showTutorialHTML("tutorial_22p.htm");
            else if (talker_occupation == shillien_oracle)
                st.showTutorialHTML("tutorial_22q.htm");
            else
                st.showTutorialHTML("tutorial_22qe.htm");
        } else if (event.equalsIgnoreCase("reply_27"))
            st.showTutorialHTML("tutorial_29.htm");
        else if (event.equalsIgnoreCase("reply_28"))
            st.showTutorialHTML("tutorial_28.htm");
        else if (event.equalsIgnoreCase("reply_29"))
            st.showTutorialHTML("tutorial_07a.htm");
        else if (event.equalsIgnoreCase("reply_30"))
            st.showTutorialHTML("tutorial_07b.htm");
        else if (event.equalsIgnoreCase("reply_31")) {
            if (talker_occupation == trooper)
                st.showTutorialHTML("tutorial_28a.htm");
            else if (talker_occupation == warder)
                st.showTutorialHTML("tutorial_28b.htm");
        } else if (event.equalsIgnoreCase("reply_32"))
            st.showTutorialHTML("tutorial_22qa.htm");
        else if (event.equalsIgnoreCase("reply_33")) {
            if (talker_occupation == trooper)
                st.showTutorialHTML("tutorial_22qb.htm");
            else if (talker_occupation == warder)
                st.showTutorialHTML("tutorial_22qc.htm");
        } else if (event.equalsIgnoreCase("reply_34"))
            st.showTutorialHTML("tutorial_22qd.htm");
        if (html.isEmpty()) {
            return null;
        }
        st.showTutorialHTML(html);
        return null;
    }

    @Override
    public boolean isVisible() {
        return false;
    }

    public static class OnPlayerEnterListenerImpl implements OnPlayerEnterListener {
        @Override
        public void onPlayerEnter(Player player) {
            final Quest q = QuestManager.getQuest(255);
            if (q != null) {
                if (player.getQuestState(255) == null) {
                    q.newQuestState(player, Quest.STARTED);
                    player.processQuestEvent(255, "UC", null);
                } else
                    player.processQuestEvent(255, "UC", null);
                if (player.getLevel() < 6)
                    player.addListener(_tutorialShowListener);
            }
        }
    }

    public static class OnLevelUpListenerImpl implements OnLevelUpListener {
        @Override
        public void onLevelUp(Player player, int level) {
            QuestState qs = player.getQuestState(255);
            if (qs == null) {
                return;
            }
            switch (level) {
                case 5:
                    player.processQuestEvent(255, "CE" + TUTORIAL_LEVEL_UP_5, null);
                    break;
                case 6:
                    player.processQuestEvent(255, "CE" + TUTORIAL_LEVEL_UP_6, null);
                    break;
                case 7:
                    player.processQuestEvent(255, "CE" + TUTORIAL_LEVEL_UP_7, null);
                    break;
                case 9:
                    player.processQuestEvent(255, "CE" + TUTORIAL_LEVEL_UP_9, null);
                    break;
                case 10:
                    player.processQuestEvent(255, "CE" + TUTORIAL_LEVEL_UP_10, null);
                    break;
                case 12:
                    player.processQuestEvent(255, "CE" + TUTORIAL_LEVEL_UP_12, null);
                    break;
                case 15:
                    player.processQuestEvent(255, "CE" + TUTORIAL_LEVEL_UP_15, null);
                    break;
                case 18:
                    qs.showQuestionMark(33);
                    qs.soundEffect(SOUND_TUTORIAL);
                    break;
                case 20:
                    player.processQuestEvent(255, "CE" + TUTORIAL_LEVEL_UP_20, null);
                    break;
                case 28:
                    qs.showQuestionMark(33);
                    qs.soundEffect(SOUND_TUTORIAL);
                    break;
                case 35:
                    player.processQuestEvent(255, "CE" + TUTORIAL_LEVEL_UP_35, null);
                    break;
                case 36:
                    player.processQuestEvent(255, "CE" + TUTORIAL_LEVEL_UP_36, null);
                    break;
                case 38:
                    qs.showQuestionMark(33);
                    qs.soundEffect(SOUND_TUTORIAL);
                    break;
                case 39:
                    player.processQuestEvent(255, "CE" + TUTORIAL_LEVEL_UP_39, null);
                    break;
                case 48:
                    qs.showQuestionMark(33);
                    qs.soundEffect(SOUND_TUTORIAL);
                    break;
                case 58:
                    qs.showQuestionMark(33);
                    qs.soundEffect(SOUND_TUTORIAL);
                    break;
                case 61:
                    player.processQuestEvent(255, "CE" + TUTORIAL_LEVEL_UP_61, null);
                    break;
                case 68:
                    qs.showQuestionMark(33);
                    qs.soundEffect(SOUND_TUTORIAL);
                    break;
                case 73:
                    player.processQuestEvent(255, "CE" + TUTORIAL_LEVEL_UP_73, null);
                    break;
                case 75:
                    player.processQuestEvent(255, "CE" + TUTORIAL_LEVEL_UP_75, null);
                    break;
                case 76:
                    player.processQuestEvent(255, "CE" + TUTORIAL_LEVEL_UP_76, null);
                    break;
                case 79:
                    qs.showQuestionMark(34);
                    qs.soundEffect(SOUND_TUTORIAL);
                    break;
            }
        }
    }

    public static class TutorialShowListener implements OnCurrentHpDamageListener {
        @Override
        public void onCurrentHpDamage(Creature actor, double damage, Creature attacker, SkillEntry skill) {
            Player player = actor.getPlayer();
            if (player.getCurrentHpPercents() < 25) {
                player.removeListener(_tutorialShowListener);
                player.processQuestEvent(255, "CE" + TUTORIAL_HP_UNDER_1over3, null);
            } else if (player.getLevel() > 5) {
                player.removeListener(_tutorialShowListener);
            }
        }
    }

    public static class OnItemPickupListenerImpl implements OnItemPickupListener {
        @Override
        public void onItemPickup(final Player player, final ItemInstance item) {
            if (item.getItemId() == tutorial_blue_gem || item.getItemId() == ItemTemplate.ITEM_ID_ADENA) {
                if (item.getItemId() == ItemTemplate.ITEM_ID_ADENA && !player.getPlayerVariables().getBoolean(PlayerVariables.QUEST_STATE_OF_255_ADENA_GIVEN)) {
                    player.processQuestEvent(255, "CE" + TUTORIAL_PICK_ADENA, null);
                    player.getPlayerVariables().set(PlayerVariables.QUEST_STATE_OF_255_ADENA_GIVEN, "true", -1);
                }
                if (item.getItemId() == tutorial_blue_gem && !player.getPlayerVariables().getBoolean(PlayerVariables.QUEST_STATE_OF_255_BLUE_GEM_GIVEN)) {
                    player.processQuestEvent(255, "CE" + TUTORIAL_PICK_BLUE_GEM, null);
                    player.getPlayerVariables().set(PlayerVariables.QUEST_STATE_OF_255_BLUE_GEM_GIVEN, "true", -1);
                }
            }
        }
    }

    private class RadarTask extends RunnableImpl {
        private final int x, y, z;
        private final Player player;

        RadarTask(final int x, final int y, final int z, final Player player) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.player = player;
        }

        @Override
        public void runImpl() throws Exception {
            player.sendPacket(new RadarControl(RadarControl.RadarState.SHOW_RADAR, RadarControl.RadarType.ARROW, x, y, z));
        }
    }

    public class OnDeathListenerImpl implements OnDeathListener {
        @Override
        public void onDeath(final Creature actor, final Creature killer) {
            if (actor.getPlayer() != null && actor.getPlayer().getLevel() < 9)
                actor.getPlayer().processQuestEvent(255, "CE" + TUTORIAL_DIE_1_9, null);
        }
    }
}