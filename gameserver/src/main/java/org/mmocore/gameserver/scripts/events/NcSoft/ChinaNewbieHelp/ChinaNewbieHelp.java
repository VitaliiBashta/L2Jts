package org.mmocore.gameserver.scripts.events.NcSoft.ChinaNewbieHelp;

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
public class ChinaNewbieHelp extends Functions implements OnInitScriptListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChinaNewbieHelp.class);

    private static final String br_newbie_supporter = "br_newbie_supporter";

    private static boolean isActive() {
        return ServerVariables.getString("ChinaNewbieHelp", "off").equalsIgnoreCase("on");
    }

    @Override
    public void onInit() {
        if (isActive()) {
            LOGGER.info("Loaded Event: China Newbie Help [state: activated]");
            doSpawnGroup(false);
        } else
            LOGGER.info("Loaded Event: China Newbie Help [state: deactivated]");
    }

    @Bypass("events.NcSoft.ChinaNewbieHelp:activateEvent")
    public void activateEvent(Player player, NpcInstance lastNpc, String[] args) {
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (!isActive()) {
            LOGGER.info("Event 'China Newbie Help' started.");
            ServerVariables.set("ChinaNewbieHelp", "on");
            show("admin/events/custom/ncsoft.htm", player);
            doSpawnGroup(false);
            player.sendAdminMessage("Event 'China Newbie Help' started.");
        } else
            player.sendAdminMessage("Event 'China Newbie Help' already started.");
    }

    @Bypass("events.NcSoft.ChinaNewbieHelp:deactivateEvent")
    public void deactivateEvent(Player player, NpcInstance lastNpc, String[] args) {
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (isActive()) {
            LOGGER.info("Event 'China Newbie Help' stoped.");
            ServerVariables.set("ChinaNewbieHelp", "off");
            show("admin/events/custom/ncsoft.htm", player);
            doSpawnGroup(true);
            player.sendAdminMessage("Event 'China Newbie Help' stoped.");
        } else
            player.sendAdminMessage("Event 'China Newbie Help' not started.");
    }

    private void doSpawnGroup(final boolean despawn) {
        if (!despawn)
            SpawnManager.getInstance().spawn(ChinaNewbieHelp.br_newbie_supporter);
        else
            SpawnManager.getInstance().despawn(ChinaNewbieHelp.br_newbie_supporter);
    }
}