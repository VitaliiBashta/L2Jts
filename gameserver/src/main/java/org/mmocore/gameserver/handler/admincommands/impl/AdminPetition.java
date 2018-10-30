package org.mmocore.gameserver.handler.admincommands.impl;


import org.apache.commons.lang3.math.NumberUtils;
import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.manager.PetitionManager;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;

/**
 * @author n0nam3
 * @date 21/08/2010 1:00
 */
public class AdminPetition implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        if (!activeChar.getPlayerAccess().CanEditChar) {
            return false;
        }

        int petitionId = NumberUtils.toInt(wordList.length > 1 ? wordList[1] : "-1", -1);
        final Commands command = (Commands) comm;
        switch (command) {
            case admin_view_petitions:
                PetitionManager.getInstance().sendPendingPetitionList(activeChar);
                break;
            case admin_view_petition:
                PetitionManager.getInstance().viewPetition(activeChar, petitionId);
                break;
            case admin_accept_petition:
                if (petitionId < 0) {
                    activeChar.sendAdminMessage("Usage: //accept_petition id");
                    return false;
                }
                if (PetitionManager.getInstance().isPlayerInConsultation(activeChar)) {
                    activeChar.sendPacket(SystemMsg.YOU_MAY_ONLY_SUBMIT_ONE_PETITION_ACTIVE_AT_A_TIME);
                    return true;
                }

                if (PetitionManager.getInstance().isPetitionInProcess(petitionId)) {
                    activeChar.sendPacket(SystemMsg.YOUR_PETITION_IS_BEING_PROCESSED);
                    return true;
                }

                if (!PetitionManager.getInstance().acceptPetition(activeChar, petitionId)) {
                    activeChar.sendPacket(SystemMsg.NOT_UNDER_PETITION_CONSULTATION);
                }

                break;
            case admin_reject_petition:
                if (petitionId < 0) {
                    activeChar.sendAdminMessage("Usage: //accept_petition id");
                    return false;
                }
                if (!PetitionManager.getInstance().rejectPetition(activeChar, petitionId)) {
                    activeChar.sendPacket(SystemMsg.FAILED_TO_CANCEL_PETITION);
                }
                PetitionManager.getInstance().sendPendingPetitionList(activeChar);

                break;
            case admin_reset_petitions:
                if (PetitionManager.getInstance().isPetitionInProcess()) {
                    activeChar.sendPacket(SystemMsg.YOUR_PETITION_IS_BEING_PROCESSED);
                    return false;
                }
                PetitionManager.getInstance().clearPendingPetitions();
                PetitionManager.getInstance().sendPendingPetitionList(activeChar);
                break;
            case admin_force_peti:
                if (fullString.length() < 11) {
                    activeChar.sendAdminMessage("Usage: //force_peti text");
                    return false;
                }
                try {
                    final GameObject targetChar = activeChar.getTarget();
                    if (targetChar == null || !(targetChar instanceof Player)) {
                        activeChar.sendPacket(SystemMsg.INVALID_TARGET);
                        return false;
                    }
                    final Player targetPlayer = (Player) targetChar;

                    petitionId = PetitionManager.getInstance().submitPetition(targetPlayer, fullString.substring(10), 9);
                    PetitionManager.getInstance().acceptPetition(activeChar, petitionId);
                } catch (StringIndexOutOfBoundsException e) {
                    activeChar.sendAdminMessage("Usage: //force_peti text");
                    return false;
                }
                break;
        }
        return true;
    }

    @Override
    public Enum<?>[] getAdminCommandEnum() {
        return Commands.values();
    }

    @Override
    public String[] getAdminCommandString() {
        // TODO Auto-generated method stub
        return null;
    }

    private enum Commands {
        admin_view_petitions,
        admin_view_petition,
        admin_accept_petition,
        admin_reject_petition,
        admin_reset_petitions,
        admin_force_peti
    }
}