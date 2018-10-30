package org.mmocore.gameserver.scripts.ai.pts.the_cemetery;

import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.utils.NpcUtils;

/**
 * @author Mangol
 */
public class domb_death_cabrio extends Fighter {

    public domb_death_cabrio(final NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtDead(final Creature killer) {
        int coffer_of_the_dead = 31027;
        NpcUtils.spawnSingle(coffer_of_the_dead, getActor().getX(), getActor().getY(), getActor().getZ());
        super.onEvtDead(killer);
    }
}
