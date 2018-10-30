package org.mmocore.gameserver.network.lineage.clientpackets.Moving;


import org.mmocore.gameserver.configuration.config.GeodataConfig;
import org.mmocore.gameserver.geoengine.GeoEngine;
import org.mmocore.gameserver.network.lineage.clientpackets.L2GameClientPacket;
import org.mmocore.gameserver.network.lineage.serverpackets.Moving.CharMoveToLocation;
import org.mmocore.gameserver.network.lineage.serverpackets.Moving.FlyToLocation;
import org.mmocore.gameserver.object.Boat;
import org.mmocore.gameserver.object.ClanAirShip;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Location;

public class ValidatePosition extends L2GameClientPacket {
    private final Location _loc = new Location();

    private int _boatObjectId;
    private Location _lastClientPosition;
    private Location _lastServerPosition;

    public static void correctPositionWithMovePacket(Player player, boolean broadcastToAll) {
        if (player == null)
            return;
        CharMoveToLocation movePacket = new CharMoveToLocation(player.getObjectId(), player.getLastClientPosition(),
                player.getLastServerPosition());
        if (broadcastToAll)
            player.broadcastPacket(movePacket);
        else
            player.sendPacket(movePacket);
    }

    public static void correctPosition(Player player, CorrectType type) {
        if (player == null)
            return;
        if (player.getLastServerPosition() != null && player.getLastClientPosition() != null)
            if (player.getLastServerPosition().distance3D(player.getLastClientPosition()) > GeodataConfig.maxAsyncCoordDiffBeforeAttack) {
                switch (type) {
                    case FlyCorrect:
                        player.sendPacket(new FlyToLocation(player, player.getLastServerPosition(), FlyToLocation.FlyType.DUMMY));
                        break;
                    case ValidateCorrect:
                        player.validateLocation(1);
                        break;
                    case MoveCorrect:
                        correctPositionWithMovePacket(player, true);
                        break;
                }
            }
    }

