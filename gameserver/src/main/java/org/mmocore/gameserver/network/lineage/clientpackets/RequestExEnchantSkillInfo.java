package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.model.base.EnchantSkillLearn;
import org.mmocore.gameserver.network.lineage.serverpackets.ExEnchantSkillInfo;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.tables.SkillTreeTable;

public class RequestExEnchantSkillInfo extends L2GameClientPacket {
    private int _skillId;
    private int _skillLvl;

    @Override
    protected void readImpl() {
        _skillId = readD();
        _skillLvl = readD();
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (_skillLvl > 100) {
            final EnchantSkillLearn sl = SkillTreeTable.getSkillEnchant(_skillId, _skillLvl);
            if (sl == null) {
                return;
            }

            final SkillEntry skill = SkillTable.getInstance().getSkillEntry(_skillId, SkillTreeTable.convertEnchantLevel(sl.getBaseLevel(), _skillLvl,
                    sl.getMaxLevel()));

            if (skill == null || skill.getId() != _skillId) {
                return;
            }

            if (activeChar.getSkillLevel(_skillId) != skill.getLevel()) {
                return;
            }
        } else if (activeChar.getSkillLevel(_skillId) != _skillLvl) {
            return;
        }

        sendPacket(new ExEnchantSkillInfo(_skillId, _skillLvl));
    }
}