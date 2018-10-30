package org.mmocore.gameserver.scripts.ai.residences.fortress.siege;

import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.entity.events.impl.FortressSiegeEvent;
import org.mmocore.gameserver.model.entity.residence.Fortress;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.scripts.npc.model.residences.fortress.siege.MercenaryCaptionInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.Location;

import java.util.Collections;
import java.util.List;

/**
 * @author VISTALL
 * @date 10:58/19.04.2011
 */
public class MercenaryCaption extends Fighter {
    private List<Location> _points = Collections.emptyList();
    private int _tick = -1;

    public MercenaryCaption(NpcInstance actor) {
        super(actor);
        MAX_PURSUE_RANGE = 100;
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();
        NpcInstance actor = getActor();

        Fortress f = actor.getFortress();
        FortressSiegeEvent event = f.getSiegeEvent();

        _points = event.getObjects(FortressSiegeEvent.MERCENARY_POINTS);
    }

    @Override
    protected boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor.isActionsDisabled()) {
            return true;
        }

        if (_def_think) {
            if (doTask()) {
                clearTasks();
            }
            return true;
        }

        if (randomWalk()) {
            return true;
        }

        return false;
    }

    @Override
    public void onEvtArrived() {
        if (_tick != -1) {
            startMove(false);
        }
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        _tick = -1;
        super.onEvtAttacked(attacker, skill, damage);
    }

    public void startMove(boolean init) {
        if (init) {
            _tick = 0;
        }

        if (_tick == -1) {
            return;
        }

        if (_tick < _points.size()) {
            addTaskMove(_points.get(_tick++), true);
            doTask();
        }
    }

    @Override
    public MercenaryCaptionInstance getActor() {
        return (MercenaryCaptionInstance) super.getActor();
    }
}
