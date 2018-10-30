package org.jts.dataparser.data.holder;

import org.mmocore.commons.data.AbstractHolder;

import java.util.HashMap;

/**
 * @author : Camelion
 * @date : 27.08.12 13:30
 */
public class FStringHolder extends AbstractHolder {
    private static final FStringHolder ourInstance = new FStringHolder();
    private final HashMap<Integer, String> fStringMap;

    private FStringHolder() {
        fStringMap = new HashMap<>();
    }

    public static FStringHolder getInstance() {
        return ourInstance;
    }

    @Override
    public int size() {
        return fStringMap.size();
    }

    public void addFString(int id, String value) {
        fStringMap.put(id, value);
    }

    public HashMap<Integer, String> getFStrings() {
        return fStringMap;
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
    }
}