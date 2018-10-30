package org.mmocore.gameserver.scripts.ai.pts.tower_of_insolence;

import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.utils.NpcUtils;

/**
 * @author Mangol
 */
public class kernon extends Fighter {

    public kernon(final NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtDead(final Creature killer) {
        int chest_of_kernon = 31028;
        NpcUtils.spawnSingle(chest_of_kernon, getActor().getX(), getActor().getY(), getActor().getZ());
        super.onEvtDead(killer);
    }
}
