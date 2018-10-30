package org.mmocore.gameserver.templates.custom.community;

/**
 * @author Mangol
 * @since 02.02.2016
 */
public class RatingTemplate {
    private final String name;
    private final int sex;
    private final boolean online;
    private final int timeGame;
    private final int pvp;
    private final int pk;
    private long adenaCount;

    public RatingTemplate(final String name, final int pvp, final int pk, final int sex, final boolean online, final int timeGame) {
        this.name = name;
        this.sex = sex;
        this.online = online;
        this.timeGame = timeGame;
        this.pvp = pvp;
        this.pk = pk;
    }

    public RatingTemplate(final String name, final int pvp, final int pk, final int sex, final boolean online, final int timeGame, final long adenaCount) {
        this.name = name;
        this.sex = sex;
        this.online = online;
        this.timeGame = timeGame;
        this.pvp = pvp;
        this.pk = pk;
        this.adenaCount = adenaCount;
    }

    public String getName() {
        return name;
    }

    public int getSex() {
        return sex;
    }

    public int getTimeGame() {
        return timeGame;
    }

    public int getPvp() {
        return pvp;
    }

    public int getPk() {
        return pk;
    }

    public long getAdenaCount() {
        return adenaCount;
    }

    public boolean isOnline() {
        return online;
    }
}
