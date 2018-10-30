package org.mmocore.gameserver.utils;

import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.database.dao.impl.AutoBanDAO;
import org.mmocore.gameserver.database.dao.impl.CharacterDAO;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.ZonedDateTime;

/**
 * @author ???
 * @author Java-man
 */
public final class AutoBan {
    private static final Logger LOGGER = LoggerFactory.getLogger(AutoBan.class);

    private static final ZonedDateTime MAX_BAN_TIME = ZonedDateTime.now().plusYears(10);

    public static boolean isBanned(final int objectId) {
        final Instant endBanDate = Instant.ofEpochSecond(AutoBanDAO.getInstance().selectEndBanDate(objectId));
        final Instant now = Instant.now();
        return endBanDate.isAfter(now);
    }

    public static void banPlayer(final Player actor, final int period, final String msg, final String GM) {
        final ZonedDateTime startBanDate = ZonedDateTime.now();
        final ZonedDateTime endBanDate;

        if (period == -1)
            endBanDate = MAX_BAN_TIME;
        else if (period > 0)
            endBanDate = startBanDate.plusDays(period);
        else {
            LOGGER.warn("Negative ban period: {}", period);
            return;
        }

        if (endBanDate.isBefore(startBanDate)) {
            LOGGER.warn("Negative ban period: {} to {}", startBanDate, endBanDate);
            return;
        }

        AutoBanDAO.getInstance().banPlayer(actor, startBanDate.toEpochSecond(), endBanDate.toEpochSecond(), msg, GM);
    }

    public static boolean banOfflinePlayer(final String actor, final int acc_level, final int period, final String msg, final String GM) {
        final int obj_id = CharacterDAO.getInstance().getObjectIdByName(actor);

        if (obj_id > 0)
            return false;

        CharacterDAO.getInstance().updateAccessLevel(obj_id, acc_level);

        if (acc_level < 0) {
            final ZonedDateTime startBanDate = ZonedDateTime.now();
            final ZonedDateTime endBanDate;

            if (period == -1)
                endBanDate = MAX_BAN_TIME;
            else if (period > 0)
                endBanDate = startBanDate.plusDays(period);
            else {
                LOGGER.warn("Negative ban period: {}", period);
                return false;
            }

            if (endBanDate.isBefore(startBanDate)) {
                LOGGER.warn("Negative ban period: {} to {}", startBanDate, endBanDate);
                return false;
            }

            AutoBanDAO.getInstance().banOfflinePlayer(obj_id, startBanDate.toEpochSecond(), endBanDate.toEpochSecond(), msg, GM);
        } else {
            AutoBanDAO.getInstance().removeBan(obj_id);
        }

        return true;
    }

    public static void Banned(final Player actor, final int period, final String msg) {
        banPlayer(actor, period, msg, "AutoBan");
    }

    public static boolean ChatBan(final String actor, final int period, String GM) {
        final long NoChannel = period * 60000L;
        final int obj_id = CharacterDAO.getInstance().getObjectIdByName(actor);

        if (obj_id == 0)
            return false;

        final Player plyr = World.getPlayer(actor);
        GM = ServerConfig.BANCHAT_PRIVATE_NICK ? GM : "[incognito - [server]]";
        if (plyr != null) {
            if (NoChannel > 0)
                plyr.sendMessage(new CustomMessage("org.mmocore.gameserver.Util.AutoBan.ChatBan").addString(GM).addNumber(period));
            else
                plyr.sendMessage(new CustomMessage("org.mmocore.gameserver.Util.AutoBan.ChatUnBan").addString(GM));
            plyr.updateNoChannel(NoChannel);
        } else
            AutoBanDAO.getInstance().banChat(obj_id, NoChannel > 0 ? NoChannel / 1000 : NoChannel);

        return true;
    }
}