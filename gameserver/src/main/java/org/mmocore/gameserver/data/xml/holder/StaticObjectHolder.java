package org.mmocore.gameserver.data.xml.holder;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.gameserver.model.instances.StaticObjectInstance;
import org.mmocore.gameserver.templates.StaticObjectTemplate;

/**
 * @author VISTALL
 * @date 22:21/09.03.2011
 */
public final class StaticObjectHolder extends AbstractHolder {
    private static final StaticObjectHolder INSTANCE = new StaticObjectHolder();

    private final TIntObjectMap<StaticObjectTemplate> templates = new TIntObjectHashMap<>();
    private final TIntObjectMap<StaticObjectInstance> spawned = new TIntObjectHashMap<>();

    public static StaticObjectHolder getInstance() {
        return INSTANCE;
    }

    public void addTemplate(final StaticObjectTemplate template) {
        templates.put(template.getUId(), template);
    }

    public StaticObjectTemplate getTemplate(final int id) {
        return templates.get(id);
    }

    public void spawnAll() {
        for (final StaticObjectTemplate template : templates.valueCollection()) {
            if (template.isSpawn()) {
                final StaticObjectInstance obj = template.newInstance();

                spawned.put(template.getUId(), obj);
            }
        }
        info("spawned: " + spawned.size() + " static object(s).");
    }

    public StaticObjectInstance getObject(final int id) {
        return spawned.get(id);
    }

    @Override
    public int size() {
        return templates.size();
    }

    @Override
    public void clear() {
        templates.clear();
    }
}
