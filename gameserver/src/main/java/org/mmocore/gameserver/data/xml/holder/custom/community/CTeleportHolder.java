package org.mmocore.gameserver.data.xml.holder.custom.community;

import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.gameserver.object.components.player.community.TeleportPoint;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Create by Mangol on 12.12.2015.
 */
public class CTeleportHolder extends AbstractHolder {
    private static final CTeleportHolder INSTANCE = new CTeleportHolder();
    private final Map<Integer, TeleportPoint> teleportPoints = new HashMap<>();

    public static CTeleportHolder getInstance() {
        return INSTANCE;
    }

    public void addTeleportPoint(final TeleportPoint teleportPoint) {
        teleportPoints.put(teleportPoint.getId(), teleportPoint);
    }

    public Optional<TeleportPoint> getTeleportId(final int id) {
        final Optional<TeleportPoint> teleportPoint = Optional.ofNullable(teleportPoints.get(id));
        if (teleportPoint.isPresent()) {
            return teleportPoint;
        }
        return Optional.empty();
    }

    @Override
    public int size() {
        return teleportPoints.size();
    }

    @Override
    public void clear() {
        teleportPoints.clear();
    }
}
