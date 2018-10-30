package org.mmocore.gameserver.handler.usercommands.impl;

import org.mmocore.gameserver.handler.usercommands.IUserCommandHandler;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Player;

/**
 * @author KilRoy
 */
public class Nick implements IUserCommandHandler {
    private static final int[] COMMAND_IDS = {98};

    @Override
    public boolean useUserCommand(int id, Player player) {
        if (player.getPlayerAccess().CanEditChar) {
            player.sendAdminMessage("TODO this command!");
        } else {
            player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
        }
        return true;
    }

    @Override
    public final int[] getUserCommandList() {
        return COMMAND_IDS;
    }
}