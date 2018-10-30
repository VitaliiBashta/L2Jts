package org.mmocore.gameserver.network.lineage.serverpackets;

import org.apache.commons.lang3.StringUtils;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

/**
 * @author KilRoy
 */
public class ExPrivateStoreWholeMsg extends GameServerPacket {
    private final int objId;
    private final String name;

    /**
     * Название личного магазина продажи
     *
     * @param player
     */
    public ExPrivateStoreWholeMsg(final Player player) {
        objId = player.getObjectId();
        name = StringUtils.defaultString(player.getSellStoreName());
    }

    @Override
    protected final void writeData() {
        writeD(objId);
        writeS(name);
    }
}