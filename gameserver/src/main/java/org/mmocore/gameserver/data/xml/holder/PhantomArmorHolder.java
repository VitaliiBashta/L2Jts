package org.mmocore.gameserver.data.xml.holder;

import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.gameserver.phantoms.template.PhantomArmorTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hack
 * Date: 22.08.2016 23:37
 */
public class PhantomArmorHolder extends AbstractHolder {
    private static PhantomArmorHolder instance = new PhantomArmorHolder();
    private final Map<Integer, PhantomArmorTemplate> sets = new HashMap<>();

    public static PhantomArmorHolder getInstance() {
        return instance;
    }

    public void addSet(int id, PhantomArmorTemplate set) {
        sets.put(id, set);
    }

    public PhantomArmorTemplate getSet(int id) {
        return sets.get(id);
    }

    @Override
    public int size() {
        return sets.size();
    }

    @Override
    public void clear() {
        sets.clear();
    }
}
