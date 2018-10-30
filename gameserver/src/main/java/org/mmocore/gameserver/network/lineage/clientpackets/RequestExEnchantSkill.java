package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.model.base.EnchantSkillLearn;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.*;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.interfaces.ShortCut;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.skills.TimeStamp;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.tables.SkillTreeTable;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Log;

/**
 * Format chdd
 * c: (id) 0xD0
 * h: (subid) 0x0F
 * d: skill id
 * d: skill lvl
 */
public class RequestExEnchantSkill extends L2GameClientPacket {
    private int _skillId;
    private int _skillLvl;

    protected static void updateSkillShortcuts(final Player player, final int skillId, final int skillLevel) {
        player.getShortCutComponent().getAllShortCuts().stream().filter(sc -> sc.getId() == skillId && sc.getType() == ShortCut.TYPE_SKILL).forEach(sc -> {
            final ShortCut newsc = new ShortCut(sc.getSlot(), sc.getPage(), sc.getType(), sc.getId(), skillLevel, 1);
            player.sendPacket(new ShortCutRegister(player, newsc));
            player.getShortCutComponent().registerShortCut(newsc);
        });
    }

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

        if (AllSettingsConfig.oneClickSkillEnchant) {
            int branch = _skillLvl / 100;
            if (branch != 0)
                _skillLvl = branch * 100 + sl.getMaxLevel();
        }

        int enchantLevel = SkillTreeTable.convertEnchantLevel(sl.getBaseLevel(), _skillLvl, sl.getMaxLevel());


        // already knows the skill with this level
        if (skillEntry.getLevel() >= enchantLevel) {
            return;
        }

        // Можем ли мы перейти с текущего уровня скилла на данную заточку
        if (!AllSettingsConfig.oneClickSkillEnchant && (skillEntry.getLevel() == sl.getBaseLevel()
                ? _skillLvl % 100 != 1 : skillEntry.getLevel() != enchantLevel - 1)) {
            activeChar.sendMessage("Incorrect enchant level.");
            return;
        }

        SkillEntry skill = SkillTable.getInstance().getSkillEntry(_skillId, enchantLevel);
        if (skill == null) {
            activeChar.sendMessage("Internal error: not found skill level");
            return;
        }
        final int[] cost = sl.getCost();
        final int requiredSp = (int) (cost[1] * sl.getCostMult() * AllSettingsConfig.ALT_SKILL_ENCHANT_SP_MODIFIER);
        final int requiredAdena = (int) (cost[0] * sl.getCostMult() * AllSettingsConfig.ALT_SKILL_ENCHANT_ADENA_MODIFIER);
        final int rate = sl.getRate(activeChar);

        if (activeChar.getSp() < requiredSp) {
            activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_SP_TO_ENCHANT_THAT_SKILL);
            return;
        }

        if (activeChar.getAdena() < requiredAdena) {
            activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            return;
        }

        if (_skillLvl % 100 == 1) // only first lvl requires book (101, 201, 301 ...)
        {
            if (ItemFunctions.getItemCount(activeChar, SkillTreeTable.NORMAL_ENCHANT_BOOK) == 0) {
                activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ALL_OF_THE_ITEMS_NEEDED_TO_ENCHANT_THAT_SKILL);
                return;
            }
            ItemFunctions.removeItem(activeChar, SkillTreeTable.NORMAL_ENCHANT_BOOK, 1);
        }

        if (Rnd.chance(rate) || AllSettingsConfig.oneClickSkillEnchant) {
            activeChar.addExpAndSp(0, -1 * requiredSp);
            ItemFunctions.removeItem(activeChar, 57, requiredAdena);
            activeChar.sendPacket(new SystemMessage(SystemMsg.YOUR_SP_HAS_DECREASED_BY_S1).addNumber(requiredSp), new SystemMessage(SystemMsg.SKILL_ENCHANT_WAS_SUCCESSFUL_S1_HAS_BEEN_ENCHANTED).addSkillName(_skillId, _skillLvl), new SkillList(activeChar), new ExEnchantSkillResult(1));
            Log.add(activeChar.getName() + "|Successfully enchanted|" + _skillId + "|to+" + _skillLvl + '|' + rate, "enchant_skills");
        } else {
            skill = SkillTable.getInstance().getSkillEntry(_skillId, sl.getBaseLevel());
            activeChar.sendPacket(new SystemMessage(SystemMsg.SKILL_ENCHANT_FAILED), new ExEnchantSkillResult(0));
            Log.add(activeChar.getName() + "|Failed to enchant|" + _skillId + "|to+" + _skillLvl + '|' + rate, "enchant_skills");
        }

        activeChar.addSkill(skill, true);
        if (activeChar.isSkillDisabled(skillEntry)) {
            TimeStamp timeStamp = activeChar.removeSkillReuse(skillEntry).get();
            timeStamp.setLevel(skill.getLevel());
            activeChar.getSkillsReuse().put(skill.hashCode(), timeStamp);
        }
        updateSkillShortcuts(activeChar, _skillId, _skillLvl);
        activeChar.sendPacket(new ExEnchantSkillInfo(_skillId, activeChar.getSkillDisplayLevel(_skillId)));
    }
}