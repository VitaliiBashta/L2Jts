package org.mmocore.gameserver.scripts.events.Leprechaun;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.client.holder.NpcNameLineHolder;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.listener.actor.player.OnPlayerEnterListener;
import org.mmocore.gameserver.listener.script.OnInitScriptListener;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.manager.ServerVariables;
import org.mmocore.gameserver.manager.SpawnManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.PlayerListenerList;
import org.mmocore.gameserver.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

/**
 * @author KilRoy, feroom
 * Для нормальной работы - не забываем про диалог(data/html-xx/scripts/events/Leprechaun/NPC-ID.htm) и указание EVENT_NPC_ID в конфигурации
 */
public class Leprechaun extends Functions implements OnInitScriptListener {
    private static final Logger logger = LoggerFactory.getLogger(Leprechaun.class);

    // Configuration section start========
    private static final int EVENT_NPC_ID = 20001; // NPC ID
    private static final int EVENT_NPC_DELAY = 20; // 20 min
    private static final int EVENT_NPC_SPAWN_RANGE = 16; //Range for spawn point
    private static final Location EVENT_NPC_HOME_SPAWN_POINT = new Location(-178536, 210424, -15496); // default spawn point
    private static final int[] EVENT_PRISE_ID = {57, 57, 57}; // item id for give prise
    private static final int[] EVENT_PRISE_ID_COUNTS = {10, 100, 1000}; // item count for give prise
    // Configuration section end==========
    private static final OnPlayerEnterListener _eventAnnouncer = new OnPlayerEnterListenerImpl();
    private static NpcInstance NPC_LEPRECHAUN = null;
    private static ScheduledFuture<?> startTask = null;
    private static ScheduledFuture<?> endTask = null;
    private static Language lang = null;
    private static boolean isGone = false;
    private static boolean spawned = false;
    private static String npcName;
    private static long timeToDespawn;

    private static boolean isActive() {
        return ServerVariables.getString("Leprechaun", "off").equalsIgnoreCase("on");
    }

    @Override
    public void onInit() {
        if (ServerConfig.DEFAULT_LANG.equalsIgnoreCase("ru"))
            lang = Language.RUSSIAN;
        else
            lang = Language.ENGLISH;
        if (isActive()) {
            logger.info("Loaded Event: Leprechaun [state: activated]");
            startTask = ThreadPoolManager.getInstance().schedule(new startingTask(), 600000L);
            PlayerListenerList.addGlobal(_eventAnnouncer);
        } else
            logger.info("Loaded Event: Leprechaun [state: deactivated]");
    }

    @Bypass("events.Leprechaun:activateEvent")
    public void activateEvent(Player player, NpcInstance lastNpc, String[] args) {
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (!isActive()) {
            logger.info("Event 'Leprechaun' started.");
            AnnouncementUtils.announceToAllFromStringHolder("scripts.events.Leprechaun.AnnounceEventStarted");

            ServerVariables.set("Leprechaun", "on");

            show("admin/events/events.htm", player);
            ThreadPoolManager.getInstance().schedule(new startingTask(), 1000L);
            PlayerListenerList.addGlobal(_eventAnnouncer);
        } else
            player.sendMessage("Event 'Leprechaun' already started.");
    }

    @Bypass("events.Leprechaun:deactivateEvent")
    public void deactivateEvent(Player player, NpcInstance lastNpc, String[] args) {
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (isActive()) {
            logger.info("Event 'Leprechaun' stopped.");
            AnnouncementUtils.announceToAllFromStringHolder("scripts.events.Leprechaun.AnnounceEventStoped");

            ServerVariables.set("Leprechaun", "off");
            NPC_LEPRECHAUN = null;
            isGone = false;
            try {
                if (startTask != null)
                    startTask.cancel(false);
                if (endTask != null)
                    endTask.cancel(false);
            } catch (Exception e) {
                logger.error("Event pools not canceled. Exception={}", e);
            }

            show("admin/events/events.htm", player);
            PlayerListenerList.removeGlobal(_eventAnnouncer);
        } else
            player.sendMessage("Event 'Leprechaun' not started.");
    }

    @Bypass("events.Leprechaun:givePrise")
    public void givePrise(Player player, NpcInstance lastNpc, String[] args) {
        if (player == null || lastNpc == null || isGone)
            return;

        isGone = true;
        AnnouncementUtils.announceToAll("Леприкон найден! Игрок " + player.getName() + " получает награду!");
        ThreadPoolManager.getInstance().schedule(new endingTask(), 1000L);
        int index = Rnd.get(EVENT_PRISE_ID.length);
        int itemId = EVENT_PRISE_ID[index];
        int counts = EVENT_PRISE_ID_COUNTS[index];
        ItemFunctions.addItem(player, itemId, counts);
    }

    private static final class OnPlayerEnterListenerImpl implements OnPlayerEnterListener {
        @Override
        public void onPlayerEnter(Player player) {
            if (spawned) {
                List<SystemMessage> cm = new ArrayList<>(2);
                cm.add(new SystemMessage(SystemMsg.S1).addString("Леприкон обитает рядом с " + npcName));
                cm.add(new SystemMessage(SystemMsg.S1).addString("Леприкон свалит с места через " + ((timeToDespawn - System.currentTimeMillis()) / 60 / 1000) + "мин"));
                if (cm != null) {
                    player.sendPacket(cm);
                }
            }
        }
    }

    private class startingTask extends RunnableImpl {
        @Override
        public void runImpl() {
            isGone = false;
            spawned = true;

            if (NPC_LEPRECHAUN == null)
                NPC_LEPRECHAUN = NpcUtils.spawnSingle(EVENT_NPC_ID, EVENT_NPC_HOME_SPAWN_POINT, ReflectionManager.JAIL);
            int size = SpawnManager.getInstance().getSpawnSize();
            NpcInstance npcs = SpawnManager.getInstance().getSpawners("NONE").get(Rnd.get(size)).getLastSpawn();
            npcName = NpcNameLineHolder.getInstance().get(lang, npcs.getNpcId()).getName();
            NPC_LEPRECHAUN.teleToLocation(npcs.getLoc().x + EVENT_NPC_SPAWN_RANGE, npcs.getLoc().y + EVENT_NPC_SPAWN_RANGE, npcs.getLoc().z, 0);
            AnnouncementUtils.announceToAll("Леприкон появился рядом с " + npcName);
            AnnouncementUtils.announceToAll("Леприкон свалит с места через " + EVENT_NPC_DELAY + "мин");
            endTask = ThreadPoolManager.getInstance().schedule(new endingTask(), EVENT_NPC_DELAY * 60 * 1000);
            timeToDespawn = System.currentTimeMillis() + (EVENT_NPC_DELAY * 60 * 1000);
            startTask = null;
        }
    }

    private class endingTask extends RunnableImpl {
        @Override
        public void runImpl() {
            if (NPC_LEPRECHAUN != null && !isGone) {
                NPC_LEPRECHAUN.teleToLocation(EVENT_NPC_HOME_SPAWN_POINT, ReflectionManager.JAIL);
                AnnouncementUtils.announceToAll("Леприкон свалил! Кто не успел, тот опоздал!");
                startTask = ThreadPoolManager.getInstance().schedule(new startingTask(), 10000L);
            } else if (isGone) {
                NPC_LEPRECHAUN.teleToLocation(EVENT_NPC_HOME_SPAWN_POINT, ReflectionManager.JAIL);
                startTask = ThreadPoolManager.getInstance().schedule(new startingTask(), 10000L);
            }
            endTask = null;
            spawned = false;
        }
    }
}