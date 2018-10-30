package org.mmocore.authserver.network.gamecomm.gs2as;

import org.mmocore.authserver.network.gamecomm.ReceivablePacket;
import org.mmocore.authserver.network.gamecomm.as2gs.CheckEmailResponse;

/**
 * @author cyberhuman
 * @date 22:26/28.04.2012
 */

public class CheckEmailRequest extends ReceivablePacket {
    private String account;

    @Override
    protected void readImpl() {
        account = readS();
    }

    @Override
    protected void runImpl() {
        sendPacket(new CheckEmailResponse(account));
    }
}
