package org.mmocore.gameserver.phantoms.template;

import org.mmocore.gameserver.model.Territory;
import org.mmocore.gameserver.phantoms.ai.PhantomAiType;
import org.mmocore.gameserver.templates.item.ItemTemplate;

/**
 * Created by Hack
 * Date: 22.08.2016 4:41
 */
public class PhantomSpawnTemplate {
    private PhantomAiType type; //unused, TODO
    private int count;
    private ItemTemplate.Grade gradeMin, gradeMax;
    private Territory territory;

    public PhantomSpawnTemplate() {
    }

    public PhantomSpawnTemplate(PhantomAiType type, int count, ItemTemplate.Grade gradeMin, ItemTemplate.Grade gradeMax, Territory territory) {
        this.type = type;
        this.count = count;
        this.gradeMin = gradeMin;
        this.gradeMax = gradeMax;
        this.territory = territory;
    }

    public PhantomAiType getType() {
        return type;
    }

    public void setType(PhantomAiType type) {
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ItemTemplate.Grade getGradeMin() {
        return gradeMin;
    }

    public void setGradeMin(ItemTemplate.Grade gradeMin) {
        this.gradeMin = gradeMin;
    }

    public ItemTemplate.Grade getGradeMax() {
        return gradeMax;
    }

    public void setGradeMax(ItemTemplate.Grade gradeMax) {
        this.gradeMax = gradeMax;
    }

    public Territory getTerritory() {
        return territory;
    }

    public void setTerritory(Territory territory) {
        this.territory = territory;
    }
}
