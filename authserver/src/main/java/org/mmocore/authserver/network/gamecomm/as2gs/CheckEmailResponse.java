package org.mmocore.authserver.network.gamecomm.as2gs;

import org.mmocore.authserver.accounts.Account;
import org.mmocore.authserver.network.gamecomm.SendablePacket;

/**
 * @author cyberhuman
 * @date 22:26/28.04.2012
 */

public class CheckEmailResponse extends SendablePacket {
    private String account;
    private int checkEmail;

    public CheckEmailResponse(String account) {
        Account acc = new Account(account);
        acc.restore();
        this.checkEmail = acc.getCheckEmail();
        this.account = account;
    }

    @Override
    protected void writeImpl() {
        writeC(0x05);
        writeS(account);
        writeC(checkEmail);
    }

}
