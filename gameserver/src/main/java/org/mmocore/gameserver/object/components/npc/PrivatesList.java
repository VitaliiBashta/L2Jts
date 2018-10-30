package org.mmocore.gameserver.object.components.npc;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Список минионов моба.
 *
 * @author G1ta0, Mangol
 */
public class PrivatesList {
    protected static final Logger _log = LoggerFactory.getLogger(PrivatesList.class);
    private final Multiset<NpcInstance> _privates;
    private final Multiset<NpcInstance> _privates_ex;
    private final Lock lock;
    private final NpcInstance _master;

    public PrivatesList(final NpcInstance master) {
        _master = master;
        _privates = HashMultiset.create();
        _privates_ex = HashMultiset.create();
        lock = new ReentrantLock();
    }

    /**
     * @return имеются ли живые минионы
     */
    public boolean hasAlivePrivates() {
        lock.lock();
        try {
            for (final NpcInstance m : _privates) {
                if (m.isVisible() && !m.isDead()) {
                    return true;
                }
            }
        } finally {
            lock.unlock();
        }
        return false;
    }

    public boolean hasMinions() {
        return _privates != null && _privates.size() > 0;
    }

    /**
     * Возвращает список живых минионов
     *
     * @return список живых минионов
     */
    public List<NpcInstance> getAlivePrivates() {
        final List<NpcInstance> result = new ArrayList<>(_privates.size());
        lock.lock();
        try {
            result.addAll(_privates.stream().filter(m -> m.isVisible() && !m.isDead()).collect(Collectors.toList()));
        } finally {
            lock.unlock();
        }
        return result;
    }

    /**
     * Спавнит миниона
     *
     * @param privates - список минионов "20001:5:0;20001:1:20" id:count:respawn(sec);
     * @param addMap   - true добавить в мап для дальнейшего использования;
     *                 - false не будет добавлять в мап более гибкая система если вдруг нужно описать миниона;
     */

