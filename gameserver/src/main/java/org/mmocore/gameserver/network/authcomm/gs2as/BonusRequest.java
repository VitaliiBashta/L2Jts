package org.mmocore.gameserver.network.authcomm.gs2as;

import org.mmocore.gameserver.network.authcomm.SendablePacket;
import org.mmocore.gameserver.object.components.player.premium.PremiumBonus;

public class BonusRequest extends SendablePacket {
    //0 - delete, 1 - insert
    private final int state;
    private final String account;
    private double xp;
    private double sp;
    private double adena;
    private double drop;
    private double spoil;
    private double epaulette;
    private long bonusExpire;

    public BonusRequest(final String account, final int state) {
        this.account = account;
        this.state = state;
    }

    public BonusRequest(final String account, final int state, final PremiumBonus premiumBonus) {
        this.account = account;
        this.state = state;
        this.xp = premiumBonus.getRateXp();
        this.sp = premiumBonus.getRateSp();
        this.adena = premiumBonus.getDropAdena();
        this.drop = premiumBonus.getDropItems();
        this.spoil = premiumBonus.getDropSpoil();
        this.epaulette = premiumBonus.getBonusEpaulette();
        this.bonusExpire = premiumBonus.getBonusExpire();
    }

    protected void writeImpl() {
        writeC(0x10);
        if (state == 0) {
            writeS(account);
            writeH(state);
        } else if (state == 1) {
            writeS(account);
            writeH(state);
            writeF(xp);
            writeF(sp);
            writeF(adena);
            writeF(drop);
            writeF(spoil);
            writeF(epaulette);
            writeQ(bonusExpire);
        }
    }
}