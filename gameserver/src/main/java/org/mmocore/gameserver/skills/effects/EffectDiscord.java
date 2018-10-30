package org.mmocore.gameserver.skills.effects;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.model.entity.events.impl.SiegeEvent;
import org.mmocore.gameserver.model.instances.SummonInstance;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.ArrayList;
import java.util.List;


public class EffectDiscord extends Effect {
    public EffectDiscord(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public boolean checkCondition() {
        final int skilldiff = _effected.getLevel() - _skill.getTemplate().getMagicLevel();
        final int lvldiff = _effected.getLevel() - _effector.getLevel();
        if (skilldiff > 10 || skilldiff > 5 && Rnd.chance(30) || Rnd.chance(Math.abs(lvldiff) * 2)) {
            return false;
        }

        final boolean multitargets = _skill.getTemplate().isAoE();

        if (!_effected.isMonster()) {
            if (!multitargets) {
                getEffector().sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
            }
            return false;
        }

        if (_effected.isFearImmune() || _effected.isRaid()) {
            if (!multitargets) {
                getEffector().sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
            }
            return false;
        }

        // Discord нельзя наложить на осадных саммонов
        final Player player = _effected.getPlayer();
        if (player != null) {
            final SiegeEvent<?, ?> siegeEvent = player.getEvent(SiegeEvent.class);
            if (_effected.isSummon() && siegeEvent != null && siegeEvent.containsSiegeSummon((SummonInstance) _effected)) {
                if (!multitargets) {
                    getEffector().sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
                }
                return false;
            }
        }

        if (_effected.isInZonePeace()) {
            if (!multitargets) {
                getEffector().sendPacket(SystemMsg.YOU_MAY_NOT_ATTACK_THIS_TARGET_IN_A_PEACEFUL_ZONE);
            }
            return false;
        }

        return super.checkCondition();
    }

    @Override
    public void onStart() {
        super.onStart();
        _effected.startConfused();

        onActionTime();
    }

    @Override
    public void onExit() {
        super.onExit();

        if (!_effected.stopConfused()) {
            _effected.abortAttack(true, true);
            _effected.abortCast(true, true);
            _effected.stopMove();
            _effected.getAI().setAttackTarget(null);
            _effected.setWalking();
            _effected.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        }
    }

    @Override
    public boolean onActionTime() {
        final List<Creature> targetList = new ArrayList<>();

        for (final Creature character : _effected.getAroundCharacters(900, 200)) {
            if (character.isNpc() && !character.equals(getEffected())) {
                targetList.add(character);
            }
        }

        // if there is no target, exit function
        if (targetList.isEmpty()) {
            return true;
        }

        // Choosing randomly a new target
        final Creature target = targetList.get(Rnd.get(targetList.size()));

        // Attacking the target
        _effected.setRunning();
        _effected.getAI().Attack(target, true, false);

        return false;
    }
}