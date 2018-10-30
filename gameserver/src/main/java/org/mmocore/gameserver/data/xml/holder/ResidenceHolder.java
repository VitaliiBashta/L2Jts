package org.mmocore.gameserver.data.xml.holder;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.entity.residence.Residence;
import org.mmocore.gameserver.object.GameObject;

import java.util.*;

/**
 * @author VISTALL
 * @date 0:17/12.02.2011
 */
@SuppressWarnings("unchecked")
public final class ResidenceHolder extends AbstractHolder {
    private final TIntObjectMap<Residence> residences = new TIntObjectHashMap<>();

    private final Map<Class<? extends Residence>, List<Residence>> fastResidencesByType = new HashMap<>(4);

    private ResidenceHolder() {
    }

    public static ResidenceHolder getInstance() {
        return LazyHolder.INSTANCE;
    }

    public void addResidence(final Residence r) {
        residences.put(r.getId(), r);
    }

    public <R extends Residence> R getResidence(final int id) {
        return (R) residences.get(id);
    }

    public <R extends Residence> R getResidence(final Class<R> type, final int id) {
        final Residence r = getResidence(id);
        if (r == null || r.getClass() != type) {
            return null;
        }

        return (R) r;
    }

    public <R extends Residence> List<R> getResidenceList(final Class<R> t) {
        return (List<R>) fastResidencesByType.get(t);
    }

    public Collection<Residence> getResidences() {
        return residences.valueCollection();
    }

    public <R extends Residence> R getResidenceByObject(final Class<? extends Residence> type, final GameObject object) {
        return (R) getResidenceByCoord(type, object.getX(), object.getY(), object.getZ(), object.getReflection());
    }

    public <R extends Residence> R getResidenceByCoord(final Class<R> type, final int x, final int y, final int z, final Reflection ref) {
        final Collection<Residence> residences = type == null ? getResidences() : (Collection<Residence>) getResidenceList(type);
        for (final Residence residence : residences) {
            if (residence.checkIfInZone(x, y, z, ref)) {
                return (R) residence;
            }
        }
        return null;
    }

    public <R extends Residence> R findNearestResidence(final Class<R> clazz, final int x, final int y, final int z, final Reflection ref, final int offset) {
        Residence residence = getResidenceByCoord(clazz, x, y, z, ref);
        if (residence == null) {
            double closestDistance = offset;
            double distance;
            for (final Residence r : getResidenceList(clazz)) {
                distance = r.getZone().findDistanceToZone(x, y, z, false);
                if (closestDistance > distance) {
                    closestDistance = distance;
                    residence = r;
                }
            }
        }
        return (R) residence;
    }

    public void callInit() {
        getResidences().forEach(Residence::init);
    }

    private void buildFastLook() {
        for (final Residence residence : residences.valueCollection()) {
            List<Residence> list = fastResidencesByType.get(residence.getClass());
            if (list == null) {
                fastResidencesByType.put(residence.getClass(), (list = new ArrayList<>()));
            }
            list.add(residence);
        }
    }

    @Override
    public void log() {
        buildFastLook();
        info("total size: " + residences.size());
        for (final Map.Entry<Class<? extends Residence>, List<Residence>> entry : fastResidencesByType.entrySet()) {
            info(" - load " + entry.getValue().size() + ' ' + entry.getKey().getSimpleName().toLowerCase() + "(s).");
        }
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void clear() {
        residences.clear();
        fastResidencesByType.clear();
    }

    private static class LazyHolder {
        private static final ResidenceHolder INSTANCE = new ResidenceHolder();
    }
}
