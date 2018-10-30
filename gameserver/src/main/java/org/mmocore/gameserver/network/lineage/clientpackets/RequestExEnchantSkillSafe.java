package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.commons.utils.Rnd;
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

/**
 * Format (ch) dd
 */
public final class RequestExEnchantSkillSafe extends L2GameClientPacket {
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

        final EnchantSkillLearn sl = SkillTreeTable.getSkillEnchant(_skillId, _skillLvl);

        if (sl == null) {
            return;
        }

        final SkillEntry skillEntry = activeChar.getKnownSkill(_skillId);
        if (skillEntry.getLevel() == -1) {
            return;
        }

        final int enchantLevel = SkillTreeTable.convertEnchantLevel(sl.getBaseLevel(), _skillLvl, sl.getMaxLevel());

        // already knows the skill with this level
        if (skillEntry.getLevel() >= enchantLevel) {
            return;
        }

        // Можем ли мы перейти с текущего уровня скилла на данную заточку
        if (skillEntry.getLevel() == sl.getBaseLevel() ? _skillLvl % 100 != 1 : skillEntry.getLevel() != enchantLevel - 1) {
            activeChar.sendMessage("Incorrect enchant level.");
            return;
        }

        final SkillEntry skill = SkillTable.getInstance().getSkillEntry(_skillId, enchantLevel);
        if (skill == null) {
            return;
        }

        final int[] cost = sl.getCost();
        final int requiredSp = (int) (cost[1] * sl.getCostMult() * AllSettingsConfig.ALT_SKILL_SAFE_ENCHANT_SP_MODIFIER);
        final int requiredAdena = (int) (cost[0] * sl.getCostMult() * AllSettingsConfig.ALT_SKILL_SAFE_ENCHANT_ADENA_MODIFIER);

        final int rate = sl.getRate(activeChar);

        if (activeChar.getSp() < requiredSp) {
            activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_SP_TO_ENCHANT_THAT_SKILL);
            return;
        }

        if (activeChar.getAdena() < requiredAdena) {
            activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            return;
        }

        if (ItemFunctions.getItemCount(activeChar, SkillTreeTable.SAFE_ENCHANT_BOOK) == 0) {
            activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ALL_OF_THE_ITEMS_NEEDED_TO_ENCHANT_THAT_SKILL);
            return;
        }

        ItemFunctions.removeItem(activeChar, SkillTreeTable.SAFE_ENCHANT_BOOK, 1);

        if (Rnd.chance(rate)) {
            activeChar.addSkill(skill, true);
            if (activeChar.isSkillDisabled(skillEntry)) {
                TimeStamp timeStamp = activeChar.removeSkillReuse(skillEntry).get();
                timeStamp.setLevel(skill.getLevel());
                activeChar.getSkillsReuse().put(skill.hashCode(), timeStamp);
            }
            activeChar.addExpAndSp(0, -1 * requiredSp);
            ItemFunctions.removeItem(activeChar, 57, requiredAdena);
            activeChar.sendPacket(new SystemMessage(SystemMsg.YOUR_SP_HAS_DECREASED_BY_S1).addNumber(requiredSp), new SystemMessage(
                    SystemMsg.SKILL_ENCHANT_WAS_SUCCESSFUL_S1_HAS_BEEN_ENCHANTED).addSkillName(_skillId, _skillLvl), new ExEnchantSkillResult(1));
            activeChar.sendPacket(new SkillList(activeChar));
            RequestExEnchantSkill.updateSkillShortcuts(activeChar, _skillId, _skillLvl);
            Log.add(activeChar.getName() + "|Successfully safe enchanted|" + _skillId + "|to+" + _skillLvl + '|' + rate, "enchant_skills");
        } else {
            activeChar.sendPacket(new SystemMessage(SystemMsg.SKILL_ENCHANT_FAILED_S1).addSkillName(_skillId, _skillLvl), new ExEnchantSkillResult(
                    0));
            Log.add(activeChar.getName() + "|Failed to safe enchant|" + _skillId + "|to+" + _skillLvl + '|' + rate, "enchant_skills");
        }

        activeChar.sendPacket(new ExEnchantSkillInfo(_skillId, activeChar.getSkillDisplayLevel(_skillId)));
    }
}