package org.mmocore.gameserver.data.xml.holder;

import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.gameserver.object.components.npc.superPoint.SuperPoint;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: Felixx Company: J Develop Station
 */
public final class SuperPointHolder extends AbstractHolder {
    private static final SuperPointHolder INSTANCE = new SuperPointHolder();

    private final Map<String, SuperPoint> superPointMap = new HashMap<>();

    private SuperPointHolder() {
    }

    public static SuperPointHolder getInstance() {
        return INSTANCE;
    }

    public void addSuperPoints(final String pointName, final SuperPoint superPoints) {
        superPointMap.put(pointName, superPoints);
    }

    public SuperPoint getSuperPointsByName(final String pointName) {
        return superPointMap.get(pointName);
    }

    @Override
    public int size() {
        return superPointMap.size();
    }

    @Override
    public void clear() {
        superPointMap.clear();
    }
}