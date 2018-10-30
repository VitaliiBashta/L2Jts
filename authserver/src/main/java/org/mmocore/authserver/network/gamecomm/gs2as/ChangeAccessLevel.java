package org.mmocore.authserver.network.gamecomm.gs2as;

import org.mmocore.authserver.accounts.Account;
import org.mmocore.authserver.network.gamecomm.ReceivablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangeAccessLevel extends ReceivablePacket {
    private static final Logger _log = LoggerFactory.getLogger(ChangeAccessLevel.class);

    private String account;
    private int level;
    private int banExpire;

    @Override
    protected void readImpl() {
        account = readS();
        level = readD();
        banExpire = readD();
    }

    @Override
    protected void runImpl() {
        Account acc = new Account(account);
        acc.restore();
        acc.setAccessLevel(level);
        acc.setBanExpire(banExpire);
        acc.update();
        _log.info("Смена бана аккаунта:" + account + " установил уровень бана:" + level + " и время:" + banExpire);
    }
}