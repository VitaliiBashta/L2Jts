package org.mmocore.gameserver.model.instances;


import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.entity.events.impl.SingleMatchEvent;
import org.mmocore.gameserver.model.zone.Zone;
import org.mmocore.gameserver.model.zone.ZoneType;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


public final class ObservationInstance extends NpcInstance {
    public ObservationInstance(final int objectId, final NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(final Player player, final String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (checkForDominionWard(player)) {
            return;
        }

        if (player.getOlympiadGame() != null) {
            return;
        }

        if (player.getEvent(SingleMatchEvent.class) != null) {
            return;
        }

        if (command.startsWith("observeSiege")) {
            final String val = command.substring(13);
            final StringTokenizer st = new StringTokenizer(val);
            st.nextToken(); // Bypass cost

            final List<Zone> zones = new ArrayList<>();
            World.getZones(zones, new Location(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken())), ReflectionManager.DEFAULT);
            for (final Zone z : zones) {
                if (z.getType() == ZoneType.SIEGE && z.isActive()) {
                    doObserve(player, val);
                    return;
                }
            }

            player.sendPacket(SystemMsg.OBSERVATION_IS_ONLY_POSSIBLE_DURING_A_SIEGE);
        } else if (command.startsWith("observe")) {
            doObserve(player, command.substring(8));
        } else {
            super.onBypassFeedback(player, command);
        }
    }

    @Override
    public String getHtmlPath(final int npcId, final int val, final Player player) {
        String pom = "";
        if (val == 0) {
            pom = String.valueOf(npcId);
        } else {
            pom = npcId + "-" + val;
        }

        return "observation/" + pom + ".htm";
    }

    private void doObserve(final Player player, final String val) {
        final StringTokenizer st = new StringTokenizer(val);
        final int cost = Integer.parseInt(st.nextToken());
        final int x = Integer.parseInt(st.nextToken());
        final int y = Integer.parseInt(st.nextToken());
        final int z = Integer.parseInt(st.nextToken());

        if (!player.reduceAdena(cost, true)) {
            player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            player.sendActionFailed();
            return;
        }

        player.enterObserverMode(new Location(x, y, z));
    }
}