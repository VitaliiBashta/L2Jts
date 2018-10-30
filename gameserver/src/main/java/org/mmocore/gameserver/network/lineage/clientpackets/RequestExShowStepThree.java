package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.configuration.config.ExtConfig;
import org.mmocore.gameserver.model.petition.PetitionMainGroup;
import org.mmocore.gameserver.model.petition.PetitionSubGroup;
import org.mmocore.gameserver.network.lineage.serverpackets.ExResponseShowContents;
import org.mmocore.gameserver.object.Player;

/**
 * @author VISTALL
 */
public class RequestExShowStepThree extends L2GameClientPacket {
    private int _subId;

    @Override
    protected void readImpl() {
        _subId = readC();
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null || !ExtConfig.EX_NEW_PETITION_SYSTEM) {
            return;
        }

        final PetitionMainGroup group = player.getPetitionGroup();
        if (group == null) {
            return;
        }

        final PetitionSubGroup subGroup = group.getSubGroup(_subId);
        if (subGroup == null) {
            return;
        }

        player.sendPacket(new ExResponseShowContents(subGroup.getDescription(player.getLanguage())));
    }
}