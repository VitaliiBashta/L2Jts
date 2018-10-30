package org.mmocore.gameserver.model.instances;

import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Boat;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.world.World;

public class AirShipControllerInstance extends NpcInstance {
    public AirShipControllerInstance(final int objectID, final NpcTemplate template) {
        super(objectID, template);
    }

    private static SystemMsg canBoard(final Player player) {
        if (player.isTransformed()) {
            return SystemMsg.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_TRANSFORMED;
        }
        if (player.isParalyzed()) {
            return SystemMsg.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_PETRIFIED;
        }
        if (player.isDead() || player.isFakeDeath()) {
            return SystemMsg.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_DEAD;
        }
        if (player.isFishing()) {
            return SystemMsg.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_FISHING;
        }
        if (player.isInCombat()) {
            return SystemMsg.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_IN_BATTLE;
        }
        if (player.isInDuel()) {
            return SystemMsg.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_IN_A_DUEL;
        }
        if (player.isSitting()) {
            return SystemMsg.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_SITTING;
        }
        if (player.isCastingNow()) {
            return SystemMsg.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_CASTING;
        }
        if (player.isCursedWeaponEquipped()) {
            return SystemMsg.YOU_CANNOT_BOARD_AN_AIRSHIP_WHEN_A_CURSED_WEAPON_IS_EQUIPPED;
        }
        if (player.getActiveWeaponFlagAttachment() != null) {
            return SystemMsg.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_HOLDING_A_FLAG;
        }
        if (player.getServitor() != null || player.isMounted()) {
            return SystemMsg.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_A_PET_OR_A_SERVITOR_IS_SUMMONED;
        }

        return null;
    }

    @Override
    public void onBypassFeedback(final Player player, final String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if ("board".equalsIgnoreCase(command)) {
            final SystemMsg msg = canBoard(player);
            if (msg != null) {
                player.sendPacket(msg);
                return;
            }

            final Boat boat = getDockedAirShip();
            if (boat == null) {
                player.sendActionFailed();
                return;
            }

            if (player.getBoat() != null && player.getBoat().getObjectId() != boat.getObjectId()) {
                player.sendPacket(SystemMsg.YOU_HAVE_ALREADY_BOARDED_ANOTHER_AIRSHIP);
                return;
            }

            player._stablePoint = player.getLoc().setH(0);
            player.broadcastStopMove();
            boat.addPlayer(player, new Location());
        } else {
            super.onBypassFeedback(player, command);
        }
    }

    protected Boat getDockedAirShip() {
        for (final Creature cha : World.getAroundCharacters(this, 1000, 500)) {
            if (cha.isAirShip() && ((Boat) cha).isDocked()) {
                return (Boat) cha;
            }
        }

        return null;
    }
}