package org.mmocore.authserver.network.gamecomm.gs2as;

import org.mmocore.authserver.accounts.Account;
import org.mmocore.authserver.network.gamecomm.ReceivablePacket;

public class UpdateEmailRequest extends ReceivablePacket {
    private String account;
    private String l2email;
    private int checkEmail;

    @Override
    protected void readImpl() {
        account = readS();
        l2email = readS();
        checkEmail = readC();
    }

    @Override
    protected void runImpl() {
        Account acc = new Account(account);
        acc.restore();
        acc.setEmail(l2email);
        acc.setCheckEmail(checkEmail);
        acc.update();
    }
}
