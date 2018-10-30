package org.mmocore.gameserver.scripts.npc.model.pts.pagan_temple;

import org.mmocore.gameserver.model.instances.DoorInstance;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.ReflectionUtils;

/**
 * @author : Mangol
 */
public class SanctuaryOuttaGuardBasicInstance extends NpcInstance {
    private static final String GateOpenHTML = "pts/pagan_temple/sanctuary_outta_guard002.htm";

    public SanctuaryOuttaGuardBasicInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }
        if (command.startsWith("menu_select?ask=502&") && command.endsWith("reply=2")) {
            if (ItemFunctions.getItemCount(player, 8064) >= 1) {
                ItemFunctions.removeItem(player, 8064, 1);
                ItemFunctions.addItem(player, 8065, 1);
            }
            final DoorInstance door = ReflectionUtils.getDoor(19160001);
            door.openMe();
            showChatWindow(player, GateOpenHTML);
        } else {
            super.onBypassFeedback(player, command);
        }
    }
    /*
     * EventHandler CREATED() { myself::AddTimerEx( 3456, 15 * 1000 ); }
     * EventHandler TIMER_FIRED_EX( timer_id ) { if( timer_id == 3456 ) {
     * myself::LookNeighbor( 300 ); myself::AddTimerEx( 3456, 15 * 1000 ); }
     * super; }
     */
}
