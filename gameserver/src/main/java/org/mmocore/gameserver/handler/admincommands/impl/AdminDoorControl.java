package org.mmocore.gameserver.handler.admincommands.impl;

import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.model.instances.DoorInstance;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.world.World;

public class AdminDoorControl implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        final Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().Door) {
            return false;
        }

        final GameObject target;

        switch (command) {
            case admin_open:
                if (wordList.length > 1) {
                    target = World.getAroundObjectById(activeChar, Integer.parseInt(wordList[1]));
                } else {
                    target = activeChar.getTarget();
                }

                if (target != null && target.isDoor()) {
                    ((DoorInstance) target).openMe();
                } else {
                    activeChar.sendPacket(SystemMsg.INVALID_TARGET);
                }

                break;
            case admin_close:
                if (wordList.length > 1) {
                    target = World.getAroundObjectById(activeChar, Integer.parseInt(wordList[1]));
                } else {
                    target = activeChar.getTarget();
                }
                if (target != null && target.isDoor()) {
                    ((DoorInstance) target).closeMe();
                } else {
                    activeChar.sendPacket(SystemMsg.INVALID_TARGET);
                }
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
        admin_open,
        admin_close,
    }
}