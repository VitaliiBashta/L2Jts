package org.mmocore.gameserver.ai;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.PlayableAI.NextAction;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.serverpackets.Die;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.world.GameObjectsStorage;
import org.mmocore.gameserver.world.World;

public class CharacterAI extends AbstractAI {
    public CharacterAI(final Creature actor) {
        super(actor);
    }

    @Override
    protected void onIntentionIdle() {
        clientStopMoving();
        changeIntention(CtrlIntention.AI_INTENTION_IDLE, null, null);
    }

    @Override
    protected void onIntentionActive() {
        clientStopMoving();
        changeIntention(CtrlIntention.AI_INTENTION_ACTIVE, null, null);
        onEvtThink();
    }

    @Override
    protected void onIntentionAttack(final Creature target) {
        setAttackTarget(target);
        clientStopMoving();
        changeIntention(CtrlIntention.AI_INTENTION_ATTACK, target, null);
        onEvtThink();
    }

    @Override
    protected void onIntentionCast(final SkillEntry skill, final Creature target) {
        setAttackTarget(target);
        changeIntention(CtrlIntention.AI_INTENTION_CAST, skill, target);
        onEvtThink();
    }

    @Override
    protected void onIntentionFollow(final Creature target, final Integer offset) {
        changeIntention(CtrlIntention.AI_INTENTION_FOLLOW, target, offset);
        onEvtThink();
    }

    @Override
    protected void onIntentionInteract(final GameObject object) {
    }

    @Override
    protected void onIntentionPickUp(final GameObject item) {
    }

    @Override
    protected void onIntentionRest() {
    }

    @Override
    protected void onIntentionCoupleAction(final Player player, final Integer socialId) {
    }

    @Override
    protected void onEvtArrivedBlocked(final Location blocked_at_pos) {
        final Creature actor = getActor();
        if (actor.isPlayer()) {
            // Приводит к застреванию в стенах:
            //if(actor.isInRange(blocked_at_pos, 1000))
            //	actor.setLoc(blocked_at_pos, true);
            // Этот способ надежнее:
            final Location loc = actor.getLastServerPosition();
            if (loc != null) {
                actor.setLoc(loc, true);
            }
            actor.stopMove();
        }
        onEvtThink();
    }

    @Override
    protected void onEvtForgetObject(final GameObject object) {
        if (object == null) {
            return;
        }

        final Creature actor = getActor();

        if (actor.isAttackingNow() && getAttackTarget() == object) {
            actor.abortAttack(true, true);
        }

        if (actor.isCastingNow() && getAttackTarget() == object) {
            // Не срываем каст на олимпиаде, если противник забежал за преграду, но скастовано больше 50% скилла.
            if (actor.isPlayer() && actor.getPlayer().isInOlympiadMode()) {
                final int skillCastTime = (int) (actor.getPlayer().getAnimationEndTime() - System.currentTimeMillis());
                if (skillCastTime < skillCastTime / 2) {
                    actor.abortCast(true, true);
                }
            } else {
                actor.abortCast(true, true);
            }
        }

        if (getAttackTarget() == object) {
            setAttackTarget(null);
        }

        if (actor.getTargetId() == object.getObjectId()) {
            actor.setTarget(null);
        }

        if (actor.getFollowTarget() == object) {
            actor.setFollowTarget(null);
        }

        if (actor.getServitor() != null) {
            actor.getServitor().getAI().notifyEvent(CtrlEvent.EVT_FORGET_OBJECT, object);
        }
    }

    @Override
    protected void onEvtDead(final Creature killer) {
        final Creature actor = getActor();
        actor.abortAttack(true, true);
        actor.abortCast(true, true);
        actor.stopMove();
        actor.broadcastPacket(new Die(actor));
        setIntention(CtrlIntention.AI_INTENTION_IDLE);
    }

