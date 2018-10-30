package org.mmocore.gameserver.ai;

import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.geoengine.GeoEngine;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.Skill.SkillType;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ActionFail;
import org.mmocore.gameserver.network.lineage.serverpackets.Moving.ExRotation;
import org.mmocore.gameserver.network.lineage.serverpackets.SocialAction;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.attachments.FlagItemAttachment;
import org.mmocore.gameserver.skills.SkillEntry;

import static org.mmocore.gameserver.ai.CtrlIntention.AI_INTENTION_ACTIVE;
import static org.mmocore.gameserver.ai.CtrlIntention.AI_INTENTION_ATTACK;

public class PlayerAI extends PlayableAI {
    public PlayerAI(final Player actor) {
        super(actor);
    }

    @Override
    protected void onIntentionRest() {
        changeIntention(CtrlIntention.AI_INTENTION_REST, null, null);
        setAttackTarget(null);
        clientStopMoving();
    }

    @Override
    protected void onIntentionActive() {
        clearNextAction();
        changeIntention(AI_INTENTION_ACTIVE, null, null);
    }

    @Override
    public void onIntentionInteract(final GameObject object) {
        final Player actor = getActor();

        if (actor.getSittingTask()) {
            setNextAction(NextAction.INTERACT, object, null, false, false);
            return;
        } else if (actor.isSitting()) {
            if (actor.isInStoreMode()) {
                actor.sendPacket(SystemMsg.YOU_CANNOT_MOVE_WHILE_SITTING);
            }
            clientActionFailed();
            return;
        }

        super.onIntentionInteract(object);
    }

    @Override
    public void onIntentionPickUp(final GameObject object) {
        final Player actor = getActor();

        if (actor.getSittingTask()) {
            setNextAction(NextAction.PICKUP, object, null, false, false);
            return;
        } else if (actor.isSitting()) {
            actor.sendPacket(SystemMsg.YOU_CANNOT_MOVE_WHILE_SITTING);
            clientActionFailed();
            return;
        }
        super.onIntentionPickUp(object);
    }

    @Override
    protected void thinkAttack(final boolean checkRange) {
        final Player actor = getActor();

        if (actor.isInFlyingTransform()) {
            setIntention(AI_INTENTION_ACTIVE);
            return;
        }

        final FlagItemAttachment attachment = actor.getActiveWeaponFlagAttachment();
        if (attachment != null && !attachment.canAttack(actor)) {
            setIntention(AI_INTENTION_ACTIVE);
            actor.sendActionFailed();
            return;
        }

        if (actor.isFrozen()) {
            setIntention(AI_INTENTION_ACTIVE);
            actor.sendPacket(SystemMsg.YOU_CANNOT_MOVE_WHILE_FROZEN, ActionFail.STATIC);
            return;
        }

        super.thinkAttack(checkRange);
    }

    @Override
    protected void thinkCast(final boolean checkRange) {
        final Player actor = getActor();

        final FlagItemAttachment attachment = actor.getActiveWeaponFlagAttachment();
        if (attachment != null && !attachment.canCast(actor, _skillEntry)) {
            setIntention(AI_INTENTION_ACTIVE);
            actor.sendActionFailed();
            return;
        }

        if (actor.isFrozen()) {
            setIntention(AI_INTENTION_ACTIVE);
            actor.sendPacket(SystemMsg.YOU_CANNOT_MOVE_WHILE_FROZEN, ActionFail.STATIC);
            return;
        }

        super.thinkCast(checkRange);
    }

    @Override
    protected void thinkCoupleAction(final Player target, final Integer socialId) {
        final Player actor = getActor();
        if (target == null || !target.isOnline()) {
            actor.sendPacket(SystemMsg.THE_COUPLE_ACTION_WAS_CANCELLED);
            return;
        }

        if (false || !actor.isInRange(target, 50) || actor.isInRange(target, 20) || actor.getReflection() != target.getReflection() || !GeoEngine.canSeeTarget(actor, target, false)) {
            target.sendPacket(SystemMsg.THE_COUPLE_ACTION_WAS_CANCELLED);
            actor.sendPacket(SystemMsg.THE_COUPLE_ACTION_WAS_CANCELLED);
            return;
        }
        if (_forceUse) // служит только для флага что б активировать у другого игрока социалку
        {
            target.getAI().setIntention(CtrlIntention.AI_INTENTION_COUPLE_ACTION, actor, socialId);
        }
        //
        final int heading = actor.calcHeading(target.getX(), target.getY());
        actor.setHeading(heading);
        actor.broadcastPacket(new ExRotation(actor.getObjectId(), heading));
        //
        actor.broadcastPacket(new SocialAction(actor.getObjectId(), socialId));
    }

    @Override
    public void Attack(final GameObject target, final boolean forceUse, final boolean dontMove) {
        final Player actor = getActor();

        if (actor.isInFlyingTransform()) {
            actor.sendActionFailed();
            return;
        }

        if (System.currentTimeMillis() - actor.getLastAttackPacket() < ServerConfig.ATTACK_PACKET_DELAY) {
            actor.sendActionFailed();
            return;
        }

        actor.setLastAttackPacket();

        if (actor.getSittingTask()) {
            setNextAction(NextAction.ATTACK, target, null, forceUse, false);
            return;
        } else if (actor.isSitting()) {
            actor.sendPacket(SystemMsg.YOU_CANNOT_MOVE_WHILE_SITTING);
            clientActionFailed();
            return;
        }

        super.Attack(target, forceUse, dontMove);
    }

    @Override
    public void Cast(final SkillEntry skillEntry, final Creature target, final boolean forceUse, final boolean dontMove) {
        final Player actor = getActor();

        final Skill skill = skillEntry.getTemplate();
        if (!skill.altUse() && !skill.isToggle() && !(skill.getSkillType() == SkillType.CRAFT && AllSettingsConfig.ALLOW_TALK_WHILE_SITTING))
        // Если в этот момент встаем, то использовать скилл когда встанем
        {
            if (actor.getSittingTask()) {
                setNextAction(NextAction.CAST, skillEntry, target, forceUse, dontMove);
                clientActionFailed();
                return;
            } else if (skill.getSkillType() == SkillType.SUMMON && actor.getPrivateStoreType() != Player.STORE_PRIVATE_NONE) {
                actor.sendPacket(SystemMsg.YOU_CANNOT_SUMMON_DURING_A_TRADE_OR_WHILE_USING_A_PRIVATE_STORE);
                clientActionFailed();
                return;
            }
            // если сидим - скиллы нельзя использовать
            else if (actor.isSitting()) {
                if (skill.getSkillType() == SkillType.TRANSFORMATION) {
                    actor.sendPacket(SystemMsg.YOU_CANNOT_TRANSFORM_WHILE_SITTING);
                } else {
                    actor.sendPacket(SystemMsg.YOU_CANNOT_MOVE_WHILE_SITTING);
                }

                clientActionFailed();
                return;
            }
        }

        super.Cast(skillEntry, target, forceUse, dontMove);
    }

    @Override
    protected void onEvtReadyToAct() {
        if (getIntention() == AI_INTENTION_ATTACK) {
            final Creature target = getAttackTarget();
            if (target != null && target.isPlayable() && !((Playable) target).isAttackable(getActor(), false, false) && !isMySummon(target)) // false, true по офу делать только один физ удар в режиме флага
            {
                changeIntention(AI_INTENTION_ACTIVE, null, null);
            }
        }

        super.onEvtReadyToAct();
    }

    private boolean isMySummon(Creature target) {
        return target.getPlayer() == getActor();
    }

    @Override
    public Player getActor() {
        return (Player) super.getActor();
    }
}