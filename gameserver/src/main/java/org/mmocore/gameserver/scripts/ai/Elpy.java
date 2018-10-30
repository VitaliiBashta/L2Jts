package org.mmocore.gameserver.scripts.ai;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.geoengine.GeoEngine;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.Location;

public class Elpy extends Fighter {
    public Elpy(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        NpcInstance actor = getActor();
        if (attacker != null && Rnd.chance(50)) {
            Location pos = Location.findPointToStay(actor, 150, 200);
            if (GeoEngine.canMoveToCoord(actor.getX(), actor.getY(), actor.getZ(), pos.x, pos.y, pos.z, actor.getGeoIndex())) {
                actor.setRunning();
                addTaskMove(pos, false);
            }
        }
    }

    @Override
    public boolean checkAggression(Creature target) {
        return false;
    }

    @Override
    protected void onEvtAggression(Creature target, int aggro) {

    }
}