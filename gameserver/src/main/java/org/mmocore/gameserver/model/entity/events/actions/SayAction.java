package org.mmocore.gameserver.model.entity.events.actions;


import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.entity.events.EventAction;
import org.mmocore.gameserver.network.lineage.components.ChatType;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.components.SysString;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.L2GameServerPacket;
import org.mmocore.gameserver.network.lineage.serverpackets.Say2;
import org.mmocore.gameserver.object.Player;

import java.util.List;


/**
 * @author VISTALL
 * @date 22:25/05.01.2011
 */
public class SayAction implements EventAction {
    private final int _range;
    private final ChatType _chatType;

    private String _how;
    private NpcString _text;

    private SysString _sysString;
    private SystemMsg _systemMsg;

    protected SayAction(final int range, final ChatType type) {
        _range = range;
        _chatType = type;
    }

    public SayAction(final int range, final ChatType type, final SysString sysString, final SystemMsg systemMsg) {
        this(range, type);
        _sysString = sysString;
        _systemMsg = systemMsg;
    }

    public SayAction(final int range, final ChatType type, final String how, final NpcString string) {
        this(range, type);
        _text = string;
        _how = how;
    }

    @Override
    public void call(final Event event) {
        final List<Player> players = event.broadcastPlayers(_range);
        players.forEach(this::packet);
    }

    private void packet(final Player player) {
        if (player == null) {
            return;
        }

        final L2GameServerPacket packet = _sysString != null ? new Say2(0, _chatType, _sysString, _systemMsg) : new Say2(0, _chatType, _how, _text);
        player.sendPacket(packet);
    }
}
