package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.model.instances.DoorInstance;
import org.mmocore.gameserver.model.instances.StaticObjectInstance;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

public class StaticObject extends GameServerPacket {
    private final int staticObjectId;
    private final int objectId;
    private final int type;
    private final int isTargetable;
    private final int meshIndex;
    private final int isClosed;
    private final int isEnemy;
    private final int maxHp;
    private final int currentHp;
    private final int showHp;
    private final int damageGrade;

    public StaticObject(final StaticObjectInstance obj) {
        staticObjectId = obj.getUId();
        objectId = obj.getObjectId();
        type = 0;
        isTargetable = 1;
        meshIndex = obj.getMeshIndex();
        isClosed = 0;
        isEnemy = 0;
        maxHp = 0;
        currentHp = 0;
        showHp = 0;
        damageGrade = 0;
    }

    public StaticObject(final DoorInstance door, final Player player) {
        staticObjectId = door.getDoorId();
        objectId = door.getObjectId();
        type = 1;
        isTargetable = door.getTemplate().isTargetable() ? 1 : 0;
        meshIndex = 1;
        isClosed = door.isOpen() ? 0 : 1; //opened 0 /closed 1
        isEnemy = door.isAutoAttackable(player) ? 1 : 0;
        currentHp = (int) door.getCurrentHp();
        maxHp = (int) door.getMaxHp();
        showHp = door.isHPVisible() ? 1 : 0; //TODO [G1ta0] статус двери для осаждающих
        damageGrade = door.getDamage();
    }

    @Override
    protected final void writeData() {
        writeD(staticObjectId);
        writeD(objectId);
        writeD(type);
        writeD(isTargetable);
        writeD(meshIndex);
        writeD(isClosed);
        writeD(isEnemy);
        writeD(currentHp);
        writeD(maxHp);
        writeD(showHp);
        writeD(damageGrade);
    }
}