package org.mmocore.gameserver.object.components.player.cubicdata.task;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.cubicdata.action.ICActionCubic;

/**
 * Create by Mangol on 22.09.2015.
 */
public class CActionTask extends RunnableImpl {
    private final Player _player;
    private final ICActionCubic _action;

    public CActionTask(final Player player, final ICActionCubic action) {
        _player = player;
        _action = action;
    }

    @Override
    public void runImpl() {
        if (_player == null) {
            return;
        }
        _action.useAction(_player);
    }

    public Player getPlayer() {
        return _player;
    }
}
