package org.mmocore.gameserver.model.entity.events.objects;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import org.mmocore.gameserver.model.entity.events.impl.SiegeEvent;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.world.GameObjectsStorage;

import java.time.Instant;

public final class CMGSiegeClanObject extends SiegeClanObject {
    private final TIntSet players = new TIntHashSet();
    private long param;

    public CMGSiegeClanObject(final String type, final Clan clan, final long param, final Instant date) {
        super(type, clan, param, date);
        this.param = param;
    }

    public CMGSiegeClanObject(final String type, final Clan clan, final long param) {
        super(type, clan, param);
        this.param = param;
    }

    public void addPlayer(final int objectId) {
        players.add(objectId);
    }

    @Override
    public long getParam() {
        return param;
    }

    public void setParam(final long param) {
        this.param = param;
    }

    @Override
    public boolean isParticle(final Player player) {
        return players.contains(player.getObjectId());
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void setEvent(final boolean start, final SiegeEvent event) {
        for (final int i : players.toArray()) {
            final Player player = GameObjectsStorage.getPlayer(i);
            if (player != null) {
                if (start) {
                    player.addEvent(event);
                } else {
                    player.removeEvent(event);
                }
                player.broadcastCharInfo();
            }
        }
    }

    public TIntSet getPlayers() {
        return players;
    }
}