    public void createPrivates(final String privates, final boolean addMap) {
        lock.lock();
        try {
            if (privates != null && privates.length() > 0) {
                if (_master.isVisible() && !_master.isAlikeDead() && !_master.isMinion()) {
                    final String[] privat = privates.split(";");
                    for (final String Privates_group : privat) {
                        final String[] minion = Privates_group.split(":");
                        final int minion_id = Integer.parseInt(minion[0]);
                        final int minion_count = Integer.parseInt(minion[1]);
                        final int minion_respawn = Integer.parseInt(minion[2]);
                        final NpcTemplate template = NpcHolder.getInstance().getTemplate(minion_id);
                        if (minion_count > 0) {
                            for (int i = 0; i < minion_count; i++) {
                                final NpcInstance m = template.getNewInstance();
                                if (m == null) {
                                    continue;
                                }
                                m.setLeader(_master);
                                m.setParameter("respawn_minion", minion_respawn * 1000);
                                _master.spawnMinion(m);
                                if (addMap) {
                                    _privates.add(m);
                                }
                            }
                        }
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Спавнит миниона
     *
     * @param minion_id      - ид миниона
     * @param minion_count   - количество минионов
     * @param minion_respawn - респавн миниона;
     * @param addMap         - true добавить в мап для дальнейшего использования - при респавне главари или смерти обязательно зачистить мап;
     *                       - false не будет добавлять в мап более гибкая система если вдруг нужно описать миниона;
     */
    public void createMinion(final int minion_id, final int minion_count, final int minion_respawn, final boolean addMap) {
        lock.lock();
        try {
            if (_master.isVisible() && !_master.isAlikeDead() && !_master.isMinion()) {
                if (minion_count > 0) {
                    final NpcTemplate template = NpcHolder.getInstance().getTemplate(minion_id);
                    for (int i = 0; i < minion_count; i++) {
                        final NpcInstance m = template.getNewInstance();
                        if (m == null) {
                            continue;
                        }
                        m.setLeader(_master);
                        m.setParameter("respawn_minion", minion_respawn * 1000);
                        _master.spawnMinion(m);
                        if (addMap) {
                            _privates.add(m);
                        }
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Выводит из инвизибла тех минионов которые уже были заспавнены.
     */
    public void useSpawnPrivates() {
        lock.lock();
        try {
            if (_master.isVisible() && !_master.isAlikeDead() && !_master.isMinion()) {
                for (final NpcInstance m : _privates) {
                    m.stopDecay();
                    m.decayMe(); // doDecay() не выполнится !
                    m.refreshID();
                    _master.spawnMinion(m);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Деспавнит всех минионов
     */
    public void unspawnPrivates() {
        lock.lock();
        try {
            _privates.stream().forEach(m -> m.decayMe());
        } finally {
            lock.unlock();
        }
    }

    /**
     * Удаляет минионов и чистит список
     */
    public void deletePrivates() {
        lock.lock();
        try {
            _privates.stream().forEach(m -> m.deleteMe());
            _privates.clear();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Удаляет минионов и чистит список
     * Работает только с createOnePrivateEx
     */
    public void deleteOnePrivateEx() {
        lock.lock();
        try {
            _privates_ex.stream().forEach(m -> m.deleteMe());
            _privates_ex.clear();
        } finally {
            lock.unlock();
        }
    }

    /**
     * @return имеются ли живые минионы
     * Работает только с createOnePrivateEx
     */
    public boolean hasAliveOnePrivateEx() {
        lock.lock();
        try {
            for (final NpcInstance m : _privates_ex) {
                if (m.isVisible() && !m.isDead()) {
                    return true;
                }
            }
        } finally {
            lock.unlock();
        }
        return false;
    }

    public boolean hasOnePrivateEx() {
        return _privates_ex != null && _privates_ex.size() > 0;
    }

    /**
     * Возвращает список живых минионов
     *
     * @return список живых минионов
     * Работает только с createOnePrivateEx
     */
    public List<NpcInstance> getAliveOnePrivateEx() {
        final List<NpcInstance> result = new ArrayList<>(_privates_ex.size());
        lock.lock();
        try {
            result.addAll(_privates_ex.stream().filter(m -> m.isVisible() && !m.isDead()).collect(Collectors.toList()));
        } finally {
            lock.unlock();
        }
        return result;
    }

    /**
     * Спавнит 1 миниона подобие класса как в птс
     * P.S - еще сыроват развить...
     *
     * @param minion_id - ид миниона
     * @param x         - координата по x;
     * @param y         - координата по y;
     * @param z         - координата по z;
     * @param h         - координата по h;
     * @param arg
     * @param i0
     * @param i1
     * @param addMap    - true добавить в мап для дальнейшего использования - при респавне главаря или смерти обязательно зачистить мап;
     *                  - false не будет добавлять в мап более гибкая система если вдруг нужно описать миниона в аи;
     */
    public void createOnePrivateEx(final int minion_id, final int x, final int y, final int z, final int h, final Creature arg, final int i0, final int i1, final boolean addMap) {
        lock.lock();
        try {
            final NpcTemplate template = NpcHolder.getInstance().getTemplate(minion_id);
            if (template == null) {
                throw new NullPointerException("Npc template id : " + minion_id + " not found!");
            }
            final NpcInstance m = template.getNewInstance();
            m.setLeader(_master);
            m.setSpawnedLoc(new Location(x, y, z, h));
            m.setReflection(_master.getReflection());
            m.setHeading(_master.getHeading());
            m.setCurrentHpMp(m.getMaxHp(), m.getMaxMp(), true);
            m.spawnMe(m.getSpawnedLoc());
            m.setParam2(i0);
            m.setParam3(i1);
            if (arg != null) {
                m.setParam4(arg);
            }
            if (_master.isRunning()) {
                m.setRunning();
            }
            if (addMap) {
                _privates_ex.add(m);
            }
        } finally {
            lock.unlock();
        }
    }
}