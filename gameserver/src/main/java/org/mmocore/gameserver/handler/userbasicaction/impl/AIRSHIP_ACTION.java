package org.mmocore.gameserver.handler.userbasicaction.impl;

import org.jts.dataparser.data.holder.userbasicaction.UsetBasicActionOptionType;
import org.mmocore.gameserver.handler.userbasicaction.IUserBasicActionHandler;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ExAirShipTeleportList;
import org.mmocore.gameserver.object.Boat;
import org.mmocore.gameserver.object.ClanAirShip;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;

import java.util.Optional;
import java.util.OptionalInt;

/**
 * Create by Mangol on 20.10.2015.
 */
public class AIRSHIP_ACTION implements IUserBasicActionHandler {
    @Override
    public void useAction(final Player player, final int id, Optional<String> option, final OptionalInt useSkill, final Optional<GameObject> target, final boolean ctrlPressed, final boolean shiftPressed) {
        if (player.isOutOfControl() || player.isActionsDisabled()) {
            player.sendActionFailed();
            return;
        }
        if (!player.isInBoat()) {
            player.sendActionFailed();
            return;
        }
        final Boat boat = player.getBoat();
        ClanAirShip ship = null;
        if (boat.isClanAirShip()) {
            ship = (ClanAirShip) boat;
        }
        final UsetBasicActionOptionType optionAction = UsetBasicActionOptionType.valueOf(option.get());
        switch (optionAction) {
            case wheel: {
                if (boat.isClanAirShip() && !boat.isMoving) {
                    if (ship.getDriver() == null) {
                        ship.setDriver(player);
                    } else {
                        player.sendPacket(SystemMsg.ANOTHER_PLAYER_IS_PROBABLY_CONTROLLING_THE_TARGET);
                    }
                }
                break;
            }
            case leave_wheel: {
                if (boat.isClanAirShip() && player == ship.getDriver()) {
                    ship.setDriver(null);
                    player.broadcastCharInfo();
                }
                break;
            }
            case start_ship: {
                if (boat.isClanAirShip() && player == ship.getDriver() && boat.isDocked()) {
                    player.sendPacket(new ExAirShipTeleportList((ClanAirShip) player.getBoat()));
                }
                break;
            }
            case leave_ship: {
                if (boat.isAirShip() && boat.isDocked()) {
                    boat.oustPlayer(player, player.getBoat().getReturnLoc(), true);
                }
                break;
            }
        }
    }
}
