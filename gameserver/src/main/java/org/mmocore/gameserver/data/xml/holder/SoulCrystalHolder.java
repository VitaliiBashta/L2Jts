package org.mmocore.gameserver.data.xml.holder;

import gnu.trove.map.hash.TIntObjectHashMap;
import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.gameserver.templates.SoulCrystal;

/**
 * @author: VISTALL
 * @date: 10:55/08.12.2010
 */
public final class SoulCrystalHolder extends AbstractHolder {
    private static final SoulCrystalHolder INSTANCE = new SoulCrystalHolder();

    private final TIntObjectHashMap<SoulCrystal> crystals = new TIntObjectHashMap<>();

    public static SoulCrystalHolder getInstance() {
        return INSTANCE;
    }

    public void addCrystal(final SoulCrystal crystal) {
        crystals.put(crystal.getItemId(), crystal);
    }

    public SoulCrystal getCrystal(final int item) {
        return crystals.get(item);
    }

    public SoulCrystal[] getCrystals() {
        return crystals.values(new SoulCrystal[crystals.size()]);
    }

    @Override
    public int size() {
        return crystals.size();
    }

    @Override
    public void clear() {
        crystals.clear();
    }
}
