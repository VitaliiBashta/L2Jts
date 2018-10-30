package org.mmocore.gameserver.listener.actor.player.impl;

import org.mmocore.gameserver.listener.actor.player.OnPlayerEnterLeaveTransformListener;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.community.ICommunityComponent;
import org.mmocore.gameserver.object.components.player.community.enums.DBState;
import org.mmocore.gameserver.object.components.player.transformdata.TransformComponent;

/**
 * @author Mangol
 * @since 04.02.2016
 */
public class PlayerEnterLeaveTransformListener implements OnPlayerEnterLeaveTransformListener {
    @Override
    public void onPlayerTransfromEnter(final Player player, final TransformComponent component) {
        final ICommunityComponent communityComponent = player.getCommunityComponent();
        if (communityComponent != null) {
            if (communityComponent.getLeaseTransformId() > 0) {
                communityComponent.refreshLeaseTransformStats();
            }
        }
    }

    @Override
    public void onPlayerTransfromLeave(final Player player) {
        final ICommunityComponent communityComponent = player.getCommunityComponent();
        if (communityComponent != null) {
            if (!player.isLogoutStarted() && communityComponent.getLeaseTransformId() > 0) {
                communityComponent.setLeaseTransformId(0, DBState.delete);
            }
        }
    }
}
