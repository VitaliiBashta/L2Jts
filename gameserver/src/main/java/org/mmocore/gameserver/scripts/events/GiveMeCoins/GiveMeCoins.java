package org.mmocore.gameserver.scripts.events.GiveMeCoins;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.listener.actor.OnDeathListener;
import org.mmocore.gameserver.listener.actor.player.OnPlayerEnterListener;
import org.mmocore.gameserver.listener.script.OnInitScriptListener;
import org.mmocore.gameserver.manager.ServerVariables;
import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.creatures.listeners.CharListenerList;
import org.mmocore.gameserver.object.components.player.PlayerListenerList;
import org.mmocore.gameserver.utils.AnnouncementUtils;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author KilRoy, feroom
 */
public class GiveMeCoins extends Functions implements OnInitScriptListener {
    private static final Logger logger = LoggerFactory.getLogger(GiveMeCoins.class);
    private static final OnDeathListenerImpl playerEnterListener = new OnDeathListenerImpl();
    private static final OnPlayerEnterListenerImpl deathListener = new OnPlayerEnterListenerImpl();
    private static int EVENT_WORLDDROP_ITEM1_CHANCE = 99;
    private static int EVENT_WORLDDROP_ITEM2_CHANCE = 1;
    private static int EVENT_ITEM1 = 4356;
    private static int EVENT_ITEM2 = 4037;
    private static int EVENT_WORLDDROP_ITEM1_COUNT = 1;
    private static int EVENT_WORLDDROP_ITEM2_COUNT = 1;
    private static boolean active = false;

    /**
     * Читает статус эвента из базы.
     *
     * @return
     */
    private static boolean isActive() {
        return active;
    }

    private static boolean CheckDropChamp(Creature mob, Creature killer) {
        return mob != null && mob.getLevel() >= 50 && mob.getKnownSkill(4407) != null && !mob.isRaid() && killer != null && killer.getPlayer() != null && killer.getLevel() - mob.getLevel() < 10;
    }

    @Override
    public void onInit() {
        active = ServerVariables.getString("GiveMeCoins", "off").equalsIgnoreCase("on");
        if (isActive()) {
            PlayerListenerList.addGlobal(playerEnterListener);
            CharListenerList.addGlobal(deathListener);
            logger.info("Loaded Event: Give me Coins [state: activated]");
        } else {
            logger.info("Loaded Event: Give me Coins [state: deactivated]");
        }
    }

    /**
     * Запускает эвент
     */
    @Bypass("events.GiveMeCoins:activateEvent")
    public void activateEvent(Player player, NpcInstance lastNpc, String[] args) {
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (!isActive()) {
            PlayerListenerList.addGlobal(playerEnterListener);
            CharListenerList.addGlobal(deathListener);
            logger.info("Event 'Coin Champ drop' started.");
            AnnouncementUtils.announceToAllFromStringHolder("scripts.events.GiveMeCoins.AnnounceEventStarted");

            ServerVariables.set("GiveMeCoins", "on");

            show("admin/events/events.htm", player);
        } else
            player.sendMessage("Event 'Coin Champ drop' already started.");
    }

    /**
     * Останавливает эвент
     */
    @Bypass("events.GiveMeCoins:deactivateEvent")
    public void deactivateEvent(Player player, NpcInstance lastNpc, String[] args) {
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (isActive()) {
            PlayerListenerList.removeGlobal(playerEnterListener);
            CharListenerList.removeGlobal(deathListener);
            logger.info("Event 'Coin Champ drop' stopped.");
            AnnouncementUtils.announceToAllFromStringHolder("scripts.events.GiveMeCoins.AnnounceEventStoped");

            ServerVariables.set("GiveMeCoins", "off");

            show("admin/events/events.htm", player);
        } else
            player.sendMessage("Event 'Coin Champ drop' not started.");
    }

    /**
     * Обработчик смерти мобов, управляющий эвентовым дропом
     */
    private static class OnDeathListenerImpl implements OnDeathListener {
        @Override
        public void onDeath(Creature self, Creature killer) {
            if (active && CheckDropChamp(self, killer)) {
                if (Rnd.chance(EVENT_WORLDDROP_ITEM1_CHANCE * ((MonsterInstance) self).getTemplate().rateHp))
                    ItemFunctions.addItem(killer.getPlayer(), EVENT_ITEM1, EVENT_WORLDDROP_ITEM1_COUNT);
                if (Rnd.chance(EVENT_WORLDDROP_ITEM2_CHANCE * ((MonsterInstance) self).getTemplate().rateHp))
                    ItemFunctions.addItem(killer.getPlayer(), EVENT_ITEM2, EVENT_WORLDDROP_ITEM2_COUNT);
            }
        }
    }

    private static class OnPlayerEnterListenerImpl implements OnPlayerEnterListener {
        @Override
        public void onPlayerEnter(Player player) {
            if (active)
                AnnouncementUtils.announceToPlayerFromStringHolder("scripts.events.GiveMeCoins.AnnounceEventStarted", player);
        }
    }
}