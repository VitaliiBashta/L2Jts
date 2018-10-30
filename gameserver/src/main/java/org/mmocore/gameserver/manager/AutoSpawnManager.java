package org.mmocore.gameserver.manager;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.model.SimpleSpawner;
import org.mmocore.gameserver.model.Spawner;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.idfactory.IdFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Auto Spawn Handler
 * <p/>
 * Allows spawning of a NPC object based on a timer.
 * (From the official idea used for the Merchant and Blacksmith of Mammon)
 * <p/>
 * General Usage:
 * - Call registerSpawn() with the parameters listed below.
 * int npcId
 * int[][] spawnPoints or specify NULL to add points later.
 * int initialDelay (If < 0 = default value)
 * int respawnDelay (If < 0 = default value)
 * int despawnDelay (If < 0 = default value or if = 0, function disabled)
 * <p/>
 * spawnPoints is a standard two-dimensional int array containing X,Y and Z coordinates.
 * The default respawn/despawn delays are currently every hour (as for Mammon on official servers).
 * <p/>
 * - The resulting AutoSpawnInstance object represents the newly added spawn index.
 * - The interal methods of this object can be used to adjust random spawning, for instance a call to setRandomSpawn(1, true); would set the spawn at index 1
 * to be randomly rather than sequentially-based.
 * - Also they can be used to specify the number of NPC instances to spawn
 * using setSpawnCount(), and broadcast a message to all users using setBroadcast().
 * <p/>
 * Random Spawning = OFF by default
 * Broadcasting = OFF by default
 *
 * @author Tempy
 */
@Deprecated
public class AutoSpawnManager {
    private static final Logger _log = LoggerFactory.getLogger(AutoSpawnManager.class);

    private static final int DEFAULT_INITIAL_SPAWN = 30000; // 30 seconds after registration
    private static final int DEFAULT_RESPAWN = 3600000; //1 hour in millisecs
    private static final int DEFAULT_DESPAWN = 3600000; //1 hour in millisecs

    protected final Map<Integer, AutoSpawnInstance> _registeredSpawns;
    protected final Map<Integer, ScheduledFuture<?>> _runningSpawns;

    private AutoSpawnManager() {
        _registeredSpawns = new ConcurrentHashMap<>();
        _runningSpawns = new ConcurrentHashMap<>();

        load();

        _log.info("Loaded " + size() + " handlers in total.");
    }

    public static AutoSpawnManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    public final int size() {
        return _registeredSpawns.size();
    }

