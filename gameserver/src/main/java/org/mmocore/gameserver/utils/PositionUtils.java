package org.mmocore.gameserver.utils;

import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.GameObject;

/**
 * @author VISTALL
 * @date 16:06/03.05.2011
 */
public class PositionUtils {
    private static final int MAX_ANGLE = 360;
    private static final double FRONT_MAX_ANGLE = 90;
    private static final double BACK_MAX_ANGLE = 70;

    public static TargetDirection getDirectionTo(final Creature target, final Creature attacker) {
        if (target == null || attacker == null) {
            return TargetDirection.NONE;
        }
        if (isBehind(target, attacker)) {
            return TargetDirection.BEHIND;
        }
        if (isInFrontOf(target, attacker)) {
            return TargetDirection.FRONT;
        }
        return TargetDirection.SIDE;
    }

    /**
     * Those are altered formulas for blow lands
     * Return True if the target is IN FRONT of the L2Character.<BR><BR>
     */
    public static boolean isInFrontOf(final Creature target, final Creature attacker) {
        if (target == null) {
            return false;
        }

        final double angleChar;
        final double angleTarget;
        double angleDiff;
        angleTarget = calculateAngleFrom(target, attacker);
        angleChar = convertHeadingToDegree(target.getHeading());
        angleDiff = angleChar - angleTarget;
        if (angleDiff <= -MAX_ANGLE + FRONT_MAX_ANGLE) {
            angleDiff += MAX_ANGLE;
        }
        if (angleDiff >= MAX_ANGLE - FRONT_MAX_ANGLE) {
            angleDiff -= MAX_ANGLE;
        }
        if (Math.abs(angleDiff) <= FRONT_MAX_ANGLE) {
            return true;
        }
        return false;
    }

    /**
     * Those are altered formulas for blow lands
     * Return True if the L2Character is behind the target and can't be seen.<BR><BR>
     */
    public static boolean isBehind(final Creature target, final Creature attacker) {
        if (target == null) {
            return false;
        }

        final double angleChar;
        final double angleTarget;
        double angleDiff;
        angleChar = calculateAngleFrom(attacker, target);
        angleTarget = convertHeadingToDegree(target.getHeading());
        angleDiff = angleChar - angleTarget;
        if (angleDiff <= -MAX_ANGLE + BACK_MAX_ANGLE) {
            angleDiff += MAX_ANGLE;
        }
        if (angleDiff >= MAX_ANGLE - BACK_MAX_ANGLE) {
            angleDiff -= MAX_ANGLE;
        }
        if (Math.abs(angleDiff) <= BACK_MAX_ANGLE) {
            return true;
        }
        return false;
    }

    /**
     * Returns true if target is in front of L2Character (shield def etc)
     */
    public static boolean isFacing(final Creature attacker, final GameObject target, final int maxAngle) {
        final double angleChar;
        final double angleTarget;
        double angleDiff;
        final double maxAngleDiff;
        if (target == null) {
            return false;
        }
        maxAngleDiff = maxAngle / 2.0d;
        angleTarget = calculateAngleFrom(attacker, target);
        angleChar = convertHeadingToDegree(attacker.getHeading());
        angleDiff = angleChar - angleTarget;
        if (angleDiff <= -360 + maxAngleDiff) {
            angleDiff += 360;
        }
        if (angleDiff >= 360 - maxAngleDiff) {
            angleDiff -= 360;
        }
        if (Math.abs(angleDiff) <= maxAngleDiff) {
            return true;
        }
        return false;
    }

    public static int calculateHeadingFrom(final GameObject obj1, final GameObject obj2) {
        return calculateHeadingFrom(obj1.getX(), obj1.getY(), obj2.getX(), obj2.getY());
    }

    public static int calculateHeadingFrom(final int obj1X, final int obj1Y, final int obj2X, final int obj2Y) {
        double angleTarget = Math.toDegrees(Math.atan2(obj2Y - obj1Y, obj2X - obj1X));
        if (angleTarget < 0) {
            angleTarget = MAX_ANGLE + angleTarget;
        }
        return (int) (angleTarget * 182.044444444);
    }

