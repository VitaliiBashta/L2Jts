package services;

import org.mmocore.gameserver.configuration.config.ServicesConfig;
import org.mmocore.gameserver.database.dao.impl.HwidLocksDAO;
import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.listener.script.OnInitScriptListener;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;

/**
 * Created by Hack
 * Date: 13.09.2016 0:47
 */
public class HwidLock implements OnInitScriptListener {
    @Override
    public void onInit() {}

    @Bypass("services.HwidLock:lock")
    public void lock(Player player, NpcInstance npc, String[] arg) {
        if (player == null || !ServicesConfig.allowHwidLock || player.getNetConnection().getHWID() == null)
            return;
        HwidLocksDAO.getInstance().setHwid(player.getAccountName(), player.getNetConnection().getHWID());
        player.setHwidLockVisual(true);
        player.sendMessage("Your HWID successfully saved.");
    }

    @Bypass("services.HwidLock:unlock")
    public void unlock(Player player, NpcInstance npc, String[] arg) {
        if (player == null)
            return;
        HwidLocksDAO.getInstance().removeHwid(player.getAccountName());
        player.setHwidLockVisual(false);
        player.sendMessage("Your account successfully unlocked.");
    }
}
