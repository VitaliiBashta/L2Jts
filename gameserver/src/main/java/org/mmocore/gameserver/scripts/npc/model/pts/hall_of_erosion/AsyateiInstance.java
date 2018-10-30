package org.mmocore.gameserver.scripts.npc.model.pts.hall_of_erosion;

import org.mmocore.gameserver.data.xml.holder.MultiSellHolder;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * @author Mangol
 */
public class AsyateiInstance extends NpcInstance {
    private static final int multisellno1 = 647;
    private static final int multisellno2 = 698;
    private static final int check_item = 13692;
    private static final String fnHi2 = "pts/hall_of_erosion/trader_immortality002.htm";

    public AsyateiInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        if (player.getInventory().getCountOf(check_item) >= 1) {
            showChatWindow(player, fnHi2);
        } else {
            super.showChatWindow(player, val);
        }
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }
        if (command.equalsIgnoreCase("menu_select?ask=-7801&reply=2")) {
            if (player.getInventory().getCountOf(check_item) >= 1) {
                MultiSellHolder.getInstance().SeparateAndSend(multisellno2, player, getObjectId(), 0);
            }
        } else if (command.equalsIgnoreCase("menu_select?ask=-7801&reply=1")) {
            MultiSellHolder.getInstance().SeparateAndSend(multisellno1, player, getObjectId(), 0);
        } else {
            super.onBypassFeedback(player, command);
        }
    }

    @Override
    public boolean isMerchantNpc() {
        return true;
    }
}
