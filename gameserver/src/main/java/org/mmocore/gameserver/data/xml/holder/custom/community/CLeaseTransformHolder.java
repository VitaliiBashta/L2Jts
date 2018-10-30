package org.mmocore.gameserver.data.xml.holder.custom.community;

import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.gameserver.templates.custom.community.leaseTransform.LeaseTransformTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Mangol
 * @since 03.02.2016
 */
public class CLeaseTransformHolder extends AbstractHolder {
    private static final CLeaseTransformHolder INSTANCE = new CLeaseTransformHolder();
    private static final Map<Integer, LeaseTransformTemplate> leaseMap = new HashMap<>();

    public static CLeaseTransformHolder getInstance() {
        return INSTANCE;
    }

    @Override
    public void clear() {
        leaseMap.clear();
    }

    @Override
    public int size() {
        return leaseMap.size();
    }

    public void addLeaseTransform(final LeaseTransformTemplate lease) {
        leaseMap.put(lease.getId(), lease);
    }

    public Optional<LeaseTransformTemplate> getLeaseTransform(final int id) {
        return Optional.ofNullable(leaseMap.get(id));
    }

    public Map<Integer, LeaseTransformTemplate> getLeaseTransforms() {
        return leaseMap;
    }
}
