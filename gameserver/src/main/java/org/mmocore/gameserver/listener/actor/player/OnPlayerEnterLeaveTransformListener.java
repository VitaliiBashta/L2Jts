package org.mmocore.gameserver.listener.actor.player;

import org.mmocore.gameserver.listener.PlayerListener;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.transformdata.TransformComponent;

/**
 * @author Mangol
 * @since 04.02.2016
 */
public interface OnPlayerEnterLeaveTransformListener extends PlayerListener {
    void onPlayerTransfromEnter(final Player player, final TransformComponent component);

    void onPlayerTransfromLeave(final Player player);
}
