package org.mmocore.gameserver.scripts.events.TestServerHelper;

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
public class TestServerHelper extends Functions implements OnInitScriptListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestServerHelper.class);

    private static final String test_server_helper = "test_2nd_class";

    private static boolean isActive() {
        return ServerVariables.getString("TestServerHelper", "off").equalsIgnoreCase("on");
    }

    @Override
    public void onInit() {
        if (isActive()) {
            LOGGER.info("Loaded Event: Test Server Helper [state: activated]");
            doSpawnGroup(false);
        } else
            LOGGER.info("Loaded Event: Test Server Helper [state: deactivated]");
    }

    @Bypass("events.TestServerHelper:activateEvent")
    public void activateEvent(Player player, NpcInstance lastNpc, String[] args) {
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (!isActive()) {
            LOGGER.info("Event 'Test Server Helper' started.");
            ServerVariables.set("TestServerHelper", "on");
            show("admin/events/custom/events.htm", player);
            doSpawnGroup(false);
        } else
            player.sendAdminMessage("Event 'Test Server Helper' already started.");
    }

    @Bypass("events.TestServerHelper:deactivateEvent")
    public void deactivateEvent(Player player, NpcInstance lastNpc, String[] args) {
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (isActive()) {
            LOGGER.info("Event 'Test Server Helper' stoped.");
            ServerVariables.set("TestServerHelper", "off");
            show("admin/events/custom/events.htm", player);
            doSpawnGroup(true);
        } else
            player.sendAdminMessage("Event 'Test Server Helper' not started.");
    }

    private void doSpawnGroup(final boolean despawn) {
        if (!despawn)
            SpawnManager.getInstance().spawn(TestServerHelper.test_server_helper);
        else
            SpawnManager.getInstance().despawn(TestServerHelper.test_server_helper);
    }
}