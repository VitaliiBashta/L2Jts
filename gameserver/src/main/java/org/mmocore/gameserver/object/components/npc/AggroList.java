package org.mmocore.gameserver.object.components.npc;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.mmocore.commons.collections.LazyArrayList;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.world.World;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Аггролист NPC.
 *
 * @author G1ta0
 */
public class AggroList {
    private final NpcInstance npc;
    private final TIntObjectHashMap<AggroInfo> hateList = new TIntObjectHashMap<>();
    /**
     * Блокировка для чтения/записи объектов списка
     */
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();

    public AggroList(final NpcInstance npc) {
        this.npc = npc;
    }

    public void addDamageHate(final Creature attacker, int damage, final int aggro) {
        damage = Math.max(damage, 0);

        if (damage == 0 && aggro == 0) {
            return;
        }

        writeLock.lock();
        try {
            AggroInfo ai;

            if ((ai = hateList.get(attacker.getObjectId())) == null) {
                hateList.put(attacker.getObjectId(), ai = new AggroInfo(attacker));
            }

            ai.damage += damage;
            ai.hate += aggro;
            ai.damage = Math.max(ai.damage, 0);
            ai.hate = Math.max(ai.hate, 0);
        } finally {
            writeLock.unlock();
        }
    }

    public AggroInfo get(final Creature attacker) {
        readLock.lock();
        try {
            return hateList.get(attacker.getObjectId());
        } finally {
            readLock.unlock();
        }
    }

    public void remove(final Creature attacker, final boolean onlyHate) {
        writeLock.lock();
        try {
            if (!onlyHate) {
                hateList.remove(attacker.getObjectId());
                return;
            }

            final AggroInfo ai = hateList.get(attacker.getObjectId());
            if (ai != null) {
                ai.hate = 0;
            }
        } finally {
            writeLock.unlock();
        }
    }

    public void clear() {
        clear(false);
    }

    public void clear(final boolean onlyHate) {
        writeLock.lock();
        try {
            if (hateList.isEmpty()) {
                return;
            }

            if (!onlyHate) {
                hateList.clear();
                return;
            }

            AggroInfo ai;
            for (TIntObjectIterator<AggroInfo> itr = hateList.iterator(); itr.hasNext(); ) {
                itr.advance();
                ai = itr.value();
                ai.hate = 0;
                if (ai.damage == 0) {
                    itr.remove();
                }
            }
        } finally {
            writeLock.unlock();
        }
    }

    public boolean isEmpty() {
        readLock.lock();
        try {
            return hateList.isEmpty();
        } finally {
            readLock.unlock();
        }
    }

    public List<Creature> getHateList(final int radius) {
        AggroInfo[] hated;

        readLock.lock();
        try {

            if (hateList.isEmpty()) {
                return Collections.emptyList();
            }

            hated = hateList.values(new AggroInfo[hateList.size()]);
        } finally {
            readLock.unlock();
        }

        Arrays.sort(hated, HateComparator.getInstance().reversed());
        if (hated[0].hate == 0) {
            return Collections.emptyList();
        }

        final List<Creature> hateList = new ArrayList<>();
        final List<Creature> chars = World.getAroundCharacters(npc, radius, radius);
        AggroInfo ai;
        for (final AggroInfo aHated : hated) {
            ai = aHated;
            if (ai.hate == 0) {
                continue;
            }
            for (final Creature cha : chars) {
                if (cha.getObjectId() == ai.attackerId) {
                    hateList.add(cha);
                    break;
                }
            }
        }

        return hateList;
    }

    public Creature getMostHated() {
        AggroInfo[] hated;

        readLock.lock();
        try {

            if (hateList.isEmpty()) {
                return null;
            }

            hated = hateList.values(new AggroInfo[hateList.size()]);
        } finally {
            readLock.unlock();
        }

        Arrays.sort(hated, HateComparator.getInstance().reversed());
        if (hated[0].hate == 0) {
            return null;
        }

        final List<Creature> chars = World.getAroundCharacters(npc);

        AggroInfo ai;
        loop:
        for (final AggroInfo aHated : hated) {
            ai = aHated;
            if (ai.hate == 0) {
                continue;
            }
            for (final Creature cha : chars) {
                if (cha.getObjectId() == ai.attackerId) {
                    if (cha.isDead()) {
                        continue loop;
                    }
                    return cha;
                }
            }
        }

        return null;
    }

