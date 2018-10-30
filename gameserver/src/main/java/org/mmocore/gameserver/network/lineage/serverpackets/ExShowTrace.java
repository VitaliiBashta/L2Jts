package org.mmocore.gameserver.network.lineage.serverpackets;


import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.utils.Location;

import java.util.ArrayList;
import java.util.List;


public class ExShowTrace extends GameServerPacket {
    private final List<Trace> traces = new ArrayList<>();

    public void addTrace(final int x, final int y, final int z, final int time) {
        traces.add(new Trace(x, y, z, time));
    }

    public void addLine(final Location from, final Location to, final int step, final int time) {
        addLine(from.x, from.y, from.z, to.x, to.y, to.z, step, time);
    }

    public void addLine(final int from_x, final int from_y, final int from_z, final int to_x, final int to_y, final int to_z, final int step, final int time) {
        final int x_diff = to_x - from_x;
        final int y_diff = to_y - from_y;
        final int z_diff = to_z - from_z;
        final double xy_dist = Math.sqrt(x_diff * x_diff + y_diff * y_diff);
        final double full_dist = Math.sqrt(xy_dist * xy_dist + z_diff * z_diff);
        final int steps = (int) (full_dist / step);

        addTrace(from_x, from_y, from_z, time);
        if (steps > 1) {
            final int step_x = x_diff / steps;
            final int step_y = y_diff / steps;
            final int step_z = z_diff / steps;

            for (int i = 1; i < steps; i++) {
                addTrace(from_x + step_x * i, from_y + step_y * i, from_z + step_z * i, time);
            }
        }
        addTrace(to_x, to_y, to_z, time);
    }

    public void addTrace(final GameObject obj, final int time) {
        this.addTrace(obj.getX(), obj.getY(), obj.getZ(), time);
    }

    @Override
    protected final void writeData() {
        writeH(traces.size());
        for (final Trace t : traces) {
            writeD(t.x);
            writeD(t.y);
            writeD(t.z);
            writeH(t.time);
        }
    }

    static final class Trace {
        public final int x;
        public final int y;
        public final int z;
        public final int time;

        public Trace(final int x, final int y, final int z, final int time) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.time = time;
        }
    }
}