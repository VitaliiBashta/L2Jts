package org.mmocore.gameserver.object.components.creatures.tasks;

import org.mmocore.commons.lang.reference.HardReference;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.object.Creature;

public class HitEndTask extends RunnableImpl {
    private final HardReference<? extends Creature> _charRef;

    public HitEndTask(final Creature creature) {
        _charRef = creature.getRef();
    }

    @Override
    protected void runImpl() {
        final Creature creature = _charRef.get();
        if (creature != null) {
            creature.getAI().notifyEvent(CtrlEvent.EVT_READY_TO_ACT);
        }
    }
}
