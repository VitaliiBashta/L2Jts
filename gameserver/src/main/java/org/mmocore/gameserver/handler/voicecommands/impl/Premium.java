package org.mmocore.gameserver.handler.voicecommands.impl;

import org.mmocore.gameserver.handler.voicecommands.IVoicedCommandHandler;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.TimeUtils;

import java.time.Instant;

/**
 * @author KilRoy
 */
public class Premium implements IVoicedCommandHandler {
    private final String[] commandList = {"premium", "menu"};

    @Override
    public String[] getVoicedCommandList() {
        return commandList;
    }

    @Override
    public boolean useVoicedCommand(final String command, final Player player, final String args) {
        if ("premium".equals(command)) {
            if (player.getPremiumAccountComponent().hasBonus()) {
                final Instant instant = Instant.ofEpochMilli(player.getPremiumAccountComponent().getPremiumBonus().getBonusExpire() * 1000L);
                final String bonusEnd = (player.isLangRus() ? "Дата окончания подписки премиум аккаунта: "
                        : "End date of subscription a premium account: ") + TimeUtils.dateTimeFormat(instant);
                player.sendMessage(bonusEnd);
            } else
                player.sendMessage(player.isLangRus() ? "Вы не имеете подписки на премиум аккаунт." : "You do not have a subscription to a premium account.");
            player.sendMessage((player.isLangRus() ? "Ваше колличество премиум очков: " : "Your amount of premium points: ") + player.getPremiumAccountComponent().getPremiumPoints());
            return true;
        } else if ("menu".equals(command)) {
            final HtmlMessage html = new HtmlMessage(5);
            html.setFile("command/menu.htm");
            html.replace("%PremiumPoints%", String.valueOf(player.getPremiumAccountComponent().getPremiumPoints()));
            player.sendPacket(html);
        }
        return true;
    }
}