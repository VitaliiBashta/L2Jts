package org.mmocore.gameserver.utils.idfactory;

import org.mmocore.commons.math.PrimeFinder;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.BitSet;
import java.util.concurrent.atomic.AtomicInteger;

public class BitSetIdFactory extends IdFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(BitSetIdFactory.class);

    // TODO: поменять на менее жрущий память битмап
    private BitSet freeIds;
    private AtomicInteger freeIdCount;
    private AtomicInteger nextFreeId;

    public class BitSetCapacityCheck extends RunnableImpl {
        @Override
        public void runImpl() {
            if (reachingBitSetCapacity()) {
                increaseBitSetCapacity();
            }
        }
    }

    protected BitSetIdFactory() {
        initialize();

        ThreadPoolManager.getInstance().scheduleAtFixedRate(new BitSetCapacityCheck(), 30000, 30000);
    }

    private void initialize() {
        try {
            freeIds = new BitSet(PrimeFinder.nextPrime(100000));
            freeIds.clear();
            freeIdCount = new AtomicInteger(FREE_OBJECT_ID_SIZE);

            for (final int usedObjectId : extractUsedObjectIDTable()) {
                final int objectID = usedObjectId - FIRST_OID;
                if (objectID < 0) {
                    LOGGER.warn("Object ID {} in DB is less than minimum ID of " + FIRST_OID, usedObjectId);
                    continue;
                }
                freeIds.set(usedObjectId - FIRST_OID);
                freeIdCount.decrementAndGet();
            }

            nextFreeId = new AtomicInteger(freeIds.nextClearBit(0));
            initialized = true;

            LOGGER.info("IdFactory: {} id's available.", freeIds.size());
        } catch (Exception e) {
            initialized = false;
            LOGGER.error("BitSet ID Factory could not be initialized correctly!", e);
        }
    }

    @Override
    public synchronized void releaseId(final int objectID) {
        if (objectID - FIRST_OID > -1) {
            freeIds.clear(objectID - FIRST_OID);
            freeIdCount.incrementAndGet();
            super.releaseId(objectID);
        } else {
            LOGGER.warn("BitSet ID Factory: release objectID {} failed (< " + FIRST_OID + ')', objectID);
        }
    }

    @Override
    public synchronized int getNextId() {
        final int newID = nextFreeId.get();
        freeIds.set(newID);
        freeIdCount.decrementAndGet();

        int nextFree = freeIds.nextClearBit(newID);

        if (nextFree < 0) {
            nextFree = freeIds.nextClearBit(0);
        }
        if (nextFree < 0) {
            if (freeIds.size() < FREE_OBJECT_ID_SIZE) {
                increaseBitSetCapacity();
            } else {
                throw new NullPointerException("Ran out of valid Id's.");
            }
        }

        nextFreeId.set(nextFree);

        return newID + FIRST_OID;
    }

    @Override
    public synchronized int size() {
        return freeIdCount.get();
    }

    protected synchronized int usedIdCount() {
        return size() - FIRST_OID;
    }

    protected synchronized boolean reachingBitSetCapacity() {
        return PrimeFinder.nextPrime(usedIdCount() * 11 / 10) > freeIds.size();
    }

    protected synchronized void increaseBitSetCapacity() {
        final BitSet newBitSet = new BitSet(PrimeFinder.nextPrime(usedIdCount() * 11 / 10));
        newBitSet.or(freeIds);
        freeIds = newBitSet;
    }
}