    private void load() {
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringComments(true);

            final File file = new File(ServerConfig.DATAPACK_ROOT, "data/xmlscript/static/random_spawn.xml");

            final Document document = factory.newDocumentBuilder().parse(file);
            for (Node firstNode = document.getFirstChild(); firstNode != null; firstNode = firstNode.getNextSibling()) {
                if ("list".equalsIgnoreCase(firstNode.getNodeName())) {
                    for (Node secondNode = firstNode.getFirstChild(); secondNode != null; secondNode = secondNode.getNextSibling()) {
                        if ("random_spawn".equalsIgnoreCase(secondNode.getNodeName())) {
                            final NamedNodeMap attrs = secondNode.getAttributes();
                            final int npcId = Integer.parseInt(attrs.getNamedItem("npcId").getNodeValue());
                            final int initialDelay = Integer.parseInt(attrs.getNamedItem("initialDelay").getNodeValue());
                            final int respawnDelay = Integer.parseInt(attrs.getNamedItem("respawnDelay").getNodeValue());
                            final int despawnDelay = Integer.parseInt(attrs.getNamedItem("despawnDelay").getNodeValue());

                            final AutoSpawnInstance spawnInst = registerSpawn(npcId, initialDelay, respawnDelay, despawnDelay);
                            spawnInst.setSpawnCount(Integer.parseInt(attrs.getNamedItem("count").getNodeValue()));
                            spawnInst.setBroadcast(Boolean.parseBoolean(attrs.getNamedItem("broadcastSpawn").getNodeValue()));
                            spawnInst.setRandomSpawn(Boolean.parseBoolean(attrs.getNamedItem("randomSpawn").getNodeValue()));

                            for (Node thirdNode = secondNode.getFirstChild(); thirdNode != null; thirdNode = thirdNode.getNextSibling()) {
                                if ("random_position".equalsIgnoreCase(thirdNode.getNodeName())) {
                                    final NamedNodeMap attrs1 = thirdNode.getAttributes();
                                    final int x = Integer.parseInt(attrs1.getNamedItem("x").getNodeValue());
                                    final int y = Integer.parseInt(attrs1.getNamedItem("y").getNodeValue());
                                    final int z = Integer.parseInt(attrs1.getNamedItem("z").getNodeValue());
                                    final int h = Integer.parseInt(attrs1.getNamedItem("h").getNodeValue());
                                    spawnInst.addSpawnLocation(x, y, z, h);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            _log.error("returned exception: " + e.getLocalizedMessage());
        }
    }

    /**
     * Registers a spawn with the given parameters with the spawner, and marks it as
     * active. Returns a AutoSpawnInstance containing info about the spawn.
     *
     * @param int     npcId
     * @param int[][] spawnPoints
     * @param int     initialDelay (If < 0 = default value)
     * @param int     respawnDelay (If < 0 = default value)
     * @param int     despawnDelay (If < 0 = default value or if = 0, function disabled)
     * @return AutoSpawnInstance spawnInst
     */
    public AutoSpawnInstance registerSpawn(final int npcId, final int[][] spawnPoints, int initialDelay, int respawnDelay, int despawnDelay) {
        if (initialDelay < 0) {
            initialDelay = DEFAULT_INITIAL_SPAWN;
        }

        if (respawnDelay < 0) {
            respawnDelay = DEFAULT_RESPAWN;
        }

        if (despawnDelay < 0) {
            despawnDelay = DEFAULT_DESPAWN;
        }

        final AutoSpawnInstance newSpawn = new AutoSpawnInstance(npcId, initialDelay, respawnDelay, despawnDelay);

        if (spawnPoints != null) {
            for (final int[] spawnPoint : spawnPoints) {
                newSpawn.addSpawnLocation(spawnPoint);
            }
        }

        final int newId = IdFactory.getInstance().getNextId();
        newSpawn._objectId = newId;
        _registeredSpawns.put(newId, newSpawn);

        setSpawnActive(newSpawn, true);

        return newSpawn;
    }

    /**
     * Registers a spawn with the given parameters with the spawner, and marks it as
     * active. Returns a AutoSpawnInstance containing info about the spawn.
     * <BR>
     * <B>Warning:</B> Spawn locations must be specified separately using addSpawnLocation().
     *
     * @param int npcId
     * @param int initialDelay (If < 0 = default value)
     * @param int respawnDelay (If < 0 = default value)
     * @param int despawnDelay (If < 0 = default value or if = 0, function disabled)
     * @return AutoSpawnInstance spawnInst
     */
    public AutoSpawnInstance registerSpawn(final int npcId, final int initialDelay, final int respawnDelay, final int despawnDelay) {
        return registerSpawn(npcId, null, initialDelay, respawnDelay, despawnDelay);
    }

    /**
     * Remove a registered spawn from the list, specified by the given spawn instance.
     *
     * @param AutoSpawnInstance spawnInst
     * @return boolean removedSuccessfully
     */
    public boolean removeSpawn(final AutoSpawnInstance spawnInst) {
        if (!isSpawnRegistered(spawnInst)) {
            return false;
        }

        try {
            // Try to remove from the list of registered spawns if it exists.
            _registeredSpawns.remove(spawnInst.getNpcId());

            // Cancel the currently associated running scheduled task.
            final ScheduledFuture<?> respawnTask = _runningSpawns.remove(spawnInst._objectId);
            respawnTask.cancel(false);
        } catch (Exception e) {
            _log.warn("Could not auto spawn for NPC ID " + spawnInst._npcId + " (Object ID = " + spawnInst._objectId + "): " + e);
            return false;
        }

        return true;
    }

    /**
     * Remove a registered spawn from the list, specified by the given spawn object ID.
     *
     * @param int objectId
     * @return boolean removedSuccessfully
     */
    public void removeSpawn(final int objectId) {
        removeSpawn(_registeredSpawns.get(objectId));
    }

    /**
     * Sets the active state of the specified spawn.
     *
     * @param AutoSpawnInstance spawnInst
     * @param boolean           isActive
     */
    public void setSpawnActive(final AutoSpawnInstance spawnInst, final boolean isActive) {
        final int objectId = spawnInst._objectId;

        if (isSpawnRegistered(objectId)) {
            ScheduledFuture<?> spawnTask = null;

            if (isActive) {
                final AutoSpawner rset = new AutoSpawner(objectId);
                if (spawnInst._desDelay > 0) {
                    spawnTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(rset, spawnInst._initDelay, spawnInst._resDelay);
                } else {
                    spawnTask = ThreadPoolManager.getInstance().schedule(rset, spawnInst._initDelay);
                }
                //spawnTask = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(rset, spawnInst._initDelay, spawnInst._resDelay);
                _runningSpawns.put(objectId, spawnTask);
            } else {
                spawnTask = _runningSpawns.remove(objectId);

                if (spawnTask != null) {
                    spawnTask.cancel(false);
                }
            }

            spawnInst.setSpawnActive(isActive);
        }
    }

    /**
     * Returns the number of milliseconds until the next occurrance of
     * the given spawn.
     *
     * @param AutoSpawnInstance spawnInst
     * @param long              milliRemaining
     */
    public final long getTimeToNextSpawn(final AutoSpawnInstance spawnInst) {
        final int objectId = spawnInst._objectId;

        if (!isSpawnRegistered(objectId)) {
            return -1;
        }

        return _runningSpawns.get(objectId).getDelay(TimeUnit.MILLISECONDS);
    }

    /**
     * Attempts to return the AutoSpawnInstance associated with the given NPC or Object ID type.
     * <BR>
     * Note: If isObjectId == false, returns first instance for the specified NPC ID.
     *
     * @param int     id
     * @param boolean isObjectId
     * @return AutoSpawnInstance spawnInst
     */
    public final AutoSpawnInstance getAutoSpawnInstance(final int id, final boolean isObjectId) {
        if (isObjectId) {
            if (isSpawnRegistered(id)) {
                return _registeredSpawns.get(id);
            }
        } else {
            for (final AutoSpawnInstance spawnInst : _registeredSpawns.values()) {
                if (spawnInst._npcId == id) {
                    return spawnInst;
                }
            }
        }

        return null;
    }

    public Map<Integer, AutoSpawnInstance> getAllAutoSpawnInstance(final int id) {
        final Map<Integer, AutoSpawnInstance> spawnInstList = new ConcurrentHashMap<>();

        for (final AutoSpawnInstance spawnInst : _registeredSpawns.values()) {
            if (spawnInst._npcId == id) {
                spawnInstList.put(spawnInst._objectId, spawnInst);
            }
        }

        return spawnInstList;
    }

    /**
     * Tests if the specified object ID is assigned to an auto spawn.
     *
     * @param int objectId
     * @return boolean isAssigned
     */
    public final boolean isSpawnRegistered(final int objectId) {
        return _registeredSpawns.containsKey(objectId);
    }

    /**
     * Tests if the specified spawn instance is assigned to an auto spawn.
     *
     * @param AutoSpawnInstance spawnInst
     * @return boolean isAssigned
     */
    public final boolean isSpawnRegistered(final AutoSpawnInstance spawnInst) {
        return _registeredSpawns.containsValue(spawnInst);
    }

    /**
     * AutoSpawnInstance Class
     * <BR><BR>
     * Stores information about a registered auto spawn.
     *
     * @author Tempy
     */
    public static class AutoSpawnInstance {
        protected final int _npcId;
        protected final int _initDelay;
        protected final int _resDelay;
        protected final int _desDelay;
        private final List<NpcInstance> _npcList = new ArrayList<>();
        private final List<Location> _locList = new ArrayList<>();
        protected int _objectId;
        protected int _spawnIndex;
        protected int _spawnCount = 1;
        protected int _lastLocIndex = -1;
        private boolean _spawnActive;
        private boolean _randomSpawn = false;
        private boolean _broadcastAnnouncement = false;

        protected AutoSpawnInstance(final int npcId, final int initDelay, final int respawnDelay, final int despawnDelay) {
            _npcId = npcId;
            _initDelay = initDelay;
            _resDelay = respawnDelay;
            _desDelay = despawnDelay;
        }

        boolean addAttackable(final NpcInstance npcInst) {
            return _npcList.add(npcInst);
        }

        boolean removeAttackable(final NpcInstance npcInst) {
            return _npcList.remove(npcInst);
        }

        public int getObjectId() {
            return _objectId;
        }

        public int getInitialDelay() {
            return _initDelay;
        }

        public int getRespawnDelay() {
            return _resDelay;
        }

        public int getDespawnDelay() {
            return _desDelay;
        }

        public int getNpcId() {
            return _npcId;
        }

        public int getSpawnCount() {
            return _spawnCount;
        }

        public void setSpawnCount(final int spawnCount) {
            _spawnCount = spawnCount;
        }

        public Location[] getLocationList() {
            return _locList.toArray(new Location[_locList.size()]);
        }

        public NpcInstance[] getAttackableList() {
            return _npcList.toArray(new NpcInstance[_npcList.size()]);
        }

        public Spawner[] getSpawns() {
            final List<Spawner> npcSpawns = new ArrayList<>();

            for (final NpcInstance npcInst : _npcList) {
                npcSpawns.add(npcInst.getSpawn());
            }

            return npcSpawns.toArray(new Spawner[npcSpawns.size()]);
        }

        public void setBroadcast(final boolean broadcastValue) {
            _broadcastAnnouncement = broadcastValue;
        }

        public boolean isSpawnActive() {
            return _spawnActive;
        }

        void setSpawnActive(final boolean activeValue) {
            _spawnActive = activeValue;
        }

        public boolean isRandomSpawn() {
            return _randomSpawn;
        }

        public void setRandomSpawn(final boolean randValue) {
            _randomSpawn = randValue;
        }

        public boolean isBroadcasting() {
            return _broadcastAnnouncement;
        }

        public boolean addSpawnLocation(final int x, final int y, final int z, final int heading) {
            return _locList.add(new Location(x, y, z, heading));
        }

        public boolean addSpawnLocation(final int[] spawnLoc) {
            if (spawnLoc.length != 3) {
                return false;
            }

            return addSpawnLocation(spawnLoc[0], spawnLoc[1], spawnLoc[2], -1);
        }

        public Location removeSpawnLocation(final int locIndex) {
            try {
                return _locList.remove(locIndex);
            } catch (IndexOutOfBoundsException e) {
                return null;
            }
        }
    }

    private static class LazyHolder {
        private static final AutoSpawnManager INSTANCE = new AutoSpawnManager();
    }

    /**
     * AutoSpawner Class
     * <BR><BR>
     * This handles the main spawn task for an auto spawn instance, and initializes
     * a despawner if required.
     *
     * @author Tempy
     */
    private class AutoSpawner extends RunnableImpl {
        private final int _objectId;

        AutoSpawner(final int objectId) {
            _objectId = objectId;
        }

        @Override
        public void runImpl() {
            try {
                // Retrieve the required spawn instance for this spawn task.
                final AutoSpawnInstance spawnInst = _registeredSpawns.get(_objectId);

                // If the spawn is not scheduled to be active, cancel the spawn task.
                if (!spawnInst.isSpawnActive() || ServerConfig.DONTLOADSPAWN) {
                    return;
                }

                final Location[] locationList = spawnInst.getLocationList();

                // If there are no set co-ordinates, cancel the spawn task.
                if (locationList.length == 0) {
                    LOGGER.info("AutoSpawnHandler: No location co-ords specified for spawn instance (Object ID = " + _objectId + ").");
                    return;
                }

                final int locationCount = locationList.length;
                int locationIndex = Rnd.get(locationCount);

                /*
                 * If random spawning is disabled, the spawn at the next set of
                 * co-ordinates after the last. If the index is greater than the number
                 * of possible spawns, reset the counter to zero.
                 */
                if (!spawnInst.isRandomSpawn()) {
                    locationIndex = spawnInst._lastLocIndex;
                    locationIndex++;

                    if (locationIndex == locationCount) {
                        locationIndex = 0;
                    }

                    spawnInst._lastLocIndex = locationIndex;
                }

                // Set the X, Y and Z co-ordinates, where this spawn will take place.
                final int x = locationList[locationIndex].x;
                final int y = locationList[locationIndex].y;
                final int z = locationList[locationIndex].z;
                final int heading = locationList[locationIndex].h;

                // Fetch the template for this NPC ID and create a new spawn.
                final NpcTemplate npcTemp = NpcHolder.getInstance().getTemplate(spawnInst.getNpcId());
                final SimpleSpawner newSpawn = new SimpleSpawner(npcTemp);

                newSpawn.setLocx(x);
                newSpawn.setLocy(y);
                newSpawn.setLocz(z);
                if (heading != -1) {
                    newSpawn.setHeading(heading);
                }
                newSpawn.setAmount(spawnInst.getSpawnCount());
                if (spawnInst._desDelay == 0) {
                    newSpawn.setRespawnDelay(spawnInst._resDelay);
                }

                // Add the new spawn information to the spawn table, but do not store it.
                NpcInstance npcInst = null;

                for (int i = 0; i < spawnInst._spawnCount; i++) {
                    npcInst = newSpawn.doSpawn(true);

                    // To prevent spawning of more than one NPC in the exact same spot,
                    // move it slightly by a small random offset.
                    npcInst.setXYZ(npcInst.getX() + Rnd.get(50), npcInst.getY() + Rnd.get(50), npcInst.getZ());

                    // Add the NPC instance to the list of managed instances.
                    spawnInst.addAttackable(npcInst);
                }

				/*DEPRECATED
				 String nearestTown = TownManager.getInstance().getClosestTownName(npcInst);

				// Announce to all players that the spawn has taken place, with the nearest town location.
				if(spawnInst.isBroadcasting() && npcInst != null)
					AnnouncementUtils.announceByCustomMessage("org.mmocore.gameserver.model.AutoSpawnHandler.spawnNPC", new String[] {
							npcInst.getName(),
							nearestTown });*/

                // If there is no despawn time, do not create a despawn task.
                if (spawnInst.getDespawnDelay() > 0) {
                    final AutoDespawner rd = new AutoDespawner(_objectId);
                    ThreadPoolManager.getInstance().schedule(rd, spawnInst.getDespawnDelay() - 1000);
                }
            } catch (Exception e) {
                LOGGER.warn("AutoSpawnHandler: An error occurred while initializing spawn instance (Object ID = " + _objectId + "): " + e);
                LOGGER.error("", e);
            }
        }
    }

    /**
     * AutoDespawner Class
     * <BR><BR>
     * Simply used as a secondary class for despawning an auto spawn instance.
     *
     * @author Tempy
     */
    private class AutoDespawner extends RunnableImpl {
        private final int _objectId;

        AutoDespawner(final int objectId) {
            _objectId = objectId;
        }

        @Override
        public void runImpl() {
            try {
                final AutoSpawnInstance spawnInst = _registeredSpawns.get(_objectId);

                for (final NpcInstance npcInst : spawnInst.getAttackableList()) {
                    npcInst.deleteMe();
                    spawnInst.removeAttackable(npcInst);
                }
            } catch (Exception e) {
                LOGGER.warn("AutoSpawnHandler: An error occurred while despawning spawn (Object ID = " + _objectId + "): " + e);
            }
        }
    }
}
