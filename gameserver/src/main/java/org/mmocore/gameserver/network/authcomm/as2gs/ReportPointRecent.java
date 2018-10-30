package org.mmocore.gameserver.network.authcomm.as2gs;

import org.mmocore.gameserver.network.authcomm.AuthServerCommunication;
import org.mmocore.gameserver.network.authcomm.ReceivablePacket;
import org.mmocore.gameserver.network.lineage.GameClient;
import org.mmocore.gameserver.object.Player;

public class ReportPointRecent extends ReceivablePacket {
    private String account;
    private int points;

    @Override
    public void readImpl() {
        account = readS();
        points = readD();
    }

    @Override
    protected void runImpl() {
        final GameClient client = AuthServerCommunication.getInstance().getAuthedClient(account);
        if (client != null) {
            final Player player = client.getActiveChar();
            if (player != null && player.isOnline())
                player.getBotPunishComponent().setReportsPoints(points);
        }
    }
}