    public Creature getRandomHated() {
        AggroInfo[] hated;

        readLock.lock();
        try {
            if (hateList.isEmpty()) {
                return null;
            }

            hated = hateList.values(new AggroInfo[hateList.size()]);
        } finally {
            readLock.unlock();
        }

        Arrays.sort(hated, HateComparator.getInstance().reversed());
        if (hated[0].hate == 0) {
            return null;
        }

        final List<Creature> chars = World.getAroundCharacters(npc);

        final List<Creature> randomHated = new LazyArrayList<>();

        AggroInfo ai;
        final Creature mostHated;
        loop:
        for (final AggroInfo aHated : hated) {
            ai = aHated;
            if (ai.hate == 0) {
                continue;
            }
            for (final Creature cha : chars) {
                if (cha.getObjectId() == ai.attackerId) {
                    if (cha.isDead()) {
                        continue loop;
                    }
                    randomHated.add(cha);
                    break;
                }
            }
        }

        if (randomHated.isEmpty()) {
            mostHated = null;
        } else {
            mostHated = Rnd.get(randomHated);
        }

        randomHated.clear();

        return mostHated;
    }

    public Creature getTopDamager() {
        AggroInfo[] hated;

        readLock.lock();
        try {
            if (hateList.isEmpty()) {
                return null;
            }

            hated = hateList.values(new AggroInfo[hateList.size()]);
        } finally {
            readLock.unlock();
        }

        Creature topDamager = null;
        Arrays.sort(hated, DamageComparator.getInstance().reversed());
        if (hated[0].damage == 0) {
            return null;
        }

        final List<Creature> chars = World.getAroundCharacters(npc);
        AggroInfo ai;
        for (final AggroInfo aHated : hated) {
            ai = aHated;
            if (ai.damage == 0) {
                continue;
            }
            for (final Creature cha : chars) {
                if (cha.getObjectId() == ai.attackerId) {
                    topDamager = cha;
                    return topDamager;
                }
            }
        }
        return null;
    }

    public Map<Creature, HateInfo> getCharMap() {
        if (isEmpty()) {
            return Collections.emptyMap();
        }

        final Map<Creature, HateInfo> aggroMap = new HashMap<>();
        final List<Creature> chars = World.getAroundCharacters(npc);
        readLock.lock();
        try {
            AggroInfo ai;
            for (TIntObjectIterator<AggroInfo> itr = hateList.iterator(); itr.hasNext(); ) {
                itr.advance();
                ai = itr.value();
                if (ai.damage == 0 && ai.hate == 0) {
                    continue;
                }
                for (final Creature attacker : chars) {
                    if (attacker.getObjectId() == ai.attackerId) {
                        aggroMap.put(attacker, new HateInfo(attacker, ai));
                        break;
                    }
                }
            }
        } finally {
            readLock.unlock();
        }

        return aggroMap;
    }

    public Map<Playable, HateInfo> getPlayableMap() {
        if (isEmpty()) {
            return Collections.emptyMap();
        }

        final Map<Playable, HateInfo> aggroMap = new HashMap<>();
        final List<Playable> chars = World.getAroundPlayables(npc);
        readLock.lock();
        try {
            AggroInfo ai;
            for (TIntObjectIterator<AggroInfo> itr = hateList.iterator(); itr.hasNext(); ) {
                itr.advance();
                ai = itr.value();
                if (ai.damage == 0 && ai.hate == 0) {
                    continue;
                }
                for (final Playable attacker : chars) {
                    if (attacker.getObjectId() == ai.attackerId) {
                        aggroMap.put(attacker, new HateInfo(attacker, ai));
                        break;
                    }
                }
            }
        } finally {
            readLock.unlock();
        }

        return aggroMap;
    }

    private abstract static class DamageHate {
        public int hate;
        public int damage;
    }

    public static class HateInfo extends DamageHate {
        public final Creature attacker;

        HateInfo(final Creature attacker, final AggroInfo ai) {
            this.attacker = attacker;
            this.hate = ai.hate;
            this.damage = ai.damage;
        }
    }

    public static class AggroInfo extends DamageHate {
        public final int attackerId;

        AggroInfo(final Creature attacker) {
            this.attackerId = attacker.getObjectId();
        }
    }

    public static class DamageComparator implements Comparator<DamageHate> {
        private static final Comparator<DamageHate> instance = new DamageComparator();

        DamageComparator() {
        }

        public static Comparator<DamageHate> getInstance() {
            return instance;
        }

        @Override
        public int compare(final DamageHate o1, final DamageHate o2) {
            return Integer.compare(o1.damage, o2.damage);
        }
    }

    public static class HateComparator implements Comparator<DamageHate> {
        private static final Comparator<DamageHate> instance = new HateComparator();

        HateComparator() {
        }

        public static Comparator<DamageHate> getInstance() {
            return instance;
        }

        @Override
        public int compare(final DamageHate o1, final DamageHate o2) {
            final int diffHate = Integer.compare(o1.hate, o2.hate);
            return diffHate == 0 ? Integer.compare(o1.damage, o2.damage) : diffHate;
        }
    }
}