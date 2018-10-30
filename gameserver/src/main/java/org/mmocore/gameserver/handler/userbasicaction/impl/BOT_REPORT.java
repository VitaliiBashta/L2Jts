package org.mmocore.gameserver.handler.userbasicaction.impl;

import org.mmocore.gameserver.configuration.config.ExtConfig;
import org.mmocore.gameserver.handler.userbasicaction.IUserBasicActionHandler;
import org.mmocore.gameserver.manager.BotReportManager;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;

import java.util.Optional;
import java.util.OptionalInt;

/**
 * Create by Mangol on 20.10.2015.
 */
public class BOT_REPORT implements IUserBasicActionHandler {
    @Override
    public void useAction(final Player player, final int id, final Optional<String> option, final OptionalInt useSkill, final Optional<GameObject> target, final boolean ctrlPressed, final boolean shiftPressed) {
        if (player.isOutOfControl() || player.isActionsDisabled()) {
            player.sendActionFailed();
            return;
        }
        if (ExtConfig.EX_ENABLE_AUTO_HUNTING_REPORT) {
            if (target.isPresent() && target.get().isPlayer()) {
                final Player reported = (Player) target.get();
                if (!BotReportManager.getInstance().validateBot(reported, player)) {
                    return;
                }
                if (!BotReportManager.getInstance().validateReport(player, reported.getName())) {
                    return;
                }
                try {
                    BotReportManager.getInstance().reportBot(reported, player);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            player.sendMessage("Action disabled.");
        }
    }
}
