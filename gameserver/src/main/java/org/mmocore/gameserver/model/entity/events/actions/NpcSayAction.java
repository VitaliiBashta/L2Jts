package org.mmocore.gameserver.model.entity.events.actions;

import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.entity.events.EventAction;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.ChatType;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.serverpackets.NpcSay;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.world.GameObjectsStorage;
import org.mmocore.gameserver.world.World;

/**
 * @author VISTALL
 * @date 21:44/10.12.2010
 */
public class NpcSayAction implements EventAction {
    private final int _npcId;
    private final int _range;
    private final ChatType _chatType;
    private final NpcString _text;

    public NpcSayAction(final int npcId, final int range, final ChatType type, final NpcString string) {
        _npcId = npcId;
        _range = range;
        _chatType = type;
        _text = string;
    }

    @Override
    public void call(final Event event) {
        final NpcInstance npc = GameObjectsStorage.getByNpcId(_npcId);
        if (npc == null) {
            return;
        }

        for (final Player player : World.getAroundObservers(npc)) {
            if (_range <= 0 || player.isInRangeZ(npc, _range)) {
                packet(npc, player);
            }
        }
    }

    private void packet(final NpcInstance npc, final Player player) {
        player.sendPacket(new NpcSay(npc, _chatType, _text));
    }
}