    /**
     * packet type id 0x48
     * format:		cddddd
     */
    @Override
    protected void readImpl() {
        _loc.x = readD();
        _loc.y = readD();
        _loc.z = readD();
        _loc.h = readD();
        _boatObjectId = readD();
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (activeChar.isTeleporting() || activeChar.isInObserverMode()) {
            return;
        }

        _lastClientPosition = activeChar.getLastClientPosition();
        _lastServerPosition = activeChar.getLastServerPosition();

        if (_lastClientPosition == null) {
            _lastClientPosition = activeChar.getLoc();
        }
        if (_lastServerPosition == null) {
            _lastServerPosition = activeChar.getLoc();
        }

        if (activeChar.isAirshipCaptain()) {
            return;
        }

        if (!activeChar.isInBoat() && activeChar.getX() == 0 && activeChar.getY() == 0 && activeChar.getZ() == 0) {
            correctPosition(activeChar);
            _loc.set(activeChar.getLoc());
        }

        if (activeChar.isInFlyingTransform() && (activeChar.getX() > -166168 || activeChar.getZ() <= 0 || activeChar.getZ() >= 6000)) {
            activeChar.stopTransformation();
            return;
        }
		/*
		if(_boatObjectId > 0)
		{
			final Boat boat = BoatHolder.getInstance().getBoat(_boatObjectId);
			if(boat != null && activeChar.getBoat() == boat)
			{
				activeChar.setHeading(_loc.h);
				boat.validateLocationPacket(activeChar);
			}
			activeChar.setLastClientPosition(_loc.setH(activeChar.getHeading()));
			activeChar.setLastServerPosition(activeChar.getLoc());
			return;
		}
		*/
        final Boat boat = activeChar.getBoat();
        if (boat != null) {
            if (boat.isClanAirShip() && ((ClanAirShip) boat).getDriver() == activeChar) // у драйвера _boatObjectId = 0
            {
                return;
            }
            if (boat.getObjectId() == _boatObjectId) // DS: перемещение на корабле не реализовано, сразу задается конечная точка, поэтому рассылаем валидейт только при очень грубых ошибках
            {
                Location boatLoc = activeChar.getInBoatPosition();
                if (boatLoc != null && (boatLoc.distance(_loc) > 1024 || Math.abs(_loc.z - boatLoc.z) > 256)) {
                    activeChar.sendPacket(boat.validateLocationPacket(activeChar));
                }
            }
            return;
        }

        final double diff = _loc.distance3D(activeChar.getLoc()); // activeChar.getDistance(_loc.x, _loc.y)
        final int h = _lastServerPosition.z - activeChar.getZ();
        if (!activeChar.isFlying() && !activeChar.isInWater() && h >= activeChar.getPlayerTemplateComponent().getPcSafeFallHeight())// Пока падаем, высоту не корректируем
        {
            activeChar.falling(h);
        }
        /**
         int dz = Math.abs(_loc.z - activeChar.getZ());
         int h = _lastServerPosition.z - activeChar.getZ();
         //int pingDiff = ((getClient().getLastPing() * activeChar.getMoveSpeed()) / 100) * 5;

         // Если мы уже падаем, то отключаем все валидейты
         if(activeChar.isFalling())
         {
         diff = 0;
         dz = 0;
         h = 0;
         }

         if(!activeChar.isInWater() && h >= activeChar.getPlayerTemplateComponent().getPcSafeFallHeight())// Пока падаем, высоту не корректируем
         {
         activeChar.falling(h);
         }
         else if(dz >= (activeChar.isFlying() ? 1024 : 512))
         {
         if(activeChar.getIncorrectValidateCount() >= 3)
         {
         activeChar.teleToClosestTown();
         }
         else
         {
         activeChar.teleToLocation(activeChar.getLoc());
         activeChar.setIncorrectValidateCount(activeChar.getIncorrectValidateCount() + 1);
         }
         }
         else if(!activeChar.isInWater() && dz >= 256) // FIXME[K]
         {
         activeChar.validateLocation(0);
         }
         else if(_loc.z < -30000 || _loc.z > 30000)
         {
         if(activeChar.getIncorrectValidateCount() >= 3)
         {
         activeChar.teleToClosestTown();
         }
         else
         {
         correctPosition(activeChar);
         activeChar.setIncorrectValidateCount(activeChar.getIncorrectValidateCount() + 1);
         }
         }
         else if(diff > 1024)
         {
         if(activeChar.getIncorrectValidateCount() >= 3)
         {
         activeChar.teleToClosestTown();
         }
         else
         {
         activeChar.teleToLocation(activeChar.getLoc());
         activeChar.setIncorrectValidateCount(activeChar.getIncorrectValidateCount() + 1);
         }
         }
         else if(diff > 512) //Тут бля не проблема, тут просто адовая проблемища всех оверов блеать!1111
         {
         // Не рассылаем валидацию во время скиллов с полетом
         if(activeChar.isCastingNow())
         {
         final SkillEntry skill = activeChar.getCastingSkill();
         if(skill != null && skill.getTemplate().getFlyType() != NONE)
         {
         return;
         }
         }
         activeChar.validateLocation(1);
         activeChar.stopMove(); // Лучше будем останавливать движение(более этично)
         }
         else
         {
         activeChar.setIncorrectValidateCount(0);
         }

         activeChar.setLastClientPosition(_loc.setH(activeChar.getHeading()));
         activeChar.setLastServerPosition(activeChar.getLoc());
         */

		/*
		activeChar.validateLocation(2);
		Creature attackTarget = activeChar.getAI().getAttackTarget();
		if (attackTarget != null)
			attackTarget.validateLocation(2);
		*/

//		System.out.println("Client loc: " + _loc.getX() + " " + _loc.getY() + " " + _loc.getZ());
//		System.out.println("Server loc: " + activeChar.getX() + " " + activeChar.getY() + " " + activeChar.getZ());
//		System.out.println("Diff: " + diff);
        if (diff > GeodataConfig.maxAsyncCoordDiff && !activeChar.isFalling()) {
            if (activeChar.isFlying() || activeChar.isInWater()) {
                activeChar.teleToLocation(_lastServerPosition);
            } else {
                activeChar.validateLocation(1);
            }
        }
        //else if(player.isFloating())
        //	player.sendMessage("vd: " + String.format("%.2f ss: %.2f", _diff, player.getSwimSpeed()));

        if (activeChar.getServitor() != null && !activeChar.getServitor().isInRange()) {
            activeChar.getServitor().stopMove();
        }

        //if(GeoConfig.GEODATA_DEBUG && "Rage".equals(player.getName()))
        //	_logD.info("ValidatePos: " + GameTimeController.getGameTicks() + " follow diff: " + String.format("%.2f", _diff) + " cDist: " + String.format("%.2f", _loc.getDistance(player.getXTo(), player.getYTo())));

        activeChar.setLastClientPosition(_loc.setH(activeChar.getHeading()));
        activeChar.setLastServerPosition(activeChar.getLoc());
    }

    @Deprecated
    private void correctPosition(final Player activeChar) {
        if (activeChar.isGM()) {
            activeChar.sendAdminMessage("Server loc: " + activeChar.getLoc());
            activeChar.sendAdminMessage("Correcting position...");
        }
        if (_lastServerPosition.x != 0 && _lastServerPosition.y != 0 && _lastServerPosition.z != 0) {
            if (GeoEngine.getNSWE(_lastServerPosition.x, _lastServerPosition.y, _lastServerPosition.z, activeChar.getGeoIndex()) == GeoEngine.NSWE_ALL) {
                activeChar.teleToLocation(_lastServerPosition);
            } else {
                activeChar.teleToClosestTown();
            }
        } else if (_lastClientPosition.x != 0 && _lastClientPosition.y != 0 && _lastClientPosition.z != 0) {
            if (GeoEngine.getNSWE(_lastClientPosition.x, _lastClientPosition.y, _lastClientPosition.z, activeChar.getGeoIndex()) == GeoEngine.NSWE_ALL) {
                activeChar.teleToLocation(_lastClientPosition);
            } else {
                activeChar.teleToClosestTown();
            }
        } else {
            activeChar.teleToClosestTown();
        }
    }

    public enum CorrectType {
        FlyCorrect, ValidateCorrect, MoveCorrect
    }
}