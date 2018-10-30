package org.mmocore.gameserver.data.xml.holder.custom.community;

import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.gameserver.object.components.player.community.BuffScheme;

import java.util.HashMap;

/**
 * Create by Mangol on 13.12.2015.
 */
public class CBufferHolder extends AbstractHolder {
    private static final CBufferHolder INSTANCE = new CBufferHolder();
    private static final HashMap<Integer, BuffScheme> buffSchemes = new HashMap<>();

    public static CBufferHolder getInstance() {
        return INSTANCE;
    }

    public void addBuffScheme(final BuffScheme buffScheme) {
        buffSchemes.put(buffScheme.getId(), buffScheme);
    }

    public BuffScheme getBuffScheme(final int buffSchemeId) {
        return buffSchemes.get(buffSchemeId);
    }

    public HashMap<Integer, BuffScheme> getBuffScheme() {
        return buffSchemes;
    }

    @Override
    public int size() {
        return buffSchemes.size();
    }

    @Override
    public void clear() {
        buffSchemes.clear();
    }
}
