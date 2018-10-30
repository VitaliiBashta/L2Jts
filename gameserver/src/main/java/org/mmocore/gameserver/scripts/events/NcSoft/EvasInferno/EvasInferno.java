package org.mmocore.gameserver.scripts.events.NcSoft.EvasInferno;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.EventsConfig;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.listener.actor.OnDeathListener;
import org.mmocore.gameserver.listener.actor.player.OnPlayerEnterListener;
import org.mmocore.gameserver.listener.script.OnInitScriptListener;
import org.mmocore.gameserver.manager.ServerVariables;
import org.mmocore.gameserver.manager.SpawnManager;
import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ExBR_BroadcastEventState;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.creatures.listeners.CharListenerList;
import org.mmocore.gameserver.object.components.player.PlayerListenerList;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.world.GameObjectsStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author KilRoy
 */
public class EvasInferno extends Functions implements OnInitScriptListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(EvasInferno.class);

    private static final String br_fire_elemental = "br_fire_elemental";
    private static final int br_seed_of_fire = 20585;
    private static final int br_seed_of_explosive_fire = 20586;
    private static final OnDeathListener deathListener = new OnDeathListenerImpl();
    private static final OnPlayerEnterListener enterListener = new OnPlayerEnterListenerImpl();
    private static long elapsedTime = 0;
    private static EventStage currentStage = EventStage.STAGE1;
    private static long eventValue = 0;
    private static long DBValue = 0;
    private static boolean givePresent = false;
    private static int presentId = 0;
    private static int param1;
    private static int param2;
    private static int param3;

    public static boolean isActive() {
        return ServerVariables.getString("EvasInferno", "off").equalsIgnoreCase("on");
    }

    public static void startBuffEvent(final int type, final int present, final int time) {
        final SystemMessage packet = new SystemMessage(SystemMsg.EVAS_BLESSING_STAGE_S1_HAS_ENDED).addNumber(getCurrentEventStage().ordinal());
        givePresent = true;
        presentId = present;
        if (type == 1) {
            final SystemMessage expSpPresent = new SystemMessage(SystemMsg.IT_IS_EVAS_BLESSING_PERIOD_S1_WILL_BE_EFFECTIVE_UNTIL_S2).addString(String.valueOf(present) + "%").addString("00:00");
            final SystemMessage expSpMultiple = new SystemMessage(SystemMsg.A_BLESSING_THAT_INCREASES_EXP_BY_S1_S2).addNumber(present).addString("%");
            for (final Player player : GameObjectsStorage.getPlayers()) {
                if (player.isOnline()) {
                    player.sendPacket(packet, expSpPresent);
                    if (!player.getPlayerVariables().getBoolean(PlayerVariables.EVENT_GIVED_EXP)) {
                        player.sendPacket(expSpMultiple);
                        player.getPremiumAccountComponent().getPremiumBonus().setRateXp(player.getPremiumAccountComponent().getPremiumBonus().getRateXp() + (presentId / 10));
                        player.getPremiumAccountComponent().getPremiumBonus().setRateSp(player.getPremiumAccountComponent().getPremiumBonus().getRateSp() + (presentId / 10));
                        player.getPlayerVariables().set(PlayerVariables.EVENT_GIVED_EXP, "true", -1);
                    }
                }
            }
        } else if (type == 2) {
            final SystemMessage itemPresent = new SystemMessage(SystemMsg.IT_IS_EVAS_BLESSING_PERIOD_UNTIL_S1_JACK_SAGE_CAN_GIFT_YOU_WITH_S2).addString("00:00").addItemName(presentId);
            for (final Player player : GameObjectsStorage.getPlayers()) {
                if (player.isOnline()) {
                    player.sendPacket(packet, itemPresent);
                }
            }
        }
        ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                givePresent = false;
                for (final Player player : GameObjectsStorage.getPlayers()) {
                    if (player.isOnline()) {
                        if (player.getPlayerVariables().getBoolean(PlayerVariables.EVENT_GIVED_EXP)) {
                            player.getPremiumAccountComponent().getPremiumBonus().setRateXp(player.getPremiumAccountComponent().getPremiumBonus().getRateXp() - (presentId / 10));
                            player.getPremiumAccountComponent().getPremiumBonus().setRateSp(player.getPremiumAccountComponent().getPremiumBonus().getRateSp() - (presentId / 10));
                            player.getPlayerVariables().remove(PlayerVariables.EVENT_GIVED_EXP);
                        }
                    }
                }
                presentId = 0;
            }
        }, time * 1000);
    }

    public static void sendEventState(final int eventState, final int show, final int eventStage, final int eventPercent) {
        param1 = show;
        param2 = eventStage;
        param3 = eventPercent;
        final ExBR_BroadcastEventState packet = new ExBR_BroadcastEventState(eventState, show, eventStage, eventPercent, 0, 0, 0, "", "");
        for (final Player player : GameObjectsStorage.getPlayers()) {
            if (player.isOnline()) {
                player.sendPacket(packet);
            }
        }
    }

    public static void startNewStage(final int nextStage) {
        givePresent = false;
        setCurrentEventStage(EventStage.values()[nextStage]);
        final SystemMessage packet = new SystemMessage(SystemMsg.EVAS_BLESSING_STAGE_S1_HAS_BEGUN).addNumber(nextStage);
        for (final Player player : GameObjectsStorage.getPlayers()) {
            if (player.isOnline()) {
                player.sendPacket(packet);
                if ((presentId > 1 && presentId <= 40) && player.getPlayerVariables().getBoolean(PlayerVariables.EVENT_GIVED_EXP)) {
                    player.getPremiumAccountComponent().getPremiumBonus().setRateXp(player.getPremiumAccountComponent().getPremiumBonus().getRateXp() - (presentId / 10));
                    player.getPremiumAccountComponent().getPremiumBonus().setRateSp(player.getPremiumAccountComponent().getPremiumBonus().getRateSp() - (presentId / 10));
                    player.getPlayerVariables().remove(PlayerVariables.EVENT_GIVED_EXP);
                }
            }
        }
        presentId = 0;
    }

    public static long getDBValue() {
        return DBValue;
    }

    public static void setDBValue(final long value) {
        DBValue = value;
        ServerVariables.set("EvasInfernoDBValue", value);
    }

    public static long getEventValue() {
        return eventValue;
    }

    public static void setEventValue(final long value) {
        eventValue = value;
        ServerVariables.set("EvasInfernoEventValue", value);
    }

    public static long getEventElapsedTime() {
        return elapsedTime;
    }

    public static void setEventElapsedTime(final long time) {
        elapsedTime = time;
        ServerVariables.set("EvasInfernoElapsedTime", time);
    }

    public static EventStage getCurrentEventStage() {
        return currentStage;
    }

    public static void setCurrentEventStage(final EventStage eventStage) {
        currentStage = eventStage;
        ServerVariables.set("EvasInfernoStage", eventStage.ordinal());
    }

    @Override
    public void onInit() {
        if (isActive()) {
            PlayerListenerList.addGlobal(enterListener);
            CharListenerList.addGlobal(deathListener);
            eventValue = ServerVariables.getLong("EvasInfernoEventValue");
            DBValue = ServerVariables.getLong("EvasInfernoDBValue");
            elapsedTime = ServerVariables.getLong("EvasInfernoElapsedTime");
            currentStage = EventStage.values()[ServerVariables.getInt("EvasInfernoStage")];
            LOGGER.info("Loaded Event: Evas Inferno [state: activated]");
            doSpawnGroup(false);
        } else
            LOGGER.info("Loaded Event: Evas Inferno [state: deactivated]");
    }

    @Bypass("events.NcSoft.EvasInferno:activateEvent")
    public void activateEvent(Player player, NpcInstance lastNpc, String[] args) {
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (!isActive()) {
            PlayerListenerList.addGlobal(enterListener);
            CharListenerList.addGlobal(deathListener);
            LOGGER.info("Event 'Evas Inferno' started.");
            ServerVariables.set("EvasInferno", "on");
            ServerVariables.set("EvasInfernoStage", currentStage.ordinal());
            ServerVariables.set("EvasInfernoElapsedTime", 0);
            ServerVariables.set("EvasInfernoEventValue", 0);
            ServerVariables.set("EvasInfernoDBValue", 0);
            show("admin/events/custom/ncsoft.htm", player);
            doSpawnGroup(false);
            player.sendAdminMessage("Event 'Evas Inferno' started.");
            sendEventState(20090801, 1, 1, 0);
            final SystemMessage packet = new SystemMessage(SystemMsg.EVAS_BLESSING_STAGE_S1_HAS_BEGUN).addNumber(1);
            for (final Player players : GameObjectsStorage.getPlayers()) {
                if (players.isOnline()) {
                    players.sendPacket(packet);
                }
            }
        } else
            player.sendAdminMessage("Event 'Gift Of Vitality' already started.");
    }

    @Bypass("events.NcSoft.EvasInferno:deactivateEvent")
    public void deactivateEvent(Player player, NpcInstance lastNpc, String[] args) {
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (isActive()) {
            PlayerListenerList.removeGlobal(enterListener);
            CharListenerList.removeGlobal(deathListener);
            LOGGER.info("Event 'Evas Inferno' stoped.");
            ServerVariables.set("EvasInferno", "off");
            ServerVariables.unset("EvasInfernoElapsedTime");
            ServerVariables.unset("EvasInfernoStage");
            ServerVariables.unset("EvasInfernoEventValue");
            ServerVariables.unset("EvasInfernoDBValue");
            show("admin/events/custom/ncsoft.htm", player);
            doSpawnGroup(true);
            player.sendAdminMessage("Event 'Evas Inferno' stoped.");
            sendEventState(20090801, 0, 1, 0);
        } else
            player.sendAdminMessage("Event 'Evas Inferno' not started.");
    }

    private void doSpawnGroup(final boolean despawn) {
        if (!despawn)
            SpawnManager.getInstance().spawn(EvasInferno.br_fire_elemental);
        else
            SpawnManager.getInstance().despawn(EvasInferno.br_fire_elemental);
    }

    public enum EventStage {
        STAGE0,
        STAGE1,
        STAGE2,
        STAGE3,
        STAGE4,
        STAGE5,
        STAGE6,
        STAGE7,
        STAGE8,
        STAGE9,
        STAGE10,
        STAGE11,
        STAGE12,
        STAGE13,
        STAGE14,
    }

    private static final class OnPlayerEnterListenerImpl implements OnPlayerEnterListener {
        @Override
        public void onPlayerEnter(final Player player) {
            player.sendPacket(new ExBR_BroadcastEventState(20090801, param1, param2, param3, 0, 0, 0, "", ""));
            if (givePresent) {
                if (presentId > 40) {
                    player.sendPacket(new SystemMessage(SystemMsg.IT_IS_EVAS_BLESSING_PERIOD_UNTIL_S1_JACK_SAGE_CAN_GIFT_YOU_WITH_S2).addString("00:00").addItemName(presentId));
                } else if (presentId > 0 && presentId <= 40) {
                    player.sendPacket(new SystemMessage(SystemMsg.IT_IS_EVAS_BLESSING_PERIOD_S1_WILL_BE_EFFECTIVE_UNTIL_S2).addString(String.valueOf(presentId) + "%").addString("00:00"));
                    if (!player.getPlayerVariables().getBoolean(PlayerVariables.EVENT_GIVED_EXP)) {
                        player.getPremiumAccountComponent().getPremiumBonus().setRateXp(player.getPremiumAccountComponent().getPremiumBonus().getRateXp() + (presentId / 10));
                        player.getPremiumAccountComponent().getPremiumBonus().setRateSp(player.getPremiumAccountComponent().getPremiumBonus().getRateSp() + (presentId / 10));
                        player.getPlayerVariables().set(PlayerVariables.EVENT_GIVED_EXP, "true", -1);
                    }
                }
            } else {
                if (player.getPlayerVariables().getBoolean(PlayerVariables.EVENT_GIVED_EXP)) {
                    player.getPlayerVariables().remove(PlayerVariables.EVENT_GIVED_EXP);
                }
            }
        }
    }

    private static final class OnDeathListenerImpl implements OnDeathListener {
        @Override
        public void onDeath(final Creature self, final Creature killer) {
            if (self != null && killer != null) {
                if (self.isMonster() && (killer.getLevel() - self.getLevel() < 9)) {
                    if (Rnd.chance(EventsConfig.dropChance)) {
                        final MonsterInstance actor = (MonsterInstance) self;
                        if (Rnd.chance(EventsConfig.explosionChance)) {
                            actor.dropItem(killer.getPlayer(), br_seed_of_explosive_fire, 1);
                        } else {
                            actor.dropItem(killer.getPlayer(), br_seed_of_fire, 1);
                        }
                    }
                }
            }
        }
    }
}