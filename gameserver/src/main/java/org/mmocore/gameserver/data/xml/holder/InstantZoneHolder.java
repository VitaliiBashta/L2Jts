package org.mmocore.gameserver.data.xml.holder;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.InstantZone;
import org.quartz.CronExpression;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author VISTALL
 * @date 1:35/30.06.2011
 */
public class InstantZoneHolder extends AbstractHolder {
    private final TIntObjectMap<InstantZone> zones = new TIntObjectHashMap<>();

    public static InstantZoneHolder getInstance() {
        return LazyHolder.INSTANCE;
    }

    public void addInstantZone(final InstantZone zone) {
        zones.put(zone.getId(), zone);
    }

    public InstantZone getInstantZone(final int id) {
        return zones.get(id);
    }

    private CronExpression getResetReuseById(final int id) {
        final InstantZone zone = getInstantZone(id);
        return zone == null ? null : zone.getResetReuse();
    }

    public int getMinutesToNextEntrance(final int id, final Player player) {
        final CronExpression resetReuse = getResetReuseById(id);
        if (resetReuse == null) {
            return 0;
        }

        Long time = null;
        if (getSharedReuseInstanceIds(id) != null && !getSharedReuseInstanceIds(id).isEmpty()) {
            final List<Long> reuses = getSharedReuseInstanceIds(id).stream().filter(i -> player.getInstanceReuse(i) != null).map(player::getInstanceReuse).collect(Collectors
                    .toList());
            if (!reuses.isEmpty()) {
                Collections.sort(reuses);
                time = reuses.get(reuses.size() - 1);
            }
        } else {
            time = player.getInstanceReuse(id);
        }
        if (time == null) {
            return 0;
        }
        return (int) Math.max((resetReuse.getNextValidTimeAfter(new Date(time)).getTime() - System.currentTimeMillis()) / 60000L, 0);
    }


    public List<Integer> getSharedReuseInstanceIds(final int id) {
        if (getInstantZone(id).getSharedReuseGroup() < 1) {
            return null;
        }
        return zones.valueCollection().stream().filter(iz -> iz.getSharedReuseGroup() > 0 && getInstantZone(id).getSharedReuseGroup() > 0
                && iz.getSharedReuseGroup() == getInstantZone(id)
                .getSharedReuseGroup()).map(InstantZone::getId).collect(Collectors.toList());
    }

    public List<Integer> getSharedReuseInstanceIdsByGroup(final int groupId) {
        if (groupId < 1) {
            return null;
        }
        return zones.valueCollection().stream().filter(iz -> iz.getSharedReuseGroup() > 0 && iz.getSharedReuseGroup() == groupId)
                .map(InstantZone::getId).collect(Collectors.toList());
    }

    @Override
    public int size() {
        return zones.size();
    }

    @Override
    public void clear() {
        zones.clear();
    }

    private static class LazyHolder {
        private static final InstantZoneHolder INSTANCE = new InstantZoneHolder();
    }
}
