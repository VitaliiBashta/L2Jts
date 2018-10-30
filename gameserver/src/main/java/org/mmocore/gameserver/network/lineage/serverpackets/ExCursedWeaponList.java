package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.manager.CursedWeaponsManager;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ExCursedWeaponList extends GameServerPacket {
    private final int[] cursedWeapon_ids;

    public ExCursedWeaponList() {
        cursedWeapon_ids = CursedWeaponsManager.getInstance().getCursedWeaponsIds();
    }

    @Override
    protected final void writeData() {
        writeDD(cursedWeapon_ids, true);
    }
}