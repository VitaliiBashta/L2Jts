package org.mmocore.gameserver.handler.userbasicaction.impl;

import org.mmocore.gameserver.handler.userbasicaction.IUserBasicActionHandler;
import org.mmocore.gameserver.model.instances.StaticObjectInstance;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;

import java.util.Optional;
import java.util.OptionalInt;

/**
 * Create by Mangol on 20.10.2015.
 */
public class SIT_STAND implements IUserBasicActionHandler {
    @Override
    public void useAction(final Player player, final int id, final Optional<String> option, final OptionalInt useSkill, final Optional<GameObject> target, final boolean ctrlPressed, final boolean shiftPressed) {
        if (player.isFakeDeath()) {
            player.breakFakeDeath();
            player.updateEffectIcons();
            return;
        }
		/*if(player.isOutOfControl() || player.isActionsDisabled())
		{
			player.sendActionFailed();
			return;
		}*/
        // Сесть/встать
        // На страйдере нельзя садиться
        if (player.isMounted()) {
            player.sendActionFailed();
            return;
        }
        if (!player.isSitting()) {
            if (target.isPresent() && target.get() instanceof StaticObjectInstance && ((StaticObjectInstance) target.get()).getType() == 1 &&
                    player.getDistance3D(target.get()) <= player.getInteractDistance(target.get())) {
                player.sitDown((StaticObjectInstance) target.get());
            } else {
                player.sitDown(null);
            }
        } else {
            player.standUp();
        }
    }
}
