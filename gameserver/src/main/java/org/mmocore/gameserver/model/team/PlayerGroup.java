package org.mmocore.gameserver.model.team;

import org.mmocore.gameserver.network.lineage.components.IBroadcastPacket;
import org.mmocore.gameserver.object.Player;

import java.util.Collections;
import java.util.Iterator;

/**
 * @author VISTALL
 * @date 14:03/22.06.2011
 */
public interface PlayerGroup extends Iterable<Player> {
    PlayerGroup EMPTY = new PlayerGroup() {
        @Override
        public void broadCast(final IBroadcastPacket... packet) {
        }

        @Override
        public int getMemberCount() {
            return 0;
        }

        @Override
        public Player getGroupLeader() {
            return null;
        }

        @Override
        public Iterator<Player> iterator() {
            return Collections.emptyIterator();
//                    Iterators.emptyIterator();
        }
    };

    void broadCast(IBroadcastPacket... packet);

    int getMemberCount();

    /**
     * @return the leader of group.
     */
    Player getGroupLeader();
}
