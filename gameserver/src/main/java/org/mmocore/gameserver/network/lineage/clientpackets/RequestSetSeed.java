package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.manager.CastleManorManager;
import org.mmocore.gameserver.model.Manor;
import org.mmocore.gameserver.model.Manor.SeedData;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.manor.SeedProduction;

import java.util.ArrayList;
import java.util.List;

/**
 * Format: (ch) dd [ddd]
 * d - manor id
 * d - size
 * [
 * d - seed id
 * d - sales
 * d - price
 * ]
 */
public class RequestSetSeed extends L2GameClientPacket {
    private int _count, _manorId;

    private long[] _items; // _size*3

    @Override
    protected void readImpl() {
        _manorId = readD();
        _count = readD();
        if (_count * 20 > _buf.remaining() || _count > Short.MAX_VALUE || _count < 1) {
            _count = 0;
            return;
        }
        _items = new long[_count * 3];
        for (int i = 0; i < _items.length; ) {
            int id = readD();
            long sales = readQ();
            long price = readQ();
            if (id < 1 || sales < 0 || price < 0) {
                _count = 0;
                return;
            }
            _items[i++] = id;
            _items[i++] = sales;
            _items[i++] = price;
        }
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null || _count == 0) {
            return;
        }

        if (activeChar.getClan() == null) {
            activeChar.sendActionFailed();
            return;
        }

        final Castle castle = ResidenceHolder.getInstance().getResidence(Castle.class, _manorId);
        if (castle == null || castle.getOwnerId() != activeChar.getClanId() // clan owns castle
                || (activeChar.getClanPrivileges() & Clan.CP_CS_MANOR_ADMIN) != Clan.CP_CS_MANOR_ADMIN) // has manor rights
        {
            activeChar.sendActionFailed();
            return;
        }

        if (castle.isNextPeriodApproved()) {
            activeChar.sendPacket(SystemMsg.A_MANOR_CANNOT_BE_SET_UP_BETWEEN_430_AM_AND_8_PM);
            activeChar.sendActionFailed();
            return;
        }

        final NpcInstance chamberlain = activeChar.getLastNpc();
        if (chamberlain == null || chamberlain.getCastle() != castle) {
            activeChar.sendActionFailed();
            return;
        }

        final List<SeedProduction> seeds = new ArrayList<>(_count);
        final List<SeedData> checkList = Manor.getInstance().getSeedsForCastle(_manorId);
        for (int i = 0; i < _count; i++) {
            final int id = (int) _items[i * 3 + 0];
            final long sales = _items[i * 3 + 1];
            final long price = _items[i * 3 + 2];
            if (id > 0) {
                for (final SeedData check : checkList) {
                    if (check.getId() == id) {
                        if (sales > check.getSeedLimit()) {
                            break;
                        }

                        long basePrice = Manor.getInstance().getSeedBasicPrice(id);
                        if (price < basePrice * 60 / 100 || price > basePrice * 10) {
                            break;
                        }

                        SeedProduction s = CastleManorManager.getInstance().getNewSeedProduction(id, sales, price, sales);
                        seeds.add(s);
                        break;
                    }
                }
            }
        }

        castle.setSeedProduction(seeds, CastleManorManager.PERIOD_NEXT);
        castle.saveSeedData(CastleManorManager.PERIOD_NEXT);
    }
}