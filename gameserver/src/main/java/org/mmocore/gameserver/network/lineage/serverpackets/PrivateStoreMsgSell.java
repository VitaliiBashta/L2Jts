package org.mmocore.gameserver.network.lineage.serverpackets;


import org.apache.commons.lang3.StringUtils;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

public class PrivateStoreMsgSell extends GameServerPacket {
    private final int objId;
    private final String name;

    /**
     * Название личного магазина продажи
     *
     * @param player
     */
    public PrivateStoreMsgSell(final Player player) {
        objId = player.getObjectId();
        name = StringUtils.defaultString(player.getSellStoreName());
    }

    @Override
    protected final void writeData() {
        writeD(objId);
        writeS(name);
    }
}