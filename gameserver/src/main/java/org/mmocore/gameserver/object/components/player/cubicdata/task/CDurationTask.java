package org.mmocore.gameserver.object.components.player.cubicdata.task;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.object.components.player.cubicdata.CubicComponent;

/**
 * Create by Mangol on 22.09.2015.
 */
public class CDurationTask extends RunnableImpl {
    private final CubicComponent _cub;

    public CDurationTask(final CubicComponent cub) {
        _cub = cub;
    }

    @Override
    public void runImpl() {
        _cub.delete(true);
    }
}
