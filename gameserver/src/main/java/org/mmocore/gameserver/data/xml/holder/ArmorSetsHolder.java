package org.mmocore.gameserver.data.xml.holder;

import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.gameserver.model.ArmorSet;

import java.util.ArrayList;
import java.util.List;

public final class ArmorSetsHolder extends AbstractHolder {
    private static final ArmorSetsHolder INSTANCE = new ArmorSetsHolder();
    private final List<ArmorSet> armorSets = new ArrayList<>();

    public static ArmorSetsHolder getInstance() {
        return INSTANCE;
    }

    public void addArmorSet(ArmorSet armorset) {
        armorSets.add(armorset);
    }

    public ArmorSet getArmorSet(int chestItemId) {
        for (ArmorSet as : armorSets) {
            if (as.getChestItemIds().contains(chestItemId)) {
                return as;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return armorSets.size();
    }

    @Override
    public void clear() {
        armorSets.clear();
    }
}
