package org.mmocore.gameserver.object.components.player.bot_punish;

import org.mmocore.gameserver.database.dao.impl.BotReportDAO;
import org.mmocore.gameserver.network.authcomm.AuthServerCommunication;
import org.mmocore.gameserver.network.authcomm.gs2as.RequestReportPoints;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.bot_punish.BotPunish.Punish;
import org.mmocore.gameserver.skills.EffectType;

/**
 * @author KilRoy
 */
public class BotPunishComponent {
    private final Player _playerRef;
    private BotPunish autoHuntingPunish = null;
    private int reportsPoints;
    private long firstExp;

    public BotPunishComponent(final Player player) {
        _playerRef = player;
    }

    public Player getPlayer() {
        return _playerRef;
    }

    /**
     * Sets exp holded by the character on log in
     *
     * @param current exp
     */
    public void setFirstExp(final long expValue) {
        firstExp = expValue;
    }

    /**
     * Will return true if the player has gained exp
     * since logged in
     *
     * @return
     */
    public boolean hasEarnedExp() {
        if (firstExp != 0) {
            return true;
        }
        return false;
    }

    /**
     * Initializes his _botPunish object with the specified punish
     * and for the specified time
     *
     * @param punishType
     * @param minsOfPunish
     */
    public synchronized void setPunishDueBotting(final Punish punishType, final int minsOfPunish) {
        if (autoHuntingPunish == null) {
            autoHuntingPunish = new BotPunish(punishType, minsOfPunish);
        }
    }

    /**
     * Returns the current object-representative player punish
     *
     * @return
     */
    public BotPunish getPlayerPunish() {
        return autoHuntingPunish;
    }

    /**
     * Returns the type of punish being applied
     *
     * @return
     */
    public Punish getBotPunishType() {
        return autoHuntingPunish.getBotPunishType();
    }

    /**
     * Will return true if the player has any bot punishment
     * active
     *
     * @return
     */
    public boolean isBeingPunished() {
        return autoHuntingPunish != null;
    }

    /**
     * Will end the punishment once a player attempt to
     * perform any forbid action and his punishment has
     * expired
     */
    public void endPunishment() {
        if (getPlayer().getEffectList().getEffectByType(EffectType.ReportBlock) != null) {
            getPlayer().getEffectList().getEffectByType(EffectType.ReportBlock).exit();
        }
        BotReportDAO.getInstance().deletePunish(getPlayer());
        autoHuntingPunish = null;
    }

    public void requestReportsPoints() {
        AuthServerCommunication.getInstance().sendPacket(new RequestReportPoints(getPlayer().getAccountName(), false, 0));
    }

    public void requestReportsPoints(final boolean bool, final int value) {
        AuthServerCommunication.getInstance().sendPacket(new RequestReportPoints(getPlayer().getAccountName(), bool, value));
    }

    public int getReportsPoints() {
        return reportsPoints;
    }

    public void setReportsPoints(final int points) {
        reportsPoints = points;
    }

    public void reduceReportsPoints() {
        if (reportsPoints != 0) {
            reportsPoints--;
        }
    }
}