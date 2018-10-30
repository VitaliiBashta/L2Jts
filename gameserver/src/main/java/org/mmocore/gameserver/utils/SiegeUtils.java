package org.mmocore.gameserver.utils;

import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.tables.SkillTable;

/**
 * @author VISTALL
 * @date 12:23/21.02.2011
 */
public class SiegeUtils {
    public static void addSiegeSkills(final Player character) {
        character.addSkill(SkillTable.getInstance().getSkillEntry(246, 1), false);
        character.addSkill(SkillTable.getInstance().getSkillEntry(247, 1), false);
        if (character.isNoble()) {
            character.addSkill(SkillTable.getInstance().getSkillEntry(326, 1), false);
        }

        character.addSkill(SkillTable.getInstance().getSkillEntry(844, 1), false);
        character.addSkill(SkillTable.getInstance().getSkillEntry(845, 1), false);
    }

    public static void removeSiegeSkills(final Player character) {
        character.removeSkill(SkillTable.getInstance().getSkillEntry(246, 1), false);
        character.removeSkill(SkillTable.getInstance().getSkillEntry(247, 1), false);
        character.removeSkill(SkillTable.getInstance().getSkillEntry(326, 1), false);
        character.removeSkill(SkillTable.getInstance().getSkillEntry(844, 1), false);
        character.removeSkill(SkillTable.getInstance().getSkillEntry(845, 1), false);
    }
}
