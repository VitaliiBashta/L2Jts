package org.mmocore.gameserver.data.xml.holder;

import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.gameserver.model.dress.DressCloakData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hack
 * Date: 17.08.2016 18:49
 */
public final class DressCloakHolder extends AbstractHolder {
    private static final DressCloakHolder _instance = new DressCloakHolder();
    private List<DressCloakData> _cloak = new ArrayList<DressCloakData>();

    public static DressCloakHolder getInstance() {
        return _instance;
    }

    public void addCloak(DressCloakData cloak) {
        _cloak.add(cloak);
    }

    public List<DressCloakData> getAllCloaks() {
        return _cloak;
    }

    public DressCloakData getCloak(int id) {
        for (DressCloakData cloak : _cloak) {
            if (cloak.getId() == id)
                return cloak;
        }

        return null;
    }

    @Override
    public int size() {
        return _cloak.size();
    }

    @Override
    public void clear() {
        _cloak.clear();
    }
}
