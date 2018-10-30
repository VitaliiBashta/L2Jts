package org.mmocore.gameserver.skills.skillclasses;

import org.mmocore.commons.math.random.RndSelector;
import org.mmocore.commons.math.random.RndSelector.RndNode;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.reward.RewardList;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemHolder;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.utils.ItemFunctions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author VISTALL
 * @date 17:21/31.08.2011
 */
public class Extract extends Skill {
    private final RndSelector<ExtractGroup> _selector;

    @SuppressWarnings("unchecked")
    public Extract(final StatsSet set) {
        super(set);
        List<ExtractGroup> extractGroupList = (List<ExtractGroup>) set.get("extractlist");
        if (extractGroupList == null) {
            extractGroupList = Collections.emptyList();
        }

        final RndNode<ExtractGroup>[] nodes = extractGroupList.stream()
                .map(extractGroup -> RndNode.of(extractGroup, (int) (extractGroup._chance * 10000))).toArray(RndNode[]::new);
        _selector = RndSelector.of(nodes);
    }

    @Override
    public boolean checkCondition(final SkillEntry skillEntry, final Creature activeChar, final Creature target, final boolean forceUse, final boolean dontMove, final boolean first) {
        if (!super.checkCondition(skillEntry, activeChar, target, forceUse, dontMove, first)) {
            return false;
        }

        if (!activeChar.isPlayer()) {
            return false;
        }

        final Player player = (Player) activeChar;
        if (!player.isQuestContinuationPossible(true)) {
            return false;
        }

        return true;
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature activeChar, final List<Creature> targets) {
        for (final Creature target : targets) {
            final Player targetPlayer = target.getPlayer();
            if (targetPlayer == null) {
                return;
            }

            final ExtractGroup extractGroup = _selector.chance(RewardList.MAX_CHANCE);
            if (extractGroup != null) {
                for (final ItemHolder item : extractGroup) {
                    ItemFunctions.addItem(targetPlayer, item.getItemId(), item.getCount(), true);
                }
            } else {
                targetPlayer.sendPacket(SystemMsg.THERE_WAS_NOTHING_FOUND_INSIDE);
            }
        }
    }

    public static class ExtractGroup extends ArrayList<ItemHolder> {
        private final double _chance;

        public ExtractGroup(final double chance) {
            _chance = chance;
        }
    }
}
