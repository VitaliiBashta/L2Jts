package org.mmocore.gameserver.ai;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.commons.lang.reference.HardReference;
import org.mmocore.commons.lang.reference.HardReferences;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractAI extends RunnableImpl {
    protected static final Logger _log = LoggerFactory.getLogger(AbstractAI.class);

    protected final Creature _actor;
    private HardReference<? extends Creature> _attackTarget = HardReferences.emptyRef();

    private CtrlIntention _intention = CtrlIntention.AI_INTENTION_IDLE;

    protected AbstractAI(final Creature actor) {
        _actor = actor;
    }

    public void changeIntention(final CtrlIntention intention, final Object arg0, final Object arg1) {
        _intention = intention;
        if (intention != CtrlIntention.AI_INTENTION_CAST && intention != CtrlIntention.AI_INTENTION_ATTACK) {
            setAttackTarget(null);
        }
    }

    public final void setIntention(final CtrlIntention intention, final Object arg0) {
        setIntention(intention, arg0, null);
    }

    public void setIntention(CtrlIntention intention, final Object arg0, final Object arg1) {
        if (intention != CtrlIntention.AI_INTENTION_CAST && intention != CtrlIntention.AI_INTENTION_ATTACK) {
            setAttackTarget(null);
        }

        final Creature actor = getActor();

        if (!actor.isVisible()) {
            if (_intention == CtrlIntention.AI_INTENTION_IDLE) {
                return;
            }

            intention = CtrlIntention.AI_INTENTION_IDLE;
        }

        actor.getListeners().onAiIntention(intention, arg0, arg1);

        switch (intention) {
            case AI_INTENTION_IDLE:
                onIntentionIdle();
                break;
            case AI_INTENTION_ACTIVE:
                onIntentionActive();
                break;
            case AI_INTENTION_REST:
                onIntentionRest();
                break;
            case AI_INTENTION_ATTACK:
                onIntentionAttack((Creature) arg0);
                break;
            case AI_INTENTION_CAST:
                onIntentionCast((SkillEntry) arg0, (Creature) arg1);
                break;
            case AI_INTENTION_PICK_UP:
                onIntentionPickUp((GameObject) arg0);
                break;
            case AI_INTENTION_INTERACT:
                onIntentionInteract((GameObject) arg0);
                break;
            case AI_INTENTION_FOLLOW:
                onIntentionFollow((Creature) arg0, (Integer) arg1);
                break;
            case AI_INTENTION_COUPLE_ACTION:
                onIntentionCoupleAction((Player) arg0, (Integer) arg1);
                break;
        }
    }

    public final void notifyEvent(final CtrlEvent evt) {
        notifyEvent(evt, ArrayUtils.EMPTY_OBJECT_ARRAY);
    }

    public final void notifyEvent(final CtrlEvent evt, final Object arg0) {
        notifyEvent(evt, new Object[]{arg0});
    }

    public final void notifyEvent(final CtrlEvent evt, final Object arg0, final Object arg1) {
        notifyEvent(evt, new Object[]{arg0, arg1});
    }

    public final void notifyEvent(final CtrlEvent evt, final Object arg0, final Object arg1, final Object arg2) {
        notifyEvent(evt, new Object[]{arg0, arg1, arg2});
    }

    public void notifyEvent(final CtrlEvent evt, final Object[] args) {
        final Creature actor = getActor();
        if (actor == null || !actor.isVisible()) {
            return;
        }

        actor.getListeners().onAiEvent(evt, args);

        switch (evt) {
            case EVT_THINK:
                onEvtThink();
                break;
            case EVT_ATTACKED:
                onEvtAttacked((Creature) args[0], (SkillEntry) args[1], ((Number) args[2]).intValue());
                break;
            case EVT_CLAN_ATTACKED:
                onEvtClanAttacked((Creature) args[0], (Creature) args[1], ((Number) args[2]).intValue());
                break;
            case EVT_AGGRESSION:
                onEvtAggression((Creature) args[0], ((Number) args[1]).intValue());
                break;
            case EVT_READY_TO_ACT:
                onEvtReadyToAct();
                break;
            case EVT_ARRIVED:
                onEvtArrived();
                break;
            case EVT_ARRIVED_TARGET:
                onEvtArrivedTarget();
                break;
            case EVT_ARRIVED_BLOCKED:
                onEvtArrivedBlocked((Location) args[0]);
                break;
            case EVT_FORGET_OBJECT:
                onEvtForgetObject((GameObject) args[0]);
                break;
            case EVT_DEAD:
                onEvtDead((Creature) args[0]);
                break;
            case EVT_FAKE_DEATH:
                onEvtFakeDeath();
                break;
            case EVT_FINISH_CASTING:
                onEvtFinishCasting((SkillEntry) args[0]);
                break;
            case EVT_SEE_SPELL:
                onEvtSeeSpell((SkillEntry) args[0], (Creature) args[1]);
                break;
            case EVT_SPAWN:
                onEvtSpawn();
                break;
            case EVT_DESPAWN:
                onEvtDeSpawn();
                break;
            case EVT_MENU_SELECTED:
                onEvtMenuSelected((Player) args[0], ((Number) args[1]).intValue(), ((Number) args[2]).intValue());
                break;
            case EVT_SCRIPT_EVENT:
                onEvtScriptEvent(((ScriptEvent) args[0]), args[1], args[2]);
                break;
            case EVT_TIMER_FIRED_EX:
                onEvtTimerFiredEx(((Number) args[0]).intValue(), args[1], args[2]);
                break;
        }
    }

    protected void clientActionFailed() {
        final Creature actor = getActor();
        if (actor != null && actor.isPlayer()) {
            actor.sendActionFailed();
        }
    }

    /**
     * Останавливает движение
     *
     * @param validate - рассылать ли ValidateLocation
     */
    public void clientStopMoving(final boolean validate) {
        final Creature actor = getActor();
        actor.stopMove(validate);
    }

    /**
     * Останавливает движение и рассылает ValidateLocation
     */
    public void clientStopMoving() {
        final Creature actor = getActor();
        actor.stopMove();
    }

    public Creature getActor() {
        return _actor;
    }

    public CtrlIntention getIntention() {
        return _intention;
    }

    public final void setIntention(final CtrlIntention intention) {
        setIntention(intention, null, null);
    }

    public Creature getAttackTarget() {
        return _attackTarget.get();
    }

    public void setAttackTarget(final Creature target) {
        _attackTarget = target == null ? HardReferences.<Creature>emptyRef() : target.getRef();
    }

    /**
     * Означает, что AI всегда включен, независимо от состояния региона
     */
    public boolean isGlobalAI() {
        return false;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " for " + getActor();
    }

    @Override
    public void runImpl() throws Exception {
    }

    protected abstract void onIntentionIdle();

    protected abstract void onIntentionActive();

    protected abstract void onIntentionRest();

    protected abstract void onIntentionAttack(Creature target);

    protected abstract void onIntentionCast(SkillEntry skill, Creature target);

    protected abstract void onIntentionPickUp(GameObject item);

    protected abstract void onIntentionInteract(GameObject object);

    protected abstract void onIntentionCoupleAction(Player player, Integer socialId);

    protected abstract void onEvtThink();

    protected abstract void onEvtAttacked(Creature attacker, SkillEntry skill, int damage);

    protected abstract void onEvtClanAttacked(Creature attacked_member, Creature attacker, int damage);

    protected abstract void onEvtAggression(Creature target, int aggro);

    protected abstract void onEvtReadyToAct();

    protected abstract void onEvtArrived();

    protected abstract void onEvtArrivedTarget();

    protected abstract void onEvtArrivedBlocked(Location blocked_at_pos);

    protected abstract void onEvtForgetObject(GameObject object);

    protected abstract void onEvtDead(Creature killer);

    protected abstract void onEvtFakeDeath();

    protected abstract void onEvtFinishCasting(SkillEntry skill);

    protected abstract void onEvtSeeSpell(SkillEntry skill, Creature caster);

    protected abstract void onEvtSpawn();

    protected abstract void onEvtDeSpawn();

    protected abstract void onIntentionFollow(Creature target, Integer offset);

    protected abstract void onEvtMenuSelected(Player player, int ask, int reply);

    protected abstract void onEvtScriptEvent(ScriptEvent event, Object arg1, Object arg2);

    protected abstract void onEvtTimerFiredEx(int timer_id, Object arg1, Object arg2);

    protected abstract void onTeleportRequested(Player talker);
}