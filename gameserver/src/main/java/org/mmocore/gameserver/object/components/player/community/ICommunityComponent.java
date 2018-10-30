package org.mmocore.gameserver.object.components.player.community;

import org.mmocore.gameserver.object.components.player.community.enums.DBState;

import java.util.Map;
import java.util.Optional;
import java.util.Stack;

/**
 * @author Java-man
 * @since 12.01.2016
 */
public interface ICommunityComponent {
    void restore();

    void loadBufferScheme();

    void loadTeleportPoints();

    void deleteTeleportId(final int teleportId);

    void addTeleportId(final TeleportPoint teleportPoint);

    Map<Integer, TeleportPoint> getTeleportPoints();

    void deleteBuffScheme(final String schemeName);

    void addBuffScheme(final Scheme scheme);

    Map<String, Scheme> getBuffSchemes();

    Optional<Scheme> getBuffScheme(final String schemeName);

    Stack<String> getStackBypass();

    int getLeaseTransformId();

    void setLeaseTransformId(final int leaseTransformId, final DBState db);

    void refreshLeaseTransformStats();
}
