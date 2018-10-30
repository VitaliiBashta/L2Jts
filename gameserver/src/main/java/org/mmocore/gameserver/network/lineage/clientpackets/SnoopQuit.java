package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.listener.actor.player.impl.SnoopPlayerSayListener;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.world.GameObjectsStorage;

public class SnoopQuit extends L2GameClientPacket {
    private int _targetObjectId;

    @Override
    protected void readImpl() {
        _targetObjectId = readD();
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }

        final Player target = GameObjectsStorage.getPlayer(_targetObjectId);
        if (target == null) {
            return;
        }

        for (final SnoopPlayerSayListener listener : target.getListeners().getListeners(SnoopPlayerSayListener.class))
            if (listener.getOwner() == player) {
                target.removeListener(listener);
                player.getPlayerVariables().remove(PlayerVariables.SNOOP_TARGET);
                break;
            }
    }
}