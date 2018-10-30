package org.mmocore.gameserver.data.client.holder;

import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.gameserver.templates.client.EtcitemgrpLine;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Create by Mangol on 20.12.2015.
 */
public class EtcitemgrpLineHolder extends AbstractHolder {
    private final static EtcitemgrpLineHolder INSTANCE = new EtcitemgrpLineHolder();
    private final Map<Integer, EtcitemgrpLine> etcitemgrp = new HashMap<>();

    public static EtcitemgrpLineHolder getInstance() {
        return INSTANCE;
    }

    public void addEtcitemgrp(final EtcitemgrpLine armorgprLine) {
        etcitemgrp.put(armorgprLine.getId(), armorgprLine);
    }

    public Optional<EtcitemgrpLine> getEtcitemgrp(final int id) {
        return Optional.ofNullable(etcitemgrp.get(id));
    }

    @Override
    public int size() {
        return etcitemgrp.size();
    }

    @Override
    public void clear() {
        //
    }
}
