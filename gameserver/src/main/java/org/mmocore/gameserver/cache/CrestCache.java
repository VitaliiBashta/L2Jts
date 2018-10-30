package org.mmocore.gameserver.cache;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.mmocore.gameserver.database.dao.impl.CrestCacheDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Импровизированный "кэш" для значков кланов и альянсов.
 *
 * @author G1ta0
 * @author Java-man
 */
public final class CrestCache {
    public static final int ALLY_CREST_SIZE = 192;
    public static final int CREST_SIZE = 256;
    public static final int LARGE_CREST_SIZE = 2176;
    private static final Logger LOGGER = LoggerFactory.getLogger(CrestCache.class);
    private static final CrestCacheDAO dao = CrestCacheDAO.getInstance();
    /**
     * Блокировка для чтения/записи объектов из "кэша"
     */
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();
    private Table<CrestType, Integer, Crest> crestCache; // crestType, pledgeId, crest
    private CrestCache() {
        load();
    }

    public static CrestCache getInstance() {
        return LazyHolder.INSTANCE;
    }

    public void load() {
        final Table<CrestType, Integer, Crest> crestCacheTemp = HashBasedTable.create();

        final ImmutableTable<CrestType, Integer, byte[]> result = dao.selectAllCrests();
        for (final Table.Cell<CrestType, Integer, byte[]> cell : result.cellSet()) {
            final CrestType crestType = cell.getRowKey();
            final int pledgeId = cell.getColumnKey();
            final byte[] crestHash = cell.getValue();
            final int crestId = generateCrestId(pledgeId, crestHash);

            crestCacheTemp.put(crestType, pledgeId, new Crest(crestId, crestHash));
        }

        crestCache = HashBasedTable.create(crestCacheTemp);

        crestCacheTemp.clear();

        LOGGER.info("CrestCache: Loaded {} crests", crestCache.columnKeySet().size());
    }

    public byte[] getCrestData(final CrestType crestType, final int crestId) {
        byte[] crestHash = null;

        readLock.lock();
        try {
            final Optional<Crest> optionalCrest = crestCache.row(crestType).values().stream().filter(crest -> crest.id == crestId).findFirst();
            if (optionalCrest.isPresent()) {
                crestHash = optionalCrest.get().hash;
            }
        } finally {
            readLock.unlock();
        }

        return crestHash;
    }

    public int getCrestId(final CrestType crestType, final int pledgeId) {
        if (!crestCache.contains(crestType, pledgeId)) {
            return 0;
        }

        int crestId = 0;

        readLock.lock();
        try {
            crestId = crestCache.get(crestType, pledgeId).id;
        } finally {
            readLock.unlock();
        }

        return crestId;
    }

    public int saveCrest(final CrestType crestType, final int pledgeId, final byte[] crestHash) {
        final int crestId = generateCrestId(pledgeId, crestHash);

        writeLock.lock();
        try {
            crestCache.put(crestType, pledgeId, new Crest(crestId, crestHash));
        } finally {
            writeLock.unlock();
        }

        dao.save(crestType, pledgeId, crestHash);

        return crestId;
    }

    public void removeCrest(final CrestType crestType, final int pledgeId) {
        writeLock.lock();
        try {
            crestCache.remove(crestType, pledgeId);
        } finally {
            writeLock.unlock();
        }

        dao.remove(crestType, pledgeId);
    }

    /**
     * Генерирует уникальный положительный ID на основе данных: ID клана/альянса и значка
     *
     * @param pledgeId  ID клана или альянса
     * @param crestHash данные значка
     * @return ID значка в "кэше"
     */
    private int generateCrestId(final int pledgeId, final byte[] crestHash) {
        return Math.abs(new HashCodeBuilder(15, 87).append(pledgeId).append(crestHash).toHashCode());
    }

    public enum CrestType {
        PLEDGE, PLEDGE_LARGE, ALLY
    }

    static final class Crest {
        final int id;
        final byte[] hash;

        private Crest(final int id, final byte[] hash) {
            this.id = id;
            this.hash = hash;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            final Crest crest = (Crest) o;
            return Objects.equals(id, crest.id) &&
                    Objects.equals(hash, crest.hash);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, hash);
        }
    }

    private static class LazyHolder {
        private static final CrestCache INSTANCE = new CrestCache();
    }
}