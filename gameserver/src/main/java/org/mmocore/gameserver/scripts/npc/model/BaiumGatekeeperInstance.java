package org.mmocore.gameserver.scripts.npc.model;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.scripts.bosses.BaiumManager;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.world.GameObjectsStorage;

/**
 * @author pchayka
 */

public final class BaiumGatekeeperInstance extends NpcInstance {
    private static final int Baium = 29020;
    private static final int BaiumNpc = 29025;
    private static final int BloodedFabric = 4295;
    private static final Location TELEPORT_POSITION = new Location(113100, 14500, 10077);

    public BaiumGatekeeperInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (command.startsWith("request_entrance")) {
            if (ItemFunctions.getItemCount(player, BloodedFabric) > 0) {
                NpcInstance baiumBoss = GameObjectsStorage.getByNpcId(Baium);
                if (baiumBoss != null) {
                    showChatWindow(player, "default/31862-1.htm");
                    return;
                }
                NpcInstance baiumNpc = GameObjectsStorage.getByNpcId(BaiumNpc);
                if (baiumNpc == null) {
                    showChatWindow(player, "default/31862-2.htm");
                    return;
                }
                ItemFunctions.removeItem(player, BloodedFabric, 1, true);
                player.getPlayerVariables().set(PlayerVariables.BAIUM_PERMISSION, "granted", -1);
                player.teleToLocation(TELEPORT_POSITION);
            } else {
                showChatWindow(player, "default/31862-3.htm");
            }
        } else if (command.startsWith("request_wakeup")) {
            final String baiumPermission = player.getPlayerVariables().get(PlayerVariables.BAIUM_PERMISSION);
            if (baiumPermission == null || !"granted".equalsIgnoreCase(baiumPermission)) {
                showChatWindow(player, "default/29025-1.htm");
                return;
            }
            if (isBusy()) {
                showChatWindow(player, "default/29025-2.htm");
            }
            setBusy(true);
            BaiumManager.spawnBaium(this, player);
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}