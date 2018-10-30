package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.utils.Location;

public class PlaySound extends GameServerPacket {
    public static final GameServerPacket SIEGE_VICTORY = new PlaySound("Siege_Victory");
    public static final GameServerPacket B04_S01 = new PlaySound("B04_S01");
    public static final GameServerPacket HB01 = new PlaySound(Type.MUSIC, "HB01", 0, 0, 0, 0, 0);
    private final Type type;
    private final String soundFile;
    private final int hasCenterObject;
    private final int objectId;
    private final int x;
    private final int y;
    private final int z;
    public PlaySound(final String soundFile) {
        this(Type.SOUND, soundFile, 0, 0, 0, 0, 0);
    }

    public PlaySound(final Type type, final String soundFile) {
        this(type, soundFile, 0, 0, 0, 0, 0);
    }

    public PlaySound(final Type type, final String soundFile, final int c, final int objectId, final Location loc) {
        this(type, soundFile, c, objectId, loc == null ? 0 : loc.x, loc == null ? 0 : loc.y, loc == null ? 0 : loc.z);
    }

    public PlaySound(final Type type, final String soundFile, final int c, final int objectId, final int x, final int y, final int z) {
        this.type = type;
        this.soundFile = soundFile;
        hasCenterObject = c;
        this.objectId = objectId;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    protected final void writeData() {
        //dSdddddd
        writeD(type.ordinal()); //0 for quest and ship, c4 toturial = 2
        writeS(soundFile);
        writeD(hasCenterObject); //0 for quest; 1 for ship;
        writeD(objectId); //0 for quest; objectId of ship
        writeD(x); //x
        writeD(y); //y
        writeD(z); //z
    }

    public enum Type {
        SOUND,
        MUSIC,
        VOICE
    }
}