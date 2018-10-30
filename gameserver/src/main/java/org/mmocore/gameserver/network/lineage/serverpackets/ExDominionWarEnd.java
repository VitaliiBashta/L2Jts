package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

/**
 * @author VISTALL
 * @date 12:11/05.03.2011
 */
@Deprecated  // not used
public class ExDominionWarEnd extends GameServerPacket {
    public static final GameServerPacket STATIC = new ExDominionWarEnd();

    @Override
    public void writeData() {
    }
}
