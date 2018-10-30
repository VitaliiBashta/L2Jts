package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.configuration.config.OtherConfig;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.world.GameObjectsStorage;

public final class SendStatus extends L2GameServerPacket {
    private static final long MIN_UPDATE_PERIOD = 30000;
    private static int online_players = 0;
    private static int max_online_players = 0;
    private static int online_priv_store = 0;
    private static long last_update = 0;

    public SendStatus() {
        if (System.currentTimeMillis() - last_update < MIN_UPDATE_PERIOD) {
            return;
        }
        last_update = System.currentTimeMillis();
        int i = 0;
        int j = 0;
        for (final Player player : GameObjectsStorage.getPlayers()) {
            i++;
            if (player.isInStoreMode() && (!OtherConfig.SENDSTATUS_TRADE_JUST_OFFLINE || player.isInOfflineMode())) {
                j++;
            }
        }
        online_players = i;
        online_priv_store = (int) Math.floor(j * OtherConfig.SENDSTATUS_TRADE_MOD);
        max_online_players = Math.max(max_online_players, online_players);
    }

    @Override
    protected final void writeImpl() {
        writeC(0x00); // Packet ID
        writeD(0x01); // World ID
        writeD(max_online_players); // Max Online
        writeD(online_players + 500); // Current Online
        writeD(online_players + 500); // Current Online
        writeD(online_priv_store); // Priv.Store Chars

        // SEND TRASH
        writeD(0x002C0030);
        for (int x = 0; x < 10; x++) {
            writeH(41 + Rnd.get(17));
        }
        writeD(43 + Rnd.get(17));
        final int z = 36219 + Rnd.get(1987);
        writeD(z);
        writeD(z);
        writeD(37211 + Rnd.get(2397));
        writeD(0x00);
        writeD(0x02);
    }
}