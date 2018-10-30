package org.mmocore.gameserver.stats.conditions;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.Optional;
import java.util.OptionalDouble;

public class Condition_op_exist_npc extends Condition {
    private final int[] _npcId;
    private final int _radius;

    public Condition_op_exist_npc(String npcId, final int radius) {
        String[] n = npcId.split(";");
        _npcId = new int[n.length];
        for (int i = 0; i < _npcId.length; i++) {
            _npcId[i] = Integer.parseInt(n[i].trim());
        }
        _radius = radius;
    }

    @Override
    public boolean testImpl(final Creature creature, final Optional<Creature> optionalTarget, final Optional<SkillEntry> skill,
                            final Optional<ItemInstance> item, final OptionalDouble initialValue) {
        for (NpcInstance npc : creature.getAroundNpc(_radius, _radius)) {
            if (ArrayUtils.contains(_npcId, npc.getNpcId())) {
                return true;
            }
        }
        return false;
    }
}
