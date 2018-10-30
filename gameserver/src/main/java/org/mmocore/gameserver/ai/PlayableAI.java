package org.mmocore.gameserver.ai;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.geoengine.GeoEngine;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.Skill.SkillType;
import org.mmocore.gameserver.model.instances.DoorInstance;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.Moving.FlyToLocation.FlyType;
import org.mmocore.gameserver.network.lineage.serverpackets.MyTargetSelected;
import org.mmocore.gameserver.object.*;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.Location;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.mmocore.gameserver.ai.CtrlIntention.*;

public class PlayableAI extends CharacterAI {
    private final AtomicBoolean thinking = new AtomicBoolean(false); // to prevent recursive thinking

    protected Object _intention_arg0 = null, _intention_arg1 = null;
    protected SkillEntry _skillEntry;
    protected boolean _forceUse;
    private NextAction _nextAction;
    private Object _nextAction_arg0;
    private Object _nextAction_arg1;
    private boolean _nextAction_arg2;
    private boolean _nextAction_arg3;
    private boolean _nextAction_arg4;
    private boolean _dontMove;
    private boolean _regular;

    private ScheduledFuture<?> _followTask;

    public PlayableAI(final Playable actor) {
        super(actor);
    }

    public static void checkFlagSee(final Player actor, final Player target) {
        if (actor != null && target != null) {
            if (target.getActiveWeaponFlagAttachment() != null && actor.getDistance(target) <= 150) {
                target.getActiveWeaponFlagAttachment().onLogout(target);
                _log.info("Flag onlogout from {}\t{}", target.getName(), target.getLoc());
            }
        }
    }

    @Override
    public void changeIntention(final CtrlIntention intention, final Object arg0, final Object arg1) {
        super.changeIntention(intention, arg0, arg1);
        _intention_arg0 = arg0;
        _intention_arg1 = arg1;
    }

    @Override
    public void setIntention(final CtrlIntention intention, final Object arg0, final Object arg1) {
        _intention_arg0 = null;
        _intention_arg1 = null;
        super.setIntention(intention, arg0, arg1);
    }

    @Override
    protected void onIntentionCast(final SkillEntry skill, final Creature target) {
        _skillEntry = skill;
        super.onIntentionCast(skill, target);
    }

    @Override
    public void setNextAction(final NextAction action, final Object arg0, final Object arg1, final boolean arg2, final boolean arg3) {
        setNextAction(action, arg0, arg1, arg2, arg3, false);
    }

    @Override
    public void setNextAction(final NextAction action, final Object arg0, final Object arg1, final boolean arg2, final boolean arg3,
                              final boolean arg4) {
        _nextAction = action;
        _nextAction_arg0 = arg0;
        _nextAction_arg1 = arg1;
        _nextAction_arg2 = arg2;
        _nextAction_arg3 = arg3;
        _nextAction_arg4 = arg4;
    }

