package org.mmocore.gameserver.skills.effects;

import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.hash.TObjectIntHashMap;
import org.mmocore.commons.lang.reference.HardReference;
import org.mmocore.gameserver.listener.actor.OnCurrentHpDamageListener;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;


public final class EffectCurseOfLifeFlow extends Effect {
    private final TObjectIntHashMap<HardReference<? extends Creature>> _damageList = new TObjectIntHashMap<>();
    private CurseOfLifeFlowListener _listener;

    public EffectCurseOfLifeFlow(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public void onStart() {
        super.onStart();
        _listener = new CurseOfLifeFlowListener();
        _effected.addListener(_listener);
    }

    @Override
    public void onExit() {
        super.onExit();
        _effected.removeListener(_listener);
        _listener = null;
    }

    @Override
    public boolean onActionTime() {
        if (_effected.isDead()) {
            return false;
        }

        for (TObjectIntIterator<HardReference<? extends Creature>> iterator = _damageList.iterator(); iterator.hasNext(); ) {
            iterator.advance();
            final Creature damager = iterator.key().get();
            if (damager == null || damager.isDead() || damager.isCurrentHpFull()) {
                continue;
            }

            final int damage = iterator.value();
            if (damage <= 0) {
                continue;
            }

            final double max_heal = calc();
            final double heal = Math.min(damage, max_heal);
            final double newHp = Math.min(damager.getCurrentHp() + heal, damager.getMaxHp());

            damager.sendPacket(new SystemMessage(SystemMsg.S1_HP_HAS_BEEN_RESTORED).addNumber((long) (newHp - damager.getCurrentHp())));
            damager.setCurrentHp(newHp, false);
        }

        _damageList.clear();

        return true;
    }

    private class CurseOfLifeFlowListener implements OnCurrentHpDamageListener {
        @Override
        public void onCurrentHpDamage(final Creature actor, final double damage, final Creature attacker, final SkillEntry skill) {
            if (attacker.equals(actor) || attacker.equals(_effected)) {
                return;
            }
            final int old_damage = _damageList.get(attacker.getRef());
            _damageList.put(attacker.getRef(), old_damage == 0 ? (int) damage : old_damage + (int) damage);
        }
    }
}