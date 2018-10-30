package org.mmocore.gameserver.scripts.events.NcSoft.JackGame;

import gnu.trove.map.hash.TIntIntHashMap;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.listener.actor.OnDeathListener;
import org.mmocore.gameserver.listener.actor.player.OnPlayerEnterListener;
import org.mmocore.gameserver.listener.actor.player.OnPlayerReceivedPacketListener;
import org.mmocore.gameserver.listener.script.OnInitScriptListener;
import org.mmocore.gameserver.manager.ServerVariables;
import org.mmocore.gameserver.manager.SpawnManager;
import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.serverpackets.ExBR_BroadcastEventState;
import org.mmocore.gameserver.network.lineage.serverpackets.ExBR_LoadEventTopRankers;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.creatures.listeners.CharListenerList;
import org.mmocore.gameserver.object.components.player.PlayerListenerList;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.world.GameObjectsStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author KilRoy
 */
public class JackGame extends Functions implements OnInitScriptListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(JackGame.class);

    private static final int HALLOWEEN_EVENT_ID = 20091031;
    private static final int[] event_drop = new int[]{20707, 20708};
    private static final TIntIntHashMap playerMap = new TIntIntHashMap();
    private static final String br_halloween09_event = "br_halloween09_event";
    private static final int br_valakas_the_flame = 20725;
    private static final OnDeathListener deathListener = new OnDeathListenerImpl();
    private static final OnPlayerEnterListener enterListener = new OnPlayerEnterListenerImpl();
    private static final OnPlayerReceivedPacketListener receivedPacketListener = new OnPlayerReceivedPacketListenerImpl();
    private static TIntIntHashMap oldPlayerMap = new TIntIntHashMap();
    private static long eventValue = 0;
    private static boolean firstActivateReward;
    private static int allRewardAdena;
    private static int bestScore;
    private static int oldBestScore;

    public static boolean isActive() {
        return ServerVariables.getString("JackGame", "off").equalsIgnoreCase("on");
    }

    public static void insertEventRanking(final Player player, final int score) {
        if (bestScore < score) {
            bestScore = score;
        }
        synchronized (playerMap) {
            if (playerMap.get(player.getObjectId()) == 0) {
                playerMap.put(player.getObjectId(), score);
                if (getEventValue() == 0) {
                    setEventValue(1);
                }
            } else {
                final int oldScore = playerMap.get(player.getObjectId());
                if (oldScore < score) {
                    playerMap.remove(player.getObjectId());
                    playerMap.put(player.getObjectId(), score);
                }
                if (score >= 10 && !firstActivateReward) {
                    firstActivateReward = true;
                    setEventValue(2);
                }
            }
        }
    }

    public static void getEventRankerInfo(final Player player, final int targetScore) {
        try {
            synchronized (playerMap) {
                int topPlayerCount = 0;
                for (int allScore : playerMap.values()) {
                    if (allScore >= 10) {
                        topPlayerCount++;
                    }
                }
                if (!player.getPlayerVariables().getBoolean(PlayerVariables.EVENT_CURRENT_DAY_REWARD)) {
                    if (playerMap.get(player.getObjectId()) != 0) {
                        final int playerScore = playerMap.get(player.getObjectId());
                        if (playerScore >= targetScore) {
                            ItemFunctions.addItem(player, br_valakas_the_flame, 1);
                            final int adenaReward = allRewardAdena / topPlayerCount;
                            ItemFunctions.addItem(player, 57, adenaReward);
                            allRewardAdena -= adenaReward;
                            if (player.getLastNpc() != null) {
                                player.getLastNpc().showChatWindow(player, "pts/events/jack_game/br_halloween_scooldie006.htm");
                            }
                            player.getPlayerVariables().set(PlayerVariables.EVENT_CURRENT_DAY_REWARD, "true", -1);
                        } else {
                            if (player.getLastNpc() != null) {
                                player.getLastNpc().showChatWindow(player, "pts/events/jack_game/br_halloween_scooldie007.htm");
                            }
                        }
                    } else if (player.getLastNpc() != null) {
                        player.getLastNpc().showChatWindow(player, "pts/events/jack_game/br_halloween_scooldie007.htm");
                    }
                } else {
                    if (player.getLastNpc() != null) {
                        player.getLastNpc().showChatWindow(player, "pts/events/jack_game/br_halloween_scooldie009.htm");
                    }
                }
            }
        } catch (final Exception e) {
            LOGGER.warn("Wow, method getEventRankerInfo(player, int) return exception. Check him ", e);
        }
    }

    public static void changeEventDay() {
        oldPlayerMap.clear();
        allRewardAdena = 100000000;
        bestScore = 0;
        firstActivateReward = false;
        setEventValue(0);
        oldBestScore = bestScore;
        oldPlayerMap = playerMap;
        playerMap.clear();
        final ExBR_BroadcastEventState packet = new ExBR_BroadcastEventState(HALLOWEEN_EVENT_ID, 1, 0, 0, 0, 0, 0, "", "");
        for (final Player players : GameObjectsStorage.getPlayers()) {
            if (players.isOnline()) {
                players.sendPacket(packet);
                if (players.getPlayerVariables().getBoolean(PlayerVariables.EVENT_CURRENT_DAY_REWARD)) {
                    players.getPlayerVariables().remove(PlayerVariables.EVENT_CURRENT_DAY_REWARD);
                }
            }
        }
    }

    public static long getEventValue() {
        return eventValue;
    }

    public static void setEventValue(final long value) {
        eventValue = value;
        ServerVariables.set("JackGameEventValue", value);
    }

    @Override
    public void onInit() {
        if (isActive()) {
            CharListenerList.addGlobal(deathListener);
            PlayerListenerList.addGlobal(enterListener);
            PlayerListenerList.addGlobal(receivedPacketListener);
            bestScore = 0;
            allRewardAdena = 100000000;
            firstActivateReward = false;
            eventValue = ServerVariables.getLong("JackGameEventValue");
            LOGGER.info("Loaded Event: Jack Game [state: activated]");
            doSpawnGroup(false, br_halloween09_event);
        } else
            LOGGER.info("Loaded Event: Jack Game [state: deactivated]");
    }

    @Bypass("events.NcSoft.JackGame:activateEvent")
    public void activateEvent(Player player, NpcInstance lastNpc, String[] args) {
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (!isActive()) {
            CharListenerList.addGlobal(deathListener);
            PlayerListenerList.addGlobal(enterListener);
            PlayerListenerList.addGlobal(receivedPacketListener);
            bestScore = 0;
            allRewardAdena = 100000000;
            firstActivateReward = false;
            LOGGER.info("Event 'Jack Game' started.");
            ServerVariables.set("JackGameEventValue", 0);
            ServerVariables.set("JackGame", "on");
            show("admin/events/custom/ncsoft.htm", player);
            doSpawnGroup(false, br_halloween09_event);
            player.sendAdminMessage("Event 'Jack Game' started.");
            final ExBR_BroadcastEventState packet = new ExBR_BroadcastEventState(HALLOWEEN_EVENT_ID, 1, 0, 0, 0, 0, 0, "", "");
            for (final Player players : GameObjectsStorage.getPlayers()) {
                if (players.isOnline()) {
                    players.sendPacket(packet);
                }
            }
        } else
            player.sendAdminMessage("Event 'Jack Game' already started.");
    }

    @Bypass("events.NcSoft.JackGame:deactivateEvent")
    public void deactivateEvent(Player player, NpcInstance lastNpc, String[] args) {
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (isActive()) {
            playerMap.clear();
            oldPlayerMap.clear();
            CharListenerList.removeGlobal(deathListener);
            PlayerListenerList.removeGlobal(enterListener);
            PlayerListenerList.removeGlobal(receivedPacketListener);
            LOGGER.info("Event 'Jack Game' stoped.");
            ServerVariables.unset("JackGameEventValue");
            ServerVariables.set("JackGame", "off");
            show("admin/events/custom/ncsoft.htm", player);
            doSpawnGroup(true, br_halloween09_event);
            player.sendAdminMessage("Event 'Jack Game' stoped.");
            final ExBR_BroadcastEventState packet = new ExBR_BroadcastEventState(HALLOWEEN_EVENT_ID, 0, 0, 0, 0, 0, 0, "", "");
            for (final Player players : GameObjectsStorage.getPlayers()) {
                if (players.isOnline()) {
                    players.sendPacket(packet);
                }
            }
        } else
            player.sendAdminMessage("Event 'Jack Game' not started.");
    }

    private void doSpawnGroup(final boolean despawn, final String group) {
        if (!despawn)
            SpawnManager.getInstance().spawn(group);
        else
            SpawnManager.getInstance().despawn(group);
    }

    private static final class OnDeathListenerImpl implements OnDeathListener {
        @Override
        public void onDeath(final Creature self, final Creature killer) {
            if (self != null && killer != null) {
                if (self.isMonster() && (killer.getLevel() - self.getLevel() < 9)) {
                    if (Rnd.chance(1)) {
                        final MonsterInstance actor = (MonsterInstance) self;
                        actor.dropItem(killer.getPlayer(), Rnd.get(event_drop), 1);
                    }
                }
            }
        }
    }

    private static final class OnPlayerEnterListenerImpl implements OnPlayerEnterListener {
        @Override
        public void onPlayerEnter(final Player player) {
            player.sendPacket(new ExBR_BroadcastEventState(HALLOWEEN_EVENT_ID, 1, 0, 0, 0, 0, 0, "Kil", "Roy"));
            if (player.getPlayerVariables().getBoolean(PlayerVariables.EVENT_CURRENT_DAY_REWARD)) {
                if (playerMap.get(player.getObjectId()) == 0) {
                    player.getPlayerVariables().remove(PlayerVariables.EVENT_CURRENT_DAY_REWARD);
                }
            }
        }
    }

    private static final class OnPlayerReceivedPacketListenerImpl implements OnPlayerReceivedPacketListener {
        @Override
        public void onReceivedPacket(final Player player, final Object... param) {
            final int eventId = ((Integer) param[0]).intValue();
            final int eventState = ((Integer) param[1]).intValue();
            player.sendPacket(new ExBR_LoadEventTopRankers(eventId, eventState, playerMap.size(), bestScore, playerMap.get(player.getObjectId())));
            player.sendPacket(new ExBR_LoadEventTopRankers(eventId, eventState, oldPlayerMap.size(), oldBestScore, oldPlayerMap.get(player.getObjectId())));
        }
    }
}