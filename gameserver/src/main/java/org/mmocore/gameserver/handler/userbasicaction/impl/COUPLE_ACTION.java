package org.mmocore.gameserver.handler.userbasicaction.impl;

import org.mmocore.gameserver.geoengine.GeoEngine;
import org.mmocore.gameserver.handler.userbasicaction.IUserBasicActionHandler;
import org.mmocore.gameserver.model.Request;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ExAskCoupleAction;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;

import java.util.Optional;
import java.util.OptionalInt;

/**
 * Create by Mangol on 20.10.2015.
 */
public class COUPLE_ACTION implements IUserBasicActionHandler {
    @Override
    public void useAction(final Player player, final int id, final Optional<String> option, final OptionalInt useSkill, final Optional<GameObject> target, final boolean ctrlPressed, final boolean shiftPressed) {
        if (player.isOutOfControl() || player.isActionsDisabled() || player.isSitting()) {
            player.sendActionFailed();
            return;
        }
        if (!target.isPresent() || !target.get().isPlayer()) {
            player.sendActionFailed();
            return;
        }
        final Player pcTarget = target.get().getPlayer();
        if (pcTarget.isProcessingRequest() && pcTarget.getRequest().isTypeOf(Request.L2RequestType.COUPLE_ACTION)) {
            player.sendPacket(new SystemMessage(SystemMsg.C1_IS_ALREADY_PARTICIPATING_IN_A_COUPLE_ACTION_AND_CANNOT_BE_REQUESTED_FOR_ANOTHER_COUPLE_ACTION).addName(pcTarget));
            return;
        }
        if (pcTarget.isProcessingRequest()) {
            player.sendPacket(new SystemMessage(SystemMsg.C1_IS_ON_ANOTHER_TASK).addName(pcTarget));
            return;
        }
        if (!player.isInRange(pcTarget, 300) || player.isInRange(pcTarget, 25) || player.getTargetId() == player.getObjectId() ||
                !GeoEngine.canSeeTarget(player, pcTarget, false)) {
            player.sendPacket(SystemMsg.THE_REQUEST_CANNOT_BE_COMPLETED_BECAUSE_THE_TARGET_DOES_NOT_MEET_LOCATION_REQUIREMENTS);
            return;
        }
        if (!player.checkCoupleAction(pcTarget)) {
            return;
        }
        new Request(Request.L2RequestType.COUPLE_ACTION, player, pcTarget).setTimeout(10000L);
        player.sendPacket(new SystemMessage(SystemMsg.YOU_HAVE_REQUESTED_A_COUPLE_ACTION_WITH_C1).addName(pcTarget));
        final int optionAction = Integer.parseInt(option.get());
        pcTarget.sendPacket(new ExAskCoupleAction(player.getObjectId(), optionAction));
    }
}
