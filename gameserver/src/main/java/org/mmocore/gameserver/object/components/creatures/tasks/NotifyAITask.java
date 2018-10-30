package org.mmocore.gameserver.object.components.creatures.tasks;

import org.mmocore.commons.lang.reference.HardReference;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.object.Creature;

public class NotifyAITask implements Runnable {
    private final CtrlEvent _evt;
    private final Object _agr0;
    private final Object _agr1;
    private final Object _arg2;
    private final HardReference<? extends Creature> _charRef;

    public NotifyAITask(Creature cha, CtrlEvent evt, Object agr0, Object agr1, Object agr2) {
        _charRef = cha.getRef();
        _evt = evt;
        _agr0 = agr0;
        _agr1 = agr1;
        _arg2 = agr2;
    }

    public NotifyAITask(Creature cha, CtrlEvent evt) {
        this(cha, evt, null, null, null);
    }

    public NotifyAITask(Creature cha, CtrlEvent evt, Object arg0, Object arg1) {
        this(cha, evt, arg0, arg1, null);
    }

    @Override
    public void run() {
        Creature character = _charRef.get();
        if (character == null || !character.hasAI()) {
            return;
        }

        character.getAI().notifyEvent(_evt, _agr0, _agr1, _arg2);
    }
}