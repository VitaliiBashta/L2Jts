package org.mmocore.authserver.network.lineage.clientpackets;

import org.mmocore.authserver.network.lineage.L2LoginClient;
import org.mmocore.authserver.network.lineage.SessionKey;
import org.mmocore.authserver.network.lineage.serverpackets.LoginFail.LoginFailReason;
import org.mmocore.authserver.network.lineage.serverpackets.ServerList;

/**
 * Format: ddc
 * d: fist part of session id
 * d: second part of session id
 * c: ?
 */
public class RequestServerList extends L2LoginClientPacket {
    private int _loginOkID1;
    private int _loginOkID2;

    @Override
    protected void readImpl() {
        _loginOkID1 = readD();
        _loginOkID2 = readD();
    }

    @Override
    protected void runImpl() {
        L2LoginClient client = getClient();
        SessionKey skey = client.getSessionKey();
        if (skey == null || !skey.checkLoginPair(_loginOkID1, _loginOkID2)) {
            client.close(LoginFailReason.REASON_ACCESS_FAILED);
            return;
        }

        client.sendPacket(new ServerList(client.getAccount()));
    }
}