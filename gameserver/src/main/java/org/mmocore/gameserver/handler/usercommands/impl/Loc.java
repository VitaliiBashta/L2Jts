package org.mmocore.gameserver.handler.usercommands.impl;

import org.mmocore.gameserver.handler.usercommands.IUserCommandHandler;
import org.mmocore.gameserver.manager.MapRegionManager;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.mapregion.RestartArea;

public class Loc implements IUserCommandHandler {
    private static final int[] COMMAND_IDS = {0};

    @Override
    public boolean useUserCommand(int id, Player activeChar) {
        RestartArea ra = MapRegionManager.getInstance().getRegionData(RestartArea.class, activeChar);
        SystemMsg message = ra != null ? ra.getRestartPoint().get(activeChar.getPlayerTemplateComponent().getPlayerRace()).getMessage() : SystemMsg.CURRENT_LOCATION__S1_S2_S3_NEAR_THE_NEUTRAL_ZONE;

        if (message.size() > 0) {
            activeChar.sendPacket(new SystemMessage(message).addNumber(activeChar.getX()).addNumber(activeChar.getY()).addNumber(activeChar.getZ()));
        } else {
            activeChar.sendPacket(message);
        }
        return true;
    }

    @Override
    public final int[] getUserCommandList() {
        return COMMAND_IDS;
    }
}