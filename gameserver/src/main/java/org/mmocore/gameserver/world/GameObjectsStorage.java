package org.mmocore.gameserver.world;

import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class GameObjectsStorage {
    private static final Map<Integer, GameObject> OBJECTS = new ConcurrentHashMap<>(60000 * ServerConfig.RATE_MOB_SPAWN + ServerConfig.MAXIMUM_ONLINE_USERS);
    private static final Map<Integer, NpcInstance> NPCS = new ConcurrentHashMap<>(60000 * ServerConfig.RATE_MOB_SPAWN);
    private static final Map<Integer, Player> PLAYERS = new ConcurrentHashMap<>(ServerConfig.MAXIMUM_ONLINE_USERS);

    private GameObjectsStorage() {
    }

    public static Collection<Player> getPlayers() {
        return PLAYERS.values();
    }

    public static Iterable<NpcInstance> getNpcs() {
        return NPCS.values();
    }

    public static Stream<Player> getPlayerStream() {
        return PLAYERS.values().stream();
    }

    private static Stream<NpcInstance> getNpcStream() {
        return NPCS.values().stream();
    }

    public static Player getPlayer(final String name) {
        final Optional<Player> player = getPlayerStream().filter(p -> p.getName().equalsIgnoreCase(name)).findAny();
        return player.orElse(null);

    }

    public static Player getPlayer(final int objId) {
        return PLAYERS.get(objId);
    }

    public static GameObject findObject(final int objId) {
        return OBJECTS.get(objId);
    }

    public static NpcInstance getByNpcId(final int npcId) {
        NpcInstance result = null;
        for (final NpcInstance temp : getNpcs()) {
            if (npcId == temp.getNpcId()) {
                if (!temp.isDead()) {
                    return temp;
                }
                result = temp;
            }
        }
        return result;
    }

    public static List<NpcInstance> getAllByNpcId(final int npcId, final boolean justAlive) {

        return getNpcStream()
                .filter(npc -> npcId == npc.getNpcId() && (!justAlive || !npc.isDead()))
                .collect(Collectors.toList());
    }

    public static List<NpcInstance> getAllByNpcId(final List<Integer> npcIds, final boolean justAlive) {

        return getNpcStream()
                .filter(npc -> !justAlive || !npc.isDead())
                .filter(npc -> npcIds.contains(npc.getNpcId()))
                .collect(Collectors.toList());
    }

    public static NpcInstance getNpc(final int objId) {
        return NPCS.get(objId);
    }

    public static <T extends GameObject> void put(final T o) {
        final Map<Integer, T> map = getMapForObject(o);
        if (map != null) {
            map.put(o.getObjectId(), o);
        }

        OBJECTS.put(o.getObjectId(), o);
    }

    public static <T extends GameObject> void remove(final GameObject o) {
        final Map<Integer, T> map = getMapForObject(o);
        if (map != null) {
            map.remove(o.getObjectId());
        }

        OBJECTS.remove(o.getObjectId());
    }

    @SuppressWarnings("unchecked")
    private static <T extends GameObject> Map<Integer, T> getMapForObject(final GameObject o) {
        if (o.isNpc()) {
            return (Map<Integer, T>) NPCS;
        }

        if (o.isPlayer()) {
            return (Map<Integer, T>) PLAYERS;
        }

        return null;
    }

    public static int getAllPlayersSize() {
        return PLAYERS.size();
    }

    public static int getAllNpcsSize() {
        return NPCS.size();
    }
}