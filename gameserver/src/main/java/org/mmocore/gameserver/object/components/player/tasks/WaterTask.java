package org.mmocore.gameserver.object.components.player.tasks;

import org.mmocore.commons.lang.reference.HardReference;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;

public class WaterTask extends RunnableImpl {
    private final HardReference<Player> _playerRef;

    public WaterTask(Player player) {
        _playerRef = player.getRef();
    }

    @Override
    public void runImpl() {
        Player player = _playerRef.get();
        if (player == null) {
            return;
        }
        if (player.isDead() || !player.isInWater()) {
            player.stopWaterTask();
            return;
        }

        double reduceHp = player.getMaxHp() < 100 ? 1 : player.getMaxHp() / 100.0d;
        player.reduceCurrentHp(reduceHp, player, null, false, false, true, false, false, false, false);
        player.sendPacket(new SystemMessage(SystemMsg.YOU_RECEIVED_S1_DAMAGE_BECAUSE_YOU_WERE_UNABLE_TO_BREATHE).addNumber((long) reduceHp));
    }
}