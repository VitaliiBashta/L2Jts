package org.mmocore.gameserver.listener.actor.player.impl;

import org.mmocore.commons.lang.reference.HardReference;
import org.mmocore.gameserver.listener.actor.player.OnPlayerExitListener;
import org.mmocore.gameserver.listener.actor.player.OnPlayerSayListener;
import org.mmocore.gameserver.network.lineage.components.ChatType;
import org.mmocore.gameserver.network.lineage.serverpackets.Snoop;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;

/**
 * @author VISTALL
 * @date 22:00/15.09.2011
 */
public class SnoopPlayerSayListener implements OnPlayerSayListener, OnPlayerExitListener {
    private final HardReference<Player> _ownerRef;

    public SnoopPlayerSayListener(final Player owner) {
        _ownerRef = owner.getRef();
    }

    @Override
    public void onSay(final Player activeChar, final ChatType type, final String target, final String text) {
        final Player owner = _ownerRef.get();
        if (owner == null) {
            activeChar.removeListener(this);
            return;
        }

        final String speaker = type == ChatType.TELL ? "->" + target : activeChar.getName();

        owner.sendPacket(new Snoop(activeChar.getObjectId(), activeChar.getName(), type, speaker, text));
    }

    @Override
    public void onPlayerExit(final Player player) {
        final Player owner = _ownerRef.get();
        if (owner == null) {
            return;
        }

        owner.getPlayerVariables().remove(PlayerVariables.SNOOP_TARGET);
    }

    public Player getOwner() {
        return _ownerRef.get();
    }
}
