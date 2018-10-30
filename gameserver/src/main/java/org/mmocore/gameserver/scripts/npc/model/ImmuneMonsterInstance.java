package org.mmocore.gameserver.scripts.npc.model;

import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

public class ImmuneMonsterInstance extends MonsterInstance {
    public ImmuneMonsterInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public boolean isFearImmune() {
        return true;
    }

    @Override
    public boolean isParalyzeImmune() {
        return true;
    }

    @Override
    public boolean isLethalImmune() {
        return true;
    }
}