package org.mmocore.gameserver.object.components.player;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;

public class DeathPenalty {
    private static final int _skillId = 5076;
    private static final int _fortuneOfNobleseSkillId = 1325;
    private static final int _charmOfLuckSkillId = 2168;

    private final Player playerRef;
    private int _level;
    private boolean _hasCharmOfLuck;

    public DeathPenalty(final Player player, final int level) {
        playerRef = player;
        _level = level;
    }

    public Player getPlayer() {
        return playerRef;
    }

    /*
     * For common usage
     */
    public int getLevel() {
        // Some checks if admin set incorrect value at database
        if (_level > 15) {
            _level = 15;
        }

        if (_level < 0) {
            _level = 0;
        }

        return AllSettingsConfig.ALLOW_DEATH_PENALTY_C5 ? _level : 0;
    }

    /*
     * Used only when saving DB if admin for some reasons disabled it in config after it was enabled.
     * In if we will use getLevel() it will be reseted to 0
     */
    public int getLevelOnSaveDB() {
        if (_level > 15) {
            _level = 15;
        }

        if (_level < 0) {
            _level = 0;
        }

        return _level;
    }

    public void notifyDead(final Creature killer) {
        if (!AllSettingsConfig.ALLOW_DEATH_PENALTY_C5) {
            return;
        }

        if (_hasCharmOfLuck) {
            _hasCharmOfLuck = false;
            return;
        }

        if (killer == null || killer.isPlayable()) {
            return;
        }

        final Player player = getPlayer();
        if (player == null || player.getLevel() <= 9) {
            return;
        }

        int karmaBonus = player.getKarma() / AllSettingsConfig.ALT_DEATH_PENALTY_C5_KARMA_PENALTY;
        if (karmaBonus < 0) {
            karmaBonus = 0;
        }

        if (Rnd.chance(AllSettingsConfig.ALT_DEATH_PENALTY_C5_CHANCE + karmaBonus)) {
            addLevel();
        }
    }

    public void restore(final Player player) {
        final SkillEntry remove = player.getKnownSkill(_skillId);
        if (remove != null) {
            player.removeSkill(remove, true);
        }

        if (!AllSettingsConfig.ALLOW_DEATH_PENALTY_C5) {
            return;
        }

        if (getLevel() > 0) {
            player.addSkill(SkillTable.getInstance().getSkillEntry(_skillId, getLevel()), false);
            player.sendPacket(new SystemMessage(SystemMsg.YOUR_DEATH_PENALTY_IS_NOW_LEVEL_S1).addNumber(getLevel()));
        }
        player.sendEtcStatusUpdate();
        player.updateStats();
    }

    public void addLevel() {
        final Player player = getPlayer();
        if (player == null || getLevel() >= 15 || player.isGM()) {
            return;
        }

        if (getLevel() != 0) {
            final SkillEntry remove = player.getKnownSkill(_skillId);
            if (remove != null) {
                player.removeSkill(remove, true);
            }
        }

        _level++;

        player.addSkill(SkillTable.getInstance().getSkillEntry(_skillId, getLevel()), false);
        player.sendPacket(new SystemMessage(SystemMsg.YOUR_DEATH_PENALTY_IS_NOW_LEVEL_S1).addNumber(getLevel()));
        player.sendEtcStatusUpdate();
        player.updateStats();
    }

    public void reduceLevel() {
        final Player player = getPlayer();
        if (player == null || getLevel() <= 0) {
            return;
        }

        final SkillEntry remove = player.getKnownSkill(_skillId);
        if (remove != null) {
            player.removeSkill(remove, true);
        }

        _level--;

        if (getLevel() > 0) {
            player.addSkill(SkillTable.getInstance().getSkillEntry(_skillId, getLevel()), false);
            player.sendPacket(new SystemMessage(SystemMsg.YOUR_DEATH_PENALTY_IS_NOW_LEVEL_S1).addNumber(getLevel()));
        } else {
            player.sendPacket(SystemMsg.YOUR_DEATH_PENALTY_HAS_BEEN_LIFTED);
        }

        player.sendEtcStatusUpdate();
        player.updateStats();
    }

    public void checkCharmOfLuck() {
        final Player player = getPlayer();
        if (player != null) {
            for (final Effect e : player.getEffectList().getAllEffects()) {
                if (e.getSkill().getId() == _charmOfLuckSkillId || e.getSkill().getId() == _fortuneOfNobleseSkillId) {
                    _hasCharmOfLuck = true;
                    return;
                }
            }
        }

        _hasCharmOfLuck = false;
    }
}