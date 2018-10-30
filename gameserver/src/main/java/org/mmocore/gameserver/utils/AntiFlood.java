package org.mmocore.gameserver.utils;

import gnu.trove.iterator.TIntLongIterator;
import gnu.trove.map.hash.TIntLongHashMap;
import org.apache.commons.lang3.StringUtils;
import org.mmocore.gameserver.configuration.config.OtherConfig;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Player;

public class AntiFlood {
    private final Player player;
    private final TIntLongHashMap _recentReceivers = new TIntLongHashMap();
    private long _lastSent = 0L;
    private String _lastText = StringUtils.EMPTY;
    private long _lastHeroTime;
    private long _lastTradeTime;
    private long _lastShoutTime;
    private long _lastMailTime;

    private AntiFlood(final Player player) {
        this.player = player;
    }

    public static AntiFlood create(final Player player) {
        return new AntiFlood(player);
    }

    public boolean canTrade(final String text) {
        final long currentMillis = System.currentTimeMillis();

        if (currentMillis - _lastTradeTime < OtherConfig.holdTradeTime) {
            return false;
        }

        _lastTradeTime = currentMillis;
        return true;
    }

    public boolean canShout(final String text) {
        final long currentMillis = System.currentTimeMillis();

        if (currentMillis - _lastShoutTime < OtherConfig.holdShoutTime) {
            return false;
        }

        _lastShoutTime = currentMillis;
        return true;
    }

    public boolean canHero(final String text) {
        final long currentMillis = System.currentTimeMillis();

        if (currentMillis - _lastHeroTime < OtherConfig.holdHeroTime) {
            return false;
        }

        _lastHeroTime = currentMillis;
        return true;
    }

    public boolean canMail() {
        if (player == null) {
            return false;
        }
        if (player.getLevel() < ServerConfig.MAIL_LIMITS_PER_DAY[0]) {
            player.sendMessage(new CustomMessage("mail.minLevel").addNumber(ServerConfig.MAIL_LIMITS_PER_DAY[0]));
            return false;
        }
        if (player.getLevel() > ServerConfig.MAIL_LIMITS_PER_DAY[1]) {
            player.sendMessage(new CustomMessage("mail.maxLevel").addNumber(ServerConfig.MAIL_LIMITS_PER_DAY[1]));
            return false;
        }
        final long currentMillis = System.currentTimeMillis();
        if (currentMillis - _lastMailTime < ServerConfig.senderDelayMail) {
            if (ServerConfig.senderDelayMail == 60000)
                player.sendPacket(SystemMsg.THE_PREVIOUS_MAIL_WAS_FORWARDED_LESS_THAN_1_MINUTE_AGO_AND_THIS_CANNOT_BE_FORWARDED);
            else {
                player.sendPacket(new CustomMessage("mail.senderDelay"));
            }
            return false;
        }
        return true;
    }

    public boolean canTell(final int charId, final String text) {
        final long currentMillis = System.currentTimeMillis();
        long lastSent;

        final TIntLongIterator itr = _recentReceivers.iterator();

        int recent = 0;
        while (itr.hasNext()) {
            itr.advance();
            lastSent = itr.value();
            if (currentMillis - lastSent < (text.equalsIgnoreCase(_lastText) ? 600000L : 60000L)) {
                recent++;
            } else {
                itr.remove();
            }
        }

        lastSent = _recentReceivers.put(charId, currentMillis);

        long delay = 333L;
        if (recent > 3) {
            lastSent = _lastSent;
            delay = (recent - 3) * 3333L;
        }

        _lastText = text;
        _lastSent = currentMillis;

        return currentMillis - lastSent > delay;
    }

    public long getLastMailTime() {
        return _lastMailTime;
    }

    public void setLastMailTime(long _lastMailTime) {
        this._lastMailTime = _lastMailTime;
    }
}
