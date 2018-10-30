package org.mmocore.gameserver.model.entity.events.impl.reflection.listener;

import org.mmocore.gameserver.listener.zone.OnZoneEnterLeaveListener;
import org.mmocore.gameserver.model.entity.events.impl.ReflectionEvent;
import org.mmocore.gameserver.model.entity.events.impl.reflection.RBaseController;
import org.mmocore.gameserver.model.entity.events.impl.reflection.REventState;
import org.mmocore.gameserver.model.entity.events.impl.reflection.RTeamType;
import org.mmocore.gameserver.model.entity.events.impl.reflection.object.RFlagObject;
import org.mmocore.gameserver.model.entity.events.listener.AbstractEventListener;
import org.mmocore.gameserver.model.zone.Zone;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.attachments.FlagItemAttachment;

/**
 * @author Mangol
 * @since 02.05.2016
 */
public class RZoneEnterListener<T extends ReflectionEvent> extends AbstractEventListener<T> implements OnZoneEnterLeaveListener {
    private final RBaseController baseController;

    public RZoneEnterListener(T event, final RBaseController baseController) {
        super(event);
        this.baseController = baseController;
    }

    @Override
    public void onZoneEnter(final Zone zone, final Creature actor) {
        if (!actor.isPlayer()) {
            return;
        }
        final Player player = actor.getPlayer();
        if (getEvent().getEventState() == REventState.RAID_BOSS) {
            return;
        }
        final FlagItemAttachment flag = player.getActiveWeaponFlagAttachment();
        if (flag != null) {
            if (flag instanceof RFlagObject) {
                final RFlagObject flagObject = (RFlagObject) flag;
                final RTeamType teamType = player.getEventComponent().getTeam(ReflectionEvent.class);
                if (teamType == null)
                    return;
                if (baseController.getTeamType() != teamType) {
                    return;
                }
                final int point = baseController.addAndGet(getEvent().getStealFlagPoint());
                baseController.addPlayerPoint(player, getEvent().getStealFlagPoint());
                flagObject.goToBase(player);
                getEvent().getBaseControllerMap().entrySet().stream().filter(r -> r.getKey() != teamType).
                        forEach(b -> b.getValue().getPlayers().stream().filter(p -> p != null && p.getPlayer() != null).
                                forEach(p -> p.getPlayer().sendPacket(new ExShowScreenMessage(new CustomMessage("event.r.captureFlag").addString(teamType.getNameTeam()).toString(p.getPlayer()), 1500, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false))));
                player.sendPacket(new ExShowScreenMessage(new CustomMessage("event.r.newCount").addNumber(Math.max(0, getEvent().getMaxPoint() - point)).toString(player), 1500, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false));
                getEvent().checkWinner();
            }
        }
    }

    @Override
    public void onZoneLeave(Zone zone, Creature actor) {

    }
}
