package org.mmocore.gameserver.network.lineage.serverpackets;


import org.apache.commons.lang3.StringUtils;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

public class PrivateStoreMsgBuy extends GameServerPacket {
    private final int objId;
    private final String name;

    /**
     * Название личного магазина покупки
     *
     * @param player
     */
    public PrivateStoreMsgBuy(final Player player) {
        objId = player.getObjectId();
        name = StringUtils.defaultString(player.getBuyStoreName());
    }

    @Override
    protected final void writeData() {
        writeD(objId);
        writeS(name);
    }
}