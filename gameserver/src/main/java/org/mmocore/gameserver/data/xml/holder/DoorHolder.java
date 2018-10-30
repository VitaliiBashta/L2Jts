package org.mmocore.gameserver.data.xml.holder;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.gameserver.templates.DoorTemplate;

public final class DoorHolder extends AbstractHolder {
    private static final DoorHolder INSTANCE = new DoorHolder();

    private final TIntObjectMap<DoorTemplate> doors = new TIntObjectHashMap<>();

    public static DoorHolder getInstance() {
        return INSTANCE;
    }

    public void addTemplate(final DoorTemplate door) {
        doors.put(door.getNpcId(), door);
    }

    public DoorTemplate getTemplate(final int doorId) {
        return doors.get(doorId);
    }

    public TIntObjectMap<DoorTemplate> getDoors() {
        return doors;
    }

    @Override
    public int size() {
        return doors.size();
    }

    @Override
    public void clear() {
        doors.clear();
    }
}