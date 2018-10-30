package org.mmocore.gameserver.scripts.ai.hellbound;

import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.geoengine.GeoEngine;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.Location;

public class FoundryWorker extends Fighter {
    public FoundryWorker(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        NpcInstance actor = getActor();
        if (attacker != null) {
            Location pos = Location.findPointToStay(actor, 150, 250);
            if (GeoEngine.canMoveToCoord(attacker.getX(), attacker.getY(), attacker.getZ(), pos.x, pos.y, pos.z, actor.getGeoIndex())) {
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