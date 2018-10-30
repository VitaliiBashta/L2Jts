package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.EffectType;

public class RequestDispel extends L2GameClientPacket {
    private int _objectId, _id, _level;

    @Override
    protected void readImpl() throws Exception {
        _objectId = readD();
        _id = readD();
        _level = readD();
    }

    @Override
    protected void runImpl() throws Exception {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null || activeChar.getObjectId() != _objectId && activeChar.getServitor() == null) {
            return;
        }

        Creature target = activeChar;
        if (activeChar.getObjectId() != _objectId) {
            target = activeChar.getServitor();
        }

        for (final Effect e : target.getEffectList().getAllEffects()) {
            if (e.getDisplayId() == _id && e.getDisplayLevel() == _level) {
                if (!e.isOffensive() && (!e.getSkill().getTemplate().isMusic() || AllSettingsConfig.allowDispelDanceSong) && e.getSkill().getTemplate().isSelfDispellable() && e.getEffectType() != EffectType.p_transform && e.getTemplate().getEffectType() != EffectType.Hourglass) {
                    e.exit();
                    target.sendPacket(new SystemMessage(SystemMsg.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(e.getSkill()));
                } else {
                    return;
                }
            }
        }
    }
}