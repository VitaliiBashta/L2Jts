package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.configuration.config.ExtConfig;
import org.mmocore.gameserver.manager.PetitionManager;
import org.mmocore.gameserver.model.petition.PetitionMainGroup;
import org.mmocore.gameserver.model.petition.PetitionSubGroup;
import org.mmocore.gameserver.object.Player;

public final class RequestPetition extends L2GameClientPacket {
    private String _content;
    private int _type;

    @Override
    protected void readImpl() {
        _content = readS();
        _type = readD();
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }

        if (ExtConfig.EX_NEW_PETITION_SYSTEM) {
            final PetitionMainGroup group = player.getPetitionGroup();
            if (group == null) {
                return;
            }

            final PetitionSubGroup subGroup = group.getSubGroup(_type);
            if (subGroup == null) {
                return;
            }

            subGroup.getHandler().handle(player, _type, _content);
        } else {
            PetitionManager.getInstance().handle(player, _type, _content);
        }
    }
}
