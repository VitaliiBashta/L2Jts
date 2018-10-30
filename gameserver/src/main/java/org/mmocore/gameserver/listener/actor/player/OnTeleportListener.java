package org.mmocore.gameserver.listener.actor.player;

import org.mmocore.gameserver.listener.PlayerListener;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.object.Player;

@FunctionalInterface
public interface OnTeleportListener extends PlayerListener {
    void onTeleport(Player player, int x, int y, int z, Reflection reflection);
}
