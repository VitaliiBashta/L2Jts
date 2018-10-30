package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

/**
 * Этот пакет отвечает за анимацию высасывания душ из трупов
 *
 * @author SYS
 */
public class ExSpawnEmitter extends GameServerPacket {
    private final int monsterObjId;
    private final int playerObjId;

    public ExSpawnEmitter(final NpcInstance monster, final Player player) {
        playerObjId = player.getObjectId();
        monsterObjId = monster.getObjectId();
    }

    @Override
    protected final void writeData() {
        //ddd
        writeD(monsterObjId);
        writeD(playerObjId);
        writeD(0x00); //unk
    }
}