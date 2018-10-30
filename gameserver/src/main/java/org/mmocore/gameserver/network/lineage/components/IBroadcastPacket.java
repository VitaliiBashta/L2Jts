package org.mmocore.gameserver.network.lineage.components;

import org.mmocore.gameserver.network.lineage.serverpackets.L2GameServerPacket;
import org.mmocore.gameserver.object.Player;

@FunctionalInterface
public interface IBroadcastPacket {
    L2GameServerPacket packet(Player player);
}
