package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.manager.GmManager;
import org.mmocore.gameserver.manager.PetitionManager;
import org.mmocore.gameserver.network.lineage.components.ChatType;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.Say2;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;

/**
 * <p>Format: (c) d
 * <ul>
 * <li>d: Unknown</li>
 * </ul></p>
 *
 * @author n0nam3
 */
public final class RequestPetitionCancel extends L2GameClientPacket {
    //private int _unknown;

    @Override
    protected void readImpl() {
        //_unknown = readD(); This is pretty much a trigger packet.
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (PetitionManager.getInstance().isPlayerInConsultation(activeChar)) {
            if (activeChar.isGM()) {
                PetitionManager.getInstance().endActivePetition(activeChar);
            } else {
                activeChar.sendPacket(new SystemMessage(SystemMsg.YOUR_PETITION_IS_BEING_PROCESSED));
            }
        } else if (PetitionManager.getInstance().isPlayerPetitionPending(activeChar)) {
            if (PetitionManager.getInstance().cancelActivePetition(activeChar)) {
                final int numRemaining = AllSettingsConfig.MAX_PETITIONS_PER_PLAYER - PetitionManager.getInstance().getPlayerTotalPetitionCount(activeChar);

                activeChar.sendPacket(new SystemMessage(SystemMsg.THE_PETITION_WAS_CANCELED_S1).addString(
                        String.valueOf(numRemaining)));

                // Notify all GMs that the player's pending petition has been cancelled.
                final String msgContent = activeChar.getName() + " has canceled a pending petition.";
                GmManager.broadcastToGMs(new Say2(activeChar.getObjectId(), ChatType.HERO_VOICE, "Petition System", msgContent, null));
            } else {
                activeChar.sendPacket(new SystemMessage(SystemMsg.FAILED_TO_CANCEL_PETITION));
            }
        } else {
            activeChar.sendPacket(new SystemMessage(SystemMsg.YOU_HAVE_NOT_SUBMITTED_A_PETITION));
        }
    }
}
