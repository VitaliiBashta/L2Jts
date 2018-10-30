package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

import java.util.ArrayList;
import java.util.List;

/**
 * sample
 * <p/>
 * 0000: 85 02 00 10 04 00 00 01 00 4b 02 00 00 2c 04 00    .........K...,..
 * 0010: 00 01 00 58 02 00 00                               ...X...
 * <p/>
 * <p/>
 * format   h (dhd)
 *
 * @version $Revision: 1.3.2.1.2.6 $ $Date: 2005/04/05 19:41:08 $
 */
public class AbnormalStatusUpdate extends GameServerPacket {
    public static final int INFINITIVE_EFFECT = -1;
    private final List<Effect> effects;

    public AbnormalStatusUpdate() {
        effects = new ArrayList<>();
    }

    public void addEffect(final int skillId, final int dat, final int duration) {
        effects.add(new Effect(skillId, dat, duration));
    }

    @Override
    protected final void writeData() {
        writeH(effects.size());
        for (final Effect temp : effects) {
            writeD(temp.skillId);
            writeH(temp.dat);
            writeD(temp.duration);
        }
    }

    static class Effect {
        final int skillId;
        final int dat;
        final int duration;

        Effect(final int skillId, final int dat, final int duration) {
            this.skillId = skillId;
            this.dat = dat;
            this.duration = duration;
        }
    }
}