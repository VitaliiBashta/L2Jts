package org.mmocore.gameserver.scripts.ai.pts.tower_of_insolence;

import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.utils.NpcUtils;

/**
 * @author Mangol
 */
public class hallate_the_death_lord extends Fighter {
    private final int chest_of_hallate = 31030;

    public hallate_the_death_lord(final NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtDead(final Creature killer) {
        NpcUtils.spawnSingle(chest_of_hallate, getActor().getX(), getActor().getY(), getActor().getZ());
        super.onEvtDead(killer);
    }
}
