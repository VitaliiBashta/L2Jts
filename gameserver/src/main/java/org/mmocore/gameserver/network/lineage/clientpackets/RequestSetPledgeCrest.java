package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.cache.CrestCache;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Player;

public class RequestSetPledgeCrest extends L2GameClientPacket {
    private int _length;
    private byte[] _data;

    @Override
    protected void readImpl() {
        _length = readD();
        if (_length == CrestCache.CREST_SIZE && _length == _buf.remaining()) {
            _data = new byte[_length];
            readB(_data);
        }
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        final Clan clan = activeChar.getClan();
        if ((activeChar.getClanPrivileges() & Clan.CP_CL_EDIT_CREST) == Clan.CP_CL_EDIT_CREST) {
            if (clan.getLevel() < 3) {
                activeChar.sendPacket(SystemMsg.A_CLAN_CREST_CAN_ONLY_BE_REGISTERED_WHEN_THE_CLANS_SKILL_LEVEL_IS_3_OR_ABOVE);
                return;
            }

            int crestId = 0;

            if (_data != null) {
                crestId = CrestCache.getInstance().saveCrest(CrestCache.CrestType.PLEDGE, clan.getClanId(), _data);
                activeChar.sendPacket(SystemMsg.THE_CLAN_CREST_WAS_SUCCESSFULLY_REGISTERED);
            } else if (clan.hasCrest()) {
                CrestCache.getInstance().removeCrest(CrestCache.CrestType.PLEDGE, clan.getClanId());
                activeChar.sendPacket(SystemMsg.THE_CLANS_CREST_HAS_BEEN_DELETED);
            }
            clan.setCrestId(crestId);
            clan.broadcastClanStatus(false, true, false);
        }
    }
}