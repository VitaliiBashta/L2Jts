package org.mmocore.gameserver.scripts.ai.freya;

import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;

/**
 * @author pchayka
 */

public class IceCastleBreath extends Fighter {
    public IceCastleBreath(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        Reflection r = getActor().getReflection();
        if (r != null && r.getPlayers() != null) {
            for (Player p : r.getPlayers()) {
                this.notifyEvent(CtrlEvent.EVT_AGGRESSION, p, 5);
            }
        }
    }

    @Override
    protected void teleportHome() {
        return;
    }
}