package org.mmocore.gameserver.scripts.ai.hellbound;

import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.manager.naia.NaiaCoreManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;

/**
 * @author pchayka
 */
public class Epidos extends Fighter {

    public Epidos(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtDead(Creature killer) {
        NaiaCoreManager.removeSporesAndSpawnCube();
        super.onEvtDead(killer);
    }
}