package org.mmocore.gameserver.data.xml.holder;

import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.gameserver.model.dress.DressArmorData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hack
 * Date: 17.08.2016 18:47
 */
public class DressArmorHolder extends AbstractHolder {
    private static final DressArmorHolder _instance = new DressArmorHolder();
    private List<DressArmorData> _dress = new ArrayList<DressArmorData>();

    public static DressArmorHolder getInstance() {
        return _instance;
    }

    public void addDress(DressArmorData armorset) {
        _dress.add(armorset);
    }

    public List<DressArmorData> getAllDress() {
        return _dress;
    }

    public DressArmorData getArmor(int id) {
        for (DressArmorData dress : _dress) {
            if (dress.getId() == id)
                return dress;
        }

        return null;
    }

    @Override
    public int size() {
        return _dress.size();
    }

    @Override
    public void clear() {
        _dress.clear();
    }
}
