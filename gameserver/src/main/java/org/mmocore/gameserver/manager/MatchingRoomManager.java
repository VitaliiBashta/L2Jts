package org.mmocore.gameserver.manager;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.gameserver.model.team.matching.MatchingRoom;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.mapregion.RestartArea;
import org.mmocore.gameserver.templates.mapregion.RestartPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author VISTALL
 * @date 0:08/12.06.2011
 */
public class MatchingRoomManager {
    private final RoomsHolder[] _holder = new RoomsHolder[2];
    private final Set<Player> _players = new CopyOnWriteArraySet<>();

    private MatchingRoomManager() {
        _holder[MatchingRoom.PARTY_MATCHING] = new RoomsHolder();
        _holder[MatchingRoom.CC_MATCHING] = new RoomsHolder();
    }

    public static MatchingRoomManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    public void addToWaitingList(final Player player) {
        _players.add(player);
    }

    public void removeFromWaitingList(final Player player) {
        _players.remove(player);
    }

    public List<Player> getWaitingList(final int minLevel, final int maxLevel, final int[] classes) {
        final List<Player> res = new ArrayList<>();
        for (final Player $member : _players) {
            if ($member.getLevel() >= minLevel && $member.getLevel() <= maxLevel) {
                if (classes.length == 0 || ArrayUtils.contains(classes, $member.getPlayerClassComponent().getClassId().getId())) {
                    res.add($member);
                }
            }
        }

        return res;
    }

    public List<MatchingRoom> getMatchingRooms(final int type, final int region, final boolean allLevels, final Player activeChar) {
        final List<MatchingRoom> res = new ArrayList<>();
        for (final MatchingRoom room : _holder[type].rooms.values()) {
            if (region > 0 && room.getLocationId() != region) {
                continue;
            } else if (region == -2 && room.getLocationId() != MatchingRoomManager.getInstance().getLocation(activeChar)) {
                continue;
            }
            if (!allLevels && (room.getMinLevel() > activeChar.getLevel() || room.getMaxLevel() < activeChar.getLevel())) {
                continue;
            }
            res.add(room);
        }
        return res;
    }

    public int addMatchingRoom(final MatchingRoom r) {
        return _holder[r.getType()].addRoom(r);
    }

    public void removeMatchingRoom(final MatchingRoom r) {
        _holder[r.getType()].rooms.remove(r.getId());
    }

    public MatchingRoom getMatchingRoom(final int type, final int id) {
        return _holder[type].rooms.get(id);
    }

    public int getLocation(final Player player) {
        if (player == null) {
            return 0;
        }

        final RestartArea ra = MapRegionManager.getInstance().getRegionData(RestartArea.class, player);
        if (ra != null) {
            final RestartPoint rp = ra.getRestartPoint().get(player.getPlayerTemplateComponent().getPlayerRace());
            return rp.getBbs();
        }

        return 0;
    }

    private static class RoomsHolder {
        private final Map<Integer, MatchingRoom> rooms = new ConcurrentHashMap<>();
        private int _id = 1;

        public int addRoom(final MatchingRoom r) {
            final int val = _id++;
            rooms.put(val, r);
            return val;
        }
    }

    private static class LazyHolder {
        private static final MatchingRoomManager INSTANCE = new MatchingRoomManager();
    }
}
