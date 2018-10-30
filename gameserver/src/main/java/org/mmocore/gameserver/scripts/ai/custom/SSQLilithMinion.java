package org.mmocore.gameserver.scripts.ai.custom;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;

import java.util.List;

/**
 * @author pchayka
 */
public class SSQLilithMinion extends Fighter {
    private final int[] _enemies = {32719, 32720, 32721};

    public SSQLilithMinion(NpcInstance actor) {
        super(actor);
        actor.setHasChatWindow(false);
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        ThreadPoolManager.getInstance().schedule(new Attack(), 3000);
    }

    private NpcInstance getEnemy() {
        List<NpcInstance> around = getActor().getAroundNpc(1000, 300);
        if (around != null && !around.isEmpty()) {
            for (NpcInstance npc : around) {
                if (ArrayUtils.contains(_enemies, npc.getNpcId())) {
                    return npc;
                }
            }
        }
        return null;
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }

    public class Attack extends RunnableImpl {
        @Override
        public void runImpl() {
            if (getEnemy() != null) {
                getActor().getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, getEnemy(), null, 10000000);
            }
        }
    }
}