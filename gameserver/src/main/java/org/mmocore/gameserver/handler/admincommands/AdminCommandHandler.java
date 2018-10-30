package org.mmocore.gameserver.handler.admincommands;

import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.commons.utils.ReflectionUtils;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AdminCommandHandler extends AbstractHolder {
    private static final AdminCommandHandler _instance = new AdminCommandHandler();
    private final Map<String, IAdminCommandHandler> _datatable = new HashMap<>();

    private AdminCommandHandler() {
        ReflectionUtils.loadClassesWithConsumer("org.mmocore.gameserver.handler.admincommands.impl", IAdminCommandHandler.class,
                this::registerAdminCommandHandler);
    }

    public static AdminCommandHandler getInstance() {
        return _instance;
    }

    public void registerAdminCommandHandler(final IAdminCommandHandler handler) {
        if (handler.getAdminCommandEnum() != null) {
            for (final Enum<?> e : handler.getAdminCommandEnum()) {
                _datatable.put(e.toString().toLowerCase(), handler);
            }
        } else if (handler.getAdminCommandString() != null) {
            for (final String command : handler.getAdminCommandString()) {
                _datatable.put(command, handler);
            }
        }
    }

    public IAdminCommandHandler getAdminCommandHandler(final String adminCommand) {
        String command = adminCommand;
        if (adminCommand.contains(" ")) {
            command = adminCommand.substring(0, adminCommand.indexOf(' '));
        }
        return _datatable.get(command);
    }

    public void useAdminCommandHandler(final Player activeChar, final String adminCommand) {
        if (!(activeChar.isGM() || activeChar.getPlayerAccess().CanUseGMCommand)) {
            activeChar.sendMessage(new CustomMessage("org.mmocore.gameserver.clientpackets.SendBypassBuildCmd.NoCommandOrAccess").addString(adminCommand));
            return;
        }

        final String[] wordList = adminCommand.split(" ");
        final IAdminCommandHandler handler = _datatable.get(wordList[0]);
        if (handler != null) {
            boolean success = false;
            try {
                for (final Enum<?> e : handler.getAdminCommandEnum()) {
                    if (e.toString().equalsIgnoreCase(wordList[0])) {
                        success = handler.useAdminCommand(e, wordList, adminCommand, activeChar);
                        break;
                    }
                }
            } catch (Exception e) {
                error("", e);
            }

            Log.gmActions(activeChar, activeChar.getTarget(), adminCommand, success);
        }
    }

    @Override
    public void process() {

    }

    @Override
    public int size() {
        return _datatable.size();
    }

    @Override
    public void clear() {
        _datatable.clear();
    }

    /**
     * Получение списка зарегистрированных админ команд
     *
     * @return список команд
     */
    public Set<String> getAllCommands() {
        return _datatable.keySet();
    }
}
