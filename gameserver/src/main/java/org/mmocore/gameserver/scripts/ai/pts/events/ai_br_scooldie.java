package org.mmocore.gameserver.scripts.ai.pts.events;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.serverpackets.MagicSkillUse;
import org.mmocore.gameserver.network.lineage.serverpackets.SocialAction;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.ChatUtils;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * @author KilRoy
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class ai_br_scooldie extends ai_br_wooldie {
    private int i_ai0 = 0;
    private int i_ai1 = 0;
    private int i_ai2 = 0;
    private int i_ai3 = 0;
    private int i_ai4 = 0;

    public ai_br_scooldie(NpcInstance actor) {
        super(actor);
        i_ai0 = 0;
        i_ai1 = 0;
        i_ai2 = 0;
        i_ai3 = 0;
        i_ai4 = 0;
        FSTRING_VALUE = 52;
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        AddTimerEx(WAIT_STATE1, 4000);
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
            ChatUtils.say(getActor(), NpcString.valueOf(1900094));
            AddTimerEx(WAIT_STATE3, 1000);
        } else if (timer_id == WAIT_STATE2) {
            if (i_ai2 == 1) {
                if (Rnd.get(100) < 50) {
                    ChatUtils.say(getActor(), NpcString.valueOf(1900045));
                } else {
                    ChatUtils.say(getActor(), NpcString.valueOf(1900047));
                }
            }
        } else if (timer_id == WAIT_STATE3) {
            getActor().broadcastPacket(new MagicSkillUse(getActor(), player, s_br_halloween_gage, 1, 0, 0));
            getActor().altOnMagicUseTimer(player, SkillTable.getInstance().getSkillEntry(s_br_halloween_gage, 1));
            AddTimerEx(WAIT_STATE4, 950);
            if (i_ai3 != 0) {
                AddTimerEx(WAIT_STATE2, 3000);
            }
            AddTimerEx(GAME_STATE4, 7500);
        } else if (timer_id == WAIT_STATE4) {
            i_ai2 = 1;
        } else if (timer_id == FAIL_STATE1) {
            ChatUtils.say(getActor(), NpcString.valueOf(1900100));
            AddTimerEx(FAIL_STATE2, 4000);
        } else if (timer_id == FAIL_STATE2) {
            ChatUtils.say(getActor(), NpcString.valueOf(1900101));
            AddTimerEx(WAIT_STATE1, 4000);
        } else if (timer_id == GAME_STATE1) {
            i_ai0 = Rnd.get(20) % 2 + 1;
            if (i_ai0 == 1) {
                getActor().broadcastPacket(new MagicSkillUse(getActor(), getActor(), s_br_halloween_jack_ghost, 1, 0, 0));
                getActor().altOnMagicUseTimer(getActor(), SkillTable.getInstance().getSkillEntry(s_br_halloween_jack_ghost, 1));
            } else if (i_ai0 == 2) {
                getActor().broadcastPacket(new MagicSkillUse(getActor(), getActor(), s_br_halloween_rotten_jack_ghost, 1, 0, 0));
                getActor().altOnMagicUseTimer(getActor(), SkillTable.getInstance().getSkillEntry(s_br_halloween_rotten_jack_ghost, 1));
            }
            AddTimerEx(GAME_STATE2, 3000);
        } else if (timer_id == GAME_STATE2) {
            i_ai3 = i_ai3 + 1;
            if (i_ai0 == i_ai1) {
                getActor().broadcastPacket(new SocialAction(getActor().getObjectId(), SocialAction.GREETING)); // myself::AddEffectActionDesire( myself.sm, 2, 2000, 1000000 );
                ChatUtils.say(getActor(), NpcString.valueOf(1900095));
            } else {
                getActor().broadcastPacket(new SocialAction(getActor().getObjectId(), SocialAction.UNKNOW)); // myself::AddEffectActionDesire( myself.sm, 1, 3000, 1000000 );
                ChatUtils.say(getActor(), NpcString.valueOf(1900096));
            }

            if (i_ai3 == 3) {
                AddTimerEx(GAME_WIN_STATE1, 6000);
            } else {
                AddTimerEx(GAME_STATE3, 4000);
            }
        } else if (timer_id == GAME_STATE3) {
            if (i_ai0 == i_ai1) {
                ChatUtils.say(getActor(), NpcString.valueOf(1900097));
            } else {
                ChatUtils.say(getActor(), NpcString.valueOf(1900098));
            }
            AddTimerEx(WAIT_STATE1, 6000);
        } else if (timer_id == GAME_STATE4) {
            if (i_ai2 == 1) {
                i_ai2 = 0;
                if (i_ai4 == 0) {
                    ChatUtils.say(getActor(), NpcString.valueOf(1900099));
                    AddTimerEx(FAIL_STATE1, 4000);
                    i_ai4 = 1;
                } else {
                    ChatUtils.say(getActor(), NpcString.valueOf(1900102));
                    AddTimerEx(GAME_WIN_STATE2, 4000);
                }
            }
        } else if (timer_id == GAME_WIN_STATE1) {
            ChatUtils.say(getActor(), NpcString.valueOf(1900103));
            AddTimerEx(GAME_WIN_STATE2, 5500);
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
    protected boolean randomWalk() {
        return false;
    }
}