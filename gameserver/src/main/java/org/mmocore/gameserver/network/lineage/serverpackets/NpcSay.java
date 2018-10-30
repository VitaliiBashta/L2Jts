package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.ChatType;
import org.mmocore.gameserver.network.lineage.components.NpcString;

public class NpcSay extends NpcStringContainer {
    private final int objId;
    private final int type;
    private final int id;

    public NpcSay(final NpcInstance npc, final ChatType chatType, final String text) {
        this(npc, chatType, NpcString.NONE, text);
    }

    public NpcSay(final NpcInstance npc, final ChatType chatType, final NpcString npcString, final String... params) {
        super(npcString, params);
        objId = npc.getObjectId();
        id = npc.getNpcId();
        type = chatType.ordinal();
    }

    @Override
    protected final void writeData() {
        writeD(objId);
        writeD(type);
        writeD(1000000 + id);
        writeElements();
    }
}