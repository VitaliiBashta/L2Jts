package org.mmocore.gameserver.handler.voicecommands.impl;

import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.handler.voicecommands.IVoicedCommandHandler;
import org.mmocore.gameserver.object.Player;

/**
 * @author KilRoy
 */
public class AutoLoot implements IVoicedCommandHandler {
    private final String[] _commandList = {"autoloot", "autolooth"};

    @Override
    public String[] getVoicedCommandList() {
        return _commandList;
    }

    @Override
    public boolean useVoicedCommand(final String command, final Player player, final String args) {
        if (AllSettingsConfig.AUTO_LOOT_INDIVIDUAL) {
            if (AllSettingsConfig.AUTO_LOOT_INDIVIDUAL_ONLY_FOR_PREMIUM && !player.getPremiumAccountComponent().hasBonus()) {
                player.sendAdminMessage(!player.isLangRus() ? "Allowed only for premium account.)." : "Доступно только для премиум аккаунтов.");
                player.sendActionFailed();
                return true;
            }

            if ("autoloot".equals(command)) {
                if (player.isAutoLootEnabled() == AutoLootCfg.ENABLED_AUTOLOOT.ordinal()) {
                    player.setAutoLoot(0);
                    player.sendAdminMessage(!player.isLangRus() ? "AutoLoot disabled." : "Автолут деактивирован.");
                } else if (player.isAutoLootEnabled() == AutoLootCfg.DISABLED_AUTOLOOT.ordinal()) {
                    player.setAutoLoot(1);
                    player.sendAdminMessage(!player.isLangRus() ? "AutoLoot enabled. For disable - .autoloot again." : "Автолут активирован. Для отключения введите .autoloot повторно.");
                }
            }

            if ("autolooth".equals(command)) {
                if (player.isAutoLootHerbsEnabled()) {
                    player.setAutoLootHerbs(false);
                    player.sendAdminMessage(!player.isLangRus() ? "AutoLoot(herb's) disabled." : "Автолут(хербы) деактивирован.");
                } else if (!player.isAutoLootHerbsEnabled()) {
                    player.setAutoLootHerbs(true);
                    player.sendAdminMessage(!player.isLangRus() ? "AutoLoot(herb's) enabled. For disable - .autolooth again." : "Автолут(Хербы) активирован. Для отключения введите .autolooth повторно.");
                }
            }
        } else
            player.sendAdminMessage(!player.isLangRus() ? "AutoLoot is now disabled. Additional information on project forum, or Administration." : "Индивидуальный автолут отключен. Дополнительная информация на форуме проекта, либо у Администрации.");
        return true;
    }

    private enum AutoLootCfg {
        DISABLED_AUTOLOOT,
        ENABLED_AUTOLOOT
    }
}