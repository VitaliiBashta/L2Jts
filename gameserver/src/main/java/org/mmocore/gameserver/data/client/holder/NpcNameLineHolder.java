package org.mmocore.gameserver.data.client.holder;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.templates.client.NpcNameLine;
import org.mmocore.gameserver.utils.Language;
import org.mmocore.gameserver.utils.Util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


public class NpcNameLineHolder extends AbstractHolder {
    private static final NpcNameLineHolder INSTANCE = new NpcNameLineHolder();
    private final Table<Language, Integer, NpcNameLine> names = HashBasedTable.create();
    private final Set<String> blackList = new HashSet<>(100);

    public static NpcNameLineHolder getInstance() {
        return INSTANCE;
    }

    public NpcNameLine get(final Language lang, final int npcId) {
        return names.get(lang, npcId);
    }

    public void put(final Language lang, final NpcNameLine npcName) {
        names.put(lang, npcName.getNpcId(), npcName);
        final String name = npcName.getName().toLowerCase();
        if (!blackList.contains(name) && Util.isMatchingRegexp(name, ServerConfig.CNAME_TEMPLATE)) {
            blackList.add(name);
        }
    }

    public Collection<NpcNameLine> getNpcNames(final Language lang) {
        return names.row(lang).values();
    }

    public boolean isBlackListContainsName(final String name) {
        return blackList.contains(name.toLowerCase());
    }

    @Override
    public void log() {
        names.rowMap().entrySet().stream().forEach(entry -> info("load npcname line(s): " + entry.getValue().size() + " for lang: " + entry.getKey()));
        info("load " + blackList.size() + " blacklist names");
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