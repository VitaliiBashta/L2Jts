package org.mmocore.authserver.network.lineage.clientpackets;

import org.mmocore.authserver.accounts.Account;
import org.mmocore.authserver.configuration.config.LoginConfig;
import org.mmocore.authserver.database.DatabaseFactory;
import org.mmocore.authserver.manager.GameServerManager;
import org.mmocore.authserver.manager.IpBanManager;
import org.mmocore.authserver.manager.SessionManager;
import org.mmocore.authserver.manager.SessionManager.Session;
import org.mmocore.authserver.network.crypt.PasswordHash;
import org.mmocore.authserver.network.gamecomm.GameServer;
import org.mmocore.authserver.network.gamecomm.as2gs.GetAccountInfo;
import org.mmocore.authserver.network.lineage.L2LoginClient;
import org.mmocore.authserver.network.lineage.L2LoginClient.LoginClientState;
import org.mmocore.authserver.network.lineage.serverpackets.LoginFail.LoginFailReason;
import org.mmocore.authserver.network.lineage.serverpackets.LoginOk;
import org.mmocore.authserver.utils.Log;
import org.mmocore.commons.crypt.PBKDF2Hash;
import org.mmocore.commons.database.dbutils.DbUtils;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Format: b[128]ddddddhc
 * b[128]: the rsa encrypted block with the login an password
 */
public class RequestAuthLogin extends L2LoginClientPacket {
    private byte[] _raw = new byte[128];

    @Override
    protected void readImpl() {
        readB(_raw);
        readD();
        readD();
        readD();
        readD();
        readD();
        readD();
        readH();
        readC();
    }

    @Override
    protected void runImpl() throws Exception {
        L2LoginClient client = getClient();

        byte[] decrypted;
        try {
            Cipher rsaCipher = Cipher.getInstance("RSA/ECB/nopadding");
            rsaCipher.init(Cipher.DECRYPT_MODE, client.getRSAPrivateKey());
            decrypted = rsaCipher.doFinal(_raw, 0x00, 0x80);
        } catch (Exception e) {
            client.closeNow(true);
            return;
        }

        String user = new String(decrypted, 0x5E, 14, StandardCharsets.UTF_8).trim();
        String password = new String(decrypted, 0x6C, 16, StandardCharsets.UTF_8).trim();
        //int ncotp = ((decrypted[0x7f] & 0xFF) << 24) | ((decrypted[0x7e] & 0xFF) << 16) | ((decrypted[0x7d] & 0xFF) << 8) | ((decrypted[0x7c] & 0xFF) << 0);
        int currentTime = (int) (System.currentTimeMillis() / 1000L);

        user = user.toLowerCase();
        Account account = new Account(user);
        account.restore();

        boolean passwordCorrect = false;
        if (account.getPasswordHash() == null) {
            if (LoginConfig.AUTO_CREATE_ACCOUNTS && user.matches(LoginConfig.ANAME_TEMPLATE) && password.matches(LoginConfig.APASSWD_TEMPLATE)) {
                final String passwordHash = PBKDF2Hash.createHash(password);
                account.setPasswordHash(passwordHash);
                account.save();
                account.restore();
                passwordCorrect = true;
            } else {
                passwordCorrect = false;
            }
        } else {
            try {
                passwordCorrect = PBKDF2Hash.validatePassword(password, account.getPasswordHash());
            } catch (Exception e) {
                passwordCorrect = false;
            }

            if (!passwordCorrect) {
                // проверяем не зашифрован ли пароль одним из устаревших но поддерживаемых алгоритмов
                for (final PasswordHash hash : LoginConfig.LEGACY_CRYPT)
                    if (hash.compare(password, account.getPasswordHash())) {
                        passwordCorrect = true;
                        // если пароль зашифрован старым алгоритмом, то делаем новым хэш, основанный на новом алгоритме
                        final String passwordHash = PBKDF2Hash.createHash(password);
                        account.setPasswordHash(passwordHash);
                        account.update();
                        break;
                    }
            }
        }

        if (!IpBanManager.getInstance().tryLogin(client.getIpAddress(), passwordCorrect)) {
            client.closeNow(false);
            return;
        }

        if (!passwordCorrect) {
            client.close(LoginFailReason.REASON_USER_OR_PASS_WRONG);
            return;
        }

        if (account.getAccessLevel() < 0) {
            client.close(LoginFailReason.REASON_ACCESS_FAILED);
            return;
        }

        if (account.getBanExpire() > currentTime) {
            client.close(LoginFailReason.REASON_ACCESS_FAILED);
            return;
        }

        if (!account.isAllowedIP(client.getIpAddress())) {
            client.close(LoginFailReason.REASON_ATTEMPTED_RESTRICTED_IP);
            client.setAccountId(account.getAccountId());
            if (LoginConfig.LOG_AUTH_AND_FLAG_MAIL) {
                logGameTransaction(client, true);
            }
            return;
        }

        for (GameServer gs : GameServerManager.getInstance().getGameServers()) {
            if (gs.getProtocol() >= 2 && gs.isAuthed()) {
                gs.sendPacket(new GetAccountInfo(user));
            }
        }

        account.setLastAccess(currentTime);
        account.setLastIP(client.getIpAddress());
        //account.setAllowedIP(client.getIpAddress()); TODO

        Log.LogAccount(account);

        Session session = SessionManager.getInstance().openSession(account);

        client.setAuthed(true);
        client.setLogin(user);
        client.setAccount(account);
        client.setAccountId(account.getAccountId());
        client.setSessionKey(session.getSessionKey());
        client.setState(LoginClientState.AUTHED);

        if (LoginConfig.LOG_AUTH_AND_FLAG_MAIL) {
            logGameTransaction(client, false);
        }
        //client.sendPacket(new LoginFail(LoginFailReason.REASON_SECURITY_CARD_NUMB_I));
        client.sendPacket(new LoginOk(client.getSessionKey()));
    }

    private void logGameTransaction(L2LoginClient client, boolean loginFail) {
        if (client == null || client.getIpAddress() == null) {
            return;
        }

        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("INSERT INTO auth_log (act_time,login,ip,failed,account_id,acc) VALUES (UNIX_TIMESTAMP(),1,?,?,?,?)");
            statement.setString(1, client.getIpAddress());
            statement.setBoolean(2, loginFail);
            statement.setInt(3, client.getAccountId());
            statement.setString(4, client.getLogin());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbUtils.closeQuietly(con, statement);
        }

        if (loginFail) {
            notifyPlayer(client.getAccountId());
        }
    }

    private void notifyPlayer(int accountId) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("UPDATE `account_block` SET " + "`send_email` = 1 " + "WHERE `account_id` = ?");
            statement.setInt(1, accountId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }
}
