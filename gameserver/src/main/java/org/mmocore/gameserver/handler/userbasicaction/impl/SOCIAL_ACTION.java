package org.mmocore.gameserver.handler.userbasicaction.impl;

import org.mmocore.gameserver.handler.userbasicaction.IUserBasicActionHandler;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SocialAction;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;

import java.util.Optional;
import java.util.OptionalInt;

/**
 * Create by Mangol on 20.10.2015.
 */
public class SOCIAL_ACTION implements IUserBasicActionHandler {
    @Override
    public void useAction(final Player player, final int id, final Optional<String> option, final OptionalInt useSkill, final Optional<GameObject> target, final boolean ctrlPressed, final boolean shiftPressed) {
        if (player.isOutOfControl() || player.isActionsDisabled() || player.isSitting() ||
                player.getPrivateStoreType() != Player.STORE_PRIVATE_NONE || player.isProcessingRequest()) {
            player.sendActionFailed();
            return;
        }
        if (player.isFishing()) {
            player.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING_2);
            return;
        }
        final int optionAction = Integer.parseInt(option.get());
        player.broadcastPacket(new SocialAction(player.getObjectId(), optionAction));
    }
}
