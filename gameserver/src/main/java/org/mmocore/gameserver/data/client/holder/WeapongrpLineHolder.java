package org.mmocore.gameserver.data.client.holder;

import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.gameserver.templates.client.WeapongrpLine;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Create by Mangol on 20.12.2015.
 */
public class WeapongrpLineHolder extends AbstractHolder {
    private final static WeapongrpLineHolder INSTANCE = new WeapongrpLineHolder();
    private final Map<Integer, WeapongrpLine> weapongrp = new HashMap<>();

    public static WeapongrpLineHolder getInstance() {
        return INSTANCE;
    }

    public void addWeapongpr(final WeapongrpLine weapongrpLine) {
        weapongrp.put(weapongrpLine.getId(), weapongrpLine);
    }

    public Optional<WeapongrpLine> getWeapongrp(final int id) {
        return Optional.ofNullable(weapongrp.get(id));
    }

    @Override
    public int size() {
        return weapongrp.size();
    }

    @Override
    public void clear() {
        //
    }
}
