package org.mmocore.gameserver.data.xml.holder;

import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.gameserver.phantoms.template.PhantomSpawnTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hack
 * Date: 22.08.2016 4:37
 */
public class PhantomSpawnHolder extends AbstractHolder {
    private static final PhantomSpawnHolder instance = new PhantomSpawnHolder();
    private final List<PhantomSpawnTemplate> spawns = new ArrayList<>();

    public static PhantomSpawnHolder getInstance() {
        return instance;
    }

    public void addSpawn(PhantomSpawnTemplate template) {
        spawns.add(template);
    }

    public List<PhantomSpawnTemplate> getSpawns() {
        return spawns;
    }

    @Override
    public int size() {
        return spawns.size();
    }

    @Override
    public void clear() {
        spawns.clear();
    }
}
