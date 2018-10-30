package org.mmocore.gameserver.object.components.creatures.tasks;

import org.mmocore.commons.lang.reference.HardReference;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

public class MagicUseTask extends RunnableImpl {
    public final boolean _forceUse;
    private final HardReference<? extends Creature> _charRef;

    public MagicUseTask(Creature cha, boolean forceUse) {
        _charRef = cha.getRef();
        _forceUse = forceUse;
    }

    @Override
    public void runImpl() {
        Creature character = _charRef.get();
        if (character == null)
            return;

        SkillEntry castingSkill = character.getCastingSkill();
        Creature castingTarget = character.getCastingTarget();
        if (castingSkill == null || castingTarget == null) {
            character.clearCastVars();
            return;
        }
        character.onMagicUseTimer(castingTarget, castingSkill, _forceUse);
    }
}