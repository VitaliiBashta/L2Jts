package org.mmocore.gameserver.handler.usercommands.impl;

import org.mmocore.gameserver.handler.usercommands.IUserCommandHandler;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;

/**
 * @author KilRoy
 * TODO[K] - Судя по РПГ-Клабу, время всегда == 0, а на НА офе данного мессаджа вообще нет
 * Связан с пакетом RequestRemainTime
 */
public class FatigueTime implements IUserCommandHandler {
    private static final int[] COMMAND_IDS = {102};
    private static final SystemMessage MESSAGE = new SystemMessage(SystemMsg.YOU_PLAY_S1_S2_NEEDS_TO_REST_S3_S4).addNumber(0).addNumber(0).addNumber(0).addNumber(0);

    @Override
    public boolean useUserCommand(int id, Player player) {
        player.sendPacket(MESSAGE);
        return true;
    }

    @Override
    public final int[] getUserCommandList() {
        return COMMAND_IDS;
    }
}