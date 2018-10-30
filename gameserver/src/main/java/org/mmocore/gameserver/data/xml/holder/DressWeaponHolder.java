package org.mmocore.gameserver.data.xml.holder;

import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.gameserver.model.dress.DressWeaponData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hack
 * Date: 17.08.2016 18:50
 */
public final class DressWeaponHolder extends AbstractHolder {
    private static final DressWeaponHolder _instance = new DressWeaponHolder();
    private final List<DressWeaponData> _weapons = new ArrayList<DressWeaponData>();

    public static DressWeaponHolder getInstance() {
        return _instance;
    }

    public void addWeapon(DressWeaponData weapon) {
        _weapons.add(weapon);
    }

    public List<DressWeaponData> getAllWeapons() {
        return _weapons;
    }

    public DressWeaponData getWeapon(int id) {
        for (DressWeaponData weapon : _weapons) {
            if (weapon.getId() == id)
                return weapon;
        }

        return null;
    }

    @Override
    public int size() {
        return _weapons.size();
    }

    @Override
    public void clear() {
        _weapons.clear();
    }
}
