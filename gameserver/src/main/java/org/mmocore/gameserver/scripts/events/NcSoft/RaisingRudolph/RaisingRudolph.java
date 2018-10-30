package org.mmocore.gameserver.scripts.events.NcSoft.RaisingRudolph;

import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.listener.actor.player.OnPlayerEnterListener;
import org.mmocore.gameserver.listener.script.OnInitScriptListener;
import org.mmocore.gameserver.manager.ServerVariables;
import org.mmocore.gameserver.manager.SpawnManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.serverpackets.ExBR_BroadcastEventState;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.PlayerListenerList;
import org.mmocore.gameserver.world.GameObjectsStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author KilRoy
 */
public class RaisingRudolph extends Functions implements OnInitScriptListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(RaisingRudolph.class);
    private static final OnPlayerEnterListener PLAYER_ENTER = new OnPlayerEnterListenerImpl();
    private static final ExBR_BroadcastEventState EVENT_PACKET_ADD = new ExBR_BroadcastEventState(ExBR_BroadcastEventState.RAISING_RUDOLPH, 1);
    private static final ExBR_BroadcastEventState EVENT_PACKET_DEL = new ExBR_BroadcastEventState(ExBR_BroadcastEventState.RAISING_RUDOLPH, 0);
    private static final ExBR_BroadcastEventState EVENT_PACKET_OLD = new ExBR_BroadcastEventState(ExBR_BroadcastEventState.LOVERS_JUBILEE, 0);
    private static final String SPAWN_GROUP = "br_xmas_event";

    private static boolean isActive() {
        return ServerVariables.getString("RaisingRudolph", "off").equalsIgnoreCase("on");
    }

    @Override
    public void onInit() {
        if (isActive()) {
            LOGGER.info("Loaded Event: Raising Rudolph [state: activated]");
            PlayerListenerList.addGlobal(PLAYER_ENTER);
            SpawnManager.getInstance().spawn(SPAWN_GROUP);
        } else
            LOGGER.info("Loaded Event: Raising Rudolph [state: deactivated]");
    }

    @Bypass("events.NcSoft.RaisingRudolph:activateEvent")
    public void activateEvent(Player player, NpcInstance lastNpc, String[] args) {
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (!isActive()) {
            LOGGER.info("Event 'Raising Rudolph' started.");

            ServerVariables.set("RaisingRudolph", "on");

            show("admin/events/custom/ncsoft.htm", player);
            PlayerListenerList.addGlobal(PLAYER_ENTER);
            ThreadPoolManager.getInstance().schedule(() -> {
                for (final Player players : GameObjectsStorage.getPlayers()) {
                    players.sendPacket(EVENT_PACKET_ADD, EVENT_PACKET_OLD);
                }
                SpawnManager.getInstance().spawn(SPAWN_GROUP);
            }, 1000L);
        } else
            player.sendMessage("Event 'Raising Rudolph' already started.");
    }

    @Bypass("events.NcSoft.RaisingRudolph:deactivateEvent")
    public void deactivateEvent(Player player, NpcInstance lastNpc, String[] args) {
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (isActive()) {
            LOGGER.info("Event 'Raising Rudolph' stopped.");

            ServerVariables.set("RaisingRudolph", "off");

            show("admin/events/custom/ncsoft.htm", player);
            PlayerListenerList.removeGlobal(PLAYER_ENTER);
            ThreadPoolManager.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    for (final Player players : GameObjectsStorage.getPlayers()) {
                        players.sendPacket(EVENT_PACKET_DEL);
                    }
                    SpawnManager.getInstance().despawn(SPAWN_GROUP);
                }
            }, 1000L);
        } else
            player.sendMessage("Event 'Raising Rudolph' not started.");
    }

    private static final class OnPlayerEnterListenerImpl implements OnPlayerEnterListener {
        @Override
        public void onPlayerEnter(final Player player) {
            if (isActive()) {
                player.sendPacket(EVENT_PACKET_ADD, EVENT_PACKET_OLD);
            }
        }
    }
}