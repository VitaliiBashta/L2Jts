package org.mmocore.gameserver.scripts.npc.model.events;

import org.mmocore.gameserver.model.instances.WarehouseInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * @author KilRoy
 */
public class FantasyIslandTeleportInstance extends WarehouseInstance {
    public FantasyIslandTeleportInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        switch (getNpcId()) {
            case 4315: // g_fanta_keeper
                showChatWindow(player, "pts/fantasy_island/g_fanta_keeper001.htm");
                break;
            default:
                super.showChatWindow(player, val, arg);
                break;
        }
    }
}