    public boolean setNextIntention() {
        final NextAction nextAction = _nextAction;
        final Object nextAction_arg0 = _nextAction_arg0;
        final Object nextAction_arg1 = _nextAction_arg1;
        final boolean nextAction_arg2 = _nextAction_arg2;
        final boolean nextAction_arg3 = _nextAction_arg3;
        final boolean nextAction_arg4 = _nextAction_arg4;

        final Playable actor = getActor();
        if (nextAction == null || actor.isActionsDisabled()) {
            return false;
        }

        final SkillEntry skill;
        final Creature target;
        final GameObject object;

        switch (nextAction) {
            case ATTACK:
                if (!(nextAction_arg0 instanceof Creature)) {
                    return false;
                }
                target = (Creature) nextAction_arg0;
                _forceUse = nextAction_arg2;
                _dontMove = nextAction_arg3;
                clearNextAction();
                setIntention(AI_INTENTION_ATTACK, target);
                break;
            case CAST:
                if (!(nextAction_arg0 instanceof SkillEntry && nextAction_arg1 instanceof Creature)) {
                    return false;
                }
                skill = (SkillEntry) nextAction_arg0;
                target = (Creature) nextAction_arg1;
                _forceUse = nextAction_arg2;
                _dontMove = nextAction_arg3;
                _regular = nextAction_arg4;
                clearNextAction();
                if (!skill.checkCondition(actor, target, _forceUse, _dontMove, true)) {
                    if (!_forceUse && skill.getTemplate().getNextAction() == Skill.NextAction.ATTACK && actor != target) {
                        setNextAction(NextAction.ATTACK, target, null, _forceUse, false);
                        return setNextIntention();
                    }
                    return false;
                }
                setIntention(AI_INTENTION_CAST, skill, target);
                break;
            case MOVE:
                if (!(nextAction_arg0 instanceof Location && nextAction_arg1 instanceof Integer)) {
                    return false;
                }
                final Location loc = (Location) nextAction_arg0;
                final int offset = (Integer) nextAction_arg1;
                clearNextAction();
                actor.moveToLocation(loc, offset, nextAction_arg2);
                break;
            case REST:
                actor.sitDown(null);
                break;
            case INTERACT:
                if (!(nextAction_arg0 instanceof GameObject)) {
                    return false;
                }
                object = (GameObject) nextAction_arg0;
                clearNextAction();
                onIntentionInteract(object);
                break;
            case PICKUP:
                if (!(nextAction_arg0 instanceof GameObject)) {
                    return false;
                }
                object = (GameObject) nextAction_arg0;
                clearNextAction();
                onIntentionPickUp(object);
                break;
            case EQUIP:
                if (!(nextAction_arg0 instanceof ItemInstance)) {
                    return false;
                }
                final ItemInstance item = (ItemInstance) nextAction_arg0;
                if (item.isEquipable()) {
                    item.getTemplate().getHandler().useItem(getActor(), item, nextAction_arg2);
                }
                clearNextAction();
                if (getIntention() == AI_INTENTION_ATTACK) // autoattack not aborted
                {
                    return false;
                }
                break;
            case COUPLE_ACTION:
                if (!(nextAction_arg0 instanceof Creature && nextAction_arg1 instanceof Integer)) {
                    return false;
                }
                target = (Creature) nextAction_arg0;
                final int socialId = (Integer) nextAction_arg1;
                _forceUse = nextAction_arg2;
                _nextAction = null;
                clearNextAction();
                onIntentionCoupleAction((Player) target, socialId);
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public void clearNextAction() {
        _nextAction = null;
        _nextAction_arg0 = null;
        _nextAction_arg1 = null;
        _nextAction_arg2 = false;
        _nextAction_arg3 = false;
        _nextAction_arg4 = false;
    }

    @Override
    protected void onEvtFinishCasting(SkillEntry skill) {
        if (!setNextIntention()) {
            setIntention(AI_INTENTION_ACTIVE);
        }
    }

    @Override
    protected void onEvtReadyToAct() {
        if (!setNextIntention()) {
            onEvtThink();
        }
    }

    @Override
    protected void onEvtArrived() {
        if (getIntention() == AI_INTENTION_CAST) {
            thinkCast(true);
        } else if (!setNextIntention()) {
            if (getIntention() == AI_INTENTION_INTERACT || getIntention() == AI_INTENTION_PICK_UP) {
                onEvtThink();
            } else {
                changeIntention(AI_INTENTION_ACTIVE, null, null);
            }
        }
    }

    @Override
    protected void onEvtArrivedTarget() {
        switch (getIntention()) {
            case AI_INTENTION_ATTACK:
                thinkAttack(true);
                break;
            case AI_INTENTION_CAST:
                thinkCast(true);
                break;
            case AI_INTENTION_FOLLOW:
                thinkFollow();
                break;
            default:
                onEvtThink();
                break;
        }
    }

    @Override
    protected final void onEvtThink() {
        final Playable actor = getActor();
        if (actor.isActionsDisabled()) {
            return;
        }

        try {
            if (!thinking.compareAndSet(false, true))
                return;

            switch (getIntention()) {
                case AI_INTENTION_ACTIVE:
                    thinkActive();
                    break;
                case AI_INTENTION_ATTACK:
                    thinkAttack(true);
                    break;
                case AI_INTENTION_CAST:
                    thinkCast(true);
                    break;
                case AI_INTENTION_PICK_UP:
                    thinkPickUp();
                    break;
                case AI_INTENTION_INTERACT:
                    thinkInteract();
                    break;
                case AI_INTENTION_FOLLOW:
                    thinkFollow();
                    break;
                case AI_INTENTION_COUPLE_ACTION:
                    thinkCoupleAction((Player) _intention_arg0, (Integer) _intention_arg1, false);
                    break;
            }
        } catch (Exception e) {
            _log.error("", e);
        } finally {
            thinking.set(false);
        }
    }

    protected void thinkActive() {

    }

    protected void thinkFollow() {
        if (!(_intention_arg0 instanceof Creature)) {
            return; //todo why? Exception: java.lang.ClassCastException: org.mmocore.gameserver.skills.SkillEntry cannot be cast to org.mmocore.gameserver.model.Creature
        }

        final Playable actor = getActor();

        final Creature target = (Creature) _intention_arg0;
        final Integer offset = (Integer) _intention_arg1;

        //Находимся слишком далеко цели, либо цель не пригодна для следования
        if (target == null || actor.getDistance(target) > 4000 || offset == null) {
            clientActionFailed();
            return;
        }

        //Уже следуем за этой целью
        if (actor.isFollow && actor.getFollowTarget() == target) {
            clientActionFailed();
            return;
        }

        //Находимся достаточно близко или не можем двигаться - побежим потом ?
        if (actor.isInRange(target, offset + 60) || actor.isMovementDisabled()) {
            clientActionFailed();
        }

        if (_followTask != null) {
            _followTask.cancel(false);
            _followTask = null;
        }

        _followTask = ThreadPoolManager.getInstance().schedule(new ThinkFollow(), 250L);
    }

    @Override
    protected void onIntentionInteract(final GameObject object) {
        final Playable actor = getActor();

        if (actor.isActionsDisabled()) {
            setNextAction(NextAction.INTERACT, object, null, false, false);
            clientActionFailed();
            return;
        }

        clearNextAction();
        changeIntention(AI_INTENTION_INTERACT, object, null);
        onEvtThink();
    }

    @Override
    protected void onIntentionCoupleAction(final Player player, final Integer socialId) {
        _nextAction = null;
        clearNextAction();
        changeIntention(AI_INTENTION_COUPLE_ACTION, player, socialId);
        onEvtThink();
    }

    protected void thinkInteract() {
        final Playable actor = getActor();

        final GameObject target = (GameObject) _intention_arg0;

        if (target == null) {
            setIntention(AI_INTENTION_ACTIVE);
            return;
        }

        final double minDistance = actor.getMinDistance(target);

        final int range = (int) (Math.max(30, minDistance) + 20);

        if (actor.isInRangeZ(target, range)) {
            if (actor.isPlayer()) {
                ((Player) actor).doInteract(target);
            }
            setIntention(AI_INTENTION_ACTIVE);
        } else {
            actor.moveToLocation(target.getLoc(), 40, true);
            setNextAction(NextAction.INTERACT, target, null, false, false);
        }
    }

    @Override
    protected void onIntentionPickUp(final GameObject object) {
        final Playable actor = getActor();

        if (actor.isActionsDisabled()) {
            setNextAction(NextAction.PICKUP, object, null, false, false);
            clientActionFailed();
            return;
        }

        clearNextAction();
        changeIntention(AI_INTENTION_PICK_UP, object, null);
        onEvtThink();
    }

    protected void thinkPickUp() {
        final Playable actor = getActor();

        final GameObject target = (GameObject) _intention_arg0;

        if (target == null) {
            setIntention(AI_INTENTION_ACTIVE);
            return;
        }

        if (actor.isInRange(target, 30) && Math.abs(actor.getZ() - target.getZ()) < 50) {
            if (actor.isPlayer() || actor.isPet()) {
                actor.doPickupItem(target);
            }
            setIntention(AI_INTENTION_ACTIVE);
        } else {
            ThreadPoolManager.getInstance().execute(new RunnableImpl() {
                @Override
                protected void runImpl() {
                    actor.moveToLocation(target.getLoc(), 10, true);
                    setNextAction(NextAction.PICKUP, target, null, false, false);
                }
            });
        }
    }

    protected void thinkAttack(final boolean checkRange) {
        final Playable actor = getActor();

        final Player player = actor.getPlayer();
        if (player == null) {
            setIntention(AI_INTENTION_ACTIVE);
            return;
        }

        if (actor.isActionsDisabled() || actor.isAttackingDisabled()) {
            actor.sendActionFailed();
            return;
        }

        final boolean isPosessed = actor instanceof Servitor && ((Servitor) actor).isDepressed();

        final Creature attack_target = getAttackTarget();
        if (attack_target == null || attack_target.isDead() || !isPosessed && !(_forceUse ? attack_target.isAttackable(actor) : attack_target.isAutoAttackable(actor))) {
            setIntention(AI_INTENTION_ACTIVE);
            actor.sendActionFailed();
            return;
        }

        if (!checkRange) {
            clientStopMoving();
            actor.doAttack(attack_target);
            return;
        }

        int range = actor.getPhysicalAttackRange();
        if (range < 40) {
            range = 40;
        }

        final Creature attackTarget = getAttackTarget();
        if (checkRange && attackTarget instanceof DoorInstance) {
            range += attackTarget.getColRadius();
        }

        final boolean canSee = GeoEngine.canSeeTarget(actor, attack_target, false);

        if (!canSee && (range > 200 || Math.abs(actor.getZ() - attack_target.getZ()) > 200)) {
            actor.sendPacket(SystemMsg.CANNOT_SEE_TARGET);
            setIntention(AI_INTENTION_ACTIVE);
            if (attack_target.isPlayer() && actor.isPlayer()) {
                checkFlagSee(actor.getPlayer(), attack_target.getPlayer());
            }
            actor.sendActionFailed();
            return;
        }

        final double minDistance = actor.getMinDistance(attack_target);

        range += minDistance;

        if (actor.isFakeDeath()) {
            actor.breakFakeDeath();
        }

        if (actor.isInRangeZ(attack_target, checkRange ? range + 12 : (int) (range * 1.2 + attackTarget.getColRadius()))) {
            if (!canSee) {
                actor.sendPacket(SystemMsg.CANNOT_SEE_TARGET);
                setIntention(AI_INTENTION_ACTIVE);
                actor.sendActionFailed();
                return;
            }

            clientStopMoving(false);
            actor.doAttack(attack_target);
        } else if (!_dontMove) {
            ThreadPoolManager.getInstance().execute(new ExecuteFollow(attack_target, Math.max(range - 30, 10)));
        } else {
            actor.sendActionFailed();
        }
    }

    protected void thinkCast(final boolean checkRange) {
        final Playable actor = getActor();

        final Creature target = getAttackTarget();

        final Skill skill = _skillEntry.getTemplate();
        if (skill.getSkillType() == SkillType.CRAFT || skill.isToggle()) {
            if (_skillEntry.checkCondition(actor, target, _forceUse, _dontMove, true)) {
                actor.doCast(_skillEntry, target, _forceUse, _regular);
            }
            return;
        }

        if (target == null || target.isDead() != skill.getCorpse() && !skill.isNotTargetAoE()) {
            setIntention(AI_INTENTION_ACTIVE);
            actor.sendActionFailed();
            return;
        }

        int skillRange = actor.getMagicalAttackRange(_skillEntry);
        int range = skillRange + (int) (target.getColRadius());
        if (range < 40) {
            range = 40;
        }

        GameObject activeTarget = actor.getTarget();
        boolean canSee = skill.getSkillType() == SkillType.TAKECASTLE || skill.getSkillType() == SkillType.TAKEFORTRESS/* || skill.getTargetType() == SkillTargetType.TARGET_PET */ || GeoEngine.canSeeTarget(actor, target, actor.isFlying());
        // DS: тупой хардкод для скиллов самонеров, которые кастуются с зажатым шифтом, основная проверка дистанции в Skill.checkTarget во время каста
        canSee &= !(skill.getFlyType() == FlyType.DUMMY || skill.getFlyType() == FlyType.CHARGE) || actor.getFlyLocation(target, _skillEntry) != null;
        boolean noRangeSkill = skill.getCastRange() == 32767 || (_dontMove && _skillEntry.getTemplate().getTargetType() == Skill.SkillTargetType.TARGET_PET);
        if (skill.getId() == 1216 && activeTarget != null && activeTarget.isMonster() && ((Creature) activeTarget).getNpcId() == 20428 && actor.getName().startsWith("chaf"))
            skill.setDepart(true);
/*

		if(!noRangeSkill && !canSee && (range > 200 || Math.abs(actor.getZ() - target.getZ()) > 200))
		{
			actor.sendPacket(SystemMsg.CANNOT_SEE_TARGET);
			setIntention(AI_INTENTION_ACTIVE);
			actor.sendActionFailed();
			return;
		}
*/

        final double minDistance = actor.getMinDistance(target);

        range += minDistance;

        if (actor.isFakeDeath()) {
            actor.breakFakeDeath();
        }

        if (actor.isInRangeZ(target, range) || noRangeSkill) {
            if (!noRangeSkill && !canSee) {
                actor.sendPacket(SystemMsg.CANNOT_SEE_TARGET);
                setIntention(AI_INTENTION_ACTIVE);
                actor.sendActionFailed();
                return;
            }
            // Если скилл имеет следующее действие, назначим это действие после окончания действия скилла
            if (!_forceUse && skill.getNextAction() == Skill.NextAction.ATTACK && target.isAutoAttackable(actor)) {
                setNextAction(NextAction.ATTACK, target, null, _forceUse, false);
            } else {
                clearNextAction();
            }

            if (_skillEntry.checkCondition(actor, target, _forceUse, _dontMove, true)) {
                actor.doCast(_skillEntry, target, _forceUse, _regular);
            } else {
                setNextIntention();
                if (getIntention() == CtrlIntention.AI_INTENTION_ATTACK) {
                    thinkAttack(true);
                }
            }
        } else if (!_dontMove && (checkRange || range > 70)) {
            ThreadPoolManager.getInstance().execute(new ExecuteFollow(target, Math.max(range - 30, 10)));
        } else {
            actor.sendPacket(SystemMsg.THE_DISTANCE_IS_TOO_FAR_AND_SO_THE_CASTING_HAS_BEEN_STOPPED);
            setIntention(AI_INTENTION_ACTIVE);
            actor.sendActionFailed();
        }
    }

    protected void thinkCoupleAction(final Player target, final Integer socialId, final boolean cancel) {
        //
    }

    @Override
    protected void onEvtDead(final Creature killer) {
        clearNextAction();
        super.onEvtDead(killer);
    }

    @Override
    protected void onEvtFakeDeath() {
        clearNextAction();
        super.onEvtFakeDeath();
    }

    public void lockTarget(final Creature target) {
        final Playable actor = getActor();

        if (target == null || target.isDead()) {
            actor.setAggressionTarget(null);
        } else if (actor.getAggressionTarget() == null) {
            final GameObject actorStoredTarget = actor.getTarget();
            actor.setAggressionTarget(target);
            actor.setTarget(target);

            clearNextAction();
            // DS: агрессия только перекидывает видимую цель, но не обрывает текущую атаку/каст
			/*if (getIntention() == CtrlIntention.AI_INTENTION_ATTACK)
				setAttackTarget(target);
			switch(getIntention())
			{
				case AI_INTENTION_ATTACK:
					setAttackTarget(target);
					break;
				case AI_INTENTION_CAST:
					L2Skill skill = actor.getCastingSkill();
					if(skill == null)
						skill = _skill;
					if(skill != null && !skill.isUsingWhileCasting())
						switch(skill.getTargetType())
						{
							case TARGET_ONE:
							case TARGET_AREA:
							case TARGET_MULTIFACE:
							case TARGET_TUNNEL:
								setAttackTarget(target);
								actor.setCastingTarget(target);
								break;
						}
					break;
			}*/

            if (actorStoredTarget != target) {
                actor.sendPacket(new MyTargetSelected(target.getObjectId(), 0));
            }
        }
    }

    @Override
    public void Attack(final GameObject target, final boolean forceUse, final boolean dontMove) {
        final Playable actor = getActor();

        if (target.isCreature()) {
            if (actor.isActionsDisabled() || actor.isAttackingDisabled()) {
                // Если не можем атаковать или лук еще не перезарядился, то атаковать позже
                if (actor.isMoving)
                    clientStopMoving();
                setNextAction(NextAction.ATTACK, target, null, forceUse, false);
                actor.sendActionFailed();
                return;
            }
        }

        _dontMove = dontMove;
        _forceUse = forceUse;
        clearNextAction();
        setIntention(AI_INTENTION_ATTACK, target);
    }

    @Override
    public void Cast(final SkillEntry skillEntry, final Creature target, final boolean forceUse, final boolean dontMove) {
        final Playable actor = getActor();

        final Skill skill = skillEntry.getTemplate();
        // Если скилл альтернативного типа (например, бутылка на хп),
        // то он может использоваться во время каста других скиллов, или во время атаки, или на бегу.
        // Поэтому пропускаем дополнительные проверки.
        if (skill.altUse() || skill.isToggle()) {
            if ((skill.isToggle() || skill.isHandler()) && (actor.isOutOfControl() || actor.isStunned() || actor.isSleeping() || actor.isParalyzed() || actor.isDead())) {
                clientActionFailed();
            } else {
                actor.altUseSkill(skillEntry, target);
            }
            return;
        }

        // Если не можем кастовать, то использовать скилл позже
        if (actor.isActionsDisabled()) {
            setNextAction(NextAction.CAST, skillEntry, target, forceUse, dontMove, actor.isCastingNow());
            clientActionFailed();
            return;
        }

        _forceUse = forceUse;
        _dontMove = dontMove;
        _regular = false;

        clearNextAction();
        setIntention(AI_INTENTION_CAST, skillEntry, target);
    }

    @Override
    public Playable getActor() {
        return (Playable) super.getActor();
    }

    public enum NextAction {
        ATTACK,
        CAST,
        MOVE,
        REST,
        PICKUP,
        EQUIP,
        INTERACT,
        COUPLE_ACTION
    }

    protected class ThinkFollow extends RunnableImpl {
        @Override
        protected void runImpl() throws Exception {
            final Playable actor = getActor();

            if (getIntention() != AI_INTENTION_FOLLOW) {
                // Если пет прекратил преследование, меняем статус, чтобы не пришлось щелкать на кнопку следования 2 раза.
                if ((actor.isPet() || actor.isSummon()) && getIntention() == AI_INTENTION_ACTIVE) {
                    ((Servitor) actor).setFollowMode(false);
                }
                return;
            }

            if (!(_intention_arg0 instanceof Creature)) {
                setIntention(AI_INTENTION_ACTIVE);
                return;
            }

            final Creature target = (Creature) _intention_arg0;
            if (actor.getDistance(target) > 4000) {
                setIntention(AI_INTENTION_ACTIVE);
                return;
            }

            final Player player = actor.getPlayer();
            if (player == null || player.isLogoutStarted() || (actor.isPet() || actor.isSummon()) && player.getServitor() != actor) {
                setIntention(AI_INTENTION_ACTIVE);
                return;
            }

            final int offset = _intention_arg1 instanceof Integer ? (Integer) _intention_arg1 : 0;

            if (!actor.isAfraid() && !actor.isInRange(target, offset + 60) && (!actor.isFollow || actor.getFollowTarget() != target)) {
                actor.followToCharacter(target, offset, false);
            }
            _followTask = ThreadPoolManager.getInstance().schedule(this, 250L);
        }
    }

    protected class ExecuteFollow extends RunnableImpl {
        private final Creature _target;
        private final int _range;

        public ExecuteFollow(final Creature target, final int range) {
            _target = target;
            _range = range;
        }

        @Override
        protected void runImpl() {
            if (_target.isDoor()) {
                _actor.moveToLocation(Location.findFrontPosition(_target, getActor(), 10, 15), 1, true); // TODO[K] - временно. Но визуально уже в разы лучше :)
            } else {
                _actor.followToCharacter(_target, _range, true);
            }
        }
    }
}