package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.GameObject;

/**
 * sample
 * 06 8f19904b 2522d04b 00000000 80 950c0000 4af50000 08f2ffff 0000    - 0 damage (missed 0x80)
 * 06 85071048 bc0e504b 32000000 10 fc41ffff fd240200 a6f5ffff 0100 bc0e504b 33000000 10                                     3....
 * <p/>
 * format
 * dddc dddh (ddc)
 */
public class Attack extends GameServerPacket {
    private static final Hit[] EMPTY_HITS_ARRAY = new Hit[0];
    public final int attackerId;
    public final boolean soulshot;
    private final int grade;
    private final int x, y, z, tx, ty, tz;
    private Hit[] hits;
    public Attack(final Creature attacker, final Creature target, final boolean ss, final int grade) {
        attackerId = attacker.getObjectId();
        soulshot = ss;
        this.grade = grade;
        x = attacker.getX();
        y = attacker.getY();
        z = attacker.getZ();
        tx = target.getX();
        ty = target.getY();
        tz = target.getZ();
        hits = EMPTY_HITS_ARRAY;
    }

    /**
     * Add this hit (target, damage, miss, critical, shield) to the Server-Client packet Attack.<BR><BR>
     */
    public void addHit(final GameObject target, final int damage, final boolean miss, final boolean crit, final boolean shld) {
        // Get the last position in the hits table
        final int pos = hits.length;

        // Create a new Hit object
        final Hit[] tmp = new Hit[pos + 1];

        // Add the new Hit object to hits table
        System.arraycopy(hits, 0, tmp, 0, hits.length);
        tmp[pos] = new Hit(target, damage, miss, crit, shld);
        hits = tmp;
    }

    /**
     * Return True if the Server-Client packet Attack conatins at least 1 hit.<BR><BR>
     */
    public boolean hasHits() {
        return hits.length > 0;
    }

    @Override
    protected final void writeData() {
        writeD(attackerId);
        writeD(hits[0].targetId);
        writeD(hits[0].damage);
        writeC(hits[0].flags);
        writeD(x);
        writeD(y);
        writeD(z);
        writeH(hits.length - 1);
        for (int i = 1; i < hits.length; i++) {
            writeD(hits[i].targetId);
            writeD(hits[i].damage);
            writeC(hits[i].flags);
        }
        writeD(tx);
        writeD(ty);
        writeD(tz);
    }

    private class Hit {
        final int targetId;
        final int damage;
        int flags;

        Hit(final GameObject target, final int damage, final boolean miss, final boolean crit, final boolean shld) {
            targetId = target.getObjectId();
            this.damage = damage;
            if (soulshot) {
                flags |= 0x10 | grade;
            }
            if (crit) {
                flags |= 0x20;
            }
            if (shld) {
                flags |= 0x40;
            }
            if (miss) {
                flags |= 0x80;
            }
        }
    }
}