package org.mmocore.gameserver.data.xml.holder;

import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.gameserver.model.instances.TalismanManagerInstance;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hack
 * Date: 22.06.2017 0:04
 */
public class TalismanEventHolder extends AbstractHolder {
    private static TalismanEventHolder instance = new TalismanEventHolder();
    private Map<Integer, TalismanManagerInstance.Choice> holder = new HashMap<>();

    public static TalismanEventHolder getInstance() {
        return instance;
    }

    @Override
    public void clear() {
        holder.clear();
    }

    @Override
    public int size() {
        return holder.size();
    }

    public void add(TalismanManagerInstance.Choice choice) {
        holder.put(choice.getId(), choice);
    }

    public Map<Integer, TalismanManagerInstance.Choice> getHolder() {
        return holder;
    }

    public TalismanManagerInstance.Choice get(int id) {
        return holder.get(id);
    }
}
