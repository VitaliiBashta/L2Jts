package org.mmocore.gameserver.data.xml.holder;

import gnu.trove.map.hash.TIntObjectHashMap;
import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.gameserver.templates.AirshipDock;

//import org.mmocore.commons.annotation.OWrite;


/**
 * @author: VISTALL
 * @date: 10:44/14.12.2010
 */
public final class AirshipDockHolder extends AbstractHolder {
    //	@OWrite(customWrite = true)
    private static final AirshipDockHolder INSTANCE = new AirshipDockHolder();

    private final TIntObjectHashMap<AirshipDock> docks = new TIntObjectHashMap<>(4);

    public static AirshipDockHolder getInstance() {
        return INSTANCE;
    }

    public void addDock(final AirshipDock dock) {
        docks.put(dock.getId(), dock);
    }

    public AirshipDock getDock(final int dock) {
        return docks.get(dock);
    }

    @Override
    public int size() {
        return docks.size();
    }

    @Override
    public void clear() {
        docks.clear();
    }
}
