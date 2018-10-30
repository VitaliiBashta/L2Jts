package org.mmocore.gameserver.stats.conditions;

import org.mmocore.gameserver.data.scripts.Scripts;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.Optional;
import java.util.OptionalDouble;

/**
 * @author VISTALL
 * @date 3:38/17.05.2011
 */
@SuppressWarnings("unchecked")
public class ConditionTargetNpcClass extends Condition {
    private final Class<NpcInstance> _npcClass;

    public ConditionTargetNpcClass(final String name) {
        Class<NpcInstance> classType = null;
        try {
            classType = (Class<NpcInstance>) Class.forName("org.mmocore.gameserver.model.instances." + name + "Instance");
        } catch (ClassNotFoundException e) {
            classType = (Class<NpcInstance>) Scripts.getInstance().getClasses().get("npc.model." + name + "Instance");
        }

        if (classType == null) {
            throw new IllegalArgumentException("Not found type class for type: " + name + '.');
        } else {
            _npcClass = classType;
        }
    }

    @Override
    public boolean testImpl(final Creature creature, final Optional<Creature> optionalTarget, final Optional<SkillEntry> skill,
                            final Optional<ItemInstance> item, final OptionalDouble initialValue) {
        if (!optionalTarget.isPresent())
            return false;

        final Creature target = optionalTarget.get();
        return target.getClass() == _npcClass;
    }
}
