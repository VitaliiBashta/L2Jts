package org.mmocore.gameserver.scripts.events.NcSoft.FreyaCelebration;

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
public class FreyaCelebration extends Functions implements OnInitScriptListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(FreyaCelebration.class);

    private static final String ev_whiteday = "ev_whiteday";
    private static final int[] event_present = new int[]{17130, 17131, 17132, 17133, 17134, 17135, 17136, 17137};
    private static final OnDeathListener deathListener = new OnDeathListenerImpl();

    private static boolean isActive() {
        return ServerVariables.getString("FreyaCelebration", "off").equalsIgnoreCase("on");
    }

    @Override
    public void onInit() {
        if (isActive()) {
            CharListenerList.addGlobal(deathListener);
            LOGGER.info("Loaded Event: Freya Celebration [state: activated]");
            doSpawnGroup(false, ev_whiteday);
        } else
            LOGGER.info("Loaded Event: Freya Celebration [state: deactivated]");
    }

    @Bypass("events.NcSoft.FreyaCelebration:activateEvent")
    public void activateEvent(Player player, NpcInstance lastNpc, String[] args) {
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (!isActive()) {
            CharListenerList.addGlobal(deathListener);
            LOGGER.info("Event 'Freya Celebration' started.");
            ServerVariables.set("FreyaCelebration", "on");
            show("admin/events/custom/ncsoft.htm", player);
            doSpawnGroup(false, ev_whiteday);
            player.sendAdminMessage("Event 'Freya Celebration' started.");
        } else
            player.sendAdminMessage("Event 'Freya Celebration' already started.");
    }

    @Bypass("events.NcSoft.FreyaCelebration:deactivateEvent")
    public void deactivateEvent(Player player, NpcInstance lastNpc, String[] args) {
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (isActive()) {
            CharListenerList.removeGlobal(deathListener);
            LOGGER.info("Event 'Freya Celebration' stoped.");
            ServerVariables.set("FreyaCelebration", "off");
            show("admin/events/custom/ncsoft.htm", player);
            doSpawnGroup(true, ev_whiteday);
            player.sendAdminMessage("Event 'Freya Celebration' stoped.");
        } else
            player.sendAdminMessage("Event 'Freya Celebration' not started.");
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
                        actor.dropItem(killer.getPlayer(), Rnd.get(event_present), 1);
                    }
                }
            }
        }
    }
}