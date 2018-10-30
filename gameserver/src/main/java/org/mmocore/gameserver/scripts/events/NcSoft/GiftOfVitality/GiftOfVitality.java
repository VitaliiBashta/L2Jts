package org.mmocore.gameserver.scripts.events.NcSoft.GiftOfVitality;

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
public class GiftOfVitality extends Functions implements OnInitScriptListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(GiftOfVitality.class);

    private static final String br_vitality2010_event = "br_vitality2010_event";

    private static boolean isActive() {
        return ServerVariables.getString("GiftOfVitality", "off").equalsIgnoreCase("on");
    }

    @Override
    public void onInit() {
        if (isActive()) {
            LOGGER.info("Loaded Event: Gift Of Vitality [state: activated]");
            doSpawnGroup(false, br_vitality2010_event);
        } else
            LOGGER.info("Loaded Event: Gift Of Vitality [state: deactivated]");
    }

    @Bypass("events.NcSoft.GiftOfVitality:activateEvent")
    public void activateEvent(Player player, NpcInstance lastNpc, String[] args) {
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (!isActive()) {
            LOGGER.info("Event 'Gift Of Vitality' started.");
            ServerVariables.set("GiftOfVitality", "on");
            show("admin/events/custom/ncsoft.htm", player);
            doSpawnGroup(false, br_vitality2010_event);
            player.sendAdminMessage("Event 'Gift Of Vitality' started.");
        } else
            player.sendAdminMessage("Event 'Gift Of Vitality' already started.");
    }

    @Bypass("events.NcSoft.GiftOfVitality:deactivateEvent")
    public void deactivateEvent(Player player, NpcInstance lastNpc, String[] args) {
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (isActive()) {
            LOGGER.info("Event 'Gift Of Vitality' stoped.");
            ServerVariables.set("GiftOfVitality", "off");
            show("admin/events/custom/ncsoft.htm", player);
            doSpawnGroup(true, br_vitality2010_event);
            player.sendAdminMessage("Event 'Gift Of Vitality' stoped.");
        } else
            player.sendAdminMessage("Event 'Gift Of Vitality' not started.");
    }

    private void doSpawnGroup(final boolean despawn, final String group) {
        if (!despawn)
            SpawnManager.getInstance().spawn(group);
        else
            SpawnManager.getInstance().despawn(group);
    }
}