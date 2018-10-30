package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.cache.CrestCache;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Player;

public class RequestSetPledgeCrestLarge extends L2GameClientPacket {
    private int _length;
    private byte[] _data;

    /**
     * format: chd(b)
     */
    @Override
    protected void readImpl() {
        _length = readD();
        if (_length == CrestCache.LARGE_CREST_SIZE && _length == _buf.remaining()) {
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
        if (clan == null) {
            return;
        }
        if ((activeChar.getClanPrivileges() & Clan.CP_CL_EDIT_CREST) == Clan.CP_CL_EDIT_CREST) {
            if (clan.getCastle() == 0 && clan.getHasHideout() == 0) {
                activeChar.sendPacket(SystemMsg.A_CLAN_CREST_CAN_ONLY_BE_REGISTERED_WHEN_THE_CLANS_SKILL_LEVEL_IS_3_OR_ABOVE); // TODO[K] - найти нормальную инфу с офа
                return;
            }

            int crestId = 0;

            if (_data != null) {
                crestId = CrestCache.getInstance().saveCrest(CrestCache.CrestType.PLEDGE_LARGE, clan.getClanId(), _data);
                activeChar.sendPacket(SystemMsg.THE_CLAN_CREST_WAS_SUCCESSFULLY_REGISTERED);
            } else if (clan.hasCrestLarge()) {
                CrestCache.getInstance().removeCrest(CrestCache.CrestType.PLEDGE_LARGE, clan.getClanId());
                activeChar.sendPacket(SystemMsg.THE_CLANS_CREST_HAS_BEEN_DELETED);
            }
            clan.setCrestLargeId(crestId);
            clan.broadcastClanStatus(false, true, false);
        }
    }
}