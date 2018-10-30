package org.mmocore.gameserver.scripts.ai.fantasy_island;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.serverpackets.SocialAction;

/**
 * @author PaInKiLlEr - AI для Индивидуальных монстров (32439, 32440, 32441). - Показывает социалку. - AI проверен и работает.
 */
public class MCIndividual extends DefaultAI {
    public MCIndividual(final NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtSpawn() {
        final NpcInstance actor = getActor();
        if (actor == null) {
            return;
        }

        ThreadPoolManager.getInstance().schedule(new ScheduleSocial(), 1000);
        super.onEvtSpawn();
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }

    @Override
    protected boolean randomAnimation() {
        return false;
    }

    private class ScheduleSocial extends RunnableImpl {
        @Override
        public void runImpl() {
            final NpcInstance actor = getActor();
            actor.broadcastPacket(new SocialAction(actor.getObjectId(), 1));
        }
    }
}