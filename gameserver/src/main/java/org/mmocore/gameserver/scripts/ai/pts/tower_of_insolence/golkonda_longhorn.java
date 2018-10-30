package org.mmocore.gameserver.scripts.ai.pts.tower_of_insolence;

import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.utils.NpcUtils;

/**
 * @author Mangol
 */
public class golkonda_longhorn extends Fighter {

    public golkonda_longhorn(final NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtDead(final Creature killer) {
        int chest_of_golkonda = 31029;
        NpcUtils.spawnSingle(chest_of_golkonda, getActor().getX(), getActor().getY(), getActor().getZ());
        super.onEvtDead(killer);
    }
}
