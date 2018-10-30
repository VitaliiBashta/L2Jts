package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.model.base.EnchantSkillLearn;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ExEnchantSkillInfo;
import org.mmocore.gameserver.network.lineage.serverpackets.ExEnchantSkillResult;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.skills.TimeStamp;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.tables.SkillTreeTable;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Log;

public final class RequestExEnchantSkillRouteChange extends L2GameClientPacket {
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
        if (skillEntry.getDisplayLevel() == -1) {
            return;
        }

        if (skillEntry.getDisplayLevel() <= sl.getBaseLevel() || skillEntry.getDisplayLevel() % 100 != _skillLvl % 100) {
            return;
        }

        final int[] cost = sl.getCost();
        final int requiredSp = (int) (cost[1] * sl.getCostMult() * AllSettingsConfig.ALT_SKILL_ROUTE_CHANGE_SP_MODIFIER);
        final int requiredAdena = (int) (cost[0] * sl.getCostMult() * AllSettingsConfig.ALT_SKILL_ROUTE_CHANGE_ADENA_MODIFIER);

        if (activeChar.getSp() < requiredSp) {
            activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_SP_TO_ENCHANT_THAT_SKILL);
            return;
        }

        if (activeChar.getAdena() < requiredAdena) {
            activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            return;
        }

        if (ItemFunctions.getItemCount(activeChar, SkillTreeTable.CHANGE_ENCHANT_BOOK) == 0) {
            activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ALL_OF_THE_ITEMS_NEEDED_TO_ENCHANT_SKILL_ROUTE_CHANGE);
            return;
        }

        ItemFunctions.removeItem(activeChar, SkillTreeTable.CHANGE_ENCHANT_BOOK, 1);
        ItemFunctions.removeItem(activeChar, 57, requiredAdena);
        activeChar.addExpAndSp(0, -1 * requiredSp);

        final int levelPenalty = Rnd.get(Math.min(4, _skillLvl % 100));

        _skillLvl -= levelPenalty;
        if (_skillLvl % 100 == 0) {
            _skillLvl = sl.getBaseLevel();
        }

        final SkillEntry skill = SkillTable.getInstance().getSkillEntry(_skillId, SkillTreeTable.convertEnchantLevel(sl.getBaseLevel(), _skillLvl,
                sl.getMaxLevel()));

        if (skill != null) {
            activeChar.addSkill(skill, true);
            if (activeChar.isSkillDisabled(skillEntry)) {
                TimeStamp timeStamp = activeChar.removeSkillReuse(skillEntry).get();
                timeStamp.setLevel(skill.getLevel());
                activeChar.getSkillsReuse().put(skill.hashCode(), timeStamp);
            }
        }

        if (levelPenalty == 0) {
            activeChar.sendPacket(new SystemMessage(SystemMsg.ENCHANT_SKILL_ROUTE_CHANGE_WAS_SUCCESSFUL_S1).addSkillName(_skillId, _skillLvl));
        } else {
            activeChar.sendPacket(new SystemMessage(SystemMsg.ENCHANT_SKILL_ROUTE_CHANGE_WAS_SUCCESSFUL_S1_LEVEL_DECREASED_BY_S2).addSkillName(
                    _skillId, _skillLvl).addNumber(levelPenalty));
        }

        Log.add(activeChar.getName() + "|Successfully changed route|" + _skillId + '|' + skillEntry.getDisplayLevel() + "|to+" + _skillLvl + '|' + levelPenalty,
                "enchant_skills");

        activeChar.sendPacket(new ExEnchantSkillInfo(_skillId, activeChar.getSkillDisplayLevel(_skillId)), new ExEnchantSkillResult(1));
        RequestExEnchantSkill.updateSkillShortcuts(activeChar, _skillId, _skillLvl);
    }
}