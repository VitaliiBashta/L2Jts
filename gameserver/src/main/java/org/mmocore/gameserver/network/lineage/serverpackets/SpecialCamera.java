package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.GameObject;

public class SpecialCamera extends GameServerPacket {
    private final int id;
    private final int force;
    private final int angle1;
    private final int angle2;
    private final int time;
    private final int duration;
    private final int relYaw;
    private final int relPitch;
    private final int isWide;
    private final int relAngle;
    private final int unk;

    /**
     * Special Camera packet constructor.
     *
     * @param creature the creature
     * @param force
     * @param angle1
     * @param angle2
     * @param time
     * @param range
     * @param duration
     * @param relYaw
     * @param relPitch
     * @param isWide
     * @param relAngle
     */
    public SpecialCamera(final GameObject creature, final int force, final int angle1, final int angle2, final int time, final int range,
                         final int duration, final int relYaw, final int relPitch, final int isWide, final int relAngle) {
        this(creature, force, angle1, angle2, time, duration, range, relYaw, relPitch, isWide, relAngle, 0);
    }

    /**
     * Special Camera Ex packet constructor.
     *
     * @param creature the creature
     * @param talker
     * @param force
     * @param angle1
     * @param angle2
     * @param time
     * @param duration
     * @param relYaw
     * @param relPitch
     * @param isWide
     * @param relAngle
     */
    public SpecialCamera(final GameObject creature, final GameObject talker, final int force, final int angle1, final int angle2,
                         final int time, final int duration, final int relYaw, final int relPitch, final int isWide, final int relAngle) {
        this(creature, force, angle1, angle2, time, duration, 0, relYaw, relPitch, isWide, relAngle, 0);
    }

    /**
     * Special Camera 3 packet constructor.
     *
     * @param creature the creature
     * @param force
     * @param angle1
     * @param angle2
     * @param time
     * @param range
     * @param duration
     * @param relYaw
     * @param relPitch
     * @param isWide
     * @param relAngle
     * @param unk      unknown post-C4 parameter
     */
    public SpecialCamera(final GameObject creature, final int force, final int angle1, final int angle2, final int time, final int range,
                         final int duration, final int relYaw, final int relPitch, final int isWide, final int relAngle, final int unk) {
        this.id = creature.getObjectId();
        this.force = force;
        this.angle1 = angle1;
        this.angle2 = angle2;
        this.time = time;
        this.duration = duration;
        this.relYaw = relYaw;
        this.relPitch = relPitch;
        this.isWide = isWide;
        this.relAngle = relAngle;
        this.unk = unk;
    }

    @Override
    protected final void writeData() {
        // ddddddddddd
        writeD(id); // object id
        writeD(force); //расстояние до объекта
        writeD(angle1); // North=90, south=270, east=0, west=180
        writeD(angle2); // > 0:looks up,pitch < 0:looks down (угол наклона)
        writeD(time); //faster that small value is
        writeD(duration); //время анимации
        writeD(relYaw);
        writeD(relPitch);
        writeD(isWide);
        writeD(relAngle);
        writeD(unk);
    }
}