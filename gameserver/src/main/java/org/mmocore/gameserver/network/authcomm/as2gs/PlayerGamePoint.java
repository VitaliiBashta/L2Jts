package org.mmocore.gameserver.network.authcomm.as2gs;

import org.mmocore.gameserver.network.authcomm.AuthServerCommunication;
import org.mmocore.gameserver.network.authcomm.ReceivablePacket;
import org.mmocore.gameserver.network.lineage.serverpackets.PremiumShop.ExBR_GamePoint;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.world.GameObjectsStorage;

public class PlayerGamePoint extends ReceivablePacket {
    private int gamePoint;
    private int playerStoredId;

    @Override
    public void readImpl() {
        gamePoint = readD();
        playerStoredId = readD();
    }

    @Override
    public void runImpl() {
        final AuthServerCommunication client = getClient();
        if (client == null)
            return;

        final Player player = GameObjectsStorage.getPlayer(playerStoredId);
        if (player == null)
            return;

        player.sendPacket(new ExBR_GamePoint(player, gamePoint));
        player.getPremiumAccountComponent().setPremiumPoints(gamePoint);
    }
}