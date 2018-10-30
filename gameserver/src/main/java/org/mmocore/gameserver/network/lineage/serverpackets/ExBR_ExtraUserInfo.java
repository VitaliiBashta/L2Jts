package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.AbnormalEffectType;

public class ExBR_ExtraUserInfo extends GameServerPacket {
    private final int objectId;
    private final int effect3;
    private final int lectureMark;

    public ExBR_ExtraUserInfo(final Player cha) {
        objectId = cha.getObjectId();
        effect3 = cha.getAbnormalEffect(AbnormalEffectType.BRANCH);
        lectureMark = cha.getLectureMark();
    }

    @Override
    protected void writeData() {
        writeD(objectId); //object id of player
        writeD(effect3); // event effect id
        writeC(lectureMark);
    }
}