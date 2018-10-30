package handler.bbs;

import org.mmocore.gameserver.network.lineage.serverpackets.ExBuyList;
import org.mmocore.gameserver.network.lineage.serverpackets.ExBuySellList;
import org.mmocore.gameserver.object.Player;

/**
 * Created by Hack
 * Date: 23.09.2016 17:10
 */
public class CommunitySell extends ScriptBbsHandler {

    @Override
    public String[] getBypassCommands() {
        return new String[] { "_bbssell", "_bbs_itemsell" };
    }

    @Override
    public void onBypassCommand(Player player, String bypass) {
        switch (bypass) {
            case "_bbssell":
            case "_bbs_itemsell":
                if (!check(player))
                    return;
                player.sendPacket(new ExBuyList(null, player), new ExBuySellList(player, false));
                break;
        }
    }

    private boolean check(Player player) {
        if (player == null)
            return false;
        if(!player.getPlayerAccess().UseShop)
            return false;
        if(player.getEnchantScroll() != null)
            return false;
        if(player.isInStoreMode())
            return false;
        if(player.isInTrade())
            return false;
        return true;
    }

    @Override
    public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5) {}
}
