package org.mmocore.authserver.component;

/**
 * Класс с бонусными рейтами для игрока
 */
public class Premium {
    private double rateXp = 1.;
    private double rateSp = 1.;
    private double questDropRate = 1.;
    private double dropAdena = 1.;
    private double dropItems = 1.;
    private double dropSpoil = 1.;
    private double bonusEpaulette = 1.;
    private long bonusExpire = 0;

    public double getRateXp() {
        return rateXp;
    }

    public void setRateXp(double rateXp) {
        this.rateXp = rateXp;
    }

    public double getRateSp() {
        return rateSp;
    }

    public void setRateSp(double rateSp) {
        this.rateSp = rateSp;
    }

    public double getQuestDropRate() {
        return questDropRate;
    }

    public void setQuestDropRate(double questDropRate) {
        this.questDropRate = questDropRate;
    }

    public double getDropAdena() {
        return dropAdena;
    }

    public void setDropAdena(double dropAdena) {
        this.dropAdena = dropAdena;
    }

    public double getDropItems() {
        return dropItems;
    }

    public void setDropItems(double dropItems) {
        this.dropItems = dropItems;
    }

    public double getDropSpoil() {
        return dropSpoil;
    }

    public void setDropSpoil(double dropSpoil) {
        this.dropSpoil = dropSpoil;
    }

    public long getBonusExpire() {
        return bonusExpire;
    }

    public void setBonusExpire(long bonusExpire) {
        this.bonusExpire = bonusExpire;
    }

    public double getBonusEpaulette() {
        return bonusEpaulette;
    }

    public void setBonusEpaulette(double bonusEpaulette) {
        this.bonusEpaulette = bonusEpaulette;
    }
}