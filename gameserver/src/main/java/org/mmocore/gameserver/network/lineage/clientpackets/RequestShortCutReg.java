package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.network.lineage.serverpackets.ShortCutRegister;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.interfaces.ShortCut;

public class RequestShortCutReg extends L2GameClientPacket {
    private int _type, _id, _slot, _page, _lvl, _characterType;

    @Override
    protected void readImpl() {
        _type = readD();
        final int slot = readD();
        _id = readD();
        _lvl = readD();
        _characterType = readD();

        _slot = slot % 12;
        _page = slot / 12;
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }

        if (_page < 0 || _page > ShortCut.PAGE_MAX) {
            player.sendActionFailed();
            return;
        }

        if (_type <= 0 || _type > ShortCut.TYPE_MAX) {
            player.sendActionFailed();
            return;
        }

        final ShortCut shortCut = new ShortCut(_slot, _page, _type, _id, _lvl, _characterType);
        player.sendPacket(new ShortCutRegister(player, shortCut));
        player.getShortCutComponent().registerShortCut(shortCut);
    }
}