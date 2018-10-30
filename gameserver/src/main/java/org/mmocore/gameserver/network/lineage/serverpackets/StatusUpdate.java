package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

import java.util.ArrayList;
import java.util.List;

/**
 * Даные параметры актуальны для С6(Interlude), 04/10/2007, протокол 746
 */
public class StatusUpdate extends GameServerPacket {
    /**
     * Даный параметр отсылается оффом в паре с MAX_HP
     * Сначала CUR_HP, потом MAX_HP
     */
    public static final int CUR_HP = 0x09;
    public static final int MAX_HP = 0x0a;

    /**
     * Даный параметр отсылается оффом в паре с MAX_MP
     * Сначала CUR_MP, потом MAX_MP
     */
    public static final int CUR_MP = 0x0b;
    public static final int MAX_MP = 0x0c;

    /**
     * Меняется отображение только в инвентаре, для статуса требуется UserInfo
     */
    public static final int CUR_LOAD = 0x0e;

    /**
     * Меняется отображение только в инвентаре, для статуса требуется UserInfo
     */
    public static final int MAX_LOAD = 0x0f;

    public static final int PVP_FLAG = 0x1a;
    public static final int KARMA = 0x1b;

    /**
     * Даный параметр отсылается оффом в паре с MAX_CP
     * Сначала CUR_CP, потом MAX_CP
     */
    public static final int CUR_CP = 0x21;
    public static final int MAX_CP = 0x22;

    private final int objectId;
    private final List<Attribute> attributes = new ArrayList<>();

    public StatusUpdate(final int objectId) {
        this.objectId = objectId;
    }

    public StatusUpdate addAttribute(final int id, final int level) {
        attributes.add(new Attribute(id, level));
        return this;
    }

    @Override
    protected final void writeData() {
        writeD(objectId);
        writeD(attributes.size());

        for (final Attribute temp : attributes) {
            writeD(temp.id);
            writeD(temp.value);
        }
    }

    public boolean hasAttributes() {
        return !attributes.isEmpty();
    }

    static class Attribute {
        public final int id;
        public final int value;

        Attribute(final int id, final int value) {
            this.id = id;
            this.value = value;
        }
    }
}