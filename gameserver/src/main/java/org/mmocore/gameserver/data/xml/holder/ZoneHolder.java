package org.mmocore.gameserver.data.xml.holder;

import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.gameserver.templates.ZoneTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author G1ta0
 */
public class ZoneHolder extends AbstractHolder {
    private final Map<String, ZoneTemplate> zones = new HashMap<>();

    public static ZoneHolder getInstance() {
        return LazyHolder.INSTANCE;
    }

    public void addTemplate(final ZoneTemplate zone) {
        zones.put(zone.getName(), zone);
    }

    public ZoneTemplate getTemplate(final String name) {
        return zones.get(name);
    }

    public Map<String, ZoneTemplate> getZones() {
        return zones;
    }

    @Override
    public int size() {
        return zones.size();
    }

    @Override
    public void clear() {
        zones.clear();
    }

    private static class LazyHolder {
        private static final ZoneHolder INSTANCE = new ZoneHolder();
    }
}