    public static double calculateAngleFrom(final GameObject obj1, final GameObject obj2) {
        return calculateAngleFrom(obj1.getX(), obj1.getY(), obj2.getX(), obj2.getY());
    }

    public static double calculateAngleFrom(final int obj1X, final int obj1Y, final int obj2X, final int obj2Y) {
        double angleTarget = Math.toDegrees(Math.atan2(obj2Y - obj1Y, obj2X - obj1X));
        if (angleTarget < 0) {
            angleTarget = 360 + angleTarget;
        }
        return angleTarget;
    }

    public static boolean checkIfInRange(final int range, final int x1, final int y1, final int x2, final int y2) {
        return checkIfInRange(range, x1, y1, 0, x2, y2, 0, false);
    }

    public static boolean checkIfInRange(final int range, final int x1, final int y1, final int z1, final int x2, final int y2, final int z2, final boolean includeZAxis) {
        final long dx = x1 - x2;
        final long dy = y1 - y2;

        if (includeZAxis) {
            final long dz = z1 - z2;
            return dx * dx + dy * dy + dz * dz <= range * range;
        }
        return dx * dx + dy * dy <= range * range;
    }

    public static boolean checkIfInRange(final int range, final GameObject obj1, final GameObject obj2, final boolean includeZAxis) {
        if (obj1 == null || obj2 == null) {
            return false;
        }
        return checkIfInRange(range, obj1.getX(), obj1.getY(), obj1.getZ(), obj2.getX(), obj2.getY(), obj2.getZ(), includeZAxis);
    }

    public static double convertHeadingToDegree(final int heading) {
        return heading / 182.044444444;
    }

    public static double convertHeadingToRadian(final int heading) {
        return Math.toRadians(convertHeadingToDegree(heading) - 90);
    }

    public static int convertDegreeToClientHeading(double degree) {
        if (degree < 0) {
            degree = 360 + degree;
        }
        return (int) (degree * 182.044444444);
    }

    public static double calculateDistance(final int x1, final int y1, final int z1, final int x2, final int y2) {
        return calculateDistance(x1, y1, 0, x2, y2, 0, false);
    }

    public static double calculateDistance(final int x1, final int y1, final int z1, final int x2, final int y2, final int z2, final boolean includeZAxis) {
        final long dx = x1 - x2;
        final long dy = y1 - y2;

        if (includeZAxis) {
            final long dz = z1 - z2;
            return Math.sqrt(dx * dx + dy * dy + dz * dz);
        }
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static double calculateDistance(final GameObject obj1, final GameObject obj2, final boolean includeZAxis) {
        if (obj1 == null || obj2 == null) {
            return Integer.MAX_VALUE;
        }
        return calculateDistance(obj1.getX(), obj1.getY(), obj1.getZ(), obj2.getX(), obj2.getY(), obj2.getZ(), includeZAxis);
    }

    public static double getDistance(final GameObject a1, final GameObject a2) {
        return getDistance(a1.getX(), a2.getY(), a2.getX(), a2.getY());
    }

    public static double getDistance(final Location loc1, final Location loc2) {
        return getDistance(loc1.getX(), loc1.getY(), loc2.getX(), loc2.getY());
    }

    public static double getDistance(final int x1, final int y1, final int x2, final int y2) {
        return Math.hypot(x1 - x2, y1 - y2);
    }

    public static int getHeadingTo(final GameObject actor, final GameObject target) {
        if (actor == null || target == null || target == actor) {
            return -1;
        }
        return getHeadingTo(actor.getLoc(), target.getLoc());
    }

    public static int getHeadingTo(final Location actor, final Location target) {
        if (actor == null || target == null || target == actor) {
            return -1;
        }

        final int dx = target.x - actor.x;
        final int dy = target.y - actor.y;
        int heading = target.h - (int) (Math.atan2(-dy, -dx) * Creature.HEADINGS_IN_PI + 32768);

        if (heading < 0) {
            heading = heading + 1 + Integer.MAX_VALUE & 0xFFFF;
        } else if (heading > 0xFFFF) {
            heading &= 0xFFFF;
        }

        return heading;
    }

    public enum TargetDirection {
        NONE,
        FRONT,
        SIDE,
        BEHIND
    }
}
