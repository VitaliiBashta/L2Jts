package org.mmocore.gameserver.scripts.ai.pts.events;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.MagicSkillUse;
import org.mmocore.gameserver.network.lineage.serverpackets.SocialAction;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.scripts.events.NcSoft.JackGame.JackGame;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.ChatUtils;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * @author KilRoy
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class ai_br_wooldie extends DefaultAI {
    public static final int WAIT_STATE1 = 20090801;
    public static final int WAIT_STATE2 = 20090802;
    public static final int WAIT_STATE3 = 20090803;
    public static final int WAIT_STATE4 = 20090804;
    public static final int FAIL_STATE1 = 20090811;
    public static final int FAIL_STATE2 = 20090812;
    public static final int GAME_STATE1 = 20090821;
    public static final int GAME_STATE2 = 20090822;
    public static final int GAME_STATE3 = 20090823;
    public static final int GAME_STATE4 = 20090824;
    public static final int GAME_STATE5 = 20090825;
    public static final int GAME_WIN_STATE1 = 20090831;
    public static final int GAME_WIN_STATE2 = 20090832;
    public static final int GAME_WIN_STATE3 = 20090833;

    public static final int s_br_halloween_gage = 23121;
    public static final int s_br_halloween_target = 23122;
    public static final int s_br_halloween_jack_ghost = 23092;
    public static final int s_br_halloween_rotten_jack_ghost = 23093;
    public static final int s_br_halloween_lose = 23096;
    public static final int s_br_halloween_buff = 23091;
    public static final int s_br_halloween_jack = 21120;
    public static final int s_br_halloween_jack_ghost_b = 23094;
    public static final int s_br_halloween_rotten_jack = 21121;
    public static final int s_br_halloween_rotten_jack_ghost_b = 23095;
    public static final int s_br_halloween_pcwins1 = 23097;
    public static final int s_br_halloween_pcwins2 = 23098;
    public static final int s_br_halloween_pcwins3 = 23099;
    public static final int s_br_halloween_pcwins4 = 23100;
    public static final int s_br_halloween_pcwins5 = 23101;
    public static final int s_br_halloween_pcwins6 = 23102;
    public static final int s_br_halloween_pcwins7 = 23103;
    public static final int s_br_halloween_pcwins8 = 23104;
    public static final int s_br_halloween_pcwins9 = 23105;
    public static final int s_br_halloween_pcwins10 = 23106;
    public static final int s_br_halloween_pcwins11 = 23107;
    public static final int s_br_halloween_pcwins12 = 23108;
    public static final int s_br_halloween_pcwins13 = 23109;
    public static final int s_br_halloween_pcwins14 = 23110;
    public static final int s_br_halloween_pcwins15 = 23111;
    public static final int s_br_halloween_pcwins16 = 23112;
    public static final int s_br_halloween_pcwins17 = 23113;
    public static final int s_br_halloween_pcwins18 = 23114;
    public static final int s_br_halloween_pcwins19 = 23115;
    public static final int s_br_halloween_pcwins20 = 23116;

    private static final int br_vitality_potion_25charge = 20392;

    protected int FSTRING_VALUE = 0;

    private int i_ai0 = 0;
    private int i_ai1 = 0;
    private int i_ai2 = 0;
    private int i_ai3 = 0;
    private int i_ai4 = 0;

    public ai_br_wooldie(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtSpawn() {
        i_ai0 = 0;
        i_ai1 = 0;
        i_ai2 = 0;
        i_ai3 = 0;
        i_ai4 = 0;
        super.onEvtSpawn();
        getActor().setIsInvul(true);
        ChatUtils.say(getActor(), NpcString.valueOf(1900041 + FSTRING_VALUE));
        getActor().broadcastPacket(new MagicSkillUse(getActor(), getActor().getParam4(), s_br_halloween_target, 1, 0, 0));
        getActor().altOnMagicUseTimer(getActor().getParam4(), SkillTable.getInstance().getSkillEntry(s_br_halloween_target, 1));
        if (getActor().getNpcId() != 120) {
            AddTimerEx(WAIT_STATE1, 4000);
        }
    }

    @Override
    protected void onEvtTimerFiredEx(int timer_id, Object arg1, Object arg2) {
        final Player player = getActor().getParam4().getPlayer();
        if (getActor() == null) {
            return;
        }
        if (getActor().isDead() || player == null) {
            return;
        }
        if (player.isDead()) {
            return;
        }

        if (timer_id == WAIT_STATE1) {
            ChatUtils.say(getActor(), NpcString.valueOf(1900043 + FSTRING_VALUE));
            AddTimerEx(WAIT_STATE3, 1000);
        } else if (timer_id == WAIT_STATE2) {
            if (i_ai2 == 1) {
                if (Rnd.get(100) < 50) {
                    ChatUtils.say(getActor(), NpcString.valueOf(1900045 + FSTRING_VALUE));
                } else {
                    ChatUtils.say(getActor(), NpcString.valueOf(1900047 + FSTRING_VALUE));
                }
            }
        } else if (timer_id == WAIT_STATE3) {
            getActor().broadcastPacket(new MagicSkillUse(getActor(), player, s_br_halloween_gage, 1, 0, 0));
            getActor().altOnMagicUseTimer(player, SkillTable.getInstance().getSkillEntry(s_br_halloween_gage, 1));
            AddTimerEx(WAIT_STATE4, 950);
            if (i_ai3 != 0 && Rnd.get(100) < 40) {
                AddTimerEx(WAIT_STATE2, 3000);
            }
            AddTimerEx(GAME_STATE4, 7000);
        } else if (timer_id == WAIT_STATE4) {
            i_ai2 = 1;
        } else if (timer_id == FAIL_STATE1) {
            ChatUtils.say(getActor(), NpcString.valueOf(1900087 + FSTRING_VALUE));
            AddTimerEx(FAIL_STATE2, 3500);
        } else if (timer_id == FAIL_STATE2) {
            ChatUtils.say(getActor(), NpcString.valueOf(1900089 + FSTRING_VALUE));
            AddTimerEx(WAIT_STATE1, 3500);
        } else if (timer_id == GAME_STATE1) {
            if (JackGame.getEventValue() == 0) {
                i_ai0 = Rnd.get(20) % 2 + 1;
            } else if (JackGame.getEventValue() == 1) {
                i_ai0 = 1;
            } else if (JackGame.getEventValue() == 2) {
                i_ai0 = 2;
            } else {
                i_ai0 = Rnd.get(20) % 2 + 1;
            }
            if (i_ai0 == 1) {
                getActor().broadcastPacket(new MagicSkillUse(getActor(), player, s_br_halloween_jack_ghost, 1, 0, 0));
                getActor().altOnMagicUseTimer(getActor(), SkillTable.getInstance().getSkillEntry(s_br_halloween_jack_ghost, 1));
            } else if (i_ai0 == 2) {
                getActor().broadcastPacket(new MagicSkillUse(getActor(), player, s_br_halloween_rotten_jack_ghost, 1, 0, 0));
                getActor().altOnMagicUseTimer(getActor(), SkillTable.getInstance().getSkillEntry(s_br_halloween_rotten_jack_ghost, 1));
            }
            AddTimerEx(GAME_STATE2, 2000);
            AddTimerEx(GAME_STATE3, 3000);
        } else if (timer_id == GAME_STATE2) {
            if (i_ai0 == i_ai1) {
                i_ai3 += 1;
                switch (i_ai3) {
                    case 1:
                        i_ai4 = s_br_halloween_pcwins1;
                        break;
                    case 2:
                        i_ai4 = s_br_halloween_pcwins2;
                        break;
                    case 3:
                        i_ai4 = s_br_halloween_pcwins3;
                        break;
                    case 4:
                        i_ai4 = s_br_halloween_pcwins4;
                        break;
                    case 5:
                        i_ai4 = s_br_halloween_pcwins5;
                        break;
                    case 6:
                        i_ai4 = s_br_halloween_pcwins6;
                        break;
                    case 7:
                        i_ai4 = s_br_halloween_pcwins7;
                        break;
                    case 8:
                        i_ai4 = s_br_halloween_pcwins8;
                        break;
                    case 9:
                        i_ai4 = s_br_halloween_pcwins9;
                        break;
                    case 10:
                        i_ai4 = s_br_halloween_pcwins10;
                        break;
                    case 11:
                        i_ai4 = s_br_halloween_pcwins11;
                        break;
                    case 12:
                        i_ai4 = s_br_halloween_pcwins12;
                        break;
                    case 13:
                        i_ai4 = s_br_halloween_pcwins13;
                        break;
                    case 14:
                        i_ai4 = s_br_halloween_pcwins14;
                        break;
                    case 15:
                        i_ai4 = s_br_halloween_pcwins15;
                        break;
                    case 16:
                        i_ai4 = s_br_halloween_pcwins16;
                        break;
                    case 17:
                        i_ai4 = s_br_halloween_pcwins17;
                        break;
                    case 18:
                        i_ai4 = s_br_halloween_pcwins18;
                        break;
                    case 19:
                        i_ai4 = s_br_halloween_pcwins19;
                        break;
                    case 20:
                        i_ai4 = s_br_halloween_pcwins20;
                        break;
                }
                getActor().broadcastPacket(new MagicSkillUse(getActor(), player, i_ai4, 1, 0, 0));
                getActor().altOnMagicUseTimer(player, SkillTable.getInstance().getSkillEntry(i_ai4, 1));
            } else {
                getActor().broadcastPacket(new MagicSkillUse(getActor(), player, s_br_halloween_lose, 1, 0, 0));
                getActor().altOnMagicUseTimer(player, SkillTable.getInstance().getSkillEntry(s_br_halloween_lose, 1));
            }
        } else if (timer_id == GAME_STATE3) {
            if (i_ai0 == i_ai1) {
                if (i_ai3 >= 5) {
                    JackGame.insertEventRanking(player, i_ai3);
                }
                getActor().broadcastPacket(new SocialAction(getActor().getObjectId(), SocialAction.GREETING));
                if (i_ai3 == 1 || (i_ai3 >= 16 && i_ai3 <= 19)) {
                    ChatUtils.say(getActor(), NpcString.valueOf(1900049 + FSTRING_VALUE));
                } else if (i_ai3 == 20) {
                    ChatUtils.say(getActor(), NpcString.valueOf(1900079 + FSTRING_VALUE));
                } else if (i_ai3 >= 5 && i_ai3 <= 15) {
                    i_ai4 = 1900051 + FSTRING_VALUE + (i_ai3 - 5) * 2;
                    ChatUtils.say(getActor(), NpcString.valueOf(i_ai4));
                }
                player.sendPacket(new SystemMessage(SystemMsg.S1).addNpcString(NpcString.valueOf(1900081), player.getName(), String.valueOf(i_ai3)));
                switch (i_ai3) {
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 16:
                    case 17:
                    case 18:
                    case 19:
                    case 20:
                        i_ai4 = 20706;
                        break;
                    case 5:
                        i_ai4 = 20717;
                        break;
                    case 6:
                        i_ai4 = 22025;
                        break;
                    case 7:
                        i_ai4 = 5593;
                        break;
                    case 8:
                        i_ai4 = 3934;
                        break;
                    case 9:
                        i_ai4 = 13339;
                        break;
                    case 10:
                        i_ai4 = 20335;
                        break;
                    case 11:
                        i_ai4 = 20739;
                        break;
                    case 12:
                        i_ai4 = 20740;
                        break;
                    case 13:
                        i_ai4 = 20554;
                        break;
                    case 14:
                        i_ai4 = 20494;
                        break;
                    case 15:
                        i_ai4 = 20723;
                }
                if (!player.isInventoryLimit(false) || !player.isWeightLimit(false)) {
                    player.sendPacket(SystemMsg.THE_ITEM_CANNOT_BE_RECEIVED_BECAUSE_THE_INVENTORY_WEIGHT_QUANTITY_LIMIT_HAS_BEEN_EXCEEDED);
                } else {
                    if (i_ai4 != 0) {
                        ItemFunctions.addItem(player, i_ai4, 1);
                    }
                    if (i_ai3 == 10) {
                        ItemFunctions.addItem(player, br_vitality_potion_25charge, 4);
                    }
                }
                if (i_ai3 == 20) {
                    AddTimerEx(GAME_WIN_STATE2, 4000);
                    player.broadcastPacket(new SystemMessage(SystemMsg.S1).addNpcString(NpcString.valueOf(1900082), player.getName(), String.valueOf(i_ai3)));
                } else if (i_ai3 == 15) {
                    AddTimerEx(GAME_STATE5, 4000);
                    player.broadcastPacket(new SystemMessage(SystemMsg.S1).addNpcString(NpcString.valueOf(1900082), player.getName(), String.valueOf(i_ai3)));
                } else {
                    AddTimerEx(WAIT_STATE1, 5000);
                }
            } else {
                getActor().broadcastPacket(new SocialAction(getActor().getObjectId(), SocialAction.UNKNOW));
                if (Rnd.get(100) < 50) {
                    ChatUtils.say(getActor(), NpcString.valueOf(1900073 + FSTRING_VALUE));
                } else {
                    ChatUtils.say(getActor(), NpcString.valueOf(1900075 + FSTRING_VALUE));
                }
                AddTimerEx(GAME_WIN_STATE1, 3000);
            }
            i_ai1 = 0;
        } else if (timer_id == GAME_STATE4) {
            if (i_ai2 == 1) {
                i_ai2 = 0;
                if (i_ai4 == 0) {
                    ChatUtils.say(getActor(), NpcString.valueOf(1900085 + FSTRING_VALUE));
                    AddTimerEx(FAIL_STATE1, 3500);
                    i_ai4 = 1;
                } else {
                    ChatUtils.say(getActor(), NpcString.valueOf(1900091 + FSTRING_VALUE));
                    AddTimerEx(GAME_WIN_STATE2, 4000);
                }
            }
        } else if (timer_id == GAME_STATE5) {
            ChatUtils.say(getActor(), NpcString.valueOf(1900077 + FSTRING_VALUE));
            AddTimerEx(WAIT_STATE1, 4000);
        } else if (timer_id == GAME_WIN_STATE1) {
            //getActor().broadcastPacket(new MagicSkillUse(getActor(), player, s_br_halloween_buff, 1, 0, 0)); TODO
            //getActor().altOnMagicUseTimer(player, SkillTable.getInstance().getSkillEntry(s_br_halloween_buff, 1));
            AddTimerEx(GAME_WIN_STATE2, 2000);
        } else if (timer_id == GAME_WIN_STATE2) {
            getActor().setIsInvul(false);
            getActor().deleteMe();
        }
    }

    @Override
    protected void onEvtSeeSpell(SkillEntry skill, Creature caster) {
        final NpcInstance actor = getActor();
        if (actor.isDead() || skill == null || caster == null) {
            return;
        }

        if (i_ai2 == 1 && caster.getCastingTarget() != null && caster.getCastingTarget() == getActor()) {
            if (skill.getId() == s_br_halloween_jack) {
                i_ai2 = 0;
                getActor().broadcastPacket(new MagicSkillUse(getActor(), caster, s_br_halloween_jack_ghost_b, 1, 0, 0));
                getActor().altOnMagicUseTimer(caster, SkillTable.getInstance().getSkillEntry(s_br_halloween_jack_ghost_b, 1));
                i_ai1 = 1;
                AddTimerEx(GAME_STATE1, 1);
            } else if (skill.getId() == s_br_halloween_rotten_jack) {
                i_ai2 = 0;
                getActor().broadcastPacket(new MagicSkillUse(getActor(), caster, s_br_halloween_rotten_jack_ghost_b, 1, 0, 0));
                getActor().altOnMagicUseTimer(caster, SkillTable.getInstance().getSkillEntry(s_br_halloween_rotten_jack_ghost_b, 1));
                i_ai1 = 2;
                AddTimerEx(GAME_STATE1, 1);
            }
        }
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
    }

    @Override
    protected void onEvtAggression(Creature attacker, int aggro) {
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }

    @Override
    protected boolean randomAnimation() {
        return false;
    }
}