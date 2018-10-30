package org.mmocore.gameserver.object.components.player.custom.acp.task;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.object.components.player.custom.acp.action.interfaces.IUseAction;

/**
 * Create by Mangol on 30.12.2015.
 */
public class ActionTask extends RunnableImpl {
    private final IUseAction useAction;

    public ActionTask(final IUseAction useAction) {
        this.useAction = useAction;
    }

    @Override
    protected void runImpl() {
        if (useAction == null) {
            //TODO: Logger?
            return;
        }
        useAction.useAction();
    }
}
