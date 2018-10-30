package org.mmocore.gameserver.manager;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.object.Fence;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.idfactory.IdFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Laky
 * @author Java-man
 */
public class FenceBuilderManager {
    private final Map<Integer, Fence> fences = new HashMap<>();
    private final Cache<Integer, Fence> lastFences = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES).build();

    private FenceBuilderManager() {
    }

    public static FenceBuilderManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    public void fenceMenu(final Player pc) {
        pc.sendPacket(new HtmlMessage(5).setFile("admin/fence.htm"));
    }

    public void spawnFence(final Player pc, final int type, final int wid, final int hi, final int size) {
        for (int i = 0; i < size; i++) {
            final int id = IdFactory.getInstance().getNextId();
            final Fence inst = new Fence(id, pc.getLoc(), type, wid, hi);
            inst.setReflection(ReflectionManager.DEFAULT);
            inst.spawnMe(new Location(pc.getX(), pc.getY(), pc.getZ() + 32));
            inst.broadcastCharInfo();
            inst.setCollision(true);

            fences.put(id, inst);
            lastFences.put(id, inst);
        }

        fenceMenu(pc);
    }

    public void deleteLastFence(final Player pc) {
        if (lastFences.size() > 0) {
            for (final int key : lastFences.asMap().keySet()) {
                final Fence f = lastFences.getIfPresent(key);
                if (f != null) {
                    f.decayMe();
                    f.setCollision(false);

                    fences.remove(key);
                    lastFences.invalidate(key);
                }
            }
        }

        fenceMenu(pc);
    }

    public void deleteAllFences(final Player pc) {
        if (!fences.isEmpty()) {
            for (final int key : fences.keySet()) {
                final Fence f = fences.get(key);
                if (f != null) {
                    f.decayMe();
                    f.setCollision(false);
                }
            }

            fences.clear();
            lastFences.invalidateAll();
        }

        fenceMenu(pc);
    }

    public void changeFenceType(final Player pc, final int t) {
        if (lastFences.size() > 0) {
            for (final int key : lastFences.asMap().keySet()) {
                final Fence f = lastFences.getIfPresent(key);
                if (f != null) {
                    f.setType(t);
                    f.broadcastCharInfo();
                }
            }
        }

        fenceMenu(pc);
    }

    private static class LazyHolder {
        private static final FenceBuilderManager INSTANCE = new FenceBuilderManager();
    }
}
