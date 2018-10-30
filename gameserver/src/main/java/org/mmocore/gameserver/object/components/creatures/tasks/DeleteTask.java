package org.mmocore.gameserver.object.components.creatures.tasks;

import org.mmocore.commons.lang.reference.HardReference;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.object.Creature;

public class DeleteTask extends RunnableImpl {
    private final HardReference<? extends Creature> reference;

    public DeleteTask(Creature c) {
        reference = c.getRef();
    }

    @Override
    public void runImpl() {
        Creature c = reference.get();

        if (c != null) {
            c.deleteMe();
        }
    }
}