package org.mmocore.gameserver.object.components.creatures.tasks;

import org.mmocore.commons.lang.reference.HardReference;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

public class CastEndTimeTask extends RunnableImpl {
    private final HardReference<? extends Creature> _charRef;
    private final SkillEntry _skill;

    public CastEndTimeTask(Creature character, SkillEntry skill) {
        _charRef = character.getRef();
        _skill = skill;
    }

    @Override
    public void runImpl() {
        Creature character = _charRef.get();
        if (character == null) {
            return;
        }
        character.onCastEndTime(_skill);
    }
}