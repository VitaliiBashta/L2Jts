package org.mmocore.gameserver.scripts.npc.model.residences.castle;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.Location;

/**
 * @author VISTALL
 * @date 21:53/23.05.2011
 * 29055
 */
public class VenomTeleportCubicInstance extends NpcInstance {
    public static final Location[] LOCS = new Location[]
            {
                    new Location(11913, -48851, -1088),
                    new Location(11918, -49447, -1088)
            };

    public VenomTeleportCubicInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        player.teleToLocation(LOCS[Rnd.get(LOCS.length)]);
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        showChatWindow(player, "residence2/castle/teleport_cube_benom001.htm");
    }
}
