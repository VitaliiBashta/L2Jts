package org.mmocore.gameserver.utils;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.manager.MapRegionManager;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.base.RestartType;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.mapregion.RestartArea;
import org.mmocore.gameserver.templates.mapregion.RestartPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TeleportUtils {
    public static final Location DEFAULT_RESTART = new Location(17817, 170079, -3530);
    private static final Logger _log = LoggerFactory.getLogger(TeleportUtils.class);

    private TeleportUtils() {
    }

    public static Location getRestartLocation(final Player player, final RestartType restartType) {
        return getRestartLocation(player, player.getLoc(), restartType);
    }

    public static Location getRestartLocation(final Player player, final Location from, final RestartType restartType) {
        final Reflection r = player.getReflection();
        if (r != ReflectionManager.DEFAULT) {
            if (r.getCoreLoc() != null) {
                return r.getCoreLoc();
            } else if (r.getReturnLoc() != null) {
                return r.getReturnLoc();
            }
        }

        final Clan clan = player.getClan();

        if (clan != null) {
            // If teleport to clan hall
            if (restartType == RestartType.TO_CLANHALL && clan.getHasHideout() != 0) {
                return ResidenceHolder.getInstance().getResidence(clan.getHasHideout()).getOwnerRestartPoint();
            }

            // If teleport to castle
            if (restartType == RestartType.TO_CASTLE && clan.getCastle() != 0) {
                return ResidenceHolder.getInstance().getResidence(clan.getCastle()).getOwnerRestartPoint();
            }

            // If teleport to fortress
            if (restartType == RestartType.TO_FORTRESS && clan.getHasFortress() != 0) {
                return ResidenceHolder.getInstance().getResidence(clan.getHasFortress()).getOwnerRestartPoint();
            }
        }

        if (player.getKarma() > 1) {
            if (player.getPKRestartPoint() != null) {
                return player.getPKRestartPoint();
            }
        } else {
            if (player.getRestartPoint() != null) {
                return player.getRestartPoint();
            }
        }

        final RestartArea ra = MapRegionManager.getInstance().getRegionData(RestartArea.class, from);
        if (ra != null) {
            final RestartPoint rp = ra.getRestartPoint().get(player.getPlayerTemplateComponent().getPlayerRace());

            final Location restartPoint = Rnd.get(rp.getRestartPoints());
            final Location PKrestartPoint = Rnd.get(rp.getPKrestartPoints());

            return player.getKarma() > 1 ? PKrestartPoint : restartPoint;
        }

        _log.warn("Cannot find restart location from coordinates: " + from + '!');

        return DEFAULT_RESTART;
    }
}