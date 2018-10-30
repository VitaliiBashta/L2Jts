package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.configuration.config.ExtConfig;
import org.mmocore.gameserver.data.xml.holder.PetitionGroupHolder;
import org.mmocore.gameserver.model.petition.PetitionMainGroup;
import org.mmocore.gameserver.network.lineage.serverpackets.ExResponseShowStepTwo;
import org.mmocore.gameserver.object.Player;

/**
 * @author VISTALL
 */
public class RequestExShowStepTwo extends L2GameClientPacket {
    private int _petitionGroupId;

    @Override
    protected void readImpl() {
        _petitionGroupId = readC();
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null || !ExtConfig.EX_NEW_PETITION_SYSTEM) {
            return;
        }

        final PetitionMainGroup group = PetitionGroupHolder.getInstance().getPetitionGroup(_petitionGroupId);
        if (group == null) {
            return;
        }

        player.setPetitionGroup(group);
        player.sendPacket(new ExResponseShowStepTwo(player, group));
    }
}