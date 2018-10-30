package org.mmocore.gameserver.scripts.events.NcSoft.TrickOrTransmutation;

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
public class TrickOrTransmutation extends Functions implements OnInitScriptListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrickOrTransmutation.class);

    private static final String trick_or_transmutation = "trick_or_transmutation";
    private static final int alchemist_chest_key = 9205;
    private static final OnDeathListener deathListener = new OnDeathListenerImpl();

    private static boolean isActive() {
        return ServerVariables.getString("TrickOrTransmutation", "off").equalsIgnoreCase("on");
    }

    @Override
    public void onInit() {
        if (isActive()) {
            CharListenerList.addGlobal(deathListener);
            LOGGER.info("Loaded Event: Trick Or Transmutation [state: activated]");
            doSpawnGroup(false, trick_or_transmutation);
        } else
            LOGGER.info("Loaded Event: Trick Or Transmutation [state: deactivated]");
    }

    @Bypass("events.NcSoft.TrickOrTransmutation:activateEvent")
    public void activateEvent(Player player, NpcInstance lastNpc, String[] args) {
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (!isActive()) {
            CharListenerList.addGlobal(deathListener);
            LOGGER.info("Event 'Trick Or Transmutation' started.");
            ServerVariables.set("TrickOrTransmutation", "on");
            show("admin/events/custom/ncsoft.htm", player);
            doSpawnGroup(false, trick_or_transmutation);
            player.sendAdminMessage("Event 'Trick Or Transmutation' started.");
        } else
            player.sendAdminMessage("Event 'Trick Or Transmutation' already started.");
    }

    @Bypass("events.NcSoft.TrickOrTransmutation:deactivateEvent")
    public void deactivateEvent(Player player, NpcInstance lastNpc, String[] args) {
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (isActive()) {
            CharListenerList.removeGlobal(deathListener);
            LOGGER.info("Event 'Trick Or Transmutation' stoped.");
            ServerVariables.set("TrickOrTransmutation", "off");
            show("admin/events/custom/ncsoft.htm", player);
            doSpawnGroup(true, trick_or_transmutation);
            player.sendAdminMessage("Event 'Trick Or Transmutation' stoped.");
        } else
            player.sendAdminMessage("Event 'Trick Or Transmutation' not started.");
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
                        actor.dropItem(killer.getPlayer(), alchemist_chest_key, 1);
                    }
                }
            }
        }
    }
}