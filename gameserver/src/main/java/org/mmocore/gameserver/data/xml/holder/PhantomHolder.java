package org.mmocore.gameserver.data.xml.holder;

import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.phantoms.template.PhantomTemplate;
import org.mmocore.gameserver.templates.item.ItemTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Hack
 * Date: 21.08.2016 6:10
 */
public class PhantomHolder extends AbstractHolder {
    private static final PhantomHolder instance = new PhantomHolder();
    private final Map<ItemTemplate.Grade, List<PhantomTemplate>> phantomTemplates = new HashMap<>();

    public PhantomHolder() {
        for (ItemTemplate.Grade grade : ItemTemplate.Grade.values())
            phantomTemplates.put(grade, new ArrayList<>());
    }

    public static PhantomHolder getInstance() {
        return instance;
    }

    public void addPhantomTemplate(ItemTemplate.Grade grade, PhantomTemplate phantom) {
        phantomTemplates.get(grade).add(phantom);
    }

    public Map<ItemTemplate.Grade, List<PhantomTemplate>> getPhantomTemplateMap() {
        return phantomTemplates;
    }

    public PhantomTemplate getRandomPhantomTemplate(ItemTemplate.Grade minGrade, ItemTemplate.Grade maxGrade) {
        ItemTemplate.Grade rndGrade = ItemTemplate.Grade.values()[Rnd.get(minGrade.ordinal(), maxGrade.ordinal())];
        List<PhantomTemplate> gradeList = phantomTemplates.get(rndGrade);
        if (gradeList.size() == 0) {
            _log.warn("Can't find template for grade: " + rndGrade);
            return null;
        }
        return gradeList.get(Rnd.get(gradeList.size()));
    }

    public boolean isNameExists(String name) {
        for (List<PhantomTemplate> list : phantomTemplates.values())
            for (PhantomTemplate template : list)
                if (template.getName().equalsIgnoreCase(name))
                    return true;
        return false;

    }

    @Override
    public int size() {
        return phantomTemplates.size();
    }

    @Override
    public void clear() {
        phantomTemplates.clear();
    }
}
