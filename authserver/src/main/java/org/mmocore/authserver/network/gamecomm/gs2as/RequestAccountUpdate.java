package org.mmocore.authserver.network.gamecomm.gs2as;

import org.mmocore.authserver.accounts.Account;
import org.mmocore.authserver.network.gamecomm.GameServer;
import org.mmocore.authserver.network.gamecomm.ReceivablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mangol
 * @since 11.09.2016
 */
public final class RequestAccountUpdate extends ReceivablePacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestAccountUpdate.class);
    private String accountName;
    private String lastIp;
    private String updatePasswordHash;

    @Override
    protected void readImpl() {
        accountName = readS();
        lastIp = readS();
        updatePasswordHash = readS();
    }

    @Override
    protected void runImpl() {
        final GameServer gameServer = getGameServer();
        if (!gameServer.isAuthed()) {
            return;
        }
        final Account account = new Account(accountName);
        account.restore();
        account.setPasswordHash(updatePasswordHash);
        account.update();
        LOGGER.info("Account = " + accountName + " change password! LastIp = " + lastIp);
    }
}
