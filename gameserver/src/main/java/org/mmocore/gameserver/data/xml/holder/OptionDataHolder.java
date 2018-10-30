package org.mmocore.gameserver.data.xml.holder;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.gameserver.templates.OptionDataTemplate;

import java.util.Collection;

/**
 * @author VISTALL
 * @date 20:35/19.05.2011
 */
public final class OptionDataHolder extends AbstractHolder {
    private final TIntObjectMap<OptionDataTemplate> templates = new TIntObjectHashMap<>();

    public static OptionDataHolder getInstance() {
        return LazyHolder.INSTANCE;
    }

    public void addTemplate(final OptionDataTemplate template) {
        templates.put(template.getId(), template);
    }

    public OptionDataTemplate getTemplate(final int id) {
        return templates.get(id);
    }

    public Collection<OptionDataTemplate> getTemplates() {
        return templates.valueCollection();
    }

    @Override
    public int size() {
        return templates.size();
    }

    @Override
    public void clear() {
        templates.clear();
    }

    private static class LazyHolder {
        private static final OptionDataHolder INSTANCE = new OptionDataHolder();
    }
}
