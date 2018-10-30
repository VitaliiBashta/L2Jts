package org.mmocore.gameserver.handler.npcdialog;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.mmocore.commons.data.AbstractHolder;

import java.util.List;

/**
 * @author VISTALL
 * @date 15:32 13.08.11
 */
public class NpcDialogAppenderHolder extends AbstractHolder {
    private static final NpcDialogAppenderHolder INSTANCE = new NpcDialogAppenderHolder();

    private final ListMultimap<Integer, INpcDialogAppender> appenders = ArrayListMultimap.create();

    public static NpcDialogAppenderHolder getInstance() {
        return INSTANCE;
    }

    public void register(INpcDialogAppender ap) {
        for (int npcId : ap.getNpcIds())
            appenders.put(npcId, ap);
    }

    public List<INpcDialogAppender> getAppenders(int val) {
        return appenders.get(val);
    }

    @Override
    public int size() {
        return appenders.size();
    }

    @Override
    public void clear() {
        appenders.clear();
    }
}
