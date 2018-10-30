package org.mmocore.gameserver.data.xml.holder;

import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.gameserver.phantoms.template.PhantomEquipTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hack
 * Date: 23.08.2016 2:05
 */
public class PhantomEquipHolder extends AbstractHolder {
    private static final Logger log = LoggerFactory.getLogger(PhantomEquipHolder.class);
    private static final PhantomEquipHolder instance = new PhantomEquipHolder();
    private final Map<Integer, PhantomEquipTemplate> equip = new HashMap<>();

    public static PhantomEquipHolder getInstance() {
        return instance;
    }

    public PhantomEquipTemplate getClassEquip(int classId) {
        if (equip.containsKey(classId))
            return equip.get(classId);
        else {
            log.warn("Can't find equipment for class id: " + classId + "! Please check class_equip.xml");
            return null;
        }
    }

    public void addClassEquip(int classId, PhantomEquipTemplate template) {
        equip.put(classId, template);
    }

    @Override
    public int size() {
        return equip.size();
    }

    @Override
    public void clear() {
        equip.clear();
    }
}
