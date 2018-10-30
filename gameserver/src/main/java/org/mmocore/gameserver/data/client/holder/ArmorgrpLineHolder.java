package org.mmocore.gameserver.data.client.holder;

import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.gameserver.templates.client.ArmorgrpLine;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Create by Mangol on 20.12.2015.
 */
public class ArmorgrpLineHolder extends AbstractHolder {
    private final static ArmorgrpLineHolder INSTANCE = new ArmorgrpLineHolder();
    private final Map<Integer, ArmorgrpLine> armorgrp = new HashMap<>();

    public static ArmorgrpLineHolder getInstance() {
        return INSTANCE;
    }

    public void addArmorgpr(final ArmorgrpLine armorgrpLine) {
        armorgrp.put(armorgrpLine.getId(), armorgrpLine);
    }

    public Optional<ArmorgrpLine> getArmorgrp(final int id) {
        return Optional.ofNullable(armorgrp.get(id));
    }

    @Override
    public int size() {
        return armorgrp.size();
    }

    @Override
    public void clear() {
        //
    }
}
