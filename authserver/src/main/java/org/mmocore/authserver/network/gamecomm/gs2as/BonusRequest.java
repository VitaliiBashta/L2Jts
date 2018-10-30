package org.mmocore.authserver.network.gamecomm.gs2as;

import org.mmocore.authserver.component.Premium;
import org.mmocore.authserver.database.dao.impl.AccountBonusDAO;
import org.mmocore.authserver.network.gamecomm.ReceivablePacket;

public class BonusRequest extends ReceivablePacket {
    private String account;
    private double xp;
    private double sp;
    private double adena;
    private double drop;
    private double spoil;
    private double epaulette;
    private long bonusExpire;
    private int state;

    @Override
    protected void readImpl() {
        account = readS();
        state = readH();
        if (state == 1) {
            xp = readF();
            sp = readF();
            adena = readF();
            drop = readF();
            spoil = readF();
            epaulette = readF();
            bonusExpire = readQ();
        }
    }

    @Override
    protected void runImpl() {
        if (state == 0) {
            AccountBonusDAO.getInstance().delete(account);
        } else if (state == 1) {
            final Premium premium = new Premium();
            premium.setRateXp(xp);
            premium.setRateSp(sp);
            premium.setDropAdena(adena);
            premium.setDropItems(drop);
            premium.setDropSpoil(spoil);
            premium.setBonusEpaulette(epaulette);
            premium.setBonusExpire(bonusExpire);
            AccountBonusDAO.getInstance().insert(account, premium);
        }
    }
}
