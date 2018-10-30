package org.mmocore.gameserver.model.entity.events.impl.reflection.listener;

import org.mmocore.gameserver.listener.actor.OnDeathListener;
import org.mmocore.gameserver.model.entity.events.impl.ReflectionEvent;
import org.mmocore.gameserver.model.entity.events.listener.AbstractEventListener;
import org.mmocore.gameserver.object.Creature;

/**
 * @author Mangol
 * @since 02.05.2016
 */
public class RDeathListener<T extends ReflectionEvent> extends AbstractEventListener<T> implements OnDeathListener {
    public RDeathListener(T event) {
        super(event);
    }

    @Override
    public void onDeath(Creature actor, Creature killer) {
		/*if(actor == null || killer == null) {
			return;
		}
		if(getEvent().getEventState() == REventState.RAID_BOSS) {
			if(actor.isPlayer()) {
				final Player player = actor.getPlayer();
				final RTeamType teamType = player.getEventComponent().getTeam(ReflectionEvent.class);
				if(teamType != null) {
					final RBaseController controllerActor = getEvent().getBaseControllerMap().get(teamType);
					if(controllerActor != null && actor.isPlayer() && killer.isMonster())
						getEvent().addDeathPlayer(player, controllerActor);
				}
			}
		}
		else if(getEvent().getEventState() == REventState.PROCESS) {
			if(actor.isPlayer()) {
				final Player player = actor.getPlayer();
				final RTeamType playerType = player.getEventComponent().getTeam(ReflectionEvent.class);
				if(playerType == null)
					return;
				final RBaseController controllerActor = getEvent().getBaseController(playerType);
				if(controllerActor == null)
					return;
				if(killer.isPlayable()) {
					final Player killers = killer.getPlayer();
					final RTeamType killerType = killers.getEventComponent().getTeam(ReflectionEvent.class);
					if(killerType == null)
						return;
					final RBaseController controllerKiller = getEvent().getBaseController(killerType);
					if(controllerKiller == null)
						return;
					if(playerType != killerType) {
						final int point = controllerKiller.addAndGet(getEvent().getKillPoint());
						controllerKiller.addPlayerPoint(killers, getEvent().getKillPoint());
						killers.sendPacket(new ExShowScreenMessage(new CustomMessage("event.r.newCount").addNumber(Math.max(0, getEvent().getMaxPoint() - point)).toString(killers), 1500, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false));
						getEvent().checkWinner();
					}
				}
				getEvent().addDeathPlayer(player, controllerActor);
			}
		}*/
    }
}
