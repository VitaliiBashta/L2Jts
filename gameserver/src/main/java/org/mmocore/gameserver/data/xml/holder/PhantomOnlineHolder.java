package org.mmocore.gameserver.data.xml.holder;

import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.gameserver.phantoms.model.Phantom;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Hack
 * Date: 21.08.2016 6:33
 * <p>
 * Holder of online phantoms
 */

public class PhantomOnlineHolder extends AbstractHolder {
    private static final PhantomOnlineHolder instance = new PhantomOnlineHolder();
    private final Map<String, Phantom> phantoms = new ConcurrentHashMap<>();

    public static PhantomOnlineHolder getInstance() {
        return instance;
    }

    public void addPhantom(Phantom phantom) {
        phantoms.put(phantom.getName(), phantom);
    }

    public void deletePhantom(Phantom phantom) {
        deletePhantom(phantom.getName());
    }

    public void deletePhantom(String name) {
        phantoms.remove(name);
    }

    public void getPhantom(String name) {
        phantoms.get(name);
    }

    public Map<String, Phantom> getPhantomMap() {
        return phantoms;
    }

    public boolean contains(Phantom phantom) {
        return contains(phantom.getName());
    }

    public boolean contains(String name) {
        return phantoms.keySet().contains(name);
    }

    public boolean contains(int objId) {
        for (Phantom phantom : phantoms.values())
            if (phantom.getObjectId() == objId)
                return true;
        return false;
    }

    @Override
    public int size() {
        return phantoms.size();
    }

    @Override
    public void clear() {
        phantoms.clear();
    }

}
