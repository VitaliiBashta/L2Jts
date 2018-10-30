package org.mmocore.gameserver.scripts.events.UpdateAnnouncer;

import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.listener.script.OnInitScriptListener;
import org.mmocore.gameserver.manager.ServerVariables;
import org.mmocore.gameserver.manager.SpawnManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author KilRoy
 */
public class UpdateAnnouncer extends Functions implements OnInitScriptListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateAnnouncer.class);

    private static final String update_announcer = "update_announcer";

    private static boolean isActive() {
        return ServerVariables.getString("UpdateAnnouncer", "off").equalsIgnoreCase("on");
    }

    @Override
    public void onInit() {
        if (isActive()) {
            LOGGER.info("Loaded Event: Update Announcer [state: activated]");
            doSpawnGroup(false, update_announcer);
        } else
            LOGGER.info("Loaded Event: Update Announcer [state: deactivated]");
    }

    @Bypass("events.UpdateAnnouncer:activateEvent")
    public void activateEvent(Player player, NpcInstance lastNpc, String[] args) {
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (!isActive()) {
            LOGGER.info("Event 'Update Announcer' started.");
            ServerVariables.set("UpdateAnnouncer", "on");
            show("admin/events/custom/events.htm", player);
            doSpawnGroup(false, update_announcer);
        } else
            player.sendAdminMessage("Event 'Update Announcer' already started.");
    }

    @Bypass("events.UpdateAnnouncer:deactivateEvent")
    public void deactivateEvent(Player player, NpcInstance lastNpc, String[] args) {
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (isActive()) {
            LOGGER.info("Event 'Update Announcer' stoped.");
            ServerVariables.set("UpdateAnnouncer", "off");
            show("admin/events/custom/events.htm", player);
            doSpawnGroup(true, update_announcer);
        } else
            player.sendAdminMessage("Event 'Update Announcer' not started.");
    }

    private void doSpawnGroup(final boolean despawn, final String group) {
        if (!despawn)
            SpawnManager.getInstance().spawn(group);
        else
            SpawnManager.getInstance().despawn(group);
    }
}