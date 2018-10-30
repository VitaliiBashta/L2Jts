package org.jts.protection.manager;

import org.jts.protection.Protection;
import org.jts.protection.database.ProtectionDAO;
import org.jts.protection.manager.hwid.HWIDInfo;
import org.mmocore.gameserver.network.authcomm.AuthServerCommunication;
import org.mmocore.gameserver.network.authcomm.gs2as.ChangeAccessLevel;
import org.mmocore.gameserver.network.lineage.GameClient;
import org.mmocore.gameserver.utils.AutoBan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author KilRoy
 */
public class HWIDBanManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(HWIDBanManager.class);
    private Map<Integer, HWIDInfo> listHWIDBanned;

    private HWIDBanManager() {
        load();

        LOGGER.info("HWIDManager: Loaded " + listHWIDBanned.size() + " HWIDs banned");
    }

    public static HWIDBanManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    public void load() {
        listHWIDBanned = ProtectionDAO.getInstance().loadHWIDListBanned();
    }

    public BanType checkIsHWIDBanned(final GameClient client) {
        if (listHWIDBanned.isEmpty())
            return BanType.NONE;

        for (int i = 0; i < listHWIDBanned.size(); i++)
            if (listHWIDBanned.get(i).getHWID().equals(client.getHWID()))
                return listHWIDBanned.get(i).getBanType();

        return BanType.NONE;
    }

    public void systemBan(final GameClient client, final String comment, final BanType banType) {
        final int id = listHWIDBanned.size();
        final HWIDInfo hwidListBan = new HWIDInfo(id);
        if (client.getHWID() == null || client.getHWID().isEmpty()) {
            client.setHWID(Protection.DEFAULT_HWID);
            LOGGER.warn("Client: " + client.getIpAddr() + " returned empty HWID! Check him!");
        }
        hwidListBan.setHWIDBanned(client.getHWID());
        hwidListBan.setBanType(banType);
        switch (banType) {
            case NONE:
                client.getActiveChar().kick();
                break;
            case PLAYER_BAN:
                ProtectionDAO.getInstance().storeHWIDBanned(client, comment, banType);
                AutoBan.banPlayer(client.getActiveChar(), 365, comment, "SYSTEM_BOT");
                client.getActiveChar().kick();
                break;
            case ACCOUNT_BAN:
                ProtectionDAO.getInstance().storeHWIDBanned(client, comment, banType);
                AuthServerCommunication.getInstance().sendPacket(new ChangeAccessLevel(client.getLogin(), -100, 0));
                client.getActiveChar().kick();
                break;
        }
        listHWIDBanned.put(id, hwidListBan);
    }

    public enum BanType {
        NONE,
        PLAYER_BAN,
        ACCOUNT_BAN,
    }

    private static class LazyHolder {
        private static final HWIDBanManager INSTANCE = new HWIDBanManager();
    }
}