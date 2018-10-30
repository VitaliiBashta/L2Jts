package org.mmocore.gameserver.object.components.creatures.tasks;

import org.mmocore.commons.lang.reference.HardReference;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

public class AltMagicUseTask extends RunnableImpl {
    public final SkillEntry _skill;
    private final HardReference<? extends Creature> _charRef, _targetRef;

    public AltMagicUseTask(Creature character, Creature target, SkillEntry skill) {
        _charRef = character.getRef();
        _targetRef = target.getRef();
        _skill = skill;
    }

    @Override
    public void runImpl() {
        Creature cha, target;
        if ((cha = _charRef.get()) == null || (target = _targetRef.get()) == null) {
            return;
        }
        cha.altOnMagicUseTimer(target, _skill);
    }
}