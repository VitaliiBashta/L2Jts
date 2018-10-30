package org.mmocore.gameserver.scripts.ai.monas;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.geoengine.GeoEngine;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.world.World;

/**
 * @author PaInKiLlEr
 * AI для моба Ward of the Grail (18929).
 * Постоянно передвигаются.
 * Если обнаружил игрока в пределах радиуса, использует скил6326.
 * AI проверен и работает.
 */
public class WardGrail extends DefaultAI {
    public WardGrail(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor == null || actor.isDead())
            return true;

        for (Playable target : World.getAroundPlayables(actor, 100, 100)) {
            if (target != null && GeoEngine.canSeeTarget(actor, target, actor.isFlying())) {
                actor.doCast(SkillTable.getInstance().getSkillEntry(6326, 1), target, true);
                actor.deleteMe();
            }
        }

        return super.thinkActive();
    }

    @Override
    protected boolean randomWalk() {
        NpcInstance actor = getActor();

        if (actor == null)
            return false;

        Location sloc = actor.getSpawnedLoc();

        int x = sloc.x + Rnd.get(2 * 10000) - 10000;
        int y = sloc.y + Rnd.get(2 * 10000) - 10000;
        int z = GeoEngine.getHeight(x, y, sloc.z, actor.getGeoIndex());

        actor.moveToLocation(x, y, z, 0, true);

        return true;
    }
}