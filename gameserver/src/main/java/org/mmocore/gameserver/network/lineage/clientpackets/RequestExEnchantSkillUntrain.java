package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.model.base.EnchantSkillLearn;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ExEnchantSkillInfo;
import org.mmocore.gameserver.network.lineage.serverpackets.ExEnchantSkillResult;
import org.mmocore.gameserver.network.lineage.serverpackets.SkillList;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.skills.TimeStamp;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.tables.SkillTreeTable;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Log;

public final class RequestExEnchantSkillUntrain extends L2GameClientPacket {
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

        if (activeChar.isSitting() || activeChar.isInStoreMode()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_MOVE_WHILE_SITTING);
            return;
        }

        if (activeChar.getLevel() < 76) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_USE_THE_SKILL_ENHANCING_FUNCTION_ON_THIS_LEVEL);
            return;
        }

        if (activeChar.getPlayerClassComponent().getClassId().getLevel().ordinal() < 4) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_USE_THE_SKILL_ENHANCING_FUNCTION_IN_THIS_CLASS);
            return;
        }

        if (activeChar.isTransformed() || activeChar.isInCombat() || activeChar.isInBoat()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_USE_THE_SKILL_ENHANCING_FUNCTION_IN_THIS_CLASS_);
            return;
        }

        final SkillEntry oldSkill = activeChar.getKnownSkill(_skillId);
        if (oldSkill.getDisplayLevel() == -1) {
            return;
        }

        if (_skillLvl != (oldSkill.getDisplayLevel() - 1) || (_skillLvl / 100) != (oldSkill.getDisplayLevel() / 100)) {
            return;
        }

        final EnchantSkillLearn sl = SkillTreeTable.getSkillEnchant(_skillId, oldSkill.getDisplayLevel());
        if (sl == null) {
            return;
        }

        final SkillEntry newSkill;

        if (_skillLvl % 100 == 0) {
            _skillLvl = sl.getBaseLevel();
            newSkill = SkillTable.getInstance().getSkillEntry(_skillId, _skillLvl);
        } else {
            newSkill = SkillTable.getInstance().getSkillEntry(_skillId, SkillTreeTable.convertEnchantLevel(sl.getBaseLevel(), _skillLvl,
                    sl.getMaxLevel()));
        }

        if (newSkill == null) {
            return;
        }

        if (ItemFunctions.getItemCount(activeChar, SkillTreeTable.UNTRAIN_ENCHANT_BOOK) == 0) {
            activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ALL_OF_THE_ITEMS_NEEDED_TO_UNTRAIN_THE_ENCHANT_SKILL);
            return;
        }

        ItemFunctions.removeItem(activeChar, SkillTreeTable.UNTRAIN_ENCHANT_BOOK, 1);

        activeChar.addExpAndSp(0, (int) (sl.getCost()[1] * sl.getCostMult() * AllSettingsConfig.ALT_SKILL_UNTRAIN_REFUND_SP_MODIFIER));
        activeChar.addSkill(newSkill, true);
        if (activeChar.isSkillDisabled(oldSkill)) {
            TimeStamp timeStamp = activeChar.removeSkillReuse(oldSkill).get();
            timeStamp.setLevel(newSkill.getLevel());
            activeChar.getSkillsReuse().put(newSkill.hashCode(), timeStamp);
        }
        if (_skillLvl > 100) {
            activeChar.sendPacket(new SystemMessage(SystemMsg.UNTRAIN_OF_ENCHANT_SKILL_WAS_SUCCESSFUL_S1).addSkillName(_skillId, _skillLvl));
        } else {
            activeChar.sendPacket(new SystemMessage(SystemMsg.UNTRAIN_OF_ENCHANT_SKILL_WAS_SUCCESSFUL_S1_).addSkillName(_skillId, _skillLvl));
        }

        Log.add(activeChar.getName() + "|Successfully untranes|" + _skillId + "|to+" + _skillLvl + "|---", "enchant_skills");

        activeChar.sendPacket(new ExEnchantSkillInfo(_skillId, newSkill.getDisplayLevel()), ExEnchantSkillResult.SUCCESS, new SkillList(activeChar));
        RequestExEnchantSkill.updateSkillShortcuts(activeChar, _skillId, _skillLvl);
    }
}