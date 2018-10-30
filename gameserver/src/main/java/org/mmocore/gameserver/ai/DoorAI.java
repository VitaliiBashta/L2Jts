package org.mmocore.gameserver.ai;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.entity.events.impl.SiegeEvent;
import org.mmocore.gameserver.model.instances.DoorInstance;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;

public class DoorAI extends CharacterAI {
    public DoorAI(final DoorInstance actor) {
        super(actor);
    }

    @Override
    public void notifyEvent(CtrlEvent evt, Object[] args) {
        Creature actor = getActor();
        if (actor == null || !actor.isVisible())
            return;

        super.notifyEvent(evt, args);

        switch (evt) {
            case EVT_DBLCLICK:
                onEvtTwiceClick((Player) args[0]);
                break;
            case EVT_OPEN:
                onEvtOpen((Player) args[0]);
                break;
            case EVT_CLOSE:
                onEvtClose((Player) args[0]);
                break;
        }
    }

    protected void onEvtTwiceClick(final Player player) {
        //
    }

    protected void onEvtOpen(final Player player) {
        //
    }

    protected void onEvtClose(final Player player) {
        //
    }

    @Override
    public DoorInstance getActor() {
        return (DoorInstance) super.getActor();
    }

    //TODO [VISTALL] унести в SiegeDoor
    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        final Creature actor;
        if (attacker == null || (actor = getActor()) == null) {
            return;
        }

        final Player player = attacker.getPlayer();
        if (player == null) {
            return;
        }

        final SiegeEvent<?, ?> siegeEvent1 = player.getEvent(SiegeEvent.class);
        final SiegeEvent<?, ?> siegeEvent2 = actor.getEvent(SiegeEvent.class);

        if (siegeEvent1 == null || siegeEvent1 == siegeEvent2 && siegeEvent1.getSiegeClan(SiegeEvent.ATTACKERS, player.getClan()) != null) {
            for (final NpcInstance npc : actor.getAroundNpc(900, 200)) {
                if (!npc.isSiegeGuard()) {
                    continue;
                }

                if (Rnd.chance(20)) {
                    npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 10000);
                } else {
                    npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 2000);
                }
            }
        }
    }
}