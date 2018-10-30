package org.mmocore.gameserver.data.client.holder;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.jts.dataparser.data.holder.setting.common.PlayerSex;
import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.gameserver.templates.client.TransformDataLine;

/**
 * Create by Mangol on 19.10.2015.
 */
public class TransformDataLineHolder extends AbstractHolder {
    private static final TransformDataLineHolder INSTANCE = new TransformDataLineHolder();
    private final Table<PlayerSex, Integer, TransformDataLine> names = HashBasedTable.create();

    public static TransformDataLineHolder getInstance() {
        return INSTANCE;
    }

    public TransformDataLine get(final PlayerSex sex, final int id) {
        return names.get(sex, id);
    }

    public void put(TransformDataLine transform) {
        names.put(transform.getSex(), transform.getId(), transform);
    }

    @Override
    public void log() {
        names.rowMap().entrySet().stream().forEach(entry -> info("load transformdata line(s): " + entry.getValue().size() + " for sex: " + entry.getKey()));
    }

    @Override
    public int size() {
        return names.size();
    }

    @Override
    public void clear() {
        names.clear();
    }
}
