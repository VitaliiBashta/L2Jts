package org.mmocore.gameserver.scripts.npc.model.residences.fortress.siege;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * Данный инстанс используется NPC Ballista на осадах фортов
 *
 * @author SYS
 */
public class BallistaInstance extends NpcInstance {
    public BallistaInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
        setUndying(false);
    }

    @Override
    protected void onDeath(Creature killer) {
        super.onDeath(killer);

        if (killer == null || !killer.isPlayer()) {
            return;
        }

        Player player = killer.getPlayer();
        if (player.getClan() == null) {
            return;
        }

        player.getClan().incReputation(30, false, "Ballista " + getTitle());
        player.sendPacket(new SystemMessage(SystemMsg.THE_BALLISTA_HAS_BEEN_SUCCESSFULLY_DESTROYED));
    }

    @Override
    public boolean isAutoAttackable(Creature attacker) {
        return true;
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
    }

    @Override
    public boolean isFearImmune() {
        return true;
    }
}