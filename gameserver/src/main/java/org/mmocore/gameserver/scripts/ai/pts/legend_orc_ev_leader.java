package org.mmocore.gameserver.scripts.ai.pts;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.utils.NpcUtils;

/**
 * @author Mangol
 * @version PTS Freya
 * @npc_id 18815
 */
public class legend_orc_ev_leader extends Fighter {
    private static final int legend_orc_treasure = 18816;
    private static final int timer = 2 * 60 * 1000;

    public legend_orc_ev_leader(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtSpawn() {
        NpcInstance actor = getActor();
        if (actor == null) {
            return;
        }
        Functions.npcSay(actor, NpcString.THOSE_WHO_ARE_IN_FRONT_OF_MY_EYES_WILL_BE_DESTROYED);
        ThreadPoolManager.getInstance().schedule(new spawn_legend_orc_treasure(), timer);
        super.onEvtSpawn();
    }

    @Override
    protected void onEvtDead(Creature killer) {
        NpcInstance actor = getActor();
        Functions.npcSay(actor, NpcString.I_AM_TIRED_DO_NOT_WAKE_ME_UP_AGAIN);
        super.onEvtDead(killer);
    }

    private class spawn_legend_orc_treasure extends RunnableImpl {
        @Override
        public void runImpl() {
            NpcInstance actor = getActor();
            if (actor == null)
                return;
            NpcUtils.spawnSingle(legend_orc_treasure, actor.getLoc());
            actor.deleteMe();
        }
    }
}
