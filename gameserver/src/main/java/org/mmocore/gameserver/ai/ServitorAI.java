package org.mmocore.gameserver.ai;

import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Servitor;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.Location;

public class ServitorAI extends PlayableAI {
    private CtrlIntention _storedIntention = null;
    private Object _storedIntentionArg0 = null;
    private Object _storedIntentionArg1 = null;
    private boolean _storedForceUse = false;

    public ServitorAI(final Servitor actor) {
        super(actor);
    }

    public void storeIntention() {
        if (_storedIntention == null) {
            _storedIntention = getIntention();
            _storedIntentionArg0 = _intention_arg0;
            _storedIntentionArg1 = _intention_arg1;
            _storedForceUse = _forceUse;
        }
    }

    public boolean restoreIntention() {
        final CtrlIntention intention = _storedIntention;
        final Object arg0 = _storedIntentionArg0;
        final Object arg1 = _storedIntentionArg1;
        if (intention != null) {
            _forceUse = _storedForceUse;
            setIntention(intention, arg0, arg1);
            clearStoredIntention();

            onEvtThink();
            return true;
        }
        return false;
    }

    public void clearStoredIntention() {
        _storedIntention = null;
        _storedIntentionArg0 = null;
        _storedIntentionArg1 = null;
    }

    @Override
    protected void onIntentionIdle() {
        clearStoredIntention();
        super.onIntentionIdle();
    }

    @Override
    protected void onEvtFinishCasting(SkillEntry skill) {
        if (!restoreIntention()) {
            super.onEvtFinishCasting(skill);
        }
    }

    @Override
    protected void thinkActive() {
        final Servitor actor = getActor();

        clearNextAction();

        if (actor.isDepressed()) {
            setAttackTarget(actor.getPlayer());
            changeIntention(CtrlIntention.AI_INTENTION_ATTACK, actor.getPlayer(), null);
            thinkAttack(true);
        } else if (actor.isFollowMode() && !actor.isAfraid()) {
            changeIntention(CtrlIntention.AI_INTENTION_FOLLOW, actor.getPlayer(), AllSettingsConfig.FOLLOW_RANGE);
            thinkFollow();
        }

        super.thinkActive();
    }

    @Override
    protected void thinkAttack(final boolean checkRange) {
        final Servitor actor = getActor();

        if (actor.isDepressed()) {
            setAttackTarget(actor.getPlayer());
        }

        super.thinkAttack(checkRange);
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        final Servitor actor = getActor();

        if (attacker != null) {
/*			if(actor.getPlayer() == attacker)
			{
				actor.moveToLocation(Location.findAroundPosition(attacker, 100), 0, false);
			}*/
            if (actor.isMovementDisabled()) {
                return;
            }
            if (actor.getPlayer() != attacker && !actor.getPlayer().isDead() && !actor.isAttackingNow() /*&& actor.isInRangeZ(actor.getPlayer(), AllSettingsConfig.FOLLOW_RANGE)*/
                    && actor.getDistance(actor.getPlayer()) < 1000) // additional check that grants safety move
            {
                actor.moveToLocation(Location.findAroundPosition(actor.getPlayer(), 100), 0, false);
            } else if (actor.getPlayer().isDead() && !actor.isDepressed()) {
                Attack(attacker, false, false);
            }
        }
        super.onEvtAttacked(attacker, skill, damage);
    }

    @Override
    public void Cast(final SkillEntry skillEntry, final Creature target, final boolean forceUse, final boolean dontMove) {
        storeIntention();
        super.Cast(skillEntry, target, forceUse, dontMove);
    }

    @Override
    protected void onEvtForgetObject(GameObject object) {
        //if(getActor().isFollowMode() && !getActor().isAttackingNow() && !getActor().isCastingNow() && getAttackTarget() == null)
        //{
        //	getActor().moveToOwner();
        //}
        super.onEvtForgetObject(object);
    }

    @Override
    public Servitor getActor() {
        return (Servitor) super.getActor();
    }
}