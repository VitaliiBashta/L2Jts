package org.mmocore.gameserver.scripts.ai.freya;

import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.Mystic;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.scripts.bosses.ValakasManager;

/**
 * @author pchayka
 */

public class ValakasMinion extends Mystic {
    public ValakasMinion(NpcInstance actor) {
        super(actor);
        actor.startImmobilized();
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        for (Player p : ValakasManager.getZone().getInsidePlayers()) {
            notifyEvent(CtrlEvent.EVT_AGGRESSION, p, 5000);
        }
    }
}