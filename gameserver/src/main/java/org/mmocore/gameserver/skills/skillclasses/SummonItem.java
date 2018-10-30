package org.mmocore.gameserver.skills.skillclasses;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.utils.ItemFunctions;

import java.util.List;


public class SummonItem extends Skill {
    private final int _itemId;
    private final int _minId;
    private final int _maxId;
    private final long _minCount;
    private final long _maxCount;

    public SummonItem(final StatsSet set) {
        super(set);

        _itemId = set.getInteger("SummonItemId", 0);
        _minId = set.getInteger("SummonMinId", 0);
        _maxId = set.getInteger("SummonMaxId", _minId);
        _minCount = set.getLong("SummonMinCount");
        _maxCount = set.getLong("SummonMaxCount", _minCount);
    }

    @Override
    public boolean checkCondition(final SkillEntry skillEntry, final Creature activeChar, final Creature target, final boolean forceUse, final boolean dontMove, final boolean first) {
        if (target.isPlayer()) {
            final Player player = (Player) target;
            if (player.getWeightPenalty() >= 3 || player.getInventory().getSize() > player.getInventoryLimit() - 10) {
                player.sendPacket(SystemMsg.YOUR_INVENTORY_IS_FULL, new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(_itemId));
                return false;
            }
        }

        return super.checkCondition(skillEntry, activeChar, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature activeChar, final List<Creature> targets) {
        if (!activeChar.isPlayable()) {
            return;
        }
        targets.stream().filter(target -> target != null).forEach(target -> {
            final int itemId = _minId > 0 ? Rnd.get(_minId, _maxId) : _itemId;
            final long count = Rnd.get(_minCount, _maxCount);

            ItemFunctions.addItem((Playable) activeChar, itemId, count, true);
            getEffects(skillEntry, activeChar, target, getActivateRate() > 0, false);
        });
    }
}