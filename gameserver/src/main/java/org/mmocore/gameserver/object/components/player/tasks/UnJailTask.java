package org.mmocore.gameserver.object.components.player.tasks;

import org.mmocore.commons.lang.reference.HardReference;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.object.Player;

public class UnJailTask extends RunnableImpl {
    private final HardReference<Player> _playerRef;

    public UnJailTask(Player player) {
        _playerRef = player.getRef();
    }

    @Override
    public void runImpl() {
        Player player = _playerRef.get();
        if (player == null) {
            return;
        }
        player.unblock();
        player.standUp();
        player.teleToLocation(17817, 170079, -3530, ReflectionManager.DEFAULT);
    }
}