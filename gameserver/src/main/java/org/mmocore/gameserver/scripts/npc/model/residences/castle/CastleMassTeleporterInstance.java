package org.mmocore.gameserver.scripts.npc.model.residences.castle;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.model.entity.events.impl.CastleSiegeEvent;
import org.mmocore.gameserver.model.entity.events.impl.SiegeEvent;
import org.mmocore.gameserver.model.entity.events.objects.SiegeToggleNpcObject;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ChatUtils;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.world.World;

import java.util.List;
import java.util.concurrent.Future;

/**
 * @author VISTALL
 * @date 17:46/12.07.2011
 */
public class CastleMassTeleporterInstance extends NpcInstance {
    private Future<?> _teleportTask = null;
    private Location _teleportLoc;
    public CastleMassTeleporterInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
        _teleportLoc = Location.parseLoc(template.getAIParams().getString("teleport_loc"));
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (_teleportTask != null) {
            showChatWindow(player, "residence2/castle/CastleTeleportDelayed.htm");
            return;
        }

        _teleportTask = ThreadPoolManager.getInstance().schedule(new TeleportTask(), isAllTowersDead() ? 480000L : 30000L);

        showChatWindow(player, "residence2/castle/CastleTeleportDelayed.htm");
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        if (_teleportTask != null) {
            showChatWindow(player, "residence2/castle/CastleTeleportDelayed.htm");
        } else {
            if (isAllTowersDead()) {
                showChatWindow(player, "residence2/castle/gludio_mass_teleporter002.htm");
            } else {
                showChatWindow(player, "residence2/castle/gludio_mass_teleporter001.htm");
            }
        }
    }

    private boolean isAllTowersDead() {
        SiegeEvent siegeEvent = getEvent(SiegeEvent.class);
        if (siegeEvent == null || !siegeEvent.isInProgress()) {
            return false;
        }

        List<SiegeToggleNpcObject> towers = siegeEvent.getObjects(CastleSiegeEvent.CONTROL_TOWERS);
        for (SiegeToggleNpcObject t : towers) {
            if (t.isAlive()) {
                return false;
            }
        }

        return true;
    }

    private class TeleportTask extends RunnableImpl {
        @Override
        public void runImpl() throws Exception {
            ChatUtils.shout(CastleMassTeleporterInstance.this, NpcString.THE_DEFENDERS_OF_S1_CASTLE_WILL_BE_TELEPORTED_TO_THE_INNER_CASTLE, "#" + getCastle().getNpcStringName().getId());

            for (Player p : World.getAroundPlayers(CastleMassTeleporterInstance.this, 700, 70)) {
                p.teleToLocation(Location.findPointToStay(_teleportLoc, 10, 100, p.getGeoIndex()));
            }

            _teleportTask = null;
        }
    }
}