package org.mmocore.gameserver.object.components.creatures.recorders;

import org.mmocore.gameserver.object.Servitor;

/**
 * @author G1ta0
 */
public class SummonStatsChangeRecorder extends CharStatsChangeRecorder<Servitor> {
    public SummonStatsChangeRecorder(Servitor actor) {
        super(actor);
    }

    @Override
    protected void onSendChanges() {
        super.onSendChanges();

        if ((_changes & BROADCAST_CHAR_INFO) == BROADCAST_CHAR_INFO) {
            _activeChar.broadcastCharInfo();
        } else if ((_changes & SEND_CHAR_INFO) == SEND_CHAR_INFO) {
            _activeChar.sendPetInfo();
        }
    }
}
