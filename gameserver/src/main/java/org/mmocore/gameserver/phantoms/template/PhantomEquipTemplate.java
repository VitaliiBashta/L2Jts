package org.mmocore.gameserver.phantoms.template;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Hack
 * Date: 23.08.2016 0:41
 */
public class PhantomEquipTemplate {
    private static final Logger log = LoggerFactory.getLogger(PhantomEquipTemplate.class);
    private final Map<ItemTemplate.Grade, List<Integer>> weapon = new HashMap<>();
    private final Map<ItemTemplate.Grade, List<Integer>> shield = new HashMap<>();
    private final Map<ItemTemplate.Grade, List<PhantomArmorTemplate>> armor = new HashMap<>();
    private int classId;

    /*
    public PhantomEquipTemplate() {
        for (ItemTemplate.Grade grade : ItemTemplate.Grade.values()) {
            weapon.put(grade, new ArrayList<>());
            armor.put(grade, new ArrayList<>());
        }
    }
    */

    public void addWeaponList(ItemTemplate.Grade grade, List<Integer> weapons) {
        weapon.put(grade, weapons);
    }

    public void addArmorList(ItemTemplate.Grade grade, List<PhantomArmorTemplate> armors) {
        armor.put(grade, armors);
    }

    public void addShieldList(ItemTemplate.Grade grade, List<Integer> shields) {
        shield.put(grade, shields);
    }

    public int getRandomWeaponId(ItemTemplate.Grade grade) {
        List<Integer> weapons = weapon.get(grade);
        if (weapons == null || weapons.size() == 0) {
            log.warn("Can't find weapon with grade: " + grade + " for class id: " + classId);
            return 0;
        }
        return weapons.get(Rnd.get(weapons.size()));
    }

    public PhantomArmorTemplate getRandomArmor(ItemTemplate.Grade grade) {
        List<PhantomArmorTemplate> sets = armor.get(grade);
        if (sets == null || sets.size() == 0) {
            log.warn("Can't find armor with grade: " + grade + " for class id: " + classId);
            return null;
        }
        return sets.get(Rnd.get(sets.size()));
    }

    public int getRandomShieldId(ItemTemplate.Grade grade) {
        List<Integer> shields = shield.get(grade);
        if (shields == null || shields.size() == 0) {
//            log.warn("Can't find shield with grade: " + grade + " for class id: " + classId);
            return 0;
        }
        return shields.get(Rnd.get(shields.size()));
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }
}
