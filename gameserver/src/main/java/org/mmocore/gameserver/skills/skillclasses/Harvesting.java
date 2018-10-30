package org.mmocore.gameserver.skills.skillclasses;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.configuration.config.SpoilConfig;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.model.reward.RewardItem;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.utils.ItemFunctions;

import java.util.List;


public class Harvesting extends Skill {
    public Harvesting(final StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature activeChar, final List<Creature> targets) {
        if (!activeChar.isPlayer()) {
            return;
        }

        final Player player = (Player) activeChar;

        for (final Creature target : targets) {
            if (target != null) {
                if (!target.isMonster()) {
                    continue;
                }

                final MonsterInstance monster = (MonsterInstance) target;

                // Не посеяно
                if (!monster.isSeeded()) {
                    activeChar.sendPacket(SystemMsg.THE_HARVEST_FAILED_BECAUSE_THE_SEED_WAS_NOT_SOWN);
                    continue;
                }

                if (!monster.isSeeded(player)) {
                    activeChar.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_HARVEST);
                    continue;
                }

                double SuccessRate = SpoilConfig.MANOR_HARVESTING_BASIC_SUCCESS;
                final int diffPlayerTarget = Math.abs(activeChar.getLevel() - monster.getLevel());

                // Штраф, на разницу уровней между мобом и игроком
                // 5% на каждый уровень при разнице >5 - по умолчанию
                if (diffPlayerTarget > SpoilConfig.MANOR_DIFF_PLAYER_TARGET) {
                    SuccessRate -= (diffPlayerTarget - SpoilConfig.MANOR_DIFF_PLAYER_TARGET) * SpoilConfig.MANOR_DIFF_PLAYER_TARGET_PENALTY;
                }

                // Минимальный шанс успеха всегда 1%
                if (SuccessRate < 1) {
                    SuccessRate = 1;
                }

                if (player.isGM()) {
                    player.sendMessage(new CustomMessage("org.mmocore.gameserver.skills.skillclasses.Harvesting.Chance").addNumber((long) SuccessRate));
                }

                if (!Rnd.chance(SuccessRate)) {
                    activeChar.sendPacket(SystemMsg.THE_HARVEST_HAS_FAILED);
                    monster.clearHarvest();
                    continue;
                }

                final RewardItem item = monster.takeHarvest();
                if (item == null) {
                    continue;
                }

                final ItemInstance harvest;
                if (!player.getInventory().validateCapacity(item.itemId, item.count) || !player.getInventory().validateWeight(item.itemId, item.count)) {
                    harvest = ItemFunctions.createItem(item.itemId);
                    harvest.setCount(item.count);
                    harvest.dropToTheGround(player, monster);
                    continue;
                }

                player.getInventory().addItem(item.itemId, item.count);

                player.sendPacket(new SystemMessage(SystemMsg.C1_HARVESTED_S3_S2S).addName(player).addNumber(item.count)
                        .addItemName(item.itemId));
                if (player.isInParty()) {
                    final SystemMessage smsg = new SystemMessage(SystemMsg.C1_HARVESTED_S3_S2S).addString(player.getName()).addNumber(item.count)
                            .addItemName(item.itemId);
                    player.getParty().broadcastToPartyMembers(player, smsg);
                }
            }
        }
    }
}