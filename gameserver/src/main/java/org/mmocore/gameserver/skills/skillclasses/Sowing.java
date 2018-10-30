package org.mmocore.gameserver.skills.skillclasses;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.configuration.config.SpoilConfig;
import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.model.Manor;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.StatsSet;

import java.util.List;


public class Sowing extends Skill {
    public Sowing(final StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature activeChar, final List<Creature> targets) {
        if (!activeChar.isPlayer()) {
            return;
        }

        final Player player = (Player) activeChar;
        final int seedId = player.getUseSeed();
        final boolean altSeed = ItemTemplateHolder.getInstance().getTemplate(seedId).isAltSeed();

        // remove seed from inventory
        if (!player.getInventory().destroyItemByItemId(seedId, 1L)) {
            activeChar.sendActionFailed();
            return;
        }

        player.sendPacket(SystemMessage.removeItems(seedId, 1L));

        for (final Creature target : targets) {
            if (target != null) {
                final MonsterInstance monster = (MonsterInstance) target;
                if (monster.isSeeded()) {
                    continue;
                }

                // обработка
                double SuccessRate = SpoilConfig.MANOR_SOWING_BASIC_SUCCESS;

                final double diffPlayerTarget = Math.abs(activeChar.getLevel() - target.getLevel());
                final double diffSeedTarget = Math.abs(Manor.getInstance().getSeedLevel(seedId) - target.getLevel());

                // Штраф, на разницу уровней между мобом и игроком
                // 5% на каждый уровень при разнице >5 - по умолчанию
                if (diffPlayerTarget > SpoilConfig.MANOR_DIFF_PLAYER_TARGET) {
                    SuccessRate -= (diffPlayerTarget - SpoilConfig.MANOR_DIFF_PLAYER_TARGET) * SpoilConfig.MANOR_DIFF_PLAYER_TARGET_PENALTY;
                }

                // Штраф, на разницу уровней между семечкой и мобом
                // 5% на каждый уровень при разнице >5 - по умолчанию
                if (diffSeedTarget > SpoilConfig.MANOR_DIFF_SEED_TARGET) {
                    SuccessRate -= (diffSeedTarget - SpoilConfig.MANOR_DIFF_SEED_TARGET) * SpoilConfig.MANOR_DIFF_SEED_TARGET_PENALTY;
                }

                if (altSeed) {
                    SuccessRate *= SpoilConfig.MANOR_SOWING_ALT_BASIC_SUCCESS / SpoilConfig.MANOR_SOWING_BASIC_SUCCESS;
                }

                // Минимальный шанс успеха всегда 1%
                if (SuccessRate < 1) {
                    SuccessRate = 1;
                }

                if (player.isGM()) {
                    activeChar.sendMessage(new CustomMessage("org.mmocore.gameserver.skills.skillclasses.Sowing.Chance").addNumber((long) SuccessRate));
                }

                if (Rnd.chance(SuccessRate) && monster.setSeeded(player, seedId, altSeed)) {
                    activeChar.sendPacket(SystemMsg.THE_SEED_WAS_SUCCESSFULLY_SOWN);
                } else {
                    activeChar.sendPacket(SystemMsg.THE_SEED_WAS_NOT_SOWN);
                }
            }
        }
    }
}