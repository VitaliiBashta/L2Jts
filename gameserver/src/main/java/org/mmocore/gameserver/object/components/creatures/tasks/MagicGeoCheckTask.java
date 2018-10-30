package org.mmocore.gameserver.object.components.creatures.tasks;

import org.mmocore.commons.lang.reference.HardReference;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.geoengine.GeoEngine;
import org.mmocore.gameserver.object.Creature;

public class MagicGeoCheckTask extends RunnableImpl {
    private final HardReference<? extends Creature> _charRef;

    public MagicGeoCheckTask(final Creature cha) {
        _charRef = cha.getRef();
    }

    @Override
    public void runImpl() {
        final Creature character = _charRef.get();
        if (character == null) {
            return;
        }
        final Creature castingTarget = character.getCastingTarget();
        if (castingTarget == null) {
            return;
        }
        if (!GeoEngine.canSeeTarget(character, castingTarget, character.isFlying())) {
            return;
        }

        character._skillGeoCheckTask = null;
    }
}