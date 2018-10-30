package org.mmocore.gameserver.handler.userbasicaction.impl;

import org.mmocore.gameserver.handler.userbasicaction.IUserBasicActionHandler;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;

import java.util.Optional;
import java.util.OptionalInt;

/**
 * Create by Mangol on 20.10.2015.
 */
public class WALK_RUN implements IUserBasicActionHandler {
    @Override
    public void useAction(final Player player, final int id, final Optional<String> option, final OptionalInt useSkill, final Optional<GameObject> target, final boolean ctrlPressed, final boolean shiftPressed) {
        if (player.isOutOfControl() || player.isActionsDisabled()) {
            player.sendActionFailed();
            return;
        }
        if (player.isSitting() || player.isFakeDeath()) {
            player.sendActionFailed();
            return;
        }
        if (player.isRunning()) {
            player.setWalking();
        } else {
            player.setRunning();
        }
    }
}
