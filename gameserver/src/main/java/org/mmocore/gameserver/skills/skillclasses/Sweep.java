package org.mmocore.gameserver.skills.skillclasses;


import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.model.reward.RewardItem;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.utils.ItemFunctions;

import java.util.List;


public class Sweep extends Skill {
    public Sweep(final StatsSet set) {
        super(set);
    }

    @Override
    public boolean checkCondition(final SkillEntry skillEntry, final Creature activeChar, final Creature target, final boolean forceUse, final boolean dontMove, final boolean first) {
        if (isNotTargetAoE()) {
            return super.checkCondition(skillEntry, activeChar, target, forceUse, dontMove, first);
        }

        if (target == null) {
            return false;
        }

        if (!target.isMonster() || !target.isDead()) {
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
            return false;
        }

        if (!((MonsterInstance) target).isSpoiled()) {
            activeChar.sendPacket(SystemMsg.SWEEPER_FAILED_TARGET_NOT_SPOILED);
            return false;
        }

        if (!((MonsterInstance) target).isSpoiled((Player) activeChar)) {
            activeChar.sendPacket(SystemMsg.THERE_ARE_NO_PRIORITY_RIGHTS_ON_A_SWEEPER);
            return false;
        }

        return super.checkCondition(skillEntry, activeChar, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature activeChar, final List<Creature> targets) {
        if (!activeChar.isPlayer()) {
            return;
        }

        final Player player = (Player) activeChar;

        for (final Creature targ : targets) {
            if (targ == null || !targ.isMonster() || !targ.isDead() || !((MonsterInstance) targ).isSpoiled()) {
                continue;
            }

            final MonsterInstance target = (MonsterInstance) targ;

            if (!target.isSpoiled(player)) {
                activeChar.sendPacket(SystemMsg.THERE_ARE_NO_PRIORITY_RIGHTS_ON_A_SWEEPER);
                continue;
            }

            final List<RewardItem> items = target.takeSweep();

            if (items == null) {
                activeChar.getAI().setAttackTarget(null);
                target.endDecayTask();
                continue;
            }

            for (final RewardItem item : items) {
                final ItemInstance sweep = ItemFunctions.createItem(item.itemId);
                sweep.setCount(item.count);

                if (player.isInParty() && player.getParty().isDistributeSpoilLoot()) {
                    player.getParty().distributeItem(player, sweep, null);
                    continue;
                }

                if (!player.getInventory().validateCapacity(sweep) || !player.getInventory().validateWeight(sweep)) {
                    sweep.dropToTheGround(player, target);
                    continue;
                }

                player.getInventory().addItem(sweep);

                SystemMessage smsg;
                if (item.count == 1) {
                    smsg = new SystemMessage(SystemMsg.YOU_HAVE_OBTAINED_S1);
                    smsg.addItemName(item.itemId);
                    player.sendPacket(smsg);
                } else {
                    smsg = new SystemMessage(SystemMsg.YOU_HAVE_OBTAINED_S2_S1);
                    smsg.addItemName(item.itemId);
                    smsg.addNumber(item.count);
                    player.sendPacket(smsg);
                }
                if (player.isInParty()) {
                    if (item.count == 1) {
                        smsg = new SystemMessage(SystemMsg.C1_HAS_OBTAINED_S2_BY_USING_SWEEPER);
                        smsg.addName(player);
                        smsg.addItemName(item.itemId);
                        player.getParty().broadcastToPartyMembers(player, smsg);
                    } else {
                        smsg = new SystemMessage(SystemMsg.C1_HAS_OBTAINED_S3_S2_BY_USING_SWEEPER);
                        smsg.addName(player);
                        smsg.addItemName(item.itemId);
                        smsg.addNumber(item.count);
                        player.getParty().broadcastToPartyMembers(player, smsg);
                    }
                }
            }

            activeChar.getAI().setAttackTarget(null);
            target.endDecayTask();
        }
    }
}
