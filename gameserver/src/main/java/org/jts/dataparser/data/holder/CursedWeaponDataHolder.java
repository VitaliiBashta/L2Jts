package org.jts.dataparser.data.holder;

import org.jts.dataparser.data.annotations.Element;
import org.jts.dataparser.data.holder.cursedweapondata.CursedWeapon;
import org.mmocore.commons.data.AbstractHolder;

import java.util.List;

/**
 * @author : Camelion
 * @date : 26.08.12 21:35
 */
public class CursedWeaponDataHolder extends AbstractHolder {
    private static final CursedWeaponDataHolder ourInstance = new CursedWeaponDataHolder();
    @Element(start = "cursed_weapon_begin", end = "cursed_weapon_end")
    private List<CursedWeapon> cursedWeapons;

    private CursedWeaponDataHolder() {
    }

    public static CursedWeaponDataHolder getInstance() {
        return ourInstance;
    }

    @Override
    public int size() {
        return cursedWeapons.size();
    }

    public List<CursedWeapon> getCursedWeapons() {
        return cursedWeapons;
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
    }
}