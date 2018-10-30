package org.mmocore.gameserver.object.components.creatures.tasks;

import org.mmocore.commons.lang.reference.HardReference;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.network.lineage.serverpackets.MagicSkillLaunched;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.Util;

import java.util.List;

public class MagicLaunchedTask extends RunnableImpl {
    public final boolean _forceUse;
    private final HardReference<? extends Creature> _charRef;

    public MagicLaunchedTask(Creature cha, boolean forceUse) {
        _charRef = cha.getRef();
        _forceUse = forceUse;
    }

    @Override
    public void runImpl() {
        Creature character = _charRef.get();
        if (character == null) {
            return;
        }
        SkillEntry castingSkill = character.getCastingSkill();
        Creature castingTarget = character.getCastingTarget();
        if (castingSkill == null || castingTarget == null) {
            character.clearCastVars();
            return;
        }
        List<Creature> targets = castingSkill.getTemplate().getTargets(character, castingTarget, _forceUse);
        int[] intObjIds = Util.objectToIntArray(targets);
        character.broadcastPacket(new MagicSkillLaunched(character.getObjectId(), castingSkill.getDisplayId(), castingSkill.getDisplayLevel(), intObjIds));
    }
}