package org.mmocore.gameserver.network.lineage.clientpackets;

import org.jts.protection.Protection;
import org.mmocore.gameserver.ProtectType;
import org.mmocore.gameserver.Shutdown;
import org.mmocore.gameserver.configuration.config.ServicesConfig;
import org.mmocore.gameserver.configuration.config.protection.BaseProtectSetting;
import org.mmocore.gameserver.database.dao.impl.HwidLocksDAO;
import org.mmocore.gameserver.network.authcomm.AuthServerCommunication;
import org.mmocore.gameserver.network.authcomm.SessionKey;
import org.mmocore.gameserver.network.authcomm.gs2as.PlayerAuthRequest;
import org.mmocore.gameserver.network.lineage.GameClient;
import org.mmocore.gameserver.network.lineage.serverpackets.LoginFail;
import org.mmocore.gameserver.network.lineage.serverpackets.ServerClose;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.akumu.smartguard.core.GuardConfig;
import ru.akumu.smartguard.core.SmartCore;
import ru.akumu.smartguard.core.manager.modules.ModulesManager;
import ru.akumu.smartguard.core.manager.session.L2SessionData;
import ru.akumu.smartguard.core.network.packets.MsgPacket;
//import ru.akumu.smartguard.wrappers.SmartClient;

/**
 * cSdddddQd
 * loginName + keys must match what the loginserver used.
 */
public class AuthLogin extends L2GameClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthLogin.class);
    private String _loginName;
    private int _playKey1;
    private int _playKey2;
    private int _loginKey1;
    private int _loginKey2;
    private int languageType;
    private byte[] data = null;

    @Override
    protected void readImpl() {
        _loginName = readS(32).toLowerCase();
        _playKey2 = readD();
        _playKey1 = readD();
        _loginKey1 = readD();
        _loginKey2 = readD();
        languageType = readD();
        readQ(); // unk1
        readD(); // unk2

        if (BaseProtectSetting.protectType == ProtectType.SMART && GuardConfig.ProtectionEnabled) {
            if (_buf.remaining() > 2) {
                int dataLen = readH();
                if (_buf.remaining() >= dataLen) {
                    data = new byte[dataLen];
                    readB(data);
                }
            }
        }
    }

    @Override
    protected void runImpl() {
        final GameClient client = getClient();

        final SessionKey key = new SessionKey(_loginKey1, _loginKey2, _playKey1, _playKey2);
        client.setSessionId(key);
        client.setLoginName(_loginName);
        client.setLanguageType(languageType);

        //Protection section
        if (Protection.isProtectEnabled())
            Protection.getInstance().enterToServer(client, client.getHWID());
        if (BaseProtectSetting.protectType == ProtectType.SMART && GuardConfig.ProtectionEnabled) {
            if (data != null) {
//                SmartClient sclient = new SmartClient(client);
//                MsgPacket result = SmartCore.checkClientLogin(sclient, _loginName, new L2SessionData(_playKey2, _playKey1, _loginKey1, _loginKey2), data);
//                if (result == null) {
//                    ModulesManager.getInstance().onPlayerLogin(sclient);
//                } else {
//                    sclient.closeWithPacket(result);
            }
            } else {
                getClient().close(ServerClose.STATIC);
        }
//        }
        //Protection section end
        if (Shutdown.getInstance().getMode() != Shutdown.NONE && Shutdown.getInstance().getSeconds() <= 15) {
            client.closeNow(false);
        } else {
            if (AuthServerCommunication.getInstance().isShutdown()) {
                client.close(new LoginFail(LoginFail.SYSTEM_ERROR_LOGIN_LATER));
                return;
            }
            if (!checkHwidAccess(client))
                return;

            final GameClient oldClient = AuthServerCommunication.getInstance().addWaitingClient(client);
            if (oldClient != null) {
                oldClient.close(ServerClose.STATIC);
            }

            AuthServerCommunication.getInstance().sendPacket(new PlayerAuthRequest(client));
        }
    }

    private boolean checkHwidAccess(GameClient client) {
        String hwid = client.getHWID();
        if (hwid != null && !hwid.equals("") && ServicesConfig.allowHwidLock) {
            String lockedHwid = HwidLocksDAO.getInstance().getHwid(client.getLogin());
            if (lockedHwid != null && !lockedHwid.equals(hwid)) {
                client.close(new LoginFail(LoginFail.INCORRECT_ACCOUNT_INFO_CONTACT_CUSTOMER_SUPPORT));
                return false;
            }
        }
        return true;
    }

}