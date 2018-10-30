package org.mmocore.gameserver.scripts.npc.model;

import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

public class SeducedInvestigatorInstance extends MonsterInstance {
    public SeducedInvestigatorInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
        setHasChatWindow(true);
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        player.sendPacket(new HtmlMessage(this, "common/seducedinvestigator.htm", val));
    }

    @Override
    public boolean isAutoAttackable(Creature attacker) {
        Player player = attacker.getPlayer();
        if (player == null) {
            return false;
        }
        if (player.isPlayable()) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isMovementDisabled() {
        return true;
    }

    @Override
    public boolean canChampion() {
        return false;
    }
}