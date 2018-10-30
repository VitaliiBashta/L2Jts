package org.mmocore.gameserver.object.components.player.tasks;

import org.mmocore.commons.lang.reference.HardReference;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.object.Player;

public class MountFeedTask extends RunnableImpl {
    private final HardReference<Player> playerRef;

    public MountFeedTask(Player player) {
        playerRef = player.getRef();
    }

    @Override
    public void runImpl() {
        final Player player = playerRef.get();
        if (player == null) {
            return;
        }

        player.updateMountFed();
    }
}