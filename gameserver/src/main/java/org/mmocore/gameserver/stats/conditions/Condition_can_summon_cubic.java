package org.mmocore.gameserver.stats.conditions;

import org.jts.dataparser.data.holder.CubicDataHolder;
import org.jts.dataparser.data.holder.cubicdata.DefaultCubicData;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.EffectType;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.skills.effects.EffectTemplate;
import org.mmocore.gameserver.stats.Stats;

import java.util.Optional;
import java.util.OptionalDouble;

/**
 * Create by Mangol on 24.09.2015.
 */
public class Condition_can_summon_cubic extends Condition {
    @Override
    public boolean testImpl(final Creature creature, final Optional<Creature> optionalTarget, final Optional<SkillEntry> skill, final Optional<ItemInstance> item, final OptionalDouble initialValue) {
        if (creature.getPlayer() == null || !skill.isPresent()) {
            creature.sendPacket(SystemMsg.CUBIC_SUMMONING_FAILED);
            return false;
        }
        final Player player = creature.getPlayer();
        final Optional<EffectTemplate> effectTemplate = Optional.ofNullable(skill.get().getTemplate().getEffectTemplate(EffectType.i_summon_cubic));
        if (effectTemplate.isPresent()) {
            final String st[] = effectTemplate.get().getParam().getString("argument").split(";");
            final int id = Integer.parseInt(st[0]);
            final int level = Integer.parseInt(st[1]);
            final Optional<DefaultCubicData> cubic = Optional.ofNullable(CubicDataHolder.getInstance().getCubicTemplate(id, level));
            if (cubic.isPresent()) {
                if (player.getCubics().isEmpty()) {
                    return true;
                } else if (player.isCubicSlot(cubic.get().slot)) {
                    return true;
                } else if (player.getCubics().size() < player.calcStat(Stats.CUBICS_LIMIT, 1)) {
                    return true;
                }
            }
        }
        creature.sendPacket(SystemMsg.CUBIC_SUMMONING_FAILED);
        return false;
    }
}