package org.mmocore.gameserver.data.xml.holder.other;

import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.gameserver.templates.other.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ColorHolder extends AbstractHolder {
    private static final ColorHolder INSTANCE = new ColorHolder();
    private final Map<String, List<ColorTemplate>> map = new HashMap<>();

    public static ColorHolder getInstance() {
        return INSTANCE;
    }

    public List<ColorTemplate> getListColor(final String service) {
        return map.get(service);
    }

    public ColorTemplate getColorTemplate(final String service, final int index) {
        return map.get(service).get(index);
    }

    public void addColorTemplate(final String service, final ColorTemplate template) {
        if (map.containsKey(service)) {
            final List<ColorTemplate> list = map.get(service);
            list.add(template);
        } else {
            final List<ColorTemplate> list = new ArrayList<>();
            list.add(template);
            map.put(service, list);
        }
    }

    @Override
    public void log() {
        map.forEach((key, value) -> info("load color line(s): " + value.size() + " for service: " + key));
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public int size() {
        return map.size();
    }
}
