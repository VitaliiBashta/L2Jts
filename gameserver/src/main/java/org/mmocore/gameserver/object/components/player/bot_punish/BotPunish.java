package org.mmocore.gameserver.object.components.player.bot_punish;

public class BotPunish {
    // Kind of punish
    private Punish autoHuntingPunishPunishment;
    // Time the punish will last
    private long punishTime;
    // Punis time (in secs)
    private int punishDuration;

    public BotPunish(Punish punish, int mins) {
        autoHuntingPunishPunishment = punish;
        punishTime = System.currentTimeMillis() + mins * 60 * 1000;
        punishDuration = mins * 60;
    }

    /**
     * Returns the current punishment type
     *
     * @return Punish (BotPunish enum)
     */
    public Punish getBotPunishType() {
        return autoHuntingPunishPunishment;
    }

    /**
     * Returns the time (in millis) when the player
     * punish started
     *
     * @return long
     */
    public long getPunishStarterTime() {
        return punishTime;
    }

    /**
     * Returns the duration (in seconds) of the applied
     * punish
     *
     * @return int
     */
    public int getDuration() {
        return punishDuration;
    }

    /**
     * Return the time left to end up this punish
     *
     * @return long
     */
    public long getPunishTimeLeft() {
        long left = System.currentTimeMillis() - punishTime;
        return left;
    }

    /**
     * @return true if the player punishment has
     * expired
     */
    public boolean canWalk() {
        if (autoHuntingPunishPunishment == Punish.MOVEBAN && System.currentTimeMillis() - punishTime <= 0) {
            return false;
        }
        return true;
    }

    /**
     * @return true if the player punishment has
     * expired
     */
    public boolean canTalk() {
        if (autoHuntingPunishPunishment == Punish.CHATBAN && System.currentTimeMillis() - punishTime <= 0) {
            return false;
        }
        return true;
    }

    /**
     * @return true if the player punishment has
     * expired
     */
    public boolean canJoinParty() {
        if (autoHuntingPunishPunishment == Punish.PARTYBAN && System.currentTimeMillis() - punishTime <= 0) {
            return false;
        }
        return true;
    }

    /**
     * @return true if the player punishment has
     * expired
     */
    public boolean canPerformAction() {
        if (autoHuntingPunishPunishment == Punish.ACTIONBAN && System.currentTimeMillis() - punishTime <= 0) {
            return false;
        }
        return true;
    }

    /**
     * @return true if the player punishment has
     * expired
     */
    public boolean canAttack() {
        if (autoHuntingPunishPunishment == Punish.ATTACKBAN && System.currentTimeMillis() - punishTime <= 0) {
            return false;
        }
        return true;
    }

    // Type of punishments
    public enum Punish {
        CHATBAN,
        MOVEBAN,
        PARTYBAN,
        ACTIONBAN,
        ATTACKBAN
    }
}