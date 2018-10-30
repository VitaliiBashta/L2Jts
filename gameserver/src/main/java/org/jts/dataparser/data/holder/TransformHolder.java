package org.jts.dataparser.data.holder;

import org.jts.dataparser.data.annotations.Element;
import org.jts.dataparser.data.holder.transform.TransformData;
import org.mmocore.commons.data.AbstractHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author : Mangol
 */
public class TransformHolder extends AbstractHolder {
    @Element(start = "transform_begin", end = "transform_end")
    private static List<TransformData> transform;
    private static TransformHolder ourInstance = new TransformHolder();
    private final Map<Integer, TransformData> transform_data = new HashMap<>();

    public static TransformHolder getInstance() {
        return ourInstance;
    }

    public List<TransformData> getTransformData() {
        return transform;
    }

    public Optional<TransformData> getTransformId(final int id) {
        return Optional.ofNullable(transform_data.get(id));
    }

    @Override
    public void afterParsing() {
        transform.stream().forEach(t -> transform_data.put(t.id, t));
        transform.clear();
    }

    @Override
    public int size() {
        return transform_data.size();
    }

    @Override
    public void clear() {
        transform_data.clear();
    }
}
