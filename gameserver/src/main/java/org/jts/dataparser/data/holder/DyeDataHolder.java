package org.jts.dataparser.data.holder;

import org.jts.dataparser.data.annotations.Element;
import org.jts.dataparser.data.holder.dyedata.DyeData;
import org.mmocore.commons.data.AbstractHolder;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : Camelion
 * @date : 27.08.12 1:36
 */
public class DyeDataHolder extends AbstractHolder {
    private static final DyeDataHolder ourInstance = new DyeDataHolder();
    private final Map<Integer, DyeData> dyes_m = new HashMap<>();
    @Element(start = "dye_begin", end = "dye_end")
    private List<DyeData> dyes_l;

    private DyeDataHolder() {
    }

    public static DyeDataHolder getInstance() {
        return ourInstance;
    }

    @Override
    public int size() {
        return dyes_m.size();
    }

    @Override
    public void afterParsing() {
        dyes_l.stream().forEach(d -> dyes_m.put(d.dye_id, d));
        dyes_l.clear();
    }

    public DyeData getDye(final int dyeId) {
        return dyes_m.get(dyeId);
    }

    public Collection<DyeData> getDyes() {
        return dyes_m.values();
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
    }
}