package org.mmocore.gameserver.data.xml.holder;

import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.gameserver.model.dress.DressShieldData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hack
 * Date: 17.08.2016 18:49
 */
public final class DressShieldHolder extends AbstractHolder {
    private static final DressShieldHolder _instance = new DressShieldHolder();
    private final List<DressShieldData> _shield = new ArrayList<DressShieldData>();

    public static DressShieldHolder getInstance() {
        return _instance;
    }

    public void addShield(DressShieldData shield) {
        _shield.add(shield);
    }

    public List<DressShieldData> getAllShields() {
        return _shield;
    }

    public DressShieldData getShield(int id) {
        for (DressShieldData shield : _shield) {
            if (shield.getId() == id)
                return shield;
        }

        return null;
    }

    @Override
    public int size() {
        return _shield.size();
    }

    @Override
    public void clear() {
        _shield.clear();
    }
}
