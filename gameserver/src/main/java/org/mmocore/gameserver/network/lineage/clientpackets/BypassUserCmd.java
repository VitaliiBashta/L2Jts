package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.handler.usercommands.IUserCommandHandler;
import org.mmocore.gameserver.handler.usercommands.UserCommandHandler;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.object.Player;

/**
 * format:  cd
 * Пример пакета по команде /loc:
 * AA 00 00 00 00
 */
public class BypassUserCmd extends L2GameClientPacket {
    private int _command;

    @Override
    protected void readImpl() {
        _command = readD();
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        final IUserCommandHandler handler = UserCommandHandler.getInstance().getUserCommandHandler(_command);

        if (handler == null) {
            activeChar.sendMessage(new CustomMessage("common.S1NotImplemented").addString(String.valueOf(_command)));
        } else {
            handler.useUserCommand(_command, activeChar);
        }
    }
}