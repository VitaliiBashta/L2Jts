package org.mmocore.gameserver.network.lineage.clientpackets;


import org.mmocore.gameserver.manager.CursedWeaponsManager;
import org.mmocore.gameserver.model.items.etcitems.CursedWeapon;
import org.mmocore.gameserver.network.lineage.serverpackets.ExCursedWeaponLocation;
import org.mmocore.gameserver.network.lineage.serverpackets.ExCursedWeaponLocation.CursedWeaponInfo;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.utils.Location;

import java.util.ArrayList;
import java.util.List;


public class RequestCursedWeaponLocation extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        final Creature activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        final List<CursedWeaponInfo> list = new ArrayList<>();
        for (final CursedWeapon cw : CursedWeaponsManager.getInstance().getCursedWeapons()) {
            final Location pos = cw.getWorldPosition();
            if (pos != null) {
                list.add(new CursedWeaponInfo(pos, cw.getItemId(), cw.isActivated() ? 1 : 0));
            }
        }

        activeChar.sendPacket(new ExCursedWeaponLocation(list));
    }
}