    @Override
    protected void onEvtFakeDeath() {
        clientStopMoving();
        setIntention(CtrlIntention.AI_INTENTION_IDLE);
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {

    }

    @Override
    protected void onEvtClanAttacked(final Creature attacked_member, final Creature attacker, final int damage) {
    }

    public void Attack(final GameObject target, final boolean forceUse, final boolean dontMove) {
        setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
    }

    public void Cast(final SkillEntry skill, final Creature target) {
        Cast(skill, target, false, false);
    }

    public void Cast(final SkillEntry skill, final Creature target, final boolean forceUse, final boolean dontMove) {
        setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
    }

    @Override
    protected void onEvtThink() {
    }

    @Override
    protected void onEvtScriptEvent(ScriptEvent event, Object arg1, Object arg2) {
    }

    @Override
    protected void onEvtTimerFiredEx(int timer_id, Object arg1, Object arg2) {
    }

    @Override
    protected void onEvtAggression(final Creature target, final int aggro) {
    }

    @Override
    protected void onEvtFinishCasting(SkillEntry skill) {
    }

    @Override
    protected void onEvtReadyToAct() {
    }

    @Override
    protected void onEvtArrived() {
    }

    @Override
    protected void onEvtArrivedTarget() {
    }

    @Override
    protected void onEvtSeeSpell(final SkillEntry skill, final Creature caster) {
    }

    @Override
    protected void onEvtSpawn() {
    }

    @Override
    public void onEvtDeSpawn() {
    }

    @Override
    public void onTeleportRequested(Player talker) {
    }

    public void stopAITask() {
    }

    public void startAITask() {
    }

    public void setNextAction(final NextAction action, final Object arg0, final Object arg1, final boolean arg2, final boolean arg3) {
    }

    public void setNextAction(final NextAction action, final Object arg0, final Object arg1, final boolean arg2, final boolean arg3,
                              final boolean arg4) {
    }

    public void clearNextAction() {
    }

    public boolean isActive() {
        return true;
    }

    @Override
    protected void onEvtMenuSelected(final Player player, final int ask, final int reply) {
    }

    public void AddTimerEx(final int timerId, final long delay) {
        AddTimerEx(timerId, null, null, delay);
    }

    public void AddTimerEx(final int timerId, final Object arg1, final long delay) {
        AddTimerEx(timerId, arg1, null, delay);
    }

    public void AddTimerEx(final int timerId, final Object arg1, final Object arg2, final long delay) {
        ThreadPoolManager.getInstance().schedule(new TimerEx(timerId, arg1, arg2), delay);
    }

    /**
     * @param event  - ScriptEvent(с птсэ Неймы (enum))
     * @param arg1   - таргет,обьект
     * @param radius - радиус в котором заставим когото что то делать.
     */
    protected void broadCastScriptEvent(final ScriptEvent event, final Object arg1, final Object arg2, final int radius) {
        for (Creature creature : World.getAroundCharacters(getActor(), radius, radius)) {
            creature.getAI().notifyEvent(CtrlEvent.EVT_SCRIPT_EVENT, event, arg1, arg2);
        }
    }

    public void broadcastScriptEvent(final ScriptEvent event, final int radius) {
        broadCastScriptEvent(event, radius);
    }

    protected void broadCastScriptEvent(final ScriptEvent event, final int radius) {
        broadCastScriptEvent(event, null, null, radius);
    }

    protected void broadCastScriptEvent(final ScriptEvent event, final Object arg1, final int radius) {
        broadCastScriptEvent(event, arg1, null, radius);
    }

    /**
     * @param event - енум евента который будем передовать
     * @param objId - нпс которого будем заставлять что то выполнить
     * @param arg1  - таргет,обьект
     * @param arg2  - таргет,обьект
     */
    protected void sendScriptEvent(final ScriptEvent event, final int objId, final Object arg1, final Object arg2) {
        if (objId > 0) {
            final Creature npc = (Creature) GameObjectsStorage.findObject(objId);
            if (npc != null) {
                npc.getAI().notifyEvent(CtrlEvent.EVT_SCRIPT_EVENT, event, arg1, arg2);
            }
        }
    }

    protected void sendScriptEvent(final ScriptEvent event, final int objId) {
        sendScriptEvent(event, objId, null, null);
    }

    /**
     * Используется лишь в том случае, если данный нпс 1 в мире.
     * Либо использует первого из списка.
     */
    protected void sendScriptEventOneNpc(final ScriptEvent event, final int npcId, final Object arg1, final Object arg2) {
        final NpcInstance npc = GameObjectsStorage.getNpc(npcId);
        if (npc != null) {
            npc.getAI().notifyEvent(CtrlEvent.EVT_SCRIPT_EVENT, event, arg1, arg2);
        }
    }

    protected class TimerEx extends RunnableImpl {
        private final int _timerId;
        private final Object _arg1;
        private final Object _arg2;

        public TimerEx(final int timerId, final Object arg1, final Object arg2) {
            _timerId = timerId;
            _arg1 = arg1;
            _arg2 = arg2;
        }

        protected void runImpl() {
            notifyEvent(CtrlEvent.EVT_TIMER_FIRED_EX, _timerId, _arg1, _arg2);
        }
    }
}