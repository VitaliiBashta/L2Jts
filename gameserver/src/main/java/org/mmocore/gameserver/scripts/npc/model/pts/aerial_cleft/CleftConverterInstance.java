package org.mmocore.gameserver.scripts.npc.model.pts.aerial_cleft;

import org.mmocore.gameserver.data.xml.holder.MultiSellHolder;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * @author KilRoy
 */
public final class CleftConverterInstance extends NpcInstance {
    public CleftConverterInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        showChatWindow(player, "pts/gracia/aerial_cleft/cleft_converter001.htm");
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (command.equalsIgnoreCase("menu_select?ask=23030&")) {
            if (command.endsWith("reply=669")) {
                MultiSellHolder.getInstance().SeparateAndSend(669, player, getObjectId(), 0);
            }
        } else if (command.equalsIgnoreCase("menu_select?ask=23031")) {
            player.teleToClosestTown();
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}