package org.mmocore.gameserver.model.entity.events.objects;

import org.jdom2.Element;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.utils.Location;

/**
 * @author VISTALL
 * @date 15:47/28.12.2010
 */
public class BoatPoint extends Location {
    private final int fuel;
    private int speed1;
    private int speed2;
    private boolean teleport;
    private SystemMsg message;

    public BoatPoint(final int x, final int y, final int z, final int h, final int speed1, final int speed2, final int fuel, final boolean teleport, final SystemMsg message) {
        super(x, y, z, h);
        this.speed1 = speed1;
        this.speed2 = speed2;
        this.fuel = fuel;
        this.teleport = teleport;
        this.message = message;
    }

    public static BoatPoint parse(final Element element) {
        final int speed1 = element.getAttributeValue("speed1") == null ? 0 : Integer.parseInt(element.getAttributeValue("speed1"));
        final int speed2 = element.getAttributeValue("speed2") == null ? 0 : Integer.parseInt(element.getAttributeValue("speed2"));
        final int x = Integer.parseInt(element.getAttributeValue("x"));
        final int y = Integer.parseInt(element.getAttributeValue("y"));
        final int z = Integer.parseInt(element.getAttributeValue("z"));
        final int h = element.getAttributeValue("h") == null ? 0 : Integer.parseInt(element.getAttributeValue("h"));
        final int fuel = element.getAttributeValue("fuel") == null ? 0 : Integer.parseInt(element.getAttributeValue("fuel"));
        final boolean teleport = Boolean.parseBoolean(element.getAttributeValue("teleport"));
        final SystemMsg message = element.getAttributeValue("message") == null ? SystemMsg.NULL : SystemMsg.valueOf(element.getAttributeValue("message"));
        return new BoatPoint(x, y, z, h, speed1, speed2, fuel, teleport, message);
    }

    public int getSpeed1() {
        return speed1;
    }

    public void setSpeed1(final int speed1) {
        this.speed1 = speed1;
    }

    public int getSpeed2() {
        return speed2;
    }

    public void setSpeed2(final int speed2) {
        this.speed2 = speed2;
    }

    public int getFuel() {
        return fuel;
    }

    public SystemMsg getMessage() {
        return message;
    }

    public boolean isTeleport() {
        return teleport;
    }

    public void setTeleport(final boolean teleport) {
        this.teleport = teleport;
    }
}
