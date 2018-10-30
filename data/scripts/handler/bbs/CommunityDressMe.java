package handler.bbs;

import org.mmocore.gameserver.utils.Util;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.data.xml.holder.*;

/**
 * 
 * Date: 21.05.2017 23:10
 */
public class CommunityDressMe extends ScriptBbsHandler {

    @Override
    public String[] getBypassCommands() {
        return new String[] { "_bbsdressme" };
    }

    @Override
    public void onBypassCommand(Player player, String bypass) {
        if(bypass.startsWith("_bbsdressme")) {
            HtmlMessage html = new HtmlMessage(5).setFile("command/dressme/index.htm");
            html.replace("%weapons%", Util.formatAdena(DressWeaponHolder.getInstance().size()));
            html.replace("%armors%", Util.formatAdena(DressArmorHolder.getInstance().size()));
            html.replace("%shields%", Util.formatAdena(DressShieldHolder.getInstance().size()));
            html.replace("%cloaks%", Util.formatAdena(DressCloakHolder.getInstance().size()));
            player.sendPacket(html);
        }
    }

    @Override
    public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5) {}
}
