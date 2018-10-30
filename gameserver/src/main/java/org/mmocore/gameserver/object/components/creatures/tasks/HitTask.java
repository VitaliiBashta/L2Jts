package org.mmocore.gameserver.object.components.creatures.tasks;

import org.mmocore.commons.lang.reference.HardReference;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.model.base.AttackType;
import org.mmocore.gameserver.object.Creature;

public class HitTask extends RunnableImpl {
    final boolean _crit;
    final boolean _miss;
    final boolean _shld;
    final boolean _soulshot;
    final boolean _unchargeSS;
    final int _damage;
    private final AttackType attackType;
    private final HardReference<? extends Creature> _charRef, _targetRef;

    public HitTask(Creature cha, Creature target, AttackType attackType, int damage, boolean crit, boolean miss, boolean soulshot,
                   boolean shld, boolean unchargeSS) {
        _charRef = cha.getRef();
        _targetRef = target.getRef();
        this.attackType = attackType;
        _damage = damage;
        _crit = crit;
        _shld = shld;
        _miss = miss;
        _soulshot = soulshot;
        _unchargeSS = unchargeSS;
    }

    @Override
    public void runImpl() {
        Creature character, target;
        if ((character = _charRef.get()) == null || (target = _targetRef.get()) == null) {
            return;
        }

        if (character.isAttackAborted()) {
            return;
        }

        character.onHitTimer(target, attackType, _damage, _crit, _miss, _soulshot, _shld, _unchargeSS);
    }
}