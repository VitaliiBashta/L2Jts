package org.mmocore.gameserver.scripts.ai;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.configuration.config.AiConfig;
import org.mmocore.gameserver.geoengine.GeoEngine;
import org.mmocore.gameserver.model.Territory;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.serverpackets.MagicSkillUse;
import org.mmocore.gameserver.templates.spawn.SpawnRange;
import org.mmocore.gameserver.utils.Location;

/**
 * Моб использует телепортацию вместо рандом валка.
 *
 * @author SYS
 */
public class RndTeleportFighter extends Fighter {
    private long _lastTeleport;

    public RndTeleportFighter(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected boolean maybeMoveToHome() {
        NpcInstance actor = getActor();
        if (System.currentTimeMillis() - _lastTeleport < 10000) {
            return false;
        }

        boolean randomWalk = actor.hasRandomWalk();
        Location sloc = actor.getSpawnedLoc();
        if (sloc == null) {
            return false;
        }

        // Random walk or not?
        if (randomWalk && (!AiConfig.RND_WALK || Rnd.chance(AiConfig.RND_WALK_RATE))) {
            return false;
        }

        if (!randomWalk && actor.isInRangeZ(sloc, AiConfig.MAX_DRIFT_RANGE)) {
            return false;
        }

        int x = sloc.x + Rnd.get(-AiConfig.MAX_DRIFT_RANGE, AiConfig.MAX_DRIFT_RANGE);
        int y = sloc.y + Rnd.get(-AiConfig.MAX_DRIFT_RANGE, AiConfig.MAX_DRIFT_RANGE);
        int z = GeoEngine.getHeight(x, y, sloc.z, actor.getGeoIndex());

        if (sloc.z - z > 64) {
            return false;
        }

        SpawnRange spawnRange = actor.getSpawnRange();
        boolean isInside = true;
        if (spawnRange != null && spawnRange instanceof Territory) {
            isInside = ((Territory) spawnRange).isInside(x, y);
        }

        if (isInside) {
            actor.broadcastPacketToOthers(new MagicSkillUse(actor, actor, 4671, 1, 500, 0));
            ThreadPoolManager.getInstance().schedule(new Teleport(new Location(x, y, z)), 500);
            _lastTeleport = System.currentTimeMillis();
        }
        return isInside;
    }
}