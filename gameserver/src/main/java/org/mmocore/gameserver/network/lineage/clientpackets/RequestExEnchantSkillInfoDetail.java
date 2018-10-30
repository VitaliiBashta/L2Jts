package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.model.base.EnchantSkillLearn;
import org.mmocore.gameserver.network.lineage.serverpackets.ExEnchantSkillInfoDetail;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.tables.SkillTreeTable;

import java.util.List;

public final class RequestExEnchantSkillInfoDetail extends L2GameClientPacket {
    private static final int TYPE_NORMAL_ENCHANT = 0;
    private static final int TYPE_SAFE_ENCHANT = 1;
    private static final int TYPE_UNTRAIN_ENCHANT = 2;
    private static final int TYPE_CHANGE_ENCHANT = 3;

    private int _type;
    private int _skillId;
    private int _skillLvl;

    @Override
    protected void readImpl() {
        _type = readD();
        _skillId = readD();
        _skillLvl = readD();
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();

        if (activeChar == null) {
            return;
        }

        if (activeChar.isTransformed()) {
            activeChar.sendMessage("You must leave transformation mode first.");
            return;
        }

        if (activeChar.getLevel() < 76 || activeChar.getPlayerClassComponent().getClassId().getLevel().ordinal() < 4) {
            activeChar.sendMessage("You must have 3rd class change quest completed.");
            return;
        }

        int bookId = 0;
        int sp = 0;
        int adenaCount = 0;
        double spMult = AllSettingsConfig.ALT_SKILL_ENCHANT_SP_MODIFIER;
        double adenaMult = AllSettingsConfig.ALT_SKILL_ENCHANT_ADENA_MODIFIER;

        EnchantSkillLearn esd = null;

        switch (_type) {
            case TYPE_NORMAL_ENCHANT:
                if (_skillLvl % 100 == 1) {
                    bookId = SkillTreeTable.NORMAL_ENCHANT_BOOK;
                }
                esd = SkillTreeTable.getSkillEnchant(_skillId, _skillLvl);
                break;
            case TYPE_SAFE_ENCHANT:
                bookId = SkillTreeTable.SAFE_ENCHANT_BOOK;
                esd = SkillTreeTable.getSkillEnchant(_skillId, _skillLvl);
                spMult = AllSettingsConfig.ALT_SKILL_SAFE_ENCHANT_SP_MODIFIER;
                adenaMult = AllSettingsConfig.ALT_SKILL_SAFE_ENCHANT_ADENA_MODIFIER;
                break;
            case TYPE_UNTRAIN_ENCHANT:
                bookId = SkillTreeTable.UNTRAIN_ENCHANT_BOOK;
                esd = SkillTreeTable.getSkillEnchant(_skillId, _skillLvl + 1);
                spMult = AllSettingsConfig.ALT_SKILL_UNTRAIN_REFUND_SP_MODIFIER;
                adenaMult = 0;
                break;
            case TYPE_CHANGE_ENCHANT:
                bookId = SkillTreeTable.CHANGE_ENCHANT_BOOK;
                final List<EnchantSkillLearn> esdl = SkillTreeTable.getEnchantsForChange(_skillId, _skillLvl);
                if (esdl.isEmpty()) {
                    return;
                }
                esd = esdl.get(0);
                spMult = AllSettingsConfig.ALT_SKILL_ROUTE_CHANGE_SP_MODIFIER;
                adenaMult = AllSettingsConfig.ALT_SKILL_ROUTE_CHANGE_ADENA_MODIFIER;
                break;
        }

        if (esd == null) {
            return;
        }

        adenaMult *= esd.getCostMult();
        spMult *= esd.getCostMult();
        final int[] cost = esd.getCost();

        sp = (int) (cost[1] * spMult);
        adenaCount = (int) (cost[0] * adenaMult);

        // send skill enchantment detail
        activeChar.sendPacket(new ExEnchantSkillInfoDetail(_skillId, _skillLvl, sp, esd.getRate(activeChar), bookId, adenaCount));
    }
}