package org.mmocore.gameserver.scripts.ai.freya;

import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Player;

/**
 * @author pchayka
 */
public class FreyaQuest extends Fighter {
    public FreyaQuest(NpcInstance actor) {
        super(actor);
        MAX_PURSUE_RANGE = Integer.MAX_VALUE;
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();

        Reflection r = getActor().getReflection();
        for (Player p : r.getPlayers()) {
            this.notifyEvent(CtrlEvent.EVT_ATTACKED, p, null, 300);
        }

        Functions.npcSay(getActor(), NpcString.FREYA_HAS_STARTED_TO_MOVE);
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }

    @Override
    protected void returnHome(boolean teleport) {
        //
    }
}