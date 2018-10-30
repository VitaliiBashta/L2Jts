package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

public class L2Friend extends GameServerPacket {
    private final boolean add;
    private final boolean online;
    private final String name;
    private final int object_id;

    public L2Friend(final Player player, final boolean add) {
        this.add = add;
        name = player.getName();
        object_id = player.getObjectId();
        online = true;
    }

    public L2Friend(final String name, final boolean add, final boolean online, final int object_id) {
        this.name = name;
        this.add = add;
        this.object_id = object_id;
        this.online = online;
    }

    @Override
    protected final void writeData() {
        writeD(add ? 1 : 3); // 1 - добавить друга в спикок, 3 удалить друга со списка
        writeD(0); //и снова тут идет ID персонажа в списке оффа, не object id
        writeS(name);
        writeD(online ? 1 : 0); // онлайн или оффлайн
        writeD(object_id); //object_id if online
    }
}