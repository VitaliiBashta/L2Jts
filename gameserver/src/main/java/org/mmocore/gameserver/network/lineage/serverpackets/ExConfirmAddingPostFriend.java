package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

/**
 * @author VISTALL
 * @date 23:13/21.03.2011
 */
public class ExConfirmAddingPostFriend extends GameServerPacket {
    public static final int NAME_IS_NOT_EXISTS = 0;
    public static final int SUCCESS = 1;
    public static final int LIST_IS_FULL = -3;
    public static final int ALREADY_ADDED = -4;
    public static final int NAME_IS_NOT_REGISTERED = -4;
    public static int PREVIOS_NAME_IS_BEEN_REGISTERED = -1;  // The previous name is being registered. Please try again later.
    public static int NAME_IS_NOT_EXISTS2 = -2;
    private final String name;
    private final int result;

    public ExConfirmAddingPostFriend(final String name, final int s) {
        this.name = name;
        result = s;
    }

    @Override
    public void writeData() {
        writeS(name);
        writeD(result);
    }
}