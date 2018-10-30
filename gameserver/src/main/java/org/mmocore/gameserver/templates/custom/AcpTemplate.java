package org.mmocore.gameserver.templates.custom;

import org.mmocore.gameserver.configuration.config.custom.AcpConfig;

/**
 * Create by Mangol on 30.12.2015.
 */
public class AcpTemplate {
    private static final int MAX_REUSE = AcpConfig.MAX_REUSE;
    private static final int MIN_REUSE = AcpConfig.MIN_REUSE;
    private boolean autoSmallCp;
    private boolean autoCp;
    private boolean autoHp;
    private boolean autoMp;
    private double smallCpPercent = AcpConfig.smallCpPercent;
    private double cpPercent = AcpConfig.cpPercent;
    private double hpPercent = AcpConfig.hpPercent;
    private double mpPercent = AcpConfig.mpPercent;
    private int smallCpItemId = AcpConfig.smallCpItemId;
    private int cpItemId = AcpConfig.cpItemId;
    private int hpItemId = AcpConfig.hpItemId;
    private int mpItemId = AcpConfig.mpItemId;
    private int reuseSmallCp = AcpConfig.reuseSmallCp;
    private int reuseCp = AcpConfig.reuseCp;
    private int reuseHp = AcpConfig.reuseHp;
    private int reuseMp = AcpConfig.reuseMp;

    public AcpTemplate() {
    }

    public AcpTemplate(boolean autoCp, boolean autoSmallCp, boolean autoHp, boolean autoMp, double cpPercent, double smallCpPercent, double hpPercent, double mpPercent, int cpItemId, int smallCpItemId, int hpItemId, int mpItemId, int reuseCp, int reuseSmallCp, int reuseHp, int reuseMp) {
        this.autoCp = autoCp;
        this.autoSmallCp = autoSmallCp;
        this.autoHp = autoHp;
        this.autoMp = autoMp;
        this.cpPercent = cpPercent;
        this.smallCpPercent = smallCpPercent;
        this.hpPercent = hpPercent;
        this.mpPercent = mpPercent;
        this.cpItemId = cpItemId;
        this.smallCpItemId = smallCpItemId;
        this.hpItemId = hpItemId;
        this.mpItemId = mpItemId;
        this.reuseCp = reuseCp;
        this.reuseSmallCp = reuseSmallCp;
        this.reuseHp = reuseHp;
        this.reuseMp = reuseMp;
    }

    public int getReuseMp() {
        return reuseMp;
    }

    public void setReuseMp(final int reuseMp) {
        this.reuseMp = Math.max(Math.min(reuseMp * 1000, MAX_REUSE), MIN_REUSE);
    }

    public boolean isAutoCp() {
        return autoCp;
    }

    public void setAutoCp(final boolean autoCp) {
        this.autoCp = autoCp;
    }

    public boolean isAutoHp() {
        return autoHp;
    }

    public void setAutoHp(final boolean autoHp) {
        this.autoHp = autoHp;
    }

    public boolean isAutoMp() {
        return autoMp;
    }

    public void setAutoMp(final boolean autoMp) {
        this.autoMp = autoMp;
    }

    public double getCpPercent() {
        return cpPercent;
    }

    public void setCpPercent(final double cpPercent) {
        this.cpPercent = Math.max(Math.min(cpPercent, 100), 1);
    }

    public double getHpPercent() {
        return hpPercent;
    }

    public void setHpPercent(final double hpPercent) {
        this.hpPercent = Math.max(Math.min(hpPercent, 100), 1);
    }

    public double getMpPercent() {
        return mpPercent;
    }

    public void setMpPercent(final double mpPercent) {
        this.mpPercent = Math.max(Math.min(mpPercent, 100), 1);
    }

    public int getCpItemId() {
        return cpItemId;
    }

    public void setCpItemId(final int cpItemId) {
        this.cpItemId = cpItemId;
    }

    public int getHpItemId() {
        return hpItemId;
    }

    public void setHpItemId(final int hpItemId) {
        this.hpItemId = hpItemId;
    }

    public int getMpItemId() {
        return mpItemId;
    }

    public void setMpItemId(final int mpItemId) {
        this.mpItemId = mpItemId;
    }

    public int getReuseCp() {
        return reuseCp;
    }

    public void setReuseCp(final int reuseCp) {
        this.reuseCp = Math.max(Math.min(reuseCp * 1000, MAX_REUSE), MIN_REUSE);
    }

    public int getReuseHp() {
        return reuseHp;
    }

    public void setReuseHp(final int reuseHp) {
        this.reuseHp = Math.max(Math.min(reuseHp * 1000, MAX_REUSE), MIN_REUSE);
    }

    public boolean isAutoSmallCp() {
        return autoSmallCp;
    }

    public void setAutoSmallCp(boolean autoSmallCp) {
        this.autoSmallCp = autoSmallCp;
    }

    public double getSmallCpPercent() {
        return smallCpPercent;
    }

    public void setSmallCpPercent(double smallCpPercent) {
        this.smallCpPercent = Math.max(Math.min(smallCpPercent, 100), 1);
    }

    public int getSmallCpItemId() {
        return smallCpItemId;
    }

    public void setSmallCpItemId(int smallCpItemId) {
        this.smallCpItemId = smallCpItemId;
    }

    public int getReuseSmallCp() {
        return reuseSmallCp;
    }

    public void setReuseSmallCp(int reuseSmallCp) {
        this.reuseSmallCp = Math.max(Math.min(reuseSmallCp * 1000, MAX_REUSE), MIN_REUSE);
    }
}
