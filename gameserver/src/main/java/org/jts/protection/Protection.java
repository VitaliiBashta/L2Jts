package org.jts.protection;

import org.jts.protection.database.ProtectionDAO;
import org.jts.protection.handler.commands.AdminHWID;
import org.jts.protection.manager.HWIDBanManager;
import org.jts.protection.manager.HWIDListManager;
import org.mmocore.gameserver.ProtectType;
import org.mmocore.gameserver.configuration.config.protection.BaseProtectSetting;
import org.mmocore.gameserver.configuration.config.protection.JtsProtectionConfig;
import org.mmocore.gameserver.handler.admincommands.AdminCommandHandler;
import org.mmocore.gameserver.network.lineage.GameClient;
import org.mmocore.gameserver.network.lineage.serverpackets.LoginFail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author KilRoy
 */
public class Protection {
    public static final String DEFAULT_HWID = "DEFAULT_HWID_STRING";
    private static final Logger LOGGER = LoggerFactory.getLogger(Protection.class);
    private static Map<String, String> procBufInfo;
    private Protection() {
        if (isProtectEnabled()) {
            LOGGER.info("|========== JTS-Protection: Loading...==========|");
            registerAdminCommands();
            HWIDListManager.getInstance();
            HWIDBanManager.getInstance();
            loadProcBufInfo();
            LOGGER.info("|========== JTS-Protection: Loaded...===========|");
        }
    }

    public static Protection getInstance() {
        return LazyHolder.INSTANCE;
    }

    public static boolean isProtectEnabled() {
        return BaseProtectSetting.protectType == ProtectType.JTS && JtsProtectionConfig.PROTECTION_ENABLED;
    }

    private static void registerAdminCommands() {
        AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminHWID());
        LOGGER.info("AdminHWID is now registered in handler.");
    }

    public void enterToServer(final GameClient client, final String HWID) {
        if (isProtectEnabled() && JtsProtectionConfig.PROTECTION_ENABLED_HWID_REQUEST) {
            if (!HWID.isEmpty()) {
                switch (HWIDBanManager.getInstance().checkIsHWIDBanned(client)) {
                    case NONE:
                        HWIDListManager.getInstance().storeHWID(client);
                        break;
                    case ACCOUNT_BAN:
                        client.close(new LoginFail(LoginFail.INCORRECT_ACCOUNT_INFO_CONTACT_CUSTOMER_SUPPORT));
                        break;
                    case PLAYER_BAN: //TODO[K] - додумать вариант с ограничением доступа на перса
                        client.close(new LoginFail(LoginFail.INCORRECT_ACCOUNT_INFO_CONTACT_CUSTOMER_SUPPORT));
                        break;
                }
            } else if (!JtsProtectionConfig.PROTECTION_KICK_EMPTY_HWID)
                client.setHWID(DEFAULT_HWID);
            else {
                LOGGER.info("Account: " + client.getLogin() + " not verified in HWID! Closed connection.");
                client.close(new LoginFail(LoginFail.INCORRECT_ACCOUNT_INFO_CONTACT_CUSTOMER_SUPPORT));
            }
        }
    }

    public void storeProcBufInfo(final String login, final String buf) {
        if (procBufInfo.get(login) == null) {
            procBufInfo.put(login, buf);
            ProtectionDAO.getInstance().storeProcBufInfo(login, buf);
        } else if (!procBufInfo.get(login).equalsIgnoreCase(buf)) {
            procBufInfo.replace(login, buf);
            ProtectionDAO.getInstance().updateProcBufInfo(login, buf);
        }
    }

    private void loadProcBufInfo() {
        procBufInfo = ProtectionDAO.getInstance().loadProcBufInfo();
    }

    public enum GameGuardResponse {
        NONE,
        NORMAL_RESPONSE,
        KICK_RESPONSE,
        USED_BOT_RESPONS,
        GET_SN_IS_FALSE_RESPONSE,
        SN_NULL_LENGHT_RESPONSE,
        SP_OBJECT_CHANGED_RESPONSE,
        REQUEST_REVISION_VALIDATE,
        NOT_VALID_HOST_INFO
    }

    public enum BotResponse {
        NONE,
        L2TOWER,
        ADRENALIN,
        OTHER_SOFT,
        OTHER
    }

    private static class LazyHolder {
        private static final Protection INSTANCE = new Protection();
    }
}