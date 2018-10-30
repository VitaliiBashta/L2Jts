package org.mmocore.gameserver.handler.admincommands.impl;

import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;

public class AdminPolymorph implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        final Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().CanPolymorph) {
            return false;
        }

        GameObject target = activeChar.getTarget();

        switch (command) {
            case admin_polyself:
                target = activeChar;
            case admin_polymorph:
            case admin_poly:
                if (target == null || !target.isPlayer()) {
                    activeChar.sendPacket(SystemMsg.INVALID_TARGET);
                    return false;
                }
                try {
                    final int id = Integer.parseInt(wordList[1]);
                    if (NpcHolder.getInstance().getTemplate(id) != null) {
                        ((Player) target).setPolyId(id);
                        ((Player) target).broadcastCharInfo();
                    }
                } catch (Exception e) {
                    activeChar.sendAdminMessage("USAGE: //poly id [type:npc|item]");
                    return false;
                }
                break;
            case admin_unpolyself:
                target = activeChar;
            case admin_unpolymorph:
            case admin_unpoly:
                if (target == null || !target.isPlayer()) {
                    activeChar.sendPacket(SystemMsg.INVALID_TARGET);
                    return false;
                }
                ((Player) target).setPolyId(0);
                ((Player) target).broadcastCharInfo();
                break;
        }

        return true;
    }

    @Override
    public Enum<?>[] getAdminCommandEnum() {
        return Commands.values();
    }

    @Override
    public String[] getAdminCommandString() {
        // TODO Auto-generated method stub
        return null;
    }

    private enum Commands {
        admin_polyself,
        admin_polymorph,
        admin_poly,
        admin_unpolyself,
        admin_unpolymorph,
        admin_unpoly
    }
}