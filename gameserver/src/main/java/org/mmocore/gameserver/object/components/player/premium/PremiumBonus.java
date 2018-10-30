package org.mmocore.gameserver.object.components.player.premium;

/**
 * Класс с бонусными рейтами для игрока
 */
public class PremiumBonus implements Cloneable {
    public static final int NO_BONUS = 0;
    public static final int BONUS_GLOBAL_ON_AUTHSERVER = 1;
    public static final int BONUS_GLOBAL_ON_GAMESERVER = 2;
    private double rateXp = 1.;
    private double rateSp = 1.;
    private double questRewardRate = 1.;
    private double questDropRate = 1.;
    private double dropAdena = 1.;
    private double dropItems = 1.;
    private double dropSpoil = 1.;
    private double bonusEpaulette = 1.;
    private double enchantChance = 1;
    private double attributeChance = 1;
    private double craftChance = 1;
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

    public double getQuestRewardRate() {
        return questRewardRate;
    }

    public void setQuestRewardRate(double questRewardRate) {
        this.questRewardRate = questRewardRate;
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

    public double getEnchantChance() {
        return enchantChance;
    }

    public void setEnchantChance(double enchantChance) {
        this.enchantChance = enchantChance;
    }

    public double getAttributeChance() {
        return attributeChance;
    }

    public void setAttributeChance(double attributeChance) {
        this.attributeChance = attributeChance;
    }

    public double getCraftChance() {
        return craftChance;
    }

    public void setCraftChance(double craftChance) {
        this.craftChance = craftChance;
    }

    public void setDefault() {
        rateXp = 1.;
        rateSp = 1.;
        questRewardRate = 1.;
        questDropRate = 1.;
        dropAdena = 1.;
        dropItems = 1.;
        dropSpoil = 1.;
        bonusEpaulette = 1.;
        attributeChance = 1;
        enchantChance = 1;
        craftChance = 1;
        bonusExpire = 0;
    }

    @Override
    public PremiumBonus clone() {
        PremiumBonus premiumBonus = new PremiumBonus();
        premiumBonus.rateXp = rateXp;
        premiumBonus.rateSp = rateSp;
        premiumBonus.questRewardRate = questRewardRate;
        premiumBonus.questDropRate = questDropRate;
        premiumBonus.dropAdena = dropAdena;
        premiumBonus.dropItems = dropItems;
        premiumBonus.dropSpoil = dropSpoil;
        premiumBonus.bonusEpaulette = bonusEpaulette;
        premiumBonus.attributeChance = attributeChance;
        premiumBonus.enchantChance = enchantChance;
        premiumBonus.craftChance = craftChance;
        premiumBonus.bonusExpire = bonusExpire;
        return premiumBonus;
    }
}