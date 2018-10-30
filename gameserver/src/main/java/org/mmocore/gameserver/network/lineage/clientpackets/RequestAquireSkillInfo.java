package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.commons.lang.ArrayUtils;
import org.mmocore.gameserver.data.xml.holder.SkillAcquireHolder;
import org.mmocore.gameserver.model.SkillLearn;
import org.mmocore.gameserver.model.base.AcquireType;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.AcquireSkillInfo;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.tables.SkillTable;

/**
 * Reworked: VISTALL
 */
public class RequestAquireSkillInfo extends L2GameClientPacket {
    private int _id;
    private int _level;
    private AcquireType _type;

    @Override
    protected void readImpl() {
        _id = readD();
        _level = readD();
        _type = ArrayUtils.valid(AcquireType.VALUES, readD());
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null || player.isTransformed() || SkillTable.getInstance().getSkillEntry(_id, _level) == null || _type == null) {
            player.sendPacket(new SystemMessage(SystemMsg.THIS_ITEM_CANNOT_BE_USED_IN_THE_CURRENT_TRANSFORMATION_STATE)); // TODO[K] - найти подходящий мессадж
            return;
        }

        final NpcInstance trainer = player.getLastNpc();
        if ((trainer == null || player.getDistance(trainer.getX(), trainer.getY()) > player.getInteractDistance(trainer)) && !player.isGM()) {
            return;
        }

        final SkillLearn skillLearn = SkillAcquireHolder.getInstance().getSkillLearn(player, _id, _level, _type);
        if (skillLearn == null) {
            return;
        }

        sendPacket(new AcquireSkillInfo(_type, skillLearn));
    }
}