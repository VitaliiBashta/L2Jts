package org.mmocore.gameserver.taskmanager;

import org.mmocore.commons.lang.reference.HardReference;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.model.Spawner;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.utils.Util;

import java.util.ArrayList;
import java.util.List;

//TODO [G1ta0] переписать
public class SpawnTaskManager {
    private final Object spawnTasks_lock = new Object();
    private SpawnTask[] _spawnTasks = new SpawnTask[500];
    private int _spawnTasksSize = 0;

    public SpawnTaskManager() {
        ThreadPoolManager.getInstance().scheduleAtFixedRate(new SpawnScheduler(), 2000, 2000);
    }

    public static SpawnTaskManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    public void addSpawnTask(final NpcInstance actor, final long interval) {
        removeObject(actor);
        addObject(new SpawnTask(actor, System.currentTimeMillis() + interval));
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("============= SpawnTask Manager Report ============\n\r");
        sb.append("Tasks count: ").append(_spawnTasksSize).append("\n\r");
        sb.append("Tasks dump:\n\r");

        final long current = System.currentTimeMillis();
        for (final SpawnTask container : _spawnTasks) {
            sb.append("Class/Name: ").append(container.getClass().getSimpleName()).append('/').append(container.getActor());
            sb.append(" spawn timer: ").append(Util.formatTime((int) (container.endtime - current))).append("\n\r");
        }

        return sb.toString();
    }

    private void addObject(final SpawnTask decay) {
        synchronized (spawnTasks_lock) {
            if (_spawnTasksSize >= _spawnTasks.length) {
                final SpawnTask[] temp = new SpawnTask[_spawnTasks.length * 2];
                System.arraycopy(_spawnTasks, 0, temp, 0, _spawnTasksSize);
                _spawnTasks = temp;
            }

            _spawnTasks[_spawnTasksSize] = decay;
            _spawnTasksSize++;
        }
    }

    public void removeObject(final NpcInstance actor) {
        synchronized (spawnTasks_lock) {
            if (_spawnTasksSize > 1) {
                int k = -1;
                for (int i = 0; i < _spawnTasksSize; i++) {
                    if (_spawnTasks[i].getActor() == actor) {
                        k = i;
                    }
                }
                if (k > -1) {
                    _spawnTasks[k] = _spawnTasks[_spawnTasksSize - 1];
                    _spawnTasks[_spawnTasksSize - 1] = null;
                    _spawnTasksSize--;
                }
            } else if (_spawnTasksSize == 1 && _spawnTasks[0].getActor() == actor) {
                _spawnTasks[0] = null;
                _spawnTasksSize = 0;
            }
        }
    }

    private static class SpawnTask {
        private final HardReference<NpcInstance> _npcRef;
        public long endtime;

        SpawnTask(final NpcInstance cha, final long delay) {
            _npcRef = cha.getRef();
            endtime = delay;
        }

        public NpcInstance getActor() {
            return _npcRef.get();
        }
    }

    private static class LazyHolder {
        private static final SpawnTaskManager INSTANCE = new SpawnTaskManager();
    }

    public class SpawnScheduler extends RunnableImpl {
        @Override
        public void runImpl() {
            if (_spawnTasksSize > 0) {
                try {
                    final List<NpcInstance> works = new ArrayList<>();

                    synchronized (spawnTasks_lock) {
                        final long current = System.currentTimeMillis();
                        final int size = _spawnTasksSize;

                        for (int i = size - 1; i >= 0; i--) {
                            try {
                                final SpawnTask container = _spawnTasks[i];

                                if (container != null && container.endtime > 0 && current > container.endtime) {
                                    final NpcInstance actor = container.getActor();
                                    if (actor != null && actor.getSpawn() != null) {
                                        works.add(actor);
                                    }

                                    container.endtime = -1;
                                }

                                if (container == null || container.getActor() == null || container.endtime < 0) {
                                    if (i == _spawnTasksSize - 1) {
                                        _spawnTasks[i] = null;
                                    } else {
                                        _spawnTasks[i] = _spawnTasks[_spawnTasksSize - 1];
                                        _spawnTasks[_spawnTasksSize - 1] = null;
                                    }

                                    if (_spawnTasksSize > 0) {
                                        _spawnTasksSize--;
                                    }
                                }
                            } catch (Exception e) {
                                LOGGER.error("", e);
                            }
                        }
                    }

                    for (final NpcInstance work : works) {
                        final Spawner spawn = work.getSpawn();
                        if (spawn == null) {
                            continue;
                        }
                        spawn.decreaseScheduledCount();
                        if (spawn.isDoRespawn()) {
                            spawn.respawnNpc(work);
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("", e);
                }
            }
        }
    }
}