package org.mmocore.gameserver.model.entity.events.impl.reflection;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;

/**
 * @author Mangol
 * @since 05.10.2016
 */
public class ResurrectTask extends RunnableImpl {
    private final Player player;
    private final RBaseController controller;

    public ResurrectTask(Player player, RBaseController controller) {
        this.player = player;
        this.controller = controller;
    }

    @Override
    public void runImpl() throws Exception {
        if (controller == null || controller.getEvent() == null)
            return;
        if (controller.getEvent().getEventState().ordinal() > 2 && controller.getEvent().getEventState().ordinal() < 5) {
            if (controller.getPlayers().stream().anyMatch(p -> p.getPlayer().getObjectId() == player.getObjectId())) {
                cleanse(player);
                player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
                player.setCurrentCp(player.getMaxCp());
                player.setUndying(true);
                player.teleToLocation(controller.getBaseLocation(), controller.getEvent().getReflection());
                player.stopFrozen();
            }
            controller.getEvent().getDeathMap().remove(player.getObjectId());
        }
    }

    public void sendMessage() {
        player.sendPacket(new SystemMessage(SystemMsg.RESURRECTION_WILL_TAKE_PLACE_IN_THE_WAITING_ROOM_AFTER_S1_SECONDS).addNumber(15));
    }

    private void cleanse(Player player) {
        if (player == null)
            return;
        player.getEffectList().getAllEffects().stream().filter(Effect::isOffensive).forEach(Effect::exit);
    }
}