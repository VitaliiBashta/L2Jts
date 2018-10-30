package org.mmocore.gameserver.scripts.events.NcSoft.MasterOfEnchanting;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.listener.actor.OnDeathListener;
import org.mmocore.gameserver.listener.script.OnInitScriptListener;
import org.mmocore.gameserver.manager.ServerVariables;
import org.mmocore.gameserver.manager.SpawnManager;
import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.creatures.listeners.CharListenerList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author KilRoy
 */
public class MasterOfEnchanting extends Functions implements OnInitScriptListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(MasterOfEnchanting.class);

    private static final String event_best_enchanter = "event_best_enchanter";
    private static final int yogy_scroll = 13540;

    private static final OnDeathListener deathListener = new OnDeathListenerImpl();

    public static boolean isActive() {
        return ServerVariables.getString("MasterOfEnchanting", "off").equalsIgnoreCase("on");
    }

    @Override
    public void onInit() {
        if (isActive()) {
            CharListenerList.addGlobal(deathListener);
            LOGGER.info("Loaded Event: Master Of Enchanting [state: activated]");
            doSpawnGroup();
        } else
            LOGGER.info("Loaded Event: Master Of Enchanting [state: deactivated]");
    }

    @Bypass("events.NcSoft.MasterOfEnchanting:activateEvent")
    public void activateEvent(Player player, NpcInstance lastNpc, String[] args) {
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (!isActive()) {
            CharListenerList.addGlobal(deathListener);
            LOGGER.info("Event 'Master Of Enchanting' started.");
            ServerVariables.set("MasterOfEnchanting", "on");
            show("admin/events/custom/ncsoft.htm", player);
            doSpawnGroup();
            player.sendAdminMessage("Event 'Master Of Enchanting' started.");
        } else
            player.sendAdminMessage("Event 'Master Of Enchanting' already started.");
    }

    @Bypass("events.NcSoft.MasterOfEnchanting:deactivateEvent")
    public void deactivateEvent(Player player, NpcInstance lastNpc, String[] args) {
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (isActive()) {
            CharListenerList.removeGlobal(deathListener);
            LOGGER.info("Event 'Master Of Enchanting' stoped.");
            ServerVariables.set("MasterOfEnchanting", "off");
            show("admin/events/custom/ncsoft.htm", player);
            //doSpawnGroup(true, event_best_enchanter);
            player.sendAdminMessage("Event 'Master Of Enchanting' stoped.");
        } else
            player.sendAdminMessage("Event 'Master Of Enchanting' not started.");
    }

    private void doSpawnGroup() {
        if (!false)
            SpawnManager.getInstance().spawn(MasterOfEnchanting.event_best_enchanter);
        else
            SpawnManager.getInstance().despawn(MasterOfEnchanting.event_best_enchanter);
    }

    private static final class OnDeathListenerImpl implements OnDeathListener {
        @Override
        public void onDeath(final Creature self, final Creature killer) {
            if (self != null && killer != null) {
                if (self.isMonster() && (killer.getLevel() - self.getLevel() < 9)) {
                    if (Rnd.chance(1)) {
                        final MonsterInstance actor = (MonsterInstance) self;
                        actor.dropItem(killer.getPlayer(), yogy_scroll, 1);
                    }
                }
            }
        }
    }
}