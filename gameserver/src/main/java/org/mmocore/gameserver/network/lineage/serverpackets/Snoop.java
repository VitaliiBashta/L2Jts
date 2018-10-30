package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.ChatType;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class Snoop extends GameServerPacket {
    private final int objectId;
    private final int type;
    private final String name;
    private final String speaker;
    private final String text;

    public Snoop(final int id, final String name, final ChatType type, final String speaker, final String txt) {
        objectId = id;
        this.name = name;
        this.type = type.ordinal();
        this.speaker = speaker;
        text = txt;
    }

    @Override
    protected final void writeData() {
        writeD(objectId);
        writeS(name);
        writeD(objectId);
        writeD(type);
        writeS(speaker);
        writeS(text);
    }
}