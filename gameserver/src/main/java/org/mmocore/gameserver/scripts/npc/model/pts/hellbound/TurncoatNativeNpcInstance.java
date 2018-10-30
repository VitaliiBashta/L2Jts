package org.mmocore.gameserver.scripts.npc.model.pts.hellbound;

import org.mmocore.gameserver.manager.HellboundManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ChatUtils;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.ReflectionUtils;

/**
 * @author KilRoy
 */
public class TurncoatNativeNpcInstance extends NpcInstance {
    private static final String fnHi2 = "pts/hellbound/turncoat_native_npc002.htm";
    private static final String fnHi3 = "pts/hellbound/turncoat_native_npc002a.htm";
    private static final int item_bribe = 9676;

    public TurncoatNativeNpcInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (player == null) {
            return;
        }

        if (command.startsWith("menu_select?ask=-7801&")) {
            if (command.endsWith("reply=1")) {
                if (ItemFunctions.getItemCount(player, item_bribe) >= 10) {
                    ChatUtils.say(this, NpcString.ALRIGHT_NOW_LEODAS_IS_YOURS);
                    HellboundManager.reduceConfidence(50);
                    ItemFunctions.removeItem(player, item_bribe, 10);
                    ReflectionUtils.getDoor(19250003).openMe();
                    ReflectionUtils.getDoor(19250004).openMe();
                } else if (ItemFunctions.getItemCount(player, item_bribe) > 0 && ItemFunctions.getItemCount(player, item_bribe) < 10) {
                    showChatWindow(player, fnHi2);
                } else {
                    showChatWindow(player, fnHi3);
                }
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}