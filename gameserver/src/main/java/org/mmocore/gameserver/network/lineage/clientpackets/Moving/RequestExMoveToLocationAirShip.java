package org.mmocore.gameserver.network.lineage.clientpackets.Moving;

import org.mmocore.gameserver.network.lineage.clientpackets.L2GameClientPacket;
import org.mmocore.gameserver.object.ClanAirShip;
import org.mmocore.gameserver.object.Player;

/**
 * Format: d d|dd
 */
public class RequestExMoveToLocationAirShip extends L2GameClientPacket {
    private int _moveType;
    private int _param1, _param2;

    @Override
    protected void readImpl() {
        _moveType = readD();
        switch (_moveType) {
            case 4: // AirShipTeleport
                _param1 = readD() + 1;
                break;
            case 0: // Free move
                _param1 = readD();
                _param2 = readD();
                break;
            case 2: // Up
                readD(); //?
                readD(); //?
                break;
            case 3: //Down
                readD(); //?
                readD(); //?
                break;
        }
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null || player.getBoat() == null || !player.getBoat().isClanAirShip()) {
            return;
        }

        final ClanAirShip airship = (ClanAirShip) player.getBoat();
        if (airship.getDriver() == player) {
            switch (_moveType) {
                case 4: // AirShipTeleport
                    airship.addTeleportPoint(player, _param1);
                    break;
                case 0: // Free move
                    if (!airship.isCustomMove()) {
                        break;
                    }
                    if (_param1 > -166168) // only gracia allowed
                    {
                        break;
                    }
                    airship.moveToLocation(airship.getLoc().setX(_param1).setY(_param2), 0, false);
                    break;
                case 2: // Up
                    if (!airship.isCustomMove()) {
                        break;
                    }
                    airship.moveToLocation(airship.getLoc().changeZ(100), 0, false);
                    break;
                case 3: // Down
                    if (!airship.isCustomMove()) {
                        break;
                    }
                    airship.moveToLocation(airship.getLoc().changeZ(-100), 0, false);
                    break;
            }
        }
    }
}