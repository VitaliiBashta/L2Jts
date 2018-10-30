package org.mmocore.gameserver.handler.usercommands.impl;

import org.mmocore.gameserver.handler.usercommands.IUserCommandHandler;
import org.mmocore.gameserver.model.base.TeamType;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;

/**
 * Support for /unstuck command
 */
public class Escape implements IUserCommandHandler {
    private static final int[] COMMAND_IDS = {52};

    @Override
    public boolean useUserCommand(int id, Player activeChar) {
        if (id != COMMAND_IDS[0]) {
            return false;
        }

        if (activeChar.isMovementDisabled() || activeChar.isAfraid() || activeChar.isInOlympiadMode()) {
            return false;
        }

        if (activeChar.getTeleMode() != 0 || !activeChar.getPlayerAccess().UseTeleport) {
            activeChar.sendMessage(new CustomMessage("common.TryLater"));
            return false;
        }

        if (activeChar.isTerritoryFlagEquipped()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_TELEPORT_WHILE_IN_POSSESSION_OF_A_WARD);
            return false;
        }

        if (activeChar.isInDuel() || activeChar.getTeam() != TeamType.NONE) {
            activeChar.sendMessage(new CustomMessage("common.RecallInDuel"));
            return false;
        }

        activeChar.abortAttack(true, true);
        activeChar.abortCast(true, true);
        activeChar.stopMove();

        SkillEntry skill;
        if (activeChar.getPlayerAccess().FastUnstuck) {
            skill = SkillTable.getInstance().getSkillEntry(1050, 2);
        } else {
            skill = SkillTable.getInstance().getSkillEntry(2099, 1);
        }

        if (skill != null && skill.checkCondition(activeChar, activeChar, false, false, true)) {
            activeChar.getAI().Cast(skill, activeChar, false, true);
        }

        return true;
    }

    @Override
    public final int[] getUserCommandList() {
        return COMMAND_IDS;
    }
}
