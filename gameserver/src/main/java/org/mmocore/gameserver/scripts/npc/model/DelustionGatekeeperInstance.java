package org.mmocore.gameserver.scripts.npc.model;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.manager.DimensionalRiftManager;
import org.mmocore.gameserver.manager.DimensionalRiftManager.DimensionalRiftRoom;
import org.mmocore.gameserver.model.entity.DelusionChamber;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.Location;

import java.util.Map;

/**
 * @author pchayka
 */

public final class DelustionGatekeeperInstance extends NpcInstance {
    public DelustionGatekeeperInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (command.startsWith("enterDC")) {
            int izId = Integer.parseInt(command.substring(8));
            int type = izId - 120;
            Map<Integer, DimensionalRiftRoom> rooms = DimensionalRiftManager.getInstance().getRooms(type);
            if (rooms == null) {
                player.sendPacket(SystemMsg.SYSTEM_ERROR);
                return;
            }
            Reflection r = player.getActiveReflection();
            if (r != null) {
                if (player.canReenterInstance(izId)) {
                    player.teleToLocation(r.getTeleportLoc(), r);
                }
            } else if (player.canEnterInstance(izId)) {
                Party party = player.getParty();
                if (party != null) {
                    new DelusionChamber(party, type, Rnd.get(1, rooms.size() - 1));
                }
            }
        } else if (command.startsWith("exitDC")) {
            String var = player.getPlayerVariables().get(PlayerVariables.DC_BACK_COORDS);
            if (var == null || var.isEmpty()) {
                player.teleToLocation(new Location(43768, -48232, -800), 0);
                return;
            }
            player.teleToLocation(Location.parseLoc(var), 0);
            player.getPlayerVariables().remove(PlayerVariables.DC_BACK_COORDS);
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}