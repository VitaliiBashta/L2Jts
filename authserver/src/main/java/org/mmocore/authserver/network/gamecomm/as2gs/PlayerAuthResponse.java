package org.mmocore.authserver.network.gamecomm.as2gs;

import org.mmocore.authserver.accounts.Account;
import org.mmocore.authserver.manager.SessionManager.Session;
import org.mmocore.authserver.network.gamecomm.SendablePacket;
import org.mmocore.authserver.network.lineage.SessionKey;

public class PlayerAuthResponse extends SendablePacket {
    private String login;
    private String passwordHash;
    private boolean authed;
    private int playOkID1;
    private int playOkID2;
    private int loginOkID1;
    private int loginOkID2;
    private double xp;
    private double sp;
    private double adena;
    private double drop;
    private double spoil;
    private double epaulette;
    private long bonusExpire;
    private int accountId;

    public PlayerAuthResponse(Session session, boolean authed) {
        Account account = session.getAccount();
        this.login = account.getLogin();
        this.authed = authed;
        if (authed) {
            SessionKey skey = session.getSessionKey();
            playOkID1 = skey.playOkID1;
            playOkID2 = skey.playOkID2;
            loginOkID1 = skey.loginOkID1;
            loginOkID2 = skey.loginOkID2;
            passwordHash = account.getPasswordHash();
            xp = account.getPremium().getRateXp();
            sp = account.getPremium().getRateSp();
            adena = account.getPremium().getDropAdena();
            drop = account.getPremium().getDropItems();
            spoil = account.getPremium().getDropSpoil();
            epaulette = account.getPremium().getBonusEpaulette();
            bonusExpire = account.getPremium().getBonusExpire();
            accountId = account.getAccountId();
        }
    }

    public PlayerAuthResponse(String account) {
        this.login = account;
        authed = false;
    }

    @Override
    protected void writeImpl() {
        writeC(0x02);
        writeS(login);
        writeC(authed ? 1 : 0);
        if (authed) {
            writeD(playOkID1);
            writeD(playOkID2);
            writeD(loginOkID1);
            writeD(loginOkID2);
            writeS(passwordHash);
            writeF(xp);
            writeF(sp);
            writeF(adena);
            writeF(drop);
            writeF(spoil);
            writeF(epaulette);
            writeQ(bonusExpire);
            writeD(accountId);
        }
    }
}
