package org.mmocore.gameserver.model.entity.events.objects;


import org.mmocore.gameserver.database.dao.impl.SiegePlayerDAO;
import org.mmocore.gameserver.model.entity.events.impl.SiegeEvent;
import org.mmocore.gameserver.model.entity.residence.Residence;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.world.GameObjectsStorage;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * @author VISTALL
 * @date 14:23/31.03.2011
 */
public class CTBSiegeClanObject extends SiegeClanObject {
    private final List<Integer> _players = new ArrayList<>();
    private long _npcId;

    public CTBSiegeClanObject(final String type, final Clan clan, final long param, final Instant date) {
        super(type, clan, param, date);
        _npcId = param;
    }

    public CTBSiegeClanObject(final String type, final Clan clan, final long param) {
        this(type, clan, param, Instant.now());
    }

    public void select(final Residence r) {
        _players.addAll(SiegePlayerDAO.getInstance().select(r, getObjectId()));
    }

    public List<Integer> getPlayers() {
        return _players;
    }

    @Override
    public void setEvent(final boolean start, final SiegeEvent event) {
        for (final int i : getPlayers()) {
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

    @Override
    public boolean isParticle(final Player player) {
        return _players.contains(player.getObjectId());
    }

    @Override
    public long getParam() {
        return _npcId;
    }

    public void setParam(final int npcId) {
        _npcId = npcId;
    }
}
