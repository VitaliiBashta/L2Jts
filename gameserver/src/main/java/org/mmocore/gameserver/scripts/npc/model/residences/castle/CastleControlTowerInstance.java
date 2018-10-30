package org.mmocore.gameserver.scripts.npc.model.residences.castle;

import org.mmocore.gameserver.model.Spawner;
import org.mmocore.gameserver.model.instances.residences.SiegeToggleNpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

import java.util.HashSet;
import java.util.Set;

public class CastleControlTowerInstance extends SiegeToggleNpcInstance {
    private final Set<Spawner> _spawnList = new HashSet<Spawner>();

    public CastleControlTowerInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onDeathImpl(Creature killer) {
        _spawnList.forEach(Spawner::stopRespawn);
        _spawnList.clear();
    }

    @Override
    public void register(Spawner spawn) {
        _spawnList.add(spawn);
    }

	/* wtf?
	@Override
	public void reduceCurrentHp(double damage, Creature attacker, SkillEntry skill, boolean awake, boolean standUp, boolean directHp, boolean canReflect, boolean transferDamage, boolean isDot, boolean sendMessage, boolean lethal)
	{
		super.reduceCurrentHp(2, attacker, skill, awake, standUp, directHp, canReflect, transferDamage, isDot, sendMessage, lethal);
	}
	*/
}