package org.mmocore.gameserver.object.components.creatures.recorders;

import org.mmocore.gameserver.model.instances.NpcInstance;

/**
 * @author G1ta0
 */
public class NpcStatsChangeRecorder extends CharStatsChangeRecorder<NpcInstance> {
    public NpcStatsChangeRecorder(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onSendChanges() {
        super.onSendChanges();

        if ((_changes & BROADCAST_CHAR_INFO) == BROADCAST_CHAR_INFO) {
            _activeChar.broadcastCharInfo();
        }
    }
}
