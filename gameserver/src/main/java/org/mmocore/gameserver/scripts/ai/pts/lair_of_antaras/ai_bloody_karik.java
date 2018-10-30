package org.mmocore.gameserver.scripts.ai.pts.lair_of_antaras;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.utils.NpcUtils;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * @author KilRoy
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class ai_bloody_karik extends Fighter {
    public ai_bloody_karik(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtDead(Creature killer) {
        final NpcInstance actor = getActor();
        if (killer != null && actor != null) {
            if (Rnd.get(15) < 1) {
                NpcUtils.spawnSingleStablePoint(actor.getNpcId(), actor.getX() + 30, actor.getY() + 10, actor.getZ());
                NpcUtils.spawnSingleStablePoint(actor.getNpcId(), actor.getX() + 30, actor.getY() - 10, actor.getZ());
                NpcUtils.spawnSingleStablePoint(actor.getNpcId(), actor.getX() + 30, actor.getY() + 30, actor.getZ());
                NpcUtils.spawnSingleStablePoint(actor.getNpcId(), actor.getX() + 30, actor.getY() - 30, actor.getZ());
                NpcUtils.spawnSingleStablePoint(actor.getNpcId(), actor.getX() + 30, actor.getY() - 50, actor.getZ());
            }
        }
        super.onEvtDead(killer);
    }
}