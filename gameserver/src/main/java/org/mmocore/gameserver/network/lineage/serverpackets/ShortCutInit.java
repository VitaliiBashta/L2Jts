package org.mmocore.gameserver.network.lineage.serverpackets;


import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.interfaces.ShortCut;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ShortCutInit extends ShortCutPacket {
    private List<ShortcutInfo> shortCuts = Collections.emptyList();

    public ShortCutInit(final Player pl) {
        final Collection<ShortCut> shortCuts = pl.getShortCutComponent().getAllShortCuts();
        this.shortCuts = new ArrayList<>(shortCuts.size());
        this.shortCuts.addAll(shortCuts.stream().map(shortCut -> convert(pl, shortCut)).collect(Collectors.toList()));
    }

    @Override
    protected final void writeData() {
        writeD(shortCuts.size());

        for (final ShortcutInfo sc : shortCuts) {
            sc.write(this);
        }
    }
}