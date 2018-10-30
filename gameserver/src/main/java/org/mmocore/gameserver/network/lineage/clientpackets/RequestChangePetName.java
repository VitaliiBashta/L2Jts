package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.client.holder.NpcNameLineHolder;
import org.mmocore.gameserver.model.instances.PetInstance;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Util;

public class RequestChangePetName extends L2GameClientPacket {
    private String _name;

    @Override
    protected void readImpl() {
        _name = readS();
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        final PetInstance pet = activeChar.getServitor() != null && activeChar.getServitor().isPet() ? (PetInstance) activeChar.getServitor() : null;
        if (pet == null) {
            return;
        }

        if (pet.isDefaultName()) {
            if (_name.length() < 1 || _name.length() > 16) {
                activeChar.sendPacket(SystemMsg.YOUR_TITLE_CANNOT_EXCEED_16_CHARACTERS_IN_LENGTH);
                return;
            }
            if (!Util.isMatchingRegexp(_name, ServerConfig.CNAME_TEMPLATE)
                    || NpcNameLineHolder.getInstance().isBlackListContainsName(_name)
                    || !Util.checkIsAllowedName(_name)) {
                activeChar.sendPacket(SystemMsg.AN_INVALID_CHARACTER_IS_INCLUDED_IN_THE_PETS_NAME);
                return;
            }
            pet.setName(_name);
            pet.broadcastCharInfo();
            pet.updateControlItem();
        }
    }
}