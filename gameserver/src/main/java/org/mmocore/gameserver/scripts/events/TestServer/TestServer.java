package org.mmocore.gameserver.scripts.events.TestServer;

import org.jts.dataparser.data.holder.ExpDataHolder;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.listener.actor.player.OnPlayerEnterListener;
import org.mmocore.gameserver.listener.script.OnInitScriptListener;
import org.mmocore.gameserver.manager.ServerVariables;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.PlayerListenerList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author KilRoy
 */
public class TestServer extends Functions implements OnInitScriptListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestServer.class);
    private static final OnPlayerEnterListener onPlayerEnter = new OnPlayerEnterListenerImpl();

    private static boolean isActive() {
        return ServerVariables.getString("TestServer", "off").equalsIgnoreCase("on");
    }

    @Bypass("events.TestServer:activateEvent")
    public void activateEvent(Player player, NpcInstance lastNpc, String[] args) {
        if (!player.getPlayerAccess().IsEventGm)
            return;
        if (!isActive()) {
            LOGGER.info("Event 'Test Server' started.");
            ServerVariables.set("TestServer", "on");
            show("admin/events/events.htm", player);
            player.sendAdminMessage("Event 'Test Server' now started.");
            PlayerListenerList.addGlobal(onPlayerEnter);
        } else
            player.sendAdminMessage("Event 'Test Server' already started.");
    }

    @Bypass("events.TestServer:deactivateEvent")
    public void deactivateEvent(Player player, NpcInstance lastNpc, String[] args) {
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (isActive()) {
            LOGGER.info("Event 'Test Server' stopped.");
            ServerVariables.set("TestServer", "off");
            show("admin/events/events.htm", player);
            player.sendAdminMessage("Event 'Test Server' now stoped.");
            PlayerListenerList.removeGlobal(onPlayerEnter);
        } else
            player.sendAdminMessage("Event 'Test Server' not started.");
    }

    @Override
    public void onInit() {
        if (isActive()) {
            LOGGER.info("Loaded Event: Test Server [state: activated]");
            PlayerListenerList.addGlobal(onPlayerEnter);
        } else
            LOGGER.info("Loaded Event: Test Server [state: deactivated]");
    }

    private static final class OnPlayerEnterListenerImpl implements OnPlayerEnterListener {
        @Override
        public void onPlayerEnter(Player player) {
            if (player.getLevel() == 1 && !player.isGM()) {
                final Long exp_add = ExpDataHolder.getInstance().getExpForLevel(85) - player.getExp();
                player.addExpAndSp(exp_add, 100000000);
                player.addAdena(50000000);
                player.sendAdminMessage("You gained max level and 50 000 000 Adena.");
            }
        }
    }
}