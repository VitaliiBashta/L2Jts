package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.interfaces.ShortCut;

public class ShortCutRegister extends ShortCutPacket {
    private final ShortcutInfo shortcutInfo;

    public ShortCutRegister(final Player player, final ShortCut sc) {
        shortcutInfo = convert(player, sc);
    }

    @Override
    protected final void writeData() {
        shortcutInfo.write(this);
    }
}