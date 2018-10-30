package org.mmocore.gameserver.network.lineage.serverpackets;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;
import org.mmocore.commons.utils.TroveUtils;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.AccountPlayerInfo;

/**
 * @author VISTALL
 * @date 20:24/16.05.2011
 */
public class PackageToList extends GameServerPacket {
    private TIntObjectMap<AccountPlayerInfo> characters = TroveUtils.emptyIntObjectMap();

    public PackageToList(final Player player) {
        characters = player.getAccountChars();
    }

    @Override
    protected void writeData() {
        writeD(characters.size());
        final TIntObjectIterator<AccountPlayerInfo> iterator = characters.iterator();
        for (int i = characters.size(); i > 0; i--) {
            iterator.advance();
            writeD(iterator.key());
            writeS(iterator.value().getName());
        }
    }
}