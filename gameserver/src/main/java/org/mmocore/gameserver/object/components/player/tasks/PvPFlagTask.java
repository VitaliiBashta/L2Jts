package org.mmocore.gameserver.object.components.player.tasks;

import org.mmocore.commons.lang.reference.HardReference;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.configuration.config.PvpConfig;
import org.mmocore.gameserver.object.Player;

public class PvPFlagTask extends RunnableImpl {
    private final HardReference<Player> _playerRef;

    public PvPFlagTask(Player player) {
        _playerRef = player.getRef();
    }

    @Override
    public void runImpl() {
        Player player = _playerRef.get();
        if (player == null) {
            return;
        }

        long diff = Math.abs(System.currentTimeMillis() - player.getlastPvpAttack());
        if (diff > PvpConfig.PVP_TIME) {
            player.stopPvPFlag();
        } else if (diff > PvpConfig.PVP_TIME - 20000) {
            player.updatePvPFlag(2);
        } else {
            player.updatePvPFlag(1);
        }
    }
}